package com.power.platform.sys.type;

import java.util.LinkedHashMap;
import java.util.Map;

public class UserType {

	public static final Map<Integer, String> dict = new LinkedHashMap<Integer, String>();
	
	public static final int BID = 1;
	public static final int LOAN = 2;
	
	static {
		dict.put(BID, "投资");
		dict.put(LOAN, "融资");
	}

}
