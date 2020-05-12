package com.power.platform.lanmao.trade.service;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.config.Global;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.lanmao.common.AppUtil;
import com.power.platform.lanmao.trade.pojo.CancelPreTransaction;
import com.power.platform.lanmao.trade.pojo.ResponsePojo;
import com.power.platform.lanmao.trade.pojo.ResponsePojoUtil;
import com.power.platform.lanmao.trade.pojo.UserPreTransaction;
import com.power.platform.lanmao.type.ServiceNameEnum;
import com.power.platform.sys.utils.HttpUtil;

@Service("lanMaoPreTransactionService")
public class LanMaoPreTransactionService {

	private final static Logger logger = LoggerFactory.getLogger(LanMaoPreTransactionService.class);

	/**
	 * 直连请求地址
	 */
	private static final String SERVICE_URL = Global.getConfigLanMao("serviceUrl");

	/**
	 * 
	 * methods: cancelPreTransaction <br>
	 * description: 预处理取消 <br>
	 * author: Roy <br>
	 * date: 2019年9月22日 上午11:18:05
	 * 
	 * @param cancelPreTransaction
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> cancelPreTransaction(CancelPreTransaction cancelPreTransaction) {

		Map<String, String> result = new LinkedHashMap<String, String>();

		try {
			// 定义reqData参数集合
			Map<String, String> reqData = new HashMap<String, String>();
			reqData.put("requestNo", cancelPreTransaction.getRequestNo());
			reqData.put("preTransactionNo", cancelPreTransaction.getPreTransactionNo());
			reqData.put("amount", cancelPreTransaction.getAmount());
			// 所有的reqData的JSON内都须包含time stamp时间参数，并传入当前时间如果此时间和存管系统时间相差过大（10 分钟），则拒绝执行操作。
			reqData.put("timestamp", DateUtils.formatDate(new Date(), "yyyyMMddHHmmss"));
			Map<String, String> requestParam = AppUtil.generatePostParam(ServiceNameEnum.CANCEL_PRE_TRANSACTION.getValue(), reqData);

			Map<String, String> header = new HashMap<>();
			String responseStr = HttpUtil.sendPost(SERVICE_URL, requestParam, header, "utf-8");
			// 结果集返回
			ResponsePojo handler = ResponsePojoUtil.handler(responseStr);
			result.put("code", handler.getCode());
			result.put("status", handler.getStatus());
			result.put("errorCode", handler.getErrorCode());
			result.put("errorMessage", handler.getErrorMessage());
			return result;
		} catch (Exception e) {
			logger.error("Exception:{}", e.getMessage());
			result.put("code", "1");
			result.put("status", "INIT");
			result.put("errorCode", "1");
			result.put("errorMessage", "local exception");
			return result;
		}
	}

	/**
	 * 
	 * methods: userPreTransaction <br>
	 * description: 用户预处理 <br>
	 * author: Roy <br>
	 * date: 2019年9月22日 上午10:39:09
	 * 
	 * @param userPreTransaction
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> userPreTransaction(UserPreTransaction userPreTransaction) {

		Map<String, String> result = new LinkedHashMap<String, String>();

		try {
			// 定义reqData参数集合
			Map<String, Object> reqData = new HashMap<String, Object>();
			reqData.put("requestNo", userPreTransaction.getRequestNo());
			reqData.put("platformUserNo", userPreTransaction.getPlatformUserNo());
			reqData.put("bizType", userPreTransaction.getBizType());
			reqData.put("amount", userPreTransaction.getAmount());
			reqData.put("preMarketingAmount", userPreTransaction.getPreMarketingAmount());
			reqData.put("expired", userPreTransaction.getExpired());
			reqData.put("remark", userPreTransaction.getRemark());
			reqData.put("redirectUrl", userPreTransaction.getRedirectUrl());
			reqData.put("projectNo", userPreTransaction.getProjectNo());
			reqData.put("share", userPreTransaction.getShare());
			reqData.put("creditsaleRequestNo", userPreTransaction.getCreditsaleRequestNo());
			// 所有的reqData的JSON内都须包含time stamp时间参数，并传入当前时间如果此时间和存管系统时间相差过大（10 分钟），则拒绝执行操作。
			reqData.put("timestamp", DateUtils.formatDate(new Date(), "yyyyMMddHHmmss"));
			Map<String, String> requestParam = AppUtil.lmGeneratePostParam(ServiceNameEnum.USER_PRE_TRANSACTION.getValue(), reqData);
			// 结果集返回
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
