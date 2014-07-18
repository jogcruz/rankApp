package jjs.common.facade;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import jjs.common.adtclasses.classes.OrderCriteriaDTO;
import jjs.common.adtclasses.classes.PageDTO;
import jjs.common.adtclasses.classes.PageInfoDTO;
import jjs.common.adtclasses.classes.PropertyInfoW;
import jjs.common.adtclasses.exceptions.AccessDeniedException;
import jjs.common.adtclasses.exceptions.NotFoundException;
import jjs.common.adtclasses.exceptions.OperationFailedException;
import jjs.common.adtclasses.interfaces.IGenericServer;
import jjs.common.factories.BeanExtenderFactory;

@SuppressWarnings("unchecked")
public abstract class GenericEJB3Server<T, ID extends Serializable, W extends Serializable> extends BaseServer implements IGenericServer<T, ID, W> {

	protected Class<T> entityBeanType;

	protected String entitylabel = null;

	protected String entityname = null;

	protected Class<ID> primaryKeyType;

	protected Class<W> wrapperType;

	public GenericEJB3Server() {
		setTypes();
		setEntityname();
		setEntitylabel();
		log.debug("GenericEJB3Server Instantiating Entity Server: " + entityname + " (" + entitylabel + ")");
	}

	public final W addIdentifiable(W wrapper) throws AccessDeniedException, OperationFailedException, NotFoundException {
		// TODO JGO TEST
		try {
			T newentity = entityBeanType.newInstance();
			BeanExtenderFactory.copyProperties(wrapper, newentity);
			copyRelationsWrapperToEntity(newentity, wrapper);
			ID key = primaryKeyType.newInstance();
			BeanExtenderFactory.copyProperties(wrapper, key);
			// Verificar que no exista la entidad
			try {
				T oldentity = findById(key);
				if (oldentity != null)
					throw new AccessDeniedException("Ya existe un " + entitylabel + " con la llave primaria especificada");
			} catch (NotFoundException exc) {
			}
			Method method = entityBeanType.getMethod("setId", primaryKeyType);
			method.invoke(newentity, key);
			newentity = persist(newentity);
			W newwrapper = getWrapperByEntity(newentity);
			return newwrapper;
		} catch (InvocationTargetException e) {
			throw new OperationFailedException("No es posible invocar el método setId", e);
		} catch (NoSuchMethodException e) {
			throw new OperationFailedException("No existe método setId", e);
		} catch (InstantiationException e) {
			throw new OperationFailedException("No es posible instanciar un objeto de la clase " + entityBeanType.getName(), e);
		} catch (IllegalAccessException e) {
			throw new OperationFailedException("No es posible acceder a las propiedades del objeto", e);
		}
	}

	abstract protected void copyRelationsEntityToWrapper(T entity, W wrapper) throws OperationFailedException, NotFoundException;

	abstract protected void copyRelationsWrapperToEntity(T entity, W wrapper) throws OperationFailedException, NotFoundException;

	public int count() throws OperationFailedException, NotFoundException {
		// TODO JGO TEST
		Long result = (Long) getSingleResult("select count(model) from " + entityname + " model");
		// En la práctica, la conversión es segura
		return result.intValue();
	}

	public int countByProperties(PropertyInfoW... propertyinfos) throws OperationFailedException, NotFoundException {
		// TODO JGO TEST
		StringBuffer buffer = new StringBuffer("select count(*) from " + entityname + " model where ");
		for (int i = 0; i < propertyinfos.length; i++) {
			PropertyInfoW property = propertyinfos[i];
			buffer.append("model.");
			buffer.append(property.getName());
			buffer.append(" ");
			if (property.isUnaryOperator()) {
				buffer.append(property.getOperator());
			} else {
				buffer.append(property.getOperator());
				buffer.append(" :");
				buffer.append(property.getAlias());
			}
			if (i < (propertyinfos.length - 1))
				buffer.append(" and ");
		}
		String queryStr = buffer.toString();
		Query query = getEntityManager().createQuery(queryStr);
		for (int i = 0; i < propertyinfos.length; i++) {
			PropertyInfoW property = propertyinfos[i];
			if (!property.isUnaryOperator())
				query.setParameter(property.getAlias(), property.getValue());
		}
		Long result = (Long) query.getSingleResult();
		return result.intValue();
	}

	public int countByProperty(String propertyName, Object value) throws OperationFailedException, NotFoundException {
		// TODO JGO TEST
		if (value != null) {
			final String queryString = "select count(model) from " + entityname + " model where model." + propertyName + " = :propertyValue";
			Query query = getEntityManager().createQuery(queryString);
			query.setParameter("propertyValue", value);
			Long result = (Long) query.getSingleResult();
			return result.intValue();
		} else {
			final String queryString = "select count(model) from " + entityname + " model where model." + propertyName + " is null";
			Query query = getEntityManager().createQuery(queryString);
			Long result = (Long) query.getSingleResult();
			return result.intValue();
		}
	}
	
