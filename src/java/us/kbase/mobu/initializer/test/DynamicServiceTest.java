package us.kbase.mobu.initializer.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import us.kbase.auth.AuthService;
import us.kbase.common.service.JsonClientException;
import us.kbase.common.service.JsonServerMethod;
import us.kbase.common.service.JsonServerServlet;
import us.kbase.common.service.UObject;
import us.kbase.mobu.initializer.ModuleInitializer;
import us.kbase.mobu.installer.ClientInstaller;
import us.kbase.mobu.tester.ModuleTester;
import us.kbase.mobu.util.ProcessHelper;
import us.kbase.mobu.util.TextUtils;
import us.kbase.scripts.test.TypeGeneratorTest;

public class DynamicServiceTest {

    private static final String SIMPLE_MODULE_NAME = "TestDynamic";
    private static final boolean cleanupAfterTests = true;
    
    private static List<String> createdModuleNames = new ArrayList<String>();
    private static String user;
    private static String pwd;
    private static int serviceWizardPort;
    private static Server serviceWizardJettyServer;
    private static ServiceWizardMock serviceWizard;
    
    @BeforeClass
    public static void beforeClass() throws Exception {
        Properties props = new Properties();
        InputStream is = new FileInputStream(new File("test_scripts/test.cfg"));
        props.load(is);
        is.close();
        user = props.getProperty("test.user");
        pwd = props.getProperty("test.pwd");
        serviceWizardPort = TypeGeneratorTest.findFreePort();
        serviceWizardJettyServer = new Server(serviceWizardPort);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        serviceWizardJettyServer.setHandler(context);
        serviceWizard = new ServiceWizardMock();
        context.addServlet(new ServletHolder(serviceWizard), "/*");
        serviceWizardJettyServer.start();
    }
    
    @AfterClass
    public static void tearDownModule() throws Exception {
        if (cleanupAfterTests)
            for (String moduleName : createdModuleNames)
                try {
                    deleteDir(moduleName);
                } catch (Exception ex) {
                    System.err.println("Error cleaning up module [" + 
                            moduleName + "]: " + ex.getMessage());
                }
        if (serviceWizardJettyServer != null)
            serviceWizardJettyServer.stop();
    }
    
    @After
    public void afterText() {
        System.out.println();
    }
    
    private static void deleteDir(String moduleName) throws Exception {
        File module = new File(moduleName);
        if (module.exists() && module.isDirectory())
            FileUtils.deleteDirectory(module);
    }
    
    private void init(String lang, String moduleName, File implFile, 
            String implInitText) throws Exception {
        deleteDir(moduleName);
        createdModuleNames.add(moduleName);
        ModuleInitializer initer = new ModuleInitializer(moduleName, user, lang, false);
        initer.initialize(false);
        File specFile = new File(moduleName, moduleName + ".spec");
        String specText = FileUtils.readFileToString(specFile).replace("};", 
                "funcdef run_test(string input) returns (string);\n};");
        FileUtils.writeStringToFile(specFile, specText);
        if (implFile != null && implInitText != null)
            FileUtils.writeStringToFile(implFile, implInitText);
        ProcessHelper.cmd("make").exec(new File(moduleName));
    }

    public static String runServerInDocker(File moduleDir, 
            String user, String pwd, int port) throws Exception {
        String moduleName = moduleDir.getName();
        File testCfgFile = new File(moduleDir, "test_local/test.cfg");
        String testCfgText = FileUtils.readFileToString(testCfgFile);
        testCfgText = testCfgText.replace("test_user=", "test_user=" + user);
        testCfgText = testCfgText.replace("test_password=", "test_password=" + pwd);
        testCfgText = testCfgText.replace("kbase_endpoint=https://appdev", 
                "kbase_endpoint=https://ci");
        FileUtils.writeStringToFile(testCfgFile, testCfgText);
        File tlDir = new File(moduleDir, "test_local");
        String token = AuthService.login(user, pwd).getTokenString();
        File workDir = new File(tlDir, "workdir");
        workDir.mkdir();
        File tokenFile = new File(workDir, "token");
        FileWriter fw = new FileWriter(tokenFile);
        try {
            fw.write(token);
        } finally {
            fw.close();
        }
        String endPoint = "https://ci.kbase.us/services";
        File runDockerSh = new File(tlDir, "run_docker.sh");
        ProcessHelper.cmd("chmod", "+x", runDockerSh.getCanonicalPath()).exec(tlDir);
        String imageName = "test/" + moduleName.toLowerCase() + ":latest";
        if (!ModuleTester.buildNewDockerImageWithCleanup(moduleDir, tlDir, runDockerSh, 
                imageName))
            throw new IllegalStateException("Error building docker image");
        System.out.println();
        System.out.println("Starting up dynamic service...");
        String runDockerPath = ModuleTester.getFilePath(runDockerSh);
        String workDirPath = ModuleTester.getFilePath(workDir);
        String containerName = "test_" + moduleName.toLowerCase() + "_" + 
        System.currentTimeMillis();
        ProcessHelper.cmd("bash", runDockerPath, "run", "-d", "-p", port + ":5000",
                "--dns", "8.8.8.8", "-v", workDirPath + ":/kb/module/work", 
                "--name", containerName, "-e", "KBASE_ENDPOINT=" + endPoint, imageName).exec(tlDir);
        return containerName;
    }

