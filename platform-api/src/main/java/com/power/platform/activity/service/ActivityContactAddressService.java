/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.activity.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.activity.dao.ActivityContactAddressDao;
import com.power.platform.activity.entity.ActivityContactAddress;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;

/**
 * 用户活动联系地址Service
 * 
 * @author Roy
 * @version 2019-08-14
 */
@Service
@Transactional(readOnly = false)
public class ActivityContactAddressService extends CrudService<ActivityContactAddress> {

	@Autowired
	private ActivityContactAddressDao activityContactAddressDao;

	@Override
	protected CrudDao<ActivityContactAddress> getEntityDao() {

		return activityContactAddressDao;
	}

}