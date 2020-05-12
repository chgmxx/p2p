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
import org.joda.time.DateTime;
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
import com.power.platform.lanmao.trade.pojo.VerifyDeduct;
import com.power.platform.lanmao.type.ServiceNameEnum;

@Service("lanMaoFreezeService")
public class LanMaoFreezeService {

	private final static Logger logger = LoggerFactory.getLogger(LanMaoFreezeService.class);

	/**
	 * 直连请求地址
	 */
	private static final String SERVICE_URL = Global.getConfigLanMao("serviceUrl");

	/**
	 * 
	 * methods: unfreezeTradePassword <br>
	 * description: 交易密码解冻 <br>
	 * 用户在存管系统页面累计输入错误密码超过5次以后交易密码会被冻结，平台可以调用此接口进行直接解冻（存管系统默认在24小时以后自动解冻用户交易密码限制）。<br>
	 * R1.用户存在且已激活可用；<br>
	 * R2.针对交易密码输入错误次数过多，导致密码被冻结的用户调用此接口可以立即解冻。<br>
	 * 是否幂等：是，接口模式：直连，异步通知：否 <br>
	 * author: Roy <br>
	 * date: 2019年9月25日 上午10:58:40
	 * 
	 * @param verifyDeduct
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> unfreezeTradePassword(VerifyDeduct verifyDeduct) {

		Map<String, String> result = new LinkedHashMap<String, String>();
		CloseableHttpResponse response = null;
		String lmresult = "";
		try {
			// 定义reqData参数集合
			Map<String, Object> reqData = new HashMap<String, Object>();
			reqData.put("requestNo", verifyDeduct.getRequestNo()); // Y 请求流水号
			reqData.put("platformUserNo", verifyDeduct.getPlatformUserNo()); // Y 平台用户编号
			// 所有的reqData的JSON内都须包含time stamp时间参数，并传入当前时间如果此时间和存管系统时间相差过大（10 分钟），则拒绝执行操作。
			reqData.put("timestamp", DateUtils.formatDate(new Date(), "yyyyMMddHHmmss"));
			Map<String, String> requestParam = AppUtil.lmGeneratePostParam(ServiceNameEnum.UNFREEZE_TRADE_PASSWORD.getValue(), reqData);
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

		result.put("code", code);
		result.put("status", status);
		result.put("errorCode", handler.getErrorCode());
		result.put("errorMessage", handler.getErrorMessage());
		return result;
	}

	/**
	 * 
	 * methods: unFreeze <br>
	 * description: 资金解冻 <br>
	 * 平台调用此接口解冻平台曾经冻结的用户账户资金。<br>
	 * R1.通过本接口解冻资金需要原冻结请求流水号。<br>
	 * R2.可以多次解冻平台 曾经 冻结的金额，直到该冻结订单无可解冻金额 。<br>
	 * 是否幂等：是，接口模式：直连，异步通知：否 <br>
	 * author: Roy <br>
	 * date: 2019年9月25日 上午10:18:27
	 * 
	 * @param verifyDeduct
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> unFreeze(VerifyDeduct verifyDeduct) {

		Map<String, String> result = new LinkedHashMap<String, String>();
		CloseableHttpResponse response = null;
		String lmresult = "";
		try {
			// 定义reqData参数集合
			Map<String, Object> reqData = new HashMap<String, Object>();
			reqData.put("requestNo", verifyDeduct.getRequestNo()); // Y 请求流水号
			reqData.put("originalFreezeRequestNo", verifyDeduct.getOriginalFreezeRequestNo()); // Y 原冻结的请求流水号
			reqData.put("amount", verifyDeduct.getAmount()); // Y 解冻金额
			// 所有的reqData的JSON内都须包含time stamp时间参数，并传入当前时间如果此时间和存管系统时间相差过大（10 分钟），则拒绝执行操作。
			reqData.put("timestamp", DateUtils.formatDate(new Date(), "yyyyMMddHHmmss"));
			Map<String, String> requestParam = AppUtil.lmGeneratePostParam(ServiceNameEnum.UNFREEZE.getValue(), reqData);
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

		result.put("code", code);
		result.put("status", status);
		result.put("errorCode", handler.getErrorCode());
		result.put("errorMessage", handler.getErrorMessage());
		return result;
	}

	/**
	 * 
	 * methods: freeze <br>
	 * description: 资金冻结 <br>
	 * 是否幂等：是，接口模式：直连，异步通知：否 <br>
	 * author: Roy <br>
	 * date: 2019年9月25日 上午10:00:05
	 * 
	 * @param verifyDeduct
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> freeze(VerifyDeduct verifyDeduct) {

		Map<String, String> result = new LinkedHashMap<String, String>();
		CloseableHttpResponse response = null;
		String lmresult = "";
		try {
			// 定义reqData参数集合
			Map<String, Object> reqData = new HashMap<String, Object>();
			reqData.put("platformUserNo", verifyDeduct.getPlatformUserNo()); // Y 平台用户编号
			reqData.put("requestNo", verifyDeduct.getRequestNo()); // Y 请求流水号
			reqData.put("amount", verifyDeduct.getAmount()); // Y 冻结金额
			// 所有的reqData的JSON内都须包含time stamp时间参数，并传入当前时间如果此时间和存管系统时间相差过大（10 分钟），则拒绝执行操作。
			reqData.put("timestamp", DateUtils.formatDate(new Date(), "yyyyMMddHHmmss"));
			Map<String, String> requestParam = AppUtil.lmGeneratePostParam(ServiceNameEnum.FREEZE.getValue(), reqData);
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

		result.put("code", code);
		result.put("status", status);
		result.put("errorCode", handler.getErrorCode());
		result.put("errorMessage", handler.getErrorMessage());
		return result;
	}

	/**
	 * 
	 * methods: verifyDeduct <br>
	 * description: 验密扣费 <br>
	 * 是否幂等：是，接口模式：网关，异步通知：是 <br>
	 * author: Roy <br>
	 * date: 2019年9月25日 上午9:18:10
	 * 
	 * @param verifyDeduct
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> verifyDeduct(VerifyDeduct verifyDeduct) {

		Map<String, String> result = new LinkedHashMap<String, String>();

		try {
			// 定义reqData参数集合
			Map<String, Object> reqData = new HashMap<String, Object>();
			reqData.put("requestNo", verifyDeduct.getRequestNo()); // Y
			reqData.put("platformUserNo", verifyDeduct.getPlatformUserNo()); // Y
			reqData.put("amount", verifyDeduct.getAmount()); // Y
			reqData.put("customDefine", verifyDeduct.getCustomDefine()); // N
			reqData.put("targetPlatformUserNo", verifyDeduct.getTargetPlatformUserNo()); // Y
			reqData.put("redirectUrl", verifyDeduct.getRedirectUrl()); // Y
			// 计算页面过期时间，当前时间增加30分钟
			DateTime dateTime = new DateTime();
			reqData.put("expired", dateTime.plusMinutes(30).toString("yyyyMMddHHmmss")); // Y
			// 所有的reqData的JSON内都须包含time stamp时间参数，并传入当前时间如果此时间和存管系统时间相差过大（10 分钟），则拒绝执行操作。
			reqData.put("timestamp", DateUtils.formatDate(new Date(), "yyyyMMddHHmmss"));
			Map<String, String> requestParam = AppUtil.lmGeneratePostParam(ServiceNameEnum.VERIFY_DEDUCT.getValue(), reqData);
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
