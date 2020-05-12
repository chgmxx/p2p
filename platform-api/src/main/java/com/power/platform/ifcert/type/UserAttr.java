package com.power.platform.ifcert.type;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 
 * class: UserAttr <br>
 * description: 用户属性（1-出借方／2-借款方／3-
 * 出借方＋借款方/4-自代偿
 * 平台方/5-第三方代偿/6-
 * 受托支付方）. <br>
 * author: Roy <br>
 * date: 2019年4月23日 下午4:07:40
 */
public class UserAttr {

	public static final Map<String, String> DICT = new LinkedHashMap<String, String>();

	/**
	 * 出借方.
	 */
	public static final String USER_ATTR_1 = "1";
	/**
	 * 借款方.
	 */
	public static final String USER_ATTR_2 = "2";
	/**
	 * 出借方+借款方（说明：出借方+借款方是指
	 * 客户既是出借人又是借款
	 * 人；）.
	 */
	public static final String USER_ATTR_3 = "3";
	/**
	 * 自代偿平台方（自代偿平台方是指如果
	 * 平台出现逾期或者需给出
	 * 借人支付一定金额时，由该
	 * 平台自己垫付相关金额；）.
	 */
	public static final String USER_ATTR_4 = "4";
	/**
	 * 第三方代偿.
	 */
	public static final String USER_ATTR_5 = "5";
	/**
	 * 受托支付方（；受
	 * 托支付方是指部分消费商
	 * 户（医美平台、电商平台），
	 * 具体场景为：用户在医美、
	 * 电商消费时，向p2p 平台借
	 * 款进行消费，此时医美、电
	 * 商平台即为受托支付方）.
	 */
	public static final String USER_ATTR_6 = "6";

	static {
		DICT.put(USER_ATTR_1, "出借方");
		DICT.put(USER_ATTR_2, "借款方");
		DICT.put(USER_ATTR_3, "出借方+借款方");
		DICT.put(USER_ATTR_4, "自代偿平台方");
		DICT.put(USER_ATTR_5, "第三方代偿");
		DICT.put(USER_ATTR_6, "受托支付方");
	}

}
