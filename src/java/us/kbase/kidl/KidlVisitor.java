package us.kbase.kidl;

import java.util.List;
import java.util.Map;

//TODO unit tests with test visitor

public interface KidlVisitor<T> {

	public T visit(KbAuthdef auth);
	
	public T visit(KbFuncdef func, List<T> params, List<T> returns);
	
	public T visit(KbList list, T elementType);
	
	public T visit(KbMapping map, T keyType, T valueType);
	
	public T visit(KbModule module, List<T> components,
			Map<String, T> typeMap);
	
	public T visit(KbParameter param, T type);
	
	public T visit(KbScalar scalar);
	
	public T visit(KbStruct struct, List<T> fields);
	
	public T visit(KbStructItem field, T type);
	
	public T visit(KbTuple tuple, List<T> elementTypes);
	
	public T visit(KbTypedef typedef, T aliasType);
	
	public T visit(KbUnspecifiedObject obj);
}
