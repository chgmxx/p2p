package com.power.platform.userinfo.dao;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.userinfo.entity.UserCheckOrder;


/**
 * 订单对账DAO接口
 * @author yb
 * @version 2017-09-04
 */
@MyBatisDao
public interface UserCheckOrderDao extends CrudDao<UserCheckOrder> {

	/**
	 * 清空表数据
	 * @return
	 */
	int deleteAll();
	
}