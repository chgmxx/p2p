package com.power.platform.history.dao;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.history.entity.ZtmgModifyMobilephoneHistory;

/**
 * 历史记录表(更换客户手机)DAO接口
 * 
 * @author Roy
 * @version 2016-11-11
 */
@MyBatisDao
public interface ZtmgModifyMobilephoneHistoryDao extends CrudDao<ZtmgModifyMobilephoneHistory> {

}