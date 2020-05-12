package com.power.platform.verify.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.power.platform.activity.dao.ARateCouponDicDao;
import com.power.platform.activity.dao.AUserAwardsHistoryDao;
import com.power.platform.activity.dao.AVouchersDicDao;
import com.power.platform.activity.entity.ARateCouponDic;
import com.power.platform.activity.entity.AUserAwardsHistory;
import com.power.platform.activity.entity.AVouchersDic;
import com.power.platform.activity.service.AUserAwardsHistoryService;
import com.power.platform.cgb.dao.CgbUserAccountDao;
import com.power.platform.cgb.entity.CgbUserAccount;
import com.power.platform.common.utils.EncoderUtil;
import com.power.platform.common.utils.JedisUtils;
import com.power.platform.sys.type.UserStateType;
import com.power.platform.sys.type.UserType;
import com.power.platform.userinfo.dao.UserAccountInfoDao;
import com.power.platform.userinfo.dao.UserInfoDao;
import com.power.platform.userinfo.entity.UserInfo;

/**
 * 
 * 类: CheckMobilePhoneRestService <br>
 * 描述: 根据手机检查客户信息，提供的唯一SVC. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年4月27日 上午10:10:21
 */
@Component
@Path("/verify")
@Service("verifyRestService")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class VerifyRestService {

	private static final Logger LOG = LoggerFactory.getLogger(VerifyRestService.class);

	@Resource
	private UserInfoDao userInfoDao;
	@Resource
	private UserAccountInfoDao userAccountInfoDao;
	@Resource
	private AUserAwardsHistoryDao aUserAwardsHistoryDao;
	@Resource
	private ARateCouponDicDao aRateCouponDicDao;
	@Resource
	private AVouchersDicDao aVouchersDicDao;
	@Resource
	private CgbUserAccountDao cgbUserAccountDao;

	/**
	 * 
	 * 方法: couponsOnchange <br>
	 * 描述: 选择优惠券时，变更相应元素的值. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年6月6日 下午4:45:49
	 * 
	 * @param from
	 * @param vouid
	 * @return
	 */
	@POST
	@Path("/couponsOnchange")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> couponsOnchange(@FormParam("from") String from, @FormParam("vouid") String vouid) {

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(from) || StringUtils.isBlank(vouid)) {
			LOG.info("fn:couponsOnchange,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}

		try {

			/**
			 * 数据域.
			 */
			Map<String, Object> data = new HashMap<String, Object>();
			// 优惠券Entity.
			AUserAwardsHistory entity = aUserAwardsHistoryDao.get(vouid);
			if (entity != null) {
				// 优惠券类型.
				String type = entity.getType();
				// 优惠券字典ID.
				String awardId = entity.getAwardId();
				data.put("type", type);
				// 抵用券，处理.
				if (type.equals(AUserAwardsHistoryService.COUPONS_TYPE_1)) {
					AVouchersDic model = aVouchersDicDao.get(awardId);
					// 抵用券金额.
					Double amount = model.getAmount();
					data.put("value", amount);
				}
				// 加息券，处理.
				if (type.equals(AUserAwardsHistoryService.COUPONS_TYPE_2)) {
					ARateCouponDic model = aRateCouponDicDao.get(awardId);
					// 加息券面值.
					Double rate = model.getRate();
					data.put("value", rate);
				}
				// --
				LOG.info("fn:couponsOnchange,接口调用成功！");
				result.put("state", "0");
				result.put("message", "接口调用成功！");
				result.put("data", data);
			} else {
				LOG.info("fn:couponsOnchange,接口调用成功！");
				result.put("state", "0");
				result.put("message", "接口调用成功！");
				result.put("data", null);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:couponsOnchange,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}
		return result;
	}

	/**
	 * 
	 * 方法: loginByGesturePwd <br>
	 * 描述: 根据手势密码进行登陆. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年5月6日 下午3:43:46
	 * 
	 * @param from
	 * @param mobilePhone
	 * @param gesturePwd
	 * @return
	 */
	@POST
	@Path("/loginByGesturePwd")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> loginByGesturePwd(@FormParam("from") String from, @FormParam("mobilePhone") String mobilePhone, @FormParam("gesturePwd") String gesturePwd, @Context HttpServletRequest servletRequest) {

		// IP.
		String ip = (String) servletRequest.getAttribute("ip");
		ip = ip.replace("_", ".");

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(from) || StringUtils.isBlank(mobilePhone) || StringUtils.isBlank(gesturePwd)) {
			LOG.info("fn:loginByGesturePwd,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("token", null);
			result.put("data", null);
			return result;
		}

		try {

			/**
			 * 数据域查询，是否满足登陆条件.
			 */
			UserInfo userInfo = new UserInfo();
			userInfo.setName(mobilePhone);
			userInfo.setGesturePwd(EncoderUtil.encrypt(gesturePwd));
			UserInfo entity = userInfoDao.loginByGesturePwd(userInfo);
			if (entity != null) {
				// 是否是投资用户.
				if (Integer.valueOf(UserType.BID) == entity.getUserType()) {
					// 客户状态是否正常.
					if (entity.getState() == UserStateType.NORMAL) {
						entity.setId(entity.getId());
						entity.setLastLoginDate(new Date());
						entity.setLastLoginIp(ip);
						int flag = userInfoDao.update(entity);

						// 针对老客户需要重新开户 新增账户表数据
						if (cgbUserAccountDao.getUserAccountInfo(entity.getId()) == null) {
							// 新增账户信息
							CgbUserAccount userAccountInfo = new CgbUserAccount();
							// LOG.info("Id  =  " + entity.getAccountId());
							userAccountInfo.setId(entity.getAccountId());
							userAccountInfo.setUserId(entity.getId());
							userAccountInfo.setTotalAmount(0d);
							userAccountInfo.setTotalInterest(0d);
							userAccountInfo.setAvailableAmount(0d);
							userAccountInfo.setFreezeAmount(0d);
							userAccountInfo.setRechargeAmount(0d);
							userAccountInfo.setRechargeCount(0);
							userAccountInfo.setCashAmount(0d);
							userAccountInfo.setCashCount(0);
							userAccountInfo.setCurrentAmount(0d);
							userAccountInfo.setRegularDuePrincipal(0d);
							userAccountInfo.setRegularDueInterest(0d);
							userAccountInfo.setRegularTotalAmount(0d);
							userAccountInfo.setRegularTotalInterest(0d);
							userAccountInfo.setCurrentTotalAmount(0d);
							userAccountInfo.setCurrentTotalInterest(0d);
							userAccountInfo.setCurrentYesterdayInterest(0d);
							userAccountInfo.setReguarYesterdayInterest(0d);
							userAccountInfo.setUserInfo(entity);
							// 生成客户账户
							cgbUserAccountDao.insert(userAccountInfo);
						}

						// 更新客户信息成功.
						if (flag == 1) {
							// 生成token.
							SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhhmmss");
							Date time = new Date();
							// 生成token
							String token = EncoderUtil.encrypt(sdf.format(time) + entity.getId()).replace("+", "a");
							
							Map<String, String> cacheLoginedUser = JedisUtils.getMap("cacheLoginedUser");
							// 系统没有登录用户（一般不会进该方法）
							if (cacheLoginedUser == null) {
								cacheLoginedUser = new HashMap<String, String>();
							}
							String isexitToken = cacheLoginedUser.get(entity.getId());
							if (isexitToken != null && isexitToken != "") {
								// 不等于null 获取到原来的token，并且移除
								JedisUtils.del(isexitToken);
							}
							cacheLoginedUser.put(entity.getId(), token);
							JedisUtils.setMap("cacheLoginedUser", cacheLoginedUser, 1200);
							
							//设置缓存
							String a = JedisUtils.set(token, entity.getId(), 1200);

							// 返回参数.
							LOG.info("fn:loginByGesturePwd,使用手势密码登陆成功！");
							//用户激活情况
							result.put("isActivate", entity.getIsActivate());
							result.put("certificateChecked", entity.getCertificateChecked() == null ? "2" : entity.getCertificateChecked());
							result.put("state", "0");
							result.put("message", "使用手势密码登陆成功！");
							result.put("token", token);
							result.put("data", null);
							return result;
						}
					}
				}
			} else {
				// 返回参数.
				LOG.info("fn:loginByGesturePwd,使用手势密码登陆失败！");
				result.put("state", "5");
				result.put("message", "使用手势密码登陆失败！");
				result.put("token", null);
				result.put("data", null);
				return result;
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:loginByGesturePwd,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("token", null);
			result.put("data", null);
			return result;
		}
		return result;
	}

	/**
	 * 
	 * 方法: checkMobilePhoneIsRegistered <br>
	 * 描述: 检查手机注册. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年4月27日 上午9:32:56
	 * 
	 * @param from
	 * @param mobilePhone
	 * @return
	 */
	@POST
	@Path("/checkMobilePhoneIsRegistered")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> checkMobilePhoneIsRegistered(@FormParam("from") String from, @FormParam("mobilePhone") String mobilePhone) {

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(from) || StringUtils.isBlank(mobilePhone)) {
			LOG.info("fn:checkMobilePhoneIsRegistered,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}

		try {

			UserInfo userInfo = userInfoDao.getUserInfoByPhone(mobilePhone);
			if (null != userInfo) {
				LOG.info("fn:checkMobilePhoneIsRegistered,该移动电话'已'在【中投摩根】平台注册！");
				result.put("state", "0");
				result.put("message", "该移动电话已在【中投摩根】平台注册！");
				result.put("data", null);
				return result;
			} else {
				LOG.info("fn:checkMobilePhoneIsRegistered,该移动电话'未'在【中投摩根】平台注册！");
				result.put("state", "5");
				result.put("message", "该移动电话未在【中投摩根】平台注册！");
				result.put("data", null);
				return result;
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:checkMobilePhoneIsRegistered,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

	}
	
	/**
	 * 
	 * 方法: loginByGesturePwd <br>
	 * 描述: 根据手势密码进行登陆.---前端加密 <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年5月6日 下午3:43:46
	 * 
	 * @param from
	 * @param mobilePhone
	 * @param gesturePwd
	 * @return
	 */
	@POST
	@Path("/newLoginByGesturePwd")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> newLoginByGesturePwd(@FormParam("from") String from, @FormParam("mobilePhone") String mobilePhone, @FormParam("gesturePwd") String gesturePwd, @Context HttpServletRequest servletRequest) {

		// IP.
		String ip = (String) servletRequest.getAttribute("ip");
		ip = ip.replace("_", ".");

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(from) || StringUtils.isBlank(mobilePhone) || StringUtils.isBlank(gesturePwd)) {
			LOG.info("fn:loginByGesturePwd,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("token", null);
			result.put("data", null);
			return result;
		}

		try {

			/**
			 * 数据域查询，是否满足登陆条件.
			 */
			UserInfo userInfo = new UserInfo();
			userInfo.setName(mobilePhone);
			userInfo.setGesturePwd(gesturePwd);
			UserInfo entity = userInfoDao.loginByGesturePwd(userInfo);
			if (entity != null) {
				// 是否是投资用户.
				if (Integer.valueOf(UserType.BID) == entity.getUserType()) {
					// 客户状态是否正常.
					if (entity.getState() == UserStateType.NORMAL) {
						entity.setId(entity.getId());
						entity.setLastLoginDate(new Date());
						entity.setLastLoginIp(ip);
						int flag = userInfoDao.update(entity);

						// 针对老客户需要重新开户 新增账户表数据
						if (cgbUserAccountDao.getUserAccountInfo(entity.getId()) == null) {
							// 新增账户信息
							CgbUserAccount userAccountInfo = new CgbUserAccount();
							// LOG.info("Id  =  " + entity.getAccountId());
							userAccountInfo.setId(entity.getAccountId());
							userAccountInfo.setUserId(entity.getId());
							userAccountInfo.setTotalAmount(0d);
							userAccountInfo.setTotalInterest(0d);
							userAccountInfo.setAvailableAmount(0d);
							userAccountInfo.setFreezeAmount(0d);
							userAccountInfo.setRechargeAmount(0d);
							userAccountInfo.setRechargeCount(0);
							userAccountInfo.setCashAmount(0d);
							userAccountInfo.setCashCount(0);
							userAccountInfo.setCurrentAmount(0d);
							userAccountInfo.setRegularDuePrincipal(0d);
							userAccountInfo.setRegularDueInterest(0d);
							userAccountInfo.setRegularTotalAmount(0d);
							userAccountInfo.setRegularTotalInterest(0d);
							userAccountInfo.setCurrentTotalAmount(0d);
							userAccountInfo.setCurrentTotalInterest(0d);
							userAccountInfo.setCurrentYesterdayInterest(0d);
							userAccountInfo.setReguarYesterdayInterest(0d);
							userAccountInfo.setUserInfo(entity);
							// 生成客户账户
							cgbUserAccountDao.insert(userAccountInfo);
						}

						// 更新客户信息成功.
						if (flag == 1) {
							// 生成token.
							SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhhmmss");
							Date time = new Date();
							// 生成token
							String token = EncoderUtil.encrypt(sdf.format(time) + entity.getId()).replace("+", "a");
							
							Map<String, String> cacheLoginedUser = JedisUtils.getMap("cacheLoginedUser");
							// 系统没有登录用户（一般不会进该方法）
							if (cacheLoginedUser == null) {
								cacheLoginedUser = new HashMap<String, String>();
							}
							String isexitToken = cacheLoginedUser.get(entity.getId());
							if (isexitToken != null && isexitToken != "") {
								// 不等于null 获取到原来的token，并且移除
								JedisUtils.del(isexitToken);
							}
							cacheLoginedUser.put(entity.getId(), token);
							JedisUtils.setMap("cacheLoginedUser", cacheLoginedUser, 1200);
							
							//设置缓存
							String a = JedisUtils.set(token, entity.getId(), 1200);

							// 返回参数.
							LOG.info("fn:loginByGesturePwd,使用手势密码登陆成功！");
							result.put("state", "0");
							result.put("message", "使用手势密码登陆成功！");
							result.put("token", token);
							result.put("data", null);
							return result;
						}
					}
				}
			} else {
				// 返回参数.
				LOG.info("fn:loginByGesturePwd,使用手势密码登陆失败！");
				result.put("state", "5");
				result.put("message", "使用手势密码登陆失败！");
				result.put("token", null);
				result.put("data", null);
				return result;
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:loginByGesturePwd,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("token", null);
			result.put("data", null);
			return result;
		}
		return result;
	}

}
