package us.kbase.mobu.tester;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.ServerSocket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.yaml.snakeyaml.Yaml;

import us.kbase.auth.AuthService;
import us.kbase.auth.AuthToken;
import us.kbase.common.executionengine.CallbackServer;
import us.kbase.common.executionengine.CallbackServerConfigBuilder;
import us.kbase.common.executionengine.LineLogger;
import us.kbase.common.executionengine.ModuleMethod;
import us.kbase.common.executionengine.ModuleRunVersion;
import us.kbase.common.executionengine.CallbackServerConfigBuilder.CallbackServerConfig;
import us.kbase.common.service.JsonServerServlet;
import us.kbase.common.service.JsonServerSyslog;
import us.kbase.common.service.UObject;
import us.kbase.mobu.util.DirUtils;
import us.kbase.mobu.util.ProcessHelper;
import us.kbase.mobu.util.TextUtils;
import us.kbase.mobu.validator.ModuleValidator;
import us.kbase.templates.TemplateFormatter;
import us.kbase.tools.WinShortPath;

public class ModuleTester {
    private File moduleDir;
    protected Map<String,Object> kbaseYmlConfig;
    private Map<String, Object> moduleContext;

    public ModuleTester() throws Exception {
        this(null);
    }
    
    public ModuleTester(File dir) throws Exception {
        moduleDir = dir == null ? DirUtils.findModuleDir() : DirUtils.findModuleDir(dir);
        String kbaseYml = TextUtils.readFileText(new File(moduleDir, "kbase.yml"));
        @SuppressWarnings("unchecked")
        Map<String,Object> config = (Map<String, Object>)new Yaml().load(kbaseYml);
        kbaseYmlConfig = config;
        moduleContext = new HashMap<String, Object>();
        moduleContext.put("module_name", kbaseYmlConfig.get("module-name"));
        moduleContext.put("module_root_path", moduleDir.getAbsolutePath());
        if (kbaseYmlConfig.get("data-version") != null) {
            moduleContext.put("data_version", kbaseYmlConfig.get("data-version"));
        }
        moduleContext.put("os_name", System.getProperty("os.name"));
    }
    
    private static void checkIgnoreLine(File f, String line) throws IOException {
        List<String> lines = new ArrayList<String>();
        if (f.exists())
            lines.addAll(FileUtils.readLines(f));
        if (!new HashSet<String>(lines).contains(line)) {
            System.out.println("Warning: file \"" + f.getName() + "\" doesn't contain \"" + line + "\" line, it will be added.");
            lines.add(line);
            FileUtils.writeLines(f, lines);
        }
    }
    
