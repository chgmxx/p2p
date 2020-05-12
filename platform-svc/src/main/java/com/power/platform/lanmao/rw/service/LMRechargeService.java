package com.power.platform.lanmao.rw.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

import com.alibaba.fastjson.JSON;
import com.power.platform.cgb.entity.CgbUserBankCard;
import com.power.platform.cgb.service.CgbUserBankCardService;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.JedisUtils;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.service.userinfo.CreditUserInfoService;
import com.power.platform.lanmao.common.AppUtil;
import com.power.platform.lanmao.config.RedirectUrlConfig;
import com.power.platform.lanmao.rw.pojo.SwiftRechargeBankList;
import com.power.platform.lanmao.rw.pojo.SwiftVO;
import com.power.platform.lanmao.type.PayTypeEnum;
import com.power.platform.lanmao.type.RechargeWayEnum;
import com.power.platform.pay.utils.FuncUtils;
import com.power.platform.pay.utils.LLPayUtil;
import com.power.platform.userinfo.dao.UserLogDao;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.userinfo.entity.UserLog;
import com.power.platform.userinfo.service.UserInfoService;

import common.toolkit.java.util.StringUtil;

/**
 * lanmao 用户充值
 * 
 * @author chenhj ant-loiter.com
 *
 */
@Component
@Path("/lmpay")
@Service("lMRechargeService")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class LMRechargeService {
	private static final Logger LOG = LoggerFactory.getLogger(LMRechargeService.class);

	@Autowired
	private UserInfoService userInfoService;
	@Autowired
	private RechargeService rechargeService;  // 懒猫api业务
	@Autowired
	private UserLogDao userLogDao;
	@Autowired
	private CreditUserInfoService creditUserInfoService;
	@Autowired
	private CgbUserBankCardService cgbUserBankCardService;
	
	
	/**
	 * 懒猫2.0 网银充值
	 * @param amount 充值金额
	 * @param from  平台来源 1:pc; 2:wap; 3: android; 4: ios; 5: loan; 
	 * @param token 用户令牌
	 * @param isBankCode 是否传银行编码  0: 不传， 1： 传
	 * @param paytype 网银类型（B2C, B2B), 这个是BankCode填写是， 必填
	 * @param bankcode  银行编码（ 填写：直接跳至银行页面； 不填写： 直接跳至支付公司的收银台页面）， 接口通过用户编号查询到用户关联的银行编码
	 * @param servletRequest
	 * @return
	 */
	@POST
	@Path("/lanmaoWebRecharge")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> lanmaoWebCGBRechange(@FormParam("amount") String amount,
												@FormParam("from") String from, 
												@FormParam("token") String token, 
												@FormParam("isbankcode") String isbankcode,
												@Context HttpServletRequest servletRequest
												) {
		String ip = LLPayUtil.getIpAddr(servletRequest);
		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();
		LOG.info("传入的参数为：amount={}, from={}, token={}", amount, from, token);
		// 参数判断
		if(StringUtil.isBlank(from) || StringUtil.isBlank(token) || StringUtil.isBlank(amount)) {
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
			LOG.info("fn:authRechargeWeb,充值金额有误, >0并且保留二位小数！");
			result.put("state", "6");
			result.put("message", "充值金额有误, >0并且保留二位小数！");
			result.put("data", null);
			return result;
		}
		/**
		 * 获取用户信息，拼装请求数据
		 */
		try {
			// 从缓存获取用户信息
			String jedisUserId = Objects.equals("5", from.trim())?token:JedisUtils.get(token);
			if (!StringUtils.isBlank(jedisUserId)) {
				Map<String, Object> map = findUserInfoByCgbCredit(jedisUserId, true);
				LOG.debug("map -> {}", JSON.toJSONString(map));
				if(map == null || (StringUtil.isBlank((String)map.get("uid")) && StringUtil.isBlank((String)map.get("aid")))) {
					LOG.info("fn:authRechargeWeb, 用户信息不存在或未绑卡！");
					result.put("state", "5");
					result.put("message", " 用户信息不存在或未绑卡！");
					result.put("data", null);
					return result;
				}
				String BankNo = (String)map.get("bankno");
				if(StringUtil.isBlank(BankNo)) {
					LOG.info("fn:authRechargeWeb, 用户信息未绑卡！");
					result.put("state", "5");
					result.put("message", " 用户信息未绑卡！");
					result.put("data", null);
					return result;
				}
				String redirectUrl = Objects.equals("user", (String)map.get("uflag"))?RedirectUrlConfig.BACK_Recharg_BACKTO_URL_WEB:RedirectUrlConfig.BACK_Recharg_Credit_BACKTO_URL_WEB;
				Map<?, ?> data2_0 = rechargeService.doRechargeWEB(ip,  
															(String)map.get("uid"), 
															IdGen.uuid(), 
															Double.valueOf(amount), 
															isbankcode,
															BankNo,
															redirectUrl, 
															(String)map.get("aid"));
				int log_result = whl((String)map.get("uid"), (String)map.get("name"), Double.valueOf(amount), "网银" );
				if(log_result != 0) {
					result.put("state", "0");
					result.put("message", "懒猫网银充值订单生成成功。！");
					result.put("data", data2_0);
					return result;
				} else {
					result.put("state", "1");
					result.put("message", "懒猫网银充值异常, 插入操作日志失败！");
					result.put("data", null);
					return result;
				}
			}else {
				LOG.info("fn:authRechargeWeb,懒猫网银充值异常, 用户信息不存在！");
				result.put("state", "1");
				result.put("message", "懒猫网银充值异常, 用户信息不存在！");
				result.put("data", null);
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:authRechargeWeb,懒猫网银充值异常！");
			result.put("state", "1");
			result.put("message", "懒猫网银充值异常！");
			result.put("data", null);
			return result;
		}
	}
	


	/**
	 * 出借人， 充值日志
	 * @param userInfo
	 */
	private int whl(String id,String name, Double amount, String payType) {
		int result = 0;
		UserLog log = new UserLog();
		log.setId(String.valueOf(IdGen.randomLong()));
		log.setUserId(id);
		log.setUserName(name);
		log.setType("4");
		log.setCreateDate(new Date());
		StringBuilder sb = new StringBuilder();
		sb.append("用户：");
		sb.append(name);
		sb.append("充值（懒猫 ");
		sb.append(payType);
		sb.append("充值）订单生成成功，充值:  ");
		sb.append(amount);
		sb.append("元");
		log.setRemark(sb.toString());
		result = userLogDao.insert(log);
		return result;
	}

	

	public Map<String, Object> findUserInfoByCgbCredit(String id , boolean isCheckBankCard) {
		Map<String, Object> map = new HashMap<>();
		UserInfo userInfo = userInfoService.getCgb(id);
		CreditUserInfo creditUserInfo = creditUserInfoService.get(id);
		String userId = userInfo==null?(creditUserInfo == null?"":creditUserInfo.getId()):userInfo.getId();
		String accoundId = userInfo == null?(creditUserInfo == null?"":creditUserInfo.getAccountId()):userInfo.getAccountId();
		String real_name = userInfo == null?(creditUserInfo == null?"":creditUserInfo.getEnterpriseFullName()):userInfo.getRealName();
		String userFlag = userInfo == null?(creditUserInfo == null?"":"credit"):"user";
		Integer isBindCard = UserInfo.BIND_EMAIL_NO;
		String bankCode = "";
		if(isCheckBankCard) {
			isBindCard = userInfo == null?9:userInfo.getCgbBindBankCardState();// 为9表示用户没有绑卡， 再去查借款人信息
			if(isBindCard == 9) {
				LOG.debug(">>>> userId, >> {}, ", userId);
				CgbUserBankCard cgbUserBankCard = cgbUserBankCardService.findByUserId(userId);
				Integer state  = Integer.parseInt(cgbUserBankCard.getState());
				if(state == 1) {
					isBindCard = UserInfo.BIND_CARD_YES;
					bankCode = cgbUserBankCard.getBankNo();
				}else {
					isBindCard = UserInfo.BIND_CARD_NO;
				}
			}else {
				// 查出出借人的绑卡银行编号
				CgbUserBankCard cgbUserBankCard = cgbUserBankCardService.findByUserId(userId);
				bankCode = cgbUserBankCard.getBankNo();
			}
		}
		map.put("uid", userId);
		map.put("aid", accoundId);
		map.put("carded", isBindCard);
		map.put("name", real_name);
		map.put("uflag", userFlag);
		map.put("bankno", bankCode);
		return map;
	}
	
	/**
	 * 懒猫2.0 快捷充值
	 * @param amount  充值金额
	 * @param from   平台来源   1:pc; 2:wap; 3: android; 4: ios; 5: loan; 
	 * @param token  用户令牌
	 * @param bankcode  银行编码， 接口通过用户编号查询到用户关联的银行编码
	 * @param servletRequest
	 * @return
	 */
	@POST
	@Path("/lanmaoSwiftRecharge")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> lanmaoSwiftCGBRechange(@FormParam("amount") String amount,
													@FormParam("from") String from, 
													@FormParam("token") String token,
													@Context HttpServletRequest servletRequest
													) {
		String ip = LLPayUtil.getIpAddr(servletRequest);
		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();
		LOG.info("传入的参数为：amount={}, from={}, token={}", amount, from, token);
		// 参数判断
		if(StringUtil.isBlank(from) || StringUtil.isBlank(token) || StringUtil.isBlank(amount)) {
			LOG.info("fn:authRechargeWeb,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}
		/**
		 * 充值金额校验
		 */
		if (!FuncUtils.isMoney(amount) || Double.valueOf(amount) < 5.00) {
			LOG.info("fn:authRechargeWeb,充值金额有误, >=5 元并且保留二位小数！");
			result.put("state", "6");
			result.put("message", "充值金额有误, >=5 元并且保留二位小数！");
			result.put("data", null);
			return result;
		}
		/**
		 * 获取用户信息，拼装请求数据
		 */
		try {
			// 从缓存获取用户信息  from
			String jedisUserId = Objects.equals("5", from.trim())?token:JedisUtils.get(token);
			if (!StringUtils.isBlank(jedisUserId)) {
				Map<String, Object> map = findUserInfoByCgbCredit(jedisUserId, true);
				if(map == null || (StringUtil.isBlank((String)map.get("uid")) && StringUtil.isBlank((String)map.get("aid")))) {
					LOG.info("fn:authRechargeWeb, 用户信息不存在！");
					result.put("state", "5");
					result.put("message", " 用户信息不存在！");
					result.put("data", null);
					return result;
				}
				String BankNo = (String)map.get("bankno");
				if(StringUtil.isBlank(BankNo)) {
					LOG.info("fn:authRechargeWeb, 用户信息未绑卡！");
					result.put("state", "5");
					result.put("message", " 用户信息未绑卡！");
					result.put("data", null);
					return result;
				}else {
					// 查询当前支持的银行信息
					SwiftVO sv = SwiftRechargeBankList.getByNo(BankNo);
					if(sv == null) {
						LOG.info("fn:authRechargeWeb, 快捷充值暂不支持该银行！");
						result.put("state", "5");
						result.put("message", " 快捷充值暂不支持该银行！");
						result.put("data", null);
						return result;
					}
					Integer menoyStroke = sv.getMaxByEachStroke();
					if(Double.valueOf(amount) > menoyStroke) {
						LOG.info("fn:authRechargeWeb, 单笔充值金额超出上限！单笔上限：" + menoyStroke +"元");
						result.put("state", "5");
						result.put("message", "单笔充值金额超出上限！单笔上限：" + menoyStroke +"元");
						result.put("data", null);
						return result;
					}
					// 判断用户每日充值金额， 从redis里查询 , key值： swiftrecharge_userid_yyyyMMDD
					StringBuilder sb = new StringBuilder("swiftrecharge_");
					sb.append((String)map.get("uid"));
					sb.append("_");
					sb.append(DateUtils.getDateByymd());
					String maxByDay = AppUtil.CheckStringByDefault(JedisUtils.get(sb.toString()), "0.0");
					Double preDouble = Double.valueOf(maxByDay);
					Integer menoryByDay = sv.getMaxByEachDay();
					// 如果加这次充值金额 > 当天最大金额
					if((preDouble + Double.valueOf(amount)) > menoryByDay ) {
						LOG.info("fn:authRechargeWeb, 单日充值金额超出上限！单日上限为：" + menoryByDay +"元");
						result.put("state", "5");
						result.put("message", "单日充值金额超出上限！单日上限为：" + menoryByDay +"元");
						result.put("data", null);
						return result;
					}
				}
				String redirectUrl = Objects.equals("user", (String)map.get("uflag"))?RedirectUrlConfig.BACK_Recharg_BACKTO_URL_WEB:RedirectUrlConfig.BACK_Recharg_Credit_BACKTO_URL_WEB;
				Map<?, ?> data2_0 = rechargeService.doRechargeSwift(ip,  
															(String)map.get("uid"),
															IdGen.uuid(), 
															BankNo,
															Double.valueOf(amount), 
															redirectUrl, 
															(String)map.get("aid"));
				
				
				int log_result = whl((String)map.get("uid"), (String)map.get("name"), Double.valueOf(amount), "快捷");
				if(log_result != 0) {
					result.put("state", "0");
					result.put("message", "懒猫快捷充值订单生成成功。！");
					result.put("data", data2_0);
					return result;
				} else {
					result.put("state", "1");
					result.put("message", "懒猫快捷充值异常, 插入操作日志失败！");
					result.put("data", null);
					return result;
				}
			}else {
				LOG.info("fn:authRechargeWeb,懒猫快捷充值异常, 用户信息不存在！");
				result.put("state", "1");
				result.put("message", "懒猫快捷充值异常, 用户信息不存在！");
				result.put("data", null);
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:authRechargeWeb,懒猫快捷充值异常！");
			result.put("state", "1");
			result.put("message", "懒猫快捷充值异常！");
			result.put("data", null);
			return result;
		}
	}
	
	
	
	/**
	 * 懒猫2.0 充值----pc端web  出借人
	 * 
	 * @param amount
	 * @param from
	 * @param token
	 * @param servletRequest
	 * @return
	 */
	@POST
	@Path("/lanmaoAuthRechargeWeb")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> lanmaoAuthRechargeWeb(@FormParam("amount") String amount,
													@FormParam("from") String from, 
													@FormParam("token") String token, 
													@FormParam("paytype") String paytype,
													@FormParam("bankcode") String bankcode,
													@FormParam("device") String device,
													@Context HttpServletRequest servletRequest) {

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
				//Map<?, ?> data = newRechargeService.authRechargeWeb(ip, amount, userInfo.getId(), userInfo.getAccountId(), "01");
				///// rechargeService
				Map<?, ?> data2_0 = null; 
				// "PC".equals(device.toUpperCase())?UserDeviceEnum.PC.getValue():UserDeviceEnum.MOBILE.getValue(),
				if("fast".equals(from)) { // 快捷支付
					data2_0 = rechargeService.doRechargeR2(ip, userInfo.getId(), IdGen.uuid(), Double.valueOf(amount), 0.0, RechargeWayEnum.SWIFT.getValue(), "B2C".equals(paytype)?PayTypeEnum.B2C.getValue():PayTypeEnum.B2B.getValue(), bankcode,  RedirectUrlConfig.BACK_Recharg_BACKTO_URL_WEB, userInfo.getAccountId());
				}else {
					data2_0 = rechargeService.doRechargeR2(ip, userInfo.getId(), IdGen.uuid(), Double.valueOf(amount), 0.0, RechargeWayEnum.WEB.getValue(), RedirectUrlConfig.BACK_Recharg_BACKTO_URL_WEB,  userInfo.getAccountId());		
				}
				
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
				result.put("data", data2_0);
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
	 * 充值----借款PC端web  借款人 
	 * 
	 * @param amount
	 * @param from
	 * @param token
	 * @param servletRequest
	 * @return
	 */
	@POST
	@Path("/lanmaoCreditAuthRecharge") 
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> creditAuthRechargeWeb(@FormParam("amount") String amount, 
													 @FormParam("from") String from, 
													 @FormParam("userId") String userId, 
													 @Context HttpServletRequest servletRequest) {
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

			//Map<?, ?> data = newRechargeService.authRechargeWeb(ip, amount, userInfo.getId(), userInfo.getAccountId(), "02");
			Map<?,?> data = rechargeService.doRechargeR3(ip, 
														userInfo.getId(), 
														IdGen.uuid(), 
														Double.valueOf(amount), 
														0.0, 
														RechargeWayEnum.WEB.getValue(), 
														RedirectUrlConfig.BACK_Recharg_Credit_BACKTO_URL_WEB,  
														userInfo.getAccountId(), 
														from);
			if(data != null && "err".equals(data.get("state"))) {
				result.put("state", "9");
				result.put("message", "充值失败， 充值流水记录失败！");
				result.put("data", data); 
				return result;
			}
			 
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
}
