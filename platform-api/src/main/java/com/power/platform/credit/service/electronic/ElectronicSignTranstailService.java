/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.credit.service.electronic;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.credit.dao.electronic.ElectronicSignTranstailDao;
import com.power.platform.credit.entity.electronic.ElectronicSignTranstail;

/**
 * 电子签章明细Service
 * 
 * @author jice
 * @version 2018-03-19
 */
@Service("electronicSignTranstailService")
@Transactional(readOnly = false)
public class ElectronicSignTranstailService extends
		CrudService<ElectronicSignTranstail> {

	@Resource
	private ElectronicSignTranstailDao electronicSignTranstailDao;

	@Override
	protected CrudDao<ElectronicSignTranstail> getEntityDao() {
		return electronicSignTranstailDao;
	}

}