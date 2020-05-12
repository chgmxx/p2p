package com.power.platform.cgb.pojo;

/**
 * 
 * 类: ShareProfitOrderList <br>
 * 描述: 分润订单. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2017年5月23日 下午2:08:33
 */
public class ShareProfitOrder {

	private String receiveUserId; // 分润收款用户，网贷平台唯一的用户编码.
	private String subOrderId; // 分润订单Id.
	private Long amount; // 分润金额，单位（分）货币类型与主单一致.

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

	public Long getAmount() {

		return amount;
	}

	public void setAmount(Long amount) {

		this.amount = amount;
	}

}
