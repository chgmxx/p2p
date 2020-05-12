package com.power.platform.credit.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.power.platform.cache.Cache;
import com.power.platform.cgb.entity.CgbUserBankCard;
import com.power.platform.cgb.service.CgbUserBankCardService;
import com.power.platform.common.config.Global;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.utils.EncoderUtil;
import com.power.platform.common.utils.FileUploadUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.MemCachedUtis;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.utils.Util;
import com.power.platform.credit.dao.addressinfo.CreditAddressInfoDao;
import com.power.platform.credit.dao.bankcardinfo.CreditBankCardInfoDao;
import com.power.platform.credit.dao.basicinfo.CreditBasicInfoDao;
import com.power.platform.credit.dao.carinfo.CreditCarInfoDao;
import com.power.platform.credit.dao.censusinfo.CreditCensusInfoDao;
import com.power.platform.credit.dao.collateral.CreditCollateralInfoDao;
import com.power.platform.credit.dao.houseinfo.CreditHouseInfoDao;
import com.power.platform.credit.dao.supplierToMiddlemen.CreditSupplierToMiddlemenDao;
import com.power.platform.credit.dao.userinfo.CreditUserAccountDao;
import com.power.platform.credit.dao.userinfo.CreditUserInfoDao;
import com.power.platform.credit.dao.userinfo.CreditUserOperatorDao;
import com.power.platform.credit.entity.addressinfo.CreditAddressInfo;
import com.power.platform.credit.entity.annexfile.CreditAnnexFile;
import com.power.platform.credit.entity.bankcardinfo.CreditBankCardInfo;
import com.power.platform.credit.entity.basicinfo.CreditBasicInfo;
import com.power.platform.credit.entity.censusinfo.CreditCensusInfo;
import com.power.platform.credit.entity.collateral.CreditCollateralInfo;
import com.power.platform.credit.entity.houseinfo.CreditHouseInfo;
import com.power.platform.credit.entity.supplierToMiddlemen.CreditSupplierToMiddlemen;
import com.power.platform.credit.entity.userinfo.CreditUserAccount;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.entity.userinfo.CreditUserOperator;
import com.power.platform.credit.service.addressinfo.CreditAddressInfoService;
import com.power.platform.credit.service.annexfile.CreditAnnexFileService;
import com.power.platform.credit.service.bankcardinfo.CreditBankCardInfoService;
import com.power.platform.credit.service.censusinfo.CreditCensusInfoService;
import com.power.platform.credit.service.collateral.CreditCollateralInfoService;
import com.power.platform.credit.service.houseinfo.CreditHouseInfoService;
import com.power.platform.credit.service.supplierToMiddlemen.CreditSupplierToMiddlemenService;
import com.power.platform.credit.service.userinfo.CreditUserAccountService;
import com.power.platform.pay.cash.service.UserCashService;
import com.power.platform.pay.utils.BankCodeUtils;
import com.power.platform.pay.utils.FuncUtils;
import com.power.platform.regular.dao.WloanSubjectDao;
import com.power.platform.regular.entity.WloanSubject;
import com.power.platform.userinfo.entity.Principal;
import com.power.platform.userinfo.service.UserBankCardService;

@Path("/credit")
@Service("creditUserService")
@Produces(MediaType.APPLICATION_JSON)
public class CreditUserService {

	private static final Logger LOG = LoggerFactory.getLogger(CreditUserService.class);

	private static final String FILE_PATH = Global.getConfig("upload_file_path");
	// private static final String FILE_PATH = "E:\\temp";

	@Autowired
	private CreditUserInfoDao creditUserInfoDao;
	@Autowired
	private CreditBankCardInfoDao creditBankCardInfoDao;
	@Autowired
	private CreditAddressInfoDao creditAddressInfoDao;
	@Autowired
	private CreditHouseInfoDao creditHouseInfoDao;
	@Autowired
	private CreditCensusInfoDao creditCensusInfoDao;
	@Autowired
	private CreditBasicInfoDao creditBasicInfoDao;
	@Autowired
	private CreditCarInfoDao creditCarInfoDao;
	@Autowired
	private CreditUserAccountDao creditUserAccountDao;
	@Autowired
	private CreditCollateralInfoDao creditCollateralInfoDao;
	@Autowired
	private CreditBankCardInfoService creditBankCardInfoService;
	@Autowired
	private CreditAddressInfoService creditAddressInfoService;
	@Autowired
	private CreditHouseInfoService creditHouseInfoService;
	@Autowired
	private CreditAnnexFileService creditAnnexFileService;
	@Autowired
	private CreditCensusInfoService creditCensusInfoService;
	@Autowired
	private CreditCollateralInfoService creditCollateralService;
	@Autowired
	private UserBankCardService userBankCardService;
	@Autowired
	private CreditUserAccountService creditUserAccountService;
	@Autowired
	private UserCashService userCashService;
	@Autowired
	private CgbUserBankCardService cgbUserBankCardService;
	@Autowired
	private CreditSupplierToMiddlemenDao creditSupplierToMiddlemenDao;
	@Autowired
	private CreditSupplierToMiddlemenService creditSupplierToMiddlemenService;
	@Autowired
	private WloanSubjectDao wloanSubjectDao;
	@Autowired
	private CreditUserOperatorDao creditUserOperatorDao;

