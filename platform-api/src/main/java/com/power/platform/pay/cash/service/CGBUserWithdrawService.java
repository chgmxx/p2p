package com.power.platform.pay.cash.service;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.cunguanbao.api.utils.APIUtils;
import com.power.platform.cgb.dao.CgbUserBankCardDao;
import com.power.platform.cgb.entity.CgbUserAccount;
import com.power.platform.cgbpay.config.ServerURLConfig;
import com.power.platform.common.config.Global;
import com.power.platform.common.exception.WinException;
import com.power.platform.common.utils.JedisUtils;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.credit.entity.userinfo.CreditUserAccount;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.lanmao.type.StatusEnum;
import com.power.platform.pay.cash.dao.UserCashDao;
import com.power.platform.pay.cash.entity.UserCash;
import com.power.platform.userinfo.dao.UserBankCardDao;
import com.power.platform.userinfo.dao.UserInfoDao;
import com.power.platform.userinfo.entity.UserInfo;


@Service("cGBUserWithdrawService")
public class CGBUserWithdrawService {
	
	
	//商户号
    private static final String merchantId = Global.getConfig("merchantId");
	//存管系统为你分配的，用来对AES私钥加密，同时生成签名
	private static final String merchantRsaPublicKey = Global.getConfig("merchantRsaPublicKey");
	 
	//商户自己的RSA私钥
    private static final String merchantRsaPrivateKey = Global.getConfig("merchantRsaPrivateKey");
	
	@Resource
	private UserBankCardDao userBankCardDao;
	@Resource
	private UserCashDao userCashDao;
	@Resource
	private CgbUserBankCardDao cgbUserBankCardDao;
	@Resource
	private UserInfoDao userInfoDao;
	
	

