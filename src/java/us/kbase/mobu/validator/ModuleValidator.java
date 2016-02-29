package us.kbase.mobu.validator;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import us.kbase.kidl.KbFuncdef;
import us.kbase.kidl.KbList;
import us.kbase.kidl.KbModule;
import us.kbase.kidl.KbModuleComp;
import us.kbase.kidl.KbScalar;
import us.kbase.kidl.KbService;
import us.kbase.kidl.KbStruct;
import us.kbase.kidl.KbStructItem;
import us.kbase.kidl.KbTuple;
import us.kbase.kidl.KbType;
import us.kbase.kidl.KbTypedef;
import us.kbase.kidl.KidlParser;
import us.kbase.mobu.util.TextUtils;
import us.kbase.narrativemethodstore.NarrativeMethodStoreClient;
import us.kbase.narrativemethodstore.ValidateMethodParams;
import us.kbase.narrativemethodstore.ValidationResults;


public class ModuleValidator {
	
	
	private static final String KBASE_YML_FILE = "kbase.yml";
	
	
	protected List<String> modulePaths;
	protected boolean verbose;
	protected String methodStoreUrl;
	protected boolean allowSyncMethods;
	
	public ModuleValidator(List<String> modulePaths, boolean verbose, String methodStoreUrl,
	        boolean allowSyncMethods) {
		this.modulePaths = modulePaths;
		this.verbose = verbose;
		this.methodStoreUrl = methodStoreUrl;
		this.allowSyncMethods = allowSyncMethods;
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
			
			KbModule parsedKidl = null;
            try {
                Map<String,Object> config = parseKBaseYaml(new File(module, KBASE_YML_FILE));
                String moduleName = (String)config.get("module-name");
                if (moduleName == null)
                    throw new IllegalStateException("\"module-name\" key isn't found in " + KBASE_YML_FILE);
                File specFile = new File(module, moduleName + ".spec");
                if (!specFile.exists())
                    throw new IllegalStateException("Spec-file isn't found: " + specFile);
                List<KbService> services = KidlParser.parseSpec(KidlParser.parseSpecInt(specFile, null));
                if (services.size() != 1)
                    throw new IllegalStateException("Unexpected number of services found: " + services.size());
                KbService srv = services.get(0);
                if (srv.getModules().size() != 1)
                    throw new IllegalStateException("Unexpected number of modules found: " + srv.getModules().size());
                parsedKidl = srv.getModules().get(0);
            } catch (Exception e) {
                System.err.println("  **ERROR** - KIDL-spec validation failed:");
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
			                int status = validateMethodSpec(methodDir, parsedKidl, this.allowSyncMethods);
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
			parseKBaseYaml(kbaseYmlFile);
			if(verbose) System.out.println("  - configuration file is valid YAML");
		} catch(Exception e) {
			System.err.println("  **ERROR** - "+KBASE_YML_FILE+" configuration file location is invalid:");
			System.err.println("                "+e.getMessage());
			return 1;
			
		}
		
		
		
		return 0;
	}

