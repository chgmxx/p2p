package com.power.platform.lanmao.search.pojo;

import java.util.Date;

public class LanMaoOnlineRechange {
	/**
	 * 网银转账充值记录查询-交易记录明细
	 */
	private String requestNo; // 请求流水号
	private String platformUserNo; // 平台用户编号
	private String payerAccount; // 付款方账号（加密）
	private String payerName; // 付款方账户名
	private String amount; // 充值金额
	private String transactionTime ; // 交易完成时间
	public String getRequestNo() {
		return requestNo;
	}
	public void setRequestNo(String requestNo) {
		this.requestNo = requestNo;
	}
	public String getPlatformUserNo() {
		return platformUserNo;
	}
	public void setPlatformUserNo(String platformUserNo) {
		this.platformUserNo = platformUserNo;
	}
	public String getPayerAccount() {
		return payerAccount;
	}
	public void setPayerAccount(String payerAccount) {
		this.payerAccount = payerAccount;
	}
	public String getPayerName() {
		return payerName;
	}
	public void setPayerName(String payerName) {
		this.payerName = payerName;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getTransactionTime() {
		return transactionTime;
	}
	public void setTransactionTime(String transactionTime) {
		this.transactionTime = transactionTime;
	}

}
