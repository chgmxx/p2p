/**
 * Copyright &copy; 2012-2014 <a
 * href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.credit.dao.audit;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.credit.entity.audit.CreditAuditInfo;

/**
 * 借款审核信息DAO接口
 * 
 * @author Roy
 * @version 2019-01-16
 */
@MyBatisDao
public interface CreditAuditInfoDao extends CrudDao<CreditAuditInfo> {

}