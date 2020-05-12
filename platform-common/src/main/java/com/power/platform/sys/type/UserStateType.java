package com.power.platform.sys.type;

import java.util.LinkedHashMap;
import java.util.Map;

public class UserStateType {
	public static final Map<Integer, String> dict = new LinkedHashMap<Integer, String>();
	public static final int NORMAL = 2;
	public static final int FORBIDDEN = 1;
	public static final int DELETED = 0;
	public static final int PRE_REGISTRATION = 3;
	
	
	static {
		dict.put(DELETED, "已销户");
		dict.put(FORBIDDEN, "已禁用");
		dict.put(NORMAL, "正常");
		dict.put(PRE_REGISTRATION, "预注册");
	}
}