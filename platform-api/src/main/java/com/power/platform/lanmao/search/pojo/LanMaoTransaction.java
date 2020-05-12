package com.power.platform.lanmao.search.pojo;

import java.util.Date;

public class LanMaoTransaction {
	/**
	 * 交易确认明细
	 */
	
	private String projectNo; // 标的号
	private String confirmTradeType; // 同【预处理业务类型】
	private String requestNo; // 交易确认请求流水号
	private String commission; // 平台佣金
	private String createTime; // 交易发起时间
	private String status; // SUCCESS 表示成功,FAIL 表示失败,INIT 表示初始化,ERROR 表示异常,ACCEPT 表示已受理, PROCESSING 表示处理中。
	private String transactionTime; // 交易完成时间
	private String errorCode; // 错误码
	private String errorMessage; // 错误描述
	public String getProjectNo() {
		return projectNo;
	}
	public void setProjectNo(String projectNo) {
		this.projectNo = projectNo;
	}
	public String getConfirmTradeType() {
		return confirmTradeType;
	}
	public void setConfirmTradeType(String confirmTradeType) {
		this.confirmTradeType = confirmTradeType;
	}
	public String getRequestNo() {
		return requestNo;
	}
	public void setRequestNo(String requestNo) {
		this.requestNo = requestNo;
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
	public String getCommission() {
		return commission;
	}
	public void setCommission(String commission) {
		this.commission = commission;
	}
	
}
