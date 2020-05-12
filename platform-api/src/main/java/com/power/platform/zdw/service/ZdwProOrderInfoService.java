/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.zdw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.zdw.dao.ZdwProOrderInfoDao;
import com.power.platform.zdw.entity.ZdwProOrderInfo;

/**
 * 中等网满标落单Service
 * 
 * @author Roy
 * @version 2019-07-12
 */
@Service
@Transactional(readOnly = false)
public class ZdwProOrderInfoService extends CrudService<ZdwProOrderInfo> {

	@Autowired
	private ZdwProOrderInfoDao zdwProOrderInfoDao;

	@Override
	protected CrudDao<ZdwProOrderInfo> getEntityDao() {

		return zdwProOrderInfoDao;
	}

}