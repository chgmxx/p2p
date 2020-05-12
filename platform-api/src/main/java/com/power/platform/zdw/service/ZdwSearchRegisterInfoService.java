/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.zdw.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.zdw.dao.ZdwSearchRegisterInfoDao;
import com.power.platform.zdw.entity.ZdwSearchRegisterInfo;

/**
 * 中登网应收账款和转让记录登记列表Service
 * 
 * @author Roy
 * @version 2019-07-07
 */
@Service
@Transactional(readOnly = false)
public class ZdwSearchRegisterInfoService extends CrudService<ZdwSearchRegisterInfo> {

	@Resource
	private ZdwSearchRegisterInfoDao zdwSearchRegisterInfoDao;

	@Override
	protected CrudDao<ZdwSearchRegisterInfo> getEntityDao() {

		return zdwSearchRegisterInfoDao;
	}

}