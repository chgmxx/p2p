/**
 * 银行托管-账户-Service.
 */
package com.power.platform.cgb.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.cgb.dao.CgbUserAccountDao;
import com.power.platform.cgb.entity.CgbUserAccount;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.service.CrudService;

/**
 * 银行托管-账户-Service.
 * 
 * @author lance
 * @version 2017-10-26
 */
@Service("cgbUserAccountService")
@Transactional(readOnly = false)
public class CgbUserAccountService extends CrudService<CgbUserAccount> {

	@Resource
	private CgbUserAccountDao cgbUserAccountDao;

	@Override
	protected CrudDao<CgbUserAccount> getEntityDao() {

		return cgbUserAccountDao;
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int updateUserAccountInfo(CgbUserAccount entity) {

		int flag = 0;
		try {
			flag = cgbUserAccountDao.update(entity);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("fn:updateUserAccountInfo,{异常：" + e.getMessage() + "}");
		}

		return flag;
	}

	/**
	 * 根据用户id查找用户账户信息
	 * 
	 * @param userid
	 * @return
	 */

	public CgbUserAccount getUserAccountInfo(String userid) {

		return cgbUserAccountDao.getUserAccountInfo(userid);
	}
	
	/**
	 * 可用余额为0
	 * @param page
	 * @param entity
	 * @return
	 */
	public Page<CgbUserAccount> findPage0(Page<CgbUserAccount> page, CgbUserAccount entity) {
		entity.setPage(page);
		page.setList(cgbUserAccountDao.findAmountList0(entity));
		return page;
	}
	
	/**
	 * 可用余额不为0
	 * @param page
	 * @param entity
	 * @return
	 */
	public Page<CgbUserAccount> findPage1(Page<CgbUserAccount> page, CgbUserAccount entity) {
		entity.setPage(page);
		page.setList(cgbUserAccountDao.findAmountList1(entity));
		return page;
	}
}