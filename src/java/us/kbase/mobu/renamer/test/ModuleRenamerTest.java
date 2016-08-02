package us.kbase.mobu.renamer.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import us.kbase.mobu.initializer.ModuleInitializer;
import us.kbase.mobu.renamer.ModuleRenamer;
import us.kbase.mobu.tester.test.ModuleTesterTest;

public class ModuleRenamerTest {
    private static final String SIMPLE_MODULE_NAME = "a_SimpleModule_for_unit_testing";
    private static final String TARGET_MODULE_NAME = "TargetModule_for_unit_testing";
    private static final List<File> dirsToRemove = new ArrayList<File>();
    private static final boolean deleteTempDirs = true;

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
        ModuleInitializer initer = new ModuleInitializer(moduleName, user, lang, false);
        initer.initialize(true);
        return ret;
    }

    @Test
    public void testJava() throws Exception {
        String newModuleName = TARGET_MODULE_NAME + "_java";
        File moduleDir = initRepo("java");
        new ModuleRenamer(moduleDir).rename(newModuleName);
        int exitCode = ModuleTesterTest.runTestsInDocker(moduleDir, user, pwd);
        Assert.assertEquals(0, exitCode);
    }

    @Test
    public void testPython() throws Exception {
        String newModuleName = TARGET_MODULE_NAME + "_python";
        File moduleDir = initRepo("python");
        new ModuleRenamer(moduleDir).rename(newModuleName);
        int exitCode = ModuleTesterTest.runTestsInDocker(moduleDir, user, pwd);
        Assert.assertEquals(0, exitCode);
    }
    
    @Test
    public void testPerl() throws Exception {
        String newModuleName = TARGET_MODULE_NAME + "_perl";
        File moduleDir = initRepo("perl");
        new ModuleRenamer(moduleDir).rename(newModuleName);
        int exitCode = ModuleTesterTest.runTestsInDocker(moduleDir, user, pwd);
        Assert.assertEquals(0, exitCode);
    }
    
    @Test
    public void testR() throws Exception {
        String newModuleName = TARGET_MODULE_NAME + "_r";
        File moduleDir = initRepo("r");
        new ModuleRenamer(moduleDir).rename(newModuleName);
        int exitCode = ModuleTesterTest.runTestsInDocker(moduleDir, user, pwd);
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
