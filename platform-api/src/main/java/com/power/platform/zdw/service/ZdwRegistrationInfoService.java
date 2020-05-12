/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.zdw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.zdw.dao.ZdwRegistrationInfoDao;
import com.power.platform.zdw.entity.ZdwRegistrationInfo;

/**
 * 中登网登记信息Service
 * 
 * @author Roy
 * @version 2019-07-15
 */
@Service
@Transactional(readOnly = true)
public class ZdwRegistrationInfoService extends CrudService<ZdwRegistrationInfo> {

	@Autowired
	private ZdwRegistrationInfoDao zdwRegistrationInfoDao;

	@Override
	protected CrudDao<ZdwRegistrationInfo> getEntityDao() {

		return zdwRegistrationInfoDao;
	}

}