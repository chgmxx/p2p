package com.power.platform.lanmao.search.pojo;

import java.util.Date;

public class LanMaoWithDraw {
	/**
	 * 提现明细
	 */
	private String amount; // 提现金额
	private String commission; // 提现分拥
	private String platformUserNo; // 平台用户编号
	private String createTime; // 交易发起时间
	private String transactionTime; // 交易完成时间
	private String remitTime; // 出款时间
	private String completedTime; // 到账时间
	private String status; // 见【提现交易状态】
	private String bankcardNo; // 提现银行卡号(仅显示银行卡后四位)
	private String withdrawForm; // 提现类型。IMMEDIATE 为直接提现,CONFIRMED 为待确认提现,默认为直接提 现方式。
	private String backRollStatus; // 提现失败的订单回充后会返回该状态:SUCCESS(已回退)、PENDDING(回退中) INIT(初始化) ONLINE_SUCCESS(特殊回退)
//（1）ONLINE_SUCCESS(特殊回退)为已回退但虚户资金不回充状态，用于建行特殊退汇时系统无法自动识别为退汇，在符合白名单条件下识别为网银转账充值并上账情况。
//（2）提现失败原因若不是特殊退汇不返回充值流水号，若为特殊退汇，则返回格式为failReason：”提现失败原因，充值流水号”。

	private String remitType; // 出款类型。NORMAL:T+1 出款;NORMAL_URGENT:普通 D0 出款;URGENT: 实时 D0 出款。
	private String withdrawWay; // 见【提现方式】。NORMAL: T+1 提现; NORMAL_URGENT:智能 D0 提现; URGENT:加急 D0 提现。
	private String floatAmount; // D0 出款时的垫资金额
	private String failReason; // 提现失败错误原因描述
	public String getRemitTime() {
		return remitTime;
	}
	public void setRemitTime(String remitTime) {
		this.remitTime = remitTime;
	}
	public String getCompletedTime() {
		return completedTime;
	}
	public void setCompletedTime(String completedTime) {
		this.completedTime = completedTime;
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
	
	
	public String getAmount() {
		return amount;
	}

	public String getPlatformUserNo() {
		return platformUserNo;
	}
	public void setPlatformUserNo(String platformUserNo) {
		this.platformUserNo = platformUserNo;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getBankcardNo() {
		return bankcardNo;
	}
	public void setBankcardNo(String bankcardNo) {
		this.bankcardNo = bankcardNo;
	}
	public String getWithdrawForm() {
		return withdrawForm;
	}
	public void setWithdrawForm(String withdrawForm) {
		this.withdrawForm = withdrawForm;
	}
	public String getBackRollStatus() {
		return backRollStatus;
	}
	public void setBackRollStatus(String backRollStatus) {
		this.backRollStatus = backRollStatus;
	}
	public String getRemitType() {
		return remitType;
	}
	public void setRemitType(String remitType) {
		this.remitType = remitType;
	}
	public String getWithdrawWay() {
		return withdrawWay;
	}
	public void setWithdrawWay(String withdrawWay) {
		this.withdrawWay = withdrawWay;
	}
	public String getFailReason() {
		return failReason;
	}
	public void setFailReason(String failReason) {
		this.failReason = failReason;
	}
	public String getCommission() {
		return commission;
	}
	public void setCommission(String commission) {
		this.commission = commission;
	}
	public String getFloatAmount() {
		return floatAmount;
	}
	public void setFloatAmount(String floatAmount) {
		this.floatAmount = floatAmount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	
	
}
