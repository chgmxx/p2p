package com.power.platform.sys.type;

import java.util.LinkedHashMap;
import java.util.Map;

public class RegisterFromType {

	public static final Map<Integer, String> dict = new LinkedHashMap<Integer, String>();

	public static final int PC = 1;
	public static final int MOBILE_WEB = 2;
	public static final int ANDROID = 3;
	public static final int IOS = 4;
	public static final int WIN_PHONE = 5;

	static {
		dict.put(PC, "PC");
		dict.put(MOBILE_WEB, "手机web");
		dict.put(ANDROID, "android");
		dict.put(IOS, "ios");
		dict.put(WIN_PHONE, "window phone");
	}

}
