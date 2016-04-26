package us.kbase.mobu.renamer.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Test;

import us.kbase.mobu.initializer.ModuleInitializer;
import us.kbase.mobu.renamer.ModuleRenamer;

public class ModuleRenamerTest {
    private static final String SIMPLE_MODULE_NAME = "a_SimpleModule_for_unit_testing";
    private static final String TARGET_MODULE_NAME = "TargetModule_for_unit_testing";
    private static final List<File> dirsToRemove = new ArrayList<File>();

    @AfterClass
    public static void tearDownModule() throws IOException {
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
        ModuleInitializer initer = new ModuleInitializer(moduleName, "kbasetest", lang, false);
        initer.initialize(true);
        return ret;
    }

    @Test
    public void testJava() throws Exception {
        String newModuleName = TARGET_MODULE_NAME + "_java";
        new ModuleRenamer(initRepo("java")).rename(newModuleName);
    }

    @Test
    public void testPython() throws Exception {
        String newModuleName = TARGET_MODULE_NAME + "_python";
        new ModuleRenamer(initRepo("python")).rename(newModuleName);
    }
    
    @Test
    public void testPerl() throws Exception {
        String newModuleName = TARGET_MODULE_NAME + "_perl";
        new ModuleRenamer(initRepo("perl")).rename(newModuleName);
    }
    
    @Test
    public void testR() throws Exception {
        String newModuleName = TARGET_MODULE_NAME + "_r";
        new ModuleRenamer(initRepo("r")).rename(newModuleName);
    }
}
