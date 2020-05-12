package com.power.platform.cgb.pojo;

/**
 * 
 * 类: RepayOrderList <br>
 * 描述: 还款订单. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2017年6月13日 上午10:33:01
 */
public class RepayOrder {

	// 外部子订单号.
	private String subOrderId;
	// 收款人，网贷平台唯一的用户编码.
	private String receiveUserId;
	// 还款金额，单位（分）.
	private long amount;
	// 本笔还款的交易类型：C-网贷平台佣金，P-本金，I-利息，S-分润.
	private String type;

	public String getSubOrderId() {

		return subOrderId;
	}

	public void setSubOrderId(String subOrderId) {

		this.subOrderId = subOrderId;
	}

	public String getReceiveUserId() {

		return receiveUserId;
	}

	public void setReceiveUserId(String receiveUserId) {

		this.receiveUserId = receiveUserId;
	}

	public long getAmount() {

		return amount;
	}

	public void setAmount(long amount) {

		this.amount = amount;
	}

	public String getType() {

		return type;
	}

	public void setType(String type) {

		this.type = type;
	}

}
