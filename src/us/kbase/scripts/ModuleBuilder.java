package us.kbase.scripts;

import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import us.kbase.jkidl.FileIncludeProvider;
import us.kbase.jkidl.IncludeProvider;
import us.kbase.kidl.KbService;
import us.kbase.kidl.KidlParser;

public class ModuleBuilder {
    private static final String defaultParentPackage = "us.kbase";

    public static void main(String[] args) throws Exception {
        Args a = new Args();
        CmdLineParser parser = new CmdLineParser(a);
        parser.setUsageWidth(100);
        if (args.length == 0 || (args.length == 1 && (args[0].equals("-h") || 
                args[0].equals("--help")))) {
            parser.parseArgument("no.spec");
            showUsage(parser, null, System.out);
            return;
        }
        try {
            parser.parseArgument(args);
        } catch( CmdLineException e ) {
            showError(parser, e.getMessage());
            return;
        }
        File outDir = a.outDir == null ? new File(".") : new File(a.outDir);
        outDir = outDir.getCanonicalFile();
        if (!outDir.exists())
            outDir.mkdirs();
        if (a.url != null) {
            try {
                new URL(a.url);
            } catch (MalformedURLException mue) {
                showError(parser, "The provided url " + a.url + " is invalid.");
                return;
            }
        }
        generate(a.specFile, a.url, a.jsClientSide, a.jsClientName, a.perlClientSide, 
                a.perlClientName, a.perlServerSide, a.perlServerName, a.perlImplName, 
                a.perlPsgiName, a.perlEnableRetries, a.pyClientSide, a.pyClientName, 
                a.pyServerSide, a.pyServerName, a.pyImplName, a.javaClientSide, 
                a.javaServerSide, a.javaPackageParent, a.javaSrcDir, a.javaLibDir, 
                a.javaBuildXml, a.javaGwtPackage, true, outDir, a.jsonSchema);
    }

