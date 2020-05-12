package com.power.platform.common.listener;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ApplicationListener implements ServletContextListener{
	
	public final static String USERINFO="userinfo";
	 
	public void contextDestroyed(ServletContextEvent arg0) {
	}

	 
	public void contextInitialized(ServletContextEvent arg0) {
		Map<String,Object> usersMap = new HashMap<String,Object>();
		arg0.getServletContext().setAttribute(USERINFO, usersMap);
	}

}
