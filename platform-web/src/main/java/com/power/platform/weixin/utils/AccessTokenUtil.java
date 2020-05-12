package com.power.platform.weixin.utils;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.power.platform.cache.Cache;
import com.power.platform.common.utils.MemCachedUtis;

@Service("accessToken")
@Lazy(false)
public class AccessTokenUtil {

	private static final Logger logger = Logger.getLogger(AccessTokenUtil.class);

	@Scheduled(initialDelay = 0, fixedRate = 1000 * 60 * 115)
	public void runJob() {

		// 获得token
		Cache cache = null;
		try {
			cache = MemCachedUtis.getMemCached();
			String requestUrl = WeixinUtil.access_token_url.replace("APPID", "123456").replace("APPSECRET", WeixinUtil.WEIXIN_APP_SECRET);
			JSONObject jsonObject = WeixinUtil.httpRequest(requestUrl, "GET", null);
			if (jsonObject != null) {
				cache.set(WeixinUtil.WEIXIN_ACCESS_TOKEN, 6000, jsonObject.getString("access_token"));
				logger.info("请求获得access_token接口成功");
			} else {
				logger.info("请求获得access_token接口失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
