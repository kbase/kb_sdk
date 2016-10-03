package us.kbase.kidl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import us.kbase.common.service.Tuple2;

/**
 * Class represents function definition in spec-file.
 */
public class KbFuncdef implements KbModuleDef {
	private String name;
	private boolean async;
	private String authentication;
	private String comment;
	private List<KbParameter> parameters;
	private List<KbParameter> returnType;
	private KbAnnotations annotations;
	private Map<?,?> data = null;
	
	private static int pyDocstringWidth = 70;
	private static String pyDocstringIndent = "   ";
	
	public KbFuncdef() {}

	public KbFuncdef(final String name, final String comment)
			throws KidlParseException {
		this(name, comment, false);
	}
	
	public KbFuncdef(final String name, final String comment,
			final boolean async)
			throws KidlParseException {
		this.name = name;
		this.async = async;
		this.comment = comment == null ? "" : comment;
		parameters = new ArrayList<KbParameter>();
		returnType = new ArrayList<KbParameter>();
		annotations = new KbAnnotations().loadFromComment(this.comment, this);
	}

	public KbFuncdef loadFromMap(Map<?,?> data, String defaultAuth) throws KidlParseException {
		name = Utils.prop(data, "name");
		async = (0 != Utils.intPropFromString(data, "async"));
		authentication = Utils.prop(data, "authentication");  // defaultAuth was already involved on kidl stage
		comment = Utils.prop(data, "comment");
		parameters = loadParameters(Utils.propList(data, "parameters"), false);
		returnType = loadParameters(Utils.propList(data, "return_type"), true);
		annotations = new KbAnnotations();
		if (data.containsKey("annotations")) {
			annotations.loadFromMap(Utils.propMap(data, "annotations"));
		}
		this.data = data;
		return this;
	}
	
	private static List<KbParameter> loadParameters(List<?> inputList, boolean isReturn) throws KidlParseException {
		List<KbParameter> ret = new ArrayList<KbParameter>();
		for (Map<?,?> data : Utils.repareTypingMap(inputList)) {
			ret.add(new KbParameter().loadFromMap(data, isReturn, ret.size() + 1));
		}
		return Collections.unmodifiableList(ret);
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isAsync() {
		return async;
	}
	
	public String getAuthentication() {
		return authentication;
	}

	public boolean isAuthenticationRequired() {
	    return KbAuthdef.REQUIRED.equals(authentication);
	}

	public boolean isAuthenticationOptional() {
	    return KbAuthdef.OPTIONAL.equals(authentication);
	}

	public void setAuthentication(String authentication) {
		this.authentication = authentication;
	}
	
	public String getComment() {
		return comment;
	}
	
	public List<KbParameter> getParameters() {
		return parameters;
	}
	
	public List<KbParameter> getReturnType() {
		return returnType;
	}
	
	public Map<?, ?> getData() {
		return data;
	}
	

	@Override
	public KbAnnotations getAnnotations() {
		return annotations;
	}
	
	@Override
	public <T> T accept(final KidlVisitor<T> visitor, final KidlNode parent) {
		final List<T> params = new LinkedList<T>();
		final List<T> returns = new LinkedList<T>();
		for (final KbParameter p: parameters) {
			params.add(p.accept(visitor, this));
		}
		for (final KbParameter p: returnType) {
			returns.add(p.accept(visitor, this));
		}
		return visitor.visit(this, params, returns);
	}

	@Override
	public Map<String, Object> forTemplates() {
		Map<String, Object> ret = new LinkedHashMap<String, Object>();
		ret.put("name", name);
		ret.put("arg_count", parameters.size());
		List<String> paramNames = getNameList(parameters, false);
		ret.put("args", getNames(paramNames, null));
		ret.put("arg_vars", getNames(paramNames, "$"));
		ret.put("ret_count", returnType.size());
		List<String> returnNames = getNameList(returnType, true);
		ret.put("ret_vars", getNames(returnNames, "$"));
		ret.put("authentication", authentication == null ? "none" : authentication);
		List<String> docLines = new ArrayList<String>();
		LinkedList<Tuple2<String, KbType>> typeQueue = new LinkedList<Tuple2<String, KbType>>();
		for (int paramPos = 0; paramPos < parameters.size(); paramPos++) {
			KbParameter arg = parameters.get(paramPos);
			String item = paramNames.get(paramPos);
			typeQueue.add(new Tuple2<String, KbType>().withE1("$" + item).withE2(arg.getType()));
		}
		for (int returnPos = 0; returnPos < returnType.size(); returnPos++) {
			KbParameter arg = returnType.get(returnPos);
			String item = returnNames.get(returnPos);
			typeQueue.add(new Tuple2<String, KbType>().withE1("$" + item).withE2(arg.getType()));
		}
		processArgDoc(typeQueue, docLines, null, true);
		ret.put("arg_doc", docLines);
		String docWithoutStars = Utils.removeStarsInComment(comment);
		ret.put("doc", docWithoutStars);
		List<String> pyDocLines = new ArrayList<String>();
		for (String line : Utils.parseCommentLines(docWithoutStars)) {
			pyDocLines.add(removeThreeQuotes(line));
		}
		for (int paramPos = 0; paramPos < parameters.size(); paramPos++) {
			KbParameter arg = parameters.get(paramPos);
			String paramName = paramNames.get(paramPos);
			String descr = getTypeDescr(arg.getType(), null);
			pyDocLines.addAll(cutLine(":param " + paramName + ": " + removeThreeQuotes(descr),
					pyDocstringWidth, pyDocstringIndent));
		}
		if (returnType.size() > 0) {
			String descr;
			if (returnType.size() == 1) {
				KbParameter arg = returnType.get(0);
				descr = getTypeDescr(arg.getType(), null);
			} else {
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < returnType.size(); i++) {
					KbParameter arg = returnType.get(i);
					if (sb.length() > 0)
						sb.append(", ");
					sb.append('(').append((i + 1)).append(") ");
					sb.append(getTypeDescr(arg.getType(), arg.getName()));
				}
				descr = "multiple set - " + sb.toString();
			}
			pyDocLines.addAll(cutLine(":returns: " + removeThreeQuotes(descr), 
					pyDocstringWidth, pyDocstringIndent));
		}
		ret.put("py_doc_lines", pyDocLines);
		List<Object> params = new ArrayList<Object>();
		for (int paramPos = 0; paramPos < parameters.size(); paramPos++) {
			KbParameter param = parameters.get(paramPos);
			Map<String, Object> paramMap =  param.forTemplates(paramNames.get(paramPos));
			paramMap.put("index", paramPos + 1);
			params.add(paramMap);
		}
		ret.put("params", params);
		List<Object> returns = new ArrayList<Object>();
		for (int retPos = 0; retPos < returnType.size(); retPos++) {
			KbParameter retParam = returnType.get(retPos);
			Map<String, Object> paramMap =  retParam.forTemplates(returnNames.get(retPos));
			paramMap.put("index", retPos + 1);
			returns.add(paramMap);
		}
		ret.put("returns", returns);
		if (isAsync())
			ret.put("async", true);
		return ret;
	}

