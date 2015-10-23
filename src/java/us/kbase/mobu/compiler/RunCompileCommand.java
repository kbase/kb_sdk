package us.kbase.mobu.compiler;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import us.kbase.jkidl.FileIncludeProvider;
import us.kbase.jkidl.IncludeProvider;
import us.kbase.kidl.KbService;
import us.kbase.kidl.KidlParser;
import us.kbase.mobu.util.DiskFileSaver;
import us.kbase.mobu.util.FileSaver;
import us.kbase.mobu.util.OneFileSaver;

public class RunCompileCommand {

    public static void generate(File specFile, String url, boolean jsClientSide, 
            String jsClientName, boolean perlClientSide, String perlClientName, 
            boolean perlServerSide, String perlServerName, String perlImplName, 
            String perlPsgiName, boolean perlEnableRetries, boolean pyClientSide, 
            String pyClientName, boolean pyServerSide, String pyServerName, 
            String pyImplName, boolean javaClientSide, boolean javaServerSide, 
            String javaPackageParent, String javaSrcPath, String javaLibPath, 
            boolean javaBuildXml, String javaGwtPackage, boolean newStyle, 
            File outDir, String jsonSchemaPath, boolean createMakeFile) throws Exception {
    	
        FileSaver javaSrcOut = null;
        if (javaSrcPath != null)
            javaSrcOut = new DiskFileSaver(correctRelativePath(javaSrcPath, outDir));
        FileSaver javaLibOut = null;
        if (javaLibPath != null) {
            javaLibOut = new DiskFileSaver(correctRelativePath(javaLibPath, outDir));
        }
        IncludeProvider ip = new FileIncludeProvider(specFile.getCanonicalFile().getParentFile());
        FileSaver output = new DiskFileSaver(outDir);
        if (javaBuildXml && new File(outDir, "build.xml").exists()) {
            System.err.println("Warning: build.xml file already exists, generation is skipped for it");
            javaBuildXml = false;
        }
        FileSaver buildXml = javaBuildXml ? new OneFileSaver(output, "build.xml") : null;
        FileSaver jsonSchemaOut = null;
        if (jsonSchemaPath != null) {
            jsonSchemaOut = new DiskFileSaver(correctRelativePath(jsonSchemaPath, outDir));
        }
        Reader specReader = new FileReader(specFile);
        generate(specReader, url, jsClientSide, jsClientName, perlClientSide, 
                perlClientName, perlServerSide, perlServerName, perlImplName, 
                perlPsgiName, perlEnableRetries, pyClientSide, pyClientName, pyServerSide, 
                pyServerName, pyImplName, javaClientSide, javaServerSide, 
                javaPackageParent, javaSrcOut, javaLibOut, buildXml, javaGwtPackage, 
                newStyle, ip, output, jsonSchemaOut, createMakeFile);
    }

    private static File correctRelativePath(String javaSrcPath, File outDir) {
        File javaSrcDir = new File(javaSrcPath);
        if (!javaSrcDir.isAbsolute())
            javaSrcDir = new File(outDir, javaSrcPath);
        return javaSrcDir;
    }

    public static void generate(Reader specFile, String url, boolean jsClientSide, 
            String jsClientName, boolean perlClientSide, String perlClientName, 
            boolean perlServerSide, String perlServerName, String perlImplName, 
            String perlPsgiName, boolean perlEnableRetries, boolean pyClientSide, 
            String pyClientName, boolean pyServerSide, String pyServerName, 
            String pyImplName, boolean javaClientSide, boolean javaServerSide, 
            String javaPackageParent, FileSaver javaSrcDir, FileSaver javaLibDir, 
            FileSaver javaBuildXml, String javaGwtPackage, boolean newStyle, 
            IncludeProvider ip, FileSaver output, FileSaver jsonSchemas,
            boolean createMakefile) throws Exception {
        Map<String, Map<String, String>> modelToTypeJsonSchemaReturn = null;
        if (jsonSchemas != null)
            modelToTypeJsonSchemaReturn = new TreeMap<String, Map<String, String>>();
        List<KbService> services = KidlParser.parseSpec(KidlParser.parseSpecInt(specFile, 
                modelToTypeJsonSchemaReturn, ip));
        if (jsonSchemas != null) {
            for (String module : modelToTypeJsonSchemaReturn.keySet()) {
                Map<String, String> typeToSchema = modelToTypeJsonSchemaReturn.get(module);
                for (String type : typeToSchema.keySet()) {
                    Writer w = jsonSchemas.openWriter(module + "/" + type + ".json");
                    w.write(typeToSchema.get(type));
                    w.close();
                }
            }
        }
        FileSaver perlMakefile = null;
        FileSaver pyMakefile = null;
        FileSaver javaMakefile = null;
        if (createMakefile) {
            perlServerSide = TemplateBasedGenerator.genPerlServer(perlServerSide, 
                    perlServerName, perlImplName, perlPsgiName);
            pyServerSide = TemplateBasedGenerator.genPythonServer(pyServerSide, 
                    pyServerName, pyImplName);
            int srvNum = (javaServerSide ? 1 : 0) + (perlServerSide ? 1 : 0) +
                    (pyServerSide ? 1 : 0);
            if (perlServerSide)
                perlMakefile = new OneFileSaver(output, "makefile." + 
                        (srvNum > 1 ? "perl_" : "") + "template");
            if (pyServerSide)
                pyMakefile = new OneFileSaver(output, "makefile." + 
                        (srvNum > 1 ? "py_" : "") + "template");
            if (javaServerSide)
                javaMakefile = new OneFileSaver(output, "makefile." + 
                        (srvNum > 1 ? "java_" : "") + "template");
        }
        if (javaServerSide)
            javaClientSide = true;
        if (javaGwtPackage != null)
            javaClientSide = true;
        if (javaClientSide)
            JavaTypeGenerator.processSpec(services, javaSrcDir, javaPackageParent, 
                    javaServerSide, javaLibDir, javaGwtPackage, 
                    url == null ? null : new URL(url), javaBuildXml, javaMakefile);
        TemplateBasedGenerator.generate(services, url, jsClientSide, jsClientName, 
                perlClientSide, perlClientName, perlServerSide, perlServerName, 
                perlImplName, perlPsgiName, pyClientSide, pyClientName, 
                pyServerSide, pyServerName, pyImplName, perlEnableRetries, newStyle, 
                ip, output, perlMakefile, pyMakefile, newStyle);
    }
	
	
}
