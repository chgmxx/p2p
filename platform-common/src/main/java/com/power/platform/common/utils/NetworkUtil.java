package com.power.platform.common.utils;


import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

public class NetworkUtil {
	//throws IOException
	 public final static String getIpAddress(HttpServletRequest request) throws IOException {
		 

			 String ip = request.getHeader("X-Forwarded-For");

			 if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {

			 ip = request.getHeader("Proxy-Client-IP");

			 }

			 if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {

			 ip = request.getHeader("WL-Proxy-Client-IP");

			 }

			 if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {

			 ip = request.getRemoteAddr();

			 }

			 return ip;
		 
	 }
}