    private static void testDynamicClients(File moduleDir, int port, 
            String contName) throws Exception {
        try {
            String moduleName = moduleDir.getName();
            FileUtils.writeStringToFile(new File(moduleDir, "sdk.cfg"), 
                    "catalog_url=http://kbase.us");
            File specFile = new File(moduleDir, moduleName + ".spec");
            String dockerAddress = "localhost";
            String dockerHost = System.getenv("DOCKER_HOST");
            if (dockerHost != null && dockerHost.startsWith("tcp://")) {
                dockerAddress = dockerHost.substring(6).split(":")[0];
            }
            serviceWizard.fwdUrl = "http://" + dockerAddress + ":" + port;
            // Java client
            ClientInstaller clInst = new ClientInstaller(moduleDir);
            clInst.install("java", false, false, true, "dev", false, 
                    specFile.getCanonicalPath(), "lib2");
            File binDir = new File(moduleDir, "bin");
            if (!binDir.exists())
                binDir.mkdir();
            File srcDir = new File(moduleDir, "lib2/src");
            File clientJavaFile = new File(srcDir, moduleName.toLowerCase() + "/" +
                    moduleName + "Client.java");
            String classPath = System.getProperty("java.class.path");
            ProcessHelper.cmd("javac", "-g:source,lines", "-d", binDir.getCanonicalPath(), 
                    "-sourcepath", srcDir.getCanonicalPath(), "-cp", classPath, 
                    "-Xlint:deprecation").add(clientJavaFile.getCanonicalPath())
                    .exec(moduleDir);
            List<URL> cpUrls = new ArrayList<URL>();
            cpUrls.add(binDir.toURI().toURL());
            URLClassLoader urlcl = URLClassLoader.newInstance(cpUrls.toArray(
                    new URL[cpUrls.size()]));
            String clientClassName = moduleName.toLowerCase() + "." + moduleName + "Client";
            Class<?> clientClass = urlcl.loadClass(clientClassName);
            Object client = clientClass.getConstructor(URL.class)
                    .newInstance(new URL("http://localhost:" + serviceWizardPort));
            Method method = null;
            for (Method m : client.getClass().getMethods())
                if (m.getName().equals("runTest"))
                    method = m;
            String input = "Super-string";
            Object obj = null;
            Exception error = null;
            for (int i = 0; i < 10; i++) {
                try {
                    obj = method.invoke(client, input, null);
                    error = null;
                    break;
                } catch (Exception ex) {
                    error = ex;
                }
                Thread.sleep(100);
            }
            if (error != null)
                throw error;
            Assert.assertNotNull(obj);
            Assert.assertTrue(obj instanceof String);
            Assert.assertEquals(input, obj);
            // Common non-java preparation
            Map<String, Object> config = new LinkedHashMap<String, Object>();
            config.put("package", moduleName + "Client");
            config.put("class", moduleName);
            Map<String, Object> test1 = new LinkedHashMap<String, Object>();
            test1.put("method", "run_test");
            test1.put("auth", false);
            test1.put("params", Arrays.asList(input));
            test1.put("outcome", UObject.getMapper().readValue("{\"status\":\"pass\"}", Map.class));
            config.put("tests", Arrays.asList(test1));
            File configFile = new File(moduleDir, "tests.json");
            UObject.getMapper().writeValue(configFile, config);
            File lib2 = new File(moduleDir, "lib2");
            // Python client
            clInst.install("python", false, false, true, "dev", false, 
                    specFile.getCanonicalPath(), "lib2");
            File shellFile = new File(moduleDir, "test_python_client.sh");
            List<String> lines = new ArrayList<String>(Arrays.asList("#!/bin/bash"));
            lines.add("python " + new File("test_scripts/python/test_client.py").getAbsolutePath() + 
                    " -t " + configFile.getAbsolutePath() + 
                    " -e http://localhost:" + serviceWizardPort + "/");
            TextUtils.writeFileLines(lines, shellFile);
            if (shellFile != null) {
                ProcessHelper ph = ProcessHelper.cmd("bash", shellFile.getCanonicalPath()).exec(
                        new File(lib2, moduleName).getCanonicalFile(), null, true, true);
                int exitCode = ph.getExitCode();
                if (exitCode != 0) {
                    String out = ph.getSavedOutput();
                    if (!out.isEmpty())
                        System.out.println("Python client output:\n" + out);
                    String err = ph.getSavedErrors();
                    if (!err.isEmpty())
                        System.err.println("Python client errors:\n" + err);
                }
                Assert.assertEquals("Python client exit code should be 0", 0, exitCode);
            }
            // Perl client
            Map<String, Object> config2 = new LinkedHashMap<String, Object>(config);
            config2.put("package", moduleName + "::" + moduleName + "Client");
            File configFilePerl = new File(moduleDir, "tests_perl.json");
            UObject.getMapper().writeValue(configFilePerl, config2);
            clInst.install("perl", false, false, true, "dev", false, 
                    specFile.getCanonicalPath(), "lib2");
            shellFile = new File(moduleDir, "test_perl_client.sh");
            lines = new ArrayList<String>(Arrays.asList("#!/bin/bash"));
            lines.addAll(Arrays.asList(
                    "perl " + new File("test_scripts/perl/test-client.pl").getAbsolutePath() + 
                    " -tests " + configFilePerl.getAbsolutePath() + 
                    " -endpoint http://localhost:" + serviceWizardPort + "/"
                    ));
            TextUtils.writeFileLines(lines, shellFile);
            if (shellFile != null) {
                ProcessHelper ph = ProcessHelper.cmd("bash", shellFile.getCanonicalPath()).exec(
                        lib2.getCanonicalFile(), null, true, true);
                int exitCode = ph.getExitCode();
                if (exitCode != 0) {
                    String out = ph.getSavedOutput();  //outSw.toString();
                    if (!out.isEmpty())
                        System.out.println("Perl client output:\n" + out);
                    String err = ph.getSavedErrors();  //errSw.toString();
                    if (!err.isEmpty())
                        System.err.println("Perl client errors:\n" + err);
                }
                Assert.assertEquals("Perl client exit code should be 0", 0, exitCode);
            }
            // JavaScript
            if (!TypeGeneratorTest.isCasperJsInstalled()) {
                System.err.println("- JavaScript client tests are skipped");
                return;
            }
            clInst.install("javascript", false, false, true, "dev", false, 
                    specFile.getCanonicalPath(), "lib2");
            shellFile = new File(moduleDir, "test_js_client.sh");
            lines = new ArrayList<String>(Arrays.asList("#!/bin/bash"));
            lines.addAll(Arrays.asList(
                    "casperjs test " + new File("test_scripts/js/test-client.js").getAbsolutePath() + " "
                            + "--jq=" + new File("test_scripts/js/jquery-1.10.2.min.js").getAbsolutePath() + " "
                            + "--tests=" + configFile.getAbsolutePath() + 
                            " --endpoint=http://localhost:" + serviceWizardPort + "/ --token=1"
                    ));
            TextUtils.writeFileLines(lines, shellFile);
            if (shellFile != null) {
                ProcessHelper ph = ProcessHelper.cmd("bash", shellFile.getCanonicalPath()).exec(
                        new File(lib2, moduleName), null, true, true);
                int exitCode = ph.getExitCode();
                if (exitCode != 0) {
                    String out = ph.getSavedOutput();
                    if (!out.isEmpty())
                        System.out.println("JavaScript client output:\n" + out);
                    String err = ph.getSavedErrors();
                    if (!err.isEmpty())
                        System.err.println("JavaScript client errors:\n" + err);
                }
                Assert.assertEquals("JavaScript client exit code should be 0", 0, exitCode);
            }
        } finally {
            String runDockerPath = ModuleTester.getFilePath(new File(
                    new File(moduleDir, "test_local"), "run_docker.sh"));
            ProcessHelper.cmd("bash", runDockerPath, "logs", 
                    contName).exec(moduleDir);
            ProcessHelper.cmd("bash", runDockerPath, "rm", "-v", "-f", 
                    contName).exec(moduleDir);
            System.out.println("Docker container " + contName + " was stopped and " +
            		"removed");
        }
    }

