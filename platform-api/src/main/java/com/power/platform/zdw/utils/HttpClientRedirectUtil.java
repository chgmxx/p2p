package com.power.platform.zdw.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

public class HttpClientRedirectUtil {

	/**
	 * 
	 * methods: getRedirectInfo <br>
	 * description: 获取重定向后的页面信息. <br>
	 * author: Roy <br>
	 * date: 2019年6月18日 下午2:24:04
	 * 
	 * @param url 即将访问
	 */
	public static void getRedirectInfo(String url) {

		HttpClient httpClient = new DefaultHttpClient();
		HttpContext httpContext = new BasicHttpContext();
		HttpGet httpGet = new HttpGet(url);
		try {
			// 将HttpContext对象作为参数传给execute()方法
			// 则HttpClient会把请求响应交互过程中的状态信息存储在HttpContext中
			HttpResponse response = httpClient.execute(httpGet, httpContext);
			// 获取重定向之后的主机地址信息，即"http://127.0.0.1:8088"
			HttpHost targetHost = (HttpHost) httpContext.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
			// 获取实际的请求对象的URI，即重定向之后的"/blog/admin/login.jsp"
			HttpUriRequest realRequest = (HttpUriRequest) httpContext.getAttribute(ExecutionContext.HTTP_REQUEST);
			System.out.println("主机地址:" + targetHost);
			System.out.println("URI信息:" + realRequest.getURI());
			HttpEntity entity = response.getEntity();
			if (null != entity) {
				System.out.println("响应内容:" + EntityUtils.toString(entity, ContentType.getOrDefault(entity).getCharset()));
				EntityUtils.consume(entity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
	}
}
