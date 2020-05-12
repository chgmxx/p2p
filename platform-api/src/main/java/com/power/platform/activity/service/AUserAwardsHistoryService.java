package com.power.platform.activity.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.activity.dao.AUserAwardsHistoryDao;
import com.power.platform.activity.entity.AUserAwardsHistory;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.service.CrudService;

/**
 * 
 * 类: AUserAwardsHistoryService <br>
 * 描述: 活动客户奖励历史Service. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年4月20日 上午11:56:20
 */
@Service("aUserAwardsHistoryService")
public class AUserAwardsHistoryService extends CrudService<AUserAwardsHistory> {

	/**
	 * 1：抵用券.
	 */
	public static final String COUPONS_TYPE_1 = "1";
	/**
	 * 2：加息券.
	 */
	public static final String COUPONS_TYPE_2 = "2";
	/**
	 * 0:暂不可用
	 */
	public static final String A_USER_AWARDS_HISTORY_STATE_0 = "0";
	/**
	 * 1：可用，未使用.
	 */
	public static final String A_USER_AWARDS_HISTORY_STATE_1 = "1";
	/**
	 * 2：已使用.
	 */
	public static final String A_USER_AWARDS_HISTORY_STATE_2 = "2";
	/**
	 * 3：逾期的，过期的.
	 */
	public static final String A_USER_AWARDS_HISTORY_STATE_3 = "3";
	/**
	 * 4：使用中.
	 */
	public static final String A_USER_AWARDS_HISTORY_STATE_4 = "4";
	/**
	 * 5：未知状态.
	 */
	public static final String A_USER_AWARDS_HISTORY_STATE_5 = "5";

	@Resource
	private AUserAwardsHistoryDao aUserAwardsHistoryDao;

	/**
	 * 
	 * 方法: findRateCouponPage <br>
	 * 描述: 加息券分页结果集. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年5月10日 下午7:39:18
	 * 
	 * @param page
	 * @param entity
	 * @return
	 */
	public Page<AUserAwardsHistory> findRateCouponPage(Page<AUserAwardsHistory> page, AUserAwardsHistory entity) {

		entity.setPage(page);
		page.setList(aUserAwardsHistoryDao.findRateCouponList(entity));
		return page;
	}

	/**
	 * 
	 * 方法: findRateCoupon <br>
	 * 描述: 获取客户全部加息券. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年5月11日 上午9:45:56
	 * 
	 * @param entity
	 * @return
	 */
	public List<AUserAwardsHistory> findRateCoupon(AUserAwardsHistory entity) {

		return aUserAwardsHistoryDao.findRateCouponList(entity);
	}

	/**
	 * 
	 * 方法: findVouchersPage <br>
	 * 描述: 抵用券分页结果集. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年5月10日 下午7:39:37
	 * 
	 * @param page
	 * @param entity
	 * @return
	 */
	public Page<AUserAwardsHistory> findVouchersPage(Page<AUserAwardsHistory> page, AUserAwardsHistory entity) {

		entity.setPage(page);
		page.setList(aUserAwardsHistoryDao.findVouchersList(entity));
		return page;
	}

	/**
	 * 
	 * 方法: findVouchers <br>
	 * 描述: 获取客户全部抵用券. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年5月11日 上午9:44:40
	 * 
	 * @param entity
	 * @return
	 */
	public List<AUserAwardsHistory> findVouchers(AUserAwardsHistory entity) {

		return aUserAwardsHistoryDao.findVouchersList(entity);
	}

	@Override
	protected CrudDao<AUserAwardsHistory> getEntityDao() {

		logger.info("fn:getEntityDao,{获取当前DAO}");
		return aUserAwardsHistoryDao;
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int insertUserAward(AUserAwardsHistory aUserAwardsHistory) {
		// TODO Auto-generated method stub
		return aUserAwardsHistoryDao.insert(aUserAwardsHistory);
	}

	public AUserAwardsHistory findByBidId(String bidId) {
		// TODO Auto-generated method stub
		return aUserAwardsHistoryDao.findByBidId(bidId);
	}

}