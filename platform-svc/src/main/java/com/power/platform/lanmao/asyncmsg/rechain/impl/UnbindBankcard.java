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
import com.power.platform.lanmao.asyncmsg.rechain.IResponsibility;
import com.power.platform.lanmao.dao.LmTransactionDao;
import com.power.platform.lanmao.entity.LmTransaction;
import com.power.platform.lanmao.rw.pojo.NotifyException;
import com.power.platform.lanmao.rw.pojo.NotifyVo;
import com.power.platform.lanmao.search.pojo.LanMaoWhiteList;
import com.power.platform.lanmao.type.ServiceNameEnum;
import com.power.platform.lanmao.dao.CgbBigrechargeWhiteDao;
import com.power.platform.lanmao.search.service.LanMaoWhiteListDelDataService;
import com.power.platform.lanmao.type.UserRoleEnum;
import com.power.platform.userinfo.dao.UserInfoDao;
import com.power.platform.userinfo.entity.UserInfo;

/**
 * 
 * class: UnbindBankcard <br>
 * description: 解绑银行卡 <br>
 * author: Roy <br>
 * date: 2019年10月19日 上午11:44:34
 */
public class UnbindBankcard implements IResponsibility {

	private static final Logger log = LoggerFactory.getLogger(UnbindBankcard.class);

	private UserInfoDao userInfoDao;

	private CgbUserBankCardDao cgbUserBankCardDao;

	private LmTransactionDao lmTransactionDao;

	private CgbBigrechargeWhiteDao cgbBigrechargeWhiteDao;

	private LanMaoWhiteListDelDataService whiteListDelDataService;

	private CreditUserInfoDao creditUserInfoDao;

