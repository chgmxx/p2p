package com.power.platform.lanmao.rw.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cunguanbao.api.utils.APIUtils;
import com.power.platform.cgb.dao.CgbUserAccountDao;
import com.power.platform.cgb.entity.CgbUserAccount;
import com.power.platform.cgb.entity.CgbUserBankCard;
import com.power.platform.cgb.entity.CgbUserTransDetail;
import com.power.platform.cgb.service.CgbUserAccountService;
import com.power.platform.cgb.service.CgbUserBankCardService;
import com.power.platform.cgb.service.CgbUserTransDetailService;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.JedisUtils;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.credit.dao.userinfo.CreditUserAccountDao;
import com.power.platform.credit.entity.userinfo.CreditUserAccount;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.service.userinfo.CreditUserAccountService;
import com.power.platform.credit.service.userinfo.CreditUserInfoService;
import com.power.platform.lanmao.common.AppUtil;
import com.power.platform.lanmao.rw.pojo.NotifyVo;
import com.power.platform.lanmao.rw.pojo.WithdrawNotifyVo;
import com.power.platform.lanmao.rw.pojo.WithdrawVo;
import com.power.platform.lanmao.type.StatusEnum;
import com.power.platform.lanmao.type.WithdrawFormEnum;
import com.power.platform.lanmao.type.WithdrawStatusEnum;
import com.power.platform.pay.cash.entity.UserCash;
import com.power.platform.pay.cash.service.UserCashService;
import com.power.platform.pay.recharge.entity.UserRecharge;
import com.power.platform.pay.recharge.service.UserRechargeService;
import com.power.platform.trandetail.entity.UserTransDetail;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.userinfo.service.UserInfoService;
import com.power.platform.weixin.service.WeixinSendTempMsgService;
 
/**
 * 懒猫 2.0 充值接口回调状态处理
 * @author google
 *
 */
@Service("lMWithdrawNotifyService")
public class LMWithdrawNotifyService {
	private static final Logger LOG = LoggerFactory.getLogger(LMWithdrawNotifyService.class);
 
	@Autowired
	private CgbUserAccountService cgbUserAccountService;
	@Autowired
	private CgbUserAccountDao cgbUserAccountDao;
	@Autowired
	private CgbUserTransDetailService cgbUserTransDetailService;
	@Autowired
	private WeixinSendTempMsgService weixinSendTempMsgService;
	@Autowired
	private CreditUserAccountService creditUserAccountService;
	@Autowired
	private CreditUserAccountDao creditUserAccountDao;
	@Autowired
	private UserCashService userCashService;
	@Autowired
	private WithdrawService withdrawService;
	 
