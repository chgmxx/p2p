package com.power.platform.ifcert.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.power.platform.common.utils.DateUtils;

/**
 * 
 * class: ServiceCostUtil <br>
 * description: 平台服务费率表. <br>
 * author: Roy <br>
 * date: 2019年5月10日 上午10:20:46
 */
public class ServiceCostUtil {

	public static class ServiceCostPojo {

		private String span;
		private String serviceRate;

		public String getSpan() {

			return span;
		}

		public void setSpan(String span) {

			this.span = span;
		}

		public String getServiceRate() {

			return serviceRate;
		}

		public void setServiceRate(String serviceRate) {

			this.serviceRate = serviceRate;
		}

	}

	/**
	 * 
	 * methods: axtServiceCost <br>
	 * description: 安心投服务费率表. <br>
	 * author: Roy <br>
	 * date: 2019年5月10日 上午10:25:24
	 * 
	 * @param onlineDate
	 *            标的上线日期
	 * @return
	 */
	public static List<ServiceCostPojo> axtServiceCost(Date onlineDate) {

		// 安心投项目上线日期时间节点，2019-03-01 00:00:00.
		String endPointDateStr = "2019-03-01 00:00:00";
		// 项目上线时间.
		String onlineDateStr = DateUtils.formatDate(onlineDate, "yyyy-MM-dd HH:mm:ss");
		List<ServiceCostPojo> axtServiceCostList = new ArrayList<ServiceCostPojo>();

		if (DateUtils.compare_date(onlineDateStr, endPointDateStr)) { // 之前.
			for (int i = 0; i < 4; i++) {
				if (i == 0) {
					ServiceCostPojo scp = new ServiceCostPojo();
					scp.setSpan("30");
					scp.setServiceRate("3");
					axtServiceCostList.add(scp);
				} else if (i == 1) {
					ServiceCostPojo scp = new ServiceCostPojo();
					scp.setSpan("90");
					scp.setServiceRate("3");
					axtServiceCostList.add(scp);
				} else if (i == 2) {
					ServiceCostPojo scp = new ServiceCostPojo();
					scp.setSpan("180");
					scp.setServiceRate("3");
					axtServiceCostList.add(scp);
				} else if (i == 3) {
					ServiceCostPojo scp = new ServiceCostPojo();
					scp.setSpan("360");
					scp.setServiceRate("3.5");
					axtServiceCostList.add(scp);
				}
			}
		} else { // 之后.
			for (int i = 0; i < 4; i++) {
				if (i == 0) {
					ServiceCostPojo scp = new ServiceCostPojo();
					scp.setSpan("30");
					scp.setServiceRate("3");
					axtServiceCostList.add(scp);
				} else if (i == 1) {
					ServiceCostPojo scp = new ServiceCostPojo();
					scp.setSpan("90");
					scp.setServiceRate("4");
					axtServiceCostList.add(scp);
				} else if (i == 2) {
					ServiceCostPojo scp = new ServiceCostPojo();
					scp.setSpan("180");
					scp.setServiceRate("4");
					axtServiceCostList.add(scp);
				} else if (i == 3) {
					ServiceCostPojo scp = new ServiceCostPojo();
					scp.setSpan("360");
					scp.setServiceRate("4.5");
					axtServiceCostList.add(scp);
				}
			}
		}

		return axtServiceCostList;
	}

}
