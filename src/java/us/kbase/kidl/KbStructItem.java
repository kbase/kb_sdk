package us.kbase.kidl;

import java.util.Map;
import java.util.Set;

/**
 * Class represents structure item in spec-file.
 */
public class KbStructItem implements KidlNode {
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
	
	@Override
	public <T> T accept(final KidlVisitor<T> visitor, final KidlNode parent) {
		return visitor.visit(this, itemType.accept(visitor, this));
	}
	
}
