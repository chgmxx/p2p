/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.sys.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.TreeDao;
import com.power.platform.common.service.TreeService;
import com.power.platform.common.utils.CacheUtils;
import com.power.platform.sys.dao.OfficeDao;
import com.power.platform.sys.entity.Office;
import com.power.platform.sys.utils.UserUtils;

/**
 * 机构Service
 * @author ThinkGem
 * @version 2014-05-16
 */
@Service("officeService")
@Transactional(readOnly = true)
public class OfficeService extends TreeService<Office>{

	@Resource
	private OfficeDao officeDao;
	
	 
	protected TreeDao<Office> getEntityDao() {
		return officeDao;
	}
	
	 
	public List<Office> findAll(){
		return UserUtils.getOfficeAllList();
	}

	public List<Office> findList(Boolean isAll,String userId){
		if (isAll != null && isAll){
			return UserUtils.getOfficeAllList();
		}else{
			return UserUtils.getOfficeList(userId);
		}
	}
	
	@Transactional(readOnly = true)
	public List<Office> findList(Office office){
		office.setParentIds(office.getParentIds()+"%");
		return officeDao.findByParentIdsLike(office);
	}
	
	@Transactional(readOnly = false)
	public void save(Office office) {
		super.save(office);
		CacheUtils.remove(UserUtils.CACHE_OFFICE_ALL_LIST);
		CacheUtils.remove(UserUtils.USER_CACHE, UserUtils.CACHE_OFFICE_LIST+"*");
	}
	
	@Transactional(readOnly = false)
	public void delete(Office office) {
		super.delete(office);
		CacheUtils.remove(UserUtils.CACHE_OFFICE_ALL_LIST);
		CacheUtils.remove(UserUtils.USER_CACHE, UserUtils.CACHE_OFFICE_LIST+"*");
	}
}
