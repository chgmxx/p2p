/**
 * Copyright &copy; 2012-2014 <a
 * href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.credit.service.audit;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.credit.dao.audit.CreditAuditInfoDao;
import com.power.platform.credit.entity.audit.CreditAuditInfo;

/**
 * 借款审核信息Service
 * 
 * @author Roy
 * @version 2019-01-16
 */
@Service("creditAuditInfoService")
public class CreditAuditInfoService extends CrudService<CreditAuditInfo> {

	@Resource
	private CreditAuditInfoDao creditAuditInfoDao;

	@Override
	protected CrudDao<CreditAuditInfo> getEntityDao() {

		return creditAuditInfoDao;
	}

}