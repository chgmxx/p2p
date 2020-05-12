package com.power.platform.coupon.dao;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.coupon.entity.CouponInfo;

@MyBatisDao
public interface CouponInfoDao extends CrudDao<CouponInfo> {
	public void updateState(CouponInfo couponInfo);
}
