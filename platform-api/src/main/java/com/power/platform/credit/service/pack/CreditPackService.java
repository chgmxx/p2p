/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.credit.service.pack;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.credit.dao.pack.CreditPackDao;
import com.power.platform.credit.dao.voucher.CreditVoucherDao;
import com.power.platform.credit.entity.pack.CreditPack;


/**
 * 合同Service
 * @author jice
 * @version 2018-03-14
 */
@Service
@Transactional(readOnly = true)
public class CreditPackService extends CrudService<CreditPack> {
	
	@Resource
	private CreditPackDao creditPackDao;

	public CreditPack get(String id) {
		return super.get(id);
	}
	
	public List<CreditPack> findList(CreditPack creditPack) {
		return super.findList(creditPack);
	}
	
	public Page<CreditPack> findPage(Page<CreditPack> page, CreditPack creditPack) {
		return super.findPage(page, creditPack);
	}
	
	@Transactional(readOnly = false)
	public void save(CreditPack creditPack) {
		super.save(creditPack);
	}
	
	@Transactional(readOnly = false)
	public void delete(CreditPack creditPack) {
		super.delete(creditPack);
	}

	@Override
	protected CrudDao<CreditPack> getEntityDao() {
		// TODO Auto-generated method stub
		return creditPackDao;
	}
	
}