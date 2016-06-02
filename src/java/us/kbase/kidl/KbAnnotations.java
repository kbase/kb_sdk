package us.kbase.kidl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

/**
 * Class represents comment annotations started with symbol @.
 */
public class KbAnnotations {
	private List<String> optional = null;
	private KbAnnotationId idReference = null;
	//TODO ROMAN can this be removed?
	private KbAnnotationSearch searchableWsSubset = null;
	private KbAnnotationMetadata wsMetadata = null;
	private KbAnnotationRange range = null;
	private boolean isDeprecated = false;
	private String deprecationReplacement = null;
	private Map<String, Object> unknown = new HashMap<String, Object>();
	
	/* Implementation notes:
	 * loadFromMap has two uses:
	 * 1) loading from XML produced by the old Perl type compiler (obsolete)
	 * 2) loading from maps produced by toJson in this method.
	 * Thus, toJson must produce output that loadFromMap can consume.
	 * 
	 * This class does not have toJsonSchema because only KbType and specific
	 * annotation classes need toJsonSchema.
	 */
	
	@SuppressWarnings("unchecked")
	KbAnnotations loadFromMap(Map<?,?> data) throws KidlParseException {
		for (final Map.Entry<?, ?> entry : data.entrySet()) {
			final String key = entry.getKey().toString();
			if (key.equals("optional")) {
				optional = (List<String>)entry.getValue();
			} else if (key.equals("deprecated")) {
				isDeprecated = true;
				final String repl = (String) entry.getValue();
				//is there any other checking we should do here?
				if (repl != null && !repl.isEmpty()) {
					deprecationReplacement = repl;
				}
			} else if (key.equals("id")) {
				idReference = new KbAnnotationId();
				idReference.loadFromMap((Map<String, Object>)entry.getValue());
			} else if (key.equals("searchable_ws_subset")) {
				searchableWsSubset = new KbAnnotationSearch();
				searchableWsSubset.loadFromMap((Map<String, Object>)entry.getValue());
			} else if (key.equals("metadata")) {
				wsMetadata = new KbAnnotationMetadata();
				wsMetadata.loadFromMap((Map<String, Object>)entry.getValue());
			}else if (key.equals("range")) {
				range = new KbAnnotationRange();
				range.loadFromMap((Map<String, Object>)entry.getValue());
			} else if (key.equals("unknown_annotations")) {
				unknown.putAll((Map<String, Object>)entry.getValue());
			} else {
				//throw new KidlParseException("Unknown type of annotation: " + key);
				if (!unknown.containsKey(key))
					unknown.put(key, entry.getValue());
			}
		}
		if (optional != null)
			optional = Collections.unmodifiableList(optional);
		unknown = Collections.unmodifiableMap(unknown);
		return this;
	}
	
	public KbAnnotations loadFromComment(
			final String comment, final KbFuncdef caller)
			throws KidlParseException {
		final List<List<String>> lines = tokenize(comment);
		for (final List<String> words: lines) {
			final String annType = getAnnotationType(words);
			if (annType == null) {
				continue;
			}
			final List<String> value = words.subList(1, words.size());
			if (annType.equals("deprecated")) {
				setDeprecated(value);
			} else {
				// TODO: Probably we should throw an exception here
				unknown.put(annType, value);
			}
		}
		return this;
	}
	
	
	public KbAnnotations loadFromComment(
			final String comment, final KbTypedef caller)
			throws KidlParseException {
		final List<List<String>> lines = tokenize(comment);
		for (final List<String> words: lines) {
			final String annType = getAnnotationType(words);
			if (annType == null) {
				continue;
			}
			final List<String> value = words.subList(1, words.size());
			if (annType.equals("optional")) {
				if (optional == null)
					optional = new ArrayList<String>();
				optional.addAll(value);
			} else if (annType.equals("deprecated")) {
				setDeprecated(value);
			} else if (annType.equals("id")) {
				idReference = new KbAnnotationId();
				idReference.loadFromComment(value);
			} else if (annType.equals("searchable")) {
				if (searchableWsSubset == null)
					searchableWsSubset = new KbAnnotationSearch();
				searchableWsSubset.loadFromComment(value, caller);
			} else if (annType.equals("metadata")) {
				if (wsMetadata == null)
					wsMetadata = new KbAnnotationMetadata();
				wsMetadata.loadFromComment(value,caller);
			} else if(annType.equals("range")) {
				range = new KbAnnotationRange();
				range.loadFromComment(value);
			} else {
				// TODO: Probably we should throw an exception here
				unknown.put(annType, value);
			}
		}
		return this;
	}

