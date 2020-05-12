/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.coupon.dao;

import java.util.List;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.coupon.entity.CouponInfoUser;

/**
 * 客户优惠券信息DAO接口
 * @author Mr.Jia
 * @version 2016-01-21
 */
@MyBatisDao
public interface CouponInfoUserDao extends CrudDao<CouponInfoUser> {
	
	public List<CouponInfoUser> findByUserInfo(CouponInfoUser couponInfoUser);
	
	public List<CouponInfoUser> findByEndTime();
	
	public void updateStateByEndTime();
	
	
}