package com.power.platform.sms.dao;

import java.util.List;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.sms.entity.SmsMsgHistory;
import com.power.platform.sms.entity.SmsRejectHistory;

/**
 * 
 * 类: SmsRejectHistoryDao <br>
 * 描述: 短信验证码屏蔽DAO. <br>
 */
@MyBatisDao
public interface SmsRejectHistoryDao extends CrudDao<SmsRejectHistory> {

	List<SmsRejectHistory> getByIP(String ip);

	SmsRejectHistory getByPhone(String phone);

}
