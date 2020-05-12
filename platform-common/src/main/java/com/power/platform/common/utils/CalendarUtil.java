package com.power.platform.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 日期工具类
 * 
 * @author Brain
 */
public class CalendarUtil {

	public static final String YEAR = "year";
	public static final String MONTH = "month";
	public static final String MONTH_FIRST_DAY_TIME = "month_first_day_time";
	public static final String MONTH_LAST_DAY_TIME = "month_last_day_time";

	// 获取当前年的前5年数据
	public static List<String> getPre5Years() {

		List<String> years = new ArrayList<String>();
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		for (int i = 0; i < 5; i++) {
			years.add((year - i) + "");
		}
		return years;
	}

	// 获取12个月的数据
	public static List<String> get12MonthStr() {

		List<String> months = new ArrayList<String>();
		for (int i = 1; i < 13; i++) {
			months.add(i + "");
		}
		return months;
	}

	// 获取当前明天的数据
	public static String getNextDay(String format) {

		Calendar calendar = Calendar.getInstance();
		Calendar tmpCalendar = Calendar.getInstance();
		tmpCalendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));// 设置本月最大日期
		int maxDate = tmpCalendar.get(Calendar.DATE);

		int month = calendar.get(Calendar.MONTH);
		int year = calendar.get(Calendar.YEAR);
		int date = calendar.get(Calendar.DATE);

		if (date + 1 > maxDate) {
			if (month + 1 > 11) {
				calendar.add(Calendar.YEAR, 1);
				calendar.set(Calendar.MONTH, 0);
				calendar.set(Calendar.DATE, 1);
			} else {
				calendar.set(year, month, date + 1);
			}
		} else {
			calendar.set(year, month, date + 1);
		}
		return DateUtil.getDateText(calendar.getTime(), format);
	}

	// 获取n天后的日期
	public static Date getNextNDay(Date startTime, Integer n) {

		Calendar start = Calendar.getInstance();
		start.setTime(startTime);
		start.add(Calendar.DATE, n);
		return start.getTime();
	}

	// 获取n天后的日期 00:00:00
	public static Date getNextNDayBegin(Date startTime, Integer n) {

		Calendar start = Calendar.getInstance();
		start.setTime(startTime);
		start.add(Calendar.DATE, n);
		start.set(Calendar.HOUR_OF_DAY, 0);
		start.set(Calendar.MINUTE, 0);
		start.set(Calendar.SECOND, 0);
		return start.getTime();
	}

	// 获取n天后的日期 23:59:59
	public static Date getNextNDayEnd(Date startTime, Integer n) {

		Calendar start = Calendar.getInstance();
		start.setTime(startTime);
		start.add(Calendar.DATE, n);
		start.set(Calendar.HOUR_OF_DAY, 23);
		start.set(Calendar.MINUTE, 59);
		start.set(Calendar.SECOND, 59);
		return start.getTime();
	}

	// 获取n天前的日期
	public static Date getPreNDay(Date startTime, Integer n) {

		Calendar start = Calendar.getInstance();
		start.setTime(startTime);
		start.add(Calendar.DATE, -n);
		return start.getTime();
	}

	// 获取n天前的日期 00:00:00
	public static Date getPreNDayBegin(Date startTime, Integer n) {

		Calendar start = Calendar.getInstance();
		start.setTime(startTime);
		start.add(Calendar.DATE, -n);
		start.set(Calendar.HOUR_OF_DAY, 0);
		start.set(Calendar.MINUTE, 0);
		start.set(Calendar.SECOND, 0);
		return start.getTime();
	}

	// 获取n天前的日期 23:59:59
	public static Date getPreNDayEnd(Date startTime, Integer n) {

		Calendar start = Calendar.getInstance();
		start.setTime(startTime);
		start.add(Calendar.DATE, -n);
		start.set(Calendar.HOUR_OF_DAY, 23);
		start.set(Calendar.MINUTE, 59);
		start.set(Calendar.SECOND, 59);
		return start.getTime();
	}

	// 获取前x天的数据, x < 10;
	public static String getPreXDay(int x, String format) {

		Calendar calendar = Calendar.getInstance();
		int month = calendar.get(Calendar.MONTH);
		int year = calendar.get(Calendar.YEAR);
		int date = calendar.get(Calendar.DATE);

		if (date - x < 0) {
			if (month - 1 < 0) {
				calendar.add(Calendar.YEAR, -1);
				calendar.set(Calendar.MONTH, 11);
				calendar.set(Calendar.DATE, 31 - (x - date));
			} else {
				calendar.add(Calendar.MONTH, -1);
				calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));// 设置本月最大日期
				int maxDate = calendar.get(Calendar.DATE);
				calendar.set(year, month - 1, maxDate - (x - date));
			}
		} else {
			calendar.set(year, month, date - x);
		}
		return DateUtil.getDateText(calendar.getTime(), format);
	}

	// 获取当前年
	public static String getYear() {

		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		return year + "";
	}

	// 获取当前月
	public static String getMonth() {

		Calendar calendar = Calendar.getInstance();
		int month = calendar.get(Calendar.MONTH);
		month = month + 1;
		if (month > 12) {
			return "12";
		}
		return month + "";
	}

	// 获取 年-月的 第一天 和 最后一天 时间
	public static Map<String, String> getMonthFirstEndDayTime(String year, String month) {

		Map<String, String> rst = new HashMap<String, String>();
		Calendar calendar = Calendar.getInstance();
		int m = Integer.parseInt(month);
		m = m - 1;
		if (m < 0) {
			m = 0;
		}
		calendar.set(Integer.parseInt(year), m, 1);
		String firstDayTime = DateUtil.COMMON.getDateText(calendar.getTime()) + " 00:00:00";

		calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));// 设置本月最大日期
		String lastDayTime = DateUtil.COMMON.getDateText(calendar.getTime()) + " 23:59:59";

		rst.put(CalendarUtil.MONTH_FIRST_DAY_TIME, firstDayTime);
		rst.put(CalendarUtil.MONTH_LAST_DAY_TIME, lastDayTime);

		return rst;
	}

	// 获取前一个月
	public static Map<String, String> getPreMonth() {

		Map<String, String> rst = new HashMap<String, String>();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -1);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		month = month + 1;

		rst.put("year", year + "");
		if (month > 12) {
			rst.put("month", "12");
		} else {
			rst.put("month", month + "");
		}
		return rst;
	}

	// 获取前一个月
	public static Map<String, String> getPreMonth(Integer year, Integer month) {

		Map<String, String> rst = new HashMap<String, String>();
		if (month == 1) {
			rst.put("year", (year - 1) + "");
			rst.put("month", "12");
		} else {
			rst.put("year", year + "");
			rst.put("month", (month - 1) + "");
		}
		return rst;
	}

	// 获取开始月份和结束月份中间的所有月份，包括开始月份和结束月份
	public static List<String> getBetweenMonths(String startMonth, String endMonth) {

		String[] startArr = startMonth.split("-");
		String[] endArr = endMonth.split("-");

		Integer startY = Integer.parseInt(startArr[0]);
		Integer startM = Integer.parseInt(startArr[1]);

		Integer endY = Integer.parseInt(endArr[0]);
		Integer endM = Integer.parseInt(endArr[1]);

		Calendar startCalendar = Calendar.getInstance();
		startCalendar.set(startY, startM - 1, 1, 0, 0);

		Calendar endCalendar = Calendar.getInstance();
		endCalendar.set(endY, endM - 1, 1, 0, 0);

		List<String> months = new ArrayList<String>();
		while (startCalendar.getTimeInMillis() <= endCalendar.getTimeInMillis()) {
			months.add(DateUtil.getDateText(startCalendar.getTime(), "yyyy-MM"));
			startCalendar.add(Calendar.MONTH, 1);
		}
		return months;
	}

	// 获取日期之间的天List formt = "yyyy-MM-dd"
	public static List<String> getBetweenDateStr(String startTime, String endTime) {

		List<String> dateList = new ArrayList<String>();
		try {
			Long startM = DateUtil.COMMON.getTextDate(startTime).getTime();
			Long endM = DateUtil.COMMON.getTextDate(endTime).getTime();
			long result = (endM - startM) / (24 * 60 * 60 * 1000);
			String[] startTimeStr = startTime.split("-");
			Calendar startCalendar = Calendar.getInstance();
			startCalendar.set(Integer.parseInt(startTimeStr[0]), Integer.parseInt(startTimeStr[1]) - 1, Integer.parseInt(startTimeStr[2]));
			startCalendar.add(Calendar.DATE, -1);
			for (int i = 0; i <= result; i++) {
				startCalendar.add(Calendar.DATE, 1);
				dateList.add(DateUtil.COMMON.getDateText(startCalendar.getTime()));
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dateList;
	}

	public static List<String> getBetweenDateStr(String startTime, String endTime, String format) {

		List<String> dateList = new ArrayList<String>();
		try {
			Long startM = DateUtil.COMMON.getTextDate(startTime).getTime();
			Long endM = DateUtil.COMMON.getTextDate(endTime).getTime();
			long result = (endM - startM) / (24 * 60 * 60 * 1000);
			String[] startTimeStr = startTime.split("-");
			Calendar startCalendar = Calendar.getInstance();
			startCalendar.set(Integer.parseInt(startTimeStr[0]), Integer.parseInt(startTimeStr[1]) - 1, Integer.parseInt(startTimeStr[2]));
			startCalendar.add(Calendar.DATE, -1);
			for (int i = 0; i <= result; i++) {
				startCalendar.add(Calendar.DATE, 1);
				dateList.add(DateUtil.getDateText(startCalendar.getTime(), format));
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dateList;
	}

	// 获取日期的前n个月的月份（闭区间）
	public static List<String> getPreNMonths(String curMonth, Integer n) {

		String[] curArr = curMonth.split("-");
		Integer curY = Integer.parseInt(curArr[0]);
		Integer curM = Integer.parseInt(curArr[1]);

		Calendar curCalendar = Calendar.getInstance();
		curCalendar.set(curY, curM - 1, 1, 0, 0);

		Calendar tmpCalendar = Calendar.getInstance();
		tmpCalendar.set(curY, curM - 1 - n, 1, 0, 0);

		List<String> months = new ArrayList<String>();
		while (tmpCalendar.getTimeInMillis() <= curCalendar.getTimeInMillis()) {
			months.add(DateUtil.getDateText(tmpCalendar.getTime(), "yyyy-MM"));
			tmpCalendar.add(Calendar.MONTH, 1);
		}
		return months;
	}

	// 获取当前天
	public static String getToday(String format) {

		Calendar calendar = Calendar.getInstance();
		return DateUtil.getDateText(calendar.getTime(), format);
	}

	// 获取当前月的第一天
	public static String getFirstDay(String format) {

		Calendar calendar = Calendar.getInstance();
		int month = calendar.get(Calendar.MONTH);
		int year = calendar.get(Calendar.YEAR);
		calendar.set(year, month, 1);
		return DateUtil.getDateText(calendar.getTime(), format);
	}

	// 获取当前月的最后一天
	public static String getLastDay(String format) {

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));// 设置本月最大日期
		return DateUtil.getDateText(calendar.getTime(), format);
	}

	// 获取当前月的第一天
	public static Date getFirstDate() {

		Calendar calendar = Calendar.getInstance();
		int month = calendar.get(Calendar.MONTH);
		int year = calendar.get(Calendar.YEAR);
		calendar.set(year, month, 1);
		return calendar.getTime();
	}

	// 获取当前月的最后一天
	public static Date getLastDate() {

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));// 设置本月最大日期
		return calendar.getTime();
	}

	// 日期的天是否一样；
	public static boolean isDayEqual(Date date1, Date date2) {

		if (date1 == null || date2 == null) {
			return false;
		}
		String date1Str = DateUtil.COMPAT.getDateText(date1);
		String date2Str = DateUtil.COMPAT.getDateText(date2);
		return date1Str.equals(date2Str);
	}

	/**
	 * 时间间距是否为xx；
	 * 如果在 space 之内返回true；否则返回false
	 * 
	 * @param startTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @param space
	 *            毫秒
	 * @return
	 */
	public static boolean isTimeSpace(Date startTime, Date endTime, long space) {

		if (startTime == null || endTime == null) {
			return false;
		}
		return endTime.getTime() - startTime.getTime() <= space;
	}

	/**
	 * 时间间距是否在 startSpace 和 endSpace 之间
	 * 
	 * @param startTime
	 * @param endTime
	 * @param startSpace
	 * @param endSpace
	 * @return
	 */
	public static boolean isTimeBetween(Date startTime, Date endTime, long startSpace, long endSpace) {

		return endTime.getTime() - startTime.getTime() <= endSpace && endTime.getTime() - startTime.getTime() >= startSpace;
	}

	public static boolean isTimeBetween(Date time, Date startTime, Date endTime) {

		return time.getTime() >= startTime.getTime() && time.getTime() <= endTime.getTime();
	}

	// 获取日期之间年的距离
	public static Integer getYearSpace(Date startTime, Date endTime) {

		Calendar start = Calendar.getInstance();
		start.setTime(startTime);
		Calendar end = Calendar.getInstance();
		end.setTime(endTime);
		return end.get(Calendar.YEAR) - start.get(Calendar.YEAR);
	}

	// 获取日期之间天的距离
	public static Integer getDaySpace(Date startTime, Date endTime) {

		return (int) (endTime.getTime() - startTime.getTime()) / (24 * 60 * 60 * 1000);
	}

	// 获取当前日期 毫秒
	public static long getTimeInMillis() {

		Calendar now = Calendar.getInstance();
		return now.getTimeInMillis();
	}

	// 获取当前日期 秒
	public static long getTimeInSeconds() {

		return getTimeInMillis() / 1000L;
	}

	/**
	 * 
	 * 方法: differentDaysByMillisecond <br>
	 * 描述: 两个日期相差的天数. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年12月4日 下午3:49:16
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int differentDaysByMillisecond(Date date1, Date date2) {

		int days = (int) ((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24));
		return days;
	}

	/**
	 * 
	 * 方法: differentDays <br>
	 * 描述: 两个日期相差的天数. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年12月4日 下午4:11:22
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int differentDays(Date date1, Date date2) {

		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);

		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);
		int day1 = cal1.get(Calendar.DAY_OF_YEAR);
		int day2 = cal2.get(Calendar.DAY_OF_YEAR);

		int year1 = cal1.get(Calendar.YEAR);
		int year2 = cal2.get(Calendar.YEAR);
		if (year1 != year2) { // 同一年.
			int timeDistance = 0;
			for (int i = year1; i < year2; i++) {
				if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) { // 闰年
					timeDistance += 366;
				} else { // 不是闰年.
					timeDistance += 365;
				}
			}
			return timeDistance + (day2 - day1);
		} else { // 不同年.
			return day2 - day1;
		}
	}

	/**
	 * 
	 * 方法: getNextFewMonthFifteen <br>
	 * 描述: 获取指定日期，下m个月的15号. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年12月5日 上午11:18:41
	 * 
	 * @param specifiedDate
	 * @param m
	 * @return
	 */
	public static String getNextFewMonthFifteen(Date specifiedDate, int m) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar lastDate = Calendar.getInstance();
		lastDate.setTime(specifiedDate);
		lastDate.add(Calendar.MONTH, m);// 减一个月
		lastDate.set(Calendar.DATE, 15);// 把日期设置为当月第一天
		String str = sdf.format(lastDate.getTime());
		return str;
	}

	/**
	 * 
	 * 方法: getSpecifiedMonthAfter <br>
	 * 描述: 获取指定时间，d天后的日期. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年12月4日 下午8:16:25
	 * 
	 * @param specifiedDay
	 * @param d
	 * @return
	 */
	public static String getSpecifiedMonthAfter(String specifiedDay, int d) {

		Calendar c = Calendar.getInstance();
		Date date = null;
		try {
			date = new SimpleDateFormat("yy-MM-dd").parse(specifiedDay);
		} catch (Exception e) {
			e.printStackTrace();
		}
		c.setTime(date);
		int day = c.get(Calendar.DATE);
		c.set(Calendar.DATE, (day + d));
		String dayAfter = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
		return dayAfter;
	}

	/**
	 * 
	 * 方法: isLeapYear <br>
	 * 描述: 是否闰年. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年12月4日 下午8:28:32
	 * 
	 * @param year
	 * @return
	 */
	public static boolean isLeapYear(int year) {

		return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
	}

	/**
	 * 
	 * 方法: getDaysOfMonth <br>
	 * 描述: 获取某月的天数. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年12月5日 上午9:50:19
	 * 
	 * @param date
	 * @return
	 */
	public static int getDaysOfMonth(Date date) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 
	 * 方法: getMonth <br>
	 * 描述: 获取指定日期的月份. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年12月5日 上午10:59:07
	 * 
	 * @param date
	 * @return
	 */
	public static int getMonth(Date date) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		month = month + 1;
		if (month > 12) {
			return 12;
		}
		return month;
	}

	public static void projectPlanInit() {

		// 融资金额.
		// Double amount = 500000D;
		// 年化收益率.
		// Double annualRate = 9D;
		// 每日利息(保留两位小数).
		// Double dayInterest = InterestUtils.getDayInterestFormat(amount, annualRate);

		int span = 360;
		int v = 1;
		String loanDateStr = "2017-02-28";
		Date loanDate = DateUtils.getShortDateOfString(loanDateStr);
		while (v <= 14) {
			System.out.println("v = " + v);
			if (v == 1) { // 第一个月天数.
				System.out.println("放款日期：" + loanDateStr);
				// 放款日期开始，到下月的15号还款日期，还款天数.
				int day = differentDaysByMillisecond(loanDate, DateUtils.getShortDateOfString(getNextFewMonthFifteen(loanDate, v)));
				System.out.println("第" + v + "期开始时间：" + loanDateStr);
				System.out.println("第" + v + "期还款天数：" + day);
				span = span - day;
				System.out.println("第" + v + "期剩余还款天数：" + span);
				System.out.println("第" + v + "期还款日期：" + getNextFewMonthFifteen(loanDate, v));
			} else {
				// 每月的天数，即为还款天数.
				int day = getDaysOfMonth(DateUtils.getShortDateOfString(getNextFewMonthFifteen(loanDate, v - 1)));
				System.out.println("第" + v + "个月天数：" + day);
				if (span >= day) { // 还款天数，是否满足还款日期.
					System.out.println("第" + v + "期开始时间：" + getNextFewMonthFifteen(loanDate, v - 1));
					System.out.println("第" + v + "期还款天数：" + day);
					span = span - day;
					System.out.println("第" + v + "期剩余还款天数：" + span);
					System.out.println("第" + v + "期还款日期：" + getNextFewMonthFifteen(loanDate, v));
				} else { // 最后一期还款.
					if (span == 0) {
						break;
					}
					System.out.println("第" + v + "期开始时间：" + getNextFewMonthFifteen(loanDate, v - 1));
					System.out.println("第" + v + "期还款天数：" + span);
					System.out.println("第" + v + "期还款日期：" + getSpecifiedMonthAfter(getNextFewMonthFifteen(loanDate, v - 1), span));
					span = span - span;
					System.out.println("第" + v + "期剩余还款天数：" + span);
				}
			} // --.

			// 终止条件.
			++v;
		}

	}

	public static void main(String[] args) {

		/*
		 * String startTime = "2009-12-14";
		 * String endTime = "2010-1-14";
		 * List<String> list = getBetweenDateStr(startTime,endTime);
		 * for(String s : list){
		 * System.out.println(s);
		 * }
		 */
		/*
		 * String nextday = getNextDay("yyyy-MM-dd");
		 * String xday = getPreXDay(6,"yyyy-MM-dd");
		 * System.out.println(nextday);
		 * System.out.println(xday);
		 * System.out.println(getFirstDay("yyyy-MM-dd"));
		 * System.out.println(getLastDay("yyyy-MM-dd"));
		 */

		try {
			// Date startTime =
			// DateUtil.COMMON.getTextDate("2015-06-03 00:00:00");
			// Date endTime =
			// DateUtil.COMMON_FULL.getTextDate("2015-06-03 23:59:59");
			// System.out.println(isTimeBetween(new Date(),startTime,endTime));
			//
			// List<String> month = getPreNMonths("2015-06",11);
			// for(String s : month){
			// System.out.println(s);
			// }
			//
			// String str = DateUtil.COMMON_FULL.getDateText(getPreNDayEnd(new
			// Date(),1));
			// System.out.println(str);
			//
			// List<String> days =
			// getBetweenDateStr("2015-03-09","2015-06-15","MM/dd");
			// for(String s : days){
			// System.out.println(s);
			// }
			// Map<String, String> map = getMonthFirstEndDayTime(getYear(),
			// getMonth());
			// System.out.println("当前月的第一天：" + map.get("month_first_day_time"));
			// System.out.println("当前月的最后一天：" + map.get("month_last_day_time"));
			// System.out.println("当前月的最后一天：" +
			// getLastDay("yyyy-MM-dd HH:mm:ss"));
			// System.out.println("当前天：" + getToday("yyyy-MM-dd"));
			System.out.println("两日期之间的天数：" + differentDaysByMillisecond(DateUtils.getShortDateOfString("2017-12-01"), DateUtils.getShortDateOfString("2018-01-15")));
			System.out.println("两日期之间的天数：" + differentDaysByMillisecond(DateUtils.getShortDateOfString("2018-01-15"), DateUtils.getShortDateOfString("2018-02-15")));
			System.out.println("两日期之间的天数：" + differentDaysByMillisecond(DateUtils.getShortDateOfString("2018-02-15"), DateUtils.getShortDateOfString("2018-03-15")));
			System.out.println("两日期之间的天数：" + differentDaysByMillisecond(DateUtils.getShortDateOfString("2018-02-28"), DateUtils.getShortDateOfString("2018-03-15")));
			// System.out.println("两日期之间的天数：" +
			// differentDays(DateUtils.getDateOfString("2017-12-31 00:05:35"),
			// DateUtils.getDateOfString(map.get("month_last_day_time"))));
			// System.out.println("两日期之间的天数：" +
			// differentDaysByMillisecond(DateUtils.getShortDateOfString(map.get("month_first_day_time")),
			// DateUtils.getShortDateOfString(map.get("month_last_day_time"))));

			// System.err.println(DateUtils.getDateOfString(map.get("month_first_day_time")));
			// System.err.println(DateUtils.getDateOfString(map.get("month_last_day_time")));

			System.out.println(12 % 12);
			System.out.println(DateUtils.getSpecifiedMonthAfter("2017-02-15 00:00:00", 1));
			// 当前年.
			int year = Integer.valueOf(CalendarUtil.getYear());
			// 当前月.
			int month = Integer.valueOf(CalendarUtil.getMonth());
			System.out.println("当前年：" + year);
			System.out.println("当前月：" + month);
			if (month % 12 == 0) { // 满12个月，进1个月.
				year = year + 1;
			}
			// 下一月.
			int nextMonth = (month % 12) + 1;

			System.err.println("下月十五日：" + "".concat(String.valueOf(year)).concat("-").concat(String.valueOf(nextMonth).concat("-")).concat("15 00:00:00"));

			System.out.println(isLeapYear(2017));
			System.out.println(isLeapYear(2018));
			System.out.println(isLeapYear(2019));
			System.out.println(isLeapYear(2020));

			projectPlanInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
