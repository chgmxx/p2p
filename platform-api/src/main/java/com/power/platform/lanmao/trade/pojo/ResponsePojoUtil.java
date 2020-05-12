package com.power.platform.lanmao.trade.pojo;

import com.alibaba.fastjson.JSONObject;

public class ResponsePojoUtil {

	static ResponsePojo responsePojo = null;

	public static ResponsePojo handler(String responseStr) {

		if (responseStr != null && !responseStr.equals("")) {
			// 解析响应
			JSONObject jsonObject = JSONObject.parseObject(responseStr);
			responsePojo = new ResponsePojo();
			responsePojo.setCode(jsonObject.getString("code") != null ? jsonObject.getString("code") : "");
			responsePojo.setStatus(jsonObject.getString("status") != null ? jsonObject.getString("status") : "");
			responsePojo.setErrorCode(jsonObject.getString("errorCode") != null ? jsonObject.getString("errorCode") : "");
			responsePojo.setErrorMessage(jsonObject.getString("errorMessage") != null ? jsonObject.getString("errorMessage") : "");
			responsePojo.setProjectStatus(jsonObject.getString("projectStatus") != null ? jsonObject.getString("projectStatus") : "");
			responsePojo.setRequestNo(jsonObject.getString("requestNo") != null ? jsonObject.getString("requestNo") : "");
			responsePojo.setTransactionStatus(jsonObject.getString("transactionStatus") != null ? jsonObject.getString("transactionStatus") : "");
			responsePojo.setCreateTime(jsonObject.getString("createTime") != null ? jsonObject.getString("createTime") : "");
			responsePojo.setTransactionTime(jsonObject.getString("transactionTime") != null ? jsonObject.getString("transactionTime") : "");
			responsePojo.setPlatformUserNo(jsonObject.getString("platformUserNo") != null ? jsonObject.getString("platformUserNo") : "");
			responsePojo.setBizType(jsonObject.getString("bizType") != null ? jsonObject.getString("bizType") : "");
		}
		return responsePojo;
	}
}
