package com.power.platform.lanmao.search.service;

import java.security.GeneralSecurityException;



import java.security.PrivateKey;
import java.util.ArrayList;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.power.platform.lanmao.common.AppUtil;
import com.power.platform.lanmao.common.HttpUtils;
import com.power.platform.lanmao.common.SignatureAlgorithm;
import com.power.platform.lanmao.common.SignatureUtils;
import com.power.platform.lanmao.search.utils.ResultVOUtil;
import com.power.platform.lanmao.type.ServiceNameEnum;
import com.power.platform.lanmao.type.ProductTypeEnum;
import com.power.platform.lanmao.type.RepaymentWayEnum;
import com.power.platform.lanmao.type.ProjectStatusEnum;
import com.power.platform.lanmao.type.ProjectTypeEnum;
import com.power.platform.lanmao.type.ServiceNameEnum;
import com.power.platform.sys.utils.HttpUtil;
import com.power.platform.common.config.Global;
import com.power.platform.common.utils.DateUtils;
/**
 * 标的信息查询
 * @author fuwei
 *
 */
@Service("lanMaoSearchPruductDataService")
public class LanMaoSearchProjectDataService {
	/**
	 * 直连请求地址
	 */
	private static final String SERVICE_URL = Global.getConfigLanMao("serviceUrl");
	private static final Logger log = LoggerFactory.getLogger(LanMaoSearchProjectDataService.class);

	public Map<String, Object> searchProject(String projectNo) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		
		try {

			// 定义reqData参数集合：标的号
			Map<String, String> reqDataMap = new HashMap<String, String>();
			reqDataMap.put("projectNo", projectNo);
			// 所有的reqData的JSON内都须包含time stamp时间参数，并传入当前时间如果此时间和存管系统时间相差过大（10 分钟），则拒绝执行操作。
			reqDataMap.put("timestamp", DateUtils.formatDate(new Date(), "yyyyMMddHHmmss"));

			// logger.debug("reques data json:{}", JSON.toJSONString(reqDataMap));
			Map<String, String> requestMap = AppUtil.generatePostParam(ServiceNameEnum.QUERY_PROJECT_INFORMATION.getValue(), reqDataMap);
			log.debug("reques json:{}", JSON.toJSONString(requestMap));

			Map<String, String> header = new HashMap<>();
			String responseStr = HttpUtil.sendPost(SERVICE_URL, requestMap, header, "utf-8");
			// logger.debug("response json:{}", responseStr);
			JSONObject jsonObject = JSONObject.parseObject(responseStr);
			if(jsonObject.getString("code").equals("0")&&jsonObject.getString("status").contains("SUCCESS")) {
				result.put("code", ResultVOUtil.code(jsonObject.getString("code")));
				result.put("status", jsonObject.getString("status") != null ? ResultVOUtil.status(jsonObject.getString("status")): "");
				result.put("platformUserNo", jsonObject.getString("platformUserNo") != null ? jsonObject.getString("platformUserNo") : "");
				result.put("projectNo", jsonObject.getString("projectNo") != null ? jsonObject.getString("projectNo") : "");
				result.put("projectAmount", jsonObject.getString("projectAmount") != null ? jsonObject.getString("projectAmount") : "");
				result.put("projectName", jsonObject.getString("projectName") != null ? jsonObject.getString("projectName") : "");
				result.put("projectType", jsonObject.getString("projectType") != null ? ProjectTypeEnum.getTextByValue(jsonObject.getString("projectType")) : "");
				result.put("projectPeriod", jsonObject.getString("projectPeriod") != null ? jsonObject.getString("projectPeriod") : "");
				String projectProperties = "";
				if(jsonObject.getString("projectProperties") != null) {
					if(jsonObject.getString("projectProperties").equals("STOCK")) {
						projectProperties = "存量标的";
					}else if(jsonObject.getString("projectProperties").equals("NEW")) {
						projectProperties = "新增标的";
					}
				}
				result.put("projectProperties", projectProperties);
				result.put("annualInterestRate", jsonObject.getString("annualInterestRate") != null ? jsonObject.getString("annualInterestRate") : "");
				result.put("repaymentWay", jsonObject.getString("repaymentWay") != null ? RepaymentWayEnum.getTextByValue(jsonObject.getString("repaymentWay")) : "");
				result.put("projectStatus", jsonObject.getString("projectStatus") != null ? ProjectStatusEnum.getTextByValue(jsonObject.getString("projectStatus")) : "");
				result.put("loanAmount", jsonObject.getString("loanAmount") != null ? jsonObject.getString("loanAmount") : "");
				result.put("repaymentAmount", jsonObject.getString("repaymentAmount") != null ? jsonObject.getString("repaymentAmount") : "");
				result.put("income", jsonObject.getString("income") != null ? jsonObject.getString("income") : "");
				result.put("tenderAmount", jsonObject.getString("tenderAmount") != null ? jsonObject.getString("tenderAmount") : "");
			}else {
				result.put("code", jsonObject.getString("code") != null ? ResultVOUtil.code(jsonObject.getString("code")): "");
				result.put("status", jsonObject.getString("status") != null ? ResultVOUtil.status(jsonObject.getString("status")) : "");
				result.put("errorCode", jsonObject.getString("errorCode") != null ?  ResultVOUtil.getErrorCode(jsonObject.getString("errorCode")) : "");
				result.put("errorMessage", jsonObject.getString("errorMessage") != null ? jsonObject.getString("errorMessage") : "");
			}			
			return result;
		} catch (Exception e) {
			log.error("Exception:{}", e.getMessage());
			result.put("code", "1");
			result.put("status", "INIT");
			result.put("errorCode", "1");
			result.put("errorMessage", "调用失败");
			return result;
		}	
	}
}


