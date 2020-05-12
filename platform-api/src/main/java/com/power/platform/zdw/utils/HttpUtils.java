package com.power.platform.zdw.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.cookie.BestMatchSpecFactory;
import org.apache.http.impl.cookie.BrowserCompatSpecFactory;
import org.apache.http.impl.cookie.IgnoreSpecFactory;
import org.apache.http.impl.cookie.NetscapeDraftSpecFactory;
import org.apache.http.impl.cookie.RFC2109SpecFactory;
import org.apache.http.impl.cookie.RFC2965SpecFactory;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.power.platform.common.utils.JedisUtils;

public class HttpUtils {

	public static final String DEFAULT_USER_AGENT = "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:32.0) Gecko/20100101 Firefox/33.0";
	public static final String MOBILE_USER_AGENT = "Mozilla/5.0 (Linux; Android 4.4.4; HTC One_M8 Build/KTU84P) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.93 Mobile Safari/537.36";
	public static final String UTF_8 = "UTF-8";
	public static final String GBK = "GBK";
	public static final String IEPROXY_IP = "101.69.178.161";
	public static final int IEPROXY_PORT = 8888;
	public static final String FIDDLER_IP = "127.0.0.1";
	public static final int FIDDLER_PORT = 8888;
	public static final HttpHost PROXY_FIDDLER = new HttpHost(FIDDLER_IP, FIDDLER_PORT, "http");
	private static final Logger LOG = LoggerFactory.getLogger(HttpUtils.class);
	private static final Pattern RE_UNICODE = Pattern.compile("\\\\u([0-9a-zA-Z]{4})");
	public static boolean userFiddler = false;
	private static RequestConfig DEFAULT_REQUEST_CONFIG = null;
	private static int timeout = 60 * 1000;
	private static int defaultMaxPerRoute = 20;

	public static HttpPost post(String url) {

		return post(url, null);
	}

	public static HttpPost post(String url, Map<String, Object> params) {

		return post(url, params, null);
	}

	public static HttpPost post(String url, Map<String, Object> params, HttpHost proxy) {

		return post(url, params, proxy, DEFAULT_USER_AGENT);
	}

	public static HttpPost post(String url, Map<String, Object> params, HttpHost proxy, String userAgent, String encoding) {

		HttpPost result = new HttpPost(url);
		result.addHeader("User-Agent", userAgent == null ? DEFAULT_USER_AGENT : userAgent);
		if (params != null && !params.isEmpty()) {
			result.setEntity(buildParams(params, encoding));
		}
		result.setConfig(copyDefaultConfig().build());
		return result;
	}

	public static HttpPost post(String url, Map<String, Object> params, HttpHost proxy, String userAgent) {

		HttpPost result = new HttpPost(url);
		// result.addHeader("Connection", "close");
		result.addHeader("User-Agent", userAgent == null ? DEFAULT_USER_AGENT : userAgent);
		if (params != null && !params.isEmpty()) {
			result.setEntity(buildParams(params));
		}
		result.setConfig(copyDefaultConfig().build());
		return result;
	}

	public static HttpGet get(String url) {

		return get(url, null);
	}

	public static HttpGet get(String url, Map<String, Object> params, String userAgent) {

		url += buildParamString(params);
		HttpGet result = new HttpGet(url);
		// result.addHeader("Connection", "close");
		result.addHeader("User-Agent", userAgent == null ? DEFAULT_USER_AGENT : userAgent);
		result.setConfig(copyDefaultConfig().build());
		return result;
	}

	public static HttpGet get(String url, Map<String, Object> params) {

		url += buildParamString(params);
		HttpGet result = new HttpGet(url);
		// result.addHeader("Connection", "close");
		result.addHeader("User-Agent", DEFAULT_USER_AGENT);
		result.setConfig(copyDefaultConfig().build());
		return result;
	}

	public static RequestConfig.Builder copyDefaultConfig() {

		RequestConfig.Builder builder = RequestConfig.copy(getDefaultRequestConfig());
		if (userFiddler || Boolean.valueOf(System.getProperty("use.fiddler", "false"))) {
			builder.setProxy(PROXY_FIDDLER);
		}
		return builder;
	}

