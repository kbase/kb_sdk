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
	private KbAnnotationSearch searchableWsSubset = null;
	private KbAnnotationMetadata wsMetadata = null;
	private KbAnnotationRange range = null;
	private Map<String, Object> unknown = new HashMap<String, Object>();
	
	@SuppressWarnings("unchecked")
	KbAnnotations loadFromMap(Map<?,?> data) throws KidlParseException {
		for (Map.Entry<?, ?> enrty : data.entrySet()) {
			String key = enrty.getKey().toString();
			if (key.equals("optional")) {
				optional = (List<String>)enrty.getValue();
			} else if (key.equals("id")) {
				idReference = new KbAnnotationId();
				idReference.loadFromMap((Map<String, Object>)enrty.getValue());
			} else if (key.equals("searchable_ws_subset")) {
				searchableWsSubset = new KbAnnotationSearch();
				searchableWsSubset.loadFromMap((Map<String, Object>)enrty.getValue());
			} else if (key.equals("metadata")) {
				wsMetadata = new KbAnnotationMetadata();
				wsMetadata.loadFromMap((Map<String, Object>)enrty.getValue());
			}else if (key.equals("range")) {
				range = new KbAnnotationRange();
				range.loadFromMap((Map<String, Object>)enrty.getValue());
			} else if (key.equals("unknown_annotations")) {
				unknown.putAll((Map<String, Object>)enrty.getValue());
			} else {
				//throw new KidlParseException("Unknown type of annotation: " + key);
				if (!unknown.containsKey(key))
					unknown.put(key, enrty.getValue());
			}
		}
		if (optional != null)
			optional = Collections.unmodifiableList(optional);
		unknown = Collections.unmodifiableMap(unknown);
		return this;
	}
	
	public KbAnnotations loadFromComment(String comment, KbTypedef caller) throws KidlParseException {
		List<List<String>> lines = new ArrayList<List<String>>();
		StringTokenizer st = new StringTokenizer(comment, "\r\n");
		while (st.hasMoreTokens()) {
			String line = st.nextToken();
			StringTokenizer st2 = new StringTokenizer(line, " \t");
			List<String> words = new ArrayList<String>();
			while (st2.hasMoreTokens())
				words.add(st2.nextToken());
			lines.add(words);
		}
		for (int pos = 0; pos < lines.size(); pos++) {
			if (lines.get(pos).size() == 0)
				continue;
			List<String> words = lines.get(pos);
			String annType = words.get(0);
			if (!annType.startsWith("@"))
				continue;
			annType = annType.substring(1);
			List<String> value = words.subList(1, words.size());
			if (annType.equals("optional")) {
				if (optional == null)
					optional = new ArrayList<String>();
				optional.addAll(value);
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
	
	public Map<String, Object> getUnknown() {
		return unknown;
	}
	
	public Object toJson(boolean isTypedef) {
		Map<String, Object> ret = new TreeMap<String, Object>();
		if (optional != null)
			ret.put("optional", new ArrayList<String>(optional));
		if (idReference != null)
			ret.put("id", idReference.toJson());
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
}
