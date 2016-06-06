package us.kbase.kidl;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Input or output part of KbFuncdef.
 * @author rsutormin
 */
public class KbParameter implements KidlNode {
	private String name;
	private String nameNotNullIfPossible;
	private KbType type;
	
	public KbParameter() {}
	
	public KbParameter(KbType type, String name) {
		this.name = name;
		this.nameNotNullIfPossible = name;
		this.type = type;
	}
	
	public KbParameter loadFromMap(Map<?,?> data, boolean isReturn, int paramNum) throws KidlParseException {
		name = Utils.propOrNull(data, "name"); // Utils.prop(data, "name");
		type = Utils.createTypeFromMap(Utils.propMap(data, "type"));
		nameNotNullIfPossible = name;
		if (nameNotNullIfPossible == null && !isReturn) {
			nameNotNullIfPossible = "arg" + paramNum;
		}
		return this;
	}

	public String getOriginalName() {
		return name;
	}

	public String getName() {
		return nameNotNullIfPossible;
	}

	public KbType getType() {
		return type;
	}
	
	@Override
	public <T> T accept(final KidlVisitor<T> visitor, final KidlNode parent) {
		return visitor.visit(this, type.accept(visitor, this));
	}
	
	public Map<String, Object> forTemplates(String altName) {
		Map<String, Object> ret = new LinkedHashMap<String, Object>();
		String name = this.name != null ? this.name : altName;
		ret.put("name", name);
		String validator = null;
		KbType t = type;
		while (t instanceof KbTypedef) {
			t = ((KbTypedef)t).getAliasType();
		}
		if (t instanceof KbMapping || t instanceof KbStruct) {
			validator = "ref($" + name + ") eq 'HASH'";
		} else if (t instanceof KbList || t instanceof KbTuple) {
			validator = "ref($" + name + ") eq 'ARRAY'";
		} else if (t instanceof KbUnspecifiedObject) {
			validator = "defined $" + name;
		} else {
			validator = "!ref($" + name + ")";
		}
		ret.put("validator", validator);
		ret.put("perl_var", "$" + name);
		ret.put("baretype", getBareType(t));
		return ret;
	}

	private static String getBareType(KbType t) {
		if (t instanceof KbScalar) {
			return ((KbScalar)t).getSpecName();
		} else if (t instanceof KbList) {
			return "list";
		} else if (t instanceof KbMapping) {
			return "mapping";
		} else if (t instanceof KbTuple) {
			return "tuple";
		} else if (t instanceof KbStruct) {
			return "struct";
		} else if (t instanceof KbUnspecifiedObject) {
			return "UnspecifiedObject";
		} else {
			throw new IllegalStateException(t.getClass().getSimpleName());
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("KbParameter [name=");
		builder.append(name);
		builder.append(", nameNotNullIfPossible=");
		builder.append(nameNotNullIfPossible);
		builder.append(", type=");
		builder.append(type);
		builder.append("]");
		return builder.toString();
	}
}