	public static RequestConfig getDefaultRequestConfig() {

		if (DEFAULT_REQUEST_CONFIG == null) {
			synchronized (HttpUtils.class) {
				if (DEFAULT_REQUEST_CONFIG == null) {
					RequestConfig.Builder builder = RequestConfig.custom();
					builder.setRedirectsEnabled(false).setRelativeRedirectsAllowed(false);
					builder.setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY);
					// connect to a url 1min
					builder.setConnectTimeout(timeout);
					// socket inputstream.read() 2min
					builder.setSocketTimeout(timeout * 2);
					DEFAULT_REQUEST_CONFIG = builder.build();
				}
			}
		}
		return DEFAULT_REQUEST_CONFIG;
	}

	public static UrlEncodedFormEntity buildParams(Map<String, ? extends Object> params) {

		return buildParams(params, UTF_8);
	}

	@SuppressWarnings("rawtypes")
	public static UrlEncodedFormEntity buildParams(Map<String, ? extends Object> params, String encoding) {

		if (params == null || params.isEmpty()) {
			return null;
		}
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		for (Entry<String, ? extends Object> entry : params.entrySet()) {
			Object value = entry.getValue();
			if (value != null) {
				if (value instanceof List) {
					for (Object o : (List) value) {
						if (o != null) {
							parameters.add(new BasicNameValuePair(entry.getKey(), o.toString()));
						}
					}
				} else {
					parameters.add(new BasicNameValuePair(entry.getKey(), value.toString()));
				}
			} else {
				parameters.add(new BasicNameValuePair(entry.getKey(), null));
			}
		}
		return new UrlEncodedFormEntity(parameters, Charset.forName(encoding));
	}

	public static String buildParamString(Map<String, ? extends Object> params) {

		return buildParamString(params, UTF_8);
	}

	public static String buildParamString(Map<String, ? extends Object> params, String encoding) {

		if (params == null || params.isEmpty()) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		try {
			for (Entry<String, ? extends Object> entry : params.entrySet()) {
				Object value = entry.getValue();
				value = value == null ? "" : value.toString();
				sb.append("&").append(URLEncoder.encode(entry.getKey(), encoding)).append("=").append(URLEncoder.encode((String) value, encoding));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static CloseableHttpClient getHttpClient() {

		return getHttpClient(false, null);
	}

	public static CloseableHttpClient getHttpClient(boolean trustAllSSL) {

		return getHttpClient(trustAllSSL, null);
	}

	public static CloseableHttpClient getHttpClient(SSLConnectionSocketFactory sslcsf, CookieStore cookieStore) {

		return getHttpClient(sslcsf, cookieStore, false);
	}

	public static CloseableHttpClient getHttpClient(SSLConnectionSocketFactory sslcsf, CookieStore cookieStore, boolean useProxy) {

		HttpClientBuilder builder = getBuilder();
		if (cookieStore != null) {
			builder.setDefaultCookieStore(cookieStore);
		}
		builder.setSSLSocketFactory(sslcsf);
		if (useProxy) {
			// TODO
			JSONObject ipjob = JSON.parseObject(ProxyUtils.getSocksProxyHost());
			builder.setProxy(new HttpHost(ipjob.getString("ip"), ipjob.getInteger("port"), "http"));
		}
		return builder.build();
	}

	public static CloseableHttpClient getHttpClientProxy(SSLConnectionSocketFactory sslcsf, CookieStore cookieStore, String ipJob) {

		JSONObject ipjob = JSON.parseObject(ipJob);
		HttpClientBuilder builder = getProxyBuilder(ipjob.getString("ip"), ipjob.getInteger("port"));
		if (cookieStore != null) {
			builder.setDefaultCookieStore(cookieStore);
		}
		builder.setSSLSocketFactory(sslcsf);
		return builder.build();
	}

	public static CloseableHttpClient getHttpClient(boolean trustAllSSL, CookieStore cookieStore) {

		HttpClientBuilder builder = getBuilder();
		if (cookieStore != null) {
			builder.setDefaultCookieStore(cookieStore);
		}
		if (trustAllSSL) {
			builder.setSSLSocketFactory(SSLUtils.TRUAT_ALL_SSLSF);
		}
		return builder.build();
	}

	/**
	 * 使用代理
	 *
	 * @param trustAllSSL
	 * @param cookieStore
	 * @param isProxy
	 * @return
	 */
	public static CloseableHttpClient getHttpClient_Proxy(boolean trustAllSSL, CookieStore cookieStore, String ipJob) {

		JSONObject ipjob = JSON.parseObject(ipJob);
		HttpClientBuilder builder = null;
		builder = getProxyBuilder(ipjob.getString("ip"), ipjob.getInteger("port"));
		if (cookieStore != null) {
			builder.setDefaultCookieStore(cookieStore);
		}
		if (trustAllSSL) {
			builder.setSSLSocketFactory(SSLUtils.TRUAT_ALL_SSLSF);
		}
		return builder.build();
	}

	/**
	 * 使用代理
	 *
	 * @param trustAllSSL
	 * @param cookieStore
	 * @param isProxy
	 * @return
	 */
	public static CloseableHttpClient getHttpClient_Proxy(boolean trustAllSSL, CookieStore cookieStore, String ip, int port) {

		HttpClientBuilder builder = null;
		builder = getProxyBuilder(ip, port);
		if (cookieStore != null) {
			builder.setDefaultCookieStore(cookieStore);
		}
		if (trustAllSSL) {
			builder.setSSLSocketFactory(SSLUtils.TRUAT_ALL_SSLSF);
		}
		return builder.build();
	}

	// public static CloseableHttpClient getHttpClient(CookieStore cookieStore)
	// {
	// HttpClientBuilder builder = getProxyBuilder();
	// if (cookieStore != null) {
	// builder.setDefaultCookieStore(cookieStore);
	// }
	// builder.setSSLSocketFactory(SSLUtils.TRUAT_ALL_SSLSF);
	// return builder.build();
	// }

	public static CloseableHttpClient getHttpClient(boolean trustAllSSL, CookieStore cookieStore, boolean isFiddler) {

		if (isFiddler) {
			userFiddler = true;
		}
		HttpClientBuilder builder = getBuilder();
		if (cookieStore != null) {
			builder.setDefaultCookieStore(cookieStore);
		}
		if (trustAllSSL) {
			builder.setSSLSocketFactory(SSLUtils.TRUAT_ALL_SSLSF);
		}
		return builder.build();
	}

	/**
	 * 获取socks5代理
	 *
	 * @param cookieStore
	 * @param socks5Proxy
	 * @return
	 * @author wangyepeng
	 */
	public static CloseableHttpClient getHttpClient(CookieStore cookieStore, InetSocketAddress socks5Proxy) {

		HttpClientBuilder builder = getBuilder(socks5Proxy);
		if (cookieStore != null) {
			builder.setDefaultCookieStore(cookieStore);
		}
		return builder.build();
	}

	public static CloseableHttpClient getHttpClient(CookieStore cookieStore, String s5_ip, String s5_port, boolean isHttpSameAsHttps) {

		return getHttpClient(cookieStore, new InetSocketAddress(s5_ip, Integer.valueOf(s5_port)), isHttpSameAsHttps);
	}

	public static CloseableHttpClient getHttpClient(CookieStore cookieStore, InetSocketAddress socks5Proxy, boolean isHttpSameAsHttps) {

		HttpClientBuilder builder = getBuilder(socks5Proxy, isHttpSameAsHttps);
		if (cookieStore != null) {
			builder.setDefaultCookieStore(cookieStore);
		}
		return builder.build();
	}

	/**
	 * 获取socks5代理
	 *
	 * @param cookieStore
	 * @param socks5Proxy
	 * @return
	 * @author wangyepeng
	 */
	public static CloseableHttpClient getHttpClientWithSocksProxy(CookieStore cookieStore) {

		CloseableHttpClient result = null;
		String proxy = ProxyUtils.getSocksProxyHost();
		if (StringUtils.isNotBlank(proxy)) {
			LOG.info("use socks5 proxy " + proxy);
			String[] arr = proxy.split(":");
			if (arr.length == 2) {
				result = HttpUtils.getHttpClient(cookieStore, new InetSocketAddress(arr[0].trim(), Integer.parseInt(arr[1].trim())));
			}
		}

		if (result == null) {
			result = HttpUtils.getHttpClient(cookieStore, null);
		}
		return result;
	}

	/**
	 * 传入socket代理
	 * 
	 * @param cookieStore
	 * @param proxy
	 * @param isHttpSameAsHttps
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static CloseableHttpClient getHttpClientWithSocksProxy(CookieStore cookieStore, String proxy, boolean isHttpSameAsHttps) throws ClientProtocolException, IOException {

		JSONObject ipjob = JSON.parseObject(proxy);
		CloseableHttpClient result = null;
		Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory> create().register("http", new PlainConnectionSocketFactory()).register("https", new MySSLConnectionSocketFactory(SSLUtils.gettrustAllSSLContext(), new InetSocketAddress(ipjob.getString("ip"), ipjob.getInteger("port")))).build();
		// HTTP客户端连接管理池
		PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(reg);
		result = HttpClients.custom().setConnectionManager(connManager).build();
		HttpGet request = new HttpGet("https://uac.10010.com/portal/mallLogin.jsp?redirectURL=http://www.10010.com");
		CloseableHttpResponse response = result.execute(request);
		try {
			HttpEntity entity = response.getEntity();
			System.out.println("----------------------------------------");
			System.out.println("返回响应：" + response.getStatusLine());
			System.out.println("响应内容：" + EntityUtils.toString(entity, "UTF-8"));
			System.out.println("----------------------------------------");
		} finally {
			response.close();
		}
		return result;
	}

	public static CloseableHttpClient getHttpClientWithSocksProxy(CookieStore cookieStore, boolean isHttpSameAsHttps) {

		CloseableHttpClient result = null;
		String proxy = ProxyUtils.getSocksProxyHost();
		if (StringUtils.isNotBlank(proxy)) {
			LOG.info("use socks5 proxy " + proxy);
			String[] arr = proxy.split(":");
			if (arr.length == 2) {
				result = HttpUtils.getHttpClient(cookieStore, new InetSocketAddress(arr[0].trim(), Integer.parseInt(arr[1].trim())), isHttpSameAsHttps);
			}
		}

		if (result == null) {
			result = HttpUtils.getHttpClient(cookieStore, null, isHttpSameAsHttps);
		}
		return result;
	}

	/**
	 * 获取socks5代理
	 *
	 * @param cookieStore
	 * @param socks5Proxy
	 * @return
	 * @author wangyepeng
	 */
	public static CloseableHttpClient getHttpClientWithSocksProxy(SSLContext ssLContext, CookieStore cookieStore) {

		try {
			String proxy = ProxyUtils.getSocksProxyHost();
			if (StringUtils.isNotBlank(proxy)) {
				LOG.info("use socks5 proxy " + proxy);
				String[] arr = proxy.split(":");
				if (arr.length == 2) {
					return getHttpClientWithSocksProxy(ssLContext, cookieStore, new InetSocketAddress(arr[0].trim(), Integer.parseInt(arr[1].trim())));
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return getHttpClientWithSocksProxy(ssLContext, cookieStore, null);
	}

	/**
	 * 使用指定的sslcontext和socks proxy创建httpclient
	 *
	 * @param ssLContext
	 * @param cookieStore
	 * @param proxy
	 * @return
	 * @author wangyepeng
	 */
	public static CloseableHttpClient getHttpClientWithSocksProxy(SSLContext ssLContext, CookieStore cookieStore, InetSocketAddress proxy) {

		HttpClientBuilder builder = null;
		if (proxy != null) {
			LOG.info("use socks5 proxy " + proxy.toString());
			builder = getBuilder(new MySSLConnectionSocketFactory(ssLContext, proxy));
		} else {
			builder = getBuilder(new MySSLConnectionSocketFactory(ssLContext));
		}
		if (cookieStore != null) {
			builder.setDefaultCookieStore(cookieStore);
		}
		return builder.build();
	}

	/**
	 * @param socks5Proxy
	 *            使用socks5代理创建httpclient
	 * @return
	 * @author wangyepeng
	 */
	private static HttpClientBuilder getBuilder(InetSocketAddress socks5Proxy) {

		return getBuilder(new MySSLConnectionSocketFactory(SSLUtils.gettrustAllSSLContext(), socks5Proxy));
	}

	private static HttpClientBuilder getBuilder(InetSocketAddress socks5Proxy, boolean isHttpSameAsHttps) {

		return getBuilder(new MySSLConnectionSocketFactory(SSLUtils.gettrustAllSSLContext(), socks5Proxy), isHttpSameAsHttps);
	}

	private static HttpClientBuilder getBuilder() {

		return getBuilder(SSLUtils.TRUAT_ALL_SSLSF);
	}

	private static HttpClientBuilder getBuilder(LayeredConnectionSocketFactory sslSocketFactory) {

		return getBuilder(sslSocketFactory, true);
	}

	/**
	 * @param sslSocketFactory
	 * @param isHttpSameAsHttps
	 *            https使用代理时,http是否使用相同的代理发起请求
	 * @return
	 * @author wangyepeng
	 */
	private static HttpClientBuilder getBuilder(LayeredConnectionSocketFactory sslSocketFactory, boolean isHttpSameAsHttps) {

		HttpClientBuilder builder = HttpClients.custom();
		if (userFiddler || Boolean.valueOf(System.getProperty("use.fiddler", "false"))) {
			builder.setProxy(PROXY_FIDDLER);
		}
		// builder.setMaxConnPerRoute(defaultMaxPerRoute);
		// builder.setMaxConnTotal(defaultMaxPerRoute * 2);
		// builder.disableAutomaticRetries();// 禁用retry
		builder.setRetryHandler(new DefaultHttpRequestRetryHandler(1, false));// 重试一次

		// X509HostnameVerifier hostnameVerifier =
		// SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;
		// LayeredConnectionSocketFactory sslSocketFactory = new
		// SSLConnectionSocketFactory(SSLContexts.createDefault(),hostnameVerifier);
		RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.<ConnectionSocketFactory> create().register("https", sslSocketFactory);
		// 联通的http不能使用代理
		if (!isHttpSameAsHttps) {
			registryBuilder.register("http", PlainConnectionSocketFactory.getSocketFactory());
		} else {
			if (sslSocketFactory != null && sslSocketFactory instanceof MySSLConnectionSocketFactory) {
				InetSocketAddress proxy = ((MySSLConnectionSocketFactory) sslSocketFactory).getProxy();
				LOG.info("MyPlainConnectionSocketFactory " + proxy);
				registryBuilder.register("http", new MyPlainConnectionSocketFactory(proxy));
			} else {
				registryBuilder.register("http", PlainConnectionSocketFactory.getSocketFactory());
			}
		}
		final PoolingHttpClientConnectionManager poolingmgr = new PoolingHttpClientConnectionManager(registryBuilder.build());
		poolingmgr.closeIdleConnections(0, TimeUnit.SECONDS);
		poolingmgr.setDefaultMaxPerRoute(defaultMaxPerRoute);
		poolingmgr.setMaxTotal(defaultMaxPerRoute * 4);
		RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
		// 2min socket read
		requestConfigBuilder.setSocketTimeout(timeout * 2);
		// 1min connect to a url
		requestConfigBuilder.setConnectTimeout(timeout);
		// 30s get a connection from pool
		requestConfigBuilder.setConnectionRequestTimeout(timeout / 2);
		requestConfigBuilder.setCookieSpec("compatibilityEx");
		// requestConfigBuilder.setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY);
		builder.setDefaultRequestConfig(requestConfigBuilder.build());
		builder.setConnectionManager(poolingmgr);
		// 链接保持策略
		ConnectionKeepAliveStrategy myStrategy = new ConnectionKeepAliveStrategy() {

			public long getKeepAliveDuration(HttpResponse response, HttpContext context) {

				// Honor 'keep-alive' header
				HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
				while (it.hasNext()) {
					HeaderElement he = it.nextElement();
					String param = he.getName();
					String value = he.getValue();
					if (value != null && param.equalsIgnoreCase("timeout")) {
						try {
							return Long.parseLong(value) * 1000;
						} catch (NumberFormatException ignore) {
						}
					}
				}
				// keep alive for 120 seconds
				return 120 * 1000;
			}
		};
		builder.setKeepAliveStrategy(myStrategy);

		builder.setDefaultCookieSpecRegistry(createCookieSpecRegistry());
		return builder;
	}

	private static HttpClientBuilder getProxyBuilder(String ip, int port) {

		HttpClientBuilder builder = getBuilder();
		// HttpClientBuilder builder = HttpClients.custom();
		HttpHost proxy = new HttpHost(ip, port);
		builder.setProxy(proxy);
		return builder;
	}

	public static void executeGet(CloseableHttpClient client, String url) throws ClientProtocolException, IOException {

		HttpGet get = HttpUtils.get(url);
		client.execute(get).close();
	}

	public static String executeGetWithResult(CloseableHttpClient client, String url) throws ClientProtocolException, IOException {

		HttpGet get = get(url);
		CloseableHttpResponse resp = client.execute(get);
		String result = EntityUtils.toString(resp.getEntity());
		resp.close();
		return result;
	}

	public static String executeGetWithResult(CloseableHttpClient client, String url, String encoding) throws ClientProtocolException, IOException {

		HttpGet get = get(url);
		CloseableHttpResponse resp = client.execute(get);
		String result = EntityUtils.toString(resp.getEntity(), encoding);
		resp.close();
		return result;
	}

	public static String executeGetWithResult(CloseableHttpClient client, HttpGet get) throws ClientProtocolException, IOException {

		CloseableHttpResponse resp = client.execute(get);
		String result = EntityUtils.toString(resp.getEntity(), "UTF-8");
		resp.close();
		return result;
	}

	public static String executeGetWithResult(CloseableHttpClient client, HttpGet get, String encoding) throws ClientProtocolException, IOException {

		CloseableHttpResponse resp = client.execute(get);
		String result = EntityUtils.toString(resp.getEntity(), encoding);
		resp.close();
		return result;
	}

	public static void executePost(CloseableHttpClient client, String url) throws ClientProtocolException, IOException {

		executePost(client, url, null);
	}

	public static String executePostWithResult(CloseableHttpClient client, HttpPost post) throws ClientProtocolException, IOException {

		CloseableHttpResponse resp = client.execute(post);
		String result = EntityUtils.toString(resp.getEntity());
		resp.close();
		return result;
	}

	public static Map<String, Object> executePostWithHead(CloseableHttpClient client, HttpPost post) throws ClientProtocolException, IOException {

		Map<String, Object> map = new HashMap<String, Object>();
		CloseableHttpResponse resp = client.execute(post);
		String result = EntityUtils.toString(resp.getEntity());
		map.put("result", result);
		map.put("url", getLocationFromHeader(resp));
		resp.close();
		return map;
	}

	public static String executePostWithResult(CloseableHttpClient client, HttpPost post, String encoding) throws ClientProtocolException, IOException {

		CloseableHttpResponse resp = client.execute(post);
		String result = EntityUtils.toString(resp.getEntity(), encoding);
		resp.close();
		return result;
	}

	public static String executePostWithResult(CloseableHttpClient client, String url, Map<String, Object> params) throws ClientProtocolException, IOException {

		return executePostWithResult(client, url, params, HttpUtils.UTF_8);
	}

	public static String executePostWithResult(CloseableHttpClient client, String url, Map<String, Object> params, String charset) throws ClientProtocolException, IOException {

		HttpPost post = params == null ? post(url) : post(url, params);
		CloseableHttpResponse resp = client.execute(post);
		String result = EntityUtils.toString(resp.getEntity(), charset);
		resp.close();
		return result;
	}

	public static String excuteResultPost(CloseableHttpClient client, HttpPost post, Object json) throws ClientProtocolException, IOException {

		StringEntity entity = new StringEntity(json.toString(), "utf-8");
		post.setEntity(entity);
		CloseableHttpResponse resp = client.execute(post);
		String result = EntityUtils.toString(resp.getEntity(), "UTF-8");
		resp.close();
		return result;
	}

	public static void executePost(CloseableHttpClient client, String url, Map<String, Object> params) throws ClientProtocolException, IOException {

		HttpPost post = params == null ? post(url) : post(url, params);
		client.execute(post).close();
	}

	public static String getFirstCookie(CookieStore cookieStore, String name) {

		List<String> values = getCookie(cookieStore, name);
		return values.isEmpty() ? null : values.get(0);
	}

	public static List<String> getCookie(CookieStore cookieStore, String name) {

		List<String> result = new ArrayList<String>();
		if (cookieStore == null) {
			return result;
		}
		for (Cookie cookie : cookieStore.getCookies()) {
			if (name.equals(cookie.getName())) {
				result.add(cookie.getValue());
			}
		}
		return result;
	}

	public static void printCookies(CookieStore cookieStore) {

		for (Cookie cookie : cookieStore.getCookies()) {
			System.out.println(cookie.toString());
		}
	}

	/**
	 * 保存cookies
	 *
	 * @param cookieStore
	 * @return
	 */
	// public String saveCookies(CookieStore cookieStore) {
	// List<Cookie> cookies = cookieStore.getCookies();
	// JSONArray cookiesJson = new JSONArray();
	// for (int i = 0; i < cookies.size(); i++) {
	// cookiesJson.add(JSON.toJSONString(cookies.get(i)));
	// }
	// // 保存300秒
	// RedisUtils.set(userid + token, cookiesJson.toJSONString(), 60 * 60 * 48);
	// try {
	// String proxy = RedisUtils.get(userid + "socket");
	// JSONObject job = JSON.parseObject(proxy);
	// String key = job.getString("ip") + job.getString("port") + "cookies";
	// RedisUtils.set(key, cookiesJson.toJSONString(), 60 * 60 * 24);
	// } catch (Exception e) {
	// logger.info(e.getMessage());
	// }
	// return cookiesJson.toJSONString();
	// }

	public static HttpPost buildPostFromHtml(String html) {

		return buildPostFromHtml(html, "form");
	}

	public static HttpPost buildPostFromHtml(String html, String selector) {

		return buildPostFromHtml(html, selector, HttpUtils.GBK);
	}

	public static HttpPost buildPostFromHtml(String html, String selector, String charSet) {

		Document document = Jsoup.parse(html, charSet == null ? HttpUtils.GBK : charSet);
		Elements elements = document.select(selector);
		if (elements.size() > 0) {
			Element form = elements.get(0);
			String url = form.attr("action");
			Elements inputs = form.select("input[type=hidden]");
			Map<String, Object> params = new HashMap<String, Object>();
			for (int i = 0; i < inputs.size(); i++) {
				params.put(inputs.get(i).attr("name"), inputs.get(i).attr("value"));
			}
			return HttpUtils.post(url, params);
		}
		return null;
	}

	public static Map<String, Object> getFormUrlAndParamsFromHtml(String html, String selector) {

		return getFormUrlAndParamsFromHtml(html, selector, HttpUtils.GBK);
	}

	public static Map<String, Object> getFormUrlAndParamsFromHtml(String html, String selector, String charSet) {

		Document document = Jsoup.parse(html, charSet == null ? HttpUtils.GBK : charSet);
		Elements elements = document.select(selector);
		if (elements.size() > 0) {
			Element form = elements.get(0);
			String url = form.attr("action");
			Elements inputs = form.select("input[type=hidden]");
			Map<String, Object> params = new HashMap<String, Object>();
			for (int i = 0; i < inputs.size(); i++) {
				params.put(inputs.get(i).attr("name"), inputs.get(i).attr("value"));
			}
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("url", url);
			result.put("params", params);
			return result;
		}
		return null;
	}

	/*
	 * 获取cookies
	 */
	public static CookieStore getCookies(String key) {

		CookieStore cookieStore = new BasicCookieStore();
		String clientStr = JedisUtils.get(key);
		// String clientStr = RedisUtils.get(userid + token);// http代理等信息初始化httpClient
		if (StringUtils.isNotBlank(clientStr)) {
			// System.out.println(clientStr);
			JSONArray object = JSON.parseArray(clientStr);
			for (int i = 0; i < object.size(); i++) {
				JSONObject s = (JSONObject) JSONObject.parse(object.get(i).toString());
				BasicClientCookie basicCookie = new BasicClientCookie(s.get("name").toString(), s.get("value").toString());
				basicCookie.setDomain(s.getString("domain"));
				basicCookie.setPath(s.getString("path"));
				cookieStore.addCookie(basicCookie);
			}
		}
		return cookieStore;
	}

	// /**
	// * 保存cookies
	// *
	// * @param cookieStore
	// * @return
	// */
	// public String saveCookies(CookieStore cookieStore) {
	// List<Cookie> cookies = cookieStore.getCookies();
	// JSONArray cookiesJson = new JSONArray();
	// for (int i = 0; i < cookies.size(); i++) {
	// cookiesJson.add(JSON.toJSONString(cookies.get(i)));
	// }
	// // 保存300秒
	// RedisUtils.set(userid + token, cookiesJson.toJSONString(), 60 * 60 * 48);
	// try {
	// String proxy = RedisUtils.get(userid + "socket");
	// JSONObject job = JSON.parseObject(proxy);
	// String key = job.getString("ip") + job.getString("port") + "cookies";
	// RedisUtils.set(key, cookiesJson.toJSONString(), 60 * 60 * 24);
	// } catch (Exception e) {
	// logger.info(e.getMessage());
	// }
	// return cookiesJson.toJSONString();
	// }

	/**
	 * 获取input[type=hidden]
	 *
	 * @param html
	 * @return
	 * @author wangyepeng
	 */
	public static Map<String, Object> buildHiddenInputParamsFromHtml(String html) {

		return buildHiddenInputParamsFromHtml(html, HttpUtils.GBK);
	}

	/**
	 * 获取input[type=hidden]
	 *
	 * @param html
	 * @param charSet
	 * @return
	 * @author wangyepeng
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> buildHiddenInputParamsFromHtml(String html, String charSet) {

		Document document = Jsoup.parse(html, charSet == null ? HttpUtils.GBK : charSet);
		Elements inputs = document.select("input[type=hidden]");
		Map<String, Object> params = new HashMap<String, Object>();
		for (int i = 0; i < inputs.size(); i++) {
			String name = inputs.get(i).attr("name");
			String value = inputs.get(i).attr("value");
			if (params.get(name) != null) {
				Object v = params.get(name);
				if (v instanceof List) {
					((List<Object>) v).add(value);
				} else {
					List<Object> l = new ArrayList<Object>();
					l.add(v);
					l.add(value);
					params.put(name, l);
				}
			} else {
				params.put(name, value);
			}
		}
		return params;
	}

	/**
	 * 获取input[type=hidden]
	 *
	 * @param html
	 * @param charSet
	 * @return
	 * @author wangyepeng
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> buildHiddenInputParamsFromHtmlById(String html, String charSet) {

		Document document = Jsoup.parse(html, charSet == null ? HttpUtils.GBK : charSet);
		Elements inputs = document.select("input[type=hidden]");
		Map<String, Object> params = new HashMap<String, Object>();
		for (int i = 0; i < inputs.size(); i++) {
			String name = inputs.get(i).attr("id");
			String value = inputs.get(i).attr("value");
			if (params.get(name) != null) {
				Object v = params.get(name);
				if (v instanceof List) {
					((List<Object>) v).add(value);
				} else {
					List<Object> l = new ArrayList<Object>();
					l.add(v);
					l.add(value);
					params.put(name, l);
				}
			} else {
				params.put(name, value);
			}
		}
		return params;
	}

	public static Map<String, Object> buildParamsFromHtml(String html, String selector) {

		return buildParamsFromHtml(html, selector, HttpUtils.GBK);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> buildParamsFromHtml(String html, String selector, String charSet) {

		Document document = Jsoup.parse(html, charSet == null ? HttpUtils.GBK : charSet);
		Elements elements = document.select(selector);
		if (elements.size() > 0) {
			Element form = elements.get(0);
			Elements inputs = form.select("input[type=hidden]");
			Map<String, Object> params = new HashMap<String, Object>();
			for (int i = 0; i < inputs.size(); i++) {
				String name = inputs.get(i).attr("name");
				String value = inputs.get(i).attr("value");
				if (params.get(name) != null) {
					Object v = params.get(name);
					if (v instanceof List) {
						((List<Object>) v).add(value);
					} else {
						List<Object> l = new ArrayList<Object>();
						l.add(v);
						l.add(value);
						params.put(name, l);
					}
				} else {
					params.put(name, value);
				}
			}
			return params;
		}
		return new HashMap<String, Object>();
	}

	public static String getCharsetFromContentType(String contentType) {

		if (StringUtils.isBlank(contentType)) {
			return null;
		}
		String[] cts = contentType.toLowerCase().split(";");
		for (String s : cts) {
			if (StringUtils.isNotBlank(s) && s.contains("charset")) {
				return s.split("=")[1];
			}
		}
		return null;
	}

	/**
	 * 去掉url中的路径，留下请求参数部分
	 *
	 * @param strURL
	 *            url地址
	 * @return url请求参数部分
	 */
	@SuppressWarnings("unused")
	private static String TruncateUrlPage(String strURL) throws URISyntaxException {

		URI uri = new URI(strURL);
		return uri.getRawQuery();
	}

	/**
	 * 解析出url参数中的键值对 如 "index.jsp?Action=del&id=123"，解析出Action:del,id:123存入map中
	 *
	 * @param URL
	 *            url地址
	 * @return url请求参数部分
	 */
	public static Map<String, String> URLRequest(String URL) {

		Map<String, String> paramsMap = new HashMap<String, String>();
		try {
			URI url = new URI(URL);
			List<NameValuePair> list = URLEncodedUtils.parse(url.getRawQuery(), Charset.forName("UTF-8"));
			for (NameValuePair nameValue : list) {
				paramsMap.put(nameValue.getName(), nameValue.getValue());
			}
		} catch (Exception e) {

		}
		return paramsMap;
	}

	/**
	 * @param response
	 * @param name
	 * @param encode
	 * @return
	 * @author wangyepeng
	 */
	public static String getHeader(CloseableHttpResponse response, String name) {

		Header[] headers = response.getHeaders(name);
		if (headers.length > 0) {
			return headers[0].getValue();
		}
		return null;
	}

	/**
	 * 从header里获取Location
	 *
	 * @param response
	 * @return
	 * @author wangyepeng
	 */
	public static String getLocationFromHeader(CloseableHttpResponse response) {

		return getLocationFromHeader(response, false);
	}

	/**
	 * @param name
	 * @param value
	 * @param path
	 * @param domain
	 * @return
	 * @author wangyepeng
	 */
	public static BasicClientCookie getCookie(String name, String value, String domain, String path) {

		BasicClientCookie clientCookie = new BasicClientCookie(name, value);
		clientCookie.setDomain(domain);
		clientCookie.setPath(path);
		return clientCookie;
	}

	public static String getLocationFromHeader(CloseableHttpResponse response, boolean closeResponse) {

		String result = getHeader(response, "Location");
		if (closeResponse) {
			try {
				response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public static String getLocationFromHeader(CloseableHttpClient client, String url) {

		return getLocationFromHeader(client, url, null, false);
	}

	public static String getLocationFromHeader(CloseableHttpClient client, String url, Map<String, Object> params) {

		return getLocationFromHeader(client, url, params, false);
	}

	public static String getLocationFromHeader(CloseableHttpClient client, String url, Map<String, Object> params, boolean isPost) {

		CloseableHttpResponse response;
		try {
			HttpPost request = null;
			if (isPost) {
				request = post(url, params);
				response = client.execute(request);

			} else {
				HttpGet get = get(url);
				response = client.execute(get);
			}
			return getLocationFromHeader(response, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String unicodeToString(String s) {

		Matcher m = RE_UNICODE.matcher(s);
		StringBuffer sb = new StringBuffer(s.length());
		while (m.find()) {
			m.appendReplacement(sb, Character.toString((char) Integer.parseInt(m.group(1), 16)));
		}
		m.appendTail(sb);
		return sb.toString();
	}

	public static File getCaptchaCodeImage(CloseableHttpClient client, String url) {

		return getCaptchaCodeImage(client, HttpUtils.get(url));
	}

	public static File getCaptchaCodeImage(CloseableHttpClient client, HttpGet get) {

		LOG.info("验证码图片url:" + get.getURI().toString());
		try {
			CloseableHttpResponse response = client.execute(get);
			// 生产环境-图片存放目录.
			String path = "/data/upload/codeImages/";
			// 测试环境-图片存放目录.
			// String path = "D:/codeImages/";
			File codeFile = new File(path, "now.jpg");
			FileUtils.copyInputStreamToFile(response.getEntity().getContent(), codeFile);
			response.close();
			LOG.info("获取验证码成功,codeFile.length:" + codeFile.length());
			return codeFile;
		} catch (Exception e) {
			LOG.error("获取验证码图片失败", e);
		}
		return null;
	}

	/**
	 * 聚信立用post请求
	 * 
	 * @param url
	 * @param jsonStrData
	 * @return
	 * @throws IOException
	 */
	public static JSONObject postJsonData(String url, String jsonStrData) {

		LOG.info("# POST JSON 请求URL 为" + url);
		LOG.info("# POST JSON 数据为" + jsonStrData);
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		// RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
		// // 2min socket read
		// requestConfigBuilder.setSocketTimeout(timeout * 2);
		// // 1min connect to a url
		// requestConfigBuilder.setConnectTimeout(timeout);
		// // 30s get a connection from pool
		// requestConfigBuilder.setConnectionRequestTimeout(timeout / 2);
		// httpClientBuilder.setDefaultRequestConfig(requestConfigBuilder.build());
		HttpPost post = new HttpPost(url);
		JSONObject jsonObject = new JSONObject();
		CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
		try {
			// HttpEntity entity = new StringEntity(jsonStrData);
			// 修复 POST json 导致中文乱码
			HttpEntity entity = new StringEntity(jsonStrData, "UTF-8");
			post.setEntity(entity);
			post.setHeader("Content-type", "application/json");
			HttpResponse resp = closeableHttpClient.execute(post);
			jsonObject = convertResponseBytes2JsonObj(resp);
		} catch (IOException e) {
			LOG.error("异常90000" + e.getMessage());
			jsonObject.put("error", "异常90000" + e.getMessage());
		} finally {
			try {
				if (closeableHttpClient != null) {
					closeableHttpClient.close();
				}
			} catch (IOException e) {
				LOG.info(e.getMessage(), e);
			}
		}
		String html = jsonObject.toString();
		LOG.info("请求结果:" + (html.length() > 100 ? html.substring(0, 98) : html));
		return jsonObject;
	}

	/**
	 * 测试代理
	 *
	 * @param url
	 *            地址
	 * @throws IOException
	 */
	public static boolean testProxy(HttpGet get, JSONObject ipjob) {

		JSONObject jsonObject = new JSONObject();
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		HttpHost proxy = new HttpHost(ipjob.getString("ip"), ipjob.getInteger("port"));
		httpClientBuilder.setProxy(proxy);
		RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
		requestConfigBuilder.setSocketTimeout(2000);
		requestConfigBuilder.setConnectTimeout(2000);
		requestConfigBuilder.setConnectionRequestTimeout(2000);
		httpClientBuilder.setDefaultRequestConfig(requestConfigBuilder.build());
		CloseableHttpClient closeableHttpClient = null;
		try {
			closeableHttpClient = httpClientBuilder.build();
			HttpResponse resp = closeableHttpClient.execute(get);
			String httpStr = EntityUtils.toString(resp.getEntity(), "utf-8");
			if (!httpStr.contains("html")) {
				LOG.info(ipjob + "代理不可用");
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (closeableHttpClient != null) {
					closeableHttpClient.close();
				}
			} catch (IOException e) {
				LOG.info(e.getMessage(), e);
			}
		}
		return true;
	}

	/**
	 * 测试代理
	 *
	 * @param url
	 *            地址
	 * @throws IOException
	 */
	public static boolean testProxy(JSONObject ipjob) {

		boolean flg = false;
		ArrayList<String> list = new ArrayList<>();
		list.add("https://uac.10010.com/portal/mallLogin.jsp?redirectURL=http://www.10010.com");
		list.add("https://bj.ac.10086.cn/login");
		list.add("http://login.189.cn/web/login");
		ArrayList<String> listName = new ArrayList<>();
		listName.add("中国联通");
		listName.add("北京移动");
		listName.add("北京电信");
		for (int i = 0; i < list.size(); i++) {
			HttpGet get = new HttpGet(list.get(i));
			boolean okFlg = testProxy(get, ipjob);
			if (okFlg) {
				JSONArray exclude = new JSONArray();
				if (ipjob.containsKey("exclude")) {
					exclude = ipjob.getJSONArray("exclude");
					exclude.remove("中国联通");
					ipjob.put("exclude", exclude);
				}
				if (i == 0) {
					flg = true;
				}
			} else {
				JSONArray exclude = new JSONArray();
				if (ipjob.containsKey("exclude")) {
					exclude = ipjob.getJSONArray("exclude");
				}
				if (!exclude.contains(listName.get(i))) {
					exclude.add(listName.get(i));
				}
				ipjob.put("exclude", exclude);
			}
		}
		return flg;
	}

	/**
	 * 聚信立用发送 get 请求
	 *
	 * @param url
	 *            地址
	 * @throws IOException
	 */
	public static JSONObject getJsonResponse(String url) {

		JSONObject jsonObject = new JSONObject();
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		HttpGet get = new HttpGet(url);
		CloseableHttpClient closeableHttpClient = null;
		try {
			closeableHttpClient = httpClientBuilder.build();
			HttpResponse resp = closeableHttpClient.execute(get);
			jsonObject = convertResponseBytes2JsonObj(resp);
		} catch (IOException e) {
			LOG.error("异常90000" + e.getMessage());
			jsonObject.put("error", "异常90000" + e.getMessage());
		} finally {
			try {
				if (closeableHttpClient != null) {
					closeableHttpClient.close();
				}
			} catch (IOException e) {
				LOG.info(e.getMessage(), e);
			}
		}
		String html = jsonObject.toJSONString();
		LOG.info(url + ",结果:" + (html.length() > 1000 ? html.substring(0, 998) : html));
		return jsonObject;
	}

	/**
	 * Header 为 application/json POST 请求数据
	 *
	 * @param resp
	 */
	private static JSONObject convertResponseBytes2JsonObj(HttpResponse resp) {

		JSONObject jsonObject = null;
		try {
			InputStream respIs = resp.getEntity().getContent();
			byte[] respBytes = IOUtils.toByteArray(respIs);
			String result = new String(respBytes, Charset.forName("UTF-8"));
			if (null == result || result.length() == 0) {
				LOG.info("无响应");
			} else {
				if (result.startsWith("{") && result.endsWith("}")) {
					jsonObject = JSON.parseObject(result);
				} else {
					LOG.info("不能转成JSON对象");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonObject;
	}

	/**
	 * 转换CookieStore
	 * 
	 * @author 王业鹏
	 * @param cookie
	 * @return 没有cookie返回一个新的,有的话返回设置后的
	 */
	public static CookieStore processCookies(String cookie) {

		CookieStore cookieStore = new BasicCookieStore();
		if (StringUtils.isBlank(cookie)) {
			return cookieStore;
		}
		try {
			JSONArray object = JSON.parseArray(cookie);
			for (int i = 0; i < object.size(); i++) {
				JSONObject s = object.getJSONObject(i);
				BasicClientCookie basicCookie = new BasicClientCookie(s.get("name").toString(), s.get("value").toString());
				basicCookie.setDomain(s.get("domain").toString());
				basicCookie.setPath(s.get("path").toString());
				cookieStore.addCookie(basicCookie);
			}
		} catch (Exception e1) {
			LOG.info(e1.getMessage());
		}
		HttpUtils.printCookies(cookieStore);
		return cookieStore;
	}

	/**
	 * url重复进行重定向
	 * 
	 * @param client
	 * @param url
	 * @return response
	 */
	public static String redirects(CloseableHttpClient client, String url) {

		LOG.info(url + "输入的网址是： " + url);
		String result = null;
		try {
			int num = 0;
			while (true) {
				LOG.info("第" + ++num + "次:" + url);

				HttpGet get = HttpUtils.get(url);
				get.setHeader("Host", URI.create(url).getHost());
				get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36");
				CloseableHttpResponse response1 = client.execute(get);
				int statusCode = response1.getStatusLine().getStatusCode();

				if (statusCode == 302 || statusCode == 301) {

					url = HttpUtils.getLocationFromHeader(response1, true);

				} else {
					return EntityUtils.toString(response1.getEntity(), "utf-8");
				}

			}
		} catch (Exception e) {
			LOG.info(url + e.getMessage(), e);
		}
		return result;
	}

	/**
	 * 提交申请表单接口测试
	 * 
	 * @throws IOException
	 */
	public static String getPhoneInfo(String phone) {

		try {
			String url = "http://10.10.51.180:17301/openapi/phone/getTelInfo";
			HttpPost get = HttpUtils.post(url);
			get.setEntity(new StringEntity("[{\"tel\":\"" + phone + "\"}]"));
			String phoneInfo = HttpUtils.executePostWithResult(getHttpClient(), get);
			JSONObject phoneJob = JSON.parseObject(phoneInfo);
			if (phoneJob.getBoolean("success")) {
				JSONArray result = phoneJob.getJSONArray("phone");
				JSONObject job = result.getJSONObject(0);
				return job.getString("province") + job.getString("company");
			} else {
				return getPhoneInfo(phone);
			}
		} catch (Exception e) {
			return getPhoneInfo(phone);
		}
	}

	/**
	 * 聚信立用post请求
	 * 
	 * @param url
	 * @param jsonStrData
	 * @return
	 * @throws IOException
	 */
	public static String postJsonData2(String url, String jsonStrData) {

		LOG.info("# POST JSON 请求URL 为" + url);
		LOG.info("# POST JSON 数据为" + jsonStrData);
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		HttpPost post = new HttpPost(url);
		CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
		String httpStr = "";
		try {
			// HttpEntity entity = new StringEntity(jsonStrData);
			// 修复 POST json 导致中文乱码
			HttpEntity entity = new StringEntity(jsonStrData, "UTF-8");
			post.setEntity(entity);
			post.setHeader("Content-type", "application/json");
			HttpResponse resp = closeableHttpClient.execute(post);
			httpStr = EntityUtils.toString(resp.getEntity(), "utf-8");
		} catch (IOException e) {
			LOG.info("异常90000" + e.getMessage());
		} finally {
			try {
				if (closeableHttpClient != null) {
					closeableHttpClient.close();
				}
			} catch (IOException e) {
				LOG.info(e.getMessage(), e);
			}
		}
		return httpStr;
	}

	public static HttpClientBuilder makeBuilder() {

		return getBuilder(SSLUtils.TRUAT_ALL_SSLSF);
	}

	public static Registry<CookieSpecProvider> createCookieSpecRegistry() {

		RegistryBuilder<CookieSpecProvider> registry = RegistryBuilder.<CookieSpecProvider> create().register("compatibilityEx", new BrowserCompatSpecExFactory());
		registry.register(CookieSpecs.BEST_MATCH, new BestMatchSpecFactory());
		registry.register(CookieSpecs.BROWSER_COMPATIBILITY, new BrowserCompatSpecFactory());
		registry.register(CookieSpecs.NETSCAPE, new NetscapeDraftSpecFactory());
		registry.register(CookiePolicy.RFC_2109, new RFC2109SpecFactory());
		registry.register(CookiePolicy.RFC_2965, new RFC2965SpecFactory());
		registry.register(CookieSpecs.IGNORE_COOKIES, new IgnoreSpecFactory());
		return registry.build();
	}
}
