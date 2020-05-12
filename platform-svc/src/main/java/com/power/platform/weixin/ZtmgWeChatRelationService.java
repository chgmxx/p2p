package com.power.platform.weixin;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.MemCachedUtis;
import com.power.platform.userinfo.dao.UserAccountInfoDao;
import com.power.platform.userinfo.dao.UserLoginDao;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.weixin.dao.ZtmgWechatRelationDao;
import com.power.platform.weixin.entity.ZtmgWechatRelation;
import com.power.platform.weixin.service.WeixinSendTempMsgService;
import com.power.platform.weixin.service.ZtmgWechatRelationService;

/**
 * 
 * 类: ZtmgWeChatRelationService <br>
 * 描述: 中投摩根，客户资料与微信资料的关系建立. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年6月7日 上午10:59:43
 */
@Component
@Path("/weChatRelation")
@Service("ztmgWeChatRelationService")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class ZtmgWeChatRelationService {

	private static final Logger LOG = LoggerFactory.getLogger(ZtmgWeChatRelationService.class);

	@Resource
	private UserLoginDao userLoginDao;
	@Resource
	private UserAccountInfoDao userAccountInfoDao;
	@Resource
	private ZtmgWechatRelationDao ztmgWechatRelationDao;
	@Resource
	private WeixinSendTempMsgService weixinSendTempMsgService;
	
	/**
	 * 
	 * 方法: wechatIsExistsBinding <br>
	 * 描述: 客户是否绑定微信. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年6月7日 下午3:20:41
	 * 
	 * @param from
	 * @param openId
	 * @return
	 */
	@POST
	@Path("/wechatIsExistsBinding")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> wechatIsExistsBinding(@FormParam("from") String from, @FormParam("openId") String openId) {

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();
		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(from) || StringUtils.isBlank(openId)) {
			LOG.info("fn:wechatIsExistsBinding,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}

		try {
			// 根据openId查询关系是否建立.
			ZtmgWechatRelation ztmgWechatRelation = ztmgWechatRelationDao.findByOpenId(openId);
			if (ztmgWechatRelation != null && ztmgWechatRelation.getState().equals(ZtmgWechatRelationService.BIND_STATE_3)) {
				LOG.info("fn:wechatIsExistsBinding,客户已绑定，可以解除绑定！");
				result.put("state", "0");
				result.put("message", "客户已绑定，可以解除绑定！");
				result.put("data", null);
			} else {
				LOG.info("fn:wechatIsExistsBinding,客户没有绑定，可以立即绑定！");
				result.put("state", "5");
				result.put("message", "客户没有绑定，可以立即绑定！");
				result.put("data", null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:wechatIsExistsBinding,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}
		return result;
	}

	/**
	 * 
	 * 方法: immediatelyBinding <br>
	 * 描述: 立即绑定. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年6月7日 上午11:44:50
	 * 
	 * @param from
	 * @param mobile
	 * @param pwd
	 * @param openId
	 * @param nickname
	 * @param headPortraitUrl
	 * @return
	 */
	@POST
	@Path("/immediatelyBinding")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> immediatelyBinding(@FormParam("from") String from, @FormParam("mobile") String mobile, @FormParam("pwd") String pwd, @FormParam("openId") String openId) {

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(from) || StringUtils.isBlank(mobile) || StringUtils.isBlank(pwd) || StringUtils.isBlank(openId)) {
			LOG.info("fn:immediatelyBinding,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}

		try {
			// 根据openId查询关系是否建立.
			ZtmgWechatRelation ztmgWechatRelation = ztmgWechatRelationDao.findByOpenId(openId);
			if (ztmgWechatRelation != null && ztmgWechatRelation.getState().equals(ZtmgWechatRelationService.BIND_STATE_3) ) {
				LOG.info("fn:immediatelyBinding,微信重复绑定！");
				result.put("state", "6");
				result.put("message", "微信重复绑定！");
				result.put("data", null);
				return result;
			}
			
			// --
			UserInfo userInfo = new UserInfo();
			userInfo.setName(mobile);
			List<UserInfo> list = userLoginDao.findList(userInfo);
			if (list != null && list.size() > 0) {
				// 账号信息.
				UserInfo model = list.get(0);
				// 判断密码是否输入正确
				String old_pwd = model.getPwd();
				String new_pwd = EncoderUtil.encrypt(pwd);
				if(!old_pwd.equals(new_pwd)){
					LOG.info("fn:immediatelyBinding,密码输入错误！");
					result.put("state", "4");
					result.put("message", "密码输入错误");
					return result;
				}
				
				if (ztmgWechatRelation != null ) {
					// 进行关系绑定
					ztmgWechatRelation.setState(ZtmgWechatRelationService.BIND_STATE_3);
					ztmgWechatRelation.setBindDate(new Date());
					ztmgWechatRelation.setUserId(model.getId());
					int flag = ztmgWechatRelationDao.update(ztmgWechatRelation);
					if (flag == 1) {
						weixinSendTempMsgService.sendBindWeixinMsg(userInfo);
						LOG.info("fn:immediatelyBinding,微信绑定成功！");
						result.put("state", "0");
						result.put("message", "微信绑定成功！");
						result.put("data", null);
					} else {
						LOG.info("fn:immediatelyBinding,系统错误！");
						result.put("state", "1");
						result.put("message", "系统错误！");
						result.put("data", null);
					}
				}else {
					ztmgWechatRelation = new ZtmgWechatRelation();
					ztmgWechatRelation.setState(ZtmgWechatRelationService.BIND_STATE_3);
					ztmgWechatRelation.setBindDate(new Date());
					ztmgWechatRelation.setUserId(model.getId());
					ztmgWechatRelation.setOpenId(openId);
					ztmgWechatRelation.setNickname("");
					ztmgWechatRelation.setAccountId(model.getAccountId());
					ztmgWechatRelation.setCreateDate(new Date());
					ztmgWechatRelation.setId(IdGen.uuid());
					int flag = ztmgWechatRelationDao.insert(ztmgWechatRelation);
					if (flag == 1) {
						weixinSendTempMsgService.sendBindWeixinMsg(userInfo);
						LOG.info("fn:immediatelyBinding,微信绑定成功！");
						result.put("state", "0");
						result.put("message", "微信绑定成功！");
						result.put("data", null);
					} else {
						LOG.info("fn:immediatelyBinding,系统错误！");
						result.put("state", "1");
						result.put("message", "系统错误！");
						result.put("data", null);
					}
				}
				
			} else {
				LOG.info("fn:immediatelyBinding,查无此人，请确认输入信息！");
				result.put("state", "5");
				result.put("message", "该手机号码未在平台注册！");
				result.put("data", null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:immediatelyBinding,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}
		return result;
	}

	/**
	 * 
	 * 方法: unBinding <br>
	 * 描述: 解除绑定. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年6月7日 上午11:45:05
	 * 
	 * @param from
	 * @param mobile
	 * @param pwd
	 * @param openId
	 * @param nickname
	 * @param headPortraitUrl
	 * @return
	 */
	@POST
	@Path("/unBinding")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> unBinding(@FormParam("from") String from, @FormParam("mobile") String mobile, @FormParam("pwd") String pwd, @FormParam("openId") String openId) {

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(from) || StringUtils.isBlank(mobile) || StringUtils.isBlank(pwd) || StringUtils.isBlank(openId)) {
			LOG.info("fn:unBinding,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}

		try {
			Cache cache = MemCachedUtis.getMemCached();
			UserInfo userInfo = new UserInfo();
			userInfo.setName(mobile);
			List<UserInfo> list = userLoginDao.findList(userInfo);
			if (list != null && list.size() > 0) {
				userInfo = list.get(0);
				// 判断密码是否输入正确
				String old_pwd = userInfo.getPwd();
				String new_pwd = EncoderUtil.encrypt(pwd);
				if(!old_pwd.equals(new_pwd)){
					LOG.info("fn:immediatelyBinding,密码输入错误！");
					result.put("state", "4");
					result.put("message", "密码输入错误");
					return result;
				}
				
				ZtmgWechatRelation ztmgWechatRelation = ztmgWechatRelationDao.findByOpenId(openId);
				if(ztmgWechatRelation.getUserId() != null && ztmgWechatRelation.getUserId() != "" ){
					if(userInfo.getId().equals(ztmgWechatRelation.getUserId())){
						// 解除绑定.
						ztmgWechatRelation.setState(ZtmgWechatRelationService.FOCUS_STATE_1);
						ztmgWechatRelation.setUpdateDate(new Date());;
						int flag = ztmgWechatRelationDao.update(ztmgWechatRelation);
						if (flag == 1) {
							String cacheUser = cache.get(userInfo.getId());
							if(cacheUser != null && !StringUtils.isBlank(cacheUser)){
								// 移除token信息
								cache.delete(cacheUser);
								cache.delete(userInfo.getId());
							}
							weixinSendTempMsgService.sendUnBindWeixinMsg(userInfo);
							LOG.info("fn:unBinding,微信解除绑定成功！");
							result.put("state", "0");
							result.put("message", "微信解除绑定成功！");
							result.put("data", null);
						} else {
							LOG.info("fn:unBinding,系统错误！");
							result.put("state", "1");
							result.put("message", "系统错误！");
							result.put("data", null);
						}
					} else {
						LOG.info("fn:unBinding,微信号暂未绑定平台账户，解绑失败");
						result.put("state", "6");
						result.put("message", "微信号暂未绑定平台账户，解绑失败");
						result.put("data", null);
						return result;
					}
				} else {
					LOG.info("fn:unBinding,关系不存在，微信解绑失败！");
					result.put("state", "6");
					result.put("message", "关系不存在，微信解绑失败！");
					result.put("data", null);
					return result;
				}
					
			} else {
				LOG.info("fn:unBinding,查无此人，请确认输入信息！");
				result.put("state", "5");
				result.put("message", "该手机号码未在平台注册！");
				result.put("data", null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:unBinding,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}
		return result;
	}

}
