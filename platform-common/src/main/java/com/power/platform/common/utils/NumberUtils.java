package com.power.platform.common.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 
 * 类: NumberUtils <br>
 * 描述: 数字工具类. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年1月6日 下午1:30:19
 */
public class NumberUtils {

	/**
	 * 默认除法运算精度
	 */
	private static final int DEF_DIV_SCALE = 10;

	/**
	 * 
	 * methods: round <br>
	 * description: 提供精确的小数四舍五入处理 <br>
	 * author: Mr.Roy <br>
	 * date: 2018年12月4日 下午3:24:59
	 * 
	 * @param v
	 *            需要四舍五入的数字
	 * @param scale
	 *            小数点后保留几位
	 * @return 四舍五入的结果
	 */
	public static double round(double v, int scale) {

		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		BigDecimal b = new BigDecimal(Double.toString(v));
		BigDecimal one = new BigDecimal("1");
		return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * 
	 * methods: divide <br>
	 * description: 提供（相对）精确的除法运算当发生除不尽时，由scale参数指定精度到小数点以后10位，以后的数字四舍五入 <br>
	 * author: Mr.Roy <br>
	 * date: 2018年12月4日 下午3:23:26
	 * 
	 * @param v1
	 *            被除数
	 * @param v2
	 *            除数
	 * @return 两个参数的商
	 */
	public static double divide(double v1, double v2) {

		return divide(v1, v2, DEF_DIV_SCALE);
	}

	/**
	 * 
	 * methods: divide <br>
	 * description: 提供（相对）精确的除法运算，当发生除不尽时，由scale参数指定精度，以后的数字四舍五入 <br>
	 * author: Mr.Roy <br>
	 * date: 2018年12月4日 下午3:20:53
	 * 
	 * @param v1
	 *            被除数
	 * @param v2
	 *            除数
	 * @param scale
	 *            表示需要精确到小数点以后几位
	 * @return 两个参数的商
	 */
	public static double divide(double v1, double v2, int scale) {

		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * 
	 * methods: multiply <br>
	 * description: 提供精确的乘法运算 <br>
	 * author: Mr.Roy <br>
	 * date: 2018年12月4日 下午3:18:10
	 * 
	 * @param v1
	 *            被乘数
	 * @param v2
	 *            乘数
	 * @return 两个参数的积
	 */
	public static double multiply(double v1, double v2) {

		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.multiply(b2).doubleValue();
	}

	/**
	 * 
	 * methods: subtract <br>
	 * description: 提供精确的减法运算 <br>
	 * author: Mr.Roy <br>
	 * date: 2018年12月4日 下午3:17:13
	 * 
	 * @param v1
	 *            被减数
	 * @param v2
	 *            减数
	 * @return 两个参数的差
	 */
	public static double subtract(double v1, double v2) {

		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.subtract(b2).doubleValue();
	}

	/**
	 * 
	 * methods: add <br>
	 * description: 提供精确的加法运算 <br>
	 * author: Mr.Roy <br>
	 * date: 2018年12月4日 下午3:16:05
	 * 
	 * @param v1
	 *            被加数
	 * @param v2
	 *            加数
	 * @return 两个参数的和
	 */
	public static double add(double v1, double v2) {

		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.add(b2).doubleValue();
	}

	/**
	 * 
	 * 方法: scaleDouble <br>
	 * 描述: 保留两位小数. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年1月6日 下午1:31:18
	 * 
	 * @param dou
	 * @return
	 */
	public static Double scaleDouble(Double dou) {

		BigDecimal b = new BigDecimal(dou);
		// ROUND_HALF_EVEN，银行家舍入法，主要在美国使用，四舍六入，五，分两种情况，如果前一位为奇数，则入位，否则舍去。
		dou = b.setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue();
		return dou;
	}

	/**
	 * 
	 * 方法: scaleOneStr <br>
	 * 描述: 保留一位小数. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年9月12日 下午5:06:51
	 * 
	 * @param dou
	 * @return
	 */
	public static String scaleOneStr(double dou) {

		// -.
		BigDecimal b = new BigDecimal(dou);
		// ROUND_HALF_EVEN，银行家舍入法，主要在美国使用，四舍六入，五，分两种情况，如果前一位为奇数，则入位，否则舍去。
		dou = b.setScale(1, BigDecimal.ROUND_HALF_EVEN).doubleValue();
		// 展示两位小数.
		DecimalFormat df = new DecimalFormat("#0.0");
		return df.format(dou);
	}

	/**
	 * 
	 * 方法: scaleDoubleStr <br>
	 * 描述: 保留两位小数. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年6月20日 上午11:33:52
	 * 
	 * @param dou
	 * @return
	 */
	public static String scaleDoubleStr(double dou) {

		// -.
		BigDecimal b = new BigDecimal(dou);
		// ROUND_HALF_EVEN，银行家舍入法，主要在美国使用，四舍六入，五，分两种情况，如果前一位为奇数，则入位，否则舍去。
		dou = b.setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue();
		// 展示两位小数.
		DecimalFormat df = new DecimalFormat("#0.00");
		return df.format(dou);
	}

	/**
	 * 
	 * 方法: scaleThree <br>
	 * 描述: 保留三位小数. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年1月15日 下午5:49:29
	 * 
	 * @param dou
	 * @return
	 */
	public static Double scaleThree(Double dou) {

		BigDecimal b = new BigDecimal(dou);
		// ROUND_HALF_EVEN，银行家舍入法，主要在美国使用，四舍六入，五，分两种情况，如果前一位为奇数，则入位，否则舍去。
		dou = b.setScale(3, BigDecimal.ROUND_HALF_EVEN).doubleValue();
		return dou;
	}

	/**
	 * 
	 * 方法: scaleThreeStr <br>
	 * 描述: 保留三位小数转换成String. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年9月12日 下午2:42:31
	 * 
	 * @param dou
	 * @return
	 */
	public static String scaleThreeStr(double dou) {

		// -.
		BigDecimal b = new BigDecimal(dou);
		// ROUND_HALF_EVEN，银行家舍入法，主要在美国使用，四舍六入，五，分两种情况，如果前一位为奇数，则入位，否则舍去。
		dou = b.setScale(3, BigDecimal.ROUND_HALF_EVEN).doubleValue();
		// 展示三位小数.
		DecimalFormat df = new DecimalFormat("#0.000");
		return df.format(dou);
	}

	/**
	 * 
	 * methods: scaleSixStr <br>
	 * description: 保留六位小数转换成String. <br>
	 * author: Roy <br>
	 * date: 2019年5月8日 下午3:53:39
	 * 
	 * @param dou
	 * @return
	 */
	public static String scaleSixStr(double dou) {

		// -.
		BigDecimal b = new BigDecimal(dou);
		// ROUND_HALF_EVEN，银行家舍入法，主要在美国使用，四舍六入，五，分两种情况，如果前一位为奇数，则入位，否则舍去。
		dou = b.setScale(6, BigDecimal.ROUND_HALF_EVEN).doubleValue();
		// 展示三位小数.
		DecimalFormat df = new DecimalFormat("#0.000000");
		return df.format(dou);
	}

	/**
	 * 
	 * 方法: scaleFourStr <br>
	 * 描述: 保留四位小数转换成String. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年9月12日 下午4:03:02
	 * 
	 * @param dou
	 * @return
	 */
	public static String scaleFourStr(double dou) {

		// -.
		BigDecimal b = new BigDecimal(dou);
		// ROUND_HALF_EVEN，银行家舍入法，主要在美国使用，四舍六入，五，分两种情况，如果前一位为奇数，则入位，否则舍去。
		dou = b.setScale(4, BigDecimal.ROUND_HALF_EVEN).doubleValue();
		// 展示三位小数.
		DecimalFormat df = new DecimalFormat("#0.0000");
		return df.format(dou);
	}

	public static void main(String[] args) {

		Double amount = 1000000D;
		Double rate = 64600D;
		rate = (rate / 1000000) * 100;

		Double a = 156451D;
		Double b = 12313.45456D;

		System.out.println(add(a, b));
		System.out.println(subtract(a, b));
		System.out.println(multiply(a, b));
		System.out.println(scaleDoubleStr(multiply(a, b)));
		System.out.println(divide(a, b));
		System.out.println(multiply(divide(a, b), b));
		System.out.println(scaleDoubleStr(multiply(divide(a, b), b)));
		System.out.println(scaleDouble(multiply(divide(a, b), b)));

		System.out.println(round(rate, 2));

		System.out.println(scaleOneStr(rate));
		System.out.println(scaleDoubleStr(0.00));
		System.out.println(scaleThreeStr(amount));
		System.out.println(scaleFourStr(amount));
		System.out.println(scaleFourStr(rate));
		System.out.println(NumberUtils.divide(8, 100D));
		System.out.println(scaleSixStr(NumberUtils.divide(8, 100D)));

	}

}
