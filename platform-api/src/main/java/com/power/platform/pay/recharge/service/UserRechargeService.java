package com.power.platform.pay.recharge.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.service.CrudService;
import com.power.platform.pay.recharge.dao.UserRechargeDao;
import com.power.platform.pay.recharge.entity.UserRecharge;

@Service("userRechargeService")
@Transactional(readOnly = true)
public class UserRechargeService extends CrudService<UserRecharge> {

	/**
	 * 充值状态---处理中
	 */
	public static final Integer RECHARG_DOING = 1;
	/**
	 * 充值状态---成功
	 */
	public static final Integer RECHARG_SUCCESS = 2;
	/**
	 * 充值状态---失败
	 */
	public static final Integer RECHARG_ERROR = 3;

	/**
	 * 支付平台---汇付天下
	 */
	public static final Integer HUI_FU = 0;
	/**
	 * 支付平台---连连支付
	 */
	public static final Integer LIAN_LIAN = 1;
	/**
	 * 支付平台---连连wap
	 */
	public static final Integer LIAN_LIAN_WAP = 2;
	/**
	 * 支付平台---连连android
	 */
	public static final Integer LIAN_LIAN_ANDROID = 3;
	/**
	 * 支付平台---连连ios
	 */
	public static final Integer LIAN_LIAN_IOS = 4;

	/**
	 * 访问来源---PC
	 */
	public static final int FROM_PC = 1;

	/**
	 * 访问来源---手机WEB
	 */
	public static final int FROM_MOBILE_WEB = 2;
	/**
	 * 访问来源---ANDROID
	 */
	public static final int FROM_ANDROID = 3;
	/**
	 * 访问来源---IOS
	 */
	public static final int FROM_IOS = 4;
	/**
	 * 访问来源---WINDOW PHONE
	 */
	public static final int FROM_WIN_PHONE = 5;

	private static final Logger logger = Logger.getLogger(UserRechargeService.class);

	@Resource
	private UserRechargeDao userRechargeDao;

	protected CrudDao<UserRecharge> getEntityDao() {

		return userRechargeDao;
	}

	public Page<UserRecharge> findExcelReportPage(Page<UserRecharge> page, UserRecharge entity) {

		entity.setPage(page);
		page.setList(userRechargeDao.findExcelReportList(entity));
		return page;
	}

	/**
	 * 保存充值信息
	 */
	@Transactional(readOnly = false)
	public int insert(UserRecharge userRecharge) {

		int flag = 0;
		try {

			flag = userRechargeDao.insert(userRecharge);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("fn:insert,{异常：" + e.getMessage() + "}");
		}
		return flag;
	}

	/**
	 * 更新充值状态
	 */

	@Transactional(readOnly = false)
	public int updateState(UserRecharge userRecharge) {

		int a = userRechargeDao.updateState(userRecharge);
		return a;
	}

	public List<UserRecharge> getUncompleteRecharge(Date date) {

		return userRechargeDao.getUncompleteRecharge(date);
	}

	public List<UserRecharge> findAll() {

		return null;
	}

	public UserRecharge getById(String orderId) {
		// TODO Auto-generated method stub
		return userRechargeDao.getById(orderId);
	}

}
