package com.power.platform.lanmao.rw.pojo;

/**
 * 通知类自定义异常
 */
public  class NotifyException extends RuntimeException {

 	static final long serialVersionUID = -7034897110214576693L;
	public NotifyException() {
		
    }
	public NotifyException(String msg) {
		super(msg);
	}
}