    public static void generate(File specFile, String url, boolean jsClientSide, 
            String jsClientName, boolean perlClientSide, String perlClientName, 
            boolean perlServerSide, String perlServerName, String perlImplName, 
            String perlPsgiName, boolean perlEnableRetries, boolean pyClientSide, 
            String pyClientName, boolean pyServerSide, String pyServerName, 
            String pyImplName, boolean javaClientSide, boolean javaServerSide, 
            String javaPackageParent, String javaSrcPath, String javaLibPath, 
            boolean javaBuildXml, String javaGwtPackage, boolean newStyle, 
            File outDir, String jsonSchemaPath) throws Exception {
        FileSaver javaSrcOut = new DiskFileSaver(correctRelativePath(javaSrcPath, outDir));
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
                newStyle, ip, output, jsonSchemaOut);
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
            IncludeProvider ip, FileSaver output, FileSaver jsonSchemas) throws Exception {
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
        if (javaServerSide)
            javaClientSide = true;
        if (javaGwtPackage != null)
            javaClientSide = true;
        if (javaClientSide)
            JavaTypeGenerator.processSpec(services, javaSrcDir, javaPackageParent, 
                    javaServerSide, javaLibDir, javaGwtPackage, 
                    url == null ? null : new URL(url), javaBuildXml);
        TemplateBasedGenerator.generate(services, url, jsClientSide, jsClientName, 
                perlClientSide, perlClientName, perlServerSide, perlServerName, 
                perlImplName, perlPsgiName, pyClientSide, pyClientName, 
                pyServerSide, pyServerName, pyImplName, perlEnableRetries, true, 
                ip, output);
    }

    private static void showError(CmdLineParser parser, String message) {
        showUsage(parser, message, System.err);
    }

    private static void showUsage(CmdLineParser parser, String message, PrintStream out) {
        if (message != null)
            out.println(message);
        out.println("Program generates client and server source files for JSON RPC calls in supported languages.");
        out.println("Usage: module_builder [options...] <spec-file>");
        out.println("Usage: module_builder {-h|--help}     - to see this help");
        parser.printUsage(out);
    }

    public static class Args {
        @Option(name="-out",usage="Common output folder (instead of default . folder) which " +
        		"will be used for all relative paths", metaVar="<out-dir>")
        String outDir = null;
        
        @Option(name="-js", usage="Defines whether or not java-script code for client side " +
        		"should be created, default value is false, use -js for true")
        boolean jsClientSide = false;

        @Option(name="-jsclname", usage="JavaScript client name (if defined then -js will be " +
        		"treated as true automatically)", metaVar = "<js-client-name>")
        String jsClientName = null;

        @Option(name="-perl", usage="Defines whether or not perl code for client side should " +
        		"be created, default value is false, use -perl for true")
        boolean perlClientSide = false;

        @Option(name="-perlclname", usage="Perl client name including prefix with module " +
        		"subfolders separated by :: if necessary (if defined then -perl will be " +
        		"treated as true automatically)", metaVar = "<perl-client-name>")
        String perlClientName = null;

        @Option(name="-perlsrv", usage="Defines whether or not perl code for server side " +
        		"should be created, default value is false, use -perlsrv for true (if defined " +
        		"then -perl will be treated as true automatically)")
        boolean perlServerSide = false;

        @Option(name="-perlsrvname", usage="Perl server name including prefix with module " +
        		"subfolders separated by :: if necessary (if defined then -perlsrv will be " +
        		"treated as true automatically)", metaVar = "<perl-server-name>")
        String perlServerName = null;

        @Option(name="-perlimplname", usage="Perl impl name including prefix with module " +
        		"subfolders separated by :: if necessary (if defined then -perlsrv will be " +
        		"treated as true automatically)", metaVar = "<perl-impl-name>")
        String perlImplName = null;

        @Option(name="-perlpsginame", usage="Perl PSGI name (if defined then -perlsrv will be " +
        		"treated as true automatically)", metaVar = "<perl-psgi-name>")
        String perlPsgiName = null;

        @Option(name="-perlenableretries", usage="Defines whether or not perl code for client " +
        		"side should include reconnection retries, default value is false")
        boolean perlEnableRetries = false;

        @Option(name="-py", usage="Defines whether or not python code for client side should be " +
        		"created, default value is false, use -py for true")
        boolean pyClientSide = false;

        @Option(name="-pyclname", usage="Python client name including prefix with module " +
        		"subfolders separated by '.' if necessary (if defined then -py will be treated " +
        		"as true automatically)", metaVar = "<py-client-name>")
        String pyClientName = null;

        @Option(name="-pysrv", usage="Defines whether or not python code for server side should " +
        		"be created, default value is false, use -pysrv for true (if defined then -py " +
        		"will be treated as true automatically)")
        boolean pyServerSide = false;

        @Option(name="-pysrvname", usage="Python server name including prefix with module " +
        		"subfolders separated by '.' if necessary (if defined then -perlsrv will be " +
        		"treated as true automatically)", metaVar = "<py-server-name>")
        String pyServerName = null;

        @Option(name="-pyimplname", usage="Python impl name including prefix with module " +
        		"subfolders separated by '.' if necessary (if defined then -perlsrv will be " +
        		"treated as true automatically)", metaVar = "<py-impl-name>")
        String pyImplName = null;

        @Option(name="-java", usage="Defines whether or not java code for client side should " +
        		"be created, default value is false, use -java for true (if defined then 'src' " +
        		"default value is used for -javasrc if it's not overwritten explicitly)")
        boolean javaClientSide = false;

        @Option(name="-javasrc",usage="Source output folder (if defined then -java will be " +
        		"treated as true automatically), default value is 'src'", metaVar = 
        		"<java-src-dir>")
        String javaSrcDir = "src";

        @Option(name="-javalib",usage="Jars output folder (if defined then -java will be " +
        		"treated as true automatically), is not defined by default", metaVar = 
        		"<java-lib-dir>")
        String javaLibDir = null;

        @Option(name="-javabuildxml",usage="Will generate build.xml template for Ant")
        boolean javaBuildXml;
        
        @Option(name="-url", usage="Default url for service", metaVar = "<url>")
        String url = null;

        @Option(name="-javapackage",usage="Java package parent (module subpackages are " +
        		"created in this package), default value is " + defaultParentPackage, 
        		metaVar = "<java-package>")      
        String javaPackageParent = defaultParentPackage;

        @Option(name="-javasrv", usage="Defines whether or not java code for server side " +
        		"should be created, default value is false, use -javasrv for true (if defined " +
        		"then -java will be treated as true automatically)")
        boolean javaServerSide = false;

        @Option(name="-javagwt",usage="Gwt client java package (define it in case you need " +
        		"copies of generated classes for GWT client)", metaVar="<java-gwt-pckg>")     
        String javaGwtPackage = null;

        @Option(name="-jsonschema",usage="JSON schema output folder, is not defined by " +
        		"default", metaVar="<json-schema>")
        String jsonSchema = null;

        @Argument(metaVar="<spec-file>",required=true,usage="File *.spec for compilation into " +
        		"java classes")
        File specFile;
    }

}
