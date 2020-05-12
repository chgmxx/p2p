package com.power.platform.bill.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.bill.dao.MerchantWithdrawDao;
import com.power.platform.bill.entity.MerchantWithdraw;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;

/**
 * 平台商户对账，提现文件Service
 * 
 * @author lance
 * @version 2018-03-08
 */
@Service("merchantWithdrawService")
@Transactional(readOnly = false)
public class MerchantWithdrawService extends CrudService<MerchantWithdraw> {

	@Resource
	private MerchantWithdrawDao merchantWithdrawDao;

	@Override
	protected CrudDao<MerchantWithdraw> getEntityDao() {

		return merchantWithdrawDao;
	}

}