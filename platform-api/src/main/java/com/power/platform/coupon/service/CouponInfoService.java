package com.power.platform.coupon.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.coupon.dao.CouponInfoDao;
import com.power.platform.coupon.entity.CouponInfo;

@Service("couponInfoService")
@Transactional(readOnly=true)
public class CouponInfoService extends CrudService<CouponInfo> {
	
	@Resource
	private CouponInfoDao couponInfoDao;
	
	protected CrudDao<CouponInfo> getEntityDao() {
		return couponInfoDao;
	}

	public void updateState(CouponInfo couponInfo) {
		couponInfoDao.updateState(couponInfo);
	}

}