	/**
	 * 校验借款用户是否注册
	 * 
	 * @param phone
	 * @return
	 */
	@POST
	@Path("/checkCreditRegist")
	public Map<String, Object> checkRegist(@FormParam("phone") String phone) {

		Map<String, Object> result = new HashMap<String, Object>();
		// N1.判断必要参数是否为空
		if (StringUtils.isBlank(phone)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		// N2.判断是否已经注册
		CreditUserInfo credituser = new CreditUserInfo();
		credituser.setPhone(phone);
		List<CreditUserInfo> returnuser = creditUserInfoDao.findList(credituser);
		if (returnuser != null && returnuser.size() > 0) {
			result.put("state", "1");
			result.put("message", "该借款用户已注册");
		} else {
			result.put("state", "0");
			result.put("message", "该借款用户未注册");
		}
		return result;
	}

	/**
	 * 校验借款用户信息是否完全
	 * 
	 * @param phone
	 * @return
	 */
	@POST
	@Path("/checkCreditInfo")
	public Map<String, Object> checkCreditInfo(@FormParam("token") String token) {

		Map<String, Object> result = new HashMap<String, Object>();
		// N1.判断必要参数是否为空
		if (StringUtils.isBlank(token)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		// N2.判断借款用户信息是否完全
		try {

			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			if (null != principal && null != principal.getCreditUserInfo()) {
				String creditUserId = principal.getCreditUserInfo().getId();
				if (creditUserId != null) {
					List<CreditBasicInfo> basicInfoList = creditBasicInfoDao.findByUserId(creditUserId);
					if (basicInfoList != null && basicInfoList.size() > 0) {
						result.put("state", "0");
						result.put("message", "信息已录入");
						return result;
					} else {
						result.put("state", "1");
						result.put("message", "基本信息未录入");
						return result;
					}
				} else {
					result.put("state", "4");
					result.put("message", "系统超时");
					result.put("data", "");
					return result;
				}
			} else {
				result.put("state", "4");
				result.put("message", "系统超时");
				result.put("data", "");
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", "系统异常");
			result.put("state", "3");
			result.put("data", null);
			return result;
		}
	}

	/**
	 * 借款用户注册
	 * 
	 * @param from
	 * @param name
	 * @param pwd
	 * @return
	 */
	@POST
	@Path("/creditRegist")
	public Map<String, Object> regist(@FormParam("phone") String phone, @FormParam("pwd") String pwd, @Context HttpServletRequest servletrequest) {

		// IP.
		String ip = (String) servletrequest.getAttribute("ip");
		ip.replace("_", ".");
		Map<String, Object> result = new HashMap<String, Object>();
		// N1.判断必要参数是否为空
		if (StringUtils.isBlank(phone) || StringUtils.isBlank(pwd)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		// N2.判断是否已经注册
		CreditUserInfo credituser = new CreditUserInfo();
		credituser.setPhone(phone);
		List<CreditUserInfo> returnuser = creditUserInfoDao.findList(credituser);
		if (returnuser != null && returnuser.size() > 0) {
			result.put("state", "1");
			result.put("message", "该借款用户已注册");
			return result;
		}
		// N3.开始注册
		CreditUserInfo user = new CreditUserInfo();
		String userId = String.valueOf(IdGen.randomLong());
		String userAccountId = String.valueOf(IdGen.randomLong());
		user.setId(userId);
		user.setAccountId(userAccountId);
		user.setPhone(phone);
		user.setPwd(EncoderUtil.encrypt(pwd));
		user.setRegisterDate(new Date());
		user.setLastLoginDate(new Date());
		user.setLastLoginIp(ip);
		user.setCreditScore("0");
		user.setState("1");
		int i = creditUserInfoDao.insert(user);
		if (i > 0) {
			LOG.info("借款用户注册成功");
			// N4.新增借款用户账户
			CreditUserAccount userAccount = new CreditUserAccount();
			userAccount.setId(userAccountId);
			userAccount.setCreditUserId(userId);
			userAccount.setTotalAmount(0d);
			userAccount.setAvailableAmount(0d);
			userAccount.setRechargeAmount(0d);
			userAccount.setWithdrawAmount(0d);
			userAccount.setRepayAmount(0d);
			userAccount.setSurplusAmount(0d);
			userAccount.setFreezeAmount(0d);
			int j = creditUserAccountDao.insert(userAccount);
			if (j > 0) {
				LOG.info("借款用户账户新增成功");
				// N5.缓存借款用户
				Principal principal = new Principal();
				Date time = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhhmmss");
				String token = EncoderUtil.encrypt(sdf.format(time) + user.getId()).replace("+", "a");
				try {
					Cache cache = MemCachedUtis.getMemCached();
					principal.setCreditUserInfo(user);
					;
					cache.set(token, 1200, principal);
				} catch (Exception e) {
					e.printStackTrace();
				}
				result.put("state", "0");
				result.put("message", "借款用户注册成功");
				result.put("token", token);
				result.put("phone", user.getPhone());
				result.put("userId", user.getId());
			}
			return result;
		} else {
			result.put("state", "3");
			result.put("message", "借款用户注册失败");
			return result;
		}

	}
	
	/**
	 * 借款用户注册ERP
	 * 
	 * @param from
	 * @param name
	 * @param pwd
	 * @return
	 */
	@POST
	@Path("/creditRegistErp")
	public Map<String, Object> registErp(@FormParam("phone") String phone, @FormParam("pwd") String pwd, @FormParam("middlemenId") String middlemenId, @FormParam("enterpriseFullName") String enterpriseFullName, @Context HttpServletRequest servletrequest) {

		// IP.
		String ip = (String) servletrequest.getAttribute("ip");
		ip.replace("_", ".");
		Map<String, Object> result = new HashMap<String, Object>();
		// N1.判断必要参数是否为空
		if (StringUtils.isBlank(phone) || StringUtils.isBlank(pwd) || StringUtils.isBlank(middlemenId) || StringUtils.isBlank(enterpriseFullName)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		// N2.判断是否已经注册
		CreditUserInfo credituser = new CreditUserInfo();
		credituser.setPhone(phone);
		List<CreditUserInfo> returnuser = creditUserInfoDao.findList(credituser);
		if (returnuser != null && returnuser.size() > 0) {
			result.put("state", "1");
			result.put("message", "该借款用户已注册");
			return result;
		}
		// N3.开始注册
		CreditUserInfo user = new CreditUserInfo();
		String userId = String.valueOf(IdGen.randomLong());
		String userAccountId = String.valueOf(IdGen.randomLong());
		user.setId(userId);
		user.setAccountId(userAccountId);
		user.setPhone(phone);
		user.setPwd(EncoderUtil.encrypt(pwd));
		user.setRegisterDate(new Date());
		user.setLastLoginDate(new Date());
		user.setLastLoginIp(ip);
		user.setCreditScore("0");
		user.setEnterpriseFullName(enterpriseFullName);
		user.setState("1");
		user.setCreditUserType("02");
		int i = creditUserInfoDao.insert(user);
		if (i > 0) {
			LOG.info("借款用户注册成功");
			// N4.新增借款用户账户
			CreditUserAccount userAccount = new CreditUserAccount();
			userAccount.setId(userAccountId);
			userAccount.setCreditUserId(userId);
			userAccount.setTotalAmount(0d);
			userAccount.setAvailableAmount(0d);
			userAccount.setRechargeAmount(0d);
			userAccount.setWithdrawAmount(0d);
			userAccount.setRepayAmount(0d);
			userAccount.setSurplusAmount(0d);
			userAccount.setFreezeAmount(0d);
			int j = creditUserAccountDao.insert(userAccount);
			if (j > 0) {
				LOG.info("借款用户账户新增成功");
				//N5.新增借款户代偿户关系
				CreditSupplierToMiddlemen creditSupplierToMiddlemen = new CreditSupplierToMiddlemen();
				creditSupplierToMiddlemen.setId(IdGen.uuid());
				creditSupplierToMiddlemen.setSupplierId(user.getId());
				creditSupplierToMiddlemen.setMiddlemenId(middlemenId);
				int n = creditSupplierToMiddlemenService.insertCreditSupplierToMiddlemen(creditSupplierToMiddlemen);
				if(n>0){
					LOG.info("借代中间表新增成功");
					LOG.info("企业信息插入开始");
					WloanSubject wloanSubject = new WloanSubject();
					wloanSubject.setId(IdGen.uuid());
					wloanSubject.setType("2");
					wloanSubject.setLoanApplyId(user.getId());
					wloanSubject.setCompanyName(enterpriseFullName);//供应商名称
//					wloanSubject.setOrganNo(orgCode);//组织机构代码
					wloanSubject.setIsEntrustedPay("0");
					wloanSubject.setCreateDate(new Date());
					wloanSubject.setUpdateDate(new Date());

					int m = wloanSubjectDao.insert(wloanSubject);
					if (m > 0) {
						LOG.info("企业开户信息新增成功");
					}
				}
				
				result.put("state", "0");
				result.put("message", "供应商新增成功");
				result.put("phone", user.getPhone());
				result.put("userId", user.getId());
			}
			return result;
		} else {
			result.put("state", "3");
			result.put("message", "借款用户注册失败");
			return result;
		}

	}

	/**
	 * 借款用户信息
	 * 
	 * @param token
	 * @return
	 */
	@POST
	@Path("/creditUserInfo")
	public Map<String, Object> creditUserInfo(@FormParam("token") String token) {

		Map<String, Object> result = new HashMap<String, Object>();
		CreditUserInfo userInfo = new CreditUserInfo();
		// N1.判断必要参数是否为空
		if (StringUtils.isBlank(token)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		// N2.获取借款用户信息
		try {

			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			if (null != principal && null != principal.getCreditUserInfo()) {
				userInfo = creditUserInfoDao.get(principal.getCreditUserInfo().getId());
				if (userInfo != null) {
					CgbUserBankCard userBankCard = cgbUserBankCardService.findByUserId1(userInfo.getId());
					CreditUserAccount creditUserAccount = creditUserAccountService.get(userInfo.getAccountId());
					userInfo = creditUserInfoDao.get(userInfo.getId());
					result.put("state", "0");
					result.put("message", "查询成功");
					result.put("creditUserId", userInfo.getId());
					result.put("name", userInfo.getEnterpriseFullName() == null ? "" : userInfo.getEnterpriseFullName());
					result.put("phone", userInfo.getPhone().equals("") ? "" : Util.hideString(userInfo.getPhone(), 6, 8));
					result.put("IdCard", userInfo.getCertificateNo());
					/*
					 * 判断是否是房产抵押借款户
					 */
					CreditSupplierToMiddlemen entity = new CreditSupplierToMiddlemen();
					entity.setSupplierId(principal.getCreditUserInfo().getId());
					List<CreditSupplierToMiddlemen> supplierToMiddlemenList = creditSupplierToMiddlemenDao.findCreditSupplierToMiddlemens(entity);
					if(supplierToMiddlemenList!=null&&supplierToMiddlemenList.size()>0){
						for (CreditSupplierToMiddlemen creditSupplierToMiddlemen : supplierToMiddlemenList) {
							//若supplierId==middlemenId则为房产抵押借款户
							if(creditSupplierToMiddlemen.getSupplierId().equals(creditSupplierToMiddlemen.getMiddlemenId())){
								result.put("type", "12");
							}else{
								result.put("type", userInfo.getCreditUserType());
							}
						}
					}else{
						result.put("type", userInfo.getCreditUserType());
					}
					
					if (userBankCard != null) {// 0-未认证开户 1-已认证开户
						result.put("bindBankCard", userBankCard.getState());
						String str = userBankCard.getBankAccountNo();
						result.put("bankCardNo", str.substring(str.length() - 4, str.length()));
						result.put("bankNo", userBankCard.getBankName());
					} else {
						result.put("bindBankCard", "2");//未填写企业开户信息
						result.put("bankCardNo", "");
						result.put("bankNo", "");
					}
					if (creditUserAccount != null) {
						result.put("totalAmount", creditUserAccount.getTotalAmount());
						result.put("availableAmount", creditUserAccount.getAvailableAmount());
						if(userInfo.getCreditUserType()!=null){
							if(userInfo.getCreditUserType().equals("11")){
								 Double borrowingTotalAmount = 0d;
//								 List<CreditUserAccount> list = creditUserAccountDao.findAllCreditUserAccountList();
								 List<CreditUserAccount> list = creditUserAccountDao.findCreditUserAccountListByMiddlemenId(userInfo.getId());
								 for (CreditUserAccount userAccount : list) {
									 borrowingTotalAmount = borrowingTotalAmount + userAccount.getSurplusAmount();
								}
								 result.put("borrowingTotalAmount", borrowingTotalAmount);
							}else{
								result.put("borrowingTotalAmount", creditUserAccount.getSurplusAmount());
							}
						}else{
							result.put("borrowingTotalAmount", creditUserAccount.getSurplusAmount());
						}
					}
					result.put("token", token);
					return result;
				} else {
					result.put("state", "4");
					result.put("message", "系统超时");
					return result;
				}
			} else {
				result.put("state", "4");
				result.put("message", "系统超时");
				result.put("data", "");
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", "系统异常");
			result.put("state", "3");
			result.put("data", null);
			return result;
		}
	}
	
	
	/**
	 * 用户银行卡信息
	 * 
	 * @param from
	 * @return
	 */
	@POST
	@Path("/getUserBankCard")
	public Map<String, Object> getUserBankCard(@FormParam("from") String from, @FormParam("token") String token) {

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> map = new HashMap<String, Object>();
		// 判断必要参数是否为空
		if (StringUtils.isBlank(from)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		/**
		 * 获取token.
		 */
		try {
			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			if (null != principal) {
				CreditUserInfo userInfo = principal.getCreditUserInfo();
				if (userInfo != null) {
					String userId = userInfo.getId();
					CgbUserBankCard userBankCard = cgbUserBankCardService.getBankCardInfoByUserId(userId);
					CreditUserAccount userAccount = creditUserAccountService.get(userInfo.getAccountId());
					if (userBankCard != null) {
						map.put("bindBankCardNo", FuncUtils.hiddenBankCard(userBankCard.getBankAccountNo()));
						map.put("bankCode", userBankCard.getBankNo());
						map.put("bankName", userBankCard.getBankName()==null?"":userBankCard.getBankName());
						map.put("dayLimitAmount", userBankCard.getBankNo() == null ? "" : BankCodeUtils.getDayLimit(userBankCard.getBankNo()));
						map.put("singleLimitAmount", userBankCard.getBankNo() == null ? "" : BankCodeUtils.getSingleLimit(userBankCard.getBankNo()));
						map.put("bankCardPhone", userBankCard.getBankCardPhone());
						int freeCash = userCashService.getCreditUserCashCount(userId);
						map.put("freeCash", freeCash);
						if(userAccount!=null){
							map.put("availableAmount", userAccount.getAvailableAmount());
						}else{
							map.put("availableAmount", "");
						}
						result.put("state", "0");
						result.put("message", "获取用户银行卡成功");
						result.put("data", map);
						return result;
					} else {
						result.put("state", "5");
						result.put("message", "用户未绑定银行卡");
						return result;
					}

				} else {
					result.put("state", "4");
					result.put("message", "用户登录超时，请重新登录");
				}
			}else{
				result.put("state", "4");
				result.put("message", "用户登录超时，请重新登录");
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统异常");
		}
		return result;
	}

	/**
	 * 借款用户登录-----密码登录
	 * 
	 * @param from
	 * @param mobile
	 * @param pwd
	 * @return
	 */
	@POST
	@Path("/creditLogin")
	public Map<String, Object> creditLogin(@FormParam("phone") String phone, @FormParam("pwd") String pwd, @Context HttpServletRequest servletrequest) {

		// 缓存中登录用户的token信息（key=userId，value=token）

		String ip = (String) servletrequest.getAttribute("ip");
		ip.replace("_", ".");
		Map<String, Object> result = new HashMap<String, Object>();
		Principal principal = new Principal();
		Date time = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhhmmss");
		// N1.判断必要参数是否为空
		if (StringUtils.isBlank(phone) || StringUtils.isBlank(pwd)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		// N2.判断是否有此借款用户
		CreditUserInfo credituser = new CreditUserInfo();
		credituser.setPhone(phone);
		credituser.setPwd(EncoderUtil.encrypt(pwd));

		try {

			List<CreditUserInfo> credituserList = creditUserInfoDao.findList(credituser);
			if (credituserList != null && credituserList.size() > 0) {
				CreditUserInfo user = credituserList.get(0);
				// N3.开始登录
				user.setId(user.getId());
				user.setLastLoginDate(new Date());
				user.setLastLoginIp(ip);
				creditUserInfoDao.update(user);
				// N4.生成token
				principal.setCreditUserInfo(creditUserInfoDao.get(user.getId()));
				String token = EncoderUtil.encrypt(sdf.format(time) + user.getId()).replace("+", "a");
				// 获取缓存
				Cache cache = MemCachedUtis.getMemCached();
				Map<String, String> cacheLoginedUser = cache.get("cacheLoginedUser");
				// 系统没有登录用户（一般不会进该方法）
				if (cacheLoginedUser == null) {
					cacheLoginedUser = new HashMap<String, String>();
				}

				String isexitToken = cacheLoginedUser.get(user.getId());
				if (isexitToken != null && isexitToken != "") {
					// 不等于null 获取到原来的token，并且移除
					cache.delete(isexitToken);
				}

				cacheLoginedUser.put(user.getId(), token);
				cache.set("cacheLoginedUser", cacheLoginedUser);

				// 原来未登录，
				cache.set(token, 1200, principal);
				cache.set(user.getId(), 1200, token);// 用userid缓存token

				result.put("state", "0");
				result.put("message", "登录成功");
				result.put("token", token);
				result.put("phone", Util.hideString(principal.getCreditUserInfo().getPhone(), 6, 8));
			} else {
				result.put("state", "3");
				result.put("message", "用户名或密码错误");
				result.put("token", "");
				result.put("phone", "");
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "借款用户异常");
		}

		return result;
	}

	/**
	 * 借款用户登录--------验证码登录
	 * 
	 * @param phone
	 * @param smsCode
	 * @param servletrequest
	 * @return
	 */
	@POST
	@Path("/smsCodeLogin")
	public Map<String, Object> smsCodeLogin(@FormParam("phone") String phone, @FormParam("smsCode") String smsCode, @Context HttpServletRequest servletrequest) {

		Map<String, Object> result = new HashMap<String, Object>();
		Principal principal = new Principal();
		Date time = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhhmmss");
		String ip = (String) servletrequest.getAttribute("ip");
		ip.replace("_", ".");
		// N1.判断参数是否传递.
		if (StringUtils.isBlank(phone) || StringUtils.isBlank(smsCode)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			return result;
		}
		// N2.判断是否有此借款用户
		CreditUserInfo credituser = new CreditUserInfo();
		credituser.setPhone(phone);
		try {

			List<CreditUserInfo> credituserList = creditUserInfoDao.findList(credituser);
			if (credituserList != null && credituserList.size() > 0) {
				CreditUserInfo user = credituserList.get(0);
				// N3.判断验证码是否正确
				Cache cache = MemCachedUtis.getMemCached();
				String cachSmsCode = cache.get(phone);
				// 空指针异常.
				if (null == cachSmsCode) {
					result.put("state", "3");
					result.put("message", "缓存中验证码不存在！");
					result.put("token", "");
				} else if (cachSmsCode.equals(smsCode)) {
					// N4.登录成功
					user.setId(user.getId());
					user.setLastLoginDate(new Date());
					user.setLastLoginIp(ip);
					creditUserInfoDao.update(user);
					principal.setCreditUserInfo(creditUserInfoDao.get(user.getId()));
					String token = EncoderUtil.encrypt(sdf.format(time) + user.getId()).replace("+", "a");
					// 获取缓存
					Map<String, String> cacheLoginedUser = cache.get("cacheLoginedUser");
					// 系统没有登录用户（一般不会进该方法）
					if (cacheLoginedUser == null) {
						cacheLoginedUser = new HashMap<String, String>();
					}

					String isexitToken = cacheLoginedUser.get(user.getId());
					if (isexitToken != null && isexitToken != "") {
						// 不等于null 获取到原来的token，并且移除
						cache.delete(isexitToken);
					}

					cacheLoginedUser.put(user.getId(), token);
					cache.set("cacheLoginedUser", cacheLoginedUser);

					// 原来未登录，
					cache.set(token, 1200, principal);
					cache.set(user.getId(), 1200, token);// 用userid缓存token

					result.put("state", "0");
					result.put("message", "验证码登录成功");
					result.put("token", token);
					result.put("phone", Util.hideString(principal.getCreditUserInfo().getPhone(), 6, 8));
				} else {
					result.put("state", "6");
					result.put("message", "验证码验证失败！");
					result.put("token", "");
				}
			} else {
				result.put("state", "5");
				result.put("message", "借款用户未注册");
				result.put("token", "");
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:verifySmsCode,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("token", "");
		}
		return result;
	}

	/**
	 * 忘记登陆密码接口
	 * 
	 * @param from
	 * @param pass
	 * @param token
	 * @return
	 */
	@POST
	@Path("/creditForgetPwd")
	public Map<String, Object> forgetPassword(@FormParam("pwd") String pwd, @FormParam("phone") String phone) {

		Map<String, Object> result = new HashMap<String, Object>();

		// N1.判断必要参数是否为空
		if (StringUtils.isBlank(pwd) || StringUtils.isBlank(phone)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		try {
			CreditUserInfo creditUser = new CreditUserInfo();
			creditUser.setPhone(phone);
			List<CreditUserInfo> userList = creditUserInfoDao.findList(creditUser);
			
			CreditUserOperator operator = new CreditUserOperator();
			operator.setPhone(phone);
			List<CreditUserOperator> operatorList = creditUserOperatorDao.findList(operator);
			if (userList != null && userList.size() > 0) {
				CreditUserInfo user = userList.get(0);
				user.setPwd(EncoderUtil.encrypt(pwd));
				user.setFirstLogin(1);
				int i = creditUserInfoDao.update(user);
				if (i > 0) {
					result.put("state", "0");
					result.put("message", "重设登陆密码成功");
				}
			}else if(operatorList!=null && operatorList.size()>0){
				   CreditUserOperator userOperator = operatorList.get(0);
				   userOperator.setPassword(EncoderUtil.encrypt(pwd));
				   int j = creditUserOperatorDao.update(userOperator);
				   if(j >0){
						result.put("state", "0");
						result.put("message", "重设密码成功");
				   }
			} else {
				throw new Exception();
			}
			
		} catch (Exception e) {
			result.put("state", "1");
			result.put("message", "退出登录失败");
			result.put("phone", "");
		}

		return result;

	}

	/**
	 * 借款用户退出登录
	 * 
	 * @param from
	 * @param token
	 * @return
	 */
	@POST
	@Path("/creditLogout")
	public Map<String, Object> creditLogout(@FormParam("token") String token) {

		Map<String, Object> result = new HashMap<String, Object>();
		// 判断必要参数是否为空
		if (StringUtils.isBlank(token)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		try {
			Cache cache = MemCachedUtis.getMemCached();
			boolean deltoken = cache.delete(token);
			if (deltoken) {
				result.put("state", "0");
				result.put("message", "退出登录成功");
			} else {
				result.put("state", "1");
				result.put("message", "系统异常");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 银行卡信息录入
	 * 
	 * @param token
	 * @param bankCardNo
	 *            银行卡号
	 * @param bankName
	 *            开户行
	 * @param mobile
	 *            银行预留手机
	 * @return
	 */
	@POST
	@Path("/saveBankCardInfo")
	public Map<String, Object> saveBankcardInfo(@FormParam("token") String token, @FormParam("bankcardno") String bankCardNo, @FormParam("bankname") String bankName, @FormParam("mobile") String mobile) {

		Map<String, Object> result = new HashMap<String, Object>();
		CreditBankCardInfo bankCardInfo = new CreditBankCardInfo();
		// N1.判断必要参数是否为空
		if (StringUtils.isBlank(token) || StringUtils.isBlank(bankCardNo) || StringUtils.isBlank(bankName) || StringUtils.isBlank(mobile)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}

		try {
			// N2.通过token获取借款用户信息
			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			if (null != principal && null != principal.getCreditUserInfo()) {
				CreditUserInfo userInfo = principal.getCreditUserInfo();
				// N3.判断是否达到最大张数
				CreditBankCardInfo cardInfo = new CreditBankCardInfo();
				cardInfo.setCreditUserId(userInfo.getId());
				List<CreditBankCardInfo> list = creditBankCardInfoDao.findList(cardInfo);
				if (list != null && list.size() > 3) {
					result.put("state", "5");
					result.put("message", "银行卡达到最大张数");
					principal.setCreditUserInfo(userInfo);
					cache.set(token, 1200, principal);
					return result;
				}
				// N4.判断是否重复录入
				cardInfo.setBankCardNo(bankCardNo);
				List<CreditBankCardInfo> bankList = creditBankCardInfoDao.findList(cardInfo);
				if (bankList != null && bankList.size() > 0) {
					result.put("state", "6");
					result.put("message", "银行卡重复录入");
					principal.setCreditUserInfo(userInfo);
					cache.set(token, 1200, principal);
					return result;
				}
				bankCardInfo.setId(String.valueOf(IdGen.randomLong()));
				bankCardInfo.setCreditUserId(userInfo.getId());
				bankCardInfo.setBankCardNo(bankCardNo);
				bankCardInfo.setBankName(bankName);
				bankCardInfo.setMobile(mobile);
				bankCardInfo.setCreateDate(new Date());
				bankCardInfo.setUpdateDate(new Date());
				int i = creditBankCardInfoDao.insert(bankCardInfo);
				if (i > 0) {
					result.put("state", "0");
					result.put("message", "银行卡录入成功");
					principal.setCreditUserInfo(userInfo);
					cache.set(token, 1200, principal);
				} else {
					result.put("state", "1");
					result.put("message", "银行卡录入失败");
					principal.setCreditUserInfo(userInfo);
					cache.set(token, 1200, principal);
				}
			} else {
				result.put("state", "4");
				result.put("message", "系统超时");
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "3");
			result.put("message", "系统异常");
		}
		return result;
	}

	/**
	 * 银行卡信息详情
	 * 
	 * @param token
	 * @param bankCardId
	 * @return
	 */
	@POST
	@Path("/getBankCardInfo")
	public Map<String, Object> getBankCardInfo(@FormParam("token") String token, @FormParam("bankCardId") String bankCardId) {

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		CreditBankCardInfo bankCardInfo = new CreditBankCardInfo();
		// N1.判断必要参数是否为空
		if (StringUtils.isBlank(token) || StringUtils.isBlank(bankCardId)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}

		try {
			// N2.通过token获取借款用户信息
			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			if (null != principal && null != principal.getCreditUserInfo()) {
				CreditUserInfo userInfo = principal.getCreditUserInfo();
				bankCardInfo = creditBankCardInfoDao.get(bankCardId);
				if (bankCardInfo != null) {
					data.put("id", bankCardInfo.getId());
					data.put("bankCardNo", bankCardInfo.getBankCardNo());
					data.put("bankCardName", bankCardInfo.getBankName());
					data.put("bankMobile", bankCardInfo.getMobile());
					data.put("creatDate", bankCardInfo.getCreateDate());
					data.put("updateDate", bankCardInfo.getUpdateDate());
					result.put("state", "0");
					result.put("message", "获取银行卡信息详情成功");
					result.put("data", data);
					principal.setCreditUserInfo(userInfo);
					cache.set(token, 1200, principal);
				} else {
					result.put("state", "0");
					result.put("message", "查无数据");
					result.put("data", null);
					principal.setCreditUserInfo(userInfo);
					cache.set(token, 1200, principal);
				}
			} else {
				result.put("state", "4");
				result.put("message", "系统超时");
			}

		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "3");
			result.put("message", "系统异常");
		}
		return result;
	}

	/**
	 * 银行卡修改信息
	 * 
	 * @param token
	 * @param bankCardId
	 * @param bankCardNo
	 * @param bankName
	 * @param mobile
	 * @return
	 */
	@POST
	@Path("/updateBankCardInfo")
	public Map<String, Object> updateBankcardInfo(@FormParam("token") String token, @FormParam("bankCardId") String bankCardId, @FormParam("bankcardno") String bankCardNo, @FormParam("bankname") String bankName, @FormParam("mobile") String mobile) {

		Map<String, Object> result = new HashMap<String, Object>();
		CreditBankCardInfo bankCardInfo = new CreditBankCardInfo();
		// N1.判断必要参数是否为空
		if (StringUtils.isBlank(token) || StringUtils.isBlank(bankCardId) || StringUtils.isBlank(bankCardNo) || StringUtils.isBlank(bankName) || StringUtils.isBlank(mobile)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}

		try {
			// N2.通过token获取借款用户信息
			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			if (null != principal && null != principal.getCreditUserInfo()) {
				CreditUserInfo userInfo = principal.getCreditUserInfo();
				bankCardInfo = creditBankCardInfoDao.get(bankCardId);
				if (bankCardInfo != null) {
					bankCardInfo.setId(bankCardInfo.getId());
					bankCardInfo.setCreditUserId(userInfo.getId());
					bankCardInfo.setBankCardNo(bankCardNo);
					bankCardInfo.setBankName(bankName);
					bankCardInfo.setMobile(mobile);
					bankCardInfo.setUpdateDate(new Date());
					int i = creditBankCardInfoDao.update(bankCardInfo);
					if (i > 0) {
						result.put("state", "0");
						result.put("message", "银行卡信息修改成功");
						principal.setCreditUserInfo(userInfo);
						cache.set(token, 1200, principal);
					} else {
						result.put("state", "0");
						result.put("message", "查无数据");
						principal.setCreditUserInfo(userInfo);
						cache.set(token, 1200, principal);
					}
				} else {
					result.put("state", "3");
					result.put("message", "未查询到银行卡信息");
					principal.setCreditUserInfo(userInfo);
					cache.set(token, 1200, principal);
				}
			} else {
				result.put("state", "4");
				result.put("message", "系统超时");
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "5");
			result.put("message", "系统异常");
		}
		return result;
	}

	/**
	 * 银行卡删除信息
	 * 
	 * @param token
	 * @param bankCardId
	 * @return
	 */
	@POST
	@Path("/deleteBankCardInfo")
	public Map<String, Object> deleteBankcardInfo(@FormParam("token") String token, @FormParam("bankCardId") String bankCardId) {

		Map<String, Object> result = new HashMap<String, Object>();
		CreditBankCardInfo bankCardInfo = new CreditBankCardInfo();
		// N1.判断必要参数是否为空
		if (StringUtils.isBlank(token) || StringUtils.isBlank(bankCardId)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}

		try {
			// N2.通过token获取借款用户信息
			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			if (null != principal && null != principal.getCreditUserInfo()) {
				CreditUserInfo userInfo = principal.getCreditUserInfo();
				bankCardInfo = creditBankCardInfoDao.get(bankCardId);
				if (bankCardInfo != null) {
					bankCardInfo.setId(bankCardInfo.getId());
					bankCardInfo.setUpdateDate(new Date());
					int i = creditBankCardInfoDao.delete(bankCardInfo);
					if (i > 0) {
						result.put("state", "0");
						result.put("message", "银行卡信息删除成功");
						principal.setCreditUserInfo(userInfo);
						cache.set(token, 1200, principal);
					} else {
						result.put("state", "0");
						result.put("message", "查无数据");
						principal.setCreditUserInfo(userInfo);
						cache.set(token, 1200, principal);
					}
				} else {
					result.put("state", "3");
					result.put("message", "未查询到银行卡信息");
					principal.setCreditUserInfo(userInfo);
					cache.set(token, 1200, principal);
				}
			} else {
				result.put("state", "4");
				result.put("message", "系统超时");
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "5");
			result.put("message", "系统异常");
		}
		return result;
	}

	/**
	 * 银行卡信息列表
	 * 
	 * @param from
	 * @param pageNo
	 * @param pageSize
	 * @param token
	 * @return
	 */
	@POST
	@Path("/getBankCardInfoList")
	public Map<String, Object> getBankCardInfoList(@FormParam("pageNo") Integer pageNo, @FormParam("pageSize") Integer pageSize, @FormParam("token") String token) {

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();

		if (StringUtils.isBlank(token)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		try {

			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			if (null != principal && null != principal.getCreditUserInfo()) {
				CreditBankCardInfo creditBankCardInfo = new CreditBankCardInfo();
				creditBankCardInfo.setCreditUserId(principal.getCreditUserInfo().getId());
				Page<CreditBankCardInfo> page = new Page<CreditBankCardInfo>();
				page.setPageNo(1);
				page.setPageSize(5);
				page.setOrderBy("create_date DESC");
				Page<CreditBankCardInfo> bankCardInfoPage = creditBankCardInfoService.findPage(page, creditBankCardInfo);
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				List<CreditBankCardInfo> bankCardList = bankCardInfoPage.getList();
				if (bankCardList != null && bankCardList.size() > 0) {
					for (int i = 0; i < bankCardList.size(); i++) {
						Map<String, Object> map = new HashMap<String, Object>();
						creditBankCardInfo = bankCardList.get(i);
						map.put("id", creditBankCardInfo.getId());
						map.put("bankCard", FuncUtils.hiddenBankCard(creditBankCardInfo.getBankCardNo()));
						map.put("bankName", creditBankCardInfo.getBankName());
						map.put("mobile", creditBankCardInfo.getMobile());
						list.add(map);
					}
					data.put("bankcardlist", list);
					data.put("pageNo", bankCardInfoPage.getPageNo());
					data.put("pageSize", bankCardInfoPage.getPageSize());
					data.put("totalCount", bankCardInfoPage.getCount());
					data.put("last", bankCardInfoPage.getLast());
					data.put("pageCount", bankCardInfoPage.getLast());
					result.put("state", "0");
					result.put("message", "查询用户银行卡信息成功");
					result.put("data", data);
					return result;
				} else {
					data.put("bankcardlist", list);
					result.put("state", "0");
					result.put("message", "查无数据");
					result.put("data", data);
					return result;
				}
			} else {
				result.put("state", "4");
				result.put("message", "系统超时");
				result.put("data", "");
				return result;
			}

		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", "系统异常");
			result.put("state", "3");
			result.put("data", null);
			return result;
		}

	}

	/**
	 * 房产信息录入
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@POST
	@Path("/saveHouseInfo")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> saveHouseInfo(@Context HttpServletRequest request) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 数据域.
		Map<String, Object> data = new HashMap<String, Object>();
		// 键值对存储的Map接口，以HashMap(HashMap非线程安全，效率比较高)实现.
		Map<String, String> map = new HashMap<String, String>();

		// 普通文本表单字段.
		int isTextFormField = 0;
		// 文件表单字段.
		int isFileFormField = 0;
		// 上传的文件名.
		String fileName = "";
		// 新命名的文件名.
		String newFileName = "";
		// 文件格式.
		String fileFormat = "";

		// 在解析请求之前先判断请求类型是否为文件上传类型.
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		LOG.info("isMultipart:" + isMultipart + ".");
		// 是否为文件上传类型.
		if (isMultipart) {
			LOG.info("isMultipart:" + isMultipart + ",the file upload type.");
		} else {
			LOG.info("isMultipart:" + isMultipart + ",not the file upload type.");
		}

		/**
		 * 保存房产信息.
		 */
		CreditHouseInfo creditHouseInfo = new CreditHouseInfo();
		// 基本信息主键.
		String uuid = IdGen.uuid();
		creditHouseInfo.setId(uuid);

		try {
			// 文件处理工厂.
			FileItemFactory factory = new DiskFileItemFactory();
			// 文件处理器.
			ServletFileUpload upload = new ServletFileUpload(factory);
			// 支持中文文件名.
			upload.setHeaderEncoding("utf-8");
			// 储存普通文本数据和文件数据.
			List<FileItem> items = new ArrayList<FileItem>();
			// 解析HTTP请求出来.
			items = upload.parseRequest(request);
			for (int i = 0; i < items.size(); i++) {
				FileItem item = (FileItem) items.get(i);
				// 普通文本数据.
				if (item.isFormField()) {
					// 记录普通文本表单字段个数.
					isTextFormField = isTextFormField + 1;
					// 将普通文本数据存入Map.
					map.put(item.getFieldName(), item.getString("UTF-8"));
					LOG.info("FORM DATA:{" + item.getFieldName() + " = " + item.getString("UTF-8") + "}");
					/**
					 * 判断参数是否传递.
					 */
					if (!item.getFieldName().equals("token") && !item.getFieldName().equals("area") && !item.getFieldName().equals("address")) {
						LOG.info("FORM DATA:{房产信息参数名错误.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
					if (StringUtils.isBlank(item.getString("UTF-8"))) {
						LOG.info("FORM DATA:{缺少必要参数.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
				} else { // 文件数据.
					// 文本表单字段个数.
					if (isTextFormField != CreditHouseInfoService.IS_TEXT_FORM_FIELD_3) {
						LOG.info("FORM DATA:{房产信息参数不足" + CreditHouseInfoService.IS_TEXT_FORM_FIELD_3 + "个.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
					// 记录文件表单字段个数.
					isFileFormField = isFileFormField + 1;
					// 客户ID.
					Cache cache = MemCachedUtis.getMemCached();
					Principal principal = cache.get(map.get("token"));
					if (null != principal) {
						if (null != principal.getCreditUserInfo()) {
							String creditUserId = principal.getCreditUserInfo().getId();
							creditHouseInfo.setCreditUserId(creditUserId);
							List<CreditHouseInfo> list = creditHouseInfoDao.findList(creditHouseInfo);
							if (list != null && list.size() > 2) {
								LOG.info("FORM DATA:{房产信息达到最大数量.}");
								result.put("state", "3");
								result.put("message", "房产信息达到最大数量.");
								result.put("data", data);
								return result;
							}
						}
					} else {
						LOG.info("FORM DATA:{系统超时.}");
						result.put("state", "4");
						result.put("message", "系统超时.");
						result.put("data", data);
						return result;
					}

					// 上传的文件名(IE上是文件全路径，火狐等浏览器仅文件名).
					String name = item.getName();
					// 只获取文件名.
					fileName = name.substring(name.lastIndexOf(File.separator) + 1, name.length());
					// 文件扩展名
					fileFormat = fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());
					LOG.info("MULTIPART FORM DATA:{" + item.getFieldName() + " = " + fileName + "},文件格式:" + fileFormat);
					// 文件上传路径.
					String path = FileUploadUtils.createFilePath(fileFormat);
					LOG.info("PATH:" + path);
					// 新的文件名.
					newFileName = path.substring(path.lastIndexOf(File.separator) + 1, path.length());
					boolean flag = FileUploadUtils.uploadStream(newFileName, FILE_PATH, item.getInputStream());
					if (flag) {
						LOG.info("{原文件名:" + fileName + ",新文件名:" + newFileName + ",上传成功.}");
						/**
						 * 保存房产信息附件.
						 */
						CreditAnnexFile creditAnnexFile = new CreditAnnexFile();
						creditAnnexFile.setOtherId(uuid); // 房产信息ID.
						creditAnnexFile.setUrl(path); // 图片保存路径.
						creditAnnexFile.setType(CreditAnnexFileService.CREDIT_ANNEX_FILE_TYPE_4); // 类型：房产信息.
						creditAnnexFile.setRemark(CreditAnnexFileService.CREDIT_ANNEX_FILE_REMARK_4); // 备注.
						int tag = creditAnnexFileService.insertCreditAnnexFile(creditAnnexFile, i,IdGen.uuid());
						if (tag == 1) {
							LOG.info("PATH:" + path + ", save success.");
						} else {
							LOG.info("PATH:" + path + ", save failure.");
						}
					}
				}
			}

			// 文本表单字段个数.
			if (isTextFormField != CreditHouseInfoService.IS_TEXT_FORM_FIELD_3) {
				LOG.info("FORM DATA:{房产信息参数不足" + CreditHouseInfoService.IS_TEXT_FORM_FIELD_3 + "个.}");
				result.put("state", "2");
				result.put("message", "缺少必要参数.");
				result.put("data", data);
				return result;
			}
			// 文件表单字段个数为零.
			if (isFileFormField == 0) {
				/**
				 * 避免重复添加.
				 */
				Cache cache = MemCachedUtis.getMemCached();
				Principal principal = cache.get(map.get("token"));
				if (null != principal) {
					if (null != principal.getCreditUserInfo()) {
						String creditUserId = principal.getCreditUserInfo().getId();
						creditHouseInfo.setCreditUserId(creditUserId);
						List<CreditHouseInfo> list = creditHouseInfoDao.findList(creditHouseInfo);
						if (list != null && list.size() > 2) {
							LOG.info("FORM DATA:{房产信息达到最大数量.}");
							result.put("state", "3");
							result.put("message", "房产信息达到最大数量.");
							result.put("data", data);
							return result;
						}
					}
				} else {
					LOG.info("FORM DATA:{系统超时.}");
					result.put("state", "4");
					result.put("message", "系统超时.");
					result.put("data", data);
					return result;
				}
			}
			// 房产信息保存.
			int houseInfoFlag = creditHouseInfoService.insertCreditHouseInfo(creditHouseInfo, map);
			if (houseInfoFlag == 1) {
				LOG.info("CreditHouseInfo save success.");
			} else {
				LOG.info("CreditHouseInfo save failure.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统异常.");
			result.put("data", data);
			LOG.error("fn:saveHouseInfo,{" + e.getMessage() + "}");
			return result;
		}

		result.put("state", "0");
		result.put("message", "接口请求成功.");
		result.put("data", data);
		return result;
	}

	/**
	 * 房产信息列表
	 * 
	 * @param token
	 * @return
	 */
	@POST
	@Path("/getHouseInfoList")
	public Map<String, Object> getHouseInfoList(@FormParam("token") String token) {

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();

		if (StringUtils.isBlank(token)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		try {

			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			if (null != principal && null != principal.getCreditUserInfo()) {
				CreditHouseInfo creditHouseInfo = new CreditHouseInfo();
				creditHouseInfo.setCreditUserId(principal.getCreditUserInfo().getId());
				List<CreditHouseInfo> houseList = creditHouseInfoDao.findList(creditHouseInfo);
				if (houseList != null && houseList.size() > 0) {
					for (CreditHouseInfo houseInfo : houseList) {
						List<String> tempList = new ArrayList<String>();
						List<CreditAnnexFile> list = creditAnnexFileService.findCreditAnnexFileList(houseInfo.getId());
						for (CreditAnnexFile annexFile : list) {
							tempList.add(annexFile.getUrl());
						}
						houseInfo.setImgList(tempList);
					}
					data.put("houseInfoList", houseList);
					result.put("state", "0");
					result.put("message", "查询用户房产信息成功");
					result.put("data", data);
					return result;
				} else {
					data.put("houseInfoList", houseList);
					result.put("state", "0");
					result.put("message", "查无数据");
					result.put("data", data);
					return result;
				}
			} else {
				result.put("state", "4");
				result.put("message", "系统超时");
				result.put("data", "");
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", "系统异常");
			result.put("state", "3");
			result.put("data", null);
			return result;
		}

	}

	/**
	 * 房产信息详情
	 * 
	 * @param token
	 * @param bankCardId
	 * @return
	 */
	@POST
	@Path("/getHouseInfo")
	public Map<String, Object> getHouseInfo(@FormParam("token") String token, @FormParam("houseId") String houseId) {

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		CreditHouseInfo houseInfo = new CreditHouseInfo();
		// N1.判断必要参数是否为空
		if (StringUtils.isBlank(token) || StringUtils.isBlank(houseId)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}

		try {
			// N2.通过token获取借款用户信息
			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			if (null != principal && null != principal.getCreditUserInfo()) {
				CreditUserInfo userInfo = principal.getCreditUserInfo();
				houseInfo = creditHouseInfoDao.get(houseId);
				if (houseInfo != null) {
					List<CreditAnnexFile> list = creditAnnexFileService.findCreditAnnexFileList(houseInfo.getId());
					List<String> tempList = new ArrayList<String>();
					for (CreditAnnexFile annexFile : list) {
						tempList.add(annexFile.getUrl());
					}
					data.put("id", houseInfo.getId());
					data.put("province", houseInfo.getAreaProvince());
					data.put("city", houseInfo.getAreaCity());
					data.put("address", houseInfo.getAddress());
					data.put("imgList", tempList);
					result.put("state", "0");
					result.put("message", "获取房产信息详情成功");
					result.put("data", data);
					principal.setCreditUserInfo(userInfo);
					cache.set(token, 1200, principal);
				} else {
					result.put("state", "0");
					result.put("message", "查无数据");
					result.put("data", null);
					principal.setCreditUserInfo(userInfo);
					cache.set(token, 1200, principal);
				}
			} else {
				result.put("state", "4");
				result.put("message", "系统超时");
			}

		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "3");
			result.put("message", "系统异常");
		}
		return result;
	}

	/**
	 * 房产删除信息
	 * 
	 * @param token
	 * @param bankCardId
	 * @return
	 */
	@POST
	@Path("/deleteHouseInfo")
	public Map<String, Object> deleteHouseInfo(@FormParam("token") String token, @FormParam("houseId") String houseId) {

		Map<String, Object> result = new HashMap<String, Object>();
		CreditHouseInfo houseInfo = new CreditHouseInfo();
		// N1.判断必要参数是否为空
		if (StringUtils.isBlank(token) || StringUtils.isBlank(houseId)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}

		try {
			// N2.通过token获取借款用户信息
			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			if (null != principal && null != principal.getCreditUserInfo()) {
				CreditUserInfo userInfo = principal.getCreditUserInfo();
				houseInfo = creditHouseInfoDao.get(houseId);
				if (houseInfo != null) {
					// N3.获取附件列表(物理删除).
					List<CreditAnnexFile> list = creditAnnexFileService.findCreditAnnexFileList(houseId);
					for (CreditAnnexFile creditAnnexFile : list) {
						// 表数据删除.
						int flag = creditAnnexFileService.deleteCreditAnnexFileById(creditAnnexFile.getId());
						if (flag == 1) {
							LOG.info("CreditAnnexFile delete success.");
						} else {
							LOG.info("CreditAnnexFile delete failure.");
						}
						// 文件删除.
						File file = new File(FILE_PATH + File.separator + creditAnnexFile.getUrl());
						if (file.delete()) {
							LOG.info("File delete success.");
						} else {
							LOG.info("File delete failure.");
						}
					}
					// N4.删除房产信息
					houseInfo.setId(houseInfo.getId());
					houseInfo.setUpdateDate(new Date());
					int i = creditHouseInfoDao.delete(houseInfo);
					if (i > 0) {
						result.put("state", "0");
						result.put("message", "房产信息删除成功");
						principal.setCreditUserInfo(userInfo);
						cache.set(token, 1200, principal);
					} else {
						result.put("state", "1");
						result.put("message", "房产信息删除失败");
						principal.setCreditUserInfo(userInfo);
						cache.set(token, 1200, principal);
					}
				} else {
					result.put("state", "3");
					result.put("message", "未查询到房产信息");
					principal.setCreditUserInfo(userInfo);
					cache.set(token, 1200, principal);
				}
			} else {
				result.put("state", "4");
				result.put("message", "系统超时");
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "5");
			result.put("message", "系统异常");
		}
		return result;
	}

	/**
	 * 现住址信息录入
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@POST
	@Path("/saveAddressInfo")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> saveAddressInfo(@Context HttpServletRequest request) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 数据域.
		Map<String, Object> data = new HashMap<String, Object>();
		// 键值对存储的Map接口，以HashMap(HashMap非线程安全，效率比较高)实现.
		Map<String, String> map = new HashMap<String, String>();

		// 普通文本表单字段.
		int isTextFormField = 0;
		// 文件表单字段.
		int isFileFormField = 0;
		// 上传的文件名.
		String fileName = "";
		// 新命名的文件名.
		String newFileName = "";
		// 文件格式.
		String fileFormat = "";

		// 在解析请求之前先判断请求类型是否为文件上传类型.
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		LOG.info("isMultipart:" + isMultipart + ".");
		// 是否为文件上传类型.
		if (isMultipart) {
			LOG.info("isMultipart:" + isMultipart + ",the file upload type.");
		} else {
			LOG.info("isMultipart:" + isMultipart + ",not the file upload type.");
		}

		/**
		 * 保存现住址信息.
		 */
		CreditAddressInfo creditAddressInfo = new CreditAddressInfo();
		// 信息主键.
		String uuid = IdGen.uuid();
		creditAddressInfo.setId(uuid);

		try {
			// 文件处理工厂.
			FileItemFactory factory = new DiskFileItemFactory();
			// 文件处理器.
			ServletFileUpload upload = new ServletFileUpload(factory);
			// 支持中文文件名.
			upload.setHeaderEncoding("utf-8");
			// 储存普通文本数据和文件数据.
			List<FileItem> items = new ArrayList<FileItem>();
			// 解析HTTP请求出来.
			items = upload.parseRequest(request);
			for (int i = 0; i < items.size(); i++) {
				FileItem item = (FileItem) items.get(i);
				// 普通文本数据.
				if (item.isFormField()) {
					// 记录普通文本表单字段个数.
					isTextFormField = isTextFormField + 1;
					// 将普通文本数据存入Map.
					map.put(item.getFieldName(), item.getString("UTF-8"));
					LOG.info("FORM DATA:{" + item.getFieldName() + " = " + item.getString("UTF-8") + "}");
					/**
					 * 判断参数是否传递.
					 */
					if (!item.getFieldName().equals("token") && !item.getFieldName().equals("area") && !item.getFieldName().equals("address")) {
						LOG.info("FORM DATA:{现住址信息参数名错误.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
					if (StringUtils.isBlank(item.getString("UTF-8"))) {
						LOG.info("FORM DATA:{缺少必要参数.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
				} else { // 文件数据.
					// 文本表单字段个数.
					if (isTextFormField != CreditAddressInfoService.IS_TEXT_FORM_FIELD_3) {
						LOG.info("FORM DATA:{现住址信息参数不足" + CreditAddressInfoService.IS_TEXT_FORM_FIELD_3 + "个.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
					// 记录文件表单字段个数.
					isFileFormField = isFileFormField + 1;
					// 客户ID.
					Cache cache = MemCachedUtis.getMemCached();
					Principal principal = cache.get(map.get("token"));
					if (null != principal) {
						if (null != principal.getCreditUserInfo()) {
							String creditUserId = principal.getCreditUserInfo().getId();
							creditAddressInfo.setCreditUserId(creditUserId);
							List<CreditAddressInfo> list = creditAddressInfoDao.findList(creditAddressInfo);
							if (list != null && list.size() > 0) {
								LOG.info("FORM DATA:{现住址信息达到最大数量.}");
								result.put("state", "3");
								result.put("message", "现住址信息达到最大数量.");
								result.put("data", data);
								return result;
							}
						}
					} else {
						LOG.info("FORM DATA:{系统超时.}");
						result.put("state", "4");
						result.put("message", "系统超时.");
						result.put("data", data);
						return result;
					}

					// 上传的文件名(IE上是文件全路径，火狐等浏览器仅文件名).
					String name = item.getName();
					// 只获取文件名.
					fileName = name.substring(name.lastIndexOf(File.separator) + 1, name.length());
					// 文件扩展名
					fileFormat = fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());
					LOG.info("MULTIPART FORM DATA:{" + item.getFieldName() + " = " + fileName + "},文件格式:" + fileFormat);
					// 文件上传路径.
					String path = FileUploadUtils.createFilePath(fileFormat);
					LOG.info("PATH:" + path);
					// 新的文件名.
					newFileName = path.substring(path.lastIndexOf(File.separator) + 1, path.length());
					boolean flag = FileUploadUtils.uploadStream(newFileName, FILE_PATH, item.getInputStream());
					if (flag) {
						LOG.info("{原文件名:" + fileName + ",新文件名:" + newFileName + ",上传成功.}");
						/**
						 * 保存房产信息附件.
						 */
						CreditAnnexFile creditAnnexFile = new CreditAnnexFile();
						creditAnnexFile.setOtherId(uuid); // 现住址信息ID.
						creditAnnexFile.setUrl(path); // 图片保存路径.
						creditAnnexFile.setType(CreditAnnexFileService.CREDIT_ANNEX_FILE_TYPE_5); // 类型：现住址信息.
						creditAnnexFile.setRemark(CreditAnnexFileService.CREDIT_ANNEX_FILE_REMARK_5); // 备注.
						int tag = creditAnnexFileService.insertCreditAnnexFile(creditAnnexFile, i, IdGen.uuid());
						if (tag == 1) {
							LOG.info("PATH:" + path + ", save success.");
						} else {
							LOG.info("PATH:" + path + ", save failure.");
						}
					}
				}
			}

			// 文本表单字段个数.
			if (isTextFormField != CreditAddressInfoService.IS_TEXT_FORM_FIELD_3) {
				LOG.info("FORM DATA:{房产信息参数不足" + CreditAddressInfoService.IS_TEXT_FORM_FIELD_3 + "个.}");
				result.put("state", "2");
				result.put("message", "缺少必要参数.");
				result.put("data", data);
				return result;
			}
			// 文件表单字段个数为零.
			if (isFileFormField == 0) {
				/**
				 * 避免重复添加.
				 */
				Cache cache = MemCachedUtis.getMemCached();
				Principal principal = cache.get(map.get("token"));
				if (null != principal) {
					if (null != principal.getCreditUserInfo()) {
						String creditUserId = principal.getCreditUserInfo().getId();
						creditAddressInfo.setCreditUserId(creditUserId);
						List<CreditAddressInfo> list = creditAddressInfoDao.findList(creditAddressInfo);
						if (list != null && list.size() > 0) {
							LOG.info("FORM DATA:{现住址信息达到最大数量.}");
							result.put("state", "3");
							result.put("message", "现住址信息达到最大数量.");
							result.put("data", data);
							return result;
						}
					}
				} else {
					LOG.info("FORM DATA:{系统超时.}");
					result.put("state", "4");
					result.put("message", "系统超时.");
					result.put("data", data);
					return result;
				}
			}
			// 房产信息保存.
			int addressInfoFlag = creditAddressInfoService.insertCreditAddressInfo(creditAddressInfo, map);
			if (addressInfoFlag == 1) {
				LOG.info("CreditHouseInfo save success.");
			} else {
				LOG.info("CreditHouseInfo save failure.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "5");
			result.put("message", "系统异常.");
			result.put("data", data);
			LOG.error("fn:saveHouseInfo,{" + e.getMessage() + "}");
			return result;
		}

		result.put("state", "0");
		result.put("message", "接口请求成功.");
		result.put("data", data);
		return result;
	}

	/**
	 * 现住址信息列表
	 * 
	 * @param token
	 * @return
	 */
	@POST
	@Path("/getAddressInfoList")
	public Map<String, Object> getAddressInfoList(@FormParam("token") String token) {

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();

		if (StringUtils.isBlank(token)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		try {

			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			if (null != principal && null != principal.getCreditUserInfo()) {
				CreditAddressInfo creditAddressInfo = new CreditAddressInfo();
				creditAddressInfo.setCreditUserId(principal.getCreditUserInfo().getId());
				List<CreditAddressInfo> addressList = creditAddressInfoDao.findList(creditAddressInfo);
				if (addressList != null && addressList.size() > 0) {
					for (CreditAddressInfo addressInfo : addressList) {
						List<String> tempList = new ArrayList<String>();
						List<CreditAnnexFile> list = creditAnnexFileService.findCreditAnnexFileList(addressInfo.getId());
						for (CreditAnnexFile annexFile : list) {
							tempList.add(annexFile.getUrl());
						}
						addressInfo.setImgList(tempList);
					}
					data.put("houseInfoList", addressList);
					result.put("state", "0");
					result.put("message", "查询用户现住址信息成功");
					result.put("data", data);
					return result;
				} else {
					data.put("houseInfoList", addressList);
					result.put("state", "0");
					result.put("message", "查无数据");
					result.put("data", data);
					return result;
				}
			} else {
				result.put("state", "4");
				result.put("message", "系统超时");
				result.put("data", "");
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", "系统异常");
			result.put("state", "3");
			result.put("data", null);
			return result;
		}

	}

	/**
	 * 现住址信息详情
	 * 
	 * @param token
	 * @param bankCardId
	 * @return
	 */
	@POST
	@Path("/getAddressInfo")
	public Map<String, Object> getAddressInfo(@FormParam("token") String token, @FormParam("addressId") String addressId) {

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		CreditAddressInfo addressInfo = new CreditAddressInfo();
		// N1.判断必要参数是否为空
		if (StringUtils.isBlank(token) || StringUtils.isBlank(addressId)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}

		try {
			// N2.通过token获取借款用户信息
			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			if (null != principal && null != principal.getCreditUserInfo()) {
				CreditUserInfo userInfo = principal.getCreditUserInfo();
				addressInfo = creditAddressInfoDao.get(addressId);
				if (addressInfo != null) {
					List<CreditAnnexFile> list = creditAnnexFileService.findCreditAnnexFileList(addressInfo.getId());
					List<String> tempList = new ArrayList<String>();
					for (CreditAnnexFile annexFile : list) {
						tempList.add(annexFile.getUrl());
					}
					data.put("id", addressInfo.getId());
					data.put("province", addressInfo.getAreaProvince());
					data.put("city", addressInfo.getAreaCity());
					data.put("address", addressInfo.getAddress());
					data.put("imgList", tempList);
					result.put("state", "0");
					result.put("message", "获取现住址信息详情成功");
					result.put("data", data);
					principal.setCreditUserInfo(userInfo);
					cache.set(token, 1200, principal);
				} else {
					result.put("state", "0");
					result.put("message", "查无数据");
					result.put("data", null);
					principal.setCreditUserInfo(userInfo);
					cache.set(token, 1200, principal);
				}
			} else {
				result.put("state", "4");
				result.put("message", "系统超时");
			}

		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "3");
			result.put("message", "系统异常");
		}
		return result;
	}

	/**
	 * 现住址删除信息
	 * 
	 * @param token
	 * @param bankCardId
	 * @return
	 */
	@POST
	@Path("/deleteAddressInfo")
	public Map<String, Object> deleteAddressInfo(@FormParam("token") String token, @FormParam("addressId") String addressId) {

		Map<String, Object> result = new HashMap<String, Object>();
		CreditAddressInfo addressInfo = new CreditAddressInfo();
		// N1.判断必要参数是否为空
		if (StringUtils.isBlank(token) || StringUtils.isBlank(addressId)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}

		try {
			// N2.通过token获取借款用户信息
			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			if (null != principal && null != principal.getCreditUserInfo()) {
				CreditUserInfo userInfo = principal.getCreditUserInfo();
				addressInfo = creditAddressInfoDao.get(addressId);
				if (addressInfo != null) {
					// N3.获取附件列表(物理删除).
					List<CreditAnnexFile> list = creditAnnexFileService.findCreditAnnexFileList(addressId);
					for (CreditAnnexFile creditAnnexFile : list) {
						// 表数据删除.
						int flag = creditAnnexFileService.deleteCreditAnnexFileById(creditAnnexFile.getId());
						if (flag == 1) {
							LOG.info("CreditAnnexFile delete success.");
						} else {
							LOG.info("CreditAnnexFile delete failure.");
						}
						// 文件删除.
						File file = new File(FILE_PATH + File.separator + creditAnnexFile.getUrl());
						if (file.delete()) {
							LOG.info("File delete success.");
						} else {
							LOG.info("File delete failure.");
						}
					}
					// N4.删除现住址信息
					addressInfo.setId(addressInfo.getId());
					addressInfo.setUpdateDate(new Date());
					int i = creditAddressInfoDao.delete(addressInfo);
					if (i > 0) {
						result.put("state", "0");
						result.put("message", "现住址信息删除成功");
						principal.setCreditUserInfo(userInfo);
						cache.set(token, 1200, principal);
					} else {
						result.put("state", "1");
						result.put("message", "现住址信息删除失败");
						principal.setCreditUserInfo(userInfo);
						cache.set(token, 1200, principal);
					}
				} else {
					result.put("state", "3");
					result.put("message", "未查询到现住址信息");
					principal.setCreditUserInfo(userInfo);
					cache.set(token, 1200, principal);
				}
			} else {
				result.put("state", "4");
				result.put("message", "系统超时");
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "5");
			result.put("message", "系统异常");
		}
		return result;
	}

	/**
	 * 户籍信息录入
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@POST
	@Path("/saveCensusInfo")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> saveCensusInfo(@Context HttpServletRequest request) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 数据域.
		Map<String, Object> data = new HashMap<String, Object>();
		// 键值对存储的Map接口，以HashMap(HashMap非线程安全，效率比较高)实现.
		Map<String, String> map = new HashMap<String, String>();

		// 普通文本表单字段.
		int isTextFormField = 0;
		// 文件表单字段.
		int isFileFormField = 0;
		// 上传的文件名.
		String fileName = "";
		// 新命名的文件名.
		String newFileName = "";
		// 文件格式.
		String fileFormat = "";

		// 在解析请求之前先判断请求类型是否为文件上传类型.
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		LOG.info("isMultipart:" + isMultipart + ".");
		// 是否为文件上传类型.
		if (isMultipart) {
			LOG.info("isMultipart:" + isMultipart + ",the file upload type.");
		} else {
			LOG.info("isMultipart:" + isMultipart + ",not the file upload type.");
		}

		/**
		 * 保存现住址信息.
		 */
		CreditCensusInfo creditCensusInfo = new CreditCensusInfo();
		// 信息主键.
		String uuid = IdGen.uuid();
		creditCensusInfo.setId(uuid);

		try {
			// 文件处理工厂.
			FileItemFactory factory = new DiskFileItemFactory();
			// 文件处理器.
			ServletFileUpload upload = new ServletFileUpload(factory);
			// 支持中文文件名.
			upload.setHeaderEncoding("utf-8");
			// 储存普通文本数据和文件数据.
			List<FileItem> items = new ArrayList<FileItem>();
			// 解析HTTP请求出来.
			items = upload.parseRequest(request);
			for (int i = 0; i < items.size(); i++) {
				FileItem item = (FileItem) items.get(i);
				// 普通文本数据.
				if (item.isFormField()) {
					// 记录普通文本表单字段个数.
					isTextFormField = isTextFormField + 1;
					// 将普通文本数据存入Map.
					map.put(item.getFieldName(), item.getString("UTF-8"));
					LOG.info("FORM DATA:{" + item.getFieldName() + " = " + item.getString("UTF-8") + "}");
					/**
					 * 判断参数是否传递.
					 */
					if (!item.getFieldName().equals("token")) {
						LOG.info("FORM DATA:{户口信息参数名错误.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
					if (StringUtils.isBlank(item.getString("UTF-8"))) {
						LOG.info("FORM DATA:{缺少必要参数.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
				} else { // 文件数据.
					// 文本表单字段个数.
					if (isTextFormField != CreditCensusInfoService.IS_TEXT_FORM_FIELD_1) {
						LOG.info("FORM DATA:{户口信息参数不足" + CreditCensusInfoService.IS_TEXT_FORM_FIELD_1 + "个.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
					// 记录文件表单字段个数.
					isFileFormField = isFileFormField + 1;
					// 客户ID.
					Cache cache = MemCachedUtis.getMemCached();
					Principal principal = cache.get(map.get("token"));
					if (null != principal) {
						if (null != principal.getCreditUserInfo()) {
							String creditUserId = principal.getCreditUserInfo().getId();
							creditCensusInfo.setCreditUserId(creditUserId);
							List<CreditCensusInfo> list = creditCensusInfoDao.findList(creditCensusInfo);
							if (list != null && list.size() > 0) {
								LOG.info("FORM DATA:{户籍信息达到最大数量.}");
								result.put("state", "3");
								result.put("message", "户籍信息达到最大数量.");
								result.put("data", data);
								return result;
							}
						}
					} else {
						LOG.info("FORM DATA:{系统超时.}");
						result.put("state", "4");
						result.put("message", "系统超时.");
						result.put("data", data);
						return result;
					}

					// 上传的文件名(IE上是文件全路径，火狐等浏览器仅文件名).
					String name = item.getName();
					// 只获取文件名.
					fileName = name.substring(name.lastIndexOf(File.separator) + 1, name.length());
					// 文件扩展名
					fileFormat = fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());
					LOG.info("MULTIPART FORM DATA:{" + item.getFieldName() + " = " + fileName + "},文件格式:" + fileFormat);
					// 文件上传路径.
					String path = FileUploadUtils.createFilePath(fileFormat);
					LOG.info("PATH:" + path);
					// 新的文件名.
					newFileName = path.substring(path.lastIndexOf(File.separator) + 1, path.length());
					boolean flag = FileUploadUtils.uploadStream(newFileName, FILE_PATH, item.getInputStream());
					if (flag) {
						LOG.info("{原文件名:" + fileName + ",新文件名:" + newFileName + ",上传成功.}");
						/**
						 * 保存现住址信息附件.
						 */
						CreditAnnexFile creditAnnexFile = new CreditAnnexFile();
						creditAnnexFile.setOtherId(uuid); // 现住址信息ID.
						creditAnnexFile.setUrl(path); // 图片保存路径.
						creditAnnexFile.setType(CreditAnnexFileService.CREDIT_ANNEX_FILE_TYPE_5); // 类型：现住址信息.
						creditAnnexFile.setRemark(CreditAnnexFileService.CREDIT_ANNEX_FILE_REMARK_5); // 备注.
						int tag = creditAnnexFileService.insertCreditAnnexFile(creditAnnexFile, i, IdGen.uuid());
						if (tag == 1) {
							LOG.info("PATH:" + path + ", save success.");
						} else {
							LOG.info("PATH:" + path + ", save failure.");
						}
					}
				}
			}

			// 文本表单字段个数.
			if (isTextFormField != CreditCensusInfoService.IS_TEXT_FORM_FIELD_1) {
				LOG.info("FORM DATA:{户口信息参数不足" + CreditCensusInfoService.IS_TEXT_FORM_FIELD_1 + "个.}");
				result.put("state", "2");
				result.put("message", "缺少必要参数.");
				result.put("data", data);
				return result;
			}
			// 文件表单字段个数为零.
			if (isFileFormField == 0) {
				/**
				 * 避免重复添加.
				 */
				Cache cache = MemCachedUtis.getMemCached();
				Principal principal = cache.get(map.get("token"));
				if (null != principal) {
					if (null != principal.getCreditUserInfo()) {
						String creditUserId = principal.getCreditUserInfo().getId();
						creditCensusInfo.setCreditUserId(creditUserId);
						List<CreditCensusInfo> list = creditCensusInfoDao.findList(creditCensusInfo);
						if (list != null && list.size() > 0) {
							LOG.info("FORM DATA:{户籍信息达到最大数量.}");
							result.put("state", "3");
							result.put("message", "户籍信息达到最大数量.");
							result.put("data", data);
							return result;
						}
					}
				} else {
					LOG.info("FORM DATA:{系统超时.}");
					result.put("state", "4");
					result.put("message", "系统超时.");
					result.put("data", data);
					return result;
				}
			}
			// 户籍信息保存.
			int censusInfoFlag = creditCensusInfoService.insertCreditCensusInfo(creditCensusInfo, map);
			if (censusInfoFlag == 1) {
				LOG.info("CreditHouseInfo save success.");
			} else {
				LOG.info("CreditHouseInfo save failure.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统异常.");
			result.put("data", data);
			LOG.error("fn:saveHouseInfo,{" + e.getMessage() + "}");
			return result;
		}

		result.put("state", "0");
		result.put("message", "接口请求成功.");
		result.put("data", data);
		return result;
	}

	/**
	 * 户籍信息列表
	 * 
	 * @param token
	 * @return
	 */
	@POST
	@Path("/getCensusInfoList")
	public Map<String, Object> getCensusInfoList(@FormParam("token") String token) {

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();

		if (StringUtils.isBlank(token)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		try {

			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			if (null != principal && null != principal.getCreditUserInfo()) {
				CreditCensusInfo creditCensusInfo = new CreditCensusInfo();
				creditCensusInfo.setCreditUserId(principal.getCreditUserInfo().getId());
				List<CreditCensusInfo> censusList = creditCensusInfoDao.findList(creditCensusInfo);
				if (censusList != null && censusList.size() > 0) {
					for (CreditCensusInfo censusInfo : censusList) {
						List<String> tempList = new ArrayList<String>();
						List<CreditAnnexFile> list = creditAnnexFileService.findCreditAnnexFileList(censusInfo.getId());
						for (CreditAnnexFile annexFile : list) {
							tempList.add(annexFile.getUrl());
						}
						censusInfo.setImgList(tempList);
					}
					data.put("censusInfoList", censusList);
					result.put("state", "0");
					result.put("message", "查询用户户籍信息成功");
					result.put("data", data);
					return result;
				} else {
					data.put("censusInfoList", censusList);
					result.put("state", "0");
					result.put("message", "查无数据");
					result.put("data", data);
					return result;
				}
			} else {
				result.put("state", "4");
				result.put("message", "系统超时");
				result.put("data", "");
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", "系统异常");
			result.put("state", "3");
			result.put("data", null);
			return result;
		}

	}

	/**
	 * 户籍信息详情
	 * 
	 * @param token
	 * @param bankCardId
	 * @return
	 */
	@POST
	@Path("/getCensusInfo")
	public Map<String, Object> getCensusInfo(@FormParam("token") String token, @FormParam("censusId") String censusId) {

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		CreditCensusInfo censusInfo = new CreditCensusInfo();
		// N1.判断必要参数是否为空
		if (StringUtils.isBlank(token) || StringUtils.isBlank(censusId)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}

		try {
			// N2.通过token获取借款用户信息
			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			if (null != principal && null != principal.getCreditUserInfo()) {
				CreditUserInfo userInfo = principal.getCreditUserInfo();
				censusInfo = creditCensusInfoDao.get(censusId);
				if (censusInfo != null) {
					List<CreditAnnexFile> list = creditAnnexFileService.findCreditAnnexFileList(censusInfo.getId());
					List<String> tempList = new ArrayList<String>();
					for (CreditAnnexFile annexFile : list) {
						tempList.add(annexFile.getUrl());
					}
					data.put("id", censusInfo.getId());
					data.put("imgList", tempList);
					result.put("state", "0");
					result.put("message", "获取户籍信息详情成功");
					result.put("data", data);
					principal.setCreditUserInfo(userInfo);
					cache.set(token, 1200, principal);
				} else {
					result.put("state", "0");
					result.put("message", "查无数据");
					result.put("data", null);
					principal.setCreditUserInfo(userInfo);
					cache.set(token, 1200, principal);
				}
			} else {
				result.put("state", "4");
				result.put("message", "系统超时");
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "3");
			result.put("message", "系统异常");
		}
		return result;
	}

	/**
	 * 户籍删除信息
	 * 
	 * @param token
	 * @param bankCardId
	 * @return
	 */
	@POST
	@Path("/deleteCensusInfo")
	public Map<String, Object> deleteCensusInfo(@FormParam("token") String token, @FormParam("censusId") String censusId) {

		Map<String, Object> result = new HashMap<String, Object>();
		CreditCensusInfo censusInfo = new CreditCensusInfo();
		// N1.判断必要参数是否为空
		if (StringUtils.isBlank(token) || StringUtils.isBlank(censusId)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}

		try {
			// N2.通过token获取借款用户信息
			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			if (null != principal && null != principal.getCreditUserInfo()) {
				CreditUserInfo userInfo = principal.getCreditUserInfo();
				censusInfo = creditCensusInfoDao.get(censusId);
				if (censusInfo != null) {
					// N3.获取附件列表(物理删除).
					List<CreditAnnexFile> list = creditAnnexFileService.findCreditAnnexFileList(censusId);
					for (CreditAnnexFile creditAnnexFile : list) {
						// 表数据删除.
						int flag = creditAnnexFileService.deleteCreditAnnexFileById(creditAnnexFile.getId());
						if (flag == 1) {
							LOG.info("CreditAnnexFile delete success.");
						} else {
							LOG.info("CreditAnnexFile delete failure.");
						}
						// 文件删除.
						File file = new File(FILE_PATH + File.separator + creditAnnexFile.getUrl());
						if (file.delete()) {
							LOG.info("File delete success.");
						} else {
							LOG.info("File delete failure.");
						}
					}
					// N4.删除户籍信息
					censusInfo.setId(censusInfo.getId());
					censusInfo.setUpdateDate(new Date());
					int i = creditCensusInfoDao.delete(censusInfo);
					if (i > 0) {
						result.put("state", "0");
						result.put("message", "户籍信息删除成功");
						principal.setCreditUserInfo(userInfo);
						cache.set(token, 1200, principal);
					} else {
						result.put("state", "1");
						result.put("message", "户籍信息删除失败");
						principal.setCreditUserInfo(userInfo);
						cache.set(token, 1200, principal);
					}
				} else {
					result.put("state", "3");
					result.put("message", "未查询到户籍信息");
					principal.setCreditUserInfo(userInfo);
					cache.set(token, 1200, principal);
				}
			} else {
				result.put("state", "4");
				result.put("message", "系统超时");
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "5");
			result.put("message", "系统异常");
		}
		return result;
	}

	/**
	 * 
	 * 方法: updateAddressInfo <br>
	 * 描述: 修改现住址信息. <br>
	 * 时间: 2017年4月5日 下午5:08:36
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@POST
	@Path("/updateAddressInfo")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> updateAddressInfo(@Context HttpServletRequest request) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 数据域.
		Map<String, Object> data = new HashMap<String, Object>();
		// 键值对存储的Map接口，以HashMap(HashMap非线程安全，效率比较高)实现.
		Map<String, String> map = new HashMap<String, String>();
		// 普通文本表单字段.
		int isTextFormField = 0;
		// 文件表单字段.
		int isFileFormField = 0;
		// 上传的文件名.
		String fileName = "";
		// 新命名的文件名.
		String newFileName = "";
		// 文件格式.
		String fileFormat = "";

		// 在解析请求之前先判断请求类型是否为文件上传类型.
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		LOG.info("isMultipart:" + isMultipart + ".");
		// 是否为文件上传类型.
		if (isMultipart) {
			LOG.info("isMultipart:" + isMultipart + ",the file upload type.");
		} else {
			LOG.info("isMultipart:" + isMultipart + ",not the file upload type.");
		}

		/**
		 * 家庭信息.
		 */
		CreditAddressInfo creditAddressInfo = null;

		try {
			// 文件处理工厂.
			FileItemFactory factory = new DiskFileItemFactory();
			// 文件处理器.
			ServletFileUpload upload = new ServletFileUpload(factory);
			// 支持中文文件名.
			upload.setHeaderEncoding("utf-8");
			// 储存普通文本数据和文件数据.
			List<FileItem> items = new ArrayList<FileItem>();
			// 解析HTTP请求出来.
			items = upload.parseRequest(request);
			for (int i = 0; i < items.size(); i++) {
				FileItem item = (FileItem) items.get(i);
				// 普通文本数据.
				if (item.isFormField()) {
					// 记录普通文本表单字段个数.
					isTextFormField = isTextFormField + 1;
					// 将普通文本数据存入Map.
					map.put(item.getFieldName(), item.getString("UTF-8"));
					LOG.info("FORM DATA:{" + item.getFieldName() + " = " + item.getString("UTF-8") + "}");
					/**
					 * 判断参数是否传递.
					 */
					if (!item.getFieldName().equals("token") && !item.getFieldName().equals("area") && !item.getFieldName().equals("address") && !item.getFieldName().equals("addressId")) {
						LOG.info("fn:modifyFamilyInfo,{现住址信息参数名错误.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
					if (StringUtils.isBlank(item.getString("UTF-8"))) {
						LOG.info("fn:modifyFamilyInfo,{现住址信息参数为null或空串.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
				} else { // 文件数据.
					// 文本表单字段个数.
					if (isTextFormField != CreditAddressInfoService.IS_TEXT_FORM_FIELD_4) {
						LOG.info("fn:modifyFamilyInfo,{现住址信息参数不足" + CreditAddressInfoService.IS_TEXT_FORM_FIELD_4 + "个.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
					// 记录文件表单字段个数.
					isFileFormField = isFileFormField + 1;
					// 客户ID.
					Cache cache = MemCachedUtis.getMemCached();
					Principal principal = cache.get(map.get("token"));
					if (null != principal) {
						if (null != principal.getCreditUserInfo()) {
							if (isFileFormField == 1) {
								// 获取现住址信息.
								creditAddressInfo = creditAddressInfoService.get(map.get("addressId"));
								if (null == creditAddressInfo) {
									LOG.info("fn:modifyFamilyInfo,{查无此信息.}");
									result.put("state", "5");
									result.put("message", "查无此信息.");
									result.put("data", data);
									return result;
								}
								// 获取附件列表(物理删除).
								List<CreditAnnexFile> list = creditAnnexFileService.findCreditAnnexFileList(map.get("addressId"));
								for (CreditAnnexFile creditAnnexFile : list) {
									// 表数据删除.
									int flag = creditAnnexFileService.deleteCreditAnnexFileById(creditAnnexFile.getId());
									if (flag == 1) {
										LOG.info("CreditAnnexFile delete success.");
									} else {
										LOG.info("CreditAnnexFile delete failure.");
									}
									// 文件删除.
									File file = new File(FILE_PATH + File.separator + creditAnnexFile.getUrl());
									if (file.delete()) {
										LOG.info("File delete success.");
									} else {
										LOG.info("File delete failure.");
									}
								}
							}
						} else {
							LOG.info("fn:modifyFamilyInfo,{系统超时.}");
							result.put("state", "4");
							result.put("message", "系统超时.");
							result.put("data", data);
							return result;
						}
					} else {
						LOG.info("FORM DATA:{系统超时.}");
						result.put("state", "4");
						result.put("message", "系统超时.");
						result.put("data", data);
						return result;
					}

					// 上传的文件名(IE上是文件全路径，火狐等浏览器仅文件名).
					String name = item.getName();
					// 只获取文件名.
					fileName = name.substring(name.lastIndexOf(File.separator) + 1, name.length());
					// 文件扩展名
					fileFormat = fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());
					LOG.info("MULTIPART FORM DATA:{" + item.getFieldName() + " = " + fileName + "},文件格式:" + fileFormat);
					// 文件上传路径.
					String path = FileUploadUtils.createFilePath(fileFormat);
					LOG.info("PATH:" + path);
					// 新的文件名.
					newFileName = path.substring(path.lastIndexOf(File.separator) + 1, path.length());
					boolean flag = FileUploadUtils.uploadStream(newFileName, FILE_PATH, item.getInputStream());
					if (flag) {
						LOG.info("{原文件名:" + fileName + ",新文件名:" + newFileName + ",上传成功.}");
						/**
						 * 保存家庭信息附件.
						 */
						CreditAnnexFile creditAnnexFile = new CreditAnnexFile();
						creditAnnexFile.setOtherId(creditAddressInfo.getId()); // 家庭信息ID.
						creditAnnexFile.setUrl(path); // 图片保存路径.
						creditAnnexFile.setType(CreditAnnexFileService.CREDIT_ANNEX_FILE_TYPE_5); // 类型：家庭信息.
						creditAnnexFile.setRemark(CreditAnnexFileService.CREDIT_ANNEX_FILE_REMARK_5); // 备注.
						int tag = creditAnnexFileService.insertCreditAnnexFile(creditAnnexFile, i, IdGen.uuid());
						if (tag == 1) {
							LOG.info("PATH:" + path + ", save success.");
						} else {
							LOG.info("PATH:" + path + ", save failure.");
						}
					}

				}
			}

			LOG.info("MULTIPART FORM DATA IS TEXT FORM FIELD:{" + isTextFormField + "}");
			LOG.info("MULTIPART FORM DATA IS FILE FORM FIELD:{" + isFileFormField + "}");

			// 文本表单字段个数.
			if (isTextFormField != CreditAddressInfoService.IS_TEXT_FORM_FIELD_4) {
				LOG.info("FORM DATA:{家庭信息参数不足" + CreditAddressInfoService.IS_TEXT_FORM_FIELD_4 + "个.}");
				result.put("state", "2");
				result.put("message", "缺少必要参数.");
				result.put("data", data);
				return result;
			}
			// 文件表单字段个数为零.
			if (isFileFormField == 0) {
				// 客户ID.
				Cache cache = MemCachedUtis.getMemCached();
				Principal principal = cache.get(map.get("token"));
				if (null != principal) {
					if (null != principal.getCreditUserInfo()) {
						// 获取家庭信息.
						creditAddressInfo = creditAddressInfoService.get(map.get("addressId"));
						if (null == creditAddressInfo) {
							LOG.info("fn:updateAddressInfo,{查无此信息.}");
							result.put("state", "5");
							result.put("message", "查无此信息.");
							result.put("data", data);
							return result;
						}
					} else {
						LOG.info("fn:updateAddressInfo,{系统超时.}");
						result.put("state", "4");
						result.put("message", "系统超时.");
						result.put("data", data);
						return result;
					}
				} else {
					LOG.info("FORM DATA:{系统超时.}");
					result.put("state", "4");
					result.put("message", "系统超时.");
					result.put("data", data);
					return result;
				}
			}

			// 更新现住址信息.
			int addressInfoFlag = creditAddressInfoService.updateCreditAddressInfo(creditAddressInfo, map);
			if (addressInfoFlag == 1) {
				LOG.info("update success.");
			} else {
				LOG.info("update failure.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统异常.");
			result.put("data", data);
			LOG.error("fn:saveFamilyInfo,{" + e.getMessage() + "}");
			return result;
		}

		result.put("state", "0");
		result.put("message", "接口请求成功.");
		result.put("data", data);
		return result;
	}

	/**
	 * 
	 * 方法: updateHouseInfo <br>
	 * 描述: 修改房产信息. <br>
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@POST
	@Path("/updateHouseInfo")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> updateHouseInfo(@Context HttpServletRequest request) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 数据域.
		Map<String, Object> data = new HashMap<String, Object>();
		// 键值对存储的Map接口，以HashMap(HashMap非线程安全，效率比较高)实现.
		Map<String, String> map = new HashMap<String, String>();
		// 普通文本表单字段.
		int isTextFormField = 0;
		// 文件表单字段.
		int isFileFormField = 0;
		// 上传的文件名.
		String fileName = "";
		// 新命名的文件名.
		String newFileName = "";
		// 文件格式.
		String fileFormat = "";

		// 在解析请求之前先判断请求类型是否为文件上传类型.
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		LOG.info("isMultipart:" + isMultipart + ".");
		// 是否为文件上传类型.
		if (isMultipart) {
			LOG.info("isMultipart:" + isMultipart + ",the file upload type.");
		} else {
			LOG.info("isMultipart:" + isMultipart + ",not the file upload type.");
		}

		/**
		 * 家庭信息.
		 */
		CreditHouseInfo creditHouseInfo = null;

		try {
			// 文件处理工厂.
			FileItemFactory factory = new DiskFileItemFactory();
			// 文件处理器.
			ServletFileUpload upload = new ServletFileUpload(factory);
			// 支持中文文件名.
			upload.setHeaderEncoding("utf-8");
			// 储存普通文本数据和文件数据.
			List<FileItem> items = new ArrayList<FileItem>();
			// 解析HTTP请求出来.
			items = upload.parseRequest(request);
			for (int i = 0; i < items.size(); i++) {
				FileItem item = (FileItem) items.get(i);
				// 普通文本数据.
				if (item.isFormField()) {
					// 记录普通文本表单字段个数.
					isTextFormField = isTextFormField + 1;
					// 将普通文本数据存入Map.
					map.put(item.getFieldName(), item.getString("UTF-8"));
					LOG.info("FORM DATA:{" + item.getFieldName() + " = " + item.getString("UTF-8") + "}");
					/**
					 * 判断参数是否传递.
					 */
					if (!item.getFieldName().equals("token") && !item.getFieldName().equals("area") && !item.getFieldName().equals("address") && !item.getFieldName().equals("houseId")) {
						LOG.info("fn:modifyFamilyInfo,{房产信息参数名错误.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
					if (StringUtils.isBlank(item.getString("UTF-8"))) {
						LOG.info("fn:modifyFamilyInfo,{房产信息参数为null或空串.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
				} else { // 文件数据.
					// 文本表单字段个数.
					if (isTextFormField != CreditHouseInfoService.IS_TEXT_FORM_FIELD_4) {
						LOG.info("fn:modifyFamilyInfo,{房产信息参数不足" + CreditHouseInfoService.IS_TEXT_FORM_FIELD_4 + "个.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
					// 记录文件表单字段个数.
					isFileFormField = isFileFormField + 1;
					// 客户ID.
					Cache cache = MemCachedUtis.getMemCached();
					Principal principal = cache.get(map.get("token"));
					if (null != principal) {
						if (null != principal.getCreditUserInfo()) {
							if (isFileFormField == 1) {
								// 获取现住址信息.
								creditHouseInfo = creditHouseInfoService.get(map.get("houseId"));
								if (null == creditHouseInfo) {
									LOG.info("fn:updateHouseInfo,{查无此信息.}");
									result.put("state", "5");
									result.put("message", "查无此信息.");
									result.put("data", data);
									return result;
								}
								// 获取附件列表(物理删除).
								List<CreditAnnexFile> list = creditAnnexFileService.findCreditAnnexFileList(map.get("houseId"));
								for (CreditAnnexFile creditAnnexFile : list) {
									// 表数据删除.
									int flag = creditAnnexFileService.deleteCreditAnnexFileById(creditAnnexFile.getId());
									if (flag == 1) {
										LOG.info("CreditAnnexFile delete success.");
									} else {
										LOG.info("CreditAnnexFile delete failure.");
									}
									// 文件删除.
									File file = new File(FILE_PATH + File.separator + creditAnnexFile.getUrl());
									if (file.delete()) {
										LOG.info("File delete success.");
									} else {
										LOG.info("File delete failure.");
									}
								}
							}
						} else {
							LOG.info("fn:updateHouseInfo,{系统超时.}");
							result.put("state", "4");
							result.put("message", "系统超时.");
							result.put("data", data);
							return result;
						}
					} else {
						LOG.info("FORM DATA:{系统超时.}");
						result.put("state", "4");
						result.put("message", "系统超时.");
						result.put("data", data);
						return result;
					}

					// 上传的文件名(IE上是文件全路径，火狐等浏览器仅文件名).
					String name = item.getName();
					// 只获取文件名.
					fileName = name.substring(name.lastIndexOf(File.separator) + 1, name.length());
					// 文件扩展名
					fileFormat = fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());
					LOG.info("MULTIPART FORM DATA:{" + item.getFieldName() + " = " + fileName + "},文件格式:" + fileFormat);
					// 文件上传路径.
					String path = FileUploadUtils.createFilePath(fileFormat);
					LOG.info("PATH:" + path);
					// 新的文件名.
					newFileName = path.substring(path.lastIndexOf(File.separator) + 1, path.length());
					boolean flag = FileUploadUtils.uploadStream(newFileName, FILE_PATH, item.getInputStream());
					if (flag) {
						LOG.info("{原文件名:" + fileName + ",新文件名:" + newFileName + ",上传成功.}");
						/**
						 * 保存房产信息附件.
						 */
						CreditAnnexFile creditAnnexFile = new CreditAnnexFile();
						creditAnnexFile.setOtherId(creditHouseInfo.getId()); // 房产信息ID.
						creditAnnexFile.setUrl(path); // 图片保存路径.
						creditAnnexFile.setType(CreditAnnexFileService.CREDIT_ANNEX_FILE_TYPE_4); // 类型：房产信息.
						creditAnnexFile.setRemark(CreditAnnexFileService.CREDIT_ANNEX_FILE_REMARK_4); // 备注.
						int tag = creditAnnexFileService.insertCreditAnnexFile(creditAnnexFile, i, IdGen.uuid());
						if (tag == 1) {
							LOG.info("PATH:" + path + ", save success.");
						} else {
							LOG.info("PATH:" + path + ", save failure.");
						}
					}

				}
			}

			LOG.info("MULTIPART FORM DATA IS TEXT FORM FIELD:{" + isTextFormField + "}");
			LOG.info("MULTIPART FORM DATA IS FILE FORM FIELD:{" + isFileFormField + "}");

			// 文本表单字段个数.
			if (isTextFormField != CreditAddressInfoService.IS_TEXT_FORM_FIELD_4) {
				LOG.info("FORM DATA:{家庭信息参数不足" + CreditAddressInfoService.IS_TEXT_FORM_FIELD_4 + "个.}");
				result.put("state", "2");
				result.put("message", "缺少必要参数.");
				result.put("data", data);
				return result;
			}
			// 文件表单字段个数为零.
			if (isFileFormField == 0) {
				// 客户ID.
				Cache cache = MemCachedUtis.getMemCached();
				Principal principal = cache.get(map.get("token"));
				if (null != principal) {
					if (null != principal.getCreditUserInfo()) {
						// 获取房产信息.
						creditHouseInfo = creditHouseInfoService.get(map.get("houseId"));
						if (null == creditHouseInfo) {
							LOG.info("fn:updateHouseInfo,{查无此信息.}");
							result.put("state", "5");
							result.put("message", "查无此信息.");
							result.put("data", data);
							return result;
						}
					} else {
						LOG.info("fn:updateHouseInfo,{系统超时.}");
						result.put("state", "4");
						result.put("message", "系统超时.");
						result.put("data", data);
						return result;
					}
				} else {
					LOG.info("FORM DATA:{系统超时.}");
					result.put("state", "4");
					result.put("message", "系统超时.");
					result.put("data", data);
					return result;
				}
			}

			// 更新现住址信息.
			int houseInfoFlag = creditHouseInfoService.updateCreditHouseInfo(creditHouseInfo, map);
			if (houseInfoFlag == 1) {
				LOG.info("update success.");
			} else {
				LOG.info("update failure.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统异常.");
			result.put("data", data);
			LOG.error("fn:saveFamilyInfo,{" + e.getMessage() + "}");
			return result;
		}

		result.put("state", "0");
		result.put("message", "接口请求成功.");
		result.put("data", data);
		return result;
	}

	/**
	 * 
	 * 方法: updateCensusInfo <br>
	 * 描述: 修改户籍信息. <br>
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@POST
	@Path("/updateCensusInfo")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> updateCensusInfo(@Context HttpServletRequest request) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 数据域.
		Map<String, Object> data = new HashMap<String, Object>();
		// 键值对存储的Map接口，以HashMap(HashMap非线程安全，效率比较高)实现.
		Map<String, String> map = new HashMap<String, String>();
		// 普通文本表单字段.
		int isTextFormField = 0;
		// 文件表单字段.
		int isFileFormField = 0;
		// 上传的文件名.
		String fileName = "";
		// 新命名的文件名.
		String newFileName = "";
		// 文件格式.
		String fileFormat = "";

		// 在解析请求之前先判断请求类型是否为文件上传类型.
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		LOG.info("isMultipart:" + isMultipart + ".");
		// 是否为文件上传类型.
		if (isMultipart) {
			LOG.info("isMultipart:" + isMultipart + ",the file upload type.");
		} else {
			LOG.info("isMultipart:" + isMultipart + ",not the file upload type.");
		}

		/**
		 * 家庭信息.
		 */
		CreditCensusInfo creditCensusInfo = null;

		try {
			// 文件处理工厂.
			FileItemFactory factory = new DiskFileItemFactory();
			// 文件处理器.
			ServletFileUpload upload = new ServletFileUpload(factory);
			// 支持中文文件名.
			upload.setHeaderEncoding("utf-8");
			// 储存普通文本数据和文件数据.
			List<FileItem> items = new ArrayList<FileItem>();
			// 解析HTTP请求出来.
			items = upload.parseRequest(request);
			for (int i = 0; i < items.size(); i++) {
				FileItem item = (FileItem) items.get(i);
				// 普通文本数据.
				if (item.isFormField()) {
					// 记录普通文本表单字段个数.
					isTextFormField = isTextFormField + 1;
					// 将普通文本数据存入Map.
					map.put(item.getFieldName(), item.getString("UTF-8"));
					LOG.info("FORM DATA:{" + item.getFieldName() + " = " + item.getString("UTF-8") + "}");
					/**
					 * 判断参数是否传递.
					 */
					if (!item.getFieldName().equals("token") && !item.getFieldName().equals("censusId")) {
						LOG.info("fn:updateCensusInfo,{户籍信息参数名错误.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
					if (StringUtils.isBlank(item.getString("UTF-8"))) {
						LOG.info("updateCensusInfo,{户籍信息参数为null或空串.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
				} else { // 文件数据.
					// 文本表单字段个数.
					if (isTextFormField != CreditCensusInfoService.IS_TEXT_FORM_FIELD_2) {
						LOG.info("updateCensusInfo,{户籍信息参数不足" + CreditCensusInfoService.IS_TEXT_FORM_FIELD_2 + "个.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
					// 记录文件表单字段个数.
					isFileFormField = isFileFormField + 1;
					// 客户ID.
					Cache cache = MemCachedUtis.getMemCached();
					Principal principal = cache.get(map.get("token"));
					if (null != principal) {
						if (null != principal.getCreditUserInfo()) {
							if (isFileFormField == 1) {
								// 获取户籍信息.
								creditCensusInfo = creditCensusInfoService.get(map.get("censusId"));
								if (null == creditCensusInfo) {
									LOG.info("fn:updateCensusInfo,{查无此信息.}");
									result.put("state", "5");
									result.put("message", "查无此信息.");
									result.put("data", data);
									return result;
								}
								// 获取附件列表(物理删除).
								List<CreditAnnexFile> list = creditAnnexFileService.findCreditAnnexFileList(map.get("censusId"));
								for (CreditAnnexFile creditAnnexFile : list) {
									// 表数据删除.
									int flag = creditAnnexFileService.deleteCreditAnnexFileById(creditAnnexFile.getId());
									if (flag == 1) {
										LOG.info("CreditAnnexFile delete success.");
									} else {
										LOG.info("CreditAnnexFile delete failure.");
									}
									// 文件删除.
									File file = new File(FILE_PATH + File.separator + creditAnnexFile.getUrl());
									if (file.delete()) {
										LOG.info("File delete success.");
									} else {
										LOG.info("File delete failure.");
									}
								}
							}
						} else {
							LOG.info("fn:updateCensusInfo,{系统超时.}");
							result.put("state", "4");
							result.put("message", "系统超时.");
							result.put("data", data);
							return result;
						}
					} else {
						LOG.info("FORM DATA:{系统超时.}");
						result.put("state", "4");
						result.put("message", "系统超时.");
						result.put("data", data);
						return result;
					}

					// 上传的文件名(IE上是文件全路径，火狐等浏览器仅文件名).
					String name = item.getName();
					// 只获取文件名.
					fileName = name.substring(name.lastIndexOf(File.separator) + 1, name.length());
					// 文件扩展名
					fileFormat = fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());
					LOG.info("MULTIPART FORM DATA:{" + item.getFieldName() + " = " + fileName + "},文件格式:" + fileFormat);
					// 文件上传路径.
					String path = FileUploadUtils.createFilePath(fileFormat);
					LOG.info("PATH:" + path);
					// 新的文件名.
					newFileName = path.substring(path.lastIndexOf(File.separator) + 1, path.length());
					boolean flag = FileUploadUtils.uploadStream(newFileName, FILE_PATH, item.getInputStream());
					if (flag) {
						LOG.info("{原文件名:" + fileName + ",新文件名:" + newFileName + ",上传成功.}");
						/**
						 * 保存户籍信息附件.
						 */
						CreditAnnexFile creditAnnexFile = new CreditAnnexFile();
						creditAnnexFile.setOtherId(creditCensusInfo.getId()); // 户籍信息ID.
						creditAnnexFile.setUrl(path); // 图片保存路径.
						creditAnnexFile.setType(CreditAnnexFileService.CREDIT_ANNEX_FILE_TYPE_3); // 类型：户籍信息.
						creditAnnexFile.setRemark(CreditAnnexFileService.CREDIT_ANNEX_FILE_REMARK_3); // 备注.
						int tag = creditAnnexFileService.insertCreditAnnexFile(creditAnnexFile, i, IdGen.uuid());
						if (tag == 1) {
							LOG.info("PATH:" + path + ", save success.");
						} else {
							LOG.info("PATH:" + path + ", save failure.");
						}
					}

				}
			}

			LOG.info("MULTIPART FORM DATA IS TEXT FORM FIELD:{" + isTextFormField + "}");
			LOG.info("MULTIPART FORM DATA IS FILE FORM FIELD:{" + isFileFormField + "}");

			// 文本表单字段个数.
			if (isTextFormField != CreditCensusInfoService.IS_TEXT_FORM_FIELD_2) {
				LOG.info("FORM DATA:{户籍信息参数不足" + CreditCensusInfoService.IS_TEXT_FORM_FIELD_2 + "个.}");
				result.put("state", "2");
				result.put("message", "缺少必要参数.");
				result.put("data", data);
				return result;
			}
			// 文件表单字段个数为零.
			if (isFileFormField == 0) {
				// 客户ID.
				Cache cache = MemCachedUtis.getMemCached();
				Principal principal = cache.get(map.get("token"));
				if (null != principal) {
					if (null != principal.getCreditUserInfo()) {
						// 获取房产信息.
						creditCensusInfo = creditCensusInfoService.get(map.get("censusId"));
						if (null == creditCensusInfo) {
							LOG.info("fn:updateCensusInfo,{查无此信息.}");
							result.put("state", "5");
							result.put("message", "查无此信息.");
							result.put("data", data);
							return result;
						}
					} else {
						LOG.info("fn:updateCensusInfo,{系统超时.}");
						result.put("state", "4");
						result.put("message", "系统超时.");
						result.put("data", data);
						return result;
					}
				} else {
					LOG.info("FORM DATA:{系统超时.}");
					result.put("state", "4");
					result.put("message", "系统超时.");
					result.put("data", data);
					return result;
				}
			}

			// 更新户籍信息.
			int censusInfoFlag = creditCensusInfoService.updateCreditCensusInfo(creditCensusInfo, map);
			if (censusInfoFlag == 1) {
				LOG.info("update success.");
			} else {
				LOG.info("update failure.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统异常.");
			result.put("data", data);
			LOG.error("fn:saveFamilyInfo,{" + e.getMessage() + "}");
			return result;
		}

		result.put("state", "0");
		result.put("message", "接口请求成功.");
		result.put("data", data);
		return result;
	}

	/**
	 * 新增抵押物信息
	 * 
	 * @param token
	 * @param modelnumber
	 *            车辆型号
	 * @param platenumber
	 *            车辆号码
	 * @param buyprice
	 *            购买价格
	 * @param mileage
	 *            行程里数
	 * @param collateralprice
	 *            抵押预估价
	 * @return
	 */
	@POST
	@Path("/saveCollateralInfo")
	public Map<String, Object> saveCollateralInfo(@FormParam("token") String token, @FormParam("modelnumber") String modelNumber, @FormParam("platenumber") String plateNumber, @FormParam("buyprice") String buyPrice, @FormParam("mileage") String mileage, @FormParam("buyDate") String buyDate) {

		Map<String, Object> result = new HashMap<String, Object>();
		CreditCollateralInfo collateraInfo = new CreditCollateralInfo();
		// N1.判断必要参数是否为空
		if (StringUtils.isBlank(token) || StringUtils.isBlank(modelNumber) || StringUtils.isBlank(plateNumber) || StringUtils.isBlank(buyPrice) || StringUtils.isBlank(buyDate)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}

		try {
			// N2.通过token获取借款用户信息
			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			if (null != principal && null != principal.getCreditUserInfo()) {
				CreditUserInfo userInfo = principal.getCreditUserInfo();
				// 先判断车牌号是否唯一
				collateraInfo.setPlateNumber(plateNumber);
				List<CreditCollateralInfo> list = creditCollateralService.findList(collateraInfo);
				if(list!=null && list.size()>0){
					result.put("state", "5");
					result.put("message", "该以抵押物信息已存在");
					return result;
				}
				// N4.新增抵押物信息
				collateraInfo.setId(IdGen.uuid());
				collateraInfo.setCreditUserId(userInfo.getId());
				collateraInfo.setModelNumber(modelNumber);
				
				collateraInfo.setBuyPrice(buyPrice);
				collateraInfo.setMileage(mileage);
				collateraInfo.setBuyDate(buyDate);
				collateraInfo.setState(CreditCollateralInfoService.CREDIT_COLLATERAL_INFO_STATE_1);
				collateraInfo.setCreateDate(new Date());
				collateraInfo.setUpdateDate(new Date());
				int i = creditCollateralInfoDao.insert(collateraInfo);
				if (i > 0) {
					result.put("state", "0");
					result.put("message", "添加抵押物信息成功");
					result.put("collateraId", collateraInfo.getId());
					principal.setCreditUserInfo(userInfo);
					cache.set(token, 1200, principal);
				} else {
					result.put("state", "1");
					result.put("message", "添加抵押物信息失败");
					principal.setCreditUserInfo(userInfo);
					cache.set(token, 1200, principal);
				}
			} else {
				result.put("state", "4");
				result.put("message", "系统超时");
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "3");
			result.put("message", "系统异常");
		}
		return result;
	}

	/**
	 * 获取抵押物信息
	 * 
	 * @param token
	 * @return
	 */
	@POST
	@Path("/getCollateralInfoList")
	public Map<String, Object> getCollateralInfoList(@FormParam("token") String token) {

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();

		if (StringUtils.isBlank(token)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		try {

			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			if (null != principal && null != principal.getCreditUserInfo()) {
				CreditCollateralInfo creditCollateralInfo = new CreditCollateralInfo();
				creditCollateralInfo.setCreditUserId(principal.getCreditUserInfo().getId());
				Page<CreditCollateralInfo> page = new Page<CreditCollateralInfo>();
				page.setPageNo(1);
				page.setPageSize(5);
				page.setOrderBy("create_date DESC");
				Page<CreditCollateralInfo> collateralInfoPage = creditCollateralService.findPage(page, creditCollateralInfo);
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				List<CreditCollateralInfo> collateralList = collateralInfoPage.getList();
				if (collateralList != null && collateralList.size() > 0) {
					for (int i = 0; i < collateralList.size(); i++) {
						Map<String, Object> map = new HashMap<String, Object>();
						creditCollateralInfo = collateralList.get(i);
						map.put("id", creditCollateralInfo.getId());
						map.put("modelnumber", creditCollateralInfo.getModelNumber()); // 车辆型号
						map.put("platenumber", creditCollateralInfo.getPlateNumber()); // 车辆号码
						map.put("buyprice", creditCollateralInfo.getBuyPrice()); // 购买价格
						map.put("mileage", creditCollateralInfo.getMileage()); // 行程里数
						map.put("collateralprice", creditCollateralInfo.getCollateralPrice()); // 抵押预估价
						map.put("buyDate", creditCollateralInfo.getBuyDate()); // 购买日期
						map.put("state", creditCollateralInfo.getState()); // 审核状态
						list.add(map);
					}
					data.put("collaterallist", list);
					result.put("state", "0");
					result.put("message", "查询用户抵押物信息成功");
					result.put("data", data);
					return result;
				} else {
					data.put("collaterallist", list);
					result.put("state", "0");
					result.put("message", "查无数据");
					result.put("data", data);
					return result;
				}
			} else {
				result.put("state", "4");
				result.put("message", "系统超时");
				result.put("data", "");
				return result;
			}

		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", "系统异常");
			result.put("state", "3");
			result.put("data", null);
			return result;
		}

	}

	/**
	 * 获取单条抵押物信息
	 * 
	 * @param token
	 * @param collateraId
	 * @return
	 */
	@POST
	@Path("/getCollateraInfo")
	public Map<String, Object> getCollateraInfo(@FormParam("token") String token, @FormParam("collateraId") String collateraId) {

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		CreditCollateralInfo collateraInfo = new CreditCollateralInfo();
		// N1.判断必要参数是否为空
		if (StringUtils.isBlank(token) || StringUtils.isBlank(collateraId)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		try {
			// N2.通过token获取借款用户信息
			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			if (null != principal && null != principal.getCreditUserInfo()) {
				collateraInfo = creditCollateralInfoDao.get(collateraId);
				if (collateraInfo != null) {
					data.put("Id", collateraInfo.getId());
					data.put("modelnumber", collateraInfo.getModelNumber()); // 车辆型号
					data.put("platenumber", collateraInfo.getPlateNumber()); // 车辆号码
					data.put("buyprice", collateraInfo.getBuyPrice()); // 购买价格
					data.put("mileage", collateraInfo.getMileage()); // 行程里数
					data.put("collateralprice", collateraInfo.getCollateralPrice()); // 抵押预估价
					data.put("buyDate", collateraInfo.getBuyDate());
					result.put("state", "0");
					result.put("message", "查询成功");
					result.put("data", data);
					principal.setCreditUserInfo(principal.getCreditUserInfo());
					cache.set(token, 1200, principal);
				} else {
					result.put("state", "0");
					result.put("message", "查无数据");
					result.put("data", null);
				}
			} else {
				result.put("state", "4");
				result.put("message", "系统超时");
			}

		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "3");
			result.put("message", "系统异常");
		}
		return result;
	}

	/**
	 * 修改抵押物信息
	 * 
	 * @param token
	 * @param collateraId
	 * @param modelNumber
	 * @param plateNumber
	 * @param buyPrice
	 * @param mileage
	 * @param collateralPrice
	 * @return
	 */
	@POST
	@Path("/updateCollateralInfo")
	public Map<String, Object> updateCollateralInfo(@FormParam("token") String token, @FormParam("collateraId") String collateraId, @FormParam("modelnumber") String modelNumber, @FormParam("platenumber") String plateNumber, @FormParam("buyprice") String buyPrice, @FormParam("mileage") String mileage, @FormParam("buyDate") String buyDate) {

		Map<String, Object> result = new HashMap<String, Object>();
		CreditCollateralInfo collateraInfo = new CreditCollateralInfo();
		// N1.判断必要参数是否为空
		if (StringUtils.isBlank(token) || StringUtils.isBlank(collateraId) || StringUtils.isBlank(modelNumber) || StringUtils.isBlank(plateNumber) || StringUtils.isBlank(buyPrice) || StringUtils.isBlank(buyDate)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}

		try {
			// N2.通过token获取借款用户信息
			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			if (null != principal && null != principal.getCreditUserInfo()) {
				CreditUserInfo userInfo = principal.getCreditUserInfo();
				collateraInfo = creditCollateralInfoDao.get(collateraId);
				if (collateraInfo != null) {
					collateraInfo.setId(collateraInfo.getId());
					collateraInfo.setCreditUserId(userInfo.getId());
					collateraInfo.setModelNumber(modelNumber);
					collateraInfo.setPlateNumber(plateNumber);
					collateraInfo.setBuyPrice(buyPrice);
					collateraInfo.setMileage(mileage);
					collateraInfo.setBuyDate(buyDate);
					collateraInfo.setState(CreditCollateralInfoService.CREDIT_COLLATERAL_INFO_STATE_1);
					collateraInfo.setUpdateDate(new Date());
					int i = creditCollateralInfoDao.update(collateraInfo);
					if (i > 0) {
						result.put("state", "0");
						result.put("message", "修改抵押物信息成功");
						principal.setCreditUserInfo(userInfo);
						cache.set(token, 1200, principal);
					} else {
						result.put("state", "1");
						result.put("message", "修改抵押物信息失败");
						principal.setCreditUserInfo(userInfo);
						cache.set(token, 1200, principal);
					}
				} else {
					result.put("state", "3");
					result.put("message", "未查询到抵押物信息");
				}
			} else {
				result.put("state", "4");
				result.put("message", "系统超时");
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "5");
			result.put("message", "系统异常");
		}
		return result;
	}

	/**
	 * 删除抵押物信息
	 * 
	 * @param token
	 * @param collateraId
	 * @return
	 */
	@POST
	@Path("/deleteCollateraInfo")
	public Map<String, Object> deleteCollateraInfo(@FormParam("token") String token, @FormParam("collateraId") String collateraId) {

		Map<String, Object> result = new HashMap<String, Object>();
		CreditCollateralInfo collateraInfo = new CreditCollateralInfo();
		// N1.判断必要参数是否为空
		if (StringUtils.isBlank(token) || StringUtils.isBlank(collateraId)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		try {
			// N2.通过token获取借款用户信息
			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			if (null != principal && null != principal.getCreditUserInfo()) {
				CreditUserInfo userInfo = principal.getCreditUserInfo();
				collateraInfo = creditCollateralInfoDao.get(collateraId);
				if (collateraInfo != null) {
					collateraInfo.setId(collateraInfo.getId());
					collateraInfo.setUpdateDate(new Date());
					int i = creditCollateralInfoDao.delete(collateraInfo);
					if (i > 0) {
						result.put("state", "0");
						result.put("message", "抵押物信息删除成功");
					} else {
						result.put("state", "1");
						result.put("message", "抵押物信息删除失败");
					}
				} else {
					result.put("state", "3");
					result.put("message", "未查询到抵押物信息");
					principal.setCreditUserInfo(userInfo);
					cache.set(token, 1200, principal);
				}
			} else {
				result.put("state", "4");
				result.put("message", "系统超时");
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "5");
			result.put("message", "系统异常");
		}
		return result;
	}
}
