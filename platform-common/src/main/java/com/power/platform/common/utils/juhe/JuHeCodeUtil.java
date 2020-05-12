package com.power.platform.common.utils.juhe;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * 
 * class: JuHeCodeUtil <br>
 * description: 聚合数据，图片验证码识别工具类. <br>
 * author: Roy <br>
 * date: 2019年6月21日 下午4:46:00
 */
public class JuHeCodeUtil {

	public static final String DEF_CHATSET = "UTF-8";
	public static final int DEF_CONN_TIMEOUT = 30000;
	public static final int DEF_READ_TIMEOUT = 30000;
	public static String userAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.66 Safari/537.36";

	/**
	 * 配置申请的KEY，用于付费查询.
	 */
	public static final String APPKEY = "b4be7e40f21fabf64a7825335dbcc4c0";

	/**
	 * 
	 * methods: post <br>
	 * description: 识别图片验证码并返回结果. <br>
	 * author: Roy <br>
	 * date: 2019年6月21日 下午4:47:02
	 * 
	 * @param type
	 *            1004-4位英数混合
	 * @param file
	 *            获取到的图片验证码文件
	 * @return
	 * @throws Exception
	 */
	public static String post(String type, File file) throws Exception {

		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String result = null;
		try {
			RequestConfig config = RequestConfig.custom().setConnectTimeout(30000).setSocketTimeout(30000).build();
			HttpPost httppost = new HttpPost("http://op.juhe.cn/vercode/index");
			StringBody keyBody = new StringBody(APPKEY, ContentType.TEXT_PLAIN);
			StringBody typeBody = new StringBody(type, ContentType.TEXT_PLAIN);
			HttpEntity reqEntity = MultipartEntityBuilder.create().addBinaryBody("image", file, ContentType.create("image/jpeg"), file.getName()).addPart("key", keyBody).addPart("codeType", typeBody).build();
			httppost.setEntity(reqEntity);
			httppost.setConfig(config);
			response = httpClient.execute(httppost);
			HttpEntity resEntity = response.getEntity();
			if (resEntity != null) {
				result = IOUtils.toString(resEntity.getContent(), "UTF-8");
			}
			EntityUtils.consume(resEntity);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			response.close();
			httpClient.close();
		}
		return result;
	}

	/**
	 * 
	 * methods: getRequestCodeType <br>
	 * description: 查询验证码类型. <br>
	 * author: Roy <br>
	 * date: 2019年6月21日 下午4:48:59
	 */
	public static void getRequestCodeType() {

		String result = null;
		String url = "http://op.juhe.cn/vercode/codeType";// 请求接口地址
		Map<String, String> params = new HashMap<String, String>();// 请求参数
		params.put("key", APPKEY);// 您申请到的APPKEY
		params.put("dtype", "json");// 返回的数据的格式，json或xml，默认为json

		try {
			result = net(url, params, "GET");
			JSONObject object = JSONObject.fromObject(result);
			if (object.getInt("error_code") == 0) {
				System.out.println(object.get("result"));
			} else {
				System.out.println(object.get("error_code") + ":" + object.get("reason"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * methods: net <br>
	 * description: 请求接口，获取数据. <br>
	 * author: Roy <br>
	 * date: 2019年6月21日 下午4:54:36
	 * 
	 * @param strUrl
	 * @param params
	 * @param method
	 * @return
	 * @throws Exception
	 */
	public static String net(String strUrl, Map<String, String> params, String method) throws Exception {

		HttpURLConnection conn = null;
		BufferedReader reader = null;
		String rs = null;
		try {
			StringBuffer sb = new StringBuffer();
			if (method == null || method.equals("GET")) {
				strUrl = strUrl + "?" + urlEncoder(params);
			}
			URL url = new URL(strUrl);
			conn = (HttpURLConnection) url.openConnection();
			if (method == null || method.equals("GET")) {
				conn.setRequestMethod("GET");
			} else {
				conn.setRequestMethod("POST");
				conn.setDoOutput(true);
			}
			conn.setRequestProperty("User-agent", userAgent);
			conn.setUseCaches(false);
			conn.setConnectTimeout(DEF_CONN_TIMEOUT);
			conn.setReadTimeout(DEF_READ_TIMEOUT);
			conn.setInstanceFollowRedirects(false);
			conn.connect();
			if (params != null && method.equals("POST")) {
				try (DataOutputStream out = new DataOutputStream(conn.getOutputStream())) {
					out.writeBytes(urlEncoder(params));
				}
			}
			InputStream is = conn.getInputStream();
			reader = new BufferedReader(new InputStreamReader(is, DEF_CHATSET));
			String strRead = null;
			while ((strRead = reader.readLine()) != null) {
				sb.append(strRead);
			}
			rs = sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				reader.close();
			}
			if (conn != null) {
				conn.disconnect();
			}
		}
		return rs;
	}

	/**
	 * 
	 * methods: urlEncoder <br>
	 * description: 将map型转为请求参数型. <br>
	 * author: Roy <br>
	 * date: 2019年6月21日 下午4:53:20
	 * 
	 * @param data
	 * @return
	 */
	public static String urlEncoder(Map<String, String> data) {

		StringBuilder sb = new StringBuilder();

		Iterator<Map.Entry<String, String>> it = data.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> entry = it.next();
			System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
			try {
				sb.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue() + "", "UTF-8")).append("&");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

}
