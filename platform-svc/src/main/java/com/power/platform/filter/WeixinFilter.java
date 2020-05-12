package com.power.platform.filter;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.weixin.utils.WeixinUtil;

public class WeixinFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest; 
	    HttpServletResponse response = (HttpServletResponse) servletResponse;
	    String code = request.getParameter("code");
	    String returnUrl = request.getRequestURL().toString();
	    System.out.println(code + "===============================code");
	    System.out.println(returnUrl+ "===============================returnUrl");
	    if(StringUtils.isBlank(code)){
	    	// 发送请求，获取code
			String requestUrl = WeixinUtil.base_share_url.replace("APPID",WeixinUtil.WEIXIN_APP_ID).replace("REDIRECT_REPLACE", URLEncoder.encode(returnUrl,"UTF-8"));
			try {
				response.sendRedirect(requestUrl);
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }else{
	    	String url = WeixinUtil.web_oauth_accesstoken_url.replace("APPID",WeixinUtil.WEIXIN_APP_ID).replace("SECRET", WeixinUtil.WEIXIN_APP_SECRET).replace("CODE", code);
			JSONObject jsonObject = WeixinUtil.httpRequest(url, "GET", null);
			if (jsonObject != null) {
				String openid = jsonObject.getString("openid");
				System.out.println("getopenid==========="+openid);
				if(returnUrl.equals(WeixinUtil.weixinBindUrl)){							// 绑定解绑
					response.sendRedirect("http://www.cicmorgan.com/weixin_bind.html?openid="+openid);
				} else {											
					request.setAttribute("openId", openid);
					chain.doFilter(request, response);
				}
			}
	    }
	}

	@Override
	public void destroy() {

	}

}