	public int deleteByProperties(PropertyInfoW... propertyinfos) throws OperationFailedException, NotFoundException {
		// TODO JGO TEST
		StringBuffer buffer = new StringBuffer("delete from " + entityname + " where ");
		for (int i = 0; i < propertyinfos.length; i++) {
			PropertyInfoW property = propertyinfos[i];
			buffer.append(property.getName());
			buffer.append(" ");
			if (property.isUnaryOperator()) {
				buffer.append(property.getOperator());
			} else {
				buffer.append(property.getOperator());
				buffer.append(" :");
				buffer.append(property.getAlias());
			}
			if (i < (propertyinfos.length - 1))
				buffer.append(" and ");
		}
		String queryStr = buffer.toString();
		Query query = getEntityManager().createQuery(queryStr);
		for (int i = 0; i < propertyinfos.length; i++) {
			PropertyInfoW property = propertyinfos[i];
			if (!property.isUnaryOperator())
				query.setParameter(property.getAlias(), property.getValue());
		}
		int result = query.executeUpdate();
		return result;
	}

	public int deleteByProperty(String propertyName, Object value) throws OperationFailedException, NotFoundException {
		// TODO JGO TEST
		if (value != null) {
			final String queryString = "delete from " + entityname + " where " + propertyName + " = :propertyValue";
			Query query = getEntityManager().createQuery(queryString);
			query.setParameter("propertyValue", value);
			int result = query.executeUpdate();
			return result;
		} else {
			final String queryString = "delete from " + entityname + " where " + propertyName + " is null";
			Query query = getEntityManager().createQuery(queryString);
			int result = query.executeUpdate();
			return result;
		}
	}

	public int deleteAll() throws OperationFailedException {
		// TODO JGO TEST
		final String queryString = "delete from " + entityname;
		Query query = getEntityManager().createQuery(queryString);
		int result = query.executeUpdate();
		return result;
	}

	public final void deleteIdentifiable(Object ikey) throws OperationFailedException, NotFoundException {
		// TODO JGO TEST
		try {
			ID key = primaryKeyType.newInstance();
			BeanExtenderFactory.copyProperties(ikey, key);
			T entity = findById(key, true);
			remove(entity);
		} catch (InstantiationException e) {
			throw new OperationFailedException("No es posible instanciar un objeto de la clase " + primaryKeyType.getName(), e);
		} catch (IllegalAccessException e) {
			throw new OperationFailedException("No es posible acceder a las propiedades del objeto", e);
		}
	}


	public List<T> findAll() throws OperationFailedException {
		// TODO JGO TEST
		List<T> result = getResultList("select model from " + entityname + " model order by model.id");
		return result;
	}

	public List<T> findAll(int pagenumber, int rows) throws OperationFailedException {
		// TODO JGO TEST
		List<T> result = getResultList(pagenumber, rows, "select model from " + entityname + " model order by model.id");
		return result;
	}

	public T[] findAllAsArray() throws OperationFailedException {
		List<T> list = findAll();
		return getEntityArrayByCollection(list);
	}

	public T[] findAllAsArray(int pagenumber, int rows) throws OperationFailedException {
		List<T> list = findAll(pagenumber, rows);
		return getEntityArrayByCollection(list);
	}

	public T[] findAllAsArrayOrdered(int pagenumber, int rows, OrderCriteriaDTO... criterias) throws OperationFailedException {
		List<T> list = findAllOrdered(pagenumber, rows, criterias);
		return getEntityArrayByCollection(list);
	}

	public T[] findAllAsArrayOrdered(OrderCriteriaDTO... criterias) throws OperationFailedException {
		List<T> list = findAllOrdered(criterias);
		return getEntityArrayByCollection(list);
	}

	public PageDTO<T> findAllAsArrayPage(int pagenumber, int rows) throws OperationFailedException, NotFoundException {
		List<T> list = findAll(pagenumber, rows);
		T[] entities = getEntityArrayByCollection(list);
		int count = count();
		PageInfoDTO info = new PageInfoDTO();
		info.setPagenumber(pagenumber);
		info.setRows(entities.length);
		info.setTotalpages((count % rows) != 0 ? (count / rows) + 1 : (count / rows));
		info.setTotalrows(count);
		PageDTO<T> page = new PageDTO<T>();
		page.setPageinfo(info);
		page.setValues(entities);
		return page;
	}

