package com.power.platform.task;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.power.platform.activity.dao.AUserAwardsHistoryDao;
import com.power.platform.activity.entity.AUserAwardsHistory;
import com.power.platform.activity.service.AUserAwardsHistoryService;

/**
 * 
 * 类: CouponsExpirationTimeTask <br>
 * 描述: 优惠券过期时间，Spring任务调度. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年6月17日 上午9:58:47
 */
@Service
@Lazy(false)
public class CouponsExpirationTimeTask {

	private static final Logger logger = LoggerFactory.getLogger(CouponsExpirationTimeTask.class);

	@Autowired
	private AUserAwardsHistoryService aUserAwardsHistoryService;
	@Resource
	private AUserAwardsHistoryDao aUserAwardsHistoryDao;

	/**
	 * 
	 * 方法: couponsExpirationTimeModifyByState <br>
	 * 描述: 优惠券过期时间定时轮询 <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年6月17日 上午10:07:01
	 */
	@Scheduled(cron = "0 0/30 * * * ?")
	public void couponsExpirationTimeModifyByState() {

		// 当前时间.
		Date nowDate = new Date();

		// --
		/**
		 * type = 1 : 抵用券.
		 */
		logger.info("抵用券过期时间定时轮询...start...");
		AUserAwardsHistory aUserAwardsHistory = new AUserAwardsHistory();
		aUserAwardsHistory.setState(AUserAwardsHistoryService.A_USER_AWARDS_HISTORY_STATE_1);
		List<AUserAwardsHistory> vouchers = aUserAwardsHistoryService.findVouchers(aUserAwardsHistory);
		for (AUserAwardsHistory model : vouchers) {
			Date overdueDate = model.getOverdueDate();
			if (null != overdueDate) {
				boolean flag = overdueDate.before(nowDate);
				if (flag) {
					model.setState(AUserAwardsHistoryService.A_USER_AWARDS_HISTORY_STATE_3);
					model.setValue(model.getaVouchersDic().getAmount().toString());
					model.setOverdueDate(overdueDate);
					model.setUpdateDate(nowDate);
					int updateFlag = aUserAwardsHistoryDao.update(model);
					logger.info("抵用券已过期，更新状态:{}", updateFlag == 1 ? "成功" : "失败");
				}
			}
		}
		logger.info("抵用券过期时间定时轮询...end...");

		/**
		 * type = 2 : 加息券.
		 */
		logger.info("加息券过期时间定时轮询...start...");
		AUserAwardsHistory entity = new AUserAwardsHistory();
		entity.setState(AUserAwardsHistoryService.A_USER_AWARDS_HISTORY_STATE_1);
		List<AUserAwardsHistory> rateCoupons = aUserAwardsHistoryService.findRateCoupon(entity);
		for (AUserAwardsHistory model : rateCoupons) {
			Date overdueDate = model.getOverdueDate();
			if (null != overdueDate) {
				boolean flag = overdueDate.before(nowDate);
				if (flag) {
					model.setState(AUserAwardsHistoryService.A_USER_AWARDS_HISTORY_STATE_3);
					model.setValue(model.getaRateCouponDic().getRate().toString());
					model.setOverdueDate(overdueDate);
					model.setUpdateDate(nowDate);
					int updateFlag = aUserAwardsHistoryDao.update(model);
					logger.info("加息券已过期，更新状态:{}", updateFlag == 1 ? "成功" : "失败");
				}
			}
		}
		logger.info("加息券过期时间定时轮询...end...");
		// --
	}

}
