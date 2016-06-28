package us.kbase.mobu.initializer.test;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import us.kbase.common.service.JsonClientException;
import us.kbase.common.service.JsonServerMethod;
import us.kbase.common.service.JsonServerServlet;
import us.kbase.mobu.tester.ModuleTester;
import us.kbase.mobu.util.ProcessHelper;
import us.kbase.scripts.test.TypeGeneratorTest;

public class DynamicServiceTest extends DockerClientServerTester {

    private static final String SIMPLE_MODULE_NAME = "TestDynamic";
    
    private static int serviceWizardPort;
    private static Server serviceWizardJettyServer;
    private static ServiceWizardMock serviceWizard;
    
    @BeforeClass
    public static void beforeClass() throws Exception {
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
        if (serviceWizardJettyServer != null)
            serviceWizardJettyServer.stop();
    }
    
    public static String runServerInDocker(File moduleDir, 
            int port) throws Exception {
        String imageName = prepareDockerImage(moduleDir, user, pwd);
        String moduleName = moduleDir.getName();
        File tlDir = new File(moduleDir, "test_local");
        File workDir = new File(tlDir, "workdir");
        File runDockerSh = new File(tlDir, "run_docker.sh");
        System.out.println();
        System.out.println("Starting up dynamic service...");
        String runDockerPath = ModuleTester.getFilePath(runDockerSh);
        String workDirPath = ModuleTester.getFilePath(workDir);
        String containerName = "test_" + moduleName.toLowerCase() + "_" + 
                System.currentTimeMillis();
        String endPoint = "https://ci.kbase.us/services";
        ProcessHelper.cmd("bash", runDockerPath, "run", "-d", "-p", port + ":5000",
                "--dns", "8.8.8.8", "-v", workDirPath + ":/kb/module/work", 
                "--name", containerName, "-e", "KBASE_ENDPOINT=" + endPoint, imageName).exec(tlDir);
        return containerName;
    }

    private static void testDynamicClients(File moduleDir, int port, 
            String contName) throws Exception {
        try {
            FileUtils.writeStringToFile(new File(moduleDir, "sdk.cfg"), 
                    "catalog_url=http://kbase.us");
            String dockerAddress = "localhost";
            String dockerHost = System.getenv("DOCKER_HOST");
            if (dockerHost != null && dockerHost.startsWith("tcp://")) {
                dockerAddress = dockerHost.substring(6).split(":")[0];
            }
            serviceWizard.fwdUrl = "http://" + dockerAddress + ":" + port;
            String clientEndpointUrl = "http://localhost:" + serviceWizardPort;
            testClients(moduleDir, clientEndpointUrl, false, true);
            testStatus(moduleDir, clientEndpointUrl, false, true);
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
        File moduleDir = initPerl(SIMPLE_MODULE_NAME + "Perl");
        int port = TypeGeneratorTest.findFreePort();
        String contName = runServerInDocker(moduleDir, port);
        testDynamicClients(moduleDir, port, contName);
    }

    @Test
    public void testJavaDynamicService() throws Exception {
        System.out.println("Test [testJavaDynamicService]");
        File moduleDir = initJava(SIMPLE_MODULE_NAME + "Java");
        int port = TypeGeneratorTest.findFreePort();
        String contName = runServerInDocker(moduleDir, port);
        testDynamicClients(moduleDir, port, contName);
    }

    @Test
    public void testPythonDynamicService() throws Exception {
        System.out.println("Test [testPythonDynamicService]");
        File moduleDir = initPython(SIMPLE_MODULE_NAME + "Python");
        int port = TypeGeneratorTest.findFreePort();
        String contName = runServerInDocker(moduleDir, port);
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
