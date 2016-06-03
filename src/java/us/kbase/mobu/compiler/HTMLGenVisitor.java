package us.kbase.mobu.compiler;

import static j2html.TagCreator.div;
import static j2html.TagCreator.br;
import static j2html.TagCreator.span;
import static j2html.TagCreator.pre;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

	//TODO HTML move to html sub package
	//TODO HTML add comment markers back to comments, highlight annotations
	
	private static final String CLS_NAME = "name";
	private static final String CLS_KEYWORD = "keyword";
	private static final String CLS_MODULE = "module";
	private static final String CLS_COMMENT = "comment";
	private static final String CLS_TAB = "tab";
	
	private static final String CLS_AUTHDEF = "authdef";
	
	private static final String MODULE = "module";
	private static final String AUTH = "authentication";
	
	private static final Tag BLANK_LINE = br();

	@Override
	public Tag visit(KbAuthdef auth) {
		System.out.println("visit auth");
		return span().withClass(CLS_AUTHDEF).with(
				span().withClass(CLS_TAB),
				span().withClass(CLS_KEYWORD).withText(AUTH),
				span().withClass(CLS_KEYWORD).withText(auth.getType()),
				span().withText(";") //TODO HTML probably need a class for this to set distance correctly
				);
	}

	@Override
	public Tag visit(KbFuncdef func, List<Tag> params, List<Tag> returns) {
		return div().withText("funcdef " + func.getName() + " " + func.getAuthentication());
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
		final LinkedList<Tag> processed = new LinkedList<Tag>();
		processed.add(BLANK_LINE);
		for (final Tag c: components) {
			processed.add(div().with(c));
			processed.add(BLANK_LINE);
		}
		processed.removeLast();
		return div().withClass(CLS_MODULE)
				.with(
					div().withClass(CLS_COMMENT).with(
							pre().withText(module.getComment())),
					span().withClass(CLS_KEYWORD).withText(MODULE),
					span().withClass(CLS_NAME).withText(
							module.getModuleName()),
					span().withText("{"),
					BLANK_LINE)
				.with(processed)
				.with(div().withText("};"));
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
		return div().withText("typedef");
	}

	@Override
	public Tag visit(KbUnspecifiedObject obj) {
		// TODO Auto-generated method stub
		return null;
	}

}
