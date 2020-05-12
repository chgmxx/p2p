package com.power.platform.common.web.listener;

import javax.servlet.ServletContext;

import org.springframework.web.context.WebApplicationContext;

import com.power.platform.common.config.Global;

public class WebContextListener extends org.springframework.web.context.ContextLoaderListener {
	
	 
	public WebApplicationContext initWebApplicationContext(ServletContext servletContext) {
		if (!printKeyLoadMessage()){
			return null;
		}
		return super.initWebApplicationContext(servletContext);
	}
	
	/**
	 * 获取Key加载信息
	 */
	private boolean printKeyLoadMessage(){
		StringBuilder sb = new StringBuilder();
		sb.append("\r\n======================================================================\r\n");
		sb.append("\r\n    欢迎使用 "+Global.getConfig("productName")+" \r\n");
		sb.append("\r\n======================================================================\r\n");
		System.out.println(sb.toString());
		return true;
	}
}
