/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.activity.dao;

import com.power.platform.activity.entity.ActivityContactAddress;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;

/**
 * 用户活动联系地址DAO接口
 * 
 * @author Roy
 * @version 2019-08-14
 */
@MyBatisDao
public interface ActivityContactAddressDao extends CrudDao<ActivityContactAddress> {

}