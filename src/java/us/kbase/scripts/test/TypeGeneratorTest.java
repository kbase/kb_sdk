package us.kbase.scripts.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.ini4j.Ini;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;

import us.kbase.auth.AuthService;
import us.kbase.common.service.JsonClientException;
import us.kbase.common.service.JsonServerMethod;
import us.kbase.common.service.JsonServerServlet;
import us.kbase.common.service.ServerException;
import us.kbase.common.service.UObject;
import us.kbase.common.test.TestException;
import us.kbase.kbasejobservice.KBaseJobServiceServer;
import us.kbase.kidl.KbFuncdef;
import us.kbase.kidl.KbService;
import us.kbase.kidl.KidlParser;
import us.kbase.kidl.test.KidlTest;
import us.kbase.mobu.compiler.JavaData;
import us.kbase.mobu.compiler.JavaFunc;
import us.kbase.mobu.compiler.JavaModule;
import us.kbase.mobu.compiler.JavaTypeGenerator;
import us.kbase.mobu.compiler.PrevCodeParser;
import us.kbase.mobu.compiler.RunCompileCommand;
import us.kbase.mobu.util.DiskFileSaver;
import us.kbase.mobu.util.OneFileSaver;
import us.kbase.mobu.util.ProcessHelper;
import us.kbase.mobu.util.TextUtils;

/**
 * Main test class for JavaTypeGenerator. It contains 10 tests checking different cases 
 * including basic primitive, collection and structure types, authentication, #includes,
 * syslog, documentation and GWT-stubs. 
 * @author rsutormin
 */
public class TypeGeneratorTest extends Assert {
    
    //TODO TESTING test client with python3.
    // might be as simple as running the client test script with python3 vs
    // python.
    //TODO TESTING test python client with dynamic services
    // probably best way is with a mock service wizard that returns a url to
    // the running service
    //TODO TESTING pep8 test? Not really sure about this.
    
	public static final String rootPackageName = "us.kbase";
    public static final String tempDirName = "temp_test";
    private static final String TEST_CFG = "kb_sdk_test";
    
    private static Boolean isCasperJsInstalled = null;
    private static String token = null;
    public static boolean debugClientTimes = false;
	
	public static void main(String[] args) throws Exception{
		int testNum = Integer.parseInt(args[0]);
		if (testNum == 5) {
			new TypeGeneratorTest().testSyslog();
		} else if (testNum == 6) {
			new TypeGeneratorTest().testAuth();
		} else if (testNum == 8) {
			new TypeGeneratorTest().testServerCodeStoring();
		} else if (testNum == 9 || testNum == 10) {
			startTest(testNum, false);
		} else {
			startTest(testNum);
		}
	}
	
    @BeforeClass
    public static void prepareTestConfigParams() throws Exception {
        final Ini testini = new Ini(new File("test_scripts/test.cfg"));
        for (Object key: testini.get(TEST_CFG).keySet()) {
            String prop = key.toString();
            String value = testini.get(TEST_CFG, key);
            System.setProperty(prop, value);
        }
        final String user = System.getProperty("test.user");
        final String pwd = System.getProperty("test.pwd");
        if (user == null || user.isEmpty() || pwd == null || pwd.isEmpty()) {
            throw new TestException("missing user and / or pws from test cfg");
        }
        suppressJettyLogging();
	}

    public static void suppressJettyLogging() {
        Log.setLog(new Logger() {
            @Override
            public void warn(String arg0, Object arg1, Object arg2) {}
            @Override
            public void warn(String arg0, Throwable arg1) {}
            @Override
            public void warn(String arg0) {}
            @Override
            public void setDebugEnabled(boolean arg0) {}
            @Override
            public boolean isDebugEnabled() {
                return false;
            }
            @Override
            public void info(String arg0, Object arg1, Object arg2) {}
            @Override
            public void info(String arg0) {}
            @Override
            public String getName() {
                return null;
            }
            @Override
            public Logger getLogger(String arg0) {
                return this;
            }
            @Override
            public void debug(String arg0, Object arg1, Object arg2) {}
            @Override
            public void debug(String arg0, Throwable arg1) {}
            @Override
            public void debug(String arg0) {}
        });
    }
	
	@Before
	public void beforeCleanup() {
	    System.clearProperty("KB_JOB_CHECK_WAIT_TIME");
	}
	
	@Test
	public void testSimpleTypesAndStructures() throws Exception {
		startTest(1);
	}

	@Test
	public void testIncludsAndMultiModules() throws Exception {
		startTest(2);
	}

	@Test
	public void testTuples() throws Exception {
		startTest(3);
	}

	@Test
	public void testObject() throws Exception {
		startTest(4);
	}

	@Test
	public void testSyslog() throws Exception {
		int testNum = 5;
		File workDir = prepareWorkDir(testNum);
		System.out.println();
		System.out.println("Test " + testNum + " (testSyslog) is starting in directory: " + workDir.getName());
		String testPackage = rootPackageName + ".test" + testNum;
		File srcDir = new File(workDir, "src");
		File libDir = new File(workDir, "lib");
		File binDir = new File(workDir, "bin");
		JavaData parsingData = prepareJavaCode(testNum, workDir, testPackage, libDir, binDir, null, true);
		javaServerCorrectionForTestCallback(srcDir, testPackage, parsingData, testPackage + ".Test" + testNum);
		String classPath = prepareClassPath(libDir, new ArrayList<URL>());
    	runJavac(workDir, srcDir, classPath, binDir, "src/us/kbase/test5/syslogtest/SyslogTestServer.java");
        int portNum = findFreePort();
		runJavaServerTest(testNum, true, testPackage, libDir, binDir, parsingData, null, portNum);
	}
	
	@Test
	public void testAuth() throws Exception {
		int testNum = 6;
		File workDir = prepareWorkDir(testNum);
		System.out.println();
		System.out.println("Test " + testNum + " (testAuth) is starting in directory: " + workDir.getAbsolutePath());
		String testPackage = rootPackageName + ".test" + testNum;
		File libDir = new File(workDir, "lib");
		File binDir = new File(workDir, "bin");
        int portNum = findFreePort();
		JavaData parsingData = prepareJavaCode(testNum, workDir, testPackage, libDir, binDir, portNum, true);
		File serverOutDir = preparePerlAndPyServerCode(testNum, workDir, false);
		runPerlServerTest(testNum, true, workDir, testPackage, libDir, binDir, parsingData, serverOutDir, false, portNum);
        portNum = findFreePort();
        parsingData = prepareJavaCode(testNum, workDir, testPackage, libDir, binDir, portNum, true);
        serverOutDir = preparePerlAndPyServerCode(testNum, workDir, true);
        runPerlServerTest(testNum, true, workDir, testPackage, libDir, binDir, parsingData, serverOutDir, true, portNum);
        portNum = findFreePort();
		parsingData = prepareJavaCode(testNum, workDir, testPackage, libDir, binDir, portNum, true);
		serverOutDir = preparePerlAndPyServerCode(testNum, workDir, false);
		runPythonServerTest(testNum, true, workDir, testPackage, libDir, binDir, parsingData, serverOutDir, false, portNum);
        portNum = findFreePort();
        parsingData = prepareJavaCode(testNum, workDir, testPackage, libDir, binDir, portNum, true);
        serverOutDir = preparePerlAndPyServerCode(testNum, workDir, true);
        runPythonServerTest(testNum, true, workDir, testPackage, libDir, binDir, parsingData, serverOutDir, true, portNum);
        portNum = findFreePort();
		parsingData = prepareJavaCode(testNum, workDir, testPackage, libDir, binDir, portNum, true);
		runJavaServerTest(testNum, true, testPackage, libDir, binDir, parsingData, serverOutDir, portNum);
	}

	@Test
	public void testEmptyArgsAndReturns() throws Exception {
		startTest(7);
	}
	
