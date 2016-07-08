package us.kbase.kidl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * Class represents module in spec file. It consists of 3 blocks in parsing structure:
 * properties, type info, name to type map.
 */
public class KbModule implements KidlNode {
	private String moduleName;
	private String serviceName;
	private String comment;
	private List<String> options;
	private List<KbModuleComp> moduleComponents;
	private List<KbTypeInfo> typeInfoList;
	private Map<String, KbType> nameToType;
    private Map<String, KbFuncdef> nameToFuncdef;
	private String lastAuthTempMode = "none";
	private List<?> data = null;
	
	public KbModule() {
	}
	
	public KbModule(String serviceName, String moduleName, String comment) {
		this.serviceName = serviceName == null ? moduleName : serviceName;
		this.moduleName = moduleName;
		this.comment = comment == null ? "" : comment;
		this.options = new ArrayList<String>();
		this.moduleComponents = new ArrayList<KbModuleComp>();
		this.typeInfoList = new ArrayList<KbTypeInfo>();
		this.nameToType = new LinkedHashMap<String, KbType>();
		nameToType.put("int", new KbScalar("int"));
		nameToType.put("float", new KbScalar("float"));
		nameToType.put("string", new KbScalar("string"));
		nameToType.put("UnspecifiedObject", new KbUnspecifiedObject());
        this.nameToFuncdef = new LinkedHashMap<String, KbFuncdef>();
	}
	
	public void addModuleComponent(KbModuleComp comp) throws KidlParseException {
		moduleComponents.add(comp);
		if (comp instanceof KbAuthdef) {
			lastAuthTempMode = ((KbAuthdef)comp).getType();
		} else if (comp instanceof KbTypedef) {
			KbTypedef typeDef = (KbTypedef)comp;
			if (nameToType.containsKey(typeDef.getName()))
			    throw new KidlParseException("Type " + typeDef.getName() + 
			            " was already declared");
			nameToType.put(typeDef.getName(), typeDef);
		} else {
            KbFuncdef func = (KbFuncdef)comp;
            if (nameToFuncdef.containsKey(func.getName()))
                throw new KidlParseException("Function " + func.getName() + 
                        " was already declared");
            nameToFuncdef.put(func.getName(), func);
		    if (lastAuthTempMode != null && func.getAuthentication() == null)
				func.setAuthentication(lastAuthTempMode);
		}
	}
	
	public <T> T accept(final KidlVisitor<T> visitor) {
		return accept(visitor, null);
	}
	
	@Override
	public <T> T accept(final KidlVisitor<T> visitor, final KidlNode parent) {
		final List<T> components = new LinkedList<T>();
		for (final KbModuleComp c: moduleComponents) {
			components.add(c.accept(visitor, this));
		}
		final Map<String, T> typeMap = new TreeMap<String, T>();
		for (final Entry<String, KbType> entry: nameToType.entrySet()) {
			typeMap.put(entry.getKey(),
					entry.getValue().accept(visitor, this));
		}
		return visitor.visit(this, components, typeMap);
	}

