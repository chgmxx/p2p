/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.lanmao.dao;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.lanmao.entity.UserAccountOrder;

/**
 * 用户账户订单DAO接口
 * 
 * @author Mr.yun.li
 * @version 2019-10-03
 */
@MyBatisDao
public interface UserAccountOrderDao extends CrudDao<UserAccountOrder> {

}