package com.power.platform.bouns.dao;

import com.power.platform.bouns.entity.UserConsigneeAddress;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;

/**
 * 用户收货地址DAO接口
 * @author Mr.Jia
 * @version 2016-12-13
 */
@MyBatisDao
public interface UserConsigneeAddressDao extends CrudDao<UserConsigneeAddress> {
	
}