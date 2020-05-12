/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.credit.service.userinfo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.service.CrudService;
import com.power.platform.credit.dao.userinfo.CreditUserOperatorDao;
import com.power.platform.credit.entity.userinfo.CreditUserOperator;


/**
 * 借款端，操作人Service
 * @author yb
 * @version 2018-03-08
 */
@Service
@Transactional(readOnly = true)
public class CreditUserOperatorService extends CrudService<CreditUserOperator> {

	@Autowired
	private CreditUserOperatorDao creditUserOperatorDao;
	
	public CreditUserOperator get(String id) {
		return super.get(id);
	}
	
	public List<CreditUserOperator> findList(CreditUserOperator creditUserOperator) {
		return super.findList(creditUserOperator);
	}
	
	public Page<CreditUserOperator> findPage(Page<CreditUserOperator> page, CreditUserOperator creditUserOperator) {
		return super.findPage(page, creditUserOperator);
	}
	
	@Transactional(readOnly = false)
	public void save(CreditUserOperator creditUserOperator) {
		super.save(creditUserOperator);
	}
	
	@Transactional(readOnly = false)
	public void delete(CreditUserOperator creditUserOperator) {
		super.delete(creditUserOperator);
	}

	@Override
	protected CrudDao<CreditUserOperator> getEntityDao() {
		// TODO Auto-generated method stub
		return creditUserOperatorDao;
	}

	
	public List<CreditUserOperator> findByPhone(
			CreditUserOperator creditUserOperator) {
		// TODO Auto-generated method stub
		return creditUserOperatorDao.findByPhone(creditUserOperator);
	}
	
}