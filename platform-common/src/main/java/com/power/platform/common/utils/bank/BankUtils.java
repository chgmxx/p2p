/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.common.utils.bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.alibaba.fastjson.JSONObject;


public class BankUtils {

	/**
	 * TODO
	 * @param cardNo 银行卡卡号
	 * @return {"bank":"CMB","validated":true,"cardType":"DC","key":"(卡号)","messages":[],"stat":"ok"}
	 */
	public static String getCardDetail(String cardNo) {
	    // 创建HttpClient实例
	    String url = "https://ccdcapi.alipay.com/validateAndCacheCardInfo.json?_input_charset=utf-8&cardNo=";
	    url+=cardNo;
	    url+="&cardBinCheck=true";
	    StringBuilder sb = new StringBuilder();
	try {
	  URL urlObject = new URL(url);
	  URLConnection uc = urlObject.openConnection();
	  BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
	  String inputLine = null;
	  while ( (inputLine = in.readLine()) != null) {
	    sb.append(inputLine);
	  }
	  in.close();
	} catch (MalformedURLException e) {
	  e.printStackTrace();
	} catch (IOException e) {
	  e.printStackTrace();
	}
	return sb.toString();
	}
	public static void main(String[] args) {
	    System.out.println(getCardDetail("6222520787685454"));
	    JSONObject json = JSONObject.parseObject(getCardDetail("6222520787685454"));
	    String a = json.getString("validated");
	    System.out.println(a);
	    if("true".equals(a)) {
	    	System.out.println(json.get("bank"));
	    }else {
	    	System.out.println("银行卡号不正确");
	    }
	}
	
}
