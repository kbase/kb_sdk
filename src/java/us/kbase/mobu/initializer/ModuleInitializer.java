package us.kbase.mobu.initializer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import us.kbase.templates.TemplateFormatter;

public class ModuleInitializer {
	public static final String DEFAULT_LANGUAGE = "python";
	
	private String moduleName,
				   userName,
				   language;
	private boolean verbose;
	
	private static String[] subdirs = {"data", 
									   "docs", 
									   "scripts",
									   "lib",
									   "test", 
									   "ui", 
									   "ui/narrative", 
									   "ui/narrative/methods",
									   "ui/widgets"};	
	/**
	 * 
	 * @param moduleName
	 */
	public ModuleInitializer(String moduleName) {
		this(moduleName, false);
	}
	
	/**
	 * 
	 * @param moduleName
	 * @param verbose
	 */
	public ModuleInitializer(String moduleName, boolean verbose) {
		this(moduleName, null, DEFAULT_LANGUAGE, verbose);
	}
	
	/**
	 * 
	 * @param moduleName
	 * @param userName
	 * @param verbose
	 */
	public ModuleInitializer(String moduleName, String userName, String language, boolean verbose) {
		this.moduleName = moduleName;
		this.userName = userName;
		this.language = language;
		this.verbose = verbose;
	}
	
	/**
	 * 
	 * @param example
	 * @throws IOException
	 */
	public void initialize(boolean example) throws IOException {
		if (this.moduleName == null) {
			throw new RuntimeException("Unable to create a null directory!");
		}
		this.language = qualifyLanguage(this.language);
		
		if (this.verbose) {
			String msg = "Initializing module \"" + this.moduleName + "\"";
			if (example)
				msg += " with example methods";
			System.out.println(msg);
		}

		List<String> subdirList = new ArrayList<String>(Arrays.asList(subdirs));
		if (example) {
			subdirList.add("ui/narrative/methods/count_contigs_in_set");
			subdirList.add("ui/narrative/methods/count_contigs_in_set/img");
		}
		else {
			subdirList.add("ui/narrative/methods/example_method");
			subdirList.add("ui/narrative/methods/example_method/img");
		}
	
		// 1. build dir with moduleName
		initDirectory(Paths.get(this.moduleName), true);
		
		// 2. build skeleton subdirs
		for (String dir : subdirList) {
			initDirectory(Paths.get(this.moduleName, dir), true);
		}

		/*
		 * 3. Fill in templated files and write them
		 *
		 * Set up the context - the set of variables used to flesh out the templates */
		String specFile = Paths.get(this.moduleName + ".spec").toString();
		
		Map<String, Object> moduleContext = new HashMap<String, Object>();
		moduleContext.put("module_name", this.moduleName);
		moduleContext.put("user_name", this.userName);
		moduleContext.put("spec_file", specFile);
		moduleContext.put("language", this.language);
		moduleContext.put("module_root_path", Paths.get(this.moduleName).toAbsolutePath());
		moduleContext.put("example", example);

		/* Set up the templates to be used 
		 * TODO: move this to some kind of declarative config file
		 */
		Map<String, Path> templateFiles = new HashMap<String, Path>();
		templateFiles.put("module_typespec", Paths.get(this.moduleName, specFile));
		templateFiles.put("module_travis", Paths.get(this.moduleName, ".travis.yml"));
		templateFiles.put("module_dockerfile", Paths.get(this.moduleName, "scripts", "Dockerfile"));
		templateFiles.put("module_readme", Paths.get(this.moduleName, "README.md"));
		templateFiles.put("module_makefile", Paths.get(this.moduleName, "Makefile"));
		templateFiles.put("module_deploy_cfg", Paths.get(this.moduleName, "deploy.cfg"));
		templateFiles.put("module_license", Paths.get(this.moduleName, "LICENSE"));
		templateFiles.put("module_docker_entrypoint", Paths.get(this.moduleName, "scripts", "entrypoint.sh"));
		templateFiles.put("module_readme_lib", Paths.get(this.moduleName, "lib", "README.md"));
		templateFiles.put("module_readme_ui", Paths.get(this.moduleName, "ui", "README.md"));
		templateFiles.put("module_readme_test", Paths.get(this.moduleName, "test", "README.md"));
		templateFiles.put("module_readme_docs", Paths.get(this.moduleName, "docs", "README.md"));
		templateFiles.put("module_readme_data", Paths.get(this.moduleName, "data", "README.md"));
		
		if (example) {
			templateFiles.put("module_method_spec_json", Paths.get(this.moduleName, "ui", "narrative", "methods", "count_contigs_in_set", "spec.json"));
			templateFiles.put("module_method_spec_yaml", Paths.get(this.moduleName, "ui", "narrative", "methods", "count_contigs_in_set", "display.yaml"));
			templateFiles.put("module_test_perl_client", Paths.get(this.moduleName, "test", "test_perl_client.pl"));
			templateFiles.put("module_test_python_client", Paths.get(this.moduleName, "test", "test_python_client.py"));
			templateFiles.put("module_test_java_client", Paths.get(this.moduleName, "test", "test_java_client.java"));
			templateFiles.put("module_test_all_clients", Paths.get(this.moduleName, "test", "test_all_clients.sh"));

			switch(this.language) {
				// Perl just needs an impl file and a start server script
				case "perl":
					templateFiles.put("module_start_perl_server", Paths.get(this.moduleName, "scripts", "start_server.sh"));
					templateFiles.put("module_perl_impl", Paths.get(this.moduleName, "lib", "Bio", "KBase", this.moduleName, "Impl.pm"));
					break;
				// Python needs some empty __init__.py files and the impl file
				case "python":
					initDirectory(Paths.get(this.moduleName, "lib", "biokbase", this.moduleName), false);
					initFile(Paths.get(this.moduleName, "lib", "biokbase", "__init__.py"), false);
					initFile(Paths.get(this.moduleName, "lib", "biokbase", this.moduleName, "__init__.py"), false);
					templateFiles.put("module_python_impl", Paths.get(this.moduleName, "lib", "biokbase", this.moduleName, "Impl.py"));
					templateFiles.put("module_start_python_server", Paths.get(this.moduleName, "scripts", "start_server.sh"));
					break;
				// Not sure what java needs yet. This isn't really implemented, other than as a placeholder.
				case "java":
					templateFiles.put("module_java_impl", Paths.get(this.moduleName, "lib", "src", "us", "kbase", this.moduleName, this.moduleName + "_impl.java"));
					templateFiles.put("module_start_java_server", Paths.get(this.moduleName, "scripts", "start_server.sh"));
					break;
				default:
					break;
			}
		} else {
			templateFiles.put("module_method_spec_json", Paths.get(this.moduleName, "ui", "narrative", "methods", "example_method", "spec.json"));
			templateFiles.put("module_method_spec_yaml", Paths.get(this.moduleName, "ui", "narrative", "methods", "example_method", "display.yaml"));
		}

		for (String templateName : templateFiles.keySet()) {
			fillTemplate(moduleContext, templateName, templateFiles.get(templateName));
		}
		
		System.out.println("Done! Your module is available in the " + this.moduleName + " directory.");
		if (example) {
			System.out.println("Compile and run the example methods with the following inputs:");
			System.out.println("  cd " + this.moduleName);
			System.out.println("  make");
			System.out.println();
		}
	}
	
