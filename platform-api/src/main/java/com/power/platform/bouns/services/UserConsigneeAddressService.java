package com.power.platform.bouns.services;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.bouns.dao.UserConsigneeAddressDao;
import com.power.platform.bouns.entity.UserConsigneeAddress;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;

/**
 * 用户收货地址Service
 * @author Mr.Jia
 * @version 2016-12-13
 */
@Service
@Transactional(readOnly = true)
public class UserConsigneeAddressService extends CrudService<UserConsigneeAddress> {

	// 是否默认收货地址 - 是
	public static final String IS_DEFAULT_ADDRESS_YES = "1";
	
	// 是否默认收货地址 - 否
	public static final String IS_DEFAULT_ADDRESS_NO = "0";
	
	
	@Resource
	private UserConsigneeAddressDao userConsigneeAddressDao;
	
	
	@Override
	protected CrudDao<UserConsigneeAddress> getEntityDao() {
		return userConsigneeAddressDao;
	}

	
	@Transactional(readOnly = false)
	public int insert(UserConsigneeAddress userConsigneeAddress) {
		return userConsigneeAddressDao.insert(userConsigneeAddress);
	}
	
	@Transactional(readOnly = false)
	public int update(UserConsigneeAddress userConsigneeAddress) {
		return userConsigneeAddressDao.update(userConsigneeAddress);
	}
}