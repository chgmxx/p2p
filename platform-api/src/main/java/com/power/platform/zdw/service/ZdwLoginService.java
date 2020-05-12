package com.power.platform.zdw.service;

import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.power.platform.common.utils.JedisUtils;
import com.power.platform.common.utils.juhe.JuHeCodeUtil;
import com.power.platform.zdw.entity.Search;
import com.power.platform.zdw.entity.User;
import com.power.platform.zdw.type.CodeDiscernEnum;
import com.power.platform.zdw.type.CodeTypeEnum;
import com.power.platform.zdw.utils.HttpUtils;
import com.power.platform.zdw.utils.OtherUtil;
import com.power.platform.zdw.utils.Response;

import org.jsoup.Jsoup;

@Service("zdwLoginService")
public class ZdwLoginService {

	private static final Logger logger = LoggerFactory.getLogger(ZdwLoginService.class);
	private static String captchaCode;
	private static int num = 0;

	public String login(User user) {

		try {
			if (user.getUserName() == null || user.getUserName() == "") {
				return "登录名不能为空！";
			}
			if (user.getPassword() == null || user.getPassword() == "") {
				return "密码不能为空！";
			}
			if (user.getUserName().length() > 20) {
				return "登录名长度不超过20个字符！";
			}
			// 创建httpClient实例
			CookieStore cookieStore = new BasicCookieStore();
			CloseableHttpClient httpClient = HttpUtils.getHttpClient(true, cookieStore);
			String url1 = "https://www.zhongdengwang.org.cn/zhongdeng/index.shtml";
			HttpGet get1 = new HttpGet(url1);
			get1.addHeader("Host", "www.zhongdengwang.org.cn");
			get1.addHeader("Referer", "https://www.zhongdengwang.org.cn/rs/main.do");

			HttpUtils.executeGetWithResult(httpClient, get1);
			String url2 = "https://www.zhongdengwang.org.cn/rs/main.jsp";
			HttpGet get2 = new HttpGet(url2);
			get2.addHeader("Referer", url1);
			String result = HttpUtils.executeGetWithResult(httpClient, get2);
			System.out.println(result);
			/**
			 * 获取图片验证码，并识别.
			 */
			Response responseResult = getCode(httpClient);
			JSONObject jsonObject = JSONObject.parseObject(responseResult.toString());
			if (jsonObject != null) {
				boolean success = (boolean) jsonObject.get("success");
				if (success) {
					logger.info("按主体查询，验证码校验:{}", success);
				} else {
					return "false";
				}
			} else {
				return "false";
			}

			String sessionId = result.split("sessionId=\\\"")[1].split("\\\";")[0];
			System.out.println(sessionId);
			List<String> a = HttpUtils.getCookie(cookieStore, "RSOUT");
			System.out.println(a.toString());
			// 密码加密
			String jsPassword = OtherUtil.ctAes(OtherUtil.ctAes(user.getPassword()).toUpperCase() + sessionId).toUpperCase();
			System.out.println(jsPassword);
			// 创建httpGet实例
			String url = "https://www.zhongdengwang.org.cn/rs/login.do?method=login&v=" + Math.random();
			Map<String, Object> params = new HashMap<>();

			params.put("type", "");
			params.put("conone", "1");
			params.put("userCode", user.getUserName());
			params.put("showpassword", "密码");
			params.put("password", jsPassword);
			params.put("validateCode", captchaCode);
			params.put("paper", "1");
			params.put("num", "登记证明编号");
			params.put("papercode", "校验码");
			HttpPost post = HttpUtils.post(url, params);
			post.addHeader("Origin", "https://www.zhongdengwang.org.cn");
			post.addHeader("Host", "www.zhongdengwang.org.cn");
			post.addHeader("Referer", "https://www.zhongdengwang.org.cn/rs/main.jsp");
			String html = HttpUtils.executePostWithResult(httpClient, post, "UTF-8");
			logger.info("html5:" + html);
			if (html.contains("rs/main.do")) {
				String url3 = "https://www.zhongdengwang.org.cn/rs/main.do";
				HttpGet get = new HttpGet(url3);
				get2.addHeader("Referer", url);
				result = HttpUtils.executeGetWithResult(httpClient, get);
				// 存储cookies
				JedisUtils.set(user.getUserName() + "|" + user.getPassword(), cookieStore, 6 * 10000);
				// JedisClient.set(user.getUserName() + "|" + user.getPassword(), cookieStore, 6 * 10000);
				logger.info(cookieStore.toString());
				// channelMapping.insert(user);
				// Search u =new Search();
				// u.setUserName("cicmorgan123");
				// u.setPassword("cicmorgan123");
				// u.setGuarantor("德州市久盛食品有限公司");
				// getReport(u);
				return "登录成功";
			} else if (Jsoup.parse(html).select(".ts_info").text().contains("用户名或密码错误，若连续5次错误将锁定")) {
				return "用户名或密码错误，若连续5次错误将锁定";
			} else if (Jsoup.parse(html).select(".ts_info").text().contains("校验码错误")) {
				return login(user);
			} else {
				return "登录其他错误";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "登录异常";
		}
	}

	private static Response getCode(CloseableHttpClient client) {

		try {

			String url = "https://www.zhongdengwang.org.cn/rs/include/vcodeimage3.jsp?" + Math.random();
			HttpGet get = HttpUtils.get(url);
			get.addHeader("referer", "https://www.zhongdengwang.org.cn/rs/main.jsp");
			File imageFile = HttpUtils.getCaptchaCodeImage(client, get);
			// String captchaCode = OCRUtils.commitCat("6", "999", image);
			// Scanner sc = new Scanner(System.in);
			// String code = sc.next();
			String responseStr = JuHeCodeUtil.post(CodeTypeEnum.CODE_TYPE_1004.getValue(), imageFile);
			JSONObject jsonObject = JSONObject.parseObject(responseStr);
			if (jsonObject != null) {
				Integer errorCode = (Integer) jsonObject.get("error_code");
				if (CodeDiscernEnum.ERROR_CODE_0.getValue().equals(errorCode)) { // 识别成功.
					String result = (String) jsonObject.get("result");
					captchaCode = result;
				} else {
					return Response.FAILURE;
				}
			}
			// 图片识别
			logger.info("继续验证图片" + captchaCode);
			num++;
			if (num > 10) {
				return Response.SYSTEM_WRONG_TIME_CODE;
			}
			return Response.SUCCESS;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return Response.SYSTEM_WRONG_UNKNOWN;
		}
	}

	public String getReport(Search s) {

		String key = s.getUserName() + "|" + s.getPassword();
		CookieStore cookieStore = HttpUtils.getCookies(key);
		logger.info(cookieStore.toString());
		CloseableHttpClient httpClient = HttpUtils.getHttpClient(true, cookieStore);
		getCode1(httpClient);
		serch(httpClient, s);
		return "";
	}

	private void serch(CloseableHttpClient httpClient, Search s) {

		try {
			String url = "https://www.zhongdengwang.org.cn/rs/query/bysubject.do";
			HttpGet get = new HttpGet(url);
			HttpUtils.executeGetWithResult(httpClient, get);
			url = "https://www.zhongdengwang.org.cn/rs/conditionquery/byname.do?method=init";
			get = new HttpGet(url);
			HttpUtils.executeGetWithResult(httpClient, get);
			url = "https://www.zhongdengwang.org.cn/rs/conditionquery/byname.do?method=QueryByName";
			Map<String, Object> params = new HashMap<>();
			params.put("debttype", "100");
			params.put("name", "德州市久盛食品有限公司");
			params.put("cert_no", "");
			params.put("validateCode", "x3ud");
			params.put("confirm", "true");
			// params.put("validateCode", captchaCode);
			HttpPost post = HttpUtils.post(url, params);
			post.addHeader("Origin", "https://www.zhongdengwang.org.cn");
			post.addHeader("Host", "www.zhongdengwang.org.cn");
			post.addHeader("Referer", "https://www.zhongdengwang.org.cn/rs/conditionquery/byname.do?method=init");
			String html = HttpUtils.executePostWithResult(httpClient, post, "UTF-8");

			logger.info("html:" + html);
			if (html.contains("rs/main.do")) {

			} else if (Jsoup.parse(html).select("#code").text().trim().contains("用户名或密码错误，若连续5次错误将锁定")) {
				getReport(s);
				System.out.println("验证码错误");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Response getCode1(CloseableHttpClient client) {

		try {
			String url = "https://www.zhongdengwang.org.cn/rs/include/vcodeimage4.jsp";
			HttpGet get = HttpUtils.get(url);
			get.addHeader("referer", "https://www.zhongdengwang.org.cn/rs/conditionquery/byname.do?method=init");
			File imageFile = HttpUtils.getCaptchaCodeImage(client, get);
			// String captchaCode = OCRUtils.commitCat("6", "999", image);
			// Scanner sc = new Scanner(System.in);
			// String code = sc.next();
			// captchaCode = code;
			// 图片识别
			String responseStr = JuHeCodeUtil.post(CodeTypeEnum.CODE_TYPE_1004.getValue(), imageFile);
			JSONObject jsonObject = JSONObject.parseObject(responseStr);
			if (jsonObject != null) {
				Integer errorCode = (Integer) jsonObject.get("error_code");
				if (CodeDiscernEnum.ERROR_CODE_0.getValue().equals(errorCode)) { // 识别成功.
					String result = (String) jsonObject.get("result");
					captchaCode = result;
				} else {
					return Response.FAILURE;
				}
			}
			logger.info("继续验证图片" + captchaCode);
			num++;
			if (num > 10) {
				return Response.SYSTEM_WRONG_TIME_CODE;
			}
			return Response.SUCCESS;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return Response.SYSTEM_WRONG_UNKNOWN;
		}
	}

	public static void main(String args[]) {

		ZdwLoginService z = new ZdwLoginService();
		User u = new User(null, null);
		String result = z.login(u);
		System.out.println(result);

	}
}