	public List<T> findAllOrdered(int pagenumber, int rows, OrderCriteriaDTO... criterias) throws OperationFailedException {
		// TODO JGO TEST
		String order = getOrderCriteria(criterias);
		List<T> result = findByQuery(pagenumber, rows, "select model from " + entityname + " model order by " + order);
		return result;
	}

	public List<T> findAllOrdered(OrderCriteriaDTO... criterias) throws OperationFailedException {
		// TODO JGO TEST
		String order = getOrderCriteria(criterias);
		List<T> result = findByQuery("select model from " + entityname + " model order by " + order);
		return result;
	}

	public T findById(ID id) throws OperationFailedException, NotFoundException {
		// TODO JGO TEST
		return findById(id, false);
	}

	public T findById(ID id, boolean lock) throws OperationFailedException, NotFoundException {
		// TODO JGO TEST
		T result = getEntityManager().find(entityBeanType, id);
		if (result == null)
			throw new NotFoundException("No existe " + entitylabel + " con la llave primaria especificada");
		if (lock) {
			try {
				getEntityManager().lock(result, LockModeType.WRITE);
			} catch (RuntimeException e) {
				// Algún problema al adquirir el lock debe lanzar una OperationFailedException para poder hacer rollback
				throw new OperationFailedException("Error al bloquear entidad" + entityname, e);
			}
		}
		return result;
	}

	public T findByMaxId() throws OperationFailedException, NotFoundException {
		// TODO JGO TEST
		T entity = (T) getSingleResult("select model from " + entityname + " model where model.id = (select max(o.id) from " + entityname + " o)");
		return entity;
	}

	public T findByMinId() throws OperationFailedException, NotFoundException {
		// TODO JGO TEST
		T entity = (T) getSingleResult("select model from " + entityname + " model where model.id = (select min(o.id) from " + entityname + " o)");
		return entity;
	}

	private List<T> findByProperties(boolean paginated, int pagenumber, int rows, PropertyInfoW... propertyinfos) throws OperationFailedException, NotFoundException {
		// TODO JGO TEST
		StringBuffer buffer = new StringBuffer("select model from " + entityname + " model where ");
		for (int i = 0; i < propertyinfos.length; i++) {
			PropertyInfoW property = propertyinfos[i];
			buffer.append("model.");
			buffer.append(property.getName());
			buffer.append(" ");
			if (property.isUnaryOperator()) {
				buffer.append(property.getOperator());
			} else {
				buffer.append(property.getOperator());
				buffer.append(" :");
				buffer.append(property.getAlias());
			}
			if (i < (propertyinfos.length - 1))
				buffer.append(" and ");
		}
		String queryStr = buffer.toString();
		Query query = getEntityManager().createQuery(queryStr);
		for (int i = 0; i < propertyinfos.length; i++) {
			PropertyInfoW property = propertyinfos[i];
			if (!property.isUnaryOperator())
				query.setParameter(property.getAlias(), property.getValue());
		}
		if (paginated) {
			query.setFirstResult((pagenumber - 1) * rows);
			query.setMaxResults(rows);
		}
		return query.getResultList();
	}

	public List<T> findByProperties(int pagenumber, int rows, PropertyInfoW... propertyinfos) throws OperationFailedException, NotFoundException {
		// TODO JGO TEST
		return findByProperties(true, pagenumber, rows, propertyinfos);
	}

	public List<T> findByProperties(PropertyInfoW... propertyinfos) throws OperationFailedException, NotFoundException {
		// TODO JGO TEST
		return findByProperties(false, 0, 0, propertyinfos);
	}

	private List<T> findByProperty(boolean paginated, int pagenumber, int rows, String propertyName, Object value) {
		// TODO JGO TEST
		if (value != null) {
			final String queryString = "select model from " + entityname + " model where model." + propertyName + " = :propertyValue";
			Query query = getEntityManager().createQuery(queryString);
			query.setParameter("propertyValue", value);
			if (paginated) {
				query.setFirstResult((pagenumber - 1) * rows);
				query.setMaxResults(rows);
			}
			return query.getResultList();
		} else {
			final String queryString = "select model from " + entityname + " model where model." + propertyName + " is null";
			Query query = getEntityManager().createQuery(queryString);
			if (paginated) {
				query.setFirstResult((pagenumber - 1) * rows);
				query.setMaxResults(rows);
			}
			return query.getResultList();
		}
	}

	public List<T> findByProperty(int pagenumber, int rows, String propertyName, Object value) throws OperationFailedException {
		// TODO JGO TEST
		return findByProperty(true, pagenumber, rows, propertyName, value);
	}

	public List<T> findByProperty(String propertyName, Object value) {
		// TODO JGO TEST
		return findByProperty(false, 0, 0, propertyName, value);
	}

