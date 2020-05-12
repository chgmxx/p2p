package com.power.platform.task;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.power.platform.regular.service.WloanTermInvestService;
import com.power.platform.trandetail.service.UserTransDetailService;
import com.power.platform.userinfo.service.UserCheckAccountService;

@Service("checkInvestDetailTask")
@Lazy(false)
public class CheckInvestDetailTask {
	
	private static final Logger logger = Logger.getLogger(CheckInvestDetailTask.class);
	@Resource
	private WloanTermInvestService wloanTermInvestService;
	
	// @Scheduled(cron = "0 0/5 * * * ?")
	public void runJob(){
		logger.info("定时器CheckInvestDetailTask更新出借订单开始");
		try{
			// wloanTermInvestService.checkInvestDetail();
		}catch (Exception e) {
			e.printStackTrace();
			logger.info("【定时器CheckInvestDetailTask更新出借订单出现问题");
		}
	}
}
