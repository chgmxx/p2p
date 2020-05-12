package com.power.platform.userinfo.service;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.userinfo.dao.UserBankCardHistoryDao;
import com.power.platform.userinfo.entity.UserBankCardHistory;

@Service("userBankCardHistoryService")
@Transactional(readOnly = true)
public class UserBankCardHistoryService extends CrudService<UserBankCardHistory> {

	/**
	 * 审核中.
	 */
	public static final String REPLACE_IDENTITY_CARD_STATE_1 = "1";
	/**
	 * 审核成功.
	 */
	public static final String REPLACE_IDENTITY_CARD_STATE_2 = "2";
	/**
	 * 审核失败.
	 */
	public static final String REPLACE_IDENTITY_CARD_STATE_3 = "3";

	private static final Logger logger = Logger.getLogger(UserBankCardHistoryService.class);

	@Resource
	private UserBankCardHistoryDao userBankCardHistoryDao;

	protected CrudDao<UserBankCardHistory> getEntityDao() {

		return userBankCardHistoryDao;
	}

	/**
	 * 
	 * 方法: updateUserBankCardHistoryInfo <br>
	 * 描述: 更新客户银行卡更换信息. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2015年12月23日 下午1:36:06
	 * 
	 * @param entity
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int updateUserBankCardHistoryInfo(UserBankCardHistory entity) {

		int flag = 0;
		try {
			flag = userBankCardHistoryDao.update(entity);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("fn:updateUserBankCardHistoryInfo,{异常：" + e.getMessage() + "}");
		}

		return flag;
	}

	/**
	 * 
	 * 方法: insertUserBankCardHistoryInfo <br>
	 * 描述: 客户新银行卡信息. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2015年12月22日 下午3:09:49
	 * 
	 * @param entity
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int insertUserBankCardHistoryInfo(UserBankCardHistory entity) {

		int flag = 0;
		try {

			flag = userBankCardHistoryDao.insert(entity);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("fn:insertUserBankCardHistoryInfo,{异常：" + e.getMessage() + "}");
		}
		return flag;
	}

	/**
	 * 
	 * 方法: checkUserBankCardHistoryExist <br>
	 * 描述: 判断客户是否在重新提交更换银行卡操作. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2015年12月21日 下午8:27:38
	 * 
	 * @param userId
	 * @param state
	 * @return
	 */
	public boolean checkUserBankCardHistoryExist(String userId, String state) {

		boolean flag = false;

		try {

			List<UserBankCardHistory> list = userBankCardHistoryDao.checkUserBankCardHistoryExist(userId, state);
			if (list != null && list.size() > 0) {
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("fn:checkUserBankCardHistoryExist,{异常：" + e.getMessage() + "}");
		}

		return flag;
	}

}
