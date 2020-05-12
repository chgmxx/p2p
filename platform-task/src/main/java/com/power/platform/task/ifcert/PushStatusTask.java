package com.power.platform.task.ifcert;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.power.platform.cgb.dao.ZtmgOrderInfoDao;
import com.power.platform.cgb.entity.ZtmgOrderInfo;
import com.power.platform.cgb.service.ZtmgOrderInfoService;
import com.power.platform.cgbpay.config.ServerURLConfig;
import com.power.platform.ifcert.status.service.ScatterInvestStatusDataAccessService;
import com.power.platform.plan.dao.WloanTermProjectPlanDao;
import com.power.platform.plan.entity.WloanTermProjectPlan;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.regular.service.WloanTermProjectService;

@Service("pushStatusTask")
@Lazy(false)
public class PushStatusTask {

	private static final Logger logger = Logger.getLogger(PushStatusTask.class);

	@Resource
	private ZtmgOrderInfoDao ztmgOrderInfoDao;
	@Resource
	private WloanTermProjectPlanDao wloanTermProjectPlanDao;
	@Resource
	private WloanTermProjectService wloanTermProjectService;
	@Resource
	private ScatterInvestStatusDataAccessService scatterInvestStatusDataAccessService;

	@Scheduled(cron = "0 0 23 * * ?")
	public void pushStatusTask() {

		logger.info("推送散标的结束状态---开始");
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date = new Date();
			String newStr = sdf.format(date);
			String starStr = newStr + " 00:00:00";
			String endStr = newStr + " 23:10:00";
			List<ZtmgOrderInfo> orderList = ztmgOrderInfoDao.findZtmgOrderInfo(starStr, endStr);
			for (ZtmgOrderInfo ztmgOrderInfo : orderList) {
				// WloanTermProjectPlan wrp = wloanTermProjectPlanDao.getBySubOrderId(ztmgOrderInfo.getOrderId());
				// 利用订单Id查询标的
				WloanTermProject wloanTermProject = wloanTermProjectService.getWloanTermProject(ztmgOrderInfo.getOrderId());
				if (wloanTermProject != null) {
					if (WloanTermProjectService.FINISH.equals(wloanTermProject.getState())) {
						if (ServerURLConfig.IS_REAL_TIME_PUSH) {
							// 4.1.3 散标状态-（结束）
							// 每天定时推送散标状态
							Map<String, Object> map = scatterInvestStatusDataAccessService.pushScatterInvestStatus(wloanTermProject.getId());
							logger.info("时时推送散标状态:" + map.get("respMsg").toString());
						}
					}
				}
			}
			logger.info("推送散标 的结束状态---结束");
			System.out.println("时时推送散标 的结束状态");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("【推送散标的结束状态出现问题");
		}
	}
}
