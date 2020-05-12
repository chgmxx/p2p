/**
 * 银行托管-银行卡-Service.
 */
package com.power.platform.cgb.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.cgb.dao.CgbUserBankCardDao;
import com.power.platform.cgb.entity.CgbUserBankCard;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.service.CrudService;

/**
 * 银行托管-银行卡-Service.
 * 
 * @author lance
 * @version 2017-10-26
 */
@Service("cgbUserBankCardService")
@Transactional(readOnly = false)
public class CgbUserBankCardService extends CrudService<CgbUserBankCard> {

	@Resource
	private CgbUserBankCardDao cgbUserBankCardDao;

	@Override
	protected CrudDao<CgbUserBankCard> getEntityDao() {

		return cgbUserBankCardDao;
	}

	public Page<CgbUserBankCard> findCreditPage(Page<CgbUserBankCard> page, CgbUserBankCard entity) {
		entity.setPage(page);
		page.setList(cgbUserBankCardDao.findCreditList(entity));
		return page;
	}
	
	public CgbUserBankCard getInfoById(String orderId) {

		return cgbUserBankCardDao.getInfoById(orderId);
	}

	public CgbUserBankCard getBankCardInfoByUserId(String userId) {

		CgbUserBankCard userBankCard = null;
		try {
			userBankCard = cgbUserBankCardDao.getUserBankCardByUserId(userId);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("fn:getBankCardInfoByUserId,{异常：" + e.getMessage() + "}");
		}
		return userBankCard;
	}

	/**
	 * 根据userId获取银行卡信息
	 * 
	 * @param userId
	 * @return
	 */
	public CgbUserBankCard findByUserId(String userId) {

		CgbUserBankCard userBankCard = cgbUserBankCardDao.getUserBankCardByUserId(userId);
		return userBankCard;
	}
	
	public CgbUserBankCard findByUserId1(String userId) {

		CgbUserBankCard userBankCard = cgbUserBankCardDao.getUserBankCardByUserId2(userId);
		return userBankCard;
	}

	public CgbUserBankCard getBankCardInfoByUserId1(String userId) {
		// TODO Auto-generated method stub
		CgbUserBankCard userBankCard = null;
		try {
			userBankCard = cgbUserBankCardDao.getUserBankCardByUserId1(userId);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("fn:getBankCardInfoByUserId,{异常：" + e.getMessage() + "}");
		}
		return userBankCard;
	}
	
	public List<CgbUserBankCard> findState0(String createDate) {
		List<CgbUserBankCard> list = cgbUserBankCardDao.findState0(createDate);
		return list;
	}
	
	public Integer updateState2(String id) {
		Integer resultInteger = 0;
		Integer a = cgbUserBankCardDao.updateState2(id);
		if(a == 1){
			System.out.println("修改成功！");
			resultInteger = a;
		}
		return resultInteger;
	}
	
	//供应商销户
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void deleteBankByUserId(String userId){
		String delUserId = "del"+userId;
		cgbUserBankCardDao.deleteBankByUserId(delUserId,userId);
	};

}