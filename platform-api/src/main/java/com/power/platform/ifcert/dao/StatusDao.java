/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.ifcert.dao;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.ifcert.entity.Status;

/**
 * 国家应急中心散标状态信息DAO接口
 * 
 * @author Roy
 * @version 2019-05-13
 */
@MyBatisDao
public interface StatusDao extends CrudDao<Status> {

}