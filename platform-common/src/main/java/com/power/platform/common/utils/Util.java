package com.power.platform.common.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {

	/**
	 * 根据索引获取
	 * 
	 * @param <T>
	 * @param clazz
	 * @param ordinal
	 * @return
	 */
	public static <T extends Enum<T>> T valueOfEnum(Class<T> clazz, int ordinal) {
		return (T) clazz.getEnumConstants()[ordinal];
	}

	/**
	 * 根据name获取
	 * 
	 * @param <T>
	 * @param enumType
	 * @param name
	 * @return
	 */
	public static <T extends Enum<T>> T valueOfEnum(Class<T> enumType,
			String name) {
		return (T) Enum.valueOf(enumType, name);
	}

	// 获取一个double类型数值的百分比
	// example: FormatPercent(0.33333333,2) = 33%
	public static String FormatPercent(double number, int newValue) {
		java.text.NumberFormat nf = java.text.NumberFormat.getPercentInstance();
		nf.setMinimumFractionDigits(newValue);
		return nf.format(number);
	}

	private static long getTime() {
		// 取得资源对象
		URL url;
		URLConnection uc = null;

		try {
			url = new URL("http://www.bjtime.cn");
			// 生成连接对象
			uc = url.openConnection();
			// 发出连接
			uc.connect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		long time = uc.getDate();
		return time;
	}

	public static String getInternetTime() {

		// System.out.println("long time:"+time);
		Date date = new Date(getTime());
		// System.out.println("date:"+date.toString());
		return new SimpleDateFormat("yyyyMMddHHmmss").format(date);
	}

	public static Date getInternetDate() {
		return new Date(getTime());
	}

	public static String getInternetDateStr() {
		Date date = new Date(getTime());
		return new SimpleDateFormat("yyyyMMdd").format(date);
	}

	public static Timestamp getTimestamp() {
		return new Timestamp(getTime());

	}

	/**
	 * 汇付的金额表示格式2.00或600.50
	 * 
	 * @param money
	 * @return
	 * @author 汤长海 2014年10月30日
	 */
	public static String formatMoneyforHF(double money) {
		return new DecimalFormat("#.00").format(money);
	}

	/**
	 * 
	 * @param src
	 * @param from
	 * @param count
	 * @return
	 */
	public static String hideString(String src, int from, int count) {
		if (src == null || src.length() <= from)
			return src;
		StringBuffer buf = new StringBuffer();
		buf.append(src.substring(0, from));
		for (int i = 0; i < count; i++) {
			if (from + i < src.length())
				buf.append("*");
			else
				break;
		}
		if (from + count < src.length())
			buf.append(src.substring(from + count));
		return buf.toString();
	}

	public static String hideIdCardNo(String idCardNo) {
		if (idCardNo == null || idCardNo.length() <= 4)
			return idCardNo;
		StringBuffer buf = new StringBuffer();
		buf.append(idCardNo.substring(0, 2));
		for (int i = 4; i < idCardNo.length(); i++)
			buf.append("*");
		buf.append(idCardNo.substring(idCardNo.length() - 2));
		return buf.toString();
	}

	public static String hideEmail(String email) {
		if (email == null)
			return email;
		int index = email.indexOf("@");
		if (index == -1)
			return email;
		String result = email.substring(0, index);
		if (result.length() > 4)
			result = result.substring(0, 4);
		result += "****";
		int index2 = email.lastIndexOf(".");
		if (index2 > index)
			result += email.substring(index2 + 1);
		return result;
	}

	public static void main(String[] args) {
		String str = "138105";
		System.out.println(hideString(str, 0, 4));
	}
}
