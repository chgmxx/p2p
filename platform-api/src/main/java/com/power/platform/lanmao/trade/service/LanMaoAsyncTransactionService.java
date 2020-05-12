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
import com.power.platform.lanmao.trade.pojo.AsyncTransaction;
import com.power.platform.lanmao.trade.pojo.ResponsePojo;
import com.power.platform.lanmao.trade.pojo.ResponsePojoUtil;
import com.power.platform.lanmao.type.ServiceNameEnum;

/**
 * 
 * class: LanMaoAsyncTransactionService <br>
 * description: 批量交易 <br>
 * author: Roy <br>
 * date: 2019年9月23日 上午10:01:55
 */
@Service("lanMaoAsyncTransactionService")
public class LanMaoAsyncTransactionService {

	private final static Logger logger = LoggerFactory.getLogger(LanMaoAsyncTransactionService.class);

	/**
	 * 直连请求地址
	 */
	private static final String SERVICE_URL = Global.getConfigLanMao("serviceUrl");

	/**
	 * 
	 * methods: asyncTransaction <br>
	 * description: 批量交易，是否幂等：是，接口模式：直连，异步通知：是 <br>
	 * author: Roy <br>
	 * date: 2019年9月24日 下午9:05:41
	 * 
	 * @param asyncTransaction
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> asyncTransaction(AsyncTransaction asyncTransaction) {

		Map<String, String> result = new LinkedHashMap<String, String>();
		CloseableHttpResponse response = null;
		String lmresult = "";
		try {
			// 定义reqData参数集合
			Map<String, Object> reqData = new HashMap<String, Object>();
			reqData.put("batchNo", asyncTransaction.getBatchNo());
			reqData.put("bizDetails", asyncTransaction.getBizDetails());
			// 所有的reqData的JSON内都须包含time stamp时间参数，并传入当前时间如果此时间和存管系统时间相差过大（10 分钟），则拒绝执行操作。
			reqData.put("timestamp", DateUtils.formatDate(new Date(), "yyyyMMddHHmmss"));
			Map<String, String> requestParam = AppUtil.lmGeneratePostParam(ServiceNameEnum.ASYNC_TRANSACTION.getValue(), reqData);
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
		result.put("requestNo", handler.getRequestNo());
		result.put("errorCode", handler.getErrorCode());
		result.put("errorMessage", handler.getErrorMessage());
		return result;
	}

}
