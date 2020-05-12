package com.power.platform.lanmao.asyncmsg.rechain.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.power.platform.cgb.dao.CgbUserBankCardDao;
import com.power.platform.cgb.entity.CgbUserBankCard;
import com.power.platform.common.utils.IdGen;
import com.power.platform.credit.dao.userinfo.CreditUserInfoDao;
import com.power.platform.lanmao.rw.pojo.NotifyVo;
import com.power.platform.lanmao.search.pojo.LanMaoWhiteList;
import com.power.platform.lanmao.search.service.LanMaoWhiteListAddDataService;
import com.power.platform.lanmao.asyncmsg.rechain.*;
import com.power.platform.lanmao.dao.CgbBigrechargeWhiteRecordDao;
import com.power.platform.lanmao.dao.LmTransactionDao;
import com.power.platform.lanmao.entity.CgbBigrechargeWhiteRecord;
import com.power.platform.lanmao.entity.LmTransaction;
import com.power.platform.lanmao.type.ServiceNameEnum;
import com.power.platform.lanmao.type.UserRoleEnum;
import com.power.platform.regular.dao.WloanSubjectDao;
import com.power.platform.regular.service.WloanSubjectService;

/**
 * 企业绑卡
 */
public class EnterpriseBindBankcard implements IResponsibility {

	private static final Logger log = LoggerFactory.getLogger(EnterpriseBindBankcard.class);

	private CreditUserInfoDao creditUserInfoDao;

	private CgbUserBankCardDao cgbUserBankCardDao;

	private WloanSubjectDao wloanSubjectDao;

	private WloanSubjectService wloanSubjectService;
	
	private CgbBigrechargeWhiteRecordDao cgbBigrechargeWhiteRecordDao;

	private LmTransactionDao lmTransactionDao;

	private LanMaoWhiteListAddDataService whiteListAddDataService;

	public EnterpriseBindBankcard(CreditUserInfoDao creditUserInfoDao, CgbUserBankCardDao cgbUserBankCardDao, 
			WloanSubjectDao wloanSubjectDao, WloanSubjectService wloanSubjectService, 
			LanMaoWhiteListAddDataService whiteListAddDataService, 
			CgbBigrechargeWhiteRecordDao cgbBigrechargeWhiteRecordDao, 
			LmTransactionDao lmTransactionDao) {

		this.creditUserInfoDao = creditUserInfoDao;
		this.cgbUserBankCardDao = cgbUserBankCardDao;
		this.wloanSubjectDao = wloanSubjectDao;
		this.wloanSubjectService = wloanSubjectService;
		this.whiteListAddDataService = whiteListAddDataService;
		this.cgbBigrechargeWhiteRecordDao = cgbBigrechargeWhiteRecordDao;
		this.lmTransactionDao = lmTransactionDao;
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void doSomething(NotifyVo input, IResponsibility responsibility) {

		if (input != null && ServiceNameEnum.ENTERPRISE_BIND_BANKCARD.getValue().equals(input.getServiceName().toUpperCase())) {
			JSONObject json = JSONObject.parseObject(input.getRespData());
			String requestNo = "";
			String platformUserNo = "";
			String code = json.getString("code");
			String status = json.getString("status");
			String errorCode = "";
			String errorMessage = "";
			CgbUserBankCard userBankCard = null;
			long currentTimeMillis = System.currentTimeMillis();
			LmTransaction lm = null;
			if ("0".equals(code) && "SUCCESS".equals(status)) {
				requestNo = json.getString("requestNo");
				platformUserNo = json.getString("platformUserNo");
				/**
				 * 银行卡，已认证逻辑处理
				 */
				userBankCard = new CgbUserBankCard();
				userBankCard.setUserId(platformUserNo);
				List<CgbUserBankCard> list = cgbUserBankCardDao.findCreditList(userBankCard);
				if (null != list) {
					CgbUserBankCard bankCard = null;
					for (int i = 0; i < list.size(); i++) {
						bankCard = list.get(i); // 有且仅有一条银行卡记录
						if (i == 0) {
							String bankAccountNo = null;
							CgbBigrechargeWhiteRecord whiteRecord = new CgbBigrechargeWhiteRecord();
							whiteRecord.setRequestNo(requestNo);
							List<CgbBigrechargeWhiteRecord> whiteRecordList = cgbBigrechargeWhiteRecordDao.findList(whiteRecord);
							if(whiteRecordList!=null && whiteRecordList.size() != 0) {
								CgbBigrechargeWhiteRecord white = whiteRecordList.get(0);
								bankAccountNo = white.getBankNo();
							}
							if(bankAccountNo!=null&&!"".equals(bankAccountNo)) {
								bankCard.setBankAccountNo(bankAccountNo);
							}
							bankCard.setState(CgbUserBankCard.CERTIFY_YES);
							bankCard.setUpdateDate(new Date(currentTimeMillis));
							int updateBankCardFlag = cgbUserBankCardDao.update(bankCard);
							log.info("企业绑卡，状态变更:{}", updateBankCardFlag == 1 ? "成功" : "失败");
							//
							LanMaoWhiteList whiteList = new LanMaoWhiteList();
							whiteList.setRequestNo(json.getString("requestNo"));
							whiteList.setPlatformUserNo(platformUserNo);
							whiteList.setBankcardNo(bankCard.getBankAccountNo()); // 银行对公账户
							whiteList.setUserRole(UserRoleEnum.BORROWERS.getValue());
							Map<String, Object> result = whiteListAddDataService.whiteListAdd(whiteList,requestNo);
							if ("调用成功".equals(result.get("code")) && "处理成功".equals(result.get("status"))) {
								
								log.info("企业绑卡，添加白名单成功......");
							} else {
								log.info("errorCode:{}，errorMessage{}，企业绑卡，添加白名单失败......", result.get("errorCode"), result.get("errorMessage"));
							}
						}
					}
				}

				lm = new LmTransaction();
				// 懒猫交易留存
				lm = new LmTransaction();
				lm.setId(IdGen.uuid());
				lm.setRequestNo(requestNo);
				lm.setServiceName(ServiceNameEnum.ENTERPRISE_BIND_BANKCARD.getValue());
				lm.setPlatformUserNo(platformUserNo);
				lm.setCreateDate(new Date(currentTimeMillis));
				lm.setUpdateDate(new Date(currentTimeMillis));
				lm.setCode(code);
				lm.setStatus(status);
				int lmTransactionFlag = lmTransactionDao.insert(lm);
				log.info("懒猫交易留存，插入:{}", lmTransactionFlag == 1 ? "成功" : "失败");
			} else {
				errorCode = json.getString("errorCode");
				errorMessage = json.getString("errorMessage");
				// 懒猫交易留存
				lm = new LmTransaction();
				lm.setId(IdGen.uuid());
				lm.setRequestNo(requestNo);
				lm.setServiceName(ServiceNameEnum.ENTERPRISE_BIND_BANKCARD.getValue());
				lm.setPlatformUserNo(platformUserNo);
				lm.setCreateDate(new Date(currentTimeMillis));
				lm.setUpdateDate(new Date(currentTimeMillis));
				lm.setCode(code);
				lm.setStatus(status);
				lm.setErrorCode(errorCode);
				lm.setErrorMessage(errorMessage);
				int lmTransactionFlag = lmTransactionDao.insert(lm);
				log.info("懒猫交易留存，插入:{}", lmTransactionFlag == 1 ? "成功" : "失败");
			}
			return;
		}
		// 当前没法处理，回调回去，让下一个去处理
		responsibility.doSomething(input, responsibility);
	}
}
