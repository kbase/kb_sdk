package us.kbase.scripts;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import us.kbase.jkidl.FileIncludeProvider;
import us.kbase.jkidl.IncludeProvider;
import us.kbase.kidl.KbService;
import us.kbase.kidl.KidlParser;

public class ModuleBuilder {
    private static final String defaultParentPackage = "us.kbase";
    private static final String MODULE_BUILDER_SH_NAME = "kb-module-builder";
    
    private static final String COMPILE_COMMAND = "compile";
    private static final String HELP_COMMAND = "help";
    
    public static void main(String[] args) throws Exception {
    	
    	// setup the basic CLI argument parser with global -h and --help commands
    	GlobalArgs gArgs = new GlobalArgs();
    	JCommander jc = new JCommander(gArgs);
    	jc.setProgramName(MODULE_BUILDER_SH_NAME);

    	// add the 'compile' command
    	CompileCommandArgs compileArgs = new CompileCommandArgs();
    	jc.addCommand(COMPILE_COMMAND, compileArgs);

    	// add the 'help' command
    	HelpCommandArgs help = new HelpCommandArgs();
    	jc.addCommand(HELP_COMMAND, help);
    	
    	// parse the arguments and gracefully catch any errors
    	try {
    		jc.parse(args);
    	} catch (RuntimeException e) {
    		showError("Command Line Arguement Error", e.getMessage());
    		System.exit(1);
    	}
    	
    	// if the help flag is set, then show some help and exit
    	if(gArgs.help) {
    		showBriefHelp(jc, System.out);
    		return;
    	}
    	
	    // no command entered, just print brief info and exit
	    if(jc.getParsedCommand()==null) {
	    	showBriefHelp(jc, System.out);
	    	return;
	    }
	    
	    // if we get here, we have a valid command, so process it and do stuff
	    int returnCode = 0;
	    if(jc.getParsedCommand().equals(HELP_COMMAND)) {
		    showCommandUsage(jc,help,System.out);
		    
	    } else if(jc.getParsedCommand().equals(COMPILE_COMMAND)) {
	    	returnCode = runCompileCommand(compileArgs,jc);
	    }
	    
	    if(returnCode!=0) {
	    	System.exit(returnCode);
	    }
    }
    
    
    public static int runCompileCommand(CompileCommandArgs a, JCommander jc) {
    	
    	// Step 1: convert list of args to a  Files (this must be defined because it is required)
    	File specFile = null;
    	try {
    		if(a.specFileNames.size()>1) {
    			// right now we only support compiling one file at a time
                showError("Compile Error","Too many input KIDL spec files provided",
                		"Currently there is only support for compiling one module at a time, which \n"+
                		"is required to register data types and KIDL specs with the Workspace.  This \n"+
                		"code may be extended in the future to allow multiple modules to be compiled \n"+
                		"together from separate files into the same client/server.");
                return 1;
    		}
        	List<File> specFiles = convertToFiles(a.specFileNames);
        	specFile = specFiles.get(0);
		} catch (IOException | RuntimeException e) {
            showError("Error accessing input KIDL spec file", e.getMessage());
            return 1;
		}
    	
    	// Step 2: check or create the output directory
    	File outDir = a.outDir == null ? new File(".") : new File(a.outDir);
        try {
			outDir = outDir.getCanonicalFile();
		} catch (IOException e) {
            showError("Error accessing output directory", e.getMessage());
            return 1;
		}
        if (!outDir.exists())
            outDir.mkdirs();
        
    	// Step 3: validate the URL option if it is defined
        if (a.url != null) {
            try {
                new URL(a.url);
            } catch (MalformedURLException mue) {
                showError("Compile Option error", "The provided url " + a.url + " is invalid.");
                return 1;
            }
        }
        
        try {
			generate(specFile, a.url, a.jsClientSide, a.jsClientName, a.perlClientSide, 
			        a.perlClientName, a.perlServerSide, a.perlServerName, a.perlImplName, 
			        a.perlPsgiName, a.perlEnableRetries, a.pyClientSide, a.pyClientName, 
			        a.pyServerSide, a.pyServerName, a.pyImplName, a.javaClientSide, 
			        a.javaServerSide, a.javaPackageParent, a.javaSrcDir, a.javaLibDir, 
			        a.javaBuildXml, a.javaGwtPackage, true, outDir, a.jsonSchema, a.makefile);
		} catch (Exception e) {
			System.err.println("Error compiling KIDL specfication:");
			System.err.println(e.getMessage());
			return 1;
		}
        
        return 0;
    }
    

