package com.power.filter.utils;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.power.platform.pay.utils.LLPayUtil;

public class EnCoding implements Filter {
	private FilterConfig fc;

	public void destroy() {}

	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
	    HttpServletResponse response = (HttpServletResponse) servletResponse;
		response.setContentType("text/html;charset=UTF-8");
		String code = fc.getInitParameter("code");
		request.setCharacterEncoding(code);
		response.setCharacterEncoding(code);
		String ip = LLPayUtil.getIpAddr(request);
		request.setAttribute("ip", ip);
		chain.doFilter(request, response);
	} 

	public void init(FilterConfig fc) throws ServletException {
		this.fc = fc;
	}

}
