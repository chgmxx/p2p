package com.power.platform.lanmao.search.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.power.platform.common.config.Global;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.FileUtils;
import com.power.platform.lanmao.common.AppUtil;
import com.power.platform.lanmao.common.HttpUtils;
import com.power.platform.lanmao.common.VerifySignUtils;
import com.power.platform.lanmao.search.pojo.FileTypePojo;
import com.power.platform.lanmao.search.utils.ResultVOUtil;
import com.power.platform.lanmao.trade.pojo.ResponsePojo;
import com.power.platform.lanmao.trade.pojo.ResponsePojoUtil;
import com.power.platform.lanmao.type.FileTypeEnum;
import com.power.platform.lanmao.type.ServiceNameEnum;
import com.power.platform.sys.utils.HttpUtil;
/**
 * 对账文件
 */
@Service("LanMaoReconciliationService")
public class LanMaoReconciliationService {
	/**
	 * 直连请求地址
	 */
	private static final String SERVICE_URL = Global.getConfigLanMao("serviceUrl");
	private static final Logger log = LoggerFactory.getLogger(LanMaoReconciliationService.class);

	/**
	 * 对账文件确认
	 */
	public Map<String, Object> confirmCheckFile(String dateStr) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		CloseableHttpResponse response = null;
		String lmresult = "";
		try {

			Map<String, Object> reqDataMap = new HashMap<String, Object>();
			String orderId = UUID.randomUUID().toString().replace("-", "");
			System.out.println("orderId = " + orderId);
			reqDataMap.put("requestNo", orderId);
			reqDataMap.put("fileDate", dateStr);//对账文件日期
			
			/**
			 * "requestNo":"CDMS1502849048146",
				"fileDate":"20171024",
				"detail":[
				{"fileType":"RECHARGE"},
				{"fileType":"TRANSACTION"},
				{"fileType":"WITHDRAW"},
				{"fileType":"COMMISSION"},
				{"fileType":"BACKROLL_RECHARGE"}
				],
				"timestamp":"20171024100357"
				}
			 */

			JSONArray jSONArray = new JSONArray(); 
			FileTypePojo fileTypePojo = new FileTypePojo();
			fileTypePojo.setFileType(FileTypeEnum.RECHARGE.getValue());
			jSONArray.add(fileTypePojo);
			fileTypePojo = new FileTypePojo();
			fileTypePojo.setFileType(FileTypeEnum.WITHDRAW.getValue());
			jSONArray.add(fileTypePojo);
			fileTypePojo = new FileTypePojo();
			fileTypePojo.setFileType(FileTypeEnum.COMMISSION.getValue());
			jSONArray.add(fileTypePojo);
			fileTypePojo = new FileTypePojo();
			fileTypePojo.setFileType(FileTypeEnum.TRANSACTION.getValue());
			jSONArray.add(fileTypePojo);
			fileTypePojo = new FileTypePojo();
			fileTypePojo.setFileType(FileTypeEnum.BACKROLL_RECHARGE.getValue());
			jSONArray.add(fileTypePojo);
//			fileTypePojo = new FileTypePojo();
//			fileTypePojo.setFileType(FileTypeEnum.USER.getValue());
//			jSONArray.add(fileTypePojo);
			fileTypePojo = new FileTypePojo();
			fileTypePojo.setFileType(FileTypeEnum.ADJUST_BALANCE.getValue());
			jSONArray.add(fileTypePojo);
			fileTypePojo = new FileTypePojo();
			fileTypePojo.setFileType(FileTypeEnum.ALLBALANCE.getValue());
			jSONArray.add(fileTypePojo);
			fileTypePojo = new FileTypePojo();
			reqDataMap.put("detail", jSONArray);
			
			// 所有的reqData的JSON内都须包含time stamp时间参数，并传入当前时间如果此时间和存管系统时间相差过大（10 分钟），则拒绝执行操作。
			reqDataMap.put("timestamp", DateUtils.formatDate(new Date(), "yyyyMMddHHmmss"));

			// logger.debug("reques data json:{}", JSON.toJSONString(reqDataMap));
			Map<String, String> requestMap = AppUtil.lmGeneratePostParam(ServiceNameEnum.CONFIRM_CHECKFILE.getValue(), reqDataMap);
			log.debug("reques json:{}", JSON.toJSONString(requestMap));

			List<BasicNameValuePair> formParams = new ArrayList<BasicNameValuePair>();
			BasicNameValuePair n1 = new BasicNameValuePair("serviceName", requestMap.get("serviceName"));
			BasicNameValuePair n2 = new BasicNameValuePair("platformNo", requestMap.get("platformNo"));
			BasicNameValuePair n3 = new BasicNameValuePair("reqData", requestMap.get("reqData"));
			BasicNameValuePair n4 = new BasicNameValuePair("keySerial", requestMap.get("keySerial"));
			BasicNameValuePair n5 = new BasicNameValuePair("sign", requestMap.get("sign"));
			formParams.add(n1);
			formParams.add(n2);
			formParams.add(n3);
			formParams.add(n4);
			formParams.add(n5);
			response = HttpUtils.httpPostWithPAaram(SERVICE_URL, formParams);
			lmresult = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
			
			ResponsePojo handler = ResponsePojoUtil.handler(lmresult);
			// 验签
			String code = handler.getCode();
			String status = handler.getStatus();
			try {
				if (!"0".equals(code) || !"SUCCESS".equals(status)) {
					log.debug("接口返回code!=0 || status!=SUCCESS时，不做验签处理");
				}
				else {
					if (!VerifySignUtils.verifySign(response, lmresult)) {
						result.put("code", "1");
						result.put("status", "INIT");
						result.put("errorCode", "1");
						result.put("errorMessage", "sign fail");
						return result;
					}
				}
			} catch (Exception e) {
				log.error("sign fail..... " + e.getMessage());
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
			
		} catch (Exception e) {
			log.error("Exception:{}", e.getMessage());
			result.put("code", "1");
			result.put("status", "INIT");
			result.put("errorCode", "1");
			result.put("errorMessage", "调用失败");
			return result;
		}	
	}
	
	
	/**
	 * 对账文件确认DOWNLOAD_CHECKFILE
	 */
	public Map<String, Object> downLoadCheckFile(String dateStr) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		
		try {

			Map<String, String> reqDataMap = new HashMap<String, String>();
//			String orderId = UUID.randomUUID().toString().replace("-", "");
//			System.out.println("orderId = " + orderId);
//			reqDataMap.put("requestNo", orderId);
			reqDataMap.put("fileDate", dateStr);//对账文件日期
			// 所有的reqData的JSON内都须包含time stamp时间参数，并传入当前时间如果此时间和存管系统时间相差过大（10 分钟），则拒绝执行操作。
			reqDataMap.put("timestamp", DateUtils.formatDate(new Date(), "yyyyMMddHHmmss"));

			// logger.debug("reques data json:{}", JSON.toJSONString(reqDataMap));
			Map<String, String> requestMap = AppUtil.generatePostParam(ServiceNameEnum.DOWNLOAD_CHECKFILE.getValue(), reqDataMap);
			log.debug("reques json:{}", JSON.toJSONString(requestMap));

			Map<String, String> header = new HashMap<>();
			String responseStr = HttpUtil.sendPost(SERVICE_URL, requestMap, header, "utf-8");
			
//			boolean flag = FileUtils.string2File(responseStr, "D:\\downLoad");
//			System.out.println("flag="+flag);
			
//			// logger.debug("response json:{}", responseStr);
//			JSONObject jsonObject = JSONObject.parseObject(responseStr);
//			
//			result.put("code", jsonObject.getString("code") != null ? ResultVOUtil.code(jsonObject.getString("code")): "");
//			result.put("status", jsonObject.getString("status") != null ? ResultVOUtil.status(jsonObject.getString("status")) : "");
//			result.put("errorCode", jsonObject.getString("errorCode") != null ?  ResultVOUtil.getErrorCode(jsonObject.getString("errorCode")) : "");
//			result.put("errorMessage", jsonObject.getString("errorMessage") != null ? jsonObject.getString("errorMessage") : "");	
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


