/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.credit.service.info;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.service.CrudService;
import com.power.platform.credit.dao.info.CreditInfoDao;
import com.power.platform.credit.entity.info.CreditInfo;


/**
 * 借款资料Service
 * @author yb
 * @version 2017-12-11
 */
@Service("CreditInfoService")
@Transactional(readOnly = false)
public class CreditInfoService extends CrudService<CreditInfo> {

	@Resource
	private CreditInfoDao creditInfoDao;
	
	public CreditInfo get(String id) {
		return super.get(id);
	}
	
	public List<CreditInfo> findList(CreditInfo creditInfo) {
		return super.findList(creditInfo);
	}
	
	public Page<CreditInfo> findPage(Page<CreditInfo> page, CreditInfo creditInfo) {
		return super.findPage(page, creditInfo);
	}
	
	@Transactional(readOnly = false)
	public void save(CreditInfo creditInfo) {
		super.save(creditInfo);
	}
	
	@Transactional(readOnly = false)
	public void delete(CreditInfo creditInfo) {
		super.delete(creditInfo);
	}

	@Override
	protected CrudDao<CreditInfo> getEntityDao() {
		// TODO Auto-generated method stub
		return creditInfoDao;
	}
	
}