package com.power.platform.filter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.power.platform.common.utils.JedisUtils;
import com.power.platform.common.utils.StringUtils;

public class TokenFilter implements Filter {

	// private static final String URL = "/services/check/checkToken";

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		JSONObject jsonObject = new JSONObject();
		PrintWriter out = null;
		try {
			String token = request.getParameter("token");
			if (StringUtils.isNotBlank(token)) {
				String userId = JedisUtils.get(token);
				if (StringUtils.isNotBlank(userId)) {
					JedisUtils.set(token, userId, 1200);
					chain.doFilter(request, response);
				} else {
					out = response.getWriter();
					jsonObject.put("message", "系统超时");
					jsonObject.put("state", "4");
					out.append(jsonObject.toJSONString());
					out.close();
				}
			} else {
				jsonObject.put("message", "系统超时");
				jsonObject.put("state", "4");
				out = response.getWriter();
				out.append(jsonObject.toJSONString());
				out.close();
				// response.sendRedirect(request.getContextPath()+URL+"?flag="+CheckService.IS_NULL);
			}
		} catch (Exception e) {
			e.printStackTrace();
			out = response.getWriter();
			jsonObject.put("message", "系统异常");
			jsonObject.put("Exception", e.getMessage());
			jsonObject.put("state", "1");
			out.append(jsonObject.toJSONString());
			out.close();
		}
	}

	@Override
	public void destroy() {

	}

}