	@Test
	public void testServerCodeStoring() throws Exception {
	    ////////////////////////////////////// Java ///////////////////////////////////////
		int testNum = 8;
		File workDir = prepareWorkDir(testNum);
		System.out.println();
		System.out.println("Test " + testNum + " (testServerCodeStoring) is staring in directory: " + workDir.getName());
		String testFileName = "test" + testNum + ".spec";
		extractSpecFiles(testNum, workDir, testFileName);
		File srcDir = new File(workDir, "src");
		String testPackage = rootPackageName + ".test" + testNum;
    	String serverFilePath = "src/" + testPackage.replace('.', '/') + "/storing/StoringServer.java";
        File serverJavaFile = new File(workDir, serverFilePath);
        serverJavaFile.getParentFile().mkdirs();
        serverJavaFile.createNewFile();
		File libDir = new File(workDir, "lib");
		String gwtPackageName = getGwtPackageName(testNum);
        // Test for empty server file
		try {
			JavaTypeGenerator.processSpec(new File(workDir, testFileName),
					srcDir, testPackage, true, libDir, gwtPackageName, null);
		} catch (Exception ex) {
			boolean key = ex.getMessage().contains("Missing header in original file");
			if (!key)
				ex.printStackTrace();
			Assert.assertTrue(key);
		}
        String testJavaResource = "Test" + testNum + ".java.properties";
        InputStream testClassIS = TypeGeneratorTest.class.getResourceAsStream(testJavaResource);
        if (testClassIS == null) {
        	Assert.fail("Java test class resource was not found: " + testJavaResource);
        }
        TextUtils.copyStreams(testClassIS, new FileOutputStream(serverJavaFile));
        // Test for full server file
		JavaData parsingData = JavaTypeGenerator.processSpec(
				new File(workDir, testFileName), srcDir, testPackage,
				true, libDir, gwtPackageName, null);
		List<URL> cpUrls = new ArrayList<URL>();
		String classPath = prepareClassPath(libDir, cpUrls);
		File binDir = new File(workDir, "bin");
        cpUrls.add(binDir.toURI().toURL());
		compileModulesIntoBin(workDir, srcDir, testPackage, parsingData, classPath, binDir);
		for (JavaModule module : parsingData.getModules())
        	createServerServletInstance(module, libDir, binDir, testPackage);
		String text = TextUtils.readFileText(serverJavaFile);
		Assert.assertTrue(text.contains("* Header comment."));
		Assert.assertTrue(text.contains("private int myValue = -1;"));
		Assert.assertTrue(text.contains("myValue = 0;"));
		Assert.assertTrue(text.contains("myValue = 1;"));
		Assert.assertTrue(text.contains("myValue = 2;"));
        Assert.assertTrue(text.contains("myValue = 3;"));
        Assert.assertTrue(text.contains("myValue = 4;"));
		/////////////////////////////// Perl and python ////////////////////////////////////
        for (int i = 0; i < 2; i++) {
            boolean newStyle = i == 1;
            File serverOutDir = preparePerlAndPyServerCode(testNum, workDir, newStyle);
            String implName = parsingData.getModules().get(0).getOriginal().getModuleName() + "Impl";
            File perlImplFile = new File(serverOutDir, implName + ".pm");
            TextUtils.copyStreams(TypeGeneratorTest.class.getResourceAsStream(
                    "Test" + testNum + ".perl.properties"), new FileOutputStream(perlImplFile));
            File pythonImplFile = new File(serverOutDir, implName + ".py");
            TextUtils.copyStreams(TypeGeneratorTest.class.getResourceAsStream(
                    "Test" + testNum + ".python.properties"), new FileOutputStream(pythonImplFile));
            preparePerlAndPyServerCode(testNum, workDir, newStyle);
            text = TextUtils.readFileText(perlImplFile);
            Assert.assertTrue(text.contains("# Header comment."));
            Assert.assertTrue(text.contains("myValue = -1"));
            Assert.assertTrue(text.contains("myValue = 1"));
            Assert.assertTrue(text.contains("myValue = 2"));
            Assert.assertTrue(text.contains("myValue = 3"));
            Assert.assertTrue(text.contains("myValue = 4"));
            text = TextUtils.readFileText(pythonImplFile);
            Assert.assertTrue(text.contains("# Header comment."));
            Assert.assertTrue(text.contains("# Class header comment."));
            Assert.assertTrue(text.contains("myValue = -1"));
            Assert.assertTrue(text.contains("myValue = 1"));
            Assert.assertTrue(text.contains("myValue = 2"));
            Assert.assertTrue(text.contains("myValue = 3"));
            Assert.assertTrue(text.contains("myValue = 4"));
        }
        ///////////////////////////////// Windows EOL chars /////////////////////////////////
        String codeText = "" +
                "#BEGIN_HEADER\r\n" +
                "text1\r\n" +
                "#END_HEADER\r\n" +
                "\r\n" +
                "class Storing:\r\n" +
                "    #BEGIN_CLASS_HEADER\r\n" +
                "    text2\r\n" +
                "    #END_CLASS_HEADER\r\n" +
                "    \r\n" +
                "    def __init__(self, config):\r\n" +
                "        #BEGIN_CONSTRUCTOR\r\n" +
                "        text3\r\n" +
                "        #END_CONSTRUCTOR\r\n" +
                "    \r\n" +
                "    def m1(self, ctx):\r\n" +
                "        #BEGIN m1\r\n" +
                "        text4\r\n" +
                "        #END m1\r\n";
        File tempFile = new File(workDir, "test.py");
        FileUtils.write(tempFile, codeText);
        Map<String, String> prevCode = PrevCodeParser.parsePrevCode(tempFile, "#", 
                Arrays.asList("m1"), true);
        Assert.assertEquals("text1", prevCode.get(PrevCodeParser.HEADER).trim());
        Assert.assertEquals("text2", prevCode.get(PrevCodeParser.CLSHEADER).trim());
        Assert.assertEquals("text3", prevCode.get(PrevCodeParser.CONSTRUCTOR).trim());
        Assert.assertEquals("text4", prevCode.get(PrevCodeParser.METHOD + "m1").trim());
	}

	@Test
	public void testGwtTransform() throws Exception {
		startTest(9, false);
	}

	@Test
	public void testComments() throws Exception {
		startTest(10, false);
	}

	@Test
	public void testIncludsAndMultiModules2() throws Exception {
	    startTest(11);
	}

    @Test
    public void testAsyncMethods() throws Exception {
	    int testNum = 12;
	    File workDir = prepareWorkDir(testNum);
	    System.out.println();
	    System.out.println("Test " + testNum + " (testAsyncMethods) is starting in directory: " + workDir.getName());
	    Server jettyServer = startJobService(workDir, workDir);
        int jobServicePort = jettyServer.getConnectors()[0].getLocalPort();
	    try {
	        String testPackage = rootPackageName + ".test" + testNum;
	        File libDir = new File(workDir, "lib");
	        File binDir = new File(workDir, "bin");
	        JavaData parsingData = prepareJavaCode(testNum, workDir, testPackage, libDir, binDir, null, true);
	        String moduleName = parsingData.getModules().get(0).getModuleName();
	        String modulePackage = parsingData.getModules().get(0).getModulePackage();
	        StringBuilder cp = new StringBuilder(binDir.getAbsolutePath());
	        for (File f : libDir.listFiles())
	            cp.append(":").append(f.getAbsolutePath());
            File serverOutDir = preparePerlAndPyServerCode(testNum, workDir, true);
	        List<String> lines = null;
            System.setProperty("KB_JOB_CHECK_WAIT_TIME", "100");
	        //////////////////////////////////////// Perl server ///////////////////////////////////////////
	        lines = new ArrayList<String>(Arrays.asList("#!/bin/bash"));
            lines.addAll(Arrays.asList(
                    "cd \"" + serverOutDir.getAbsolutePath() + "\"",
                    "perl " + findPerlServerScript(serverOutDir).getName() + " $1 $2 $3 > perl_cli.out 2> perl_cli.err"
                    ));
            TextUtils.writeFileLines(lines, new File(workDir, "run_" + moduleName + "_async_job.sh"));
            runPerlServerTest(testNum, true, workDir, testPackage, libDir, binDir, 
                    parsingData, serverOutDir, true, jobServicePort, null, null);
            //////////////////////////////////////// Python server ///////////////////////////////////////////
            lines = new ArrayList<String>(Arrays.asList("#!/bin/bash"));
            lines.addAll(Arrays.asList(
                    "cd \"" + serverOutDir.getAbsolutePath() + "\"",
                    "python " + findPythonServerScript(serverOutDir).getName() + " $1 $2 $3 > py_cli.out 2> py_cli.err"
                    ));
            TextUtils.writeFileLines(lines, new File(workDir, "run_" + moduleName + "_async_job.sh"));
	        runPythonServerTest(testNum, true, workDir, testPackage, libDir, binDir, 
	                parsingData, serverOutDir, true, jobServicePort, null, null);
            //////////////////////////////////////// Java server ///////////////////////////////////////////
            lines = new ArrayList<String>(Arrays.asList("#!/bin/bash"));
            lines.addAll(Arrays.asList(
                    "java -cp \"" + cp + "\" " + testPackage + "." + modulePackage + "." + moduleName + "Server $1 $2 $3"
                    ));
            TextUtils.writeFileLines(lines, new File(workDir, "run_" + moduleName + "_async_job.sh"));
            runJavaServerTest(testNum, true, testPackage, libDir, binDir, 
                    parsingData, serverOutDir, jobServicePort, null);
	    } finally {
	        jettyServer.stop();
	    }
	}
    
    @Test
    public void testErrors() throws Exception {
        startTest(13, true, true, true);
    }

