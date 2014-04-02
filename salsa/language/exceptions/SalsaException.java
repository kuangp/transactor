package salsa.language.exceptions;

/**
 	SalsaExceptions are thrown when an error occurs within the salsa.language package.
 	
	@author stepha
 */
public class SalsaException extends RuntimeException {

	/**
	 * Constructor for SalsaException.
	 */
	public SalsaException() {
		super();
	}

	/**
	 * Constructor for SalsaException.
	 * @param message
	 */
	public SalsaException(String message) {
		super( "Exception from inside salsa.language: " + message);
	}

}
