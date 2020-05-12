package com.power.platform.lanmao.search.pojo;

import java.util.Date;

public class LanMaoFreeze {
	/**
	 * 冻结明细
	 */
	private String platformUserNo; // 平台用户编号
	private String requestNo; // 冻结流水号
	private String amount; // 冻结金额
	private String unfreezeAmount; //累计解冻金额
	private String status; // FREEZED 表示尚有冻结,UNFREEZED 表示已解冻,FAIL 表示失败,INIT 表示初始 化,ERROR 表示异常。
	private String createTime; // 交易发起时间
	private String transactionTime; // 交易完成时间
	
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
	public String getRequestNo() {
		return requestNo;
	}
	public void setRequestNo(String requestNo) {
		this.requestNo = requestNo;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getUnfreezeAmount() {
		return unfreezeAmount;
	}
	public void setUnfreezeAmount(String unfreezeAmount) {
		this.unfreezeAmount = unfreezeAmount;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}


}
