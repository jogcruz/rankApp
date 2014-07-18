package jjs.common.facade;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import jjs.common.adtclasses.classes.PropertyInfoW;
import jjs.common.adtclasses.exceptions.NotFoundException;
import jjs.common.adtclasses.exceptions.OperationFailedException;
import jjs.common.adtclasses.interfaces.IBaseServer;

import org.apache.log4j.Logger;

public abstract class BaseServer implements IBaseServer {

	protected EntityManager em;

	protected Logger log = Logger.getLogger(this.getClass());

	public void clear() {
		// TODO DVI TEST
		getEntityManager().clear();
	}

	public int executeUpdate(String querystr) throws OperationFailedException, NotFoundException {
		// TODO DVI TEST
		Query query = getEntityManager().createQuery(querystr);
		return query.executeUpdate();
	}

	public void flush() {
		// TODO DVI TEST
		getEntityManager().flush();
	}

	protected EntityManager getEntityManager() {
		return em;
	}

	public List getResultList(int pagenumber, int rows, String querystr) throws OperationFailedException {
		// TODO DVI TEST
		Query query = getEntityManager().createQuery(querystr);
		query.setFirstResult((pagenumber - 1) * rows);
		query.setMaxResults(rows);
		return query.getResultList();
	}

	public List getResultList(int pagenumber, int rows, String querystr, PropertyInfoW... propertyinfos) throws OperationFailedException {
		// TODO DVI TEST
		Query query = getEntityManager().createQuery(querystr);
		query.setFirstResult((pagenumber - 1) * rows);
		query.setMaxResults(rows);
		for (int i = 0; i < propertyinfos.length; i++) {
			PropertyInfoW property = propertyinfos[i];
			if (!property.isUnaryOperator())
				query.setParameter(property.getAlias(), property.getValue());
		}
		return query.getResultList();
	}

	public List getResultList(String querystr) throws OperationFailedException {
		// TODO DVI TEST
		Query query = getEntityManager().createQuery(querystr);
		return query.getResultList();
	}

	public List getResultList(String querystr, PropertyInfoW... propertyinfos) throws OperationFailedException {
		// TODO DVI TEST
		Query query = getEntityManager().createQuery(querystr);
		for (int i = 0; i < propertyinfos.length; i++) {
			PropertyInfoW property = propertyinfos[i];
			if (!property.isUnaryOperator())
				query.setParameter(property.getAlias(), property.getValue());
		}
		return query.getResultList();
	}

	public Object getSingleResult(String querystr) throws OperationFailedException, NotFoundException {
		// TODO DVI TEST
		Query query = getEntityManager().createQuery(querystr);
		return query.getSingleResult();
	}

	abstract public void setEntityManager(EntityManager em);

}
