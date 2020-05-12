package com.power.platform.pay.recharge.service;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.cunguanbao.api.utils.APIUtils;
import com.power.platform.cgbpay.config.ServerURLConfig;
import com.power.platform.common.config.Global;
import com.power.platform.common.exception.WinException;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.JedisUtils;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.pay.recharge.dao.UserRechargeDao;
import com.power.platform.pay.recharge.entity.UserRecharge;
import com.power.platform.userinfo.dao.UserInfoDao;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.userinfo.entity.UserLog;

@Service("newRechargeService")
public class NewRechargeService extends CrudService<UserRecharge> {

	@Resource
	private UserRechargeDao userRechargeDao;

	@Resource
	private UserInfoDao userInfoDao;

	// 商户号
	private static final String merchantId = Global.getConfig("merchantId");
	// 存管系统为你分配的，用来对AES私钥加密，同时生成签名
	private static final String merchantRsaPublicKey = Global.getConfig("merchantRsaPublicKey");

	// 商户自己的RSA私钥
	private static final String merchantRsaPrivateKey = Global.getConfig("merchantRsaPrivateKey");

	@Override
	protected CrudDao<UserRecharge> getEntityDao() {
		return userRechargeDao;
	}

	/**
	 * 充值---pc端web端
	 * 
	 * @param ip
	 * @param amount
	 * @param token 
	 * @return
	 * @throws WinException
	 * @throws Exception
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> authRechargeWeb(String ip, String amount, String userId, String accountId, String type) throws WinException, Exception {

		/*
		 * 构造请求参数
		 */
		Map<String, String> params = new HashMap<String, String>();
		String orderId = UUID.randomUUID().toString().replace("-", "");
		Double rAmount = 100 * NumberUtils.scaleDouble(Double.valueOf(amount));
		BigDecimal rechargeAmount = new BigDecimal(rAmount).setScale(0, BigDecimal.ROUND_HALF_UP);
		String rechargeAmountString = rechargeAmount.toString();

		params.put("orderId", orderId);
		params.put("userId", userId);
		params.put("amount", rechargeAmountString);
		params.put("currency", "CNY");
		params.put("service", "web.p2p.trade.account.recharge");
		params.put("method", "RSA");
		params.put("merchantId", merchantId);
		params.put("source", "1");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String requestTime = sdf.format(new Date());
		params.put("requestTime", requestTime);
		params.put("version", "1.0.0");
		params.put("reqSn", UUID.randomUUID().toString().replace("-", ""));
		if (type.equals("02")) { // 借款人
			params.put("returnUrl", ServerURLConfig.BACK_BACKTO_URL_BORROWWEB + "&id=" + userId);
		} else { // 出借人
			params.put("returnUrl", ServerURLConfig.BACK_BACKTO_URL_WEB);
		}

		params.put("callbackUrl", ServerURLConfig.BACK_RECHARGE_URL);
		// 生成签名
		String sign = APIUtils.createSign(merchantRsaPrivateKey, params, "RSA");
		params.put("signature", sign);
		String jsonString = JSON.toJSONString(params);
		System.out.println("充值PC端Web[请求参数]" + jsonString);
		// 加密
		Map<String, String> encryptRet = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
		encryptRet.put("merchantId", merchantId);

		String data = encryptRet.get("data");
		data = URLEncoder.encode(data, "UTF-8");
		String tm = encryptRet.get("tm");
		tm = URLEncoder.encode(tm, "UTF-8");
		encryptRet.put("tm", tm);
		encryptRet.put("data", data);

		// 记录充值信息
		insertRecharge(Double.valueOf(amount), ip, orderId, userId, accountId);

