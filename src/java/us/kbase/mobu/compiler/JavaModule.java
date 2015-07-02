package us.kbase.mobu.compiler;

import java.util.Collections;
import java.util.List;

import us.kbase.kidl.KbModule;
import us.kbase.mobu.util.TextUtils;

public class JavaModule {
	private String moduleName;
	private KbModule original;
	private List<JavaFunc> funcs;
	
	public JavaModule(KbModule original, List<JavaFunc> funcs) {
		this.moduleName = TextUtils.capitalize(original.getModuleName());
		this.original = original;
		this.funcs = Collections.unmodifiableList(funcs);
	}
	
	public String getModuleName() {
		return moduleName;
	}
	
	public String getModulePackage() {
		return moduleName.toLowerCase();
	}
	
	public KbModule getOriginal() {
		return original;
	}
	
	public List<JavaFunc> getFuncs() {
		return funcs;
	}
}
