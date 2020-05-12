package com.power.platform.userinfo.dao;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.userinfo.entity.UserCheckAccount;


/**
 * 客户账户对账DAO接口
 * @author soler
 * @version 2016-06-23
 */
@MyBatisDao
public interface UserCheckAccountDao extends CrudDao<UserCheckAccount> {

	int deleteAll();
	
}