package com.power.platform.lanmao.trade.pojo;

import java.io.Serializable;

/**
 * 
 * class: UserPreTransaction <br>
 * description: 用户预处理参数列表 <br>
 * author: Roy <br>
 * date: 2019年9月21日 上午11:22:19
 */
public class UserPreTransaction implements Serializable {

	/**  */
	private static final long serialVersionUID = 1L;
	private String requestNo; // 请求流水号
	private String platformUserNo; // 出款人平台用户编号
	private String bizType; // 根据业务的不同，需要传入不同的值，见【预处理业务类型】。
	private String amount; // 冻结金额
	private String preMarketingAmount; // 预备使用的红包金额，只记录不冻结，仅限出借业务类型
	private String expired; // 超过此时间即页面过期
	private String remark; // 备注
	private String redirectUrl; // 页面回跳URL
	private String projectNo; // 标的号
	private String share; // 购买债转份额，业务类型为债权认购时，需要传此参数
	private String creditsaleRequestNo; // 债权出让请求流水号，只有债权认购业务需传此参数

	private String userDevice; // N 用户终端设备类型，见枚举【终端类型】，如果该参数为空，则系统会根据请求head中userAgent信息判断是PC还是移动设备

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

	public String getBizType() {

		return bizType;
	}

	public void setBizType(String bizType) {

		this.bizType = bizType;
	}

	public String getAmount() {

		return amount;
	}

	public void setAmount(String amount) {

		this.amount = amount;
	}

	public String getPreMarketingAmount() {

		return preMarketingAmount;
	}

	public void setPreMarketingAmount(String preMarketingAmount) {

		this.preMarketingAmount = preMarketingAmount;
	}

	public String getExpired() {

		return expired;
	}

	public void setExpired(String expired) {

		this.expired = expired;
	}

	public String getRemark() {

		return remark;
	}

	public void setRemark(String remark) {

		this.remark = remark;
	}

	public String getRedirectUrl() {

		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {

		this.redirectUrl = redirectUrl;
	}

	public String getProjectNo() {

		return projectNo;
	}

	public void setProjectNo(String projectNo) {

		this.projectNo = projectNo;
	}

	public String getShare() {

		return share;
	}

	public void setShare(String share) {

		this.share = share;
	}

	public String getCreditsaleRequestNo() {

		return creditsaleRequestNo;
	}

	public void setCreditsaleRequestNo(String creditsaleRequestNo) {

		this.creditsaleRequestNo = creditsaleRequestNo;
	}

	public String getUserDevice() {

		return userDevice;
	}

	public void setUserDevice(String userDevice) {

		this.userDevice = userDevice;
	}

}
