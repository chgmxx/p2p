package com.power.platform.sms.dao;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.sms.entity.SmsMsgHistory;

/**
 * 
 * 类: SmsMsgHistoryDao <br>
 * 描述: 短信验证码历史DAO. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年4月26日 下午6:33:20
 */
@MyBatisDao
public interface SmsMsgHistoryDao extends CrudDao<SmsMsgHistory> {

}
