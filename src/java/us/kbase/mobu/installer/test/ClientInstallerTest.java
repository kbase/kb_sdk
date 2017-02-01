package us.kbase.mobu.installer.test;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import us.kbase.mobu.initializer.ModuleInitializer;
import us.kbase.mobu.installer.ClientInstaller;

public class ClientInstallerTest {
    private static File tempDir = null;
    
    @BeforeClass
    public static void prepareClass() throws Exception {
        File rootTemp = new File("temp_test");
        tempDir = Files.createTempDirectory(rootTemp.toPath(), "test_install_").toFile();
    }
    
    @AfterClass
    public static void teardownClass() throws Exception {
        if (tempDir.exists()) {
            FileUtils.deleteQuietly(tempDir);
        }
    }
    
    @Test
    public void testJava() throws Exception {
        String moduleName = "JavaModule";
        ModuleInitializer init = new ModuleInitializer(moduleName, "kbasetest", "java", false, 
                tempDir);
        init.initialize(false);
        File moduleDir = new File(tempDir, moduleName);
        File sdkCfgFile = new File(moduleDir, "sdk.cfg");
        FileUtils.writeLines(sdkCfgFile, Arrays.asList("catalog_url=" +
                "https://ci.kbase.us/services/catalog"));
        ClientInstaller ci = new ClientInstaller(moduleDir, true);
        String module2 = "onerepotest";
        ci.install(null, false, false, false, "dev", true, module2, null, null);
        File dir = new File(moduleDir, "lib/src/" + module2);
        //ProcessHelper.cmd("ls", "-l", dir.getAbsolutePath()).exec(moduleDir);
        Assert.assertTrue(new File(dir, "OnerepotestClient.java").exists());
        Assert.assertTrue(new File(dir, "OnerepotestServiceClient.java").exists());
    }
    
    @Test
    public void testPython() throws Exception {
        String moduleName = "PythonModule";
        ModuleInitializer init = new ModuleInitializer(moduleName, "kbasetest", "python", false,
                tempDir);
        init.initialize(false);
        File moduleDir = new File(tempDir, moduleName);
        File sdkCfgFile = new File(moduleDir, "sdk.cfg");
        FileUtils.writeLines(sdkCfgFile, Arrays.asList("catalog_url=" +
                "https://ci.kbase.us/services/catalog"));
        ClientInstaller ci = new ClientInstaller(moduleDir, true);
        String module2 = "onerepotest";
        ci.install(null, false, false, false, "dev", true, module2, null, null);
        File dir = new File(moduleDir, "lib/" + module2);
        Assert.assertTrue(new File(dir, "onerepotestClient.py").exists());
        Assert.assertTrue(new File(dir, "onerepotestServiceClient.py").exists());
    }
}
