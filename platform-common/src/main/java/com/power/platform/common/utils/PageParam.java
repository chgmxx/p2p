package com.power.platform.common.utils;

public class PageParam {

	private int pageNumber = 1;
	private int pageSize = 10;
	private int recordStart;
	private int total;

	public int getPageNumber() {
		return pageNumber < 1 ? 1 : pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
		setRecordStart(getPageSize() * (getPageNumber() - 1));
	}

	public int getPageSize() {
		return pageSize <= 0 ? 10 : pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
		setRecordStart(getPageSize() * (getPageNumber() - 1));
	}

	public int getRecordStart() {
		return recordStart < 0 ? 0 : recordStart;
	}

	public void setRecordStart(int recordStart) {
		this.recordStart = recordStart;
	}

	public int getRecordEnd() {
		return getRecordStart() + getPageSize();
	}

	public int getTotal() {
		return total < 0 ? 0 : total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

}
