package com.power.platform.bouns.dao;


import com.power.platform.bouns.entity.UserBounsPoint;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;


/**
 * 用户积分信息DAO接口
 * @author Mr.Jia
 * @version 2016-12-13
 */
@MyBatisDao
public interface UserBounsPointDao extends CrudDao<UserBounsPoint> {
	
}