package com.power.platform.lanmao.search.utils;

import java.util.List;

public class OneTransactionResult<T> {
	/**
     * 错误码
     */ 
    private Integer code;

    /**
     * 提示信息
     */
    private String status;
    /**
     * 提示信息
     */
    private String errorCode;
    /**
     * 提示信息
     */
    private String errorMessage;

    /**
     * 具体内容
     */
    private List<T> t;
	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public List<T> getT() {
		return t;
	}

	public void setT(List<T> t) {
		this.t = t;
	}




    
    
}
