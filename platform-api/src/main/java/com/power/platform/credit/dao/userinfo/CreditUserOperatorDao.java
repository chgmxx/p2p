/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.credit.dao.userinfo;

import java.util.List;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.credit.entity.userinfo.CreditUserOperator;


/**
 * 借款端，操作人DAO接口
 * @author yb
 * @version 2018-03-08
 */
@MyBatisDao
public interface CreditUserOperatorDao extends CrudDao<CreditUserOperator> {

	/**
	 * 根据手机号查询
	 * @param creditUserOperator
	 * @return
	 */
	List<CreditUserOperator> findByPhone(CreditUserOperator creditUserOperator);
	
}