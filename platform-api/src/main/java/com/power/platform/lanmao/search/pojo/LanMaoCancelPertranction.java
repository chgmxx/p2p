package com.power.platform.lanmao.search.pojo;

import java.util.Date;

public class LanMaoCancelPertranction {
	/**
	 * 取消预处理明细
	 */
	private String requestNo; // 请求流水号
	private String preTransactionNo; // 预处理业务流水号
	private String amount; // 取消金额
	private String createTime; // 交易发起时间
	private String transactionTime; // 交易完成时间
	private String status; // SUCCESS 表示成功,FAIL 表示失败,INIT 表示初始化,ERROR 表示异常。
	
	
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getRequestNo() {
		return requestNo;
	}
	public void setRequestNo(String requestNo) {
		this.requestNo = requestNo;
	}
	public String getPreTransactionNo() {
		return preTransactionNo;
	}
	public void setPreTransactionNo(String preTransactionNo) {
		this.preTransactionNo = preTransactionNo;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	
	
	
}
