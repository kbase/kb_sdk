package us.kbase.mobu.initializer;

public class ModuleInitializer {

	public static int initialize(String moduleName, boolean verbose) {
		if (moduleName == null) {
			throw new RuntimeException("Unable to create a null directory!");
		}
		if (verbose) System.out.println("Initializing module \"" + moduleName + "\"");
		
		// 1. 
		
		return 0;
	}
}
