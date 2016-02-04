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
import java.util.TreeMap;

import us.kbase.common.service.Tuple2;

/**
 * Class represents function definition in spec-file.
 */
public class KbFuncdef implements KbModuleComp {
	private String name;
	private boolean async;
	private String authentication;
	private String comment;
	private List<KbParameter> parameters;
	private List<KbParameter> returnType;
	private Map<?,?> data = null;
	
	public KbFuncdef() {}

	public KbFuncdef(String name, String comment) {
	    this(name, comment, false);
	}
	
	public KbFuncdef(String name, String comment, boolean async) {
		this.name = name;
		this.async = async;
		this.comment = comment == null ? "" : comment;
		parameters = new ArrayList<KbParameter>();
		returnType = new ArrayList<KbParameter>();
	}

	public KbFuncdef loadFromMap(Map<?,?> data, String defaultAuth) throws KidlParseException {
		name = Utils.prop(data, "name");
		async = (0 != Utils.intPropFromString(data, "async"));
		authentication = Utils.prop(data, "authentication");  // defaultAuth was already involved on kidl stage
		comment = Utils.prop(data, "comment");
		parameters = loadParameters(Utils.propList(data, "parameters"), false);
		returnType = loadParameters(Utils.propList(data, "return_type"), true);
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
	
	private List<Object> toJson(List<KbParameter> list) {
		List<Object> ret = new ArrayList<Object>();
		for (KbParameter param : list)
			ret.add(param.toJson());
		return ret;
	}
	
	@Override
	public Object toJson() {
		Map<String, Object> ret = new TreeMap<String, Object>();
		ret.put("!", "Bio::KBase::KIDL::KBT::Funcdef");
		ret.put("annotations", new KbAnnotations().toJson(false));
		ret.put("async", async ? "1" : "0");
		ret.put("authentication", authentication);
		ret.put("comment", comment);
		ret.put("name", name);
		ret.put("parameters", toJson(parameters));
		ret.put("return_type", toJson(returnType));
		return ret;
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
        ret.put("doc", Utils.removeStarsInComment(comment));
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
}
