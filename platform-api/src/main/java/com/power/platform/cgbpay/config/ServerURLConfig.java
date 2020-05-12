package com.power.platform.cgbpay.config;

import com.power.platform.common.config.Global;

/**
 * CGB---支付配置信息
 */
public interface ServerURLConfig {

	public static final String PLATFORM_NAME = "中投摩根"; // 平台名称.
	public static final boolean IS_REAL_TIME_PUSH = false; // 是否实时推送.
	// 正式
	public static final String BACK_OFFLINE_RECHARGE_URL = "https://www.cicmorgan.com/svc/services/callbackrecharge/offlineRechargeWebNotify"; // 借款端转账充值（PC端WEB）回调
	public static final String BACK_RECHARGE_URL = "https://www.cicmorgan.com/svc/services/callbackrecharge/rechargeWebNotify";
	public static final String BACK_RECHARGE_URL_LARGE = "https://www.cicmorgan.com/svc/services/callbackrecharge/largeRechargeWebNotify";
	public static final String BACK_WITHDRAW_URL = "https://www.cicmorgan.com/svc/services/callbackwithdraw/withdrawWebNotify";
	public static final String BACK_INVEST_URL = "https://www.cicmorgan.com/svc/services/callbackinvest/investCreateWebNotify";
	public static final String BACK_ACCOUNT_URL = "https://www.cicmorgan.com/svc/services/callback/accountCreateWebNotify";
	public static final String BACK_ACCOUNT_URL_COMPANY = "https://www.cicmorgan.com/svc/services/callback/accountCreateByCompany";
	public static final String BACK_REDPACKET_URL = "https://www.cicmorgan.com/svc/services/callbackredpacket/redPacketWebNotify";
	public static final String BACK_AUTO_URL = "https://www.cicmorgan.com/svc/services/callbackAutorization/autorizationWebNotify";
	public static final String BACK_CHANGECARD_URL = "https://www.cicmorgan.com/svc/services/callbackchange/changeBankCardWebNotify";
	public static final String BACK_UPDATEMEMBERACCOUNT_URL = "https://www.cicmorgan.com/svc/services/callback/accountUpdateByCompany";
	public static final String CGB_URL = Global.getConfig("CGB_URL");
	public static final String BACK_INVEST_URL_BACKTOWEB = "https://www.cicmorgan.com/svc/services/backto/backto?backto=investWeb";
	public static final String BACK_BACKTO_URL_WEB = "https://www.cicmorgan.com/svc/services/backto/backto?backto=web";
	public static final String BACK_BACKTO_URL_BORROWWEB = "https://www.cicmorgan.com/svc/services/backto/backto?backto=borrowWeb";
	public static final String BACK_BACKTO_URL = "https://www.cicmorgan.com/";
	public static final String BACK_BACKTO_URL_WAP = "https://www.cicmorgan.com/svc/services/backto/backto?backto=wap";
	public static final String RETURN_URL_BORROWING_WEB_AUTHORIZATION = "https://www.cicmorgan.com/svc/services/backto/backto"; // 用户授权，前台通知地址.
	public static final String BACK_TO_BORROWING_WEB_AUTHORIZATION = "https://loan.cicmorgan.com/loan/a/sys/user/account"; // 用户授权，借款端响应页面地址.
	public static final String CALLBACK_URL_BORROWING_WEB_AUTHORIZATION = "https://www.cicmorgan.com/svc/services/callbackAutorization/callbackBorrowingWebAuthorization"; // 用户授权，异步通知地址.
	public static final String CREDITANNEXFILEURL = "https://www.cicmorgan.com/upload/image/";
	public static final String RETURN_INVEST_URL = "https://www.cicmorgan.com/account_home.html";
	public static final String CREDIT_COMPANY_RETURN_URL = "https://loan.cicmorgan.com/loan/a/credit/userinfo/creditUserInfo/creditUserCompanyInfo";
	public static final String BACK_CHANGEPHONE_URL = "https://www.cicmorgan.com/svc/services/callbackchange/changeBankPhoneWebNotify";
	public static final String BACK_INVEST_NEWURL = "https://www.cicmorgan.com/svc/services/callbackinvest/userInvestCreateWebNotify";
	public static final String BACK_INVEST_URL_BACKTOWEBSTATE = "https://www.cicmorgan.com//invest_state.html";
	public static final String NEW_BACK_INVEST_URL = "https://www.cicmorgan.com/svc/services/callbackinvest/newUserInvestCreateWebNotify";
	public static final String NEW_BACK_INVEST_URL2_2_1 = "https://www.cicmorgan.com/svc/services/callbackinvest/newUserInvestCreateWebNotify2_2_1";
	public static final String BACK_REDPACKET_URL2_2_1 = "https://www.cicmorgan.com/svc/services/callbackredpacket/redPacketWebNotify2_2_1";
	public static final String VERSION = Global.getConfig("VERSION"); // 国家应急中心数据接入，接口版本号.
	public static final String SOURCE_CODE = Global.getConfig("SOURCE_CODE"); // 国家应急中心数据接入，平台编码.
	public static final String API_KEY = Global.getConfig("API_KEY"); // 国家应急中心数据接入，API KEY.
	public static final String ENDPOINT_USERINFO = Global.getConfig("ENDPOINT_USERINFO"); // 用户信息接口正式地址.
	public static final String ENDPOINT_SCATTER_INVEST = Global.getConfig("ENDPOINT_SCATTER_INVEST"); // 散标信息接口正式地址.
	public static final String ENDPOINT_SCATTER_INVEST_STATUS = Global.getConfig("ENDPOINT_SCATTER_INVEST_STATUS"); // 散标状态接口正式地址.
	public static final String ENDPOINT_REPAY_PLAN = Global.getConfig("ENDPOINT_REPAY_PLAN"); // 还款计划接口正式地址.
	public static final String ENDPOINT_CREDITOR = Global.getConfig("ENDPOINT_CREDITOR"); // 初始债权接口正式地址.
	public static final String ENDPOINT_TRANSACT = Global.getConfig("ENDPOINT_TRANSACT"); // 交易流水接口正式地址.
	public static final String ENDPOINT_LEND_PRODUCT = Global.getConfig("ENDPOINT_LEND_PRODUCT"); // 产品信息接口正式地址.
	public static final String ENDPOINT_LEND_PRODUCT_CONFIG = Global.getConfig("ENDPOINT_LEND_PRODUCT_CONFIG"); // 产品配置接口正式地址.
	public static final String ENDPOINT_LEND_PARTICULARS = Global.getConfig("ENDPOINT_LEND_PARTICULARS"); // 投资明细接口正式地址.
	public static final String RECONCILIATION_MESSAGE_URL = Global.getConfig("RECONCILIATION_MESSAGE_URL"); // 对账接口-批次异步消息接口.

}
