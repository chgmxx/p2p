package com.power.platform.pay.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.power.platform.cache.Cache;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.JedisUtils;
import com.power.platform.common.utils.MemCachedUtis;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.pay.cash.dao.UserCashDao;
import com.power.platform.pay.cash.entity.UserCash;
import com.power.platform.pay.config.PartnerConfig;
import com.power.platform.pay.config.ServerURLConfig;
import com.power.platform.pay.conn.HttpRequestSimple;
import com.power.platform.pay.utils.LLPayUtil;
import com.power.platform.pay.vo.AuthPayInfo;
import com.power.platform.pay.vo.CashBean;
import com.power.platform.pay.vo.PrcptcdInfo;
import com.power.platform.pay.recharge.dao.UserRechargeDao;
import com.power.platform.pay.recharge.entity.UserRecharge;
import com.power.platform.pay.vo.PayDataBean;
import com.power.platform.trandetail.dao.UserTransDetailDao;
import com.power.platform.trandetail.entity.UserTransDetail;
import com.power.platform.userinfo.dao.UserAccountInfoDao;
import com.power.platform.userinfo.dao.UserBankCardDao;
import com.power.platform.userinfo.dao.UserInfoDao;
import com.power.platform.userinfo.entity.Principal;
import com.power.platform.userinfo.entity.UserAccountInfo;
import com.power.platform.userinfo.entity.UserBankCard;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.weixin.service.WeixinSendTempMsgService;



/**
 *连连支付Service
 * @author 曹智
 * @version 2015-12-23
 */

@Service("llPayService")
public class LLPayService  {
	
	
	private static final Logger LOG = LoggerFactory
			.getLogger(LLPayService.class);
	
	@Resource
	private UserRechargeDao userRechargeDao;
	@Resource
	private UserCashDao userCashDao;
	@Resource
	private UserAccountInfoDao userAccountInfoDao;
	
	@Resource
	private UserTransDetailDao  userTransDetailDao;
	
	@Resource
	private UserInfoDao userInfoDao;
	
	@Resource
	private UserBankCardDao userBankCardDao;
	
	@Resource
	private WeixinSendTempMsgService weixinSendTempMsgService;
	
	
	
