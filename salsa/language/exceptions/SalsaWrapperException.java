package salsa.language.exceptions;

/**
	@author stepha
 */
public class SalsaWrapperException extends SalsaException {

	private Throwable enclosed;

	/**
	 * Constructor for SalsaWrapperException.
	 * @param message
	 */
	public SalsaWrapperException(String message, Throwable wrapped) {
		
		super(message + "Enclosed Exception: " + wrapped );
		
		enclosed = wrapped;
	}

	/**
	 * Returns the enclosed Throwable.
	 * @return Throwable
	 */
	public Throwable getEnclosed() {
		return enclosed;
	}

}
