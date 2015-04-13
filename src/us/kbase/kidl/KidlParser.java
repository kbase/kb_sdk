package us.kbase.kidl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import us.kbase.jkidl.FileIncludeProvider;
import us.kbase.jkidl.IncludeProvider;
import us.kbase.jkidl.ParseException;
import us.kbase.jkidl.SpecParser;

/**
 * Wrapper for parser of spec-file.
 */
public class KidlParser {

	public static List<KbService> parseSpec(File specFile, File tempDir) throws KidlParseException {
		return parseSpec(specFile, tempDir, null);
	}

	public static List<KbService> parseSpec(File specFile, File tempDir, 
			Map<String, Map<String, String>> modelToTypeJsonSchemaReturn) throws KidlParseException {
		return parseSpec(specFile, tempDir, modelToTypeJsonSchemaReturn, null);
	}

	public static List<KbService> parseSpec(File specFile, File tempDir, 
			Map<String, Map<String, String>> modelToTypeJsonSchemaReturn, File typecompDir) throws KidlParseException {
		return parseSpec(specFile, tempDir, modelToTypeJsonSchemaReturn, typecompDir, true);
	}
	
	public static List<KbService> parseSpec(File specFile, File tempDir, 
			Map<String, Map<String, String>> modelToTypeJsonSchemaReturn, File typecompDir, boolean internal) throws KidlParseException {
		Map<?,?> map = null;
		try {
			if (internal) {
				map = parseSpecInt(specFile, modelToTypeJsonSchemaReturn);
			} else {
				map = parseSpecExt(specFile, tempDir, modelToTypeJsonSchemaReturn, typecompDir);
			}
			return parseSpec(map);
		} catch (KidlParseException ex) {
			throw ex;
		} catch (Throwable ex) {
			throw new KidlParseException("Error during parsing spec-file: " + ex.getMessage(), ex);
		}
	}
	
	public static List<KbService> parseSpec(Map<?,?> parseMap) throws KidlParseException {
		return KbService.loadFromMap(parseMap);
	}

	public static Map<?,?> parseSpecInt(File specFile,
			Map<String, Map<String, String>> modelToTypeJsonSchemaReturn) throws Exception {
        IncludeProvider ip = new FileIncludeProvider(specFile.getCanonicalFile().getParentFile());
		return parseSpecInt(new FileReader(specFile), modelToTypeJsonSchemaReturn, ip);
	}
	
