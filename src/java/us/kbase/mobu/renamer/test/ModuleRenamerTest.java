package us.kbase.mobu.renamer.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import us.kbase.auth.AuthToken;
import us.kbase.mobu.initializer.ModuleInitializer;
import us.kbase.mobu.renamer.ModuleRenamer;
import us.kbase.mobu.tester.test.ModuleTesterTest;
import us.kbase.scripts.test.TestConfigHelper;

public class ModuleRenamerTest {
    private static final String SIMPLE_MODULE_NAME = "a_SimpleModule_for_unit_testing";
    private static final String TARGET_MODULE_NAME = "TargetModule_for_unit_testing";
    private static final List<File> dirsToRemove = new ArrayList<File>();
    private static final boolean deleteTempDirs = true;

    private static AuthToken token = null;

    @BeforeClass
    public static void beforeClass() throws Exception {
        token = TestConfigHelper.getToken();
    }

    @AfterClass
    public static void tearDownModule() throws IOException {
        if (deleteTempDirs)
            for (File module : dirsToRemove) {
                if (module.exists() && module.isDirectory())
                    FileUtils.deleteDirectory(module);
            }
    }

    private static File initRepo(String lang) throws Exception {
        String moduleName = SIMPLE_MODULE_NAME + "_" + lang;
        File ret = new File(moduleName);
        if (ret.exists())
            FileUtils.deleteDirectory(ret);
        dirsToRemove.add(ret);
        ModuleInitializer initer = new ModuleInitializer(moduleName, 
                token.getUserName(), lang, false);
        initer.initialize(true);
        return ret;
    }

    @Test
    public void testJava() throws Exception {
        String newModuleName = TARGET_MODULE_NAME + "_java";
        File moduleDir = initRepo("java");
        new ModuleRenamer(moduleDir).rename(newModuleName);
        int exitCode = ModuleTesterTest.runTestsInDocker(moduleDir, token);
        Assert.assertEquals(0, exitCode);
    }

    @Test
    public void testPython() throws Exception {
        String newModuleName = TARGET_MODULE_NAME + "_python";
        File moduleDir = initRepo("python");
        new ModuleRenamer(moduleDir).rename(newModuleName);
        int exitCode = ModuleTesterTest.runTestsInDocker(moduleDir, token);
        Assert.assertEquals(0, exitCode);
    }
    
    @Test
    public void testPerl() throws Exception {
        String newModuleName = TARGET_MODULE_NAME + "_perl";
        File moduleDir = initRepo("perl");
        new ModuleRenamer(moduleDir).rename(newModuleName);
        int exitCode = ModuleTesterTest.runTestsInDocker(moduleDir, token);
        Assert.assertEquals(0, exitCode);
    }
    
    @Test
    public void testWindowsEOFs() throws Exception {
        String oldModuleName = "a_SimpleModule_for_unit_testing";
        String newModuleName = "a_SimpleModule_for_UnitTesting";
        String text = "" +
        		"module-name:\n" +
                "    " + oldModuleName + "\n" +
                "\n" +
                "module-description:\n" +
                "    A KBase module\n";
        String newText = ModuleRenamer.replace(text, "module-name:\\s*(" + oldModuleName + ")", 
                newModuleName, "module-name key is not found");
        Assert.assertTrue(newText.contains(newModuleName));
    }
}
