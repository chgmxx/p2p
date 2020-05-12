package com.power.platform.lanmao.account.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.power.platform.cgb.dao.CgbUserAccountDao;
import com.power.platform.cgb.dao.CgbUserBankCardDao;
import com.power.platform.cgb.entity.CgbUserAccount;
import com.power.platform.cgb.entity.CgbUserBankCard;
import com.power.platform.cgb.service.CicmorganBankCodeService;
import com.power.platform.cgbpay.config.ServerURLConfig;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.IdcardUtils;
import com.power.platform.credit.dao.userinfo.CreditUserInfoDao;
import com.power.platform.credit.service.supplierToMiddlemen.CreditSupplierToMiddlemenService;
import com.power.platform.credit.service.userinfo.CreditUserInfoService;
import com.power.platform.ifcert.dao.LendProductDao;
import com.power.platform.lanmao.common.AppUtil;
import com.power.platform.lanmao.config.RedirectUrlConfig;
import com.power.platform.lanmao.dao.LmTransactionDao;
import com.power.platform.lanmao.entity.LmTransaction;
import com.power.platform.lanmao.type.AuthEnum;
import com.power.platform.lanmao.type.BankCodeEnum;
import com.power.platform.lanmao.type.CheckEnum;
import com.power.platform.lanmao.type.CheckTypeEnum;
import com.power.platform.lanmao.type.IdCardTypeEnum;
import com.power.platform.lanmao.type.ServiceNameEnum;
import com.power.platform.lanmao.type.UserLimitEnum;
import com.power.platform.lanmao.type.UserRoleEnum;
import com.power.platform.pay.cash.dao.UserCashDao;
import com.power.platform.pay.recharge.dao.UserRechargeDao;
import com.power.platform.regular.dao.WloanSubjectDao;
import com.power.platform.userinfo.dao.UserAccountInfoDao;
import com.power.platform.userinfo.dao.UserBankCardDao;
import com.power.platform.userinfo.dao.UserInfoDao;
import com.power.platform.userinfo.entity.UserBankCard;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.weixin.service.WeixinSendTempMsgService;
import com.power.platform.regular.service.WloanSubjectService;
import com.power.platform.trandetail.dao.UserTransDetailDao;

@Service("personBindCardService")
public class PersonBindCardService {

	private static final Logger log = LoggerFactory.getLogger(PersonBindCardService.class);

	@Resource
	private LendProductDao lendProductDao;

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
	@Autowired
	private LmTransactionDao lmTransactionDao;