	private T[] findByPropertyAsArray(boolean paginated, int pagenumber, int rows, String propertyName, Object value) throws OperationFailedException {
		// TODO JGO TEST
		List<T> list = findByProperty(paginated, pagenumber, rows, propertyName, value);
		return getEntityArrayByCollection(list);
	}

	public T[] findByPropertyAsArray(int pagenumber, int rows, String propertyName, Object value) throws OperationFailedException {
		// TODO JGO TEST
		return findByPropertyAsArray(true, pagenumber, rows, propertyName, value);
	}

	public T[] findByPropertyAsArray(String propertyName, Object value) throws OperationFailedException {
		// TODO JGO TEST
		return findByPropertyAsArray(false, 0, 0, propertyName, value);
	}

	public PageDTO<T> findByPropertyAsArrayPage(int pagenumber, int rows, String propertyName, Object value) throws OperationFailedException, NotFoundException {
		// TODO JGO TEST
		List<T> list = findByProperty(pagenumber, rows, propertyName, value);
		T[] entities = getEntityArrayByCollection(list);
		int count = countByProperty(propertyName, value);
		PageInfoDTO info = new PageInfoDTO();
		info.setPagenumber(pagenumber);
		info.setRows(entities.length);
		info.setTotalpages((count % rows) != 0 ? (count / rows) + 1 : (count / rows));
		info.setTotalrows(count);
		PageDTO<T> page = new PageDTO<T>();
		page.setPageinfo(info);
		page.setValues(entities);
		return page;
	}

	public T findByPropertyAsSingleResult(String propertyName, Object value) throws OperationFailedException {
		// TODO JGO TEST
		final String queryString = "select model from " + entityname + " model where model." + propertyName + " = :propertyValue";
		Query query = getEntityManager().createQuery(queryString);
		query.setParameter("propertyValue", value);
		try {
			T result = (T) query.getSingleResult();
			return result;
		} catch (NoResultException e) {
			throw new OperationFailedException("No se encontró la entidad", e);
		} catch (NonUniqueResultException e) {
			throw new OperationFailedException("Existe más de una entidad con el atributo buscado", e);
		}
	}

	private List<T> findByPropertyOrdered(boolean paginated, int pagenumber, int rows, String propertyName, Object value, OrderCriteriaDTO... criterias) throws OperationFailedException {
		// TODO JGO TEST
		String order = getOrderCriteria(criterias);
		final String queryString = "select model from " + entityname + " model where model." + propertyName + "= :propertyValue order by " + order;
		Query query = getEntityManager().createQuery(queryString);
		query.setParameter("propertyValue", value);
		if (paginated) {
			query.setFirstResult((pagenumber - 1) * rows);
			query.setMaxResults(rows);
		}
		return query.getResultList();
	}

	public List<T> findByPropertyOrdered(int pagenumber, int rows, String propertyName, Object value, OrderCriteriaDTO... criterias) throws OperationFailedException {
		// TODO JGO TEST
		return findByPropertyOrdered(true, pagenumber, rows, propertyName, value, criterias);
	}

	public List<T> findByPropertyOrdered(String propertyName, Object value, OrderCriteriaDTO... criterias) throws OperationFailedException {
		// TODO JGO TEST
		return findByPropertyOrdered(false, 0, 0, propertyName, value, criterias);
	}

	public List<T> findByQuery(int pagenumber, int rows, String querystr) throws OperationFailedException {
		// TODO JGO TEST
		List<T> result = getResultList(pagenumber, rows, querystr);
		return result;
	}

	public List<T> findByQuery(int pagenumber, int rows, String querystr, PropertyInfoW... propertyinfos) throws OperationFailedException {
		// TODO JGO TEST
		List<T> result = getResultList(pagenumber, rows, querystr, propertyinfos);
		return result;
	}

	public List<T> findByQuery(String querystr) throws OperationFailedException {
		// TODO JGO TEST
		List<T> result = getResultList(querystr);
		return result;
	}

	public List<T> findByQuery(String querystr, PropertyInfoW... propertyinfos) throws OperationFailedException {
		// TODO JGO TEST
		List<T> result = getResultList(querystr, propertyinfos);
		return result;
	}

	public List<T> findByStringProperty(String propertyName, String value) throws OperationFailedException {
		// TODO JGO TEST
		return findByStringProperty(propertyName, value, true);
	}

