package us.kbase.scripts;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

public class DiskFileSaver implements FileSaver {
    private File rootDir;
    
    public DiskFileSaver(File rootDir) {
        this.rootDir = rootDir;
        if (this.rootDir == null)
            this.rootDir = new File(".");
        if (!this.rootDir.exists())
            this.rootDir.mkdirs();
    }
    
    @Override
    public Writer openWriter(String path) throws IOException {
        File f = getAsFileOrNull(path);
        File dir = f.getParentFile();
        if (!dir.exists())
            dir.mkdirs();
        return new FileWriter(f);
    }
    
    @Override
    public OutputStream openStream(String path) throws IOException {
        File f = getAsFileOrNull(path);
        File dir = f.getParentFile();
        if (!dir.exists())
            dir.mkdirs();
        return new FileOutputStream(f);
    }
    
    @Override
    public File getAsFileOrNull(String path) throws IOException {
        path = path.replace('\\', '/');
        if (path.isEmpty() || path.startsWith("/") || path.equals("..") || 
                path.startsWith("../") || path.contains("/../") || path.endsWith("/.."))
            throw new IOException("Unallowed relative path: " + path);
        return new File(rootDir, path);
    }
}
