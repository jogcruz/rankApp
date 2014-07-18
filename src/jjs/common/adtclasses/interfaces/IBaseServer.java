package jjs.common.adtclasses.interfaces;

import java.util.List;

import jjs.common.adtclasses.classes.PropertyInfoW;
import jjs.common.adtclasses.exceptions.NotFoundException;
import jjs.common.adtclasses.exceptions.OperationFailedException;

@SuppressWarnings("rawtypes")
public interface IBaseServer {

	public void clear() throws OperationFailedException;

	public int executeUpdate(String querystr) throws OperationFailedException, NotFoundException;

	public void flush() throws OperationFailedException;

	public List getResultList(int pagenumber, int rows, String querystr) throws OperationFailedException;

	public List getResultList(int pagenumber, int rows, String querystr, PropertyInfoW... infos) throws OperationFailedException;

	public List getResultList(String querystr) throws OperationFailedException;

	public List getResultList(String querystr, PropertyInfoW... infos) throws OperationFailedException;

	public Object getSingleResult(String querystr) throws OperationFailedException, NotFoundException;

}
