package com.power.platform.activity.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.power.platform.activity.dao.UserVouchersHistoryDao;
import com.power.platform.activity.entity.UserVouchersHistory;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.service.CrudService;

@Service("userVouchersHistoryService")
public class UserVouchersHistoryService extends CrudService<UserVouchersHistory> {

	/**
	 * 1：抵用券.
	 */
	public static final String USER_VOUCHERS_HISTORY_TYPE_1 = "1";
	/**
	 * 2：加息券.
	 */
	public static final String USER_VOUCHERS_HISTORY_TYPE_2 = "2";

	/**
	 * 项目期限，1：通用.
	 */
	public static final String SPAN_1 = "1";
	/**
	 * 项目期限，30：30天.
	 */
	public static final String SPAN_30 = "30";
	/**
	 * 项目期限，60：60天.
	 */
	public static final String SPAN_60 = "60";
	/**
	 * 项目期限，90：90天.
	 */
	public static final String SPAN_90 = "90";
	/**
	 * 项目期限，120：120天.
	 */
	public static final String SPAN_120 = "120";
	/**
	 * 项目期限，180：180天.
	 */
	public static final String SPAN_180 = "180";
	/**
	 * 项目期限，360：360天.
	 */
	public static final String SPAN_360 = "360";

	@Resource
	private UserVouchersHistoryDao userVouchersHistoryDao;

	public Page<UserVouchersHistory> findVouchersPage(Page<UserVouchersHistory> page, UserVouchersHistory entity) {

		entity.setPage(page);
		page.setList(userVouchersHistoryDao.findVouchersList(entity));
		return page;
	}

	@Override
	protected CrudDao<UserVouchersHistory> getEntityDao() {

		return userVouchersHistoryDao;
	}

}