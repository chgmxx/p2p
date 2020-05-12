package com.power.platform.ifcert.utils.sha;

import org.cert.utils.SHA;

/**
 * 
 * class: ShaApiKey <br>
 * description: 数据接入针对平台APIKEY进行加密. <br>
 * author: Roy <br>
 * date: 2019年5月6日 下午4:53:53
 */
public class ShaApiKey {

	/**
	 * 
	 * methods: getApiKey <br>
	 * description: APIKEY加密. <br>
	 * author: Roy <br>
	 * date: 2019年5月6日 下午4:53:49
	 * 
	 * @param apiKey
	 *            平台APIKEY
	 * @param source_code
	 *            平台编号
	 * @param versionStr
	 *            接口版本号
	 * @param currentTime
	 *            时间戳
	 * @param nonce
	 *            随机数
	 * @return
	 */
	public static String getApiKey(String apiKey, String source_code, String versionStr, Long currentTime, String nonce) {

		double version_double = Double.valueOf(versionStr);
		int version = (int) (version_double * 100);
		String versionHex = "0x" + Integer.toHexString(version);
		StringBuilder s = new StringBuilder();
		s.append(source_code);
		s.append(versionHex);
		s.append(apiKey);
		s.append(currentTime);
		s.append(nonce);
		String key = SHA.SHA256(s.toString());
		return key;
	}
}
