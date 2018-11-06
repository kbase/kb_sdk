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

import us.kbase.mobu.compiler.JavaData;
import us.kbase.mobu.compiler.JavaModule;
import us.kbase.mobu.compiler.JavaTypeGenerator;
import us.kbase.mobu.installer.ClientInstaller;
import us.kbase.templates.TemplateFormatter;

public class ModuleInitializer {
	public static final String DEFAULT_LANGUAGE = "python";

	private String moduleName;
	private String userName;
	private String language;
	private boolean verbose;
	private File dir;
	
	private static String[] subdirs = {"data",
										"scripts",
										"lib",
										"test",
										"ui",
										"ui/narrative",
										"ui/narrative/methods"};
	
	public ModuleInitializer(String moduleName, String userName, boolean verbose) {
		this(moduleName, userName, DEFAULT_LANGUAGE, verbose);
	}
	
	public ModuleInitializer(String moduleName, String userName, String language, boolean verbose) {
	    this(moduleName, userName, language, verbose, null);
	}
	
    public ModuleInitializer(String moduleName, String userName, String language, 
            boolean verbose, File dir) {
		this.moduleName = moduleName;
		this.userName = userName;
		this.language = language == null ? DEFAULT_LANGUAGE : language;
		this.verbose = verbose;
		this.dir = dir;
	}
	