	private static String removeThreeQuotes(String line) {
		return line.replaceAll("\"{3,}", "\"\"");
	}

	private static List<String> cutLine(String line, int width, String indent) {
		if (indent.length() >= width)
			throw new IllegalStateException("Indent width is larger than " + width);
		line = line.trim();
		List<String> ret = new ArrayList<String>();
		String curLine = line;
		boolean firstLine = true;
		while (curLine.length() > 0) {
			int curWidth = firstLine ? width : (width - indent.length());
			int pos = curLine.length() <= curWidth ? curLine.length() :
				curLine.substring(0, curWidth).lastIndexOf(' ');
			if (pos < 0)
				pos = curWidth;
			ret.add((firstLine ? "" : indent) + curLine.substring(0, pos));
			curLine = curLine.substring(pos).trim();
			firstLine = false;
		}
		return ret;
	}

	private static void processArgDoc(LinkedList<Tuple2<String, KbType>> typeQueue, 
			List<String> docLines, Set<String> allKeys, boolean topLevel) {
		if (allKeys == null)
			allKeys = new HashSet<String>();
		List<String> additional = new ArrayList<>();
		LinkedList<Tuple2<String, KbType>> subQueue = new LinkedList<Tuple2<String, KbType>>();
		while (!typeQueue.isEmpty()) {
			Tuple2<String, KbType> namedType = typeQueue.removeFirst();
			String key = namedType.getE1();
			if (allKeys.contains(key))
				continue;
			allKeys.add(key);
			KbType type = namedType.getE2();
			additional.clear();
			String argLine = key + " is " + 
					Utils.getEnglishTypeDescr(type, subQueue, allKeys, additional);
			if (additional.size() > 0)
				argLine += ":";
			docLines.add(argLine);
			for (String add : additional)
				if (add.isEmpty()) {
					docLines.add("");
				} else {
					docLines.add("\t" + add);
				}
			if (subQueue.size() > 0 && !topLevel) {
				processArgDoc(subQueue, docLines, allKeys, false);
				if (subQueue.size() > 0)
					throw new IllegalStateException("Not empty: " + subQueue);
			}
		}
		if (subQueue.size() > 0)
			processArgDoc(subQueue, docLines, allKeys, false);
	}

