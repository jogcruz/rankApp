package jjs.common.adtclasses.exceptions;

@SuppressWarnings("serial")
public class OperationFailedException extends ServiceException {

	/**
	 * Creates a OperationFailedException with the specified detail message.
	 * 
	 * @param message
	 *            the detail message.
	 */
	public OperationFailedException(String message) {
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
	public OperationFailedException(String friendlymessage, String message) {
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
	public OperationFailedException(String friendlymessage, Exception e) {
		super(friendlymessage, e);
	}

}
