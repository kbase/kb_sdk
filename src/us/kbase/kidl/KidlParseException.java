package us.kbase.kidl;

public class KidlParseException extends Exception {

	private static final long serialVersionUID = -7697707902617135728L;

	public KidlParseException(String message) {
		super(message);
	}

	public KidlParseException(String message, Throwable e) {
		super(message,e);
	}
}