	public String pressLMRechargeNotify(NotifyVo input) { 
		JSONObject respData = JSON.parseObject(input.getRespData());
		WithdrawNotifyVo withdrawVo = (WithdrawNotifyVo)JSON.parseObject(input.getRespData(), WithdrawNotifyVo.class);
		 LOG.info("提现对象： {}", withdrawVo.toString());

		 LOG.info("提现回调订单号{} ",withdrawVo.getRequestNo());
		//验密成功，进行业务处理
		 String orderId = AppUtil.CheckStringByDefault(withdrawVo.getRequestNo(), "");
		 String status = AppUtil.CheckStringByDefault(withdrawVo.getStatus(), "");
		 Double withdrawAmount = Double.valueOf(withdrawVo.getAmount());
		 String withdrawForm = AppUtil.CheckStringByDefault(withdrawVo.getWithdrawForm(), ""); // 提现类型；IMMEDIATE 为直接提现，CONFIRMED 为待确认提现。
		 String withdrawStatus = AppUtil.CheckStringByDefault(withdrawVo.getWithdrawStatus(), "FAIL");
		 String platformUserNo = AppUtil.CheckStringByDefault(withdrawVo.getPlatformUserNo(), "");
		 String amount = AppUtil.CheckStringByDefault(withdrawVo.getAmount(), "0.0");
		 LOG.info(new Date()+"返回结果订单"+ orderId + "&状态为"+ status);
		 LOG.info("提现回调订单号{} ",orderId);
		 LOG.info("提现回调状态{} ",status);
		 LOG.info("提现金额{} ",withdrawAmount);
		 //N1.用户更新提现表状态
		 UserCash userCash = userCashService.getInfoById(orderId);
//		 LOG.info("查询提现结果用户ID为{} ",userId);
		 LOG.info("查询提现结果状态为{} ",userCash.getState());
		 if(userCash != null && userCash.getState() == UserCash.CASH_DOING ){
			String orderluck = "luck" + orderId;
			String luckKey = JedisUtils.get(orderluck);
			if(luckKey != null) { // 如果不为null
				LOG.debug("》》》》》》》》》》》》》 重复的充值异步通知已经在处理，本次不处理{}......", orderluck);
				return "FAIL";
			}else {
				try {
					Random random = new Random();
					int s = random.nextInt(15000)%(15000-1000+1) + 1000;
					LOG.info("没有查到redis的key= {} >>>>>>>>>>>>>>>{}", orderluck, s);
					Thread.sleep(s);
				}catch(Exception e) {
					e.printStackTrace();
				}
				luckKey = JedisUtils.get(orderluck);
				if(luckKey != null) {
					LOG.debug("》》》》》》》》》》》》》 重复的充值异步通知已经在处理，本次不处理{}......", orderluck);
					return "FAIL";
				}else {
					String kvalue = JedisUtils.set(orderluck, orderId, 1000);
					LOG.debug("》》》》》》》》》》》》》 创建了redis的锁值key:{},value:{},kv:{}......", orderluck, orderId, kvalue);
				}
			}
			 if(status.equals(StatusEnum.SUCCESS.getValue()) && withdrawStatus.equals(WithdrawStatusEnum.SUCCESS.getValue())){  // 提现成功
				   //N2.用户账户解除冻结
				   CgbUserAccount userAccountInfo = cgbUserAccountService.get(userCash.getAccountId()); // 出借人
				   CreditUserAccount creditUserAccount = creditUserAccountService.get(userCash.getAccountId()); // 借款人
				   // 当前的提现是出借人
				   if(userAccountInfo!=null){
						String accKey = "ACC"+userAccountInfo.getId();
						String lockAccValue = JedisUtils.lockWithTimeout(accKey, 1000, 2000);//账户锁
						if(lockAccValue!=null && !lockAccValue.equals("")){
							
							    userCash.setState(UserCash.CASH_SUCCESS); 
								userCash.setUpdateDate(new Date());
								LOG.info("更新提现ID为"+userCash.getId()+"状态为"+userCash.getState()+"开始====>>>");
								int updateState = userCashService.updateState(userCash);
								LOG.info("提现更新sql执行结果======>>>"+updateState);
							    if(updateState>0){
								 LOG.info("[提现状态更新为"+userCash.getState()+"成功]");
								}
							
							    LOG.info("投资用户"+userCash.getUserId()+"提现"+withdrawAmount);
								double freezeAmount = userAccountInfo.getFreezeAmount();
								double totalAmount = userAccountInfo.getTotalAmount();
								double availableAmount = userAccountInfo.getAvailableAmount();
								// 冻结金额扣掉 
								userAccountInfo.setFreezeAmount(NumberUtils.scaleDouble(freezeAmount - withdrawAmount));
								// 直接扣除可用金额
//								userAccountInfo.setAvailableAmount(NumberUtils.scaleDouble(availableAmount - withdrawAmount));
								userAccountInfo.setCashAmount(userAccountInfo.getCashAmount() + withdrawAmount);
								userAccountInfo.setCashCount(userAccountInfo.getCashCount() +1);
								userAccountInfo.setTotalAmount(NumberUtils.scaleDouble(totalAmount - withdrawAmount));
								cgbUserAccountDao.update(userAccountInfo);
								 //N3.投资用户记录交易流水
								CgbUserTransDetail userTransDetail = new CgbUserTransDetail();
								userTransDetail.setUserId(userCash.getUserId());
								userTransDetail.setAmount(userCash.getAmount());
								userTransDetail.setAccountId(userCash.getAccountId());
								userTransDetail.setAvaliableAmount(userAccountInfo.getAvailableAmount());
								userTransDetail.setTransDate(new Date());
								userTransDetail.setBeginTransDate(new Date());
								userTransDetail.setInOutType(UserTransDetail.TRANS_TYPE_OUT);
								userTransDetail.setRemarks("投资用户提现");
								userTransDetail.setState(UserTransDetail.TRANS_STATE_SUCCESS);
								userTransDetail.setTrustType(UserTransDetail.TRANS_CASH);
								userTransDetail.setId(IdGen.uuid());
								userTransDetail.setTransId(userCash.getId());
								int i =cgbUserTransDetailService.insert(userTransDetail);
								if(i>0){
									LOG.info("投资用户交易流水记录成功");
						    		 LOG.info("账户锁释放开始======");
						    		 if(JedisUtils.releaseLock(accKey, lockAccValue)){
						    			 LOG.info("账户锁已释放");
										}
						    		 LOG.info("账户锁释放结束======");
								}
								 if(status.equals(StatusEnum.SUCCESS.getValue())){
									 // 发送提现申请微信、短信提醒
									 weixinSendTempMsgService.sendCashSuccessMsg(userCash);
									 return "SUCCESS";
								 }
						}
						// 借款人 
				   }else if(creditUserAccount!=null){ 
						   LOG.info("[借款用户]"+userCash.getUserId()+"提现"+withdrawAmount);
						   double freezaAmount = creditUserAccount.getFreezeAmount();
						   double totalAmount = creditUserAccount.getTotalAmount();
						   double availableAmount = creditUserAccount.getAvailableAmount();
							// 不操作冻结金额， 
						   creditUserAccount.setFreezeAmount(NumberUtils.scaleDouble(freezaAmount - withdrawAmount));
						   // 直接接口可用余额
//						   creditUserAccount.setAvailableAmount(NumberUtils.scaleDouble(availableAmount - withdrawAmount));
						   creditUserAccount.setWithdrawAmount(creditUserAccount.getWithdrawAmount() + withdrawAmount);
						   creditUserAccount.setTotalAmount(NumberUtils.scaleDouble(totalAmount - withdrawAmount));
						   creditUserAccountDao.update(creditUserAccount);
							 //N3.借款用户记录交易流水
						   CgbUserTransDetail userTransDetail = new CgbUserTransDetail();
							userTransDetail.setUserId(userCash.getUserId());
							userTransDetail.setAmount(userCash.getAmount());
							userTransDetail.setAccountId(userCash.getAccountId());
							userTransDetail.setAvaliableAmount(creditUserAccount.getAvailableAmount());
							userTransDetail.setTransDate(new Date());
							userTransDetail.setBeginTransDate(new Date());
							userTransDetail.setInOutType(UserTransDetail.TRANS_TYPE_OUT);
							userTransDetail.setRemarks("借款用户提现");
							userTransDetail.setState(UserTransDetail.TRANS_STATE_SUCCESS);
							userTransDetail.setTrustType(UserTransDetail.TRANS_CASH);
							userTransDetail.setId(IdGen.uuid());
							userTransDetail.setTransId(userCash.getId());
							int i =cgbUserTransDetailService.insert(userTransDetail);
							if(i>0){
								LOG.info("借款用户交易流水记录成功");
							}
							if(status.equals(StatusEnum.SUCCESS.getValue())){
							 // 发送提现申请微信、短信提醒
							 weixinSendTempMsgService.sendCashSuccessMsg(userCash);
							 return "SUCCESS";
							}
				   }
			     } else if(withdrawStatus.equals(WithdrawStatusEnum.FAIL.getValue()) || withdrawStatus.equals(WithdrawStatusEnum.ACCEPT_FAIL.getValue())){ // 提现失败
			    	  
					   CgbUserAccount userAccountInfo = cgbUserAccountService.get(userCash.getAccountId());
					   CreditUserAccount creditUserAccount = creditUserAccountService.get(userCash.getAccountId());
					   // 存在提现订单先成功后， 再推送失败的通知。 通过判断当前提现订单的状态， 如果是已成功，则只添加可用金额， 不再解冻， ；如果订单是提现中， 则正常解冻并添加可用金额；
					     if(creditUserAccount!=null){
					    	 LOG.info("[借款用户]提现失败资金解冻退回");
						     double freezaAmount = creditUserAccount.getFreezeAmount();
						     double availableAmount = creditUserAccount.getAvailableAmount();
					    	 double totalAmount = userAccountInfo.getTotalAmount();
						     if( userCash.getState() == UserCash.CASH_SUCCESS) { // 先成功， 后失败， 不用解冻
						    	 ;
						    	//需要添加一条收入提现流水
						    	 LOG.info("[借款用户]提现先成后后失败，添加提现流水，提现流水号：{}", orderId);
						    	 adduserTransDetail(userCash, NumberUtils.scaleDouble(availableAmount + withdrawAmount),0);
						     } else {
						    	 creditUserAccount.setFreezeAmount(NumberUtils.scaleDouble(freezaAmount - withdrawAmount));
						     }
					    	 creditUserAccount.setAvailableAmount(NumberUtils.scaleDouble(availableAmount + withdrawAmount));
					    	 creditUserAccount.setTotalAmount(NumberUtils.scaleDouble(totalAmount + withdrawAmount));
					    	 int i = creditUserAccountDao.update(creditUserAccount);
					    	 if(i>0){
					    		 LOG.info("借款户提现失败资金解冻成功");
					    	 }
					    	 
					     }else if(userAccountInfo!=null){
					    	 LOG.info("[出借用户]提现失败资金解冻退回");
					    	 double freezaAmount = userAccountInfo.getFreezeAmount();
					    	 double availableAmount = userAccountInfo.getAvailableAmount();
					    	 double totalAmount = userAccountInfo.getTotalAmount();
					    	 int cashCount = userAccountInfo.getCashCount();
					    	 if( userCash.getState() == UserCash.CASH_SUCCESS) { // 先成功， 后失败， 不用解冻
						    	 ;
						    	 //需要添加一条收入提现流水
						    	 LOG.info("[出借用户]提现先成后后失败，添加提现流水，提现流水号：{}", orderId);
						    	 adduserTransDetail(userCash, NumberUtils.scaleDouble(availableAmount + withdrawAmount),1);
						     } else {
						    	 userAccountInfo.setFreezeAmount(NumberUtils.scaleDouble(freezaAmount - withdrawAmount)); 
						     }
					    	 userAccountInfo.setAvailableAmount(NumberUtils.scaleDouble(availableAmount + withdrawAmount));
					    	 userAccountInfo.setTotalAmount(NumberUtils.scaleDouble(totalAmount + withdrawAmount));
					    	 userAccountInfo.setCashCount(cashCount>1?cashCount - 1:0);
					    	 int i = cgbUserAccountDao.update(userAccountInfo);
					    	 if(i>0){
					    		 LOG.info("出借用户提现失败资金解冻成功");
					    	 }
					     }
					     userCash.setState(UserCash.CASH_VERIFY_FAIL);
						 userCash.setUpdateDate(new Date());
						 int updateState = userCashService.updateState(userCash);
					     if(updateState>0){
					    	 LOG.info("[提现状态更新为"+userCash.getState()+"成功]");
						 }
					     return "SUCCESS";
				   } else if (withdrawStatus.equals(WithdrawStatusEnum.ACCEPT.getValue()) || withdrawStatus.equals(WithdrawStatusEnum.REMITING.getValue())) { // 提现处理中，需要冻结提现金额
					   LOG.info("提现异步通知状态为，{}， 提现中间状态，继续待终及状态, 提现流水号为：", withdrawStatus, orderId);
					   return "SUCCESS";
				   }
		 }else {
			
			 LOG.info("未查询到提现流水， 流水号为：{}", orderId);
			 return "SUCCESS";
		 }
		return "FAIL";
	}
	
