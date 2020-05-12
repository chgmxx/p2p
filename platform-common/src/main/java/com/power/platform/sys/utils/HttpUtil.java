package com.power.platform.sys.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;

/**
 * HTTP 工具类
 * 
 * @author sonta
 *
 */
public class HttpUtil {

	private static class TrustAnyTrustManager implements X509TrustManager {

		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
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
	 * 发送HTTP或者 HTTPS请求
	 * @param url 请求url
	 * @param params 参数
	 * @return
	 */
	public static String sendPost(String url, Map<String, String> params) {
		return sendPost(url, params, null, "UTF-8");
	}
	
	/**
	 * 发送HTTP或者 HTTPS请求
	 * @param url 请求url
	 * @param params 参数
	 * @param header 头部参数
	 * @return
	 */
	public static String sendPost(String url, Map<String, String> params, Map<String, String> header) {
		return sendPost(url, params, header, "UTF-8");
	}
	
	/**
	 * 发送HTTP或者 HTTPS请求
	 * @param url 请求url
	 * @param params 参数
	 * @param header 头部参数
	 * @return
	 */
	public static String sendPost(String url, Map<String, String> params, String charset) {
		return sendPost(url, params, null, charset);
	}
	
	
	/**
	 * 发送HTTP或者 HTTPS请求
	 * @param url 请求url
	 * @param param 参数
	 * @param header 头部参数
	 * @param charset 编码方式
	 */
	public static String sendPost(String url, Map<String, String> param, Map<String, String> header, String charset) {
		if (url.toLowerCase().startsWith("https://")) {
			return sendHttpsPost(url, param, header, charset);
		}
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		String paramStr = getParamStr(param);
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			
			// 设置用户自定义header
			if (header != null) {
				for (String key : header.keySet()) {
					conn.setRequestProperty(key, header.get(key));
				}
			}
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print(paramStr);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream(), charset));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 将参数转为 key1=v1&key2=v2&key3=v3 的形式
	 * 汉字进行UTF-8 encode
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
				sb.append(entry.getKey()).append("=")
						.append(URLEncoder.encode(entry.getValue(), "UTF-8")).append("&");
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

	private static String sendHttpsPost(String url, Map<String, String> params, Map<String, String> header, String charset) {
		SSLContext sc = null;
		URL console = null;
		HttpsURLConnection conn = null;
		DataOutputStream out = null;
		InputStream is = null;
		ByteArrayOutputStream outStream = null;
		try {
			sc = SSLContext.getInstance("SSL");
			sc.init(null, new TrustManager[] { new TrustAnyTrustManager() },
					new java.security.SecureRandom());
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
}
