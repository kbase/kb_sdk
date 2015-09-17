package us.kbase.mobu.initializer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import us.kbase.templates.TemplateFormatter;

public class ModuleInitializer {
	private String moduleName;
	private boolean verbose;
	
	private static String[] subdirs = {"data", "docs", "service", "test", "ui"};
	
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
		
		/**
		 *  Steps:
		 *  1. Build the directory with moduleName.
		 *  2. Populate with skeleton subdirs - test, src, scripts, etc.
		 *  3. Copy over static files that are mainly unchanged - .travis.yml, .gitignore, etc.
		 *  4. Add a bunch of files with name filled in from template - Makefile, spec file, etc.
		 *  5. Done!
		 */

		// 1. build dir with moduleName
		initDirectory(Paths.get(this.moduleName));
		
		// 2. build skeleton subdirs
		for (String dir : subdirs) {
			initDirectory(Paths.get(this.moduleName, dir));
		}
		
		/*
		 *  3. copy over static files
		 *  - Intro readmes - UI, docs, service
		 */
		
		
		/*
		 *  4. Fill in templated files and write them
		 *  - Typespec
		 *  - Dockerfile
		 *  - Readme.md
		 *  - method spec
		 *  - method yaml
		 *  - Makefile
		 *  - .travis.yml
		 */
		Map<String, Object> typespecContext = new LinkedHashMap<String, Object>();
		typespecContext.put("module_name", this.moduleName);
		TemplateFormatter.formatTemplate("typespec", typespecContext, true, Paths.get(this.moduleName, this.moduleName + ".spec").toFile());
		
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
	
	private void copyFile(Path source, Path target) throws IOException {
		
	}
	
	private void fillTemplate(Path template) {
		
	}
}
