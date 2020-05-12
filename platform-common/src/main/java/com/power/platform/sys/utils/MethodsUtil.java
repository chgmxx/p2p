package com.power.platform.sys.utils;

public class MethodsUtil {

	/**
	 * 
	 * @param src
	 * @param from
	 * @param count
	 * @return
	 */
	public static String hideStr(String src, int from, int count) {
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

	public static int[] computePageIndex(int index, int pages, int size) {
		int before = size / 2;
		int after = size - before;
		int pageStart = index - before;
		int pageEnd = index + after;
		if (pageStart < 0)
			pageStart = 0;
		if (pageEnd > pages)
			pageEnd = pages;
		if (pageStart == 0) {
			if (pages < size)
				pageEnd = pages;
			else
				pageEnd = size;
		} else if (pageEnd == pages) {
			if (pages < size)
				pageStart = 0;
			else
				pageStart = pages - size;
		}
		int[] result = new int[2];
		result[0] = pageStart;
		result[1] = pageEnd;
		return result;
	}

	public static void main(String[] args) {
		String str = "138105";
		System.out.println(hideStr(str, 0, 4));
	}
}
