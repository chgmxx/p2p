package com.power.platform.common.exception;

public class WinException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6401167199705430888L;

	private String desc;

	public WinException() {
		super();
	}

	public WinException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		this.desc = message;
	}

	public WinException(String message, Throwable cause) {
		super(message, cause);
		this.desc = message;
	}

	public WinException(String message) {
		super(message);
		this.desc = message;
	}

	public WinException(Throwable cause) {
		super(cause);
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

}
