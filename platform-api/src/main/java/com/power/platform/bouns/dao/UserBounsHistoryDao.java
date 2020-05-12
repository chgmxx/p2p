package com.power.platform.bouns.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.power.platform.bouns.entity.UserBounsHistory;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;

/**
 * 用户积分历史明细DAO接口
 * 
 * @author Mr.Jia
 * @version 2016-12-13
 */
@MyBatisDao
public interface UserBounsHistoryDao extends CrudDao<UserBounsHistory> {

	List<UserBounsHistory> findBounsHistoryList(UserBounsHistory entity);

	// 邀请好友总积分
	public double bounsTotalAmount(@Param("userId") String userId);

	List<UserBounsHistory> findInviteByUserId(UserBounsHistory uBounsHistory);

	/**
	 * 
	 * methods: findFriendsIntegralByTransId <br>
	 * description: 邀请好友（统计好友累计出借金额和累计积分）. <br>
	 * author: Mr.Roy <br>
	 * date: 2019年1月7日 上午11:30:30
	 * 
	 * @param uBounsHistory
	 * @return
	 */
	List<UserBounsHistory> findFriendsIntegralByTransId(UserBounsHistory uBounsHistory);

}