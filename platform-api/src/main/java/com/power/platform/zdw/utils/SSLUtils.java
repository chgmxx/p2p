package com.power.platform.zdw.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.ssl.TrustStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wangyepeng
 * @Description:http ssl 认证握手
 * @time:2016年8月31日 上午11:34:31
 */
public class SSLUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(SSLUtils.class);
	/**
	 * 创建捂手连接
	 */
	public final static SSLConnectionSocketFactory TRUAT_ALL_SSLSF = new SSLConnectionSocketFactory(gettrustAllSSLContext(), null);

	private SSLUtils() {

	}

	/**
	 * @param file
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public static SSLConnectionSocketFactory createSSLConnectionSocketFactory(File file, String password) throws Exception {

		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
		FileInputStream instream = new FileInputStream(file);
		try {
			trustStore.load(instream, password.toCharArray());
		} finally {
			instream.close();
		}

		// Trust own CA and all self-signed certs
		SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(trustStore, new TrustSelfSignedStrategy()).build();
		// Allow TLSv1 protocol only
		return new SSLConnectionSocketFactory(sslcontext, new String[] { "TLSv1" }, null, SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
	}

	/**
	 * @param in
	 * @param password
	 * @return
	 */
	public static SSLConnectionSocketFactory createSSLConnectionSocketFactory(InputStream in, String password) {

		KeyStore trustStore = null;
		try {
			trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(in, password.toCharArray());
			// Trust own CA and all self-signed certs
			SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(trustStore, new TrustSelfSignedStrategy()).build();
			// Allow TLSv1 protocol only
			return new SSLConnectionSocketFactory(sslcontext, new String[] { "TLSv1" }, null, SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
		} catch (Exception e) {
			LOGGER.info(e.getMessage(), e);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				LOGGER.info(e.getMessage(), e);
			}
		}
		return null;
	}

	/***
	 * @param file
	 * @return
	 */
	public static SSLConnectionSocketFactory createSSLConnectionSocketFactory(String file) {

		InputStream in = null;
		KeyStore trustStore = null;
		try {
			trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			in = SSLUtils.class.getResourceAsStream(file);
			trustStore.load(in, "123456".toCharArray());
			// Trust own CA and all self-signed certs
			SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(trustStore, new TrustSelfSignedStrategy()).build();
			// Allow TLSv1 protocol only
			return new SSLConnectionSocketFactory(sslcontext, new String[] { "TLSv1" }, null, SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		return null;
	}

	/**
	 * @return
	 */
	public static SSLContext gettrustAllSSLContext() {

		try {
			return new SSLContextBuilder().loadTrustMaterial(null, new TrustAllStrategy()).build();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * @author wangyepeng
	 * @Description: 内部类
	 * @time:2016年8月31日 上午11:42:23
	 */
	public static class TrustAllStrategy implements TrustStrategy {

		// 信任所有
		public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {

			return true;
		}
	}
}