	private static List<String> getNameList(List<KbParameter> args, boolean returned) {
		List<String> ret = new ArrayList<String>();
		for (int i = 0; i < args.size(); i++) {
			KbParameter arg = args.get(i);
			String item = arg.getOriginalName();
			if (item == null) {
				if (returned) {
					item = "return" + (args.size() > 1 ? ("_" + (i + 1)) : "");
				} else {
					KbType type = arg.getType();
					if (type instanceof KbTypedef) {
						item = ((KbTypedef)type).getName();
					} else {
						item = "arg_" + (i + 1);
					}
				}
			}
			ret.add(item);
		}
		Map<String, int[]> valToCount = new HashMap<String, int[]>();
		for (String val : ret) {
			int[] count = valToCount.get(val);
			if (count == null) {
				valToCount.put(val, new int[] {1, 0});
			} else {
				count[0]++;
			}
		}
		for (int pos = 0; pos < ret.size(); pos++) {
			String val = ret.get(pos);
			int[] count = valToCount.get(val);
			if (count[0] > 1) {
				val += "_" + (++count[1]);
				ret.set(pos, val);
			}
		}
		return ret;
	}

	private static String getNames(List<String> items, String prefix) {
		StringBuilder ret = new StringBuilder();
		for (String arg : items) {
			if (ret.length() > 0)
				ret.append(", ");
			if (prefix != null)
				ret.append(prefix);
			ret.append(arg);
		}
		return ret.toString();
	}

	private static String getTypeDescr(KbType type, String paramName) {
		StringBuilder sb = new StringBuilder();
		if (paramName == null)
			sb.append("instance of ");
		createTypeDescr(type, paramName, sb);
		return sb.toString();
	}

	private static void createTypeDescr(KbType type, String paramName, StringBuilder sb) {
		if (paramName != null)
			sb.append("parameter \"").append(paramName).append("\" of ");
		if (type instanceof KbTypedef) {
			KbTypedef ref = (KbTypedef)type;
			sb.append("type \"").append(ref.getName()).append("\"");
			List<String> refCommentLines = Utils.parseCommentLines(ref.getComment());
			if (refCommentLines.size() > 0) {
				StringBuilder concatLines = new StringBuilder();
				for (String l : refCommentLines) {
					if (concatLines.length() > 0 && concatLines.charAt(concatLines.length() - 1) != ' ')
						concatLines.append(' ');
					concatLines.append(l.trim());
				}
				sb.append(" (").append(concatLines).append(")");
			}
			if (!(ref.getAliasType() instanceof KbScalar)) {
				sb.append(" -> ");
				createTypeDescr(ref.getAliasType(), null, sb);
			}
		} else if (type instanceof KbStruct) {
			KbStruct struct = (KbStruct)type;
			sb.append("structure: ");
			for (int i = 0; i < struct.getItems().size(); i++) {
				if (i > 0)
					sb.append(", ");
				String itemName = struct.getItems().get(i).getName();
				createTypeDescr(struct.getItems().get(i).getItemType(), itemName, sb);        
			}
		} else if (type instanceof KbList) {
			KbList list = (KbList)type;
			sb.append("list of ");
			createTypeDescr(list.getElementType(), null, sb);           
		} else if (type instanceof KbMapping) {
			KbMapping map = (KbMapping)type;
			sb.append("mapping from ");
			createTypeDescr(map.getKeyType(), null, sb);
			sb.append(" to ");
			createTypeDescr(map.getValueType(), null, sb);
		} else if (type instanceof KbTuple) {
			KbTuple tuple = (KbTuple)type;
			sb.append("tuple of size ").append(tuple.getElementTypes().size()).append(": ");
			for (int i = 0; i < tuple.getElementTypes().size(); i++) {
				if (i > 0)
					sb.append(", ");
				String tupleParamName = tuple.getElementNames().get(i);
				if (tupleParamName.equals("e_" + (i + 1)))
					tupleParamName = null;
				createTypeDescr(tuple.getElementTypes().get(i), tupleParamName, sb);        
			}
		} else if (type instanceof KbUnspecifiedObject) {
			sb.append("unspecified object");
		} else if (type instanceof KbScalar) {
			sb.append(((KbScalar)type).getJavaStyleName());
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("KbFuncdef [name=");
		builder.append(name);
		builder.append(", async=");
		builder.append(async);
		builder.append(", authentication=");
		builder.append(authentication);
		builder.append(", comment=");
		builder.append(comment);
		builder.append(", parameters=");
		builder.append(parameters);
		builder.append(", returnType=");
		builder.append(returnType);
		builder.append(", annotations=");
		builder.append(annotations);
		builder.append(", data=");
		builder.append(data);
		builder.append("]");
		return builder.toString();
	}
}
