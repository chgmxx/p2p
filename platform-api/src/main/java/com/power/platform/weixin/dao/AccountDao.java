package com.power.platform.weixin.dao;
import java.util.List;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.weixin.entity.Account;


@MyBatisDao
public interface AccountDao extends CrudDao<Account>{

	public Account getSingleAccount();
}