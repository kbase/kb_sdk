package us.kbase.mobu.initializer.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import us.kbase.auth.AuthService;
import us.kbase.auth.AuthToken;
import us.kbase.mobu.initializer.ModuleInitializer;
import us.kbase.mobu.installer.ClientInstaller;
import us.kbase.mobu.tester.ModuleTester;
import us.kbase.mobu.util.ProcessHelper;
import us.kbase.scripts.test.TypeGeneratorTest;

public class DynamicServiceTest {

    private static final String SIMPLE_MODULE_NAME = "TestDynamic";
    private static final boolean cleanupAfterTests = false;
    
    private static List<String> createdModuleNames = new ArrayList<String>();
    private static String user;
    private static String pwd;
    
    @BeforeClass
    public static void beforeClass() throws Exception {
        Properties props = new Properties();
        InputStream is = new FileInputStream(new File("test_scripts/test.cfg"));
        props.load(is);
        is.close();
        user = props.getProperty("test.user");
        pwd = props.getProperty("test.pwd");
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
        File configPropsFile = new File(workDir, "config.properties");
        PrintWriter pw = new PrintWriter(configPropsFile);
        try {
            pw.println("[global]");
            pw.println("kbase_endpoint = " + endPoint);
        } finally {
            pw.close();
        }
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
                "--name", containerName, imageName).exec(tlDir);
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
            String srvUrl = "http://" + dockerAddress + ":" + port;
            System.out.println("Service URL: " + srvUrl);
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
                    .newInstance(new URL(srvUrl));
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
                    ex.printStackTrace();
                }
                Thread.sleep(100);
            }
            if (error != null)
                throw error;
            Assert.assertNotNull(obj);
            Assert.assertTrue(obj instanceof String);
            Assert.assertEquals(input, obj);
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

}
