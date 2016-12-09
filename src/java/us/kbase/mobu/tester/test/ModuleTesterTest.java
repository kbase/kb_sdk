package us.kbase.mobu.tester.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.ini4j.Ini;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import us.kbase.common.test.TestException;
import us.kbase.mobu.ModuleBuilder;
import us.kbase.mobu.initializer.ModuleInitializer;
import us.kbase.mobu.initializer.test.DockerClientServerTester;
import us.kbase.mobu.tester.ModuleTester;

public class ModuleTesterTest {

	private static final String SIMPLE_MODULE_NAME = "ASimpleModule_for_unit_testing";
	private static final boolean cleanupAfterTests = true;
	
	private static final String TEST_CFG = "kb_sdk_test";
	
	private static List<String> createdModuleNames = new ArrayList<String>();
	private static String user;
	private static String pwd;
	
    @BeforeClass
    public static void beforeClass() throws Exception {
        final Ini testini = new Ini(new File("test_scripts/test.cfg"));
        user = testini.get(TEST_CFG, "test.user");
        pwd = testini.get(TEST_CFG, "test.pwd");
        if (user == null || user.isEmpty() || pwd == null || pwd.isEmpty()) {
            throw new TestException("missing user and / or pws from test cfg");
        }
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
	
    private void init(String lang, String moduleName) throws Exception {
        deleteDir(moduleName);
        createdModuleNames.add(moduleName);
        ModuleInitializer initer = new ModuleInitializer(moduleName, user, lang, false);
        initer.initialize(true);
    }

	private int runTestsInDocker(String moduleName) throws Exception {
	    File moduleDir = new File(moduleName);
	    return runTestsInDocker(moduleDir, user, pwd);
	}
	
	public static int runTestsInDocker(File moduleDir, String user, String pwd) throws Exception {
	    return runTestsInDocker(moduleDir, user, pwd, false);
	}

	public static int runTestsInDocker(File moduleDir, String user, String pwd, 
	        boolean skipValidation) throws Exception {
	    DockerClientServerTester.correctDockerfile(moduleDir);
	    File testCfgFile = new File(moduleDir, "test_local/test.cfg");
	    String testCfgText = FileUtils.readFileToString(testCfgFile);
	    testCfgText = testCfgText.replace("test_user=", "test_user=" + user);
        testCfgText = testCfgText.replace("test_password=", "test_password=" + pwd);
        testCfgText = testCfgText.replace("kbase_endpoint=https://appdev", 
                "kbase_endpoint=https://ci");
        FileUtils.writeStringToFile(testCfgFile, testCfgText);
	    int exitCode = new ModuleTester(moduleDir).runTests(ModuleBuilder.DEFAULT_METHOD_STORE_URL,
	            skipValidation, false);
	    System.out.println("Exit code: " + exitCode);
	    return exitCode;
	}
	
	@Test
	public void testPerlModuleExample() throws Exception {
	    System.out.println("Test [testPerlModuleExample]");
		String lang = "perl";
		String moduleName = SIMPLE_MODULE_NAME + "Perl";
		init(lang, moduleName);
		int exitCode = runTestsInDocker(moduleName);
		Assert.assertEquals(0, exitCode);
	}

	@Test
	public void testPerlModuleError() throws Exception {
	    System.out.println("Test [testPerlModuleError]");
	    String lang = "perl";
	    String moduleName = SIMPLE_MODULE_NAME + "PerlError";
	    init(lang, moduleName);
	    File implFile = new File(moduleName + "/lib/" + moduleName + "/" + moduleName + "Impl.pm");
	    String implText = FileUtils.readFileToString(implFile);
	    implText = implText.replace("    #BEGIN filter_contigs", 
	            "    #BEGIN filter_contigs\n" +
	            "    die \"Special error\";");
	    FileUtils.writeStringToFile(implFile, implText);
	    int exitCode = runTestsInDocker(moduleName);
	    Assert.assertEquals(2, exitCode);
	}

	@Test
	public void testPythonModuleExample() throws Exception {
	    System.out.println("Test [testPythonModuleExample]");
	    String lang = "python";
	    String moduleName = SIMPLE_MODULE_NAME + "Python";
	    init(lang, moduleName);
	    int exitCode = runTestsInDocker(moduleName);
        Assert.assertEquals(0, exitCode);
	}

	@Test
	public void testPythonModuleError() throws Exception {
        System.out.println("Test [testPythonModuleError]");
		String lang = "python";
        String moduleName = SIMPLE_MODULE_NAME + "PythonError";
        init(lang, moduleName);
        File implFile = new File(moduleName + "/lib/" + moduleName + "/" + moduleName + "Impl.py");
        String implText = FileUtils.readFileToString(implFile);
        implText = implText.replace("    #BEGIN filter_contigs", 
                "        #BEGIN filter_contigs\n" +
                "        raise ValueError('Special error')");
        FileUtils.writeStringToFile(implFile, implText);
        int exitCode = runTestsInDocker(moduleName);
        Assert.assertEquals(2, exitCode);
	}

	@Test
	public void testJavaModuleExample() throws Exception {
        System.out.println("Test [testJavaModuleExample]");
		String lang = "java";
        String moduleName = SIMPLE_MODULE_NAME + "Java";
        init(lang, moduleName);
        int exitCode = runTestsInDocker(moduleName);
        Assert.assertEquals(0, exitCode);
	}

	@Test
	public void testJavaModuleError() throws Exception {
	    System.out.println("Test [testJavaModuleError]");
	    String lang = "java";
	    String moduleName = SIMPLE_MODULE_NAME + "JavaError";
	    init(lang, moduleName);
        File implFile = new File(moduleName + "/lib/src/" +
        		"asimplemoduleforunittestingjavaerror/ASimpleModuleForUnitTestingJavaErrorServer.java");
        String implText = FileUtils.readFileToString(implFile);
        implText = implText.replace("        //BEGIN filter_contigs", 
                "        //BEGIN filter_contigs\n" +
                "        if (true) throw new IllegalStateException(\"Special error\");");
        FileUtils.writeStringToFile(implFile, implText);
	    int exitCode = runTestsInDocker(moduleName);
	    Assert.assertEquals(2, exitCode);
	}

    @Test
    public void testRModuleExample() throws Exception {
        System.out.println("Test [testRModuleExample]");
        String lang = "r";
        String moduleName = SIMPLE_MODULE_NAME + "R";
        init(lang, moduleName);
        int exitCode = runTestsInDocker(moduleName);
        Assert.assertEquals(0, exitCode);
    }

    @Test
    public void testRModuleError() throws Exception {
        System.out.println("Test [testRModuleError]");
        String lang = "r";
        String moduleName = SIMPLE_MODULE_NAME + "RError";
        init(lang, moduleName);
        File implFile = new File(moduleName + "/lib/" + moduleName + "/" + moduleName + "Impl.r");
        String implText = FileUtils.readFileToString(implFile);
        implText = implText.replace("    #BEGIN count_contigs", 
                "    #BEGIN count_contigs\n" +
                "    stop(\"Special error\")");
        FileUtils.writeStringToFile(implFile, implText);
        int exitCode = runTestsInDocker(moduleName);
        Assert.assertEquals(2, exitCode);
    }
    
    @Test
    public void testSelfCalls() throws Exception {
        System.out.println("Test [testSelfCalls]");
        String lang = "python";
        String moduleName = SIMPLE_MODULE_NAME + "Self";
        deleteDir(moduleName);
        createdModuleNames.add(moduleName);
        String implInit = "" +
                "#BEGIN_HEADER\n" +
                "import os\n"+
                "from " + moduleName + "." + moduleName + "Client import " + moduleName + " as local_client\n" +
                "#END_HEADER\n" +
                "\n" +
                "    #BEGIN_CLASS_HEADER\n" +
                "    #END_CLASS_HEADER\n" +
                "\n" +
                "        #BEGIN_CONSTRUCTOR\n" +
                "        #END_CONSTRUCTOR\n" +
                "\n" +
                "        #BEGIN run_local\n" +
                "        returnVal = local_client(os.environ['SDK_CALLBACK_URL']).calc_square(input)\n" +
                "        #END run_local\n" +
                "\n" +
                "        #BEGIN calc_square\n" +
                "        returnVal = input * input\n" +
                "        #END calc_square\n";
        File moduleDir = new File(moduleName);
        File implFile = new File(moduleDir, "lib/" + moduleName + "/" + 
                moduleName + "Impl.py");
        ModuleInitializer initer = new ModuleInitializer(moduleName, user, lang, false);
        initer.initialize(false);
        File specFile = new File(moduleDir, moduleName + ".spec");
        String specText = FileUtils.readFileToString(specFile).replace("};", 
                "funcdef run_local(int input) returns (int) authentication required;\n" +
                "funcdef calc_square(int input) returns (int) authentication required;\n" +
                "};");
        File testFile = new File(moduleDir, "test/" + moduleName + "_server_test.py");
        String testCode = FileUtils.readFileToString(testFile).replace("    def test_your_method(self):", 
                "    def test_your_method(self):\n" +
                "        self.assertEqual(25, self.getImpl().run_local(self.getContext(), 5)[0])"
        );
        FileUtils.writeStringToFile(specFile, specText);
        FileUtils.writeStringToFile(implFile, implInit);
        FileUtils.writeStringToFile(testFile, testCode);
        int exitCode = runTestsInDocker(moduleDir, user, pwd, true);
        Assert.assertEquals(0, exitCode);
   }
}