    public int runTests(String methodStoreUrl, boolean skipValidation, boolean allowSyncMethods) throws Exception {
        if (skipValidation) {
            System.out.println("Validation step is skipped");
        } else {
            ModuleValidator mv = new ModuleValidator(Arrays.asList(moduleDir.getCanonicalPath()), 
                    false, methodStoreUrl, allowSyncMethods);
            int returnCode = mv.validateAll();
            if (returnCode!=0) {
                System.out.println("You can skip validation step using -s (or --skip_validation) flag");
                System.exit(returnCode);
            }
        }
        String testLocal = "test_local";
        checkIgnoreLine(new File(moduleDir, ".gitignore"), testLocal);
        checkIgnoreLine(new File(moduleDir, ".dockerignore"), testLocal);
        File tlDir = new File(moduleDir, testLocal);
        File readmeFile = new File(tlDir, "readme.txt");
        File testCfg = new File(tlDir, "test.cfg");
        File runTestsSh = new File(tlDir, "run_tests.sh");
        File runBashSh = new File(tlDir, "run_bash.sh");
        File runDockerSh = new File(tlDir, "run_docker.sh");
        if (!tlDir.exists())
            tlDir.mkdir();
        if (!readmeFile.exists())
            TemplateFormatter.formatTemplate("module_readme_test_local", moduleContext, true, readmeFile);
        if (kbaseYmlConfig.get("data-version") != null) {
            File refDataDir = new File(tlDir, "refdata");
            if (!refDataDir.exists()) {
                TemplateFormatter.formatTemplate("module_run_tests", moduleContext, true, runTestsSh);
                refDataDir.mkdir();
            }
        }
        if (!runTestsSh.exists())
            TemplateFormatter.formatTemplate("module_run_tests", moduleContext, true, runTestsSh);
        if (!runBashSh.exists())
            TemplateFormatter.formatTemplate("module_run_bash", moduleContext, true, runBashSh);
        if (!runDockerSh.exists())
            TemplateFormatter.formatTemplate("module_run_docker", moduleContext, true, runDockerSh);
        if (!testCfg.exists()) {
            TemplateFormatter.formatTemplate("module_test_cfg", moduleContext, true, testCfg);
            System.out.println("Set KBase account credentials in test_local/test.cfg and then test again");
            return 1;
        }
        Properties props = new Properties();
        InputStream is = new FileInputStream(new File(tlDir, "test.cfg"));
        try {
            props.load(is);
        } finally {
            is.close();
        }
        String user = props.getProperty("test_user");
        String password = props.getProperty("test_password");
        if (user == null || user.trim().isEmpty()) {
            throw new IllegalStateException("Error: KBase account credentials are not set in test_local/test.cfg");
        }
        if (password == null || password.trim().isEmpty()) {
            System.out.println("You haven't preset your password in test_local/test.cfg file. Please enter it now.");
            password = new String(System.console().readPassword("Password: "));
        }
        String token = AuthService.login(user.trim(), password.trim())
                .getTokenString();
        File workDir = new File(tlDir, "workdir");
        workDir.mkdir();
        File tokenFile = new File(workDir, "token");
        FileWriter fw = new FileWriter(tokenFile);
        try {
            fw.write(token);
        } finally {
            fw.close();
        }
        String endPoint = props.getProperty("kbase_endpoint");
        if (endPoint == null)
            throw new IllegalStateException("Error: KBase services end-point is not set in test_local/test.cfg");
        String jobSrvUrl = props.getProperty("job_service_url");
        if (jobSrvUrl == null)
            jobSrvUrl = endPoint + "/userandjobstate";
        String wsUrl = props.getProperty("workspace_url");
        if (wsUrl == null)
            wsUrl = endPoint + "/ws";
        String shockUrl = props.getProperty("shock_url");
        if (shockUrl == null)
            shockUrl = endPoint + "/shock-api";
        File configPropsFile = new File(workDir, "config.properties");
        PrintWriter pw = new PrintWriter(configPropsFile);
        try {
            pw.println("[global]");
            pw.println("job_service_url = " + jobSrvUrl);
            pw.println("workspace_url = " + wsUrl);
            pw.println("shock_url = " + shockUrl);
            pw.println("kbase_endpoint = " + endPoint);
        } finally {
            pw.close();
        }
        ProcessHelper.cmd("chmod", "+x", runBashSh.getCanonicalPath()).exec(tlDir);
        ProcessHelper.cmd("chmod", "+x", runDockerSh.getCanonicalPath()).exec(tlDir);
        String moduleName = (String)kbaseYmlConfig.get("module-name");
        String imageName = "test/" + moduleName.toLowerCase() + ":latest";
        if (!buildNewDockerImageWithCleanup(moduleDir, tlDir, runDockerSh, imageName))
            return 1;
        File subjobsDir = new File(tlDir, "subjobs");
        if (subjobsDir.exists())
            TextUtils.deleteRecursively(subjobsDir);
        File scratchDir = new File(workDir, "tmp");
        if (scratchDir.exists())
            TextUtils.deleteRecursively(scratchDir);
        scratchDir.mkdir();
        ///////////////////////////////////////////////////////////////////////////////////////////////
        int callbackPort = findFreePort();
        URL callbackUrl = CallbackServer.getCallbackUrl(callbackPort);
        Server jettyServer = null;
        if (callbackUrl != null) {
            if( System.getProperty("os.name").startsWith("Windows") ) {
                JsonServerSyslog.setStaticUseSyslog(false);
                JsonServerSyslog.setStaticMlogFile("callback.log");
            }
            CallbackServerConfig cfg = new CallbackServerConfigBuilder(
                    new URL(endPoint), callbackUrl, tlDir.toPath(),
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
                    new AuthToken(token), cfg, runver, new ArrayList<UObject>(),
                    new ArrayList<String>());
            jettyServer = new Server(callbackPort);
            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.setContextPath("/");
            jettyServer.setHandler(context);
            context.addServlet(new ServletHolder(catalogSrv),"/*");
            jettyServer.start();
        } else {
            System.out.println("WARNING: No callback URL was recieved " +
                    "by the job runner. Local callbacks are disabled.");
        }
        ///////////////////////////////////////////////////////////////////////////////////////////////
        try {
            System.out.println();
            ProcessHelper.cmd("chmod", "+x", runTestsSh.getCanonicalPath()).exec(tlDir);
            int exitCode = ProcessHelper.cmd("bash", getFilePath(runTestsSh),
                    callbackUrl.toExternalForm()).exec(tlDir).getExitCode();
            return exitCode;
        } finally {
            if (jettyServer != null) {
                System.out.println("Shutting down callback server...");
                jettyServer.stop();
            }
        }
    }

