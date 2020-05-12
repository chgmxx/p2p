package com.power.platform.lanmao.search.pojo;

import java.util.Date;

public class LanMaoRechange {
	/**
	 * 充值明细
	 */
	private String amount; // 充值金额
	private String platformUserNo; // 平台用户编号
	private String createTime; // 交易发起时间
	private String transactionTime; // 交易完成时间
	private String status; // SUCCESS 表示支付成功, FAIL 表示支付失败,ERROR 表示支付错误, PENDDING 表示支付中
	private String rechargeWay; // 见【支付方式】
	private String payCompany; // 见【支付公司】
	private String payCompanyRequestNo; // 支付公司订单号
	private String errorCode; // 【存管错误码】
	private String errorMessage; // 【存管错误描述】
	private String channelErrorCode; // 【支付通道返回错误消息】
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
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRechargeWay() {
		return rechargeWay;
	}
	public void setRechargeWay(String rechargeWay) {
		this.rechargeWay = rechargeWay;
	}
	public String getPayCompany() {
		return payCompany;
	}
	public void setPayCompany(String payCompany) {
		this.payCompany = payCompany;
	}
	public String getPayCompanyRequestNo() {
		return payCompanyRequestNo;
	}
	public void setPayCompanyRequestNo(String payCompanyRequestNo) {
		this.payCompanyRequestNo = payCompanyRequestNo;
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
	public String getChannelErrorCode() {
		return channelErrorCode;
	}
	public void setChannelErrorCode(String channelErrorCode) {
		this.channelErrorCode = channelErrorCode;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}

}
