package com.power.platform.pay.config;

/**
* 支付配置信息
* @author guoyx e-mail:guoyx@lianlian.com
* @date:2013-6-25 下午01:45:06
* @version :1.0
*
*/
public interface ServerURLConfig{
	  public static final String PAY_URL = "https://yintong.com.cn/payment/authpay.htm"; 
	  public static final String QUERY_USER_BANKCARD_URL = "https://yintong.com.cn/traderapi/userbankcard.htm";
	  public static final String QUERY_BANKCARD_URL = "https://yintong.com.cn/traderapi/bankcardquery.htm";
	  public static final String CASH_URL = "https://yintong.com.cn/traderapi/cardandpay.htm";
	  public static final String RECHARGE_QUERY = "https://yintong.com.cn/traderapi/orderquery.htm";
	  public static final String CASH_QUERY = "https://yintong.com.cn/traderapi/orderquery.htm";
	  public static final String BANK_GATE_WAY_URL = "https://yintong.com.cn/payment/bankgateway.htm";
	  public static final String BANK_GATEWAY_URL = "https://yintong.com.cn/payment/bankgateway.htm";
	  public static final String MWEB_RECHARGE_QUERY = "https://yintong.com.cn/llpayh5/payment.htm";
	  public static final String RECHARGE_NOTIFY_URL = "https://erp.cicmorgan.com/erp/llpay/rechargenotify/notify";
}
