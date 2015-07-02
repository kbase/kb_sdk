package us.kbase.mobu.validator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import us.kbase.mobu.util.TextUtils;


public class ModuleValidator {
	
	
	private static final String KBASE_YML_FILE = "kbase.yml";
	
	
	protected List<String> modulePaths;
	protected boolean verbose;
	
	public ModuleValidator(List<String> modulePaths, boolean verbose) {
		this.modulePaths = modulePaths;
		this.verbose = verbose;
	}
	
	public int validateAll() {
		
		int errors = 0;
		
		for(String modulePathString : modulePaths) {
			File module = new File(modulePathString);
			System.out.println("\nValidating module in ("+module+")");
			
			if(!module.exists()) {
				System.err.println("  **ERROR** - the module does not exist");
				errors++; continue;
			}
			if(!module.isDirectory()) {
				System.err.println("  **ERROR** - the module location is not a directory.");
				errors++; continue;
			}
			
			try {
				if(verbose) System.out.println("  - canonical path = "+module.getCanonicalPath()+"");
			} catch (IOException e) {
				System.err.println("  **ERROR** - unable to extract module canonical path:");
				System.err.println("                "+e.getMessage());
			}
			
			// 1) Validate the configuration file
			try {
				int status = validateKBaseYmlConfig(module);
				if(status!=0) {
					errors++; continue;
				}
			} catch (Exception e) {
				System.err.println("  **ERROR** - configuration file validation failed:");
				System.err.println("                "+e.getMessage());
				errors++; continue;
			}
			
			
			// 2) Validate UI components
			
			//     2a) Validate Narrative Methods
			
			
			
			
		}
		
		
		
		
		if(errors>0) {
			if(modulePaths.size()==1) {
				System.out.println("\n\nThis module contains errors.\n");
			} else {
				System.out.println("\n\nErrors detected in "+errors +" of "+modulePaths.size()+" modules.\n");
			}
			return 1;
		}
		if(modulePaths.size()==1) {
			System.out.println("\n\nCongrats- this module is valid.\n");
		} else {
			System.out.println("\n\nCongrats- all "+modulePaths.size()+" modules are valid.\n");
		}
		return 0;
	}
	
	
	
	protected int validateKBaseYmlConfig(File module) throws IOException {
		File kbaseYmlFile = new File(module.getCanonicalPath()+File.separator+KBASE_YML_FILE);
		if(verbose) System.out.println("  - configuration file = "+kbaseYmlFile);
		
		if(!kbaseYmlFile.exists()) {
			System.err.println("  **ERROR** - "+KBASE_YML_FILE+" configuration file does not exist in module directory.");
			return 1;
		}
		if(kbaseYmlFile.isDirectory()) {
			System.err.println("  **ERROR** - "+KBASE_YML_FILE+" configuration file location is a directory, not a file.");
			return 1;
		}
		
		try {
			Yaml yaml = new Yaml();
			String kbaseYml = TextUtils.readFileText(kbaseYmlFile);
			@SuppressWarnings("unchecked")
			Map<String,Object> config = (Map<String, Object>) yaml.load(kbaseYml);
			if(verbose) System.out.println("  - configuration file is valid YAML");
		} catch(Exception e) {
			System.err.println("  **ERROR** - "+KBASE_YML_FILE+" configuration file location is invalid:");
			System.err.println("                "+e.getMessage());
			return 1;
			
		}
		
		
		
		return 0;
	}
	
	
	
	
}
