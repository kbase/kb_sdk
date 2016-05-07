package us.kbase.kidl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

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
	public Object toJson() {
		Map<String, Object> ret = new TreeMap<String, Object>();
		ret.put("!", "Bio::KBase::KIDL::KBT::Mapping");
		ret.put("key_type", keyType.toJson());
		ret.put("value_type", valueType.toJson());
		ret.put("annotations", new HashMap<String, Object>());
		return ret;
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
}
