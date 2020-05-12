 package com.power.platform.userinfo.dao;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.userinfo.entity.UserMessages;

/**
 * 用户消息Dao
 * @author lc
 *
 */
@MyBatisDao
public interface UserMessagesDao extends CrudDao<UserMessages> {
 
	
	public void updateStates(UserMessages userMessages);
	

}