    public static boolean buildNewDockerImageWithCleanup(File moduleDir, File tlDir,
            File runDockerSh, String imageName) throws Exception {
        System.out.println();
        System.out.println("Delete old Docker containers");
        String runDockerPath = getFilePath(runDockerSh);
        List<String> lines = exec(tlDir, "bash", getFilePath(runDockerSh), "ps", "-a");
        for (String line : lines) {
            String[] parts = splitByWhiteSpaces(line);
            if (parts[1].equals(imageName)) {
                String cntId = parts[0];
                ProcessHelper.cmd("bash", runDockerPath, "rm", "-v", "-f", cntId).exec(tlDir);
            }
        }
        String oldImageId = findImageIdByName(tlDir, imageName, runDockerSh);    
        System.out.println();
        System.out.println("Build Docker image");
        boolean ok = buildImage(moduleDir, imageName, runDockerSh);
        if (!ok)
            return false;
        if (oldImageId != null) {
            String newImageId = findImageIdByName(tlDir, imageName, runDockerSh);
            if (!newImageId.equals(oldImageId)) {  // It's not the same image (not all layers are cached)
                System.out.println("Delete old Docker image");
                ProcessHelper.cmd("bash", runDockerPath, "rmi", oldImageId).exec(tlDir);
            }
        }
        return true;
    }
    
    private static int findFreePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (IOException e) {}
        throw new IllegalStateException("Can not find available port in system");
    }
    
    public static String findImageIdByName(File tlDir, String imageName,
            File runDockerSh) throws Exception {
        List<String> lines;
        String ret = null;
        lines = exec(tlDir, "bash", getFilePath(runDockerSh), "images");
        for (String line : lines) {
            String[] parts = splitByWhiteSpaces(line);
            String name = parts[0] + ":" + parts[1];
            if (name.equals(imageName)) {
                ret = parts[2];
                break;
            }
        }
        return ret;
    }

    public static String[] splitByWhiteSpaces(String line) {
        String[] parts = line.split("\\s+");
        return parts;
    }
    
    private static List<String> exec(File workDir, String... cmd) throws Exception {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ProcessHelper.cmd(cmd).exec(workDir, null, pw, pw);
        pw.close();
        List<String> ret = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new StringReader(sw.toString()));
        while (true) {
            String l = br.readLine();
            if (l == null)
                break;
            ret.add(l);
        }
        br.close();
        return ret;
    }
    
    public static boolean buildImage(File repoDir, String targetImageName, 
            File runDockerSh) throws Exception {
        String scriptPath = getFilePath(runDockerSh);
        String repoPath = getFilePath(repoDir);
        Process p = Runtime.getRuntime().exec(new String[] {"bash", 
                scriptPath, "build", "--rm", "-t", 
                targetImageName, repoPath});
        List<Thread> workers = new ArrayList<Thread>();
        InputStream[] inputStreams = new InputStream[] {p.getInputStream(), p.getErrorStream()};
        final String[] cntIdToDelete = {null};
        final String[] imageIdToDelete = {null};
        for (int i = 0; i < inputStreams.length; i++) {
            final InputStream is = inputStreams[i];
            final boolean isError = i == 1;
            Thread ret = new Thread(new Runnable() {
                public void run() {
                    try {
                        BufferedReader br = new BufferedReader(new InputStreamReader(is));
                        while (true) {
                            String line = br.readLine();
                            if (line == null)
                                break;
                            if (isError) {
                                System.err.println(line);
                            } else {
                                System.out.println(line);
                                if (line.startsWith(" ---> Running in ")) {
                                    String[] parts = splitByWhiteSpaces(line.trim());
                                    if (parts.length > 3) {
                                        String cntId = parts[parts.length - 1];
                                        cntIdToDelete[0] = cntId;
                                    }
                                } else if (line.startsWith(" ---> ")) {
                                    String[] parts = splitByWhiteSpaces(line.trim());
                                    if (parts.length > 1) {
                                        String imageId = parts[parts.length - 1];
                                        imageIdToDelete[0] = imageId;
                                    }
                                }
                            }
                        }
                        br.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new IllegalStateException("Error reading data from executed container", e);
                    }
                }
            });
            ret.start();
            workers.add(ret);
        }
        for (Thread t : workers)
            t.join();
        p.waitFor();
        int exitCode = p.exitValue();
        if (exitCode != 0) {
            try {
                if (cntIdToDelete[0] != null) {
                    System.out.println("Cleaning up building container: " + cntIdToDelete[0]);
                    Thread.sleep(1000);
                    ProcessHelper.cmd("bash", scriptPath, 
                            "rm", "-v", "-f", cntIdToDelete[0]).exec(repoDir);
                }
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
        }
        return exitCode == 0;
    }

    public static String getFilePath(File f) throws Exception {
        boolean isWindows = System.getProperty("os.name").startsWith("Windows");
        String ret = f.getCanonicalPath();
        if (isWindows)
            ret = WinShortPath.getWinShortPath(ret);
        return ret;
    }
}