    private static List<File> convertToFiles(List<String> filenames) throws IOException {
    	List <File> files = new ArrayList<File>(filenames.size());
    	for(String filename: filenames) {
    		File f = new File(filename);
    		if(!f.exists()) {
    			throw new IOException("KIDL file \""+filename+"\" does not exist");
    		}
            files.add(f);
    	}
		return files;
    }
    
    
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
                ip, output, perlMakefile, pyMakefile);
    }

    private static class GlobalArgs {
    	@Parameter(names = {"-h","--help"}, help = true, description="Display help and full usage information.")
    	boolean help;
    }
    
    @Parameters(commandDescription = "Get help and usage information.")
    private static class HelpCommandArgs {
    	@Parameter(description="Show usage for this command")
        List<String> command;
    	@Parameter(names={"-a","--all"}, description="Show usage for all commands")
        boolean showAll = false;
    }
    
    @Parameters(commandDescription = "Compile a KIDL file into client and server code")
    private static class CompileCommandArgs {
     
    	@Parameter(names="-out",description="Common output folder (instead of default . folder) which " +
        		"will be used for all relative paths")//, metaVar="<out-dir>")
        String outDir = null;
        
    	@Parameter(names="-js", description="Defines whether or not java-script code for client side " +
        		"should be created, default value is false, use -js for true")
        boolean jsClientSide = false;

    	@Parameter(names="-jsclname", description="JavaScript client name (if defined then -js will be " +
        		"treated as true automatically)")//, metaVar = "<js-client-name>")
        String jsClientName = null;

    	@Parameter(names="-perl", description="Defines whether or not perl code for client side should " +
        		"be created, default value is false, use -perl for true")
        boolean perlClientSide = false;

    	@Parameter(names="-perlclname", description="Perl client name including prefix with module " +
        		"subfolders separated by :: if necessary (if defined then -perl will be " +
        		"treated as true automatically)")//, metaVar = "<perl-client-name>")
        String perlClientName = null;

    	@Parameter(names="-perlsrv", description="Defines whether or not perl code for server side " +
        		"should be created, default value is false, use -perlsrv for true (if defined " +
        		"then -perl will be treated as true automatically)")
        boolean perlServerSide = false;

    	@Parameter(names="-perlsrvname", description="Perl server name including prefix with module " +
        		"subfolders separated by :: if necessary (if defined then -perlsrv will be " +
        		"treated as true automatically)")//, metaVar = "<perl-server-name>")
        String perlServerName = null;

    	@Parameter(names="-perlimplname", description="Perl impl name including prefix with module " +
        		"subfolders separated by :: if necessary (if defined then -perlsrv will be " +
        		"treated as true automatically)")//, metaVar = "<perl-impl-name>")
        String perlImplName = null;

    	@Parameter(names="-perlpsginame", description="Perl PSGI name (if defined then -perlsrv will be " +
        		"treated as true automatically)")//, metaVar = "<perl-psgi-name>")
        String perlPsgiName = null;

    	@Parameter(names="-perlenableretries", description="Defines whether or not perl code for client " +
        		"side should include reconnection retries, default value is false")
        boolean perlEnableRetries = false;

    	@Parameter(names="-py", description="Defines whether or not python code for client side should be " +
        		"created, default value is false, use -py for true")
        boolean pyClientSide = false;

    	@Parameter(names="-pyclname", description="Python client name including prefix with module " +
        		"subfolders separated by '.' if necessary (if defined then -py will be treated " +
        		"as true automatically)")//, metaVar = "<py-client-name>")
        String pyClientName = null;

    	@Parameter(names="-pysrv", description="Defines whether or not python code for server side should " +
        		"be created, default value is false, use -pysrv for true (if defined then -py " +
        		"will be treated as true automatically)")
        boolean pyServerSide = false;

    	@Parameter(names="-pysrvname", description="Python server name including prefix with module " +
        		"subfolders separated by '.' if necessary (if defined then -perlsrv will be " +
        		"treated as true automatically)")//, metaVar = "<py-server-name>")
        String pyServerName = null;

    	@Parameter(names="-pyimplname", description="Python impl name including prefix with module " +
        		"subfolders separated by '.' if necessary (if defined then -perlsrv will be " +
        		"treated as true automatically)")//, metaVar = "<py-impl-name>")
        String pyImplName = null;

    	@Parameter(names="-java", description="Defines whether or not java code for client side should " +
        		"be created, default value is false, use -java for true (if defined then 'src' " +
        		"default value is used for -javasrc if it's not overwritten explicitly)")
        boolean javaClientSide = false;

    	@Parameter(names="-javasrc",description="Source output folder (if defined then -java will be " +
        		"treated as true automatically), default value is 'src'")//, metaVar = 
        		//"<java-src-dir>")
        String javaSrcDir = "src";

    	@Parameter(names="-javalib",description="Jars output folder (if defined then -java will be " +
        		"treated as true automatically), is not defined by default")//, metaVar = 
        		//"<java-lib-dir>")
        String javaLibDir = null;

    	@Parameter(names="-javabuildxml",description="Will generate build.xml template for Ant")
        boolean javaBuildXml;
        
    	@Parameter(names="-url", description="Default url for service")//, metaVar = "<url>")
        String url = null;

    	@Parameter(names="-javapackage",description="Java package parent (module subpackages are " +
        		"created in this package), default value is " + defaultParentPackage)//, 
        		//metaVar = "<java-package>")      
        String javaPackageParent = defaultParentPackage;

    	@Parameter(names="-javasrv", description="Defines whether or not java code for server side " +
        		"should be created, default value is false, use -javasrv for true (if defined " +
        		"then -java will be treated as true automatically)")
        boolean javaServerSide = false;

    	@Parameter(names="-javagwt",description="Gwt client java package (define it in case you need " +
        		"copies of generated classes for GWT client)")//, metaVar="<java-gwt-pckg>")     
        String javaGwtPackage = null;

    	@Parameter(names="-jsonschema",description="JSON schema output folder, is not defined by " +
        		"default")//, metaVar="<json-schema>")
        String jsonSchema = null;

    	@Parameter(names="-makefile",description="Will generate makefile templates for servers and/or java client")
        boolean makefile = false;

        @Parameter(required=true, description="KIDL spec file to compile")
        List <String> specFileNames;
    }
    
    
    
    
    
    
    private static void showBriefHelp(JCommander jc, PrintStream out) {
    	Map<String,JCommander> commands = jc.getCommands();
    	out.println("");
    	out.println(MODULE_BUILDER_SH_NAME + " - tool for building and validating KBase modules and services");
    	out.println("");
    	out.println("usage: "+MODULE_BUILDER_SH_NAME+" <command> [options]");
    	out.println("");
    	out.println("    The available commands are:");
    	for (Map.Entry<String, JCommander> command : commands.entrySet()) {
    	    out.println("        " + command.getKey() +" - "+jc.getCommandDescription(command.getKey()));
    	}
    	out.println("");
    	out.println("    For help on a specific command, see \"kb-module-builder help <command>\".");
    	out.println("    For full usage information, see \"kb-module-builder help -a\".");
    	out.println("");
    };
    
    private static void showCommandUsage(JCommander jc, HelpCommandArgs helpArgs, PrintStream out) {
    	if(helpArgs.showAll) {
    		showFullUsage(jc, out);
    	} else if(helpArgs.command !=null && helpArgs.command.size()>0) {
	    	String indent = "";
			StringBuilder usage = new StringBuilder();
    		for(String cmd:helpArgs.command) {
    			if(jc.getCommands().containsKey(cmd)) {
	    			usage.append(MODULE_BUILDER_SH_NAME + " "+cmd+"\n");
	    			jc.usage(cmd,usage,indent+"    ");
	    			usage.append("\n");
    			} else {
    				out.println("Command \""+cmd+"\" is not a valid command.  To view available commands, run:");
    				out.println("    "+MODULE_BUILDER_SH_NAME+" "+HELP_COMMAND);
    				return;
    			}
    		}
    		out.print(usage.toString());
    	} else {
    		showBriefHelp(jc, out);
    	}
    };
    
    private static void showFullUsage(JCommander jc, PrintStream out) {
    	String indent = "";
		StringBuilder usage = new StringBuilder();
		jc.usage(usage,indent);
		out.print(usage.toString());
    };
    
    
    private static void showError(String error, String message, String extraHelp) {
		System.err.println(error + ": " + message+"\n");
		if(!extraHelp.isEmpty())
			System.err.println(extraHelp+"\n");
		System.err.println("For more help and usage information, run:");
		System.err.println("    "+MODULE_BUILDER_SH_NAME+" "+HELP_COMMAND);
		System.err.println("");
    }
    
    private static void showError(String error, String message) {
    	showError(error,message,"");
    }

}
