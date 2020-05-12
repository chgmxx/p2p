package com.power.platform.lanmao.account.service;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.power.platform.cache.Cache;
import com.power.platform.cgb.dao.CgbUserBankCardDao;
import com.power.platform.cgb.dao.ZtmgUserAuthorizationDao;
import com.power.platform.cgb.entity.CgbUserBankCard;
import com.power.platform.cgb.entity.CicmorganBankCode;
import com.power.platform.cgb.entity.ZtmgUserAuthorization;
import com.power.platform.cgb.service.CicmorganBankCodeService;
import com.power.platform.cgbpay.config.ServerURLConfig;
import com.power.platform.common.config.Global;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.MemCachedUtis;
import com.power.platform.common.utils.bank.BankEnum;
import com.power.platform.credit.dao.userinfo.CreditUserInfoDao;
import com.power.platform.credit.entity.supplierToMiddlemen.CreditSupplierToMiddlemen;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.service.supplierToMiddlemen.CreditSupplierToMiddlemenService;
import com.power.platform.credit.service.userinfo.CreditUserInfoService;
import com.power.platform.lanmao.common.AppUtil;
import com.power.platform.lanmao.config.RedirectUrlConfig;
import com.power.platform.lanmao.dao.CgbBigrechargeWhiteRecordDao;
import com.power.platform.lanmao.dao.CreditUserAuditInfoDao;
import com.power.platform.lanmao.dao.LmTransactionDao;
import com.power.platform.lanmao.entity.CgbBigrechargeWhiteRecord;
import com.power.platform.lanmao.entity.CreditUserAuditInfo;
import com.power.platform.lanmao.entity.LmTransaction;
import com.power.platform.lanmao.rw.pojo.NotifyVo;
import com.power.platform.lanmao.type.AuditStatusEnum;
import com.power.platform.lanmao.type.AuthEnum;
import com.power.platform.lanmao.type.BankCodeEnum;
import com.power.platform.lanmao.type.CheckTypeEnum;
import com.power.platform.lanmao.type.CreditUserOpenAccountEnum;
import com.power.platform.lanmao.type.IdCardTypeEnum;
import com.power.platform.lanmao.type.ServiceNameEnum;
import com.power.platform.lanmao.type.UserRoleEnum;
import com.power.platform.lanmao.type.WhiteStatusEnum;
import com.power.platform.regular.dao.WloanSubjectDao;
import com.power.platform.regular.entity.WloanSubject;
import com.power.platform.regular.service.WloanSubjectService;
import com.power.platform.userinfo.entity.Principal;
import com.power.platform.userinfo.entity.UserBankCard;
import com.power.platform.lanmao.search.pojo.LanMaoWhiteList;
import com.power.platform.lanmao.search.service.LanMaoWhiteListAddDataService;
import com.power.platform.lanmao.search.service.LanMaoWhiteListDelDataService;

@Service("businessBindCardService")
public class BusinessBindCardService {

	private static final Logger log = LoggerFactory.getLogger(BusinessBindCardService.class);
	private static final String PLATFORM_NO = Global.getConfigLanMao("platformNo");
	
	@Resource
	private CreditUserInfoDao creditUserInfoDao;
	@Resource
	private CgbUserBankCardDao cgbUserBankCardDao;
	@Resource
	private WloanSubjectDao wloanSubjectDao;
	@Resource
	private WloanSubjectService wloanSubjectService;
	@Autowired
	private LmTransactionDao lmTransactionDao;
	@Autowired
	private CicmorganBankCodeService cicmorganBankCodeService;
	@Autowired
	private CreditSupplierToMiddlemenService creditSupplierToMiddlemenService;
	@Autowired
	private CreditUserInfoService creditUserInfoService;
	@Autowired
	private ZtmgUserAuthorizationDao ztmgUserAuthorizationDao;
	@Autowired
	private CreditUserAuditInfoDao creditUserAuditInfoDao;
	@Autowired
	private LanMaoWhiteListAddDataService lanMaoWhiteListAddDataService;
	@Autowired
	private LanMaoWhiteListDelDataService lanMaoWhiteListDelDataService;
	@Autowired
	private LanMaoWhiteListAddDataService whiteListAddDataService;

	@Autowired
	private CgbBigrechargeWhiteRecordDao cgbBigrechargeWhiteRecordDao;
	/**
	 * 
	 * methods: enterpriseRegisterNotify <br>
	 * description: 企业绑卡注册，异步通知处理 <br>
	 * author: Roy <br>
	 * date: 2019年9月27日 下午5:18:52
	 * 
	 * @param input
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public boolean enterpriseRegisterNotify(NotifyVo input) {

		boolean flag = false;

		try {
			JSONObject respDataJsonObject = JSONObject.parseObject(input.getRespData());
			String sign = input.getSign(); // 签名
			String platformUserNo = ""; // 借款人平台用户编号
			String requestNo = ""; // 请求流水号
			CreditUserInfo entity = null; // 借款人用户信息
			CreditUserAuditInfo creditUserAuditInfo = null; // 审核信息
			if ("SUCCESS".equals(respDataJsonObject.getString("status"))) {
				platformUserNo = respDataJsonObject.getString("platformUserNo");
				requestNo = respDataJsonObject.getString("requestNo");
				log.info("platformUserNo:{}", platformUserNo);
				entity = creditUserInfoService.get(platformUserNo);
				creditUserAuditInfo = creditUserAuditInfoDao.get(platformUserNo);
				if (null != entity) {
					if (AuditStatusEnum.PASSED.getValue().equals(respDataJsonObject.getString("auditStatus"))) {
						entity.setOpenAccountState(CreditUserOpenAccountEnum.OPEN_ACCOUNT_STATE_1.getValue());
						/**
						 * 银行卡，已认证逻辑处理
						 */
						CgbUserBankCard cgbUserBankCard = new CgbUserBankCard();
						cgbUserBankCard.setUserId(platformUserNo);
						CgbUserBankCard cubc = cgbUserBankCardDao.getUserBankCardByCreditUserIdAndState(cgbUserBankCard);
						if (null != cubc) {
							String bankAccountNo = null;
							CgbBigrechargeWhiteRecord whiteRecord = new CgbBigrechargeWhiteRecord();
							whiteRecord.setRequestNo(requestNo);
							List<CgbBigrechargeWhiteRecord> whiteRecordList = cgbBigrechargeWhiteRecordDao.findList(whiteRecord);
							if(whiteRecordList!=null && whiteRecordList.size() != 0) {
								CgbBigrechargeWhiteRecord white = whiteRecordList.get(0);
								bankAccountNo = white.getBankNo();
							}
							if(bankAccountNo!=null&&!"".equals(bankAccountNo)) {
								cubc.setBankAccountNo(bankAccountNo);
							}
							cubc.setState(UserBankCard.CERTIFY_YES);
							cubc.setUpdateDate(new Date());
							int updateCubcFlag = cgbUserBankCardDao.update(cubc);
							log.info("银行卡已认证:{}", updateCubcFlag == 1 ? "成功" : "失败");
						}
						/**
						 * @author fuwei
						 *         添加白名单
						 */
						LanMaoWhiteList whiteList = new LanMaoWhiteList();
						whiteList.setRequestNo(respDataJsonObject.getString("requestNo"));
						whiteList.setPlatformUserNo(platformUserNo);
						whiteList.setBankcardNo(respDataJsonObject.getString("bankcardNo")); // 银行对公账户
						whiteList.setUserRole(UserRoleEnum.BORROWERS.getValue());
						Map<String, Object> result = whiteListAddDataService.whiteListAdd(whiteList,requestNo);
						if (result.get("code").equals("调用成功") && result.get("status").equals("处理成功")) {
							log.info("添加白名单成功");
						} else {
							log.info("添加白名单失败：" + result.get("errorMessage"));
						}
						
