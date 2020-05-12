package com.power.platform.userinfo.service;

import java.net.URLEncoder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.cunguanbao.api.utils.APIUtils;
import com.power.platform.activity.dao.ZtmgPartnerPlatformDao;
import com.power.platform.cgb.dao.CgbUserAccountDao;
import com.power.platform.cgb.entity.CgbUserAccount;
import com.power.platform.cgbpay.config.ServerURLConfig;
import com.power.platform.common.config.Global;
import com.power.platform.common.exception.WinException;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.service.CrudService;
import com.power.platform.sys.utils.HttpUtil;
import com.power.platform.userinfo.dao.UserInfoDao;
import com.power.platform.userinfo.entity.UserInfo;

/**
 * 用户信息管理Service
 * 
 * @author jiajunfeng
 * @version 2015-12-16
 */

@Service("userInfoService")
@Transactional(readOnly = true)
public class UserInfoService extends CrudService<UserInfo> {

	/**
	 * 性别，男.
	 */
	public static final Integer SEX_TYPE_1 = 1;
	/**
	 * 性别，女.
	 */
	public static final Integer SEX_TYPE_2 = 2;
	/**
	 * 证件是否通过验证，通过.
	 */
	public static final Integer CERTIFICATE_CHECKED_1 = 1;
	/**
	 * 证件是否通过验证，没有通过..
	 */
	public static final Integer CERTIFICATE_CHECKED_2 = 2;
	/**
	 * 证件类型，户口薄.
	 */
	public static final Integer CERTIFICATE_TYPE_1 = 1;
	/**
	 * 证件类型，身份证.
	 */
	public static final Integer CERTIFICATE_TYPE_2 = 2;
	/**
	 * 证件类型，护照.
	 */
	public static final Integer CERTIFICATE_TYPE_3 = 3;
	/**
	 * 证件类型，军官证.
	 */
	public static final Integer CERTIFICATE_TYPE_4 = 4;

	// 商户号
	private static final String merchantId = Global.getConfig("merchantId");
	// 存管系统为你分配的，用来对AES私钥加密，同时生成签名
	private static final String merchantRsaPublicKey = Global.getConfig("merchantRsaPublicKey");

	// 商户自己的RSA私钥
	private static final String merchantRsaPrivateKey = Global.getConfig("merchantRsaPrivateKey");

	private static final Logger logger = Logger.getLogger(UserInfoService.class);

	@Resource
	private UserInfoDao userInfoDao;
	@Resource
	private ZtmgPartnerPlatformDao ztmgPartnerPlatformDao;
	@Resource
	private CgbUserAccountDao cgbUserAccountDao;

	protected CrudDao<UserInfo> getEntityDao() {

		return userInfoDao;
	}

	/**
	 * 
	 * 方法: findPageByBirthDay <br>
	 * 描述: 存管系统客户信息列表查询 <br>
	 * 作者: Roy <br>
	 * 时间: 2019年11月7日 下午3:23:44
	 * 
	 * @param page
	 * @param entity
	 * @return
	 */
	public Page<UserInfo> findPageByBirthDay(Page<UserInfo> page, UserInfo entity) {

		page.setOrderBy("a.register_date ASC");
		entity.setPage(page);
		page.setList(userInfoDao.findList1(entity));
		return page;
	}

	public Page<UserInfo> findPage(Page<UserInfo> page, UserInfo entity) {

		entity.setPage(page);
		List<UserInfo> list = userInfoDao.findList(entity);
		UserInfo parent = null;
		for (UserInfo userInfo : list) {
			if (null != userInfo.getRecommendUserId() && !"".equals(userInfo.getRecommendUserId())) {
				parent = userInfoDao.get(userInfo.getRecommendUserId());
				if (parent == null) {
					parent = userInfoDao.getCgb(userInfo.getRecommendUserId());
				}
				if (null != parent) {
					userInfo.setParentPhone(parent.getName());
				}
			} else {
				userInfo.setParentPhone(" ");
			}
		}
		page.setList(list);
		return page;
	}

