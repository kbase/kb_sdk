package us.kbase.mobu.runner;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.fasterxml.jackson.core.type.TypeReference;

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
import us.kbase.kbasejobservice.FinishJobParams;
import us.kbase.mobu.ModuleBuilder;
import us.kbase.mobu.tester.DockerMountPoints;
import us.kbase.mobu.tester.SDKCallbackServer;
import us.kbase.mobu.util.DirUtils;
import us.kbase.mobu.util.ProcessHelper;
import us.kbase.mobu.util.TextUtils;

public class ModuleRunner {
    private final URL catalogUrl;
    private final String kbaseEndpoint;
    private final File runDir;
    private String user;
    private String password;
    private String[] callbackNetworks = null;
    
    public ModuleRunner(String sdkHome) throws Exception {
        if (sdkHome == null) {
            sdkHome = System.getenv(ModuleBuilder.GLOBAL_SDK_HOME_ENV_VAR);
            if (sdkHome == null)
                throw new IllegalStateException("Path to kb-sdk home folder should be set either" +
                		" in command line (-h) or in " + ModuleBuilder.GLOBAL_SDK_HOME_ENV_VAR + 
                		" system environment variable");
        }
        File sdkHomeDir = new File(sdkHome);
        if (!sdkHomeDir.exists())
            sdkHomeDir.mkdirs();
        File sdkCfgFile = new File(sdkHomeDir, "sdk.cfg");
        String sdkCfgPath = sdkCfgFile.getCanonicalPath();
        if (!sdkCfgFile.exists()) {
            System.out.println("Warning: file " + sdkCfgFile.getAbsolutePath() + " will be " +
                    "initialized (with 'kbase_endpoint'/'catalog_url' pointing to AppDev " +
                    "environment, user and password will be prompted every time if not set)");
            FileUtils.writeLines(sdkCfgFile, Arrays.asList(
                    "kbase_endpoint=https://appdev.kbase.us/services",
                    "catalog_url=https://appdev.kbase.us/services/catalog",
                    "user=",
                    "password=",
                    "### Please use next parameter to specify custom list of network",
                    "### interfaces used for callback IP address lookup:",
                    "#callback_networks=docker0,vboxnet0,vboxnet1,en0,en1,en2,en3"));
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
        runDir = new File(sdkHomeDir, "run_local");
        user = sdkConfig.getProperty("user");
        password = sdkConfig.getProperty("password");
        if (user == null || user.trim().isEmpty()) {
            System.out.println("You haven't preset your user/password in " + sdkCfgPath + ". " +
            		"Please enter it now.");
            user = new String(System.console().readLine("User: "));
            password = new String(System.console().readPassword("Password: "));
        } else {
            if (password == null || password.trim().isEmpty()) {
                System.out.println("You haven't preset your password in " + sdkCfgPath + ". " +
                		"Please enter it now.");
                password = new String(System.console().readPassword("Password: "));
            }
        }
        String callbackNetworksText = sdkConfig.getProperty("callback_networks");
        if (callbackNetworksText != null) {
            callbackNetworks = callbackNetworksText.trim().split("\\s*,\\s*");
        }
    }
    
    public ModuleRunner(URL catalogUrl, String kbaseEndpoint, File runDir, String user, 
            String password, String[] callbackNetworks) {
        this.catalogUrl = catalogUrl;
        this.kbaseEndpoint = kbaseEndpoint;
        this.runDir = runDir;
        this.user = user;
        this.password = password;
        this.callbackNetworks = callbackNetworks;
    }
    
    public int run(String methodName, File inputFile, boolean stdin, String inputJson, 
            File output, String tagVer, boolean verbose, boolean keepTempFiles,
            String provRefs, String mountPoints) throws Exception {
        AuthToken auth = AuthService.login(user, password).getToken();
        ////////////////////////////////// Loading image name /////////////////////////////////////
        CatalogClient client = new CatalogClient(catalogUrl);
        String moduleName = methodName.split(Pattern.quote("."))[0];
        ModuleVersion mv = client.getModuleVersion(
                new SelectModuleVersion().withModuleName(moduleName).withVersion(tagVer)
                .withIncludeCompilationReport(1L));
        if (mv.getDataVersion() != null)
            throw new IllegalStateException("Reference data is required for module " + moduleName +
            		". This feature is not supported for local calls.");
        String dockerImage = mv.getDockerImgName();
        System.out.println("Docker image name recieved from Catalog: " + dockerImage);
        ////////////////////////////////// Standard files in run_local ////////////////////////////
        if (!runDir.exists())
            runDir.mkdir();
        File runLocalSh = new File(runDir, "run_local.sh");
        File runDockerSh = new File(runDir, "run_docker.sh");
        if (!runLocalSh.exists()) {
            final boolean isMac = System.getProperty("os.name").toLowerCase()
                    .contains("mac");
            final boolean isWin = System.getProperty("os.name").toLowerCase()
                    .contains("win");
            FileUtils.writeLines(runLocalSh, Arrays.asList(
                    "#!/bin/bash",
                    "sdir=\"$(cd \"$(dirname \"$(readlink -f \"$0\")\")\" && pwd)\"",
                    "callback_url=$1",
                    "cnt_id=$2",
                    "docker_image=$3",
                    "mount_points=$4",
                    "$sdir/run_docker.sh run " +
                    (isMac || isWin ? "" : "--user $(id -u) ") +
                    "-v $sdir/workdir:/kb/module/work $mount_points " +
                    "-e \"SDK_CALLBACK_URL=$callback_url\" --name $cnt_id $docker_image async"));
            ProcessHelper.cmd("chmod", "+x", runLocalSh.getCanonicalPath()).exec(runDir);
        }
        if (!runDockerSh.exists()) {
            FileUtils.writeLines(runDockerSh, Arrays.asList(
                    "#!/bin/bash",
                    "docker \"$@\""));
            ProcessHelper.cmd("chmod", "+x", runDockerSh.getCanonicalPath()).exec(runDir);
        }
        ////////////////////////////////// Temporary files ////////////////////////////////////////
        final DockerMountPoints mounts = new DockerMountPoints(
                Paths.get("/kb/module/work"), Paths.get("tmp"));
        if (mountPoints != null) {
            for (String part : mountPoints.split(Pattern.quote(","))) {
                mounts.addMount(part);
            }
        }
        File workDir = new File(runDir, "workdir");
        if (workDir.exists())
            FileUtils.deleteDirectory(workDir);
        workDir.mkdir();
        File subjobsDir = new File(runDir, "subjobs");
        if (subjobsDir.exists())
            FileUtils.deleteDirectory(subjobsDir);
        File tokenFile = new File(workDir, "token");
        try (FileWriter fw = new FileWriter(tokenFile)) {
            fw.write(auth.getToken());
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
        File scratchDir = new File(workDir, "tmp");
        if (scratchDir.exists())
            TextUtils.deleteRecursively(scratchDir);
        scratchDir.mkdir();
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
        if (verbose)
            System.out.println("Input parameters: " + jsonString);
        Map<String, Object> rpc = new LinkedHashMap<String, Object>();
        rpc.put("version", "1.1");
        rpc.put("method", methodName);
        List<UObject> params = UObject.getMapper().readValue(jsonString, 
                new TypeReference<List<UObject>>() {});
        rpc.put("params", params);
        rpc.put("context", new LinkedHashMap<String, Object>());
        UObject.getMapper().writeValue(new File(workDir, "input.json"), rpc);
        ////////////////////////////////// Starting callback service //////////////////////////////
        int callbackPort = NetUtils.findFreePort();
        URL callbackUrl = CallbackServer.getCallbackUrl(callbackPort, callbackNetworks);
        Server jettyServer = null;
        if (callbackUrl != null) {
            if( System.getProperty("os.name").startsWith("Windows") ) {
                JsonServerSyslog.setStaticUseSyslog(false);
                JsonServerSyslog.setStaticMlogFile(
                        new File(workDir, "callback.log").getCanonicalPath());
            }
            CallbackServerConfig cfg = new CallbackServerConfigBuilder(
                    new URL(kbaseEndpoint), callbackUrl, runDir.toPath(),
                    new LineLogger() {
                        @Override
                        public void logNextLine(String line, boolean isError) {
                            if (isError) {
                                System.err.println(line);
                            } else {
                                System.out.println(line);
                            }
                        }
                    }).build();
            Set<String> releaseTags = new TreeSet<String>();
            if (mv.getReleaseTags() != null)
                releaseTags.addAll(mv.getReleaseTags());
            String requestedRelease = releaseTags.contains("release") ? "release" :
                (releaseTags.contains("beta") ? "beta" : "dev");
            final ModuleRunVersion runver = new ModuleRunVersion(
                    new URL(mv.getGitUrl()), new ModuleMethod(methodName),
                    mv.getGitCommitHash(), mv.getVersion(),
                    requestedRelease);
            List<String> inputWsObjects = new ArrayList<String>();
            if (provRefs != null) {
                inputWsObjects.addAll(Arrays.asList(provRefs.split(Pattern.quote(","))));
            }
            JsonServerServlet catalogSrv = new SDKCallbackServer(
                    auth, cfg, runver, params, inputWsObjects, mounts);
            jettyServer = new Server(callbackPort);
            ServletContextHandler context = new ServletContextHandler(
                    ServletContextHandler.SESSIONS);
            context.setContextPath("/");
            jettyServer.setHandler(context);
            context.addServlet(new ServletHolder(catalogSrv),"/*");
            jettyServer.start();
        } else {
            if (callbackNetworks != null && callbackNetworks.length > 0) {
                throw new IllegalStateException("No proper callback IP was found, " +
                        "please check callback_networks parameter in configuration");
            }
            System.out.println("WARNING: No callback URL was recieved " +
                    "by the job runner. Local callbacks are disabled.");
        }
        ////////////////////////////////// Running Docker /////////////////////////////////////////
        final String containerName = "local_" + moduleName.toLowerCase() + "_" + 
                System.currentTimeMillis();
        try {
            System.out.println();
            int exitCode = ProcessHelper.cmd("bash", DirUtils.getFilePath(runLocalSh),
                    callbackUrl.toExternalForm(), containerName, dockerImage, 
                    mounts.getDockerCommand()).exec(runDir).getExitCode();
            File outputTmpFile = new File(workDir, "output.json");
            if (!outputTmpFile.exists())
                throw new IllegalStateException("Output JSON file was not found");
            FinishJobParams outObj = UObject.getMapper().readValue(outputTmpFile, 
                    FinishJobParams.class);
            if (outObj.getError() != null || outObj.getResult() == null) {
                System.out.println();
                if (outObj.getError() == null) {
                    System.err.println("Unknown error (no information)");
                } else {
                    System.err.println("Error: " + outObj.getError().getMessage());
                    if (verbose && outObj.getError().getError() != null) {
                        System.err.println("Error details: \n" + outObj.getError().getError());
                    }
                }
                System.out.println();
            } else {
                String outputJson = UObject.getMapper().writeValueAsString(outObj.getResult());
                if (output != null) {
                    FileUtils.writeStringToFile(output, outputJson);
                    System.out.println("Output is saved to file: " + output.getCanonicalPath());
                } else {
                    System.out.println();
                    System.out.println("Output returned by the method:");
                    System.out.println(outputJson);
                    System.out.println();
                }
            }
            return exitCode;
        } finally {
            try {
                System.out.println("Deleteing docker container...");
                ProcessHelper.cmd("bash", DirUtils.getFilePath(runDockerSh), "rm", "-v", "-f",
                        containerName).exec(runDir);
            } catch (Exception ex) {
                System.out.println("Error deleting container [" + containerName + "]: " + 
                        ex.getMessage());
            }
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
