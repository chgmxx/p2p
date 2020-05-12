/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.credit.dao.info;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.credit.entity.info.CreditInfo;


/**
 * 借款资料DAO接口
 * @author yb
 * @version 2017-12-11
 */
@MyBatisDao
public interface CreditInfoDao extends CrudDao<CreditInfo> {
	
}