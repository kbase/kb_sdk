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
									   "ui/narrative/methods"};
	
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
		initDirectory(Paths.get(this.moduleName));
		
		// 2. build skeleton subdirs
		for (String dir : subdirList) {
			initDirectory(Paths.get(this.moduleName, dir));
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

		/* Set up the templates to be used 
		 * TODO: move this to some kind of declarative config file
		 */
		Map<String, File> templateFiles = new HashMap<String, File>();
		templateFiles.put("module_typespec", Paths.get(this.moduleName, specFile).toFile());
		templateFiles.put("module_travis", Paths.get(this.moduleName, ".travis.yml").toFile());
		templateFiles.put("module_dockerfile", Paths.get(this.moduleName, "scripts", "Dockerfile").toFile());
		templateFiles.put("module_readme", Paths.get(this.moduleName, "README.md").toFile());
		templateFiles.put("module_makefile", Paths.get(this.moduleName, "Makefile").toFile());
		
		if (example) {
			templateFiles.put("module_method_spec_json", Paths.get(this.moduleName, "ui", "narrative", "methods", "count_contigs_in_set", "spec.json").toFile());
			templateFiles.put("module_method_spec_yaml", Paths.get(this.moduleName, "ui", "narrative", "methods", "count_contigs_in_set", "display.yaml").toFile());
			switch(this.language) {
				case "perl":
					templateFiles.put("module_perl_impl", Paths.get(this.moduleName, "lib", "bio", "kbase", this.moduleName, this.moduleName + "_impl.pm").toFile());
					break;
				case "python":
					initDirectory(Paths.get(this.moduleName, "lib", "biokbase", this.moduleName));
					initFile(Paths.get(this.moduleName, "lib", "biokbase", "__init__.py"));
					initFile(Paths.get(this.moduleName, "lib", "biokbase", this.moduleName, "__init__.py"));
					templateFiles.put("module_python_impl", Paths.get(this.moduleName, "lib", "biokbase", this.moduleName, "Impl.py").toFile());
					break;
				case "java":
					templateFiles.put("module_java_impl", Paths.get(this.moduleName, "lib", "biokbase", this.moduleName, this.moduleName + "_impl.java").toFile());
					break;
				default:
					break;
			}
		} else {
			templateFiles.put("module_method_spec_json", Paths.get(this.moduleName, "ui", "narrative", "methods", "example_method", "spec.json").toFile());
			templateFiles.put("module_method_spec_yaml", Paths.get(this.moduleName, "ui", "narrative", "methods", "example_method", "display.yaml").toFile());
		}

		for (String templateName : templateFiles.keySet()) {
			fillTemplate(moduleContext, templateName, templateFiles.get(templateName), example);
		}
		
		System.out.println("Done! Your module is available in the " + this.moduleName + " directory.");
		if (example) {
			System.out.println("Compile and run the example methods with the following inputs:");
		}
	}
	
	/**
	 * 
	 * @param dirPath
	 * @throws IOException
	 */
	private void initDirectory(Path dirPath) throws IOException {
		if (this.verbose) System.out.println("Making directory \"" + dirPath.toString() + "\"");
		File newDir = dirPath.toFile();
		if (!newDir.exists()) {
			newDir.mkdirs();
		}
		else {
			throw new IOException("Error while creating module - " + dirPath + " already exists!");
		}
	}
	
	private void initFile(Path filePath) throws IOException {
		if (this.verbose) System.out.println("Building empty file \"" + filePath.toString() + "\"");
		boolean done = filePath.toFile().createNewFile();
		if (!done)
			throw new IOException("Unable to create file \"" + filePath.toString() + "\" - file already exists!");
	}
	/**
	 * 
	 * @param context
	 * @param templateName
	 * @param outfile
	 * @throws IOException
	 */
	private void fillTemplate(Map<?,?> context, String templateName, File outfile, boolean useExample) throws IOException {
		if (this.verbose) System.out.println("Building file \"" + outfile.toString() + "\"");
		if (useExample)
			templateName += "_example";
		TemplateFormatter.formatTemplate(templateName, context, true, outfile);
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