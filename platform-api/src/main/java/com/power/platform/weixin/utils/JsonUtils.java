package com.power.platform.weixin.utils;

import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

public class JsonUtils {
	private static ObjectMapper mapper = new ObjectMapper();
	public static String objectToJson(Object o){
		try {
			return mapper.writeValueAsString(o);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public static Map jsonToMap(String json){
		try {
			return mapper.readValue(json, Map.class);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
