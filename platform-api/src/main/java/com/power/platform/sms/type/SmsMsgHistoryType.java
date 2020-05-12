package com.power.platform.sms.type;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 
 * 类: SmsMsgHistoryType <br>
 * 描述: 短信验证码历史类型. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年4月26日 下午5:19:21
 */
public class SmsMsgHistoryType {

	public static final Map<Integer, String> dict = new LinkedHashMap<Integer, String>();

	/**
	 * 平台注册.
	 */
	public static final Integer PLATFORM_REGISTER = 1;
	/**
	 * 找回登陆密码.
	 */
	public static final Integer FIND_LOGIN_PWD = 2;
	/**
	 * 修改登陆密码.
	 */
	public static final Integer MODIFY_LOGIN_PWD = 3;
	/**
	 * 设置交易密码.
	 */
	public static final Integer SET_TRADING_PWD = 4;
	/**
	 * 找回交易密码.
	 */
	public static final Integer FIND_TRADING_PWD = 5;
	/**
	 * 修改交易密码.
	 */
	public static final Integer MODIFY_TRADING_PWD = 6;
	/**
	 * 绑定银行卡.
	 */
	public static final Integer BINDING_BANK = 7;

	static {
		dict.put(PLATFORM_REGISTER, "平台注册");
		dict.put(FIND_LOGIN_PWD, "找回登陆密码");
		dict.put(MODIFY_LOGIN_PWD, "修改登陆密码");
		dict.put(SET_TRADING_PWD, "设置交易密码");
		dict.put(FIND_TRADING_PWD, "找回交易密码");
		dict.put(MODIFY_TRADING_PWD, "修改交易密码");
		dict.put(BINDING_BANK, "绑定银行卡");
	}

}
