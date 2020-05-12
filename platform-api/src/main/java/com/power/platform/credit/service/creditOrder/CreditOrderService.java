/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.credit.service.creditOrder;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.service.CrudService;
import com.power.platform.credit.dao.creditOrder.CreditOrderDao;
import com.power.platform.credit.entity.creditOrder.CreditOrder;

/**
 * 订单信息Service
 * @author jice
 * @version 2018-05-23
 */
@Service
@Transactional(readOnly = true)
public class CreditOrderService extends CrudService<CreditOrder> {
	
	@Resource
	private CreditOrderDao creditOrderDao;

	public CreditOrder get(String id) {
		return super.get(id);
	}
	
	public List<CreditOrder> findList(CreditOrder creditOrder) {
		return super.findList(creditOrder);
	}
	
	public Page<CreditOrder> findPage(Page<CreditOrder> page, CreditOrder creditOrder) {
		return super.findPage(page, creditOrder);
	}
	
	@Transactional(readOnly = false)
	public void save(CreditOrder creditOrder) {
		super.save(creditOrder);
	}
	
	@Transactional(readOnly = false)
	public void delete(CreditOrder creditOrder) {
		super.delete(creditOrder);
	}

	@Override
	protected CrudDao<CreditOrder> getEntityDao() {
		// TODO Auto-generated method stub
		return creditOrderDao;
	}
	
}