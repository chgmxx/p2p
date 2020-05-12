package com.power.platform.sms.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.common.utils.IdGen;
import com.power.platform.sms.dao.SmsMsgHistoryDao;
import com.power.platform.sms.entity.SmsMsgHistory;
import com.power.platform.sms.type.SmsMsgHistoryType;

@Service("smsSendMsgService")
public class SmsSendMsgService extends CrudService<SmsMsgHistory> {

	@Resource
	private SmsMsgHistoryDao smsMsgHistoryDao;

	protected CrudDao<SmsMsgHistory> getEntityDao() {

		return smsMsgHistoryDao;
	}

	/**
	 * 查找短信验证码历史
	 */

	public List<SmsMsgHistory> findMsgHistory(String phone) {

		// TODO Auto-generated method stub
		System.out.println("查找短信验证码历史");
		SmsMsgHistory smsMsgHistoryInfo = new SmsMsgHistory();
		smsMsgHistoryInfo.setPhone(phone);
		smsMsgHistoryInfo.setType(SmsMsgHistoryType.PLATFORM_REGISTER);
		List<SmsMsgHistory> smsList = smsMsgHistoryDao.findList(smsMsgHistoryInfo);
		return smsList;
	}

	/**
	 * 验证同号手机验证
	 */

	public int getSamePhonenoSendCount(String phone, String endTime, String startTime) {

		System.out.println("验证同号手机" + phone);
		SmsMsgHistory smsMsgHistoryInfo = new SmsMsgHistory();
		smsMsgHistoryInfo.setPhone(phone);
		smsMsgHistoryInfo.setBeginCreateTime(startTime);
		smsMsgHistoryInfo.setEndCreateTime(endTime);
		List<SmsMsgHistory> smsList = smsMsgHistoryDao.findList(smsMsgHistoryInfo);
		int count = smsList.size();
		System.out.println("从" + startTime + "到" + endTime + "发送了" + count + "条验证码");
		return count;
	}

	/**
	 * 验证同IP
	 */

	public int getSameIPSendCount(String ip, String endTime, String startTime) {

		// TODO Auto-generated method stub
		System.out.println("验证同IP" + ip);
		SmsMsgHistory smsMsgHistoryInfo = new SmsMsgHistory();
		smsMsgHistoryInfo.setIp(ip);
		smsMsgHistoryInfo.setBeginCreateTime(startTime);
		smsMsgHistoryInfo.setEndCreateTime(endTime);
		List<SmsMsgHistory> smsList = smsMsgHistoryDao.findList(smsMsgHistoryInfo);
		int count = smsList.size();
		System.out.println("从" + startTime + "到" + endTime + "发送了" + count + "条验证码");
		return count;
	}

	/**
	 * 保存短信验证码信息
	 */

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void saveMsgHistory(SmsMsgHistory smsMsgHistoryInfo) {

		// TODO Auto-generated method stub
		smsMsgHistoryInfo.setType(SmsMsgHistoryType.PLATFORM_REGISTER);
		smsMsgHistoryInfo.setId(String.valueOf(new IdGen().randomLong()));
		smsMsgHistoryDao.insert(smsMsgHistoryInfo);
	}
}
