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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.power.platform.cgb.service.callback.CallbackRechargeService;
import com.power.platform.cgbpay.config.ServerURLConfig;
import com.power.platform.common.config.Global;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.JedisUtils;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.service.userinfo.CreditUserInfoService;
import com.power.platform.lanmao.common.AppUtil;
import com.power.platform.lanmao.config.RedirectUrlConfig;
import com.power.platform.lanmao.rw.pojo.NotifyVo;
import com.power.platform.lanmao.type.StatusEnum;
import com.power.platform.pay.recharge.entity.UserRecharge;
import com.power.platform.pay.recharge.service.NewRechargeService;
import com.power.platform.pay.recharge.service.UserRechargeService;
import com.power.platform.pay.utils.LLPayUtil;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.userinfo.service.UserInfoService;

/**
 *  懒猫 2.0 充值接口回调状态处理服务
 * @author chenhj ant-loiter
 *
 */
@Component
@Path("/callbacklmrecharge")
@Service("callbackLMRechargeService")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class CallbackLMRechargeService {
	private static final Logger logger = LoggerFactory.getLogger(CallbackLMRechargeService.class);

	@Autowired
	private LMRechargeNotifyService lMRechargeNotifyService;
	@Autowired 
	private NewRechargeService newRechargeService;
	@Autowired
	private UserInfoService userInfoService;
	@Autowired
	private CreditUserInfoService creditUserInfoService;
	@Autowired 
	private UserRechargeService userRechargeService;
	
	@POST 
	@Path("/rechargeWebNotify")
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
//			String ip = LLPayUtil.getIpAddr(servletRequest);
			// 这是处理异常通知业务
//			String result = lMRechargeNotifyService.pressLMRechargeNotify(input);
//			addTradeLog(respJSON, ip);
			// 重定向跳转
			logger.info("出借人充值callback信息：{}", JSON.toJSONString(input));
			response.sendRedirect(RedirectUrlConfig.RETURN_LANMAO_INVEST_URL);
			
			return "result";
		} else {
			return "FAIL";
		}
	}
	
	@POST 
	@Path("/rechargeWebNotifyH5")
	@Produces(MediaType.APPLICATION_JSON)
	public String lmrechargeNotifyH5(@FormParam("platformNo") String platformNo,
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
		if(AppUtil.checkLanMaoNotifySign(sign, respData)) {
			NotifyVo input = new NotifyVo();
			input.setServiceName(serviceName);
			input.setPlatformNo(platformNo);
			input.setResponseType(responseType);
			input.setKeySerial(keySerial);
			
			JSONObject _respData = JSON.parseObject(respData);
			String platformUserNo = AppUtil.CheckStringByDefault(_respData.getString("platformUserNo"), "");
			logger.info("借款人充值callback信息：{}", JSON.toJSONString(input));
			response.sendRedirect(RedirectUrlConfig.RETURN_LANMAO_Credit_INVEST_URL+"?id="+platformUserNo);
			
			return "result";
		} else {
			return "FAIL";
		}
	}
	
	public void addTradeLog(JSONObject respJSON, String ip) {
		String status = AppUtil.CheckStringByDefault(respJSON.getString("status"), "");
		String rechargeStatus = AppUtil.CheckStringByDefault(respJSON.getString("rechargeStatus"), "");
		String requestNo = AppUtil.CheckStringByDefault(respJSON.getString("requestNo"), "");
		String platformUserNo = AppUtil.CheckStringByDefault(respJSON.getString("platformUserNo"), "");
		if(status.equals(StatusEnum.SUCCESS.getValue()) && rechargeStatus.equals(StatusEnum.SUCCESS.getValue())) {
			String amount = AppUtil.CheckStringByDefault(respJSON.getString("amount"), "0.0");
			String commission = AppUtil.CheckStringByDefault(respJSON.getString("commission"), "");
			String payCompany = AppUtil.CheckStringByDefault(respJSON.getString("payCompany"), "");
			String rechargeWay = AppUtil.CheckStringByDefault(respJSON.getString("rechargeWay"), "");
			String payMobile = AppUtil.CheckStringByDefault(respJSON.getString("payMobile"), "");
			
			UserInfo userInfo = userInfoService.getCgb(platformUserNo);
			CreditUserInfo creditUserInfo  = creditUserInfoService.get(platformUserNo);
			try {
				// 记录充值信息 , 异步通知进行业务处理
				UserRecharge userRecharge = userRechargeService.getById(requestNo);
				if(userRecharge != null && userRecharge.getState() == UserRecharge.RECHARGE_INIT) {
//					newRechargeService.insertRecharge(Double.valueOf(amount), ip, requestNo, platformUserNo, userInfo!=null?userInfo.getAccountId():creditUserInfo.getAccountId());
					userRecharge.setState(UserRecharge.RECHARGE_DOING);
					userRecharge.setUpdateDate(new Date());
					userRechargeService.updateState(userRecharge);
				}
			}catch(Exception e) {
				e.printStackTrace();
				logger.debug("{}用户添加充值订单异常，请核查，流水号为：{}。", platformUserNo, requestNo);
			}
		}else {
			logger.debug("{}用户充值异常，请核查，流水号为：{}。", platformUserNo, requestNo);
		}
	}
}
