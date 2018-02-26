package us.kbase.mobu.initializer;

/** Thrown when a configuration is invalid and the service cannot start.
 * @author gaprice@lbl.gov
 *
 */
public class ModuleInitializerException extends Exception {

	private static final long serialVersionUID = 1L;

	public ModuleInitializerException(final String message) {
		super(message);
	}
	
	public ModuleInitializerException(
			final String message,
			final Throwable cause) {
		super(message, cause);
	}
}
