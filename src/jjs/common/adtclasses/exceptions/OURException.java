package jjs.common.adtclasses.exceptions;

@SuppressWarnings("serial")
public class OURException extends Exception {
	/**
	 * Creates a OURException with the specified detail message.
	 * 
	 * @param message
	 *            the detail message.
	 * @param friendlymessage
	 *            the friendly detail message.
	 */
	public OURException(String friendlymessage, String message) {
		super(message);
		this.friendlymessage = friendlymessage;
	}

	private Throwable innerexception = null;

	/**
	 * Creates a OURException with the specified detail message.
	 * 
	 * @param message
	 *            the detail message.
	 * @param friendlymessage
	 *            the friendly detail message.
	 */
	public OURException(String friendlymessage, Throwable e) {
		super(e.getMessage());
		this.innerexception = e;
		this.friendlymessage = friendlymessage;
	}

	/**
	 * Creates a OURException with the specified detail message.
	 * 
	 * @param message
	 *            the detail message.
	 */
	public OURException(String message) {
		super(message);
		this.friendlymessage = message;
	}

	private String friendlymessage = null;

	/**
	 * @return
	 */
	public String getFriendlymessage() {
		return friendlymessage;
	}

	/**
	 * @return
	 */
	public Throwable getInnerexception() {
		return innerexception;
	}

}
