package us.kbase.mobu.compiler.html;

import static j2html.TagCreator.a;
import static j2html.TagCreator.div;
import static j2html.TagCreator.br;
import static j2html.TagCreator.h2;
import static j2html.TagCreator.span;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import j2html.tags.ContainerTag;
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

	public static final String DOT_HTML = ".html";
	
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
	private static final String CLS_DEPRECATED = "deprecated";
	private static final String CLS_ANNOTATION = "annotation";
	private static final String CLS_INCLUDE = "include";
	private static final String CLS_INDEX = "index";
	
	private static final String CLS_AUTHDEF = "authdef";
	private static final String CLS_TYPEDEF = "typedef";
	private static final String CLS_FUNCDEF = "funcdef";
	
	private static final String MODULE = "module";
	private static final String AUTH = "authentication";
	private static final String TYPEDEF = "typedef";
	private static final String FUNCDEF = "funcdef";
	private static final String RETURNS = "returns";
	private static final String INCLUDE = "#include";
	
	private static final Tag BLANK_LINE = br();
	private static final Tag SPACE = span().withClass(CLS_SPACE);
	private static final Tag TAB = span().withClass(CLS_TAB);
	private static final Tag SEMICOLON = span().withText(";");
	private static final Tag COMMA = span().withText(",");
	private static final Tag COMMENT_INNER = span().withText("*");
	private static final Tag PAREN_OPEN = span().withText("(");
	private static final Tag PAREN_CLOSE = span().withText(")");
	private static final Tag BRKT_OPEN = span().withText("{");
	private static final Tag BRKT_CLOSE = span().withText("}");
	//j2html takes care of translation
	private static final Tag LT = span().withText("<");
	private static final Tag GT = span().withText(">");

	private final String moduleName;
	
	private final Comparator<String> comp = new Comparator<String>() {
		@Override
		public int compare(String o1, String o2) {
			return o1.compareToIgnoreCase(o2);
		}
	};
	
	private final Set<String> refsmodules = new TreeSet<String>(comp);
	private final Set<String> hastypes = new TreeSet<String>(comp);
	private final Map<String, KbTypedef> deptypes =
			new HashMap<String, KbTypedef>();
	private final Set<String> hasfuncs = new TreeSet<String>(comp);
	private final Map<String, KbFuncdef> depfuncs =
			new HashMap<String, KbFuncdef>();
	
	public HTMLGenVisitor(final String moduleName) {
		if (moduleName == null || moduleName.isEmpty()) {
			throw new IllegalArgumentException(
					"moduleName cannot be null or empty");
		}
		this.moduleName = moduleName;
	}
	
	
	@Override
	public Tag visit(final KbAuthdef auth) {
		return span().withClass(CLS_AUTHDEF).with(
				TAB,
				span().withClass(CLS_KEYWORD).withText(AUTH),
				SPACE,
				span().withClass(CLS_KEYWORD).withText(auth.getType()),
				SEMICOLON
				);
	}

	@Override
	public Tag visit(final KbFuncdef funcdef, final List<Tag> params,
			final List<Tag> returns) {
		hasfuncs.add(funcdef.getName());
		ContainerTag fd = span().withClass(CLS_NAME)
				.withText(funcdef.getName());
		if (funcdef.getAnnotations().isDeprecated()) {
			depfuncs.put(funcdef.getName(), funcdef);
			fd.withClass(CLS_DEPRECATED);
			final String dep = funcdef.getAnnotations()
					.getDeprecationReplacement();
			if (dep != null) {
				final String[] modMeth = dep.split("\\.");
				//should only deprecate to a method in the same service
				//right?
				if (modMeth.length >= 2) {
					fd = a().withHref("#" + FUNCDEF + moduleName + "." +
							modMeth[1]).with(fd);
				} else {
					fd = a().withHref("#" + FUNCDEF + moduleName + "." +
							modMeth[0]).with(fd);
				}
			}
		}
		final List<Tag> contents = new LinkedList<Tag>();
		contents.addAll(Arrays.asList(
				div().withClass(CLS_COMMENT).with(
						processComment(funcdef.getComment(), true)),
				TAB,
				span().withClass(CLS_KEYWORD).withText(FUNCDEF),
				SPACE,
				fd.withId(FUNCDEF + moduleName + "." + funcdef.getName()),
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
		return span().withClass(CLS_FUNCDEF).with(contents);
	}

	private Tag makeCommaSepList(final List<Tag> list, final String cls) {
		final LinkedList<Tag> r = new LinkedList<Tag>();
		for (final Tag t: list) {
			r.add(t);
			r.add(COMMA);
			r.add(SPACE);
		}
		if (!list.isEmpty()) {
			r.removeLast();
			r.removeLast();
		}
		
		return span().withClass(cls).with(r);
	}

	@Override
	public Tag visit(final KbList list, final Tag elementType) {
		return span().with(
				span().withClass(CLS_PRIMITIVE).withText("list"),
				LT, elementType, GT);
	}

	@Override
	public Tag visit(final KbMapping map, final Tag keyType,
			final Tag valueType) {
		return span().with(
				span().withClass(CLS_PRIMITIVE).withText("mapping"),
				LT, keyType, COMMA, SPACE, valueType, GT);
	}

	@Override
	public Tag visit(final KbModule module, final List<Tag> components,
			final Map<String, Tag> typeMap) {
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
							processComment(module.getComment(), false)),
					span().withClass(CLS_KEYWORD).withText(MODULE),
					SPACE,
					span().withClass(CLS_NAME).withText(
							module.getModuleName()),
					SPACE,
					BRKT_OPEN,
					BLANK_LINE)
				.with(processed)
				.with(BRKT_CLOSE, SEMICOLON);
	}

	private List<Tag> processComment(final String comment, final boolean tab) {
		if (comment == null || comment.trim().isEmpty()) {
			return new LinkedList<Tag>();
		}
		final List<String> lines = Arrays.asList(comment.split("\\r?\\n"));
		if (lines.isEmpty()) {
			return new LinkedList<Tag>();
		}
		final List<Tag> ret = new LinkedList<Tag>();
		if (tab) {
			ret.add(div().with(TAB, span().withText("/*")));
		} else {
			ret.add(div().withText("/*"));
		}
		for (final String l: lines) {
			final ContainerTag tagline = processWhiteSpace(l);
			final String t = l.trim();
			if (t.startsWith("@")) {
				final String[] a = t.split("\\s", 2);
				tagline.with(span().withClass(CLS_ANNOTATION).withText(a[0]));
				if (a.length == 2) {
					tagline.with(SPACE, span().withText(a[1]));
				}
			} else {
				tagline.withText(t);
			}
			if (tab) {
				ret.add(div().with(TAB, SPACE, COMMENT_INNER, SPACE, tagline));
			} else {
				ret.add(div().with(SPACE, COMMENT_INNER, SPACE, tagline));
			}
		}
		
		if (tab) {
			ret.add(div().with(TAB, SPACE, span().withText("*/")));
		} else {
			ret.add(div().with(SPACE, span().withText("*/")));
		}
		return ret;
	}
	
	// there's got to be a better way to do this
	private ContainerTag processWhiteSpace(final String l) {
		final List<Tag> ws = new LinkedList<Tag>();
		for (int offset = 0; offset < l.length(); ) {
			final int codepoint = l.codePointAt(offset);
			final String c = new String(new int[] {codepoint}, 0, 1);
			if (!Character.isWhitespace(codepoint) ||
					c.equals("\n") || c.equals("\r")) {
				break;
			}
			if (c.equals("\t")) {
				ws.add(TAB);
			} else {
				ws.add(SPACE); // anything else? don't think so
			}
			offset += Character.charCount(codepoint);
		}
		return span().with(ws);
	}

	@Override
	public Tag visit(final KbParameter param, final Tag type) {
		final List<Tag> p = new LinkedList<Tag>();
		p.add(type);
		if (param.getName() != null) {
			p.add(SPACE);
			p.add(span().withClass(CLS_NAME).withText(param.getName()));
			
		}
		return span().withClass(CLS_PARAM).with(p);
	}

	@Override
	public Tag visit(final KbScalar scalar) {
		return span().withClass(CLS_PRIMITIVE).withText(scalar.getSpecName());
	}

	//TODO HTML if nested structures ever compile this needs to be smarter about indents
	// easy enough to just keep track of depth and insert the correct # of tabs
	// another options is to just have the structure def on one line if the
	// parent isn't a kbmodule
	@Override
	public Tag visit(final KbStruct struct, final List<Tag> fields) {
		final List<Tag> f = new LinkedList<Tag>();
		for (final Tag t: fields) {
			f.add(div().with(t));
		}
		return span().with(
				span().withClass(CLS_PRIMITIVE).withText("structure"),
				SPACE, BRKT_OPEN
				)
				.with(f)
				.with(TAB, BRKT_CLOSE);
	}

	@Override
	public Tag visit(final KbStructItem field, final Tag type) {
		return span().with(
				TAB, TAB,
				type,
				SPACE,
				span().withClass(CLS_NAME).withText(field.getName()),
				SEMICOLON
				);
	}

	@Override
	public Tag visit(final KbTuple tuple, final List<Tag> elementTypes) {
		return span().with(
				span().withClass(CLS_PRIMITIVE).withText("tuple"), LT)
			.with(tupleList(tuple, elementTypes))
			.with(GT);
	}

	private List<Tag> tupleList(final KbTuple tuple,
			final List<Tag> elementTypes) {
		if (tuple.getElementNames().size() != elementTypes.size()) {
			throw new IllegalStateException(
					"A programming error occured. Some tuple entries are " +
					"missing names.");
		}
		final LinkedList<Tag> ret = new LinkedList<Tag>();
		for (int i = 0; i < elementTypes.size(); i++) {
			ret.add(elementTypes.get(i));
			//actually never null, set to e_{i}, but might change in future?
			if (tuple.getElementNames().get(i) != null) {
				ret.add(SPACE);
				ret.add(span().withClass(CLS_NAME)
						.withText(tuple.getElementNames().get(i)));
			}
			ret.add(COMMA);
			ret.add(SPACE);
		}
		if (!ret.isEmpty()) {
			ret.removeLast();
			ret.removeLast();
		}
		return ret;
	}

	@Override
	public Tag visit(final KbTypedef typedef, final KidlNode parent,
			final Tag aliasType) {
		if (!(parent instanceof KbModule)) {
			return makeTypeDefLink(typedef);
		}
		hastypes.add(typedef.getName());
		ContainerTag td = span().withClass(CLS_NAME)
				.withText(typedef.getName());
		if (typedef.getAnnotations().isDeprecated()) {
			deptypes.put(typedef.getName(), typedef);
			td.withClass(CLS_DEPRECATED);
			final String dep = typedef.getAnnotations()
					.getDeprecationReplacement();
			if (dep != null) {
				td = makeTypedefAnchor(dep.split("\\.")).with(td);
			}
		}
		return span().withClass(CLS_TYPEDEF)
				.with(
					div().withClass(CLS_COMMENT).with(
							processComment(typedef.getComment(), true)),
					TAB,
					span().withClass(CLS_KEYWORD).withText(TYPEDEF),
					SPACE, aliasType, SPACE,
					td.withId(TYPEDEF + moduleName + "." + typedef.getName()),
					SEMICOLON
				);
	}

	private ContainerTag makeTypeDefLink(final KbTypedef typedef) {
		final String linkname;
		if (typedef.getModule().equals(moduleName)) {
			linkname = typedef.getName();
		} else {
			linkname = typedef.getModule() + "." + typedef.getName();
			refsmodules.add(typedef.getModule());
		}
		final ContainerTag n = span().withClass(CLS_NAME).with(
				makeTypedefAnchor(typedef.getModule(), typedef.getName())
				.withText(linkname));
		if (typedef.getAnnotations().isDeprecated()) {
			n.withClass(CLS_DEPRECATED);
		}
		return n;
	}
	
	private ContainerTag makeTypedefAnchor(final String[] modMeth) {
		//TODO HTML WARN if modMeth.length != 1 or 2
		if (modMeth.length >= 2) {
			return makeTypedefAnchor(modMeth[0], modMeth[1]);
		}
		return makeTypedefAnchor(null, modMeth[0]);
	}
	
	private ContainerTag makeTypedefAnchor(
			String moduleName, final String name) {
		final String linkpref;
		if (moduleName == null || this.moduleName.equals(moduleName)) {
			linkpref = "";
			moduleName = this.moduleName;
		} else {
			linkpref = "./" + moduleName + DOT_HTML;
		}
		return a().withHref(linkpref + "#" + TYPEDEF + moduleName + "." +
				name);
	}

	@Override
	public Tag visit(final KbUnspecifiedObject obj) {
		return span().withClass(CLS_PRIMITIVE).withText(obj.getSpecName());
	}

	public List<Tag> buildIncludes() {
		final List<Tag> includes = new LinkedList<Tag>();
		for (final String module: refsmodules) {
			includes.add(div().withClass(CLS_INCLUDE).with(
					span().withClass(CLS_KEYWORD).withText(INCLUDE),
					SPACE,
					LT,
					span().withClass(CLS_NAME).with(
							a().withHref("./" + module + DOT_HTML)
							.withText(module)),
					GT));
		}
		return includes;
	}

	public Tag buildIndexes() {
		final ContainerTag indexes = div().withClass(CLS_INDEX);
		if (!hasfuncs.isEmpty()) {
			indexes.with(h2().withText("Function Index"));
			for (final String f: hasfuncs) {
				final ContainerTag fd = span().withClass(CLS_NAME).with(
						a().withHref("#" + FUNCDEF + moduleName + "." + f)
						.withText(f));
				if (depfuncs.containsKey(f)) {
					fd.withClass(CLS_DEPRECATED);
				}
				indexes.with(div().with(TAB, fd));
			}
		}
		if (!hastypes.isEmpty()) {
			indexes.with(h2().withText("Type Index"));
			for (final String t: hastypes) {
				final ContainerTag td = span().withClass(CLS_NAME).with(
						a().withHref("#" + TYPEDEF + moduleName + "." + t)
						.withText(t));
				if (deptypes.containsKey(t)) {
					td.withClass(CLS_DEPRECATED);
				}
				indexes.with(div().with(TAB, td));
			}
		}
		return indexes;
	}

}
