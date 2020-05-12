package com.power.platform.lanmao.rw.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.credit.dao.userinfo.CreditUserAccountDao;
import com.power.platform.credit.entity.userinfo.CreditUserAccount;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.service.userinfo.CreditUserAccountService;
import com.power.platform.credit.service.userinfo.CreditUserInfoService;
import com.power.platform.lanmao.common.AppUtil;
import com.power.platform.lanmao.common.SignatureAlgorithm;
import com.power.platform.lanmao.common.SignatureUtils;
import com.power.platform.lanmao.config.RedirectUrlConfig;
import com.power.platform.lanmao.rw.pojo.WithdrawVo;
import com.power.platform.lanmao.rw.utils.ResultVO;
import com.power.platform.lanmao.rw.utils.ResultVOUtil;
import com.power.platform.lanmao.type.ServiceNameEnum;
import com.power.platform.lanmao.type.StatusEnum;
import com.power.platform.lanmao.type.WithdrawWayEnum;
import com.power.platform.pay.cash.entity.UserCash;
import com.power.platform.pay.cash.service.CGBUserWithdrawService;
import com.power.platform.pay.cash.service.UserCashService;
import com.power.platform.trandetail.entity.UserTransDetail;
import com.power.platform.userinfo.dao.UserLogDao;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.userinfo.entity.UserLog;
import com.power.platform.userinfo.service.UserInfoService;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.joda.time.DateTime;
import com.alibaba.fastjson.JSON;
import java.security.PrivateKey;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.power.platform.cgb.dao.CgbUserAccountDao;
import com.power.platform.cgb.entity.CgbUserAccount;
import com.power.platform.cgb.entity.CgbUserTransDetail;
import com.power.platform.cgb.service.CgbUserAccountService;
import com.power.platform.cgb.service.CgbUserTransDetailService;
import com.power.platform.cgbpay.config.ServerURLConfig;
import com.power.platform.common.config.Global;
import com.power.platform.common.exception.WinException;

import org.apache.commons.codec.binary.Base64;

/**
 * 用户在网贷平台发起提现请求，跳转到存管系统提现页面，输入交易密码进行直接提现或待确认提现
 *  R1.用户未绑卡不能进行提现操作；
    R2.若提现为直接提现，余额实时扣减，且同步返回提现受理结果，当存管操作出款以后，提现状态会变更为
    提现成功或提现失败，并增加异步回调通知，请参考异步通知参数；若提现为待确认提现，则仅冻结提现金
    额，同步返回提现请求受理结果，之后可以操作提现确认或取消提现；
    R3.待确认提现，何时确认何时受理提现；
    R4.提现分佣必须小于提现金额；
    R5.提现失败以后存管系统会发起自动资金回充，操作用户提现受理时已扣减资金的回充调增；若原提现订单
    包含提现佣金，则资金回充时佣金将原路返回，从平台收入账户中直接扣除，平台需要保证平台收入账户余
    额充足；
    R6.系统自动资金回充成功后，会主动发送异步通知，告知平台该笔提现失败订单已经操作资金回充；平台需
    要更新用户账户余额；
    R7.系统自动回充成功以后日终会生成对应的资金回退充值对账文件；
    R8.智能 D0 提现，当平台垫资账户余额不足时系统自动转为 T+1 提现； R9.D0 提现业务规则详见 D0 提现产品说明文档

    chenhj ant-loiter.com

 */
@Service("withdrawService")
public class WithdrawService {
    private final static Logger  logger = LoggerFactory.getLogger(WithdrawService.class);

	@Autowired
    private CGBUserWithdrawService cGBUserWithdrawService;
	
	@Autowired
	private UserInfoService userInfoService; 
	@Autowired
	private UserLogDao userLogDao;
	
	@Autowired
	private CreditUserInfoService creditUserInfoService; 
	
	@Autowired
	private CgbUserAccountService cgbUserAccountService;  
	
	@Resource
	private CgbUserAccountDao cgbUserAccountDao; 
	
	@Resource
	private CreditUserAccountDao creditUserAccountDao; 
	
	@Autowired
	private CreditUserAccountService creditUserAccountService; 
	