		// 返回订单信息
		System.out.println(encryptRet);
		return encryptRet;
	}

	/**
	 * 
	 * methods: offlineRechargeWeb <br>
	 * description: 转账充值（PC端WEB）. <br>
	 * author: Roy <br>
	 * date: 2019年4月1日 上午11:47:27
	 * 
	 * @param ip
	 *            用户终端IP
	 * @param amount
	 *            充值金额
	 * @param userId
	 *            用户唯一标识ID
	 * @param accountId
	 *            用户账户唯一标识ID
	 * @param type
	 *            服务端类型
	 * @return
	 * @throws WinException
	 * @throws Exception
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> offlineRechargeWeb(String ip, String amount, String userId, String accountId) throws WinException, Exception {

		Map<String, String> params = new HashMap<String, String>();
		String orderId = UUID.randomUUID().toString().replace("-", "");
		Double rAmount = 100 * NumberUtils.scaleDouble(Double.valueOf(amount));
		BigDecimal rechargeAmount = new BigDecimal(rAmount).setScale(0, BigDecimal.ROUND_HALF_UP);
		String rechargeAmountString = rechargeAmount.toString();

		params.put("orderId", orderId);
		params.put("userId", userId);
		params.put("amount", rechargeAmountString);
		params.put("currency", "CNY");
		params.put("remark", "用户通过转账的方式将资金充值至存管帐户");
		params.put("service", "web.p2p.trade.account.offlineRecharge");
		params.put("method", "RSA");
		params.put("merchantId", merchantId);
		params.put("source", "1");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String requestTime = sdf.format(new Date());
		params.put("requestTime", requestTime);
		params.put("version", "1.0.0");
		params.put("reqSn", UUID.randomUUID().toString().replace("-", ""));
		// 借款端页面响应地址（账户管理）.
		String returnUrlParam = "?backto=borrowingWebAuthorization&id=".concat(userId);
		params.put("returnUrl", ServerURLConfig.RETURN_URL_BORROWING_WEB_AUTHORIZATION.concat(returnUrlParam));
		// 转账充值异步回调.
		params.put("callbackUrl", ServerURLConfig.BACK_OFFLINE_RECHARGE_URL);
		// 生成签名
		String sign = APIUtils.createSign(merchantRsaPrivateKey, params, "RSA");
		params.put("signature", sign);
		String jsonString = JSON.toJSONString(params);
		// 加密
		Map<String, String> encryptRet = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
		encryptRet.put("merchantId", merchantId);

		String data = encryptRet.get("data");
		data = URLEncoder.encode(data, "UTF-8");
		encryptRet.put("data", data);
		String tm = encryptRet.get("tm");
		tm = URLEncoder.encode(tm, "UTF-8");
		encryptRet.put("tm", tm);

		// 充值订单信息.
		UserRecharge userRecharge = new UserRecharge();
		userRecharge.setAccountId(accountId);
		userRecharge.setUserId(userId);
		userRecharge.setAmount(Double.valueOf(amount));
		userRecharge.setBeginBeginDate(new Date());
		userRecharge.setBeginDate(new Date());
		userRecharge.setCreateDate(new Date());
		userRecharge.setFeeAmount(0d);
		userRecharge.setState(UserRecharge.RECHARGE_DOING); // 充值申请中
		userRecharge.setBankAccount("-----------");
		userRecharge.setPlatForm(UserRecharge.OFFLINE_RECHARGE); // 转账充值
		userRecharge.setIp(ip);
		userRecharge.setId(orderId);// 订单号
		userRecharge.setSn(orderId);// 订单号
		int insertFlag = userRechargeDao.insert(userRecharge);
		if (insertFlag == 1) {
			logger.info("借款端转账充值，订单信息生成成功！");
		} else {
			logger.info("借款端转账充值，订单信息生成失败！");
		}

		logger.info("借款端转账充值，订单信息：" + encryptRet);

		return encryptRet;
	}

	/**
	 * 大额充值---pc端web端
	 * 
	 * @param ip
	 * @param amount
	 * @param token
	 * @return
	 * @throws WinException
	 * @throws Exception
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> largeRechargeWeb(String ip, String amount, String userId, String accountId, String type) throws WinException, Exception {

		/*
		 * 构造请求参数
		 */
		Map<String, String> params = new HashMap<String, String>();
		String orderId = UUID.randomUUID().toString().replace("-", "");
		Double rAmount = 100 * NumberUtils.scaleDouble(Double.valueOf(amount));
		BigDecimal rechargeAmount = new BigDecimal(rAmount).setScale(0, BigDecimal.ROUND_HALF_UP);
		String rechargeAmountString = rechargeAmount.toString();

		params.put("orderId", orderId);
		params.put("userId", userId);
		params.put("amount", rechargeAmountString);
		params.put("currency", "CNY");
		params.put("service", "web.p2p.trade.account.offlineRecharge");
		params.put("method", "RSA");
		params.put("merchantId", merchantId);
		params.put("source", "1");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String requestTime = sdf.format(new Date());
		params.put("requestTime", requestTime);
		params.put("version", "1.0.0");
		params.put("reqSn", UUID.randomUUID().toString().replace("-", ""));
		if (type.equals("02")) {
			params.put("returnUrl", ServerURLConfig.BACK_BACKTO_URL_BORROWWEB + "&id=" + userId);
		} else {
			params.put("returnUrl", ServerURLConfig.BACK_BACKTO_URL_WEB);
		}

		params.put("callbackUrl", ServerURLConfig.BACK_RECHARGE_URL_LARGE);
		// 生成签名
		String sign = APIUtils.createSign(merchantRsaPrivateKey, params, "RSA");
		params.put("signature", sign);
		String jsonString = JSON.toJSONString(params);
		System.out.println("充值PC端Web[请求参数]" + jsonString);
		// 加密
		Map<String, String> encryptRet = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
		encryptRet.put("merchantId", merchantId);

		String data = encryptRet.get("data");
		data = URLEncoder.encode(data, "UTF-8");
		String tm = encryptRet.get("tm");
		tm = URLEncoder.encode(tm, "UTF-8");
		encryptRet.put("tm", tm);
		encryptRet.put("data", data);

		// 记录充值信息
		largeInsertRecharge(Double.valueOf(amount), ip, orderId, userId, accountId);

		// 返回订单信息
		System.out.println(encryptRet);
		return encryptRet;
	}

	/**
	 * 充值---手机端H5
	 * 
	 * @param ip
	 * @param amount
	 * @param token
	 * @return
	 * @throws WinException
	 * @throws Exception
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> authRechargeH5(String ip, String amount, String token, String bizType, String from) throws WinException, Exception {

		String jedisUserId = JedisUtils.get(token);
		String userId = "";
		String accountId = "";
		if (bizType.equals("01")) {
			UserInfo user = userInfoDao.getCgb(jedisUserId);
			userId = user.getId();// 投资用户
			accountId = user.getAccountId();
		}
		/*
		 * 构造请求参数
		 */
		Map<String, String> params = new HashMap<String, String>();
		String orderId = UUID.randomUUID().toString().replace("-", "");
		Double rAmount = 100 * NumberUtils.scaleDouble(Double.valueOf(amount));
		BigDecimal rechargeAmount = new BigDecimal(rAmount).setScale(0, BigDecimal.ROUND_HALF_UP);
		String rechargeAmountString = rechargeAmount.toString();

		params.put("orderId", orderId);
		params.put("userId", userId);
		params.put("amount", rechargeAmountString);
		params.put("currency", "CNY");
		params.put("service", "h5.p2p.trade.account.recharge");
		params.put("method", "RSA");
		params.put("merchantId", merchantId);
		params.put("source", "2");
		if (from.equals("2")) {
			params.put("mobileType", "22");
		}
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
		params.put("callbackUrl", ServerURLConfig.BACK_RECHARGE_URL);
		// 生成签名
		String sign = APIUtils.createSign(merchantRsaPrivateKey, params, "RSA");
		params.put("signature", sign);
		String jsonString = JSON.toJSONString(params);
		System.out.println("充值手机端H5[请求参数]" + jsonString);
		// 加密
		Map<String, String> encryptRet = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
		encryptRet.put("merchantId", merchantId);

		String data = encryptRet.get("data");
		data = URLEncoder.encode(data, "UTF-8");
		String tm = encryptRet.get("tm");
		tm = URLEncoder.encode(tm, "UTF-8");
		encryptRet.put("tm", tm);
		encryptRet.put("data", data);

		// 记录充值信息
		insertRecharge(Double.valueOf(amount), ip, orderId, userId, accountId);

		return encryptRet;
	}

	/**
	 * 大额充值---手机端H5
	 * 
	 * @param ip
	 * @param amount
	 * @param token
	 * @return
	 * @throws WinException
	 * @throws Exception
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> largeRechargeH5(String ip, String amount, String token, String bizType, String from) throws WinException, Exception {

		String jedisUserId = JedisUtils.get(token);
		String userId = "";
		String accountId = "";
		if (bizType.equals("01")) {
			UserInfo user = userInfoDao.getCgb(jedisUserId);
			userId = user.getId();// 投资用户
			accountId = user.getAccountId();
		}
		/*
		 * 构造请求参数
		 */
		Map<String, String> params = new HashMap<String, String>();
		String orderId = UUID.randomUUID().toString().replace("-", "");
		Double rAmount = 100 * NumberUtils.scaleDouble(Double.valueOf(amount));
		BigDecimal rechargeAmount = new BigDecimal(rAmount).setScale(0, BigDecimal.ROUND_HALF_UP);
		String rechargeAmountString = rechargeAmount.toString();

		params.put("orderId", orderId);
		params.put("userId", userId);
		params.put("amount", rechargeAmountString);
		params.put("currency", "CNY");
		params.put("service", "h5.p2p.trade.account.offlineRecharge");

		params.put("method", "RSA");
		params.put("merchantId", merchantId);
		params.put("source", "2");
		if (from.equals("2")) {
			params.put("mobileType", "22");
		}
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
		params.put("callbackUrl", ServerURLConfig.BACK_RECHARGE_URL_LARGE);
		// 生成签名
		String sign = APIUtils.createSign(merchantRsaPrivateKey, params, "RSA");
		params.put("signature", sign);
		String jsonString = JSON.toJSONString(params);
		System.out.println("大额充值手机端H5[请求参数]" + jsonString);
		// 加密
		Map<String, String> encryptRet = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
		encryptRet.put("merchantId", merchantId);

		String data = encryptRet.get("data");
		data = URLEncoder.encode(data, "UTF-8");
		String tm = encryptRet.get("tm");
		tm = URLEncoder.encode(tm, "UTF-8");
		encryptRet.put("tm", tm);
		encryptRet.put("data", data);

		// 记录充值信息
		largeInsertRecharge(Double.valueOf(amount), ip, orderId, userId, accountId);
