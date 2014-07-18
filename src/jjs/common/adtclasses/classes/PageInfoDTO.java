package jjs.common.adtclasses.classes;

import java.io.Serializable;

@SuppressWarnings("serial")
public class PageInfoDTO implements Serializable {
	
	private int pagenumber;
	private int rows;
	private int totalrows;
	private int totalpages;
	
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
	public int getTotalrows() {
		return totalrows;
	}
	public void setTotalrows(int totalrows) {
		this.totalrows = totalrows;
	}
	public int getTotalpages() {
		return totalpages;
	}
	public void setTotalpages(int totalpages) {
		this.totalpages = totalpages;
	}
	
	
}
