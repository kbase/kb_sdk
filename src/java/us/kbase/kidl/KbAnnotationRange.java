package us.kbase.kidl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

/**
 * Class represents kind of comment annotation called 'range'.
 */
public class KbAnnotationRange {
	
	BigDecimal minValue;
	BigDecimal maxValue;
	boolean isExclusiveMin;
	boolean isExclusiveMax;
	
	KbAnnotationRange() {
		minValue = null;
		maxValue = null;
		isExclusiveMin = false;
		isExclusiveMax = false;
	}
	
	/**
	 * the range is defined in the standard way (see: http://en.wikipedia.org/wiki/ISO_31-11):
	 * [min,max] where brackets indicate inclusive, or (min,max) where parentheses indicate
	 * exclusive, or where brackets in the outer direction also indicate exclusive as ]min,max[.
	 * 
	 * If brackets are left out completely, inclusive range is assumed.
	 * 
	 * If the min or max value is ommited, then no min or max value is assumed.
	 * 
	 */
	void parseRangeString(String range) throws KidlParseException {
		String rangeStr = range.trim();
		isExclusiveMin = false;
		if(rangeStr.startsWith("[")) {
			rangeStr = rangeStr.substring(1);
		} else if(rangeStr.startsWith("(") || rangeStr.startsWith("]")) {
			isExclusiveMin = true;
			rangeStr = rangeStr.substring(1);
		}

		isExclusiveMax = false;
		if(rangeStr.endsWith("]")) {
			rangeStr = rangeStr.substring(0, rangeStr.length()-1);
		} else if(rangeStr.endsWith(")") || rangeStr.endsWith("[")) {
			isExclusiveMax = true;
			rangeStr = rangeStr.substring(0, rangeStr.length()-1);
		}
		String [] values = rangeStr.split(",");
		if(values.length>=1) {
			minValue = null; maxValue = null;
			if(!values[0].trim().isEmpty()) {
				try {
					minValue = new BigDecimal(values[0].trim());
				} catch (Exception e) {
					throw new KidlParseException("Error in specifying the valid range of a number, invalid minimum value: "+e.getMessage());
				}
			}
		}
		if(values.length==2) {
			if(!values[1].trim().isEmpty()) {
				try {
					maxValue = new BigDecimal(values[1].trim());
				} catch (Exception e) {
					throw new KidlParseException("Error in specifying the valid range of a number, invalid minimum value: "+e.getMessage());
				}
			}
		}
		if(values.length > 2) {
			throw new KidlParseException("Error in specifying the valid range of a number, too many commas given");
		}
		
		if(minValue!=null && maxValue!=null) {
			if(minValue.compareTo(maxValue) > 0) {
				throw new KidlParseException("Error in specifying a valid range, min value was greater than max value.");
			}
		}
	}
	
	
	void loadFromComment(List<String> words) throws KidlParseException {
		if (words.size() == 0)
			throw new KidlParseException("range annotation without specifying a range is invalid");
		StringBuilder concatRange = new StringBuilder();
		for(String w:words) {
			concatRange.append(w);
		}
		parseRangeString(concatRange.toString());
	}
	
	void loadFromMap(Map<String,Object> data) throws KidlParseException {
		Iterator<Entry<String, Object>> it = data.entrySet().iterator();
		isExclusiveMin = false;
		isExclusiveMax = false;
		while (it.hasNext()) {
			Entry<String, Object> pairs = it.next();
			String key = pairs.getKey();
			if(key.equals("maximum")) {
				maxValue = new BigDecimal(pairs.getValue().toString());
			} else if(key.equals("minimum")) {
				minValue = new BigDecimal(pairs.getValue().toString());
			} else if(key.equals("exclusiveMaximum")) {
				//if((Boolean)pairs.getValue()) {
				//	isExclusiveMax = true;
				//}
			}else if(key.equals("exclusiveMinimum")) {
				//if((Boolean)pairs.getValue()) {
				//	isExclusiveMin = true;
				//}
			}
		}
	}
	
	public boolean isMinSet() {
		return minValue!=null;
	}
	public boolean isMaxSet() {
		return maxValue!=null;
	}
	
	Object toJson() {
		Map<String, Object> ret = new TreeMap<String, Object>();
		if(isMinSet()) {
			ret.put("minimum", minValue.toString());
			if(isExclusiveMin) {
				ret.put("exclusiveMinimum","1");
			} else {
				ret.put("exclusiveMinimum","0");
			}
		}
		if(isMaxSet()) {
			ret.put("maximum", maxValue.toString());
			if(isExclusiveMax) {
				ret.put("exclusiveMaximum","1");
			} else {
				ret.put("exclusiveMaximum","0");
			}
		}
		return ret;
	}
	
	Object toJsonSchemaForFloat() {
		Map<String, Object> rangeMap = new TreeMap<String, Object>();
		if(isMinSet()) {
			rangeMap.put("minimum", minValue);
			if(isExclusiveMin) {
				rangeMap.put("exclusiveMinimum",new Boolean(true));
			}
		}
		if(isMaxSet()) {
			rangeMap.put("maximum", maxValue);
			if(isExclusiveMax) {
				rangeMap.put("exclusiveMaximum",new Boolean(true));
			}
		}
		return rangeMap;
	}
	
	Object toJsonSchemaForInt() {
		Map<String, Object> rangeMap = new TreeMap<String, Object>();
		if(isMinSet()) {
			BigDecimal rounded = minValue.setScale(0,RoundingMode.CEILING);
			rangeMap.put("minimum", rounded );
			if(rounded.compareTo(minValue)==0) {
				if(isExclusiveMin) {
					rangeMap.put("exclusiveMinimum",new Boolean(true));
				}
			}
		}
		if(isMaxSet()) {
			// always round towards negative infinity, and discard the 'exclusive' flag
			// unless we did not round (in which case an integer value was provided, so we are ok)
			BigDecimal rounded =  maxValue.setScale(0,RoundingMode.FLOOR);
			rangeMap.put("maximum", rounded);
			if(rounded.compareTo(maxValue)==0) {
				if(isExclusiveMax) {
					rangeMap.put("exclusiveMaximum",new Boolean(true));
				}
			}
		}
		return rangeMap;
	}
}
