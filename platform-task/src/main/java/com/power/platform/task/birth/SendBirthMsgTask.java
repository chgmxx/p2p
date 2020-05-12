package com.power.platform.task.birth;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.utils.DateUtils;
import com.power.platform.userinfo.dao.UserInfoDao;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.weixin.service.WeixinSendTempMsgService;

@Service
@Lazy(false)
public class SendBirthMsgTask {

	private static final Logger log = LoggerFactory.getLogger(SendBirthMsgTask.class);

	@Autowired
	private WeixinSendTempMsgService weixinSendTempMsgService;
	@Autowired
	private UserInfoDao userInfoDao;

	/**
	 * 
	 * 方法: sendBirthMsg <br>
	 * 描述: 发送生日短消息祝福，每天任务轮询 <br>
	 * 作者: Roy <br>
	 * 时间: 2019年11月7日 下午4:09:33
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	@Scheduled(cron = "0 0 0 * * ?")
	public void sendBirthMsg() {

		log.info("发送生日祝福短消息...start...");
		// 短信内容
		StringBuffer sms_message = new StringBuffer();
		sms_message.append("亲爱的用户，今天是您的生日，中投摩根祝您生日快乐！");
		// 查询
		UserInfo userInfo = new UserInfo();
		userInfo.setBirthday(DateUtils.getDate("MMdd"));
		List<UserInfo> userInfoList = userInfoDao.findList1(userInfo);
		if (null != userInfoList && userInfoList.size() > 0) {
			for (UserInfo uInfo : userInfoList) {
				if (null != uInfo.getName()) {
					if (uInfo.getName().length() == 11) {
						weixinSendTempMsgService.ztmgSendRepayRemindMsg(uInfo.getName(), sms_message.toString());
					}
				}
			}
		}
		log.info("发送生日祝福短消息...end...");
	}

}