	private void setDeprecated(final List<String> value)
			throws KidlParseException {
		isDeprecated = true;
		if (value.size() > 1) {
			throw new KidlParseException(
					"deprecation annotations may have at most one argument");
		}
		final String repl = value.isEmpty() ? null : value.get(0);
		if (repl != null && !repl.isEmpty()) {
			deprecationReplacement = repl;
		}
	}
	
	private String getAnnotationType(final List<String> words) {
		//this can't actually happen given the tokenizer setup
		if (words.size() == 0) {
			return null;
		}
		final String annType = words.get(0);
		if (!annType.startsWith("@")) {
			return null;
		}
		return annType.substring(1);
	}

	private List<List<String>> tokenize(final String comment) {
		final List<List<String>> lines = new ArrayList<List<String>>();
		final StringTokenizer st = new StringTokenizer(comment, "\r\n");
		while (st.hasMoreTokens()) {
			final String line = st.nextToken();
			final StringTokenizer st2 = new StringTokenizer(line, " \t");
			final List<String> words = new ArrayList<String>();
			while (st2.hasMoreTokens())
				words.add(st2.nextToken());
			lines.add(words);
		}
		return lines;
	}
	
	public List<String> getOptional() {
		return optional;
	}
	
	public KbAnnotationId getIdReference() {
		return idReference;
	}
	
	public KbAnnotationSearch getSearchable() {
		return searchableWsSubset;
	}
	
	public KbAnnotationMetadata getWsMetadata() {
		return wsMetadata;
	}
	
	public KbAnnotationRange getRange() {
		return range;
	}
	
	public boolean isDeprecated() {
		return isDeprecated;
	}
	
	public String getDeprecationReplacement() {
		return deprecationReplacement;
	}
	
	public Map<String, Object> getUnknown() {
		return unknown;
	}
	
	public Object toJson(boolean isTypedef) {
		Map<String, Object> ret = new TreeMap<String, Object>();
		if (optional != null) {
			ret.put("optional", new ArrayList<String>(optional));
		}
		if (isDeprecated) {
			ret.put("deprecated", deprecationReplacement);
		}
		if (idReference != null) {
			ret.put("id", idReference.toJson());
		}
		if (isTypedef) {
			Object searchableJson = searchableWsSubset == null ?
					new HashMap<String, Object>() : searchableWsSubset.toJson();
			ret.put("searchable_ws_subset", searchableJson);
			Object metadataJson = wsMetadata == null ?
					new HashMap<String, Object>() : wsMetadata.toJson();
			ret.put("metadata", metadataJson);
		}
		if(range!=null) {
			ret.put("range", range.toJson());
		}
		
		ret.put("unknown_annotations", unknown);
		return ret;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("KbAnnotations [optional=");
		builder.append(optional);
		builder.append(", idReference=");
		builder.append(idReference);
		builder.append(", searchableWsSubset=");
		builder.append(searchableWsSubset);
		builder.append(", wsMetadata=");
		builder.append(wsMetadata);
		builder.append(", range=");
		builder.append(range);
		builder.append(", isDeprecated=");
		builder.append(isDeprecated);
		builder.append(", deprecationReplacement=");
		builder.append(deprecationReplacement);
		builder.append(", unknown=");
		builder.append(unknown);
		builder.append("]");
		return builder.toString();
	}
}