    @SuppressWarnings("unchecked")
    public Map<String,Object> parseKBaseYaml(File kbaseYmlFile) throws IOException {
        Yaml yaml = new Yaml();
        String kbaseYml = TextUtils.readFileText(kbaseYmlFile);
        return (Map<String, Object>) yaml.load(kbaseYml);
    }
	
	
	protected int validateMethodSpec(File methodDir, KbModule parsedKidl,
	        boolean allowSyncMethods) throws IOException {
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
                validateMethodSpecMapping(spec, parsedKidl, allowSyncMethods);
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

	protected void validateMethodSpecMapping(String specText, KbModule parsedKidl,
	        boolean allowSyncMethods) throws IOException {
	    JsonNode spec = new ObjectMapper().readTree(specText);
        if (!allowSyncMethods) {
            String jobId = get("/", spec, "job_id_output_field").asText();
            if (!jobId.equals("docker")) {
                throw new IllegalStateException("  **ERROR** - can't find \"docker\" value within path " +
                        "[job_id_output_field] in spec.json");
            }
        } else if (spec.get("job_id_output_field") == null ||
                !spec.get("job_id_output_field").asText().equals("docker")) {
            System.err.println("  **WARNINGS** - method is declared as synchronous and " +
                    "will be skipped");
            return;
        }
        JsonNode parametersNode = get("/", spec, "parameters");
        Map<String, JsonNode> inputParamIdToType = new TreeMap<String, JsonNode>();
        for (int i = 0; i < parametersNode.size(); i++) {
            JsonNode paramNode = parametersNode.get(i);
            String paramPath = "parameters/" + i;
            String paramId = get(paramPath, paramNode, "id").asText();
            inputParamIdToType.put(paramId, paramNode);
        }
        JsonNode behaviorNode = get("/", spec, "behavior");
        JsonNode serviceMappingNode = get("behavior", behaviorNode, "service-mapping");
        String moduleName = get("behavior/service-mapping", serviceMappingNode, "name").asText();
        String methodName = get("behavior/service-mapping", serviceMappingNode, "method").asText();
        if (methodName.contains(".")) {
            String[] parts = methodName.split(Pattern.quote("."));
            moduleName = parts[0];
            methodName = parts[1];
        }
        KbFuncdef func = null;
        for (KbModuleComp mc : parsedKidl.getModuleComponents()) {
            if (mc instanceof KbFuncdef) {
                KbFuncdef f = (KbFuncdef)mc;
                if (f.getName().equals(methodName)) {
                    func = f;
                    break;
                }
            }
        }
        if (func == null) {
            throw new IllegalStateException("  **ERROR** - unknown method \"" + 
                    methodName + "\" defined within path " +
                    "[behavior/service-mapping/method] in spec.json");
        }
        if (!parsedKidl.getModuleName().equals(moduleName)) {
            throw new IllegalStateException("  **ERROR** - value doesn't match " +
                    "\"" + parsedKidl.getModuleName() + "\" within path " +
                    "[behavior/service-mapping/name] in spec.json");
        }
        String serviceUrl = get("behavior/service-mapping", serviceMappingNode, "url").asText();
        if (serviceUrl.length() > 0) {
            throw new IllegalStateException("  **ERROR** - async method has non-empty value within path " +
                    "[behavior/service-mapping/url] in spec.json");
        }
        JsonNode paramsMappingNode = get("behavior/service-mapping", serviceMappingNode, "input_mapping");
        Set<String> paramsUsed = new TreeSet<String>();
        Set<Integer> argsUsed = new TreeSet<Integer>();
        for (int j = 0; j < paramsMappingNode.size(); j++) {
            JsonNode paramMappingNode = paramsMappingNode.get(j);
            String path = "behavior/service-mapping/input_mapping/" + j;
            JsonNode targetArgPosNode = paramMappingNode.get("target_argument_position");
            int targetArgPos = 0;
            if (targetArgPosNode != null && !targetArgPosNode.isNull())
                targetArgPos = targetArgPosNode.asInt();
            if (targetArgPos >= func.getParameters().size()) {
                throw new IllegalStateException("  **ERROR** - value " + targetArgPos + " within " +
                		"path [" + path + "/target_argument_position] in spec.json is out of " +
                        "bounds (number of arguments defined for function \"" + methodName + "\" " + 
                		"is " + func.getParameters().size() + ")");
            }
            argsUsed.add(targetArgPos);
            KbType argType = func.getParameters().get(targetArgPos).getType();
            while (argType instanceof KbTypedef) {
                KbTypedef ref = (KbTypedef)argType;
                argType = ref.getAliasType();
            }
            JsonNode targetPropNode = paramMappingNode.get("target_property");
            if (targetPropNode != null && !targetPropNode.isNull()) {
                String targetProp = targetPropNode.asText();
                if (argType instanceof KbScalar || argType instanceof KbList ||
                        argType instanceof KbTuple) {
                    throw new IllegalStateException("  **ERROR** - value " + targetProp + " within " +
                            "path [" + path + "/target_property] in spec.json can't be applied to " +
                            "type " + argType.getClass().getSimpleName() + " (defined for argument " + 
                            targetArgPos + ")");
                }
                if (argType instanceof KbStruct) {
                    KbStruct struct = (KbStruct)argType;
                    boolean found = false;
                    for (KbStructItem item : struct.getItems()) {
                        if (item.getName() != null && item.getName().equals(targetProp)) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        System.err.println("  **WARNINGS** - value \"" + targetProp + "\" within " +
                        		"path [" + path + "/target_property] in spec.json doesn't match " +
                        		"any field of structure defined as argument type" + 
                                (struct.getName() != null ? (" (" + struct.getName() + ")") : ""));
                    }
                }
            }
            JsonNode inputParamObj = paramMappingNode.get("input_parameter");
            if (inputParamObj != null && !inputParamObj.isNull()) {
                String inputParamId = inputParamObj.asText();
                if (!inputParamIdToType.containsKey(inputParamId)) {
                    throw new IllegalStateException("  **ERROR** - value \"" + inputParamId + "\" " +
                            "within path [" + path + "/input_parameter] in spec.json is not any " +
                            "input ID listed in \"parameters\" block");
                }
                paramsUsed.add(inputParamId);
            }
        }
        if (func.getParameters().size() != argsUsed.size()) {
            throw new IllegalStateException("  **ERROR** - not all arguments are set for function " +
            		"\"" + func.getName() + "\", list of defined arguments is: " + argsUsed);
        }
        if (inputParamIdToType.size() != paramsUsed.size()) {
            Set<String> paramsNotUsed = new TreeSet<String>(inputParamIdToType.keySet());
            paramsNotUsed.removeAll(paramsUsed);
            System.err.println("  **WARNINGS** - some of input parameters are not used: " + 
                    paramsNotUsed);
        }
	}
	
	private static JsonNode get(String nodePath, JsonNode node, String childName) {
	    JsonNode ret = node.get(childName);
	    if (ret == null)
	        throw new IllegalStateException("  **ERROR** - can't find sub-node [" + childName + 
	                "] within path [" + nodePath + "] in spec.json");
	    return ret;
	}

}
