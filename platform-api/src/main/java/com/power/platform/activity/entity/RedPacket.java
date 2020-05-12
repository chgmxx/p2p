package com.power.platform.activity.entity;

import com.power.platform.common.persistence.DataEntity;

public class RedPacket  extends DataEntity<RedPacket>{

	private static final long serialVersionUID = 1L;
	
	/**
	 * 返利
	 */
	private String amount;//金额，单位（分）
	private String bizType;//bizType枚举值：8001-返利 8003-红包
	private String payUserId;//出款方，网贷平台唯一的用户编码
	private String receiveUserId;//收款方，网贷平台唯一的用户编码
	private String subOrderId;//子订单Id
	
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getBizType() {
		return bizType;
	}
	public void setBizType(String bizType) {
		this.bizType = bizType;
	}
	public String getPayUserId() {
		return payUserId;
	}
	public void setPayUserId(String payUserId) {
		this.payUserId = payUserId;
	}
	public String getReceiveUserId() {
		return receiveUserId;
	}
	public void setReceiveUserId(String receiveUserId) {
		this.receiveUserId = receiveUserId;
	}
	public String getSubOrderId() {
		return subOrderId;
	}
	public void setSubOrderId(String subOrderId) {
		this.subOrderId = subOrderId;
	}
	
	
}
