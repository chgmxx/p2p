/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.lanmao.dao;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.lanmao.entity.LmTransaction;

/**
 * 懒猫交易留存DAO接口
 * 
 * @author Mr.yun.li
 * @version 2019-09-23
 */
@MyBatisDao
public interface LmTransactionDao extends CrudDao<LmTransaction> {

}