/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.cgb.dao;

import com.power.platform.cgb.entity.CgbCheckAccount;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;


/**
 * 存管宝账户对账DAO接口
 * @author yb
 * @version 2018-06-11
 */
@MyBatisDao
public interface CgbCheckAccountDao extends CrudDao<CgbCheckAccount> {

	int deleteAll();
	
}