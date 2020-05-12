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
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.cache.Cache;
import com.power.platform.cgb.entity.CgbUserAccount;
import com.power.platform.cgb.entity.CgbUserBankCard;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.JedisUtils;
import com.power.platform.common.utils.MemCachedUtis;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.credit.entity.userinfo.CreditUserAccount;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.service.userinfo.CreditUserAccountService;
import com.power.platform.credit.service.userinfo.CreditUserInfoService;
import com.power.platform.pay.cash.service.CGBUserWithdrawService;
import com.power.platform.pay.cash.service.UserCashService;
import com.power.platform.pay.utils.FuncUtils;
import com.power.platform.pay.utils.LLPayUtil;
import com.power.platform.userinfo.dao.UserLogDao;
import com.power.platform.userinfo.entity.Principal;
import com.power.platform.userinfo.entity.UserAccountInfo;
import com.power.platform.userinfo.entity.UserBankCard;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.userinfo.entity.UserLog;
import com.power.platform.userinfo.service.UserAccountInfoService;
import com.power.platform.userinfo.service.UserBankCardService;
import com.power.platform.userinfo.service.UserInfoService;

/**
 * 用户提现
 * @author YHAGZALUN WO SJIAOSY
 *
 */
@Component
@Path("/newwithdraw")
@Service("cGBWithdrawService")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class CGBWithdrawService {
	
	private static final Logger LOG = LoggerFactory
			.getLogger(CGBWithdrawService.class);
	
	
	@Autowired
	private UserAccountInfoService userAccountInfoService;
	@Autowired
	private UserCashService userCashService;
	@Autowired
	private CGBUserWithdrawService cGBUserWithdrawService;
	@Autowired
	private CreditUserAccountService creditUserAccountService;
	@Autowired
	private UserBankCardService userBankCardService;
	@Autowired
	private CgbUserAccountService cgbUserAccountService;
	@Autowired
	private CgbUserBankCardService cgbUserBankCardService;
	@Autowired
	private CreditUserInfoService creditUserInfoService;
	@Autowired
	private UserInfoService userInfoService;
	@Autowired
	private UserLogDao userLogDao;
	
	

	
	/**
	 * 提现接口(PC端web)
	 * @param amount 金额
	 * @param from
	 * @param token
	 * @param branchBank 开户支行
	 * @param cityCode 开户城市编码
	 * @return
	 */
	@POST
	@Path("/withdrawWeb")
	@Transactional
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized Map<String, Object> withdrawWeb(@FormParam("amount") String amount,	@FormParam("from") String from, @FormParam("token") String token,
			@FormParam("branchBank") String branchBank,@FormParam("cityCode") String cityCode,@Context HttpServletRequest servletRequest) {
		
		String ip = LLPayUtil.getIpAddr(servletRequest);
		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(token) || StringUtils.isBlank(from)) {
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
			UserInfo userInfo = userInfoService.getCgb(jedisUserId);
			if (userInfo.getCgbBindBankCardState() == UserInfo.BIND_CARD_NO) {
				LOG.info("fn:cash,用户未绑卡，请先进行绑卡！");
				result.put("state", "7");
				result.put("message", "用户未绑卡，请先进行绑卡！");
				result.put("data", null);
				return result;
			}
			CgbUserAccount userAccountInfo = cgbUserAccountService.get(userInfo.getAccountId());
			double availableAmount = userAccountInfo.getAvailableAmount();
			double amountD = Double.valueOf(amount);
			if (amountD > availableAmount) {
				LOG.info("fn:cash,提现金额不可以大于可用余额！"+"可用余额为"+availableAmount);
				result.put("state", "8");
				result.put("message", "提现金额不可以大于可用余额！");
				result.put("data", null);
				return result;
			}
			//提现次数
			int freeCash = userCashService.getFreeCashCount(userInfo.getId());
			double feeAomunt = 0d;
			if (freeCash <=0) {
				feeAomunt = 1;
			}
			Map<?,?> data = cGBUserWithdrawService.withdrawWeb(userInfo.getId(),userInfo.getAccountId(),branchBank,cityCode,amount,feeAomunt,ip,"01");

			if (userInfo != null) {
				UserLog log = new UserLog();
	            log.setId(String.valueOf(IdGen.randomLong()));
	            log.setUserId(userInfo.getId());
	            if(userInfo.getRealName()!=null)
	            	log.setUserName(userInfo.getRealName()+userInfo.getName());
	            else
	            	log.setUserName(userInfo.getName()); 
	            log.setType("5");
	            log.setCreateDate(new Date());
	            if(userInfo.getRealName()!=null)
	                log.setRemark(userInfo.getRealName()+"提现"+amount+"元");
	            else
	            	log.setRemark(userInfo.getName()+"提现"+amount+"元"); 
	            userLogDao.insert(log);
				}
			
			//返回结果
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
	 * 提现接口(手机端H5)
	 * @param bizType 用户类型  01-投资  02-借款
	 * @param amount 金额
	 * @param from
	 * @param token
	 * @param branchBank 开户支行
	 * @param cityCode 开户城市编码
	 * @return
	 */
	@POST
	@Path("/withdrawH5")
	@Transactional
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized Map<String, Object> withdrawH5(@FormParam("bizType") String bizType,@FormParam("amount") String amount,	@FormParam("from") String from, @FormParam("token") String token,
			@FormParam("branchBank") String branchBank,@FormParam("cityCode") String cityCode, @Context HttpServletRequest servletRequest) {
		
		String ip = LLPayUtil.getIpAddr(servletRequest);
		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(token) || StringUtils.isBlank(from) || StringUtils.isBlank(amount) || StringUtils.isBlank(bizType)) {
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
			UserInfo userInfo = userInfoService.getCgb(jedisUserId);
			if(userInfo!=null){
				if (userInfo.getCgbBindBankCardState() == UserInfo.CGB_BIND_CARD_NO) {
					LOG.info("fn:cash,用户未绑卡，请先进行绑卡！");
					result.put("state", "7");
					result.put("message", "用户未绑卡，请先进行绑卡！");
					result.put("data", null);
					return result;
				}
			}else{
				result.put("state", "4");
				result.put("message", "系统超时");
				result.put("data", null);
				return result;
			}

			//投资用户账户余额判断
			double feeAomunt = 0d;
			double withdrawAmount = Double.valueOf(amount);
			if(bizType.equals("01")){
				CgbUserAccount  userAccountInfo =  cgbUserAccountService.get(userInfo.getAccountId());
				double availableAmount = userAccountInfo.getAvailableAmount();
				if (withdrawAmount > availableAmount) {
					LOG.info("fn:cash,提现金额不可以大于可用余额！");
					result.put("state", "8");
					result.put("message", "提现金额不可以大于可用余额！");
					result.put("data", null);
					return result;
				}
				//提现次数
				int freeCash = userCashService.getFreeCashCount(userInfo.getId());
				if (freeCash <=0) {
					feeAomunt = 1;
				}
			}

			Map<?,?> data = cGBUserWithdrawService.withdrawH5(token,branchBank,cityCode,amount,feeAomunt,ip,bizType,from);

			//返回结果
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
	 * 提现接口(借款PC端web)
	 * @param amount 金额
	 * @param from
	 * @param token
	 * @param branchBank 开户支行
	 * @param cityCode 开户城市编码
	 * @return
	 */
	@POST
	@Path("/creditWithdrawWeb")
	@Transactional
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized Map<String, Object> creditWithdrawWeb(@FormParam("amount") String amount,	@FormParam("from") String from, @FormParam("userId") String userId,
			@FormParam("branchBank") String branchBank,@FormParam("cityCode") String cityCode,@Context HttpServletRequest servletRequest) {
		
		String ip = LLPayUtil.getIpAddr(servletRequest);
		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(userId) || StringUtils.isBlank(from)) {
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

			CreditUserInfo creditUserInfo = creditUserInfoService.get(userId);
			if(creditUserInfo!=null){
				CgbUserBankCard bankCard = cgbUserBankCardService.findByUserId(creditUserInfo.getId());
				if(bankCard!=null){
					if(bankCard.getState() == UserBankCard.CERTIFY_NO){
						LOG.info("fn:cash,用户未绑卡，请先进行绑卡！");
						result.put("state", "7");
						result.put("message", "用户未绑卡，请先进行绑卡！");
						result.put("data", null);
						return result;
					}
				}else{
					LOG.info("fn:cash,用户未绑卡，请先进行绑卡！");
					result.put("state", "7");
					result.put("message", "用户未绑卡，请先进行绑卡！");
					result.put("data", null);
					return result;
				}
			}

			CreditUserAccount userAccountInfo = creditUserAccountService.get(creditUserInfo.getAccountId());
			double availableAmount = userAccountInfo.getAvailableAmount();
			double amountD = Double.valueOf(amount);
			if (amountD > availableAmount) {
				LOG.info("fn:cash,提现金额不可以大于可用余额！"+"可用余额为"+availableAmount);
				result.put("state", "8");
				result.put("message", "提现金额不可以大于可用余额！");
				result.put("data", null);
				return result;
			}
			//提现次数
			int freeCash = userCashService.getFreeCashCount(creditUserInfo.getId());
			double feeAomunt = 0d;
			if (freeCash <=0) {
				feeAomunt = 1;
			}
			Map<?,?> data = cGBUserWithdrawService.withdrawWeb(creditUserInfo.getId(),creditUserInfo.getAccountId(),branchBank,cityCode,amount,feeAomunt,ip,"02");

			//返回结果
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
	
}