	public void loadFromList(List<?> data) throws KidlParseException {
		this.data = data;
		if (data.size() != 3)
			throw new KidlParseException("List has wrong number of elements: " + data.size());
		Map<?,?> props = (Map<?,?>)data.get(0);
		moduleName = Utils.prop(props, "module_name");
		serviceName = Utils.prop(props, "service_name");
		comment = Utils.propOrNull(props, "comment");
		List<?> optionList = Utils.propList(props, "options");
		options = Collections.unmodifiableList(Utils.repareTypingString(optionList));
		moduleComponents = new ArrayList<KbModuleComp>();
		List<?> compList = Utils.propList(props, "module_components");
		String defaultAuth = null;
        nameToFuncdef = new LinkedHashMap<String, KbFuncdef>();
		for (Object item : compList) {
			if (!Map.class.isInstance(item)) {
				if (item instanceof String) {
					defaultAuth = ((String)item).replace("auth_default", "");
					moduleComponents.add(new KbAuthdef(defaultAuth));
				} else {
					throw new KidlParseException("List item is not compatible with type " +
							"[" + Map.class.getName() + "], it has type: " + item.getClass().getName());
				}
			} else {
				@SuppressWarnings("rawtypes")
				Map<?,?> compProps = (Map)item;
				String compType = Utils.getPerlSimpleType(compProps);
				if (compType.equals("Typedef")) {
					moduleComponents.add(new KbTypedef().loadFromMap(compProps));
				} else if (compType.equals("Funcdef")) {
				    KbFuncdef func = new KbFuncdef().loadFromMap(compProps, defaultAuth);
					moduleComponents.add(func);
					nameToFuncdef.put(func.getName(), func);
				} else {
					throw new KidlParseException("Unknown module component type: " + compType);
				}
			}
		}
		moduleComponents = Collections.unmodifiableList(moduleComponents);
		typeInfoList = new ArrayList<KbTypeInfo>();
		for (Map<?,?> infoProps : Utils.repareTypingMap((List<?>)data.get(1))) {
			typeInfoList.add(new KbTypeInfo().loadFromMap(infoProps));
		}
		typeInfoList = Collections.unmodifiableList(typeInfoList);
		Map<?,?> typeMap = (Map<?,?>)data.get(2);
		nameToType = new LinkedHashMap<String, KbType>();
		for (Object key : typeMap.keySet()) {
			String typeName = key.toString();
			Map<?,?> typeProps = Utils.propMap(typeMap, typeName);
			nameToType.put(typeName, Utils.createTypeFromMap(typeProps));
		}
		nameToType = Collections.unmodifiableMap(nameToType);
	}
	
	public String getServiceName() {
		return serviceName;
	}
	
	public String getModuleName() {
		return moduleName;
	}
	
	public List<String> getOptions() {
		return options;
	}
	
	public String getComment() {
		return comment;
	}
	
	public List<KbModuleComp> getModuleComponents() {
		return moduleComponents;
	}
	
	public List<KbTypeInfo> getTypeInfoList() {
		return typeInfoList;
	}
	
	public Map<String, KbType> getNameToType() {
		return nameToType;
	}
	
	public List<?> getData() {
		return data;
	}

	public Map<String, Object> forTemplates() {
		Map<String, Object> ret = new LinkedHashMap<String, Object>();
		ret.put("module_name", moduleName);
		ret.put("module_doc", Utils.removeStarsInComment(comment));
		List<Object> methods = new ArrayList<Object>();
		List<Object> types = new ArrayList<Object>();
		boolean anyAsync = false;
		for (KbModuleComp comp : moduleComponents)
			if (comp instanceof KbFuncdef) {
				Map<String, Object> map = comp.forTemplates();
				if (map.get("async") != null && map.get("async").toString().equals("true"))
					anyAsync = true;
				methods.add(map);
			} else if (comp instanceof KbTypedef) {
				types.add(comp.forTemplates());
			} else if (comp instanceof KbAuthdef) {
				//do nothing
			} else {
				System.out.println("Module component: " + comp);
			}
		ret.put("methods", methods);
		ret.put("types", types);
		if (anyAsync)
			ret.put("any_async", anyAsync);
		return ret;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("KbModule [moduleName=");
		builder.append(moduleName);
		builder.append(", serviceName=");
		builder.append(serviceName);
		builder.append(", comment=");
		builder.append(comment);
		builder.append(", options=");
		builder.append(options);
		builder.append(", moduleComponents=");
		builder.append(moduleComponents);
		builder.append(", typeInfoList=");
		builder.append(typeInfoList);
		builder.append(", nameToType=");
		builder.append(nameToType);
		builder.append(", lastAuthTempMode=");
		builder.append(lastAuthTempMode);
		builder.append(", data=");
		builder.append(data);
		builder.append("]");
		return builder.toString();
	}
}
