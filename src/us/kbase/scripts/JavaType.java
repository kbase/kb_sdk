package us.kbase.scripts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import us.kbase.kidl.KbBasicType;
import us.kbase.kidl.KbStruct;
import us.kbase.kidl.KbTypedef;

public class JavaType implements Comparable<JavaType> {
	private List<KbTypedef> aliasHistoryOuterToDeep;
	private KbBasicType mainType;
	private List<JavaType> internalTypes = new ArrayList<JavaType>();
	private List<String> internalFields = new ArrayList<String>();
	private List<String> internalComments = new ArrayList<String>();
	private String moduleName;
	private String originalTypeName;
	private String javaClassName;
	
	public JavaType(String typeName, KbBasicType mainType, String moduleName, List<KbTypedef> aliasHistoryOuterToDeep) {
		this.mainType = mainType;
		this.moduleName = TextUtils.capitalize(moduleName).toLowerCase();
		this.aliasHistoryOuterToDeep = Collections.unmodifiableList(aliasHistoryOuterToDeep);
		this.originalTypeName = typeName;
		this.javaClassName = typeName == null ? null : TextUtils.capitalize(typeName);
	}
	
	public boolean needClassGeneration() {
		return mainType instanceof KbStruct;
	}
	
	public void addInternalType(JavaType iType) {
		internalTypes.add(iType);
	}

	public void addInternalField(String name, String comment) {
		internalFields.add(name);
		internalComments.add(comment == null ? "" : comment);
	}
	
	public List<KbTypedef> getAliasHistoryOuterToDeep() {
		return aliasHistoryOuterToDeep;
	}
	
	public KbBasicType getMainType() {
		return mainType;
	}
	
	public List<JavaType> getInternalTypes() {
		return internalTypes;
	}
	
	public List<String> getInternalFields() {
		while (internalFields.size() < internalTypes.size()) {
			internalFields.add("e" + (internalFields.size() + 1));
		}
		return internalFields;
	}
	
	public String getInternalComment(int itemPos) {
		if (internalComments.size() == 0)
			return "";
		return internalComments.get(itemPos);
	}
	
	public String getModuleName() {
		return moduleName;
	}
	
	public String getOriginalTypeName() {
		return originalTypeName;
	}
	
	public String getJavaClassName() {
		if (javaClassName == null) {
			StringBuilder sb = new StringBuilder(mainType.getJavaStyleName());
			for (int typePos = 0; typePos < internalTypes.size(); typePos++) {
				JavaType iType = internalTypes.get(typePos);
				sb.append(getTypeNameWithModuleIfNeed(iType)).append(typePos + 1);
			}
			javaClassName = sb.toString();
		}
		return javaClassName;
	}
	
	private String getModuleAndJavaName() {
	    return moduleName + "." + getJavaClassName();
	}

	private String getTypeNameWithModuleIfNeed(JavaType iType) {
		return (iType.getModuleName().equals(getModuleName()) ? "" : iType.getModuleName()) + iType.getJavaClassName();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof JavaType))
			return false;
		JavaType type = (JavaType)obj;
		return getModuleAndJavaName().equals(type.getModuleAndJavaName());
	}
	
	@Override
	public int compareTo(JavaType o) {
		return getModuleAndJavaName().compareTo(o.getModuleAndJavaName());
	}
}
