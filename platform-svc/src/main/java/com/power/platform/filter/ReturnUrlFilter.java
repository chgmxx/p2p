package com.power.platform.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.power.platform.cgbpay.config.ServerURLConfig;

public class ReturnUrlFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		String from = request.getParameter("backto");
		String orderId = request.getParameter("orderId");

		// 用户授权前台通知地址拦截.
		if (from != null) {
			if (from.equals("borrowingWebAuthorization")) {
				String creditUserId = request.getParameter("id");
				// 页面响应.
				response.sendRedirect(ServerURLConfig.BACK_TO_BORROWING_WEB_AUTHORIZATION + "?id=" + creditUserId);
				return;
			}
		}

		if (from != "wap" && !"wap".equals(from)) {
			if (from == "borrowWeb" || "borrowWeb".equals(from)) { // 借款端充值，返回账户管理页面.
				String creditUserId = request.getParameter("id");
//				response.sendRedirect(ServerURLConfig.CREDIT_COMPANY_RETURN_URL + "?id=" + creditUserId);
				// 页面响应.
				response.sendRedirect(ServerURLConfig.BACK_TO_BORROWING_WEB_AUTHORIZATION + "?id=" + creditUserId);
				return;
			} else {
				if (!StringUtils.isBlank(orderId)) {
					response.sendRedirect(ServerURLConfig.BACK_INVEST_URL_BACKTOWEBSTATE + "?orderId=" + orderId);
				} else {
					response.sendRedirect(ServerURLConfig.RETURN_INVEST_URL);
				}
			}

		} else {
			if (!StringUtils.isBlank(orderId)) {
				response.sendRedirect(ServerURLConfig.BACK_INVEST_URL_BACKTOWEBSTATE + "?orderId=" + orderId);
			} else {
				response.sendRedirect(ServerURLConfig.RETURN_INVEST_URL);
			}

		}

	}

	@Override
	public void destroy() {

	}

}
