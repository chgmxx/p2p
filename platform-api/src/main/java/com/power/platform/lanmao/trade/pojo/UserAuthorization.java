package com.power.platform.lanmao.trade.pojo;

import java.io.Serializable;

/**
 * 
 * class: UserAuthorization <br>
 * description: 用户授权参数列表 <br>
 * author: Roy <br>
 * date: 2019年9月24日 上午10:27:27
 */
public class UserAuthorization implements Serializable {

	/**  */
	private static final long serialVersionUID = 1L;
	private String platformUserNo; // Y 平台用户编号
	private String requestNo; // Y 请求流水号
	private String authList; // Y 见【用户授权列表】此处可传一个或多个值，传多个值用“ “,,”英文半角逗号分隔 。
	private String amount; // N 授权金额
	private String failTime; // N 授权截止期限
	private String redirectUrl; // Y 页面回跳URL

	// 授权预处理，amount:冻结金额
	private String originalRechargeNo; // N 关联充值请求流水号（原充值成功请求流水号）
	private String bizType; // Y 见【预处理业务类型】若传入关联请求流水号，则固定为TENDER
	private String preMarketingAmount; // N 预备使用的红包金额，只记录不冻结，仅限出借业务类型
	private String remark; // N 备注
	private String projectNo; // Y 标的号,若传入关联充值请求流水号，则标的号固定为充值请求传入的标的号
	private String share; // N 购买债转份额，业务类型为债权认购时，需要传此参数
	private String creditsaleRequestNo; // 债权出让请求流水号，只有债权认购业务需填此参数

	public String getPlatformUserNo() {

		return platformUserNo;
	}

	public void setPlatformUserNo(String platformUserNo) {

		this.platformUserNo = platformUserNo;
	}

	public String getRequestNo() {

		return requestNo;
	}

	public void setRequestNo(String requestNo) {

		this.requestNo = requestNo;
	}

	public String getAuthList() {

		return authList;
	}

	public void setAuthList(String authList) {

		this.authList = authList;
	}

	public String getAmount() {

		return amount;
	}

	public void setAmount(String amount) {

		this.amount = amount;
	}

	public String getFailTime() {

		return failTime;
	}

	public void setFailTime(String failTime) {

		this.failTime = failTime;
	}

	public String getRedirectUrl() {

		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {

		this.redirectUrl = redirectUrl;
	}

	public String getOriginalRechargeNo() {

		return originalRechargeNo;
	}

	public void setOriginalRechargeNo(String originalRechargeNo) {

		this.originalRechargeNo = originalRechargeNo;
	}

	public String getBizType() {

		return bizType;
	}

	public void setBizType(String bizType) {

		this.bizType = bizType;
	}

	public String getPreMarketingAmount() {

		return preMarketingAmount;
	}

	public void setPreMarketingAmount(String preMarketingAmount) {

		this.preMarketingAmount = preMarketingAmount;
	}

	public String getRemark() {

		return remark;
	}

	public void setRemark(String remark) {

		this.remark = remark;
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

}
