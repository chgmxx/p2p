package com.power.platform.cms.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.cms.dao.LeaveMessageDao;
import com.power.platform.cms.entity.LeaveMessage;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;

/**
 * 用户留言Service
 * 
 */
@Service("leaveMessageService")
@Transactional(readOnly = true)
public class LeaveMessageService extends CrudService<LeaveMessage> {

	@Resource
	private LeaveMessageDao leaveMessageDao;
	
	@Override
	protected CrudDao<LeaveMessage> getEntityDao() {
		// TODO Auto-generated method stub
		return leaveMessageDao;
	}

}
