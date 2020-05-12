package com.power.platform.common.exception;

public class DistributionLockException extends RuntimeException {

	/**  */
	private static final long serialVersionUID = 1L;
	private String desc;

	public DistributionLockException(String desc) {

		this.desc = desc;
	}

	public String getDesc() {

		return desc;
	}

	public void setDesc(String desc) {

		this.desc = desc;
	}

}
