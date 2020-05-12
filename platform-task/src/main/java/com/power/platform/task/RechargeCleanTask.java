package com.power.platform.task;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.power.platform.pay.recharge.entity.UserRecharge;
import com.power.platform.pay.recharge.service.UserRechargeService;
import com.power.platform.trandetail.entity.UserTransDetail;
import com.power.platform.trandetail.service.UserTransDetailService;

/**
 * 清理十五分钟前还是充值状态的定时
 * @author caozhi
 */
@Service("rechargeCleanTask")
@Lazy(false)
public class RechargeCleanTask {
	
	private static final Logger logger = Logger.getLogger(RechargeCleanTask.class);
	@Autowired
	private UserRechargeService userRechargeService;
	@Autowired
	private UserTransDetailService userTransDetailService;
	// @Scheduled(cron = "0 */15 * * * ?")
	public void runJob(){	
		try {
			System.out.println("======================================");
			logger.info("定时器RechargeCleanTask清理充值中");
			Calendar calerder = Calendar.getInstance();
			calerder.add(Calendar.MINUTE, -15);
			Date date = calerder.getTime();
			List<UserRecharge> rechargeList = userRechargeService.getUncompleteRecharge(date);
			if (rechargeList !=null && rechargeList.size() >0) {
				for (UserRecharge userRecharge : rechargeList) {
					userRecharge.setState(UserRecharge.RECHARGE_FAIL);
					userRecharge.setEndDate(new Date());
					userRechargeService.save(userRecharge);				
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("【定时器RechargeCleanTask清理投标中出现问题");
		}

	}
	
}
