/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.sys.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.power.platform.common.persistence.TreeDao;
import com.power.platform.common.service.TreeService;
import com.power.platform.common.utils.CacheUtils;
import com.power.platform.sys.dao.AreaDao;
import com.power.platform.sys.entity.Area;
import com.power.platform.sys.utils.UserUtils;

/**
 * 区域Service
 * @author ThinkGem
 * @version 2014-05-16
 */
@Service("areaService")
@Transactional(readOnly = true) 
public class AreaService extends TreeService<Area>{

	@Resource
	private AreaDao areaDao;
	
	public AreaService(){
		
	}
	
	 
	protected TreeDao<Area> getEntityDao() {
		return areaDao;
	};
	
	 
	public List<Area> findAll(){
		List<Area> result = Lists.newArrayList();
		for(Area area : UserUtils.getAreaList()){
			result.add((Area)area);
		}
		return result;
	}

	@Transactional(readOnly = false)
	public void save(Area area) {
		super.save(area);
		CacheUtils.remove(UserUtils.CACHE_AREA_ALL_LIST);
		CacheUtils.remove(UserUtils.USER_CACHE,UserUtils.CACHE_AREA_LIST+"*");
	}
	
	@Transactional(readOnly = false)
	public void delete(Area area) {
		super.delete(area);
		CacheUtils.remove(UserUtils.CACHE_AREA_ALL_LIST);
		CacheUtils.remove(UserUtils.USER_CACHE,UserUtils.CACHE_AREA_LIST+"*");
	}
}
