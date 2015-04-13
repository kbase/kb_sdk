package us.kbase.kidl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Class represents kind of comment annotation called 'searchable'.
 */
public class KbAnnotationSearch {
	private Subset fields = new Subset();
	private Subset keys = new Subset();
	
	public KbAnnotationSearch() {}
	
	@SuppressWarnings("unchecked")
	void loadFromMap(Map<String, Object> data) {
		Map<String, Object> map1 = (Map<String, Object>)data.get("fields");
		if (map1 != null)
			fields.loadFromMap(map1);
		Map<String, Object> map2 = (Map<String, Object>)data.get("keys");
		if (map2 != null)
			keys.loadFromMap(map2);
	}
	
	KbAnnotationSearch loadFromComment(List<String> words, KbTypedef caller) throws KidlParseException {
		if (words.size() == 0)
			throw new KidlParseException("Searchable annotations without type are not supported");
		if (!words.get(0).equals("ws_subset"))
			throw new KidlParseException("Searchable annotations of type " + words.get(0) + " are not supported");
		KbType type = resolveTypedefs(caller);
		if (!(type instanceof KbStruct))
			throw new KidlParseException("Searchable annotation should be used only for structures");
		words = new ArrayList<String>(words.subList(1, words.size()));
		for (int i = 1; i < words.size();) {
			String w1 = words.get(i - 1);
			String w2 = words.get(i);
			boolean glue = (w1.equals("keys_of") && w2.startsWith("(")) || w1.endsWith("(") || 
					w2.equals(")") || w1.endsWith(".") || w1.endsWith(",") || w2.startsWith(".") || 
					w2.startsWith(",");
			if (glue) {
				String w = words.remove(i);
				words.set(i - 1, words.get(i - 1) + w);
			} else {
				i++;
			}
		}
		for (String word : words) {
			if (word.startsWith("keys_of(") && word.endsWith(")")) {
				keys.loadFromComment("", word.substring(8, word.length() - 1), type, true);
			} else {
				fields.loadFromComment("", word, type, false);
			}
		}
		return this;
	}
	
	public Object toJson() {
		Map<String, Object> ret = new TreeMap<String, Object>();
		if (fields != null)
			ret.put("fields", fields.toJson());
		if (keys != null)
			ret.put("keys", keys.toJson());
		return ret;
	}

	public Object toJsonSchema() {
		Map<String, Object> ret = new TreeMap<String, Object>();
		if (fields != null)
			ret.put("fields", fields.toJsonSchema());
		if (keys != null)
			ret.put("keys", keys.toJsonSchema());
		return ret;
	}

	private static KbType resolveTypedefs(KbType type) {
		if (type instanceof KbTypedef) 
			return resolveTypedefs(((KbTypedef)type).getAliasType());
		return type;
	}
	
	@SuppressWarnings("serial")
	public static class Subset extends TreeMap<String, Subset> {
		
		@SuppressWarnings("unchecked")
		public Subset loadFromMap(Map<String, Object> data) {
			for (Map.Entry<String, Object> entry : data.entrySet()) {
				Subset value = new Subset();
				value.loadFromMap((Map<String, Object>)entry.getValue());
				put(entry.getKey(), value);
			}
			return this;
		}
		
		public Object toJson() {
			Map<String, Object> ret = new TreeMap<String, Object>();
			for (Map.Entry<String, Subset> entry : entrySet()) {
				ret.put(entry.getKey(), entry.getValue().toJson());
			}
			return ret;
		}

		public Object toJsonSchema() {
			return toJson();
		}

		void loadFromComment(String historyPrefix, String text, KbType type, boolean keys) throws KidlParseException {
			List<String> commaParts = new ArrayList<String>();
			while (true) {
				int commaPos = indexOfRoundBrackets(text, ',');
				if (commaPos > 0) {
					commaParts.add(text.substring(0, commaPos));
					text = text.substring(commaPos + 1);
				} else {
					commaParts.add(text);
					break;
				}
			}
			for (String commaPart : commaParts) {
				int dotPos = indexOfRoundBrackets(commaPart, '.');
				String key = dotPos > 0 ? commaPart.substring(0, dotPos) : commaPart;
				KbType itemType = null;
				if (key.equals("*")) {
					checkSubsetType(historyPrefix, key, KbMapping.class, type);
					itemType = resolveTypedefs(((KbMapping)type).getValueType());
				} else if (key.equals("[*]")) {
					checkSubsetType(historyPrefix, key, KbList.class, type);
					itemType = resolveTypedefs(((KbList)type).getElementType());
				} else {
					checkSubsetType(historyPrefix, key, KbStruct.class, type);
					for (KbStructItem item : ((KbStruct)type).getItems()) {
						if (item.getName().equals(key)) {
							itemType = resolveTypedefs(item.getItemType());
							break;
						}
					}
					if (itemType == null)
						throw new KidlParseException("Can not match path " + historyPrefix + key +
								" in searchable annotation to any field in actual structure");
				}
				Subset value = get(key);
				if (value == null)
					value = new Subset();
				if (dotPos > 0) {
					String leftPart = commaPart.substring(dotPos + 1);
					if (leftPart.startsWith("(") && leftPart.endsWith(")"))
						leftPart = leftPart.substring(1, leftPart.length() - 1);
					value.loadFromComment(historyPrefix + key + ".", leftPart, itemType, keys);
				} else {
					if (keys) {
						checkSubsetType(historyPrefix, key, KbMapping.class, itemType);
					}
				}
				put(key, value);
			}
		}

		private void checkSubsetType(String historyPrefix, String key,
				Class<? extends KbType> expectedType, KbType actualType) throws KidlParseException {
			if (!(actualType.getClass().equals(expectedType)))
				throw new KidlParseException("Can not match path " + historyPrefix + key + 
						" in searchable annotation to a " + getTypeSimpleName(expectedType) + 
						", actual type is " + getTypeSimpleName(actualType.getClass()));
		}
				
		private String getTypeSimpleName(Class<? extends KbType> type) {
			if (type.equals(KbScalar.class)) {
				return "scalar";
			} else if (type.equals(KbList.class)) {
				return "list";
			} else if (type.equals(KbMapping.class)) {
				return "mapping";
			} else if (type.equals(KbTuple.class)) {
				return "tuple";
			} else if (type.equals(KbStruct.class)) {
				return "structure";
			} else if (type.equals(KbUnspecifiedObject.class)) {
				return "UnspecifiedObject";
			} else if (type.equals(KbTypedef.class)) {
				return "typedef";
			} else {
				return type.getSimpleName();
			}
		}
		
		private int indexOfRoundBrackets(String text, char ch) {
			int level = 0;
			for (int pos = 0; pos < text.length(); pos++) {
				if (text.charAt(pos) == '(') {
					level++;
				} else if (text.charAt(pos) == ')') {
					level--;
				} else if (level == 0 && text.charAt(pos) == ch) {
					return pos;
				}
			}
			return -1;
		}
	}
}
