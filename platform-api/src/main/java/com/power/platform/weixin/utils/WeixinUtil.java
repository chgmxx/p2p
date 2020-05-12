package com.power.platform.weixin.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.alibaba.fastjson.JSONObject;
import com.power.platform.common.utils.JedisUtils;
import com.power.platform.common.utils.StringUtils;

public class WeixinUtil {

	// 获取access_token的接口地址（GET） 限200（次/天）
	public final static String access_token_url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
	// 菜单创建（POST） 限100（次/天）
	public static String menu_create_url = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";
	// 客服接口地址
	public static String send_message_url = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=ACCESS_TOKEN";
	// 创建二维码ticket请求
	public static String qrcode_ticket_url = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=ACCESS_TOKEN";
	// 通过ticket换取二维码
	public static String get_qrcode_url = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=TICKET";
	// 微信网页授权获取CODE
	public static String web_oauth_url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect";
	// 微信网页授权获取网页accesstoken和OPENID
	public static String web_oauth_accesstoken_url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
	// 微信网页授权获取用户信息
	public static String web_oauth_userinfo_url = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID";
	// 获取微信服务器IP地址
	public static String get_callbackip_url = "https://api.weixin.qq.com/cgi-bin/getcallbackip?access_token=ACCESS_TOKEN";

	public static String get_userinfo_by_sns = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=http%3A%2F%2Fqweasd.cc%2Foauth2%2Fgetinfo&response_type=code&scope=snsapi_userinfo&state=0#wechat_redirect";

	// 模板消息发送接口
	public static String send_templete_url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=ACCESS_TOKEN";

	public final static String user_info_url = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";

	public final static String WEIXIN_APP_SECRET = "181a78bf52fa3bbf279949e3b8841b9b";

	public final static String WEIXIN_APP_ID = "wx18fecbc45e8ba28b";

	public final static String WEIXIN_ACCOUNT_TOKEN = "weixinwin22";

	public static String base_share_url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_REPLACE&response_type=code&scope=snsapi_base&state=STAGEID#wechat_redirect";

	public static final String WEIXIN_ACCESS_TOKEN = "weixin_access_token";

	// 微信模板消息模板地址--------------------------------------------------------------------------------------
	// 微信还款地址
	public static final String weixinBackMoneyModelId = "6yPc8A9skdZ8IjxFvSTZEZCYwfuDSpMCgQNPK326vmM";
	// 微信投标成功地址
	public static final String weixinBidSuccessModelId = "o1Fpwp50WqcVcsG0fFe9y9OhTEFDPaLkFDeChF-NXgk";
	// 微信充值成功地址
	public static final String weixinNetSaveModelId = "h9YbGcKCFtrjtZpcas2yHizLqljUdiKqCc0uGIafcbk";
	// 提现复核通过地址
	public static final String weixinCashModelId = "PSUop_gwoV60I3i8r00EuTfJZAfNxNe9vIpLyO7eLlU";
	// 密码更改地址
	public static final String weixinPasswordChangeModelId = "Cqcqriw-NikZoimPDff18aZKuLtRrQnA0nJBlg9XqzE";
	// 账号绑定成功地址
	public static final String weixinBandSuccessModelId = "9rLw-cxqE7xnW_gpw6bDsFHOVu8HjuxOQp83sOEm_gI";
	// 账号解绑成功地址
	public static final String weixinUnBandSuccessModelId = "ifP6MOSDYFcxnS7cydZPYy6VoMYkX6rEfYlJJKwo8uE";
	// 微信模板消息模板地址--------------------------------------------------------------------------------------

	// 微信公众号按钮链接地址----------------------------------------------------------------------------------------
	// 菜单生成方法（GET）
	// http://win11.com/svc/services/wxconfig/createMenu

	// 绑定解绑地址
	public static final String weixinBindUrl = "http://cicmorgan.com/svc/services/wxopen/getOpenid";
	// 进入官网地址
	public static final String weixinIndexUrl = "http://cicmorgan.com/svc/services/wxopen/weixinIndex";

