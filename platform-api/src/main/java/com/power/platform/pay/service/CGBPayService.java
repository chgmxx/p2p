package com.power.platform.pay.service;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.cunguanbao.api.utils.APIUtils;
import com.power.platform.cache.Cache;
import com.power.platform.cgb.dao.CgbUserAccountDao;
import com.power.platform.cgb.dao.CgbUserBankCardDao;
import com.power.platform.cgb.entity.CgbUserAccount;
import com.power.platform.cgb.entity.CgbUserBankCard;
import com.power.platform.cgb.entity.CicmorganBankCode;
import com.power.platform.cgb.service.CicmorganBankCodeService;
import com.power.platform.cgbpay.config.ServerURLConfig;
import com.power.platform.common.config.Global;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.JedisUtils;
import com.power.platform.common.utils.MemCachedUtis;
import com.power.platform.credit.dao.userinfo.CreditUserInfoDao;
import com.power.platform.credit.entity.supplierToMiddlemen.CreditSupplierToMiddlemen;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.service.supplierToMiddlemen.CreditSupplierToMiddlemenService;
import com.power.platform.credit.service.userinfo.CreditUserInfoService;
import com.power.platform.pay.cash.dao.UserCashDao;
import com.power.platform.pay.recharge.dao.UserRechargeDao;
import com.power.platform.regular.dao.WloanSubjectDao;
import com.power.platform.regular.entity.WloanSubject;
import com.power.platform.regular.service.WloanSubjectService;
import com.power.platform.sys.utils.HttpUtil;
import com.power.platform.trandetail.dao.UserTransDetailDao;
import com.power.platform.userinfo.dao.UserAccountInfoDao;
import com.power.platform.userinfo.dao.UserBankCardDao;
import com.power.platform.userinfo.dao.UserInfoDao;
import com.power.platform.userinfo.entity.Principal;
import com.power.platform.userinfo.entity.UserBankCard;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.weixin.service.WeixinSendTempMsgService;

/**
 * CGB支付Service
 * 
 * @author 曹智
 * @version 2015-12-23
 */

@Service("cGBPayService")
public class CGBPayService {

	private static final Logger LOG = LoggerFactory.getLogger(CGBPayService.class);
	/**
	 * 2：未开户
	 */
//	private static final String BANK_STATE_2 = "2";
	// 商户号
	private static final String merchantId = Global.getConfig("merchantId");
	// 存管系统为你分配的，用来对AES私钥加密，同时生成签名
	private static final String merchantRsaPublicKey = Global.getConfig("merchantRsaPublicKey");
	// 商户自己的RSA私钥
	private static final String merchantRsaPrivateKey = Global.getConfig("merchantRsaPrivateKey");
	@Resource
	private UserRechargeDao userRechargeDao;
	@Resource
	private UserCashDao userCashDao;
	@Resource
	private UserAccountInfoDao userAccountInfoDao;
	@Resource
	private UserTransDetailDao userTransDetailDao;
	@Resource
	private UserInfoDao userInfoDao;
	@Resource
	private UserBankCardDao userBankCardDao;
	@Resource
	private CreditUserInfoDao creditUserInfoDao;
	@Resource
	private WeixinSendTempMsgService weixinSendTempMsgService;
	@Resource
	private CgbUserAccountDao cgbUserAccountDao;
	@Resource
	private CgbUserBankCardDao cgbUserBankCardDao;
	@Resource
	private WloanSubjectDao wloanSubjectDao;
	@Resource
	private CreditSupplierToMiddlemenService creditSupplierToMiddlemenService;
	@Resource
	private WloanSubjectService wloanSubjectService;
	@Autowired
	private CicmorganBankCodeService cicmorganBankCodeService;
	@Autowired
	private CreditUserInfoService creditUserInfoService;

