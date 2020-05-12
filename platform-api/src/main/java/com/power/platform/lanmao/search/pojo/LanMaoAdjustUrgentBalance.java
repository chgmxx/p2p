package com.power.platform.lanmao.search.pojo;

import java.util.Date;

public class LanMaoAdjustUrgentBalance {
      /**
        * 调整平台垫资额度明细
       */
	private String requestNo; // 请求流水号
	private String urgentAccountAdjustStatus; // 交易处理状态
	private String accountAdjustType; // 垫资额度调整类型
	private String adjustAmount; // 调整垫资额度
	private String lastAmount; // 调整前平台总垫资额度
	private String balance; // 账户余额
	private String availableAmount; // 可用余额
	private String newestAmount; // 调整后平台总垫资额度
	private String sourcePlatformUserNo; // 出款方用户编号
	private String targetPlatformUserNo; // 收款方用户编号
	private String createTime; // 交易发起时间
	private String transactionTime; // 交易完成时间
	private String transferTime; // 转账时间
	private String transferCompletedTime; // 到账时间
	public String getRequestNo() {
		return requestNo;
	}
	public void setRequestNo(String requestNo) {
		this.requestNo = requestNo;
	}
	public String getUrgentAccountAdjustStatus() {
		return urgentAccountAdjustStatus;
	}
	public void setUrgentAccountAdjustStatus(String urgentAccountAdjustStatus) {
		this.urgentAccountAdjustStatus = urgentAccountAdjustStatus;
	}
	public String getAccountAdjustType() {
		return accountAdjustType;
	}
	public void setAccountAdjustType(String accountAdjustType) {
		this.accountAdjustType = accountAdjustType;
	}
	public String getAdjustAmount() {
		return adjustAmount;
	}
	public void setAdjustAmount(String adjustAmount) {
		this.adjustAmount = adjustAmount;
	}
	public String getLastAmount() {
		return lastAmount;
	}
	public void setLastAmount(String lastAmount) {
		this.lastAmount = lastAmount;
	}
	public String getBalance() {
		return balance;
	}
	public void setBalance(String balance) {
		this.balance = balance;
	}
	public String getAvailableAmount() {
		return availableAmount;
	}
	public void setAvailableAmount(String availableAmount) {
		this.availableAmount = availableAmount;
	}
	public String getNewestAmount() {
		return newestAmount;
	}
	public void setNewestAmount(String newestAmount) {
		this.newestAmount = newestAmount;
	}
	public String getSourcePlatformUserNo() {
		return sourcePlatformUserNo;
	}
	public void setSourcePlatformUserNo(String sourcePlatformUserNo) {
		this.sourcePlatformUserNo = sourcePlatformUserNo;
	}
	public String getTargetPlatformUserNo() {
		return targetPlatformUserNo;
	}
	public void setTargetPlatformUserNo(String targetPlatformUserNo) {
		this.targetPlatformUserNo = targetPlatformUserNo;
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
	public String getTransferTime() {
		return transferTime;
	}
	public void setTransferTime(String transferTime) {
		this.transferTime = transferTime;
	}
	public String getTransferCompletedTime() {
		return transferCompletedTime;
	}
	public void setTransferCompletedTime(String transferCompletedTime) {
		this.transferCompletedTime = transferCompletedTime;
	}

	
	
	
	
}
