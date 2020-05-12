package com.power.platform.weixin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.weixin.dao.AccountErWeiMaDao;
import com.power.platform.weixin.entity.AccountErWeiMa;

@Transactional(readOnly = false)
@Service("accountErWeiMaService")
public class AccountErWeiMaService extends CrudService<AccountErWeiMa> {

	@Autowired
	private AccountErWeiMaDao accountErWeiMaDao;

	protected CrudDao<AccountErWeiMa> getEntityDao() {
		return accountErWeiMaDao;
	}
	
}