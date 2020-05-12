package com.power.platform.pay.service;

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
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.power.platform.cache.Cache;
import com.power.platform.common.utils.EncoderUtil;
import com.power.platform.common.utils.JedisUtils;
import com.power.platform.common.utils.MemCachedUtis;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.pay.cash.entity.UserCash;
import com.power.platform.pay.cash.service.UserCashService;
import com.power.platform.pay.utils.FuncUtils;
import com.power.platform.pay.utils.LLPayUtil;
import com.power.platform.pay.utils.Validator;
import com.power.platform.userinfo.entity.Principal;
import com.power.platform.userinfo.entity.UserAccountInfo;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.userinfo.service.UserAccountInfoService;
import com.power.platform.userinfo.service.UserInfoService;
import com.power.platform.weixin.utils.mapTest;

/**
 * 
 * 类: PayRestService <br>
 * 描述: 支付接口. <br>
 * 作者: cao <br>
 * 时间: 2016年5月4日 下午1:25:23
 */
@Component
@Path("/pay")
@Service("payRestService")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class PayRestService {

	private static final Logger LOG = LoggerFactory
			.getLogger(PayRestService.class);

	@Autowired
	private LLPayService llPayService;
	@Autowired
	private UserCashService userCashService;
	@Autowired
	private UserAccountInfoService userAccountInfoService;
	@Autowired
	private UserInfoService userInfoService;

	/**
	 * 提现接口
	 * @param amount 金额
	 * @param from
	 * @param token
	 * @param branchBank 开户支行
	 * @param cityCode 开户城市编码
	 * @return
	 */
	@POST
	@Path("/cash")
	@Transactional
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized Map<String, Object> cash(@FormParam("amount") String amount,	@FormParam("from") String from, @FormParam("token") String token,
			@FormParam("branchBank") String branchBank,@FormParam("cityCode") String cityCode,@FormParam("busiPwd") String busiPwd,@Context HttpServletRequest servletRequest) {
		
		String ip = LLPayUtil.getIpAddr(servletRequest);
		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(token) || StringUtils.isBlank(from)/*|| StringUtils.isBlank(branchBank)*/
				|| StringUtils.isBlank(amount)||/* StringUtils.isBlank(cityCode)||*/ StringUtils.isBlank(busiPwd)) {
			LOG.info("fn:cash,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}
		/**
		 * 验证提现金额
		 */
		if (!FuncUtils.isMoney(amount)) {
			LOG.info("fn:cash,提现金额有误！");
			result.put("state", "5");
			result.put("message", "提现金额有误，请您核实。");
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
			UserInfo userInfo = userInfoService.get(jedisUserId);
			if (userInfo.getBindBankCardState() == UserInfo.BIND_CARD_NO) {
				LOG.info("fn:cash,用户未绑卡，请先进行绑卡！");
				result.put("state", "7");
				result.put("message", "用户未绑卡，请先进行绑卡！");
				result.put("data", null);
				return result;
			}
			//校验交易密码
			String pwd = EncoderUtil.encrypt(busiPwd);
			if (!pwd.equals(userInfo.getBusinessPwd())) {
				LOG.info("fn:cash,交易密码错误，请核实！");
				result.put("state", "6");
				result.put("message", "交易密码错误，请核实！");
				result.put("data", null);
				return result;
			}
			UserAccountInfo userAccountInfo = userAccountInfoService.get(userInfo.getAccountId());
			userAccountInfo = userAccountInfoService.get(userAccountInfo.getId());
			double availableAmount = userAccountInfo.getAvailableAmount();
			double freezeAmount = userAccountInfo.getFreezeAmount();
			double amountD = Double.valueOf(amount);
			if (amountD > availableAmount) {
				LOG.info("fn:cash,提现金额不可以大于可用余额！");
				result.put("state", "8");
				result.put("message", "提现金额不可以大于可用余额！");
				result.put("data", null);
				return result;
			}
			int freeCash = userCashService.getFreeCashCount(userInfo.getId());
			double feeAomunt = 0d;
			if (freeCash <=0) {
				feeAomunt = 1;
			}
			int cashResult = llPayService.cash(userInfo,userAccountInfo,branchBank,cityCode,amount,feeAomunt,ip);
			if (cashResult < 1 ) {
				LOG.info("fn:cash,系统错误！");
				result.put("state", "1");
				result.put("message", "系统出现错误，请稍后重试");
				result.put("data", null);
				return result;
			}
			//用户账户冻结
			userAccountInfo.setAvailableAmount(NumberUtils.scaleDouble(availableAmount - amountD));
			userAccountInfo.setFreezeAmount(NumberUtils.scaleDouble(freezeAmount + amountD));
			userAccountInfoService.save(userAccountInfo);
			//返回结果
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("amount", amount);
			data.put("feeAmount", feeAomunt);
			result.put("state", "0");
			result.put("data", data);
			result.put("message", "提现申请成功");
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:cash,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

	}
	/**
	 * 
	 * 方法: authRechargeApp <br>
	 * 描述: 认证支付(app). <br>
	 * 作者: Mr.Cao <br>
	 * 时间: 2016年5月6日 下午21:14:04
	 * 
	 * @param token
	 * <br>
	 *            客户唯一标识
	 * @param amount
	 * <br>
	 *            充值金额
	 * @param from
	 * <br>
	 *            请求平台.
	 * @return
	 */
	@POST
	@Path("/authRechargeApp")
	@Transactional
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> authRechargeApp(@FormParam("amount") String amount,
			@FormParam("from") String from, @FormParam("token") String token,@FormParam("block")String block,@Context HttpServletRequest servletRequest) {
		
		String ip = LLPayUtil.getIpAddr(servletRequest);
		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(token) || StringUtils.isBlank(from)
				|| StringUtils.isBlank(amount) || StringUtils.isBlank(block)) {
			LOG.info("fn:authRechargeApp,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}
		
		/**
		 * 充值金额校验
		 */
		if (!FuncUtils.isMoney(amount)) {
			LOG.info("fn:authRechargeApp,充值金额有误！");
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
			if(!StringUtils.isBlank(jedisUserId)){
				UserInfo userInfo = userInfoService.get(jedisUserId);
				if (userInfo.getBindBankCardState() == UserInfo.BIND_CARD_NO) {
					LOG.info("fn:authRechargeApp,用户未绑卡，请先进行绑卡！");
					result.put("state", "5");
					result.put("message", "用户未绑卡，请先进行绑卡！");
					result.put("data", null);
					return result;
				}
				Map<String, Object> data = llPayService.authRechargeApp(ip, amount, token);
				result.put("state", "0");
				result.put("message", "认证支付订单生成成功。！");
				result.put("data", data);
				return result;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:authRechargeApp,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}
		return result;
	}
	
	/**
	 * 
	 * 方法: authRechargeWeb <br>
	 * 描述: 认证支付. <br>
	 * 作者: Mr.Cao <br>
	 * 时间: 2016年5月6日 下午21:14:04
	 * 
	 * @param token
	 * <br>
	 *            客户唯一标识
	 * @param amount
	 * <br>
	 *            充值金额
	 * @param from
	 * <br>
	 *            请求平台.
	 * @return
	 */
	@POST
	@Path("/authRechargeWeb")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> authRechargeWeb(@FormParam("amount") String amount,
			@FormParam("from") String from, @FormParam("token") String token,@Context HttpServletRequest servletRequest) {
		
		String ip = LLPayUtil.getIpAddr(servletRequest);
		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(token) || StringUtils.isBlank(from)
				|| StringUtils.isBlank(amount)) {
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
			if(!StringUtils.isBlank(jedisUserId)){
				UserInfo userInfo = userInfoService.get(jedisUserId);
				if (userInfo.getBindBankCardState() == UserInfo.BIND_CARD_NO) {
					LOG.info("fn:authRechargeWeb,用户未绑卡，请先进行绑卡！");
					result.put("state", "5");
					result.put("message", "用户未绑卡，请先进行绑卡！");
					result.put("data", null);
					return result;
				}
			}
			Map<String, Object> data = llPayService.authRechargeWeb(ip, amount, token);
			result.put("state", "0");
			result.put("message", "认证支付订单生成成功。！");
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
	 * 
	 * 方法: authRechargeWeb <br>
	 * 描述: 认证支付. <br>
	 * 作者: Mr.Cao <br>
	 * 时间: 2016年5月6日 下午21:14:04
	 * 
	 * @param token
	 * <br>
	 *            客户唯一标识
	 * @param amount
	 * <br>
	 *            充值金额
	 * @param from
	 * <br>
	 *            请求平台.
	 * @return
	 */
	@POST
	@Path("/authRechargeWap")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> authRechargeWap(@FormParam("amount") String amount,
			@FormParam("from") String from, @FormParam("token") String token,@Context HttpServletRequest servletRequest) {
		
		String ip = LLPayUtil.getIpAddr(servletRequest);
		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(token) || StringUtils.isBlank(from)
				|| StringUtils.isBlank(amount)) {
			LOG.info("fn:authRechargeWap,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}

		/**
		 * 充值金额校验
		 */
		if (!FuncUtils.isMoney(amount)) {
			LOG.info("fn:authRechargeWap,充值金额有误！");
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
			if(!StringUtils.isBlank(jedisUserId)){
				UserInfo userInfo = userInfoService.get(jedisUserId);
				if (userInfo.getBindBankCardState() == UserInfo.BIND_CARD_NO) {
					LOG.info("fn:authRechargeWap,用户未绑卡，请先进行绑卡！");
					result.put("state", "5");
					result.put("message", "用户未绑卡，请先进行绑卡！");
					result.put("data", null);
					return result;
				}
			}
			Map<String, Object> data = llPayService.authRechargeWap(ip, amount, token);
			result.put("state", "0");
			result.put("message", "认证支付订单生成成功。！");
			result.put("data", data);
			return result;

		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:authRechargeWap,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}
	}
	
	/**
	 * 
	 * 方法: bindCardWeb <br>
	 * 描述: 实名认证(pc、wap). <br>
	 * 作者: Mr.Cao <br>
	 * 时间: 2016年5月6日 下午21:51:04
	 * 
	 * @param token
	 * <br>
	 *            客户唯一标识
	 * @param bankCard
	 * <br>
	 *            银行卡号
	 * @param from
	 * <br>
	 *            请求平台.	 
	 * @param idCard
	 * <br>
	 *            身份证号
	 * @param realName
	 * <br>
	 *            真实姓名
	 * @param mobile
	 * <br>
	 *            银行预留手机号.
	 * @return
	 */
	@POST
	@Path("/bindCardWeb")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> bindCardWeb(@FormParam("bankCard") String bankCard,@FormParam("idCard") String idCard,@FormParam("realName") String realName,
			@FormParam("from") String from, @FormParam("token") String token,@FormParam("bankCode") String bankCode,@Context HttpServletRequest servletRequest) {
		
		String ip = LLPayUtil.getIpAddr(servletRequest);
		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(bankCard) || StringUtils.isBlank(idCard)
				|| StringUtils.isBlank(realName)|| StringUtils.isBlank(from)|| StringUtils.isBlank(token)|| StringUtils.isBlank(ip)) {
			LOG.info("fn:bindCardWeb,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}
		/**
		 * 银行卡验证
		 */
		
		if (!FuncUtils.isBankCard(bankCard)) {
			LOG.info("fn:bindCardWeb,绑卡银行卡号有误！");
			result.put("state", "5");
			result.put("message", "银行卡号有误，请核实后重新输入");
			result.put("data", null);
			return result;
		}
		
		/**
		 * 身份证号验证
		 */
		if (!Validator.isIdCard(idCard)) {
			LOG.info("fn:bindCardWeb,身份证号有误！");
			result.put("state", "6");
			result.put("message", "身份证号有误，请核实后重新输入");
			result.put("data", null);
			return result;
		}

		/**
		 * 获取用户信息，拼装请求数据
		 */
		try {
			result.put("state", "0");
			result.put("message", "绑卡订单生成成功！");
			Map<String, Object> data = llPayService.bindCardWeb(bankCard,idCard,realName,ip,bankCode,token);
			result.put("data", data);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:bindCardWeb,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

	}
	
	
	/**
	 * 
	 * 方法: bindCardWap <br>
	 * 描述: 实名认证(wap). <br>
	 * 作者: Mr.Cao <br>
	 * 时间: 2016年5月6日 下午21:51:04
	 * 
	 * @param token
	 * <br>
	 *            客户唯一标识
	 * @param bankCard
	 * <br>
	 *            银行卡号
	 * @param from
	 * <br>
	 *            请求平台.	 
	 * @param idCard
	 * <br>
	 *            身份证号
	 * @param realName
	 * <br>
	 *            真实姓名
	 * @param mobile
	 * <br>
	 *            银行预留手机号.
	 * @return
	 */
	@POST
	@Path("/bindCardWap")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> bindCardWap(@FormParam("bankCard") String bankCard,@FormParam("idCard") String idCard,@FormParam("realName") String realName,
			@FormParam("from") String from, @FormParam("token") String token,@FormParam("bankCode") String bankCode,@Context HttpServletRequest servletRequest) {
		
		String ip = LLPayUtil.getIpAddr(servletRequest);
		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(bankCard) || StringUtils.isBlank(idCard)
				|| StringUtils.isBlank(realName)|| StringUtils.isBlank(from)|| StringUtils.isBlank(token)|| StringUtils.isBlank(ip)) {
			LOG.info("fn:bindCardWap,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}
		/**
		 * 银行卡验证
		 */
		
		if (!FuncUtils.isBankCard(bankCard)) {
			LOG.info("fn:bindCardWap,绑卡银行卡号有误！");
			result.put("state", "5");
			result.put("message", "银行卡号有误，请核实后重新输入");
			result.put("data", null);
			return result;
		}
		
		/**
		 * 身份证号验证
		 */
		if (!Validator.isIdCard(idCard)) {
			LOG.info("fn:bindCardWap,身份证号有误！");
			result.put("state", "6");
			result.put("message", "身份证号有误，请核实后重新输入");
			result.put("data", null);
			return result;
		}

		/**
		 * 获取用户信息，拼装请求数据
		 */
		try {
			result.put("state", "0");
			result.put("message", "绑卡订单生成成功！");
			Map<String, Object> data = llPayService.bindCardWap(bankCard,idCard,realName,ip,bankCode,token);
			result.put("data", data);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:bindCardWap,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

	}
	
	/**
	 * 
	 * 方法: bindCardApp <br>
	 * 描述: 实名认证(app). <br>
	 * 作者: Mr.Cao <br>
	 * 时间: 2016年5月6日 下午21:51:04
	 * 
	 * @param token
	 * <br>
	 *            客户唯一标识
	 * @param bankCard
	 * <br>
	 *            银行卡号
	 * @param from
	 * <br>
	 *            请求平台.	 
	 * @param idCard
	 * <br>
	 *            身份证号
	 * @param realName
	 * <br>
	 *            真实姓名
	 * @param mobile
	 * <br>
	 *            银行预留手机号.
	 * @return
	 */
	@POST
	@Path("/bindCardApp")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> bindCardApp(@FormParam("bankCard") String bankCard,@FormParam("idCard") String idCard,@FormParam("realName") String realName,
			@FormParam("from") String from, @FormParam("token") String token,@FormParam("block") String block,@Context HttpServletRequest servletRequest) {
		
		String ip = LLPayUtil.getIpAddr(servletRequest);
		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(bankCard) || StringUtils.isBlank(idCard)
				|| StringUtils.isBlank(realName)|| StringUtils.isBlank(from)|| StringUtils.isBlank(token)|| StringUtils.isBlank(ip)|| StringUtils.isBlank(block)) {
			LOG.info("fn:bindCardApp,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "请更新到最新版本");
			result.put("data", null);
			return result;
		}
		
		/**
		 * 银行卡验证
		 */
		
		if (!FuncUtils.isBankCard(bankCard)) {
			LOG.info("fn:bindCardApp,绑卡银行卡号有误！");
			result.put("state", "5");
			result.put("message", "银行卡号有误，请核实后重新输入");
			result.put("data", null);
			return result;
		}
		
		/**
		 * 身份证号验证
		 */
		if (!Validator.isIdCard(idCard)) {
			LOG.info("fn:bindCardApp,身份证号有误！");
			result.put("state", "6");
			result.put("message", "身份证号有误，请核实后重新输入");
			result.put("data", null);
			return result;
		}

		try {
			result.put("state", "0");
			result.put("message", "绑卡订单生成成功！");
			Map<String, Object> data = llPayService.bindCardApp(bankCard,idCard,realName,ip,token);
			result.put("data", data);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:bindCardApp,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

	}
		
	/**
	 * 
	 * @param amount 金额
	 * @param from 请求来源
	 * @param token token
	 * @param ip 访问ip
	 * @param bankCode 银行编码
	 * @return
	 */
	@POST
	@Path("/gateWayPay")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> gateWayPay(@FormParam("amount") String amount,@FormParam("from") String from, 
			@FormParam("token") String token,@Context HttpServletRequest servletRequest,@FormParam("bankCode") String bankCode) {
		
		String ip = LLPayUtil.getIpAddr(servletRequest);

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(token) || StringUtils.isBlank(from)|| StringUtils.isBlank(bankCode)|| StringUtils.isBlank(ip)
				|| StringUtils.isBlank(amount)) {
			LOG.info("fn:gateWayPay,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}
		if (!FuncUtils.isMoney(amount)) {
			LOG.info("fn:gateWayPay,充值金额有误！");
			result.put("state", "6");
			result.put("message", "充值金额有误，请核实！");
			result.put("data", null);
			return result;
		}
		/**
		 * 获取用户信息，拼装请求数据
		 */
		try {
			// 从缓存获取用户信息
			String jedisUserId = JedisUtils.get(token);
			
			if(!StringUtils.isBlank(jedisUserId)){
				UserInfo userInfo = userInfoService.get(jedisUserId);
				if (userInfo.getBindBankCardState() == UserInfo.BIND_CARD_NO) {
					LOG.info("fn:gateWayPay,用户未绑卡，请先进行绑卡！");
					result.put("state", "5");
					result.put("message", "用户未绑卡，请先进行绑卡！");
					result.put("data", null);
					return result;
				}
			}
			
			Map<String, Object> dataMap = llPayService.gateWayPay(ip, amount, token, bankCode);
			result.put("state", "0");
			result.put("message", "网银充值订单生成成功！");
			result.put("data", dataMap);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:gateWayPay,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

	}
	
	@POST
	@Path("/queryCardBin")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> queryCardBin(@FormParam("bankCard") String bankCard,@FormParam("from") String from,
		@FormParam("token") String token) {

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(bankCard)|| StringUtils.isBlank(from)|| StringUtils.isBlank(token)) {
			LOG.info("queryCardBin,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}

		try {
			String resJSON = llPayService.queryCardBin(bankCard);
			JSONObject jsonObject = JSONObject.parseObject(resJSON);
			String ret_code = jsonObject.getString("ret_code");
			if ("0000"!=ret_code&&!"0000".equals(ret_code) ) {
				result.put("state", "5");
				result.put("message", "银行卡错误，请核实！");
				result.put("data", null);
				return result;
			}
			result.put("state", "0");
			result.put("message", "银行卡查询成功！");
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("bankCode", jsonObject.getString("bank_code"));
			data.put("cardType", jsonObject.getString("card_type"));
			result.put("data", data);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("queryCardBin,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

	}
}
