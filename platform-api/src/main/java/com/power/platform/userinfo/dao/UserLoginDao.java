package com.power.platform.userinfo.dao;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.userinfo.entity.UserInfo;

/**
 * 用户登陆Dao接口
 * @author YHAGZALUN WO SJIAOSY
 *
 */
@MyBatisDao
public interface UserLoginDao extends CrudDao<UserInfo>{

}
