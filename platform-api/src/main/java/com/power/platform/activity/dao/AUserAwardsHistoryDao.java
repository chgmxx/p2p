package com.power.platform.activity.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.power.platform.activity.entity.AUserAwardsHistory;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;

/**
 * 
 * 类: AUserAwardsHistoryDao <br>
 * 描述: 活动客户奖励历史DAO接口. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年4月20日 上午11:29:51
 */
@MyBatisDao
public interface AUserAwardsHistoryDao extends CrudDao<AUserAwardsHistory> {

	/**
	 * 
	 * 方法: findRateCouponList <br>
	 * 描述: 加息券列表. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年5月10日 下午7:20:51
	 * 
	 * @param aUserAwardsHistory
	 * @return
	 */
	public abstract List<AUserAwardsHistory> findRateCouponList(AUserAwardsHistory aUserAwardsHistory);

	/**
	 * 
	 * 方法: getUserVouchersList <br>
	 * 描述: 抵用券列表. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年5月10日 下午7:21:59
	 * 
	 * @param aUserAwardsHistory
	 * @return
	 */
	public abstract List<AUserAwardsHistory> findVouchersList(AUserAwardsHistory aUserAwardsHistory);

	/**
	 * 根据投资ID查询抵用劵信息
	 * @param bidId
	 * @return
	 */
	public abstract AUserAwardsHistory findByBidId(@Param("bidId")String bidId);

	public abstract int deleteBy(@Param("id") String id);

}