	/**
	 * 
	 */
	public void adduserTransDetail(UserCash userCash, Double availableAmount, int flag) {
		//N3.借款用户记录交易流水
	   CgbUserTransDetail userTransDetail = new CgbUserTransDetail();
		userTransDetail.setUserId(userCash.getUserId());
		userTransDetail.setAmount(userCash.getAmount());
		userTransDetail.setAccountId(userCash.getAccountId());
		userTransDetail.setAvaliableAmount(availableAmount);
		userTransDetail.setTransDate(new Date());
		userTransDetail.setBeginTransDate(new Date());
		userTransDetail.setInOutType(UserTransDetail.TRANS_TYPE_IN);
		if(flag == 1) {
			userTransDetail.setRemarks("出借用户提现成功后又失败，上账交易流水");
		}else {
			userTransDetail.setRemarks("借款用户提现成功后又失败，上账交易流水");
		}
		userTransDetail.setState(UserTransDetail.TRANS_STATE_SUCCESS);
		userTransDetail.setTrustType(UserTransDetail.TRANS_CASH);
		userTransDetail.setId(IdGen.uuid());
		userTransDetail.setTransId(userCash.getId());
		int i =cgbUserTransDetailService.insert(userTransDetail);
		if(i>0){
			LOG.info("借款用户交易流水记录成功");
		}
	}
}
