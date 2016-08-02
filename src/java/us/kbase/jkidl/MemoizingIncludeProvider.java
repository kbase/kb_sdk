package us.kbase.jkidl;

import java.util.HashMap;
import java.util.Map;

import us.kbase.kidl.KbModule;
import us.kbase.kidl.KidlParseException;

/** Provides parsed structures for included KIDL specs and memoizes the
 * structures.
 * @author gaprice@lbl.gov
 *
 */
public class MemoizingIncludeProvider implements IncludeProvider {

	private final IncludeProvider wrapped;
	private final Map<String, KbModule> seen = new HashMap<String, KbModule>();
	
	/** Construct the memoizing provider
	 * @param wrapped an IncludeProvider for which the results will be
	 * memoized.
	 */
	public MemoizingIncludeProvider(final IncludeProvider wrapped) {
		this.wrapped = wrapped;
	}
	
	@Override
	public Map<String, KbModule> parseInclude(String includeLine)
			throws KidlParseException {
		final Map<String, KbModule> parse = wrapped.parseInclude(includeLine);
		seen.putAll(parse);
		return parse;
	}
	
	/** Note that the Kb* classes are mutable and this function does not make
	 * a deep copy of the Map.
	 * @return A mapping of the service name (including the module name) to a
	 * module for each service encountered in an import. 
	 */
	public Map<String, KbModule> getParsed() {
		return new HashMap<String, KbModule>(seen);
	}

}
