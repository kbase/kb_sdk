package us.kbase.jkidl;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

import us.kbase.kidl.KbModule;
import us.kbase.kidl.KidlParseException;

/**
 * File implementation of {@link IncludeProvider}.
 */
public class FileIncludeProvider implements IncludeProvider {
	private File dir;
	
	public FileIncludeProvider(File dir) {
		this.dir = dir;
	}
	
	@Override
	public Map<String, KbModule> parseInclude(String includeLine) throws KidlParseException {
		String specPath = includeLine.trim();
		if (specPath.startsWith("#include"))
			specPath = specPath.substring(8).trim();
		if (specPath.startsWith("<"))
			specPath = specPath.substring(1).trim();
		if (specPath.endsWith(">"))
			specPath = specPath.substring(0, specPath.length() - 1).trim();
		File specFile = new File(specPath);
		if (!specFile.isAbsolute())
			specFile = new File(dir, specPath);
		if (!specFile.exists())
			throw new KidlParseException("Can not find included spec-file: " + specFile.getAbsolutePath());
		try {
	        SpecParser p = new SpecParser(new DataInputStream(new FileInputStream(specFile)));
			return p.SpecStatement(this);
		} catch (FileNotFoundException e) {
			throw new IllegalStateException("Unexpected error", e);
		} catch (ParseException e) {
			throw new KidlParseException("Error parsing spec-file [" + specFile.getAbsolutePath() + "]: " + e.getMessage());
		}
	}
}
