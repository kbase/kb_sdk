package us.kbase.kidl;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Class represents mapping in spec-file.
 */
public class KbMapping extends KbBasicType {
	private KbType keyType;
	private KbType valueType;
	
	public KbMapping() {}
	
	public KbMapping(KbType keyType, KbType valueType) {
		this.keyType = keyType;
		this.valueType = valueType;
	}
	
	public KbMapping loadFromMap(Map<?,?> data) throws KidlParseException {
		keyType = Utils.createTypeFromMap(Utils.propMap(data, "key_type"));
		valueType = Utils.createTypeFromMap(Utils.propMap(data, "value_type"));
		return this;
	}
	
	public KbType getKeyType() {
		return keyType;
	}
	
	public KbType getValueType() {
		return valueType;
	}
	
	@Override
	public String getJavaStyleName() {
		return "Map";
	}
	
	@Override
	public <T> T accept(final KidlVisitor<T> visitor, final KidlNode parent) {
		return visitor.visit(this, keyType.accept(visitor, this),
				valueType.accept(visitor, this));
	}
	
	@Override
	public Object toJsonSchema(boolean inner) {
		Map<String, Object> ret = new LinkedHashMap<String, Object>();
		ret.put("type", "object");
		ret.put("original-type", "kidl-mapping");
		ret.put("additionalProperties", getValueType().toJsonSchema(true));
		KbType keyType = Utils.resolveTypedefs(getKeyType());
		if (keyType instanceof KbScalar) {
			KbScalar sc = (KbScalar)keyType;
			if (sc.getIdReference() != null)
				ret.put("id-reference", sc.getIdReference().toJsonSchema());
		}
		return ret;
	}
	
	@Override
	public String getSpecName() {
	    return "mapping<" + keyType.getSpecName() + "," + valueType.getSpecName() + ">";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("KbMapping [keyType=");
		builder.append(keyType);
		builder.append(", valueType=");
		builder.append(valueType);
		builder.append("]");
		return builder.toString();
	}
}