	public List<T> findByStringProperty(String propertyName, String value, boolean iscasesensitive) throws OperationFailedException {
		// TODO JGO TEST
		value = iscasesensitive ? value : value.toUpperCase();
		final String queryString = iscasesensitive ? "select model from " + entityname + " model where model." + propertyName + " = :propertyValue" : "select model from " + entityname + " model where upper(model." + propertyName + ") = :propertyValue";
		Query query = getEntityManager().createQuery(queryString);
		query.setParameter("propertyValue", value);
		return query.getResultList();
	}

	public List<T> findLikeStringProperty(String propertyName, String value) throws OperationFailedException {
		// TODO JGO TEST
		return findLikeStringProperty(propertyName, value, true);
	}

	public List<T> findLikeStringProperty(String propertyName, String value, boolean iscasesensitive) throws OperationFailedException {
		// TODO JGO TEST
		value = iscasesensitive ? value : value.toUpperCase();
		final String queryString = iscasesensitive ? "select model from " + entityname + " model where model." + propertyName + " like :propertyValue" : "select model from " + entityname + " model where upper(model." + propertyName + ") like :propertyValue";
		Query query = getEntityManager().createQuery(queryString);
		query.setParameter("propertyValue", "%" + value + "%");
		return query.getResultList();
	}

	public T[] findLikeStringPropertyAsArray(String propertyName, String value) throws OperationFailedException {
		// TODO JGO TEST
		List<T> list = findLikeStringProperty(propertyName, value);
		return getEntityArrayByCollection(list);
	}

	public List<W> getAll() throws OperationFailedException, NotFoundException {
		// TODO JGO TEST
		List<T> allentities = findAll();
		List<W> result = getWrappersByEntities(allentities);
		return result;
	}

	public List<W> getAll(int pagenumber, int rows) throws OperationFailedException, NotFoundException {
		// TODO JGO TEST
		List<T> allentities = findAll(pagenumber, rows);
		List<W> result = getWrappersByEntities(allentities);
		return result;
	}

	public W[] getAllAsArray() throws OperationFailedException, NotFoundException {
		List<W> list = getAll();
		return getWrapperArrayByCollection(list);
	}

	public W[] getAllAsArray(int pagenumber, int rows) throws OperationFailedException, NotFoundException {
		List<W> list = getAll(pagenumber, rows);
		return getWrapperArrayByCollection(list);
	}

	public W[] getAllAsArrayOrdered(int pagenumber, int rows, OrderCriteriaDTO... criterias) throws OperationFailedException, NotFoundException {
		// TODO JGO TEST
		List<W> list = getAllOrdered(pagenumber, rows, criterias);
		return getWrapperArrayByCollection(list);
	}

	public W[] getAllAsArrayOrdered(OrderCriteriaDTO... criterias) throws OperationFailedException, NotFoundException {
		// TODO JGO TEST
		List<W> list = getAllOrdered(criterias);
		return getWrapperArrayByCollection(list);
	}

	public PageDTO<W> getAllAsArrayPage(int pagenumber, int rows) throws OperationFailedException, NotFoundException {
		List<T> list = findAll(pagenumber, rows);
		W[] wrappers = getWrapperArrayByEntities(list);
		int count = count();
		PageInfoDTO info = new PageInfoDTO();
		info.setPagenumber(pagenumber);
		info.setRows(wrappers.length);
		info.setTotalpages((count % rows) != 0 ? (count / rows) + 1 : (count / rows));
		info.setTotalrows(count);
		PageDTO<W> page = new PageDTO<W>();
		page.setPageinfo(info);
		page.setValues(wrappers);
		return page;
	}

	public List<W> getAllOrdered(int pagenumber, int rows, OrderCriteriaDTO... criterias) throws OperationFailedException, NotFoundException {
		// TODO JGO TEST
		List<T> allentities = findAllOrdered(pagenumber, rows, criterias);
		List<W> result = getWrappersByEntities(allentities);
		return result;
	}

	public List<W> getAllOrdered(OrderCriteriaDTO... criterias) throws OperationFailedException, NotFoundException {
		// TODO JGO TEST
		List<T> allentities = findAllOrdered(criterias);
		List<W> result = getWrappersByEntities(allentities);
		return result;
	}

	public W getById(ID id) throws OperationFailedException, NotFoundException {
		// TODO JGO TEST
		return getById(id, false);
	}

	public W getById(ID id, boolean lock) throws OperationFailedException, NotFoundException {
		// TODO JGO TEST
		T entity = findById(id, lock);
		W wrapper = getWrapperByEntity(entity);
		return wrapper;
	}

	public W getByMaxId() throws OperationFailedException, NotFoundException {
		T entity = findByMaxId();
		W wrapper = getWrapperByEntity(entity);
		return wrapper;
	}

	public W getByMinId() throws OperationFailedException, NotFoundException {
		T entity = findByMinId();
		W wrapper = getWrapperByEntity(entity);
		return wrapper;
	}