	/**
	 * 存管宝出借人信息
	 * 
	 * @param page
	 * @param entity
	 * @return
	 */
	public Page<UserInfo> findPage1(Page<UserInfo> page, UserInfo entity) {

		entity.setPage(page);
		List<UserInfo> list = userInfoDao.findList1(entity);
		UserInfo parent = null;
		for (UserInfo userInfo : list) {
			if (null != userInfo.getRecommendUserId() && !"".equals(userInfo.getRecommendUserId())) {
				parent = userInfoDao.getCgb(userInfo.getRecommendUserId());
				if (null != parent) {
					userInfo.setParentPhone(parent.getName());
				}
			} else {
				userInfo.setParentPhone(" ");
			}
		}
		page.setList(list);
		return page;
	}

	// public List<UserInfo> findListForRegist(String recommendUserId){
	// List<UserInfo> list = userInfoDao.findListForRegist(recommendUserId);
	// return list;
	// }

	public List<UserInfo> findListForRegist(UserInfo userInfo) {

		List<UserInfo> list = userInfoDao.findListForRegist(userInfo);
		return list;
	}

	/**
	 * 修改密码
	 */

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void updateUser(UserInfo userInfo, String resetMobile) {

		System.out.println("更改时间" + userInfo.getUpdateDate());
		System.out.println("更改密码" + userInfo.getPwd());
		userInfo.setName(resetMobile);
		userInfoDao.updateUser(userInfo);

	}

	/**
	 * 修改邮箱校验码
	 */

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void updateUserByName(UserInfo userInfo) {

		System.out.println("发送验证邮箱校验码，给校验码赋值。。。");
		userInfoDao.updateByName(userInfo);
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int updateUserInfo(UserInfo userInfo) {

		int flag = 0;
		try {
			flag = userInfoDao.update(userInfo);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("fn:updateUserInfo,{异常：" + e.getMessage() + "}");
		}
		return flag;
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void updateUserPhone(UserInfo userInfo) {

		userInfoDao.updateUserPhone(userInfo);
	}

	/**
	 * 用户授权Pc端web
	 * 
	 * @param user
	 * @param grant
	 * @return
	 */
	public Map<String, String> userAuthorizationWeb(UserInfo user, String grant) throws WinException, Exception {

		// TODO Auto-generated method stub
		/*
		 * 构造请求参数
		 */
		String userId = user.getId();
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", userId);
		params.put("grantList", grant);
		params.put("service", "web.member.authorization.create");
		params.put("method", "RSA");
		params.put("merchantId", merchantId);
		params.put("source", "1");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String requestTime = sdf.format(new Date());
		params.put("requestTime", requestTime);
		params.put("version", "1.0.0");
		params.put("reqSn", UUID.randomUUID().toString().replace("-", ""));
		params.put("returnUrl", ServerURLConfig.BACK_BACKTO_URL_WEB);
		params.put("callbackUrl", ServerURLConfig.BACK_AUTO_URL);
		// 生成签名
		String sign = APIUtils.createSign(merchantRsaPrivateKey, params, "RSA");
		params.put("signature", sign);
		String jsonString = JSON.toJSONString(params);
		System.out.println("用户授权Pc端web[请求参数]" + jsonString);
		// 加密
		Map<String, String> encryptRet = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);

		String data = encryptRet.get("data");
		data = URLEncoder.encode(data, "UTF-8");
		String tm = encryptRet.get("tm");
		tm = URLEncoder.encode(tm, "UTF-8");
		encryptRet.put("tm", tm);
		encryptRet.put("data", data);
		encryptRet.put("merchantId", merchantId);
		// 返回订单信息
		return encryptRet;
	}

	/**
	 * 用户授权手机端H5
	 * 
	 * @param user
	 * @param grant
	 * @return
	 */
	public Map<String, String> userAuthorizationH5(String userId, String grant, String from) throws WinException, Exception {

		// TODO Auto-generated method stub
		/*
		 * 构造请求参数
		 */
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", userId);
		params.put("grantList", grant);
		params.put("service", "h5.p2p.member.authorization.create");
		params.put("method", "RSA");
		params.put("merchantId", merchantId);
		if (from.equals("2")) {
			params.put("mobileType", "22");
		}
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
		params.put("callbackUrl", ServerURLConfig.BACK_AUTO_URL);
		// 生成签名
		String sign = APIUtils.createSign(merchantRsaPrivateKey, params, "RSA");
		params.put("signature", sign);
		String jsonString = JSON.toJSONString(params);
		System.out.println("用户授权手机端H5[请求参数]" + jsonString);
		// 加密
		Map<String, String> encryptRet = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);

		String data = encryptRet.get("data");
		data = URLEncoder.encode(data, "UTF-8");
		String tm = encryptRet.get("tm");
		tm = URLEncoder.encode(tm, "UTF-8");
		encryptRet.put("tm", tm);
		encryptRet.put("data", data);
		encryptRet.put("merchantId", merchantId);
		// 返回订单信息
		return encryptRet;
	}

