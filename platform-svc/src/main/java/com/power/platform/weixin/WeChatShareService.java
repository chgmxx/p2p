package com.power.platform.weixin;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.power.platform.cms.dao.NoticeDao;
import com.power.platform.cms.entity.Notice;
import com.power.platform.common.config.Global;
import com.power.platform.common.utils.JedisUtils;
import com.power.platform.userinfo.dao.UserInfoDao;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.utils.SHA1;
import com.power.platform.weixin.utils.WeixinUtil;

/**
 * 
 * 类: WeChatShareService <br>
 * 描述: 微信分享(分享到朋友圈;发送给朋友) <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年6月6日 下午2:54:40
 */
@Component
@Path("/weChatShare")
@Service("weChatShareService")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class WeChatShareService {

	private static final Logger LOG = LoggerFactory.getLogger(WeChatShareService.class);

	/**
	 * 3：中投摩根，2018年战报.
	 */
	private static final Integer ORDER_SUM_3 = 3;

	@Resource
	private NoticeDao noticeDao;
	@Resource
	private UserInfoDao userInfoDao;

	/**
	 * 
	 * methods: shareAnnualReport_2018 <br>
	 * description: 分享中投摩根，2018年战报. <br>
	 * author: Mr.Roy <br>
	 * date: 2019年1月21日 下午2:35:21
	 * 
	 * @param from
	 * @return
	 */
	@POST
	@Path("/shareAnnualReport_2018")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> shareAnnualReport_2018(@FormParam("from") String from) {

		Map<String, Object> result = new HashMap<String, Object>();
		if (StringUtils.isBlank(from)) {
			LOG.info("fn:shareAnnualReport_2018，缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}

		LOG.info("fn:shareAnnualReport_2018，from = " + from);

		try {
			result.put("inviteUrl", "http://cicmorgan.com/zt_annual_report_share.html");
			List<Notice> list = noticeDao.findNoticeByOrdersum(ORDER_SUM_3);
			Notice notice = null;
			if (list != null && list.size() > 0) {
				notice = list.get(0);
				notice.setLogopath(Global.getConfig("img_new_path") + notice.getLogopath());
			}
			LOG.info("fn:shareAnnualReport_2018，接口调用成功！");
			result.put("state", "0");
			result.put("message", "接口调用成功！");
			result.put("data", notice);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:shareAnnualReport_2018，系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

		return result;
	}

	@POST
	@Path("/getWeChatShareInfo")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> getCmsListByType(@FormParam("from") String from, @FormParam("ordersum") String ordersum, @FormParam("token") String token) {

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(from) || StringUtils.isBlank(ordersum) || StringUtils.isBlank(token)) {
			LOG.info("fn:getWeChatShareInfo,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}

		try {

			/**
			 * 缓存中获取客户信息.
			 */
			String jedisUserId = JedisUtils.get(token);

			if (!StringUtils.isBlank(jedisUserId)) {
				UserInfo userInfo = userInfoDao.get(jedisUserId);
				if (userInfo == null) {
					userInfo = userInfoDao.getCgb(jedisUserId);
				}
				if (null != userInfo) {
					result.put("inviteUrl", Global.getConfig("wap_invite_url") + userInfo.getName());
				}
				List<Notice> list = noticeDao.findNoticeByOrdersum(Integer.valueOf(ordersum));
				Notice notice = null;
				if (list != null && list.size() > 0) {
					notice = list.get(0);
					notice.setLogopath(Global.getConfig("img_new_path") + notice.getLogopath());
				}
				LOG.info("fn:getWeChatShareInfo,接口调用成功！");
				result.put("state", "0");
				result.put("message", "接口调用成功！");
				result.put("data", notice);
			} else {
				LOG.info("fn:getWeChatShareInfo,客户帐号信息为null！");
				result.put("state", "5");
				result.put("message", "客户帐号信息为null！");
				result.put("data", null);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:getWeChatShareInfo,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

		return result;
	}

	@POST
	@Path("/getWeixinShare")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> getWeixinShare(@FormParam("from") String from, @FormParam("token") String token, @FormParam("url") String url) {

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		try {

			/**
			 * 缓存中获取客户信息.
			 */

			String appId = "wx18fecbc45e8ba28b";
			String timestamp = new Date().getTime() / 1000 + "";
			String nonceStr = getRandomStringByLength(32);
			String str1 = null;
			String signature = null;

			String ACCESS_TOKEN = null;
			String jsapi_ticket = null;

			String requestUrl = WeixinUtil.access_token_url.replace("APPID", "wx18fecbc45e8ba28b").replace("APPSECRET", WeixinUtil.WEIXIN_APP_SECRET);
			JSONObject jsonObject = WeixinUtil.httpRequest(requestUrl, "GET", null);
			if (jsonObject != null) {
				ACCESS_TOKEN = jsonObject.getString("access_token");
				String requestUrl2 = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=" + ACCESS_TOKEN + "&type=jsapi";
				JSONObject jsonObject2 = WeixinUtil.httpRequest(requestUrl2, "GET", null);

				if (jsonObject2 != null) {
					jsapi_ticket = jsonObject2.getString("ticket");
					str1 = "jsapi_ticket=" + jsapi_ticket + "&noncestr=" + nonceStr + "&timestamp=" + timestamp + "&url=" + url;
					signature = new SHA1().getDigestOfString(str1.getBytes());
					LOG.info("fn:getWeixinShare,接口调用成功！");
					result.put("state", "0");
					result.put("message", "接口调用成功！");
					result.put("appId", appId);
					result.put("jsapi_ticket", jsapi_ticket);
					result.put("nonceStr", nonceStr);
					result.put("timestamp", timestamp);
					result.put("signature", signature);
				}
			} else {
				LOG.info("fn:getWeixinShare,系统异常！");
				result.put("state", "3");
				result.put("message", "系统异常！");
				result.put("data", null);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:getWeixinShare,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

		return result;
	}

	/**
	 * 获取一定长度的随机字符串
	 * 
	 * @param length
	 *            指定字符串长度
	 * @return 一定长度的字符串
	 */
	public static String getRandomStringByLength(int length) {

		String base = "abcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}
}
