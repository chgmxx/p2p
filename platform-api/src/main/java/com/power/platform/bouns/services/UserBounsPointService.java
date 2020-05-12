package com.power.platform.bouns.services;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.bouns.dao.UserBounsPointDao;
import com.power.platform.bouns.entity.UserBounsPoint;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.common.utils.IdGen;
import com.power.platform.current.service.WloanCurrentPoolService;
import com.power.platform.userinfo.dao.UserInfoDao;
import com.power.platform.userinfo.entity.UserInfo;


/**
 * 用户积分信息Service
 * @author Mr.Jia
 * @version 2016-12-13
 */
@Service
@Transactional(readOnly = true)
public class UserBounsPointService extends CrudService<UserBounsPoint> {
	
	public static final Integer USER_DRAW_LOTTERY_BOUNS = 10;

	private static final Logger logger = Logger.getLogger(WloanCurrentPoolService.class);
	
	@Resource
	private UserBounsPointDao userBounsPointDao;
	@Resource
	private UserInfoDao userInfoDao;
	
	protected CrudDao<UserBounsPoint> getEntityDao() {
		return userBounsPointDao;
	}
	
	
	/**
	 * 根据userId 获取用户积分账户信息
	 * 如果没有账户则新建账户
	 * 如果有则返回账户信息
	 * @param userId
	 * @return
	 */
	@Transactional(readOnly = false)
	public UserBounsPoint getUserBounsPoint(String userId){
		UserBounsPoint userBounsPoint = new UserBounsPoint();
		userBounsPoint.setUserId(userId);
		List<UserBounsPoint> userBounsPoints = userBounsPointDao.findList(userBounsPoint);
		if (userBounsPoints != null && userBounsPoints.size() > 0) {
			// 存在用户积分账户信息，不需要新建
			userBounsPoint = userBounsPoints.get(0);
		} else {
			// 用户积分账户不存在，需要新建
			UserInfo userInfo = userInfoDao.get(userId);
			if(userInfo==null){
				userInfo = userInfoDao.getCgb(userId);
			}
			userBounsPoint.setId(IdGen.uuid());
			userBounsPoint.setUserId(userId);
			userBounsPoint.setScore(0);
			userBounsPoint.setCreateDate(new Date());
			userBounsPoint.setUpdateDate(new Date());
			userBounsPoint.setUserInfo(userInfo);
			userBounsPointDao.insert(userBounsPoint);
		}
		return userBounsPoint;	
	}


	/**
	 * 修改用户积分账户信息
	 * @param userBounsPoint
	 * @return
	 */
	@Transactional(readOnly = false)
	public int update(UserBounsPoint userBounsPoint) {
		return userBounsPointDao.update(userBounsPoint);
	}
	
	
	
}


