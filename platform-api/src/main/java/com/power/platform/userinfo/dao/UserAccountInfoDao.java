/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.userinfo.dao;

import org.apache.ibatis.annotations.Param;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.userinfo.entity.UserAccountInfo;

/**
 * 账户管理DAO接口
 * @author jiajunfeng
 * @version 2015-12-18
 */
@MyBatisDao
public interface UserAccountInfoDao extends CrudDao<UserAccountInfo> {
	
	/**
	 * 根据用户id查找用户账户信息
	 * @param userid
	 * @return
	 */
	public UserAccountInfo getUserAccountInfo(@Param("userid") String userid);
}