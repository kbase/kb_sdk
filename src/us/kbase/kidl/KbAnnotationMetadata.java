package us.kbase.kidl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import us.kbase.kidl.KbScalar.Type;

/**
 * Class represents kind of comment annotation called 'range'.
 */
public class KbAnnotationMetadata {
	
	/* keys are metadata name, values are the expression that is evaluated to compute the metadata value */
	private Map<String,String> wsMetaDataMap;
	
	public static final String TYPE_WS = "ws";
	
	KbAnnotationMetadata() {
		wsMetaDataMap = new TreeMap<String,String>();
	}
	
	/**
	 * how parse works: 
	 * first token is the metadata 'type', which for now can only be 'ws', this specifies the context for using the metadata
	 * if type is 'ws', then read in every token until we reach 'as', which becomes the expression to select the value of the metadata
	 * every token after 'as' is concatenated with a space and becomes the metadata name.  The name and expression are stored in this
	 * annotation object.
	 */
	void loadFromComment(List<String> words, KbTypedef caller) throws KidlParseException {
		
		KbType callerType = resolveTypedefs(caller);
		if (!(callerType instanceof KbStruct))
			throw new KidlParseException("Searchable annotation should be used only for structures");
		
		if (words.size() == 0)
			throw new KidlParseException("metadata annotation is invalid, must define at least the context for specifying the metadata (e.g. 'ws')");
		
		String type = words.get(0);
		words.remove(0);
		if(type.equals(TYPE_WS)) {
			if (words.size() < 1)
				throw new KidlParseException("metadata annotation is invalid, must define at least 2 tokens: @metadata ws [expression]");
			StringBuilder expression = new StringBuilder();
			StringBuilder metadataName = new StringBuilder();
			boolean seenAsKeyword = false;
			
			boolean isFirstWord = true; // 'as' cannot be the first token parsed (in case there is a field named 'as')
			for(String w:words) {
				if(!isFirstWord && w.toLowerCase().equals("as")) {
					seenAsKeyword = true;
					continue;
				}
				if(isFirstWord) { isFirstWord = false; }
				if(seenAsKeyword) { metadataName.append(w+" "); }
				else { expression.append(w); }
			}
			if(!seenAsKeyword) {
				metadataName.append(expression.toString());
			}
			
			validateExpression(expression.toString(),(KbStruct) callerType);
			
			String name = metadataName.toString().trim();
			if(wsMetaDataMap.containsKey(name)) {
				throw new KidlParseException("metadata annotation is invalid, you cannot redefine a metadata name; attempted to redefine '"+name+"'");
			}
			wsMetaDataMap.put(name,expression.toString());
		} else {
			throw new KidlParseException("metadata annotation is invalid, unsupported meta data type; only valid type is 'ws', you gave: "+ type);
		}
	}
	
	private static KbType resolveTypedefs(KbType type) {
		if (type instanceof KbTypedef) 
			return resolveTypedefs(((KbTypedef)type).getAliasType());
		return type;
	}
	
	/* We only support extraction of paths into structures, so the expression must be a dot
	 * delimited path through structures to get to a valid scalar type (in which case
	 * the type is cast to a string) or a list/mapping in which case it requires the operator
	 * length(field_name) to specify that we want the length of the field name.
	 * 
	 * If we extend this simple expression in the future, it should be validated here.
	 *  */
	void validateExpression(String expression, KbStruct caller) throws KidlParseException {
		List <KbStructItem> items = caller.getItems();
		expression = expression.trim();
		boolean gettingLengthOf = false;
		if(expression.startsWith("length(")) {
			if(expression.endsWith(")")) {
				expression = expression.substring(7);
				expression = expression.substring(0, expression.length()-1);
				gettingLengthOf = true;
			} else {
				throw new KidlParseException("metadata annotation is invalid, expression starts with length(, but does not have closing parenthesis");
			}
		}
		String [] expTokens = expression.split("\\.");

		// returns nothing, but throws an exception if things are not valid.
		validateMetadataPath(expTokens, items, 0, gettingLengthOf, expression);
	}
	
