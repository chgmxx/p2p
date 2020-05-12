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
import com.power.platform.credit.dao.voucher.CreditVoucherInfoDao;
import com.power.platform.credit.entity.voucher.CreditVoucherInfo;


/**
 * 发票信息Service
 * @author jice
 * @version 2018-06-20
 */
@Service
@Transactional(readOnly = true)
public class CreditVoucherInfoService extends CrudService<CreditVoucherInfo> {

	@Resource
	private CreditVoucherInfoDao creditVoucherInfoDao;
	
	public CreditVoucherInfo get(String id) {
		return super.get(id);
	}
	
	public List<CreditVoucherInfo> findList(CreditVoucherInfo creditVoucherInfo) {
		return super.findList(creditVoucherInfo);
	}
	
	public Page<CreditVoucherInfo> findPage(Page<CreditVoucherInfo> page, CreditVoucherInfo creditVoucherInfo) {
		return super.findPage(page, creditVoucherInfo);
	}
	
	@Transactional(readOnly = false)
	public void save(CreditVoucherInfo creditVoucherInfo) {
		super.save(creditVoucherInfo);
	}
	
	@Transactional(readOnly = false)
	public void delete(CreditVoucherInfo creditVoucherInfo) {
		super.delete(creditVoucherInfo);
	}
	
	@Override
	protected CrudDao<CreditVoucherInfo> getEntityDao() {

		return creditVoucherInfoDao;
	}
	
}