package us.kbase.jkidl;

import java.util.Map;

import us.kbase.kidl.KbModule;
import us.kbase.kidl.KidlParseException;

/**
 * Class represents abstract provider that can find spec-documents referred to in includes.
 */
public interface IncludeProvider {
	public Map<String, KbModule> parseInclude(String includeLine) throws KidlParseException;
}
