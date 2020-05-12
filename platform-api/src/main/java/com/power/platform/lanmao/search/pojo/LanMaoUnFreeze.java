package com.power.platform.lanmao.search.pojo;

import java.util.Date;

public class LanMaoUnFreeze {
	/**
	 * 解冻明细
	 */
	private String requestNo; // 冻结流水号
	private String platformUserNo; // 平台用户编号
	private String amount; //解冻金额
	private String status; //SUCCESS 表示已成功
	private String createTime; // 交易发起时间
	private String transactionTime; // 交易完成时间
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
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	
}
