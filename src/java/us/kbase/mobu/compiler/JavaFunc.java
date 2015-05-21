package us.kbase.mobu.compiler;

import java.util.Collections;
import java.util.List;

import us.kbase.kidl.KbAuthdef;
import us.kbase.kidl.KbFuncdef;
import us.kbase.mobu.util.TextUtils;

public class JavaFunc {
	private String moduleName;
	private KbFuncdef original;
	private String javaName;
	private List<JavaFuncParam> params;
	private List<JavaFuncParam> returns;
	private JavaType retMultyType;
	
	public JavaFunc(String moduleName, KbFuncdef original, String javaName, List<JavaFuncParam> params, List<JavaFuncParam> returns, JavaType retMultyType) {
		this.moduleName = TextUtils.capitalize(moduleName).toLowerCase();
		this.original = original;
		this.javaName = javaName;
		this.params = Collections.unmodifiableList(params);
		this.returns = returns;
		this.retMultyType = retMultyType;
	}
	
	public String getModuleName() {
		return moduleName;
	}
	
	public KbFuncdef getOriginal() {
		return original;
	}
	
	public String getJavaName() {
		return javaName;
	}
	
	public List<JavaFuncParam> getParams() {
		return params;
	}

	public List<JavaFuncParam> getReturns() {
		return returns;
	}
	
	public JavaType getRetMultyType() {
		return retMultyType;
	}

	public boolean isAuthCouldBeUsed() {
		return isAuthRequired() || isAuthOptional();
	}

	public boolean isAuthRequired() {
		return KbAuthdef.REQUIRED.equals(original.getAuthentication());
	}
	
	public boolean isAuthOptional() {
		return KbAuthdef.OPTIONAL.equals(original.getAuthentication());
	}
}