    /**
     * 提现PC端web
     * @param token
     * @param userAccountInfo
     * @param branchBank
     * @param cityCode
     * @param amount
     * @param feeAomunt
     * @param ip
     * @return
     * @throws WinException
     * @throws Exception
     */
	public Map<String, String> withdrawWeb(String userId, String accountId,
			String branchBank, String cityCode, String amount,
			double feeAomunt, String ip,String type) throws WinException, Exception {

		/*
		 * 构造请求参数
		 */
		Map<String, String> params = new HashMap<String, String>();
		String orderId = UUID.randomUUID().toString().replace("-", "");
		//提现金额, 单位为分
        Double wAmount = NumberUtils.scaleDouble(Double.valueOf(amount) * 100);
        BigDecimal withdrawAmount = new BigDecimal(wAmount);
        //手续费
        Double fAmount = 100*NumberUtils.scaleDouble(Double.valueOf(feeAomunt));
        BigDecimal feAmount = new BigDecimal(fAmount);
		params.put("orderId", orderId);
		params.put("userId", userId);
		params.put("amount", withdrawAmount.toString());
		params.put("feeAmount", feAmount.toString());
		params.put("currency", "CNY");
        params.put("service", "web.p2p.trade.account.withdraw");
        params.put("method", "RSA");
        params.put("merchantId", merchantId);
        params.put("source", "1");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String requestTime =sdf.format(new Date());
        params.put("requestTime", requestTime);
        params.put("version", "1.0.0");
        params.put("reqSn", UUID.randomUUID().toString().replace("-", ""));
        if(type.equals("02")){ // 借款人
        	params.put("returnUrl", ServerURLConfig.BACK_BACKTO_URL_BORROWWEB+"&id="+userId);
        }else{ // 出借人
        	params.put("returnUrl", ServerURLConfig.BACK_BACKTO_URL_WEB);
        }
        // 提现
        params.put("callbackUrl", ServerURLConfig.BACK_WITHDRAW_URL);
        //生成签名
        String sign = APIUtils.createSign(merchantRsaPrivateKey, params,"RSA");
      	params.put("signature", sign);
      	String jsonString =JSON.toJSONString(params);
      	System.out.println("提现PC端Web[请求参数]"+jsonString);
      	//加密
      	Map<String, String> encryptRet =APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
      	encryptRet.put("merchantId", merchantId);
      	
      	String data = encryptRet.get("data");
      	data = URLEncoder.encode(data,"UTF-8");
      	String tm = encryptRet.get("tm");
      	tm = URLEncoder.encode(tm,"UTF-8");
      	encryptRet.put("tm", tm);
      	encryptRet.put("data", data);
      	encryptRet.put("merchantId", merchantId);
      	//提现申请存表
      	withdraw(branchBank, cityCode, amount, feeAomunt, ip, orderId, "01", userId, accountId);
      	
        //返回订单信息
      	encryptRet.put("amount", amount);
      	encryptRet.put("feeAmount", String.valueOf(feeAomunt));
		return encryptRet;
	}
	
	
    /**
     * 提现手机端H5
     * @param token
     * @param userAccountInfo
     * @param branchBank
     * @param cityCode
     * @param amount
     * @param feeAomunt
     * @param ip
     * @return
     * @throws WinException
     * @throws Exception
     */
	public Map<String, String> withdrawH5(String token, String branchBank, String cityCode, String amount, double feeAomunt, String ip, String bizType, String from) throws WinException, Exception {

		// TODO Auto-generated method stub
		String jedisUserId = JedisUtils.get(token);
		String userId = "";
		String accountId = "";
		if (!StringUtils.isBlank(jedisUserId)) {
			userId = jedisUserId;
			UserInfo user = userInfoDao.getCgb(jedisUserId);
			accountId = user.getAccountId();
		}
		
		/*
		 * 构造请求参数
		 */
		Map<String, String> params = new HashMap<String, String>();
		String orderId = UUID.randomUUID().toString().replace("-", "");
		//提现金额
        Double wAmount = 100*NumberUtils.scaleDouble(Double.valueOf(amount));
        BigDecimal withdrawAmount = new BigDecimal(wAmount);
        String withdrawAmountString = withdrawAmount.toString();
        if (withdrawAmountString.split("\\.").length >0) {
        	withdrawAmountString= withdrawAmountString.split("\\.")[0];
		}
        //手续费
        Double fAmount = 100*NumberUtils.scaleDouble(Double.valueOf(feeAomunt));
        BigDecimal feAmount = new BigDecimal(fAmount);
		params.put("orderId", orderId);
		params.put("userId", userId);
		params.put("amount", withdrawAmountString);
		params.put("feeAmount", feAmount.toString());
		params.put("currency", "CNY");
        params.put("service", "h5.p2p.trade.account.withdraw.apply");
        params.put("method", "RSA");
        params.put("merchantId", merchantId);
        params.put("source", "2");
        if(from.equals("2")){
        	params.put("mobileType", "22");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String requestTime =sdf.format(new Date());
        params.put("requestTime", requestTime);
        params.put("version", "1.0.0");
        params.put("reqSn", UUID.randomUUID().toString().replace("-", ""));
        if(from.equals("3")||from.equals("4")){
        	params.put("returnUrl", ServerURLConfig.BACK_BACKTO_URL);
        }else{
        	params.put("returnUrl", ServerURLConfig.BACK_BACKTO_URL_WAP);
        }
        params.put("callbackUrl", ServerURLConfig.BACK_WITHDRAW_URL);
        //生成签名
        String sign = APIUtils.createSign(merchantRsaPrivateKey, params,"RSA");
      	params.put("signature", sign);
      	String jsonString =JSON.toJSONString(params);
      	System.out.println("提现手机端H5[请求参数]"+jsonString);
      	//加密
      	Map<String, String> encryptRet =APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
      	encryptRet.put("merchantId", merchantId);
      	
      	String data = encryptRet.get("data");
      	data = URLEncoder.encode(data,"UTF-8");
      	String tm = encryptRet.get("tm");
      	tm = URLEncoder.encode(tm,"UTF-8");
      	encryptRet.put("tm", tm);
      	encryptRet.put("data", data);
      	encryptRet.put("merchantId", merchantId);
      	//提现申请存表
      	withdraw(branchBank, cityCode, amount, feeAomunt, ip, orderId, bizType, userId, accountId);
      	
        //返回订单信息
      	encryptRet.put("amount", amount);
      	encryptRet.put("feeAmount", String.valueOf(feeAomunt));
		return encryptRet;
	}
	
	/**
	 * 提现申请
	 * @param token
	 * @param branchBank
	 * @param cityCode
	 * @param amount
	 * @param ip2 
	 * @return
	 * @throws Exception 
	 */
	@Transactional(readOnly = false,rollbackFor=Exception.class)
	public UserCash withdraw(String branchBank, String cityCode, String amount,Double feeAmount, String ip, String orderId, 
			String bizType, String userId, String accountId) throws Exception {
		
		//保存到提现表
		UserCash userCash = new UserCash();
		userCash.setAccountId(accountId);
		userCash.setAmount(Double.valueOf(amount));
		userCash.setState(UserCash.CASH_APPLY);
		userCash.setBank(cgbUserBankCardDao.getUserBankCardByUserId(userId).getBankNo());
		userCash.setBankAccount(cgbUserBankCardDao.getUserBankCardByUserId(userId).getBankAccountNo());
		userCash.setBeginDate(new Date());
		userCash.setBrabankName(branchBank);
		userCash.setCityCode(cityCode);
		userCash.setCreateDate(new Date());
		userCash.setFeeAccount(accountId);
		userCash.setFeeAmount(feeAmount);
		userCash.setId(orderId);
		userCash.setIp(ip);
		userCash.setSn("W_" + new Date().getTime());
		userCash.setFrom(2);
		userCash.setUserId(userId);
		userCash.setEndDate(userCash.getBeginDate());
		int result = userCashDao.insert(userCash);
		return userCash; 
	}
	
	

}