	/**
	 * 
	 * @param dirPath
	 * @throws IOException
	 */
	private void initDirectory(Path dirPath, boolean failOnExist) throws IOException {
		if (this.verbose) System.out.println("Making directory \"" + dirPath.toString() + "\"");
		File newDir = dirPath.toFile();
		if (!newDir.exists()) {
			newDir.mkdirs();
		}
		else if (failOnExist) {
			throw new IOException("Error while creating module - " + dirPath + " already exists!");
		}
	}
	
	private void initFile(Path filePath, boolean failOnExist) throws IOException {
		if (this.verbose) System.out.println("Building empty file \"" + filePath.toString() + "\"");
		boolean done = filePath.toFile().createNewFile();
		if (!done && failOnExist)
			throw new IOException("Unable to create file \"" + filePath.toString() + "\" - file already exists!");
	}
	/**
	 * 
	 * @param context
	 * @param templateName
	 * @param outfile
	 * @throws IOException
	 */
	private void fillTemplate(Map<?,?> context, String templateName, Path outfilePath) throws IOException {
		if (this.verbose) System.out.println("Building file \"" + outfilePath.toString() + "\"");
		initDirectory(outfilePath.getParent(), false);
		TemplateFormatter.formatTemplate(templateName, context, true, outfilePath.toFile());
	}
	
	/**
	 * Takes a language string and returns a "qualified" form. E.g. "perl", "Perl", "pl", ".pl", should all 
	 * return "perl", "Python", "python", ".py", and "py" should all return Python, etc.
	 * 
	 * Right now, we support Perl, Python, and Java for implementation languages (nodejs next?)
	 * @param language
	 * @return
	 */
	private String qualifyLanguage(String language) {
		String lang = language.toLowerCase();
		
		String[] perlNames = {"perl", ".pl", "pl"};
		if (Arrays.asList(perlNames).contains(lang))
			return "perl";
		
		String[] pythonNames = {"python", ".py", "py"};
		if (Arrays.asList(pythonNames).contains(lang))
			return "python";
		
		String[] javaNames = {"java", ".java"};
		if (Arrays.asList(javaNames).contains(lang))
			return "java";
		
		// If we get here, then we don't recognize it! throw a runtime exception
		throw new RuntimeException("Unrecognized language: " + language + "\n\tWe currently only support Python, Perl, and Java.");
	}
}