    @Test
    public void testRpcContext() throws Exception {
        int testNum = 14;
        File workDir = prepareWorkDir(testNum);
        System.out.println();
        System.out.println("Test " + testNum + " (testRpcContext) is starting in directory: " + workDir.getName());
        String testPackage = rootPackageName + ".test" + testNum;
        File libDir = new File(workDir, "lib");
        File binDir = new File(workDir, "bin");
        int portNum = findFreePort();
        Map<String, String> serverManualCorrections = new HashMap<String, String>();
        serverManualCorrections.put("send_context", "returnVal = arg1; " +
        		"returnVal.getMethods().add(jsonRpcContext.getCallStack().get(0).getMethod()); " +
        		"returnVal.getMethods().add(jsonRpcContext.getCallStack().get(1).getMethod())");
        JavaData parsingData = prepareJavaCode(testNum, workDir, testPackage, libDir, binDir, portNum, true,
                false, serverManualCorrections);
        runJavaServerTest(testNum, true, testPackage, libDir, binDir, parsingData, null, portNum);
    }

    @Test
    public void testServerAuth() throws Exception {
        startTest(15);
    }
    
    @Test
    public void testMissingMethods() throws Exception {
        int testNum = 16;
        File workDir = prepareWorkDir(testNum);
        System.out.println();
        System.out.println("Test " + testNum + " (testMissingMethods) is starting in directory: " + workDir.getName());
        String testPackage = rootPackageName + ".test" + testNum;
        File libDir = new File(workDir, "lib");
        File binDir = new File(workDir, "bin");
        JavaData parsingData = prepareJavaCode(testNum, workDir, testPackage, libDir, binDir, null, true);
        File serverOutDir = preparePerlAndPyServerCode(testNum, workDir, true);
        runPerlServerTest(testNum, true, workDir, testPackage, libDir, binDir, parsingData, 
                serverOutDir, true, findFreePort());
        runPythonServerTest(testNum, true, workDir, testPackage, libDir, binDir, parsingData, 
                serverOutDir, true, findFreePort());
        runJavaServerTest(testNum, true, testPackage, libDir,
                binDir, parsingData, serverOutDir, findFreePort());
    }

    @Test
    public void testEmptyPackageParent() throws Exception {
        int testNum = 17;
        File workDir = prepareWorkDir(testNum);
        System.out.println();
        System.out.println("Test " + testNum + " (testEmptyPackageParent) is starting in directory: " + workDir.getName());
        String testPackage = ".";
        File libDir = new File(workDir, "lib");
        File binDir = new File(workDir, "bin");
        JavaData parsingData = prepareJavaCode(testNum, workDir, testPackage, libDir, binDir, null, true);
        runJavaServerTest(testNum, true, testPackage, libDir,
                binDir, parsingData, null, findFreePort());
    }

    @Test
    public void testProvenance() throws Exception {
        ////////////////////////////////////// Java ///////////////////////////////////////
        int testNum = 18;
        File workDir = prepareWorkDir(testNum);
        System.out.println();
        System.out.println("Test " + testNum + " (testProvenance) is staring in directory: " + workDir.getName());
        String testPackage = rootPackageName + ".test" + testNum;
        File libDir = new File(workDir, "lib");
        File binDir = new File(workDir, "bin");
        int portNum = findFreePort();
        Map<String, String> javaSrvManualCorr = new HashMap<String, String>();
        javaSrvManualCorr.put("get_prov", "us.kbase.workspace.ProvenanceAction pa = " +
        		"((java.util.List<us.kbase.workspace.ProvenanceAction>)jsonRpcContext.getProvenance()).get(0); " +
        		"returnVal = new UObject(new java.util.ArrayList<us.kbase.workspace.ProvenanceAction>(" +
        		"java.util.Arrays.asList(new us.kbase.workspace.ProvenanceAction().withService(pa.getService()).withMethod(pa.getMethod()))))");
        Map<String, String> pySrvManualCorr = new HashMap<String, String>();
        pySrvManualCorr.put("get_prov", "returnVal = [{'service':ctx['provenance'][0]['service'],'method':ctx['provenance'][0]['method']}]");
        Map<String, String> perlSrvManualCorr = new HashMap<String, String>();
        perlSrvManualCorr.put("get_prov", "$return = [{'service'=>$ctx->provenance->[0]->{'service'},'method'=>$ctx->provenance->[0]->{'method'}}]");
        if (!libDir.exists())
            libDir.mkdirs();
        JavaTypeGenerator.checkLib(new DiskFileSaver(libDir), "WorkspaceClient");
        JavaData parsingData = prepareJavaCode(testNum, workDir, testPackage, libDir, binDir, portNum, true,
                false, javaSrvManualCorr);
        File serverOutDir = preparePerlAndPyServerCode(testNum, workDir, true);
        int perlPort = findFreePort();
        runPerlServerTest(testNum, true, workDir, testPackage, libDir, binDir, parsingData, 
                serverOutDir, true, perlPort, perlPort, perlSrvManualCorr);
        int pyPort = findFreePort();
        runPythonServerTest(testNum, true, workDir, testPackage, libDir, binDir, parsingData, 
                serverOutDir, true, pyPort, pyPort, pySrvManualCorr);
        runJavaServerTest(testNum, true, testPackage, libDir, binDir, parsingData, serverOutDir, portNum);
    }

    @Test
    public void testStatus() throws Exception {
        int testNum = 19;
        File workDir = prepareWorkDir(testNum);
        System.out.println();
        System.out.println("Test " + testNum + " (testStatus) is starting in directory: " + workDir.getName());
        String testPackage = rootPackageName + ".test" + testNum;
        File libDir = new File(workDir, "lib");
        File binDir = new File(workDir, "bin");
        JavaData parsingData = prepareJavaCode(testNum, workDir, testPackage, libDir, binDir, null, true);
        File serverOutDir = preparePerlAndPyServerCode(testNum, workDir, true);
        JavaModule module = parsingData.getModules().get(0);
        File javaServerImpl = new File(workDir, "src/" + testPackage.replace('.', '/') + "/" + 
                module.getModulePackage() +"/" + getServerClassName(module) + ".java");
        checkFileForKeyword(javaServerImpl, "BEGIN_STATUS", true);
        File perlServerImpl = new File(serverOutDir, module.getOriginal().getModuleName() + "Impl.pm");
        checkFileForKeyword(perlServerImpl, "BEGIN_STATUS", true);
        File pythonServerImpl = new File(serverOutDir, module.getOriginal().getModuleName() + "Impl.py");
        checkFileForKeyword(pythonServerImpl, "BEGIN_STATUS", true);
        runPerlServerTest(testNum, true, workDir, testPackage, libDir, binDir, parsingData, 
                serverOutDir, true, findFreePort());
        runPythonServerTest(testNum, true, workDir, testPackage, libDir, binDir, parsingData, 
                serverOutDir, true, findFreePort());
        runJavaServerTest(testNum, true, testPackage, libDir,
                binDir, parsingData, serverOutDir, findFreePort());
    }

    private static void checkFileForKeyword(File f, String keyword, boolean occure) throws Exception {
        String text = FileUtils.readFileToString(f);
        Assert.assertEquals(occure, text.contains(keyword));
    }
    
    @Test
    public void testNoStatus() throws Exception {
        int testNum = 20;
        File workDir = prepareWorkDir(testNum);
        System.out.println();
        System.out.println("Test " + testNum + " (testNoStatus) is starting in directory: " + workDir.getName());
        String testPackage = rootPackageName + ".test" + testNum;
        File libDir = new File(workDir, "lib");
        File binDir = new File(workDir, "bin");
        JavaData parsingData = prepareJavaCode(testNum, workDir, testPackage, libDir, binDir, null, true);
        File serverOutDir = preparePerlAndPyServerCode(testNum, workDir, true);
        JavaModule module = parsingData.getModules().get(0);
        File javaServerImpl = new File(workDir, "src/" + testPackage.replace('.', '/') + "/" + 
                module.getModulePackage() +"/" + getServerClassName(module) + ".java");
        checkFileForKeyword(javaServerImpl, "BEGIN_STATUS", false);
        File perlServerImpl = new File(serverOutDir, module.getOriginal().getModuleName() + "Impl.pm");
        checkFileForKeyword(perlServerImpl, "BEGIN_STATUS", false);
        File pythonServerImpl = new File(serverOutDir, module.getOriginal().getModuleName() + "Impl.py");
        checkFileForKeyword(pythonServerImpl, "BEGIN_STATUS", false);
        runPerlServerTest(testNum, true, workDir, testPackage, libDir, binDir, parsingData, 
                serverOutDir, true, findFreePort());
        runPythonServerTest(testNum, true, workDir, testPackage, libDir, binDir, parsingData, 
                serverOutDir, true, findFreePort());
        runJavaServerTest(testNum, true, testPackage, libDir,
                binDir, parsingData, serverOutDir, findFreePort());
    }