	/**
	 * 用户开户接口(pc)
	 * 
	 * @param bankCardNo
	 *            银行卡号
	 * @param certNo
	 *            身份证号
	 * @param realName
	 *            真实姓名
	 * @param bankCardPhone
	 *            银行预留手机号
	 * @param ip
	 *            访问ip
	 * @param token
	 *            token
	 * @return
	 * @throws Exception
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> accountCreateWeb(String bankCardNo, String certNo, String realName, String ip, String bankCardPhone, String token) throws Exception {

		String orderId = UUID.randomUUID().toString().replace("-", "");
		// 出借人ID.
		String userInfoId = JedisUtils.get(token);
		// 重新获取一遍出借人信息(解决一些字段重新赋值的情况).
		UserInfo userInfo = null;
		userInfo = userInfoDao.get(userInfoId);
		if (null == userInfo) {
			userInfo = userInfoDao.getCgb(userInfoId);
		}
		// 构造请求参数
		Map<String, String> params = new HashMap<String, String>();
		params.put("orderId", orderId);
		params.put("userId", userInfo.getId());
		params.put("bankCardPhone", bankCardPhone);
		params.put("bankCardNo", bankCardNo);
		params.put("realName", realName);
		params.put("bizType", "01");
		params.put("certNo", certNo);
		params.put("certType", "IDC");
		params.put("service", "web.member.account.create");
		params.put("method", "RSA");
		params.put("merchantId", merchantId);
		params.put("source", "1");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String requestTime = sdf.format(new Date());
		params.put("requestTime", requestTime);
		params.put("version", "1.0.0");
		params.put("reqSn", UUID.randomUUID().toString().replace("-", ""));
		params.put("returnUrl", ServerURLConfig.BACK_BACKTO_URL_WEB);
		params.put("callbackUrl", ServerURLConfig.BACK_ACCOUNT_URL);
		// 生成签名
		String sign = APIUtils.createSign(merchantRsaPrivateKey, params, "RSA");
		params.put("signature", sign);
		String jsonString = JSON.toJSONString(params);
		System.out.println("用户开户PC端Web[请求参数]" + jsonString);
		// 加密
		Map<String, String> encryptRet = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);

		// N1.先查询用户是否有绑卡,有则进行删除
		CgbUserBankCard userBankCard = new CgbUserBankCard();
		userBankCard.setUserId(userInfo.getId());
		List<CgbUserBankCard> list = cgbUserBankCardDao.findList(userBankCard);
		if (list != null) {
			for (CgbUserBankCard bankCard : list) {
				int i = cgbUserBankCardDao.physicallyDeleted(bankCard);
				if (i > 0) {
					LOG.info("用户银行卡删除成功,进行开户");
				}
			}
		}
		// N2.插入新的绑卡信息
		userBankCard.setAccountId(userInfo.getAccountId());
		userBankCard.setUserId(userInfo.getId());
		userBankCard.setBankAccountNo(bankCardNo);
		userBankCard.setBankCardPhone(bankCardPhone);
		userBankCard.setBeginBindDate(new Date());
		userBankCard.setId(orderId);
		userBankCard.setIsDefault(UserBankCard.DEFAULT_YES);
		userBankCard.setState(UserBankCard.CERTIFY_NO);
		userBankCard.setBindDate(new Date());
		userBankCard.setCreateDate(new Date());
		userBankCard.setUpdateDate(new Date());
		int i = cgbUserBankCardDao.insert(userBankCard);
		if (i > 0) {
			LOG.info("银行卡信息新增成功");
		}
		// N3.用户信息保存
		if (userInfo.getCgbBindBankCardState() == null || userInfo.getCgbBindBankCardState().intValue() == UserInfo.BIND_CARD_NO) {
			userInfo.setRealName(realName);// 真实姓名
			userInfo.setCertificateNo(certNo);// 身份证号
			userInfo.setCertificateChecked(UserInfo.CERTIFICATE_NO);
			userInfo.setCgbBindBankCardState(UserInfo.CGB_BIND_CARD_NO);
			int j = userInfoDao.update(userInfo);
			if (j > 0) {
				LOG.info("用户信息修改成功");
			}
		}

		// 针对老客户需要重新开户 新增账户表数据
		if (cgbUserAccountDao.getUserAccountInfo(userInfo.getId()) == null) {
			// 生成客户账户
			CgbUserAccount userAccountInfo = new CgbUserAccount();
			System.out.println("用户注册插入客户表完成");
			userAccountInfo.setId(userInfo.getAccountId());
			userAccountInfo.setUserId(userInfo.getId());
			userAccountInfo.setTotalAmount(0d);
			userAccountInfo.setTotalInterest(0d);
			userAccountInfo.setAvailableAmount(0d);
			userAccountInfo.setFreezeAmount(0d);
			userAccountInfo.setRechargeAmount(0d);
			userAccountInfo.setRechargeCount(0);
			userAccountInfo.setCashAmount(0d);
			userAccountInfo.setCashCount(0);
			userAccountInfo.setCurrentAmount(0d);
			userAccountInfo.setRegularDuePrincipal(0d);
			userAccountInfo.setRegularDueInterest(0d);
			userAccountInfo.setRegularTotalAmount(0d);
			userAccountInfo.setRegularTotalInterest(0d);
			userAccountInfo.setCurrentTotalAmount(0d);
			userAccountInfo.setCurrentTotalInterest(0d);
			userAccountInfo.setCurrentYesterdayInterest(0d);
			userAccountInfo.setReguarYesterdayInterest(0d);
			// 同时生成客户账户
			cgbUserAccountDao.insert(userAccountInfo);
		}

		// 对生成数据进行urlencoding
		String data = encryptRet.get("data");
		data = URLEncoder.encode(data, "UTF-8");
		String tm = encryptRet.get("tm");
		tm = URLEncoder.encode(tm, "UTF-8");
		encryptRet.put("data", data);
		encryptRet.put("tm", tm);
		encryptRet.put("merchantId", merchantId);
		// 返回订单信息
		return encryptRet;
	}

	/**
	 * 企业开户接口（PC）
	 * 
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
	 * @throws Exception
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> accountCreateByCompany(String token, String bizType, String enterpriseFullName, String businessLicenseType, String businessLicense, String bankPermitCertNo, String taxRegCertNo, String orgCode, String agentPersonName, String agentPersonPhone, String agentPersonCertType, String agentPersonCertNo, String corporationName, String corporationCertType, String corporationCertNo, String bankName, String bankCode, String bankCardNo, String bankCardName, String bankProvince, String bankCity, String issuerName, String issuer, String supplierId) throws Exception {

		Cache cache = MemCachedUtis.getMemCached();
		Principal principal = cache.get(token);
		String userId = null;
		if (principal != null) {
			userId = principal.getCreditUserInfo().getId();
		}
		CreditUserInfo userInfo = creditUserInfoDao.get(userId);// 借款用户
		// 构造请求参数
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", userInfo.getId());// 网贷平台统一用户编码
		params.put("bizType", bizType);// 业务类型：02-借款户
		params.put("enterpriseFullName", enterpriseFullName);// 企业全称
		params.put("businessLicenseType", businessLicenseType);// BLC-营业执照
																// USCC-同意社会信用代码
		if (businessLicenseType.equals("BLC")) {
			params.put("taxRegCertNo", taxRegCertNo);// 税务登记证
			params.put("orgCode", orgCode);// 组织机构代码
		}
		params.put("businessLicense", businessLicense);// 证照号

		params.put("bankPermitCertNo", bankPermitCertNo);// 银行开户许可证编号regionCode
		params.put("regionCode", "086");// 国家区域码 中国086
		params.put("agentPersonName", agentPersonName);// 联系人姓名
		params.put("agentPersonPhone", agentPersonPhone);// 联系人手机号
		params.put("agentPersonCertType", agentPersonCertType);// 联系人证件类型
																// IDC-身份证
																// GAT-港澳台身份证
																// MILIARY-军官证
																// PASS-PORT-护照
		params.put("agentPersonCertNo", agentPersonCertNo);// 联系人证件号
		params.put("corporationName", corporationName);// 法人姓名
		params.put("corporationCertType", corporationCertType);// 法人证件类型 IDC-身份证
																// GAT-港澳台身份证
																// MILIARY-军官证
																// PASS-PORT-护照
		params.put("corporationCertNo", corporationCertNo);// 法人证件号
		params.put("bankName", bankName);// 银行名称
		params.put("bankCode", bankCode);// 银行编码
		params.put("bankCardNo", bankCardNo);// 银行账号
		params.put("bankCardName", bankCardName);// 银行开户名
		params.put("bankProvince", bankProvince);// 省
		params.put("bankCity", bankCity);// 市
		params.put("issuerName", issuerName);// 支行名称
		params.put("issuer", issuer);// 支行-联行号

		params.put("service", "web.member.account.createenterprise");
		params.put("method", "RSA");
		params.put("merchantId", merchantId);
		params.put("source", "1");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String requestTime = sdf.format(new Date());
		params.put("requestTime", requestTime);
		params.put("version", "1.0.0");
		params.put("reqSn", UUID.randomUUID().toString().replace("-", ""));
		params.put("returnUrl", ServerURLConfig.BACK_BACKTO_URL_BORROWWEB);
		params.put("callbackUrl", ServerURLConfig.BACK_ACCOUNT_URL_COMPANY);
		// 生成签名
		String sign = APIUtils.createSign(merchantRsaPrivateKey, params, "RSA");
		params.put("signature", sign);
		String jsonString = JSON.toJSONString(params);
		System.out.println("企业开户PC端[请求参数]" + jsonString);
		// 加密
		Map<String, String> encryptRet = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);

		// 插入企业信息
		LOG.info("企业信息更新开始");
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
					LOG.info("企业开户信息更新成功");
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
				LOG.info("企业开户信息新增成功");
			}
		}

		// 银行卡信息录入
		// N1.先查询用户是否有绑卡,有则进行删除
		LOG.info("查询用户银行卡个数开始");
		CgbUserBankCard userBankCard = new CgbUserBankCard();
		userBankCard.setUserId(userInfo.getId());
		List<CgbUserBankCard> list = cgbUserBankCardDao.findList(userBankCard);
		LOG.info("查询用户银行卡个数为" + list.size());
		if (list != null) {
			for (CgbUserBankCard bankCard : list) {
				int j = cgbUserBankCardDao.physicallyDeleted(bankCard);
				if (j > 0) {
					LOG.info("用户银行卡删除成功,进行开户");
				}
			}
		}
		// N2.插入新的绑卡信息
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
			LOG.info("借款端银行卡信息新增成功");
		}

		// 更新借款用户用户类型
		LOG.info("更新借款用户用户名称" + agentPersonName + "类型开始");
		userInfo = creditUserInfoDao.get(userInfo.getId());
		if (userInfo != null) {
			userInfo.setCreditUserType(bizType);
			userInfo.setName(agentPersonName);
			userInfo.setEnterpriseFullName(enterpriseFullName);
			userInfo.setCertificateNo(agentPersonCertNo);
			userInfo.setUpdateDate(new Date());
			int p = creditUserInfoDao.update(userInfo);
			if (p > 0) {
				LOG.info("更新借款用户用户类型成功");
			}
		}

		// 对生成数据进行urlencoding
		String data = encryptRet.get("data");
		data = URLEncoder.encode(data, "UTF-8");
		String tm = encryptRet.get("tm");
		tm = URLEncoder.encode(tm, "UTF-8");
		encryptRet.put("data", data);
		encryptRet.put("tm", tm);
		encryptRet.put("merchantId", merchantId);
		// 返回订单信息
		return encryptRet;
	}

	/**
	 * 企业开户接口（ERP借款端）
	 * 
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
	 * @throws Exception
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> accountCreateByCompanyForErp(String id, String bizType, String enterpriseFullName, String businessLicenseType, String businessLicense, String bankPermitCertNo, String taxRegCertNo, String orgCode, String agentPersonName, String agentPersonPhone, String agentPersonCertType, String agentPersonCertNo, String corporationName, String corporationCertType, String corporationCertNo, String bankName, String bankCode, String bankCardNo, String bankCardName, String bankProvince, String bankCity, String issuerName, String issuer, String supplierId, String email, String registAddress) throws Exception {

		CreditUserInfo userInfo = creditUserInfoDao.get(id);// 借款用户
		// 构造请求参数
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", userInfo.getId());// 网贷平台统一用户编码

		if (bizType.equals("15")) {
			params.put("bizType", "02");// 业务类型：02-借款户
		} else {
			params.put("bizType", bizType);// 业务类型：02-借款户
		}
		params.put("enterpriseFullName", enterpriseFullName);// 企业全称
		params.put("businessLicenseType", businessLicenseType);// BLC-营业执照
																// USCC-同意社会信用代码
		if (businessLicenseType.equals("BLC")) {
			params.put("taxRegCertNo", taxRegCertNo);// 税务登记证
			params.put("orgCode", orgCode);// 组织机构代码
		}
		params.put("businessLicense", businessLicense);// 证照号

		params.put("bankPermitCertNo", bankPermitCertNo);// 银行开户许可证编号regionCode
		params.put("regionCode", "086");// 国家区域码 中国086
		params.put("agentPersonName", agentPersonName);// 联系人姓名
		params.put("agentPersonPhone", agentPersonPhone);// 联系人手机号
		params.put("agentPersonCertType", agentPersonCertType);// 联系人证件类型
																// IDC-身份证
																// GAT-港澳台身份证
																// MILIARY-军官证
																// PASS-PORT-护照
		params.put("agentPersonCertNo", agentPersonCertNo);// 联系人证件号
		params.put("corporationName", corporationName);// 法人姓名
		params.put("corporationCertType", corporationCertType);// 法人证件类型 IDC-身份证
																// GAT-港澳台身份证
																// MILIARY-军官证
																// PASS-PORT-护照
		params.put("corporationCertNo", corporationCertNo);// 法人证件号
		params.put("bankName", bankName);// 银行名称
		params.put("bankCode", bankCode);// 银行编码
		params.put("bankCardNo", bankCardNo);// 银行账号
		params.put("bankCardName", bankCardName);// 银行开户名
		params.put("bankProvince", bankProvince);// 省
		params.put("bankCity", bankCity);// 市
		params.put("issuerName", issuerName);// 支行名称
		params.put("issuer", issuer);// 支行-联行号

		params.put("service", "web.member.account.createenterprise");
		params.put("method", "RSA");
		params.put("merchantId", merchantId);
		params.put("source", "1");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String requestTime = sdf.format(new Date());
		params.put("requestTime", requestTime);
		params.put("version", "1.0.0");
		params.put("reqSn", UUID.randomUUID().toString().replace("-", ""));
		params.put("returnUrl", ServerURLConfig.BACK_BACKTO_URL_BORROWWEB + "&id=" + id);
		params.put("callbackUrl", ServerURLConfig.BACK_ACCOUNT_URL_COMPANY);
		// 生成签名
		String sign = APIUtils.createSign(merchantRsaPrivateKey, params, "RSA");
		params.put("signature", sign);
		String jsonString = JSON.toJSONString(params);
		System.out.println("企业开户PC端[请求参数]" + jsonString);
		// 加密
		Map<String, String> encryptRet = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);

		// 插入企业信息
		LOG.info("企业信息更新开始");
		WloanSubject entity = new WloanSubject();
		entity.setLoanApplyId(id);
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
				wloanSubject.setEmail(email);
				wloanSubject.setRegistAddress(registAddress);

				int i = wloanSubjectDao.update(wloanSubject);
				if (i > 0) {
					LOG.info("企业开户信息更新成功");
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
			wloanSubject1.setEmail(email);
			wloanSubject1.setRegistAddress(registAddress);

			int i = wloanSubjectDao.insert(wloanSubject1);
			if (i > 0) {
				LOG.info("企业开户信息新增成功");
			}
		}

		// 银行卡信息录入
		// N1.先查询用户是否有绑卡,有则进行删除
		LOG.info("查询用户银行卡个数开始");
		CgbUserBankCard userBankCard = new CgbUserBankCard();
		userBankCard.setUserId(userInfo.getId());
		List<CgbUserBankCard> list = cgbUserBankCardDao.findList(userBankCard);
		LOG.info("查询用户银行卡个数为" + list.size());
		if (list != null) {
			for (CgbUserBankCard bankCard : list) {
				int j = cgbUserBankCardDao.physicallyDeleted(bankCard);
				if (j > 0) {
					LOG.info("用户银行卡删除成功,进行开户");
				}
			}
		}
		// N2.插入新的绑卡信息
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
			LOG.info("借款端银行卡信息新增成功");
		}

		// 更新借款用户用户类型
		LOG.info("更新借款用户用户名称" + agentPersonName + "类型开始");
		userInfo = creditUserInfoDao.get(userInfo.getId());
		if (userInfo != null) {
			if (bizType.equals("15")) {
				userInfo.setCreditUserType("02");
				userInfo.setAccountType(CreditUserInfoService.ACCOUNT_TYPE1);
				userInfo.setOwnedCompany("房产抵押");
			} else {
				userInfo.setCreditUserType(bizType);
				userInfo.setAccountType(CreditUserInfoService.ACCOUNT_TYPE2);
				if (bizType.equals("02")) {
					CreditSupplierToMiddlemen supplierToMiddlemen = new CreditSupplierToMiddlemen();
					supplierToMiddlemen.setSupplierId(userInfo.getId());
					List<CreditSupplierToMiddlemen> supplierToMiddlemenList = creditSupplierToMiddlemenService.findList(supplierToMiddlemen);
					if (supplierToMiddlemenList != null) {
						String middlementId = supplierToMiddlemenList.get(0).getMiddlemenId();
						CreditUserInfo cUserInfo = creditUserInfoDao.get(middlementId);
						if (cUserInfo != null) {
							userInfo.setOwnedCompany(cUserInfo.getEnterpriseFullName());
						}
					}
				} else if (bizType.equals("11")) {
					userInfo.setOwnedCompany(enterpriseFullName);
				}
			}
			userInfo.setName(agentPersonName);
			userInfo.setEnterpriseFullName(enterpriseFullName);
			userInfo.setCertificateNo(agentPersonCertNo);
			userInfo.setUpdateDate(new Date());
			int p = creditUserInfoDao.update(userInfo);
			if (p > 0) {
				LOG.info("更新借款用户用户类型成功");
			}
		}

		if (bizType.equals("15")) {
			// 添加中间表
			LOG.info("抵押户中间表添加");
			CreditSupplierToMiddlemen creditSupplierToMiddlemen = new CreditSupplierToMiddlemen();
			creditSupplierToMiddlemen.setId(IdGen.uuid());
			creditSupplierToMiddlemen.setSupplierId(userInfo.getId());
			creditSupplierToMiddlemen.setMiddlemenId(userInfo.getId());
			int n = creditSupplierToMiddlemenService.insertCreditSupplierToMiddlemen(creditSupplierToMiddlemen);
			if (n > 0) {
				LOG.info("抵押户中间表添加成功");
			}
		}

		// 对生成数据进行urlencoding
		String data = encryptRet.get("data");
		data = URLEncoder.encode(data, "UTF-8");
		String tm = encryptRet.get("tm");
		tm = URLEncoder.encode(tm, "UTF-8");
		encryptRet.put("data", data);
		encryptRet.put("tm", tm);
		encryptRet.put("merchantId", merchantId);
		// 返回订单信息
		return encryptRet;
	}

	/**
	 * 用户开户(H5)
	 * 
	 * @param bankCardNo
	 * @param certNo
	 * @param realName
	 * @param ip
	 * @param bankCardPhone
	 * @param token
	 * @return
	 * @throws Exception
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> accountCreateH5(String bankCardNo, String certNo, String realName, String ip, String bankCardPhone, String token, String bizType, String from) throws Exception {

		// 从缓存获取用户信息
		String jedisUserId = JedisUtils.get(token);

		String orderId = UUID.randomUUID().toString().replace("-", "");
		String userId = "";
		String accountId = "";
		if (bizType.equals("01")) {
			userId = jedisUserId;
			UserInfo user = userInfoDao.getCgb(jedisUserId);
			if (user == null) {
				user = userInfoDao.get(jedisUserId);
			}
			accountId = user.getAccountId();
		}
		// 构造请求参数
		Map<String, String> params = new HashMap<String, String>();
		params.put("orderId", orderId);
		params.put("userId", userId);
		params.put("bankCardPhone", bankCardPhone);
		params.put("bankCardNo", bankCardNo);
		params.put("realName", realName);
		if (from.equals("2")) {
			params.put("mobileType", "22");
		}
		params.put("bizType", bizType);
		params.put("certNo", certNo);
		params.put("certType", "IDC");
		params.put("service", "h5.p2p.member.account.create");
		params.put("method", "RSA");
		params.put("merchantId", merchantId);
		params.put("source", "2");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String requestTime = sdf.format(new Date());
		params.put("requestTime", requestTime);
		params.put("version", "1.0.0");
		params.put("reqSn", UUID.randomUUID().toString().replace("-", ""));
		if (from.equals("3") || from.equals("4")) {
			params.put("returnUrl", ServerURLConfig.BACK_BACKTO_URL);
		} else {
			params.put("returnUrl", ServerURLConfig.BACK_BACKTO_URL_WAP);
		}
		params.put("callbackUrl", ServerURLConfig.BACK_ACCOUNT_URL);
		// 生成签名
		String sign = APIUtils.createSign(merchantRsaPrivateKey, params, "RSA");
		params.put("signature", sign);
		String jsonString = JSON.toJSONString(params);
		System.out.println("用户开户手机端H5[请求参数]" + jsonString);
		// 加密
		Map<String, String> encryptRet = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);

		// N1.先查询用户是否有绑卡,有则进行删除
		CgbUserBankCard userBankCard = new CgbUserBankCard();
		userBankCard.setUserId(userId);
		List<CgbUserBankCard> list = cgbUserBankCardDao.findList(userBankCard);
		LOG.info("查询出用户" + userId + "共有银行卡" + list.size());
		if (list != null) {
			for (CgbUserBankCard bankCard : list) {
				int i = cgbUserBankCardDao.physicallyDeleted(bankCard);
				if (i > 0) {
					LOG.info("用户银行卡删除成功,进行开户");
				}
			}
		}
		// N2.插入新的绑卡信息
		userBankCard.setAccountId(accountId);
		userBankCard.setUserId(userId);
		userBankCard.setBankAccountNo(bankCardNo);
		userBankCard.setBankCardPhone(bankCardPhone);
		userBankCard.setBeginBindDate(new Date());
		userBankCard.setId(orderId);
		userBankCard.setIsDefault(UserBankCard.DEFAULT_YES);
		userBankCard.setState(UserBankCard.CERTIFY_NO);
		userBankCard.setBindDate(new Date());
		userBankCard.setCreateDate(new Date());
		userBankCard.setUpdateDate(new Date());
		int i = cgbUserBankCardDao.insert(userBankCard);
		if (i > 0) {
			LOG.info("银行卡信息新增成功");
		}
		// N3.投资用户信息保存
		if (bizType.equals("01")) {
			UserInfo userInfo = userInfoDao.getCgb(jedisUserId);
			if (userInfo == null) {
				userInfo = userInfoDao.get(jedisUserId);
			}
			if (userInfo.getCgbBindBankCardState() == null || userInfo.getCgbBindBankCardState().intValue() == UserInfo.BIND_CARD_NO) {
				userInfo.setRealName(realName);// 真实姓名
				userInfo.setCertificateNo(certNo);// 身份证号
				userInfo.setCertificateChecked(UserInfo.CERTIFICATE_NO);
				userInfo.setCgbBindBankCardState(UserInfo.CGB_BIND_CARD_NO);
				int j = userInfoDao.update(userInfo);
				if (j > 0) {
					LOG.info("用户信息修改成功");
				}
			}
		}

		if (bizType.equals("01")) {
			// 针对老客户需要重新开户 新增账户表数据
			if (cgbUserAccountDao.getUserAccountInfo(userId) == null) {
				// 生成客户账户
				CgbUserAccount userAccountInfo = new CgbUserAccount();
				System.out.println("用户注册插入客户表完成");
				userAccountInfo.setId(accountId);
				userAccountInfo.setUserId(userId);
				userAccountInfo.setTotalAmount(0d);
				userAccountInfo.setTotalInterest(0d);
				userAccountInfo.setAvailableAmount(0d);
				userAccountInfo.setFreezeAmount(0d);
				userAccountInfo.setRechargeAmount(0d);
				userAccountInfo.setRechargeCount(0);
				userAccountInfo.setCashAmount(0d);
				userAccountInfo.setCashCount(0);
				userAccountInfo.setCurrentAmount(0d);
				userAccountInfo.setRegularDuePrincipal(0d);
				userAccountInfo.setRegularDueInterest(0d);
				userAccountInfo.setRegularTotalAmount(0d);
				userAccountInfo.setRegularTotalInterest(0d);
				userAccountInfo.setCurrentTotalAmount(0d);
				userAccountInfo.setCurrentTotalInterest(0d);
				userAccountInfo.setCurrentYesterdayInterest(0d);
				userAccountInfo.setReguarYesterdayInterest(0d);
				// 同时生成客户账户
				cgbUserAccountDao.insert(userAccountInfo);
			}
		}

		// 对生成数据进行urlencoding
		String data = encryptRet.get("data");
		data = URLEncoder.encode(data, "UTF-8");
		String tm = encryptRet.get("tm");
		tm = URLEncoder.encode(tm, "UTF-8");
		encryptRet.put("data", data);
		encryptRet.put("tm", tm);
		encryptRet.put("merchantId", merchantId);
		// 返回订单信息
		return encryptRet;
	}

	/**
	 * 用户更换银行卡---pc端
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> changeBankCardWeb(String userId) throws Exception {

		String orderId = UUID.randomUUID().toString().replace("-", "");
		// 构造请求参数
		Map<String, String> params = new HashMap<String, String>();
		params.put("orderId", orderId);
		params.put("userId", userId);
		params.put("service", "web.p2p.member.card.change");
		params.put("method", "RSA");
		params.put("merchantId", merchantId);
		params.put("source", "1");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String requestTime = sdf.format(new Date());
		params.put("requestTime", requestTime);
		params.put("reqSn", UUID.randomUUID().toString().replace("-", ""));
		params.put("version", "1.0.0");
		params.put("returnUrl", ServerURLConfig.BACK_BACKTO_URL_WEB);
		params.put("callbackUrl", ServerURLConfig.BACK_CHANGECARD_URL);
		// 生成签名
		String sign = APIUtils.createSign(merchantRsaPrivateKey, params, "RSA");
		params.put("signature", sign);
		String jsonString = JSON.toJSONString(params);
		System.out.println("用户更换银行卡PC端Web[请求参数]" + jsonString);
		// 加密
		Map<String, String> encryptRet = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);

		// 对生成数据进行urlencoding
		String data = encryptRet.get("data");
		data = URLEncoder.encode(data, "UTF-8");
		String tm = encryptRet.get("tm");
		tm = URLEncoder.encode(tm, "UTF-8");
		encryptRet.put("data", data);
		encryptRet.put("tm", tm);
		encryptRet.put("merchantId", merchantId);
		// 返回订单信息
		return encryptRet;
	}

	/**
	 * 用户更换银行卡---H5
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> changeBankCardH5(String userId) throws Exception {

		String orderId = UUID.randomUUID().toString().replace("-", "");
		// 构造请求参数
		Map<String, String> params = new HashMap<String, String>();
		params.put("orderId", orderId);
		params.put("userId", userId);
		params.put("service", "h5.p2p.member.trade.changecard");
		params.put("method", "RSA");
		params.put("merchantId", merchantId);
		params.put("source", "1");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String requestTime = sdf.format(new Date());
		params.put("requestTime", requestTime);
		params.put("reqSn", UUID.randomUUID().toString().replace("-", ""));
		params.put("version", "1.0.0");
		params.put("returnUrl", ServerURLConfig.BACK_BACKTO_URL_WEB);
		params.put("callbackUrl", ServerURLConfig.BACK_CHANGECARD_URL);
		// 生成签名
		String sign = APIUtils.createSign(merchantRsaPrivateKey, params, "RSA");
		params.put("signature", sign);
		String jsonString = JSON.toJSONString(params);
		System.out.println("用户更换银行卡wap端[请求参数]" + jsonString);
		// 加密
		Map<String, String> encryptRet = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);

		// 对生成数据进行urlencoding
		String data = encryptRet.get("data");
		data = URLEncoder.encode(data, "UTF-8");
		String tm = encryptRet.get("tm");
		tm = URLEncoder.encode(tm, "UTF-8");
		encryptRet.put("data", data);
		encryptRet.put("tm", tm);
		encryptRet.put("merchantId", merchantId);
		// 返回订单信息
		return encryptRet;
	}

	/**
	 * 修改企业用户信息
	 * 
	 * @param supplierId
	 * @param issuer
	 * @param issuerName
	 * @param bankCity
	 * @param bankProvince
	 * @param bankCardName
	 * @param bankCardNo
	 * @param bankCode
	 * @param bankName
	 * @param corporationCertNo
	 * @param corporationCertType
	 * @param corporationName
	 * @param agentPersonCertNo
	 * @param agentPersonCertType
	 * @param agentPersonPhone
	 * @param agentPersonName
	 * @param orgCode
	 * @param taxRegCertNo
	 * @param bankPermitCertNo
	 * @param businessLicense
	 * @param businessLicenseType
	 * @param enterpriseFullName
	 * @param bizType
	 * @param id
	 * @return
	 * @throws Exception
	 * @throws IOException
	 */
	public Map<?, ?> updateenterprise(String id, String bizType, String enterpriseFullName, String businessLicenseType, String businessLicense, String bankPermitCertNo, String taxRegCertNo, String orgCode, String agentPersonName, String agentPersonPhone, String agentPersonCertType, String agentPersonCertNo, String corporationName, String corporationCertType, String corporationCertNo, String bankName, String bankCode, String bankCardNo, String bankCardName, String bankProvince, String bankCity, String issuerName, String issuer, String supplierId, String email, String registAddress) throws IOException, Exception {

		String orderId = UUID.randomUUID().toString().replace("-", "");
		WloanSubject wloanSubject = new WloanSubject();
		// 获取用户融资主体
		WloanSubject entity = new WloanSubject();
		entity.setLoanApplyId(id);
		List<WloanSubject> wloanSubjectList = wloanSubjectService.findList(entity);
		if (wloanSubjectList != null && wloanSubjectList.size() > 0) {
			wloanSubject = wloanSubjectList.get(0);
		}
		// 获取用户银行卡信息
		CgbUserBankCard userBankCard = new CgbUserBankCard();
		userBankCard.setUserId(id.trim());
		List<CgbUserBankCard> list = cgbUserBankCardDao.findCreditList(userBankCard);
		if (list != null && list.size() > 0) {
			userBankCard = list.get(0);
		}

		/*
		 * 构造请求参数
		 */
		Map<String, String> params = new HashMap<String, String>();
		params.put("orderId", orderId);
		params.put("userId", id);
		params.put("bizType", bizType);// 业务类型：02-借款户
		if (!StringUtils.isBlank(enterpriseFullName)) {
			params.put("enterpriseFullName", enterpriseFullName);// 企业全称
			wloanSubject.setCompanyName(enterpriseFullName);
		}
		if (!StringUtils.isBlank(businessLicenseType)) {
			params.put("businessLicenseType", businessLicenseType);// BLC-营业执照
			wloanSubject.setBusinessLicenseType(businessLicenseType);
		}
		if (!StringUtils.isBlank(taxRegCertNo)) {
			params.put("taxRegCertNo", taxRegCertNo);// 税务登记证
			wloanSubject.setTaxCode(taxRegCertNo);
		}
		if (!StringUtils.isBlank(orgCode)) {
			params.put("orgCode", orgCode);// 组织机构代码
			wloanSubject.setOrganNo(orgCode);
		}
		if (!StringUtils.isBlank(businessLicense)) {
			params.put("businessLicense", businessLicense);// 证照号
			wloanSubject.setBusinessNo(businessLicense);
		}
		if (!StringUtils.isBlank(bankPermitCertNo)) {
			params.put("bankPermitCertNo", bankPermitCertNo);// 银行开户许可证编号regionCode
			wloanSubject.setBankPermitCertNo(bankPermitCertNo);
		}
		if (!StringUtils.isBlank(agentPersonName)) {
			params.put("agentPersonName", agentPersonName);// 联系人姓名
			wloanSubject.setAgentPersonName(agentPersonName);
		}
		if (!StringUtils.isBlank(agentPersonPhone)) {
			params.put("agentPersonPhone", agentPersonPhone);// 联系人手机号
			wloanSubject.setAgentPersonPhone(agentPersonPhone);
		}
		if (!StringUtils.isBlank(corporationName)) {
			params.put("corporationName", corporationName);// 法人姓名
			wloanSubject.setLoanUser(corporationName);
		}
		if (!StringUtils.isBlank(corporationCertNo)) {
			params.put("corporationCertNo", corporationCertNo);// 法人证件号
			wloanSubject.setCorporationCertNo(corporationCertNo);
			wloanSubject.setLoanIdCard(corporationCertNo);
		}
		if (!StringUtils.isBlank(bankName)) {
			params.put("bankName", bankName);// 银行名称
			wloanSubject.setLoanBankName(bankName);
			userBankCard.setBankName(bankName);
		}
		if (!StringUtils.isBlank(bankCode)) {
			params.put("bankCode", bankCode);// 银行编码
			wloanSubject.setLoanBankCode(bankCode);
			userBankCard.setBankNo(bankCode);
		}
		if (!StringUtils.isBlank(bankCardNo)) {
			params.put("bankCardNo", bankCardNo);// 银行账号
			wloanSubject.setLoanBankNo(bankCardNo);
			userBankCard.setBankAccountNo(bankCardNo);
		}
		if (!StringUtils.isBlank(bankCardName)) {
			params.put("bankCardName", bankCardName);// 银行开户名
		}
		if (!StringUtils.isBlank(bankProvince)) {
			params.put("bankProvince", bankProvince);// 省
		}
		if (!StringUtils.isBlank(bankCity)) {
			params.put("bankCity", bankCity);// 市
		}
		if (!StringUtils.isBlank(issuerName)) {
			params.put("issuerName", issuerName);// 支行名称
		}
		if (!StringUtils.isBlank(issuer)) {
			params.put("issuer", issuer);// 支行-联行号
			wloanSubject.setLoanIssuer(issuer);
		}
		// 平台信息修改
		if (!StringUtils.isBlank(agentPersonCertType)) {
			params.put("agentPersonCertType", agentPersonCertType);
			wloanSubject.setAgentPersonCertType(agentPersonCertType);
		}
		if (!StringUtils.isBlank(agentPersonCertNo)) {
			params.put("agentPersonCertNo", agentPersonCertNo);
			wloanSubject.setAgentPersonCertNo(agentPersonCertNo);
		}
		params.put("agentPersonEmail", "");
		if (!StringUtils.isBlank(corporationCertType)) {
			params.put("corporationCertType", corporationCertType);
			wloanSubject.setCorporationCertType(corporationCertType);
		}
		params.put("corporationCell", "");
		params.put("corporationEmail", "");
		if (!StringUtils.isBlank(email)) {
			wloanSubject.setEmail(email);
		}
		if (!StringUtils.isBlank(registAddress)) {
			wloanSubject.setRegistAddress(registAddress);
		}
		wloanSubject.setUpdateDate(new Date());
		userBankCard.setState(UserBankCard.CERTIFY_NO);
		userBankCard.setUpdateDate(new Date());

		// 融资主体更新.
		int i = wloanSubjectDao.update(wloanSubject);
		if (i > 0) {
			LOG.info("融资主体更新成功");
		}
		// 银行卡信息更新.
		int q = cgbUserBankCardDao.update(userBankCard);
		if (q > 0) {
			LOG.info("银行卡信息更新成功");
		}

		params.put("service", "p2p.member.account.updateenterprise");
		params.put("method", "RSA");
		params.put("merchantId", merchantId);
		params.put("source", "1");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String requestTime = sdf.format(new Date());
		params.put("requestTime", requestTime);
		params.put("version", "1.0.0");
		params.put("reqSn", UUID.randomUUID().toString().replace("-", ""));
		params.put("callbackUrl", ServerURLConfig.BACK_UPDATEMEMBERACCOUNT_URL);
		// 生成签名
		String sign = APIUtils.createSign(merchantRsaPrivateKey, params, "RSA");
		params.put("signature", sign);
		String jsonString = JSON.toJSONString(params);
		System.out.println("修改企业用户信息[请求参数]" + jsonString);
		// 加密
		Map<String, String> encryptRet = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
		encryptRet.put("merchantId", merchantId);
		// 返回订单信息
		System.out.println(encryptRet);
		String url = ServerURLConfig.CGB_URL;
		String result = HttpUtil.sendPost(url, encryptRet);
		System.out.println("返回结果报文" + result);
		JSONObject jsonObject = JSONObject.parseObject(result);
		String respTm = (String) jsonObject.get("tm");
		String respData = (String) jsonObject.getString("data");
		String jsonRet = APIUtils.decryptDataBySSL(respTm, respData, merchantRsaPrivateKey);
		Map<String, String> maps = JSON.parseObject(jsonRet, new TypeReference<Map<String, String>>() {
		});
		System.out.println("解密结果:" + maps);
		if (maps.get("respCode").equals("00")) {
			LOG.info("企业用户信息修改，发送请求成功 ...");
		}
		return maps;
	}

