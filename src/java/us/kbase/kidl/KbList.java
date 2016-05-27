package us.kbase.kidl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Class represents list in spec-file.
 */
public class KbList extends KbBasicType {
	private KbType elementType;
	
	public KbList() {}
	
	public KbList(KbType elementType) {
		this.elementType = elementType;
	}
	
	public KbList loadFromMap(Map<?,?> data) throws KidlParseException {
		elementType = Utils.createTypeFromMap(Utils.propMap(data, "element_type"));
		return this;
	}
	
	public KbType getElementType() {
		return elementType;
	}
	
	@Override
	public String getJavaStyleName() {
		return "List";
	}
	
	@Override
	public Object toJson() {
		Map<String, Object> ret = new TreeMap<String, Object>();
		ret.put("!", "Bio::KBase::KIDL::KBT::List");
		ret.put("annotations", new HashMap<String, Object>());
		ret.put("element_type", elementType.toJson());
		return ret;
	}

	@Override
	public Object toJsonSchema(boolean inner) {
		Map<String, Object> ret = new LinkedHashMap<String, Object>();
		ret.put("type", "array");
		ret.put("original-type", "kidl-list");
		ret.put("items", getElementType().toJsonSchema(true));
		return ret;
	}
	
	@Override
	public String getSpecName() {
	    return "list<" + elementType.getSpecName() + ">";
	}
}
