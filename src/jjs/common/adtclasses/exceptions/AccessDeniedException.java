package jjs.common.adtclasses.exceptions;

@SuppressWarnings("serial")
public class AccessDeniedException extends ServiceException {

	/**
	 * Creates a OperationFailedException with the specified detail message.
	 * 
	 * @param message
	 *            the detail message.
	 */
	public AccessDeniedException(String message) {
		super(message);
	}

	/**
	 * Creates a OURException with the specified detail message.
	 * 
	 * @param message
	 *            the detail message.
	 * @param friendlymessage
	 *            the friendly detail message.
	 */
	public AccessDeniedException(String friendlymessage, String message) {
		super(friendlymessage, message);
	}

	/**
	 * Creates a OURException with the specified detail message.
	 * 
	 * @param message
	 *            the detail message.
	 * @param friendlymessage
	 *            the friendly detail message.
	 */
	public AccessDeniedException(String friendlymessage, Exception e) {
		super(friendlymessage, e);
	}

}
