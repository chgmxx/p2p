package com.power.platform.sys.utils;

import java.util.ArrayList;
import java.util.List;

public class WinPageResult<T> extends WinResult{

	private int pageNumber = 1;
	private int pageSize = 10;
	private int total;
	private List<T> rows = new ArrayList<T>();

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<T> getRows() {
		return rows;
	}

	public void setRows(List<T> rows) {
		if (rows == null)
			rows = new ArrayList<T>();
		this.rows = rows;
	}

}
