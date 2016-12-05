package us.kbase.mobu.compiler;

import us.kbase.kidl.KbParameter;

public class JavaFuncParam {
	private KbParameter original;
	private String javaName;
	private JavaType type;
	
	public JavaFuncParam(KbParameter original, String javaName, JavaType type) {
		this.original = original;
		this.javaName = javaName;
		this.type = type;
	}
	
	public KbParameter getOriginal() {
		return original;
	}
	
	public String getJavaName() {
		return javaName;
	}
	
	public JavaType getType() {
		return type;
	}
}
