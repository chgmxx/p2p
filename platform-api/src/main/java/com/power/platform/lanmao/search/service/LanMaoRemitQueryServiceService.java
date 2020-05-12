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
import com.power.platform.lanmao.type.ServiceNameEnum;
import com.power.platform.lanmao.type.RemitStatusEnum;
import com.power.platform.sys.utils.HttpUtil;
import com.power.platform.lanmao.search.pojo.LanMaoRemit;
import com.power.platform.lanmao.search.utils.ResultVOUtil;

/**
 * 网银转账充值代付查询
 * @author fuwei
 *
 */
@Service("lanMaoRemitQueryServiceService")
public class LanMaoRemitQueryServiceService {

	/**
	 * 直连请求地址
	 */
	private static final String SERVICE_URL = Global.getConfigLanMao("serviceUrl");

	private static final Logger log = LoggerFactory.getLogger(LanMaoRemitQueryServiceService.class);

	public Map<String, Object> remitQueryService(String accountNo,String startTime,String endTime) {

		Map<String, Object> result = new LinkedHashMap<String, Object>();
		SimpleDateFormat sdt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	      SimpleDateFormat sdt2 = new SimpleDateFormat("yyyyMMddHHmmss");
		try {

			// 定义reqData参数集合：专户账户号
			Map<String, String> reqDataMap = new HashMap<String, String>();
			reqDataMap.put("accountNo", accountNo);
			reqDataMap.put("startTime", startTime);
			reqDataMap.put("endTime", endTime);
			// 所有的reqData的JSON内都须包含time stamp时间参数，并传入当前时间如果此时间和存管系统时间相差过大（10 分钟），则拒绝执行操作。
			reqDataMap.put("timestamp", DateUtils.formatDate(new Date(), "yyyyMMddHHmmss"));

			Map<String, String> requestMap = AppUtil.generatePostParam(ServiceNameEnum.REMIT_QUERY_SERVICE.getValue(), reqDataMap);
			log.debug("reques json:{}", JSON.toJSONString(requestMap));

			Map<String, String> header = new HashMap<>();
			String responseStr = HttpUtil.sendPost(SERVICE_URL, requestMap, header, "utf-8");
			// logger.debug("response json:{}", responseStr);
           
			JSONObject jsonObject = JSONObject.parseObject(responseStr);
			if(jsonObject.getString("code").equals("0")&&jsonObject.getString("status").contains("SUCCESS")) {
			result.put("code",ResultVOUtil.code(jsonObject.getString("code")));
			result.put("status", jsonObject.getString("status") != null ? ResultVOUtil.status(jsonObject.getString("status")) : "");
			List<LanMaoRemit> remits = null;
			if(jsonObject.getString("transactionList") != null) {
				remits = JSONObject.parseArray(jsonObject.getString("transactionList"),LanMaoRemit.class);
				for(LanMaoRemit remit : remits) {
					if(remit.getTransactionTime()!=null)
						remit.setTransactionTime(sdt.format(sdt2.parse(remit.getTransactionTime())));
					if(remit.getStatus()!=null)
						remit.setStatus(RemitStatusEnum.getTextByValue(remit.getStatus()));
				}
			}
			result.put("transactionList", jsonObject.getString("transactionList") != null ? remits: "");
			result.put("accountNo", jsonObject.getString("accountNo") != null ? jsonObject.getString("accountNo") : "");
			}else{
				result.put("code", jsonObject.getString("code") != null ? ResultVOUtil.code(jsonObject.getString("code")) : "");
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
