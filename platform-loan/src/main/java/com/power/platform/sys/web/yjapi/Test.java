package com.power.platform.sys.web.yjapi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.power.platform.common.utils.HttpRequestUtil;

public class Test {

	public static void main(String[] args) {

		String appKey = "cicmorgan";
		String uscc = "9111010859963405XW";

		// 企业关键字精确获取详细信息.
		StringBuffer strB = new StringBuffer();
		// main host.
		strB.append("http://dev.i.yjapi.com/ECIV4/GetDetailsByName");
		// key.
		strB.append("?key=").append(appKey);
		// keyword.
		strB.append("&keyword=").append(uscc);
		String url = strB.toString();
		System.err.println("URL = " + url);
		String resultStr = HttpRequestUtil.get(url);
		System.err.println(resultStr);

		// 因为JSONObject继承了JSON，所以这样也是可以的.
		JSONObject jsonObject = JSONObject.parseObject(resultStr);
		// Status.
		String Status = jsonObject.getString("Status");
		System.out.println(Status);
		// Message.
		String Message = jsonObject.getString("Message");
		System.out.println(Message);
		// OrderNumber.
		String OrderNumber = jsonObject.getString("OrderNumber");
		System.out.println(OrderNumber);
		// Result.
		JSONObject yjapiResultObject = jsonObject.getJSONObject("Result");
		System.out.println(yjapiResultObject);
		// 复杂JSON格式字符串与与JavaBean之间的转换.
		String resultJsonStr = JSONObject.toJSONString(yjapiResultObject);
		YjapiResult yjapiResult = JSON.parseObject(resultJsonStr, new TypeReference<YjapiResult>() {});
		
		System.out.println(yjapiResult.toString());

		// Map<String, Object> map = jsonObject;
		// // System.err.println(map);
		// System.out.println("Status：" + map.get("Status"));
		// System.out.println("Message：" + map.get("Message"));
		// YjapiResult yjapiResult = (YjapiResult) map.get("Result");
		// System.out.println(yjapiResult.toString());
	}
}