    @Test
    public void testPerlDynamicService() throws Exception {
        System.out.println("Test [testPerlDynamicService]");
        String lang = "perl";
        String moduleName = SIMPLE_MODULE_NAME + "Perl";
        File moduleDir = new File(moduleName);
        String implInit = "" +
        		"#BEGIN_HEADER\n" +
        		"#END_HEADER\n" +
        		"\n" +
        		"    #BEGIN_CONSTRUCTOR\n" +
        		"    #END_CONSTRUCTOR\n" +
        		"\n" +
        		"    #BEGIN run_test\n" +
        		"    $return = $input;\n" +
        		"    #END run_test\n";
        File implFile = new File(moduleDir, "lib/" + moduleName + "/" + 
        		moduleName + "Impl.pm");
        init(lang, moduleName, implFile, implInit);
        int port = TypeGeneratorTest.findFreePort();
        String contName = runServerInDocker(moduleDir, user, pwd, port);
        testDynamicClients(moduleDir, port, contName);
    }

    @Test
    public void testJavaDynamicService() throws Exception {
        System.out.println("Test [testJavaDynamicService]");
        String lang = "java";
        String moduleName = SIMPLE_MODULE_NAME + "Java";
        File moduleDir = new File(moduleName);
        String implInit = "" +
                "//BEGIN_HEADER\n" +
                "//END_HEADER\n" +
                "\n" +
                "    //BEGIN_CLASS_HEADER\n" +
                "    //END_CLASS_HEADER\n" +
                "\n" +
                "        //BEGIN_CONSTRUCTOR\n" +
                "        //END_CONSTRUCTOR\n" +
                "\n" +
                "        //BEGIN run_test\n" +
                "        returnVal = input;\n" +
                "        //END run_test\n";
        File implFile = new File(moduleDir, "lib/src/" + moduleName.toLowerCase() + "/" + 
                moduleName + "Server.java");
        init(lang, moduleName, implFile, implInit);
        int port = TypeGeneratorTest.findFreePort();
        String contName = runServerInDocker(moduleDir, user, pwd, port);
        testDynamicClients(moduleDir, port, contName);
    }

