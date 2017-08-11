package us.kbase.mobu;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.yaml.snakeyaml.Yaml;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import us.kbase.mobu.compiler.RunCompileCommand;
import us.kbase.mobu.initializer.ModuleInitializer;
import us.kbase.mobu.installer.ClientInstaller;
import us.kbase.mobu.renamer.ModuleRenamer;
import us.kbase.mobu.runner.ModuleRunner;
import us.kbase.mobu.tester.ModuleTester;
import us.kbase.mobu.util.ProcessHelper;
import us.kbase.mobu.util.TextUtils;
import us.kbase.mobu.validator.ModuleValidator;

public class ModuleBuilder {
	
    private static final String defaultParentPackage = "us.kbase";
    private static final String MODULE_BUILDER_SH_NAME = "kb-sdk";

    private static final String INIT_COMMAND     = "init";
    private static final String VALIDATE_COMMAND = "validate";
    private static final String COMPILE_COMMAND  = "compile";
    private static final String HELP_COMMAND     = "help";
    private static final String TEST_COMMAND     = "test";
    private static final String VERSION_COMMAND  = "version";
    private static final String RENAME_COMMAND   = "rename";
    private static final String INSTALL_COMMAND  = "install";
    private static final String RUN_COMMAND      = "run";
    //private static final String SUBMIT_COMMAND   = "submit";
    
    public static final String GLOBAL_SDK_HOME_ENV_VAR = "KB_SDK_HOME";
    public static final String DEFAULT_METHOD_STORE_URL = "https://appdev.kbase.us/services/narrative_method_store/rpc";
    
    public static final String VERSION = "1.0.17";
    
    
    public static void main(String[] args) throws Exception {
    	
    	// setup the basic CLI argument parser with global -h and --help commands
    	GlobalArgs gArgs = new GlobalArgs();
    	JCommander jc = new JCommander(gArgs);
    	jc.setProgramName(MODULE_BUILDER_SH_NAME);

    	
    	// add the 'init' command
    	InitCommandArgs initArgs = new InitCommandArgs();
    	jc.addCommand(INIT_COMMAND, initArgs);

    	// add the 'compile' command
    	ValidateCommandArgs validateArgs = new ValidateCommandArgs();
    	jc.addCommand(VALIDATE_COMMAND, validateArgs);
    	
    	// add the 'compile' command
    	CompileCommandArgs compileArgs = new CompileCommandArgs();
    	jc.addCommand(COMPILE_COMMAND, compileArgs);

    	// add the 'help' command
    	HelpCommandArgs help = new HelpCommandArgs();
    	jc.addCommand(HELP_COMMAND, help);

        // add the 'test' command
        TestCommandArgs testArgs = new TestCommandArgs();
        jc.addCommand(TEST_COMMAND, testArgs);

        // add the 'version' command
        VersionCommandArgs versionArgs = new VersionCommandArgs();
        jc.addCommand(VERSION_COMMAND, versionArgs);

        // add the 'rename' command
        RenameCommandArgs renameArgs = new RenameCommandArgs();
        jc.addCommand(RENAME_COMMAND, renameArgs);

        // add the 'install' command
        InstallCommandArgs installArgs = new InstallCommandArgs();
        jc.addCommand(INSTALL_COMMAND, installArgs);
        
        // add the 'run' command
        RunCommandArgs runArgs = new RunCommandArgs();
        jc.addCommand(RUN_COMMAND, runArgs);

    	// parse the arguments and gracefully catch any errors
    	try {
    		jc.parse(args);
    	} catch (RuntimeException e) {
    		showError("Command Line Argument Error", e.getMessage());
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
		    
	    } else if(jc.getParsedCommand().equals(INIT_COMMAND)) {
	    	returnCode = runInitCommand(initArgs,jc);
	    } else if(jc.getParsedCommand().equals(VALIDATE_COMMAND)) {
	    	returnCode = runValidateCommand(validateArgs,jc);
	    } else if(jc.getParsedCommand().equals(COMPILE_COMMAND)) {
	    	returnCode = runCompileCommand(compileArgs,jc);
        } else if(jc.getParsedCommand().equals(TEST_COMMAND)) {
            returnCode = runTestCommand(testArgs,jc);
	    } else if (jc.getParsedCommand().equals(VERSION_COMMAND)) {
	        returnCode = runVersionCommand(versionArgs, jc);
	    } else if (jc.getParsedCommand().equals(RENAME_COMMAND)) {
	        returnCode = runRenameCommand(renameArgs, jc);
	    } else if (jc.getParsedCommand().equals(INSTALL_COMMAND)) {
	        returnCode = runInstallCommand(installArgs, jc);
	    } else if (jc.getParsedCommand().equals(RUN_COMMAND)) {
	        returnCode = runRunCommand(runArgs, jc);
	    }
	    
	    if(returnCode!=0) {
	    	System.exit(returnCode);
	    }
    }
    
