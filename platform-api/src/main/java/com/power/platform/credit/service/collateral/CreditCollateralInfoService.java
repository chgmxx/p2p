package com.power.platform.credit.service.collateral;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.service.CrudService;
import com.power.platform.credit.dao.collateral.CreditCollateralInfoDao;
import com.power.platform.credit.entity.collateral.CreditCollateralInfo;

/**
 * 抵押物信息Service
 * 
 * @author nice
 * @version 2017-05-10
 */
@Service
public class CreditCollateralInfoService extends CrudService<CreditCollateralInfo> {

	/**
	 * 审核中.
	 */
	public static final String CREDIT_COLLATERAL_INFO_STATE_1 = "1";
	/**
	 * 已通过.
	 */
	public static final String CREDIT_COLLATERAL_INFO_STATE_2 = "2";
	/**
	 * 已拒绝.
	 */
	public static final String CREDIT_COLLATERAL_INFO_STATE_3 = "3";

	@Resource
	private CreditCollateralInfoDao creditCollateralInfoDao;

	public CreditCollateralInfo get(String id) {

		return super.get(id);
	}

	public List<CreditCollateralInfo> findList(CreditCollateralInfo creditCollateralInfo) {

		return super.findList(creditCollateralInfo);
	}

	public Page<CreditCollateralInfo> findPage(Page<CreditCollateralInfo> page, CreditCollateralInfo creditCollateralInfo) {

		return super.findPage(page, creditCollateralInfo);
	}

	@Transactional(readOnly = false)
	public void save(CreditCollateralInfo creditCollateralInfo) {

		super.save(creditCollateralInfo);
	}

	@Transactional(readOnly = false)
	public void delete(CreditCollateralInfo creditCollateralInfo) {

		super.delete(creditCollateralInfo);
	}

	@Override
	protected CrudDao<CreditCollateralInfo> getEntityDao() {

		return creditCollateralInfoDao;
	}

}