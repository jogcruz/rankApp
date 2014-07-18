package jjs.common.facade;


import javax.persistence.EntityManager;

import jjs.common.adtclasses.interfaces.IBaseReportServer;

import org.apache.log4j.Logger;

public abstract class BaseReportServer implements IBaseReportServer {

	protected EntityManager em;

	protected Logger log = Logger.getLogger(this.getClass());

	public abstract void setEntityManager(EntityManager em);

	protected EntityManager getEntityManager() {
		return em;
	}

}
