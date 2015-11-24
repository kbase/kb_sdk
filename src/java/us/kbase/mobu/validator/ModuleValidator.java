package us.kbase.mobu.validator;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.Yaml;

import us.kbase.mobu.util.TextUtils;
import us.kbase.narrativemethodstore.NarrativeMethodStoreClient;
import us.kbase.narrativemethodstore.ValidateMethodParams;
import us.kbase.narrativemethodstore.ValidationResults;


public class ModuleValidator {
	
	
	private static final String KBASE_YML_FILE = "kbase.yml";
	
	
	protected List<String> modulePaths;
	protected boolean verbose;
	protected String methodStoreUrl;
	
	public ModuleValidator(List<String> modulePaths, boolean verbose, String methodStoreUrl) {
		this.modulePaths = modulePaths;
		this.verbose = verbose;
		this.methodStoreUrl = methodStoreUrl;
	}
	
    private static boolean isModuleDir(File dir) {
        return  new File(dir, "Dockerfile").exists() &&
                new File(dir, "Makefile").exists() &&
                new File(dir, "kbase.yml").exists() &&
                new File(dir, "lib").exists() &&
                new File(dir, "scripts").exists() &&
                new File(dir, "test").exists() &&
                new File(dir, "ui").exists();
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
	            File dir = module.getCanonicalFile();
	            while (!isModuleDir(dir)) {
	                dir = dir.getParentFile();
	                if (dir == null)
	                    throw new IllegalStateException("  **ERROR** - cannot find folder with module structure");
	            }
	            module = dir;
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
			
			File uiNarrativeMethodsDir = new File(new File(new File(module, "ui"), "narrative"), "methods");
			if (uiNarrativeMethodsDir.exists()) {
			    for (File methodDir : uiNarrativeMethodsDir.listFiles()) {
			        if (methodDir.isDirectory()) {
			            System.out.println("\nValidating method in ("+methodDir+")");
			            try {
			                int status = validateMethodSpec(methodDir);
			                if (status != 0) {
			                    errors++; 
			                    continue;
			                }
			            } catch (Exception e) {
			                System.err.println("  **ERROR** - method-spec validation failed:");
			                System.err.println("                "+e.getMessage());
			                errors++; continue;
			            }
			        }
			    }
			}
			
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
	
	
	protected int validateMethodSpec(File methodDir) throws IOException {
	    NarrativeMethodStoreClient nms = new NarrativeMethodStoreClient(new URL(methodStoreUrl));
	    nms.setAllSSLCertificatesTrusted(true);
	    nms.setIsInsecureHttpConnectionAllowed(true);
	    String spec = FileUtils.readFileToString(new File(methodDir, "spec.json"));
        String display = FileUtils.readFileToString(new File(methodDir, "display.yaml"));
        Map<String, String> extraFiles = new LinkedHashMap<String, String>();
        for (File f : methodDir.listFiles()) {
            if (f.isFile() && f.getName().endsWith(".html"))
                extraFiles.put(f.getName(), FileUtils.readFileToString(f));
        }
        try {
            ValidationResults vr = nms.validateMethod(new ValidateMethodParams().withId(
                    methodDir.getName()).withSpecJson(spec).withDisplayYaml(display)
                    .withExtraFiles(extraFiles));
            if (vr.getIsValid() == 1L) {
                if (vr.getWarnings().size() > 0) {
                    System.err.println("  **WARNINGS** - method-spec validation:");
                    for (int num = 0; num < vr.getWarnings().size(); num++) {
                        String warn = vr.getWarnings().get(num);
                        System.err.println("                (" + (num + 1) + ") " + warn);
                    }
                }
                return 0;
            }
            System.err.println("  **ERROR** - method-spec validation failed:");
            for (int num = 0; num < vr.getErrors().size(); num++) {
                String error = vr.getErrors().get(num);
                System.err.println("                (" + (num + 1) + ") " + error);
            }
            return  1;
        } catch (Exception e) {
            System.err.println("  **ERROR** - method-spec validation failed:");
            System.err.println("                "+e.getMessage());
            return 1;
        }
	}
	
}
