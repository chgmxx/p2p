/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.lanmao.dao;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.lanmao.entity.CreditUserAuditInfo;

/**
 * 开户审核记录DAO接口
 * 
 * @author Mr.yun.li
 * @version 2019-09-28
 */
@MyBatisDao
public interface CreditUserAuditInfoDao extends CrudDao<CreditUserAuditInfo> {

}