	@Autowired
	private UserCashService userCashService; 
	@Autowired
	private CgbUserTransDetailService cgbUserTransDetailService;
    /**
	 * 出借人
	 * @param rechargeVo
	 */
	public Map<String, String>  doWithdraw( String userId, 
											String accountId,
											String amount,
											double feeAomunt, 
											String ip,
											String type) throws WinException, Exception {
        
		// 构造请求参数
 		Map<String, Object> signMap = new HashMap<String, Object>();
 		signMap.put("platformUserNo", userId);
 		String requestNo = IdGen.uuid();
 		signMap.put("requestNo", requestNo);
		signMap.put("timestamp", DateUtils.formatDate(new Date(), "yyyyMMddHHmmss"));
		// 计算页面过期时间 当前时间增加30分钟
		DateTime dateTime = new DateTime();
		signMap.put("expired", dateTime.plusMinutes(30).toString("yyyyMMddHHmmss"));
 		signMap.put("redirectUrl", RedirectUrlConfig.BACK_WITHDRAW_BACKTO_URL_WEB);
 		signMap.put("withdrawType", WithdrawWayEnum.NORMAL.getValue());
 		signMap.put("amount", amount);
 		if(feeAomunt <= 0.01) {
 			;
 		}else {
 			signMap.put("commission", String.valueOf(feeAomunt));
 		}
		String _sign = AppUtil.signParam(signMap);
		String signMapTOString = JSON.toJSONString(signMap);
		// 定义reqData参数集合
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("serviceName", ServiceNameEnum.WITHDRAW.getValue());
		paramMap.put("platformNo", Global.getConfigLanMao("platformNo"));
//		paramMap.put("userDevice", AppUtil.CheckStringByDefault(userDevice, "PC"));
		paramMap.put("reqData", signMapTOString.replace("\\", ""));
		paramMap.put("keySerial", Global.getConfigLanMao("keySerial"));
		paramMap.put("sign", _sign);
		logger.info("请求JSON： " + JSON.toJSONString(paramMap));
		//提现申请存表
		UserCash usercash = cGBUserWithdrawService.withdraw("", "", amount, feeAomunt, ip, requestNo, "01", userId, accountId);
		// 针对懒猫2.0接口， 添加一个授理成功的逻辑, 但是不会冻结可用金额
//		boolean result = withdrawOrder(usercash, userId, Double.valueOf(amount));
//		if(!result) {
// 			paramMap.put("state", "err");
//		}
		//返回订单信息
		paramMap.put("amount", amount);
		paramMap.put("feeAmount", String.valueOf(feeAomunt));
		return paramMap;
    }
	
	/**
	 * 借款端， 提现
	 * @param rechargeVo
	 */
	public Map<String, String>  doCreditWithdraw( String userId, 
											String accountId,
											String amount,
											double feeAomunt, 
											String ip,
											String type) throws WinException, Exception {
        
		// 构造请求参数
 		Map<String, Object> signMap = new HashMap<String, Object>();
 		signMap.put("platformUserNo", userId);
 		String requestNo = IdGen.uuid();
 		signMap.put("requestNo", requestNo);
		signMap.put("timestamp", DateUtils.formatDate(new Date(), "yyyyMMddHHmmss"));
		// 计算页面过期时间 当前时间增加30分钟
		DateTime dateTime = new DateTime();
		signMap.put("expired", dateTime.plusMinutes(30).toString("yyyyMMddHHmmss"));
 		signMap.put("redirectUrl", RedirectUrlConfig.BACK_WITHDRAW_Credit_BACKTO_URL_WEB);
 		signMap.put("withdrawType", WithdrawWayEnum.NORMAL.getValue());
 		signMap.put("amount", amount);
 		if(feeAomunt <= 0.01) {
 			;
 		}else {
 			signMap.put("commission", String.valueOf(feeAomunt));
 		}
		String _sign = AppUtil.signParam(signMap);
		String signMapTOString = JSON.toJSONString(signMap);
		// 定义reqData参数集合
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("serviceName", ServiceNameEnum.WITHDRAW.getValue());
		paramMap.put("platformNo", Global.getConfigLanMao("platformNo"));
		paramMap.put("reqData", signMapTOString.replace("\\", ""));
		paramMap.put("keySerial", Global.getConfigLanMao("keySerial"));
		paramMap.put("sign", _sign);
		paramMap.put("amount", amount);
		paramMap.put("feeAmount", String.valueOf(feeAomunt));
		logger.info("请求JSON： " + JSON.toJSONString(paramMap));
 		try {
			//提现申请存表
			UserCash usercash = cGBUserWithdrawService.withdraw("", "", amount, feeAomunt, ip, requestNo, "01", userId, accountId);
			// 针对懒猫2.0接口， 添加一个授理成功的逻辑, 但是不会冻结可用金额
//			boolean result = withdrawOrder(usercash, userId, Double.valueOf(amount));
//			if(!result) {
//	 			paramMap.put("state", "err");
//			}
 		}catch(Exception e) {
 			e.printStackTrace();
 			paramMap.put("state", "err");
 		}
		return paramMap;
    }
	
