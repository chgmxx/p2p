package com.power.platform.common.utils;

/**
 * 
 * class: CommonStringUtils <br>
 * description: 敏感数据脱敏处理 <br>
 * author: Mr.Roy <br>
 * date: 2018年12月9日 上午10:02:14
 */
public class CommonStringUtils {

	/**
	 * 
	 * methods: replaceNameX <br>
	 * description: 保留姓，名用星号代替 <br>
	 * author: Mr.Roy <br>
	 * date: 2018年12月9日 上午10:47:25
	 * 
	 * @param realName
	 * @return
	 */
	public static String replaceNameX(String realName) {

		if (StringUtils.isEmpty(realName)) {
			return realName;
		}
		char fir = realName.charAt(0);
		StringBuffer sb = new StringBuffer();
		sb.append(fir).append("**");
		return sb.toString();
	}

	/**
	 * 
	 * methods: mobileEncrypt <br>
	 * description: 手机号码前三后四脱敏 <br>
	 * author: Mr.Roy <br>
	 * date: 2018年12月9日 上午10:02:59
	 * 
	 * @param mobile
	 * @return
	 */
	public static String mobileEncrypt(String mobile) {

		if (StringUtils.isEmpty(mobile) || (mobile.length() != 11)) {
			return mobile;
		}
		return mobile.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
	}

	/**
	 * 
	 * methods: mobileEncryptAfterFour <br>
	 * description: 手机号码后4位脱敏. <br>
	 * author: Roy <br>
	 * date: 2019年4月29日 上午9:20:42
	 * 
	 * @param mobile
	 * @return
	 */
	public static String mobileEncryptAfterFour(String mobile) {

		if (StringUtils.isEmpty(mobile) || (mobile.length() != 11)) {
			return mobile;
		}
		return mobile.substring(0, (mobile.length() - 4)).concat("****");
	}

	/**
	 * 
	 * methods: idEncrypt <br>
	 * description: 身份证前三后四脱敏 <br>
	 * author: Mr.Roy <br>
	 * date: 2018年12月9日 上午10:03:11
	 * 
	 * @param id
	 * @return
	 */
	public static String idEncrypt(String id) {

		if (StringUtils.isEmpty(id) || (id.length() < 8)) {
			return id;
		}
		return id.replaceAll("(?<=\\w{3})\\w(?=\\w{4})", "*");
	}

	/**
	 * 
	 * methods: idEncryptAfterFour <br>
	 * description: 身份证后4位脱敏. <br>
	 * author: Roy <br>
	 * date: 2019年4月28日 下午4:03:41
	 * 
	 * @param id
	 * @return
	 */
	public static String idEncryptAfterFour(String id) {

		if (StringUtils.isEmpty(id)) {
			return id;
		}
		return id.substring(0, (id.length() - 4)).concat("****");
	}

	/**
	 * 
	 * methods: idPassport <br>
	 * description: 护照前2后3位脱敏，护照一般为8或9位 <br>
	 * author: Mr.Roy <br>
	 * date: 2018年12月9日 上午10:04:38
	 * 
	 * @param id
	 * @return
	 */
	public static String idPassport(String id) {

		if (StringUtils.isEmpty(id) || (id.length() < 8)) {
			return id;
		}
		return id.substring(0, 2) + new String(new char[id.length() - 5]).replace("\0", "*") + id.substring(id.length() - 3);
	}
}
