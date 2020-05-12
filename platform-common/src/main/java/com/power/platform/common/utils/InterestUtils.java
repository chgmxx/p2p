package com.power.platform.common.utils;

/**
 * 
 * 类: InterestUtils <br>
 * 描述: CicMorgan-利息计算. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2017年12月4日 下午3:00:57
 */
public class InterestUtils {

	/**
	 * 每一期为30天.
	 */
	private static final Integer SPAN_30 = 30;

	/**
	 * 
	 * methods: getInvInterest <br>
	 * description: 获取出借人出借利息，以30天为一期计算月利息，月利息*期数=出借利息. <br>
	 * author: Roy <br>
	 * date: 2019年7月22日 下午4:40:32
	 * 
	 * @param amount
	 *            出借金额
	 * @param annualRate
	 *            出借年化收益率
	 * @param span
	 *            出借天数（30天为一期）
	 * @return
	 */
	public static Double getInvInterest(Double amount, Double annualRate, Integer span) {

		// 出借利息.
		Double invInterest = 0D;

		// 月利息.
		Double monthInterest = getMonthInterestFormat(amount, annualRate);
		if (span >= SPAN_30) {
			if (span % SPAN_30 == 0) {
				Integer num = span / SPAN_30;
				invInterest = NumberUtils.scaleDouble(monthInterest * num);
			}
		}
		return invInterest;
	}

	/**
	 * 
	 * 方法: getDayInterest <br>
	 * 描述: 每日利息，原始小数位. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年12月4日 下午4:54:19
	 * 
	 * @param amount
	 * @param annualRate
	 * @return
	 */
	public static Double getDayInterest(Double amount, Double annualRate) {

		if (annualRate > 1) {
			annualRate = annualRate / 100;
		}
		return (amount * annualRate) / 365;
	}

	/**
	 * 
	 * 方法: getDayInterestFormat <br>
	 * 描述: 每日利息，保留两位小数. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年12月4日 下午3:02:58
	 * 
	 * @param amount
	 * @param annualRate
	 * @return
	 */
	public static Double getDayInterestFormat(Double amount, Double annualRate) {

		if (annualRate > 1) {
			annualRate = annualRate / 100;
		}
		return format((amount * annualRate) / 365);
	}

	/**
	 * 计算每期（月）利息(四舍五入保留两位)
	 * 
	 * @param amount
	 *            贷款金额
	 * @param annualRate
	 *            年化利率
	 * @return
	 */
	public static Double getMonthInterestFormat(Double amount, Double annualRate) {

		if (annualRate > 1) {
			annualRate = annualRate / 100;
		}
		return format(((amount * annualRate) / 365) * 30);
	}

	/**
	 * 计算每期（月）利息
	 * 
	 * @param amount
	 *            贷款金额
	 * @param annualRate
	 *            年化利率
	 * @return
	 */
	public static Double getMonthInterest(Double amount, Double annualRate) {

		if (annualRate > 1) {
			annualRate = annualRate / 100;
		}
		return ((amount * annualRate) / 365) * 30;
	}

	/**
	 * 
	 * 方法: format <br>
	 * 描述: 保留两位小数. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年12月4日 下午2:58:47
	 * 
	 * @param number
	 * @return
	 */
	public static Double format(Double number) {

		return NumberUtils.scaleDouble(number);
	}

	public static void main(String[] args) {

		System.out.println("每月利息：" + getMonthInterestFormat(1000D, 8.25D));
		System.out.println("出借利息：" + getInvInterest(1000D, 8.25D, 90));

		// System.out.println("每日利息：" + getDayInterest(500000D, 0.09));
		// System.out.println("每日利息：" + getDayInterestFormat(500000D, 0.09));
		// System.out.println("每月利息：" + getMonthInterest(500000D, 0.09));
		// System.out.println("每月利息：" + getMonthInterestFormat(500000D, 0.09));
		//
		// System.out.println(10 % 3);

		// int i = 90;
		// while (true) {
		// System.err.println(i);
		// --i;
		// if (i <= 60) {
		// break;
		// }
		// }

	}
}
