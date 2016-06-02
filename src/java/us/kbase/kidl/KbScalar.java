package us.kbase.kidl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Class represents scalar in spec-file.
 */
public class KbScalar extends KbBasicType {
	public enum Type {
		intType, stringType, floatType, boolType;
	}
	
	private Type scalarType;
	private String javaStyleType;
	private String jsonStyleType;
	private KbAnnotationId idReference;
	private KbAnnotationRange range;
	
	public KbScalar() {}
	
	public KbScalar(String scalarType) {
		this.scalarType = Type.valueOf(scalarType + "Type");
	}
	
	public KbScalar loadFromMap(Map<?,?> data, KbAnnotations annFromTypeDef) throws KidlParseException {
		scalarType = Type.valueOf(Utils.prop(data, "scalar_type") + "Type");
		KbAnnotations ann = null;
		if (data.containsKey("annotations")) 
			ann = new KbAnnotations().loadFromMap(Utils.propMap(data, "annotations"));
		if (ann == null)
			ann = annFromTypeDef;
		utilizeAnnotations(ann);
		return this;
	}

	void utilizeAnnotations(KbAnnotations ann) {
		if (ann != null) {
			idReference = ann.getIdReference();
			range = ann.getRange();
		}
	}
	
	public Type getScalarType() {
		return scalarType;
	}
	
    @Override
	public String getSpecName() {
		String ret = scalarType.toString();
		return ret.substring(0, ret.length() - 4);
	}
	
	@Override
	public String getJavaStyleName() {
		if (javaStyleType == null)
			javaStyleType = buildJavaStyleName();
		return javaStyleType;
	}
	
	private String buildJavaStyleName() {
		switch (scalarType) {
			case stringType: return "String";
			case intType: return "Long";
			case floatType: return "Double";
			case boolType : return "Boolean";
			default: throw new IllegalStateException("Unknown scalar type: " + scalarType);
		}
	}

	public String getFullJavaStyleName() throws KidlParseException {
		return "java.lang." + getJavaStyleName();
	}

	public String getJsonStyleName() {
		if (jsonStyleType == null)
			jsonStyleType = buildJsonStyleName();
		return jsonStyleType;
	}
	
	private String buildJsonStyleName() {
		switch (scalarType) {
			case stringType: return "string";
			case intType: return "integer";
			case floatType: return "number";
			case boolType: return "boolean";
			default: throw new IllegalStateException("Unknown scalar type: " + scalarType);
		}
	}
	
	public KbAnnotationId getIdReference() {
		return idReference;
	}
	
	public KbAnnotationRange getRange() {
		return range;
	}
	
	@Override
	public <T> T accept(final KidlVisitor<T> visitor) {
		return visitor.visit(this);
	}
	
	@Override
	public Object toJson() {
		Map<String, Object> ret = new TreeMap<String, Object>();
		ret.put("!", "Bio::KBase::KIDL::KBT::Scalar");
		//if (scalarType == Type.stringType)
		ret.put("annotations", new HashMap<String, Object>());
		ret.put("scalar_type", getSpecName());
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object toJsonSchema(boolean inner) {
		Map<String, Object> ret = new LinkedHashMap<String, Object>();
		ret.put("type", getJsonStyleName());
		ret.put("original-type", "kidl-" + getSpecName());
		if (getIdReference() != null) {
			KbAnnotationId idRef = getIdReference();
			ret.put("id-reference", idRef.toJsonSchema());
		}
		if(getRange() != null) {
			if(scalarType == Type.intType) {
				ret.putAll((Map<String, Object>)range.toJsonSchemaForInt());
			} else if(scalarType == Type.floatType) {
				ret.putAll((Map<String, Object>)range.toJsonSchemaForFloat());
			}
		}
		return ret;
	}

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("KbScalar [scalarType=");
        builder.append(scalarType);
        builder.append(", javaStyleType=");
        builder.append(javaStyleType);
        builder.append(", jsonStyleType=");
        builder.append(jsonStyleType);
        builder.append(", idReference=");
        builder.append(idReference);
        builder.append(", range=");
        builder.append(range);
        builder.append("]");
        return builder.toString();
    }
}
