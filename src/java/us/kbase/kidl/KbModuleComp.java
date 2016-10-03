package us.kbase.kidl;

import java.util.Map;

/**
 * Element of module declaration. This could be Typedef of Funcdef (or Auth string in some cases).
 * @author rsutormin
 */
public interface KbModuleComp extends KidlNode {
	public Map<String, Object> forTemplates();
}
