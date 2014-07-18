package jjs.common.adtclasses.classes;

import jjs.common.adtclasses.interfaces.IBaseResult;

@SuppressWarnings("serial")
public class BaseResultDTO implements IBaseResult {

	private String statuscode 		= "0";
	private String statusmessage	= "";

	public String getStatuscode() {
		return statuscode;
	}

	public void setStatuscode(String statuscode) {
		this.statuscode = statuscode;
	}

	public String getStatusmessage() {
		return statusmessage;
	}

	public void setStatusmessage(String statusmessage) {
		this.statusmessage = statusmessage;
	}

}
