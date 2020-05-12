package com.power.platform.activity.dao;

import java.util.List;

import com.power.platform.activity.entity.UserVouchersHistory;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;

@MyBatisDao
public interface UserVouchersHistoryDao extends CrudDao<UserVouchersHistory> {

	public abstract List<UserVouchersHistory> findVouchersList(UserVouchersHistory userVouchersHistory);

}