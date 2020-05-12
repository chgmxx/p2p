/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.cgb.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.cgb.dao.CgbCheckAccountDao;
import com.power.platform.cgb.entity.CgbCheckAccount;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.service.CrudService;


/**
 * 存管宝账户对账Service
 * @author yb
 * @version 2018-06-11
 */
@Service
@Transactional(readOnly = false)
public class CgbCheckAccountService extends CrudService<CgbCheckAccount> {

	@Resource
	private CgbCheckAccountDao cgbCheckAccountDao;
	
	public CgbCheckAccount get(String id) {
		return super.get(id);
	}
	
	public List<CgbCheckAccount> findList(CgbCheckAccount cgbCheckAccount) {
		return super.findList(cgbCheckAccount);
	}
	
	public Page<CgbCheckAccount> findPage(Page<CgbCheckAccount> page, CgbCheckAccount cgbCheckAccount) {
		return super.findPage(page, cgbCheckAccount);
	}
	
	@Transactional(readOnly = false)
	public void save(CgbCheckAccount cgbCheckAccount) {
		super.save(cgbCheckAccount);
	}
	
	@Transactional(readOnly = false)
	public void delete(CgbCheckAccount cgbCheckAccount) {
		super.delete(cgbCheckAccount);
	}

	@Override
	protected CrudDao<CgbCheckAccount> getEntityDao() {
		// TODO Auto-generated method stub
		return cgbCheckAccountDao;
	}

	public int deleteAll() {
		// TODO Auto-generated method stub
		return cgbCheckAccountDao.deleteAll();
	}

	public List<CgbCheckAccount> findAllList() {
		// TODO Auto-generated method stub
		return cgbCheckAccountDao.findAllList();
	}
	
}