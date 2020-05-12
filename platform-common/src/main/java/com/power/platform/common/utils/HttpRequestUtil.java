package com.power.platform.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Map;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import com.alibaba.fastjson.JSONObject;

public class HttpRequestUtil {

	private static CloseableHttpClient httpClient;

	static {
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		cm.setMaxTotal(100);
		cm.setDefaultMaxPerRoute(20);
		cm.setDefaultMaxPerRoute(50);
		httpClient = HttpClients.custom().setConnectionManager(cm).build();
	}

	public static String get(String url) {

		CloseableHttpResponse response = null;
		BufferedReader in = null;
		String result = "";
		try {
			HttpGet httpGet = new HttpGet(url);
			RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(30000).setConnectionRequestTimeout(30000).setSocketTimeout(30000).build();
			httpGet.setConfig(requestConfig);
			httpGet.setConfig(requestConfig);
			httpGet.addHeader("Content-type", "application/json; charset=utf-8");
			httpGet.setHeader("Accept", "application/json");
			response = httpClient.execute(httpGet);
			in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				sb.append(line + NL);
			}
			in.close();
			result = sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != response) {
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public static String post(String url, String jsonString) {

		CloseableHttpResponse response = null;
		BufferedReader in = null;
		String result = "";
		try {
			HttpPost httpPost = new HttpPost(url);
			RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(30000).setConnectionRequestTimeout(30000).setSocketTimeout(30000).build();
			httpPost.setConfig(requestConfig);
			httpPost.setConfig(requestConfig);
			httpPost.addHeader("Content-type", "application/json; charset=utf-8");
			httpPost.setHeader("Accept", "application/json");
			httpPost.setEntity(new StringEntity(jsonString, Charset.forName("UTF-8")));
			response = httpClient.execute(httpPost);
			in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				sb.append(line + NL);
			}
			in.close();
			result = sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != response) {
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public static void main(String[] args) {

		String appKey = "cicmorgan";
		String uscc = "9111010859963405XW";

		// 企业关键字精确获取详细信息.
		StringBuffer strB = new StringBuffer();
		// main host.
		strB.append("http://dev.i.yjapi.com/ECIV4/GetDetailsByName");
		// key.
		strB.append("?key=").append(appKey);
		// keyword.
		strB.append("&keyword=").append(uscc);
		String url = strB.toString();
		System.err.println("URL = " + url);
		String resultStr = HttpRequestUtil.get(url);
		System.err.println(resultStr);

		JSONObject jsonObject = JSONObject.parseObject(resultStr);
		Map<String, Object> map = jsonObject;
		System.err.println(map);
		System.out.println("Status：" + map.get("Status"));
		System.out.println("Message：" + map.get("Message"));
		// (Map<String, Object>) resultMapObject;
		// System.out.println(resultMap);
		// System.out.println("Name：" + resultMap.get("Name"));

	}

}