	/**
	 * 出借人更换预留手机号---PC端
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> changeBankPhoneWeb(String userId) throws Exception {

		String orderId = UUID.randomUUID().toString().replace("-", "");
		// 构造请求参数
		Map<String, String> params = new HashMap<String, String>();
		params.put("orderId", orderId);
		params.put("userId", userId);
		params.put("service", "web.p2p.trade.phone.change");
		params.put("method", "RSA");
		params.put("merchantId", merchantId);
		params.put("source", "1");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String requestTime = sdf.format(new Date());
		params.put("requestTime", requestTime);
		params.put("reqSn", UUID.randomUUID().toString().replace("-", ""));
		params.put("version", "1.0.0");
		params.put("returnUrl", ServerURLConfig.BACK_BACKTO_URL_WEB);
		params.put("callbackUrl", ServerURLConfig.BACK_CHANGEPHONE_URL);
		// 生成签名
		String sign = APIUtils.createSign(merchantRsaPrivateKey, params, "RSA");
		params.put("signature", sign);
		String jsonString = JSON.toJSONString(params);
		System.out.println("用户更换预留手机号PC端Web[请求参数]" + jsonString);
		// 加密
		Map<String, String> encryptRet = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);

		// 对生成数据进行urlencoding
		String data = encryptRet.get("data");
		data = URLEncoder.encode(data, "UTF-8");
		String tm = encryptRet.get("tm");
		tm = URLEncoder.encode(tm, "UTF-8");
		encryptRet.put("data", data);
		encryptRet.put("tm", tm);
		encryptRet.put("merchantId", merchantId);
		// 返回订单信息
		return encryptRet;
	}

	/**
	 * 出借人更换预留手机号---H5
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> changeBankPhoneH5(String userId) throws Exception {

		String orderId = UUID.randomUUID().toString().replace("-", "");
		// 构造请求参数
		Map<String, String> params = new HashMap<String, String>();
		params.put("orderId", orderId);
		params.put("userId", userId);
		params.put("service", "h5.p2p.trade.phone.change");
		params.put("method", "RSA");
		params.put("merchantId", merchantId);
		params.put("source", "1");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String requestTime = sdf.format(new Date());
		params.put("requestTime", requestTime);
		params.put("reqSn", UUID.randomUUID().toString().replace("-", ""));
		params.put("version", "1.0.0");
		params.put("returnUrl", ServerURLConfig.BACK_BACKTO_URL_WEB);
		params.put("callbackUrl", ServerURLConfig.BACK_CHANGEPHONE_URL);
		// 生成签名
		String sign = APIUtils.createSign(merchantRsaPrivateKey, params, "RSA");
		params.put("signature", sign);
		String jsonString = JSON.toJSONString(params);
		System.out.println("用户更换预留手机号PC端Web[请求参数]" + jsonString);
		// 加密
		Map<String, String> encryptRet = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);

		// 对生成数据进行urlencoding
		String data = encryptRet.get("data");
		data = URLEncoder.encode(data, "UTF-8");
		String tm = encryptRet.get("tm");
		tm = URLEncoder.encode(tm, "UTF-8");
		encryptRet.put("data", data);
		encryptRet.put("tm", tm);
		encryptRet.put("merchantId", merchantId);
		// 返回订单信息
		return encryptRet;
	}

	/**
	 * 
	 * methods: webMemberAccountCreateEnterprise <br>
	 * description: 企业用户开户PC端WEB数据封装 <br>
	 * author: Mr.Roy <br>
	 * date: 2018年12月9日 下午8:03:36
	 * 
	 * @param creditUserInfo
	 * @return
	 * @throws Exception
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> webMemberAccountCreateEnterprise(CreditUserInfo creditUserInfo) throws Exception {

		// 构造请求参数
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", creditUserInfo.getId()); // 网贷平台统一用户编码
		if (CreditUserInfo.CREDIT_USER_TYPE_15.equals(creditUserInfo.getCreditUserType())) { // 借款户(抵押业务)
			params.put("bizType", "02");// 业务类型：02-借款户
		} else {
			params.put("bizType", creditUserInfo.getCreditUserType());// 业务类型：02-借款户
		}
		params.put("enterpriseFullName", creditUserInfo.getEnterpriseFullName()); // 企业全称
		params.put("businessLicenseType", creditUserInfo.getWloanSubject().getBusinessLicenseType());// BLC-营业执照，USCC-同意社会信用代码
		if ("BLC".equals(creditUserInfo.getWloanSubject().getBusinessLicenseType())) { // 证照类型为营业执照时
			params.put("taxRegCertNo", creditUserInfo.getWloanSubject().getTaxCode()); // 税务登记证
			params.put("orgCode", creditUserInfo.getWloanSubject().getOrganNo()); // 组织机构代码
		}
		params.put("businessLicense", creditUserInfo.getWloanSubject().getBusinessNo()); // 证照号
		params.put("bankPermitCertNo", creditUserInfo.getWloanSubject().getBankPermitCertNo()); // 银行开户许可证编号
		params.put("regionCode", "086"); // 国家区域码，中国：086
		params.put("agentPersonName", creditUserInfo.getWloanSubject().getAgentPersonName()); // 联系人姓名
		params.put("agentPersonPhone", creditUserInfo.getWloanSubject().getAgentPersonPhone()); // 联系人手机号
		params.put("agentPersonCertType", creditUserInfo.getWloanSubject().getAgentPersonCertType());// 联系人证件类型，IDC-身份证，GAT-港澳台身份证，MILIARY-军官证，PASS-PORT-护照
		params.put("agentPersonCertNo", creditUserInfo.getWloanSubject().getAgentPersonCertNo()); // 联系人证件号
		params.put("agentPersonEmail", creditUserInfo.getWloanSubject().getEmail()); // 联系人邮箱
		params.put("corporationName", creditUserInfo.getWloanSubject().getLoanUser()); // 法人姓名
		params.put("corporationCertType", creditUserInfo.getWloanSubject().getCorporationCertType()); // 法人证件类型，IDC-身份证，GAT-港澳台身份证，MILIARY-军官证，PASS-PORT-护照
		params.put("corporationCertNo", creditUserInfo.getWloanSubject().getCorporationCertNo()); // 法人证件号
		params.put("bankName", creditUserInfo.getWloanSubject().getLoanBankName()); // 银行名称
		/**
		 * 查询银行编码对照表中的银行卡信息
		 */
		CicmorganBankCode cicmorganBankCode = cicmorganBankCodeService.get(creditUserInfo.getWloanSubject().getLoanBankCode()); // 根据银行编码对照表的主键ID，查询银行编码
		if (cicmorganBankCode != null) {
			params.put("bankCode", cicmorganBankCode.getBankCode()); // 银行编码
		}
		params.put("bankCardNo", creditUserInfo.getWloanSubject().getLoanBankNo()); // 银行账号
		params.put("bankCardName", creditUserInfo.getEnterpriseFullName()); // 银行开户名
		params.put("bankProvince", creditUserInfo.getWloanSubject().getLoanBankProvince()); // 省
		params.put("bankCity", creditUserInfo.getWloanSubject().getLoanBankCity()); // 市
		params.put("issuerName", creditUserInfo.getWloanSubject().getLoanIssuerName()); // 支行名称
		params.put("issuer", creditUserInfo.getWloanSubject().getLoanIssuer()); // 支行-联行号
		params.put("service", "web.member.account.createenterprise");
		params.put("method", "RSA");
		params.put("merchantId", merchantId);
		params.put("source", "1");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String requestTime = sdf.format(new Date());
		params.put("requestTime", requestTime);
		params.put("version", "1.0.0");
		params.put("reqSn", UUID.randomUUID().toString().replace("-", ""));
		params.put("returnUrl", ServerURLConfig.BACK_BACKTO_URL_BORROWWEB + "&id=" + creditUserInfo.getId());
		params.put("callbackUrl", ServerURLConfig.BACK_ACCOUNT_URL_COMPANY);
		// 生成签名
		String sign = APIUtils.createSign(merchantRsaPrivateKey, params, "RSA");
		params.put("signature", sign);
		String jsonString = JSON.toJSONString(params);
		LOG.info("企业用户开户PC端WEB-请求参数：\t" + jsonString);
		// 加密
		Map<String, String> encryptRet = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
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
				if (newCreditUserInfoUpdateFlag == 1) {
					LOG.info("fn:webMemberAccountCreateEnterprise：\t企业帐号信息更新成功 ...");
				} else {
					LOG.info("fn:webMemberAccountCreateEnterprise：\t企业帐号信息更新成功 ...");
				}
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
					creditUserBank.setState(CgbUserBankCard.CERTIFY_FAIL); // 由于请求数据会跳转至存管页面，发现用户有不填写的情况，所以在数据请求之前，永远是未开户状态.
					if (cicmorganBankCode != null) {
						creditUserBank.setBankNo(cicmorganBankCode.getBankCode()); // 银行编码
					}
					creditUserBank.setUpdateDate(new Date()); // 更新时间
					int creditUserBankUpdateFlag = cgbUserBankCardDao.update(creditUserBank);
					if (creditUserBankUpdateFlag == 1) {
						LOG.info("fn:webMemberAccountCreateEnterprise：\t企业银行卡信息更新成功 ...");
					} else {
						LOG.info("fn:webMemberAccountCreateEnterprise：\t企业银行卡信息更新失败 ...");
					}
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
					newCgbUserBankCard.setState(CgbUserBankCard.CERTIFY_FAIL); // 由于请求数据会跳转至存管页面，发现用户有不填写的情况，所以在数据请求之前，永远是未开户状态.
					newCgbUserBankCard.setBindDate(new Date()); // 绑卡时间
					newCgbUserBankCard.setCreateDate(new Date()); // 创建时间
					newCgbUserBankCard.setUpdateDate(new Date()); // 更新时间
					int newCgbUserBankCardInsertFlag = cgbUserBankCardDao.insert(newCgbUserBankCard);
					if (newCgbUserBankCardInsertFlag == 1) {
						LOG.info("fn:webMemberAccountCreateEnterprise：\t企业银行卡信息新增成功 ...");
					} else {
						LOG.info("fn:webMemberAccountCreateEnterprise：\t企业银行卡信息新增失败 ...");
					}
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
							if (subjectUpdateFlag == 1) {
								LOG.info("fn:webMemberAccountCreateEnterprise：\t借款企业融资主体更新成功 ...");
							} else {
								LOG.info("fn:webMemberAccountCreateEnterprise：\t借款企业融资主体更新失败 ...");
							}
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
					if (newSubjectInsertFlag == 1) {
						LOG.info("fn:webMemberAccountCreateEnterprise：\t借款企业融资主体新增成功 ...");
					} else {
						LOG.info("fn:webMemberAccountCreateEnterprise：\t借款企业融资主体新增失败 ...");
					}
				}
			}
		}
		// 对生成数据进行URL，Encoding
		String data = encryptRet.get("data");
		data = URLEncoder.encode(data, "UTF-8");
		String tm = encryptRet.get("tm");
		tm = URLEncoder.encode(tm, "UTF-8");
		encryptRet.put("data", data);
		encryptRet.put("tm", tm);
		encryptRet.put("merchantId", merchantId);
		// 封装企业开户信息成功
		return encryptRet;
	}

	/**
	 * 
	 * methods: p2pMemberAccountUpdateEnterprise <br>
	 * description: 企业用户信息修改 <br>
	 * author: Mr.Roy <br>
	 * date: 2018年12月9日 下午9:31:30
	 * 
	 * @param creditUserInfo
	 * @return
	 * @throws Exception
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> p2pMemberAccountUpdateEnterprise(CreditUserInfo creditUserInfo) throws Exception {

		// 构造请求参数
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", creditUserInfo.getId()); // 网贷平台统一用户编码
		String orderId = UUID.randomUUID().toString().replace("-", "");
		params.put("orderId", orderId); // 由网贷平台生成的唯一的交易流水号
		if (CreditUserInfo.CREDIT_USER_TYPE_15.equals(creditUserInfo.getCreditUserType())) { // 借款户(抵押业务)
			params.put("bizType", "02");// 业务类型：02-借款户
		} else {
			params.put("bizType", creditUserInfo.getCreditUserType());// 业务类型：02-借款户
		}
		params.put("enterpriseFullName", creditUserInfo.getEnterpriseFullName()); // 企业全称
		params.put("businessLicenseType", creditUserInfo.getWloanSubject().getBusinessLicenseType());// BLC-营业执照，USCC-同意社会信用代码
		if ("BLC".equals(creditUserInfo.getWloanSubject().getBusinessLicenseType())) { // 证照类型为营业执照时
			params.put("taxRegCertNo", creditUserInfo.getWloanSubject().getTaxCode()); // 税务登记证
			params.put("orgCode", creditUserInfo.getWloanSubject().getOrganNo()); // 组织机构代码
		}
		params.put("businessLicense", creditUserInfo.getWloanSubject().getBusinessNo()); // 证照号
		params.put("bankPermitCertNo", creditUserInfo.getWloanSubject().getBankPermitCertNo()); // 银行开户许可证编号
		params.put("regionCode", "086"); // 国家区域码，中国：086
		params.put("agentPersonName", creditUserInfo.getWloanSubject().getAgentPersonName()); // 联系人姓名
		params.put("agentPersonPhone", creditUserInfo.getWloanSubject().getAgentPersonPhone()); // 联系人手机号
		params.put("agentPersonCertType", creditUserInfo.getWloanSubject().getAgentPersonCertType());// 联系人证件类型，IDC-身份证，GAT-港澳台身份证，MILIARY-军官证，PASS-PORT-护照
		params.put("agentPersonCertNo", creditUserInfo.getWloanSubject().getAgentPersonCertNo()); // 联系人证件号
		params.put("agentPersonEmail", creditUserInfo.getWloanSubject().getEmail()); // 联系人邮箱
		params.put("corporationName", creditUserInfo.getWloanSubject().getLoanUser()); // 法人姓名
		params.put("corporationCertType", creditUserInfo.getWloanSubject().getCorporationCertType()); // 法人证件类型，IDC-身份证，GAT-港澳台身份证，MILIARY-军官证，PASS-PORT-护照
		params.put("corporationCertNo", creditUserInfo.getWloanSubject().getCorporationCertNo()); // 法人证件号
		params.put("bankName", creditUserInfo.getWloanSubject().getLoanBankName()); // 银行名称
		/**
		 * 查询银行编码对照表中的银行卡信息
		 */
		CicmorganBankCode cicmorganBankCode = cicmorganBankCodeService.get(creditUserInfo.getWloanSubject().getLoanBankCode()); // 根据银行编码对照表的主键ID，查询银行编码
		if (cicmorganBankCode != null) {
			params.put("bankCode", cicmorganBankCode.getBankCode()); // 银行编码
		}
		params.put("bankCardNo", creditUserInfo.getWloanSubject().getLoanBankNo()); // 银行账号
		params.put("bankCardName", creditUserInfo.getEnterpriseFullName()); // 银行开户名
		params.put("bankProvince", creditUserInfo.getWloanSubject().getLoanBankProvince()); // 省
		params.put("bankCity", creditUserInfo.getWloanSubject().getLoanBankCity()); // 市
		params.put("issuerName", creditUserInfo.getWloanSubject().getLoanIssuerName()); // 支行名称
		params.put("issuer", creditUserInfo.getWloanSubject().getLoanIssuer()); // 支行-联行号
		params.put("service", "p2p.member.account.updateenterprise");
		params.put("method", "RSA");
		params.put("merchantId", merchantId);
		params.put("source", "1");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String requestTime = sdf.format(new Date());
		params.put("requestTime", requestTime);
		params.put("reqSn", UUID.randomUUID().toString().replace("-", ""));
		params.put("version", "1.0.0");
		params.put("callbackUrl", ServerURLConfig.BACK_UPDATEMEMBERACCOUNT_URL);
		// 生成签名
		String sign = APIUtils.createSign(merchantRsaPrivateKey, params, "RSA");
		params.put("signature", sign);
		String jsonString = JSON.toJSONString(params);
		LOG.info("企业用户开户PC端WEB-请求参数：\t" + jsonString);
		// 加密
		Map<String, String> encryptRet = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
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
				if (newCreditUserInfoUpdateFlag == 1) {
					LOG.info("fn:p2pMemberAccountUpdateEnterprise：\t企业帐号信息更新成功 ...");
				} else {
					LOG.info("fn:p2pMemberAccountUpdateEnterprise：\t企业帐号信息更新成功 ...");
				}
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
					creditUserBank.setState(UserBankCard.CERTIFY_NO); // 未认证
					if (cicmorganBankCode != null) {
						creditUserBank.setBankNo(cicmorganBankCode.getBankCode()); // 银行编码
					}
					creditUserBank.setUpdateDate(new Date()); // 更新时间
					int creditUserBankUpdateFlag = cgbUserBankCardDao.update(creditUserBank);
					if (creditUserBankUpdateFlag == 1) {
						LOG.info("fn:p2pMemberAccountUpdateEnterprise：\t企业银行卡信息更新成功 ...");
					} else {
						LOG.info("fn:p2pMemberAccountUpdateEnterprise：\t企业银行卡信息更新失败 ...");
					}
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
					newCgbUserBankCard.setState(UserBankCard.CERTIFY_NO); // 新增时未认证
					newCgbUserBankCard.setBindDate(new Date()); // 绑卡时间
					newCgbUserBankCard.setCreateDate(new Date()); // 创建时间
					newCgbUserBankCard.setUpdateDate(new Date()); // 更新时间
					int newCgbUserBankCardInsertFlag = cgbUserBankCardDao.insert(newCgbUserBankCard);
					if (newCgbUserBankCardInsertFlag == 1) {
						LOG.info("fn:p2pMemberAccountUpdateEnterprise：\t企业银行卡信息新增成功 ...");
					} else {
						LOG.info("fn:p2pMemberAccountUpdateEnterprise：\t企业银行卡信息新增失败 ...");
					}
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
								subject.setLoanPhone(newCreditUserInfo.getPhone()); // 借款人帐号.
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
							if (subjectUpdateFlag == 1) {
								LOG.info("fn:p2pMemberAccountUpdateEnterprise：\t借款企业融资主体更新成功 ...");
							} else {
								LOG.info("fn:p2pMemberAccountUpdateEnterprise：\t借款企业融资主体更新失败 ...");
							}
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
					if (newSubjectInsertFlag == 1) {
						LOG.info("fn:p2pMemberAccountUpdateEnterprise：\t借款企业融资主体新增成功 ...");
					} else {
						LOG.info("fn:p2pMemberAccountUpdateEnterprise：\t借款企业融资主体新增失败 ...");
					}
				}
			}
		}
		/**
		 * HTTP.
		 */
		encryptRet.put("merchantId", merchantId);
		String requestJsonStr = JSON.toJSONString(encryptRet);
		LOG.info("fn:p2pMemberAccountUpdateEnterprise-请求：\t" + requestJsonStr);
		String url = ServerURLConfig.CGB_URL;
		String responseStr = HttpUtil.sendPost(url, encryptRet);
		LOG.info("fn:p2pMemberAccountUpdateEnterprise-响应：\t" + responseStr);
		/**
		 * 解析响应.
		 */
		JSONObject jsonObject = JSONObject.parseObject(responseStr);
		String tm = (String) jsonObject.get("tm");
		String data = (String) jsonObject.getString("data");
		String jsonRet = APIUtils.decryptDataBySSL(tm, data, merchantRsaPrivateKey);
		Map<String, String> map = JSON.parseObject(jsonRet, new TypeReference<Map<String, String>>() {
		});
		LOG.info("fn:p2pMemberAccountUpdateEnterprise-解析响应：\t" + JSON.toJSONString(map));
		// 封装企业开户信息成功
		return map;
	}
}