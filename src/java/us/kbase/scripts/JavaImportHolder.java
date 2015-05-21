package us.kbase.scripts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class JavaImportHolder {
	private String currentPackage;
	private Set<String> importedClasses = new TreeSet<String>();
	private Map<String, String> usedSimpleNameToFullName = new HashMap<String, String>();
	
	public JavaImportHolder(String currentPackage) {
		this.currentPackage = currentPackage;
	}
	
	public String ref(String fullyQualifiedClass) {
		String className = fullyQualifiedClass;
		String packageName = "";
		int dotPos = fullyQualifiedClass.lastIndexOf('.');
		if (dotPos > 0) {
			className = fullyQualifiedClass.substring(dotPos + 1);
			packageName = fullyQualifiedClass.substring(0, dotPos);
		}
		if (usedSimpleNameToFullName.containsKey(className)) {
		    if (fullyQualifiedClass.equals(usedSimpleNameToFullName.get(className)))
		        return className;
		    return fullyQualifiedClass;
		}
		if (!(packageName.equals("java.lang") || packageName.equals(currentPackage)))
			importedClasses.add(fullyQualifiedClass);
		usedSimpleNameToFullName.put(className, fullyQualifiedClass);
		return className;
	}
	
	public List<String> generateImports() {
		List<String> ret = new ArrayList<String>();
		for (String className : importedClasses)
			ret.add("import " + className + ";");
		return ret;
	}
}
