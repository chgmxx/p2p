/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.credit.service.voucher;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.service.CrudService;
import com.power.platform.credit.dao.voucher.CreditVoucherDao;
import com.power.platform.credit.entity.userinfo.CreditUserAccount;
import com.power.platform.credit.entity.voucher.CreditVoucher;

/**
 * 发票Service
 * @author jice
 * @version 2018-03-14
 */
@Service
@Transactional(readOnly = true)
public class CreditVoucherService extends CrudService<CreditVoucher> {
	
	@Resource
	private CreditVoucherDao creditVoucherDao;

	public CreditVoucher get(String id) {
		return super.get(id);
	}
	
	public List<CreditVoucher> findList(CreditVoucher creditVoucher) {
		return super.findList(creditVoucher);
	}
	
	public Page<CreditVoucher> findPage(Page<CreditVoucher> page, CreditVoucher creditVoucher) {
		return super.findPage(page, creditVoucher);
	}
	
	@Transactional(readOnly = false)
	public void save(CreditVoucher creditVoucher) {
		super.save(creditVoucher);
	}
	
	@Transactional(readOnly = false)
	public void delete(CreditVoucher creditVoucher) {
		super.delete(creditVoucher);
	}
	
	@Override
	protected CrudDao<CreditVoucher> getEntityDao() {

		return creditVoucherDao;
	}

	/**
	 *借款端ERP根据申请查询发票
	 * @param creditInfoId
	 * @return
	 */
	public List<CreditVoucher> findListByInfoId(String creditInfoId) {
		// TODO Auto-generated method stub
		return creditVoucherDao.findListByInfoId(creditInfoId);
	}
	/**
	 *中登网根据标的id查询发票信息
	 */
	public List<CreditVoucher> findCreditVoucher(String projectId) {
		return creditVoucherDao.findCreditVoucher(projectId);
	}
	
}