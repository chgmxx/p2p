package com.power.platform.common.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * 日期工具类, 继承org.apache.commons.lang.time.DateUtils类
 * 
 * @author wangjingsong
 * @version 2014-4-15
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

	private static String[] parsePatterns = { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM", "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM", "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM" };

	/**
	 * 日期相加减
	 * 
	 * @param time
	 *            时间字符串 yyyy-MM-dd HH:mm:ss
	 * @param num
	 *            加的数，-num就是减去
	 * @return
	 *         减去相应的数量的年的日期
	 * @throws ParseException
	 */
	public static Date yearAddNum(Date time, Integer num) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(time);
		calendar.add(Calendar.YEAR, num);
		Date newTime = calendar.getTime();
		return newTime;
	}

	/**
	 * 
	 * 方法: getDateStr <br>
	 * 描述: 获取当前日期，字符串(年月日时分秒). <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2015年12月23日 下午7:23:22
	 * 
	 * @return
	 */
	public static String getDateStr() {

		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		Date uDate = new Date();
		return df.format(uDate);
	}

	public static String getCurrentDateTimeStr() {

		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHMMssSSS");
		Date uDate = new Date();
		return df.format(uDate);
	}

	/**
	 * 得到当前日期字符串 格式（yyyy-MM-dd）
	 */
	public static String getDate() {

		return getDate("yyyy-MM-dd");
	}
	
	public static String getDateByymd() {
		return getDate("yyyyMMdd");
	}
	
	public static int getSeconds(){
		Calendar curDate = Calendar.getInstance();
		Calendar tommorowDate = new GregorianCalendar(curDate
				.get(Calendar.YEAR), curDate.get(Calendar.MONTH), curDate
				.get(Calendar.DATE) + 1, 0, 0, 0);
		return (int)(tommorowDate.getTimeInMillis() - curDate .getTimeInMillis()) / 1000;
	}

	/**
	 * 得到当前日期字符串 格式（yyyy-MM-dd）
	 */
	public static String getFileDate() {

		return getDate("yyyyMM");
	}

	/**
	 * 得到当前日期字符串 格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
	 */
	public static String getDate(String pattern) {

		return DateFormatUtils.format(new Date(), pattern);
	}

	/**
	 * 
	 * 方法: getDate <br>
	 * 描述: 根据日期和格式获取日期字符串. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年1月8日 下午3:59:50
	 * 
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String getDate(Date date, String pattern) {

		return DateFormatUtils.format(date, pattern);
	}

	/**
	 * 得到日期字符串 默认格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
	 */
	public static String formatDate(Date date, Object... pattern) {

		String formatDate = null;
		if (pattern != null && pattern.length > 0) {
			formatDate = DateFormatUtils.format(date, pattern[0].toString());
		} else {
			formatDate = DateFormatUtils.format(date, "yyyy-MM-dd");
		}
		return formatDate;
	}

	/**
	 * 得到日期时间字符串，转换格式（yyyy-MM-dd HH:mm:ss）
	 */
	public static String formatDateTime(Date date) {

		return formatDate(date, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 得到当前时间字符串 格式（HH:mm:ss）
	 */
	public static String getTime() {

		return formatDate(new Date(), "HH:mm:ss");
	}

	/**
	 * 得到当前日期和时间字符串 格式（yyyy-MM-dd HH:mm:ss）
	 */
	public static String getDateTime() {

		return formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 得到当前日期和时间字符串 格式（yyyyMMddHHmmss）
	 */
	public static String getDt() {

		return formatDate(new Date(), "yyyyMMddHHmmss");
	}

	/**
	 * 得到当前年份字符串 格式（yyyy）
	 */
	public static String getYear() {

		return formatDate(new Date(), "yyyy");
	}

	/**
	 * 得到当前月份字符串 格式（MM）
	 */
	public static String getMonth() {

		return formatDate(new Date(), "MM");
	}

	/**
	 * 得到当天字符串 格式（dd）
	 */
	public static String getDay() {

		return formatDate(new Date(), "dd");
	}

	/**
	 * 得到当前星期字符串 格式（E）星期几
	 */
	public static String getWeek() {

		return formatDate(new Date(), "E");
	}

	/**
	 * 日期型字符串转化为日期 格式
	 * { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm",
	 * "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm",
	 * "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm" }
	 */
	public static Date parseDate(Object str) {

		if (str == null) {
			return null;
		}
		try {
			return parseDate(str.toString(), parsePatterns);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 获取过去的天数
	 * 
	 * @param date
	 * @return
	 */
	public static long pastDays(Date date) {

		long t = new Date().getTime() - date.getTime();
		return t / (24 * 60 * 60 * 1000);
	}

	/**
	 * 获取过去的小时
	 * 
	 * @param date
	 * @return
	 */
	public static long pastHour(Date date) {

		long t = new Date().getTime() - date.getTime();
		return t / (60 * 60 * 1000);
	}

	/**
	 * 获取过去的分钟
	 * 
	 * @param date
	 * @return
	 */
	public static long pastMinutes(Date date) {

		long t = new Date().getTime() - date.getTime();
		return t / (60 * 1000);
	}

	public static Date beforeMinutes30(Date date) {

		System.out.println("当前时间：" + date + "，字符串时间：" + getDate(date, "yyyy-MM-dd HH:mm:ss"));
		Calendar beforeTime = Calendar.getInstance();
		beforeTime.add(Calendar.MINUTE, -30); // 30分钟之前.
		System.out.println("过去30分钟时间：" + beforeTime.getTime() + "，字符串时间：" + getDate(beforeTime.getTime(), "yyyy-MM-dd HH:mm:ss"));
		return beforeTime.getTime();
	}

	/**
	 * 转换为时间（天,时:分:秒.毫秒）
	 * 
	 * @param timeMillis
	 * @return
	 */
	public static String formatDateTime(long timeMillis) {

		long day = timeMillis / (24 * 60 * 60 * 1000);
		long hour = (timeMillis / (60 * 60 * 1000) - day * 24);
		long min = ((timeMillis / (60 * 1000)) - day * 24 * 60 - hour * 60);
		long s = (timeMillis / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
		long sss = (timeMillis - day * 24 * 60 * 60 * 1000 - hour * 60 * 60 * 1000 - min * 60 * 1000 - s * 1000);
		return (day > 0 ? day + "," : "") + hour + ":" + min + ":" + s + "." + sss;
	}

	/**
	 * 获取两个日期之间的天数
	 * 
	 * @param before
	 * @param after
	 * @return
	 */
	public static double getDistanceOfTwoDate(Date before, Date after) {

		long beforeTime = before.getTime();
		long afterTime = after.getTime();
		return (afterTime - beforeTime) / (1000 * 60 * 60 * 24);
	}

	/**
	 * 获得当前日期的前一天日期
	 * 
	 * @return
	 */
	public static String getDateBefore() {

		Date dNow = new Date();
		Calendar calendar = Calendar.getInstance(); // 得到日历
		calendar.setTime(dNow);// 把当前时间赋给日历
		calendar.add(Calendar.DAY_OF_MONTH, -1); // 设置为前一天
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return df.format(calendar.getTime());
	}

	/**
	 * 获得指定日期的前一天日期
	 * 
	 * @return
	 */
	public static String getSpecifiedDayBefore(String specifiedDay) {

		// SimpleDateFormat simpleDateFormat = new
		// SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		Date date = null;
		try {
			date = new SimpleDateFormat("yy-MM-dd").parse(specifiedDay);
		} catch (Exception e) {
			e.printStackTrace();
		}
		c.setTime(date);
		int day = c.get(Calendar.DATE);
		c.set(Calendar.DATE, day - 1);

		String dayBefore = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
		return dayBefore;
	}

	/**
	 * 获得指定日期的后一天
	 * 
	 * @param specifiedDay
	 * @return
	 */
	public static String getSpecifiedDayAfter(String specifiedDay) {

		Calendar c = Calendar.getInstance();
		Date date = null;
		try {
			date = new SimpleDateFormat("yy-MM-dd").parse(specifiedDay);
		} catch (Exception e) {
			e.printStackTrace();
		}
		c.setTime(date);
		int day = c.get(Calendar.DATE);
		c.set(Calendar.DATE, day + 1);

		String dayAfter = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
		return dayAfter;
	}

	/**
	 * 获得指定日期的后30天
	 * 
	 * @param specifiedDay
	 * @return
	 */
	public static String getSpecifiedMonthAfter(String specifiedDay, int i) {

		Calendar c = Calendar.getInstance();
		Date date = null;
		try {
			date = new SimpleDateFormat("yy-MM-dd").parse(specifiedDay);
		} catch (Exception e) {
			e.printStackTrace();
		}
		c.setTime(date);
		int day = c.get(Calendar.DATE);
		c.set(Calendar.DATE, day + (30 * i));
		String dayAfter = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
		return dayAfter;
	}

	/**
	 * 获得指定日期的后30天yyyy-MM-dd hh:mm:ss
	 * 
	 * @param specifiedDay
	 * @return
	 */
	public static String getSpecifiedMonthAfterFormat(Date date, int i) {

		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int day = c.get(Calendar.DATE);
		c.set(Calendar.DATE, day + (i));
		String dayAfter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c.getTime());
		return dayAfter;
	}

	/**
	 * 
	 * 方法: getSpecifiedMonthAfter <br>
	 * 描述: 获取指定日期后的N天过后的日期. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年5月11日 下午2:36:54
	 * 
	 * @param date
	 * @param i
	 * @return
	 */
	public static Date getSpecifiedMonthAfter(Date date, int i) {

		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int day = c.get(Calendar.DATE);
		c.set(Calendar.DATE, day + (i));
		return c.getTime();
	}

	/**
	 * 获得指定日期的后N天
	 * 
	 * @param specifiedDay
	 * @return
	 */
	public static Date getAddDaysDate(int days) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DATE, days);
		return calendar.getTime();
	}

	/**
	 * 字符串日期转换为DATE
	 * 
	 * @param sdate
	 *            (String - yyyy-MM-dd HH:mm:ss)
	 * @return Date date
	 */
	public static Date getDateOfString(String sdate) {

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = formatter.parse(sdate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 字符串日期转换为DATE
	 * 
	 * @param sdate
	 *            (String - yyyy-MM-dd)
	 * @return Date date
	 */
	public static Date getShortDateOfString(String sdate) {

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = formatter.parse(sdate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 比较两个时间是否为同一天
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean isSameDate(Date date1, Date date2) {

		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);
		boolean isSameYear = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
		boolean isSameMonth = isSameYear && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
		boolean isSameDate = isSameMonth && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);

		return isSameDate;
	}

	/**
	 * 日期比较大小
	 */
	public static boolean compare_date(String DATE1, String DATE2) {

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date dt1 = df.parse(DATE1);
			Date dt2 = df.parse(DATE2);
			if (dt1.getTime() > dt2.getTime()) {
				return false;
			} else if (dt1.getTime() < dt2.getTime()) {
				return true;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return true;
	}

	public static boolean compare_date_T(String DATE1, String DATE2) {

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date dt1 = df.parse(DATE1);
			Date dt2 = df.parse(DATE2);
			if (dt1.getTime() > dt2.getTime()) {
				return false;
			} else if (dt1.getTime() < dt2.getTime()) {
				return true;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return true;
	}

	/**
	 * 
	 * methods: compareDateByDay <br>
	 * description: 日期比较大小. <br>
	 * author: Roy <br>
	 * date: 2019年3月8日 上午11:37:32
	 * 
	 * @param DATE1
	 * @param DATE2
	 * @return 1:DATE1>DATE2,-1:DATE1<DATE2,0DATE1=DATE2
	 */
	public static int compareDateByDay(String DATE1, String DATE2) {

		int flag = 0;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date dt1 = df.parse(DATE1);
			Date dt2 = df.parse(DATE2);
			if (dt1.getTime() > dt2.getTime()) {
				return flag = 1;
			} else if (dt1.getTime() < dt2.getTime()) {
				return flag = -1;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return flag;
	}

	/**
	 * 
	 * 方法: dayOfMonth_Start <br>
	 * 描述: 本月第一天. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年5月9日 上午9:01:18
	 * 
	 * @return
	 */
	public static String dayOfMonth_Start() {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance(); // 获取日历抽象类的实例.
		c.add(Calendar.MONTH, 0); // 当前月份.
		c.set(Calendar.DAY_OF_MONTH, 1); // 设置为1号，当前日期既为本月第一天.
		String firstDay = format.format(c.getTime());
		return firstDay;
	}

	/**
	 * 
	 * 方法: dayOfMonth_End <br>
	 * 描述: 本月最后一天. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年5月9日 上午9:01:00
	 * 
	 * @return
	 */
	public static String dayOfMonth_End() {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance(); // 获取日历抽象类的实例.
		c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH)); // 获取日历当前月份的最大一天.
		String firstDay = format.format(c.getTime());
		return firstDay;
	}

	/**
	 * 
	 * 方法: calendarDate_Negative <br>
	 * 描述: 获取指定日期的前几天. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年5月9日 上午9:04:46
	 * 
	 * @param date
	 *            指定的日期
	 * @param negativeNumber
	 *            前几天(负数)
	 * @return
	 */
	public static Date calendarDate_Negative(Date date, int negativeNumber) {

		Calendar c = Calendar.getInstance(); // 获取日历抽象类的实例.
		c.setTime(date); // 设置指定日期.
		c.add(Calendar.DATE, negativeNumber); // 负数-前几天，整数-后几天.
		Date negativeDay = c.getTime();

		return negativeDay;
	}

	/**
	 * 
	 * methods: getStartTimeEveryday <br>
	 * description: TODO <br>
	 * author: Roy <br>
	 * date: 2019年7月24日 下午2:25:11
	 * 
	 * @return
	 */
	public static Date getStartTimeEveryday() {

		Calendar c = Calendar.getInstance();
		c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		System.out.println("日期：" + c.getTime() + "字符串时间：" + getDate(c.getTime(), "yyyy-MM-dd HH:mm:ss"));
		return c.getTime();
	}

	public static Date getNowTime() {

		Calendar c = Calendar.getInstance();
		System.out.println("日期：" + c.getTime() + "字符串时间：" + getDate(c.getTime(), "yyyy-MM-dd HH:mm:ss"));
		return c.getTime();
	}

	public static void main(String[] args) throws ParseException {

		// SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		// System.out.println(compare_date_T("2017-12-31", "2018-01-01"));
		// System.out.println(compareDateByDay("2019-03-08", "2019-03-09"));
		// System.out.println(getDateOfString(getDate(new Date(), "yyyy-MM-dd")));
		// System.out.println(dayOfMonth_Start());
		// System.out.println(dayOfMonth_End());
		// System.out.println(format.format(calendarDate_Negative(new Date(), -7)));
		// System.out.println(format.format(calendarDate_Negative(new Date(), -5)));
		// System.out.println(format.format(calendarDate_Negative(new Date(), -2)));
		// beforeMinutes30(new Date());
		// getStartTimeEveryday();
		// getNowTime();
		// System.out.println(getSpecifiedMonthAfter("2019-07-31", 3));
		System.out.println(formatDate(parseDate("20220927", "yyyyMMdd"), "yyyy年MM月dd日"));

	}

}
