package com.power.platform.user;

import java.util.HashMap;
import java.util.Map;

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

import com.power.platform.cache.Cache;
import com.power.platform.common.utils.EncoderUtil;
import com.power.platform.common.utils.JedisUtils;
import com.power.platform.common.utils.MemCachedUtis;
import com.power.platform.userinfo.dao.UserAccountInfoDao;
import com.power.platform.userinfo.dao.UserInfoDao;
import com.power.platform.userinfo.entity.Principal;
import com.power.platform.userinfo.entity.UserInfo;

/**
 * 
 * 类: UserGesturePwdRestService <br>
 * 描述: 用户手势密码，提供的唯一SVC. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年5月6日 下午1:33:09
 */
@Component
@Path("/gesture")
@Service("userGesturePwdRestService")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class UserGesturePwdRestService {

	private static final Logger LOG = LoggerFactory.getLogger(UserGesturePwdRestService.class);

	@Resource
	private UserInfoDao userInfoDao;
	@Resource
	private UserAccountInfoDao userAccountInfoDao;

	/**
	 * 
	 * 方法: cancelGesturePwd <br>
	 * 描述: 取消手势密码. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年5月23日 下午2:43:11
	 * 
	 * @param from
	 * @param token
	 * @return
	 */
	@POST
	@Path("/cancelGesturePwd")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> cancelGesturePwd(@FormParam("from") String from, @FormParam("token") String token) {

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(from) || StringUtils.isBlank(token)) {
			LOG.info("fn:cancelGesturePwd,缺少必要参数！");
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
					UserInfo entity = userInfoDao.get(jedisUserId);
					if(entity==null){
						entity = userInfoDao.getCgb(jedisUserId);
					}
					if (null != entity) {
						// 设置手势密码.
						entity.setGesturePwd(null);
						int flag = userInfoDao.update(entity);
						if (flag == 1) {
							//设置缓存
							JedisUtils.set(token, entity.getId(), 1200);

							LOG.info("fn:cancelGesturePwd,取消手势密码成功！");
							result.put("state", "0");
							result.put("message", "取消手势密码成功！");
							result.put("data", null);
							return result;
						} else {
							LOG.info("fn:cancelGesturePwd,取消手势密码失败！");
							result.put("state", "6");
							result.put("message", "取消手势密码失败！");
							result.put("data", null);
							return result;
						}
					} else {
						LOG.info("fn:cancelGesturePwd,客户账号信息为NULL！");
						result.put("state", "5");
						result.put("message", "客户账号信息为NULL！");
						result.put("data", null);
						return result;
					}
			} else {
				LOG.info("fn:cancelGesturePwd,客户账号信息为NULL！");
				result.put("state", "5");
				result.put("message", "客户账号信息为NULL！");
				result.put("data", null);
				return result;
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:cancelGesturePwd,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}
	}

	/**
	 * 
	 * 方法: setGesturePwd <br>
	 * 描述: 客户设置手势密码. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年5月6日 下午1:44:50
	 * 
	 * @param from
	 * @param token
	 * @param gesture
	 * @return
	 */
	@POST
	@Path("/setGesturePwd")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> setGesturePwd(@FormParam("from") String from, @FormParam("token") String token, @FormParam("gesturePwd") String gesturePwd) {

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(from) || StringUtils.isBlank(token) || StringUtils.isBlank(gesturePwd)) {
			LOG.info("fn:setGesturePwd,缺少必要参数！");
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
					UserInfo entity = userInfoDao.get(jedisUserId);
					if(entity==null){
						entity = userInfoDao.getCgb(jedisUserId);
					}
					if (null != entity) {
						// 设置手势密码.
						entity.setGesturePwd(EncoderUtil.encrypt(gesturePwd));
						int flag = userInfoDao.update(entity);
						if (flag == 1) {
							//设置缓存
							JedisUtils.set(token, entity.getId(), 1200);

							LOG.info("fn:setGesturePwd,设置手势密码成功！");
							result.put("state", "0");
							result.put("message", "设置手势密码成功！");
							return result;
						} else {
							LOG.info("fn:setGesturePwd,设置手势密码失败！");
							result.put("state", "6");
							result.put("message", "设置手势密码失败！");
							result.put("data", null);
							return result;
						}
					} else {
						LOG.info("fn:setGesturePwd,客户账号信息为NULL！");
						result.put("state", "5");
						result.put("message", "客户账号信息为NULL！");
						result.put("data", null);
						return result;
					}
				}else {
				LOG.info("fn:setGesturePwd,客户账号信息为NULL！");
				result.put("state", "5");
				result.put("message", "客户账号信息为NULL！");
				result.put("data", null);
				return result;
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:setGesturePwd,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}
	}
	
	
	/**
	 * 
	 * 方法: setGesturePwd <br>
	 * 描述: 客户设置手势密码.---前端加密 <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年5月6日 下午1:44:50
	 * 
	 * @param from
	 * @param token
	 * @param gesture
	 * @return
	 */
	@POST
	@Path("/newSetGesturePwd")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> newSetGesturePwd(@FormParam("from") String from, @FormParam("token") String token, @FormParam("gesturePwd") String gesturePwd) {

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(from) || StringUtils.isBlank(token) || StringUtils.isBlank(gesturePwd)) {
			LOG.info("fn:setGesturePwd,缺少必要参数！");
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
					UserInfo entity = userInfoDao.get(jedisUserId);
					if(entity==null){
						entity = userInfoDao.getCgb(jedisUserId);
					}
					if (null != entity) {
						// 设置手势密码.
						entity.setGesturePwd(gesturePwd);
						int flag = userInfoDao.update(entity);
						if (flag == 1) {
							//设置缓存
							JedisUtils.set(token, entity.getId(), 1200);

							LOG.info("fn:setGesturePwd,设置手势密码成功！");
							result.put("state", "0");
							result.put("message", "设置手势密码成功！");
							return result;
						} else {
							LOG.info("fn:setGesturePwd,设置手势密码失败！");
							result.put("state", "6");
							result.put("message", "设置手势密码失败！");
							result.put("data", null);
							return result;
						}
					} else {
						LOG.info("fn:setGesturePwd,客户账号信息为NULL！");
						result.put("state", "5");
						result.put("message", "客户账号信息为NULL！");
						result.put("data", null);
						return result;
					}
				}else {
				LOG.info("fn:setGesturePwd,客户账号信息为NULL！");
				result.put("state", "5");
				result.put("message", "客户账号信息为NULL！");
				result.put("data", null);
				return result;
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:setGesturePwd,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}
	}

}
