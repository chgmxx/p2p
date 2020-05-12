package com.power.platform.weixin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.weixin.dao.AccountDao;
import com.power.platform.weixin.entity.Account;

@Service("accountService")
@Transactional(readOnly = false)
public class AccountService extends CrudService<Account> {

	@Autowired
	private AccountDao accountDao;

	protected CrudDao<Account> getEntityDao() {
		return accountDao;
	}

}