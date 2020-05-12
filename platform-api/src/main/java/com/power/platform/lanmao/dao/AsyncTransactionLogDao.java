/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.lanmao.dao;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.lanmao.entity.AsyncTransactionLog;

/**
 * 批量交易日志DAO接口
 * 
 * @author Mr.yun.li
 * @version 2019-10-06
 */
@MyBatisDao
public interface AsyncTransactionLogDao extends CrudDao<AsyncTransactionLog> {

}