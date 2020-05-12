package com.power.platform.lanmao.search.service;

import java.security.PrivateKey;



import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ibm.icu.text.SimpleDateFormat;
import com.power.platform.common.config.Global;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.lanmao.common.AppUtil;
import com.power.platform.lanmao.search.pojo.LanMaoOnlineRechange;
import com.power.platform.lanmao.type.ServiceNameEnum;
import com.power.platform.sys.utils.HttpUtil;
import com.power.platform.lanmao.search.utils.ResultVOUtil;

/**
 * 网银转账充值记录查询
 * @author fuwei
 *
 */
@Service("lanMaoQueryTransactionRecordService")
public class LanMaoQueryTransactionRecordService {

	/**
	 * 直连请求地址
	 */
	private static final String SERVICE_URL = Global.getConfigLanMao("serviceUrl");

	private static final Logger log = LoggerFactory.getLogger(LanMaoQueryTransactionRecordService.class);

	public Map<String, Object> rueryTransactionRecord(String platformUserNo,String transactionType,String startTime,String endTime) {

		Map<String, Object> result = new LinkedHashMap<String, Object>();
		  SimpleDateFormat sdt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	      SimpleDateFormat sdt2 = new SimpleDateFormat("yyyyMMddHHmmss");
		try {

			// 定义reqData参数集合：平台用户编号+交易类型：网银转账充值【ONLINE_RECHARGE】
			Map<String, String> reqDataMap = new HashMap<String, String>();
			reqDataMap.put("platformUserNo", platformUserNo);
			reqDataMap.put("transactionType", transactionType);
			reqDataMap.put("startTime", startTime);
			reqDataMap.put("endTime", endTime);
			// 所有的reqData的JSON内都须包含time stamp时间参数，并传入当前时间如果此时间和存管系统时间相差过大（10 分钟），则拒绝执行操作。
			reqDataMap.put("timestamp", DateUtils.formatDate(new Date(), "yyyyMMddHHmmss"));

			Map<String, String> requestMap = AppUtil.generatePostParam(ServiceNameEnum.QUERY_TRANSACTION_RECORD.getValue(), reqDataMap);
			log.debug("reques json:{}", JSON.toJSONString(requestMap));

			Map<String, String> header = new HashMap<>();
			String responseStr = HttpUtil.sendPost(SERVICE_URL, requestMap, header, "utf-8");
			// logger.debug("response json:{}", responseStr);
           
			JSONObject jsonObject = JSONObject.parseObject(responseStr);
			if(jsonObject.getString("code").equals("0")&&jsonObject.getString("status").contains("SUCCESS")) {
			result.put("code",ResultVOUtil.code(jsonObject.getString("code")));
			result.put("status", jsonObject.getString("status") != null ? ResultVOUtil.status(jsonObject.getString("status")) : "");
			result.put("transactionType", jsonObject.getString("transactionType") != null ? jsonObject.getString("transactionType") : "");
			result.put("platformUserNo", jsonObject.getString("platformUserNo") != null ? jsonObject.getString("platformUserNo") : "");
			result.put("startTime", jsonObject.getString("startTime") != null ? jsonObject.getString("startTime") : "");
			result.put("endTime", jsonObject.getString("endTime") != null ? jsonObject.getString("endTime") : "");
			List<LanMaoOnlineRechange> onlineRechanges = null;
			if(jsonObject.getString("transactionList") != null) {
				onlineRechanges = JSONObject.parseArray(jsonObject.getString("transactionList"),LanMaoOnlineRechange.class);
				for(LanMaoOnlineRechange rechange : onlineRechanges) {
					if(rechange.getTransactionTime()!=null)
						rechange.setTransactionTime(sdt.format(sdt2.parse(rechange.getTransactionTime())));
				}
			}
			result.put("transactionList", jsonObject.getString("transactionList") != null ?onlineRechanges: "");
			}else{
				result.put("code", jsonObject.getString("code") != null ? ResultVOUtil.code(jsonObject.getString("code")) : "");
				result.put("status", jsonObject.getString("status") != null ? ResultVOUtil.status(jsonObject.getString("status")) : "");
				result.put("errorCode", jsonObject.getString("errorCode") != null ? ResultVOUtil.getErrorCode(jsonObject.getString("errorCode")) : "");
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