	/**
	 * @description:个人绑卡注册
	 * @param bankCardNo
	 * <br>
	 *            银行卡号
	 * @param from
	 * <br>
	 *            请求平台.
	 * @param certNo
	 * <br>
	 *            身份证号
	 * @param realName
	 * <br>
	 *            真实姓名
	 * @param bankCardPhone
	 * <br>
	 *            银行预留手机号.
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public Map<String, String> cardRegister(UserInfo userInfo, String bankCardNo, String certNo, String realName,String bankCardPhone,String orderId) {

		// 定义reqData参数集合
		Map<String, String> reqData = new HashMap<String, String>();
		Map<String, String> result = new HashMap<String, String>();
		try {
//			String orderId = UUID.randomUUID().toString().replace("-", "");
			System.out.println("orderId = " + orderId);
			reqData.put("platformUserNo", userInfo.getId());
			reqData.put("requestNo", orderId);
			reqData.put("realName", realName);
			reqData.put("idCardType", IdCardTypeEnum.PRC_ID.getValue());
			reqData.put("userRole", UserRoleEnum.INVESTOR.getValue());
			reqData.put("idCardNo", certNo);
			reqData.put("mobile", bankCardPhone);
			reqData.put("bankcardNo", bankCardNo);
			reqData.put("checkType", CheckTypeEnum.LIMIT.getValue());
			reqData.put("redirectUrl", RedirectUrlConfig.PERSONAL_REGISTER_EXPAND_URL);
			reqData.put("userLimitType", UserLimitEnum.ID_CARD_NO_UNIQUE.getValue());
			reqData.put("authList", AuthEnum.TENDER.getValue());
			reqData.put("timestamp", DateUtils.getDateStr());
			reqData.put("amount", "1000000.00"); // 授权还款金额1000000.00万每笔
			reqData.put("failTime", DateUtils.formatDate(DateUtils.yearAddNum(new Date(), 3), "yyyyMMdd")); // 授权截至时间，默认三年
			LmTransaction lt = new LmTransaction();
			lt.setPlatformUserNo(userInfo.getId());
			lt.setServiceName(ServiceNameEnum.PERSONAL_REGISTER_EXPAND.getValue());
			lt.setRequestNo(orderId);
			lt.setCreateDate(new Date());
			lt.setUpdateDate(new Date());
			lt.setId(orderId);
			lt.setStatus(LmTransaction.PROCESSING);
			int lm = lmTransactionDao.insert(lt);
			log.info("懒猫交易留存插入:{}", lm == 1 ? "成功" : "失败");
			result = AppUtil.generatePostParam(ServiceNameEnum.PERSONAL_REGISTER_EXPAND.getValue(), reqData);
			
			//查询管保数据
			CgbUserBankCard userBankCard = new CgbUserBankCard();
			userBankCard.setUserId(userInfo.getId());
//			userBankCard.setState(CgbUserBankCard.CERTIFY_YES);
			List<CgbUserBankCard> list = cgbUserBankCardDao.findList(userBankCard);
			if (list != null&& list.size()!=0) {
				//确保存管保数据唯一
				for (CgbUserBankCard bankCard : list) {
//					int i = cgbUserBankCardDao.physicallyDeleted(bankCard);
//					bankCard.setDelFlag("1");
//					int i = cgbUserBankCardDao.update(bankCard);
					int i = cgbUserBankCardDao.delete(bankCard);
					if (i > 0) {
						log.info("用户银行卡逻辑删除成功,进行开户");
					}
				}
			}
			// N2.插入新的绑卡信息
			userBankCard.setId(IdGen.uuid());
			userBankCard.setAccountId(userInfo.getAccountId());
			userBankCard.setUserId(userInfo.getId());
			userBankCard.setBankAccountNo(bankCardNo);
			userBankCard.setBankCardPhone(bankCardPhone);
			userBankCard.setBeginBindDate(new Date());
			userBankCard.setIsDefault(UserBankCard.DEFAULT_YES);
			userBankCard.setState(CgbUserBankCard.CERTIFY_NO);
			userBankCard.setBindDate(new Date());
			userBankCard.setCreateDate(new Date());
			userBankCard.setUpdateDate(new Date());
			int i = cgbUserBankCardDao.insert(userBankCard);
			if (i > 0) {
				log.info("银行卡信息新增成功");
			}
			return result;
		} catch (Exception e) {
			e.getStackTrace();
			return result;
		}
	}

	/**
	 * @description:个人绑卡注册回调
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public void callbackCardRegister(JSONObject json) {

		// 出借人ID.
		String userId = json.getString("platformUserNo");
		String orderId = json.getString("requestNo");
		UserInfo userInfo = userInfoDao.getCgb(userId);
		if (null == userInfo) {
			log.info("出借人用户不存在");
			return;
		}
		// N1.先查询用户是否有绑卡,有则进行删除
		CgbUserBankCard userBankCard = new CgbUserBankCard();
		userBankCard.setUserId(userInfo.getId());
		List<CgbUserBankCard> list = cgbUserBankCardDao.findList(userBankCard);
		if (list != null) {
			for (CgbUserBankCard bankCard : list) {
//				int i = cgbUserBankCardDao.physicallyDeleted(bankCard);
//				if (i > 0) {
//					log.info("用户银行卡删除成功,进行开户");
//				}
				bankCard.setDelFlag("1");
				int i = cgbUserBankCardDao.update(bankCard);
				if (i > 0) {
					log.info("用户银行卡逻辑删除成功,进行开户");
				}
			}
		}
		// N2.插入新的绑卡信息
		userBankCard.setAccountId(userInfo.getAccountId());
		userBankCard.setUserId(userInfo.getId());
		userBankCard.setBankAccountNo(json.getString("bankcardNo"));
		userBankCard.setBankCardPhone(json.getString("mobile"));
		userBankCard.setBankNo(json.getString("bankcode").toString());
		userBankCard.setBankName(BankCodeEnum.getTextByValue(json.getString("bankcode")));
		userBankCard.setBeginBindDate(new Date());
		userBankCard.setId(orderId);
		userBankCard.setIsDefault(UserBankCard.DEFAULT_YES);
		userBankCard.setState(CgbUserBankCard.CERTIFY_YES);
		userBankCard.setBindDate(new Date());
		userBankCard.setCreateDate(new Date());
		userBankCard.setUpdateDate(new Date());
		int i = cgbUserBankCardDao.insert(userBankCard);
		if (i > 0) {
			log.info("银行卡信息新增成功");
		}
		// N3.用户信息保存
		userInfo.setRealName(json.getString("realName"));// 真实姓名
		userInfo.setCertificateNo(json.getString("idCardNo"));// 身份证号
		userInfo.setCertificateChecked(UserInfo.CERTIFICATE_YES);
		if("PRC_ID".equals(json.getString("idCardType"))) {//身份证
			userInfo.setUserType(userInfo.PRC_ID);
		}else if("PASSPORT".equals(json.getString("idCardType"))) {//护照
			userInfo.setUserType(userInfo.PASSPORT);
		}else if("COMPATRIOTS_CARD".equals(json.getString("idCardType"))) {//港澳台通行证
			userInfo.setUserType(userInfo.COMPATRIOTS_CARD);
		}else {//外国人永久居留证
			userInfo.setUserType(userInfo.PERMANENT_RESIDENCE);
		}
		userInfo.setIsActivate("TRUE");// 懒猫2.0迁移，会员激活标识，TRUE、FALSE
		userInfo.setCgbBindBankCardState(UserInfo.CGB_BIND_CARD_YES);
		userInfo.setBindBankCardState(UserInfo.BIND_CARD_YES);
		String birthday = IdcardUtils.getBirthByIdCard(json.getString("idCardNo"));
		userInfo.setBirthday(birthday.substring(4, birthday.length())); // 客户生日设置.
		int j = userInfoDao.update(userInfo);
		if (j > 0) {
			log.info("用户信息修改成功");
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
		// 查询流水表
		LmTransaction lm = new LmTransaction();
		lm.setServiceName(ServiceNameEnum.PERSONAL_REGISTER_EXPAND.getValue());
		lm.setPlatformUserNo(userId);
		lm.setRequestNo(orderId);
		List<LmTransaction> ltList = lmTransactionDao.findList(lm);
		if (ltList != null && ltList.size() > 0) {
			LmTransaction lt = ltList.get(0);
			lt.setUpdateDate(new Date());
			lt.setCode(json.getString("code"));
			lt.setStatus(json.getString("status"));
			lt.setAuditStatus(json.getString("auditStatus"));
			// 鉴权通过类型
			lt.setAccessType(json.getString("accessType"));
			lt.setErrorCode(json.getString("errorCode"));
			lt.setErrorMessage(json.getString("errorMessage"));
			int m = lmTransactionDao.update(lt);
			if (m > 0) {
				log.info("个人绑卡注册流水号修改生成成功");
			}
		}
	}

	/**
	 * @description:个人绑卡
	 * @param userId
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> changeBankCard(UserInfo userInfo, String bankCardNo, String certNo, String realName, String bankCardPhone, String orderId) throws Exception {

		// 定义reqData参数集合
		Map<String, String> reqData = new HashMap<String, String>();
		Map<String, String> result = new HashMap<String, String>();

		try {
//			String orderId = UUID.randomUUID().toString().replace("-", "");
			System.out.println("orderId = " + orderId);

			reqData.put("platformUserNo", userInfo.getId());
			reqData.put("requestNo", orderId);
			reqData.put("mobile", bankCardPhone);
			reqData.put("bankcardNo", bankCardNo);
			reqData.put("checkType", CheckTypeEnum.LIMIT.getValue());
			reqData.put("redirectUrl", RedirectUrlConfig.PERSONAL_BIND_BANKCARD_EXPAND);
			reqData.put("timestamp", DateUtils.getDateStr());
			// reqData.put("amount", "1000000.00"); // 授权还款金额1000000.00万每笔
			// reqData.put("failTime", DateUtils.formatDate(DateUtils.yearAddNum(new Date(), 3), "yyyyMMdd")); // 授权截至时间，默认三年
			result = AppUtil.generatePostParam(ServiceNameEnum.PERSONAL_BIND_BANKCARD_EXPAND.getValue(), reqData);

			CgbUserBankCard userBankCard = new CgbUserBankCard();
			userBankCard.setUserId(userInfo.getId());
			userBankCard.setState(CgbUserBankCard.CERTIFY_NO);
			List<CgbUserBankCard> list = cgbUserBankCardDao.findList(userBankCard);
			if (list != null&& list.size()!=0) {
				//确保存管保数据唯一
				CgbUserBankCard cubc = list.get(0);
				// N2.插入新的绑卡信息
				cubc.setBankAccountNo(bankCardNo);
				cubc.setBankCardPhone(bankCardPhone);
				cubc.setUpdateDate(new Date());
				int i = cgbUserBankCardDao.update(cubc);
				log.info("银行卡信息更新成功:{}", i == 1 ? "成功" : "失败");
			}
			
			LmTransaction lt = new LmTransaction();
			lt.setPlatformUserNo(userInfo.getId());
			lt.setServiceName(ServiceNameEnum.PERSONAL_BIND_BANKCARD_EXPAND.getValue());
			lt.setRequestNo(orderId);
			lt.setCreateDate(new Date());
			lt.setUpdateDate(new Date());
			lt.setId(orderId);
			lt.setStatus(LmTransaction.PROCESSING);
			int lm = lmTransactionDao.insert(lt);
			log.info("懒猫交易留存插入:{}", lm == 1 ? "成功" : "失败");
		} catch (Exception e) {
			e.getMessage();
		}
		return result;
	}

	/**
	 * @description:个人绑卡回调
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public void callChangeBankCard(JSONObject json) {

		// 出借人ID.
		String userId = json.getString("platformUserNo");
		String orderId = json.getString("requestNo");
		UserInfo userInfo = userInfoDao.getCgb(userId);

		// N1.先查询用户是否有绑卡
		CgbUserBankCard userBankCard = new CgbUserBankCard();
		userBankCard.setUserId(userInfo.getId());
		List<CgbUserBankCard> list = cgbUserBankCardDao.findList(userBankCard);
		// 必有一条记录
		CgbUserBankCard cbc = list.get(0);
		// N2.唯一的银行卡
		cbc.setAccountId(userInfo.getAccountId());
		cbc.setUserId(userInfo.getId());
		cbc.setBankAccountNo(json.getString("bankcardNo"));
		cbc.setBankCardPhone(json.getString("mobile"));
		cbc.setBankNo(json.getString("bankcode").toString());
		cbc.setBankName(BankCodeEnum.getTextByValue(json.getString("bankcode")));
		cbc.setBeginBindDate(new Date());
		cbc.setState(CgbUserBankCard.CERTIFY_YES);
		cbc.setBindDate(new Date());
		cbc.setUpdateDate(new Date());
		int i = cgbUserBankCardDao.update(cbc);
		if (i > 0) {
			log.info("银行卡信息修改成功");
		}
		// N3.用户信息保存
		userInfo.setCertificateChecked(UserInfo.CERTIFICATE_YES);
		userInfo.setCgbBindBankCardState(UserInfo.CGB_BIND_CARD_YES);
		userInfo.setBindBankCardState(UserInfo.BIND_CARD_YES);
		// String birthday = IdcardUtils.getBirthByIdCard(json.getString("idCardNo"));--没有返回身份证号
		// userInfo.setBirthday(birthday.substring(4, birthday.length())); // 客户生日设置.
		int j = userInfoDao.update(userInfo);
		if (j > 0) {
			log.info("用户信息修改成功");
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
		// 查询流水表
		LmTransaction lm = new LmTransaction();
		lm.setServiceName(ServiceNameEnum.PERSONAL_BIND_BANKCARD_EXPAND.getValue());
		lm.setPlatformUserNo(userId);
		lm.setRequestNo(orderId);
		List<LmTransaction> ltList = lmTransactionDao.findList(lm);
		if (ltList != null && ltList.size() > 0) {
			LmTransaction lt = ltList.get(0);
			lt.setUpdateDate(new Date());
			lt.setCode(json.getString("code"));
			lt.setStatus(json.getString("status"));
			// 鉴权通过类型
			lt.setAccessType(json.getString("accessType"));
			lt.setErrorCode(json.getString("errorCode"));
			lt.setErrorMessage(json.getString("errorMessage"));
			int m = lmTransactionDao.update(lt);
			if (m > 0) {
				log.info("个人绑卡流水号修改成功");
			}
		}

	}

	/**
	 * @description:解绑银行卡
	 * @param userId
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public Map<String, String> untyingCard(String userId,String orderId) {

		// 定义reqData参数集合
		Map<String, String> reqData = new HashMap<String, String>();
		Map<String, String> result = new HashMap<String, String>();
		try {
//			String orderId = UUID.randomUUID().toString().replace("-", "");
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
			log.info("懒猫交易留存插入:{}", lm == 1 ? "成功" : "失败");
		} catch (Exception e) {
			e.getStackTrace();
		}

		return result;
	}

	/**
	 * @description:解绑银行卡回调
	 * @param userId
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public void callbackUntyingCard(JSONObject json, UserInfo userInfo) {

		String userId = json.getString("platformUserNo");
		String orderId = json.getString("requestNo");
		CgbUserBankCard userBankCard = new CgbUserBankCard();
		userBankCard.setUserId(userId);
		List<CgbUserBankCard> list = cgbUserBankCardDao.findList(userBankCard);
		if (list != null) {
			CgbUserBankCard cbk = list.get(0);
			cbk.setState(CgbUserBankCard.CERTIFY_NO);
//			cbk.setBankAccountNo(null); // 银行账户
//			cbk.setBankName(null);
//			cbk.setBankNo(null); // 银行编码
			cbk.setUpdateDate(new Date()); // 更新时间
			int i = cgbUserBankCardDao.update(cbk);
			if (i > 0) {
				log.info("银行卡信息修改成功");
			}
		}
		// N3.用户信息保存
		userInfo.setCgbBindBankCardState(UserInfo.CGB_BIND_CARD_NO);
		userInfo.setBindBankCardState(UserInfo.BIND_CARD_NO);
		// userInfo.setBirthday(null); // 客户生日设置.
		int j = userInfoDao.update(userInfo);
		if (j > 0) {
			log.info("用户信息修改成功");
		}
		// 查询流水表
		LmTransaction lm = new LmTransaction();
		lm.setServiceName(ServiceNameEnum.UNBIND_BANKCARD.getValue());
		lm.setPlatformUserNo(userId);
		lm.setRequestNo(orderId);
		List<LmTransaction> ltList = lmTransactionDao.findList(lm);
		if (ltList != null && ltList.size() > 0) {
			LmTransaction lt = ltList.get(0);
			lt.setUpdateDate(new Date());
			lt.setCode(json.getString("code"));
			lt.setStatus(json.getString("status"));
			lt.setErrorCode(json.getString("errorCode"));
			lt.setErrorMessage(json.getString("errorMessage"));
			int m = lmTransactionDao.update(lt);
			if (m > 0) {
				log.info("个人用户解绑银行卡流水号修改成功");
			}
		}
	}

	/**
	 * @description:修改密码
	 * @param userId
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public Map<String, String> resetPassword(String userId) {

		// 定义reqData参数集合
		Map<String, String> reqData = new HashMap<String, String>();
		Map<String, String> result = new HashMap<String, String>();
		try {
			String orderId = UUID.randomUUID().toString().replace("-", "");
			System.out.println("orderId = " + orderId);

			// 在数据库保存流水数据
			LmTransaction lt = new LmTransaction();
			lt.setServiceName(ServiceNameEnum.RESET_PASSWORD.getValue());
			lt.setPlatformUserNo(userId);
			lt.setRequestNo(orderId);
			lt.setCreateDate(new Date());
			lt.setUpdateDate(new Date());
			lt.setId(orderId);
			lmTransactionDao.insert(lt);

			reqData.put("platformUserNo", userId);
			reqData.put("requestNo", orderId);
			reqData.put("redirectUrl", ServerURLConfig.BACK_BACKTO_URL_WEB);
			// 必须添加的参数
			reqData.put("timestamp", DateUtils.getDateStr());

			result = AppUtil.generatePostParam(ServiceNameEnum.RESET_PASSWORD.getValue(), reqData);
		} catch (Exception e) {
			e.getStackTrace();
		}

		return result;
	}

	/**
	 * @description:验证密码
	 * @param userId
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public Map<String, String> checkPassword(String userId) {

		// 定义reqData参数集合
		Map<String, String> reqData = new HashMap<String, String>();
		Map<String, String> result = new HashMap<String, String>();
		try {
			String orderId = UUID.randomUUID().toString().replace("-", "");
			System.out.println("orderId = " + orderId);

			// 在数据库保存流水数据
			LmTransaction lt = new LmTransaction();
			lt.setServiceName(ServiceNameEnum.CHECK_PASSWORD.getValue());
			lt.setPlatformUserNo(userId);
			lt.setRequestNo(orderId);
			lt.setCreateDate(new Date());
			lt.setUpdateDate(new Date());
			lt.setId(orderId);
			lmTransactionDao.insert(lt);

			reqData.put("platformUserNo", userId);
			reqData.put("requestNo", orderId);
			reqData.put("redirectUrl", ServerURLConfig.BACK_BACKTO_URL_WEB);
			// reqData.put("redirectUrl", "http://222.249.226.103:8088/svc/services/cicnotify/notify.do");
			reqData.put("bizTypeDescription", "用户查询自己银行卡密码");
			// 必须添加的参数
			reqData.put("timestamp", DateUtils.getDateStr());

			result = AppUtil.generatePostParam(ServiceNameEnum.CHECK_PASSWORD.getValue(), reqData);

		} catch (Exception e) {
			e.getStackTrace();
		}

		return result;
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
			log.info("懒猫交易留存插入:{}", lm == 1 ? "成功" : "失败");
		} catch (Exception e) {
			e.getStackTrace();
		}

		return result;
	}

	/**
	 * @description:预留手机号更新回调
	 * @param userId
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public void callbackModifyMobileExpand(JSONObject json, UserInfo userInfo) {

		String userId = json.getString("platformUserNo");
		String orderId = json.getString("requestNo");
		CgbUserBankCard userBankCard = new CgbUserBankCard();
		userBankCard.setUserId(userId);
		List<CgbUserBankCard> list = cgbUserBankCardDao.findList(userBankCard);
		if (list != null) {
			CgbUserBankCard cbk = list.get(0);
			cbk.setUpdateDate(new Date());
			cbk.setBankAccountNo(json.getString("bankcardNo"));
			cbk.setBankNo(json.getString("bankcode"));
			cbk.setBankCardPhone(json.getString("mobile"));
			int i = cgbUserBankCardDao.update(cbk);
			if (i > 0) {
				log.info("银行卡信息修改成功");
			}
		}
		// 查询流水表
		LmTransaction lm = new LmTransaction();
		lm.setServiceName(ServiceNameEnum.MODIFY_MOBILE_EXPAND.getValue());
		lm.setPlatformUserNo(userId);
		lm.setRequestNo(orderId);
		List<LmTransaction> ltList = lmTransactionDao.findList(lm);
		if (ltList != null && ltList.size() > 0) {
			LmTransaction lt = ltList.get(0);
			lt.setUpdateDate(new Date());
			lt.setCode(json.getString("code"));
			lt.setStatus(json.getString("status"));
			// 鉴权通过类型
			lt.setAccessType(json.getString("accessType"));
			lt.setErrorCode(json.getString("errorCode"));
			lt.setErrorMessage(json.getString("errorMessage"));
			int m = lmTransactionDao.update(lt);
			if (m > 0) {
				log.info("个人用户预留手机号更新回调流水号修改成功");
			}
		}
	}

	/**
	 * @description:会员激活---企业和个人共用
	 * @param userId
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public Map<String, String> activateStockedUser(String userId) {

		// 定义reqData参数集合
		Map<String, String> reqData = new HashMap<String, String>();
		Map<String, String> result = new HashMap<String, String>();
		try {
			String orderId = UUID.randomUUID().toString().replace("-", "");
			System.out.println("orderId = " + orderId);

			//测试用户激活，用户id
//			userId = "123456789001";
			reqData.put("platformUserNo", userId);
			reqData.put("requestNo", orderId);
			reqData.put("redirectUrl", RedirectUrlConfig.ACTIVATE_STOCKED_USER);
			reqData.put("checkType", CheckTypeEnum.LIMIT.getValue());
			reqData.put("authList", AuthEnum.TENDER.getValue());
			reqData.put("amount", "1000000.00"); // 授权还款金额1000000.00万每笔
			reqData.put("failTime", DateUtils.formatDate(DateUtils.yearAddNum(new Date(), 3), "yyyyMMdd")); // 授权截至时间，默认三年
			// 必须添加的参数
			reqData.put("timestamp", DateUtils.getDateStr());
			result = AppUtil.generatePostParam(ServiceNameEnum.ACTIVATE_STOCKED_USER.getValue(), reqData);

			LmTransaction lt = new LmTransaction();
			lt.setPlatformUserNo(userId);
			lt.setServiceName(ServiceNameEnum.ACTIVATE_STOCKED_USER.getValue());
			lt.setRequestNo(orderId);
			lt.setCreateDate(new Date());
			lt.setUpdateDate(new Date());
			lt.setId(orderId);
			lt.setStatus(LmTransaction.PROCESSING);
			int lm = lmTransactionDao.insert(lt);
			log.info("懒猫交易留存插入:{}", lm == 1 ? "成功" : "失败");
		} catch (Exception e) {
			e.getStackTrace();
		}

		return result;
	}

}