	/**
	 * 借款端， 提现
	 * @param rechargeVo
	 */
	public Map<String, String>  doCreditWithdraw3( String userId, 
											String accountId,
											String amount,
											double feeAomunt, 
											String ip,
											String type,
											String from) throws WinException, Exception {
		// 构造请求参数
 		Map<String, Object> signMap = new HashMap<String, Object>();
 		if("05".equals(from)) { // 平台营销账户充值 
 			signMap.put("platformUserNo", Global.getConfigLanMao("sys_generate_002"));
 		}else {
 	 		signMap.put("platformUserNo", userId);
 		}
 		String requestNo = IdGen.uuid();
 		signMap.put("requestNo", requestNo);
		signMap.put("timestamp", DateUtils.formatDate(new Date(), "yyyyMMddHHmmss"));
		// 计算页面过期时间 当前时间增加30分钟
		DateTime dateTime = new DateTime();
		signMap.put("expired", dateTime.plusMinutes(30).toString("yyyyMMddHHmmss"));
 		signMap.put("redirectUrl", RedirectUrlConfig.BACK_WITHDRAW_Credit_BACKTO_URL_WEB);
 		signMap.put("withdrawType", WithdrawWayEnum.NORMAL.getValue());
 		signMap.put("amount", amount);
 		if(feeAomunt <= 0.01) {
 			;
 		}else {
 			signMap.put("commission", String.valueOf(feeAomunt));
 		}
		String _sign = AppUtil.signParam(signMap);
		String signMapTOString = JSON.toJSONString(signMap);
		// 定义reqData参数集合
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("serviceName", ServiceNameEnum.WITHDRAW.getValue());
		paramMap.put("platformNo", Global.getConfigLanMao("platformNo"));
//		paramMap.put("userDevice", AppUtil.CheckStringByDefault(userDevice, "PC"));
		paramMap.put("reqData", signMapTOString.replace("\\", ""));
		paramMap.put("keySerial", Global.getConfigLanMao("keySerial"));
		paramMap.put("sign", _sign);
		paramMap.put("amount", amount);
		paramMap.put("feeAmount", String.valueOf(feeAomunt));
		logger.info("请求JSON： " + JSON.toJSONString(paramMap));
 		try {
			//提现申请存表
			UserCash usercash = cGBUserWithdrawService.withdraw("", "", amount, feeAomunt, ip, requestNo, "01", userId, accountId);
//			// 针对懒猫2.0接口， 添加一个授理成功的逻辑, 但是不会冻结可用金额
//			boolean result = withdrawOrder(usercash, userId, Double.valueOf(amount));
//			if(!result) {
//	 			paramMap.put("state", "err");
//			}
			//返回订单信息
 		}catch(Exception e) {
 			e.printStackTrace();
 			paramMap.put("state", "err");
 		}
		return paramMap;
    }
	