    @Test
    public void testPythonDynamicService() throws Exception {
        System.out.println("Test [testPythonDynamicService]");
        String lang = "python";
        String moduleName = SIMPLE_MODULE_NAME + "Python";
        File moduleDir = new File(moduleName);
        String implInit = "" +
                "#BEGIN_HEADER\n" +
                "#END_HEADER\n" +
                "\n" +
                "    #BEGIN_CLASS_HEADER\n" +
                "    #END_CLASS_HEADER\n" +
                "\n" +
                "        #BEGIN_CONSTRUCTOR\n" +
                "        #END_CONSTRUCTOR\n" +
                "\n" +
                "        #BEGIN run_test\n" +
                "        returnVal = input\n" +
                "        #END run_test\n";
        File implFile = new File(moduleDir, "lib/" + moduleName + "/" + 
                moduleName + "Impl.py");
        init(lang, moduleName, implFile, implInit);
        int port = TypeGeneratorTest.findFreePort();
        String contName = runServerInDocker(moduleDir, user, pwd, port);
        testDynamicClients(moduleDir, port, contName);
    }

    public static class ServiceWizardMock extends JsonServerServlet {
        private static final long serialVersionUID = 1L;
        
        public String fwdUrl;

        public ServiceWizardMock() {
            super("ServiceWizard");
        }

        @JsonServerMethod(rpc = "ServiceWizard.get_service_status")
        public Map<String, Object> getServiceStatus(Map<String, String> params) throws IOException, JsonClientException {
            Map<String, Object> ret = new LinkedHashMap<String, Object>();
            ret.put("url", fwdUrl);
            return ret;
        }
    }
}