	public static Map<?,?> parseSpecInt(Reader specDocumentReader, 
			Map<String, Map<String, String>> modelToTypeJsonSchemaReturn, IncludeProvider ip) 
					throws KidlParseException, JsonGenerationException, JsonMappingException, IOException {
        SpecParser p = new SpecParser(new BufferedReader(specDocumentReader));
        Map<String, KbModule> root;
		try {
			root = p.SpecStatement(ip);
			specDocumentReader.close();
		} catch (ParseException e) {
			throw new KidlParseException(e.getMessage(), e);
		}
        Map<String,List<Object>> ret = new LinkedHashMap<String, List<Object>>();
        for (KbModule module : root.values()) {
        	List<Object> modList = ret.get(module.getServiceName());
        	if (modList == null) {
        		modList = new ArrayList<Object>();
        		ret.put(module.getServiceName(), modList);
        	}
        	modList.add(module.toJson());
        }
        if (modelToTypeJsonSchemaReturn != null) {
        	for (Map.Entry<String, KbModule> entry : root.entrySet()) {
        		Map<String, String> typeToSchema = new TreeMap<String, String>();
        		for (KbModuleComp comp : entry.getValue().getModuleComponents())
        			if (comp instanceof KbTypedef) {
        				KbTypedef typedef = (KbTypedef)comp;
        				Object schemaMap = typedef.toJsonSchema(false);
                        ObjectMapper mapper = new ObjectMapper();
                        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
                        StringWriter sw = new StringWriter();
                        mapper.writeValue(sw, schemaMap);
                        sw.close();
        				typeToSchema.put(typedef.getName(), sw.toString());
        			}
        		if (typeToSchema.size() > 0)
        			modelToTypeJsonSchemaReturn.put(entry.getKey(), typeToSchema);
        	}
        }
        return ret;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<?,?> parseSpecExt(File specFile, File tempDir, 
			Map<String, Map<String, String>> modelToTypeJsonSchemaReturn, File typecompDir) 
					throws KidlParseException, IOException, InterruptedException, 
					ParserConfigurationException, SAXException {
		if (tempDir == null)
			tempDir = new File(".");
		File workDir = new File(tempDir, "temp_" + System.currentTimeMillis());
		workDir.mkdir();
		try {
			File bashFile = new File(workDir, "comp_server.sh");
			File specDir = specFile.getAbsoluteFile().getParentFile();
			File xmlFile = new File(workDir, "parsing_file.xml");
			PrintWriter pw = new PrintWriter(bashFile);
			pw.println("#!/bin/bash");
			boolean createJsonSchemas = modelToTypeJsonSchemaReturn != null;
			pw.println("" +
			        "export PERL5LIB=" + typecompDir + "/lib\n" +
					"perl " + typecompDir + "/scripts/compile_typespec.pl --path \"" + specDir.getAbsolutePath() + "\"" +
					" --xmldump " + xmlFile.getName() + " " + (createJsonSchemas ? "--jsonschema " : "") + 
					"\"" + specFile.getAbsolutePath() + "\" " + workDir.getName()
					);
			pw.close();
			Process proc = new ProcessBuilder("bash", bashFile.getCanonicalPath()).directory(tempDir)
					.redirectErrorStream(true).start();
			BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			StringBuilder errTextSB = new StringBuilder();
			while (true) {
				String l = br.readLine();
				if (l == null)
					break;
				System.out.println("KIDL: " + l);
				errTextSB.append(l).append('\n');
			}
			br.close();
			proc.waitFor();
			if (!xmlFile.exists()) {
				String errText = errTextSB.toString();
				String[] options = {"path", "xmldump", "jsonschema"};
				String caption = null;
				for (String opt : options) {
					if (errText.contains("Unknown option: " + opt)) {
						caption = "It seems that you're using wrong branch of \"typecomp\" module (it should be \"dev-prototypes\")";
						break;
					}
				}
				if (caption == null)
					caption = "Parsing file wasn't created";
				throw new KidlParseException(caption + ", here is KIDL output:\n" + errText);
			}
			Map<String,Object> map = SpecXmlHelper.parseXml(xmlFile);
			correctStringAnnotations(map);
			Set<String> moduleNames = new HashSet<String>();
			for (Object obj : map.values()) {
				List<List<Object>> modList = (List<List<Object>>)obj;
				for (List<Object> module : modList) {
					if (module.size() != 3)
						throw new IllegalStateException("Wrong parse structure");
					module.set(1, new ArrayList<Object>());
					Map<String, Object> moduleProps = (Map<String, Object>)module.get(0);
					String moduleName = moduleProps.get("module_name").toString();
					moduleNames.add(moduleName);
					List<Object> components = (List<Object>)moduleProps.get("module_components");
					for (Object cmp : components) {
						if (!(cmp instanceof Map))
							continue;
						Map<String, Object> comp = (Map<String, Object>)cmp;
						if (comp.get("!").equals("Bio::KBase::KIDL::KBT::Typedef")) {
							Map<String, Object> ann = (Map<String, Object>)comp.get("annotations");
							if (ann == null)
								ann = new TreeMap<String, Object>();
							if (ann.get("searchable_ws_subset") == null)
								ann.put("searchable_ws_subset", new TreeMap<String, Object>());
							if (ann.get("unknown_annotations") == null)
								ann.put("unknown_annotations", new TreeMap<String, Object>());
							comp.put("annotations", ann);
						}
					}
				}
			}
			if (createJsonSchemas) {
				File schemasRoot = new File(workDir, "jsonschema");
				if (schemasRoot.exists())
					for (File moduleDir : schemasRoot.listFiles()) {
						if (!moduleDir.isDirectory())
							continue;
						String moduleName = moduleDir.getName();
						if (!moduleNames.contains(moduleName)) {
							continue;
						}
						Map<String, String> type2schema = new TreeMap<String, String>();
						for (File schemaFile : moduleDir.listFiles()) {
							if (!schemaFile.getName().endsWith(".json"))
								continue;
							String typeName = schemaFile.getName();
							typeName = typeName.substring(0, typeName.length() - 5);
							StringWriter sw = new StringWriter();
							PrintWriter schemaPw = new PrintWriter(sw);
							BufferedReader schemaBr = new BufferedReader(new FileReader(schemaFile));
							while (true) {
								String l = schemaBr.readLine();
								if (l == null)
									break;
								schemaPw.println(l);
							}
							schemaBr.close();
							schemaPw.close();
							type2schema.put(typeName, sw.toString());
						}
						modelToTypeJsonSchemaReturn.put(moduleDir.getName(), type2schema);
					}
			}
			return map;
		} finally {
			deleteRecursively(workDir);				
		}
	}	
	
	private static void correctStringAnnotations(Object node) {
		if (node instanceof Map) {
			Map<String, Object> map = (Map<String, Object>)node;
			Object perlType = map.get("!");
			if (perlType != null && perlType instanceof String && 
					perlType.equals("Bio::KBase::KIDL::KBT::Scalar")) {
				String scalarType = (String)map.get("scalar_type");
				if (scalarType.equals("string") && map.get("annotations") == null)
					map.put("annotations", Collections.EMPTY_MAP);
			} else {
				for (Object value : map.values())
					correctStringAnnotations(value);
			}
		} else if (node instanceof List) {
			for (Object item : ((List<?>)node))
				correctStringAnnotations(item);
		}
	}

	private static void deleteRecursively(File fileOrDir) {
		if (fileOrDir.isDirectory())
			for (File f : fileOrDir.listFiles()) 
				deleteRecursively(f);
		fileOrDir.delete();
	}
}
