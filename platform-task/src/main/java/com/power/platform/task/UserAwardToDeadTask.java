package com.power.platform.task;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.power.platform.bouns.services.UserAwardService;

@Service("userAwardToDeadTask")
@Lazy(false)
public class UserAwardToDeadTask {
	private static final Logger logger = Logger.getLogger(UserAwardToDeadTask.class);
	
	@Autowired
	private UserAwardService userAwardService;
	
	/**
	 * 已失效兑奖记录每5分钟执行一次
	 * 
	 */
	@Scheduled(cron="0 0/5 * * * ?")
	public void getUserAwardToDead(){
		
		try {
			logger.info("定时器UserAwardToDeadTask查询已失效兑奖记录开始");
			userAwardService.checkTodead();
			logger.info("定时器UserAwardToDeadTask查询已失效兑奖记录结束");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("定时器UserAwardToDeadTask查询已失效兑奖记录异常");
		}

	} 
}
