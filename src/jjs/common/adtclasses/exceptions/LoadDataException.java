package jjs.common.adtclasses.exceptions;

@SuppressWarnings("serial")
public class LoadDataException extends ServiceException {

	private String file;
	private long number;
	private String ordertype;
	private Long clientid;
	
	
	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public long getNumber() {
		return number;
	}

	public void setNumber(long number) {
		this.number = number;
	}

	public String getOrdertype() {
		return ordertype;
	}

	public void setOrdertype(String ordertype) {
		this.ordertype = ordertype;
	}

	public Long getClientid() {
		return clientid;
	}

	public void setClientid(Long clientid) {
		this.clientid = clientid;
	}

	/**
	 * Creates a OperationFailedException with the specified detail message.
	 * 
	 * @param message
	 *            the detail message.
	 */
	public LoadDataException(String message) {
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
	public LoadDataException(String friendlymessage, String message) {
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
	public LoadDataException(String friendlymessage, Exception e) {
		super(friendlymessage, e);
	}

}
