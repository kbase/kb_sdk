package us.kbase.kidl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Class represents kind of comment annotation called 'id'.
 */
public class KbAnnotationId {
	private String type;
	private List<String> attributes;
	
	public static final String TYPE_WS = "ws";
	public static final String TYPE_KB = "kb";
	public static final String TYPE_EXTERNAL = "external";
	public static final String TYPE_SHOCK = "shock";

	void loadFromComment(List<String> words) throws KidlParseException {
		if (words.size() == 0)
			throw new KidlParseException("Id annotations without type are not supported");
		type = words.get(0);
		words = words.subList(1, words.size());
		
		// this is where we could perform checks on the input type names
		//if(type.equals(TYPE_WS)) {
		//}
		
		attributes = words;
	}
	
	void loadFromMap(Map<String,Object> data) throws KidlParseException {
		type = Utils.prop(data, "type");
		if(data.containsKey("attributes")) {
			attributes = Collections.unmodifiableList(Utils.repareTypingString(
				Utils.propList(data, "attributes")));
		} else if (data.containsKey("valid_typedef_names")) {
			attributes = Collections.unmodifiableList(Utils.repareTypingString(
					Utils.propList(data, "valid_typedef_names")));
		} else if (data.containsKey("sources")) {
			attributes = Collections.unmodifiableList(Utils.repareTypingString(
					Utils.propList(data, "sources")));
		} else {
			attributes = new ArrayList<String>();
		}
	}
	
	public String getType() {
		return type;
	}
	
	public List<String> getAttributes() {
		return attributes;
	}
	
	Object toJson() {
		Map<String, Object> ret = new TreeMap<String, Object>();
		ret.put("type", type);
		ret.put("attributes", attributes);
		return ret;
	}

	Object toJsonSchema() {
		Map<String, Object> idMap = new TreeMap<String, Object>();
		idMap.put("id-type", type);
		idMap.put("attributes",attributes);
		return idMap;
	}
}
