package com.power.platform.lanmao.rw.service;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.power.platform.cgb.entity.CgbUserAccount;
import com.power.platform.cgb.entity.CgbUserTransDetail;
import com.power.platform.cgb.service.CgbUserAccountService;
import com.power.platform.cgb.service.callback.CallbackRechargeService;
import com.power.platform.cgbpay.config.ServerURLConfig;
import com.power.platform.common.config.Global;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.JedisUtils;
import com.power.platform.credit.entity.userinfo.CreditUserAccount;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.service.userinfo.CreditUserAccountService;
import com.power.platform.credit.service.userinfo.CreditUserInfoService;
import com.power.platform.lanmao.common.AppUtil;
import com.power.platform.lanmao.config.RedirectUrlConfig;
import com.power.platform.lanmao.rw.pojo.NotifyVo;
import com.power.platform.lanmao.type.StatusEnum;
import com.power.platform.lanmao.type.WithdrawStatusEnum;
import com.power.platform.pay.cash.entity.UserCash;
import com.power.platform.pay.cash.service.UserCashService;
import com.power.platform.pay.recharge.entity.UserRecharge;
import com.power.platform.pay.utils.LLPayUtil;
import com.power.platform.trandetail.entity.UserTransDetail;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.userinfo.service.UserInfoService;

/**
 *  懒猫 2.0 充值接口回调状态处理服务
 * @author chenhj ant-loiter
 *
 */
