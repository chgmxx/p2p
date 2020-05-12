package com.power.platform.ifcert.utils.http;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.alibaba.fastjson.JSONObject;
import com.power.platform.cgbpay.config.ServerURLConfig;
import com.power.platform.ifcert.utils.sha.ShaApiKey;

public class HttpsUtil {
	
	private static class TrustAnyTrustManager implements X509TrustManager {

		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

		}

		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

		}

		public X509Certificate[] getAcceptedIssuers() {

			return new X509Certificate[] {};
		}
	}

	private static class TrustAnyHostnameVerifier implements HostnameVerifier {

		public boolean verify(String hostname, SSLSession session) {

			return true;
		}
	}
	
	/**
	 * 将参数转为 key1=v1&key2=v2&key3=v3 的形式
	 * 汉字进行UTF-8 encode
	 * 
	 * @param param
	 * @return
	 */
	private static String getParamStr(Map<String, String> param) {

		String paramStr = "";
		StringBuilder sb = new StringBuilder();
		for (Entry<String, String> entry : param.entrySet()) {
			try {
				if (entry.getValue() == null || "".equals(entry.getValue())) { // 空值不参与传递
					continue;
				}
				sb.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "UTF-8")).append("&");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		paramStr = sb.toString();
		if (paramStr.lastIndexOf("&") != -1) {
			paramStr = paramStr.substring(0, paramStr.lastIndexOf("&"));
		}
		return paramStr;
	}
	
	/**
	* 发起https请求并获取结果
	* @param requestUrl 请求地址
	* @param Map<String, String> params 提交的数据
	* @return 获取JSON字符串
	*/
	public static String sendHttpsGet(String requestUrl,Map<String, String> params) {
		JSONObject jsonObject = null;
		StringBuffer buffer = new StringBuffer();
		try {
			// 创建SSLContext对象，并使用我们指定的信任管理器初始化
//			TrustManager[] tm = { new DefaultTrustManager()  };
//			SSLContext sslContext = SSLContext.getInstance("SSL","SunJSSE");
			SSLContext sslContext = SSLContext.getInstance("SSL");
//			sslContext.init(null, tm, new java.security.SecureRandom());
			sslContext.init(null, new TrustManager[] { new TrustAnyTrustManager() }, new java.security.SecureRandom());
			// 从上述SSLContext对象中得到SSLSocketFactory对象
			SSLSocketFactory ssf = sslContext.getSocketFactory();
			String outputStr = getParamStr(params);
			String urlStr = requestUrl+"?"+outputStr;
			URL url = new URL(urlStr);
			HttpsURLConnection httpUrlConn = (HttpsURLConnection) url.openConnection();
			httpUrlConn.setSSLSocketFactory(ssf);
			httpUrlConn.setDoOutput(true);
			httpUrlConn.setDoInput(true);
			httpUrlConn.setUseCaches(false);
			// 设置请求方式（GET/POST）
			httpUrlConn.setRequestMethod("GET");
//			httpUrlConn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
//			httpUrlConn.setRequestProperty("Charset", "UTF-8");
//			httpUrlConn.setRequestProperty("Authorization", "Basic aXdidXNlcjp0ZXN0MDAwMA==");
//			httpUrlConn.setRequestProperty("User-Agent", "Client identifier");
			httpUrlConn.connect();
//			String outputStr = getParamStr(params);
			// 当有数据需要提交时
//			if (null != outputStr) {
//				OutputStream outputStream = httpUrlConn.getOutputStream();
//				// 注意编码格式，防止中文乱码
//				outputStream.write(outputStr.getBytes("UTF-8"));
//				outputStream.close();
//			}
			// 将返回的输入流转换成字符串
			InputStream inputStream = httpUrlConn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream,"utf-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			bufferedReader.close();
			inputStreamReader.close();
			// 释放资源
			inputStream.close();
			inputStream = null;
			httpUrlConn.disconnect();
//			jsonObject = JSONObject.fromObject(buffer.toString());
			return buffer.toString();
		} catch (ConnectException ce) {
			System.out.println("ConnectException");
			System.out.println(ce);
		} catch (Exception e) {
			System.out.println("IOException");
		    System.out.println(e);
		}
		return null;
	}
}
