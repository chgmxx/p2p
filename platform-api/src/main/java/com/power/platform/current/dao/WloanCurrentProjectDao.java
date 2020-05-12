package com.power.platform.current.dao;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.current.entity.WloanCurrentProject;

/**
 * 活期融资项目DAO接口
 * @author Mr.Jia
 * @version 2016-01-12
 */
@MyBatisDao
public interface WloanCurrentProjectDao extends CrudDao<WloanCurrentProject> {
	
}