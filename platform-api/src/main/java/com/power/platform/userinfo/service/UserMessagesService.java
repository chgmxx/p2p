package com.power.platform.userinfo.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.userinfo.dao.UserMessagesDao;
import com.power.platform.userinfo.entity.UserMessages;


@Service("userMessagesService")
@Transactional(readOnly=false,rollbackFor=Exception.class)
public class UserMessagesService extends CrudService<UserMessages> {

	@Autowired
	private UserMessagesDao userMessagesDao;
	 
	protected CrudDao<UserMessages> getEntityDao() {
		return userMessagesDao;
	}
	
	/**
	 * 用户消息状态修改
	 * @param userMessages
	 * @return
	 */
	public boolean updateStates(UserMessages userMessages) {
		boolean check = true;
		try {
			userMessagesDao.updateStates(userMessages);
		} catch (Exception e) {
			check = false;
			e.printStackTrace();
		}
		return check;
	}

	/**
	 * 添加消息
	 * @param receiverId 接收用户
	 * @param title 消息标题
	 * @param body 消息内容
	 * @return
	 */
	public boolean saveUserMessage(String receiverId, String title, String body) {
		boolean check = true;
		try {
			UserMessages message = new UserMessages();
			message.preInsert();
			message.setReceiverId(receiverId);
			message.setSendTime(new Date());
			message.setTitle(title);
			message.setBody(body);
			message.setState(1);
			message.setSenderType(1);
			userMessagesDao.insert(message);
		} catch (Exception e) {
			check = false;
			e.printStackTrace();
		}
		return check;
	}
 
	 
}
