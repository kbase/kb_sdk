package us.kbase.mobu.initializer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import us.kbase.templates.TemplateFormatter;

public class ModuleInitializer {
	private String moduleName;
	private boolean verbose;
	
	private static String[] subdirs = {"data", 
									   "docs", 
									   "scripts",
									   "service",
									   "test", 
									   "ui", 
									   "ui/spec", 
									   "ui/spec/img"};
	
	
	public ModuleInitializer(String moduleName) {
		this(moduleName, false);
	}
	
	public ModuleInitializer(String moduleName, boolean verbose) {
		this.moduleName = moduleName;
		this.verbose = verbose;
	}
	
	public void initialize() throws IOException {
		if (this.moduleName == null) {
			throw new RuntimeException("Unable to create a null directory!");
		}
		if (this.verbose) System.out.println("Initializing module \"" + this.moduleName + "\"");
		
		// 1. build dir with moduleName
		initDirectory(Paths.get(this.moduleName));
		
		// 2. build skeleton subdirs
		for (String dir : subdirs) {
			initDirectory(Paths.get(this.moduleName, dir));
		}

		/*
		 *  3. Fill in templated files and write them
		 */
		
		/* Set up the context - the set of variables used to flesh out the templates */
		Map<String, Object> moduleContext = new HashMap<String, Object>();
		moduleContext.put("module_name", this.moduleName);

		/* Set up the templates to be used 
		 * TODO: move this to some kind of declarative config file
		 */
		Map<String, File> templateFiles = new HashMap<String, File>();
		templateFiles.put("module_typespec", Paths.get(this.moduleName, "service", this.moduleName + ".spec").toFile());
		templateFiles.put("module_travis", Paths.get(this.moduleName, ".travis.yml").toFile());
		templateFiles.put("module_dockerfile", Paths.get(this.moduleName, "scripts", "Dockerfile").toFile());
		templateFiles.put("module_readme", Paths.get(this.moduleName, "README.md").toFile());
		templateFiles.put("module_makefile", Paths.get(this.moduleName, "Makefile").toFile());
		templateFiles.put("module_method_spec_json", Paths.get(this.moduleName, "ui", "spec", "spec.json").toFile());
		templateFiles.put("module_method_spec_yaml", Paths.get(this.moduleName, "ui", "spec", "display.yaml").toFile());

		for (String templateName : templateFiles.keySet()) {
			fillTemplate(moduleContext, templateName, templateFiles.get(templateName));
		}
	}
	
	private void initDirectory(Path dirPath) throws IOException {
		if (this.verbose) System.out.println("Making directory \"" + dirPath.toString() + "\"");
		File newDir = dirPath.toFile();
		if (!newDir.exists()) {
			newDir.mkdir();
		}
		else {
			throw new IOException("Error while creating module - " + dirPath + " already exists!");
		}
	}
	
	private void fillTemplate(Map<?,?> context, String templateName, File outfile) throws IOException {
		if (this.verbose) System.out.println("Building file \"" + outfile.toString() + "\"");
		TemplateFormatter.formatTemplate(templateName, context, true, outfile);
	}
}
