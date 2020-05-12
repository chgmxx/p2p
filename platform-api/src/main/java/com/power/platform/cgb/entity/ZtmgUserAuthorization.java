package com.power.platform.cgb.entity;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;

/**
 * 
 * 类: ZtmgUserAuthorization <br>
 * 描述: 中投摩根用户授权信息Entity. <br>
 * 作者: Mr.li <br>
 * 时间: 2018年11月26日 下午1:26:24
 */
public class ZtmgUserAuthorization extends DataEntity<ZtmgUserAuthorization> {

	private static final long serialVersionUID = 1L;
	private String userId; // 网贷平台唯一的用户编码
	private String merchantId; // 商户号
	private String failReason; // 失败原因
	private String status; // 状态，S：成功，F：失败，AS：受理成功
	private String grantList; // 授权列表
	private String grantAmountList; // 授权金额列表
	private String grantTimeList; // 授权期限列表
	private String signature; // 签名

	public ZtmgUserAuthorization() {

		super();
	}

	public ZtmgUserAuthorization(String id) {

		super(id);
	}

	@Length(min = 1, max = 64, message = "网贷平台唯一的用户编码长度必须介于 1 和 64 之间")
	public String getUserId() {

		return userId;
	}

	public void setUserId(String userId) {

		this.userId = userId;
	}

	@Length(min = 1, max = 64, message = "商户号长度必须介于 1 和 64 之间")
	public String getMerchantId() {

		return merchantId;
	}

	public void setMerchantId(String merchantId) {

		this.merchantId = merchantId;
	}

	@Length(min = 0, max = 256, message = "失败原因长度必须介于 0 和 256 之间")
	public String getFailReason() {

		return failReason;
	}

	public void setFailReason(String failReason) {

		this.failReason = failReason;
	}

	@Length(min = 0, max = 16, message = "状态，S：成功，F：失败，AS：受理成功长度必须介于 0 和 16 之间")
	public String getStatus() {

		return status;
	}

	public void setStatus(String status) {

		this.status = status;
	}

	@Length(min = 0, max = 64, message = "授权列表长度必须介于 0 和 64 之间")
	public String getGrantList() {

		return grantList;
	}

	public void setGrantList(String grantList) {

		this.grantList = grantList;
	}

	@Length(min = 0, max = 128, message = "授权金额列表长度必须介于 0 和 128 之间")
	public String getGrantAmountList() {

		return grantAmountList;
	}

	public void setGrantAmountList(String grantAmountList) {

		this.grantAmountList = grantAmountList;
	}

	@Length(min = 0, max = 128, message = "授权期限列表长度必须介于 0 和 128 之间")
	public String getGrantTimeList() {

		return grantTimeList;
	}

	public void setGrantTimeList(String grantTimeList) {

		this.grantTimeList = grantTimeList;
	}

	@Length(min = 0, max = 512, message = "签名长度必须介于 0 和 512 之间")
	public String getSignature() {

		return signature;
	}

	public void setSignature(String signature) {

		this.signature = signature;
	}

}