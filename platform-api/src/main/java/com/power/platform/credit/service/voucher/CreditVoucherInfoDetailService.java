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
import com.power.platform.credit.dao.voucher.CreditVoucherInfoDetailDao;
import com.power.platform.credit.entity.voucher.CreditVoucherInfoDetail;


/**
 * 开票详情Service
 * @author jice
 * @version 2018-06-25
 */
@Service
@Transactional(readOnly = true)
public class CreditVoucherInfoDetailService extends CrudService<CreditVoucherInfoDetail> {
	
	public static final String STATE1 = "1";//申请中 
	public static final String STATE2 = "2";//审核通过

	@Resource
	private CreditVoucherInfoDetailDao creditVoucherInfoDetailDao;
	
	public CreditVoucherInfoDetail get(String id) {
		return super.get(id);
	}
	
	public List<CreditVoucherInfoDetail> findList(CreditVoucherInfoDetail creditVoucherInfoDetail) {
		return super.findList(creditVoucherInfoDetail);
	}
	
	public Page<CreditVoucherInfoDetail> findPage(Page<CreditVoucherInfoDetail> page, CreditVoucherInfoDetail creditVoucherInfoDetail) {
		return super.findPage(page, creditVoucherInfoDetail);
	}
	
	@Transactional(readOnly = false)
	public void save(CreditVoucherInfoDetail creditVoucherInfoDetail) {
		super.save(creditVoucherInfoDetail);
	}
	
	@Transactional(readOnly = false)
	public void delete(CreditVoucherInfoDetail creditVoucherInfoDetail) {
		super.delete(creditVoucherInfoDetail);
	}
	
	@Override
	protected CrudDao<CreditVoucherInfoDetail> getEntityDao() {

		return creditVoucherInfoDetailDao;
	}
	
}