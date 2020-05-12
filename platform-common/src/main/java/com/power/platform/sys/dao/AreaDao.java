package com.power.platform.sys.dao;

import com.power.platform.common.persistence.TreeDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.sys.entity.Area;

/**
 * 区域DAO接口
 * @author wangjingsong
 * @version 2014-05-16
 */
@MyBatisDao
public interface AreaDao extends TreeDao<Area> {
	
}
