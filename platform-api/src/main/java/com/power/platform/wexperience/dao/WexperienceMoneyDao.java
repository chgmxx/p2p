package com.power.platform.wexperience.dao;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.wexperience.entity.WexperienceMoney;

/**
 * 体验金信息DAO接口
 * @author Mr.Jia
 * @version 2016-01-25
 */
@MyBatisDao
public interface WexperienceMoneyDao extends CrudDao<WexperienceMoney> {
	
}