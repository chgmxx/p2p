package com.power.platform.sms.service;

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
import org.springframework.stereotype.Service;

import com.power.platform.chuangLan253.CLServerUrlConfig;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.current.entity.WloanCurrentProject;

@Service("sendSmsService")
public class SendSmsService {

	private static final Logger LOG = LoggerFactory.getLogger(SendSmsService.class);

	/**
	 * 
	 * 方法: sendSmsCode <br>
	 * 描述: 中投摩根，平台发送验证码. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年4月26日 下午5:56:56
	 * 
	 * @param mobilePhone
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public String sendSmsCode(String mobilePhone, String[] params) throws Exception {

		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod();
		try {
			// 应用地址.
			URI base = new URI(CLServerUrlConfig.SMS_SINGLE_REQUEST_SERVER_URL, false);
			method.setURI(new URI(base, "HttpBatchSendSM", false));
			// account：账号，pswd：密码，mobile：多个号码，请用逗号分割，needstatus：时候需要状态报告，需要(true)，不需要(false)，msg：短信消息.
			method.setQueryString(new NameValuePair[] { new NameValuePair("account", CLServerUrlConfig.SMS_ACCOUNT), new NameValuePair("pswd", CLServerUrlConfig.SMS_PASSWORD), new NameValuePair("mobile", mobilePhone), new NameValuePair("needstatus", String.valueOf(true)), new NameValuePair("msg", getSmsTemplateContent(params)), });
			int result = client.executeMethod(method);
			LOG.info("fn:sendSmsCode,中投摩根平台发送短信验证码,发送返回报告:{" + result + "}");
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

	/**
	 * 
	 * @param url
	 *            应用地址，类似于http://ip:port/msg/
	 * @param account
	 *            账号
	 * @param pswd
	 *            密码
	 * @param mobile
	 *            手机号码，多个号码使用","分割
	 * @param msg
	 *            短信内容
	 * @param needstatus
	 *            是否需要状态报告，需要true，不需要false
	 * @return 返回值定义参见HTTP协议文档
	 * @throws Exception
	 */
	public String directSendSMS(String phone, String content) throws Exception {

		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod();// HttpSendSM这是本来的url
		try {
			URI base = new URI(CLServerUrlConfig.SMS_SINGLE_REQUEST_SERVER_URL, false);
			method.setURI(new URI(base, "HttpBatchSendSM", false));

			method.setQueryString(new NameValuePair[] { new NameValuePair("account", CLServerUrlConfig.SMS_ACCOUNT), new NameValuePair("pswd", CLServerUrlConfig.SMS_PASSWORD), new NameValuePair("mobile", phone), new NameValuePair("needstatus", String.valueOf(true)), new NameValuePair("msg", content), });
			int result = client.executeMethod(method);
			LOG.info("[验证码通知短信]通过API的形式，将验证码或者通知短信准确发至用户手中。结果:{}", result);
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

	/**
	 * 
	 * @param url
	 *            应用地址，类似于http://ip:port/msg/
	 * @param account
	 *            账号
	 * @param pswd
	 *            密码
	 * @param mobile
	 *            手机号码，多个号码使用","分割
	 * @param msg
	 *            短信内容
	 * @param needstatus
	 *            是否需要状态报告，需要true，不需要false
	 * @return 返回值定义参见HTTP协议文档
	 * @throws Exception
	 */
	public String directSendSMS(String phone, String[] params) throws Exception {

		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod();
		try {
			URI base = new URI(CLServerUrlConfig.SMS_SINGLE_REQUEST_SERVER_URL, false);
			method.setURI(new URI(base, "HttpBatchSendSM", false));
			method.setQueryString(new NameValuePair[] { new NameValuePair("account", CLServerUrlConfig.SMS_ACCOUNT), new NameValuePair("pswd", CLServerUrlConfig.SMS_PASSWORD), new NameValuePair("mobile", phone), new NameValuePair("needstatus", String.valueOf(true)), new NameValuePair("msg", getSmsTemplateContent(params)), });
			int result = client.executeMethod(method);
			LOG.info("[验证码通知短信]通过API的形式，将验证码或者通知短信准确发至用户手中。结果:{}", result);
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

	/**
	 * 
	 * 方法: getSmsTemplateContent <br>
	 * 描述: 获取中投摩根平台短信模版内容. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年4月26日 下午6:00:05
	 * 
	 * @param params
	 * @return
	 */
	public String getSmsTemplateContent(String[] params) {

		return "尊敬的用户：请在页面中输入验证码：" + params[1] + "完成验证！客服电话400-666-9068，非本人操作（发送），请忽略此短信。";
	}

	/**
	 * 活期项目到期提醒功能
	 */

	public String sendMessageToCheckUser(String mobile, WloanCurrentProject wloanCurrentProject) throws Exception {

		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod();
		try {
			URI base = new URI(CLServerUrlConfig.SMS_SINGLE_REQUEST_SERVER_URL, false);
			method.setURI(new URI(base, "HttpBatchSendSM", false));
			method.setQueryString(new NameValuePair[] { new NameValuePair("account", CLServerUrlConfig.SMS_ACCOUNT), new NameValuePair("pswd", CLServerUrlConfig.SMS_PASSWORD), new NameValuePair("mobile", mobile), new NameValuePair("needstatus", String.valueOf(true)), new NameValuePair("msg", getSendMessage(wloanCurrentProject)), });
			int result = client.executeMethod(method);
			LOG.info("[验证码通知短信]通过API的形式，将验证码或者通知短信准确发至用户手中。结果:{}", result);
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

	private String getSendMessage(WloanCurrentProject wloanCurrentProject) {

		return "活期项目编号为：" + wloanCurrentProject.getSn() + "[" + wloanCurrentProject.getName() + "],的项目即将在" + DateUtils.formatDate(wloanCurrentProject.getEndDate(), "yyyy-MM-dd") + "到期，请尽快处理。【赢多多管理员】";
	}
}
