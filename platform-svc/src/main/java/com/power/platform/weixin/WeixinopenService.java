package com.power.platform.weixin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.power.platform.cache.Cache;
import com.power.platform.cgb.dao.CgbUserAccountDao;
import com.power.platform.common.utils.EncoderUtil;
import com.power.platform.common.utils.MemCachedUtis;
import com.power.platform.userinfo.dao.UserAccountInfoDao;
import com.power.platform.userinfo.dao.UserLoginDao;
import com.power.platform.userinfo.entity.Principal;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.userinfo.service.UserInfoService;
import com.power.platform.weixin.dao.ZtmgWechatRelationDao;
import com.power.platform.weixin.entity.ZtmgWechatRelation;
import com.power.platform.weixin.service.ZtmgWechatRelationService;



/**
 * 微信接口
 * @author timefiles
 *
 */
@Path("wxopen")
@Service("weixinopenService")
@Produces({ MediaType.APPLICATION_JSON})
public class WeixinopenService {
	
	private static Logger logger = Logger.getLogger(WeixinopenService.class);	
	
	@Resource
	private ZtmgWechatRelationDao ztmgWechatRelationDao;
	@Resource
	private UserInfoService userInfoService;
	@Resource
	private UserLoginDao userLoginDao;
	@Resource
	private UserAccountInfoDao userAccountInfoDao;
	@Resource
	private CgbUserAccountDao cgbUserAccountDao;
	
	@GET
	@Path("/getOpenid")
	public String getOpenid() throws Exception{
        return null;
	}
	
	
	@GET
	@Path("/weixinIndex")
	public void weixinIndex(@Context HttpServletRequest request, @Context HttpServletResponse response) throws Exception{
		String openId = (String) request.getAttribute("openId");
		System.out.println("fn{weixinIndex}:openId = " + openId);
		ZtmgWechatRelation weixinUser = null;
		if(openId != null && !StringUtils.isBlank(openId)){
			weixinUser = ztmgWechatRelationDao.findByOpenId(openId);
			if(weixinUser != null){
				logger.info("fn{weixinIndex}:weixinUserId = " + weixinUser.getId());
			}
		}
		
		Principal principal = new Principal();
		Date time = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhhmmss");
		
		
		// 判断用户是否绑定
		if(weixinUser != null && weixinUser.getState().equals(ZtmgWechatRelationService.BIND_STATE_3)){
			String userId = weixinUser.getUserId();
			if(userId != null && !StringUtils.isBlank(userId)){
				// 获取到用户信息
				UserInfo userInfo = userInfoService.get(userId);
				if(userInfo==null){
					userInfo = userInfoService.getCgb(userId);
				}
				// 生成token
				principal.setUserInfo(userLoginDao.get(userInfo.getId()));
				principal.setUserAccountInfo(userAccountInfoDao.getUserAccountInfo(userInfo.getId()));
				principal.setCgbUserAccount(cgbUserAccountDao.getUserAccountInfo(userInfo.getId()));
				String token = EncoderUtil.encrypt(sdf.format(time) + userInfo.getId()).replace("+", "a");
				// 获取缓存
				Cache cache = MemCachedUtis.getMemCached();
				Map<String, String> cacheLoginedUser = cache.get("cacheLoginedUser");
				
				// 系统没有登录用户（一般不会进该方法）
				if (cacheLoginedUser == null) {
					cacheLoginedUser = new HashMap<String, String>();
				}

				String isexitToken = cacheLoginedUser.get(userInfo.getId());
				if (isexitToken != null && isexitToken != "") {
					// 不等于null 获取到原来的token，并且移除
					cache.delete(isexitToken);
				}

				cacheLoginedUser.put(userInfo.getId(), token);
				cache.set("cacheLoginedUser", cacheLoginedUser);

				// 原来未登录，
				cache.set(token, 1200, principal);
				cache.set(userInfo.getId(), 1200, token);
				
				logger.info("fn{weixinIndex}:用户已绑定，免登陆成功");
				response.sendRedirect("http://www.cicmorgan.com/index.html?wxNoLoginToken=" + token);
				//response.sendRedirect("http://cicmorgan.com");

			} else {		// 用户已绑定，未获取到用户ID
				logger.info("fn{weixinIndex}:用户已绑定，未获取到用户ID");
				response.sendRedirect("https://www.cicmorgan.com/");
			}
		} else {		// 用户未绑定，跳转到首页
			logger.info("fn{weixinIndex}:用户未绑定，跳转到首页");
			response.sendRedirect("http://www.cicmorgan.com");
		}
		
	}
}
