package com.power.platform.userinfo.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.userinfo.dao.UserAccountInfoDao;
import com.power.platform.userinfo.entity.UserAccountInfo;
import com.power.platform.userinfo.service.UserAccountInfoService;

/**
 * 用户信息管理Service
 * 
 * @author jiajunfeng
 * @version 2015-12-16
 */

@Service("userAccountInfoService")
@Transactional(readOnly = true)
public class UserAccountInfoService extends CrudService<UserAccountInfo> {

	@Resource
	private UserAccountInfoDao userAccountInfoDao;

	protected CrudDao<UserAccountInfo> getEntityDao() {

		return userAccountInfoDao;
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int updateUserAccountInfo(UserAccountInfo entity) {

		int flag = 0;
		try {
			flag = userAccountInfoDao.update(entity);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("fn:updateUserAccountInfo,{异常：" + e.getMessage() + "}");
		}

		return flag;
	}

	/**
	 * 根据用户id查找用户账户信息
	 * 
	 * @param userid
	 * @return
	 */

	public UserAccountInfo getUserAccountInfo(String userid) {

		return userAccountInfoDao.getUserAccountInfo(userid);
	}

}