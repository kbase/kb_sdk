package us.kbase.mobu.initializer.test;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.ini4j.Ini;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

import us.kbase.auth.AuthService;
import us.kbase.auth.AuthToken;
import us.kbase.common.service.UObject;
import us.kbase.common.test.TestException;
import us.kbase.mobu.initializer.ModuleInitializer;
import us.kbase.mobu.installer.ClientInstaller;
import us.kbase.mobu.tester.ModuleTester;
import us.kbase.mobu.util.ProcessHelper;
import us.kbase.mobu.util.TextUtils;
import us.kbase.scripts.test.TypeGeneratorTest;

public class DockerClientServerTester {

    protected static final boolean cleanupAfterTests = true;

    protected static List<String> createdModuleNames = new ArrayList<String>();
    protected static String user;
    protected static String pwd;
    
    private static final String TEST_CFG = "kb_sdk_test";
    private static final long startingTime = System.currentTimeMillis();

    @BeforeClass
    public static void beforeTesterClass() throws Exception {
        final Ini testini = new Ini(new File("test_scripts/test.cfg"));
        user = testini.get(TEST_CFG, "test.user");
        pwd = testini.get(TEST_CFG, "test.pwd");
        if (user == null || user.isEmpty() || pwd == null || pwd.isEmpty()) {
            throw new TestException("missing user and / or pws from test cfg");
        }
        TypeGeneratorTest.suppressJettyLogging();
    }
    
