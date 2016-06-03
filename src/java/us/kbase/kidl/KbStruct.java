package us.kbase.kidl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class represents structure in spec-file.
 */
public class KbStruct extends KbBasicType {

	private String name;
	private KbAnnotations annotations;
	private List<KbStructItem> items;
	private String comment;
	private String module;
	
	public KbStruct() {
		items = new ArrayList<KbStructItem>();
	}

	public KbStruct(List<KbStructItem> items) {
		this.items = Collections.unmodifiableList(items);
		//for ()
	}

	public KbStruct loadFromMap(Map<?,?> data, KbAnnotations annFromTypeDef) throws KidlParseException {
		name = Utils.prop(data, "name");
		items = new ArrayList<KbStructItem>();
		for (Map<?,?> itemProps : Utils.getListOfMapProp(data, "items"))
			items.add(new KbStructItem().loadFromMap(itemProps));
		utilizeAnnotations(annFromTypeDef);
		items = Collections.unmodifiableList(items);
		return this;
	}
	
	void utilizeAnnotations(KbAnnotations ann) throws KidlParseException {
		annotations = ann;
		Set<String> optional = ann == null || ann.getOptional() == null ? null : 
			new LinkedHashSet<String>(ann.getOptional());
		for (KbStructItem item : items)
			item.utilizeAnnotation(optional);
		if (optional != null && optional.size() > 0)
			throw new KidlParseException("Can not find field(s) from optional annotation in structure: " + 
					optional);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<KbStructItem> getItems() {
		return items;
	}
	
	public boolean addItem(KbStructItem item) {
		for (KbStructItem it : items) {
			if (it.getName().equals(item.getName()))
				return false;
		}
		items.add(item);
		return true;
	}
	
	@Override
	public String getJavaStyleName() {
		return "Struct";
	}
	
	public KbAnnotations getAnnotations() {
		return annotations;
	}
	
	public String getComment() {
		return comment;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getModule() {
		return module;
	}
	
	public void setModule(String module) {
		this.module = module;
	}
	
	@Override
	public <T> T accept(final KidlVisitor<T> visitor, final KidlNode parent) {
		final List<T> fields = new LinkedList<T>();
		for (final KbStructItem f: items) {
			fields.add(f.accept(visitor, this));
		}
		return visitor.visit(this, fields);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object toJsonSchema(boolean inner) {
		Map<String, Object> ret = new LinkedHashMap<String, Object>();
		ret.put("type", "object");
		ret.put("original-type", "kidl-structure");
		Map<String, Object> props = new LinkedHashMap<String, Object>();
		for (KbStructItem item : getItems())
			props.put(item.getName(), item.getItemType().toJsonSchema(true));
		ret.put("properties", props);
		ret.put("additionalProperties", true);
		List<String> reqList = new ArrayList<String>();
		for (KbStructItem item : getItems())
			if (!item.isOptional())
				reqList.add(item.getName());
		if (reqList.size() > 0)
			ret.put("required", reqList);
		if (getAnnotations() != null && getAnnotations().getSearchable() != null && !inner) {
			KbAnnotationSearch search = getAnnotations().getSearchable();
			ret.put("searchable-ws-subset", search.toJsonSchema());
		}
		if (getAnnotations() != null && getAnnotations().getWsMetadata() != null && !inner) {
			ret.putAll((Map<? extends String, ? extends Object>) getAnnotations().getWsMetadata().toJsonSchema());
		}
		return ret;
	}
	
	@Override
	public String getSpecName() {
	    return "structure";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("KbStruct [name=");
		builder.append(name);
		builder.append(", annotations=");
		builder.append(annotations);
		builder.append(", items=");
		builder.append(items);
		builder.append(", comment=");
		builder.append(comment);
		builder.append(", module=");
		builder.append(module);
		builder.append("]");
		return builder.toString();
	}
}