	public UnbindBankcard(UserInfoDao userInfoDao, CgbUserBankCardDao cgbUserBankCardDao, LmTransactionDao lmTransactionDao, CgbBigrechargeWhiteDao cgbBigrechargeWhiteDao, LanMaoWhiteListDelDataService whiteListDelDataService, CreditUserInfoDao creditUserInfoDao) {

		this.userInfoDao = userInfoDao;
		this.cgbUserBankCardDao = cgbUserBankCardDao;
		this.lmTransactionDao = lmTransactionDao;
		this.cgbBigrechargeWhiteDao = cgbBigrechargeWhiteDao;
		this.whiteListDelDataService = whiteListDelDataService;
		this.creditUserInfoDao = creditUserInfoDao;
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void doSomething(NotifyVo input, IResponsibility responsibility) {

		if (input != null && ServiceNameEnum.UNBIND_BANKCARD.getValue().equals(input.getServiceName().toUpperCase())) {
			try {
				log.info("解绑银行卡，业务处理开始...start...");
				JSONObject json = JSONObject.parseObject(input.getRespData());
				String requestNo = "";
				String platformUserNo = "";
				String code = json.getString("code");
				String status = json.getString("status");
				String errorCode = "";
				String errorMessage = "";
				CgbUserBankCard userBankCard = null;
				long currentTimeMillis = System.currentTimeMillis();
				if ("0".equals(code) && "SUCCESS".equals(status)) { // 处理成做相应处理

					requestNo = json.getString("requestNo");
					platformUserNo = json.getString("platformUserNo");

					UserInfo userInfo = userInfoDao.getCgb(platformUserNo); // 出借人帐号信息
					LmTransaction lm = null;
					if (null != userInfo) {
						// 解绑银行卡，处理银行卡字段state，0：已解绑（未认证），1：已绑卡（已认证）
						userBankCard = new CgbUserBankCard();
						userBankCard.setUserId(platformUserNo);
						List<CgbUserBankCard> list = cgbUserBankCardDao.findList(userBankCard);
						CgbUserBankCard bankCard = null;
						if (null != list) {
							for (int i = 0; i < list.size(); i++) {
								bankCard = list.get(i); // 有且仅有一条银行卡记录
								if (i == 0) {
									bankCard.setState(CgbUserBankCard.CERTIFY_NO);
									bankCard.setUpdateDate(new Date(currentTimeMillis));
									int updateBankCardFlag = cgbUserBankCardDao.update(bankCard);
									log.info("出借人解绑银行卡，状态变更:{}", updateBankCardFlag == 1 ? "成功" : "失败");

									LanMaoWhiteList lmWhiteList = null;
									lmWhiteList = new LanMaoWhiteList();
									lmWhiteList.setRequestNo(IdGen.uuid());
									lmWhiteList.setPlatformUserNo(platformUserNo);
									lmWhiteList.setBankcardNo(bankCard.getBankAccountNo());
									lmWhiteList.setUserRole(UserRoleEnum.INVESTOR.getValue());
									Map<String, Object> result = whiteListDelDataService.whiteListDel(lmWhiteList,requestNo);
									if ("调用成功".equals(result.get("code")) && "处理成功".equals(result.get("status"))) {
										log.info("出借人网银转账充值白名单删除成功......");
									} else {
										log.info("errorCode:{}，errorMessage{}，出借人网银转账充值白名单删除失败......", result.get("errorCode"), result.get("errorMessage"));
									}
								}
							}
						}

						// 出借人帐号信息，cgb_bind_card_state，1：未绑卡，2：已绑卡
						userInfo.setCgbBindBankCardState(UserInfo.CGB_BIND_CARD_NO); // 未绑卡
						userInfo.setBindBankCardState(UserInfo.BIND_CARD_NO); // 未绑卡
						userInfo.setUpdateDate(new Date(currentTimeMillis));
						int updateUserInfoFlag = userInfoDao.update(userInfo);
						log.info("出借人帐号，解绑银行卡状态更新:{}", updateUserInfoFlag == 1 ? "成功" : "失败");

						lm = new LmTransaction();
						// 懒猫交易留存
						lm = new LmTransaction();
						lm.setId(IdGen.uuid());
						lm.setRequestNo(requestNo);
						lm.setServiceName(ServiceNameEnum.UNBIND_BANKCARD.getValue());
						lm.setPlatformUserNo(platformUserNo);
						lm.setCreateDate(new Date(currentTimeMillis));
						lm.setUpdateDate(new Date(currentTimeMillis));
						lm.setCode(code);
						lm.setStatus(status);
						int lmTransactionFlag = lmTransactionDao.insert(lm);
						log.info("懒猫交易留存，插入:{}", lmTransactionFlag == 1 ? "成功" : "失败");
					} else {
						// 解绑银行卡，处理银行卡字段state，0：已解绑（未认证），1：已绑卡（已认证）
						userBankCard = new CgbUserBankCard();
						userBankCard.setUserId(platformUserNo);
						List<CgbUserBankCard> list = cgbUserBankCardDao.findCreditList(userBankCard);
						CgbUserBankCard bankCard = null;
						if (null != list) {
							for (int i = 0; i < list.size(); i++) {
								bankCard = list.get(i); // 有且仅有一条银行卡记录
								if (i == 0) {
									bankCard.setState(CgbUserBankCard.CERTIFY_NO);
									bankCard.setUpdateDate(new Date(currentTimeMillis));
									int updateBankCardFlag = cgbUserBankCardDao.update(bankCard);
									log.info("借款人解绑银行卡，状态变更:{}", updateBankCardFlag == 1 ? "成功" : "失败");

									LanMaoWhiteList lmWhiteList = null;
									lmWhiteList = new LanMaoWhiteList();
									lmWhiteList.setRequestNo(IdGen.uuid());
									lmWhiteList.setPlatformUserNo(platformUserNo);
									lmWhiteList.setBankcardNo(bankCard.getBankAccountNo());
									lmWhiteList.setUserRole(UserRoleEnum.BORROWERS.getValue());
									Map<String, Object> result = whiteListDelDataService.whiteListDel(lmWhiteList,requestNo);
									if ("调用成功".equals(result.get("code")) && "处理成功".equals(result.get("status"))) {
										log.info("借款人网银转账充值白名单删除成功......");
									} else {
										log.info("errorCode:{}，errorMessage{}，借款人网银转账充值白名单删除失败......", result.get("errorCode"), result.get("errorMessage"));
									}
								}
							}
						}
					}
				} else {
					errorCode = json.getString("errorCode");
					errorMessage = json.getString("errorMessage");
					LmTransaction lm = new LmTransaction();
					// 懒猫交易留存
					lm = new LmTransaction();
					lm.setId(IdGen.uuid());
					lm.setRequestNo(requestNo);
					lm.setServiceName(ServiceNameEnum.USER_AUTHORIZATION.getValue());
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
				log.info("解绑银行卡，业务处理开始...end...");
				return;
			} catch (Exception e) {
				throw new NotifyException("解绑银行卡异常------异步通知");
			}
		}
		// 当前没法处理，回调回去，让下一个去处理
		responsibility.doSomething(input, responsibility);
	}
}