	public List<W> getByProperties(int pagenumber, int rows, PropertyInfoW... propertyinfos) throws OperationFailedException, NotFoundException {
		// TODO JGO TEST
		List<T> entities = findByProperties(pagenumber, rows, propertyinfos);
		List<W> wrappers = getWrappersByEntities(entities);
		return wrappers;
	}

	public List<W> getByProperties(PropertyInfoW... propertyinfos) throws OperationFailedException, NotFoundException {
		// TODO JGO TEST
		List<T> entities = findByProperties(propertyinfos);
		List<W> wrappers = getWrappersByEntities(entities);
		return wrappers;
	}

	public W[] getByPropertiesAsArray(PropertyInfoW... propertyinfos) throws OperationFailedException, NotFoundException {
		// TODO JGO TEST
		List<W> wrappers = getByProperties(propertyinfos);
		return getWrapperArrayByCollection(wrappers);
	}

	public PageDTO<W> getByPropertiesAsArrayPage(int pagenumber, int rows, PropertyInfoW... propertyinfos) throws OperationFailedException, NotFoundException {
		// TODO JGO TEST
		List<T> list = findByProperties(pagenumber, rows, propertyinfos);
		W[] wrappers = getWrapperArrayByEntities(list);
		int count = countByProperties(propertyinfos);
		PageInfoDTO info = new PageInfoDTO();
		info.setPagenumber(pagenumber);
		info.setRows(wrappers.length);
		info.setTotalpages((count % rows) != 0 ? (count / rows) + 1 : (count / rows));
		info.setTotalrows(count);
		PageDTO<W> page = new PageDTO<W>();
		page.setPageinfo(info);
		page.setValues(wrappers);
		return page;
	}

	public List<W> getByProperty(int pagenumber, int rows, String propertyName, Object value) throws OperationFailedException, NotFoundException {
		// TODO JGO TEST
		List<T> entities = findByProperty(pagenumber, rows, propertyName, value);
		List<W> wrappers = getWrappersByEntities(entities);
		return wrappers;
	}

	public List<W> getByProperty(String propertyName, Object value) throws OperationFailedException, NotFoundException {
		// TODO JGO TEST
		List<T> entities = findByProperty(propertyName, value);
		List<W> wrappers = getWrappersByEntities(entities);
		return wrappers;
	}

	public W[] getByPropertyAsArray(int pagenumber, int rows, String propertyName, Object value) throws OperationFailedException, NotFoundException {
		// TODO JGO TEST
		List<W> list = getByProperty(pagenumber, rows, propertyName, value);
		return getWrapperArrayByCollection(list);
	}

	public W[] getByPropertyAsArray(String propertyName, Object value) throws OperationFailedException, NotFoundException {
		// TODO JGO TEST
		List<W> list = getByProperty(propertyName, value);
		return getWrapperArrayByCollection(list);
	}

	public W[] getByPropertyAsArrayOrdered(int pagenumber, int rows, String propertyName, Object value, OrderCriteriaDTO... criterias) throws OperationFailedException, NotFoundException {
		// TODO JGO TEST
		List<T> entities = findByPropertyOrdered(pagenumber, rows, propertyName, value, criterias);
		return getWrapperArrayByEntities(entities);
	}

	public W[] getByPropertyAsArrayOrdered(String propertyName, Object value, OrderCriteriaDTO... criterias) throws OperationFailedException, NotFoundException {
		// TODO JGO TEST
		List<T> entities = findByPropertyOrdered(propertyName, value, criterias);
		return getWrapperArrayByEntities(entities);
	}

	public PageDTO<W> getByPropertyAsArrayPage(int pagenumber, int rows, String propertyName, Object value) throws OperationFailedException, NotFoundException {
		// TODO JGO TEST
		List<T> list = findByProperty(pagenumber, rows, propertyName, value);
		W[] wrappers = getWrapperArrayByEntities(list);
		int count = countByProperty(propertyName, value);
		PageInfoDTO info = new PageInfoDTO();
		info.setPagenumber(pagenumber);
		info.setRows(wrappers.length);
		info.setTotalpages((count % rows) != 0 ? (count / rows) + 1 : (count / rows));
		info.setTotalrows(count);
		PageDTO<W> page = new PageDTO<W>();
		page.setPageinfo(info);
		page.setValues(wrappers);
		return page;
	}

	public final W getByPropertyAsSingleResult(String propertyName, Object value) throws OperationFailedException, NotFoundException {
		// TODO JGO TEST
		T entity = (T) findByPropertyAsSingleResult(propertyName, value);
		W wrapper = getWrapperByEntity(entity);
		return wrapper;
	}

