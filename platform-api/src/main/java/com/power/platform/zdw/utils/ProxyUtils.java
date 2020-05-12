package com.power.platform.zdw.utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class ProxyUtils {

	private static final List<String> SOCKS_PROXIES = Collections.synchronizedList(new LinkedList<String>());
	private static final Logger LOG = LoggerFactory.getLogger(ProxyUtils.class);
	private static AtomicInteger yuanziInt = new AtomicInteger(0);

	/**
	 * 初始化socks代理
	 *
	 * @author wangyepeng
	 */
	public static synchronized void initSocks() {

		try {
			SOCKS_PROXIES.clear();
			String hosts = null;
			if (StringUtils.isBlank(hosts)) {
				return;
			}
			JSONArray ipArr = JSON.parseArray(hosts);
			for (int i = 0; i < ipArr.size(); i++) {
				JSONObject socket = ipArr.getJSONObject(i);
				SOCKS_PROXIES.add(socket.toJSONString());
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	/**
	 * 重新初始化socks代理
	 *
	 * @return
	 * @author wangyepeng
	 */
	public static String resetSocks() {

		initSocks();
		return JSON.toJSONString(SOCKS_PROXIES);
	}

	/**
	 * 重新初始化socks代理
	 *
	 * @return
	 * @author wangyepeng
	 */
	public static boolean removeSocks(String ipjob) {

		return SOCKS_PROXIES.remove(ipjob);
	}

	/**
	 * 更新ip状态
	 *
	 * @return
	 * @author wangyepeng
	 */
	public static boolean updateSocks(JSONObject newJob) {

		boolean setFlg = false;
		JSONArray job = new JSONArray();
		for (int i = 0; i < SOCKS_PROXIES.size(); i++) {
			JSONObject oldJob = JSON.parseObject(SOCKS_PROXIES.get(i));
			if (oldJob.getString("ip").equals(newJob.getString("ip"))) {
				SOCKS_PROXIES.set(i, newJob.toJSONString());
				job.add(newJob);
			} else {
				job.add(oldJob);
			}
		}
		// RedisUtils.set("zhima_socket", JSON.toJSONString(job, SerializerFeature.DisableCircularReferenceDetect));
		return setFlg;
	}

	/**
	 * 获取一个socks代理
	 *
	 * @return
	 * @author wangyepeng
	 */
	public static String getSocksProxyHost() {

		initSocks();
		int size = SOCKS_PROXIES.size();
		if (size < 1) {
			return null;
		}
		int shunxu = yuanziInt.incrementAndGet();
		if (shunxu >= SOCKS_PROXIES.size()) {
			yuanziInt.set(0);
			shunxu = 0;
		}
		// 获取的第几个,顺序获取
		String ip = SOCKS_PROXIES.get(shunxu);
		return ip;
	}

	/**
	 * 获取一个socks代理
	 *
	 * @return
	 * @author wangyepeng
	 */
	public static String getSocksProxyHost(HashSet<String> blacks, String url) {

		initSocks();
		int size = SOCKS_PROXIES.size();
		if (size < 1) {
			return null;
		}
		if (blacks.size() >= size) {
			LOG.error("nothing proxy ip for " + url);
			throw new RuntimeException("nothing proxy ip");
		}
		int shunxu = yuanziInt.incrementAndGet();

		if (shunxu >= SOCKS_PROXIES.size()) {
			yuanziInt.set(0);
			shunxu = 0;
		}
		// 获取的第几个,顺序获取
		String ip = SOCKS_PROXIES.get(shunxu);
		return ip;
	}

	/**
	 * 获取一个socks代理
	 * 
	 * @param service
	 * @return
	 */
	public static String getSocksProxyHost(String service) {

		JSONObject ipjob = JSON.parseObject(getSocksProxyHost());
		if (StringUtils.isBlank(service) || !service.contains("http")) {
			LOG.info("service:" + service);
			service = "http://www.baidu.com/";
		}
		// 判断是否可用
		while (HttpUtils.testProxy(new HttpGet(service), ipjob) == false) {
			ipjob = JSON.parseObject(getSocksProxyHost());
		}
		LOG.info(service + "可用ip" + ipjob);
		// 该ip不支持的服务
		return ipjob.toString();
	}

	/**
	 * 获取一个socks代理
	 * 
	 * @param service
	 * @param blacks
	 *            ip
	 * @return
	 */
	public static String getSocksProxyHost(String service, String... blacks) {

		HashSet<String> set = new HashSet<>();
		if (blacks != null) {
			Collections.addAll(set, blacks);
		}
		// 该ip不支持的服务
		return getSocksProxyHost(service, set);
	}

	/**
	 * @param service
	 * @param blacks
	 *            ip
	 * @return
	 */
	public static String getSocksProxyHost(String service, HashSet<String> blacks) {

		JSONObject ipjob = JSON.parseObject(getSocksProxyHost(blacks, service));
		String ip = ipjob.getString("ip");
		// 判断是否可用
		while (blacks.contains(ip) || HttpUtils.testProxy(new HttpGet(service), ipjob) == false) {
			LOG.info(blacks.contains(ip) + "   " + service + " bad ip:" + ipjob);
			ipjob = JSON.parseObject(getSocksProxyHost(blacks, service));
			ip = ipjob.getString("ip");
		}
		LOG.info(service + "可用ip" + ipjob);
		// 该ip不支持的服务
		return ipjob.toString();
	}

	/*
	 * 获取cookies
	 */
	public static CookieStore getCookies(String userid, String token) {

		CookieStore cookieStore = new BasicCookieStore();
		String clientStr = null;// RedisUtils.get(userid + token);// http代理等信息初始化httpClient
		if (StringUtils.isNotBlank(clientStr)) {
			JSONArray object = JSON.parseArray(clientStr);
			for (int i = 0; i < object.size(); i++) {
				JSONObject s = (JSONObject) JSONObject.parse(object.get(i).toString());
				BasicClientCookie basicCookie = new BasicClientCookie(s.get("name").toString(), s.get("value").toString());
				basicCookie.setDomain(s.getString("domain"));
				basicCookie.setPath(s.getString("path"));
				cookieStore.addCookie(basicCookie);
			}
		}
		return cookieStore;
	}

}
