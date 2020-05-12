package com.power.platform.bill.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.bill.dao.MerchantRechargeDao;
import com.power.platform.bill.entity.MerchantRecharge;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;

/**
 * 平台商户对账文件Service
 * 
 * @author lance
 * @version 2018-03-02
 */
@Service("merchantRechargeService")
@Transactional(readOnly = false)
public class MerchantRechargeService extends CrudService<MerchantRecharge> {

	@Resource
	private MerchantRechargeDao merchantRechargeDao;

	@Override
	protected CrudDao<MerchantRecharge> getEntityDao() {

		return merchantRechargeDao;
	}

}