/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.sys.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.common.utils.CacheUtils;
import com.power.platform.sys.dao.DictDao;
import com.power.platform.sys.entity.Dict;
import com.power.platform.sys.utils.DictUtils;

/**
 * 字典Service
 * @author ThinkGem
 * @version 2014-05-16
 */
@Service("dictService")
@Transactional(readOnly = true)
public class DictService extends CrudService<Dict>{
	
	@Resource
	private DictDao dictDao;
	
	 
	protected CrudDao<Dict> getEntityDao() {
		return dictDao;
	}
	
	/**
	 * 查询字段类型列表
	 * @return
	 */
	public List<String> findTypeList(){
		return dictDao.findTypeList(new Dict());
	}

	@Transactional(readOnly = false)
	public void save(Dict dict) {
		super.save(dict);
		CacheUtils.remove(DictUtils.CACHE_DICT_MAP);
	}

	@Transactional(readOnly = false)
	public void delete(Dict dict) {
		super.delete(dict);
		CacheUtils.remove(DictUtils.CACHE_DICT_MAP);
	}
	
	 
	public List<Dict> findAll() {
		return dictDao.findAllList(new Dict());
	}

	 
	public List<Dict> findListByType(String type) {
		return dictDao.findListByType(type);
	}
}
