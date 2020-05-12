package com.power.platform.sms.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.common.utils.IdGen;
import com.power.platform.sms.dao.SmsMsgHistoryDao;
import com.power.platform.sms.dao.SmsRejectHistoryDao;
import com.power.platform.sms.entity.SmsMsgHistory;
import com.power.platform.sms.entity.SmsRejectHistory;
import com.power.platform.sms.type.SmsMsgHistoryType;

@Service("smsRejectService")
public class SmsRejectService extends CrudService<SmsRejectHistory> {

	@Resource
	private SmsRejectHistoryDao smsRejectHistoryDao;

	protected CrudDao<SmsRejectHistory> getEntityDao() {

		return smsRejectHistoryDao;
	}

	/**
	 * 查找短信验证码历史
	 */

	public List<SmsRejectHistory> findMsgHistory(String phone) {

		// TODO Auto-generated method stub
		System.out.println("查找短信验证码历史");
		SmsRejectHistory smsMsgHistoryInfo = new SmsRejectHistory();
		smsMsgHistoryInfo.setPhone(phone);
		List<SmsRejectHistory> smsList = smsRejectHistoryDao.findList(smsMsgHistoryInfo);
		return smsList;
	}

	/**
	 * 保存短信验证码信息
	 */

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void saveMsgHistory(SmsRejectHistory smsMsgHistoryInfo) {
		smsMsgHistoryInfo.setId(String.valueOf(new IdGen().randomLong()));
		smsRejectHistoryDao.insert(smsMsgHistoryInfo);
	}

	public List<SmsRejectHistory> getByIP(String ip) {
		return smsRejectHistoryDao.getByIP(ip);
	}

	public SmsRejectHistory getByPhone(String phone) {
		return smsRejectHistoryDao.getByPhone(phone);
	}
}
