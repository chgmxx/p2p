package com.power.platform.lanmao.type;

import java.util.LinkedHashMap;

import java.util.Map;
/**
 * 
 * class: AccessTypeEnum  <br>
 * description: 鉴权通过类型 <br>
 * author: Mr.fu <br>
 * date: 2019年9月19日 下午8:55:26
 */
public class WhiteStatusEnum {

	public static final Map<String, String> dict = new LinkedHashMap<String, String>();
	public static final String WHITE = "0";
	public static final String BLACK = "1";
	public static final String GRAY = "2";

	static {
		dict.put(WHITE, "白名单");
		dict.put(BLACK, "黑名单");
		dict.put(GRAY, "灰名单");
	}
}
