package com.power.platform.task;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.power.platform.cgb.service.CgbCheckAccountService;
import com.power.platform.trandetail.service.UserTransDetailService;
import com.power.platform.userinfo.service.UserCheckAccountService;

@Service("checkUserAccountTask")
@Lazy(false)
public class CheckUserAccountTask {
	
	private static final Logger logger = Logger.getLogger(CheckUserAccountTask.class);
	@Resource
	private UserTransDetailService userTransDetailService;
	@Resource
	private UserCheckAccountService userCheckAccountService;
	@Resource
	private CgbCheckAccountService cgbCheckAccountService;
	
	// @Scheduled(cron = "0 59 23 * * ?")
	public void runJob(){
		logger.info("定时器CheckUserAccountTask筛选问题账户开始");
		try{
	    //N1。清空表数据
		// int i =userCheckAccountService.deleteAll();
		//N2.连连客户对账
		logger.info("连连账户对账开始");
		// userTransDetailService.checkAccount();
		logger.info("连连账户对账结束");
		//N3.清空对账表
		// int j =cgbCheckAccountService.deleteAll();
		//N4.存管宝账户对账
		logger.info("存管宝账户对账开始");
		// userTransDetailService.checkCGBAccount();
		logger.info("存管宝账户对账结束");
		}catch (Exception e) {
			e.printStackTrace();
			logger.info("【定时器CheckUserAccountTask筛选问题账户出现问题");
		}
	}
}
