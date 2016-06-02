package us.kbase.kidl;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Class represents unspecified object in spec-file.
 */
public class KbUnspecifiedObject extends KbBasicType {
	@Override
	public String getJavaStyleName() {
		return "UObject";
	}

	@Override
	public String getSpecName() {
		return "UnspecifiedObject";
	}

	@Override
	public <T> T accept(final KidlVisitor<T> visitor) {
		return visitor.visit(this);
	}
	
	@Override
	public Object toJsonSchema(boolean inner) {
		Map<String, Object> ret = new LinkedHashMap<String, Object>();
		ret.put("type", "object");
		ret.put("original-type", "kidl-" + getSpecName());
		return ret;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("KbUnspecifiedObject []");
		return builder.toString();
	}
}
