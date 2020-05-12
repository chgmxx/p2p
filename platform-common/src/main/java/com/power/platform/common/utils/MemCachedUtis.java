package com.power.platform.common.utils;


import java.util.HashMap;
import java.util.Map;

import com.power.platform.cache.Cache;
import com.power.platform.cache.CacheFactory;

public class MemCachedUtis {
	public static Cache getMemCached() throws Exception {
		CacheFactory factory = SpringContextHolder.getBean(CacheFactory.class);
		Cache cache = factory.getObject();
		return cache;
	}
	
	public static void main(String[] args) {
		Map<String, Object> map =new HashMap<String, Object>();
		map.put("ss", "sssssss");
		map.put("aa", "sssssss111");
		System.out.println(map.get("ss"));
		System.out.println(map.get("aa"));
		map.remove("ss");
		System.out.println(map.get("ss"));
		System.out.println(map.get("aa"));
	}
	
}
