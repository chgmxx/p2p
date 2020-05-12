package com.power.platform.cgb.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.power.platform.cgb.entity.CgbUserBankCard;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.JedisUtils;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.service.userinfo.CreditUserInfoService;
import com.power.platform.pay.recharge.service.NewRechargeService;
import com.power.platform.pay.recharge.service.UserRechargeService;
import com.power.platform.pay.utils.FuncUtils;
import com.power.platform.pay.utils.LLPayUtil;
import com.power.platform.userinfo.dao.UserLogDao;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.userinfo.entity.UserLog;
import com.power.platform.userinfo.service.UserAccountInfoService;
import com.power.platform.userinfo.service.UserBankCardService;
import com.power.platform.userinfo.service.UserInfoService;

/**
 * 用户充值
 * 
 * @author YHAGZALUN WO SJIAOSY
 *
 */
@Component
@Path("/newpay")
@Service("cGBRechargeService")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class CGBRechargeService {

	private static final Logger LOG = LoggerFactory.getLogger(CGBRechargeService.class);

	@Autowired
	private NewRechargeService newRechargeService;
	@Autowired
	private UserAccountInfoService userAccountInfoService;
	@Autowired
	private UserRechargeService userRechargeService;
	@Autowired
	private UserBankCardService userBankCardService;
	@Autowired
	private CgbUserBankCardService cgbUserBankCardService;
	@Autowired
	private CreditUserInfoService creditUserInfoService;
	@Autowired
	private UserInfoService userInfoService;
	@Autowired
	private UserLogDao userLogDao;

	/**
	 * 充值----pc端web
	 * 
	 * @param amount
	 * @param from
	 * @param token
	 * @param servletRequest
	 * @return
	 */
	@POST
	@Path("/authRechargeWeb")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> authRechargeWeb(@FormParam("amount") String amount, @FormParam("from") String from, @FormParam("token") String token, @Context HttpServletRequest servletRequest) {

		String ip = LLPayUtil.getIpAddr(servletRequest);
		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(token) || StringUtils.isBlank(from) || StringUtils.isBlank(amount)) {
			LOG.info("fn:authRechargeWeb,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}

		/**
		 * 充值金额校验
		 */
		if (!FuncUtils.isMoney(amount)) {
			LOG.info("fn:authRechargeWeb,充值金额有误！");
			result.put("state", "6");
			result.put("message", "充值金额有误！");
			result.put("data", null);
			return result;
		}
		/**
		 * 获取用户信息，拼装请求数据
		 */
		try {
			// 从缓存获取用户信息
			String jedisUserId = JedisUtils.get(token);
			if (!StringUtils.isBlank(jedisUserId)) {
				UserInfo userInfo = userInfoService.getCgb(jedisUserId);
				if (userInfo.getCgbBindBankCardState().equals(UserInfo.BIND_CARD_NO)) {
					LOG.info("fn:authRechargeWeb,用户未绑卡，请先进行绑卡！");
					result.put("state", "5");
					result.put("message", "用户未绑卡，请先进行绑卡！");
					result.put("data", null);
					return result;
				}
				Map<?, ?> data = newRechargeService.authRechargeWeb(ip, amount, userInfo.getId(), userInfo.getAccountId(), "01");
				if (userInfo != null) {
					UserLog log = new UserLog();
					log.setId(String.valueOf(IdGen.randomLong()));
					log.setUserId(userInfo.getId());
					if (userInfo.getRealName() != null)
						log.setUserName(userInfo.getRealName() + userInfo.getName());
					else
						log.setUserName(userInfo.getName());
					log.setType("4");
					log.setCreateDate(new Date());
					if (userInfo.getRealName() != null)
						log.setRemark(userInfo.getRealName() + "-充值（网银）订单生成成功，充值" + amount + "元");
					else
						log.setRemark(userInfo.getName() + "-充值（网银）订单生成成功，充值" + amount + "元");
					userLogDao.insert(log);
				}
				result.put("state", "0");
				result.put("message", "充值订单生成成功。！");
				result.put("data", data);
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:authRechargeWeb,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}
		return result;
	}

	/**
	 * 大额充值----pc端web
	 * 
	 * @param amount
	 * @param from
	 * @param token
	 * @param servletRequest
	 * @return
	 */
	@POST
	@Path("/largeRechargeWeb")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> largeRechargeWeb(@FormParam("amount") String amount, @FormParam("from") String from, @FormParam("token") String token, @Context HttpServletRequest servletRequest) {

		String ip = LLPayUtil.getIpAddr(servletRequest);
		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(token) || StringUtils.isBlank(from) || StringUtils.isBlank(amount)) {
			LOG.info("largeRechargeWeb,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}

		/**
		 * 充值金额校验
		 */
		if (!FuncUtils.isMoney(amount)) {
			LOG.info("fn:largeRechargeWeb,充值金额有误！");
			result.put("state", "6");
			result.put("message", "充值金额有误！");
			result.put("data", null);
			return result;
		}
		/**
		 * 获取用户信息，拼装请求数据
		 */
		try {
			// 从缓存获取用户信息
			String jedisUserId = JedisUtils.get(token);
			if (!StringUtils.isBlank(jedisUserId)) {
				UserInfo userInfo = userInfoService.getCgb(jedisUserId);
				if (userInfo.getCgbBindBankCardState().equals(UserInfo.BIND_CARD_NO)) {
					LOG.info("fn:largeRechargeWeb,用户未绑卡，请先进行绑卡！");
					result.put("state", "5");
					result.put("message", "用户未绑卡，请先进行绑卡！");
					result.put("data", null);
					return result;
				}
				Map<?, ?> data = newRechargeService.largeRechargeWeb(ip, amount, userInfo.getId(), userInfo.getAccountId(), "01");
				if (userInfo != null) {
					UserLog log = new UserLog();
					log.setId(String.valueOf(IdGen.randomLong()));
					log.setUserId(userInfo.getId());
					if (userInfo.getRealName() != null)
						log.setUserName(userInfo.getRealName() + userInfo.getName());
					else
						log.setUserName(userInfo.getName());
					log.setType("3");
					log.setCreateDate(new Date());
					if (userInfo.getRealName() != null)
						log.setRemark(userInfo.getRealName() + "-充值（转账）订单生成成功，充值" + amount + "元");
					else
						log.setRemark(userInfo.getName() + "-充值（转账）订单生成成功，充值" + amount + "元");
					userLogDao.insert(log);
				}
				result.put("state", "0");
				result.put("message", "充值订单生成成功。！");
				result.put("data", data);
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:largeRechargeWeb,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}
		return result;
	}

	/**
	 * 充值-----手机端H5
	 * 
	 * @param amount
	 * @param from
	 * @param token
	 * @param servletRequest
	 * @return
	 */
	@POST
	@Path("/authRechargeH5")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> authRechargeH5(@FormParam("amount") String amount, @FormParam("from") String from, @FormParam("token") String token, @FormParam("bizType") String bizType, @Context HttpServletRequest servletRequest) {

		String ip = LLPayUtil.getIpAddr(servletRequest);
		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(token) || StringUtils.isBlank(from) || StringUtils.isBlank(amount)) {
			LOG.info("fn:authRechargeWeb,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}

		/**
		 * 充值金额校验
		 */
		if (!FuncUtils.isMoney(amount)) {
			LOG.info("fn:authRechargeWeb,充值金额有误！");
			result.put("state", "6");
			result.put("message", "充值金额有误！");
			result.put("data", null);
			return result;
		}
		/**
		 * 获取用户信息，拼装请求数据
		 */
		try {
			// 从缓存获取用户信息
			String jedisUserId = JedisUtils.get(token);
			if (StringUtils.isBlank(jedisUserId)) {
				LOG.info("fn:cash,登录超时，请重新登录！");
				result.put("state", "4");
				result.put("message", "登录超时，请重新登录！");
				result.put("data", null);
				return result;
			}
			UserInfo userInfo = userInfoService.getCgb(jedisUserId);
			if (userInfo != null) {
				if (userInfo.getCgbBindBankCardState() == UserInfo.BIND_CARD_NO) {
					LOG.info("fn:authRechargeWeb,用户未绑卡，请先进行绑卡！");
					result.put("state", "5");
					result.put("message", "用户未绑卡，请先进行绑卡！");
					result.put("data", null);
					return result;
				}
			} else {
				result.put("state", "4");
				result.put("message", "系统超时");
				result.put("data", null);
				return result;
			}

			Map<?, ?> data = newRechargeService.authRechargeH5(ip, amount, token, bizType, from);
			result.put("state", "0");
			result.put("message", "充值订单生成成功。！");
			result.put("data", data);
			return result;

		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:authRechargeWeb,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}
	}

	/**
	 * 大额充值-----手机端H5
	 * 
	 * @param amount
	 * @param from
	 * @param token
	 * @param servletRequest
	 * @return
	 */
	@POST
	@Path("/largeRechargeH5")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> largeRechargeH5(@FormParam("amount") String amount, @FormParam("from") String from, @FormParam("token") String token, @FormParam("bizType") String bizType, @Context HttpServletRequest servletRequest) {

		String ip = LLPayUtil.getIpAddr(servletRequest);
		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(token) || StringUtils.isBlank(from) || StringUtils.isBlank(amount)) {
			LOG.info("largeRechargeH5,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}

		/**
		 * 充值金额校验
		 */
		if (!FuncUtils.isMoney(amount)) {
			LOG.info("largeRechargeH5,充值金额有误！");
			result.put("state", "6");
			result.put("message", "充值金额有误！");
			result.put("data", null);
			return result;
		}
		/**
		 * 获取用户信息，拼装请求数据
		 */
		try {
			// 从缓存获取用户信息
			String jedisUserId = JedisUtils.get(token);
			if (StringUtils.isBlank(jedisUserId)) {
				LOG.info("fn:cash,登录超时，请重新登录！");
				result.put("state", "4");
				result.put("message", "登录超时，请重新登录！");
				result.put("data", null);
				return result;
			}
			UserInfo userInfo = userInfoService.getCgb(jedisUserId);
			if (userInfo != null) {
				if (userInfo.getCgbBindBankCardState() == UserInfo.BIND_CARD_NO) {
					LOG.info("fn:largeRechargeH5,用户未绑卡，请先进行绑卡！");
					result.put("state", "5");
					result.put("message", "用户未绑卡，请先进行绑卡！");
					result.put("data", null);
					return result;
				}
			} else {
				result.put("state", "4");
				result.put("message", "系统超时");
				result.put("data", null);
				return result;
			}

			Map<?, ?> data = newRechargeService.largeRechargeH5(ip, amount, token, bizType, from);
			result.put("state", "0");
			result.put("message", "充值订单生成成功。！");
			result.put("data", data);
			return result;

		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:largeRechargeH5,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}
	}

	/**
	 * 充值----借款PC端web
	 * 
	 * @param amount
	 * @param from
	 * @param token
	 * @param servletRequest
	 * @return
	 */
	@POST
	@Path("/creditAuthRechargeWeb")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> creditAuthRechargeWeb(@FormParam("amount") String amount, @FormParam("from") String from, @FormParam("userId") String userId, @Context HttpServletRequest servletRequest) {

		String ip = LLPayUtil.getIpAddr(servletRequest);
		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(userId) || StringUtils.isBlank(from) || StringUtils.isBlank(amount)) {
			LOG.info("fn:authRechargeWeb,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}

		/**
		 * 充值金额校验
		 */
		if (!FuncUtils.isMoney(amount)) {
			LOG.info("fn:authRechargeWeb,充值金额有误！");
			result.put("state", "6");
			result.put("message", "充值金额有误！");
			result.put("data", null);
			return result;
		}
		/**
		 * 获取用户信息，拼装请求数据
		 */
		try {
			CreditUserInfo userInfo = creditUserInfoService.get(userId);
			CgbUserBankCard userBankCard = cgbUserBankCardService.getBankCardInfoByUserId(userId);
			if (userBankCard != null) {
				if (userBankCard.getState().equals("0")) {
					LOG.info("fn:authRechargeWeb,用户未绑卡，请先进行绑卡！");
					result.put("state", "5");
					result.put("message", "用户未绑卡，请先进行绑卡！");
					result.put("data", null);
					return result;
				}
			} else {
				LOG.info("fn:authRechargeWeb,用户未绑卡，请先进行绑卡！");
				result.put("state", "5");
				result.put("message", "用户未绑卡，请先进行绑卡！");
				result.put("data", null);
				return result;
			}

			Map<?, ?> data = newRechargeService.authRechargeWeb(ip, amount, userInfo.getId(), userInfo.getAccountId(), "02");
			result.put("state", "0");
			result.put("message", "充值订单生成成功。！");
			result.put("data", data);
			return result;

		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:authRechargeWeb,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}
	}

	@POST
	@Path("/creditOfflineRechargeWeb")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> creditOfflineRechargeWeb(@FormParam("amount") String amount, @FormParam("from") String from, @FormParam("userId") String userId, @Context HttpServletRequest servletRequest) {

		String ip = LLPayUtil.getIpAddr(servletRequest);
		Map<String, Object> result = new HashMap<String, Object>();
		if (StringUtils.isBlank(userId) || StringUtils.isBlank(from) || StringUtils.isBlank(amount)) {
			LOG.info("fn:creditOfflineRechargeWeb,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}

		/**
		 * 充值金额校验
		 */
		if (!FuncUtils.isMoney(amount)) {
			LOG.info("fn:creditOfflineRechargeWeb,充值金额有误！");
			result.put("state", "6");
			result.put("message", "充值金额有误！");
			result.put("data", null);
			return result;
		}
		/**
		 * 获取用户信息，拼装请求数据
		 */
		try {
			CreditUserInfo userInfo = creditUserInfoService.get(userId);
			CgbUserBankCard userBankCard = cgbUserBankCardService.getBankCardInfoByUserId(userId);
			if (userBankCard != null) {
				if (userBankCard.getState().equals("0")) {
					LOG.info("fn:creditOfflineRechargeWeb,用户未绑卡，请先进行绑卡！");
					result.put("state", "5");
					result.put("message", "用户未绑卡，请先进行绑卡！");
					result.put("data", null);
					return result;
				}
			} else {
				LOG.info("fn:creditOfflineRechargeWeb,用户未绑卡，请先进行绑卡！");
				result.put("state", "5");
				result.put("message", "用户未绑卡，请先进行绑卡！");
				result.put("data", null);
				return result;
			}

			Map<?, ?> encryptRet = newRechargeService.offlineRechargeWeb(ip, amount, userInfo.getId(), userInfo.getAccountId());
			result.put("state", "0");
			result.put("message", "转账充值数据封装成功 ...");
			result.put("encryptRet", encryptRet);
			return result;

		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:creditOfflineRechargeWeb, 系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}
	}

}