	/**
	 * 充值成功后后续处理逻辑
	 */
	@Transactional(readOnly = false,rollbackFor=Exception.class)
	public void completeRecharge(PayDataBean payDataBean) {
		
		try {	
			//1.更新充值表状态
			String sn = payDataBean.getNo_order();
			UserRecharge userRecharge = new UserRecharge();
			userRecharge.setSn(sn);
			List<UserRecharge> userRecharges = userRechargeDao.findList(userRecharge);
			if (userRecharges !=null && userRecharges.size() > 0) {
				userRecharge = userRecharges.get(0);
			}
			userRecharge.setState(UserRecharge.RECHARGE_SUCCESS);
			userRecharge.setEndDate(new Date());
			userRechargeDao.update(userRecharge);
			String userId = userRecharge.getUserId();
			String accountId = userRecharge.getAccountId();
	        Double rechargeAmount = Double.valueOf(payDataBean.getMoney_order());
	        
			//2.用户账户表更新金额
			UserAccountInfo userAccountInfo = userAccountInfoDao.get(accountId);
			Double avaliableAmount = userAccountInfo.getAvailableAmount();
			Double totalAmount = userAccountInfo.getTotalAmount();
			Double totalRechargeAmount = userAccountInfo.getRechargeAmount();
			int rechargeCount = userAccountInfo.getRechargeCount();
			userAccountInfo.setAvailableAmount(avaliableAmount + rechargeAmount);
			userAccountInfo.setTotalAmount(totalAmount + rechargeAmount);
			userAccountInfo.setRechargeCount(rechargeCount +1);
			userAccountInfo.setRechargeAmount(totalRechargeAmount + rechargeAmount);
			userAccountInfo.setUserId(userRecharge.getUserId());
			userAccountInfoDao.update(userAccountInfo);
			
			//3.资金流水表更新状态
			UserTransDetail userTransDetail = new UserTransDetail();
			userTransDetail.setUserId(userId);
			userTransDetail.setAmount(userRecharge.getAmount());
			userTransDetail.setAccountId(userRecharge.getAccountId());
			userTransDetail.setAvaliableAmount(userAccountInfo
					.getAvailableAmount());
			userTransDetail.setTransDate(userRecharge.getBeginDate());
			userTransDetail.setBeginTransDate(userRecharge.getBeginDate());
			userTransDetail.setInOutType(UserTransDetail.TRANS_TYPE_IN);
			userTransDetail.setRemarks("用户充值");
			userTransDetail.setState(UserTransDetail.TRANS_STATE_SUCCESS);
			userTransDetail.setTrustType(UserTransDetail.TRANS_RECHARGE);
			userTransDetail.setId(IdGen.uuid());
			userTransDetail.setTransId(userRecharge.getId());
			userTransDetailDao.insert(userTransDetail);
			//4.如果是认证支付，更新用户信息、银行卡信息
			String pay_type = payDataBean.getPay_type();
			Cache cache = MemCachedUtis.getMemCached();
			Map<String, String> cacheLoginedUser = cache.get("cacheLoginedUser");
			String token = cacheLoginedUser.get(userId);
			Principal principal = null;
			if (token !=null) {
				principal = cache.get(token);
				if (principal !=null) {
					principal.setUserAccountInfo(userAccountInfo);
				}
			}
			if (pay_type.equals("D")) {
				//设置用户信息，身份证为认证通过、绑卡为认证通过
				UserInfo userInfo = userInfoDao.get(userId);
				if (userInfo.getBindBankCardState().intValue() == UserInfo.BIND_CARD_NO) {
					userInfo.setCertificateChecked(UserInfo.CERTIFICATE_YES);
					userInfo.setBindBankCardState(UserInfo.BIND_CARD_YES);
					String no_agree = payDataBean.getNo_agree();
					userInfo.setLlagreeNo(no_agree);
					userInfoDao.update(userInfo);
					if (principal !=null) {
						principal.setUserInfo(userInfo);
					}
					//设置银行卡状态
					UserBankCard userBankCard = new UserBankCard();
			      	userBankCard.setUserId(userRecharge.getUserId());
			      	userBankCard.setState(UserBankCard.CERTIFY_NO);
			      	List<UserBankCard> cards = userBankCardDao.findList(userBankCard);
			      	if (cards !=null && cards.size() > 0) {
			      		userBankCard = cards.get(0);
					}
					userBankCard.setState(UserBankCard.CERTIFY_YES);
					userBankCard.setIsDefault(UserBankCard.DEFAULT_YES);
					userBankCard.setBankNo(payDataBean.getBank_code());
					userBankCard.setUpdateDate(new Date());
					userBankCardDao.update(userBankCard);
				}			
			}
			
			// 充值成功发送微信、短信提醒
			weixinSendTempMsgService.sendUserRechargeMsg(userRecharge);
			cache.set(token, 1200,principal);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 用户绑卡接口(wap)
	 * @param bankCard 银行卡号
	 * @param idCard 身份证号
	 * @param realName 真实姓名
	 * @param mobile 银行预留手机号
	 * @param ip 访问ip
	 * @param bankCode 银行编码
	 * @param token token
	 * @return
	 * @throws Exception
	 */
	@Transactional(readOnly = false,rollbackFor=Exception.class)
	public Map<String, Object> bindCardWap(String bankCard, String idCard, String realName,String ip, String bankCode, String token) throws Exception {
		
		String jedisUserId = JedisUtils.get(token);
		UserInfo userInfo = userInfoDao.get(jedisUserId);
		UserAccountInfo userAccountInfo = userAccountInfoDao.get(userInfo.getAccountId());
		//构造支付订单
		AuthPayInfo certifiedInfo = new AuthPayInfo();
        certifiedInfo.setAcct_name(realName);//真实姓名
		certifiedInfo.setApp_request("3");//访问来源，wap端访问
		certifiedInfo.setBusi_partner(PartnerConfig.BUSI_PARTNER);//业务类型
        certifiedInfo.setCard_no(bankCard);
        certifiedInfo.setDt_order(LLPayUtil.getCurrentDateTimeStr());//订单时间
        certifiedInfo.setId_no(idCard);//身份证号
        certifiedInfo.setInfo_order("中投摩根绑卡");//备注信息
        certifiedInfo.setMoney_order("0.01");//订单金额
        certifiedInfo.setName_goods("中投摩根绑卡");//商品名称
		certifiedInfo.setNo_order("W_C_" + new Date().getTime());//订单号
        certifiedInfo.setNotify_url(ServerURLConfig.RECHARGE_NOTIFY_URL);//订单通知地址（异步）
		certifiedInfo.setOid_partner(PartnerConfig.OID_PARTNER);//平台商户号
        certifiedInfo.setRisk_item(createAuthPayRiskItem(userInfo.getId(),userInfo.getRegisterDate(),realName,idCard));//风险控制参数
		certifiedInfo.setSign_type(PartnerConfig.SIGN_TYPE);//签名方式 md5
        certifiedInfo.setTimestamp(LLPayUtil.getCurrentDateTimeStr());//签名时间戳有效时间
		certifiedInfo.setUser_id(userInfo.getId());//用户唯一编号
        certifiedInfo.setUrl_return("https://www.cicmorgan.com/svc/services/backto/backto?backto=wap");//订单回调地址（同步）
        certifiedInfo.setValid_order("10080");// 订单有效时间，单位分钟，可以为空，默认7天
		certifiedInfo.setVersion("1.1");//版本号
        // 商戶从自己系统中获取用户身份信息（认证支付必须将用户身份信息传输给连连，且修改标记flag_modify设置成1：不可修改）
        // 加签名
        String sign = LLPayUtil.addSign(JSON.parseObject(JSON
                .toJSONString(certifiedInfo)), PartnerConfig.TRADER_PRI_KEY,
                PartnerConfig.MD5_KEY);
        certifiedInfo.setSign(sign);
        //记录充值
        UserRecharge userRecharge = new UserRecharge();
        userRecharge.setAccountId(userAccountInfo.getId());
        userRecharge.setUserId(userInfo.getId());
        userRecharge.setAmount(0.01d);
        userRecharge.setBeginBeginDate(new Date());
        userRecharge.setBeginDate(userRecharge.getBeginBeginDate());
        userRecharge.setCreateDate(userRecharge.getBeginBeginDate());
        userRecharge.setFeeAmount(0d);
        userRecharge.setState(UserRecharge.RECHARGE_DOING);
        userRecharge.setBank(bankCode);
        userRecharge.setBankAccount(bankCard);
        userRecharge.setPlatForm(UserRecharge.RECHARGE_WEB);
        userRecharge.setIp(ip);
        userRecharge.setId(IdGen.uuid());//交易id
        userRecharge.setSn(certifiedInfo.getNo_order());//订单号
        userRechargeDao.insert(userRecharge);
        
        //用户银行卡
        //1.查询之前绑卡失败的卡信息，删除
      	UserBankCard userBankCard = new UserBankCard();
      	userBankCard.setAccountId(userRecharge.getAccountId());
      	userBankCard.setUserId(userRecharge.getUserId());
      	userBankCard.setState(UserBankCard.CERTIFY_NO);
      	List<UserBankCard> oldCards = userBankCardDao.findList(userBankCard);
      	if (oldCards !=null && oldCards.size() > 0) {
			for (UserBankCard userBankCard2 : oldCards) {
				userBankCardDao.physicallyDeleted(userBankCard2);
			}
		}
        //2.插入新的绑卡信息
      	userBankCard = new UserBankCard();
      	userBankCard.setAccountId(userRecharge.getAccountId());
      	userBankCard.setUserId(userRecharge.getUserId());
      	userBankCard.setBankAccountNo(bankCard);
      	userBankCard.setBankNo(bankCode);
      	userBankCard.setBeginBindDate(userRecharge.getBeginBeginDate());
      	userBankCard.setId(IdGen.uuid());
      	userBankCard.setIsDefault(UserBankCard.DEFAULT_YES);
      	userBankCard.setState(UserBankCard.CERTIFY_NO);
      	userBankCard.setBindDate(userRecharge.getBeginBeginDate());
      	userBankCard.setCreateDate(new Date());
      	userBankCard.setUpdateDate(new Date());
      	userBankCardDao.insert(userBankCard);
      //用户信息保存
      	if (userInfo.getBindBankCardState().intValue() == UserInfo.BIND_CARD_NO) {
      		userInfo.setRealName(realName);//真实姓名
      		userInfo.setCertificateNo(idCard);//身份证号
      		userInfo.setCertificateChecked(UserInfo.CERTIFICATE_NO);
      		userInfo.setBindBankCardState(UserInfo.BIND_CARD_NO);
      		userInfoDao.update(userInfo);
		}
        //返回订单信息
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("acct_name", certifiedInfo.getAcct_name());
        result.put("app_request", certifiedInfo.getApp_request());
        result.put("busi_partner", certifiedInfo.getBusi_partner());
        result.put("card_no", certifiedInfo.getCard_no());
        result.put("dt_order", certifiedInfo.getDt_order());
        result.put("id_no", certifiedInfo.getId_no());
        result.put("info_order", certifiedInfo.getInfo_order());
        result.put("money_order", certifiedInfo.getMoney_order());
        result.put("name_goods", certifiedInfo.getName_goods());
        result.put("no_order", certifiedInfo.getNo_order());
        result.put("notify_url", certifiedInfo.getNotify_url());
        result.put("oid_partner", certifiedInfo.getOid_partner());
        result.put("risk_item", certifiedInfo.getRisk_item());
        result.put("sign", certifiedInfo.getSign());
        result.put("sign_type", certifiedInfo.getSign_type());
        result.put("timestamp", certifiedInfo.getTimestamp());
        result.put("user_id", certifiedInfo.getUser_id());
        result.put("url_return", certifiedInfo.getUrl_return());
        result.put("valid_order", certifiedInfo.getValid_order());
        result.put("version", certifiedInfo.getVersion());
		return result;
	}
	/**
	 * 用户绑卡接口(pc)
	 * @param bankCard 银行卡号
	 * @param idCard 身份证号
	 * @param realName 真实姓名
	 * @param mobile 银行预留手机号
	 * @param ip 访问ip
	 * @param bankCode 银行编码
	 * @param token token
	 * @return
	 * @throws Exception
	 */
	@Transactional(readOnly = false,rollbackFor=Exception.class)
	public Map<String, Object> bindCardWeb(String bankCard, String idCard, String realName,String ip, String bankCode, String token) throws Exception {
		
		String jedisUserId = JedisUtils.get(token);
		UserInfo userInfo = userInfoDao.get(jedisUserId);
		
		//构造支付订单
		AuthPayInfo certifiedInfo = new AuthPayInfo();
		certifiedInfo.setVersion(PartnerConfig.VERSION);//版本号
		certifiedInfo.setOid_partner(PartnerConfig.OID_PARTNER);//平台商户号
		certifiedInfo.setUser_id(userInfo.getId());//用户唯一编号
		certifiedInfo.setSign_type(PartnerConfig.SIGN_TYPE);//签名方式 md5
		certifiedInfo.setBusi_partner(PartnerConfig.BUSI_PARTNER);//业务类型
        certifiedInfo.setDt_order(LLPayUtil.getCurrentDateTimeStr());//订单时间
        certifiedInfo.setName_goods("中投摩根绑卡");//商品名称
        certifiedInfo.setInfo_order("中投摩根绑卡");//备注信息
        certifiedInfo.setMoney_order("0.01");//订单金额
        certifiedInfo.setNotify_url(ServerURLConfig.RECHARGE_NOTIFY_URL);//订单通知地址（异步）
        certifiedInfo.setUrl_return("https://www.cicmorgan.com/svc/services/backto/backto?backto=web");//订单回调地址（同步）
        certifiedInfo.setUserreq_ip(ip.replaceAll("\\.","_").trim());//用户端申请ip
        certifiedInfo.setUrl_order("");
        certifiedInfo.setValid_order("10080");// 订单有效时间，单位分钟，可以为空，默认7天
        certifiedInfo.setRisk_item(createAuthPayRiskItem(userInfo.getId(),userInfo.getRegisterDate(),realName,idCard));//风险控制参数
        certifiedInfo.setTimestamp(LLPayUtil.getCurrentDateTimeStr());//签名时间戳有效时间
		certifiedInfo.setNo_order("C_" + new Date().getTime());//订单号
        // 商戶从自己系统中获取用户身份信息（认证支付必须将用户身份信息传输给连连，且修改标记flag_modify设置成1：不可修改）
        certifiedInfo.setId_type("0"); //证件类型 默认为0 身份证
        certifiedInfo.setId_no(idCard);//身份证号
        certifiedInfo.setAcct_name(realName);//真实姓名
        certifiedInfo.setFlag_modify("1");//不可修改
        //certifiedInfo.setPay_type("D");
        certifiedInfo.setCard_no(bankCard);
        //certifiedInfo.setBank_code(bankCode);
        certifiedInfo.setBack_url("https://www.cicmorgan.com/");
        // 加签名
        String sign = LLPayUtil.addSign(JSON.parseObject(JSON
                .toJSONString(certifiedInfo)), PartnerConfig.TRADER_PRI_KEY,
                PartnerConfig.MD5_KEY);
        certifiedInfo.setSign(sign);
        //记录充值
        UserRecharge userRecharge = new UserRecharge();
        userRecharge.setAccountId(userInfo.getAccountId());
        userRecharge.setUserId(userInfo.getId());
        userRecharge.setAmount(0.01d);
        userRecharge.setBeginBeginDate(new Date());
        userRecharge.setBeginDate(userRecharge.getBeginBeginDate());
        userRecharge.setCreateDate(userRecharge.getBeginBeginDate());
        userRecharge.setFeeAmount(0d);
        userRecharge.setState(UserRecharge.RECHARGE_DOING);
        userRecharge.setBank(bankCode);
        userRecharge.setBankAccount(bankCard);
        userRecharge.setPlatForm(UserRecharge.RECHARGE_WEB);
        userRecharge.setIp(ip);
        userRecharge.setId(IdGen.uuid());//交易id
        userRecharge.setSn(certifiedInfo.getNo_order());//订单号
        userRechargeDao.insert(userRecharge);
        
        //用户银行卡
        //1.查询之前绑卡失败的卡信息，删除
      	UserBankCard userBankCard = new UserBankCard();
      	userBankCard.setAccountId(userRecharge.getAccountId());
      	userBankCard.setUserId(userRecharge.getUserId());
      	userBankCard.setState(UserBankCard.CERTIFY_NO);
      	List<UserBankCard> oldCards = userBankCardDao.findList(userBankCard);
      	if (oldCards !=null && oldCards.size() > 0) {
			for (UserBankCard userBankCard2 : oldCards) {
				userBankCardDao.physicallyDeleted(userBankCard2);
			}
		}
        //2.插入新的绑卡信息
      	userBankCard = new UserBankCard();
      	userBankCard.setAccountId(userRecharge.getAccountId());
      	userBankCard.setUserId(userRecharge.getUserId());
      	userBankCard.setBankAccountNo(bankCard);
      	userBankCard.setBankNo(bankCode);
      	userBankCard.setBeginBindDate(userRecharge.getBeginBeginDate());
      	userBankCard.setId(IdGen.uuid());
      	userBankCard.setIsDefault(UserBankCard.DEFAULT_YES);
      	userBankCard.setState(UserBankCard.CERTIFY_NO);
      	userBankCard.setBindDate(userRecharge.getBeginBeginDate());
      	userBankCard.setCreateDate(new Date());
      	userBankCard.setUpdateDate(new Date());
      	userBankCardDao.insert(userBankCard);
      	
      	//用户信息保存
      	if (userInfo.getBindBankCardState().intValue() == UserInfo.BIND_CARD_NO) {
      		userInfo.setRealName(realName);//真实姓名
      		userInfo.setCertificateNo(idCard);//身份证号
      		userInfo.setCertificateChecked(UserInfo.CERTIFICATE_NO);
      		userInfo.setBindBankCardState(UserInfo.BIND_CARD_NO);
      		userInfoDao.update(userInfo);
		}
      		
        //返回订单信息
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("version", certifiedInfo.getVersion());
        result.put("oid_partner", certifiedInfo.getOid_partner());
        result.put("user_id", certifiedInfo.getUser_id());
        result.put("sign_type", certifiedInfo.getSign_type());
        result.put("busi_partner", certifiedInfo.getBusi_partner());
        result.put("no_order", certifiedInfo.getNo_order());
        result.put("dt_order", certifiedInfo.getDt_order());
        result.put("name_goods", certifiedInfo.getName_goods());
        result.put("info_order", certifiedInfo.getInfo_order());
        result.put("money_order", certifiedInfo.getMoney_order());
        result.put("notify_url", certifiedInfo.getNotify_url());
        result.put("url_return", certifiedInfo.getUrl_return());
        result.put("userreq_ip", certifiedInfo.getUserreq_ip());
        result.put("url_order", certifiedInfo.getUrl_order());
        result.put("valid_order", certifiedInfo.getValid_order());
        result.put("timestamp", certifiedInfo.getTimestamp());
        result.put("sign", certifiedInfo.getSign());
        result.put("risk_item", certifiedInfo.getRisk_item());
        result.put("id_type", certifiedInfo.getId_type());
        result.put("id_no", certifiedInfo.getId_no());
        result.put("acct_name", certifiedInfo.getAcct_name());
        result.put("flag_modify", certifiedInfo.getFlag_modify());
        result.put("card_no", certifiedInfo.getCard_no());
        result.put("bank_code", certifiedInfo.getBank_code());
        result.put("pay_type", certifiedInfo.getPay_type());
        result.put("back_url", certifiedInfo.getBack_url());
        result.put("req_url", ServerURLConfig.PAY_URL);
		return result;
	}
	
	/**
	 * 用户绑卡接口(app)
	 * @param bankCard 银行卡号
	 * @param idCard 身份证号
	 * @param realName 真实姓名
	 * @param mobile 银行预留手机号
	 * @param ip 访问ip
	 * @param token token
	 * @return
	 * @throws Exception
	 */
	@Transactional(readOnly = false,rollbackFor=Exception.class)
	public Map<String, Object> bindCardApp(String bankCard, String idCard, String realName,String ip, String token) throws Exception {
		
		String jedisUserId = JedisUtils.get(token);
		UserInfo userInfo = userInfoDao.get(jedisUserId);
		UserAccountInfo userAccountInfo = userAccountInfoDao.get(userInfo.getAccountId());
		
		//构造支付订单
		AuthPayInfo certifiedInfo = new AuthPayInfo();
		certifiedInfo.setOid_partner(PartnerConfig.OID_PARTNER);//平台商户号
		certifiedInfo.setSign_type(PartnerConfig.SIGN_TYPE);//签名方式 md5
		certifiedInfo.setBusi_partner(PartnerConfig.BUSI_PARTNER);//业务类型
		certifiedInfo.setNo_order("A_C_" + new Date().getTime());//订单号
        certifiedInfo.setDt_order(LLPayUtil.getCurrentDateTimeStr());//订单时间
        certifiedInfo.setName_goods("中投摩根绑卡");//商品名称
        certifiedInfo.setInfo_order("中投摩根绑卡");//备注信息
        certifiedInfo.setMoney_order("2");//订单金额
        certifiedInfo.setNotify_url(ServerURLConfig.RECHARGE_NOTIFY_URL);//订单通知地址（异步）
        certifiedInfo.setValid_order("10080");// 订单有效时间，单位分钟，可以为空，默认7天
        certifiedInfo.setRisk_item(createAuthPayRiskItem(userInfo.getId(),userInfo.getRegisterDate(),realName,idCard));//风险控制参数
		// 加签名
        String sign = LLPayUtil.addSign(JSON.parseObject(JSON
                .toJSONString(certifiedInfo)), PartnerConfig.TRADER_PRI_KEY,
                PartnerConfig.MD5_KEY);
		certifiedInfo.setVersion(PartnerConfig.VERSION);//版本号
        certifiedInfo.setSign(sign);
		certifiedInfo.setUser_id(userInfo.getId());//用户唯一编号
        // 商戶从自己系统中获取用户身份信息（认证支付必须将用户身份信息传输给连连，且修改标记flag_modify设置成1：不可修改）
        certifiedInfo.setId_type("0"); //证件类型 默认为0 身份证
        certifiedInfo.setId_no(idCard);//身份证号
        certifiedInfo.setAcct_name(realName);//真实姓名
        certifiedInfo.setCard_no(bankCard);
        
        //记录充值
        UserRecharge userRecharge = new UserRecharge();
        userRecharge.setAccountId(userAccountInfo.getId());
        userRecharge.setUserId(userInfo.getId());
        userRecharge.setAmount(2d);
        userRecharge.setBeginBeginDate(new Date());
        userRecharge.setBeginDate(userRecharge.getBeginBeginDate());
        userRecharge.setCreateDate(userRecharge.getBeginBeginDate());
        userRecharge.setFeeAmount(0d);
        userRecharge.setState(UserRecharge.RECHARGE_DOING);
        userRecharge.setBank("-----------");
        userRecharge.setBankAccount(bankCard);
        userRecharge.setPlatForm(UserRecharge.RECHARGE_WEB);
        userRecharge.setIp(ip);
        userRecharge.setId(IdGen.uuid());//交易id
        userRecharge.setSn(certifiedInfo.getNo_order());//订单号
        userRechargeDao.insert(userRecharge);

        //用户银行卡
        //1.查询之前绑卡失败的卡信息，删除
      	UserBankCard userBankCard = new UserBankCard();
      	userBankCard.setAccountId(userRecharge.getAccountId());
      	userBankCard.setUserId(userRecharge.getUserId());
      	userBankCard.setState(UserBankCard.CERTIFY_NO);
      	List<UserBankCard> oldCards = userBankCardDao.findList(userBankCard);
      	if (oldCards !=null && oldCards.size() > 0) {
			for (UserBankCard userBankCard2 : oldCards) {
				userBankCardDao.physicallyDeleted(userBankCard2);
			}
		}
        //2.插入新的绑卡信息
      	userBankCard = new UserBankCard();
      	userBankCard.setAccountId(userRecharge.getAccountId());
      	userBankCard.setUserId(userRecharge.getUserId());
      	userBankCard.setBankAccountNo(bankCard);
      	userBankCard.setBankNo("---------");
      	userBankCard.setBeginBindDate(userRecharge.getBeginBeginDate());
      	userBankCard.setId(IdGen.uuid());
      	userBankCard.setIsDefault(UserBankCard.DEFAULT_YES);
      	userBankCard.setState(UserBankCard.CERTIFY_NO);
      	userBankCard.setBindDate(userRecharge.getBeginBeginDate());
      	userBankCard.setCreateDate(new Date());
      	userBankCard.setUpdateDate(new Date());
      	userBankCardDao.insert(userBankCard);
      	//用户信息保存
      	if (userInfo.getBindBankCardState().intValue() == UserInfo.BIND_CARD_NO) {
      		userInfo.setRealName(realName);//真实姓名
      		userInfo.setCertificateNo(idCard);//身份证号
      		userInfo.setCertificateChecked(UserInfo.CERTIFICATE_NO);
      		userInfo.setBindBankCardState(UserInfo.BIND_CARD_NO);
      		userInfoDao.update(userInfo);
		}
      		
        //返回订单信息
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("oid_partner", certifiedInfo.getOid_partner());
        result.put("user_id", certifiedInfo.getUser_id());
        result.put("sign_type", certifiedInfo.getSign_type());
        result.put("busi_partner", certifiedInfo.getBusi_partner());
        result.put("no_order", certifiedInfo.getNo_order());
        result.put("dt_order", certifiedInfo.getDt_order());
        result.put("name_goods", certifiedInfo.getName_goods());
        result.put("info_order", certifiedInfo.getInfo_order());
        result.put("money_order", certifiedInfo.getMoney_order());
        result.put("notify_url", certifiedInfo.getNotify_url());
        result.put("valid_order", certifiedInfo.getValid_order());
        result.put("risk_item", certifiedInfo.getRisk_item());
        result.put("id_no", certifiedInfo.getId_no());
        result.put("acct_name", certifiedInfo.getAcct_name());
        result.put("card_no", certifiedInfo.getCard_no());
        result.put("sign", certifiedInfo.getSign());
		return result;
	}
	
	/**
	 * 认证充值接口(wap)
	 * @param amount 充值金额
	 * @param ip 访问ip
	 * @param token token
	 * @return
	 * @throws Exception
	 */
	@Transactional(readOnly = false,rollbackFor=Exception.class)
	public Map<String, Object> authRechargeWap(String ip, String amount, String token) throws Exception {
		
		String jedisUserId = JedisUtils.get(token);
		UserInfo userInfo = userInfoDao.get(jedisUserId);
		UserAccountInfo userAccountInfo = userAccountInfoDao.get(userInfo.getAccountId());
        UserBankCard userBankCard = userBankCardDao.getUserBankCardByUserId(userInfo.getId());

		//构造支付订单
		AuthPayInfo authPayInfo = new AuthPayInfo();
        authPayInfo.setAcct_name(userInfo.getRealName());//真实姓名
        authPayInfo.setApp_request("3");
		authPayInfo.setBusi_partner(PartnerConfig.BUSI_PARTNER);//业务类型
        authPayInfo.setNo_agree(userInfo.getLlagreeNo());
		authPayInfo.setDt_order(LLPayUtil.getCurrentDateTimeStr());//订单时间
        authPayInfo.setId_no(userInfo.getCertificateNo());//身份证号
		authPayInfo.setInfo_order("中投摩根用户充值");//备注信息
		authPayInfo.setMoney_order(amount);//订单金额
		authPayInfo.setName_goods("中投摩根用户充值");//商品名称
        authPayInfo.setNo_order("A_" + new Date().getTime());//订单号
		authPayInfo.setNotify_url(ServerURLConfig.RECHARGE_NOTIFY_URL);//订单通知地址（异步）
		authPayInfo.setOid_partner(PartnerConfig.OID_PARTNER);//平台商户号
        authPayInfo.setRisk_item(createAuthPayRiskItem(userInfo.getId(),userInfo.getRegisterDate(),userInfo.getRealName(),userInfo.getCertificateNo()));//风险控制参数
		authPayInfo.setSign_type(PartnerConfig.SIGN_TYPE);//签名方式 md5
        authPayInfo.setTimestamp(LLPayUtil.getCurrentDateTimeStr());//签名时间戳有效时间
		authPayInfo.setUser_id(userInfo.getId());//用户唯一编号
		authPayInfo.setUrl_return("https://www.cicmorgan.com/svc/services/backto/backto?backto=wap");//订单回调地址（同步）
        authPayInfo.setValid_order("10080");// 订单有效时间，单位分钟，可以为空，默认7天
		authPayInfo.setVersion("1.1");//版本号
        // 商戶从自己系统中获取用户身份信息（认证支付必须将用户身份信息传输给连连，且修改标记flag_modify设置成1：不可修改）
        // 加签名
        String sign = LLPayUtil.addSign(JSON.parseObject(JSON
                .toJSONString(authPayInfo)), PartnerConfig.TRADER_PRI_KEY,
                PartnerConfig.MD5_KEY);
        authPayInfo.setSign(sign);
        
        //记录充值
        UserRecharge userRecharge = new UserRecharge();
        userRecharge.setAccountId(userAccountInfo.getId());
        userRecharge.setUserId(userInfo.getId());
        userRecharge.setAmount(Double.valueOf(amount));
        userRecharge.setBeginBeginDate(new Date());
        userRecharge.setBeginDate(userRecharge.getBeginBeginDate());
        userRecharge.setCreateDate(userRecharge.getBeginBeginDate());
        userRecharge.setFeeAmount(0d);
        userRecharge.setState(UserRecharge.RECHARGE_DOING);
        userRecharge.setBank(userBankCard.getBankNo());
        userRecharge.setBankAccount(userBankCard.getBankAccountNo());
        userRecharge.setPlatForm(UserRecharge.RECHARGE_WEB);
        userRecharge.setIp(ip);
        userRecharge.setId(IdGen.uuid());//交易id
        userRecharge.setSn(authPayInfo.getNo_order());//订单号
        userRechargeDao.insert(userRecharge);
        
        
        //返回订单信息
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("acct_name", authPayInfo.getAcct_name());
        result.put("app_request", authPayInfo.getApp_request());
        result.put("busi_partner", authPayInfo.getBusi_partner());
        result.put("no_agree", authPayInfo.getNo_agree());
        result.put("dt_order", authPayInfo.getDt_order());
        result.put("id_no", authPayInfo.getId_no());
        result.put("info_order", authPayInfo.getInfo_order());
        result.put("money_order", authPayInfo.getMoney_order());
        result.put("name_goods", authPayInfo.getName_goods());
        result.put("no_order", authPayInfo.getNo_order());
        result.put("notify_url", authPayInfo.getNotify_url());
        result.put("oid_partner", authPayInfo.getOid_partner());
        result.put("risk_item", authPayInfo.getRisk_item());
        result.put("sign", authPayInfo.getSign());
        result.put("sign_type", authPayInfo.getSign_type());
        result.put("timestamp", authPayInfo.getTimestamp());
        result.put("user_id", authPayInfo.getUser_id());
        result.put("url_return", authPayInfo.getUrl_return());
        result.put("valid_order", authPayInfo.getValid_order());
        result.put("version", authPayInfo.getVersion());
		return result;
	}
	
	
	/**
	 * 认证充值接口(app)
	 * @param amount 充值金额
	 * @param ip 访问ip
	 * @param token token
	 * @return
	 * @throws Exception
	 */
	@Transactional(readOnly = false,rollbackFor=Exception.class)
	public Map<String, Object> authRechargeApp(String ip, String amount, String token) throws Exception {
		
		String jedisUserId = JedisUtils.get(token);
		UserInfo userInfo = userInfoDao.get(jedisUserId);
		UserAccountInfo userAccountInfo = userAccountInfoDao.get(userInfo.getAccountId());
        UserBankCard userBankCard = userBankCardDao.getUserBankCardByUserId(userInfo.getId());

		//构造支付订单
		AuthPayInfo authPayInfo = new AuthPayInfo();
		authPayInfo.setOid_partner(PartnerConfig.OID_PARTNER);//平台商户号
		authPayInfo.setSign_type(PartnerConfig.SIGN_TYPE);//签名方式 md5
		authPayInfo.setBusi_partner(PartnerConfig.BUSI_PARTNER);//业务类型
        authPayInfo.setNo_order("A_A_" + new Date().getTime());//订单号
		authPayInfo.setDt_order(LLPayUtil.getCurrentDateTimeStr());//订单时间
		authPayInfo.setName_goods("中投摩根用户充值");//商品名称
		authPayInfo.setInfo_order("中投摩根用户充值");//备注信息
		authPayInfo.setMoney_order(amount);//订单金额
		authPayInfo.setNotify_url(ServerURLConfig.RECHARGE_NOTIFY_URL);//订单通知地址（异步）
        authPayInfo.setValid_order("10080");// 订单有效时间，单位分钟，可以为空，默认7天
        authPayInfo.setRisk_item(createAuthPayRiskItem(userInfo.getId(),userInfo.getRegisterDate(),userInfo.getRealName(),userInfo.getCertificateNo()));//风险控制参数
		// 加签名
        String sign = LLPayUtil.addSign(JSON.parseObject(JSON
                .toJSONString(authPayInfo)), PartnerConfig.TRADER_PRI_KEY,
                PartnerConfig.MD5_KEY);
        authPayInfo.setSign(sign);
		authPayInfo.setUser_id(userInfo.getId());//用户唯一编号
        // 商戶从自己系统中获取用户身份信息（认证支付必须将用户身份信息传输给连连，且修改标记flag_modify设置成1：不可修改）
        authPayInfo.setId_type("0"); //证件类型 默认为0 身份证
        authPayInfo.setId_no(userInfo.getCertificateNo());//身份证号
        authPayInfo.setAcct_name(userInfo.getRealName());//真实姓名
        authPayInfo.setNo_agree(userInfo.getLlagreeNo());
        
        
        //记录充值
        UserRecharge userRecharge = new UserRecharge();
        userRecharge.setAccountId(userAccountInfo.getId());
        userRecharge.setUserId(userInfo.getId());
        userRecharge.setAmount(Double.valueOf(amount));
        userRecharge.setBeginBeginDate(new Date());
        userRecharge.setBeginDate(userRecharge.getBeginBeginDate());
        userRecharge.setCreateDate(userRecharge.getBeginBeginDate());
        userRecharge.setFeeAmount(0d);
        userRecharge.setState(UserRecharge.RECHARGE_DOING);
        userRecharge.setBank(userBankCard.getBankNo());
        userRecharge.setBankAccount(userBankCard.getBankAccountNo());
        userRecharge.setPlatForm(UserRecharge.RECHARGE_WEB);
        userRecharge.setIp(ip);
        userRecharge.setId(IdGen.uuid());//交易id
        userRecharge.setSn(authPayInfo.getNo_order());//订单号
        userRechargeDao.insert(userRecharge);
        
        //返回订单信息
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("oid_partner", authPayInfo.getOid_partner());
        result.put("user_id", authPayInfo.getUser_id());
        result.put("sign_type", authPayInfo.getSign_type());
        result.put("busi_partner", authPayInfo.getBusi_partner());
        result.put("no_order", authPayInfo.getNo_order());
        result.put("dt_order", authPayInfo.getDt_order());
        result.put("name_goods", authPayInfo.getName_goods());
        result.put("info_order", authPayInfo.getInfo_order());
        result.put("money_order", authPayInfo.getMoney_order());
        result.put("notify_url", authPayInfo.getNotify_url());
        result.put("valid_order", authPayInfo.getValid_order());
        result.put("risk_item", authPayInfo.getRisk_item());
        result.put("no_agree", authPayInfo.getNo_agree());
        result.put("id_type", authPayInfo.getId_type());
        result.put("id_no", authPayInfo.getId_no());
        result.put("acct_name", authPayInfo.getAcct_name());
        result.put("sign", authPayInfo.getSign());
		return result;
	}
	/**
	 * 认证充值接口(pc)
	 * @param amount 充值金额
	 * @param ip 访问ip
	 * @param token token
	 * @return
	 * @throws Exception
	 */
	@Transactional(readOnly = false,rollbackFor=Exception.class)
	public Map<String, Object> authRechargeWeb(String ip, String amount, String token) throws Exception {
		
		String jedisUserId = JedisUtils.get(token);
		UserInfo userInfo = userInfoDao.get(jedisUserId);
		UserAccountInfo userAccountInfo = userAccountInfoDao.get(userInfo.getAccountId());
        UserBankCard userBankCard = userBankCardDao.getUserBankCardByUserId(userInfo.getId());
        System.out.println(userInfo);
		//构造支付订单
		AuthPayInfo authPayInfo = new AuthPayInfo();	
		authPayInfo.setVersion(PartnerConfig.VERSION);//版本号
		authPayInfo.setOid_partner(PartnerConfig.OID_PARTNER);//平台商户号
		authPayInfo.setUser_id(userInfo.getId());//用户唯一编号
		authPayInfo.setSign_type(PartnerConfig.SIGN_TYPE);//签名方式 md5
		authPayInfo.setBusi_partner(PartnerConfig.BUSI_PARTNER);//业务类型
        authPayInfo.setNo_order("A_" + new Date().getTime());//订单号
		authPayInfo.setDt_order(LLPayUtil.getCurrentDateTimeStr());//订单时间
		authPayInfo.setName_goods("中投摩根用户充值");//商品名称
		authPayInfo.setInfo_order("中投摩根用户充值");//备注信息
		authPayInfo.setMoney_order(amount);//订单金额
		authPayInfo.setNotify_url(ServerURLConfig.RECHARGE_NOTIFY_URL);//订单通知地址（异步）
		authPayInfo.setUrl_return("https://www.cicmorgan.com/svc/services/backto/backto?backto=web");//订单回调地址（同步）
		authPayInfo.setUserreq_ip(ip.replaceAll("\\.","_").trim());//用户端申请ip
        authPayInfo.setValid_order("10080");// 订单有效时间，单位分钟，可以为空，默认7天
        authPayInfo.setTimestamp(LLPayUtil.getCurrentDateTimeStr());//签名时间戳有效时间

        authPayInfo.setRisk_item(createAuthPayRiskItem(userInfo.getId(),userInfo.getRegisterDate(),userInfo.getRealName(),userInfo.getCertificateNo()));//风险控制参数
        // 商戶从自己系统中获取用户身份信息（认证支付必须将用户身份信息传输给连连，且修改标记flag_modify设置成1：不可修改）
        authPayInfo.setId_type("0"); //证件类型 默认为0 身份证
        authPayInfo.setId_no(userInfo.getCertificateNo());//身份证号
        authPayInfo.setAcct_name(userInfo.getRealName());//真实姓名
        authPayInfo.setFlag_modify("1");//不可修改
        authPayInfo.setPay_type("D");
        authPayInfo.setNo_agree(userInfo.getLlagreeNo());
        // 加签名
        String sign = LLPayUtil.addSign(JSON.parseObject(JSON
                .toJSONString(authPayInfo)), PartnerConfig.TRADER_PRI_KEY,
                PartnerConfig.MD5_KEY);
        authPayInfo.setSign(sign);
        
        //记录充值
        UserRecharge userRecharge = new UserRecharge();
        userRecharge.setAccountId(userAccountInfo.getId());
        userRecharge.setUserId(userInfo.getId());
        userRecharge.setAmount(Double.valueOf(amount));
        userRecharge.setBeginBeginDate(new Date());
        userRecharge.setBeginDate(userRecharge.getBeginBeginDate());
        userRecharge.setCreateDate(userRecharge.getBeginBeginDate());
        userRecharge.setFeeAmount(0d);
        userRecharge.setState(UserRecharge.RECHARGE_DOING);
        userRecharge.setBank(userBankCard.getBankNo());
        userRecharge.setBankAccount(userBankCard.getBankAccountNo());
        userRecharge.setPlatForm(UserRecharge.RECHARGE_WEB);
        userRecharge.setIp(ip);
        userRecharge.setId(IdGen.uuid());//交易id
        userRecharge.setSn(authPayInfo.getNo_order());//订单号
        userRechargeDao.insert(userRecharge);
        
        //返回订单信息
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("version", authPayInfo.getVersion());
        result.put("oid_partner", authPayInfo.getOid_partner());
        result.put("user_id", authPayInfo.getUser_id());
        result.put("sign_type", authPayInfo.getSign_type());
        result.put("busi_partner", authPayInfo.getBusi_partner());
        result.put("no_order", authPayInfo.getNo_order());
        result.put("dt_order", authPayInfo.getDt_order());
        result.put("name_goods", authPayInfo.getName_goods());
        result.put("info_order", authPayInfo.getInfo_order());
        result.put("money_order", authPayInfo.getMoney_order());
        result.put("notify_url", authPayInfo.getNotify_url());
        result.put("url_return", authPayInfo.getUrl_return());
        result.put("userreq_ip", authPayInfo.getUserreq_ip());
        result.put("valid_order", authPayInfo.getValid_order());
        result.put("timestamp", authPayInfo.getTimestamp());
        result.put("sign", authPayInfo.getSign());
        result.put("risk_item", authPayInfo.getRisk_item());
        result.put("no_agree", authPayInfo.getNo_agree());
        result.put("id_type", authPayInfo.getId_type());
        result.put("id_no", authPayInfo.getId_no());
        result.put("acct_name", authPayInfo.getAcct_name());
        result.put("flag_modify", authPayInfo.getFlag_modify());
        result.put("pay_type", authPayInfo.getPay_type());
		return result;
	}
	
	/**
	 * 网银充值
	 * @param ip 
	 * @param amount 金额
	 * @param token
	 * @param bankCode 银行编码
	 * @throws Exception 
	 */
	@Transactional(readOnly = false,rollbackFor=Exception.class)
	public Map<String, Object> gateWayPay(String ip, String amount, String token,String bankCode) throws Exception
    {	
		String jedisUserId = JedisUtils.get(token);
		UserInfo userInfo = userInfoDao.get(jedisUserId);
		UserAccountInfo userAccountInfo = userAccountInfoDao.get(userInfo.getAccountId());
        // 构造支付请求对象
        AuthPayInfo paymentInfo = new AuthPayInfo();
		paymentInfo.setVersion(PartnerConfig.VERSION);
		paymentInfo.setOid_partner(PartnerConfig.OID_PARTNER);
		paymentInfo.setUser_id(userInfo.getId());
		paymentInfo.setSign_type(PartnerConfig.SIGN_TYPE);
		paymentInfo.setBusi_partner(PartnerConfig.BUSI_PARTNER);
		paymentInfo.setDt_order(LLPayUtil.getCurrentDateTimeStr());
		paymentInfo.setName_goods("中投摩根网银充值");
		paymentInfo.setInfo_order("中投摩根网银充值");
		paymentInfo.setMoney_order(amount);
		paymentInfo.setNotify_url(ServerURLConfig.RECHARGE_NOTIFY_URL);
	    paymentInfo.setUrl_return("https://www.cicmorgan.com/svc/services/backto/backto?backto=web");
		paymentInfo.setUserreq_ip(ip.replaceAll("\\.","_").trim());
		paymentInfo.setUrl_order("");
		paymentInfo.setValid_order("10080");// 单位分钟，可以为空，默认7天
		paymentInfo.setBank_code(bankCode);
		paymentInfo.setTimestamp(LLPayUtil.getCurrentDateTimeStr());
		paymentInfo.setRisk_item(createGateWayRiskItem(userInfo.getId(),userInfo.getRegisterDate()));   
		paymentInfo.setPay_type("1");
		paymentInfo.setNo_order("G_" + IdGen.uuid());
		// 加签名
		String sign = LLPayUtil.addSign(JSON.parseObject(JSON
				.toJSONString(paymentInfo)), PartnerConfig.TRADER_PRI_KEY,
				PartnerConfig.MD5_KEY);
		paymentInfo.setSign(sign);
		//记录充值
        UserRecharge userRecharge = new UserRecharge();
        userRecharge.setAccountId(userAccountInfo.getId());
        userRecharge.setUserId(userInfo.getId());
        userRecharge.setAmount(Double.valueOf(amount));
        userRecharge.setBeginBeginDate(new Date());
        userRecharge.setBeginDate(userRecharge.getBeginBeginDate());
        userRecharge.setCreateDate(userRecharge.getBeginBeginDate());
        userRecharge.setFeeAmount(0d);
        userRecharge.setState(UserRecharge.RECHARGE_DOING);
        userRecharge.setBank(bankCode);
        userRecharge.setBankAccount("-----------");
        userRecharge.setPlatForm(UserRecharge.RECHARGE_GATEWAY);
        userRecharge.setIp(ip);
        userRecharge.setId(IdGen.uuid());//交易id
        userRecharge.setSn(paymentInfo.getNo_order());//订单号
        userRechargeDao.insert(userRecharge);
        //返回支付参数
		LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("version", paymentInfo.getVersion());
		params.put("oid_partner", paymentInfo.getOid_partner());
		params.put("user_id", paymentInfo.getUser_id());
		params.put("sign_type", paymentInfo.getSign_type());
		params.put("busi_partner", paymentInfo.getBusi_partner());
		params.put("no_order", paymentInfo.getNo_order());
		params.put("dt_order", paymentInfo.getDt_order());
		params.put("name_goods", paymentInfo.getName_goods());
		params.put("info_order", paymentInfo.getInfo_order());
		params.put("money_order", paymentInfo.getMoney_order());
		params.put("notify_url", paymentInfo.getNotify_url());
		params.put("url_return", paymentInfo.getUrl_return());
		params.put("userreq_ip", paymentInfo.getUserreq_ip());
		params.put("url_order", paymentInfo.getUrl_order());
		params.put("valid_order", paymentInfo.getValid_order());
		params.put("timestamp", paymentInfo.getTimestamp());
		params.put("bank_code", paymentInfo.getBank_code());
		params.put("sign", paymentInfo.getSign());
		params.put("risk_item", paymentInfo.getRisk_item());
		params.put("pay_type", paymentInfo.getPay_type());
		return params;
    }
	
    /**
	 *  代付方法
	 * @param realName 真实姓名
	 * @param cardNo 银行卡号
	 * @param bankCode 银行编码（有银行卡号则可以不传）
	 * @param cityCode 城市编码
	 * @param brabankNname 开户行关键字
	 * @param money 代付金额
	 * @param infoOrder 订单描述
	 * @param flagCard 0对私 1对公
	 * @param notifyUrl 代付结果服务器异步通知地址（）
	 * @return Map 返回
	 *        key ret_code 结果编码  ret_msg 编码描述
	 *            on_order 订单号     dt_order 订单时间 （这两个字段需要保存起来用来做状态查询）
	 */
	@Transactional(readOnly = false,rollbackFor=Exception.class)
	 public Map<String,Object> goCashPay(String realName,String cardNo,String cityCode,String proviceCode,String brabankName,String idCard ,double money,String infoOrder,String noOrder,String bankCode)
	    {
		 	CashBean reqBean =new CashBean();
		 	reqBean.setPlatform("www.cicmorgan.com");
		 	reqBean.setOid_partner(PartnerConfig.OID_PARTNER);
		 	reqBean.setSign_type(PartnerConfig.SIGN_TYPE);
	        reqBean.setApi_version(PartnerConfig.CASH_VERSION);
	        reqBean.setNo_order(noOrder);
	        String dtOrder =LLPayUtil.getCurrentDateTimeStr();
	        reqBean.setDt_order(dtOrder);
	        reqBean.setMoney_order(String.valueOf(NumberUtils.scaleDoubleStr(money)));
	        reqBean.setFlag_card("0");
	        if (StringUtils.isNotBlank(bankCode)) {
		        reqBean.setFlag_card("1");
		        reqBean.setBank_code(bankCode);
	        }
	        reqBean.setBrabank_name(brabankName);
	        reqBean.setProvince_code(proviceCode);
	        reqBean.setCity_code(cityCode);
	        reqBean.setCard_no(cardNo);
	        reqBean.setAcct_name(realName);
	        reqBean.setInfo_order(infoOrder);
	        reqBean.setNotify_url("-------------");
	        reqBean.setPrcptcd(getPrcptcd(cardNo, brabankName, cityCode).trim());
		 	// 加签名
		    String sign = LLPayUtil.addSign(JSON.parseObject(JSON
		            .toJSONString(reqBean)), PartnerConfig.TRADER_PRI_KEY,
		            PartnerConfig.MD5_KEY);
		    reqBean.setSign(sign);
	        String reqJson = JSON.toJSONString(reqBean);

	        HttpRequestSimple httpclent =  HttpRequestSimple.getInstance();
	        String resJson = httpclent.postSendHttp("https://traderapi.lianlianpay.com/cardandpay.htm",
	                reqJson);
	        LOG.info("结果报文为:" + resJson) ;
	        Map<String,Object> resultmap = new HashMap<String,Object>();
	        resultmap.put("ret_code",JSON.parseObject(resJson).get("ret_code"));
	        resultmap.put("result_pay ",JSON.parseObject(resJson).get("result_pay "));
	        resultmap.put("ret_msg",JSON.parseObject(resJson).get("ret_msg"));
	        resultmap.put("on_order", noOrder);
	        resultmap.put("dt_order", dtOrder);
	        return resultmap;
	    }
	 /**
		 * 获取大额行号  CNAPSCodeQuery.htm
		 * @param bank_code 银行编码
		 * @param card_no 银行账号
		 * @param brabank_name 开户支行名称
		 * @param city_code 开户行所在市编码
		 * @return
		 */
	public static String getPrcptcd(String cardNo,String brabankName,String cityCode){
			PrcptcdInfo prcptcdInfo =new PrcptcdInfo();
			prcptcdInfo.setOid_partner(PartnerConfig.OID_PARTNER);
			prcptcdInfo.setSign_type(PartnerConfig.SIGN_TYPE);
			prcptcdInfo.setCard_no(cardNo);
			prcptcdInfo.setBrabank_name(brabankName);
			prcptcdInfo.setCity_code(cityCode);
			String prcptcdstrBack = "";
		    String sign = LLPayUtil.addSign(JSON.parseObject(JSON
		            .toJSONString(prcptcdInfo)), PartnerConfig.TRADER_PRI_KEY,
		            PartnerConfig.MD5_KEY);
		    prcptcdInfo.setSign(sign);
			String reqJson = JSON.toJSONString(prcptcdInfo);
			HttpRequestSimple httpclent =  HttpRequestSimple.getInstance();
		    String resJson = httpclent.postSendHttp("https://queryapi.lianlianpay.com/prcptcdquery.htm",
		                reqJson);
		    System.out.println("结果报文为:" + resJson) ;
		    if (resJson != null) {
		    	JSONArray  prcptcdstr = (JSONArray)JSON.parseObject(resJson).get("card_list");
			    
			        if(null!=prcptcdstr  && prcptcdstr.size()>0){
			        	 for(int i =0 ;i< prcptcdstr.size();i++){
			             	String tempa = prcptcdstr.get(i).toString();
			             	System.out.println("prcptcd in jsonarray ------------  "+JSON.parseObject(tempa).get("prcptcd"));
			             	prcptcdstrBack = String.valueOf(JSON.parseObject(tempa).get("prcptcd"));
			             }       
			        }
			}
		    
			return prcptcdstrBack;
		}
	/**
	 * 查询银行卡信息
	 * @param bankCard 银行卡号
	 * @return
	 */
	public String queryCardBin(String bankCard)
	    {

	        JSONObject reqObj = new JSONObject();
	        reqObj.put("oid_partner", PartnerConfig.OID_PARTNER);
	        reqObj.put("card_no", bankCard);
	        reqObj.put("sign_type", PartnerConfig.SIGN_TYPE);
	        String sign = LLPayUtil.addSign(reqObj, PartnerConfig.TRADER_PRI_KEY,
	                PartnerConfig.MD5_KEY);
	        reqObj.put("sign", sign);
	        String reqJSON = reqObj.toString();
	        LOG.info("银行卡卡bin信息查询请求报文[" + reqJSON + "]");
	        String resJSON = HttpRequestSimple.getInstance().postSendHttp(
	                ServerURLConfig.QUERY_BANKCARD_URL, reqJSON);
	        LOG.info("银行卡卡bin信息查询响应报文[" + resJSON + "]");
	        return resJSON;
	    }
	
	 /**
	  * 认证支付风控参数
	  * @param userId
	  * @param registerDate
	  * @param realName
	  * @param certificateNo
	  * @return
	  */
	 private String createAuthPayRiskItem(String userId,Date registerDate,String realName,String certificateNo){
		 	SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
	        JSONObject riskItemObj = new JSONObject();
	        riskItemObj.put("frms_ware_category", "2009");  //required
	        riskItemObj.put("frms_is_real_name","0" );
	        riskItemObj.put("user_info_mercht_userno", userId);  //required
	        riskItemObj.put("user_info_dt_register",format.format(registerDate));     //required
	        riskItemObj.put("user_info_full_name", realName);  //required
	        riskItemObj.put("user_info_id_type", 0);
	        riskItemObj.put("user_info_id_no", certificateNo);          //required      //required
	        riskItemObj.put("user_info_identify_state",1);      //required
	        riskItemObj.put("user_info_identify_type", 4);   //required        
	        return riskItemObj.toString();
	 }
	 /**
	  * 网银支付风控参数
	  * @param userId
	  * @param registerDate
	  * @return
	  */
	 private String createGateWayRiskItem(String userId,Date registerDate){
		 	SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
	        JSONObject riskItemObj = new JSONObject();
	        riskItemObj.put("frms_ware_category", "2009");  //required
	        riskItemObj.put("user_info_mercht_userno", userId);  //required
	        riskItemObj.put("user_info_dt_register",format.format(registerDate));     //required
	        riskItemObj.put("user_info_id_type", 0);
	        riskItemObj.put("user_info_identify_state",0);      //required
	        return riskItemObj.toString();
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
	public int cash(UserInfo userInfo,UserAccountInfo userAccountInfo ,String branchBank,
			String cityCode, String amount,Double feeAmount, String ip) throws Exception {
		//保存到提现表
		UserCash userCash = new UserCash();
		userCash.setAccountId(userAccountInfo.getId());
		userCash.setAmount(Double.valueOf(amount));
		userCash.setState(UserCash.CASH_INIT);
		userCash.setBank(userBankCardDao.getUserBankCardByUserId(userInfo.getId()).getBankNo());
		userCash.setBankAccount(userBankCardDao.getUserBankCardByUserId(userInfo.getId()).getBankAccountNo());
		userCash.setBeginDate(new Date());
		userCash.setBrabankName(branchBank);
		userCash.setCityCode(cityCode);
		userCash.setCreateDate(new Date());
		userCash.setFeeAccount(userAccountInfo.getId());
		userCash.setFeeAmount(feeAmount);
		userCash.setFrom(1);
		userCash.setId(IdGen.uuid());
		userCash.setIp(ip);
		userCash.setSn("W_" + new Date().getTime());
		userCash.setFrom(1);
		userCash.setUserId(userInfo.getId());
		userCash.setEndDate(userCash.getBeginDate());
		int result = userCashDao.insert(userCash);
		return result; 
	}
}