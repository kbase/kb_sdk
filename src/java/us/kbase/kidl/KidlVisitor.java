package us.kbase.kidl;

import java.util.List;
import java.util.Map;

//TODO unit tests with test visitor

public interface KidlVisitor<T> {

	public T visit(KbAuthdef authdef);
	
	public T visit(KbFuncdef funcdef, List<T> params, List<T> returns);
	
	public T visit(KbList list, T elementType);
	
	public T visit(KbMapping map, T keyType, T valueType);
	
	public T visit(KbModule module, List<T> components,
			Map<String, T> typeMap);
	
	public T visit(KbParameter param, T type);
	
	public T visit(KbScalar scalar);
	
	public T visit(KbStruct struct, List<T> fields);
	
	public T visit(KbStructItem field, T type);
	
	public T visit(KbTuple tuple, List<T> elementTypes);
	
	public T visit(KbTypedef typedef, KidlNode parent, T aliasType);
	
	public T visit(KbUnspecifiedObject obj);
}
