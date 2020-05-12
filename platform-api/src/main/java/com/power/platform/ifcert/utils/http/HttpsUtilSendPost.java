package com.power.platform.ifcert.utils.http;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.power.platform.cgbpay.config.ServerURLConfig;
import com.power.platform.ifcert.utils.sha.ShaApiKey;

import net.sf.json.JSONObject;

/**
 * 
 * class: HttpsUtilSendPost <br>
 * description: HTTPS协议推送数据. <br>
 * author: Roy <br>
 * date: 2019年5月6日 下午5:02:24
 */
public class HttpsUtilSendPost {

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
	 * 
	 * methods: sendPost <br>
	 * description: 封装该批次数据通过HTTPS协议进行推送. <br>
	 * author: Roy <br>
	 * date: 2019年5月6日 下午4:58:23
	 * 
	 * @param json
	 *            JSONObject
	 * @param timestamp
	 *            时间戳
	 * @param nonce
	 *            随机数
	 * @param url
	 *            接口地址
	 * @return
	 */
	public static String sendPost(JSONObject json, Long timestamp, String nonce, String url, String charset) throws java.net.ConnectException {

		Map<String, String> params = new HashMap<String, String>(2);
		String token = ShaApiKey.getApiKey(ServerURLConfig.API_KEY, ServerURLConfig.SOURCE_CODE, ServerURLConfig.VERSION, timestamp, nonce);
		params.put("apiKey", token);
		String msg = json.toString();
		params.put("msg", msg);

		String responseStr = sendHttpsPost(url, params, null, charset);
		return responseStr;
	}

	/**
	 * 
	 * methods: sendHttpsPost <br>
	 * description: HTTPS协议传输数据与接收响应. <br>
	 * author: Roy <br>
	 * date: 2019年5月6日 下午6:18:43
	 * 
	 * @param url
	 * @param params
	 * @param header
	 * @param charset
	 * @return
	 */
	public static String sendHttpsPost(String url, Map<String, String> params, Map<String, String> header, String charset) {

		SSLContext sc = null;
		URL console = null;
		HttpsURLConnection conn = null;
		DataOutputStream out = null;
		InputStream is = null;
		ByteArrayOutputStream outStream = null;
		try {
			sc = SSLContext.getInstance("SSL");
			sc.init(null, new TrustManager[] { new TrustAnyTrustManager() }, new java.security.SecureRandom());
			console = new URL(url);
			conn = (HttpsURLConnection) console.openConnection();
			String content = getParamStr(params);
			conn.setSSLSocketFactory(sc.getSocketFactory());
			conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			if (header != null && !header.isEmpty()) {
				for (String key : header.keySet()) {
					conn.setRequestProperty(key, header.get(key));
				}
			}
			conn.connect();
			out = new DataOutputStream(conn.getOutputStream());
			out.write(content.getBytes(charset));
			is = conn.getInputStream();
			if (is != null) {
				outStream = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = is.read(buffer)) != -1) {
					outStream.write(buffer, 0, len);
				}
				return outStream.toString(charset);
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} finally {
			try {
				if (outStream != null) {
					outStream.flush();
					outStream.close();
				}
				if (is != null) {
					is.close();
				}
				if (out != null) {
					out.flush();
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return null;
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
}
