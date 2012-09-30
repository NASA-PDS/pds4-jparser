package gov.nasa.pds.objectAccess;

/**
 * Encapsulates an exception the occurred while parsing an XML
 * label. Users of this class don't need to catch exceptions
 * from the underlying parsing library.
 */
public class ParseException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new instance with a given message and underlying
	 * cause.
	 * 
	 * @param message the exception message
	 * @param cause the underlying cause
	 */
	public ParseException(String message, Throwable cause) {
		super(message, cause);
	}

}
