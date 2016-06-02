package us.kbase.kidl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Class represents tuple in spec-file.
 */
public class KbTuple extends KbBasicType {
	private List<String> elementNames = new ArrayList<String>();
	private List<KbType> elementTypes = new ArrayList<KbType>();
	private String name = null;
	private String comment = null;
	
	public KbTuple() {}
	
	public KbTuple(List<KbType> types) {
		elementNames = new ArrayList<String>();
		for (@SuppressWarnings("unused") KbType type : types)
			elementNames.add("e_" + (elementNames.size() + 1));
		elementNames = Collections.unmodifiableList(elementNames);
		elementTypes = Collections.unmodifiableList(types);
	}

	public void addElement(KbParameter elem) {
		addElement(elem.getType(), elem.getName());
	}
	
	public void addElement(KbType type, String name) {
		if (name == null)
			name = "e_" + (elementNames.size() + 1);
		elementTypes.add(type);
		elementNames.add(name);
	}
	
	public KbTuple loadFromMap(Map<?,?> data) throws KidlParseException {
		List<?> optionList = Utils.propList(data, "element_names");
		elementNames = Collections.unmodifiableList(Utils.repareTypingString(optionList));
		elementTypes = new ArrayList<KbType>();
		for (Map<?,?> itemProps : Utils.getListOfMapProp(data, "element_types")) {
			elementTypes.add(Utils.createTypeFromMap(itemProps));
		}
		elementTypes = Collections.unmodifiableList(elementTypes);
		return this;
	}
		
	public List<String> getElementNames() {
		return elementNames;
	}
	
	public List<KbType> getElementTypes() {
		return elementTypes;
	}
	
	@Override
	public String getJavaStyleName() {
		return "Tuple";
	}

	public String getComment() {
		return comment;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getName() {
		if (name == null)
			throw new IllegalStateException("Property name was not set for tuple");
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public <T> T accept(final KidlVisitor<T> visitor) {
		final List<T> elementTypes = new LinkedList<T>();
		for (final KbType t: this.elementTypes) {
			elementTypes.add(t.accept(visitor));
		}
		return visitor.visit(this, elementTypes);
	}
	
	@Override
	public Object toJson() {
		Map<String, Object> ret = new TreeMap<String, Object>();
		ret.put("!", "Bio::KBase::KIDL::KBT::Tuple");
		if (comment != null && comment.length() > 0)
			ret.put("comment", comment);
		ret.put("element_names", elementNames);
		List<Object> elementTypeList = new ArrayList<Object>();
		for (KbType type : elementTypes)
			elementTypeList.add(type.toJson());
		ret.put("element_types", elementTypeList);
		ret.put("annotations", new HashMap<String, Object>());
		if (name != null)
			ret.put("name", name);
		return ret;
	}

	@Override
	public Object toJsonSchema(boolean inner) {
		Map<String, Object> ret = new LinkedHashMap<String, Object>();
		ret.put("type", "array");
		ret.put("original-type", "kidl-tuple");
		ret.put("maxItems", getElementTypes().size());
		ret.put("minItems", getElementTypes().size());
		List<Object> items = new ArrayList<Object>();
		for (KbType iType : getElementTypes())
			items.add(iType.toJsonSchema(true));
		ret.put("items", items);
		return ret;
	}
	
	@Override
	public void afterCreation() {
		// Let's check duplication in field names
		Map<String, int[]> fieldToCountAndSuffix = new HashMap<String, int[]>();
		for (String elementName : elementNames) {
			if (elementName == null)
				continue;
			int[] value = fieldToCountAndSuffix.get(elementName);
			if (value == null) {
				value = new int[] {0, 0};
				fieldToCountAndSuffix.put(elementName, value);
			}
			value[0]++;
		}
		List<String> newElementNames = new ArrayList<String>();
		for (int pos = 0; pos < elementNames.size(); pos++) {
			String elementName = elementNames.get(pos);
			if (elementName == null || fieldToCountAndSuffix.get(elementName)[0] == 1) {
				newElementNames.add(elementName);
				continue;
			} else {
				int[] value = fieldToCountAndSuffix.get(elementName);
				value[1]++;
				newElementNames.add(elementName + "_" + value[1]);
			}
		}
		elementNames = Collections.unmodifiableList(newElementNames); 
	}
	
	@Override
	public String getSpecName() {
	    StringBuilder ret = new StringBuilder();
	    for (KbType type : elementTypes) {
	        if (ret.length() > 0)
	            ret.append(",");
	        ret.append(type.getSpecName());
	    }
	    return "tuple<" + ret + ">";
	}

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("KbTuple [elementNames=");
        builder.append(elementNames);
        builder.append(", elementTypes=");
        builder.append(elementTypes);
        builder.append(", name=");
        builder.append(name);
        builder.append(", comment=");
        builder.append(comment);
        builder.append("]");
        return builder.toString();
    }
}
