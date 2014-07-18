package jjs.common.adtclasses.classes;

import java.io.Serializable;

public class PageDTO<T> implements Serializable {

	private static final long serialVersionUID = -4289937012233444906L;

	private PageInfoDTO pageinfo;

	private T[] values;

	public PageInfoDTO getPageinfo() {
		return pageinfo;
	}

	public T[] getValues() {
		return values;
	}

	public void setPageinfo(PageInfoDTO pageinfo) {
		this.pageinfo = pageinfo;
	}

	public void setValues(T[] values) {
		this.values = values;
	}

}
