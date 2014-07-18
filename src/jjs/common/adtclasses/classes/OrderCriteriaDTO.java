package jjs.common.adtclasses.classes;

import java.io.Serializable;

@SuppressWarnings("serial")
public class OrderCriteriaDTO implements Serializable {

	private String propertyname;

	private boolean ascending;

	public OrderCriteriaDTO() {
	}

	public OrderCriteriaDTO(String propertyname, boolean ascending) {
		this.propertyname = propertyname;
		this.ascending = ascending;
	}

	public String getPropertyname() {
		return propertyname;
	}

	public boolean getAscending() {
		return ascending;
	}

	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}

	public void setPropertyname(String propertyname) {
		this.propertyname = propertyname;
	}

}
