package us.kbase.kidl;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Class represents structure item in spec-file.
 */
public class KbStructItem {
	private String name;
	private boolean nullable;
	private KbType itemType;
	private boolean optional;
	
	public KbStructItem() {}
	
	public KbStructItem(KbType itemType, String name) {
		this.itemType = itemType;
		this.name = name;
		this.nullable = false;
	}
	
	public KbStructItem loadFromMap(Map<?,?> data) throws KidlParseException {
		name = Utils.prop(data, "name");
		nullable = (0 != Utils.intPropFromString(data, "nullable"));
		itemType = Utils.createTypeFromMap(Utils.propMap(data, "item_type"));
		return this;
	}

	void utilizeAnnotation(Set<String> optionalFields) {
		optional = optionalFields != null && optionalFields.contains(name);
		if (optional)
			optionalFields.remove(name);
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isNullable() {
		return nullable;
	}
	
	public KbType getItemType() {
		return itemType;
	}
	
	public boolean isOptional() {
		return optional;
	}
	
	public Object toJson() {
		Map<String, Object> ret = new TreeMap<String, Object>();
		ret.put("!", "Bio::KBase::KIDL::KBT::StructItem");
		ret.put("item_type", itemType.toJson());
		ret.put("name", name);
		ret.put("nullable", nullable ? "1" : "0");
		return ret;
	}
}
