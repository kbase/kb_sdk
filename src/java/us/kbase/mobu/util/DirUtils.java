package us.kbase.mobu.util;

import java.io.File;
import java.io.IOException;

public class DirUtils {

    public static boolean isModuleDir(File dir) {
        return  new File(dir, "Dockerfile").exists() &&
                new File(dir, "Makefile").exists() &&
                new File(dir, "kbase.yml").exists() &&
                new File(dir, "lib").exists() &&
                new File(dir, "scripts").exists() &&
                new File(dir, "test").exists() &&
                new File(dir, "ui").exists();
    }

    public static File findModuleDir() throws IOException {
        return findModuleDir(new File("."));
    }
    
    public static File findModuleDir(File dir) throws IOException {
        dir = dir.getCanonicalFile();
        while (!isModuleDir(dir)) {
            dir = dir.getParentFile();
            if (dir == null)
                throw new IllegalStateException("You're currently not in module folder");
        }
        return dir;
    }
}
