package us.kbase.kidl;

/**
 * Predefined of artificial type. This could be either KbBasicType successor (unnamed type) 
 * or KbTypedef (named type). 
 * @author rsutormin
 */
public interface KbType extends KidlNode {
	/**
	 * Method creates structure to save parsing structure of type in JSON schema.
	 * @param inner is important for typedefs, defines if this typedef of top level or not.
	 */
	public Object toJsonSchema(boolean inner);
	
	/**
	 * Method is invoked from parser after type is built and it's time to check internal integrity.
	 */
	public void afterCreation();
	
	public String getSpecName();
}
