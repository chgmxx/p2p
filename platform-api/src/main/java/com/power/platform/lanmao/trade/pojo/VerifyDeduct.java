package com.power.platform.lanmao.trade.pojo;

import java.io.Serializable;

/**
 * 
 * class: VerifyDeduct <br>
 * description: 验密扣费参数列表，用于各种场景下平台向用户收取大额费用，需要跳转到存管系统页面，用户主动进行密码验证，验证成功以后
 * 扣费成功 。<br>
 * R1. 扣费金额不超过用户可用余额<br>
 * R2.出款方不能为平台 功能 账户（例如平台 收入 账户、平台营销款账户 、 平台 派息账户 、 平台总账户...）<br>
 * R3.收款方只能是平台 功能 账户 <br>
 * author: Roy <br>
 * date: 2019年9月25日 上午9:16:18
 */
public class VerifyDeduct implements Serializable {

	/**  */
	private static final long serialVersionUID = 1L;
	private String requestNo;
	private String platformUserNo;
	private String amount;
	private String customDefine;
	private String targetPlatformUserNo;
	private String redirectUrl;
	private String expired;

	private String originalFreezeRequestNo;

	public String getRequestNo() {

		return requestNo;
	}

	public void setRequestNo(String requestNo) {

		this.requestNo = requestNo;
	}

	public String getPlatformUserNo() {

		return platformUserNo;
	}

	public void setPlatformUserNo(String platformUserNo) {

		this.platformUserNo = platformUserNo;
	}

	public String getAmount() {

		return amount;
	}

	public void setAmount(String amount) {

		this.amount = amount;
	}

	public String getCustomDefine() {

		return customDefine;
	}

	public void setCustomDefine(String customDefine) {

		this.customDefine = customDefine;
	}

	public String getTargetPlatformUserNo() {

		return targetPlatformUserNo;
	}

	public void setTargetPlatformUserNo(String targetPlatformUserNo) {

		this.targetPlatformUserNo = targetPlatformUserNo;
	}

	public String getRedirectUrl() {

		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {

		this.redirectUrl = redirectUrl;
	}

	public String getExpired() {

		return expired;
	}

	public void setExpired(String expired) {

		this.expired = expired;
	}

	public String getOriginalFreezeRequestNo() {

		return originalFreezeRequestNo;
	}

	public void setOriginalFreezeRequestNo(String originalFreezeRequestNo) {

		this.originalFreezeRequestNo = originalFreezeRequestNo;
	}

}
