/**
 * 工程: user-svc <br>
 * 标题: UserBankCardServiceImpl.java <br>
 * 描述: 用户银行卡实现. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2015年12月18日 下午3:07:24 <br>
 * 版权: Copyright 2015 1000CHI Software Technology Co.,Ltd. <br>
 * All rights reserved.
 *
 */
package com.power.platform.userinfo.service;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.service.CrudService;
import com.power.platform.userinfo.dao.UserBankCardDao;
import com.power.platform.userinfo.entity.UserBankCard;

/**
 * 类: UserBankCardServiceImpl <br>
 * 描述: 用户银行卡实现. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2015年12月18日 下午3:07:24
 */
@Service("userBankCardService")
@Transactional(readOnly = true)
public class UserBankCardService extends CrudService<UserBankCard> {

	/**
	 * 是否默认账号，是默认银行卡.
	 */
	public static final String USER_BANK_CARD_IS_DEFAULT_1 = "1";
	/**
	 * 状态，未认证.
	 */
	public static final String USER_BANK_CARD_STATE_0 = "0";
	/**
	 * 状态，已认证.
	 */
	public static final String USER_BANK_CARD_STATE_1 = "1";

	private static final Logger logger = Logger.getLogger(UserBankCardService.class);

	@Resource
	private UserBankCardDao userBankCardDao;

	protected CrudDao<UserBankCard> getEntityDao() {

		return userBankCardDao;
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int physicallyDeleted(UserBankCard entity) {

		int flag = 0;

		try {
			flag = userBankCardDao.physicallyDeleted(entity);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("fn:physicallyDeleted,{异常：" + e.getMessage() + "}");
		}
		return flag;
	}

	public UserBankCard getBankCardInfoByUserId(String userId) {

		UserBankCard userBankCard = null;
		try {
			userBankCard = userBankCardDao.getUserBankCardByUserId(userId);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("fn:getBankCardInfoByUserId,{异常：" + e.getMessage() + "}");
		}
		return userBankCard;
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int insertBankCardInfo(UserBankCard entity) {

		int flag = 0;

		try {
			flag = userBankCardDao.insert(entity);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("fn:insertBankCardInfo,{异常：" + e.getMessage() + "}");
		}
		return flag;
	}

	public Page<UserBankCard> findPage(Page<UserBankCard> page, UserBankCard entity) {

		entity.setPage(page);
		page.setList(userBankCardDao.queryUserBankCardByWhere(entity));
		return page;
	}

	/**
	 * 根据userId获取银行卡信息
	 * @param userId
	 * @return
	 */
	public UserBankCard findByUserId(String userId) {
		// TODO Auto-generated method stub
		UserBankCard userBankCard = userBankCardDao.getUserBankCardByUserId(userId);
		return userBankCard;
	}

	public UserBankCard getInfoById(String orderId) {
		// TODO Auto-generated method stub
		return userBankCardDao.getInfoById(orderId);
	}

}
