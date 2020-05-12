package com.power.platform.lanmao.search.utils;

import com.alibaba.fastjson.JSONObject;

import com.power.platform.lanmao.search.pojo.LanMaoUserInfo;

public class ResponsePojoUtil {


	public static LanMaoUserInfo handler(String responseStr) {

		if (responseStr != null && !responseStr.equals("")) {
			// 解析响应
			JSONObject jsonObject = JSONObject.parseObject(responseStr);
			LanMaoUserInfo  lanMaoUserInfo= new LanMaoUserInfo();
			lanMaoUserInfo.setCode(jsonObject.getString("code") != null ? jsonObject.getString("code") : "");
			lanMaoUserInfo.setStatus(jsonObject.getString("status") != null ? jsonObject.getString("status") : "");
//			lanMaoUserInfo.setErrorCode(jsonObject.getString("errorCode") != null ? jsonObject.getString("errorCode") : "");
//			lanMaoUserInfo.setErrorMessage(jsonObject.getString("errorMessage") != null ? jsonObject.getString("errorMessage") : "");
			lanMaoUserInfo.setPlatformUserNo(jsonObject.getString("platformUserNo") != null ? jsonObject.getString("platformUserNo") : "");
			lanMaoUserInfo.setUserType(jsonObject.getString("userType") != null ? jsonObject.getString("userType") : "");
			lanMaoUserInfo.setUserRole(jsonObject.getString("userRole") != null ? jsonObject.getString("userRole") : "");
			lanMaoUserInfo.setAuditStatus(jsonObject.getString("auditStatus") != null ? jsonObject.getString("auditStatus") : "");
			lanMaoUserInfo.setActiveStatus(jsonObject.getString("activeStatus") != null ? jsonObject.getString("activeStatus") : "");
			lanMaoUserInfo.setBalance(jsonObject.getString("balance") != null ? jsonObject.getString("balance") : "");
			lanMaoUserInfo.setAvailableAmount(jsonObject.getString("availableAmount") != null ? jsonObject.getString("availableAmount") : "");
			lanMaoUserInfo.setFreezeAmount(jsonObject.getString("freezeAmount") != null ? jsonObject.getString("freezeAmount") : "");
			lanMaoUserInfo.setArriveBalance(jsonObject.getString("arriveBalance") != null ? jsonObject.getString("arriveBalance") : "");
			lanMaoUserInfo.setFloatBalance(jsonObject.getString("floatBalance") != null ? jsonObject.getString("floatBalance") : "");
			lanMaoUserInfo.setBankcardNo(jsonObject.getString("bankcardNo") != null ? jsonObject.getString("bankcardNo") : "");
			lanMaoUserInfo.setBankcode(jsonObject.getString("bankcode") != null ? jsonObject.getString("bankcode") : "");
			lanMaoUserInfo.setMobile(jsonObject.getString("mobile") != null ? jsonObject.getString("mobile") : "");
			lanMaoUserInfo.setAuthlist(jsonObject.getString("authlist") != null ? jsonObject.getString("authlist") : "");
			lanMaoUserInfo.setIsImportUserActivate(jsonObject.getString("isImportUserActivate") != null ? jsonObject.getString("isImportUserActivate") : "");
			lanMaoUserInfo.setAccessType(jsonObject.getString("accessType") != null ? jsonObject.getString("accessType") : "");
			lanMaoUserInfo.setIdCardType(jsonObject.getString("idCardType") != null ? jsonObject.getString("idCardType") : "");
			lanMaoUserInfo.setIdCardNo(jsonObject.getString("idCardNo") != null ? jsonObject.getString("idCardNo") : "");
			lanMaoUserInfo.setName(jsonObject.getString("name") != null ? jsonObject.getString("name") : "");
			
			lanMaoUserInfo.setFailTime(jsonObject.getString("failTime") != null ? jsonObject.getString("failTime") : "");
			lanMaoUserInfo.setAmount(jsonObject.getString("amount") != null ? jsonObject.getString("amount") : "");
			return lanMaoUserInfo;
		}else
		return null;
	}
}
