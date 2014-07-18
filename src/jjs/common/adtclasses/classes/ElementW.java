package jjs.common.adtclasses.classes;

import java.io.Serializable;

import jjs.common.adtclasses.interfaces.IElement;

@SuppressWarnings("serial")
public class ElementW implements IElement, Serializable {

	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
