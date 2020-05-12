package com.power.platform.bouns.services;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.bouns.dao.UserBounsHistoryDao;
import com.power.platform.bouns.entity.UserBounsHistory;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.service.CrudService;
import com.power.platform.userinfo.entity.UserInfo;

/**
 * 用户积分历史明细Service
 * 
 * @author Mr.Jia
 * @version 2016-12-13
 */

@Service
@Transactional(readOnly = true)
public class UserBounsHistoryService extends CrudService<UserBounsHistory> {

	// 积分类型 - 投资
	public static final String BOUNS_TYPE_INVEST = "0";
	// 积分类型 - 注册
	public static final String BOUNS_TYPE_REGIST = "1";
	// 积分类型 - 邀请好友
	public static final String BOUNS_TYPE_REQUEST = "2";
	// 积分类型 - 签到
	public static final String BOUNS_TYPE_SIGNED = "3";
	// 积分类型 - 抽奖
	public static final String BOUNS_TYPE_LOTTERY_DRAW = "4";
	// 积分类型 - 兑换奖品
	public static final String BOUNS_TYPE_CASH_LOTTERY = "5";	
	/**
	 * 好友投资.
	 */
	public static final String BOUNS_TYPE_FRIEND_INVEST = "6";
	// 积分类型 - 流标扣除
	public static final String BOUNS_TYPE_MISCARRY = "7";

	@Resource
	private UserBounsHistoryDao userBounsHistoryDao;

	@Override
	protected CrudDao<UserBounsHistory> getEntityDao() {

		return userBounsHistoryDao;
	}

	/**
	 * 添加获取积分历史明细方法
	 * 
	 * @param userBounsHistory
	 * @return
	 */
	@Transactional(readOnly = false)
	public int insert(UserBounsHistory userBounsHistory) {

		return userBounsHistoryDao.insert(userBounsHistory);
	}

	/**
	 * 获取获奖列表
	 * 
	 * @param userBounsHistory
	 * @return
	 */
	public List<UserBounsHistory> findBounsHistoryList(UserBounsHistory userBounsHistory) {

		// TODO Auto-generated method stub
		return userBounsHistoryDao.findBounsHistoryList(userBounsHistory);
	}

	public Double bounsTotalAmount(UserInfo user) {

		Double bounsTotalAmount = userBounsHistoryDao.bounsTotalAmount(user.getId());
		return bounsTotalAmount;
	}

	public List<UserBounsHistory> findInviteByUserId(UserBounsHistory uBounsHistory) {

		// TODO Auto-generated method stub
		return userBounsHistoryDao.findInviteByUserId(uBounsHistory);
	}

	public Page<UserBounsHistory> findInvitePageByUserId(Page<UserBounsHistory> page, UserBounsHistory uBounsHistory) {

		uBounsHistory.setPage(page);
		page.setList(userBounsHistoryDao.findInviteByUserId(uBounsHistory));
		return page;
	}

	public Page<UserBounsHistory> findFriendsIntegralByTransId(Page<UserBounsHistory> page, UserBounsHistory uBounsHistory) {

		uBounsHistory.setPage(page);
		page.setList(userBounsHistoryDao.findFriendsIntegralByTransId(uBounsHistory));
		return page;
	}

	/**
	 * 中奖列表
	 * 
	 * @param page
	 * @param userBounsHistory
	 * @return
	 */
	public Page<UserBounsHistory> findPage1(Page<UserBounsHistory> page, UserBounsHistory userBounsHistory) {

		// TODO Auto-generated method stub
		userBounsHistory.setPage(page);
		page.setList(userBounsHistoryDao.findBounsHistoryList(userBounsHistory));
		return page;
	}

}