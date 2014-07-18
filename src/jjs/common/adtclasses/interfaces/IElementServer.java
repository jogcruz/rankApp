package jjs.common.adtclasses.interfaces;

import java.io.Serializable;

import jjs.common.adtclasses.exceptions.AccessDeniedException;
import jjs.common.adtclasses.exceptions.NotFoundException;
import jjs.common.adtclasses.exceptions.OperationFailedException;

public interface IElementServer<T, W extends Serializable> extends IGenericServer<T, Long, W> {

	public W addElement(W wrapper) throws AccessDeniedException, OperationFailedException, NotFoundException;

	public void deleteElement(Long ikey) throws OperationFailedException, NotFoundException;

	public W updateElement(W wrapper) throws OperationFailedException, NotFoundException;

}
