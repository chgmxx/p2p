package com.power.platform.lanmao.trade.pojo;

import java.io.Serializable;

public class ResponsePojo implements Serializable {

	/**  */
	private static final long serialVersionUID = 1L;
	private String code;
	private String status;
	private String errorCode;
	private String errorMessage;
	private String projectStatus;
	private String requestNo;
	private String transactionStatus;
	private String createTime;
	private String transactionTime;
	private String platformUserNo;
	private String bizType;

	public String getCode() {

		return code;
	}

	public void setCode(String code) {

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

	public String getProjectStatus() {

		return projectStatus;
	}

	public void setProjectStatus(String projectStatus) {

		this.projectStatus = projectStatus;
	}

	public String getRequestNo() {

		return requestNo;
	}

	public void setRequestNo(String requestNo) {

		this.requestNo = requestNo;
	}

	public String getTransactionStatus() {

		return transactionStatus;
	}

	public void setTransactionStatus(String transactionStatus) {

		this.transactionStatus = transactionStatus;
	}

	public String getCreateTime() {

		return createTime;
	}

	public void setCreateTime(String createTime) {

		this.createTime = createTime;
	}

	public String getTransactionTime() {

		return transactionTime;
	}

	public void setTransactionTime(String transactionTime) {

		this.transactionTime = transactionTime;
	}

	public String getPlatformUserNo() {

		return platformUserNo;
	}

	public void setPlatformUserNo(String platformUserNo) {

		this.platformUserNo = platformUserNo;
	}

	public String getBizType() {

		return bizType;
	}

	public void setBizType(String bizType) {

		this.bizType = bizType;
	}

}
