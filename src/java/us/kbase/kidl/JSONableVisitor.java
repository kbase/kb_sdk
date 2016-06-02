package us.kbase.kidl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Tranform a KIDL class structure to a JSONable object (e.g. Maps and
 * Lists).
 */
public class JSONableVisitor implements KidlVisitor<Object> {

	@Override
	public Object visit(final KbAuthdef auth) {
		return "auth_default" + auth.getType();
	}

	@Override
	public Object visit(final KbFuncdef func, final List<Object> params,
			final List<Object> returns) {
		final Map<String, Object> ret = new TreeMap<String, Object>();
		ret.put("!", "Bio::KBase::KIDL::KBT::Funcdef");
		ret.put("annotations", func.getAnnotations().toJson(false));
		ret.put("async", func.isAsync() ? "1" : "0");
		ret.put("authentication", func.getAuthentication());
		ret.put("comment", func.getComment());
		ret.put("name", func.getName());
		ret.put("parameters", params);
		ret.put("return_type", returns);
		return ret;
	}

	@Override
	public Object visit(final KbList list, final Object elementType) {
		final Map<String, Object> ret = new TreeMap<String, Object>();
		ret.put("!", "Bio::KBase::KIDL::KBT::List");
		ret.put("annotations", new HashMap<String, Object>());
		ret.put("element_type", elementType);
		return ret;
	}

	@Override
	public Object visit(final KbMapping map, final Object keyType,
			final Object valueType) {
		final Map<String, Object> ret = new TreeMap<String, Object>();
		ret.put("!", "Bio::KBase::KIDL::KBT::Mapping");
		ret.put("key_type", keyType);
		ret.put("value_type", valueType);
		ret.put("annotations", new HashMap<String, Object>());
		return ret;
	}

	@Override
	public Object visit(final KbModule module, final List<Object> components,
			final Map<String, Object> typeMap) {
		final List<Object> ret = new ArrayList<Object>();
		final Map<String, Object> main = new TreeMap<String, Object>();
		main.put("!", "Bio::KBase::KIDL::KBT::DefineModule");
		//may need to add annotations to the class in the future
		main.put("annotations", new KbAnnotations().toJson(false));
		if (module.getComment() != null)
			main.put("comment", module.getComment());
		main.put("module_components", components);
		main.put("module_name", module.getModuleName());
		main.put("options", module.getOptions());
		main.put("service_name", module.getServiceName());
		ret.add(main);
		// compatibility with Perl type compiler, obsolete
		ret.add(new ArrayList<Object>());
		ret.add(typeMap);
		return ret;
	}

	@Override
	public Object visit(final KbParameter param, final Object type) {
		final Map<String, Object> ret = new TreeMap<String, Object>();
		if (param.getName() != null)
			ret.put("name", param.getName());
		ret.put("type", type);
		return ret;
	}

	@Override
	public Object visit(final KbScalar scalar) {
		final Map<String, Object> ret = new TreeMap<String, Object>();
		ret.put("!", "Bio::KBase::KIDL::KBT::Scalar");
		//if (scalarType == Type.stringType)
		ret.put("annotations", new HashMap<String, Object>());
		ret.put("scalar_type", scalar.getSpecName());
		return ret;
	}

	@Override
	public Object visit(final KbStruct struct, final List<Object> fields) {
		final Map<String, Object> ret = new TreeMap<String, Object>();
		ret.put("!", "Bio::KBase::KIDL::KBT::Struct");
		/* per Roman, this weirdness with the annotations is due to matching
		 * the output of the old type compiler. Raw structs can't actually
		 * have annotations, only funcdefs and typedefs
		 */
		if (struct.getAnnotations() == null) {
			final Map<String, Object> ann = new HashMap<String, Object>();
			ann.put("searchable_ws_subset", new HashMap<String, Object>());
			ann.put("metadata", new HashMap<String, Object>());
			ret.put("annotations", ann);
		} else {
			final Map<String, Object> ann = new HashMap<String, Object>();
			if (struct.getAnnotations().getSearchable() == null) {
				ann.put("searchable_ws_subset", new HashMap<String, Object>());
			}
			if(struct.getAnnotations().getWsMetadata() == null) {
				ann.put("metadata", new HashMap<String, Object>());
			}
			ret.put("annotations", ann);
		}
		if (struct.getComment() != null && struct.getComment().length() > 0)
			ret.put("comment", struct.getComment());
		ret.put("items", fields);
		if (struct.getModule() != null)
			ret.put("module", struct.getModule());
		if (struct.getName() != null)
			ret.put("name", struct.getName());
		return ret;
	}

	@Override
	public Object visit(final KbStructItem field, final Object type) {
		Map<String, Object> ret = new TreeMap<String, Object>();
		ret.put("!", "Bio::KBase::KIDL::KBT::StructItem");
		ret.put("item_type", type);
		ret.put("name", field.getName());
		ret.put("nullable", field.isNullable() ? "1" : "0");
		return ret;
	}

	@Override
	public Object visit(final KbTuple tuple, final List<Object> elementTypes) {
		final Map<String, Object> ret = new TreeMap<String, Object>();
		ret.put("!", "Bio::KBase::KIDL::KBT::Tuple");
		if (tuple.getComment() != null && tuple.getComment().length() > 0)
			ret.put("comment", tuple.getComment());
		ret.put("element_names", tuple.getElementNames());
		ret.put("element_types", elementTypes);
		ret.put("annotations", new HashMap<String, Object>());
		if (tuple.getName() != null)
			ret.put("name", tuple.getName());
		return ret;
	}

	@Override
	public Object visit(final KbTypedef typedef, final Object aliasType) {
		final Map<String, Object> ret = new TreeMap<String, Object>();
		ret.put("!", "Bio::KBase::KIDL::KBT::Typedef");
		ret.put("alias_type", aliasType);
		ret.put("annotations", typedef.getAnnotations().toJson(true));
		ret.put("comment", typedef.getComment());
		ret.put("module", typedef.getModule());
		ret.put("name", typedef.getName());
		return ret;
	}

	@Override
	public Object visit(final KbUnspecifiedObject obj) {
		final Map<String, Object> ret = new TreeMap<String, Object>();
		ret.put("!", "Bio::KBase::KIDL::KBT::UnspecifiedObject");
		ret.put("annotations", new HashMap<String, Object>());
		return ret;
	}

}
