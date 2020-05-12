package com.power.platform.credit.service.bankcardinfo;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.credit.dao.bankcardinfo.CreditBankCardInfoDao;
import com.power.platform.credit.entity.bankcardinfo.CreditBankCardInfo;

/**
 * 信贷银行卡Service
 * 
 * @author nice
 * @version 2017-03-23
 */
@Service
@Transactional(readOnly = true)
public class CreditBankCardInfoService extends CrudService<CreditBankCardInfo> {

	@Resource
	private CreditBankCardInfoDao creditBankCardInfoDao;
	
	@Override
	protected CrudDao<CreditBankCardInfo> getEntityDao() {

		// TODO Auto-generated method stub
		return creditBankCardInfoDao;
	}

}