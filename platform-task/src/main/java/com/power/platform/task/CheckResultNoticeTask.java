package com.power.platform.task;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.power.platform.regular.service.WloanTermInvestService;

@Service("checkResultNoticeTask")
@Lazy(false)
public class CheckResultNoticeTask {
	
	private static final Logger logger = Logger.getLogger(CheckResultNoticeTask.class);
	@Resource
	private WloanTermInvestService wloanTermInvestService;

	
	// @Scheduled(cron = "0 0 23 * * ?")
	public void runJob(){
		logger.info("定时器CheckResultNoticeTask对账结果确认开始");
		try{
	    //N1。清空表数据
		logger.info("商户充值对账确认开始");
		// wloanTermInvestService.notice("E");
		logger.info("商户充值对账确认结束");
		//N2.连连客户对账
		logger.info("商户提现对账确认开始");
		// wloanTermInvestService.notice("I");
		logger.info("商户提现对账确认结束");
		//N3.
		logger.info("商户交易对账确认开始");
		// wloanTermInvestService.notice("T");
		logger.info("商户交易对账确认结束");

		}catch (Exception e) {
			e.printStackTrace();
			logger.info("【定时器CheckResultNoticeTask对账结果确认问题");
		}
	}
}
