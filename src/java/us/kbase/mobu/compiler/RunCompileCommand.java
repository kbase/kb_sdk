package us.kbase.mobu.compiler;

import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

import us.kbase.common.service.UObject;
import us.kbase.jkidl.FileIncludeProvider;
import us.kbase.jkidl.IncludeProvider;
import us.kbase.kidl.KbFuncdef;
import us.kbase.kidl.KbModule;
import us.kbase.kidl.KbModuleComp;
import us.kbase.kidl.KbService;
import us.kbase.kidl.KidlParser;
import us.kbase.mobu.ModuleBuilder;
import us.kbase.mobu.util.DiskFileSaver;
import us.kbase.mobu.util.FileSaver;
import us.kbase.mobu.util.OneFileSaver;
import us.kbase.mobu.util.TextUtils;

public class RunCompileCommand {

    public static void generate(File specFile, String url, boolean jsClientSide, 
            String jsClientName, boolean perlClientSide, String perlClientName, 
            boolean perlServerSide, String perlServerName, String perlImplName, 
            String perlPsgiName, boolean perlEnableRetries, boolean pyClientSide, 
            String pyClientName, boolean pyServerSide, String pyServerName, 
            String pyImplName, boolean javaClientSide, boolean javaServerSide, 
            String javaPackageParent, String javaSrcPath, String javaLibPath, 
            boolean withJavaBuildXml, String javaGwtPackage, boolean rClientSide, 
            String rClientName, boolean rServerSide, String rServerName, 
            String rImplName, boolean newStyle, File outDir, 
            String jsonSchemaPath, boolean createMakefile) throws Exception {
    	
        FileSaver javaSrcDir = null;
        if (javaSrcPath != null)
            javaSrcDir = new DiskFileSaver(correctRelativePath(javaSrcPath, outDir));
        FileSaver javaLibDir = null;
        if (javaLibPath != null) {
            javaLibDir = new DiskFileSaver(correctRelativePath(javaLibPath, outDir));
        }
        IncludeProvider ip = new FileIncludeProvider(specFile.getCanonicalFile().getParentFile());
        FileSaver output = new DiskFileSaver(outDir);
        if (withJavaBuildXml && new File(outDir, "build.xml").exists()) {
            System.err.println("Warning: build.xml file already exists, generation is skipped for it");
            withJavaBuildXml = false;
        }
        FileSaver javaBuildXml = withJavaBuildXml ? new OneFileSaver(output, "build.xml") : null;
        FileSaver jsonSchemas = null;
        if (jsonSchemaPath != null) {
            jsonSchemas = new DiskFileSaver(correctRelativePath(jsonSchemaPath, outDir));
        }
        Reader specReader = new FileReader(specFile);
        Map<String, Map<String, String>> modelToTypeJsonSchemaReturn = null;
        if (jsonSchemas != null)
            modelToTypeJsonSchemaReturn = new TreeMap<String, Map<String, String>>();
        List<KbService> services = KidlParser.parseSpec(KidlParser.parseSpecInt(specReader, 
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
        JavaData javaParsingData = null;
        if (javaClientSide)
            javaParsingData = JavaTypeGenerator.processSpec(services, javaSrcDir, 
                    javaPackageParent, javaServerSide, javaLibDir, javaGwtPackage, 
                    url == null ? null : new URL(url), javaBuildXml, javaMakefile);
        TemplateBasedGenerator.generate(services, url, jsClientSide, jsClientName, 
                perlClientSide, perlClientName, perlServerSide, perlServerName, 
                perlImplName, perlPsgiName, pyClientSide, pyClientName, 
                pyServerSide, pyServerName, pyImplName, rClientSide, rClientName, 
                rServerSide, rServerName, rImplName, perlEnableRetries, newStyle, 
                ip, output, perlMakefile, pyMakefile, newStyle);
        String reportFile = System.getenv("KB_SDK_COMPILE_REPORT_FILE");
        if (reportFile == null || reportFile.isEmpty())
            reportFile = System.getProperty("KB_SDK_COMPILE_REPORT_FILE");
        if (reportFile != null && !reportFile.isEmpty()) {
            perlServerSide = TemplateBasedGenerator.genPerlServer(perlServerSide, 
                    perlServerName, perlImplName, perlPsgiName);
            pyServerSide = TemplateBasedGenerator.genPythonServer(pyServerSide, 
                    pyServerName, pyImplName);
            rServerSide = TemplateBasedGenerator.genPythonServer(rServerSide, 
                    rServerName, rImplName);
            try {
                prepareCompileReport(outDir, services, perlServerSide, perlImplName, 
                        pyServerSide, pyImplName, rServerSide, rImplName, 
                        javaServerSide, javaPackageParent, javaSrcPath, 
                        javaParsingData, new File(reportFile));
            } catch (Exception ex) {
                ex.printStackTrace();
                throw ex;
            }
        }
    }
	
    private static File correctRelativePath(String javaSrcPath, File outDir) {
        File javaSrcDir = new File(javaSrcPath);
        if (!javaSrcDir.isAbsolute())
            javaSrcDir = new File(outDir, javaSrcPath);
        return javaSrcDir;
    }
    
    public static void prepareCompileReport(File codeDir, List<KbService> services,
            boolean perlServerSide, String perlImplName, boolean pyServerSide, 
            String pyImplName, boolean rServerSide, String rImplName,
            boolean javaServerSide, String javaPackageParent, String javaSrcPath, 
            JavaData javaParsingData, File reportFile) throws Exception {
        System.out.println("Preparing SDK compilation report...");
        String sdkVersion = ModuleBuilder.VERSION;
        String sdkGitCommit = ModuleBuilder.getGitCommit();
        String moduleName = null;
        List<String> funcNames = new ArrayList<String>();
        for (KbService srv : services) {
            for (KbModule module : srv.getModules()) {
                moduleName = module.getModuleName();
                for (KbModuleComp comp : module.getModuleComponents()) {
                    if (comp instanceof KbFuncdef) {
                        KbFuncdef func = (KbFuncdef)comp;
                        funcNames.add(func.getName());
                    }
                }
            }
        }
        FileSaver output = new DiskFileSaver(codeDir);
        File implFile = null;
        String comment = null;
        if (perlServerSide) {
            if (perlImplName == null)
                perlImplName = moduleName + "Impl";
            implFile = output.getAsFileOrNull(TemplateBasedGenerator.fixPath(
                    perlImplName, "::") + ".pm");
            comment = "#";
        } else if (pyServerSide) {
            if (pyImplName == null)
                pyImplName = moduleName + "Impl";
            implFile = output.getAsFileOrNull(TemplateBasedGenerator.fixPath(
                    pyImplName, ".") + ".py");
            comment = "#";
        } else if (rServerSide) {
            if (rImplName == null)
                rImplName = moduleName + "Impl";
            implFile = output.getAsFileOrNull(rImplName + ".r");
            comment = "#";
        } else if (javaServerSide) {
            if (javaPackageParent.equals("."))  // Special value meaning top level package.
                javaPackageParent = "";
            FileSaver javaSrcDir = new DiskFileSaver(correctRelativePath(javaSrcPath, 
                    codeDir));
            for (JavaModule module : javaParsingData.getModules()) {
                if (module.getOriginal().getModuleName().equals(moduleName)) {
                    String moduleDir = JavaTypeGenerator.sub(javaPackageParent, 
                            module.getModulePackage()).replace('.', '/');
                    String serverClassName = TextUtils.capitalize(module.getModuleName()) + 
                            "Server";
                    implFile = javaSrcDir.getAsFileOrNull(moduleDir + "/" + 
                            serverClassName + ".java");
                }
            }
            comment = "//";
        }
        String implText = IOUtils.toString(new LineNumberReader(new FileReader(
                implFile)));
        Map<String, FunctionPlace> funcPositions = 
                parsePrevCode(implText, comment, funcNames);
        Map<String, Object> report = new LinkedHashMap<String, Object>();
        report.put("sdk_version", sdkVersion);
        report.put("sdk_git_commit", sdkGitCommit);
        String implPath = implFile.getCanonicalPath();
        String rootPath = new File(".").getCanonicalPath();
        if (implPath.startsWith(rootPath + "/"))
            rootPath += "/";
        if (implPath.startsWith(rootPath))
            implPath = implPath.substring(rootPath.length());
        report.put("impl_file_path", implPath);
        report.put("function_places", funcPositions);
        UObject.getMapper().writeValue(reportFile, report);
        System.out.println("Report was stored into " + reportFile.getAbsolutePath());
    }
    
    public static Map<String, FunctionPlace> parsePrevCode(String implText, 
            String commentPrefix, List<String> funcs) throws Exception {
        commentPrefix = Pattern.quote(commentPrefix);
        Map<String, FunctionPlace> code = new LinkedHashMap<String, FunctionPlace>();
        for (String funcName : funcs) {
            Pattern p = Pattern.compile(MessageFormat.format(".*" + commentPrefix + 
                    "BEGIN {0}\n(.*\n)?[ \t]*" + commentPrefix + "END {0}\n.*", 
                    funcName), Pattern.DOTALL);
            checkMatch(code, p, implText, funcName);
        }
        return code;
    }
    
    private static void checkMatch(Map<String, FunctionPlace> code, Pattern matcher,
            String fullText, String codekey) throws Exception {
        Matcher m = matcher.matcher(fullText);
        if (!m.matches())
            return;
        int start = m.start(1);
        int end = m.end(1);
        if (start >= 0 && end >= start) {
            LineNumberReader lnr = new LineNumberReader(new StringReader(fullText));
            lnr.skip(start);
            int l1 = lnr.getLineNumber();
            lnr.skip(end - start);
            int l2 = lnr.getLineNumber() + 1;
            code.put(codekey, new FunctionPlace().with(l1, l2));
        }
    }
    
    public static class FunctionPlace {
        @JsonProperty("start_line")
        public int startLine;
        @JsonProperty("end_line")
        public int endLine;
        
        public FunctionPlace with(int startLine, int endLine) {
            this.startLine = startLine;
            this.endLine = endLine;
            return this;
        }
    }
}
