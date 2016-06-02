package us.kbase.kidl;

import java.util.LinkedHashMap;
import java.util.Map;

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
	public <T> T accept(final KidlVisitor<T> visitor) {
		return visitor.visit(this, elementType.accept(visitor));
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

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("KbList [elementType=");
        builder.append(elementType);
        builder.append("]");
        return builder.toString();
    }
}
