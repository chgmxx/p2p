package com.power.platform.weixin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.weixin.dao.AccountMenuGroupDao;
import com.power.platform.weixin.entity.AccountMenuGroup;

@Transactional(readOnly = false)
@Service("accountMenuGroupService")
public class AccountMenuGroupService extends CrudService<AccountMenuGroup> {

	@Autowired
	private AccountMenuGroupDao accountMenuGroupDao;
	
	 
	protected CrudDao<AccountMenuGroup> getEntityDao() {
		return accountMenuGroupDao;
	}
	
}

