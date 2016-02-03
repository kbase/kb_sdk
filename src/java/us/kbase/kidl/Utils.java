package us.kbase.kidl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import us.kbase.common.service.Tuple2;

public class Utils {

	public static String prop(Map<?,?> map, String propName) throws KidlParseException {
		return propAbstract(map, propName, String.class);
	}

	public static String propOrNull(Map<?,?> map, String propName) throws KidlParseException {
		if (map.get(propName) == null)
			return null;
		return prop(map, propName);
	}

	public static List<?> propList(Map<?,?> map, String propName) throws KidlParseException {
		return propAbstract(map, propName, List.class);
	}

	public static Map<?,?> propMap(Map<?,?> map, String propName) throws KidlParseException {
		return propAbstract(map, propName, Map.class);
	}
	
	private static <T> T propAbstract(Map<?,?> map, String propName, Class<T> returnType) throws KidlParseException {
		if (!map.containsKey(propName))
			throw new KidlParseException("No property in the map: " + propName);
		Object ret = map.get(propName);
		if (ret == null)
			throw new KidlParseException("No property in the map: " + propName);
		if (returnType != null && !returnType.isInstance(ret))
			throw new KidlParseException("Value for property [" + propName + "] is not compatible " +
					"with type [" + returnType.getName() + "], it has type: " + ret.getClass().getName());
		return (T)ret;
	}

	public static List<String> repareTypingString(List<?> list) throws KidlParseException {
		return repareTypingAbstract(list, String.class);
	}

	public static List<Map> repareTypingMap(List<?> list) throws KidlParseException {
		return repareTypingAbstract(list, Map.class);
	}

	private static <T> List<T> repareTypingAbstract(List<?> list, Class<T> itemType) throws KidlParseException {
		List<T> ret = new ArrayList<T>();
		for (Object item : list) {
			if (!itemType.isInstance(item))
				throw new KidlParseException("List item is not compatible with type " +
						"[" + itemType.getName() + "], it has type: " + item.getClass().getName());
			ret.add((T)item);
		}
		return ret;
	}

	public static List<Map> getListOfMapProp(Map<?,?> data, String propName) throws KidlParseException {
		return Utils.repareTypingMap(Utils.propList(data, propName));
	}
	
	public static String getPerlSimpleType(Map<?,?> map) throws KidlParseException {
		return getPerlSimpleType(prop(map, "!"));
	}

	public static String getPerlSimpleType(String type) {
		return type.contains("::") ? type.substring(type.lastIndexOf("::") + 2) : type;
	}

	public static KbType createTypeFromMap(Map<?,?> data) throws KidlParseException {
		return createTypeFromMap(data, null);
	}
	
	public static KbType createTypeFromMap(Map<?,?> data, KbAnnotations annFromTypeDef) throws KidlParseException {
		String typeType = Utils.getPerlSimpleType(data);
		KbType ret = typeType.equals("Typedef") ? new KbTypedef().loadFromMap(data) :
			KbBasicType.createFromMap(data, annFromTypeDef);
		return ret;
	}
	
	public static int intPropFromString(Map<?,?> map, String propName) throws KidlParseException {
		String value = prop(map, propName);
		try {
			return Integer.parseInt(value);
		} catch(Exception ex) {
			throw new KidlParseException("Value for property [" + propName + "] is not integer: " + value);
		}
	}

	public  static KbType resolveTypedefs(KbType type) {
		if (type instanceof KbTypedef) 
			return resolveTypedefs(((KbTypedef)type).getAliasType());
		return type;
	}
	
    public static String getEnglishTypeDescr(
            KbType type, LinkedList<Tuple2<String, KbType>> typeQueue, 
            Set<String> allKeys, List<String> additional) {
        return getEnglishTypeDescr(type, typeQueue, allKeys, additional, 0);
    }
    
