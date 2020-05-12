package com.power.platform.lanmao.account.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.power.platform.cgb.dao.CgbUserAccountDao;
import com.power.platform.cgb.dao.CgbUserBankCardDao;
import com.power.platform.cgb.dao.ZtmgUserAuthorizationDao;
import com.power.platform.cgb.entity.CgbUserBankCard;
import com.power.platform.cgb.entity.CicmorganBankCode;
import com.power.platform.cgb.entity.ZtmgUserAuthorization;
import com.power.platform.cgb.service.CicmorganBankCodeService;
import com.power.platform.common.config.Global;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.IdcardUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.credit.dao.userinfo.CreditUserInfoDao;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.service.userinfo.CreditUserInfoService;
import com.power.platform.lanmao.dao.LmTransactionDao;
import com.power.platform.lanmao.entity.LmTransaction;
import com.power.platform.lanmao.rw.pojo.NotifyVo;
import com.power.platform.lanmao.search.pojo.LanMaoWhiteList;
import com.power.platform.lanmao.search.service.LanMaoWhiteListAddDataService;
import com.power.platform.lanmao.type.AuditStatusEnum;
import com.power.platform.lanmao.type.AuthEnum;
import com.power.platform.lanmao.type.ServiceNameEnum;
import com.power.platform.userinfo.dao.UserInfoDao;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.userinfo.service.UserInfoService;

/**
 * 处理已开户用户激活异步通知
 * 
 * @author google
 *
 */
@Service("activateStockedUserNotifyService")
public class ActivateStockedUserNotifyService {

	private static final Logger log = LoggerFactory.getLogger(ActivateStockedUserNotifyService.class);
	@Autowired
	private UserInfoDao userInfoDao;
	@Autowired
	private CgbUserAccountDao cgbUserAccountDao;
	@Autowired
	private CgbUserBankCardDao cgbUserBankCardDao;
	@Autowired
	private LmTransactionDao lmTransactionDao;
	@Autowired
	private UserInfoService userInfoService;
	@Autowired
	private CreditUserInfoService creditUserInfoService;
	@Autowired
	private CreditUserInfoDao creditUserInfoDao;
	@Autowired
	private LanMaoWhiteListAddDataService whiteListAddDataService;
	@Autowired
	private CicmorganBankCodeService cicmorganBankCodeService;
	@Autowired
	private ZtmgUserAuthorizationDao ztmgUserAuthorizationDao;

