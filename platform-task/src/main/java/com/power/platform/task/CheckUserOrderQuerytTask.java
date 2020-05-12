package com.power.platform.task;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.power.platform.userinfo.service.UserCheckOrderService;

@Service("checkUserOrderQuerytTask")
@Lazy(false)
public class CheckUserOrderQuerytTask {

	private static final Logger logger = Logger.getLogger(CheckUserOrderQuerytTask.class);
	@Resource
    private UserCheckOrderService userCheckOrderService;
	
	// @Scheduled(cron = "0 0 17 * * ?")
	public void runJob(){
		logger.info("定时器CheckUserOrderQuerytTask筛选用户充值提现订单开始");
		try{
	    //N1。清空表数据
		// int i = userCheckOrderService.deleteAll();
		// if(i>=0){
			//N2.客户充值提现订单对账
			// userCheckOrderService.checkOrder();
		// }
		}catch (Exception e) {
			e.printStackTrace();
			logger.info("【定时器CheckUserOrderQuerytTask筛选用户充值提现订单出现问题");
		}
	}
}