	public List<W> getByPropertyOrdered(String propertyName, Object value, OrderCriteriaDTO... criterias) throws OperationFailedException, NotFoundException {
		// TODO JGO TEST
		List<T> entities = findByPropertyOrdered(propertyName, value, criterias);
		List<W> result = getWrappersByEntities(entities);
		return result;
	}

	public List<W> getByQuery(int pagenumber, int rows, String querystr) throws OperationFailedException, NotFoundException {
		// TODO JGO TEST
		List<T> entities = findByQuery(pagenumber, rows, querystr);
		List<W> result = getWrappersByEntities(entities);
		return result;
	}

	public List<W> getByQuery(int pagenumber, int rows, String querystr, PropertyInfoW... propertyinfos) throws OperationFailedException, NotFoundException {
		// TODO JGO TEST
		List<T> entities = findByQuery(pagenumber, rows, querystr, propertyinfos);
		List<W> result = getWrappersByEntities(entities);
		return result;
	}

	public List<W> getByQuery(String querystr) throws OperationFailedException, NotFoundException {
		// TODO JGO TEST
		List<T> entities = findByQuery(querystr);
		List<W> result = getWrappersByEntities(entities);
		return result;
	}

	public List<W> getByQuery(String querystr, PropertyInfoW... propertyinfos) throws OperationFailedException, NotFoundException {
		// TODO JGO TEST
		List<T> entities = findByQuery(querystr, propertyinfos);
		List<W> result = getWrappersByEntities(entities);
		return result;
	}

	public List<W> getByStringProperty(String propertyName, String value) throws OperationFailedException, NotFoundException {
		// TODO JGO TEST
		return getByStringProperty(propertyName, value, true);
	}

	public List<W> getByStringProperty(String propertyName, String value, boolean iscasesensitive) throws OperationFailedException, NotFoundException {
		// TODO JGO TEST
		List<T> entities = findByStringProperty(propertyName, value, iscasesensitive);
		List<W> wrappers = getWrappersByEntities(entities);
		return wrappers;
	}

	public final T[] getEntityArrayByCollection(Collection<T> tcollection) throws OperationFailedException {
		// TODO JGO TEST
		T[] result = (T[]) Array.newInstance(entityBeanType, tcollection.size());
		int i = 0;
		for (Iterator<T> iterator = tcollection.iterator(); iterator.hasNext(); i++) {
			T t = iterator.next();
			result[i] = t;
		}
		return result;
	}

	protected Class<T> getEntityBeanType() {
		return entityBeanType;
	}

	protected W[] getIdentifiables() throws OperationFailedException, NotFoundException {
		// TODO JGO TEST
		List<W> allwrappers = getAll();
		return getWrapperArrayByCollection(allwrappers);
	}

	public List<W> getLikeStringProperty(String propertyName, String value) throws OperationFailedException, NotFoundException {
		// TODO JGO TEST
		return getLikeStringProperty(propertyName, value, true);
	}

	public List<W> getLikeStringProperty(String propertyName, String value, boolean iscasesensitive) throws OperationFailedException, NotFoundException {
		// TODO JGO TEST
		List<T> entities = findLikeStringProperty(propertyName, value, iscasesensitive);
		List<W> wrappers = getWrappersByEntities(entities);
		return wrappers;
	}

	public W[] getLikeStringPropertyAsArray(String propertyName, String value) throws OperationFailedException, NotFoundException {
		// TODO JGO TEST
		List<W> list = getLikeStringProperty(propertyName, value);
		return getWrapperArrayByCollection(list);
	}

