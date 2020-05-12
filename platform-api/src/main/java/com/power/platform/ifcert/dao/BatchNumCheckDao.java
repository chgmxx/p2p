/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.ifcert.dao;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.ifcert.entity.BatchNumCheck;

/**
 * 批次数据入库状态信息表DAO接口
 * 
 * @author yangzf
 * @version 2019-06-27
 */
@MyBatisDao
public interface BatchNumCheckDao extends CrudDao<BatchNumCheck> {

}