	public UserInfo getCgb(String userId) {

		// TODO Auto-generated method stub
		return userInfoDao.getCgb(userId);
	}

	public List<UserInfo> findList1(UserInfo userInfo) {

		// TODO Auto-generated method stub
		return userInfoDao.findList1(userInfo);
	}

	/**
	 * 更新用户邀请人手机号字段
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void updateUserInfoInvite() {

		// TODO Auto-generated method stub
		List<UserInfo> list = userInfoDao.findRecommendUser();
		if (list != null) {
			System.out.println("需更新的用户数为" + list.size());
			logger.info("需更新的用户数为" + list.size());
			for (UserInfo userInfo : list) {
				String recommendUserId = userInfo.getRecommendUserId();
				if (recommendUserId != null) {
					// 查询用户邀请人手机号
					UserInfo user = userInfoDao.get(recommendUserId);
					if (user != null) {
						userInfo.setRecommendUserPhone(user.getName());
						int i = userInfoDao.update(userInfo);
						if (i > 0) {
							System.out.println("更新用户" + userInfo.getName() + "字段成功");
							logger.info("更新用户" + userInfo.getName() + "字段成功");
						}
					}
				}
			}
		}

	}

	/**
	 * 用户ID查询 用户余额
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> getUserCgbAmount(String userId) throws Exception {

		// TODO Auto-generated method stub
		// 根据用户ID查询 用户余额
		/*
		 * 构造请求参数
		 */
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", userId);
		params.put("service", "p2p.trade.balance.search");
		params.put("method", "RSA");
		params.put("merchantId", merchantId);
		params.put("source", "1");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String requestTime = sdf.format(new Date());
		params.put("requestTime", requestTime);
		params.put("version", "1.0.0");
		params.put("reqSn", UUID.randomUUID().toString().replace("-", ""));
		// 生成签名
		String sign = APIUtils.createSign(merchantRsaPrivateKey, params, "RSA");
		params.put("signature", sign);
		String jsonString = JSON.toJSONString(params);
		System.out.println("出借用户余额[请求参数]" + jsonString);
		// 加密
		Map<String, String> encryptRet = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
		encryptRet.put("merchantId", merchantId);
		// 返回订单信息
		System.out.println(encryptRet);

		// 发送请求

		String respo = HttpUtil.sendPost(ServerURLConfig.CGB_URL, encryptRet);
		System.out.println("返回结果报文" + respo);
		JSONObject jsonObject = JSONObject.parseObject(respo);
		String respTm = (String) jsonObject.get("tm");
		String respData = (String) jsonObject.getString("data");
		String jsonRet = APIUtils.decryptDataBySSL(respTm, respData, merchantRsaPrivateKey);
		Map<String, String> maps = JSON.parseObject(jsonRet, new TypeReference<Map<String, String>>() {
		});

