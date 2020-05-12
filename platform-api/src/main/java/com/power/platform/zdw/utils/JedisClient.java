package com.power.platform.zdw.utils;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.power.platform.common.utils.SpringContextHolder;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class JedisClient {

	private static final Logger logger = LoggerFactory.getLogger(JedisClient.class);

	private static JedisPool jedisPool = SpringContextHolder.getBean(JedisPool.class);

	private static Jedis jedis = null;

	public static Jedis jedis_object = null;
	// @Value("${spring.redis.host}")
	// private static String host;
	// @Value("${spring.redis.password}")
	// private static String password;
	// @Value("${spring.redis.port}")
	// private static Integer port;
	// private static String host = "47.106.10.57";
	// private static String host = "127.0.0.1";// redis配置的ip地址
	// private static String password = "9drur!@34";
	// private static Integer port = 6379;// redis配置的端口号

	static {
		if (jedisPool == null) {
			logger.info("Redis连接池为[null]...");
			// JedisPoolConfig config = new JedisPoolConfig();
			// 设置最大连接数
			// config.setMaxTotal(500);
			// 设置最大空闲数
			// config.setMaxIdle(20);
			// 设置最小空闲数
			// config.setMinIdle(8);
			// 设置超时时间
			// config.setMaxWaitMillis(3000);
			// Idle时进行连接扫描
			// config.setTestWhileIdle(true);
			// 表示idle object evitor两次扫描之间要sleep的毫秒数
			// config.setTimeBetweenEvictionRunsMillis(30000);
			// 表示idle object evitor每次扫描的最多的对象数
			// config.setNumTestsPerEvictionRun(10);
			// 表示一个对象至少停留在idle状态的最短时间，然后才能被idle object evitor扫描并驱逐；这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义
			// config.setMinEvictableIdleTimeMillis(60000);
			// 初始化连接池
			// jedisPool = new JedisPool(config, host, port);
			// jedis_object = new Jedis(host, port);
		}
	}

	private JedisClient() {

	}

	private static Jedis getJedisInstance() {

		try {
			if (null == jedis) {
				jedis = jedisPool.getResource();
				// jedis.auth(password);
			}
		} catch (Exception e) {
			logger.error("实例化jedis失败.........", e);
		}
		return jedis;
	}

	/**
	 * 向缓存中设置字符串内容
	 *
	 * @author HHR
	 * @date 2017年8月29日 下午3:19:41
	 */
	public static boolean set(String key, org.apache.http.client.CookieStore cookieStore) throws Exception {

		List<org.apache.http.cookie.Cookie> cookies = cookieStore.getCookies();
		JSONArray cookiesJson = new JSONArray();
		for (int i = 0; i < cookies.size(); i++) {
			cookiesJson.add(JSON.toJSONString(cookies.get(i)));
		}
		Jedis jedis = null;
		try {
			jedis = getJedisInstance();
			jedis.set(key, cookiesJson.toJSONString());
			return true;
		} catch (Exception e) {
			logger.error("redis set方法失败...key=" + key + "  value=" + cookiesJson, e);
		} finally {
			jedisPool.destroy();
		}
		return false;
	}

	/**
	 * 向缓存中设置字符串内容 ,设置过期时间
	 *
	 * @author HHR
	 * @date 2017年8月29日 下午3:19:41
	 */
	public static boolean set(String key, org.apache.http.client.CookieStore cookieStore, Integer seconds) throws Exception {

		List<org.apache.http.cookie.Cookie> cookies = cookieStore.getCookies();
		JSONArray cookiesJson = new JSONArray();
		for (int i = 0; i < cookies.size(); i++) {
			cookiesJson.add(JSON.toJSONString(cookies.get(i)));
		}
		Jedis jedis = null;
		try {
			jedis = getJedisInstance();
			jedis.set(key, cookiesJson.toJSONString());
			jedis.expire(key, seconds);
			return true;
		} catch (Exception e) {
			logger.error("redis set方法失败...key=" + key + "  value=" + cookiesJson.toJSONString(), e);
		} finally {
			jedisPool.destroy();
		}
		return false;
	}

	/**
	 * 根据key 获取内容
	 *
	 * @author HHR
	 * @date 2017年8月29日 下午3:19:47
	 */
	public static Object get(String key) {

		Jedis jedis = null;
		try {
			jedis = getJedisInstance();
			Object value = jedis.get(key);
			return value;
		} catch (Exception e) {
			logger.error("redis get方法失败...key=" + key);
		} finally {
			jedisPool.destroy();
		}
		return null;
	}

	/**
	 * 删除缓存中得对象，根据key
	 *
	 * @author HHR
	 * @date 2017年8月29日 下午3:19:53
	 */
	public static boolean del(String key) {

		Jedis jedis = null;
		try {
			jedis = getJedisInstance();
			jedis.del(key);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			jedisPool.destroy();
		}
		return false;
	}

	/**
	 * 根据key 获取对象
	 *
	 * @author HHR
	 * @date 2017年8月29日 下午3:19:58
	 */
	public static <T> T get(String key, Class<T> clazz) {

		Jedis jedis = null;
		try {
			jedis = getJedisInstance();
			String value = jedis.get(key);
			return JSON.parseObject(value, clazz);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			jedisPool.destroy();
		}
		return null;
	}

	/**
	 * 设置key过期
	 *
	 * @author HHR
	 * @date 2017年8月29日 下午3:20:03
	 */
	public static boolean expire(String key, int seconds) {

		Jedis jedis = null;
		try {
			jedis = getJedisInstance();
			jedis.expire(key, seconds);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			jedisPool.destroy();
		}
		return false;
	}

	/**
	 * list push
	 *
	 * @author HHR
	 * @date 2017年8月29日 下午3:20:10
	 */
	public static boolean lpush(String key, String value) {

		Jedis jedis = null;
		try {
			jedis = getJedisInstance();
			jedis.lpush(key, value);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			jedisPool.destroy();
		}
		return false;
	}

	/**
	 * list lpop
	 *
	 * @author HHR
	 * @date 2017年8月29日 下午3:20:14
	 */
	public static String lpop(String key) {

		Jedis jedis = null;
		try {
			jedis = getJedisInstance();
			return jedis.lpop(key);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			jedisPool.destroy();
		}
		return null;
	}

	/**
	 * list rpush
	 *
	 * @author HHR
	 * @date 2017年8月29日 下午3:20:20
	 */
	public static boolean rpush(String key, String value) {

		Jedis jedis = null;
		try {
			jedis = getJedisInstance();
			jedis.lpush(key, value);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			jedisPool.destroy();
		}
		return false;
	}

	/**
	 * list rpop
	 *
	 * @author HHR
	 * @date 2017年8月29日 下午3:20:27
	 */
	public static String rpop(String key) {

		Jedis jedis = null;
		try {
			jedis = getJedisInstance();
			return jedis.rpop(key);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			jedisPool.destroy();
		}
		return null;
	}

	/**
	 * 散列添加
	 *
	 * @author HHR
	 * @date 2017年8月29日 下午3:20:32
	 */
	public static boolean hsetAddField(String key, String fieldName, String value) {

		Jedis jedis = null;
		try {
			jedis = getJedisInstance();
			jedis.hset(key, fieldName, value);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			jedisPool.destroy();
		}
		return false;
	}

	/**
	 * 用于将键的整数值递增1
	 *
	 * @author HHR
	 * @date 2017年8月29日 下午3:21:23
	 */
	public static Long incr(String key) {

		Jedis jedis = null;
		try {
			jedis = getJedisInstance();
			return jedis.incr(key);
		} catch (Exception e) {
			e.printStackTrace();
			return -100l;
		} finally {
			jedisPool.destroy();
		}
	}

	/**
	 * 判断是否存在key
	 *
	 * @author HHR
	 * @date 2017年8月29日 下午3:20:42
	 */
	public static Boolean exists(String key) {

		Jedis jedis = null;
		try {
			jedis = getJedisInstance();
			return jedis.exists(key);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			jedisPool.destroy();
		}
	}

}
