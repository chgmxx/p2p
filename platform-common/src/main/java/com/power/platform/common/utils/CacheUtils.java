/**
 * Copyright &copy; 2012-2014 <a
 * href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.common.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.power.platform.cache.CacheException;

/**
 * Cache工具类
 * 
 * @author ThinkGem
 * @version 2013-5-29
 */
public class CacheUtils {

	private static final String SYS_CACHE = "sysCache";

	private static Map<String, Object> map;

	/**
	 * 获取SYS_CACHE缓存
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 * @throws CacheException
	 * @throws TimeoutException
	 */
	public static Object get(String key) {

		try {
			map = getSysMap(SYS_CACHE);
			if (null == map) {
				map = new HashMap<String, Object>();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map.get(key);
	}

	/**
	 * 写入SYS_CACHE缓存
	 * 
	 * @param key
	 * @return
	 */
	public static void put(String key, Object value) {

		try {
			map = getSysMap(SYS_CACHE);
			if (null == map) {
				map = new HashMap<String, Object>();
			}
			map.put(key, value);
			MemCachedUtis.getMemCached().set(SYS_CACHE, map);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 从SYS_CACHE缓存中移除
	 * 
	 * @param key
	 * @return
	 */
	public static void remove(String key) {

		try {
			map = getSysMap(SYS_CACHE);
			if (null == map) {
				map = new HashMap<String, Object>();
			}
			map.remove(key);
			MemCachedUtis.getMemCached().set(SYS_CACHE, map);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取缓存
	 * 
	 * @param cacheName
	 * @param key
	 * @return
	 */
	public static Object get(String cacheName, String key) {

		try {
			map = getSysMap(cacheName);
			if (null == map) {
				map = new HashMap<String, Object>();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map.get(key);
	}

	/**
	 * 写入缓存
	 * 
	 * @param cacheName
	 * @param key
	 * @param value
	 */
	public static void put(String cacheName, String key, Object value) {

		try {
			map = getSysMap(cacheName);
			if (null == map) {
				map = new HashMap<String, Object>();
			}
			map.put(key, value);
			MemCachedUtis.getMemCached().set(cacheName, map);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 从缓存中移除
	 * 
	 * @param cacheName
	 * @param key
	 */
	public static void remove(String cacheName, String key) {

		try {
			map = getSysMap(cacheName);
			if (null == map) {
				map = new HashMap<String, Object>();
			}
			map.remove(key);
			MemCachedUtis.getMemCached().set(cacheName, map);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void removeByCacheName(String cacheName) {

		try {
			MemCachedUtis.getMemCached().delete(cacheName);
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (CacheException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Map<String, Object> getSysMap(String cacheName) {

		try {
			map = MemCachedUtis.getMemCached().get(cacheName);
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (CacheException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

}