@Component
@Path("/callbacklmwithdraw")
@Service("callbackLMWithdrawService")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class CallbackLMWithdrawService {
	private static final Logger logger = LoggerFactory.getLogger(CallbackLMWithdrawService.class);

	@Autowired
	private UserCashService userCashService;
	@Autowired
	private WithdrawService withdrawService;
	@Autowired
	private CgbUserAccountService cgbUserAccountService;
	@Autowired
	private CreditUserAccountService creditUserAccountService;
	
	@POST 
	@Path("/withdrawWebNotify")
	@Produces(MediaType.APPLICATION_JSON)
	public String lmrechargeNotify(@FormParam("platformNo") String platformNo,
			@FormParam("responseType") String responseType,
			@FormParam("sign") String sign,
			@FormParam("keySerial") String keySerial,
			@FormParam("respData") String respData,
			@FormParam("serviceName") String serviceName,
			@Context HttpServletRequest servletRequest,
			@Context HttpServletResponse response) throws IOException, ServletException  {
		logger.info("keySerial = " + keySerial);
		logger.info("respData = " + respData);
		logger.info("sign = " + sign);
		logger.info("responseType = " + responseType);
		logger.info("serviceName = " + serviceName);
		logger.info("platformNo = " + platformNo);
		logger.debug("这里需要处理通知信息。。。。。");
		if(AppUtil.checkLanMaoNotifySign(sign, respData)) {
			NotifyVo input = new NotifyVo();
			input.setServiceName(serviceName);
			input.setPlatformNo(platformNo);
			input.setResponseType(responseType);
			input.setKeySerial(keySerial);
			input.setRespData(respData);
			// 处理异步通道
//			String result = lMWithdrawNotifyService.pressLMRechargeNotify(input);
			// 处理提现冻结的事情
			JSONObject respJSON = JSON.parseObject(input.getRespData());
			String ip = LLPayUtil.getIpAddr(servletRequest);
			 doWithdrawLog(respJSON, ip, UserCash.CASH_DOING); // 必须冻结提现金额
			 // 重定向跳转
			response.sendRedirect(RedirectUrlConfig.RETURN_LANMAO_INVEST_URL);
			
			return "result";
		} else {
			return "FAIL";
		}
	}
	
	public void doWithdrawLog(JSONObject respJSON, String ip, Integer freezeStatus) {
		String status = AppUtil.CheckStringByDefault(respJSON.getString("status"), "");
		String withdrawStatus = AppUtil.CheckStringByDefault(respJSON.getString("withdrawStatus"), "");
		String requestNo = AppUtil.CheckStringByDefault(respJSON.getString("requestNo"), "");
		String platformUserNo = AppUtil.CheckStringByDefault(respJSON.getString("platformUserNo"), "");
		if(status.equals(StatusEnum.SUCCESS.getValue()) && 
				(withdrawStatus.equals(WithdrawStatusEnum.ACCEPT.getValue()) || 
						withdrawStatus.equals(WithdrawStatusEnum.REMITING.getValue()))) {
			String amount = AppUtil.CheckStringByDefault(respJSON.getString("amount"), "0.0");
			String commission = AppUtil.CheckStringByDefault(respJSON.getString("commission"), "");
			String payCompany = AppUtil.CheckStringByDefault(respJSON.getString("payCompany"), "");
			String rechargeWay = AppUtil.CheckStringByDefault(respJSON.getString("rechargeWay"), "");
			String payMobile = AppUtil.CheckStringByDefault(respJSON.getString("payMobile"), "");

//			UserInfo userInfo = userInfoService.getCgb(platformUserNo);
//			CreditUserInfo creditUserInfo  = creditUserInfoService.get(platformUserNo);
			try {
				// 记录充值信息 , 异步通知进行业务处理
				 UserCash userCash = userCashService.getInfoById(requestNo);
				 withdrawService.withdrawOrder(userCash, platformUserNo, Double.valueOf(amount), freezeStatus);
				 logger.debug("{}用户处理提现订单完成，请核查，流水号为：{}， 冻结金额为： {}", platformUserNo, requestNo, amount);
				 
			}catch(Exception e) {
				e.printStackTrace();
				logger.debug("{}用户处理提现订单异常，请核查，流水号为：{}。", platformUserNo, requestNo);
			}
		}else {
			logger.debug("{}用户处理提现异常，请核查，流水号为：{}。", platformUserNo, requestNo);
		}
	}
	
	@POST 
	@Path("/withdrawWebNotifyCredit")
	@Produces(MediaType.APPLICATION_JSON)
	public String lmrechargeNotifyCredit(@FormParam("platformNo") String platformNo,
			@FormParam("responseType") String responseType,
			@FormParam("sign") String sign,
			@FormParam("keySerial") String keySerial,
			@FormParam("respData") String respData,
			@FormParam("serviceName") String serviceName,
			@Context HttpServletRequest servletRequest,
			@Context HttpServletResponse response) throws IOException, ServletException  {
		logger.info("keySerial = " + keySerial);
		logger.info("respData = " + respData);
		logger.info("sign = " + sign);
		logger.info("responseType = " + responseType);
		logger.info("serviceName = " + serviceName);
		logger.info("platformNo = " + platformNo);
		logger.debug("这里需要处理通知信息。。。。。");
		if(AppUtil.checkLanMaoNotifySign(sign, respData)) {
			NotifyVo input = new NotifyVo();
			input.setServiceName(serviceName);
			input.setPlatformNo(platformNo);
			input.setResponseType(responseType);
			input.setKeySerial(keySerial);
//			input.setRespData(respData);
			// 处理中投营销账户信息
			JSONObject _respData = JSON.parseObject(respData);
			String platformUserNo = AppUtil.CheckStringByDefault(_respData.getString("platformUserNo"), "");
			if(platformUserNo.equals(Global.getConfigLanMao("sys_generate_002"))) {
				_respData.remove("platformUserNo");
				platformUserNo = Global.getConfigLanMao("sys_generate_002_userID");
				_respData.put("platformUserNo", platformUserNo);
				input.setRespData(_respData.toJSONString());
			} else {
				input.setRespData(respData);
			}
			
			// String result = lMWithdrawNotifyService.pressLMRechargeNotify(input);
			// 处理提现冻结的事情
			JSONObject respJSON = JSON.parseObject(input.getRespData());
			String ip = LLPayUtil.getIpAddr(servletRequest);
			 doWithdrawLog(respJSON, ip, UserCash.CASH_DOING);
			 // 重定向跳转
			response.sendRedirect(RedirectUrlConfig.RETURN_LANMAO_Credit_INVEST_URL+"?id="+platformUserNo+"&withdrawflag=callback");
			
			return "result";
		} else {
			return "FAIL";
		}
	}
}
