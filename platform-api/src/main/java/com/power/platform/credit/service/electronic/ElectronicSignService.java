/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.credit.service.electronic;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.service.CrudService;
import com.power.platform.credit.dao.electronic.ElectronicSignDao;
import com.power.platform.credit.entity.electronic.ElectronicSign;


/**
 * 电子签章Service
 * @author jice
 * @version 2018-03-20
 */
@Service
@Transactional(readOnly = true)
public class ElectronicSignService extends CrudService<ElectronicSign> {
	

	@Resource
	private ElectronicSignDao electronicSignDao;

	public ElectronicSign get(String id) {
		return super.get(id);
	}
	
	public List<ElectronicSign> findList(ElectronicSign electronicSign) {
		return super.findList(electronicSign);
	}
	
	public Page<ElectronicSign> findPage(Page<ElectronicSign> page, ElectronicSign electronicSign) {
		return super.findPage(page, electronicSign);
	}
	
	@Transactional(readOnly = false)
	public void save(ElectronicSign electronicSign) {
		super.save(electronicSign);
	}
	
	@Transactional(readOnly = false)
	public void delete(ElectronicSign electronicSign) {
		super.delete(electronicSign);
	}

	@Override
	protected CrudDao<ElectronicSign> getEntityDao() {
		// TODO Auto-generated method stub
		return electronicSignDao;
	}
	
}