    @Test
    public void testDynamicClients() throws Exception {
        int testNum = 21;
        File workDir = prepareWorkDir(testNum);
        System.out.println();
        System.out.println("Test " + testNum + " (testDynamicClients) is starting in directory: " + workDir.getName());
        String testPackage = rootPackageName + ".test" + testNum;
        File libDir = new File(workDir, "lib");
        File binDir = new File(workDir, "bin");
        JavaData parsingData = prepareJavaCode(testNum, workDir, testPackage, libDir, binDir, null, true, true);
        File serverOutDir = preparePerlAndPyServerCode(testNum, workDir, true, true);
        final int[] serverPortHolder = new int[] {-1};  // Servers should startup on this port
        // Starting up service wizard
        Server jettyServer = startServiceWizard(serverPortHolder);
        try {
            int serviceWizardPort = jettyServer.getConnectors()[0].getLocalPort();  // Clients should use it for URL lookup
            serverPortHolder[0] = findFreePort();
            runPerlServerTest(testNum, true, workDir, testPackage, libDir, binDir, parsingData, 
                    serverOutDir, true, serviceWizardPort, serverPortHolder[0], null);
            serverPortHolder[0] = findFreePort();
            runPythonServerTest(testNum, true, workDir, testPackage, libDir, binDir, parsingData, 
                    serverOutDir, true, serviceWizardPort, serverPortHolder[0], null);
            serverPortHolder[0] = findFreePort();
            runJavaServerTest(testNum, true, testPackage, libDir,
                    binDir, parsingData, serverOutDir, serviceWizardPort, serverPortHolder[0]);
        } finally {
            jettyServer.stop();
        }
    }

    private Server startJobService(File binDir, File tempDir) throws Exception {
        Server jettyServer = new Server(findFreePort());
	    ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
	    context.setContextPath("/");
	    jettyServer.setHandler(context);
	    context.addServlet(new ServletHolder(new KBaseJobServiceServer().withBinDir(binDir).withTempDir(tempDir)),"/*");
	    jettyServer.start();
        return jettyServer;
    }

    private Server startServiceWizard(int[] serverPortHolder) throws Exception {
        Server jettyServer = new Server(findFreePort());
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        jettyServer.setHandler(context);
        context.addServlet(new ServletHolder(new ServiceWizardMock(serverPortHolder)), "/*");
        jettyServer.start();
        return jettyServer;
    }

	private static void startTest(int testNum) throws Exception {
		startTest(testNum, true);
	}

	private static String getCallingMethod() {
		StackTraceElement[] st = Thread.currentThread().getStackTrace();
        int pos = 3;
		String methodName = st[pos].getMethodName();
		while (methodName.equals("startTest")) {
		    pos++;
			methodName = st[pos].getMethodName();
		}
		return methodName;
	}
	
	private static void startTest(int testNum, boolean needClientServer) throws Exception {
	    startTest(testNum, needClientServer, needClientServer, needClientServer);
	}

	private static void startTest(int testNum, boolean needJavaServer, boolean needPerlServer, boolean needPythonServer) throws Exception {
		File workDir = prepareWorkDir(testNum);
		System.out.println();
		System.out.println("Test " + testNum + " (" + getCallingMethod() +
		        ") is starting in directory: " + workDir.getAbsolutePath());
		String testPackage = rootPackageName + ".test" + testNum;
		File libDir = new File(workDir, "lib");
		File binDir = new File(workDir, "bin");
		boolean needClientServer = needJavaServer || needPerlServer || needPythonServer;
		JavaData parsingData = prepareJavaCode(testNum, workDir, testPackage, libDir, binDir, null, needClientServer);
		if (needClientServer) {
            File serverOldDir = preparePerlAndPyServerCode(testNum, workDir, false);
            File serverOutDir = preparePerlAndPyServerCode(testNum, workDir, true);
		    if (needPerlServer) {
		        runPerlServerTest(testNum, needClientServer, workDir, testPackage,
		                libDir, binDir, parsingData, serverOldDir, false, 
		                findFreePort());
		        runPerlServerTest(testNum, needClientServer, workDir, testPackage,
		                libDir, binDir, parsingData, serverOutDir, true, 
		                findFreePort());
		    }
            if (needPythonServer) {
		        runPythonServerTest(testNum, needClientServer, workDir,
		                testPackage, libDir, binDir, parsingData, serverOldDir, 
		                false, findFreePort());
		        runPythonServerTest(testNum, needClientServer, workDir,
		                testPackage, libDir, binDir, parsingData, serverOutDir, 
		                true, findFreePort());
		    }
		    if (needJavaServer)
		        runJavaServerTest(testNum, needClientServer, testPackage, libDir,
		                binDir, parsingData, serverOutDir, findFreePort());
		} else {
			runClientTest(testNum, testPackage, parsingData, libDir, binDir, -1, needClientServer, null, "no");
		}
	}

	protected static void runPythonServerTest(int testNum,
	        boolean needClientServer, File workDir, String testPackage,
	        File libDir, File binDir, JavaData parsingData, File serverOutDir,
	        boolean newStyle, int portNum) throws IOException, Exception {
	    runPythonServerTest(testNum, needClientServer, workDir, testPackage, libDir, 
	            binDir, parsingData, serverOutDir, newStyle, portNum, portNum, null);
	}

	protected static void runPythonServerTest(int testNum,
			boolean needClientServer, File workDir, String testPackage,
			File libDir, File binDir, JavaData parsingData, File serverOutDir,
			boolean newStyle, int clientPortNum, Integer serverPortNum,
			Map<String, String> serverManualCorrections) throws IOException, Exception {
		String serverType = (newStyle ? "New" : "Old") + " python";
		File pidFile = new File(serverOutDir, "pid.txt");
		pythonServerCorrection(serverOutDir, parsingData, serverManualCorrections);
		try {
            File serverFile = findPythonServerScript(serverOutDir);
            File uwsgiFile = new File(serverOutDir, "start_py_server.sh");
            List<String> lines = new ArrayList<String>(Arrays.asList("#!/bin/bash"));
            //JavaTypeGenerator.checkEnvVars(lines, "PYTHONPATH");
            lines.addAll(Arrays.asList(
                    "cd \"" + serverOutDir.getAbsolutePath() + "\"",
                    "if [ ! -d biokbase ]; then",
                    "  cp -r ../../../lib/biokbase ./",
                    "  cp -r ../../../submodules/auth/python-libs/biokbase ./",
                    "fi"
                    ));
		    if (serverPortNum != null) {
		        lines.addAll(Arrays.asList(
		                "python " + serverFile.getName() + " --host localhost --port " + serverPortNum + 
		                " >py_server.out 2>py_server.err & pid=$!",
		                "echo $pid > " + pidFile.getName()
		                ));
		        TextUtils.writeFileLines(lines, uwsgiFile);
		        ProcessHelper.cmd("bash", uwsgiFile.getCanonicalPath()).exec(serverOutDir);
		        System.out.println(serverType + " server was started up");
		    } else {
                TextUtils.writeFileLines(lines, uwsgiFile);
                ProcessHelper.cmd("bash", uwsgiFile.getCanonicalPath()).exec(serverOutDir);
		    }
			runClientTest(testNum, testPackage, parsingData, libDir, binDir, clientPortNum, needClientServer, 
			        serverOutDir, serverType);
		} finally {
			if (pidFile.exists()) {
				String pid = TextUtils.readFileLines(pidFile).get(0).trim();
				ProcessHelper.cmd("kill", pid).exec(workDir);
				System.out.println(serverType + " server was stopped");
			}
		}
	}

	protected static void runJavaServerTest(int testNum,
	        boolean needClientServer, String testPackage, File libDir,
	        File binDir, JavaData parsingData, File serverOutDir, 
	        int portNum) throws Exception {
	    runJavaServerTest(testNum, needClientServer, testPackage, libDir, binDir, 
	            parsingData, serverOutDir, portNum, portNum);
	}

	protected static void runJavaServerTest(int testNum,
			boolean needClientServer, String testPackage, File libDir,
			File binDir, JavaData parsingData, File serverOutDir, int clientPortNum,
			Integer serverPortNum) throws Exception {
		Server javaServer = null;
		try {
		    if (serverPortNum != null) {
		        JavaModule mainModule = parsingData.getModules().get(0);
		        //long time = System.currentTimeMillis();
		        javaServer = new Server(serverPortNum);
		        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		        context.setContextPath("/");
		        javaServer.setHandler(context);
		        Class<?> serverClass = createServerServletInstance(mainModule, libDir, binDir, testPackage);
		        context.addServlet(new ServletHolder(serverClass), "/*");
		        javaServer.start();
		        System.out.println("Java server was started up");
		    }
			runClientTest(testNum, testPackage, parsingData, libDir, binDir, clientPortNum, needClientServer, 
			        serverOutDir, "Java");
		} finally {
			if (javaServer != null) {
				javaServer.stop();
				System.out.println("Java server was stopped");
			}
		}
	}