						/**
						 * 用户授权
						 */
						ZtmgUserAuthorization creUserAuthorization = new ZtmgUserAuthorization();
						creUserAuthorization.setUserId(platformUserNo);
						List<ZtmgUserAuthorization> authorizationList = ztmgUserAuthorizationDao.findList(creUserAuthorization);
						if (authorizationList != null && authorizationList.size() > 0) {
							ZtmgUserAuthorization ztmgUserAuthorization = authorizationList.get(0);
							ztmgUserAuthorization.setGrantList(AuthEnum.REPAYMENT.getValue());
							ztmgUserAuthorization.setStatus("S");
							ztmgUserAuthorization.setSignature(sign);
							ztmgUserAuthorization.setGrantAmountList(respDataJsonObject.getString("amount")); // 授权金额
							ztmgUserAuthorization.setGrantTimeList(respDataJsonObject.getString("failTime")); // 授权截至期限
							ztmgUserAuthorization.setUpdateDate(new Date());
							ztmgUserAuthorization.setRemarks("变更授权信息");
							int updateCreUserAuthorization = ztmgUserAuthorizationDao.update(ztmgUserAuthorization);
							log.info("变更授权信息:{}", updateCreUserAuthorization == 1 ? "成功" : "失败");
						} else {
							ZtmgUserAuthorization ztmgUserAuthorization = new ZtmgUserAuthorization();
							ztmgUserAuthorization.setId(IdGen.uuid());
							ztmgUserAuthorization.setUserId(platformUserNo);
							ztmgUserAuthorization.setMerchantId(Global.getConfigLanMao("platformNo"));
							ztmgUserAuthorization.setStatus("S");
							ztmgUserAuthorization.setSignature(sign);
							ztmgUserAuthorization.setGrantList(AuthEnum.REPAYMENT.getValue());
							ztmgUserAuthorization.setGrantAmountList(respDataJsonObject.getString("amount"));
							ztmgUserAuthorization.setGrantTimeList(respDataJsonObject.getString("failTime"));
							ztmgUserAuthorization.setCreateDate(new Date());
							ztmgUserAuthorization.setUpdateDate(new Date());
							ztmgUserAuthorization.setRemarks("新增授权信息");
							int insertCreUserAuthorization = ztmgUserAuthorizationDao.insert(ztmgUserAuthorization);
							log.info("新增授权信息:{}", insertCreUserAuthorization == 1 ? "成功" : "失败");
						}
					} else if (AuditStatusEnum.AUDIT.getValue().equals(respDataJsonObject.getString("auditStatus"))) {
						entity.setOpenAccountState(CreditUserOpenAccountEnum.OPEN_ACCOUNT_STATE_2.getValue());
						/**
						 * 用户授权
						 */
						ZtmgUserAuthorization creUserAuthorization = new ZtmgUserAuthorization();
						creUserAuthorization.setUserId(platformUserNo);
						List<ZtmgUserAuthorization> authorizationList = ztmgUserAuthorizationDao.findList(creUserAuthorization);
						if (authorizationList != null && authorizationList.size() > 0) {
							ZtmgUserAuthorization ztmgUserAuthorization = authorizationList.get(0);
							ztmgUserAuthorization.setGrantList(AuthEnum.REPAYMENT.getValue());
							ztmgUserAuthorization.setStatus("AS");
							ztmgUserAuthorization.setSignature(sign);
							ztmgUserAuthorization.setGrantAmountList(respDataJsonObject.getString("amount")); // 授权金额
							ztmgUserAuthorization.setGrantTimeList(respDataJsonObject.getString("failTime")); // 授权截至期限
							ztmgUserAuthorization.setUpdateDate(new Date());
							ztmgUserAuthorization.setRemarks("变更授权信息");
							int updateCreUserAuthorization = ztmgUserAuthorizationDao.update(ztmgUserAuthorization);
							log.info("变更授权信息:{}", updateCreUserAuthorization == 1 ? "成功" : "失败");
						} else {
							ZtmgUserAuthorization ztmgUserAuthorization = new ZtmgUserAuthorization();
							ztmgUserAuthorization.setId(IdGen.uuid());
							ztmgUserAuthorization.setUserId(platformUserNo);
							ztmgUserAuthorization.setMerchantId(Global.getConfigLanMao("platformNo"));
							ztmgUserAuthorization.setStatus("AS");
							ztmgUserAuthorization.setSignature(sign);
							ztmgUserAuthorization.setGrantList(AuthEnum.REPAYMENT.getValue());
							ztmgUserAuthorization.setGrantAmountList(respDataJsonObject.getString("amount"));
							ztmgUserAuthorization.setGrantTimeList(respDataJsonObject.getString("failTime"));
							ztmgUserAuthorization.setCreateDate(new Date());
							ztmgUserAuthorization.setUpdateDate(new Date());
							ztmgUserAuthorization.setRemarks("新增授权信息");
							int insertCreUserAuthorization = ztmgUserAuthorizationDao.insert(ztmgUserAuthorization);
							log.info("新增授权信息:{}", insertCreUserAuthorization == 1 ? "成功" : "失败");
						}
					} else if (AuditStatusEnum.BACK.getValue().equals(respDataJsonObject.getString("auditStatus"))) {
						entity.setOpenAccountState(CreditUserOpenAccountEnum.OPEN_ACCOUNT_STATE_3.getValue());
						/**
						 * 用户授权
						 */
						ZtmgUserAuthorization creUserAuthorization = new ZtmgUserAuthorization();
						creUserAuthorization.setUserId(platformUserNo);
						List<ZtmgUserAuthorization> authorizationList = ztmgUserAuthorizationDao.findList(creUserAuthorization);
						if (authorizationList != null && authorizationList.size() > 0) {
							ZtmgUserAuthorization ztmgUserAuthorization = authorizationList.get(0);
							ztmgUserAuthorization.setGrantList(AuthEnum.REPAYMENT.getValue());
							ztmgUserAuthorization.setStatus("F");
							ztmgUserAuthorization.setSignature(sign);
							ztmgUserAuthorization.setGrantAmountList(respDataJsonObject.getString("amount")); // 授权金额
							ztmgUserAuthorization.setGrantTimeList(respDataJsonObject.getString("failTime")); // 授权截至期限
							ztmgUserAuthorization.setUpdateDate(new Date());
							ztmgUserAuthorization.setRemarks("变更授权信息");
							int updateCreUserAuthorization = ztmgUserAuthorizationDao.update(ztmgUserAuthorization);
							log.info("变更授权信息:{}", updateCreUserAuthorization == 1 ? "成功" : "失败");
						} else {
							ZtmgUserAuthorization ztmgUserAuthorization = new ZtmgUserAuthorization();
							ztmgUserAuthorization.setId(IdGen.uuid());
							ztmgUserAuthorization.setUserId(platformUserNo);
							ztmgUserAuthorization.setMerchantId(Global.getConfigLanMao("platformNo"));
							ztmgUserAuthorization.setStatus("F");
							ztmgUserAuthorization.setSignature(sign);
							ztmgUserAuthorization.setGrantList(AuthEnum.REPAYMENT.getValue());
							ztmgUserAuthorization.setGrantAmountList(respDataJsonObject.getString("amount"));
							ztmgUserAuthorization.setGrantTimeList(respDataJsonObject.getString("failTime"));
							ztmgUserAuthorization.setCreateDate(new Date());
							ztmgUserAuthorization.setUpdateDate(new Date());
							ztmgUserAuthorization.setRemarks("新增授权信息");
							int insertCreUserAuthorization = ztmgUserAuthorizationDao.insert(ztmgUserAuthorization);
							log.info("新增授权信息:{}", insertCreUserAuthorization == 1 ? "成功" : "失败");
						}
					} else if (AuditStatusEnum.REFUSED.getValue().equals(respDataJsonObject.getString("auditStatus"))) {
						entity.setOpenAccountState(CreditUserOpenAccountEnum.OPEN_ACCOUNT_STATE_4.getValue());
						/**
						 * 用户授权
						 */
						ZtmgUserAuthorization creUserAuthorization = new ZtmgUserAuthorization();
						creUserAuthorization.setUserId(platformUserNo);
						List<ZtmgUserAuthorization> authorizationList = ztmgUserAuthorizationDao.findList(creUserAuthorization);
						if (authorizationList != null && authorizationList.size() > 0) {
							ZtmgUserAuthorization ztmgUserAuthorization = authorizationList.get(0);
							ztmgUserAuthorization.setGrantList(AuthEnum.REPAYMENT.getValue());
							ztmgUserAuthorization.setStatus("F");
							ztmgUserAuthorization.setSignature(sign);
							ztmgUserAuthorization.setGrantAmountList(respDataJsonObject.getString("amount")); // 授权金额
							ztmgUserAuthorization.setGrantTimeList(respDataJsonObject.getString("failTime")); // 授权截至期限
							ztmgUserAuthorization.setUpdateDate(new Date());
							ztmgUserAuthorization.setRemarks("变更授权信息");
							int updateCreUserAuthorization = ztmgUserAuthorizationDao.update(ztmgUserAuthorization);
							log.info("变更授权信息:{}", updateCreUserAuthorization == 1 ? "成功" : "失败");
						} else {
							ZtmgUserAuthorization ztmgUserAuthorization = new ZtmgUserAuthorization();
							ztmgUserAuthorization.setId(IdGen.uuid());
							ztmgUserAuthorization.setUserId(platformUserNo);
							ztmgUserAuthorization.setMerchantId(Global.getConfigLanMao("platformNo"));
							ztmgUserAuthorization.setStatus("F");
							ztmgUserAuthorization.setSignature(sign);
							ztmgUserAuthorization.setGrantList(AuthEnum.REPAYMENT.getValue());
							ztmgUserAuthorization.setGrantAmountList(respDataJsonObject.getString("amount"));
							ztmgUserAuthorization.setGrantTimeList(respDataJsonObject.getString("failTime"));
							ztmgUserAuthorization.setCreateDate(new Date());
							ztmgUserAuthorization.setUpdateDate(new Date());
							ztmgUserAuthorization.setRemarks("新增授权信息");
							int insertCreUserAuthorization = ztmgUserAuthorizationDao.insert(ztmgUserAuthorization);
							log.info("新增授权信息:{}", insertCreUserAuthorization == 1 ? "成功" : "失败");
						}
					}
					entity.setUpdateDate(new Date());
					int updateCreditUserInfoFlag = creditUserInfoDao.update(entity);
					log.info("借款人用户开户状态变更:{}", updateCreditUserInfoFlag == 1 ? "成功" : "失败");

					/**
					 * 开户审核信息更新
					 */
					if (null != creditUserAuditInfo) {
						creditUserAuditInfo.setAuditStatus(respDataJsonObject.getString("auditStatus"));
						creditUserAuditInfo.setRemark(respDataJsonObject.getString("remark"));
						creditUserAuditInfo.setCode(respDataJsonObject.getString("code"));
						creditUserAuditInfo.setStatus(respDataJsonObject.getString("status"));
						creditUserAuditInfo.setErrorCode(respDataJsonObject.getString("errorCode"));
						creditUserAuditInfo.setErrorMessage(respDataJsonObject.getString("errorMessage"));
						creditUserAuditInfo.setUpdateDate(new Date());
						int updateCreditUserAuditInfoFlag = creditUserAuditInfoDao.update(creditUserAuditInfo);
						log.info("开户审核信息更新:{}", updateCreditUserAuditInfoFlag == 1 ? "成功" : "失败");
					} else {
						/**
						 * 开户审核信息留存
						 */
						creditUserAuditInfo = new CreditUserAuditInfo();
						creditUserAuditInfo.setId(platformUserNo);
						creditUserAuditInfo.setPlatformUserNo(platformUserNo);
						creditUserAuditInfo.setAuditStatus(respDataJsonObject.getString("auditStatus"));
						creditUserAuditInfo.setUserRole(respDataJsonObject.getString("userRole"));
						creditUserAuditInfo.setBankcardNo(respDataJsonObject.getString("bankcardNo"));
						creditUserAuditInfo.setBankcode(respDataJsonObject.getString("bankcode"));
						creditUserAuditInfo.setRemark(respDataJsonObject.getString("remark"));
						creditUserAuditInfo.setCode(respDataJsonObject.getString("code"));
						creditUserAuditInfo.setStatus(respDataJsonObject.getString("status"));
						creditUserAuditInfo.setErrorCode(respDataJsonObject.getString("errorCode"));
						creditUserAuditInfo.setErrorMessage(respDataJsonObject.getString("errorMessage"));
						creditUserAuditInfo.setCreateDate(new Date());
						creditUserAuditInfo.setUpdateDate(new Date());
						int insertCreditUserAuditFlag = creditUserAuditInfoDao.insert(creditUserAuditInfo);
						log.info("开户审核记录新增:{}", insertCreditUserAuditFlag == 1 ? "成功" : "失败");
					}
					flag = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return flag;
		}
		return flag;
	}

	/**
	 * 
	 * methods: cardRegister <br>
	 * description: 企业绑卡注册 <br>
	 * author: Roy <br>
	 * date: 2019年9月27日 下午5:10:39
	 * 
	 * @param creditUserInfo
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> cardRegister(CreditUserInfo creditUserInfo) {

		Map<String, String> result = new LinkedHashMap<String, String>();
		try {
			String requestNo = IdGen.uuid(); // Y 请求流水号
			// 定义reqData参数集合
			Map<String, Object> reqData = new HashMap<String, Object>();
			reqData.put("requestNo", requestNo);
			reqData.put("platformUserNo", creditUserInfo.getId()); // Y 平台用户编号
			reqData.put("enterpriseName", creditUserInfo.getEnterpriseFullName()); // Y 企业名称
			reqData.put("bankLicense", creditUserInfo.getWloanSubject().getBankPermitCertNo());// Y 开户银行许可证号
			if ("BLC".equals(creditUserInfo.getWloanSubject().getBusinessLicenseType())) { // 证照类型为营业执照
				reqData.put("orgNo", creditUserInfo.getWloanSubject().getOrganNo());// N 组织机构代码
				reqData.put("businessLicense", creditUserInfo.getWloanSubject().getBusinessNo());// N 营业执照编号
				reqData.put("taxNo", creditUserInfo.getWloanSubject().getTaxCode());// N 税务登记号
			} else {
				reqData.put("unifiedCode", creditUserInfo.getWloanSubject().getBusinessNo()); // N 统一社会信用代码（可替代组织机构代码、营业执照编号、税务登记号此三证），统一社会信用代码和三证信息两者必须传入其中 一个。
			}
			reqData.put("creditCode", null);// N 机构信用代码
			reqData.put("legal", creditUserInfo.getWloanSubject().getLoanUser());// Y 法人姓名
			// 法人证件类型
			if ("IDC".equals(creditUserInfo.getWloanSubject().getCorporationCertType())) {
				reqData.put("idCardType", IdCardTypeEnum.PRC_ID.getValue()); // Y 见【证件类型】
			} else if ("PASS_PORT".equals(creditUserInfo.getWloanSubject().getCorporationCertType())) {
				reqData.put("idCardType", IdCardTypeEnum.PASSPORT.getValue()); // Y 见【证件类型】
			} else if ("GAT".equals(creditUserInfo.getWloanSubject().getCorporationCertType())) {
				reqData.put("idCardType", IdCardTypeEnum.COMPATRIOTS_CARD.getValue()); // Y 见【证件类型】
			} else if ("PERMANENT_RESIDENCE".equals(creditUserInfo.getWloanSubject().getCorporationCertType())) {
				reqData.put("idCardType", IdCardTypeEnum.PERMANENT_RESIDENCE.getValue()); // Y 见【证件类型】
			}
			reqData.put("legalIdCardNo", creditUserInfo.getWloanSubject().getCorporationCertNo());// Y 法人证件号
			reqData.put("contact", creditUserInfo.getWloanSubject().getAgentPersonName());// Y 企业联系人
			reqData.put("contactPhone", creditUserInfo.getWloanSubject().getAgentPersonPhone());// Y 联系人手机号
			// 用户角色
			if (CreditUserInfo.CREDIT_USER_TYPE_15.equals(creditUserInfo.getCreditUserType())) {
				reqData.put("userRole", UserRoleEnum.BORROWERS.getValue());// Y 见【用户角色】
			} else if (CreditUserInfo.CREDIT_USER_TYPE_02.equals(creditUserInfo.getCreditUserType())) {
				reqData.put("userRole", UserRoleEnum.BORROWERS.getValue());// Y 见【用户角色】
			} else if (CreditUserInfo.CREDIT_USER_TYPE_11.equals(creditUserInfo.getCreditUserType())) {
				reqData.put("userRole", UserRoleEnum.COLLABORATOR.getValue());// Y 见【用户角色】
			}
			reqData.put("bankcardNo", creditUserInfo.getWloanSubject().getLoanBankNo()); // Y 企业对公账户
			/**
			 * 查询银行编码对照表中的银行卡信息
			 */
			CicmorganBankCode cicmorganBankCode = cicmorganBankCodeService.get(creditUserInfo.getWloanSubject().getLoanBankCode()); // 根据银行编码对照表的主键ID，查询银行编码
			if (cicmorganBankCode != null) {
				reqData.put("bankcode", cicmorganBankCode.getBankCode()); // Y 银行编码
			}
			reqData.put("redirectUrl", RedirectUrlConfig.ENTERPRISE_REGISTER_REDIRECT_URL); // Y 页面回跳URL
			reqData.put("authList", AuthEnum.REPAYMENT.getValue()); // N 见【用户授权列表】；此处可传多个值，传多个值用“,”英文半角逗号分隔
			reqData.put("amount", "1000000.00"); // 授权还款金额1000000.00万每笔
			reqData.put("failTime", DateUtils.formatDate(DateUtils.yearAddNum(new Date(), 3), "yyyyMMdd")); // 授权截至时间，默认三年
			// 所有的reqData的JSON内都须包含time stamp时间参数，并传入当前时间如果此时间和存管系统时间相差过大（10 分钟），则拒绝执行操作。
			reqData.put("timestamp", DateUtils.formatDate(new Date(), "yyyyMMddHHmmss"));

			Map<String, String> requestParam = AppUtil.lmGeneratePostParam(ServiceNameEnum.ENTERPRISE_REGISTER.getValue(), reqData);
			log.debug("request:{}", JSON.toJSONString(requestParam));

			/**
			 * 借款企业帐号信息变更
			 */
			if (creditUserInfo != null) {
				CreditUserInfo newCreditUserInfo = creditUserInfoDao.get(creditUserInfo.getId());
				if (newCreditUserInfo != null) {
					if (CreditUserInfo.CREDIT_USER_TYPE_15.equals(creditUserInfo.getCreditUserType())) { // 抵押业务(借款户)
						// 账户类型
						newCreditUserInfo.setCreditUserType(CreditUserInfo.CREDIT_USER_TYPE_15); // 抵押业务(借款户)
						newCreditUserInfo.setAccountType(CreditUserInfoService.ACCOUNT_TYPE1); // 项目产品类型安心投
						newCreditUserInfo.setOwnedCompany("房产抵押"); // 所属企业备注房产抵押
					} else if (CreditUserInfo.CREDIT_USER_TYPE_02.equals(creditUserInfo.getCreditUserType())) { // 供应商(借款户)
						// 账户类型
						newCreditUserInfo.setCreditUserType(CreditUserInfo.CREDIT_USER_TYPE_02); // 借款户
						newCreditUserInfo.setAccountType(CreditUserInfoService.ACCOUNT_TYPE2); // 项目产品类型(供应链)
						CreditSupplierToMiddlemen supplierToMiddlemen = new CreditSupplierToMiddlemen();
						supplierToMiddlemen.setSupplierId(creditUserInfo.getId());
						List<CreditSupplierToMiddlemen> supplierToMiddlemens = creditSupplierToMiddlemenService.findList(supplierToMiddlemen);
						if (supplierToMiddlemens != null) {
							if (supplierToMiddlemens.size() > 0) {
								if (supplierToMiddlemens.get(0) != null) {
									String middlementId = supplierToMiddlemens.get(0).getMiddlemenId();
									CreditUserInfo middlementCreditUserInfo = creditUserInfoService.get(middlementId); // 代偿户
									if (middlementCreditUserInfo != null) {
										newCreditUserInfo.setOwnedCompany(middlementCreditUserInfo.getEnterpriseFullName()); // 供应商(借款户)所属企业
									} else { // 所属企业是他自己
										newCreditUserInfo.setOwnedCompany(creditUserInfo.getEnterpriseFullName());
									}
								}
							}
						}
					} else if (CreditUserInfo.CREDIT_USER_TYPE_11.equals(creditUserInfo.getCreditUserType())) { // 代偿户
						newCreditUserInfo.setOwnedCompany(creditUserInfo.getEnterpriseFullName()); // 核心企业(代偿户)所属企业是它自己
					}
					newCreditUserInfo.setEnterpriseFullName(creditUserInfo.getEnterpriseFullName()); // 企业名称
					newCreditUserInfo.setUpdateDate(new Date()); // 更新时间
					if (creditUserInfo.getWloanSubject() != null) {
						newCreditUserInfo.setName(creditUserInfo.getWloanSubject().getAgentPersonName()); // 联系人姓名
						newCreditUserInfo.setCertificateNo(creditUserInfo.getWloanSubject().getAgentPersonCertNo()); // 联系人身份证号码
					}
					int newCreditUserInfoUpdateFlag = creditUserInfoDao.update(newCreditUserInfo);
					log.info("企业帐号信息更新:{}", newCreditUserInfoUpdateFlag == 1 ? "成功" : "失败");
				}
				/**
				 * 借款企业银行卡信息变更/新增
				 */
				CgbUserBankCard cgbUserBankCard = new CgbUserBankCard();
				cgbUserBankCard.setUserId(creditUserInfo.getId());
				List<CgbUserBankCard> cgbUserBankCards = cgbUserBankCardDao.findCreditList(cgbUserBankCard);
				if (cgbUserBankCards != null) {
					if (cgbUserBankCards.size() > 0) {
						CgbUserBankCard creditUserBank = cgbUserBankCards.get(0);
						creditUserBank.setAccountId(newCreditUserInfo.getAccountId()); // 账户ID
						creditUserBank.setUserId(creditUserInfo.getId()); // 帐号ID
						if (creditUserInfo.getWloanSubject() != null) {
							creditUserBank.setBankAccountNo(creditUserInfo.getWloanSubject().getLoanBankNo()); // 银行账户
							creditUserBank.setBankName(creditUserInfo.getWloanSubject().getLoanBankName());
						}
						creditUserBank.setIsDefault(UserBankCard.DEFAULT_YES); // 默认卡
						creditUserBank.setState(CgbUserBankCard.CERTIFY_NO); // 0：未认证，1：已认证
						if (cicmorganBankCode != null) {
							creditUserBank.setBankNo(cicmorganBankCode.getBankCode()); // 银行编码
						}
						creditUserBank.setUpdateDate(new Date()); // 更新时间
						int creditUserBankUpdateFlag = cgbUserBankCardDao.update(creditUserBank);
						log.info("企业银行卡信息更新:{}", creditUserBankUpdateFlag == 1 ? "成功" : "失败");
					} else if (cgbUserBankCards.size() == 0) {
						CgbUserBankCard newCgbUserBankCard = new CgbUserBankCard();
						newCgbUserBankCard.setId(IdGen.uuid()); // 主键
						newCgbUserBankCard.setUserId(creditUserInfo.getId()); // 帐号ID
						newCgbUserBankCard.setAccountId(newCreditUserInfo.getAccountId()); // 账户ID
						if (creditUserInfo.getWloanSubject() != null) {
							newCgbUserBankCard.setBankAccountNo(creditUserInfo.getWloanSubject().getLoanBankNo()); // 银行账户
							newCgbUserBankCard.setBankName(creditUserInfo.getWloanSubject().getLoanBankName()); // 银行名称
						}
						if (cicmorganBankCode != null) {
							newCgbUserBankCard.setBankNo(cicmorganBankCode.getBankCode()); // 银行编码
						}
						newCgbUserBankCard.setBeginBindDate(new Date()); // 开始绑卡时间
						newCgbUserBankCard.setIsDefault(UserBankCard.DEFAULT_YES); // 默认银行卡
						newCgbUserBankCard.setState(CgbUserBankCard.CERTIFY_NO); // 0：未认证，1：已认证
						newCgbUserBankCard.setBindDate(new Date()); // 绑卡时间
						newCgbUserBankCard.setCreateDate(new Date()); // 创建时间
						newCgbUserBankCard.setUpdateDate(new Date()); // 更新时间
						int newCgbUserBankCardInsertFlag = cgbUserBankCardDao.insert(newCgbUserBankCard);
						log.info("企业银行卡信息新增:{}", newCgbUserBankCardInsertFlag == 1 ? "成功" : "失败");
					}
				}
				/**
				 * 借款企业融资主体变更/新增
				 */
				WloanSubject entity = new WloanSubject();
				entity.setLoanApplyId(creditUserInfo.getId());
				List<WloanSubject> subjects = wloanSubjectService.findList(entity);
				if (subjects != null) {
					if (subjects.size() > 0) {
						WloanSubject subject = subjects.get(0);
						if (subject != null) { // 融资主体已存在
							subject.setLoanApplyId(creditUserInfo.getId()); // 借款人ID
							subject.setType(WloanSubjectService.WLOAN_SUBJECT_TYPE_2); // 融资类型企业
							subject.setCompanyName(creditUserInfo.getEnterpriseFullName()); // 企业名称
							if (creditUserInfo.getWloanSubject() != null) {
								if (newCreditUserInfo != null) {
									subject.setLoanPhone(newCreditUserInfo.getPhone()); // 借款企业帐号.
								}
								subject.setLoanUser(creditUserInfo.getWloanSubject().getLoanUser()); // 法人姓名
								subject.setLoanIdCard(creditUserInfo.getWloanSubject().getCorporationCertNo()); // 借款人法人身份证号码
								subject.setRegistAddress(creditUserInfo.getWloanSubject().getRegistAddress()); // 注册地
								subject.setCorporationCertType(creditUserInfo.getWloanSubject().getCorporationCertType()); // 法人证件类型
								subject.setCorporationCertNo(creditUserInfo.getWloanSubject().getCorporationCertNo()); // 法人证件号码
								subject.setBusinessLicenseType(creditUserInfo.getWloanSubject().getBusinessLicenseType()); // 证照类型
								subject.setBusinessNo(creditUserInfo.getWloanSubject().getBusinessNo()); // 证照号码
								subject.setTaxCode(creditUserInfo.getWloanSubject().getTaxCode()); // 税务登记证
								subject.setOrganNo(creditUserInfo.getWloanSubject().getOrganNo()); // 组织机构代码
								subject.setLoanBankName(creditUserInfo.getWloanSubject().getLoanBankName()); // 银行名称
								if (cicmorganBankCode != null) {
									subject.setLoanBankCode(cicmorganBankCode.getBankCode()); // 银行编码
								}
								subject.setLoanBankNo(creditUserInfo.getWloanSubject().getLoanBankNo()); // 银行账号
								subject.setLoanBankProvince(creditUserInfo.getWloanSubject().getLoanBankProvince()); // 开户省
								subject.setLoanBankCity(creditUserInfo.getWloanSubject().getLoanBankCity()); // 开户市
								subject.setLoanBankCounty(creditUserInfo.getWloanSubject().getLoanBankCounty()); // 开户县区
								subject.setLoanIssuerName(creditUserInfo.getWloanSubject().getLoanIssuerName()); // 支行名称
								subject.setBankPermitCertNo(creditUserInfo.getWloanSubject().getBankPermitCertNo()); // 核准号(银行开户许可证)
								subject.setLoanIssuer(creditUserInfo.getWloanSubject().getLoanIssuer()); // 支行-联行号
								subject.setAgentPersonName(creditUserInfo.getWloanSubject().getAgentPersonName()); // 联系人姓名
								subject.setAgentPersonPhone(creditUserInfo.getWloanSubject().getAgentPersonPhone()); // 联系人手机号码
								subject.setAgentPersonCertType(creditUserInfo.getWloanSubject().getAgentPersonCertType()); // 联系人证件类型
								subject.setAgentPersonCertNo(creditUserInfo.getWloanSubject().getAgentPersonCertNo()); // 联系人证号码
								subject.setEmail(creditUserInfo.getWloanSubject().getEmail()); // 联系人邮箱
								subject.setUpdateDate(new Date()); // 更新时间
								int subjectUpdateFlag = wloanSubjectDao.update(subject);
								log.info("借款企业融资主体更新:{}", subjectUpdateFlag == 1 ? "成功" : "失败");
							}
						}
					} else if (subjects.size() == 0) { // 新增借款融资主体
						WloanSubject newSubject = new WloanSubject();
						newSubject.setId(IdGen.uuid()); // 主键
						newSubject.setType(WloanSubjectService.WLOAN_SUBJECT_TYPE_2); // 融资主体类型企业
						newSubject.setLoanApplyId(creditUserInfo.getId()); // 借款人ID
						newSubject.setCompanyName(creditUserInfo.getEnterpriseFullName()); // 企业名称
						if (newCreditUserInfo != null) {
							newSubject.setLoanPhone(newCreditUserInfo.getPhone()); // 借款人/操作人手机号码
							newSubject.setLoanBankPhone(newCreditUserInfo.getPhone()); // 银行预留手机
						}
						newSubject.setLoanIdCard(creditUserInfo.getWloanSubject().getCorporationCertNo()); // 借款人法人身份证号码
						newSubject.setLoanUser(creditUserInfo.getWloanSubject().getLoanUser()); // 法人姓名
						newSubject.setRegistAddress(creditUserInfo.getWloanSubject().getRegistAddress()); // 注册地
						newSubject.setCorporationCertType(creditUserInfo.getWloanSubject().getCorporationCertType()); // 法人证件类型
						newSubject.setCorporationCertNo(creditUserInfo.getWloanSubject().getCorporationCertNo()); // 法人证件号码
						newSubject.setBusinessLicenseType(creditUserInfo.getWloanSubject().getBusinessLicenseType()); // 证照类型
						newSubject.setBusinessNo(creditUserInfo.getWloanSubject().getBusinessNo()); // 证照号码
						newSubject.setTaxCode(creditUserInfo.getWloanSubject().getTaxCode()); // 税务登记证
						newSubject.setOrganNo(creditUserInfo.getWloanSubject().getOrganNo()); // 组织机构代码
						newSubject.setLoanBankName(creditUserInfo.getWloanSubject().getLoanBankName()); // 银行名称
						if (cicmorganBankCode != null) {
							newSubject.setLoanBankCode(cicmorganBankCode.getBankCode()); // 银行编码
						}
						newSubject.setLoanBankNo(creditUserInfo.getWloanSubject().getLoanBankNo()); // 银行账号
						newSubject.setLoanBankProvince(creditUserInfo.getWloanSubject().getLoanBankProvince()); // 开户省
						newSubject.setLoanBankCity(creditUserInfo.getWloanSubject().getLoanBankCity()); // 开户市
						newSubject.setLoanBankCounty(creditUserInfo.getWloanSubject().getLoanBankCounty()); // 开户县区
						newSubject.setLoanIssuerName(creditUserInfo.getWloanSubject().getLoanIssuerName()); // 支行名称
						newSubject.setBankPermitCertNo(creditUserInfo.getWloanSubject().getBankPermitCertNo()); // 核准号(银行开户许可证)
						newSubject.setLoanIssuer(creditUserInfo.getWloanSubject().getLoanIssuer()); // 支行-联行号
						newSubject.setAgentPersonName(creditUserInfo.getWloanSubject().getAgentPersonName()); // 联系人姓名
						newSubject.setAgentPersonPhone(creditUserInfo.getWloanSubject().getAgentPersonPhone()); // 联系人手机号码
						newSubject.setAgentPersonCertType(creditUserInfo.getWloanSubject().getAgentPersonCertType()); // 联系人证件类型
						newSubject.setAgentPersonCertNo(creditUserInfo.getWloanSubject().getAgentPersonCertNo()); // 联系人证号码
						newSubject.setEmail(creditUserInfo.getWloanSubject().getEmail()); // 联系人邮箱
						newSubject.setIsEntrustedPay(WloanSubjectService.IS_ENTRUSTED_PAY_0); // 受托支付：否
						newSubject.setCreateDate(new Date()); // 创建时间
						newSubject.setUpdateDate(new Date()); // 更新时间
						int newSubjectInsertFlag = wloanSubjectDao.insert(newSubject);
						log.info("借款企业融资主体新增:{}", newSubjectInsertFlag == 1 ? "成功" : "失败");
					}
				}
			}
			
			CgbBigrechargeWhiteRecord white = new CgbBigrechargeWhiteRecord();
			white.setId(IdGen.uuid());
			white.setPlatformId(PLATFORM_NO);
			white.setRequestNo(requestNo);
			white.setRealName(creditUserInfo.getEnterpriseFullName());
			white.setUserId(creditUserInfo.getId());
			white.setBankCode(BankCodeEnum.getTextByText(null));
			white.setBankNo(creditUserInfo.getWloanSubject().getLoanBankNo());
			white.setStatus(WhiteStatusEnum.GRAY);
			white.setUserRole(UserRoleEnum.BORROWERS.getValue());
			white.setOperationDesc(null);
			white.setCreateDate(new Date());
			white.setUpdateDate(new Date());
			white.setDescription("记录企业绑卡注册添加白名单：灰度");
			int whiteFlag = cgbBigrechargeWhiteRecordDao.insert(white);
			log.info("记录企业绑卡注册添加白名单：灰度", whiteFlag == 1 ? "成功" : "失败");

			// 懒猫交易留存
			LmTransaction lmTransaction = new LmTransaction();
			lmTransaction.setId(IdGen.uuid());
			lmTransaction.setRequestNo(requestNo);
			lmTransaction.setServiceName(ServiceNameEnum.ENTERPRISE_REGISTER.getValue());
			lmTransaction.setPlatformUserNo(creditUserInfo.getId());
			lmTransaction.setCreateDate(new Date());
			lmTransaction.setUpdateDate(new Date());
			int lmTransactionFlag = lmTransactionDao.insert(lmTransaction);
			log.debug("懒猫交易留存，插入:{}", lmTransactionFlag == 1 ? "成功" : "失败");

			return requestParam;
		} catch (Exception e) {
			log.error("Exception:{}", e.getMessage());
			result.put("code", "1");
			result.put("status", "INIT");
			result.put("errorCode", "1");
			result.put("errorMessage", "local exception");
			return result;
		}
	}

	/**
	 * @description:企业绑卡注册
	 * @param enterpriseFullName
	 * @param businessLicenseType
	 * @param businessLicense
	 * @param bankPermitCertNo
	 * @param taxRegCertNo
	 * @param orgCode
	 * @param agentPersonName
	 * @param agentPersonPhone
	 * @param agentPersonCertType
	 * @param agentPersonCertNo
	 * @param corporationName
	 * @param corporationCertType
	 * @param corporationCertNo
	 * @param bankCardNo
	 * @param supplierId
	 *            借款户
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> cardRegister(String token, String bizType, String enterpriseFullName, String businessLicenseType, String businessLicense, String bankPermitCertNo, String taxRegCertNo, String orgCode, String agentPersonName, String agentPersonPhone, String agentPersonCertType, String agentPersonCertNo, String corporationName, String corporationCertType, String corporationCertNo, String bankName, String bankCode, String bankCardNo, String bankCardName, String bankProvince, String bankCity, String issuerName, String issuer, String supplierId) {

		// 定义reqData参数集合
		Map<String, String> reqData = new HashMap<String, String>();
		Map<String, String> result = new HashMap<String, String>();
		try {
			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			String userId = null;
			if (principal != null) {
				userId = principal.getCreditUserInfo().getId();
			}
			CreditUserInfo userInfo = creditUserInfoDao.get(userId);// 借款用户
			// 插入企业信息
			log.info("企业信息更新开始");
			WloanSubject entity = new WloanSubject();
			entity.setLoanApplyId(userId);
			List<WloanSubject> wloanSubjectList = wloanSubjectService.findList(entity);
			if (wloanSubjectList != null && wloanSubjectList.size() > 0) {
				for (WloanSubject wloanSubject : wloanSubjectList) {

					wloanSubject.setId(wloanSubject.getId());
					wloanSubject.setType("2");
					wloanSubject.setLoanApplyId(userInfo.getId());
					wloanSubject.setCompanyName(enterpriseFullName);
					wloanSubject.setLoanUser(corporationName);
					wloanSubject.setLoanBankNo(bankCardNo);
					wloanSubject.setLoanBankName(bankName);
					wloanSubject.setLoanPhone(userInfo.getPhone());
					wloanSubject.setLoanBankPhone(userInfo.getPhone());
					wloanSubject.setLoanBankCode(bankCode);
					wloanSubject.setLoanIdCard(corporationCertNo);
					wloanSubject.setBusinessLicenseType(businessLicenseType);
					wloanSubject.setBusinessNo(businessLicense);
					wloanSubject.setBankPermitCertNo(bankPermitCertNo);
					wloanSubject.setOrganNo(orgCode);
					wloanSubject.setTaxCode(taxRegCertNo);
					wloanSubject.setAgentPersonPhone(agentPersonPhone);
					wloanSubject.setAgentPersonName(agentPersonName);
					wloanSubject.setAgentPersonCertType(agentPersonCertType);
					wloanSubject.setAgentPersonCertNo(agentPersonCertNo);
					wloanSubject.setCorporationCertType(corporationCertType);
					wloanSubject.setCorporationCertNo(corporationCertNo);
					wloanSubject.setIsEntrustedPay("0");
					wloanSubject.setCreateDate(new Date());
					wloanSubject.setUpdateDate(new Date());
					wloanSubject.setLoanIssuer(issuer);

					int i = wloanSubjectDao.update(wloanSubject);
					if (i > 0) {
						log.info("企业开户信息更新成功");
					}
				}
			} else {
				WloanSubject wloanSubject1 = new WloanSubject();
				wloanSubject1.setId(IdGen.uuid());
				wloanSubject1.setType("2");
				wloanSubject1.setLoanApplyId(userInfo.getId());
				wloanSubject1.setCompanyName(enterpriseFullName);
				wloanSubject1.setLoanUser(corporationName);
				wloanSubject1.setLoanBankNo(bankCardNo);
				wloanSubject1.setLoanBankName(bankName);
				wloanSubject1.setLoanPhone(userInfo.getPhone());
				wloanSubject1.setLoanBankPhone(userInfo.getPhone());
				wloanSubject1.setLoanBankCode(bankCode);
				wloanSubject1.setLoanIdCard(corporationCertNo);
				wloanSubject1.setBusinessLicenseType(businessLicenseType);
				wloanSubject1.setBusinessNo(businessLicense);
				wloanSubject1.setBankPermitCertNo(bankPermitCertNo);
				wloanSubject1.setOrganNo(orgCode);
				wloanSubject1.setTaxCode(taxRegCertNo);
				wloanSubject1.setAgentPersonPhone(agentPersonPhone);
				wloanSubject1.setAgentPersonName(agentPersonName);
				wloanSubject1.setAgentPersonCertType(agentPersonCertType);
				wloanSubject1.setAgentPersonCertNo(agentPersonCertNo);
				wloanSubject1.setCorporationCertType(corporationCertType);
				wloanSubject1.setCorporationCertNo(corporationCertNo);
				wloanSubject1.setIsEntrustedPay("0");
				wloanSubject1.setCreateDate(new Date());
				wloanSubject1.setUpdateDate(new Date());
				wloanSubject1.setLoanIssuer(issuer);

				int i = wloanSubjectDao.insert(wloanSubject1);
				if (i > 0) {
					log.info("企业开户信息新增成功");
				}
			}

			// 银行卡信息录入
			// N1.先查询用户是否有绑卡,有则进行删除
			log.info("查询用户银行卡个数开始");
			CgbUserBankCard userBankCard = new CgbUserBankCard();
			userBankCard.setUserId(userInfo.getId());
			List<CgbUserBankCard> list = cgbUserBankCardDao.findList(userBankCard);
			log.info("查询用户银行卡个数为" + list.size());
			if (list != null) {
				for (CgbUserBankCard bankCard : list) {
					int j = cgbUserBankCardDao.physicallyDeleted(bankCard);
					if (j > 0) {
						log.info("用户银行卡删除成功,进行开户");
					}
				}
			}
			String orderId = IdGen.uuid();
			// N2.插入新的绑卡信息
			userBankCard.setId(orderId);
			userBankCard.setAccountId(userInfo.getAccountId());
			userBankCard.setUserId(userInfo.getId());
			userBankCard.setBankAccountNo(bankCardNo);
			userBankCard.setBeginBindDate(new Date());
			userBankCard.setId(IdGen.uuid());
			userBankCard.setIsDefault(UserBankCard.DEFAULT_YES);
			userBankCard.setState(UserBankCard.CERTIFY_NO);
			userBankCard.setBankName(bankName);
			userBankCard.setBankNo(bankCode);
			userBankCard.setBindDate(new Date());
			userBankCard.setCreateDate(new Date());
			userBankCard.setUpdateDate(new Date());
			int q = cgbUserBankCardDao.insert(userBankCard);
			if (q > 0) {
				log.info("借款端银行卡信息新增成功");
			}

			// 更新借款用户用户类型
			log.info("更新借款用户用户名称" + agentPersonName + "类型开始");
			userInfo = creditUserInfoDao.get(userInfo.getId());
			if (userInfo != null) {
				userInfo.setCreditUserType(bizType);
				userInfo.setName(agentPersonName);
				userInfo.setEnterpriseFullName(enterpriseFullName);
				userInfo.setCertificateNo(agentPersonCertNo);
				userInfo.setUpdateDate(new Date());
				int p = creditUserInfoDao.update(userInfo);
				if (p > 0) {
					log.info("更新借款用户用户类型成功");
				}
			}

			// 在数据库保存流水数据
			LmTransaction lt = new LmTransaction();
			lt.setServiceName(ServiceNameEnum.ENTERPRISE_REGISTER.getValue());
			lt.setPlatformUserNo(userId);
			lt.setRequestNo(orderId);
			lt.setUpdateDate(new Date());
			lt.setId(orderId);
			lt.setCreateDate(new Date());
			;
			lmTransactionDao.insert(lt);

			reqData.put("platformUserNo", userId);// 平台用户编号
			reqData.put("requestNo", orderId);// 请求流水号
			reqData.put("enterpriseName", enterpriseFullName);// 企业名称
			reqData.put("bankLicense", bankPermitCertNo);// 开户银行许可证号
			reqData.put("orgNo", orgCode);// 组织机构代码
			reqData.put("businessLicense", businessLicense);// 营业执照编号
			reqData.put("taxNo", taxRegCertNo);// 税务登记号
			reqData.put("unifiedCode", businessLicense);// 统一社会信用代码
			reqData.put("creditCode", "");// 机构信用代码
			reqData.put("legal", corporationName);// 法人姓名
			if ("IDC".equals(corporationCertType)) {// IDC-身份证
				reqData.put("idCardType", IdCardTypeEnum.PRC_ID.getValue());
			} else if ("PASS-PORT".equals(corporationCertType)) {// PASS-PORT-护照
				reqData.put("idCardType", IdCardTypeEnum.PASSPORT.getValue());
			} else if ("GAT".equals(corporationCertType)) {// GAT-港澳台身份证
				reqData.put("idCardType", IdCardTypeEnum.COMPATRIOTS_CARD.getValue());
			}
			// else if("MILIARY".equals(corporationCertType)) {// MILIARY-军官证
			// reqData.put("idCardType", IdCardTypeEnum.COMPATRIOTS_CARD.getValue());
			// }
			reqData.put("legalIdCardNo", corporationCertNo);// 法人证件号
			reqData.put("contact", agentPersonName);// 企业联系人
			reqData.put("contactPhone", agentPersonPhone);// 联系人手机号
			if ("11".equals(bizType)) {
				reqData.put("userRole", UserRoleEnum.GUARANTEECORP.getValue());// 担保机构
			} else if ("02".equals(bizType)) {
				reqData.put("userRole", UserRoleEnum.BORROWERS.getValue());// 借款人
			} else if ("05".equals(bizType)) {
				reqData.put("userRole", UserRoleEnum.PLATFORM_FUNDS_TRANSFER.getValue());// 平台总账户
			} else if ("15".equals(bizType)) {
				reqData.put("userRole", UserRoleEnum.BORROWERS.getValue());// 用户角色
			}
			reqData.put("bankcardNo", bankCardNo);// 企业对公账户
			reqData.put("bankcode", bankCode);// ???银行编码
			reqData.put("redirectUrl", ServerURLConfig.BACK_BACKTO_URL_WEB);// 页面回跳 URL
			// reqData.put("authList", AuthEnum.TENDER+"");//用户授权列表

			// 必须添加的参数
			reqData.put("timestamp", DateUtils.getDateStr());

			result = AppUtil.generatePostParam(ServiceNameEnum.ENTERPRISE_REGISTER.getValue(), reqData);

		} catch (Exception e) {
			e.getStackTrace();
		}

		return result;
	}

	/**
	 * @description:企业绑卡
	 * @param userId
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public Map<String, String> enterpriseBindBankCard(CreditUserInfo creditUserInfo, String bankCardNo, String bankCode) {

		// 定义reqData参数集合
		Map<String, String> reqData = new HashMap<String, String>();
		Map<String, String> result = new HashMap<String, String>();
		try {

			String orderId = UUID.randomUUID().toString().replace("-", "");
			System.out.println("orderId = " + orderId);
			reqData.put("platformUserNo", creditUserInfo.getId());
			reqData.put("requestNo", orderId);
			reqData.put("redirectUrl", RedirectUrlConfig.ENTERPRISE_BIND_BANKCARD);
			reqData.put("bankcardNo", bankCardNo);
			reqData.put("bankcode", bankCode);
			// 必须添加的参数
			reqData.put("timestamp", DateUtils.getDateStr());
			result = AppUtil.generatePostParam(ServiceNameEnum.ENTERPRISE_BIND_BANKCARD.getValue(), reqData);
			
			CgbBigrechargeWhiteRecord white = new CgbBigrechargeWhiteRecord();
			white.setId(IdGen.uuid());
			white.setRequestNo(orderId);
			white.setPlatformId(PLATFORM_NO);
			white.setRealName(creditUserInfo.getEnterpriseFullName());
			white.setUserId(creditUserInfo.getId());
			white.setBankCode(bankCode);
			white.setBankNo(bankCardNo);
			white.setStatus(WhiteStatusEnum.GRAY);
			white.setUserRole(UserRoleEnum.BORROWERS.getValue());
			white.setOperationDesc(null);
			white.setCreateDate(new Date());
			white.setUpdateDate(new Date());
			white.setOperationDesc("记录企业绑卡白名单：灰度");
			int whiteFlag = cgbBigrechargeWhiteRecordDao.insert(white);
			log.info("记录企业绑卡白名单：灰度", whiteFlag == 1 ? "成功" : "失败");
			
			LmTransaction lt = new LmTransaction();
			lt.setPlatformUserNo(creditUserInfo.getId());
			lt.setServiceName(ServiceNameEnum.ENTERPRISE_BIND_BANKCARD.getValue());
			lt.setRequestNo(orderId);
			lt.setCreateDate(new Date());
			lt.setUpdateDate(new Date());
			lt.setId(orderId);
			lt.setStatus(LmTransaction.PROCESSING);
			int lm = lmTransactionDao.insert(lt);

		} catch (Exception e) {
			e.getStackTrace();
		}

		return result;
	}

	/**
	 * @description:企业信息修改
	 * @param userId
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public Map<String, String> informationUpdate(String userId) {

		// 定义reqData参数集合
		Map<String, String> reqData = new HashMap<String, String>();
		Map<String, String> result = new HashMap<String, String>();
		try {
			String orderId = UUID.randomUUID().toString().replace("-", "");
			System.out.println("orderId = " + orderId);

			// 在数据库保存流水数据
			LmTransaction lt = new LmTransaction();
			lt.setId(orderId);
			lt.setServiceName(ServiceNameEnum.ENTERPRISE_INFORMATION_UPDATE.getValue());
			lt.setPlatformUserNo(userId);
			lt.setRequestNo(orderId);
			lt.setCreateDate(new Date());
			;
			lt.setUpdateDate(new Date());
			lmTransactionDao.insert(lt);

			reqData.put("platformUserNo", userId);
			reqData.put("requestNo", orderId);
			reqData.put("redirectUrl", ServerURLConfig.BACK_BACKTO_URL_WEB);

			// 必须添加的参数
			reqData.put("timestamp", DateUtils.getDateStr());

			result = AppUtil.generatePostParam(ServiceNameEnum.ENTERPRISE_INFORMATION_UPDATE.getValue(), reqData);

		} catch (Exception e) {
			e.getStackTrace();
		}

		return result;
	}

	/**
	 * @description:企业解绑银行卡
	 * @param userId
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public Map<String, String> untyingCard(String userId,String orderId) {

		// 定义reqData参数集合
		Map<String, String> reqData = new HashMap<String, String>();
		Map<String, String> result = new HashMap<String, String>();
		try {
			if(orderId==null||"".equals(orderId)) {
				orderId = UUID.randomUUID().toString().replace("-", "");
			}
			System.out.println("orderId = " + orderId);
			reqData.put("platformUserNo", userId);
			reqData.put("requestNo", orderId);
			reqData.put("redirectUrl", RedirectUrlConfig.UNBIND_BANKCARD);
			// 必须添加的参数
			reqData.put("timestamp", DateUtils.getDateStr());
			result = AppUtil.generatePostParam(ServiceNameEnum.UNBIND_BANKCARD.getValue(), reqData);
			LmTransaction lt = new LmTransaction();
			lt.setPlatformUserNo(userId);
			lt.setServiceName(ServiceNameEnum.UNBIND_BANKCARD.getValue());
			lt.setRequestNo(orderId);
			lt.setCreateDate(new Date());
			lt.setUpdateDate(new Date());
			lt.setId(orderId);
			lt.setStatus(LmTransaction.PROCESSING);
			int lm = lmTransactionDao.insert(lt);
		} catch (Exception e) {
			e.getStackTrace();
		}

		return result;
	}

	/**
	 * @description:企业解绑银行卡回调
	 * @param userId
	 * @return
	 */
	public void callbackUntyingCard(JSONObject json) {

		// 为借款人解绑银行卡回调
		String userId = json.getString("platformUserNo");
		String orderId = json.getString("requestNo");
		CgbUserBankCard cgbUserBankCard = new CgbUserBankCard();
		cgbUserBankCard.setUserId(userId);
		List<CgbUserBankCard> cgbUserBankCards = cgbUserBankCardDao.findCreditList(cgbUserBankCard);
		if (cgbUserBankCards != null) {
			if (cgbUserBankCards.size() > 0) {
				CgbUserBankCard creditUserBank = cgbUserBankCards.get(0);
				if (CgbUserBankCard.CERTIFY_NO.equals(creditUserBank.getState())) {
					// 表示企业已经解绑成功
					return;
				}
				creditUserBank.setState(CgbUserBankCard.CERTIFY_NO);
				// creditUserBank.setBankAccountNo(null); // 银行账户
				// creditUserBank.setBankName(null);
				// creditUserBank.setBankNo(null); // 银行编码
				creditUserBank.setUpdateDate(new Date()); // 更新时间
				int creditUserBankUpdateFlag = cgbUserBankCardDao.update(creditUserBank);
				log.info("企业银行卡信息更新:{}", creditUserBankUpdateFlag == 1 ? "成功" : "失败");
			}
		}
		LmTransaction lm = new LmTransaction();
		lm.setServiceName(ServiceNameEnum.UNBIND_BANKCARD.getValue());
		lm.setPlatformUserNo(userId);
		lm.setRequestNo(orderId);
		List<LmTransaction> ltList = lmTransactionDao.findList(lm);
		if (ltList != null || ltList.size() != 0) {
			LmTransaction lt = ltList.get(0);
			lt.setUpdateDate(new Date());
			lt.setCode(json.getString("code"));
			lt.setStatus(json.getString("status"));
			lt.setErrorCode(json.getString("errorCode"));
			lt.setErrorMessage(json.getString("errorMessage"));
			int m = lmTransactionDao.update(lt);
			if (m > 0) {
				log.info("企业解绑银行卡流水号修改成功");
			}
		}
	}

	/**
	 * @description:预留手机号更新
	 * @param userId
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public Map<String, String> modifyMobileExpand(String userId) {

		// 定义reqData参数集合
		Map<String, String> reqData = new HashMap<String, String>();
		Map<String, String> result = new HashMap<String, String>();
		try {
			String orderId = UUID.randomUUID().toString().replace("-", "");
			System.out.println("orderId = " + orderId);

			reqData.put("platformUserNo", userId);
			reqData.put("requestNo", orderId);
			reqData.put("redirectUrl", RedirectUrlConfig.MODIFY_MOBILE_EXPAND);
			reqData.put("checkType", CheckTypeEnum.LIMIT.getValue());
			// 必须添加的参数
			reqData.put("timestamp", DateUtils.getDateStr());
			result = AppUtil.generatePostParam(ServiceNameEnum.MODIFY_MOBILE_EXPAND.getValue(), reqData);

			LmTransaction lt = new LmTransaction();
			lt.setPlatformUserNo(userId);
			lt.setServiceName(ServiceNameEnum.MODIFY_MOBILE_EXPAND.getValue());
			lt.setRequestNo(orderId);
			lt.setCreateDate(new Date());
			lt.setUpdateDate(new Date());
			lt.setId(orderId);
			lt.setStatus(LmTransaction.PROCESSING);
			int lm = lmTransactionDao.insert(lt);
		} catch (Exception e) {
			e.getStackTrace();
		}

		return result;
	}

	/**
	 * 
	 * methods: enterpriseInformationUpdate <br>
	 * description: 企业信息修改 <br>
	 * author: Roy <br>
	 * date: 2019年10月18日 下午4:11:14
	 * 
	 * @param creditUserInfo
	 * @return
	 */
	public Map<String, String> enterpriseInformationUpdate(CreditUserInfo creditUserInfo) {

		// 定义reqData参数集合
		Map<String, String> result = new HashMap<String, String>();
		String platformUserNo = "";
		String requestNo = "";
		try {
			platformUserNo = creditUserInfo.getId();
			requestNo = IdGen.uuid();

			Map<String, Object> reqData = new HashMap<String, Object>();
			reqData.put("platformUserNo", platformUserNo);
			reqData.put("requestNo", requestNo);
			reqData.put("redirectUrl", RedirectUrlConfig.ENTERPRISE_INFORMATION_UPDATE);
			// 必须添加的参数
			reqData.put("timestamp", DateUtils.getDateStr());
			result = AppUtil.lmGeneratePostParam(ServiceNameEnum.ENTERPRISE_INFORMATION_UPDATE.getValue(), reqData);

			LmTransaction lt = new LmTransaction();
			lt.setId(requestNo);
			lt.setPlatformUserNo(platformUserNo);
			lt.setServiceName(ServiceNameEnum.ENTERPRISE_INFORMATION_UPDATE.getValue());
			lt.setRequestNo(requestNo);
			lt.setCreateDate(new Date());
			lt.setUpdateDate(new Date());
			lt.setStatus(LmTransaction.PROCESSING);
			int lm = lmTransactionDao.insert(lt);
			log.info("懒猫交易留存，插入:{}", lm == 1 ? "成功" : "失败");
		} catch (Exception e) {
			e.getStackTrace();
		}

		return result;
	}

	/**
	 * @description:企业信息修改
	 * @param userId
	 * @return
	 */
	public Map<String, String> enterpriseInformationUpdate(String userId) {

		// 定义reqData参数集合
		Map<String, String> reqData = new HashMap<String, String>();
		Map<String, String> result = new HashMap<String, String>();
		try {
			String orderId = UUID.randomUUID().toString().replace("-", "");
			System.out.println("orderId = " + orderId);

			reqData.put("platformUserNo", userId);
			reqData.put("requestNo", orderId);
			reqData.put("redirectUrl", RedirectUrlConfig.ENTERPRISE_INFORMATION_UPDATE);
			// 必须添加的参数
			reqData.put("timestamp", DateUtils.getDateStr());
			result = AppUtil.generatePostParam(ServiceNameEnum.ENTERPRISE_INFORMATION_UPDATE.getValue(), reqData);

			LmTransaction lt = new LmTransaction();
			lt.setPlatformUserNo(userId);
			lt.setServiceName(ServiceNameEnum.ENTERPRISE_INFORMATION_UPDATE.getValue());
			lt.setRequestNo(orderId);
			lt.setCreateDate(new Date());
			lt.setUpdateDate(new Date());
			lt.setId(orderId);
			lt.setStatus(LmTransaction.PROCESSING);
			int lm = lmTransactionDao.insert(lt);
		} catch (Exception e) {
			e.getStackTrace();
		}

		return result;
	}

	// /**
	// * @description:预留手机号更新回调
	// * @param json
	// * @return
	// */
	// @Transactional(rollbackFor = Exception.class)
	// public void callbackModifyMobileExpand(JSONObject json) {
	//
	// String userId = json.getString("platformUserNo");
	// String orderId = json.getString("requestNo");
	// CgbUserBankCard userBankCard = new CgbUserBankCard();
	// userBankCard.setUserId(userId);
	// List<CgbUserBankCard> list = cgbUserBankCardDao.findList(userBankCard);
	// if (list != null) {
	// CgbUserBankCard cbk = list.get(0);
	// cbk.setUpdateDate(new Date());
	// cbk.setBankAccountNo(json.getString("bankcardNo"));
	// cbk.setBankNo(json.getString("bankcode"));
	// cbk.setBankCardPhone(json.getString("mobile"));
	// int i = cgbUserBankCardDao.update(cbk);
	// if (i > 0) {
	// log.info("银行卡信息修改成功");
	// }
	// }
	// // 查询流水表
	// LmTransaction lm = new LmTransaction();
	// lm.setServiceName(ServiceNameEnum.MODIFY_MOBILE_EXPAND.getValue());
	// lm.setPlatformUserNo(userId);
	// lm.setRequestNo(orderId);
	// List<LmTransaction> ltList = lmTransactionDao.findList(lm);
	// if (ltList != null || ltList.size() != 0) {
	// LmTransaction lt = ltList.get(0);
	// lt.setUpdateDate(new Date());
	// lt.setCode(json.getString("code"));
	// lt.setStatus(json.getString("status"));
	// // 鉴权通过类型
	// lt.setAccessType(json.getString("accessType"));
	// lt.setErrorCode(json.getString("errorCode"));
	// lt.setErrorMessage(json.getString("errorMessage"));
	// int m = lmTransactionDao.update(lt);
	// if (m > 0) {
	// log.info("企业用户预留手机号更新回调流水号修改生成成功");
	// }
	// }
	// }

}