	/**
	 * 发起https请求并获取结果
	 * 
	 * @param requestUrl
	 *            请求地址
	 * @param requestMethod
	 *            请求方式（GET、POST）
	 * @param outputStr
	 *            提交的数据
	 * @return JSONObject(通过JSONObject.get(key)的方式获取json对象的属性值)
	 */
	public static JSONObject httpRequest(String requestUrl, String requestMethod, String outputStr) {

		StringBuffer buffer = new StringBuffer();
		JSONObject jsonObject = null;
		try {
			// 创建SSLContext对象，并使用我们指定的信任管理器初始化
			X509TrustManager tm = new X509TrustManager() {

				public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {

				}

				public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {

				}

				public X509Certificate[] getAcceptedIssuers() {

					return null;
				}
			};
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, new TrustManager[] { tm }, new java.security.SecureRandom());
			// 从上述SSLContext对象中得到SSLSocketFactory对象
			SSLSocketFactory ssf = sslContext.getSocketFactory();

			URL url = new URL(requestUrl);
			HttpsURLConnection httpUrlConn = (HttpsURLConnection) url.openConnection();
			httpUrlConn.setSSLSocketFactory(ssf);

			httpUrlConn.setDoOutput(true);
			httpUrlConn.setDoInput(true);
			httpUrlConn.setUseCaches(false);
			// 设置请求方式（GET/POST）
			httpUrlConn.setRequestMethod(requestMethod);

			if ("GET".equalsIgnoreCase(requestMethod))
				httpUrlConn.connect();

			// 当有数据需要提交时
			if (null != outputStr) {
				OutputStream outputStream = httpUrlConn.getOutputStream();
				// 注意编码格式，防止中文乱码
				outputStream.write(outputStr.getBytes("UTF-8"));
				outputStream.close();
			}

			// 将返回的输入流转换成字符串
			InputStream inputStream = httpUrlConn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
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
			System.out.println(buffer.toString());
			jsonObject = JSONObject.parseObject(buffer.toString());
		} catch (ConnectException ce) {
			System.out.println("Weixin server connection timed out.");
		} catch (Exception e) {
			System.out.println("https request error:{}" + e.getMessage());
		}
		return jsonObject;
	}

	/**
	 * 获取Request请求的路径信息 带参数
	 * 
	 * @param request
	 * @return
	 */
	public static String getRequestUrlWithParams(HttpServletRequest request) {

		String backurl = request.getScheme() + "://" + request.getServerName() + request.getRequestURI() + "?" + request.getQueryString();
		return backurl;
	}

	public static String gerRequestUrl(HttpServletRequest request) {

		return request.getScheme() + "://" + request.getServerName() + request.getRequestURI();
	}

	/**
	 * 获得请求路径
	 * 
	 * @param request
	 * @return
	 */
	public static String getRequestPath(HttpServletRequest request) {

		/*
		 * String requestPath = request.getRequestURI() + "?" +
		 * request.getQueryString();
		 * if (requestPath.indexOf("&") > -1) {// 去掉其他参数
		 * requestPath = requestPath.substring(0, requestPath.indexOf("&"));
		 * }
		 * requestPath = requestPath.substring(request.getContextPath().length()
		 * + 1);// 去掉项目路径
		 * return requestPath;
		 */
		String requestPath = "/" + request.getRequestURI();
		if (requestPath.indexOf("&") > -1) {// 去掉其他参数
			requestPath = requestPath.substring(0, requestPath.indexOf("&"));
		}
		requestPath = requestPath.substring(request.getContextPath().length() + 1);// 去掉项目路径
		return requestPath;
	}

	/**
	 * 获取虎丘openid
	 * 对应着微信公众账号
	 * 
	 * @return
	 */
	public static String getOpenid(HttpServletRequest request) {

		return StringUtils.isBlank(request.getParameter("openid")) ? (getSessionOpenid(request)) : request.getParameter("openid");
	}

	/**
	 * 从缓存中获取openid(只要用户通过author2.0即可使用该方法获取缓存)
	 * 
	 * @param request
	 * @return
	 */
	public static String getSessionOpenid(HttpServletRequest request) {

		HttpSession session = request.getSession();
		return session.getAttribute(WeixinConstans.USER_OPENID) == null ? null : session.getAttribute(WeixinConstans.USER_OPENID).toString();
	}

	/**
	 * 获取accessToken
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String getAccessToken() throws Exception {

		String accessToken = JedisUtils.get(WEIXIN_ACCESS_TOKEN);
		if (null == accessToken || StringUtils.isBlank(accessToken)) {
			String requestUrl = WeixinUtil.access_token_url.replace("APPID", WEIXIN_APP_ID).replace("APPSECRET", WEIXIN_APP_SECRET);
			JSONObject jsonObject = WeixinUtil.httpRequest(requestUrl, "GET", null);
			if (jsonObject != null) {
				accessToken = jsonObject.getString("access_token");
				JedisUtils.set(WEIXIN_ACCESS_TOKEN, accessToken, 6000);
			}
		}
		System.out.println("fn：getAccessToken___" + accessToken);
		return accessToken;
	}

	/**
	 * 过滤特殊字符串(拉去用户信息时候)
	 * 
	 * @param str
	 * @return
	 */
	public static String dofilter(String str) {

		if (str == null) {
			str = "";
			return str;
		}
		String str_Result = "", str_OneStr = "";

		for (int z = 0; z < str.length(); z++) {
			str_OneStr = str.substring(z, z + 1);
			if (str_OneStr.matches("[\u4e00-\u9fa5]+") || str_OneStr.matches("[\\x00-\\x7F]+")) {
				str_Result = str_Result + str_OneStr;
			}
		}
		return str_Result;
	}

	public static void main(String[] args) {

		String requestUrl = user_info_url.replace("OPENID", "123");
		System.out.println(requestUrl);
	}
}