	public static int findFreePort() {
	    try (ServerSocket socket = new ServerSocket(0)) {
	        return socket.getLocalPort();
	    } catch (IOException e) {}
	    throw new IllegalStateException("Can not find available port in system");
	}

	protected static void runPerlServerTest(int testNum,
	        boolean needClientServer, File workDir, String testPackage,
	        File libDir, File binDir, JavaData parsingData, File serverOutDir,
	        boolean newStyle, int portNum) throws IOException, Exception {
	    runPerlServerTest(testNum, needClientServer, workDir, testPackage, libDir, 
	            binDir, parsingData, serverOutDir, newStyle, portNum, portNum, null);
	}

	protected static void runPerlServerTest(int testNum,
			boolean needClientServer, File workDir, String testPackage,
			File libDir, File binDir, JavaData parsingData, File serverOutDir,
			boolean newStyle, int clientPortNum, Integer serverPortNum,
			Map<String, String> serverManualCorrections) throws IOException, Exception {
	    String serverType = (newStyle ? "New" : "Old") + " perl";
		perlServerCorrection(serverOutDir, parsingData, serverManualCorrections);
		File pidFile = new File(serverOutDir, "pid.txt");
		try {
            File plackupFile = new File(serverOutDir, "start_perl_server.sh");
            List<String> lines = new ArrayList<String>(Arrays.asList("#!/bin/bash"));
            //JavaTypeGenerator.checkEnvVars(lines, "PERL5LIB");
            lines.addAll(Arrays.asList(
                    "cd \"" + serverOutDir.getAbsolutePath() + "\"",
                    "if [ ! -d Bio ]; then",
                    "  cp -r ../../../lib/Bio ./",
                    "  cp -r ../../../submodules/auth/Bio-KBase-Auth/lib/Bio ./",
                    "fi"
                    ));
		    if (serverPortNum != null) {
		        lines.addAll(Arrays.asList(
		                "plackup --listen :" + serverPortNum + " service.psgi >perl_server.out 2>perl_server.err & pid=$!",
		                "echo $pid > " + pidFile.getAbsolutePath()
		                ));
		        TextUtils.writeFileLines(lines, plackupFile);
		        ProcessHelper.cmd("bash", plackupFile.getCanonicalPath()).exec(serverOutDir);
		        System.out.println(serverType + " server was started up");
		    } else {
                TextUtils.writeFileLines(lines, plackupFile);
                ProcessHelper.cmd("bash", plackupFile.getCanonicalPath()).exec(serverOutDir);
		    }
			runClientTest(testNum, testPackage, parsingData, libDir, binDir, clientPortNum, needClientServer, 
			        serverOutDir, serverType);
		} finally {
			if (pidFile.exists()) {
				String pid = TextUtils.readFileLines(pidFile).get(0).trim();
				ProcessHelper.cmd("kill", pid).exec(workDir);
				System.out.println(serverType + " server was stopped");
			}
		}
	}

	protected static File preparePerlAndPyServerCode(int testNum, File workDir, 
	        boolean newStyle) throws Exception {
	    return preparePerlAndPyServerCode(testNum, workDir, newStyle, false);
	}
	
	protected static File preparePerlAndPyServerCode(int testNum, File workDir, 
	        boolean newStyle, boolean isClientDynamic) throws Exception {
        File testFile = new File(workDir, "test" + testNum + ".spec");
        File serverOutDir = new File(workDir, newStyle ? "out" : "old");
        if (!serverOutDir.exists())
            serverOutDir.mkdir();
        // Generate servers (old or new style)
        RunCompileCommand.generate(testFile, null, true, null, 
                true, null, true, null, null, "service.psgi", false, true, 
                null, true, null, null, false, false, null, null, null, false, 
                null, false, null, false, null, null, newStyle, serverOutDir, 
                null, true, null, null, false, null, null, null);
        // Generate clients (always new style)
        RunCompileCommand.generate(testFile, null, true, null, 
                true, null, false, null, null, null, false, true, 
                null, false, null, null, false, false, null, null, null, false, 
                null, false, null, false, null, null, true, serverOutDir, null, 
                true, null, isClientDynamic ? "dev" : null, false, null, null, null);
        return serverOutDir;
	}

	protected static JavaData prepareJavaCode(int testNum, File workDir,
	        String testPackage, File libDir, File binDir, Integer defaultUrlPort,
	        boolean needJavaServerCorrection) throws Exception,
	        IOException, MalformedURLException, FileNotFoundException {
	    return prepareJavaCode(testNum, workDir, testPackage, libDir, binDir, defaultUrlPort, 
	            needJavaServerCorrection, false, null);
	}

	protected static JavaData prepareJavaCode(int testNum, File workDir,
	        String testPackage, File libDir, File binDir, Integer defaultUrlPort,
	        boolean needJavaServerCorrection, boolean isDynamic) throws Exception,
	        IOException, MalformedURLException, FileNotFoundException {
	    return prepareJavaCode(testNum, workDir, testPackage, libDir, binDir, defaultUrlPort, 
	            needJavaServerCorrection, isDynamic, null);
	}
	
	protected static JavaData prepareJavaCode(int testNum, File workDir,
			String testPackage, File libDir, File binDir, Integer defaultUrlPort,
			boolean needJavaServerCorrection, boolean isDynamic,
			Map<String, String> serverManualCorrections) throws Exception,
			IOException, MalformedURLException, FileNotFoundException {
		JavaData parsingData = null;
		String testFileName = "test" + testNum + ".spec";
		extractSpecFiles(testNum, workDir, testFileName);
		File srcDir = new File(workDir, "src");
		String gwtPackageName = getGwtPackageName(testNum);
		URL defaultUrl = defaultUrlPort == null ? null :
			new URL("http://localhost:" + defaultUrlPort);
		parsingData = processSpec(workDir, testPackage, libDir, testFileName,
				srcDir, gwtPackageName, defaultUrl, isDynamic);
		if (needJavaServerCorrection)
			javaServerCorrection(srcDir, testPackage, parsingData, serverManualCorrections);
		parsingData = processSpec(workDir, testPackage, libDir, testFileName,
				srcDir, gwtPackageName, defaultUrl, isDynamic);
		List<URL> cpUrls = new ArrayList<URL>();
		String classPath = prepareClassPath(libDir, cpUrls);
        cpUrls.add(binDir.toURI().toURL());
		compileModulesIntoBin(workDir, srcDir, testPackage, parsingData, classPath, binDir);
        String testJavaFileName = "Test" + testNum + ".java";
    	String testFilePath = "src/" + testPackage.replace('.', '/') + "/" + testJavaFileName;
        File testJavaFile = new File(workDir, testFilePath);
        String testJavaResource = testJavaFileName + ".properties";
        InputStream testClassIS = TypeGeneratorTest.class.getResourceAsStream(testJavaResource);
        if (testClassIS == null) {
        	Assert.fail("Java test class resource was not found: " + testJavaResource);
        }
        TextUtils.copyStreams(testClassIS, new FileOutputStream(testJavaFile));
    	runJavac(workDir, srcDir, classPath, binDir, testFilePath);
    	File docDir = new File(workDir, "doc");
    	docDir.mkdir();
    	List<String> docPackages = new ArrayList<String>(Arrays.asList(testPackage));
    	for (JavaModule module : parsingData.getModules())
    		docPackages.add(testPackage + "." + module.getModulePackage());
    	runJavaDoc(workDir, srcDir, classPath, docDir, docPackages.toArray(new String[docPackages.size()]));
		return parsingData;
	}

	public static JavaData processSpec(File workDir, String testPackage,
			File libDir, String testFileName, File srcDir,
			String gwtPackageName, URL defaultUrl, boolean isDynamic) throws Exception {
		File specFile = new File(workDir, testFileName);
		File typecompDir = new File(workDir, "../../typecomp").getCanonicalFile();
		Map<String, Map<String, String>> origSchemas = new LinkedHashMap<String, Map<String, String>>();
		long time1 = System.currentTimeMillis();
		Map<?,?> origMap = KidlParser.parseSpecExt(specFile, workDir, origSchemas, typecompDir);
		time1 = System.currentTimeMillis() - time1;
		Map<String, Map<String, String>> intSchemas = new LinkedHashMap<String, Map<String, String>>();
		long time2 = System.currentTimeMillis();
		Map<?,?> intMap = KidlParser.parseSpecInt(specFile, intSchemas);
		time2 = System.currentTimeMillis() - time2;
		//System.out.println("Compilation time: " + time1 + " vs " + time2);
		Assert.assertTrue(KidlTest.compareJson(origMap, intMap, "Parsing result for " + testFileName));
		Assert.assertTrue(KidlTest.compareJsonSchemas(origSchemas, intSchemas, "Json schema for " + testFileName));
		List<KbService> services = KidlParser.parseSpec(specFile, workDir, null, null, true);
		JavaData parsingData = JavaTypeGenerator.processSpec(services, new DiskFileSaver(srcDir), 
		        testPackage, true, new DiskFileSaver(libDir), gwtPackageName, defaultUrl, 
		        new OneFileSaver(new File(workDir, "build.xml")), 
		        new OneFileSaver(new File(workDir, "makefile")), null, isDynamic ? "dev" : null, null, null, null);
		return parsingData;
	}