    private static int runValidateCommand(ValidateCommandArgs validateArgs, JCommander jc) {
    	// initialize 
    	if(validateArgs.modules==null) {
    		validateArgs.modules = new ArrayList<String>();
    	}
    	if(validateArgs.modules.size()==0) {
    		validateArgs.modules.add(".");
    	}
    	try {
    	    ModuleValidator mv = new ModuleValidator(validateArgs.modules,validateArgs.verbose,
    	            validateArgs.methodStoreUrl, validateArgs.allowSyncMethods);
    	    return mv.validateAll();
    	} catch (Exception e) {
            if (validateArgs.verbose)
                e.printStackTrace();
            showError("Error while validating module", e.getMessage());
            return 1;
        }
	}

    /**
     * Runs the module initialization command - this creates a new module in the relative directory name given.
     * There's only a couple of possible arguments here in the initArgs:
     * userName (required) - the user's Github user name, used to set up some optional fields
     * moduleNames (required) - this catchall represents the module's name. Any whitespace (e.g. token breaks) 
     * are replaced with underscores. So if a user runs:
     *   kb-sdk init my new module
     * they get a module called "my_new_module" in a directory of the same name.
     * @param initArgs
     * @param jc
     * @return
     */
	private static int runInitCommand(InitCommandArgs initArgs, JCommander jc) {
		// Figure out module name.
		// Join together spaced out names with underscores if necessary.
		if (initArgs.moduleNames == null || initArgs.moduleNames.size() == 0) {
			ModuleBuilder.showError("Init Error", "A module name is required.");
			return 1;
		}
		String moduleName = StringUtils.join(initArgs.moduleNames, "_");
		
		// Get username if available
		String userName = null;
		if (initArgs.userName != null)
			userName = initArgs.userName;
		
		// Get chosen language
		String language = ModuleInitializer.DEFAULT_LANGUAGE;
		if (initArgs.language != null)
			language = initArgs.language;
		
		try {
			ModuleInitializer initer = new ModuleInitializer(moduleName, userName, language, initArgs.verbose);
			initer.initialize(initArgs.example);
		}
		catch (Exception e) {
            if (initArgs.verbose)
                e.printStackTrace();
			showError("Error while initializing module", e.getMessage());
			return 1;
		}
		return 0;
	}

