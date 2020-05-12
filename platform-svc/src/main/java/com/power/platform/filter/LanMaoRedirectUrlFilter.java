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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.power.platform.cgbpay.config.ServerURLConfig;
import com.power.platform.lanmao.config.RedirectUrlConfig;

/**
 * 
 * class: LanMaoRedirectUrlFilter <br>
 * description: 懒猫回调地址处理 <br>
 * author: Roy <br>
 * date: 2019年9月24日 下午5:24:41
 */
public class LanMaoRedirectUrlFilter implements Filter {

	private static final Logger log = LoggerFactory.getLogger(LanMaoRedirectUrlFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;

		String backto = request.getParameter("backto");
		String orderId = request.getParameter("orderId");

		if ("USER_AUTHORIZATION".equals(backto)) { // 用户授权
			String platformUserNo = request.getParameter("platformUserNo");
			String requestNo = request.getParameter("requestNo");
			String code = request.getParameter("code");
			String status = request.getParameter("status");
			String errorCode = request.getParameter("errorCode");
			String errorMessage = request.getParameter("errorMessage");
			String authList = request.getParameter("authList");
			String amount = request.getParameter("amount");
			String failTime = request.getParameter("failTime");
			log.debug("1:{},2:{},3:{},4:{},5:{},6:{},7:{},8:{},9:{}", platformUserNo, requestNo, code, status, errorCode, errorMessage, authList, amount, failTime);
			// 页面响应.
			response.sendRedirect(RedirectUrlConfig.USER_AUTHORIZATION_RETURN_URL + "?id=" + platformUserNo);
			return;
		}else if("PCWEBRecharge".equals(backto)) {
			response.sendRedirect(ServerURLConfig.RETURN_INVEST_URL);
		}

	}

	@Override
	public void destroy() {

	}

}