	private static String getGwtPackageName(int testNum) {
		return rootPackageName + ".gwt";
	}
	
	private static File findPythonServerScript(File dir) {
		for (File f : dir.listFiles()) {
			if (f.getName().endsWith("Server.py"))
				return f;
		}
		throw new IllegalStateException("Can not find python server script");
	}

	private static File findPerlServerScript(File dir) {
	    for (File f : dir.listFiles()) {
	        if (f.getName().endsWith("Server.pm"))
	            return f;
	    }
	    throw new IllegalStateException("Can not find perl server script");
	}

	private static void compileModulesIntoBin(File workDir, File srcDir, String testPackage, 
			JavaData parsingData, String classPath, File binDir) throws IOException, MalformedURLException {
		if (!binDir.exists())
			binDir.mkdir();
        for (JavaModule module : parsingData.getModules()) {
        	String clientFilePath = "src/" + testPackage.replace('.', '/') + "/" + module.getModulePackage() + "/" + 
					getClientClassName(module) + ".java";
        	String serverFilePath = "src/" + testPackage.replace('.', '/') + "/" + module.getModulePackage() + "/" + 
					getServerClassName(module) + ".java";
        	runJavac(workDir, srcDir, classPath, binDir, clientFilePath, serverFilePath);
        }
	}

	private static String prepareClassPath(File libDir, List<URL> cpUrls)
			throws Exception {
		JavaTypeGenerator.checkLib(new DiskFileSaver(libDir), "junit-4.9");
		StringBuilder classPathSB = new StringBuilder();
		for (File jarFile : libDir.listFiles()) {
			if (!jarFile.getName().endsWith(".jar"))
				continue;
			addLib(jarFile, libDir, classPathSB, cpUrls);
		}
		return classPathSB.toString();
	}

	private static Class<?> createServerServletInstance(JavaModule module,
			File libDir, File binDir, String testPackage) throws Exception,
			MalformedURLException, ClassNotFoundException {
		URLClassLoader urlcl = prepareUrlClassLoader(libDir, binDir);
        String serverClassName = pref(testPackage) + module.getModulePackage() + "." + getServerClassName(module);
        Class<?> serverClass = urlcl.loadClass(serverClassName);
		return serverClass;
	}

	private static URLClassLoader prepareUrlClassLoader(File libDir, File binDir)
			throws Exception, MalformedURLException {
		List<URL> cpUrls = new ArrayList<URL>();
        prepareClassPath(libDir, cpUrls);
        cpUrls.add(binDir.toURI().toURL());
        URLClassLoader urlcl = URLClassLoader.newInstance(cpUrls.toArray(new URL[cpUrls.size()]));
		return urlcl;
	}
	
	private static File prepareWorkDir(int testNum) throws IOException {
		File tempDir = new File(".").getCanonicalFile();
		if (!tempDir.getName().equals(tempDirName)) {
			tempDir = new File(tempDir, tempDirName);
			if (!tempDir.exists())
				tempDir.mkdir();
		}
		for (File dir : tempDir.listFiles()) {
			if (dir.isDirectory() && dir.getName().startsWith("test" + testNum + "_"))
				try {
					TextUtils.deleteRecursively(dir);
				} catch (Exception e) {
					System.out.println("Can not delete directory [" + dir.getName() + "]: " + e.getMessage());
				}
		}
		File workDir = new File(tempDir, "test" + testNum + "_" + System.currentTimeMillis());
		if (!workDir.exists())
			workDir.mkdir();
		return workDir;
	}

	private static void runClientTest(int testNum, String testPackage, JavaData parsingData, 
			File libDir, File binDir, int portNum, boolean needClientServer, File outDir,
			String serverType) throws Exception {
	    System.out.println("- Java client -> " + serverType + " server");
	    runJavaClientTest(testNum, testPackage, parsingData, libDir, binDir, portNum, needClientServer);
	    if (outDir != null) {
            String resourceName = "Test" + testNum + ".config.properties";
            String clientConfigText = checkForClientConfig(resourceName);
	        if (clientConfigText == null) {
	            System.err.println("- Perl/Python/JavaScript client tests are skipped (" + resourceName + "not found)");	
	            return;
	        }
	        if (clientConfigText.isEmpty())
                return;
	        System.out.println("- Perl client -> " + serverType + " server");
	        runPerlClientTest(testNum, testPackage, parsingData, portNum, needClientServer, outDir);
	        System.out.println("- Python client -> " + serverType + " server");
            runPythonClientTest(testNum, testPackage, parsingData, portNum, needClientServer, outDir, false);
            System.out.println("- Python3 client -> " + serverType + " server");
            runPythonClientTest(testNum, testPackage, parsingData, portNum, needClientServer, outDir, true);
            System.out.println("- JavaScript client -> " + serverType + " server");
            runJsClientTest(testNum, testPackage, parsingData, portNum, needClientServer, outDir);
	    }
	}
	
	private static String pref(String testPackage) {
	    if (testPackage.equals("."))
	        testPackage = "";
	    return testPackage.isEmpty() ? "" : (testPackage + ".");
	}
	
    private static void runJavaClientTest(int testNum, String testPackage, JavaData parsingData, 
            File libDir, File binDir, int portNum, boolean needClientServer) throws Exception {
		//System.out.println("Port: " + portNum);
        long time = System.currentTimeMillis();
        URLClassLoader urlcl = prepareUrlClassLoader(libDir, binDir);
		ConnectException error = null;
		for (int n = 0; n < 50; n++) {
			Thread.sleep(100);
			try {
				for (JavaModule module : parsingData.getModules()) {
					Class<?> testClass = urlcl.loadClass(pref(testPackage) + "Test" + testNum);
					if (needClientServer) {
						String clientClassName = getClientClassName(module);
						Class<?> clientClass = urlcl.loadClass(pref(testPackage) + module.getModulePackage() + "." + clientClassName);
						Object client = clientClass.getConstructor(URL.class).newInstance(new URL("http://localhost:" + portNum));
						try {
							testClass.getConstructor(clientClass).newInstance(client);
						} catch (NoSuchMethodException e) {
							testClass.getConstructor(clientClass, Integer.class).newInstance(client, portNum);							
						}
					} else {
						try {
							testClass.getConstructor().newInstance();
						} catch (NoSuchMethodException e) {
							testClass.getConstructor(File.class).newInstance(binDir.getParentFile());
						}
					}
				}
				error = null;
				//System.out.println("Timeout before server response: " + (n * 100) + " ms.");
				break;
			} catch (InvocationTargetException ex) {
				Throwable t = ex.getCause();
				if (t != null && t instanceof Exception) {
					if (t instanceof ConnectException) {
						error = (ConnectException)t;
					} else {
						if (t instanceof ServerException) {
							t.printStackTrace();
							throw new IllegalStateException("ServerException: " + t.getMessage() + 
									" (" + ((ServerException)t).getData() + ")");
						}
						throw (Exception)t;
					}
				} else if (t != null && t instanceof Error) {
				    throw (Error)t;
				} else {
					throw ex;
				}
			}
		}
		if (error != null)
			throw error;
		if (debugClientTimes)
		    System.out.println("  (time=" + (System.currentTimeMillis() - time) + " ms)");
	}
    
