package us.kbase.kidl;

import java.util.Map;

public class KbTypeInfo {
	private String name;
	private String module;
	private String comment;
	private String english;
	private KbType ref;
	
	public KbTypeInfo loadFromMap(Map<?,?> data) throws KidlParseException {
		name = Utils.prop(data, "name");
		module = Utils.prop(data, "module");
		comment = Utils.prop(data, "comment");
		english = Utils.prop(data, "english");
		ref = Utils.createTypeFromMap(Utils.propMap(data, "ref"));
		return this;
	}
	
	public String getName() {
		return name;
	}
	
	public String getModule() {
		return module;
	}
	
	public String getComment() {
		return comment;
	}
	
	public String getEnglish() {
		return english;
	}
	
	public KbType getRef() {
		return ref;
	}

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("KbTypeInfo [name=");
        builder.append(name);
        builder.append(", module=");
        builder.append(module);
        builder.append(", comment=");
        builder.append(comment);
        builder.append(", english=");
        builder.append(english);
        builder.append(", ref=");
        builder.append(ref);
        builder.append("]");
        return builder.toString();
    }
}
