package us.kbase.mobu.runner;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import us.kbase.auth.AuthService;
import us.kbase.auth.AuthToken;
import us.kbase.catalog.CatalogClient;
import us.kbase.catalog.ModuleVersion;
import us.kbase.catalog.SelectModuleVersion;
import us.kbase.common.executionengine.CallbackServer;
import us.kbase.common.executionengine.CallbackServerConfigBuilder;
import us.kbase.common.executionengine.LineLogger;
import us.kbase.common.executionengine.ModuleMethod;
import us.kbase.common.executionengine.ModuleRunVersion;
import us.kbase.common.executionengine.CallbackServerConfigBuilder.CallbackServerConfig;
import us.kbase.common.service.JsonServerServlet;
import us.kbase.common.service.JsonServerSyslog;
import us.kbase.common.service.UObject;
import us.kbase.common.utils.NetUtils;
import us.kbase.mobu.ModuleBuilder;
import us.kbase.mobu.tester.SDKCallbackServer;
import us.kbase.mobu.util.DirUtils;
import us.kbase.mobu.util.ProcessHelper;

public class ModuleRunner {
    private final URL catalogUrl;
    private final String kbaseEndpoint;
    private final File runDir;
    private String user;
    private String password;
    
    public ModuleRunner() throws Exception {
        String sdkCfgPath = System.getenv(ModuleBuilder.GLOBAL_SDK_CFG_ENV_VAR);
        boolean isCfgGlobal = sdkCfgPath != null && !sdkCfgPath.isEmpty();
        if (!isCfgGlobal)
            sdkCfgPath = "sdk.cfg";
        File sdkCfgFile = new File(sdkCfgPath);
        if (!sdkCfgFile.exists()) {
            if (isCfgGlobal) {
                throw new IllegalStateException("Global sdk.cfg path defined in " + 
                        ModuleBuilder.GLOBAL_SDK_CFG_ENV_VAR + " is not found");
            } else {
                System.out.println("Warning: file " + sdkCfgFile.getAbsolutePath() + " will be " +
                        "initialized (with 'kbase_endpoint'/'catalog_url' pointing to AppDev " +
                        "environment, user and password will be prompted every time if not set)");
                FileUtils.writeLines(sdkCfgFile, Arrays.asList(
                        "kbase_endpoint=https://appdev.kbase.us/services",
                        "catalog_url=https://appdev.kbase.us/services/catalog",
                        "user=",
                        "password="));
            }
        }
        Properties sdkConfig = new Properties();
        try (InputStream is = new FileInputStream(sdkCfgFile)) {
            sdkConfig.load(is);
        }
        kbaseEndpoint = sdkConfig.getProperty("kbase_endpoint");
        if (kbaseEndpoint == null) {
            throw new IllegalStateException("Couldn't find 'kbase_endpoint' parameter in " + 
                    sdkCfgFile);
        }
        String catalogUrlText = sdkConfig.getProperty("catalog_url");
        if (catalogUrlText == null) {
            throw new IllegalStateException("Couldn't find 'catalog_url' parameter in " + 
                    sdkCfgFile);
        }
        catalogUrl = new URL(catalogUrlText);
        runDir = new File("run_local");
        user = sdkConfig.getProperty("user");
        password = sdkConfig.getProperty("password");
        if (user == null || user.trim().isEmpty()) {
            System.out.println("You haven't preset your user/password in " + sdkCfgPath + ". " +
            		"Please enter it now.");
            user = new String(System.console().readPassword("User: "));
            password = new String(System.console().readPassword("Password: "));
        } else {
            if (password == null || password.trim().isEmpty()) {
                System.out.println("You haven't preset your password in " + sdkCfgPath + ". " +
                		"Please enter it now.");
                password = new String(System.console().readPassword("Password: "));
            }
        }
    }
    
    public ModuleRunner(URL catalogUrl, String kbaseEndpoint, File runDir, String user, 
            String password) {
        this.catalogUrl = catalogUrl;
        this.kbaseEndpoint = kbaseEndpoint;
        this.runDir = runDir;
        this.user = user;
        this.password = password;
    }
    
