package jjs.common.adtclasses.exceptions;

@SuppressWarnings("serial")
public class ServiceException extends OURException {
	/**
	 * Creates a OURException with the specified detail message.
	 * 
	 * @param message
	 *            the detail message.
	 * @param friendlymessage
	 *            the friendly detail message.
	 */
	public ServiceException(String friendlymessage, String message) {
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
	public ServiceException(String friendlymessage, Exception e) {
		super(friendlymessage, e);
	}

	/**
	 * Creates a OURException with the specified detail message.
	 * 
	 * @param message
	 *            the detail message.
	 */
	public ServiceException(String message) {
		super(message);
	}

}