	// validates that path goes only through structures to a valid field
	private void validateMetadataPath(String [] pathFields, List<KbStructItem> items, int pathPosition, boolean gettingLengthOf, String expression) throws KidlParseException {
		String currentField = pathFields[pathPosition];
		boolean foundExpression = false;
		for(KbStructItem i:items) {
			if(i.getName().equals(currentField)) {
				KbType itemType = resolveTypedefs(i.getItemType());
				if(itemType instanceof KbScalar) {
					if(pathPosition+1 != pathFields.length) {
						throw new KidlParseException("metadata annotation is invalid, you are attempting to descend into a field named '"+pathFields[pathPosition+1]+"' in the expression '"+expression+"', but '"+currentField+"' is a string/int/float");
					}
					if(((KbScalar) itemType).getScalarType() != Type.stringType) {
						if(gettingLengthOf) {
							throw new KidlParseException("metadata annotation is invalid, if you are selecting an int or float for metadata, you cannot use: length("+expression+")");
						}
					}
					// we are ok
				} else if(itemType instanceof KbList) {
					if(pathPosition+1 != pathFields.length) {
						throw new KidlParseException("metadata annotation is invalid, you are attempting to descend into a field named '"+pathFields[pathPosition+1]+"' in the expression '"+expression+"', but '"+currentField+"' is a list and you cannot extract metadata from within a list.");
					}
					if(!gettingLengthOf) {
						throw new KidlParseException("metadata annotation is invalid, if you are selecting a list for metadata, you must use: length("+expression+")");
					}
				} else if(itemType instanceof KbMapping) {
					if(pathPosition+1 != pathFields.length) {
						throw new KidlParseException("metadata annotation is invalid, you are attempting to descend into a field named '"+pathFields[pathPosition+1]+"' in the expression '"+expression+"', but '"+currentField+"' is a mapping and you cannot extract metadata from within a mapping.");
					}
					if(!gettingLengthOf) {
						throw new KidlParseException("metadata annotation is invalid, if you are selecting a mapping for metadata, you must use: length("+expression+")");
					}
				} else if(itemType instanceof KbTuple) {
					if(pathPosition+1 != pathFields.length) {
						throw new KidlParseException("metadata annotation is invalid, you are attempting to descend into a field named '"+pathFields[pathPosition+1]+"' in the expression '"+expression+"', but '"+currentField+"' is a tuple and you cannot extract metadata from within a tuple.");
					}
					if(!gettingLengthOf) {
						throw new KidlParseException("metadata annotation is invalid, if you are selecting a tuple for metadata, you must use: length("+expression+")");
					}
				} else if(itemType instanceof KbStruct){
					if(pathPosition+1 < pathFields.length) {
						validateMetadataPath(pathFields, ((KbStruct) itemType).getItems(), pathPosition+1, gettingLengthOf, expression);
					} else {
						throw new KidlParseException("metadata annotation is invalid, you cannot select a structure, and  '"+pathFields[pathPosition+1]+"' in '"+expression+"' is a field that is a structure.");
					}
				} else {
					throw new KidlParseException("metadata annotation is invalid, you can only select fields that are scalars, lists, tuples, or mappings; '"+expression+"' is not one of these.");
				}
				foundExpression=true;
			}
		}
		if(!foundExpression) {
			throw new KidlParseException("metadata annotation is invalid, could not identify field named:'"+currentField+"' in '"+expression+"'");
		}
	}
	
	
	// If the json schema was parsed from the perl type compiler, use this method for instantiation
	void loadFromMap(Map<String,Object> data) throws KidlParseException {
		@SuppressWarnings("unchecked")
		Map<String,Object> info = (Map<String, Object>) data.get("ws");
		if(info==null) return;
		Iterator<Entry<String, Object>> it = info.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Object> pairs = it.next();
			String metadataName = pairs.getKey();
			String expression = (String)pairs.getValue();
			// we do not need to validate, we assume the type compiler has validated the expression already
			//validateExpression(expression,(KbStruct) callerType);
			wsMetaDataMap.put(metadataName,expression.toString());
		}
	}
	
	Object toJson() {
		Map<String, Object> metadataFields = new TreeMap<String, Object>();
		metadataFields.putAll(wsMetaDataMap);
		Map<String, Object> ret = new TreeMap<String, Object>();
		ret.put("ws", metadataFields);
		return ret;
	}
	
	Object toJsonSchema() {
		Map<String, Object> metaMap = new TreeMap<String, Object>();
		metaMap.put("metadata-ws", wsMetaDataMap);
		return metaMap;
	}
	
	
	
}