	private String getOrderCriteria(OrderCriteriaDTO... criterias) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < criterias.length; i++) {
			OrderCriteriaDTO criteria = criterias[i];
			buffer.append("model.");
			buffer.append(criteria.getPropertyname());
			buffer.append(criteria.getAscending() ? " asc," : " desc,");
		}
		buffer.deleteCharAt(buffer.length() - 1);
		return buffer.toString();
	}

	protected final ID getPrimaryKey(T entity) throws OperationFailedException
	{
		// TODO JGO TEST
		try 
		{
			Class[] paramtypes = null;
			Method method = entityBeanType.getMethod("getId", paramtypes);
			ID result = (ID) method.invoke(entity, new Object[0]);
			return result;
		}
		catch (SecurityException e)
		{
			throw new OperationFailedException("Error de seguridad al acceder al método getId", e);
		}
		catch (IllegalArgumentException e) 
		{
			throw new OperationFailedException("Error en los argumentos de llamada al método getId", e);
		}
		catch (NoSuchMethodException e)
		{
			throw new OperationFailedException("No existe el método getId", e);
		}
		catch (IllegalAccessException e)
		{
			throw new OperationFailedException("Acceso ilegal al método getId", e);
		} 
		catch (InvocationTargetException e) 
		{
			throw new OperationFailedException("No es posible invocar el método getId", e);
		}
	}

	protected Class<ID> getPrimaryKeyType() {
		return primaryKeyType;
	}

	public T getReferenceById(ID id) {
		// TODO JGO TEST
		T result = getEntityManager().getReference(entityBeanType, id);
		return result;
	}

	public final W[] getWrapperArrayByCollection(Collection<W> wcollection) throws OperationFailedException {
		// TODO JGO TEST
		W[] result = (W[]) Array.newInstance(wrapperType, wcollection.size());
		int i = 0;
		for (Iterator<W> iterator = wcollection.iterator(); iterator.hasNext(); i++) {
			W w = iterator.next();
			result[i] = w;
		}
		return result;
	}

	public final W[] getWrapperArrayByEntities(List<T> entities) throws OperationFailedException, NotFoundException {
		// TODO JGO TEST
		List<W> wlist = getWrappersByEntities(entities);
		return getWrapperArrayByCollection(wlist);
	}

	public final W getWrapperByEntity(T entity) throws OperationFailedException, NotFoundException
	{
		try 
		{
			W wrapper = null;
			
			if (entity != null)
			{
				wrapper = wrapperType.newInstance();

				ID key = getPrimaryKey(entity);
				
				BeanExtenderFactory.copyProperties(key, wrapper);
				BeanExtenderFactory.copyProperties(entity, wrapper);
				
				this.copyRelationsEntityToWrapper(entity, wrapper);
			}
			
			return wrapper;
		}
		catch (InstantiationException e) 
		{
			throw new OperationFailedException("No es posible instanciar un objeto de la clase " + wrapperType.getName(), e);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			throw new OperationFailedException("No es posible acceder a las propiedades del objeto", e);
		}
	}

	public final List<W> getWrappersByEntities(List<T> entities) throws OperationFailedException, NotFoundException 
	{
		List<W> result = new ArrayList<W>();
		
		if ((entities != null) && (entities.size() > 0))
		{
			for (T entity : entities) 
			{
				W entityWrapper = this.getWrapperByEntity(entity);
				
				if (entityWrapper != null)
				{
					result.add(entityWrapper);
				}
			}	
		}
		
		return result;
	}

	protected Class<W> getWrapperType() {
		return wrapperType;
	}

	public void lock(T entity) {
		// TODO JGO TEST
		getEntityManager().lock(entity, LockModeType.WRITE);
	}

	public T merge(T entity) {
		// TODO JGO TEST
		T newentitty = getEntityManager().merge(entity);
		return newentitty;
	}

	public T persist(T entity) {
		// TODO JGO TEST
		getEntityManager().persist(entity);
		return entity;
	}

	public void refresh(T entity) throws OperationFailedException {
		// TODO JGO TEST
		getEntityManager().refresh(entity);
	}

	public void remove(T entity) {
		// TODO JGO TEST
		getEntityManager().remove(entity);
	}

	abstract protected void setEntitylabel();

	abstract protected void setEntityname();

	protected void setTypes() {
		ParameterizedType genericType = (ParameterizedType) getClass().getGenericSuperclass();
		this.entityBeanType = (Class<T>) genericType.getActualTypeArguments()[0];
		this.primaryKeyType = (Class<ID>) genericType.getActualTypeArguments()[1];
		this.wrapperType = (Class<W>) genericType.getActualTypeArguments()[2];
	}

	public final W updateIdentifiable(W wrapper) throws OperationFailedException, NotFoundException {
		// TODO JGO TEST
		try {
			T entity = entityBeanType.newInstance();
			BeanExtenderFactory.copyProperties(wrapper, entity);
			copyRelationsWrapperToEntity(entity, wrapper);
			ID key = primaryKeyType.newInstance();
			BeanExtenderFactory.copyProperties(wrapper, key);
			Method method = entityBeanType.getMethod("setId", primaryKeyType);
			method.invoke(entity, key);
			entity = merge(entity);
			W newwrapper = getWrapperByEntity(entity);
			return newwrapper;
		} catch (InvocationTargetException e) {
			throw new OperationFailedException("No es posible invocar el método setId", e);
		} catch (NoSuchMethodException e) {
			throw new OperationFailedException("No existe método setId", e);
		} catch (InstantiationException e) {
			throw new OperationFailedException("No es posible instanciar un objeto de la clase " + entityBeanType.getName(), e);
		} catch (IllegalAccessException e) {
			throw new OperationFailedException("No es posible acceder a las propiedades del objeto", e);
		}
	}
}