/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.riskmanagement.dao;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.riskmanagement.entity.RiskManagement;


/**
 * 风控企业信息DAO接口
 * @author yb
 * @version 2016-10-11
 */
@MyBatisDao
public interface RiskManagementDao extends CrudDao<RiskManagement> {
	
}