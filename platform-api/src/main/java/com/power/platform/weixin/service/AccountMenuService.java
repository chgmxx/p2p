package com.power.platform.weixin.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.weixin.dao.AccountMenuDao;
import com.power.platform.weixin.entity.AccountMenu;

@Transactional(readOnly = false)
@Service("accountMenuService")
public class AccountMenuService extends CrudService<AccountMenu> {

	@Autowired
	private AccountMenuDao accountMenuDao;
	
	 
	protected CrudDao<AccountMenu> getEntityDao() {
		return accountMenuDao;
	}
	 
	public List<AccountMenu> parentMenuList(AccountMenu accountMenu) {
		return accountMenuDao.parentMenuList(accountMenu);
	}

}