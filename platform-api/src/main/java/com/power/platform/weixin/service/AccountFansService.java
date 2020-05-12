package com.power.platform.weixin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.weixin.dao.AccountFansDao;
import com.power.platform.weixin.entity.AccountFans;

@Transactional(readOnly = false)
@Service("accountFansService")
public class AccountFansService extends CrudService<AccountFans> {

	@Autowired
	private AccountFansDao accountFansDao;
	
	protected CrudDao<AccountFans> getEntityDao() {
		return accountFansDao;
	}
	 
}