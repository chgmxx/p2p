/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.ifcert.dao;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.ifcert.entity.LendProductConfig;

/**
 * 数据中心产品配置表DAO接口
 * 
 * @author Roy
 * @version 2019-05-17
 */
@MyBatisDao
public interface LendProductConfigDao extends CrudDao<LendProductConfig> {

}