/**
 * 银行编码对照Service.
 */
package com.power.platform.cgb.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.cgb.dao.CicmorganBankCodeDao;
import com.power.platform.cgb.entity.CicmorganBankCode;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;

/**
 * 银行编码对照Service.
 * 
 * @author lance
 * @version 2017-11-28
 */
@Service("cicmorganBankCodeService")
@Transactional(readOnly = false)
public class CicmorganBankCodeService extends CrudService<CicmorganBankCode> {

	@Resource
	private CicmorganBankCodeDao cicmorganBankCodeDao;

	@Override
	protected CrudDao<CicmorganBankCode> getEntityDao() {

		return cicmorganBankCodeDao;
	}

	public List<CicmorganBankCode> findList1(CicmorganBankCode code) {
		// TODO Auto-generated method stub
		return cicmorganBankCodeDao.findList1(code);
	}

}