	public static int runCompileCommand(CompileCommandArgs a, JCommander jc) {
    	printVersion();
    	// Step 1: convert list of args to a  Files (this must be defined because it is required)
    	File specFile = null;
    	try {
    		if(a.specFileNames.size()>1) {
    			// right now we only support compiling one file at a time
                ModuleBuilder.showError("Compile Error","Too many input KIDL spec files provided",
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
            if (a.verbose) {
                e.printStackTrace();
            }
            return 1;
		}
    	
        if (a.clAsyncVer != null && a.dynservVer != null) {
            showError("Bad arguments",
                    "clasyncver and dynserver cannot both be specified");
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
        
        // Step 4: info collection for status method
        File moduleDir = specFile.getAbsoluteFile().getParentFile();
        String semanticVersion = null;
        try {
            File kbaseYmlFile = new File(moduleDir, "kbase.yml");
            if (kbaseYmlFile.exists()) {
                String kbaseYml = TextUtils.readFileText(kbaseYmlFile);
                @SuppressWarnings("unchecked")
                Map<String,Object> kbaseYmlConfig = (Map<String, Object>)new Yaml().load(kbaseYml);
                semanticVersion = (String)kbaseYmlConfig.get("module-version");
            }
        } catch (Exception ex) {
            System.out.println("WARNING! Couldn't collect semantic version: " + ex.getMessage());
        }
        if (semanticVersion == null)
            semanticVersion = "0.0.1";
        String gitUrl = "";
        String gitCommitHash = "";
        if (new File(moduleDir, ".git").exists()) {
            try {
                gitUrl = getCmdOutput(moduleDir, "git", "config", "--get", "remote.origin.url");
            } catch (Exception ex) {
                System.out.println("WARNING! Couldn't collect git URL: " + ex.getMessage());
            }
            try {
                gitCommitHash = getCmdOutput(moduleDir, "git", "rev-parse", "HEAD");
            } catch (Exception ex) {
                System.out.println("WARNING! Couldn't collect git commit hash: " + ex.getMessage());
            }
        }
        try {
            RunCompileCommand.generate(specFile, a.url, a.jsClientSide, a.jsClientName, a.perlClientSide, 
                    a.perlClientName, a.perlServerSide, a.perlServerName, a.perlImplName, 
                    a.perlPsgiName, a.perlEnableRetries, a.pyClientSide, a.pyClientName, 
                    a.pyServerSide, a.pyServerName, a.pyImplName, a.javaClientSide, 
                    a.javaServerSide, a.javaPackageParent, a.javaSrcDir, a.javaLibDir, 
                    a.javaBuildXml, a.javaGwtPackage, a.rClientSide, a.rClientName, 
                    a.rServerSide, a.rServerName, a.rImplName, outDir, a.jsonSchema, 
                    a.makefile, a.clAsyncVer, a.dynservVer, a.html,
                    semanticVersion, gitUrl, gitCommitHash);
        } catch (Throwable e) {
            System.err.println("Error compiling KIDL specfication:");
            System.err.println(e.getMessage());
            if (a.verbose) {
                e.printStackTrace();
            }
            return 1;
        }
        return 0;
    }

	private static String getCmdOutput(File workDir, String... cmd) throws Exception {
	    StringWriter sw = new StringWriter();
	    PrintWriter pw = new PrintWriter(sw);
	    ProcessHelper.cmd(cmd).exec(workDir, null, pw);
	    return sw.toString().trim();
	}
	
    static List<File> convertToFiles(List<String> filenames) throws IOException {
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
    
    public static class GlobalArgs {
    	@Parameter(names = {"-h","--help"}, help = true, description="Display help and full usage information.")
    	boolean help;
    }
    
    /**
     * Runs the module test command - this runs tests in local docker container.
     * @param testArgs
     * @param jc
     * @return
     */
    private static int runTestCommand(TestCommandArgs testArgs, JCommander jc) {
        // Figure out module name.
        // Join together spaced out names with underscores if necessary.
        int returnCode = 1;

        try {
            ModuleTester tester = new ModuleTester();
            returnCode = tester.runTests(testArgs.methodStoreUrl, testArgs.skipValidation, testArgs.allowSyncMethods);
        }
        catch (Exception e) {
            if (testArgs.verbose)
                e.printStackTrace();
            showError("Error while testing module", e.getMessage());
            return returnCode;
        }

        return returnCode;
    }

    private static void printVersion() {
        String gitCommit = getGitCommit();
        System.out.println("KBase SDK version " + VERSION + (gitCommit == null ? "" : (" (commit " + gitCommit + ")")));
    }

    public static String getGitCommit() {
        String gitCommit = null;
        try {
            Properties gitProps = new Properties();
            InputStream is = ModuleBuilder.class.getResourceAsStream("git.properties");
            gitProps.load(is);
            is.close();
            gitCommit = gitProps.getProperty("commit");
        } catch (Exception ignore) {}
        return gitCommit;
    }
    
    private static int runVersionCommand(VersionCommandArgs testArgs, JCommander jc) {
        printVersion();
        return 0;
    }

    private static int runRenameCommand(RenameCommandArgs renameArgs, JCommander jc) {
        if (renameArgs.newModuleName == null || renameArgs.newModuleName.size() != 1) {
            ModuleBuilder.showError("Command Line Argument Error", "One and only one module name should be provided");
            return 1;
        }
        try {
            return new ModuleRenamer().rename(renameArgs.newModuleName.get(0));
        } catch (Exception e) {
            if (renameArgs.verbose)
                e.printStackTrace();
            showError("Error while renaming module", e.getMessage());
            return 1;
        }
    }

    private static int runInstallCommand(InstallCommandArgs installArgs, JCommander jc) {
        if (installArgs.moduleName == null || installArgs.moduleName.size() != 1) {
            ModuleBuilder.showError("Command Line Argument Error", 
                    "One and only one module name should be provided");
            return 1;
        }
        try {
            return new ClientInstaller().install(installArgs.lang, installArgs.async,
                    installArgs.core || installArgs.sync, installArgs.dynamic, 
                    installArgs.tagVer, installArgs.verbose, installArgs.moduleName.get(0), 
                    null, installArgs.clientName);
        } catch (Exception e) {
            if (installArgs.verbose)
                e.printStackTrace();
            showError("Error while installing client", e.getMessage());
            return 1;
        }
    }

    private static int runRunCommand(RunCommandArgs runArgs, JCommander jc) {
        if (runArgs.methodName == null || runArgs.methodName.size() != 1) {
            ModuleBuilder.showError("Command Line Argument Error", 
                    "One and only one method name should be provided");
            return 1;
        }
        try {
            if (runArgs.inputFile == null && runArgs.inputJson == null && !runArgs.stdin)
                throw new IllegalStateException("No one input method is used " +
                		"(should be one of '-i', '-j' or '-s')");
            return new ModuleRunner(runArgs.sdkHome).run(runArgs.methodName.get(0), 
                    runArgs.inputFile, runArgs.stdin, runArgs.inputJson, runArgs.output, 
                    runArgs.tagVer, runArgs.verbose, runArgs.keepTempFiles, runArgs.provRefs, 
                    runArgs.mountPoints);
        } catch (Exception e) {
            if (runArgs.verbose)
                e.printStackTrace();
            showError("Error while installing client", e.getMessage());
            return 1;
        }
    }

    @Parameters(commandDescription = "Validate a module or modules.")
    private static class ValidateCommandArgs {
    	@Parameter(names={"-v","--verbose"}, description="Show verbose output")
        boolean verbose = false;
    	
    	@Parameter(names={"-m", "--method_store"}, description="Narrative Method Store URL " +
    			"(default is " + DEFAULT_METHOD_STORE_URL + ")")
    	String methodStoreUrl = DEFAULT_METHOD_STORE_URL;
    	
    	@Parameter(names={"-a","--allow_sync_method"}, description="Allow synchonous methods " +
    			"(advanced option, default is false)")
        boolean allowSyncMethods = false;
    	
    	@Parameter(description="[path to the module directories]")
        List<String> modules;
    }
    
    @Parameters(commandDescription = "Initialize a module in the current directory.")
    private static class InitCommandArgs {
    	@Parameter(names={"-v","--verbose"}, description="Show verbose output")
    	boolean verbose = false;
    	
    	@Parameter(required=true, names={"-u","--user"}, 
    			description="(Required) provide a username to serve as the owner of this module")
    	String userName;
    	
    	@Parameter(names={"-e","--example"}, 
    			description="Include a fully featured example in your module. " +
    			"This generates an example set of code and configurations that can " +
    			"be used to demonstrate a very simple example.")
    	boolean example = false;
    	
    	@Parameter(names={"-l","--language"}, description="Choose a language for developing " + 
    			" code in your module. You can currently choose from Python, Perl, R and Java " + 
    			"(default=Python)")
    	String language = ModuleInitializer.DEFAULT_LANGUAGE;
    	
    	@Parameter(required=true, description="<module name>")
    	List<String> moduleNames;
    }
    
    
    @Parameters(commandDescription = "Get help and usage information.")
    private static class HelpCommandArgs {
    	@Parameter(description="Show usage for this command")
        List<String> command;
    	@Parameter(names={"-a","--all"}, description="Show usage for all commands")
        boolean showAll = false;
    }
    
    @Parameters(commandDescription = "Compile a KIDL file into client and server code")
    public static class CompileCommandArgs {
     
    	@Parameter(names="--out",description="Set the output folder name (default is the current directory)")
    			//, metaVar="<out-dir>")
        String outDir = null;
        
    	@Parameter(names="--js", description="Generate a JavaScript client with a standard default name")
        boolean jsClientSide = false;

    	@Parameter(names="--jsclname", description="Generate a JavaScript client with the name provided by this option" +
    			"(overrides --js option)")//, metaVar = "<js-client-name>")
        String jsClientName = null;

    	@Parameter(names="--pl", description="Generate a Perl client with the default name")
        boolean perlClientSide = false;

    	@Parameter(names="--plclname", description="Generate a Perl client with the name provided optionally " +
        		"prefixed by subdirectories separated by :: as in the standard perl module syntax (e.g. "+
    			"Bio::KBase::MyModule::Client, overrides the --pl option)")//, metaVar = "<perl-client-name>")
        String perlClientName = null;

    	@Parameter(names="--plsrv", description="Generate a Perl server with a " +
    			"standard default name.  If set, Perl clients will automatically be generated too.")
        boolean perlServerSide = false;

    	@Parameter(names="--plsrvname", description="Generate a Perl server with with the " +
    			"name provided, optionally prefixed by subdirectories separated by :: as "+
    			"in the standard perl module syntax (e.g. Bio::KBase::MyModule::Server, "+
    			"overrides the --plserv option).  If set, Perl clients will be generated too.")
    			//, metaVar = "<perl-server-name>")
        String perlServerName = null;

    	@Parameter(names="--plimplname", description="Generate a Perl server implementation with the " +
    			"name provided, optionally prefixed by subdirectories separated by :: as "+
    			"in the standard Perl module syntax (e.g. Bio::KBase::MyModule::Impl). "+
    			"If set, Perl server and client code will be generated too.")//, metaVar = "<perl-impl-name>")
        String perlImplName = null;

    	@Parameter(names="--plpsginame", description="Generate a perl PSGI file with the name provided.")//, metaVar = "<perl-psgi-name>")
        String perlPsgiName = null;

    	@Parameter(names="--plenableretries", description="When set, generated Perl client code will enable "+
        		"reconnection retries with the server")
        boolean perlEnableRetries = false;

    	@Parameter(names="--py", description="Generate a Python client with a standard default name")
        boolean pyClientSide = false;

    	@Parameter(names="--pyclname", description="Generate a Python client with with the " +
    			"name provided, optionally prefixed by subdirectories separated by '.' as "+
    			"in the standard Python module syntax (e.g. biokbase.mymodule.client,"+
    			"overrides the --py option).")//, metaVar = "<py-client-name>")
        String pyClientName = null;

    	@Parameter(names="--pysrv", description="Generate a Python server with a " +
    			"standard default name.  If set, Python clients will automatically be generated too.")
        boolean pyServerSide = false;

    	@Parameter(names="--pysrvname", description="Generate a Python server with the " +
    			"name provided, optionally prefixed by subdirectories separated by '.' as "+
    			"in the standard Python module syntax (e.g. biokbase.mymodule.server,"+
    			"overrides the --pysrv option).")//, metaVar = "<py-server-name>")
        String pyServerName = null;

    	@Parameter(names="--pyimplname", description="Generate a Python server implementation with the " +
    			"name provided, optionally prefixed by subdirectories separated by '.' as "+
    			"in the standard Python module syntax (e.g. biokbase.mymodule.impl)." +
    			" If set, Python server and client code will be generated too.")//, metaVar = "<py-impl-name>")
        String pyImplName = null;

    	@Parameter(names="--java", description="Generate Java client code in the directory set by --javasrc")
        boolean javaClientSide = false;

    	@Parameter(names="--javasrc",description="Set the output folder for generated Java code")//, metaVar = 
        		//"<java-src-dir>")
        String javaSrcDir = "src";

    	@Parameter(names="--javalib",description="Set the output folder for jar files (if defined then --java will be " +
        		"treated as true automatically)")//, metaVar = 
        		//"<java-lib-dir>")
        String javaLibDir = null;

    	@Parameter(names="--url", description="Set the default url for the service in generated client code")//, metaVar = "<url>")
        String url = null;

    	@Parameter(names="--javapackage",description="Set the Java package for generated code (module subpackages are " +
        		"created in this package), default value is " + defaultParentPackage)//, 
        		//metaVar = "<java-package>")      
        String javaPackageParent = defaultParentPackage;

    	@Parameter(names="--javasrv", description="Generate Java server code in the directory set by --javasrc")
        boolean javaServerSide = false;

    	@Parameter(names="--javagwt",description="Generate a GWT client Java package (useful if you need " +
        		"copies of generated classes for GWT clients)")//, metaVar="<java-gwt-pckg>")     
        String javaGwtPackage = null;

        @Parameter(names="--r", description="Generate a Python client with a standard default name")
        boolean rClientSide = false;

        @Parameter(names="--rclname", description="Generate an R client with with the " +
                "name provided, optionally prefixed by subdirectories separated by '/' as "+
                "in the standard file path syntax (e.g. biokbase/mymodule/client,"+
                "overrides the --r option).")
        String rClientName = null;

        @Parameter(names="--rsrv", description="Generate an R server with a " +
                "standard default name.  If set, Python clients will automatically be generated too.")
        boolean rServerSide = false;

        @Parameter(names="--rsrvname", description="Generate an R server with the " +
                "name provided, optionally prefixed by subdirectories separated by '/' as "+
                "in the standard file path syntax (e.g. biokbase/mymodule/server,"+
                "overrides the --rsrv option).")
        String rServerName = null;

        @Parameter(names="--rimplname", description="Generate an R server implementation with the " +
                "name provided, optionally prefixed by subdirectories separated by '/' as "+
                "in the standard file path syntax (e.g. biokbase/mymodule/impl)." +
                " If set, R server and client code will be generated too.")
        String rImplName = null;

    	@Parameter(names="--jsonschema",description="Generate JSON schema documents for the types in the output folder specified.")//, metaVar="<json-schema>")
        String jsonSchema = null;

    	
    	@Parameter(names="--javabuildxml",description="Will generate build.xml template for Ant")
        boolean javaBuildXml;
    	
    	@Parameter(names="--makefile",description="Will generate makefile templates for servers and/or java client")
        boolean makefile = false;

        @Parameter(names="--clasyncver",description="Will set in client code version of service for asyncronous calls " +
        		"(it could be git commit hash of version registered in catalog or one of version tags: dev/beta/release)")
        String clAsyncVer = null;
        
        @Parameter(names="--dynservver", description="Clients will be built " +
                "for use with KBase dynamic services (e.g. with URL lookup " +
                "via the Service Wizard) with the specified version " +
                "(git commit hash or dev/beta/release)." +
                "clasyncver may not be specified if " +
                "dynservver is specified.")
        String dynservVer = null;
        
        @Parameter(names="--html", description="Generate HTML version(s) of " +
               "the input spec file(s)")
        boolean html = false;
        
        @Parameter(names={"-v", "--verbose"}, description="Print full stack " +
                "trace on a compile failure")
        boolean verbose = false;

        @Parameter(required=true, description="<KIDL spec file>")
        List <String> specFileNames;
    }
    
    @Parameters(commandDescription = "Test a module with local Docker.")
    private static class TestCommandArgs {
        @Parameter(names={"-m", "--method_store"}, description="Narrative Method Store URL used in validation " +
                "(default is " + DEFAULT_METHOD_STORE_URL + ")")
        String methodStoreUrl = DEFAULT_METHOD_STORE_URL;
        
        @Parameter(names={"-s", "--skip_validation"}, description="Will skip validation step (default is false)")
        boolean skipValidation = false;
        
        @Parameter(names={"-a","--allow_sync_method"}, description="Allow synchonous methods " +
                "(advanced option, part of validation settings, default value is false)")
        boolean allowSyncMethods = false;

        @Parameter(names={"-v","--verbose"}, description="Print more details including error stack traces")
        boolean verbose = false;
    }
    
    @Parameters(commandDescription = "Print current version of kb-sdk.")
    private static class VersionCommandArgs {
    }
    
    @Parameters(commandDescription = "Rename a module name.")
    private static class RenameCommandArgs {
        @Parameter(names={"-v","--verbose"}, description="Print more details including error stack traces")
        boolean verbose = false;

        @Parameter(required=true, description="<new module name>")
        List<String> newModuleName;
    }

    @Parameters(commandDescription = "Install a client for KBase module.")
    private static class InstallCommandArgs {
        @Parameter(names={"-l", "--language"}, description="Language of generated client code " +
                "(default is one defined in kbase.yml)")
        String lang;
        
        @Parameter(names={"-a", "--async"}, description="Force generation of asynchronous calls " +
        		"(default is chosen based on information registered in catalog)")
        boolean async = false;

        @Parameter(names={"-s", "--sync"}, description="Depricated flag, means the same as -c (--core)")
        boolean sync = false;

        @Parameter(names={"-c", "--core"}, description="Force generation of calls to core service" +
        		"(WARNING: please use it only for core services not registered in catalog)")
        boolean core = false;

        @Parameter(names={"-d", "--dynamic"}, description="Force generation of dynamic service calls" +
                "(default is chosen based on information registered in catalog)")
        boolean dynamic = false;

        @Parameter(names={"-t","--tag-or-ver"}, description="Tag or version " +
                "(default is chosen based on information registered in catalog)")
        String tagVer = null;

        @Parameter(names={"-v","--verbose"}, description="Print more details including error stack traces")
        boolean verbose = false;
        
        @Parameter(names={"-n","--clientname"}, description="Optional parameter defining custom client name " +
                "(default is module name)")
        String clientName = null;

        @Parameter(required=true, description="<module name or path/URL to spec-file>")
        List<String> moduleName;
    }

    @Parameters(commandDescription = "Run a method of registered KBase module locally.")
    private static class RunCommandArgs {
        @Parameter(names={"-i", "--input"}, description="Input JSON file " +
        		"(optional, if not set -s or -j should be used)")
        File inputFile = null;

        @Parameter(names={"-s","--stdin"}, description="Flag for reading input data from STDIN " +
        		"(default is false, used if neither -i or -j is set)")
        boolean stdin = false;

        @Parameter(names={"-j","--json"}, description="Input JSON string " +
        		"(optional, if not set -s or -i should be used)")
        String inputJson = null;

        @Parameter(names={"-o", "--output"}, description="Output JSON file " +
        		"(optional, if not set output will be printed to STDOUT)")
        File output = null;

        @Parameter(names={"-t","--tag-or-ver"}, description="Tag or version " +
                "(default is chosen based on information registered in catalog)")
        String tagVer = null;

        @Parameter(names={"-v","--verbose"}, description="Print more details including error " +
        		"stack traces")
        boolean verbose = false;

        @Parameter(names={"-k","--keep-tmp"}, description="Keep temporary files/folders at the " +
        		"end (default value is false)")
        boolean keepTempFiles = false;
        
        @Parameter(names={"-h","--sdk-home"}, description="Home folder of kb-sdk where sdk.cfg " +
        		"file and run_local folder are expected to be found or created if absent " +
                "(default path is loaded from 'KB_SDK_HOME' system environment variable)")
        String sdkHome = null;

        @Parameter(names={"-r","--prov-refs"}, description="Optional comma-separated list of " +
        		"references workspace objects that will be refered from provenance")
        String provRefs = null;

        @Parameter(names={"-m","--mount-points"}, description="Optional comma-separated list of " +
        		"mount point pairs for docker container (this parameter contains of local folder" +
        		" and inner folder separated by ':', local folder points to place in local file " +
        		"system, inner folder appears inside docker container, if inner path is not " +
        		"absolute it's treated as relative to /kb/module/work folder; if some mount " +
        		"point path doesn't have ':' and inner part then it appears as " +
        		"/kb/module/work/tmp inside docker)")
        String mountPoints = null;

        @Parameter(required=true, description="<fully qualified method name " +
        		"(with SDK module prefix followed by '.')>")
        List<String> methodName;
    }

    private static void showBriefHelp(JCommander jc, PrintStream out) {
    	Map<String,JCommander> commands = jc.getCommands();
    	out.println("");
    	out.println(MODULE_BUILDER_SH_NAME + " - a developer tool for building and validating KBase modules");
    	out.println("");
    	out.println("usage: "+MODULE_BUILDER_SH_NAME+" <command> [options]");
    	out.println("");
    	out.println("    The available commands are:");
    	for (Map.Entry<String, JCommander> command : commands.entrySet()) {
    	    out.println("        " + command.getKey() +" - "+jc.getCommandDescription(command.getKey()));
    	}
    	out.println("");
    	out.println("    For help on a specific command, see \"kb-sdk help <command>\".");
    	out.println("    For full usage information, see \"kb-sdk help -a\".");
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
