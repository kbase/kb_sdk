package us.kbase.mobu.tester.test;

import org.apache.commons.io.FileUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import us.kbase.auth.AuthToken;
import us.kbase.common.executionengine.CallbackServer;
import us.kbase.mobu.ModuleBuilder;
import us.kbase.mobu.initializer.ModuleInitializer;
import us.kbase.mobu.initializer.test.DockerClientServerTester;
import us.kbase.mobu.tester.ModuleTester;
import us.kbase.scripts.test.TestConfigHelper;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.powermock.api.mockito.PowerMockito.when;

/** This class tests to ensure that the unit test run fails hard if the callback
 *  server cannot be instantiated.
 *
 */
@PowerMockIgnore({"javax.net.ssl.*"})   // PowerMockRunner tries to classload a different set of net classes
@RunWith( PowerMockRunner.class )
@PrepareForTest( CallbackServer.class )
public class ModuleTesterFailHardTest {
    private static final String SIMPLE_MODULE_NAME = "ASimpleModule_for_unit_testing";
    private static final boolean cleanupAfterTests = true;
    private static List<String> createdModuleNames = new ArrayList<String>();
    private static AuthToken token;

    @BeforeClass
    public static void beforeClass() throws Exception {
        token = TestConfigHelper.getToken();
        PowerMockito.mockStatic(CallbackServer.class);
        // force function to always return null
        when(CallbackServer.getCallbackUrl(anyInt(),any(String[].class))).thenReturn(null);
        Assert.assertNull(CallbackServer.getCallbackUrl(0, new String[1]));
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
        ModuleInitializer initer = new ModuleInitializer(moduleName, token.getUserName(),
                lang, false);
        initer.initialize(true);
    }

    private int runTestsInDocker(String moduleName) throws Exception {
        File moduleDir = new File(moduleName);
        return runTestsInDocker(moduleDir, token);
    }

    public static int runTestsInDocker(File moduleDir, AuthToken token) throws Exception {
        return runTestsInDocker(moduleDir, token, false);
    }

    public static int runTestsInDocker(File moduleDir, AuthToken token,
                                       boolean skipValidation) throws Exception {
        DockerClientServerTester.correctDockerfile(moduleDir);
        File testCfgFile = new File(moduleDir, "test_local/test.cfg");
        String testCfgText = ""+
                "test_token=" + token.getToken() + "\n" +
                "kbase_endpoint=" + TestConfigHelper.getKBaseEndpoint() + "\n" +
                "auth_service_url=" + TestConfigHelper.getAuthServiceUrl() + "\n" +
                "auth_service_url_allow_insecure=" +
                TestConfigHelper.getAuthServiceUrlInsecure() + "\n";
        FileUtils.writeStringToFile(testCfgFile, testCfgText);
        int exitCode = new ModuleTester(moduleDir).runTests(ModuleBuilder.DEFAULT_METHOD_STORE_URL,
                skipValidation, false);
        System.out.println("Exit code: " + exitCode);
        return exitCode;
    }

    @Test(expected = IllegalStateException.class)
    public void testPythonModuleExample() throws Exception {
        System.out.println("Test [testPythonModuleExample]");
        String lang = "python";
        String moduleName = SIMPLE_MODULE_NAME + "Python";
        init(lang, moduleName);
        int exitCode = runTestsInDocker(moduleName);
    }
}
