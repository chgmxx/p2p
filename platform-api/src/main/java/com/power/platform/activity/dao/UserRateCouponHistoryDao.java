package com.power.platform.activity.dao;

import java.util.List;

import com.power.platform.activity.entity.UserRateCouponHistory;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;

@MyBatisDao
public interface UserRateCouponHistoryDao extends CrudDao<UserRateCouponHistory> {

	public abstract List<UserRateCouponHistory> findRateCouponList(UserRateCouponHistory rateCouponHistory);

}