    private static String getEnglishTypeDescr(
            KbType type, LinkedList<Tuple2<String, KbType>> typeQueue, 
            Set<String> allKeys, List<String> additional, int nested) {
        String descr = null;
        if (type instanceof KbScalar) {
            KbScalar sc = (KbScalar)type;
            descr = sc.getSpecName();
        } else if (type instanceof KbTypedef) {
            KbTypedef td = (KbTypedef)type;
            descr = td.getModule() + "." + td.getName();
            if (!allKeys.contains(td.getName())) {
                typeQueue.add(new Tuple2<String, KbType>().withE1(td.getName()).withE2(td.getAliasType()));
            }
        } else if (type instanceof KbList) {
            KbList ls = (KbList)type;
            descr = "reference to a list where each element is " + 
                    getEnglishTypeDescr(ls.getElementType(), typeQueue, allKeys, additional, nested);
        } else if (type instanceof KbMapping) {
            KbMapping mp = (KbMapping)type;
            descr = "reference to a hash where the key is " + 
                    getEnglishTypeDescr(mp.getKeyType(), typeQueue, allKeys, additional, nested) + 
                    " and the value is " + 
                    getEnglishTypeDescr(mp.getValueType(), typeQueue, allKeys, additional, nested);
        } else if (type instanceof KbTuple) {
            KbTuple tp = (KbTuple)type;
            int count = tp.getElementTypes().size();
            descr = "reference to a list containing " + count + " item" + (count == 1 ? "" : "s"); 
            if (additional != null) {
                for (int i = 0; i < count; i++) {
                    String tpName = tp.getElementNames().get(i);
                    String defName = "e_" + (i + 1);
                    KbType tpType = tp.getElementTypes().get(i);
                    List<String> nestedAdds = new ArrayList<String>();
                    String text = i + ":" + (tpName != null && !tpName.equals(defName) ? (" (" + tpName + ")") : "") + 
                            " " + getEnglishTypeDescr(tpType, typeQueue, allKeys, nestedAdds, nested + 1);
                    addNested(additional, nestedAdds, text, nested + 1);
                }
            }
        } else if (type instanceof KbStruct) {
            KbStruct st = (KbStruct)type;
            descr = "reference to a hash where the following keys are defined";
            if (additional != null) {
                for (KbStructItem item : st.getItems()) {
                    String itName = item.getName();
                    KbType itType = item.getItemType();
                    List<String> nestedAdds = new ArrayList<String>();
                    String text = itName + " has a value which is " + 
                            getEnglishTypeDescr(itType, typeQueue, allKeys, nestedAdds, nested + 1);
                    addNested(additional, nestedAdds, text, nested + 1);
                }
            }
        } else if (type instanceof KbUnspecifiedObject) {
            descr = "UnspecifiedObject, which can hold any non-null object";
        } else {
            descr = type.toString();
        }
        if (descr.length() > 0) {
            char firstLetter = descr.charAt(0);
            descr = "a" + (isVowel(firstLetter) ? "n" : "") + " " + descr;
        }
        return descr;
    }

    private static void addNested(List<String> additional,
            List<String> nestedAdds, String text, int nested) {
        if (nestedAdds.size() > 0)
            text += ":";
        additional.add(text);
        if (nestedAdds.size() > 0) {
            String nestedFlank = getNestedFlank(nested);
            for (String add : nestedAdds)
                additional.add(nestedFlank + add);
            additional.add("");
        }
    }
    
    private static String getNestedFlank(int nested) {
        char[] ret = new char[nested];
        Arrays.fill(ret, '\t');
        return new String(ret);
    }
    
    public static boolean isVowel(char c) {
        return "AEIOUaeiou".indexOf(c) != -1;
    }
    
    public static String removeStarsInComment(String comment) {
        int base = 0;
        BufferedReader br = new BufferedReader(new StringReader(comment));
        StringBuilder sb = new StringBuilder();
        try {
            for (int lineNum = 0;; lineNum++) {
                String l = br.readLine();
                if (l == null)
                    break;
                int starPos = l.indexOf('*');
                if (starPos >= 0 && l.substring(0, starPos).trim().length() == 0 &&
                        (l.length() == starPos + 1 || l.charAt(starPos + 1) != '*'))
                    l = l.substring(starPos + 1);
                if (lineNum == 1)
                    while (base < l.length() && l.charAt(base) == ' ')
                        base++;
                if (l.length() >= base && l.substring(0, base).trim().length() == 0)
                    l = l.substring(base);
                sb.append(l).append('\n');
            }
            br.close();
        } catch (IOException ex) {
            throw new IllegalStateException("Unexpected error", ex);
        }
        us.kbase.jkidl.Utils.trimWhitespaces(sb);
        return sb.toString();
    }
}