    private static void runPerlClientTest(int testNum, String testPackage, JavaData parsingData, 
            int portNum, boolean needClientServer, File outDir) throws Exception {
        if (!needClientServer)
            return;
        long time = System.currentTimeMillis();
        String resourceName = "Test" + testNum + ".config.properties";
        File shellFile = null;
        File configFile = new File(outDir, "tests.json");
        prepareClientTestConfigFile(parsingData, resourceName, configFile);
        shellFile = new File(outDir, "test_perl_client.sh");
        List<String> lines = new ArrayList<String>(Arrays.asList("#!/bin/bash"));
        //JavaTypeGenerator.checkEnvVars(lines, "PERL5LIB");
        lines.addAll(Arrays.asList(
                "perl ../../../test_scripts/perl/test-client.pl -tests " + configFile.getName() + 
                " -endpoint http://localhost:" + portNum + "/ -user " + System.getProperty("test.user") +
                " -password \"" + System.getProperty("test.pwd") + "\"" +
                (System.getProperty("KB_JOB_CHECK_WAIT_TIME") == null ? "" :
                    (" -asyncchecktime " + System.getProperty("KB_JOB_CHECK_WAIT_TIME")))
                ));
        TextUtils.writeFileLines(lines, shellFile);
        if (shellFile != null) {
            ProcessHelper ph = ProcessHelper.cmd("bash", shellFile.getCanonicalPath()).exec(outDir, null, true, true);
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
        if (debugClientTimes)
            System.out.println("  (time=" + (System.currentTimeMillis() - time) + " ms)");
    }

    private static void prepareClientTestConfigFile(JavaData parsingData,
            String resourceName, File configFile) throws IOException,
            JsonParseException, JsonMappingException, JsonGenerationException {
        InputStream configIs = TypeGeneratorTest.class.getResourceAsStream(resourceName);
        Map<String, Object> config = UObject.getMapper().readValue(configIs, 
                new TypeReference<Map<String, Object>>() {});
        configIs.close();
        //TextUtils.writeFileLines(TextUtils.readStreamLines(), configFile);
        if (!config.containsKey("package")) {
            String serviceName = parsingData.getModules().get(0).getOriginal().getServiceName();
            config.put("package", serviceName + "Client");
        }
        if (!config.containsKey("class")) {
            String moduleName = parsingData.getModules().get(0).getOriginal().getModuleName();
            config.put("class", moduleName);
        }
        UObject.getMapper().writeValue(configFile, config);
    }

    private static void runPythonClientTest(int testNum, String testPackage, JavaData parsingData, 
            int portNum, boolean needClientServer, File outDir, boolean ver3) throws Exception {
        if (!needClientServer)
            return;
        long time = System.currentTimeMillis();
        String resourceName = "Test" + testNum + ".config.properties";
        File shellFile = null;
        File configFile = new File(outDir, "tests.json");
        prepareClientTestConfigFile(parsingData, resourceName, configFile);
        shellFile = new File(outDir, "test_python_client.sh");
        List<String> lines = new ArrayList<String>(Arrays.asList("#!/bin/bash"));
        String pyName = "Python" + (ver3 ? "3" : "");
        String pyCmd = pyName.toLowerCase();
        if (ver3)
            lines.add("export PYTHONPATH=");
        lines.addAll(Arrays.asList(
                pyCmd + " ../../../test_scripts/python/test_client.py -t " + configFile.getName() + 
                " -e http://localhost:" + portNum + "/ -u " + System.getProperty("test.user") +
                " -p \"" + System.getProperty("test.pwd") + "\"" +
                (System.getProperty("KB_JOB_CHECK_WAIT_TIME") == null ? "" :
                    (" -a " + System.getProperty("KB_JOB_CHECK_WAIT_TIME")))
                ));
        TextUtils.writeFileLines(lines, shellFile);
        if (shellFile != null) {
            ProcessHelper ph = ProcessHelper.cmd("bash", shellFile.getCanonicalPath()).exec(outDir, null, true, true);
            int exitCode = ph.getExitCode();
            if (exitCode != 0) {
                String out = ph.getSavedOutput();
                if (!out.isEmpty())
                    System.out.println(pyName + " client output:\n" + out);
                String err = ph.getSavedErrors();
                if (!err.isEmpty())
                    System.err.println(pyName + " client errors:\n" + err);
            }
            Assert.assertEquals(pyName + " client exit code should be 0", 0, exitCode);
        }
        if (debugClientTimes)
            System.out.println("  (time=" + (System.currentTimeMillis() - time) + " ms)");
    }

    private static String checkForClientConfig(String resourceName) throws Exception {
        InputStream configIs = TypeGeneratorTest.class.getResourceAsStream(resourceName);
        if (configIs == null)
            return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        TextUtils.copyStreams(configIs, baos);
        configIs.close();
        return new String(baos.toByteArray());
    }
    
    private static void runJsClientTest(int testNum, String testPackage, JavaData parsingData, 
            int portNum, boolean needClientServer, File outDir) throws Exception {
        if (!needClientServer)
            return;
        if (!isCasperJsInstalled()) {
            System.err.println("- JavaScript client tests are skipped");
            return;
        }
        long time = System.currentTimeMillis();
        String resourceName = "Test" + testNum + ".config.properties";
        File shellFile = null;
        File configFile = new File(outDir, "tests.json");
        prepareClientTestConfigFile(parsingData, resourceName, configFile);
        shellFile = new File(outDir, "test_js_client.sh");
        List<String> lines = new ArrayList<String>(Arrays.asList("#!/bin/bash"));
        String token = getToken();
        lines.addAll(Arrays.asList(
                "casperjs test ../../../test_scripts/js/test-client.js "
                        + "--jq=../../../test_scripts/js/jquery-1.10.2.min.js "
                        + "--tests=" + configFile.getName() + 
                        " --endpoint=http://localhost:" + portNum + "/ --token=\"" + token + "\"" +
                        (System.getProperty("KB_JOB_CHECK_WAIT_TIME") == null ? "" :
                            (" --asyncchecktime=" + System.getProperty("KB_JOB_CHECK_WAIT_TIME")))
                ));
        TextUtils.writeFileLines(lines, shellFile);
        if (shellFile != null) {
            ProcessHelper ph = ProcessHelper.cmd("bash", shellFile.getCanonicalPath()).exec(outDir, null, true, true);
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
        if (debugClientTimes)
            System.out.println("  (time=" + (System.currentTimeMillis() - time) + " ms)");
    }

	private static void extractSpecFiles(int testNum, File workDir,
			String testFileName) {
		try {
			TextUtils.writeFileLines(TextUtils.readStreamLines(TypeGeneratorTest.class.getResourceAsStream(
			        testFileName + ".properties")), new File(workDir, testFileName));
		} catch (Exception ex) {
			String zipFileName = "test" + testNum + ".zip";
			try {
				ZipInputStream zis = new ZipInputStream(TypeGeneratorTest.class.getResourceAsStream(zipFileName + ".properties"));
				while (true) {
					ZipEntry ze = zis.getNextEntry();
					if (ze == null)
						break;
					TextUtils.writeFileLines(TextUtils.readStreamLines(zis, false), new File(workDir, ze.getName()));
				}
				zis.close();
			} catch (Exception e2) {
				throw new IllegalStateException("Can not find neither " + testFileName + " resource nor " + zipFileName + 
						" in resources having .properties suffix", ex);
			}
		}
	}

	private static void perlServerCorrection(File serverOutDir, JavaData parsingData, 
	        Map<String, String> serverManualCorrections) throws IOException {
		for (JavaModule module : parsingData.getModules()) {
            Map<String, JavaFunc> origNameToFunc = new HashMap<String, JavaFunc>();
            for (JavaFunc func : module.getFuncs()) {
            	origNameToFunc.put(func.getOriginal().getName(), func);
            }
            File perlServerImpl = new File(serverOutDir, module.getOriginal().getModuleName() + "Impl.pm");
            List<String> perlServerLines = TextUtils.readFileLines(perlServerImpl);
            for (int pos = 0; pos < perlServerLines.size(); pos++) {
            	String line = perlServerLines.get(pos);
            	if (line.startsWith("    #BEGIN ")) {
            		String origFuncName = line.substring(line.lastIndexOf(' ') + 1);
            		if (serverManualCorrections != null && serverManualCorrections.containsKey(origFuncName)) {
                        pos++;
                        perlServerLines.add(pos, "    " + serverManualCorrections.get(origFuncName) + ";");                     
                    } else if (origNameToFunc.containsKey(origFuncName)) {
                        KbFuncdef origFunc = origNameToFunc.get(origFuncName).getOriginal();
                        int paramCount = origFunc.getParameters().size();
            		    if (origFuncName.equals("throw_error_on_server_side")) {
                            pos++;
                            perlServerLines.add(pos, "    Bio::KBase::Exceptions::KBaseException->throw(error => " + (paramCount > 0 ? ("$" + 
                                    getParamPyPlName(origFunc, 0)) : "\"\"") + ", method_name => '" + origFuncName + "');");
            		    } else {
            		        for (int paramPos = 0; paramPos < paramCount; paramPos++) {
            		            pos++;
            		            perlServerLines.add(pos, "    $return" + (paramCount > 1 ? ("_" + (paramPos + 1)) : "") + " = $" + 
            		                    getParamPyPlName(origFunc,paramPos) + ";");
            		        }
            		    }
            		}
            	}
            }
            TextUtils.writeFileLines(perlServerLines, perlServerImpl);
        }
	}

	private static void javaServerCorrectionForTestCallback(File srcDir, String packageParent, JavaData parsingData, String testClassName) throws IOException {
		for (JavaModule module : parsingData.getModules()) {
            File moduleDir = new File(srcDir.getAbsolutePath() + "/" + packageParent.replace('.', '/') + "/" + module.getModulePackage());
            File serverImpl = new File(moduleDir, getServerClassName(module) + ".java");
            List<String> serverLines = TextUtils.readFileLines(serverImpl);
            for (int pos = 0; pos < serverLines.size(); pos++) {
            	String line = serverLines.get(pos);
            	if (line.startsWith("        //BEGIN ") || line.startsWith("        //BEGIN_CONSTRUCTOR")) {
            		pos++;
            		serverLines.add(pos, "        " + testClassName + ".serverMethod(this);");
            	}
            }
            TextUtils.writeFileLines(serverLines, serverImpl);
        }
	}

	private static void javaServerCorrection(File srcDir, String packageParent, JavaData parsingData, 
	        Map<String, String> serverManualCorrections) throws IOException {
		for (JavaModule module : parsingData.getModules()) {
            Map<String, JavaFunc> origNameToFunc = new HashMap<String, JavaFunc>();
            for (JavaFunc func : module.getFuncs()) {
            	origNameToFunc.put(func.getOriginal().getName(), func);
            }
            File moduleDir = new File(srcDir.getAbsolutePath() + "/" + packageParent.replace('.', '/') + "/" + module.getModulePackage());
            File serverImpl = new File(moduleDir, getServerClassName(module) + ".java");
            List<String> serverLines = TextUtils.readFileLines(serverImpl);
            for (int pos = 0; pos < serverLines.size(); pos++) {
            	String line = serverLines.get(pos);
            	if (line.startsWith("        //BEGIN ")) {
            		String origFuncName = line.substring(line.lastIndexOf(' ') + 1);
            		if (serverManualCorrections != null && serverManualCorrections.containsKey(origFuncName)) {
                        pos++;
                        serverLines.add(pos, "        " + serverManualCorrections.get(origFuncName) + ";");            		    
            		} else if (origNameToFunc.containsKey(origFuncName)) {
            			JavaFunc func = origNameToFunc.get(origFuncName);
            			if (origFuncName.equals("throw_error_on_server_side")) {
                            String message = func.getParams().size() > 0 ? ("\"\" + " + func.getParams().get(0).getJavaName()) : "";
                            pos++;
                            serverLines.add(pos, "        if (true) throw new Exception(" + message + ");");                     
            			} else {
            			    int paramCount = func.getParams().size();
            			    for (int paramPos = 0; paramPos < paramCount; paramPos++) {
            			        pos++;
            			        serverLines.add(pos, "        return" + (paramCount > 1 ? ("" + (paramPos + 1)) : "Val") + " = " + 
            			                func.getParams().get(paramPos).getJavaName() + ";");
            			    }
            			}
            		}
            	}
            }
            TextUtils.writeFileLines(serverLines, serverImpl);
        }
	}

	private static void pythonServerCorrection(File serverOutDir, JavaData parsingData,
	        Map<String, String> serverManualCorrections) throws IOException {
		for (JavaModule module : parsingData.getModules()) {
            Map<String, JavaFunc> origNameToFunc = new HashMap<String, JavaFunc>();
            for (JavaFunc func : module.getFuncs()) {
            	origNameToFunc.put(func.getOriginal().getName(), func);
            }
            File pyServerImpl = new File(serverOutDir, module.getOriginal().getModuleName() + "Impl.py");
            List<String> pyServerLines = TextUtils.readFileLines(pyServerImpl);
            for (int pos = 0; pos < pyServerLines.size(); pos++) {
            	String line = pyServerLines.get(pos);
            	if (line.startsWith("        #BEGIN ")) {
            		String origFuncName = line.substring(line.lastIndexOf(' ') + 1);
                    if (serverManualCorrections != null && serverManualCorrections.containsKey(origFuncName)) {
                        pos++;
                        pyServerLines.add(pos, "        " + serverManualCorrections.get(origFuncName));                     
                    } else if (origNameToFunc.containsKey(origFuncName)) {
            			KbFuncdef origFunc = origNameToFunc.get(origFuncName).getOriginal();
            			int paramCount = origFunc.getParameters().size();
                        if (origFuncName.equals("throw_error_on_server_side")) {
                            pos++;
                            pyServerLines.add(pos, "        raise Exception(" + (paramCount > 0 ? 
                                    getParamPyPlName(origFunc,0) : "''") + ")");
                        } else {
                            for (int paramPos = 0; paramPos < paramCount; paramPos++) {
                                pos++;
                                pyServerLines.add(pos, "        return" + (paramCount > 1 ? ("_" + (paramPos + 1)) : "Val") + " = " + 
                                        getParamPyPlName(origFunc,paramPos));
                            }
                            if (paramCount == 0) {
                                pos++;
                                pyServerLines.add(pos, "        pass");
                            }
                        }
            		}
            	}
            }
            TextUtils.writeFileLines(pyServerLines, pyServerImpl);
        }
	}
	
	private static String getParamPyPlName(KbFuncdef func, int pos) {
	    String ret = func.getParameters().get(pos).getOriginalName();
	    if (ret == null)
	        ret = "arg_" + (pos + 1);
	    return ret;
	}

	private static void runJavac(File workDir, File srcDir, String classPath, File binDir, 
			String... sourceFilePaths) throws IOException {
		ProcessHelper.cmd("javac", "-g:source,lines", "-d", binDir.getName(), "-sourcepath", srcDir.getName(), "-cp", 
				classPath, "-Xlint:deprecation").add(sourceFilePaths).exec(workDir);
	}

	private static void runJavaDoc(File workDir, File srcDir, String classPath, File docDir, String... packages) throws IOException {
		ProcessHelper.cmd("javadoc", "-d", docDir.getName(), "-sourcepath", srcDir.getName(), "-classpath", 
				classPath).add(packages).exec(workDir, (File)null, null);
	}

	private static String getClientClassName(JavaModule module) {
		return TextUtils.capitalize(module.getModuleName()) + "Client";
	}

	private static String getServerClassName(JavaModule module) {
		return TextUtils.capitalize(module.getModuleName()) + "Server";
	}

	private static void addLib(File libFile, File libDir, StringBuilder classPath, List<URL> libUrls) throws Exception {
        if (classPath.length() > 0)
        	classPath.append(':');
        classPath.append("lib/").append(libFile.getName());
        libUrls.add(libFile.toURI().toURL());
	}
	
	public static boolean isCasperJsInstalled() {
	    if (isCasperJsInstalled != null)
	        return isCasperJsInstalled;
	    try {
	        ProcessHelper ph = ProcessHelper.cmd("casperjs", "--version").exec(new File("."), null, true, true);
	        isCasperJsInstalled = false;
	        String out = ph.getSavedOutput().trim();
	        if (out.startsWith("Warning") && out.indexOf('\n') > 0)
	            out = out.substring(out.indexOf('\n')).trim();
	        if (out.contains("-"))
	            out = out.substring(0, out.indexOf('-'));
	        String[] parts = out.split(Pattern.quote("."));
	        if (parts.length == 3) {
	            int major = Integer.parseInt(parts[0]);
	            int minor = Integer.parseInt(parts[1]);
	            isCasperJsInstalled = (major > 1) || (major == 1 && minor >= 1);
	        }
            if (!isCasperJsInstalled) {
                System.err.println("Unexpected CastperJS version output (it must be 1.1.0 or higher):");
                System.err.println(ph.getSavedOutput().trim());
                if (ph.getSavedErrors().trim().length() > 0) {
                    System.err.println("CastperJS errors:");
                    System.err.println(ph.getSavedErrors().trim());
                }
            }
	    } catch (Throwable ex) {
	        System.err.println("CasperJS is not installed (" + ex.getMessage() + ")");
	        isCasperJsInstalled = false;
	    }
	    return isCasperJsInstalled;
	}
	
	private static String getToken() {
        if (token == null) {
            token = "no-token";
            try {
                token = AuthService.login(System.getProperty("test.user"), System.getProperty("test.pwd")).getTokenString();
            } catch (Exception ignore) {} 
        }
        return token;
	}
	
	public static class ServiceWizardMock extends JsonServerServlet {
        private static final long serialVersionUID = 1L;
        
        private final int[] serverPortHolder;

        public ServiceWizardMock(int[] serverPortHolder) {
            super("ServiceWizard");
            this.serverPortHolder = serverPortHolder;
        }

        @JsonServerMethod(rpc = "ServiceWizard.get_service_status")
        public Map<String, Object> getServiceStatus(Map<String, String> params) throws IOException, JsonClientException {
            Map<String, Object> ret = new LinkedHashMap<String, Object>();
            ret.put("url", "http://localhost:" + serverPortHolder[0]);
            return ret;
        }
    }
}