//		insertRecharge(Double.valueOf(amount), ip, orderId, userId, accountId);

		return encryptRet;
	}

	/**
	 * 增加充值记录
	 * 
	 * @param userInfo
	 * @param amount
	 * @param ip
	 * @param orderId
	 * @throws Exception
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void insertRecharge(Double amount, String ip, String orderId, String userId, String accountId) throws Exception {

		// 记录充值
		UserRecharge userRecharge = new UserRecharge();
		userRecharge.setAccountId(accountId);
		userRecharge.setUserId(userId);
		userRecharge.setAmount(Double.valueOf(amount));
		userRecharge.setBeginBeginDate(new Date());
		userRecharge.setBeginDate(new Date());
		userRecharge.setCreateDate(new Date());
		userRecharge.setFeeAmount(0d);
		userRecharge.setState(UserRecharge.RECHARGE_DOING);
		userRecharge.setBankAccount("-----------");
		userRecharge.setPlatForm(UserRecharge.RECHARGE_GATEWAY);
		userRecharge.setIp(ip);
		userRecharge.setId(orderId);// 交易id
		userRecharge.setSn(orderId);// 订单号
		userRechargeDao.insert(userRecharge);
	}
	
	/**
	 * 增加充值记录
	 * 
	 * @param userInfo
	 * @param amount
	 * @param ip
	 * @param orderId
	 * @throws Exception
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void insertRechargeInit(Double amount, String ip, String orderId, String userId, String accountId) throws Exception {

		// 记录充值
		UserRecharge userRecharge = new UserRecharge();
		userRecharge.setAccountId(accountId);
		userRecharge.setUserId(userId);
		userRecharge.setAmount(Double.valueOf(amount));
		userRecharge.setBeginBeginDate(new Date());
		userRecharge.setBeginDate(new Date());
		userRecharge.setCreateDate(new Date());
		userRecharge.setFeeAmount(0d);
		userRecharge.setState(UserRecharge.RECHARGE_DOING);
		userRecharge.setBankAccount("-----------");
		userRecharge.setPlatForm(UserRecharge.RECHARGE_GATEWAY);
		userRecharge.setIp(ip);
		userRecharge.setId(orderId);// 交易id
		userRecharge.setSn(orderId);// 订单号
		userRechargeDao.insert(userRecharge);

	}
	
	/**
	 * 增加充值记录（大额充值）
	 * 
	 * @param userInfo
	 * @param amount
	 * @param ip
	 * @param orderId
	 * @throws Exception
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void largeInsertRecharge(Double amount, String ip, String orderId, String userId, String accountId) throws Exception {

		// 记录充值
		UserRecharge userRecharge = new UserRecharge();
		userRecharge.setAccountId(accountId);
		userRecharge.setUserId(userId);
		userRecharge.setAmount(Double.valueOf(amount));
		userRecharge.setBeginBeginDate(new Date());
		userRecharge.setBeginDate(new Date());
		userRecharge.setCreateDate(new Date());
		userRecharge.setFeeAmount(0d);
		userRecharge.setState(UserRecharge.RECHARGE_DOING);
		userRecharge.setBankAccount("-----------");
		userRecharge.setPlatForm(UserRecharge.OFFLINE_RECHARGE);
		userRecharge.setIp(ip);
		userRecharge.setId(orderId);// 交易id
		userRecharge.setSn(orderId);// 订单号
		userRechargeDao.insert(userRecharge);

	}

}
