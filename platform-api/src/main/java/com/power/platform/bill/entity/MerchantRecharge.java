package com.power.platform.bill.entity;

import org.hibernate.validator.constraints.Length;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.power.platform.common.persistence.DataEntity;

/**
 * 平台商户对账文件Entity
 * 
 * @author lance
 * @version 2018-03-02
 */
public class MerchantRecharge extends DataEntity<MerchantRecharge> {

	private static final long serialVersionUID = 1L;
	private String cgbOrderId; // 存管订单号
	private String tradingType; // 交易类型
	private String tradingAmount; // 交易金额
	private String tradingStatus; // 交易状态
	private Date completionTime; // 完成时间
	private String payCode; // 支付公司代码
	private String platformUserId; // 平台用户ID
	private String businessSource; // 业务来源
	private Date beginCompletionTime; // 开始 完成时间
	private Date endCompletionTime; // 结束 完成时间

	public MerchantRecharge() {

		super();
	}

	public MerchantRecharge(String id) {

		super(id);
	}

	@Length(min = 0, max = 64, message = "存管订单号长度必须介于 0 和 64 之间")
	public String getCgbOrderId() {

		return cgbOrderId;
	}

	public void setCgbOrderId(String cgbOrderId) {

		this.cgbOrderId = cgbOrderId;
	}

	@Length(min = 0, max = 16, message = "交易类型长度必须介于 0 和 16 之间")
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

	@Length(min = 0, max = 16, message = "交易状态长度必须介于 0 和 16 之间")
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

	@Length(min = 0, max = 16, message = "支付公司代码长度必须介于 0 和 16 之间")
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

	@Length(min = 0, max = 16, message = "业务来源长度必须介于 0 和 16 之间")
	public String getBusinessSource() {

		return businessSource;
	}

	public void setBusinessSource(String businessSource) {

		this.businessSource = businessSource;
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