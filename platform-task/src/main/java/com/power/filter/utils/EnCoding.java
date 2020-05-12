package com.power.filter.utils;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class EnCoding implements Filter {
	private FilterConfig fc;

	public void destroy() {
		// TODO Auto-generated method stub

	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		response.setContentType("text/html;charset=UTF-8");
		String code = fc.getInitParameter("code");
		request.setCharacterEncoding(code);
		response.setCharacterEncoding(code);
		chain.doFilter(request, response);
	}

	public void init(FilterConfig fc) throws ServletException {
		this.fc = fc;
	}

}
