package com.power.platform.lanmao.search.service;

import java.security.PrivateKey;


import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.power.platform.common.config.Global;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.lanmao.common.AppUtil;
import com.power.platform.lanmao.common.SignatureAlgorithm;
import com.power.platform.lanmao.common.SignatureUtils;
import com.power.platform.lanmao.search.pojo.LanMaoUserInfo;
import com.power.platform.lanmao.type.ServiceNameEnum;
import com.power.platform.lanmao.type.UserTypeEnum;
import com.power.platform.lanmao.type.UserRoleEnum;
import com.power.platform.lanmao.type.AuditStatusEnum;
import com.power.platform.lanmao.type.ActiveStatusEnum;
import com.power.platform.lanmao.type.BankCodeEnum;
import com.power.platform.lanmao.type.AccessTypeEnum;
import com.power.platform.lanmao.type.AuthEnum;
import com.power.platform.lanmao.type.IdCardTypeEnum;
import com.power.platform.sys.utils.HttpUtil;
import com.power.platform.lanmao.search.utils.ResultVOUtil;

/**
 * 用户信息查询
 * @author fuwei
 *
 */
@Service("lanMaoSearchUserIfoDataService")
public class LanMaoSearchUserInfoDataService {

	/**
	 * 直连请求地址
	 */
	private static final String SERVICE_URL = Global.getConfigLanMao("serviceUrl");

	private static final Logger log = LoggerFactory.getLogger(LanMaoSearchUserInfoDataService.class);

	public Map<String, Object> searchUserInfo(String platformUserNo) {

		Map<String, Object> result = new LinkedHashMap<String, Object>();
		try {

			// 定义reqData参数集合：平台用户编号、
			Map<String, String> reqDataMap = new HashMap<String, String>();
			reqDataMap.put("platformUserNo", platformUserNo);
			// 传入网银转账充值参数，返回信息会返回该用户白名单银行卡号；
			reqDataMap.put("additionalFields", "ONLINE_WHITELIST");
			// 所有的reqData的JSON内都须包含time stamp时间参数，并传入当前时间如果此时间和存管系统时间相差过大（10 分钟），则拒绝执行操作。
			reqDataMap.put("timestamp", DateUtils.formatDate(new Date(), "yyyyMMddHHmmss"));

			Map<String, String> requestMap = AppUtil.generatePostParam(ServiceNameEnum.QUERY_USER_INFORMATION.getValue(), reqDataMap);
			log.debug("reques json:{}", JSON.toJSONString(requestMap));

			Map<String, String> header = new HashMap<>();
			String responseStr = HttpUtil.sendPost(SERVICE_URL, requestMap, header, "utf-8");
			// logger.debug("response json:{}", responseStr);
           
			JSONObject jsonObject = JSONObject.parseObject(responseStr);
			if(jsonObject.getString("code").equals("0")&&jsonObject.getString("status").contains("SUCCESS")) {
			result.put("code",ResultVOUtil.code(jsonObject.getString("code")));
			result.put("status", jsonObject.getString("status") != null ? ResultVOUtil.status(jsonObject.getString("status")) : "");
			result.put("platformUserNo", jsonObject.getString("platformUserNo") != null ? jsonObject.getString("platformUserNo") : "");
			result.put("userType", jsonObject.getString("userType") != null ? UserTypeEnum.getTextByValue(jsonObject.getString("userType")) : "");
			result.put("userRole", jsonObject.getString("userRole") != null ? UserRoleEnum.getTextByValue(jsonObject.getString("userRole")) : "");
			result.put("auditStatus", jsonObject.getString("auditStatus") != null ? AuditStatusEnum.getTextByValue(jsonObject.getString("auditStatus")) : "");
			result.put("activeStatus", jsonObject.getString("activeStatus") != null ? ActiveStatusEnum.getTextByValue(jsonObject.getString("activeStatus")) : "");
			result.put("balance", jsonObject.getString("balance") != null ? jsonObject.getString("balance") : "");
			result.put("availableAmount", jsonObject.getString("availableAmount") != null ? jsonObject.getString("availableAmount") : "");
			result.put("freezeAmount", jsonObject.getString("freezeAmount") != null ? jsonObject.getString("freezeAmount") : "");
			result.put("arriveBalance", jsonObject.getString("arriveBalance") != null ? jsonObject.getString("arriveBalance") : "");
			result.put("floatBalance", jsonObject.getString("floatBalance") != null ? jsonObject.getString("floatBalance") : "");
			result.put("bankcardNo",jsonObject.getString("bankcardNo") != null ? jsonObject.getString("bankcardNo") : "");
			result.put("bankcode", jsonObject.getString("bankcode") != null ? BankCodeEnum.getTextByValue(jsonObject.getString("bankcode")) : "");
			result.put("mobile", jsonObject.getString("mobile") != null ? jsonObject.getString("mobile") : "");
			String value = " ";
			if(jsonObject.getString("authlist")!=null) {
				String[] authLists = jsonObject.getString("authlist").toString().split(",");
				for (int i = 0 ; i <authLists.length ; i++ ) {
                    if(AuthEnum.getTextByValue(authLists[i]) != null) {
                        if(value != " ")
                            value += "," + AuthEnum.getTextByValue(authLists[i]);
                        else
                            value = AuthEnum.getTextByValue(authLists[i]);
                    }
                }
			}
			result.put("authlist",value);
			String isImportUserActivate = "";
			if(jsonObject.getString("isImportUserActivate") != null) {
				if(jsonObject.getString("isImportUserActivate").equals("true")) {
					isImportUserActivate ="已激活";
				}else if(jsonObject.getString("isImportUserActivate").equals("false")) {
					isImportUserActivate ="未激活";
				}
			}
			result.put("isImportUserActivate", isImportUserActivate);
			result.put("accessType", jsonObject.getString("accessType") != null ? AccessTypeEnum.getTextByValue(jsonObject.getString("accessType")) : "");
			result.put("idCardType", jsonObject.getString("idCardType") != null ? IdCardTypeEnum.getTextByValue(jsonObject.getString("idCardType")) : "");
			result.put("idCardNo", jsonObject.getString("idCardNo") != null ? jsonObject.getString("idCardNo") : "");
			result.put("name", jsonObject.getString("name") != null ? jsonObject.getString("name") : "");
			
			result.put("onlineWhiteBankcards", jsonObject.getString("onlineWhiteBankcards") != null ? jsonObject.getString("onlineWhiteBankcards") : "");
			result.put("amount", jsonObject.getString("amount") != null ? jsonObject.getString("amount") : "");
			result.put("failTime", jsonObject.getString("failTime") != null ? jsonObject.getString("failTime") : "");
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
