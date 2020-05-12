package com.power.platform.lanmao.type;

/**
 * 
 * class: ServiceNameEnum <br>
 * description: 接口名称 <br>
 * author: Roy <br>
 * date: 2019年9月20日 上午11:51:13
 */
public enum ServiceNameEnum {

	/**
	 * PERSONAL_REGISTER_EXPAND:个人绑卡注册
	 */
	PERSONAL_REGISTER_EXPAND("PERSONAL_REGISTER_EXPAND", "个人绑卡注册"),
	/**
	 * ENTERPRISE_REGISTER:企业绑卡注册
	 */
	ENTERPRISE_REGISTER("ENTERPRISE_REGISTER", "企业绑卡注册"),
	/**
	 * PERSONAL_BIND_BANKCARD_EXPAND:个人绑卡
	 */
	PERSONAL_BIND_BANKCARD_EXPAND("PERSONAL_BIND_BANKCARD_EXPAND", "个人绑卡"),
	/**
	 * ENTERPRISE_BIND_BANKCARD:企业绑卡
	 */
	ENTERPRISE_BIND_BANKCARD("ENTERPRISE_BIND_BANKCARD", "企业绑卡"),
	/**
	 * UNBIND_BANKCARD:解绑银行卡
	 */
	UNBIND_BANKCARD("UNBIND_BANKCARD", "解绑银行卡"),
	/**
	 * RESET_PASSWORD:修改密码
	 */
	RESET_PASSWORD("RESET_PASSWORD", "修改密码"),
	/**
	 * CHECK_PASSWORD:验证密码
	 */
	CHECK_PASSWORD("CHECK_PASSWORD", "验证密码"),
	/**
	 * MODIFY_MOBILE_EXPAND:预留手机号更新
	 */
	MODIFY_MOBILE_EXPAND("MODIFY_MOBILE_EXPAND", "预留手机号更新"),
	/**
	 * ENTERPRISE_INFORMATION_UPDATE:企业信息修改
	 */
	ENTERPRISE_INFORMATION_UPDATE("ENTERPRISE_INFORMATION_UPDATE", "企业信息修改"),
	/**
	 * ACTIVATE_STOCKED_USER:会员激活
	 */
	ACTIVATE_STOCKED_USER("ACTIVATE_STOCKED_USER", "会员激活"),
	/**
	 * RECHARGE:充值
	 */
	RECHARGE("RECHARGE", "充值"),
	/**
	 * BACKROLL_RECHARGE:转账充值失败回调的ServiceName
	 */
	BACKROLL_RECHARGE("BACKROLL_RECHARGE", "转账充值异常"),
	/**
	 * WITHDRAW:提现
	 */
	WITHDRAW("WITHDRAW", "提现"),
	/**
	 * CONFIRM_WITHDRAW:提现确认
	 */
	CONFIRM_WITHDRAW("CONFIRM_WITHDRAW", "提现确认"),
	/**
	 * CANCEL_WITHDRAW:取消提现
	 */
	CANCEL_WITHDRAW("CANCEL_WITHDRAW", "取消提现"),
	/**
	 * INTERCEPT_WITHDRAW:提现拦截
	 */
	INTERCEPT_WITHDRAW("INTERCEPT_WITHDRAW", "提现拦截"),
	/**
	 * ADJUST_URGENT_BALANCE:调整平台垫资额度
	 */
	ADJUST_URGENT_BALANCE("ADJUST_URGENT_BALANCE", "调整平台垫资额度"),
	/**
	 * ESTABLISH_PROJECT:创建标的
	 */
	ESTABLISH_PROJECT("ESTABLISH_PROJECT", "创建标的"),
	/**
	 * MODIFY_PROJECT:变更标的
	 */
	MODIFY_PROJECT("MODIFY_PROJECT", "变更标的"),
	/**
	 * USER_PRE_TRANSACTION:用户预处理
	 */
	USER_PRE_TRANSACTION("USER_PRE_TRANSACTION", "用户预处理"),
	/**
	 * CANCEL_PRE_TRANSACTION:预处理取消
	 */
	CANCEL_PRE_TRANSACTION("CANCEL_PRE_TRANSACTION", "预处理取消"),
	/**
	 * SYNC_TRANSACTION:单笔交易
	 */
	SYNC_TRANSACTION("SYNC_TRANSACTION", "单笔交易"),
	/**
	 * ASYNC_TRANSACTION:批量交易
	 */
	ASYNC_TRANSACTION("ASYNC_TRANSACTION", "批量交易"),
	/**
	 * DEBENTURE_SALE:单笔债权出让
	 */
	DEBENTURE_SALE("DEBENTURE_SALE", "单笔债权出让"),
	/**
	 * CANCEL_DEBENTURE_SALE:取消债权出让
	 */
	CANCEL_DEBENTURE_SALE("CANCEL_DEBENTURE_SALE", "取消债权出让"),
	/**
	 * USER_AUTHORIZATION:用户授权
	 */
	USER_AUTHORIZATION("USER_AUTHORIZATION", "用户授权"),
	/**
	 * CANCEL_USER_AUTHORIZATION:取消用户授权
	 */
	CANCEL_USER_AUTHORIZATION("CANCEL_USER_AUTHORIZATION", "取消用户授权"),
	/**
	 * USER_AUTO_PRE_TRANSACTION:授权预处理
	 */
	USER_AUTO_PRE_TRANSACTION("USER_AUTO_PRE_TRANSACTION", "授权预处理"),
	/**
	 * VERIFY_DEDUCT:验密扣费
	 */
	VERIFY_DEDUCT("VERIFY_DEDUCT", "验密扣费"),
	/**
	 * FREEZE:资金冻结
	 */
	FREEZE("FREEZE", "资金冻结"),
	/**
	 * UNFREEZE:资金解冻
	 */
	UNFREEZE("UNFREEZE", "资金解冻"),
	/**
	 * UNFREEZE_TRADE_PASSWORD:交易密码解冻
	 */
	UNFREEZE_TRADE_PASSWORD("UNFREEZE_TRADE_PASSWORD", "交易密码解冻"),
	/**
	 * DOWNLOAD_CHECKFILE:对账文件下载
	 */
	DOWNLOAD_CHECKFILE("DOWNLOAD_CHECKFILE", "对账文件下载"),
	/**
	 * CONFIRM_CHECKFILE:对账文件确认
	 */
	CONFIRM_CHECKFILE("CONFIRM_CHECKFILE", "对账文件确认"),
	/**
	 * QUERY_USER_INFORMATION:用户信息查询
	 */
	QUERY_USER_INFORMATION("QUERY_USER_INFORMATION", "用户信息查询"),
	/**
	 * QUERY_TRANSACTION:单笔交易查询
	 */
	QUERY_TRANSACTION("QUERY_TRANSACTION", "单笔交易查询"),
	/**
	 * QUERY_PROJECT_INFORMATION:标的信息查询
	 */
	QUERY_PROJECT_INFORMATION("QUERY_PROJECT_INFORMATION", "标的信息查询"),
	/**
	 * ONLINE_WHITELIST_ADD:网银转账充值白名单添加
	 */
	ONLINE_WHITELIST_ADD("ONLINE_WHITELIST_ADD", "网银转账充值白名单添加"),
	/**
	 * ONLINE_WHITELIST_DEL:网银转账充值白名单删除
	 */
	ONLINE_WHITELIST_DEL("ONLINE_WHITELIST_DEL", "网银转账充值白名单删除"),
	/**
	 * QUERY_TRANSACTION_RECORD:网银转账充值记录查询
	 */
	QUERY_TRANSACTION_RECORD("QUERY_TRANSACTION_RECORD", "网银转账充值记录查询"),
	/**
	 * REMIT_QUERY_SERVICE:网银转账充值代付查询
	 */
	REMIT_QUERY_SERVICE("REMIT_QUERY_SERVICE", "网银转账充值代付查询");

	private String value;
	private String text;

	private ServiceNameEnum(String value, String text) {

		this.value = value;
		this.text = text;
	}

	public String getValue() {

		return value;
	}

	public void setValue(String value) {

		this.value = value;
	}

	public String getText() {

		return text;
	}

	public void setText(String text) {

		this.text = text;
	}
	public static String getTextByValue(String value) {

		  for (ServiceNameEnum v : ServiceNameEnum.values()) {
		   if (v.getValue().equals(value)) {
		    return v.getText();
		   }
		  }
		  return "未知结果";
		 }
}
