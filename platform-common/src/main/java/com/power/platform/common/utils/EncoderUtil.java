package com.power.platform.common.utils;

import java.security.MessageDigest;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public abstract class EncoderUtil {

	public static final String KEY_SHA = "SHA";
	public static final String KEY_MD5 = "MD5";
	public static final String KEY_MAC = "HmacMD5";
	public static final String KEY_ENCODING = "UTF-8";

	protected static final Logger logger = Logger.getLogger(EncoderUtil.class);

	/**
	 * MAC算法可选以下多种算法 HmacMD5 HmacSHA1 HmacSHA256 HmacSHA384 HmacSHA512
	 */

	/*
	 * MD5加密，base64编码
	 */
	public static String encrypt(String key) {
		byte[] data = encryptMD5(key);
		return encodeBASE64(data);
	}

	/*
	 * 比较原始字符串与给定的MD5是否相等
	 */
	public static Boolean compare(String key, String md5) {
		return md5.equals(encrypt(key));
	}

	/**
	 * BASE64加密
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String encodeBASE64(String key) {
		if (key == null)
			return null;
		try {
			return Base64.encodeBase64String(key.getBytes(KEY_ENCODING));
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return null;
	}

	/**
	 * BASE64加密
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String encodeBASE64(byte[] key) {

		if (key == null)
			return null;

		try {
			return Base64.encodeBase64String(key);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return null;
	}

	/**
	 * BASE64解密
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] decodeBASE64(String key) {

		if (StringUtils.isEmpty(key))
			return null;

		try {
			return Base64.decodeBase64(key);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	/**
	 * MD5加密
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptMD5(String key) {
		if (StringUtils.isEmpty(key))
			return null;

		try {
			MessageDigest md5 = MessageDigest.getInstance(KEY_MD5);
			md5.reset();
			md5.update(key.getBytes(KEY_ENCODING));

			return md5.digest();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	/**
	 * MD5加密
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptMD5(byte[] key) {

		if (key == null)
			return null;

		try {
			MessageDigest md5 = MessageDigest.getInstance(KEY_MD5);
			md5.update(key);

			return md5.digest();

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return null;
	}

	/**
	 * SHA加密
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptSHA(byte[] key) {
		if (key == null)
			return null;

		try {
			MessageDigest sha = MessageDigest.getInstance(KEY_SHA);
			sha.update(key);

			return sha.digest();

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return null;
	}

	/**
	 * 初始化HMAC密钥
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String initMacKey() {
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_MAC);

			SecretKey secretKey = keyGenerator.generateKey();
			return encodeBASE64(secretKey.getEncoded());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return null;
	}

	/**
	 * HMAC加密
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptHMAC(byte[] data, String key) throws Exception {
		try {
			SecretKey secretKey = new SecretKeySpec(decodeBASE64(key), KEY_MAC);
			Mac mac = Mac.getInstance(secretKey.getAlgorithm());
			mac.init(secretKey);

			return mac.doFinal(data);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return null;
	}

	public static void main(String[] args) {
		System.out.println(EncoderUtil.encrypt("123"));
		//System.out.println(Encoder.encrypt("123456"));
	}
}