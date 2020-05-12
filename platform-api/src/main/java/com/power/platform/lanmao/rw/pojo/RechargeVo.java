package com.power.platform.lanmao.rw.pojo;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * 用户在网贷平台发起充值请求，平台调用此接口引导用户跳转至存管页面完成充值
 * @author chenhj / www.ant-loiter.com
 * 测试
 */
public class RechargeVo extends BaseGatewayVo implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String platformUserNo ; // 平台用户编号
	private String userDevice;// 用户终端设备类型，见枚举【终端类型】，如果该参数为空，则系统会根据请求 head中 userAgent 信息判断是 PC 还是移动设备
	public String getUserDevice() {
		return userDevice;
	}
	public void setUserDevice(String userDevice) {
		this.userDevice = userDevice;
	}
	private String requestNo ; // 请求流水号
	private Double amount  ;  // 充值金额
	private Double commission;  //平台佣金
	private String expectPayCompany; //偏好支付公司，见【支付公司】
	private String rechargeWay;// 【支付方式】，支持网银（WEB）、快捷支付（SWIFT）
	private String bankcode; //  【见银行编码】若支付方式为快捷支付，此处必填；若支付方式为网银，此处可以不填；网银支付方式下，若此处填写，则直接跳转至银行页面，不填则跳转至支付公司收银台页面；
	private String payType ;// 若支付方式填写为网银，且对【银行编码】进行了填写，则此处必填
	private String authtradeType ;//【交易类型】，若想实现充值+出借单次授权，则此参数必传，固定“TENDER
	private Double authtenderAmount ; // 授权出借金额，充值成功后可操作对应金额范围内的出借授权预处理；若传入了【交易类型】，则此参数必传；
	private String projectNo;// 标的号；若传入了【交易类型】，则此参数必传
	private String swiftRoute;// 商户自定义快捷路由，见【快捷路由】,不传值则走存管系统默认的快捷路由
	private String redirectUrl;// 页面回跳 URL 必填
	private String expired;// 超过此时间即页面过期
	private String callbackMode;// 快捷充值回调模式，如传入 DIRECT_CALLBACK，则订单支付不论成功、失败、处理中均直接同步、异步通知商户；未传入订单在支付成功时通知商户（支付失败也会通知商户）
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
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public Double getCommission() {
		return commission;
	}
	public void setCommission(Double commission) {
		this.commission = commission;
	}
	public String getExpectPayCompany() {
		return expectPayCompany;
	}
	public void setExpectPayCompany(String expectPayCompany) {
		this.expectPayCompany = expectPayCompany;
	}
	public String getRechargeWay() {
		return rechargeWay;
	}
	public void setRechargeWay(String rechargeWay) {
		this.rechargeWay = rechargeWay;
	}
	public String getBankcode() {
		return bankcode;
	}
	public void setBankcode(String bankcode) {
		this.bankcode = bankcode;
	}
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
	public String getAuthtradeType() {
		return authtradeType;
	}
	public void setAuthtradeType(String authtradeType) {
		this.authtradeType = authtradeType;
	}
	public Double getAuthtenderAmount() {
		return authtenderAmount;
	}
	public void setAuthtenderAmount(Double authtenderAmount) {
		this.authtenderAmount = authtenderAmount;
	}
	public String getProjectNo() {
		return projectNo;
	}
	public void setProjectNo(String projectNo) {
		this.projectNo = projectNo;
	}
	public String getSwiftRoute() {
		return swiftRoute;
	}
	public void setSwiftRoute(String swiftRoute) {
		this.swiftRoute = swiftRoute;
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
	public String getCallbackMode() {
		return callbackMode;
	}
	public void setCallbackMode(String callbackMode) {
		this.callbackMode = callbackMode;
	}
	

	
}
