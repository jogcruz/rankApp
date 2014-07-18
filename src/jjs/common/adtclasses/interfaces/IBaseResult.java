package jjs.common.adtclasses.interfaces;

import java.io.Serializable;

public interface IBaseResult extends Serializable {

	public abstract String getStatuscode();

	public abstract void setStatuscode(String statuscode);

	public abstract String getStatusmessage();

	public abstract void setStatusmessage(String statusmessage);

}