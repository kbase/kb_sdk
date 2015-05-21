package us.kbase.jkidl;

import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.Map;

import us.kbase.kidl.KbModule;
import us.kbase.kidl.KidlParseException;

/**
 * Memory implementation of {@link IncludeProvider}.
 */
public class StaticIncludeProvider implements IncludeProvider {
	private Map<String, String> moduleNameToSpec = new LinkedHashMap<String, String>();

	public StaticIncludeProvider() {}
	
	public void addSpecFile(String moduleName, String specDocument) {
		moduleNameToSpec.put(moduleName, specDocument);
	}
	
	@Override
	public Map<String, KbModule> parseInclude(String includeLine) throws KidlParseException {
		String moduleName = includeLine.trim();
		if (moduleName.startsWith("#include"))
			moduleName = moduleName.substring(8).trim();
		if (moduleName.startsWith("<"))
			moduleName = moduleName.substring(1).trim();
		if (moduleName.endsWith(">"))
			moduleName = moduleName.substring(0, moduleName.length() - 1).trim();
		if (moduleName.contains("/"))
			moduleName = moduleName.substring(moduleName.lastIndexOf('/') + 1).trim();
		if (moduleName.contains("\\"))
			moduleName = moduleName.substring(moduleName.lastIndexOf('\\') + 1).trim();
		if (moduleName.contains("."))
			moduleName = moduleName.substring(0, moduleName.indexOf('.')).trim();
		String ret = moduleNameToSpec.get(moduleName);
		if (ret == null)
			throw new KidlParseException("Can not find included module [" + moduleName + "] from line: " + includeLine);
        SpecParser p = new SpecParser(new StringReader(ret));
		try {
			return p.SpecStatement(this);
		} catch (ParseException e) {
			throw new KidlParseException("Error parsing spec-document of module [" + moduleName + "]: " + e.getMessage());
		}
	}
}
