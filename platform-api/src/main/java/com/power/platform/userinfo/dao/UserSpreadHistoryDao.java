package com.power.platform.userinfo.dao;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.userinfo.entity.UserSpreadHistory;


/**
 * spreadDAO接口
 * @author yb
 * @version 2015-12-18
 */
@MyBatisDao
public interface UserSpreadHistoryDao extends CrudDao<UserSpreadHistory> {
	
}