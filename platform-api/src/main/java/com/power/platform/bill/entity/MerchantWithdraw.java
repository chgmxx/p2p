package com.power.platform.bill.entity;

import org.hibernate.validator.constraints.Length;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.power.platform.common.persistence.DataEntity;

/**
 * 平台商户对账，提现文件Entity
 * 
 * @author lance
 * @version 2018-03-08
 */
public class MerchantWithdraw extends DataEntity<MerchantWithdraw> {

	private static final long serialVersionUID = 1L;
	private String cgbOrderId; // 存管订单号，存管系统的唯一订单号
	private String tradingType; // 交易类型，2001：提现，2002：提现收费
	private String tradingAmount; // 交易金额
	private String tradingStatus; // 交易状态，订单的状态 S 为成功
	private Date completionTime; // 完成时间
	private String payCode; // 支付公司代码，交易对应的支付公司代码
	private String platformUserId; // 平台用户ID
	private Date beginCompletionTime; // 开始 完成时间
	private Date endCompletionTime; // 结束 完成时间

	public MerchantWithdraw() {

		super();
	}

	public MerchantWithdraw(String id) {

		super(id);
	}

	@Length(min = 0, max = 64, message = "存管订单号，存管系统的唯一订单号长度必须介于 0 和 64 之间")
	public String getCgbOrderId() {

		return cgbOrderId;
	}

	public void setCgbOrderId(String cgbOrderId) {

		this.cgbOrderId = cgbOrderId;
	}

	@Length(min = 0, max = 16, message = "交易类型，1001：网银充值，1002：快捷充值，1004：线下充值，1005：自动充值长度必须介于 0 和 16 之间")
	public String getTradingType() {

		return tradingType;
	}

	public void setTradingType(String tradingType) {

		this.tradingType = tradingType;
	}

	@Length(min = 0, max = 64, message = "交易金额长度必须介于 0 和 64 之间")
	public String getTradingAmount() {

		return tradingAmount;
	}

	public void setTradingAmount(String tradingAmount) {

		this.tradingAmount = tradingAmount;
	}

	@Length(min = 0, max = 16, message = "交易状态，订单的状态 S 为成功长度必须介于 0 和 16 之间")
	public String getTradingStatus() {

		return tradingStatus;
	}

	public void setTradingStatus(String tradingStatus) {

		this.tradingStatus = tradingStatus;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getCompletionTime() {

		return completionTime;
	}

	public void setCompletionTime(Date completionTime) {

		this.completionTime = completionTime;
	}

	@Length(min = 0, max = 16, message = "支付公司代码，交易对应的支付公司代码长度必须介于 0 和 16 之间")
	public String getPayCode() {

		return payCode;
	}

	public void setPayCode(String payCode) {

		this.payCode = payCode;
	}

	@Length(min = 0, max = 64, message = "平台用户ID长度必须介于 0 和 64 之间")
	public String getPlatformUserId() {

		return platformUserId;
	}

	public void setPlatformUserId(String platformUserId) {

		this.platformUserId = platformUserId;
	}

	public Date getBeginCompletionTime() {

		return beginCompletionTime;
	}

	public void setBeginCompletionTime(Date beginCompletionTime) {

		this.beginCompletionTime = beginCompletionTime;
	}

	public Date getEndCompletionTime() {

		return endCompletionTime;
	}

	public void setEndCompletionTime(Date endCompletionTime) {

		this.endCompletionTime = endCompletionTime;
	}

}