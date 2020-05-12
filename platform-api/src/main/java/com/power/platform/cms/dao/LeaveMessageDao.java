package com.power.platform.cms.dao;

import com.power.platform.cms.entity.LeaveMessage;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
/**
 * 用户留言DAO接口
 * 
 * 
 */
@MyBatisDao
public interface LeaveMessageDao extends CrudDao<LeaveMessage> {

}
