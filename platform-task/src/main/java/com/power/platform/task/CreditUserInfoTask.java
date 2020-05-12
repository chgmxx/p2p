package com.power.platform.task;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.power.platform.cgb.entity.CgbUserBankCard;
import com.power.platform.cgb.service.CgbUserBankCardService;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.trandetail.service.UserTransDetailService;
import com.power.platform.userinfo.service.UserCheckAccountService;

@Service("creditUserInfoTask")
@Lazy(false)
public class CreditUserInfoTask {
	
	private static final Logger logger = Logger.getLogger(CreditUserInfoTask.class);
	@Resource
	private UserTransDetailService userTransDetailService;
	@Resource
	private CgbUserBankCardService cgbUserBankCardService;
	
//	@Scheduled(cron = "0 0 10 * * ?")
//	public void runJob(){
//		logger.info("定时器creditUserInfoTask修改开户未审核账户开始");
//		try{
//	    //N1。获取未审核账户
//		
//		String dateString = DateUtils.getDateBefore();
//		Date date = DateUtils.parseDate(dateString);
////		CgbUserBankCard cgbUserBankCard = new CgbUserBankCard();
////		cgbUserBankCard.setCreateDate(date);
//		List<CgbUserBankCard> list =cgbUserBankCardService.findState0(dateString);
//		if(list != null &&list.size()>0){
//			for(CgbUserBankCard cgbUserBankCard2:list){
//				//修改对应信息状态为2
//				Integer a = cgbUserBankCardService.updateState2(cgbUserBankCard2.getId());
//				if(a ==1 ){
//					System.out.println("修改成功！");
//				}
//			}
//		}
//		//N2.客户对账
//		}catch (Exception e) {
//			e.printStackTrace();
//			logger.info("【定时器creditUserInfoTask修改开户未审核账户出现问题");
//		}
//	}
}
