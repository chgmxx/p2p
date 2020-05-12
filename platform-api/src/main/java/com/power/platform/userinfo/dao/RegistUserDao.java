package com.power.platform.userinfo.dao;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.userinfo.entity.UserInfo;

/**
 * 用户注册Dao接口
 * @author YHAGZALUN WO SJIAOSY
 *
 */
@MyBatisDao
public interface RegistUserDao extends CrudDao<UserInfo>{

	//物理删除
	int deletePhysics(UserInfo userInfo);

}
