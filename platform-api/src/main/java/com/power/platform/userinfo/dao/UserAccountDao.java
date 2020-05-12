package com.power.platform.userinfo.dao;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.userinfo.entity.UserAccountInfo;

/**
 * 用户账户Dao接口
 * @author YHAGZALUN WO SJIAOSY
 *
 */
@MyBatisDao
public interface UserAccountDao extends CrudDao<UserAccountInfo>{

}
