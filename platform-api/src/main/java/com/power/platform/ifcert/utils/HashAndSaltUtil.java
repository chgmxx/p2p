package com.power.platform.ifcert.utils;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.cert.open.CertException;
import org.cert.open.CertToolV1;

/**
 * 
 * class: HashAndSaltUtil <br>
 * description: 敏感数据Hash值与Salt值的获取. <br>
 * author: Roy <br>
 * date: 2019年4月29日 上午10:26:14
 */
public class HashAndSaltUtil {

	public static CertToolV1 tool = new CertToolV1();

	/**
	 * 
	 * methods: getPhoneHashAndSalt <br>
	 * description: 获取手机号码的Hash值和Salt值. <br>
	 * author: Roy <br>
	 * date: 2019年4月29日 上午10:26:50
	 * 
	 * @param phoneNum
	 * @return
	 * @throws CertException
	 */
	public static Map<String, String> getPhoneHashAndSalt(String phoneNum) throws CertException {

		Map<String, String> map = new HashMap<String, String>();
		JSONObject json = tool.phoneHash(phoneNum);
		String userPhoneHash = json.getString("phone");
		map.put("userPhoneHash", userPhoneHash);
		String userUuid = json.getString("salt");
		map.put("userUuid", userUuid);
		return map;
	}
}
