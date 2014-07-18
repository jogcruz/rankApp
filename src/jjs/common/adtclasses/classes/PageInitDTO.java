package jjs.common.adtclasses.classes;

import java.io.Serializable;

@SuppressWarnings("serial")
public class PageInitDTO implements Serializable {
	
	private int pagenumber;
	private int rows;
	
	public PageInitDTO(){
		super();
	}
	
	public PageInitDTO(int pagenumber, int rows) {
		super();
		this.pagenumber = pagenumber;
		this.rows = rows;
	}
	
	public int getPagenumber() {
		return pagenumber;
	}
	public void setPagenumber(int pagenumber) {
		this.pagenumber = pagenumber;
	}
	public int getRows() {
		return rows;
	}
	public void setRows(int rows) {
		this.rows = rows;
	}
	
}
