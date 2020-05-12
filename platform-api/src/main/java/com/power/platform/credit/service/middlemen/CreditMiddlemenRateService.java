/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.credit.service.middlemen;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.service.CrudService;
import com.power.platform.credit.dao.middlemen.CreditMiddlemenRateDao;
import com.power.platform.credit.entity.middlemen.CreditMiddlemenRate;


/**
 * 项目期限和利率Service
 * @author yb
 * @version 2018-04-20
 */
@Service
@Transactional(readOnly = false)
public class CreditMiddlemenRateService extends CrudService<CreditMiddlemenRate> {
	
	@Autowired
	private CreditMiddlemenRateDao creditMiddlemenRateDao;

	public CreditMiddlemenRate get(String id) {
		return super.get(id);
	}
	
	public List<CreditMiddlemenRate> findList(CreditMiddlemenRate creditMiddlemenRate) {
		return super.findList(creditMiddlemenRate);
	}
	
	public Page<CreditMiddlemenRate> findPage(Page<CreditMiddlemenRate> page, CreditMiddlemenRate creditMiddlemenRate) {
		return super.findPage(page, creditMiddlemenRate);
	}
	
	@Transactional(readOnly = false)
	public void save(CreditMiddlemenRate creditMiddlemenRate) {
		super.save(creditMiddlemenRate);
	}
	
	@Transactional(readOnly = false)
	public void delete(CreditMiddlemenRate creditMiddlemenRate) {
		super.delete(creditMiddlemenRate);
	}

	@Override
	protected CrudDao<CreditMiddlemenRate> getEntityDao() {
		// TODO Auto-generated method stub
		return creditMiddlemenRateDao;
	}
	
}