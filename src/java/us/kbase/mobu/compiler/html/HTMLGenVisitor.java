package us.kbase.mobu.compiler.html;

import static j2html.TagCreator.a;
import static j2html.TagCreator.div;
import static j2html.TagCreator.br;
import static j2html.TagCreator.span;
import static j2html.TagCreator.pre;

import java.util.Arrays;
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
import us.kbase.kidl.KidlNode;
import us.kbase.kidl.KidlVisitor;

public class HTMLGenVisitor implements KidlVisitor<Tag> {

	//TODO HTML move to html sub package
	//TODO HTML add comment markers back to comments, highlight annotations
	
	private static final String CLS_NAME = "name";
	private static final String CLS_KEYWORD = "keyword";
	private static final String CLS_PRIMITIVE = "primitive";
	private static final String CLS_MODULE = "module";
	private static final String CLS_COMMENT = "comment";
	private static final String CLS_TAB = "tab";
	private static final String CLS_SPACE = "space";
	private static final String CLS_PARAM = "parameter";
	private static final String CLS_PARAMS = "parameters";
	private static final String CLS_RETURNS = "returns";
	
	private static final String CLS_AUTHDEF = "authdef";
	private static final String CLS_TYPEDEF = "typedef";
	private static final String CLS_FUNCDEF = "funcdef";
	
	private static final String MODULE = "module";
	private static final String AUTH = "authentication";
	private static final String TYPEDEF = "typedef";
	private static final String FUNCDEF = "funcdef";
	private static final String RETURNS = "returns";
	
	private static final Tag BLANK_LINE = br();
	private static final Tag SPACE = span().withClass(CLS_SPACE);
	private static final Tag TAB = span().withClass(CLS_TAB);
	private static final Tag SEMICOLON = span().withText(";");
	private static final Tag PAREN_OPEN = span().withText("(");
	private static final Tag PAREN_CLOSE = span().withText(")");

	@Override
	public Tag visit(KbAuthdef auth) {
		return span().withClass(CLS_AUTHDEF).with(
				TAB,
				span().withClass(CLS_KEYWORD).withText(AUTH),
				SPACE,
				span().withClass(CLS_KEYWORD).withText(auth.getType()),
				SEMICOLON
				);
	}

	@Override
	public Tag visit(KbFuncdef funcdef, List<Tag> params, List<Tag> returns) {
		final List<Tag> contents = new LinkedList<Tag>();
		contents.addAll(Arrays.asList(
				TAB,
				span().withClass(CLS_KEYWORD).withText(FUNCDEF),
				SPACE,
				span().withClass(CLS_NAME).withText(funcdef.getName()),
				PAREN_OPEN,
				makeCommaSepList(params, CLS_PARAMS),
				PAREN_CLOSE,
				SPACE,
				span().withClass(CLS_KEYWORD).withText(RETURNS),
				PAREN_OPEN,
				makeCommaSepList(returns, CLS_RETURNS),
				PAREN_CLOSE
		));
		
		if (funcdef.getAuthentication() != null) {
			contents.addAll(Arrays.asList(
					SPACE,
					span().withClass(CLS_KEYWORD).withText(AUTH),
					SPACE,
					span().withClass(CLS_KEYWORD).withText(
							funcdef.getAuthentication())
			));
		}
		contents.add(SEMICOLON);
		return span().withClass(CLS_FUNCDEF)
				.withId(FUNCDEF + funcdef.getName()).with(contents);
	}

	private Tag makeCommaSepList(final List<Tag> list, final String cls) {
		final LinkedList<Tag> r = new LinkedList<Tag>();
		for (final Tag t: list) {
			r.add(t);
			r.add(span().withText(","));
			r.add(SPACE);
		}
		if (!list.isEmpty()) {
			r.removeLast();
			r.removeLast();
		}
		
		return span().withClass(cls).with(r);
	}

	@Override
	public Tag visit(KbList list, Tag elementType) {
		// TODO Auto-generated method stub
		return span().withText("list");
	}

	@Override
	public Tag visit(KbMapping map, Tag keyType, Tag valueType) {
		// TODO Auto-generated method stub
		return span().withText("map");
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
					SPACE,
					span().withClass(CLS_NAME).withText(
							module.getModuleName()),
					SPACE,
					span().withText("{"),
					BLANK_LINE)
				.with(processed)
				.with(
					span().withText("}"), SEMICOLON);
	}

	@Override
	public Tag visit(KbParameter param, Tag type) {
		final List<Tag> p = new LinkedList<Tag>();
		p.add(type);
		if (param.getName() != null) {
			p.add(SPACE);
			p.add(span().withClass(CLS_NAME).withText(param.getName()));
			
		}
		return span().withClass(CLS_PARAM).with(p);
	}

	@Override
	public Tag visit(KbScalar scalar) {
		return span().withClass(CLS_PRIMITIVE).withText(scalar.getSpecName());
	}

	@Override
	public Tag visit(KbStruct struct, List<Tag> fields) {
		// TODO Auto-generated method stub
		return span().withText("struct");
	}

	@Override
	public Tag visit(KbStructItem field, Tag type) {
		// TODO Auto-generated method stub
		return span().withText("structitem");
	}

	@Override
	public Tag visit(KbTuple tuple, List<Tag> elementTypes) {
		
		// TODO Auto-generated method stub
		return span().withText("tuple");
	}

	@Override
	public Tag visit(KbTypedef typedef, KidlNode parent, Tag aliasType) {
		if (parent instanceof KbParameter || parent instanceof KbTypedef) {
			return span().withClass(CLS_NAME).with(
					a()
						.withHref("#" + TYPEDEF + typedef.getName())
						.withText(typedef.getName())
					);
		}
		return span().withClass(CLS_TYPEDEF)
				.withId(TYPEDEF + typedef.getName())
				.with(
					TAB,
					span().withClass(CLS_KEYWORD).withText(TYPEDEF),
					SPACE,
					aliasType,
					SPACE,
					span().withClass(CLS_NAME).withText(typedef.getName()),
					SEMICOLON
				);
	}

	@Override
	public Tag visit(KbUnspecifiedObject obj) {
		return span().withClass(CLS_PRIMITIVE).withText(obj.getSpecName());
	}

}
