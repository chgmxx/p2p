package com.power.platform.credit.service.userinfo;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.service.CrudService;
import com.power.platform.credit.dao.userinfo.CreditUserAccountDao;
import com.power.platform.credit.entity.userinfo.CreditUserAccount;

/**
 * 账户信息Service
 * 
 * @author nice
 * @version 2017-06-14
 */
@Service
@Transactional(readOnly = false)
public class CreditUserAccountService extends CrudService<CreditUserAccount> {

	/**
	 * 投资户.
	 */
	public static final String BIZ_TYPE_01 = "01";

	/**
	 * 借款户.
	 */
	public static final String BIZ_TYPE_02 = "02";

	/**
	 * 担保户.
	 */
	public static final String BIZ_TYPE_03 = "03";

	/**
	 * 咨询户.
	 */
	public static final String BIZ_TYPE_04 = "04";

	/**
	 * P2P平台户.
	 */
	public static final String BIZ_TYPE_05 = "05";

	/**
	 * 营销户.
	 */
	public static final String BIZ_TYPE_08 = "08";

	/**
	 * 收费户.
	 */
	public static final String BIZ_TYPE_10 = "10";

	/**
	 * 代偿户.
	 */
	public static final String BIZ_TYPE_11 = "11";

	/**
	 * 第三方营销账户.
	 */
	public static final String BIZ_TYPE_12 = "12";

	/**
	 * 垫资账户.
	 */
	public static final String BIZ_TYPE_13 = "13";

	@Resource
	private CreditUserAccountDao creditUserAccountDao;

	public CreditUserAccount get(String id) {

		return super.get(id);
	}

	public List<CreditUserAccount> findList(CreditUserAccount creditUserAccount) {

		return super.findList(creditUserAccount);
	}

	public Page<CreditUserAccount> findPage(Page<CreditUserAccount> page, CreditUserAccount creditUserAccount) {

		return super.findPage(page, creditUserAccount);
	}

	@Transactional(readOnly = false)
	public void save(CreditUserAccount creditUserAccount) {

		super.save(creditUserAccount);
	}

	@Transactional(readOnly = false)
	public void delete(CreditUserAccount creditUserAccount) {

		super.delete(creditUserAccount);
	}

	@Override
	protected CrudDao<CreditUserAccount> getEntityDao() {

		return creditUserAccountDao;
	}

}