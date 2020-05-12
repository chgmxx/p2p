package com.power.platform.lanmao.config;

/**
 * 
 * class: RedirectUrlConfig <br>
 * description: 网关类接口，回调地址 <br>
 * author: Roy <br>
 * date: 2019年9月24日 下午5:11:00
 */
public class RedirectUrlConfig {

	/**
	 * www.cicmorgan.com（生产环境）
	 */
	// 用户预处理
	public static final String USER_PRE_TRANSACTION_REDIRECT_URL = "https://www.cicmorgan.com/svc/services/lanMaoTenderPre/redirectUserPreTransaction";
	public static final String USER_PRE_TENDER_TRANSACTION_RETURN_URL = "https://www.cicmorgan.com/invest_state.html";
	// 用户授权，拦截回调地址
	public static final String USER_AUTHORIZATION_REDIRECT_URL = "https://www.cicmorgan.com/svc/services/lanMaoTrade/redirectUserAuthorization";
	// 用户授权，页面回跳地址
	public static final String USER_AUTHORIZATION_RETURN_URL = "https://loan.cicmorgan.com/loan/a/sys/user/account";

	// 出借人账户首页
	public static final String RETURN_LANMAO_INVEST_URL = "https://www.cicmorgan.com/account_home.html";
	// 借款人账户首页
	public static final String RETURN_LANMAO_Credit_INVEST_URL = "https://loan.cicmorgan.com/loan/a/sys/user/account";

	// 出借人充值
	public static final String BACK_Recharg_BACKTO_URL_WEB = "https://www.cicmorgan.com/svc/services/callbacklmrecharge/rechargeWebNotify?backto=web";
	// 借款人充值
	public static final String BACK_Recharg_Credit_BACKTO_URL_WEB = "https://www.cicmorgan.com/svc/services/callbacklmrecharge/rechargeWebNotifyH5?backto=web";

	// 出借人提现
	public static final String BACK_WITHDRAW_BACKTO_URL_WEB = "https://www.cicmorgan.com/svc/services/callbacklmwithdraw/withdrawWebNotify?backto=web";
	// 借款人提现
	public static final String BACK_WITHDRAW_Credit_BACKTO_URL_WEB = "https://www.cicmorgan.com/svc/services/callbacklmwithdraw/withdrawWebNotifyCredit?backto=web";

	// 企业绑卡注册，业务回调地址
	public static final String ENTERPRISE_REGISTER_REDIRECT_URL = "https://www.cicmorgan.com/svc/services/lanmaoAccount/redirectEnterpriseRegister";
	// 企业绑卡注册，页面回调地址
	public static final String ENTERPRISE_REGISTER_RETURN_URL = "https://loan.cicmorgan.com/loan/a/credit/userinfo/creditUserInfo/creditUserCompanyInfo";
	// 企业绑卡，业务回调地址
	public static final String ENTERPRISE_BIND_BANKCARD = "https://www.cicmorgan.com/svc/services/lanmaoAccount/redirectEnterpriseBindBankCard";
	// 企业绑卡，页面回跳地址
	public static final String ENTERPRISE_BIND_BANKCARD_REDIRECT_URL = "https://loan.cicmorgan.com/loan/a/sys/user/account";
	// 企业信息修改，拦截回调地址
	public static final String ENTERPRISE_INFORMATION_UPDATE = "https://www.cicmorgan.com/svc/services/lanmaoAccount/redirectEnterpriseInformationUpdate";
	// 个人绑卡注册，业务回调地址
	public static final String PERSONAL_REGISTER_EXPAND_URL = "https://www.cicmorgan.com/svc/services/lanmaoAccount/redirectPersonalRegisterExpand";
	// 个人绑卡注册，页面回调地址
	public static final String ACCOUNT_HOME_URL = "https://www.cicmorgan.com/account_home.html";

	// 解绑银行卡，业务回调地址
	public static final String UNBIND_BANKCARD = "https://www.cicmorgan.com/svc/services/lanmaoAccount/redirectUntyingCard";
	// 企业解绑银行卡，页面回跳地址
	public static final String UNBIND_BANKCARD_REDIRECT_URL = "https://loan.cicmorgan.com/loan/a/sys/user/account";
	// 个人绑卡，业务回调地址
	public static final String PERSONAL_BIND_BANKCARD_EXPAND = "https://www.cicmorgan.com/svc/services/lanmaoAccount/redirectChangeBankCard";
	// 预留手机号更新，业务回调地址
	public static final String MODIFY_MOBILE_EXPAND = "https://www.cicmorgan.com/svc/services/lanmaoAccount/redirectModifyMobileExpand";
	// 用户激活，拦截回调地址
	public static final String ACTIVATE_STOCKED_USER = "https://www.cicmorgan.com/svc/services/lanmaoAccount/redirectMemberActivation";
	public static final String LOGIN_URL = "https://www.cicmorgan.com/login.html";
	// 企业激活回跳登录页
	public static final String ENTERPRISE_LOGIN_URL = "https://loan.cicmorgan.com/loan/a/login";
	
}
