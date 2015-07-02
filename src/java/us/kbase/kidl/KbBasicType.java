package us.kbase.kidl;

import java.util.Map;

/**
 * Unnamed standard type. This could be one of {KbScalar, KbStruct, KbList, KbTuple, KbMapping or KbUnspecifiedObject}.
 * @author rsutormin
 */
public abstract class KbBasicType implements KbType {
	
	public static KbBasicType createFromMap(Map<?,?> data, KbAnnotations annFromTypeDef) throws KidlParseException {
		String typeName = Utils.getPerlSimpleType(data);
		if (typeName.equals("Scalar")) {
			return new KbScalar().loadFromMap(data, annFromTypeDef);
		} else if (typeName.equals("List")) {
			return new KbList().loadFromMap(data);
		} else if (typeName.equals("Struct")) {
			return new KbStruct().loadFromMap(data, annFromTypeDef);
		} else if (typeName.equals("Tuple")) {
			return new KbTuple().loadFromMap(data);
		} else if (typeName.equals("Mapping")) {
			return new KbMapping().loadFromMap(data);
		} else if (typeName.equals("UnspecifiedObject")) {
			return new KbUnspecifiedObject();
		}
		throw new KidlParseException("Unsupported type: " + typeName);
	}
	
	public abstract String getJavaStyleName();
	
	@Override
	public void afterCreation() {
		// Default implementation - just do nothing
	}
}