	public boolean withdrawOrder(UserCash userCash, String userId, Double withdrawAmount, Integer status) {
		 UserInfo userInfo = userInfoService.getCgb(userId);
		 CreditUserInfo creditUserInfo = creditUserInfoService.get(userId); 
		 logger.info("String"+ String.valueOf(userCash.getState()).equals("0"));
		 logger.info("IntValue"+(userCash.getState().intValue() == 0));
		 logger.info("Integer"+(userCash.getState().equals("0")));
		 if(userInfo!=null && userCash.getState().intValue() == UserCash.CASH_APPLY){
				CgbUserAccount  userAccountInfo = cgbUserAccountService.get(userInfo.getAccountId());
				if(userAccountInfo!=null){
					logger.info("冻结前可用余额为"+userAccountInfo.getAvailableAmount());
					logger.info("冻结前冻结金额为"+userAccountInfo.getFreezeAmount());
					if(userAccountInfo.getAvailableAmount() < withdrawAmount) {
						logger.info("提现金额大于可用余额,{},{}",userAccountInfo.getAvailableAmount(), withdrawAmount);
						return false;
					}
					userAccountInfo.setAvailableAmount(NumberUtils.scaleDouble(userAccountInfo.getAvailableAmount() - withdrawAmount));
					userAccountInfo.setFreezeAmount(NumberUtils.scaleDouble(userAccountInfo.getFreezeAmount() + withdrawAmount));
					logger.info("冻结后可用余额为"+userAccountInfo.getAvailableAmount());
					logger.info("冻结后冻结金额为"+userAccountInfo.getFreezeAmount());
					cgbUserAccountDao.update(userAccountInfo);
					logger.info("投资用户提现订单状态为处理中,暂时冻结提现金额");
					// 成功冻结用户资金后， 由于可用金额临时性的变更， 所以需要记录一下可用金额临时变更交易记录； 这个变量有可能成功也有可能失败
					 //N3.投资用户记录交易流水
					CgbUserTransDetail userTransDetail = new CgbUserTransDetail();
					userTransDetail.setUserId(userCash.getUserId());
					userTransDetail.setAmount(userCash.getAmount());
					userTransDetail.setAccountId(userCash.getAccountId());
					userTransDetail.setAvaliableAmount(userAccountInfo.getAvailableAmount());
					userTransDetail.setTransDate(new Date());
					userTransDetail.setBeginTransDate(new Date());
					userTransDetail.setInOutType(UserTransDetail.TRANS_TYPE_OUT);
					userTransDetail.setRemarks("投资用户预提现冻结");
					userTransDetail.setState(UserTransDetail.TRANS_STATE_DOING);// 处理中的资金变中；
					userTransDetail.setTrustType(UserTransDetail.TRANS_CASH);
					userTransDetail.setId(IdGen.uuid());
					userTransDetail.setTransId(userCash.getId());
					int i =cgbUserTransDetailService.insert(userTransDetail);
					if(i > 0) {
						logger.info("{}用户成功操作提现， 提现金额为：{}，添加了交易id，id:{}", userCash.getUserId(), String.valueOf(userCash.getAmount()), userCash.getId());
					}
					// 添加提现日志； 
					UserLog log = new UserLog();
		            log.setId(String.valueOf(IdGen.randomLong()));
		            log.setUserId(userInfo.getId());
		            if(userInfo.getRealName()!=null)
		            	log.setUserName(userInfo.getRealName()+userInfo.getName());
		            else
		            	log.setUserName(userInfo.getName()); 
		            log.setType("5");
		            log.setCreateDate(new Date());
		            if(userInfo.getRealName()!=null)
		                log.setRemark(userInfo.getRealName()+"预提现"+userCash.getAmount()+"元");
		            else
		            	log.setRemark(userInfo.getName()+"预提现"+userCash.getAmount()+"元"); 
		            userLogDao.insert(log);
				}
		 }else if(creditUserInfo!=null && userCash.getState().intValue() == UserCash.CASH_APPLY){
				CreditUserAccount creditUserAccount = creditUserAccountService.get(creditUserInfo.getAccountId());
				logger.info("冻结前可用余额为"+creditUserAccount.getAvailableAmount());
				logger.info("冻结前冻结金额为"+creditUserAccount.getFreezeAmount());
				if(creditUserAccount.getAvailableAmount() < withdrawAmount) {
					logger.info("提现金额大于可用余额,{},{}",creditUserAccount.getAvailableAmount(), withdrawAmount);
					return false;
				}
				creditUserAccount.setAvailableAmount(NumberUtils.scaleDouble(creditUserAccount.getAvailableAmount() - withdrawAmount));
				creditUserAccount.setFreezeAmount(NumberUtils.scaleDouble(creditUserAccount.getFreezeAmount() + withdrawAmount));
				logger.info("冻结后可用余额为"+creditUserAccount.getAvailableAmount());
				logger.info("冻结后冻结金额为"+creditUserAccount.getFreezeAmount());
				creditUserAccountDao.update(creditUserAccount);
				logger.info("借款用户提现订单状态为处理中,暂时冻结提现金额");
				 //N3.借款用户记录交易流水
			   CgbUserTransDetail userTransDetail = new CgbUserTransDetail();
				userTransDetail.setUserId(userCash.getUserId());
				userTransDetail.setAmount(userCash.getAmount());
				userTransDetail.setAccountId(userCash.getAccountId());
				userTransDetail.setAvaliableAmount(creditUserAccount.getAvailableAmount());
				userTransDetail.setTransDate(new Date());
				userTransDetail.setBeginTransDate(new Date());
				userTransDetail.setInOutType(UserTransDetail.TRANS_TYPE_OUT);
				userTransDetail.setRemarks("借款用户预提现冻结");
				userTransDetail.setState(UserTransDetail.TRANS_STATE_DOING);
				userTransDetail.setTrustType(UserTransDetail.TRANS_CASH);
				userTransDetail.setId(IdGen.uuid());
				userTransDetail.setTransId(userCash.getId());
				int i =cgbUserTransDetailService.insert(userTransDetail);
				if(i>0){
					logger.info("{}用户成功操作提现， 提现金额为：{}，添加了交易id，id:{}", userCash.getUserId(), String.valueOf(userCash.getAmount()), userCash.getId());
				}
		 }
		 userCash.setState(status);
		 userCash.setUpdateDate(new Date());
		 int updateState = userCashService.updateState(userCash);
		 if(updateState>0){
			 logger.info("[提现状态更新为"+userCash.getState()+"成功]");
		 }
		 return true;
	}

}