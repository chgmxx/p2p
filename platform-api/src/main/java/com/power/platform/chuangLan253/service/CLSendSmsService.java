package com.power.platform.chuangLan253.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLDecoder;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.power.platform.chuangLan253.CLServerUrlConfig;

public class CLSendSmsService {

	private static final Logger log = LoggerFactory.getLogger(CLSendSmsService.class);

	/**
	 * 
	 * methods: directSendMsg <br>
	 * description: 直接发送短消息. <br>
	 * author: Roy <br>
	 * date: 2019年7月30日 上午11:32:22
	 * 
	 * @param phone
	 *            手机
	 * @param content
	 *            内容
	 * @return
	 */
	public static String directSendMsg(String phone, String content) throws Exception {

		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod();// HttpSendSM这是本来的url
		try {
			URI base = new URI(CLServerUrlConfig.SMS_SINGLE_REQUEST_SERVER_URL, false);
			method.setURI(new URI(base, "HttpBatchSendSM", false));
			method.setQueryString(new NameValuePair[] { new NameValuePair("account", CLServerUrlConfig.SMS_ACCOUNT), new NameValuePair("pswd", CLServerUrlConfig.SMS_PASSWORD), new NameValuePair("mobile", phone), new NameValuePair("needstatus", String.valueOf(true)), new NameValuePair("msg", content), });
			int result = client.executeMethod(method);
			log.info("[验证码通知短信]通过API的形式，将验证码或者通知短信准确发至用户手中。结果:{}", result);
			if (result == HttpStatus.SC_OK) {
				InputStream in = method.getResponseBodyAsStream();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = in.read(buffer)) != -1) {
					baos.write(buffer, 0, len);
				}
				return URLDecoder.decode(baos.toString(), "UTF-8");
			} else {
				throw new Exception("HTTP ERROR Status: " + method.getStatusCode() + ":" + method.getStatusText());
			}
		} finally {
			method.releaseConnection();
		}
	}

	public static void main(String[] args) {

		String phone = "15911180605";
		// String content = "你好,你的验证码是123456";
		String content = "尊敬的用户，中投摩根提醒您：您于2019-07-31 09:45:46成功向某贸易公司借款0247项目！出借资金2000元！";
		try {
			System.out.println(directSendMsg(phone, content));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
