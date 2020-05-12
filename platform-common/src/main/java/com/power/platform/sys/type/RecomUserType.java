package com.power.platform.sys.type;

import java.util.LinkedHashMap;
import java.util.Map;

public class RecomUserType {
	public static final Map<Integer, String> dict = new LinkedHashMap<Integer, String>();
	public static final int UNKNOW = 0;
	public static final int Common_promotion = 1;
	public static final int Silver_promotion = 2;
	public static final int Gold_promotion = 3;
	static {
		dict.put(UNKNOW, "请选择...");
		dict.put(Common_promotion, "普通推广员");
		dict.put(Silver_promotion, "银牌推广员");
		dict.put(Gold_promotion, "金牌推广员");
	}
}
