package com.power.platform.ifcert.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.power.platform.common.utils.StringUtils;

public class UserIdCardUtil {

	/** 中国公民身份证号码最小长度。 */
	public static final int CHINA_ID_MIN_LENGTH = 15;

	/**
	 * 每位加权因子.
	 */
	public static final int power[] = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };

	/**
	 * 
	 * methods: getGenderByIdCard <br>
	 * description: 获取客户性别. <br>
	 * author: Roy <br>
	 * date: 2019年4月29日 下午2:35:15
	 * 
	 * @param idCard
	 * @return 0：女，1：男.
	 */
	public static String getGenderByIdCard(String idCard) {

		String sGender = "-1";

		if (StringUtils.isBlank(idCard)) {
			// 身份证号码为null或者空串，返回1：男，必填.
			return "1";
		} else {
			if (idCard.length() == CHINA_ID_MIN_LENGTH) {
				idCard = conver15CardTo18(idCard);
			}
			String sCardNum = idCard.substring(16, 17);
			if (Integer.parseInt(sCardNum) % 2 != 0) {
				sGender = "1";
			} else {
				sGender = "0";
			}
		}

		return sGender;
	}

	/**
	 * 
	 * methods: conver15CardTo18 <br>
	 * description: 将15位身份证号码转换为18位. <br>
	 * author: Roy <br>
	 * date: 2019年4月29日 下午1:50:15
	 * 
	 * @param idCard
	 *            15位身份证
	 * @return 18位身份证
	 */
	public static String conver15CardTo18(String idCard) {

		String idCard18 = "";
		if (idCard.length() != CHINA_ID_MIN_LENGTH) {
			return null;
		}
		if (isNum(idCard)) {
			// 获取出生年月日
			String birthday = idCard.substring(6, 12);
			Date birthDate = null;
			try {
				birthDate = new SimpleDateFormat("yyMMdd").parse(birthday);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			Calendar cal = Calendar.getInstance();
			if (birthDate != null)
				cal.setTime(birthDate);
			// 获取出生年(完全表现形式,如：2010)
			String sYear = String.valueOf(cal.get(Calendar.YEAR));
			idCard18 = idCard.substring(0, 6) + sYear + idCard.substring(8);
			// 转换字符数组
			char[] cArr = idCard18.toCharArray();
			if (cArr != null) {
				int[] iCard = converCharToInt(cArr);
				int iSum17 = getPowerSum(iCard);
				// 获取校验位
				String sVal = getCheckCode18(iSum17);
				if (sVal.length() > 0) {
					idCard18 += sVal;
				} else {
					return null;
				}
			}
		} else {
			return null;
		}
		return idCard18;
	}

	/**
	 * 
	 * methods: isNum <br>
	 * description: 数字验证. <br>
	 * author: Roy <br>
	 * date: 2019年4月29日 上午11:53:36
	 * 
	 * @param val
	 * @return 提取的数字.
	 */
	public static boolean isNum(String val) {

		return val == null || "".equals(val) ? false : val.matches("^[0-9]*$");
	}

	/**
	 * 
	 * methods: converCharToInt <br>
	 * description: 字符数组转换成数字数组. <br>
	 * author: Roy <br>
	 * date: 2019年4月29日 上午11:48:29
	 * 
	 * @param ca
	 * @return
	 */
	public static int[] converCharToInt(char[] ca) {

		int len = ca.length;
		int[] iArr = new int[len];
		try {
			for (int i = 0; i < len; i++) {
				iArr[i] = Integer.parseInt(String.valueOf(ca[i]));
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return iArr;
	}

	/**
	 * 
	 * methods: getPowerSum <br>
	 * description: 将身份证的每位和对应位的加权因子相乘之后，再得到和值. <br>
	 * author: Roy <br>
	 * date: 2019年4月29日 上午11:49:41
	 * 
	 * @param iArr
	 * @return 身份证编码
	 */
	public static int getPowerSum(int[] iArr) {

		int iSum = 0;
		if (power.length == iArr.length) {
			for (int i = 0; i < iArr.length; i++) {
				for (int j = 0; j < power.length; j++) {
					if (i == j) {
						iSum = iSum + iArr[i] * power[j];
					}
				}
			}
		}
		return iSum;
	}

	/**
	 * 
	 * methods: getCheckCode18 <br>
	 * description: 将power和值与11取模获得余数进行校验码判断. <br>
	 * author: Roy <br>
	 * date: 2019年4月29日 上午11:51:25
	 * 
	 * @param iSum
	 * @return 校验位.
	 */
	public static String getCheckCode18(int iSum) {

		String sCode = "";
		switch (iSum % 11) {
			case 10:
				sCode = "2";
				break;
			case 9:
				sCode = "3";
				break;
			case 8:
				sCode = "4";
				break;
			case 7:
				sCode = "5";
				break;
			case 6:
				sCode = "6";
				break;
			case 5:
				sCode = "7";
				break;
			case 4:
				sCode = "8";
				break;
			case 3:
				sCode = "9";
				break;
			case 2:
				sCode = "x";
				break;
			case 1:
				sCode = "0";
				break;
			case 0:
				sCode = "1";
				break;
		}
		return sCode;
	}

}
