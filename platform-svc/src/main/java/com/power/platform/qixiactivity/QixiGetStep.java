package com.power.platform.qixiactivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class QixiGetStep {
	/**
	 * 里程数：15000 奖励：88元(58元现金+30元抵用券)
	 * 里程数：30000 奖励：168元(108元现金+30元抵用券)
	 * 里程数：150000 奖励：288元(208元现金+80元抵用券)
	 * 里程数：300000 奖励：688元(508元现金+180元抵用券)
	 * 里程数：600000 奖励：1488元(1208元现金+280元抵用券)
	 * 里程数：900000 奖励：1888元(1508元现金+380元抵用券)
	 * 里程数：1500000 奖励：2888元(2008元现金+880元抵用券)
	 */

	public static Integer getSterp(long steps){
		Integer step = 0;
		
		if(steps <= 0){
			return step;
		}
		
		if(steps >= 0 && steps < 15000){					// step = 0
			return step;
		} else if (steps >= 15000 && steps < 20000){		// step = 1
			return step + 1;
		} else if (steps >= 20000 && steps < 25000){		// step = 2
			return step + 2;
		} else if (steps >= 25000 && steps < 30000){		// step = 3
			return step + 3;
		} else if (steps >= 30000 && steps < 70000){		// step = 4
			return step + 4;
		} else if (steps >= 70000 && steps < 110000){		// step = 5
			return step + 5;
		} else if (steps >= 110000 && steps < 150000){		// step = 6
			return step + 6;
		} else if (steps >= 150000 && steps < 200000){		// step = 7
			return step + 7;
		} else if (steps >= 200000 && steps < 250000){		// step = 8
			return step + 8;
		} else if (steps >= 250000 && steps < 300000){		// step = 9
			return step + 9;
		} else if (steps >= 300000 && steps < 400000){		// step = 10
			return step + 10;
		} else if (steps >= 400000 && steps < 500000){		// step = 11
			return step + 11;
		} else if (steps >= 500000 && steps < 600000){		// step = 12
			return step + 12;
		} else if (steps >= 600000 && steps < 700000){		// step = 13
			return step + 13;
		} else if (steps >= 700000 && steps < 800000){		// step = 14
			return step + 14;
		} else if (steps >= 800000 && steps < 900000){		// step = 15
			return step + 15;
		} else if (steps >= 900000 && steps < 1100000){		// step = 16
			return step + 16;
		} else if (steps >= 1100000 && steps < 1300000){	// step = 17
			return step + 17;
		} else if (steps >= 1300000 && steps < 1500000){	// step = 18
			return step + 18;
		} else {											// step = 19
			return step + 19;
		}
		
	}
	
	
	/**
	 * 字符串日期转换为DATE
	 * 
	 * @param sdate
	 *            (String - yyyy-MM-dd HH:mm:ss)
	 * @return Date date
	 */
	public static Date getDateOfString(String sdate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = formatter.parse(sdate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	public static void main(String[] args) {
		System.out.println(getDateOfString("2016-08-01 00:00:00"));
		System.out.println(getDateOfString("2016-08-31 23:59:59"));
	}
}