	/**
	 * 
	 * @param example
	 * @throws IOException
	 */
	public void initialize(boolean example) throws Exception {
		if (this.moduleName == null) {
			throw new RuntimeException("Unable to create a null directory!");
		}
        String moduleDir = dir == null ? this.moduleName : 
            new File(dir, this.moduleName).getPath();
		this.language = qualifyLanguage(this.language);
		
		if (this.verbose) {
			String msg = "Initializing module \"" + this.moduleName + "\"";
			if (example)
				msg += " with example methods";
			System.out.println(msg);
		}

		List<String> subdirList = new ArrayList<String>(Arrays.asList(subdirs));
		if (example) {
		    if (this.language.equals("python") || this.language.equals("perl") || this.language.equals("java")) {
	            subdirList.add("ui/narrative/methods/filter_contigs");
	            subdirList.add("ui/narrative/methods/filter_contigs/img");
		    } else {
		        subdirList.add("ui/narrative/methods/count_contigs_in_set");
		        subdirList.add("ui/narrative/methods/count_contigs_in_set/img");
		    }
		}
		else {
			subdirList.add("ui/narrative/methods/run_" + this.moduleName);
			subdirList.add("ui/narrative/methods/run_" + this.moduleName + "/img");
		}
	
		// 1. build dir with moduleName
		initDirectory(Paths.get(moduleDir), true);
		
		// 2. build skeleton subdirs
		for (String dir : subdirList) {
			initDirectory(Paths.get(moduleDir, dir), true);
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
		moduleContext.put("module_root_path", Paths.get(moduleDir).toAbsolutePath());
		moduleContext.put("example", example);
		moduleContext.put("dollar_sign", "$");
        moduleContext.put("os_name", System.getProperty("os.name"));


		Map<String, Path> templateFiles = new HashMap<String, Path>();
		templateFiles.put("module_typespec", Paths.get(moduleDir, specFile));
		templateFiles.put("module_travis", Paths.get(moduleDir, ".travis.yml"));
		templateFiles.put("module_dockerfile", Paths.get(moduleDir, "Dockerfile"));
		templateFiles.put("module_readme", Paths.get(moduleDir, "README.md"));
		templateFiles.put("module_release_notes", Paths.get(moduleDir, "RELEASE_NOTES.md"));
		templateFiles.put("module_makefile", Paths.get(moduleDir, "Makefile"));
		templateFiles.put("module_deploy_cfg", Paths.get(moduleDir, "deploy.cfg"));
		templateFiles.put("module_license", Paths.get(moduleDir, "LICENSE"));
		templateFiles.put("module_docker_entrypoint", Paths.get(moduleDir, "scripts", "entrypoint.sh"));
        templateFiles.put("module_prepare_deploy_cfg", Paths.get(moduleDir, "scripts", "prepare_deploy_cfg.py"));
        templateFiles.put("module_run_async", Paths.get(moduleDir, "scripts", "run_async.sh"));
		templateFiles.put("module_readme_lib", Paths.get(moduleDir, "lib", "README.md"));
		templateFiles.put("module_readme_ui", Paths.get(moduleDir, "ui", "README.md"));
		templateFiles.put("module_readme_test", Paths.get(moduleDir, "test", "README.md"));
		templateFiles.put("module_readme_data", Paths.get(moduleDir, "data", "README.md"));
		templateFiles.put("module_config_yaml", Paths.get(moduleDir, "kbase.yml"));
        templateFiles.put("module_gitignore", Paths.get(moduleDir, ".gitignore"));
        templateFiles.put("module_dockerignore", Paths.get(moduleDir, ".dockerignore"));
        templateFiles.put("module_readme_test_local", Paths.get(moduleDir, "test_local", "readme.txt"));
        templateFiles.put("module_test_cfg", Paths.get(moduleDir, "test_local", "test.cfg"));
        templateFiles.put("module_run_tests", Paths.get(moduleDir, "test_local", "run_tests.sh"));
        templateFiles.put("module_run_bash", Paths.get(moduleDir, "test_local", "run_bash.sh"));
        templateFiles.put("module_run_docker", Paths.get(moduleDir, "test_local", "run_docker.sh"));
		
		switch (language) {
		case "java":
            templateFiles.put("module_build_xml", Paths.get(moduleDir, "build.xml"));
		    templateFiles.put("module_web_xml", Paths.get(moduleDir, "scripts", "web.xml"));
            templateFiles.put("module_jetty_xml", Paths.get(moduleDir, "scripts", "jetty.xml"));
		    fillTemplate(moduleContext, "module_typespec", templateFiles.remove("module_typespec"));
            String javaPackageParent = ".";  // Special value meaning top level package.
            moduleContext.put("java_package_parent", javaPackageParent);
            JavaData data = JavaTypeGenerator.parseSpec(new File(moduleDir, specFile));
            JavaModule module = data.getModules().get(0);
            moduleContext.put("java_package", module.getModulePackage());
            moduleContext.put("java_module_name", module.getModuleName());
            File testSrcDir = new File(moduleDir, "test/src");
            String modulePackage = (String)moduleContext.get("java_package");
            String javaModuleName = (String)moduleContext.get("java_module_name");
            File testJavaFile = new File(testSrcDir, modulePackage.replace('.', '/') + "/test/" + javaModuleName + "ServerTest.java");
            fillTemplate(moduleContext, "module_test_java_client", testJavaFile.toPath());
            break;
		case "python":
            templateFiles.put("module_test_python_client", Paths.get(moduleDir, "test", this.moduleName + "_server_test.py"));
            templateFiles.put("module_tox", Paths.get(moduleDir, "tox.ini"));
            break;
        case "perl":
            templateFiles.put("module_test_perl_client", Paths.get(moduleDir, "test", this.moduleName + "_server_test.pl"));
            break;
        case "r":
            templateFiles.put("module_test_r_client", Paths.get(moduleDir, "test", this.moduleName + "_server_test.r"));
            break;
		}
		
		if (example) {
            if (this.language.equals("python") || this.language.equals("perl") || this.language.equals("java")) {
                templateFiles.put("module_method_spec_json", Paths.get(moduleDir, "ui", "narrative", "methods", "filter_contigs", "spec.json"));
                templateFiles.put("module_method_spec_yaml", Paths.get(moduleDir, "ui", "narrative", "methods", "filter_contigs", "display.yaml"));
            } else {
                templateFiles.put("module_method_spec_json", Paths.get(moduleDir, "ui", "narrative", "methods", "count_contigs_in_set", "spec.json"));
                templateFiles.put("module_method_spec_yaml", Paths.get(moduleDir, "ui", "narrative", "methods", "count_contigs_in_set", "display.yaml"));
            }
		} else {
            templateFiles.put("module_method_spec_json", Paths.get(moduleDir, "ui", "narrative", "methods", "run_"+this.moduleName, "spec.json"));
            templateFiles.put("module_method_spec_yaml", Paths.get(moduleDir, "ui", "narrative", "methods", "run_"+this.moduleName, "display.yaml"));
        }

        switch(this.language) {
            // Perl just needs an impl file and a start server script
            case "perl":
                //templateFiles.put("module_start_perl_server", Paths.get(moduleDir, "scripts", "start_server.sh"));
                // start_server script is now made in Makefile
                templateFiles.put("module_perl_impl", Paths.get(moduleDir, "lib", this.moduleName, this.moduleName + "Impl.pm"));
                break;
            // Python needs some empty __init__.py files and the impl file (Done, see TemplateBasedGenerator.initPyhtonPackages)
            case "python":
                initDirectory(Paths.get(moduleDir, "lib", this.moduleName), false);
                initFile(Paths.get(moduleDir, "lib", this.moduleName, "__init__.py"), false);
                templateFiles.put("module_python_impl", Paths.get(moduleDir, "lib", this.moduleName, this.moduleName + "Impl.py"));
                //templateFiles.put("module_start_python_server", Paths.get(moduleDir, "scripts", "start_server.sh"));
                // start_server script is now made in Makefile
                break;
            case "r":
                templateFiles.put("module_r_impl", Paths.get(moduleDir, "lib", this.moduleName, this.moduleName + "Impl.r"));
                break;
            case "java":
                File srcDir = new File(moduleDir, "lib/src");
                String modulePackage = (String)moduleContext.get("java_package");
                String javaModuleName = (String)moduleContext.get("java_module_name");
                String javaPackageParent = (String)moduleContext.get("java_package_parent");
                File serverJavaFile = new File(srcDir, modulePackage.replace('.', '/') + "/" + javaModuleName + "Server.java");
                fillTemplate(moduleContext, "module_java_impl", serverJavaFile.toPath());
                JavaTypeGenerator.processSpec(new File(moduleDir, specFile), srcDir, javaPackageParent, true, null, null, null);
                //templateFiles.put("module_start_java_server", Paths.get(moduleDir, "scripts", "start_server.sh"));
                // start_server script is now made in Makefile
                break;
            default:
                break;
        }

		for (String templateName : templateFiles.keySet()) {
			fillTemplate(moduleContext, templateName, templateFiles.get(templateName));
		}
		
		if (example) {
			// Generated examples require some other SDK dependencies
            new ClientInstaller(new File(moduleDir), false).install(
                    this.language,
                    false, // async clients
                    true, // core or sync clients
                    false, // dynamic client
                    null, //tagVer
                    this.verbose,
                    "AssemblyUtil",
                    null,
                    null // clientName
            );
		}
		// Let's install fresh workspace client in any cases (we need it at least in tests):
		new ClientInstaller(new File(moduleDir), false).install(
                this.language,
                false, // async clients
                true, // core or sync clients
                false, // dynamic client
                null, //tagVer
                this.verbose,
                "https://raw.githubusercontent.com/kbase/workspace_deluxe/master/workspace.spec",
                null,
                null // clientName
            );
        new ClientInstaller(new File(moduleDir), false).install(
                this.language,
                false, // async clients
                true, // core or sync clients
                false, // dynamic client
                null, //tagVer
                this.verbose,
                "KBaseReport",
                null,
                null // clientName
        );

		System.out.println("Done! Your module is available in the " + moduleDir + " directory.");
		if (example) {
			System.out.println("Compile and run the example methods with the following inputs:");
			System.out.println("  cd " + moduleDir);
			System.out.println("  make          (required after making changes to " + new File(specFile).getName() + ")");
			System.out.println("  kb-sdk test   (will require setting test user account credentials in test_local/test.cfg)");
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
		TemplateFormatter.formatTemplate(templateName, context, outfilePath.toFile());
	}
	
	/**
	 * Takes a language string and returns a "qualified" form. E.g. "perl", "Perl", "pl", ".pl", should all 
	 * return "perl", "Python", "python", ".py", and "py" should all return Python, etc.
	 * 
	 * Right now, we support Perl, Python and Java for implementation languages
	 * @param language
	 * @return
	 */
	public static String qualifyLanguage(String language) {
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
		
		String[] rNames = {"r", ".r"};
		if (Arrays.asList(rNames).contains(lang)) {
			System.out.println(
					"************************************************************************\n" +
					"WARNING: R support is deprecated and will be removed in a future release\n" +
					"************************************************************************");
			return "r";
		}
		
		// If we get here, then we don't recognize it! throw a runtime exception
		throw new RuntimeException("Unrecognized language: " + language + "\n\tWe currently only support Python, Perl, and Java.");
	}
}