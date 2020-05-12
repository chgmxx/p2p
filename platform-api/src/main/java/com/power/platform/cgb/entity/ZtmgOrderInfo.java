package com.power.platform.cgb.entity;

import java.util.Date;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;

/**
 * 中投摩根业务订单信息Entity
 * 
 * @author lance
 * @version 2018-02-06
 */
public class ZtmgOrderInfo extends DataEntity<ZtmgOrderInfo> {

	private static final long serialVersionUID = 1L;
	private String merchantId; // 商户号.
	private String orderId; // 订单号.
	private String status; // 回调状态，S：成功，F：失败，AS：受理成功.
	private String signature; // 签名.
	private String state; // 还款状态，1：未还，2：已还.
	private String type; // 类型，1：借款户，2：代偿户.

	private Date beginUpdateDate; // 更新开始时间.
	private Date endUpdateDate; // 更新结束时间.

	public ZtmgOrderInfo() {

		super();
	}

	public ZtmgOrderInfo(String id) {

		super(id);
	}

	@Length(min = 0, max = 64, message = "商户号长度必须介于 0 和 64 之间")
	public String getMerchantId() {

		return merchantId;
	}

	public void setMerchantId(String merchantId) {

		this.merchantId = merchantId;
	}

	@Length(min = 0, max = 64, message = "订单号长度必须介于 0 和 64 之间")
	public String getOrderId() {

		return orderId;
	}

	public void setOrderId(String orderId) {

		this.orderId = orderId;
	}

	@Length(min = 0, max = 16, message = "回调状态，S：成功，F：失败，AS：受理成功长度必须介于 0 和 16 之间")
	public String getStatus() {

		return status;
	}

	public void setStatus(String status) {

		this.status = status;
	}

	@Length(min = 0, max = 512, message = "签名长度必须介于 0 和 512 之间")
	public String getSignature() {

		return signature;
	}

	public void setSignature(String signature) {

		this.signature = signature;
	}

	@Length(min = 0, max = 16, message = "还款状态，1：未还，2：已还长度必须介于 0 和 16 之间")
	public String getState() {

		return state;
	}

	public void setState(String state) {

		this.state = state;
	}

	public String getType() {

		return type;
	}

	public void setType(String type) {

		this.type = type;
	}

	public Date getBeginUpdateDate() {

		return beginUpdateDate;
	}

	public void setBeginUpdateDate(Date beginUpdateDate) {

		this.beginUpdateDate = beginUpdateDate;
	}

	public Date getEndUpdateDate() {

		return endUpdateDate;
	}

	public void setEndUpdateDate(Date endUpdateDate) {

		this.endUpdateDate = endUpdateDate;
	}

}