package us.kbase.mobu.initializer.test;

import java.io.File;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import us.kbase.scripts.test.TestConfigHelper;
import us.kbase.scripts.test.TypeGeneratorTest;

public class AsyncDockerTest extends DockerClientServerTester {

    private static final String SIMPLE_MODULE_NAME = "TestAsync";
    
    private static int execEnginePort;
    private static Server execEngineJettyServer;
    private static ExecEngineMock execEngine;
    
    @BeforeClass
    public static void beforeClass() throws Exception {
        execEnginePort = TypeGeneratorTest.findFreePort();
        execEngineJettyServer = new Server(execEnginePort);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        execEngineJettyServer.setHandler(context);
        execEngine = new ExecEngineMock().withKBaseEndpoint(TestConfigHelper.getKBaseEndpoint());
        context.addServlet(new ServletHolder(execEngine), "/*");
        execEngineJettyServer.start();
    }
    
    @AfterClass
    public static void tearDownModule() throws Exception {
        execEngine.waitAndCleanAllJobs();
        if (execEngineJettyServer != null)
            execEngineJettyServer.stop();
    }
    
    private static void testAsyncClients(File moduleDir, String serverType) throws Exception {
        try {
            String dockerImage = prepareDockerImage(moduleDir, token);
            execEngine.withModule(moduleDir.getName(), dockerImage, moduleDir);
            String clientEndpointUrl = "http://localhost:" + execEnginePort;
            testClients(moduleDir, clientEndpointUrl, true, false, serverType);
            testStatus(moduleDir, clientEndpointUrl, true, false, serverType);
        } finally {
            execEngine.waitAndCleanAllJobs();
        }
    }            

    @Test
    public void testPerlAsyncService() throws Exception {
        System.out.println("Test [testPerlAsyncService]");
        File moduleDir = initPerl(SIMPLE_MODULE_NAME + "Perl");
        testAsyncClients(moduleDir, "Perl");
    }

    @Test
    public void testJavaAsyncService() throws Exception {
        System.out.println("Test [testJavaAsyncService]");
        File moduleDir = initJava(SIMPLE_MODULE_NAME + "Java");
        testAsyncClients(moduleDir, "Java");
    }

    @Test
    public void testPythonAsyncService() throws Exception {
        System.out.println("Test [testPythonAsyncService]");
        File moduleDir = initPython(SIMPLE_MODULE_NAME + "Python");
        testAsyncClients(moduleDir, "Python");
    }
}