		System.out.println("解密结果:" + maps);
		// 返回订单信息
		return maps;
	}

	/**
	 * 查询所有连连用户
	 * 
	 * @return
	 */

	public List<UserInfo> findLLUserList() {

		// TODO Auto-generated method stub
		return userInfoDao.findLLUserList();
	}

	/**
	 * 出借用户信息迁移
	 * 
	 * @param userInfo
	 * @return
	 * @throws Exception
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> importUserInfo(UserInfo userInfo, String userType) throws Exception {

		// TODO Auto-generated method stub
		Map<String, String> params = new HashMap<String, String>();
		// 接口请求参数
		String orderId = UUID.randomUUID().toString().replace("-", "");

		params.put("orderId", orderId);
		params.put("userId", userInfo.getId());
		params.put("bizType", userType);
		params.put("mobile", userInfo.getName());
		params.put("realName", userInfo.getRealName());
		params.put("certType", "IDC");
		params.put("certNo", userInfo.getCertificateNo());
		// 公共请求参数
		params.put("service", "p2p.member.person.import");
		params.put("method", "RSA");
		params.put("merchantId", merchantId);
		params.put("source", "1");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String requestTime = sdf.format(new Date());
		params.put("requestTime", requestTime);
		params.put("version", "1.0.0");
		params.put("reqSn", UUID.randomUUID().toString().replace("-", ""));
		// 生成签名
		String sign = APIUtils.createSign(merchantRsaPrivateKey, params, "RSA");
		params.put("signature", sign);
		String jsonString = JSON.toJSONString(params);
		System.out.println("出借用户信息迁移[请求参数]" + jsonString);
		// 加密
		Map<String, String> encryptRet = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
		encryptRet.put("merchantId", merchantId);
		// 返回订单信息
		System.out.println(encryptRet);

		// 发送请求

		String respo = HttpUtil.sendPost(ServerURLConfig.CGB_URL, encryptRet);
		System.out.println("返回结果报文" + respo);
		JSONObject jsonObject = JSONObject.parseObject(respo);
		String respTm = (String) jsonObject.get("tm");
		String respData = (String) jsonObject.getString("data");
		String jsonRet = APIUtils.decryptDataBySSL(respTm, respData, merchantRsaPrivateKey);
		Map<String, String> maps = JSON.parseObject(jsonRet, new TypeReference<Map<String, String>>() {
		});

		System.out.println("解密结果:" + maps);
		/**
		 * 进行状态位记录
		 */
		if (maps.get("respSubCode").equals("000000")) {

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
			int q = cgbUserAccountDao.insert(userAccountInfo);

			if (q > 0) {
				logger.info("用户ID为" + userInfo.getId() + "账户生成成功");
			}

			// 更新存管宝绑定银行卡状态为待绑定
			userInfo.setCgbBindBankCardState(1);
			int i = userInfoDao.update(userInfo);
			if (i > 0) {
				logger.info("用户ID为" + userInfo.getId() + "迁移成功");
			}
		}
		// 返回订单信息
		return maps;
	}

	/**
	 * 借款用户信息迁移
	 * 
	 * @param userInfo
	 * @return
	 * @throws Exception
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> importCreditUserInfo(UserInfo userInfo, String userType, String phone) throws Exception {

		// TODO Auto-generated method stub
		Map<String, String> params = new HashMap<String, String>();
		// 接口请求参数
		String orderId = UUID.randomUUID().toString().replace("-", "");

		params.put("orderId", orderId);
		params.put("userId", userInfo.getId());
		params.put("bizType", userType);
		params.put("mobile", phone);
		params.put("realName", userInfo.getRealName());
		params.put("certType", "IDC");
		params.put("certNo", userInfo.getCertificateNo());
		// 公共请求参数
		params.put("service", "p2p.member.person.import");
		params.put("method", "RSA");
		params.put("merchantId", merchantId);
		params.put("source", "1");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String requestTime = sdf.format(new Date());
		params.put("requestTime", requestTime);
		params.put("version", "1.0.0");
		params.put("reqSn", UUID.randomUUID().toString().replace("-", ""));
		// 生成签名
		String sign = APIUtils.createSign(merchantRsaPrivateKey, params, "RSA");
		params.put("signature", sign);
		String jsonString = JSON.toJSONString(params);
		System.out.println("借款用户信息迁移[请求参数]" + jsonString);
		// 加密
		Map<String, String> encryptRet = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
		encryptRet.put("merchantId", merchantId);
		// 返回订单信息
		System.out.println(encryptRet);

		// 发送请求

		String respo = HttpUtil.sendPost(ServerURLConfig.CGB_URL, encryptRet);
		System.out.println("返回结果报文" + respo);
		JSONObject jsonObject = JSONObject.parseObject(respo);
		String respTm = (String) jsonObject.get("tm");
		String respData = (String) jsonObject.getString("data");
		String jsonRet = APIUtils.decryptDataBySSL(respTm, respData, merchantRsaPrivateKey);
		Map<String, String> maps = JSON.parseObject(jsonRet, new TypeReference<Map<String, String>>() {
		});

		System.out.println("解密结果:" + maps);
		// 返回订单信息
		return maps;
	}

}