    public int run(String methodName, File inputFile, boolean stdin, String inputJson, 
            File output, String tagVer, boolean verbose, boolean keepTempFiles) throws Exception {
        AuthToken auth = AuthService.login(user, password).getToken();
        ////////////////////////////////// Loading image name /////////////////////////////////////
        CatalogClient client = new CatalogClient(catalogUrl);
        String moduleName = methodName.split(Pattern.quote("."))[0];
        ModuleVersion modVer = client.getModuleVersion(
                new SelectModuleVersion().withModuleName(moduleName).withVersion(tagVer)
                .withIncludeCompilationReport(1L));
        if (modVer.getDataVersion() != null)
            throw new IllegalStateException("Reference data is required for module " + moduleName +
            		". This feature is not supported for local calls.");
        String dockerImage = modVer.getDockerImgName();
        System.out.println("Docker image name recieved from Catalog: " + dockerImage);
        ////////////////////////////////// Standard files in run_local ////////////////////////////
        if (!runDir.exists())
            runDir.mkdir();
        File runLocalSh = new File(runDir, "run_local.sh");
        File runDockerSh = new File(runDir, "run_docker.sh");
        if (!runLocalSh.exists()) {
            FileUtils.writeLines(runLocalSh, Arrays.asList(
                    "#!/bin/bash",
                    "sdir=\"$(cd \"$(dirname \"$(readlink -f \"$0\")\")\" && pwd)\"",
                    "$sdir/run_docker.sh run -i -t -v $sdir/workdir:/kb/module/work " +
                    "-e \"SDK_CALLBACK_URL=$1\" $2 async"));
            ProcessHelper.cmd("chmod", "+x", runLocalSh.getCanonicalPath()).exec(runDir);
        }
        if (!runDockerSh.exists()) {
            FileUtils.writeLines(runDockerSh, Arrays.asList(
                    "#!/bin/bash",
                    "docker \"$@\""));
            ProcessHelper.cmd("chmod", "+x", runDockerSh.getCanonicalPath()).exec(runDir);
        }
        ////////////////////////////////// Temporary files ////////////////////////////////////////
        File workDir = new File(runDir, "workdir");
        if (workDir.exists())
            FileUtils.deleteDirectory(workDir);
        workDir.mkdir();
        File subjobsDir = new File(runDir, "subjobs");
        if (subjobsDir.exists())
            FileUtils.deleteDirectory(subjobsDir);
        File tokenFile = new File(workDir, "token");
        try (FileWriter fw = new FileWriter(tokenFile)) {
            fw.write(auth.toString());
        }
        String jobSrvUrl = kbaseEndpoint + "/userandjobstate";
        String wsUrl = kbaseEndpoint + "/ws";
        String shockUrl = kbaseEndpoint + "/shock-api";
        File configPropsFile = new File(workDir, "config.properties");
        PrintWriter pw = new PrintWriter(configPropsFile);
        try {
            pw.println("[global]");
            pw.println("job_service_url = " + jobSrvUrl);
            pw.println("workspace_url = " + wsUrl);
            pw.println("shock_url = " + shockUrl);
            pw.println("kbase_endpoint = " + kbaseEndpoint);
        } finally {
            pw.close();
        }
        ////////////////////////////////// Preparing input.json ///////////////////////////////////
        String jsonString;
        if (inputFile != null) {
            jsonString = FileUtils.readFileToString(inputFile);
        } else if (inputJson != null) {
            jsonString = inputJson;
        } else if (stdin) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            IOUtils.copy(System.in, baos);
            jsonString = new String(baos.toByteArray(), Charset.forName("utf-8"));
        } else {
            throw new IllegalStateException("No one input method is used");
        }
        jsonString = jsonString.trim();
        if (!jsonString.startsWith("["))
            jsonString = "[" + jsonString + "]";  // Wrapping one argument by array
        FileUtils.writeStringToFile(new File(workDir, "input.json"), jsonString);
        ////////////////////////////////// Starting callback service //////////////////////////////
        int callbackPort = NetUtils.findFreePort();
        URL callbackUrl = CallbackServer.getCallbackUrl(callbackPort);
        Server jettyServer = null;
        if (callbackUrl != null) {
            if( System.getProperty("os.name").startsWith("Windows") ) {
                JsonServerSyslog.setStaticUseSyslog(false);
                JsonServerSyslog.setStaticMlogFile("callback.log");
            }
            CallbackServerConfig cfg = new CallbackServerConfigBuilder(
                    new URL(kbaseEndpoint), callbackUrl, runDir.toPath(),
                    new LineLogger() {
                        @Override
                        public void logNextLine(String line, boolean isError) {
                            //do nothing, SDK callback server doesn't use a logger
                        }
                    }).build();
            ModuleRunVersion runver = new ModuleRunVersion(
                    new URL("https://fakefakefakefakefake.com"),
                    new ModuleMethod("use_set_provenance.to_set_provenance_for_tests"),
                    "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", "0.0.0", "dev");
            JsonServerServlet catalogSrv = new SDKCallbackServer(
                    auth, cfg, runver, new ArrayList<UObject>(),
                    new ArrayList<String>());
            jettyServer = new Server(callbackPort);
            ServletContextHandler context = new ServletContextHandler(
                    ServletContextHandler.SESSIONS);
            context.setContextPath("/");
            jettyServer.setHandler(context);
            context.addServlet(new ServletHolder(catalogSrv),"/*");
            jettyServer.start();
        } else {
            System.out.println("WARNING: No callback URL was recieved " +
                    "by the job runner. Local callbacks are disabled.");
        }
        ////////////////////////////////// Running Docker /////////////////////////////////////////
        try {
            System.out.println();
            int exitCode = ProcessHelper.cmd("bash", DirUtils.getFilePath(runLocalSh),
                    callbackUrl.toExternalForm(), dockerImage).exec(runDir).getExitCode();
            File outputTmpFile = new File(workDir, "output.json");
            if (!outputTmpFile.exists())
                throw new IllegalStateException("Output JSON file was not found");
            if (output != null) {
                FileUtils.copyFile(outputTmpFile, output);
            } else {
                System.out.println();
                System.out.println("Output returned by the method:");
                System.out.println(FileUtils.readFileToString(outputTmpFile));
            }
            return exitCode;
        } finally {
            if (jettyServer != null) {
                System.out.println("Shutting down callback server...");
                jettyServer.stop();
            }
            if (!keepTempFiles) {
                System.out.println("Deleting temporary files...");
                FileUtils.deleteDirectory(workDir);
                FileUtils.deleteDirectory(subjobsDir);
            }
        }
    }
}
