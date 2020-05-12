package com.power.platform.common.utils;

import java.util.ArrayList;
import java.util.Date;

public class IsHolidayOrBirthday {
	
	// 判断当天是否生日、节假日
	public static boolean isHoliday(String certificateNo){
		boolean flag = false;
		String birthdayStr = "";
		
		Date todayDate = new Date();
		String todayStr = DateUtils.getDate(todayDate, "MMdd");	// 当天
		
		// 判断是否符合生日
		if(certificateNo != null && !certificateNo.equals("")){
			birthdayStr = certificateNo.substring(10, 14);	// 身份证生日
			
			if(birthdayStr.equals(todayStr)){
				flag = true;
				return flag;
			}
		}

		/**
		 * 20190426，取消节假日判断.
		 */
		// 判断是否是节假日
//		ArrayList<String> holidays = getHolidays();
//		if (holidays.contains(todayStr)) {
//			flag = true;
//			return flag;
//		}
		
		return flag;
	}
	public static boolean isCheckHoliday(String certificateNo,Date date){
		boolean flag = false;
		String birthdayStr = "";

		String todayStr = DateUtils.getDate(date, "MMdd");	// 当天
		
		// 判断是否符合生日
		if(certificateNo != null && !certificateNo.equals("")){
			birthdayStr = certificateNo.substring(10, 14);	// 身份证生日
			
			if(birthdayStr.equals(todayStr)){
				flag = true;
				return flag;
			}
		}

		/**
		 * 20190426，取消节假日判断.
		 */
		// 判断是否是节假日
//		ArrayList<String> holidays = getHolidays();
//		if (holidays.contains(todayStr)) {
//			flag = true;
//			return flag;
//		}
		
		return flag;
	}
	
	// 判断当天是否活动
	public static boolean isActivity(){
		boolean flag = false;
		
		Date todayDate = new Date();
		String todayStr = DateUtils.getDate(todayDate, "MMdd");	// 当天
		
		
		// 判断是否是活动日
		ArrayList<String> holidays = new ArrayList<String>();
		holidays.add("0000");
//		holidays.add("0212");
//		holidays.add("0213");
//		holidays.add("0214");
//		holidays.add("0215");
//		holidays.add("0216");
//		holidays.add("0217");
//		holidays.add("0218");
//		holidays.add("0219");
//		holidays.add("0220");
//		holidays.add("0221");
		if (holidays.contains(todayStr)) {
			flag = true;
			return flag;
		}
		
		return flag;
	}
	// 判断是否活动日期
	public static boolean isCheckActivity(Date data){
		boolean flag = false;
		
		String todayStr = DateUtils.getDate(data, "MMdd");	// 当天
		
		
		// 判断是否是活动日
		ArrayList<String> holidays = new ArrayList<String>();
		holidays.add("0000");
//		holidays.add("0212");
//		holidays.add("0213");
//		holidays.add("0214");
//		holidays.add("0215");
//		holidays.add("0216");
//		holidays.add("0217");
//		holidays.add("0218");
//		holidays.add("0219");
//		holidays.add("0220");
//		holidays.add("0221");
		if (holidays.contains(todayStr)) {
			flag = true;
			return flag;
		}
		
		return flag;
	}
	public static void main(String[] args) {
		isHoliday("610524199003074498");
	}
	
	
	// 返回节假日列表
	public static ArrayList<String> getHolidays() {

		ArrayList<String> holidays = new ArrayList<String>();

		// 元旦
		holidays.add("0101");

		// 过年
//		holidays.add("0204");
		holidays.add("0205");
		holidays.add("0206");
		holidays.add("0207");
//		holidays.add("0208");
//		holidays.add("0209");
//		holidays.add("0210");

		// 清明节
		holidays.add("0405");
//		holidays.add("0406");
//		holidays.add("0407");

		// 劳动节
		holidays.add("0501");

		// 端午节
		holidays.add("0607");
//		holidays.add("0608");
//		holidays.add("0609");

		// 中秋节
		holidays.add("0913");
//		holidays.add("0914");
//		holidays.add("0915");

		// 国庆
		holidays.add("1001");
//		holidays.add("1002");
//		holidays.add("1003");
//		holidays.add("1004");
//		holidays.add("1005");
//		holidays.add("1006");
//		holidays.add("1007");

		return holidays;
	}
}