	public void processingNotice(NotifyVo input) throws Exception {

		log.info("会员激活，异步回调......");
		JSONObject json = JSONObject.parseObject(input.getRespData());
		String platformUserNo = json.getString("platformUserNo");
		String requestNo = json.getString("requestNo");
		String status = json.getString("status");
		String realName = "";
		String idCardNo = "";
		String cardNolsChange = "";
		String idCardType = "";
		String mobile = "";
		String bankcardNo = "";
		String bankcode = "";
		String accessType = "";
		String userRole = "";
		String amount = "";
		String failTime = "";
		String code = "";
		String auditStatus = "";
		String errorCode = "";
		String errorMessage = "";
		UserInfo userInfo = null;
		CreditUserInfo creditUserInfo = null;
		long currentTimeMillis = System.currentTimeMillis();
		LmTransaction lm = null;
		if ("SUCCESS".equals(status)) { // 业务处理成功
			code = json.getString("code");
			auditStatus = json.getString("auditStatus");
			realName = json.getString("realName");
			idCardNo = json.getString("idCardNo");
			cardNolsChange = json.getString("cardNolsChange");
			idCardType = json.getString("idCardType");
			mobile = json.getString("mobile");
			bankcardNo = json.getString("bankcardNo");
			bankcode = json.getString("bankcode");
			accessType = json.getString("accessType");
			userRole = json.getString("userRole");
			amount = json.getString("amount");
			failTime = json.getString("failTime");
			// 出借人
			userInfo = userInfoDao.getCgb(platformUserNo);
			if (null != userInfo) {
				if ("TRUE".equals(userInfo.getIsActivate())) {
					return;
				} else {
					log.info("出借人，会员激活异步通知，业务处理...start...");
					if (AuditStatusEnum.PASSED.getValue().equals(auditStatus)) { // 审核通过
						if (!StringUtils.isBlank(realName)) {
							userInfo.setRealName(realName);
						}
						if (!StringUtils.isBlank(idCardNo)) {
							userInfo.setCertificateNo(idCardNo);
							String birthday = IdcardUtils.getBirthByIdCard(idCardNo);
							userInfo.setBirthday(birthday.substring(4, birthday.length())); // 客户生日设置.
						}
						if ("TRUE".equals(cardNolsChange)) { // 激活页面修改过已导入的卡号
							userInfo.setCertificateChecked(UserInfo.CERTIFICATE_NO);
							userInfo.setCgbBindBankCardState(UserInfo.CGB_BIND_CARD_YES);
							userInfo.setBindBankCardState(UserInfo.BIND_CARD_YES);
						}
						if ("PRC_ID".equals(idCardType)) {// 身份证
							userInfo.setUserType(UserInfo.PRC_ID);
						} else if ("PASSPORT".equals(idCardType)) {// 护照
							userInfo.setUserType(UserInfo.PASSPORT);
						} else if ("COMPATRIOTS_CARD".equals(idCardType)) {// 港澳台通行证
							userInfo.setUserType(UserInfo.COMPATRIOTS_CARD);
						} else {// 外国人永久居留证
							userInfo.setUserType(UserInfo.PERMANENT_RESIDENCE);
						}
						userInfo.setIsActivate("TRUE");// 懒猫2.0迁移，会员激活标识，TRUE、FALSE
						int j = userInfoDao.update(userInfo);
						log.info("会员激活-出借人帐号信息，更新:{}", j == 1 ? "成功" : "失败");
						CgbUserBankCard userBankCard = new CgbUserBankCard();
						userBankCard.setUserId(platformUserNo);
						List<CgbUserBankCard> list = cgbUserBankCardDao.findList(userBankCard);
						if (list != null && list.size() != 0) {
							// 银行卡数据唯一
							CgbUserBankCard cbc = list.get(0);
							cbc.setBankAccountNo(bankcardNo);
							cbc.setBankCardPhone(mobile);
							cbc.setBankNo(bankcode);

							CicmorganBankCode cicmorganBankCode = new CicmorganBankCode();
							cicmorganBankCode.setBankCode(bankcode);
							List<CicmorganBankCode> cicmorganBankCodes = cicmorganBankCodeService.findList1(cicmorganBankCode);
							if (cicmorganBankCodes != null && cicmorganBankCodes.size() > 0) {
								CicmorganBankCode cicmorganBC = cicmorganBankCodes.get(0);
								cbc.setBankName(cicmorganBC.getBankName());
							}
							cbc.setUpdateDate(new Date(currentTimeMillis));
							int i = cgbUserBankCardDao.update(cbc);
							log.info("会员激活-出借人银行卡信息，更新:{}", i == 1 ? "成功" : "失败");
						}
						// 添加白名单
						LanMaoWhiteList whiteList = new LanMaoWhiteList();
						whiteList.setRequestNo(requestNo);
						whiteList.setPlatformUserNo(platformUserNo);
						whiteList.setBankcardNo(bankcardNo);
						whiteList.setUserRole(userRole);
						Map<String, Object> result = whiteListAddDataService.whiteListAdd(whiteList,requestNo);
						if (result.get("code").equals("调用成功") && result.get("status").equals("处理成功")) {
							log.info("会员激活，出借人添加白名单成功 ......");
						} else {
							log.info("会员激活，出借人添加白名单失败 ...errorMessage:{}...", result.get("errorMessage"));
						}
						// 懒猫记录留存
						lm = new LmTransaction();
						lm.setId(IdGen.uuid());
						lm.setServiceName(ServiceNameEnum.ACTIVATE_STOCKED_USER.getValue());
						lm.setPlatformUserNo(platformUserNo);
						lm.setRequestNo(requestNo);
						lm.setCode(code);
						lm.setStatus(status);
						lm.setAccessType(accessType);
						lm.setAuditStatus(auditStatus);
						int insertLMFlag = lmTransactionDao.insert(lm);
						log.info("懒猫记录留存:{}", insertLMFlag == 1 ? "成功" : "失败");
					} else if (AuditStatusEnum.AUDIT.getValue().equals(auditStatus)) { // 审核中
						log.info("出借人，会员激活异步通知，业务处理...审核中...");
						// 懒猫记录留存
						lm = new LmTransaction();
						lm.setId(IdGen.uuid());
						lm.setServiceName(ServiceNameEnum.ACTIVATE_STOCKED_USER.getValue());
						lm.setPlatformUserNo(platformUserNo);
						lm.setRequestNo(requestNo);
						lm.setCode(code);
						lm.setStatus(status);
						lm.setAccessType(accessType);
						lm.setAuditStatus(auditStatus);
						int insertLMFlag = lmTransactionDao.insert(lm);
						log.info("懒猫记录留存:{}", insertLMFlag == 1 ? "成功" : "失败");
					} else if (AuditStatusEnum.BACK.getValue().equals(auditStatus)) { // 审核回退
						log.info("出借人，会员激活异步通知，业务处理...审核回退...");
						// 懒猫记录留存
						lm = new LmTransaction();
						lm.setId(IdGen.uuid());
						lm.setServiceName(ServiceNameEnum.ACTIVATE_STOCKED_USER.getValue());
						lm.setPlatformUserNo(platformUserNo);
						lm.setRequestNo(requestNo);
						lm.setCode(code);
						lm.setStatus(status);
						lm.setAccessType(accessType);
						lm.setAuditStatus(auditStatus);
						int insertLMFlag = lmTransactionDao.insert(lm);
						log.info("懒猫记录留存:{}", insertLMFlag == 1 ? "成功" : "失败");
					} else if (AuditStatusEnum.REFUSED.getValue().equals(auditStatus)) { // 审核拒绝
						log.info("出借人，会员激活异步通知，业务处理...审核拒绝...");
						// 懒猫记录留存
						lm = new LmTransaction();
						lm.setId(IdGen.uuid());
						lm.setServiceName(ServiceNameEnum.ACTIVATE_STOCKED_USER.getValue());
						lm.setPlatformUserNo(platformUserNo);
						lm.setRequestNo(requestNo);
						lm.setCode(code);
						lm.setStatus(status);
						lm.setAccessType(accessType);
						lm.setAuditStatus(auditStatus);
						int insertLMFlag = lmTransactionDao.insert(lm);
						log.info("懒猫记录留存:{}", insertLMFlag == 1 ? "成功" : "失败");
					}
					log.info("出借人，会员激活异步通知，业务处理...end...");
				}
			} else {
				creditUserInfo = creditUserInfoService.get(platformUserNo);
				if (null != creditUserInfo) {
					if ("TRUE".equals(creditUserInfo.getIsActivate())) {
						// 说明已经做处理了。
						return;
					} else {
						log.info("借款人，会员激活异步通知，业务处理...start...");
						if (AuditStatusEnum.PASSED.getValue().equals(auditStatus)) { // 审核通过
							creditUserInfo.setIsActivate("TRUE");// 懒猫2.0迁移，会员激活标识，TRUE、FALSE
							creditUserInfo.setUpdateDate(new Date(currentTimeMillis));
							int j = creditUserInfoDao.update(creditUserInfo);
							log.info("会员激活，借款人帐号信息修改:{}", j == 1 ? "成功" : "失败");
							CgbUserBankCard userBankCard = new CgbUserBankCard();
							userBankCard.setUserId(platformUserNo);
							List<CgbUserBankCard> list = cgbUserBankCardDao.findList(userBankCard);
							if (null != list && list.size() > 0) {
								// 银行卡数据唯一
								CgbUserBankCard cbc = list.get(0);
								cbc.setBankAccountNo(bankcardNo);
								cbc.setBankCardPhone(mobile);
								cbc.setBankNo(bankcode);

								CicmorganBankCode cicmorganBankCode = new CicmorganBankCode();
								cicmorganBankCode.setBankCode(bankcode);
								List<CicmorganBankCode> cicmorganBankCodes = cicmorganBankCodeService.findList1(cicmorganBankCode);
								if (cicmorganBankCodes != null && cicmorganBankCodes.size() > 0) {
									CicmorganBankCode cicmorganBC = cicmorganBankCodes.get(0);
									cbc.setBankName(cicmorganBC.getBankName());
								}
								cbc.setUpdateDate(new Date(currentTimeMillis));
								int i = cgbUserBankCardDao.update(cbc);
								log.info("会员激活-借款人银行卡信息，更新:{}", i == 1 ? "成功" : "失败");
							}
							// 添加白名单
							LanMaoWhiteList whiteList = new LanMaoWhiteList();
							whiteList.setRequestNo(requestNo);
							whiteList.setPlatformUserNo(platformUserNo);
							whiteList.setBankcardNo(bankcardNo);
							whiteList.setUserRole(userRole);
							Map<String, Object> result = whiteListAddDataService.whiteListAdd(whiteList,requestNo);
							if (result.get("code").equals("调用成功") && result.get("status").equals("处理成功")) {
								log.info("会员激活，借款人添加白名单成功 ......");
							} else {
								log.info("会员激活，借款人添加白名单失败 ...errorMessage:{}...", result.get("errorMessage"));
							}
							/**
							 * 用户授权
							 */
							if (!StringUtils.isBlank(amount) && !StringUtils.isBlank(failTime)) {
								ZtmgUserAuthorization creUserAuthorization = new ZtmgUserAuthorization();
								creUserAuthorization.setUserId(platformUserNo);
								List<ZtmgUserAuthorization> authorizationList = ztmgUserAuthorizationDao.findList(creUserAuthorization);
								if (authorizationList != null && authorizationList.size() > 0) {
									ZtmgUserAuthorization ztmgUserAuthorization = authorizationList.get(0);
									ztmgUserAuthorization.setGrantList(AuthEnum.REPAYMENT.getValue());
									ztmgUserAuthorization.setStatus("S");
									ztmgUserAuthorization.setSignature(null);
									ztmgUserAuthorization.setGrantAmountList(amount); // 授权金额
									ztmgUserAuthorization.setGrantTimeList(failTime); // 授权截至期限
									ztmgUserAuthorization.setUpdateDate(new Date(currentTimeMillis));
									ztmgUserAuthorization.setRemarks("变更授权信息");
									int updateCreUserAuthorization = ztmgUserAuthorizationDao.update(ztmgUserAuthorization);
									log.info("变更授权信息:{}", updateCreUserAuthorization == 1 ? "成功" : "失败");
								} else {
									ZtmgUserAuthorization ztmgUserAuthorization = new ZtmgUserAuthorization();
									ztmgUserAuthorization.setId(IdGen.uuid());
									ztmgUserAuthorization.setUserId(platformUserNo);
									ztmgUserAuthorization.setMerchantId(Global.getConfigLanMao("platformNo"));
									ztmgUserAuthorization.setStatus("S");
									ztmgUserAuthorization.setSignature(null);
									ztmgUserAuthorization.setGrantList(AuthEnum.REPAYMENT.getValue());
									ztmgUserAuthorization.setGrantAmountList(amount);
									ztmgUserAuthorization.setGrantTimeList(failTime);
									ztmgUserAuthorization.setCreateDate(new Date(currentTimeMillis));
									ztmgUserAuthorization.setUpdateDate(new Date(currentTimeMillis));
									ztmgUserAuthorization.setRemarks("新增授权信息");
									int insertCreUserAuthorization = ztmgUserAuthorizationDao.insert(ztmgUserAuthorization);
									log.info("新增授权信息:{}", insertCreUserAuthorization == 1 ? "成功" : "失败");
								}
							}
							// 懒猫记录留存
							lm = new LmTransaction();
							lm.setId(IdGen.uuid());
							lm.setServiceName(ServiceNameEnum.ACTIVATE_STOCKED_USER.getValue());
							lm.setPlatformUserNo(platformUserNo);
							lm.setRequestNo(requestNo);
							lm.setCode(code);
							lm.setStatus(status);
							lm.setAccessType(accessType);
							lm.setAuditStatus(auditStatus);
							int insertLMFlag = lmTransactionDao.insert(lm);
							log.info("懒猫记录留存:{}", insertLMFlag == 1 ? "成功" : "失败");
						} else if (AuditStatusEnum.AUDIT.getValue().equals(auditStatus)) { // 审核中
							log.info("借款人，会员激活异步通知，业务处理...审核中...");
							// 懒猫记录留存
							lm = new LmTransaction();
							lm.setId(IdGen.uuid());
							lm.setServiceName(ServiceNameEnum.ACTIVATE_STOCKED_USER.getValue());
							lm.setPlatformUserNo(platformUserNo);
							lm.setRequestNo(requestNo);
							lm.setCode(code);
							lm.setStatus(status);
							lm.setAccessType(accessType);
							lm.setAuditStatus(auditStatus);
							int insertLMFlag = lmTransactionDao.insert(lm);
							log.info("懒猫记录留存:{}", insertLMFlag == 1 ? "成功" : "失败");
						} else if (AuditStatusEnum.BACK.getValue().equals(auditStatus)) { // 审核回退
							log.info("借款人，会员激活异步通知，业务处理...审核回退...");
							// 懒猫记录留存
							lm = new LmTransaction();
							lm.setId(IdGen.uuid());
							lm.setServiceName(ServiceNameEnum.ACTIVATE_STOCKED_USER.getValue());
							lm.setPlatformUserNo(platformUserNo);
							lm.setRequestNo(requestNo);
							lm.setCode(code);
							lm.setStatus(status);
							lm.setAccessType(accessType);
							lm.setAuditStatus(auditStatus);
							int insertLMFlag = lmTransactionDao.insert(lm);
							log.info("懒猫记录留存:{}", insertLMFlag == 1 ? "成功" : "失败");
						} else if (AuditStatusEnum.REFUSED.getValue().equals(auditStatus)) { // 审核拒绝
							log.info("借款人，会员激活异步通知，业务处理...审核拒绝...");
							// 懒猫记录留存
							lm = new LmTransaction();
							lm.setId(IdGen.uuid());
							lm.setServiceName(ServiceNameEnum.ACTIVATE_STOCKED_USER.getValue());
							lm.setPlatformUserNo(platformUserNo);
							lm.setRequestNo(requestNo);
							lm.setCode(code);
							lm.setStatus(status);
							lm.setAccessType(accessType);
							lm.setAuditStatus(auditStatus);
							int insertLMFlag = lmTransactionDao.insert(lm);
							log.info("懒猫记录留存:{}", insertLMFlag == 1 ? "成功" : "失败");
						}
						log.info("借款人，会员激活异步通知，业务处理...end...");
					}
				}
			}
		} else {
			errorCode = json.getString("errorCode");
			errorMessage = json.getString("errorMessage");
			// 会员激活失败，懒猫流水留存
			// 懒猫记录留存
			lm = new LmTransaction();
			lm.setId(IdGen.uuid());
			lm.setServiceName(ServiceNameEnum.ACTIVATE_STOCKED_USER.getValue());
			lm.setPlatformUserNo(platformUserNo);
			lm.setRequestNo(requestNo);
			lm.setCode(code);
			lm.setStatus(status);
			lm.setErrorCode(errorCode);
			lm.setErrorMessage(errorMessage);
			lm.setAccessType(accessType);
			lm.setAuditStatus(auditStatus);
			int insertLMFlag = lmTransactionDao.insert(lm);
			log.info("懒猫记录留存:{}", insertLMFlag == 1 ? "成功" : "失败");
		}
		return;
	}

}
