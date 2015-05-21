package us.kbase.scripts;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

public class NestedFileSaver implements FileSaver {
    private final FileSaver folder;
    private final String relPath;
    
    public NestedFileSaver(FileSaver folder, String relPath) {
        this.folder = folder;
        this.relPath = relPath;
    }
    
    private String fix(String path) {
        return relPath + "/" + path;
    }
    
    @Override
    public File getAsFileOrNull(String path) throws IOException {
        return folder.getAsFileOrNull(fix(path));
    }
    
    @Override
    public Writer openWriter(String path) throws IOException {
        return folder.openWriter(fix(path));
    }
    
    @Override
    public OutputStream openStream(String path) throws IOException {
        return folder.openStream(fix(path));
    }
}
