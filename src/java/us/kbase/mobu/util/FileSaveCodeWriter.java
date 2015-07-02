package us.kbase.mobu.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JPackage;

/**
 * Implementation of com.sun.codemodel.CodeWriter saving files into
 * implementation of FileSaver interface.
 */
public class FileSaveCodeWriter extends CodeWriter {
    private final FileSaver target;
    
    public FileSaveCodeWriter(FileSaver target) throws IOException {
        this.target = target;
    }
    
    @Override
    public Writer openSource(JPackage pkg, String fileName) throws IOException {
        return target.openWriter(pkg.name().replace('.', '/') + "/" + fileName);
    }

    @Override
    public OutputStream openBinary(JPackage pkg, String fileName) throws IOException {
        throw new IllegalStateException("Binary files are not supported");
    }
    
    @Override
    public void close() throws IOException {
    }
}
