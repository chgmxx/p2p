package com.power.platform.coupon.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.coupon.dao.CouponInfoUserDao;
import com.power.platform.coupon.entity.CouponInfo;
import com.power.platform.coupon.entity.CouponInfoUser;
import com.power.platform.sys.entity.User;
import com.power.platform.userinfo.entity.UserInfo;

/**
 * 客户优惠券信息Service
 * @author Mr.Jia
 * @version 2016-01-21
 */
@Service("couponInfoUserService")
@Transactional(readOnly = true)
public class CouponInfoUserService extends CrudService<CouponInfoUser> {
	@Resource
	private CouponInfoUserDao couponInfoUserDao;
	
	@Autowired
	private CouponInfoService couponInfoService;

	protected CrudDao<CouponInfoUser> getEntityDao() {
		return couponInfoUserDao;
	}

	@Transactional(readOnly=false,rollbackFor=Exception.class)
	public void saveCouponInfoUser(String userInfoIds,String couponInfoId,User currentUser){
		CouponInfo couponInfo =	couponInfoService.get(couponInfoId);
		String[] userInfoIdGroup =userInfoIds.split(",");
		for (String userInfoId : userInfoIdGroup) {
			CouponInfoUser couponInfoUser =new CouponInfoUser();
			couponInfoUser.setCurrentUser(currentUser);
			couponInfoUser.setCouponInfo(couponInfo);
			UserInfo userInfo = new UserInfo();
			userInfo.setId(userInfoId);
			couponInfoUser.setUserInfo(userInfo);
			couponInfoUser.setEndDate(DateUtils.getAddDaysDate(couponInfo.getOverdue()));
			couponInfoUser.setState(CouponInfoUser.KEYONG_STATE);
			couponInfoUser.preInsert();
			couponInfoUserDao.insert(couponInfoUser);
		}
		if(couponInfo.getState().equals("1")){
			couponInfo.setState("2");
			couponInfoService.updateState(couponInfo);
		}
	}

	public List<CouponInfoUser> findByUserInfo(CouponInfoUser couponInfoUser) {
		return couponInfoUserDao.findByUserInfo(couponInfoUser);
	}

	public List<CouponInfoUser> findByEndTime() {
		return couponInfoUserDao.findByEndTime();
	}

	@Transactional(readOnly=false,rollbackFor=Exception.class)
	public void updateStateByEndTime() {
		couponInfoUserDao.updateStateByEndTime();
	}
	
}