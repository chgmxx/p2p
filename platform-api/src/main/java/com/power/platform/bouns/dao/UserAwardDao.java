/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.bouns.dao;

import java.util.List;

import com.power.platform.bouns.entity.UserAward;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;


/**
 * 用户兑换奖品DAO接口
 * @author yb
 * @version 2016-12-13
 */
@MyBatisDao
public interface UserAwardDao extends CrudDao<UserAward> {

	List<UserAward> findList2(UserAward userAward);

	List<UserAward> findNeedAmount0(UserAward userAward);

	List<UserAward> findNeedAmount1(UserAward userAward);

	List<UserAward> findToDeadList();
	
}