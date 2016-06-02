package us.kbase.mobu.compiler;

import java.util.List;
import java.util.Map;

import j2html.TagCreator;
import j2html.tags.Tag;
import us.kbase.kidl.KbAuthdef;
import us.kbase.kidl.KbFuncdef;
import us.kbase.kidl.KbList;
import us.kbase.kidl.KbMapping;
import us.kbase.kidl.KbModule;
import us.kbase.kidl.KbParameter;
import us.kbase.kidl.KbScalar;
import us.kbase.kidl.KbStruct;
import us.kbase.kidl.KbStructItem;
import us.kbase.kidl.KbTuple;
import us.kbase.kidl.KbTypedef;
import us.kbase.kidl.KbUnspecifiedObject;
import us.kbase.kidl.KidlVisitor;

public class HTMLGenVisitor implements KidlVisitor<Tag> {

	@Override
	public Tag visit(KbAuthdef auth) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tag visit(KbFuncdef func, List<Tag> params, List<Tag> returns) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tag visit(KbList list, Tag elementType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tag visit(KbMapping map, Tag keyType, Tag valueType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tag visit(KbModule module, List<Tag> components,
			Map<String, Tag> typeMap) {
		return TagCreator.div().withText(module.getModuleName());
	}

	@Override
	public Tag visit(KbParameter param, Tag type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tag visit(KbScalar scalar) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tag visit(KbStruct struct, List<Tag> fields) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tag visit(KbStructItem field, Tag type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tag visit(KbTuple tuple, List<Tag> elementTypes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tag visit(KbTypedef typedef, Tag aliasType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tag visit(KbUnspecifiedObject obj) {
		// TODO Auto-generated method stub
		return null;
	}

}
