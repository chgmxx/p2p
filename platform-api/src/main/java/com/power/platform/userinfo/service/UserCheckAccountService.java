package com.power.platform.userinfo.service;


import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.userinfo.dao.UserCheckAccountDao;
import com.power.platform.userinfo.entity.UserCheckAccount;


/**
 * 客户账户对账Service
 * @author soler
 * @version 2016-06-23
 */
@Service
@Transactional(readOnly = false)
public class UserCheckAccountService extends CrudService<UserCheckAccount> {

	@Resource
	private UserCheckAccountDao userCheckAccountDao;
	
	
	@Override
	protected CrudDao<UserCheckAccount> getEntityDao() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 清空数据
	 */
	public int deleteAll() {
		// TODO Auto-generated method stub
		return userCheckAccountDao.deleteAll();
		
	}

	public List<UserCheckAccount> findAllList() {
		// TODO Auto-generated method stub
		return userCheckAccountDao.findAllList();
	}
	
}