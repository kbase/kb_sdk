package us.kbase.kidl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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
	public Object toJson() {
		Map<String, Object> ret = new TreeMap<String, Object>();
		ret.put("!", "Bio::KBase::KIDL::KBT::Struct");
		if (annotations == null) {
			Map<String, Object> ann = new HashMap<String, Object>();
			ann.put("searchable_ws_subset", new HashMap<String, Object>());
			ann.put("metadata", new HashMap<String, Object>());
			ret.put("annotations", ann);
		} else {
			Map<String, Object> ann = new HashMap<String, Object>();
			if(annotations.getSearchable()==null) {
				ann.put("searchable_ws_subset", new HashMap<String, Object>());
			}
			if(annotations.getWsMetadata()==null) {
				ann.put("metadata", new HashMap<String, Object>());
			}
			ret.put("annotations", ann);
		}
		if (comment != null && comment.length() > 0)
			ret.put("comment", comment);
		List<Object> itemList = new ArrayList<Object>();
		for (KbStructItem item : items)
			itemList.add(item.toJson());
		ret.put("items", itemList);
		if (module != null)
			ret.put("module", module);
		if (name != null)
			ret.put("name", name);
		return ret;
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
}
