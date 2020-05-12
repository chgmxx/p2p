package com.power.platform.lanmao.trade.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.power.platform.common.config.Global;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.lanmao.common.AppUtil;
import com.power.platform.lanmao.common.HttpUtils;
import com.power.platform.lanmao.common.VerifySignUtils;
import com.power.platform.lanmao.trade.pojo.ResponsePojo;
import com.power.platform.lanmao.trade.pojo.ResponsePojoUtil;
import com.power.platform.lanmao.trade.pojo.UserAuthorization;
import com.power.platform.lanmao.type.ServiceNameEnum;

@Service("lanMaoAuthorizationService")
public class LanMaoAuthorizationService {

	private final static Logger logger = LoggerFactory.getLogger(LanMaoAuthorizationService.class);
	/**
	 * 直连请求地址
	 */
	private static final String SERVICE_URL = Global.getConfigLanMao("serviceUrl");

	/**
	 * 
	 * methods: userAutoPreTransaction <br>
	 * description: 授权预处理，如用户已授权给平台做自动交易业务确认，则平台可以调用该接口进行预处理操作，无需用户交互（自动还款） <br>
	 * 是否幂等：是，是否直连：是，异步通知：否 <br>
	 * author: Roy <br>
	 * date: 2019年9月24日 下午9:09:04
	 * 
	 * @param userAuthorization
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> userAutoPreTransaction(UserAuthorization userAuthorization) {

		Map<String, String> result = new LinkedHashMap<String, String>();
		CloseableHttpResponse response = null;
		String lmresult = "";
		try {
			// 定义reqData参数集合
			Map<String, Object> reqData = new HashMap<String, Object>();
			reqData.put("requestNo", userAuthorization.getRequestNo());
			reqData.put("platformUserNo", userAuthorization.getPlatformUserNo());
			reqData.put("originalRechargeNo", userAuthorization.getOriginalRechargeNo());
			reqData.put("bizType", userAuthorization.getBizType());
			reqData.put("amount", userAuthorization.getAmount());
			reqData.put("preMarketingAmount", userAuthorization.getPreMarketingAmount());
			reqData.put("remark", userAuthorization.getRemark());
			reqData.put("projectNo", userAuthorization.getProjectNo());
			reqData.put("share", userAuthorization.getShare());
			reqData.put("creditsaleRequestNo", userAuthorization.getCreditsaleRequestNo());
			// 所有的reqData的JSON内都须包含time stamp时间参数，并传入当前时间如果此时间和存管系统时间相差过大（10 分钟），则拒绝执行操作。
			reqData.put("timestamp", DateUtils.formatDate(new Date(), "yyyyMMddHHmmss"));
			Map<String, String> requestParam = AppUtil.lmGeneratePostParam(ServiceNameEnum.USER_AUTO_PRE_TRANSACTION.getValue(), reqData);
			logger.debug("request:{}", JSON.toJSONString(requestParam));

			List<BasicNameValuePair> formParams = new ArrayList<BasicNameValuePair>();
			BasicNameValuePair n1 = new BasicNameValuePair("serviceName", requestParam.get("serviceName"));
			BasicNameValuePair n2 = new BasicNameValuePair("platformNo", requestParam.get("platformNo"));
			BasicNameValuePair n3 = new BasicNameValuePair("reqData", requestParam.get("reqData"));
			BasicNameValuePair n4 = new BasicNameValuePair("keySerial", requestParam.get("keySerial"));
			BasicNameValuePair n5 = new BasicNameValuePair("sign", requestParam.get("sign"));
			formParams.add(n1);
			formParams.add(n2);
			formParams.add(n3);
			formParams.add(n4);
			formParams.add(n5);
			response = HttpUtils.httpPostWithPAaram(SERVICE_URL, formParams);
			lmresult = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
			logger.debug("lmresult:{}", lmresult);

		} catch (Exception e) {
			logger.error("Exception:{}", e.getMessage());
			result.put("code", "1");
			result.put("status", "INIT");
			result.put("errorCode", "1");
			result.put("errorMessage", "local exception");
			return result;
		}

		ResponsePojo handler = ResponsePojoUtil.handler(lmresult);
		// 验签
		String code = handler.getCode();
		String status = handler.getStatus();
		try {
			if (!"0".equals(code) || !"SUCCESS".equals(status)) {
				logger.debug("接口返回code!=0 || status!=SUCCESS时，不做验签处理");
			} else {
				if (!VerifySignUtils.verifySign(response, lmresult)) {
					result.put("code", "1");
					result.put("status", "INIT");
					result.put("errorCode", "1");
					result.put("errorMessage", "sign fail");
					return result;
				}
			}
		} catch (Exception e) {
			logger.error("sign fail..... " + e.getMessage());
			result.put("code", "1");
			result.put("status", "INIT");
			result.put("errorCode", "1");
			result.put("errorMessage", "local exception");
			return result;
		}

		result.put("bizType", handler.getBizType());
		result.put("code", code);
		result.put("status", status);
		result.put("errorCode", handler.getErrorCode());
		result.put("errorMessage", handler.getErrorMessage());
		result.put("requestNo", handler.getRequestNo());
		return result;
	}

	/**
	 * 
	 * methods: cancelUserAuthorization <br>
	 * description: 取消用户授权，是否幂等：否，是否直连：是，异步通知：否 <br>
	 * author: Roy <br>
	 * date: 2019年9月24日 下午8:29:07
	 * 
	 * @param userAuthorization
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> cancelUserAuthorization(UserAuthorization userAuthorization) {

		Map<String, String> result = new LinkedHashMap<String, String>();
		CloseableHttpResponse response = null;
		String lmresult = "";
		try {
			// 定义reqData参数集合
			Map<String, Object> reqData = new HashMap<String, Object>();
			reqData.put("platformUserNo", userAuthorization.getPlatformUserNo());
			reqData.put("requestNo", userAuthorization.getRequestNo());
			reqData.put("authList", userAuthorization.getAuthList());
			// 所有的reqData的JSON内都须包含time stamp时间参数，并传入当前时间如果此时间和存管系统时间相差过大（10 分钟），则拒绝执行操作。
			reqData.put("timestamp", DateUtils.formatDate(new Date(), "yyyyMMddHHmmss"));
			Map<String, String> requestParam = AppUtil.lmGeneratePostParam(ServiceNameEnum.CANCEL_USER_AUTHORIZATION.getValue(), reqData);
			logger.debug("request:{}", JSON.toJSONString(requestParam));

			List<BasicNameValuePair> formParams = new ArrayList<BasicNameValuePair>();
			BasicNameValuePair n1 = new BasicNameValuePair("serviceName", requestParam.get("serviceName"));
			BasicNameValuePair n2 = new BasicNameValuePair("platformNo", requestParam.get("platformNo"));
			BasicNameValuePair n3 = new BasicNameValuePair("reqData", requestParam.get("reqData"));
			BasicNameValuePair n4 = new BasicNameValuePair("keySerial", requestParam.get("keySerial"));
			BasicNameValuePair n5 = new BasicNameValuePair("sign", requestParam.get("sign"));
			formParams.add(n1);
			formParams.add(n2);
			formParams.add(n3);
			formParams.add(n4);
			formParams.add(n5);
			response = HttpUtils.httpPostWithPAaram(SERVICE_URL, formParams);
			lmresult = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
			logger.debug("lmresult:{}", lmresult);

		} catch (Exception e) {
			logger.error("Exception:{}", e.getMessage());
			result.put("code", "1");
			result.put("status", "INIT");
			result.put("errorCode", "1");
			result.put("errorMessage", "local exception");
			return result;
		}

		ResponsePojo handler = ResponsePojoUtil.handler(lmresult);
		// 验签
		String code = handler.getCode();
		String status = handler.getStatus();
		try {
			if (!"0".equals(code) || !"SUCCESS".equals(status)) {
				logger.debug("接口返回code!=0 || status!=SUCCESS时，不做验签处理");
			} else {
				if (!VerifySignUtils.verifySign(response, lmresult)) {
					result.put("code", "1");
					result.put("status", "INIT");
					result.put("errorCode", "1");
					result.put("errorMessage", "sign fail");
					return result;
				}
			}
		} catch (Exception e) {
			logger.error("sign fail..... " + e.getMessage());
			result.put("code", "1");
			result.put("status", "INIT");
			result.put("errorCode", "1");
			result.put("errorMessage", "local exception");
			return result;
		}

		result.put("platformUserNo", handler.getPlatformUserNo());
		result.put("requestNo", handler.getRequestNo());
		result.put("code", code);
		result.put("status", status);
		result.put("errorCode", handler.getErrorCode());
		result.put("errorMessage", handler.getErrorMessage());
		return result;
	}

	/**
	 * 
	 * methods: userAuthorization <br>
	 * description: 用户授权 <br>
	 * author: Roy <br>
	 * date: 2019年9月24日 上午11:16:42
	 * 
	 * @param userAuthorization
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> userAuthorization(UserAuthorization userAuthorization) {

		Map<String, String> result = new LinkedHashMap<String, String>();
		try {
			// 定义reqData参数集合
			Map<String, Object> reqData = new HashMap<String, Object>();
			reqData.put("platformUserNo", userAuthorization.getPlatformUserNo());
			reqData.put("requestNo", userAuthorization.getRequestNo());
			reqData.put("authList", userAuthorization.getAuthList());
			reqData.put("amount", userAuthorization.getAmount());
			reqData.put("failTime", userAuthorization.getFailTime());
			reqData.put("redirectUrl", userAuthorization.getRedirectUrl());
			// 所有的reqData的JSON内都须包含time stamp时间参数，并传入当前时间如果此时间和存管系统时间相差过大（10 分钟），则拒绝执行操作。
			reqData.put("timestamp", DateUtils.formatDate(new Date(), "yyyyMMddHHmmss"));
			Map<String, String> requestParam = AppUtil.lmGeneratePostParam(ServiceNameEnum.USER_AUTHORIZATION.getValue(), reqData);
			logger.debug("request:{}", JSON.toJSONString(requestParam));

			return requestParam;
		} catch (Exception e) {
			logger.error("Exception:{}", e.getMessage());
			result.put("code", "1");
			result.put("status", "INIT");
			result.put("errorCode", "1");
			result.put("errorMessage", "local exception");
			return result;
		}
	}

}
