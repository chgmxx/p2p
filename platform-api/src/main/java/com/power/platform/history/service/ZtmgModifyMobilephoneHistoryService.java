package com.power.platform.history.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.history.dao.ZtmgModifyMobilephoneHistoryDao;
import com.power.platform.history.entity.ZtmgModifyMobilephoneHistory;

/**
 * 历史记录表(更换客户手机)Service
 * 
 * @author Roy
 * @version 2016-11-11
 */
@Service("ztmgModifyMobilephoneHistoryService")
@Transactional(readOnly = true)
public class ZtmgModifyMobilephoneHistoryService extends CrudService<ZtmgModifyMobilephoneHistory> {

	@Resource
	private ZtmgModifyMobilephoneHistoryDao ztmgModifyMobilephoneHistoryDao;

	@Override
	protected CrudDao<ZtmgModifyMobilephoneHistory> getEntityDao() {

		return ztmgModifyMobilephoneHistoryDao;
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int insertZtmgModifyMobilephoneHistory(ZtmgModifyMobilephoneHistory ztmgModifyMobilephoneHistory) {

		int flag = 0;
		try {
			flag = ztmgModifyMobilephoneHistoryDao.insert(ztmgModifyMobilephoneHistory);
			logger.info("fn:insertZtmgModifyMobilephoneHistory,{新增保存成功}");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("fn:insertZtmgModifyMobilephoneHistory,{新增保存异常：" + e.getMessage() + "}");
		}
		return flag;
	}

}