    @AfterClass
    public static void afterTesterClass() throws Exception {
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
    
    protected static void deleteDir(String moduleName) throws Exception {
        File module = new File(moduleName);
        if (module.exists() && module.isDirectory())
            FileUtils.deleteDirectory(module);
    }
    
    protected File init(String lang, String moduleName, File implFile, 
            String implInitText) throws Exception {
        deleteDir(moduleName);
        createdModuleNames.add(moduleName);
        ModuleInitializer initer = new ModuleInitializer(moduleName, user, lang, false);
        initer.initialize(false);
        File specFile = new File(moduleName, moduleName + ".spec");
        String specText = FileUtils.readFileToString(specFile).replace("};", 
                "funcdef run_test(string input) returns (string) authentication required;\n};");
        FileUtils.writeStringToFile(specFile, specText);
        if (implFile != null && implInitText != null)
            FileUtils.writeStringToFile(implFile, implInitText);
        File moduleDir = new File(moduleName);
        // Making <kb_sdk>/bin/kb-sdk executable
        if (ProcessHelper.cmd("make").exec(new File(".")).getExitCode() != 0)
            throw new IllegalStateException("Error making kb-sdk");
        // Running make for repo with adding <kb_sdk>/bin/kb-sdk into PATH
        File shellFile = new File(moduleDir, "run_make.sh");
        String pathToSdk = new File(".").getCanonicalPath();
        List<String> lines = new ArrayList<String>(Arrays.asList("#!/bin/bash"));
        lines.add("export PATH=" + pathToSdk + "/bin:$PATH");
        lines.add("which kb-sdk");
        lines.add("make");
        TextUtils.writeFileLines(lines, shellFile);
        if(ProcessHelper.cmd("bash", shellFile.getCanonicalPath()).exec(
                moduleDir).getExitCode() != 0)
            throw new IllegalStateException("Error making " + moduleName + " repo");
        //ProcessHelper.cmd("make").exec(moduleDir);
        FileUtils.writeStringToFile(new File(moduleDir, "sdk.cfg"), 
                "catalog_url=http://kbase.us");
        return moduleDir;
    }
    
    protected File initPerl(String moduleName) throws Exception {
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
        init("perl", moduleName, implFile, implInit);
        return moduleDir;
    }

    protected File initJava(String moduleName) throws Exception {
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
        init("java", moduleName, implFile, implInit);
        return moduleDir;
    }
    
    protected File initPython(String moduleName) throws Exception {
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
        init("python", moduleName, implFile, implInit);
        return moduleDir;
    }
    
    public static void correctDockerfile(File moduleDir) throws Exception {
        File buildXmlFile = new File("build.xml");
        File sdkSubFolder = new File(moduleDir, "kb_sdk");
        File sdkDistSubFolder = new File(sdkSubFolder, "dist");
        FileUtils.copyFile(buildXmlFile, new File(sdkSubFolder, buildXmlFile.getName()));
        File sdkJarFile = new File("dist/kbase_module_builder2.jar");
        FileUtils.copyFile(sdkJarFile, new File(sdkDistSubFolder, sdkJarFile.getName()));
        File jarDepsFile = new File("JAR_DEPS");
        FileUtils.copyFile(jarDepsFile, new File(sdkSubFolder, jarDepsFile.getName()));
        File makeFile = new File("Makefile");
        FileUtils.copyFile(makeFile, new File(sdkSubFolder, makeFile.getName()));
        File dockerFile = new File(moduleDir, "Dockerfile");
        String dockerText = FileUtils.readFileToString(dockerFile);
        dockerText = dockerText.replace("COPY ./ /kb/module", "" +
                "COPY ./ /kb/module\n" +
                "RUN . /kb/dev_container/user-env.sh && \\\n" +
                "    cd /kb/dev_container/modules/jars && \\\n" +
                "    git pull && make && make deploy && \\\n" +
                "    rm /kb/dev_container/bin/kb-sdk && \\\n" + 
                "    rm /kb/deployment/bin/kb-sdk && \\\n" + 
                "    cd /kb/dev_container/modules/kb_sdk && \\\n" +
                "    cp -r /kb/module/kb_sdk/* ./ && \\\n" +
                "    make deploy && echo \"" + new Date(startingTime) + "\"");
        FileUtils.writeStringToFile(dockerFile, dockerText);
    }
    
    protected static String prepareDockerImage(File moduleDir, 
            String user, String pwd) throws Exception {
        String moduleName = moduleDir.getName();
        correctDockerfile(moduleDir);
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
        File runDockerSh = new File(tlDir, "run_docker.sh");
        ProcessHelper.cmd("chmod", "+x", runDockerSh.getCanonicalPath()).exec(tlDir);
        String imageName = "test/" + moduleName.toLowerCase() + ":latest";
        if (!ModuleTester.buildNewDockerImageWithCleanup(moduleDir, tlDir, runDockerSh, 
                imageName))
            throw new IllegalStateException("Error building docker image");
        return imageName;
    }
    
    protected static void testClients(File moduleDir, String clientEndpointUrl,
            boolean async, boolean dynamic, String serverType) throws Exception { 
        String moduleName = moduleDir.getName();
        File specFile = new File(moduleDir, moduleName + ".spec");
        //TODO AUTH make configurable?
        AuthToken token = AuthService.login(user, pwd).getToken();
        // Java client
        System.out.print("Java client -> " + serverType + " server ");
        ClientInstaller clInst = new ClientInstaller(moduleDir);
        clInst.install("java", async, false, dynamic, "dev", false, 
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
        Object client = clientClass.getConstructor(URL.class, AuthToken.class)
                .newInstance(new URL(clientEndpointUrl), token);
        clientClass.getMethod("setIsInsecureHttpConnectionAllowed", Boolean.TYPE).invoke(client, true);
        Method method = null;
        for (Method m : client.getClass().getMethods())
            if (m.getName().equals("runTest"))
                method = m;
        String input = "Super-string";
        Object obj = null;
        Exception error = null;
        int javaAttempts = dynamic ? 10 : 1;
        long time = -1;
        for (int i = 0; i < javaAttempts; i++) {
            try {
                long startTime = System.currentTimeMillis();
                obj = method.invoke(client, input, null);
                time = System.currentTimeMillis() - startTime;
                error = null;
                break;
            } catch (Exception ex) {
                error = ex;
            }
            Thread.sleep(100);
        }
        System.out.println("(" + time + " ms)");
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
        test1.put("auth", true);
        test1.put("params", Arrays.asList(input));
        test1.put("outcome", UObject.getMapper().readValue("{\"status\":\"pass\"}", Map.class));
        config.put("tests", Arrays.asList(test1));
        File configFile = new File(moduleDir, "tests.json");
        UObject.getMapper().writeValue(configFile, config);
        File lib2 = new File(moduleDir, "lib2");
        // Python client
        System.out.print("Python client -> " + serverType + " server ");
        clInst.install("python", async, false, dynamic, "dev", false, 
                specFile.getCanonicalPath(), "lib2");
        File shellFile = new File(moduleDir, "test_python_client.sh");
        List<String> lines = new ArrayList<String>(Arrays.asList("#!/bin/bash"));
        lines.add("python " + new File("test_scripts/python/test_client.py").getAbsolutePath() + 
                " -t " + configFile.getAbsolutePath() + " -u " + user + " -p " + pwd +
                " -e " + clientEndpointUrl);
        TextUtils.writeFileLines(lines, shellFile);
        {
            long startTime = System.currentTimeMillis();
            ProcessHelper ph = ProcessHelper.cmd("bash", shellFile.getCanonicalPath()).exec(
                    new File(lib2, moduleName).getCanonicalFile(), null, true, true);
            System.out.println("(" + (System.currentTimeMillis() - startTime) + " ms)");
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
        System.out.print("Perl client -> " + serverType + " server ");
        Map<String, Object> config2 = new LinkedHashMap<String, Object>(config);
        config2.put("package", moduleName + "::" + moduleName + "Client");
        File configFilePerl = new File(moduleDir, "tests_perl.json");
        UObject.getMapper().writeValue(configFilePerl, config2);
        clInst.install("perl", async, false, dynamic, "dev", false, 
                specFile.getCanonicalPath(), "lib2");
        shellFile = new File(moduleDir, "test_perl_client.sh");
        lines = new ArrayList<String>(Arrays.asList("#!/bin/bash"));
        String pathToSdk = new File(".").getCanonicalPath();
        lines.add("export PERL5LIB=" + pathToSdk + "/lib/:" +
                pathToSdk + "/submodules/auth/Bio-KBase-Auth/lib:$PERL5LIB");
        lines.add("perl " + new File("test_scripts/perl/test-client.pl").getAbsolutePath() + 
                " -tests " + configFilePerl.getAbsolutePath() + " -user " + user + 
                " -password " + pwd + " -endpoint " + clientEndpointUrl
                );
        TextUtils.writeFileLines(lines, shellFile);
        {
            long startTime = System.currentTimeMillis();
            ProcessHelper ph = ProcessHelper.cmd("bash", shellFile.getCanonicalPath()).exec(
                    lib2.getCanonicalFile(), null, true, true);
            System.out.println("(" + (System.currentTimeMillis() - startTime) + " ms)");
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
        System.out.print("JavaScript client -> " + serverType + " server ");
        clInst.install("javascript", async, false, dynamic, "dev", false, 
                specFile.getCanonicalPath(), "lib2");
        shellFile = new File(moduleDir, "test_js_client.sh");
        lines = new ArrayList<String>(Arrays.asList("#!/bin/bash"));
        lines.addAll(Arrays.asList(
                "casperjs test " + new File("test_scripts/js/test-client.js").getAbsolutePath() + " "
                        + "--jq=" + new File("test_scripts/js/jquery-1.10.2.min.js").getAbsolutePath() + " "
                        + "--tests=" + configFile.getAbsolutePath() + 
                        " --endpoint=" + clientEndpointUrl + " --token=\"" + token.getToken() + "\""
                ));
        TextUtils.writeFileLines(lines, shellFile);
        {
            long startTime = System.currentTimeMillis();
            ProcessHelper ph = ProcessHelper.cmd("bash", shellFile.getCanonicalPath()).exec(
                    new File(lib2, moduleName), null, true, true);
            System.out.println("(" + (System.currentTimeMillis() - startTime) + " ms)");
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
    }

    protected static void testStatus(File moduleDir, String clientEndpointUrl,
            boolean async, boolean dynamic, String serverType) throws Exception {
        String moduleName = moduleDir.getName();
        File specFile = new File(moduleDir, moduleName + ".spec");
        AuthToken token = AuthService.login(user, pwd).getToken();
        // Java client
        System.out.println("Java client (status) -> " + serverType + " server");
        ClientInstaller clInst = new ClientInstaller(moduleDir);
        clInst.install("java", async, false, dynamic, "dev", false, 
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
        Object client;
        if (async) {
            Class<?> clientClass = urlcl.loadClass(clientClassName);
            client = clientClass.getConstructor(URL.class, AuthToken.class)
                    .newInstance(new URL(clientEndpointUrl), token);
            clientClass.getMethod("setIsInsecureHttpConnectionAllowed", Boolean.TYPE).invoke(client, true);

        } else {
            Class<?> clientClass = urlcl.loadClass(clientClassName);
            client = clientClass.getConstructor(URL.class)
                    .newInstance(new URL(clientEndpointUrl));
        }
        Method method = null;
        for (Method m : client.getClass().getMethods())
            if (m.getName().equals("status"))
                method = m;
        Object obj = null;
        Exception error = null;
        int javaAttempts = dynamic ? 10 : 1;
        for (int i = 0; i < javaAttempts; i++) {
            try {
                obj = method.invoke(client, (Object)null);
                error = null;
                break;
            } catch (Exception ex) {
                error = ex;
            }
            Thread.sleep(100);
        }
        if (error != null)
            throw error;
        checkStatusResponse(obj);
        // Common non-java preparation
        String pcg = moduleName + "Client";
        String cls = moduleName;
        String mtd = "status";
        File inputFile = new File(moduleDir, "status_input.json");
        FileUtils.writeStringToFile(inputFile, "[]");
        File outputFile = new File(moduleDir, "status_output.json");
        File errorFile = new File(moduleDir, "status_error.json");
        File lib2 = new File(moduleDir, "lib2");
        // Python
        System.out.println("Python client (status) -> " + serverType + " server");
        clInst.install("python", async, false, dynamic, "dev", false, 
                specFile.getCanonicalPath(), "lib2");
        {
            File shellFile = new File(moduleDir, "test_python_client.sh");
            List<String> lines = new ArrayList<String>(Arrays.asList("#!/bin/bash"));
            lines.add("python " + new File("test_scripts/python/run_client.py").getAbsolutePath() + 
                    " -e " + clientEndpointUrl + " -g " + pcg + " -c " + cls + " -m " + mtd + 
                    " -i " + inputFile.getAbsolutePath() + " -o " + outputFile.getAbsolutePath() + 
                    " -r " + errorFile.getAbsolutePath() + (async ? (" -u " + user + " -p " + pwd) : ""));
            TextUtils.writeFileLines(lines, shellFile);
            ProcessHelper ph = ProcessHelper.cmd("bash", shellFile.getCanonicalPath()).exec(
                    new File(lib2, moduleName).getCanonicalFile(), null, true, true);
            int exitCode = ph.getExitCode();
            if (exitCode != 0) {
                String out = ph.getSavedOutput();
                if (!out.isEmpty())
                    System.out.println("Python client runner output:\n" + out);
                String err = ph.getSavedErrors();
                if (!err.isEmpty())
                    System.err.println("Python client runner errors:\n" + err);
                Assert.assertEquals("Python client runner exit code should be 0", 0, exitCode);
            } else {
                checkStatusResponse(outputFile, errorFile);
            }
        }
        // Perl client
        if (outputFile.exists())
            outputFile.delete();
        if (errorFile.exists())
            errorFile.delete();
        System.out.println("Perl client (status) -> " + serverType + " server");
        clInst.install("perl", async, false, dynamic, "dev", false, 
                specFile.getCanonicalPath(), "lib2");
        {
            File shellFile = new File(moduleDir, "test_perl_client.sh");
            List<String> lines = new ArrayList<String>(Arrays.asList("#!/bin/bash"));
            String pathToSdk = new File(".").getCanonicalPath();
            lines.add("export PERL5LIB=" + pathToSdk + "/lib/:" +
                    pathToSdk + "/submodules/auth/Bio-KBase-Auth/lib:$PERL5LIB");
            lines.add("perl " + new File("test_scripts/perl/run-client.pl").getAbsolutePath() + 
                    " -package " + moduleName + "::" + moduleName + "Client" + 
                    " -method " + mtd + " -input " + inputFile.getAbsolutePath() + 
                    " -output " + outputFile.getAbsolutePath() + 
                    " -error " + errorFile.getAbsolutePath() +
                    " -endpoint " + clientEndpointUrl +
                    (async ? (" -user " + user + " -password " + pwd) : ""));
            TextUtils.writeFileLines(lines, shellFile);
            ProcessHelper ph = ProcessHelper.cmd("bash", shellFile.getCanonicalPath()).exec(
                    lib2.getCanonicalFile(), null, true, true);
            int exitCode = ph.getExitCode();
            if (exitCode != 0) {
                String out = ph.getSavedOutput();  //outSw.toString();
                if (!out.isEmpty())
                    System.out.println("Perl client runner output:\n" + out);
                String err = ph.getSavedErrors();  //errSw.toString();
                if (!err.isEmpty())
                    System.err.println("Perl client runner errors:\n" + err);
                Assert.assertEquals("Perl client runner exit code should be 0", 0, exitCode);
            } else {
                checkStatusResponse(outputFile, errorFile);
            }
        }
        // JavaScript
        if (!TypeGeneratorTest.isCasperJsInstalled()) {
            System.err.println("- JavaScript client tests are skipped");
            return;
        }
        if (outputFile.exists())
            outputFile.delete();
        if (errorFile.exists())
            errorFile.delete();
        System.out.println("JavaScript client (status) -> " + serverType + " server");
        clInst.install("javascript", async, false, dynamic, "dev", false, 
                specFile.getCanonicalPath(), "lib2");
        {
            File shellFile = new File(moduleDir, "test_js_client.sh");
            List<String> lines = new ArrayList<String>(Arrays.asList("#!/bin/bash"));
            lines.addAll(Arrays.asList(
                    "phantomjs " + new File("test_scripts/js/run-client.js").getAbsolutePath() +
                            " --jq=" + new File("test_scripts/js/jquery-1.10.2.min.js").getAbsolutePath() +
                            " --input=" + inputFile.getAbsolutePath() + 
                            " --output=" + outputFile.getAbsolutePath() + 
                            " --error=" + errorFile.getAbsolutePath() + 
                            " --package=" + pcg + " --class=" + cls + " --method=" + mtd +
                            " --endpoint=" + clientEndpointUrl +  (async ? (" --token=\"" + token.getToken() + "\"") : "")
                    ));
            TextUtils.writeFileLines(lines, shellFile);
            ProcessHelper ph = ProcessHelper.cmd("bash", shellFile.getCanonicalPath()).exec(
                    new File(lib2, moduleName), null, true, true);
            int exitCode = ph.getExitCode();
            if (exitCode != 0) {
                String out = ph.getSavedOutput();
                if (!out.isEmpty())
                    System.out.println("JavaScript client runner output:\n" + out);
                String err = ph.getSavedErrors();
                if (!err.isEmpty())
                    System.err.println("JavaScript client runner errors:\n" + err);
                Assert.assertEquals("JavaScript client runner exit code should be 0", 0, exitCode);
            } else {
                checkStatusResponse(outputFile, errorFile);
            }
        }
    }

    protected static void checkStatusResponse(File output, File error) throws Exception {
        if (!output.exists()) {
            String msg = error.exists() ? FileUtils.readFileToString(error) :
                "Unknown error (error file wasn't created)";
            throw new IllegalStateException(msg);
        }
        checkStatusResponse(UObject.getMapper().readValue(output, Object.class));
    }

    protected static void checkStatusResponse(Object obj) throws Exception {
        Assert.assertNotNull(obj);
        String errMsg = "Unexpected response: " + UObject.transformObjectToString(obj);
        if (obj instanceof List) {
            @SuppressWarnings("rawtypes")
            List<?> list = (List)obj;
            Assert.assertEquals(errMsg, 1, list.size());
            obj = list.get(0);
        }
        Assert.assertTrue(obj instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, String> map = (Map<String, String>)obj;
        Assert.assertEquals(errMsg, "OK", map.get("state"));
        Assert.assertTrue(errMsg, map.containsKey("version"));
        Assert.assertTrue(errMsg, map.containsKey("git_url"));
        Assert.assertTrue(errMsg, map.containsKey("git_commit_hash"));
    }
}
