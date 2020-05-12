/**
 * Copyright &copy; 2012-2013 <a href="httparamMap://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.userinfo.service;

import java.util.List;


import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.service.CrudService;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.userinfo.dao.UserLogDao;
import com.power.platform.userinfo.entity.UserLog;

/**
 * 日志Service
 * @author ThinkGem
 * @version 2014-05-16
 */
@Service("userLogService")
@Transactional(readOnly = true)
public class UserLogService extends CrudService<UserLog> {

	@Resource
	private UserLogDao userLogDao;
	
	 
	protected CrudDao<UserLog> getEntityDao() {
		return userLogDao;
	}
	
	public Page<UserLog> findPage(Page<UserLog> page, UserLog userlog) {
		
//		// 设置默认时间范围，默认当前月
//		if (userlog.getBeginDate() == null){
//			userlog.setBeginDate(DateUtils.setDays(DateUtils.parseDate(DateUtils.getDate()), 1));
//		}
//		if (userlog.getEndDate() == null){
//			userlog.setEndDate(DateUtils.addMonths(userlog.getBeginDate(), 1));
//		}
		
		return super.findPage(page, userlog);
		
	}

	 
	public List<UserLog> findAll() {
		return userLogDao.findAllList(new UserLog());
	}
}
