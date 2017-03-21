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

	public static List<KbService> parseSpec(File specFile) throws KidlParseException {
		return parseSpec(specFile, null);
	}

	public static List<KbService> parseSpec(File specFile,
			Map<String, Map<String, String>> modelToTypeJsonSchemaReturn) throws KidlParseException {
		Map<?,?> map = null;
		try {
		    map = parseSpecInt(specFile, modelToTypeJsonSchemaReturn);
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
			modList.add(module.accept(new JSONableVisitor()));
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
}
