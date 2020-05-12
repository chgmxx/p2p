/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.credit.dao.middlemen;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.credit.entity.middlemen.CreditMiddlemenRate;


/**
 * 项目期限和利率DAO接口
 * @author yb
 * @version 2018-04-20
 */
@MyBatisDao
public interface CreditMiddlemenRateDao extends CrudDao<CreditMiddlemenRate> {
	
}