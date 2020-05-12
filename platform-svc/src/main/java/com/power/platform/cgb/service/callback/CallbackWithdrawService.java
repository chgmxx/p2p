package com.power.platform.cgb.service.callback;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.cunguanbao.api.utils.APIUtils;
import com.power.platform.cgb.dao.CgbUserAccountDao;
import com.power.platform.cgb.entity.CgbUserAccount;
import com.power.platform.cgb.entity.CgbUserTransDetail;
import com.power.platform.cgb.service.CgbUserAccountService;
import com.power.platform.cgb.service.CgbUserTransDetailService;
import com.power.platform.common.config.Global;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.JedisUtils;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.credit.dao.userinfo.CreditUserAccountDao;
import com.power.platform.credit.entity.userinfo.CreditUserAccount;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.service.userinfo.CreditUserAccountService;
import com.power.platform.credit.service.userinfo.CreditUserInfoService;
import com.power.platform.pay.cash.entity.UserCash;
import com.power.platform.pay.cash.service.UserCashService;
import com.power.platform.trandetail.entity.UserTransDetail;
import com.power.platform.trandetail.service.UserTransDetailService;
import com.power.platform.userinfo.dao.UserAccountInfoDao;
import com.power.platform.userinfo.entity.UserAccountInfo;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.userinfo.service.UserAccountInfoService;
import com.power.platform.userinfo.service.UserInfoService;
import com.power.platform.weixin.service.WeixinSendTempMsgService;


@Component
@Path("/callbackwithdraw")
@Service("callbackWithdrawService")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class CallbackWithdrawService {
	
	
	private static final Logger LOG = LoggerFactory
			.getLogger(CallbackWithdrawService.class);
	
	@Autowired
	private UserAccountInfoService userAccountInfoService;
	@Autowired
	private UserCashService userCashService;
	@Autowired
	private UserTransDetailService userTransDetailService;
	@Autowired
	private WeixinSendTempMsgService weixinSendTempMsgService;
	@Autowired
	private CreditUserAccountService creditUserAccountService;
	@Autowired
	private UserInfoService userInfoService;
	@Autowired
	private CreditUserInfoService creditUserInfoService;
	@Autowired
	private CgbUserAccountService cgbUserAccountService;
	@Autowired
	private CgbUserTransDetailService cgbUserTransDetailService;
	@Autowired
	private CreditUserAccountDao creditUserAccountDao;
	@Autowired
	private CgbUserAccountDao cgbUserAccountDao;

	//存管系统为你分配的，用来对AES私钥加密，同时生成签名
	private static final String merchantRsaPublicKey = Global.getConfig("merchantRsaPublicKey");
	 
	//商户自己的RSA私钥
    private static final String merchantRsaPrivateKey = Global.getConfig("merchantRsaPrivateKey");
	
	/**
	 * 提现回调接口
	 * @param tm
	 * @param data
	 * @return
	 * @throws IOException
	 */
    
	@POST
	@Path("/withdrawWebNotify")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, String> notify(@FormParam("tm") String tm,
			@FormParam("data") String data)
			throws IOException {

		String jsonRet;
		Map<String, String> result = new HashMap<String,String>();

		try {
			//对通知数据进行解密
			jsonRet = APIUtils.decryptDataBySSL(tm, data,
					merchantRsaPrivateKey);
			Map<String, String> map = JSON.parseObject(jsonRet,
					new TypeReference<Map<String, String>>() {
					});
			String signRet = (String) map.get("signature");
			map.remove("signature");
			
			//校验验密
			boolean verifyRet = APIUtils.verify(
					merchantRsaPublicKey, signRet, map, "RSA");
			//验密成功，进行业务处理
			if (verifyRet) {
				 String orderId = (String)map.get("orderId");
				 String status = (String)map.get("status");
				 Double withdrawAmount = Double.valueOf((String)map.get("amount"))/100;
				 LOG.info(new Date()+"返回结果订单"+ orderId + "&状态为"+ status);
				 LOG.info("提现回调订单号"+orderId);
				 LOG.info("提现回调状态"+status);
				 LOG.info("提现金额"+withdrawAmount);
				 //N1.用户更新提现表状态
				 UserCash userCash = userCashService.getInfoById(orderId);
				 String userId = "";
				 if(userCash!=null){
					 userId = userCash.getUserId();
				 }else{
					 //N4.接收通知成功，通知对方服务器不在发送通知
					 result.put("respCode","00");
					 result.put("respMsg","成功");
					 String jsonString =JSON.toJSONString(result);
					 //对返回对方服务器消息进行加密
					 result =APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
					 //返回参数对方服务器，不在发送请求
					 return result;
				 }
				 LOG.info("查询提现结果用户ID为"+userId);
				 LOG.info("查询提现结果状态为"+userCash.getState());
				 if(userCash!=null){
					 //判断订单状态
					 if(status.equals("AS")){
						 UserInfo userInfo = userInfoService.getCgb(userId);
						 CreditUserInfo creditUserInfo = creditUserInfoService.get(userId); 
						 LOG.info("String"+ String.valueOf(userCash.getState()).equals("0"));
						 LOG.info("IntValue"+(userCash.getState().intValue() == 0));
						 LOG.info("Integer"+(userCash.getState().equals("0")));
						 if(userInfo!=null && userCash.getState().intValue() == 0){
								CgbUserAccount  userAccountInfo = cgbUserAccountService.get(userInfo.getAccountId());
								if(userAccountInfo!=null){
									LOG.info("冻结前可用余额为"+userAccountInfo.getAvailableAmount());
									LOG.info("冻结前冻结金额为"+userAccountInfo.getFreezeAmount());
									userAccountInfo.setAvailableAmount(NumberUtils.scaleDouble(userAccountInfo.getAvailableAmount() - withdrawAmount));
									userAccountInfo.setFreezeAmount(NumberUtils.scaleDouble(userAccountInfo.getFreezeAmount() + withdrawAmount));
									LOG.info("冻结后可用余额为"+userAccountInfo.getAvailableAmount());
									LOG.info("冻结后冻结金额为"+userAccountInfo.getFreezeAmount());
									cgbUserAccountDao.update(userAccountInfo);
									LOG.info("投资用户提现订单状态为处理中,暂时冻结提现金额");
								}
						 }else if(creditUserInfo!=null && userCash.getState().intValue() == 0){
								CreditUserAccount creditUserAccount = creditUserAccountService.get(creditUserInfo.getAccountId());
								LOG.info("冻结前可用余额为"+creditUserAccount.getAvailableAmount());
								LOG.info("冻结前冻结金额为"+creditUserAccount.getFreezeAmount());
								creditUserAccount.setAvailableAmount(NumberUtils.scaleDouble(creditUserAccount.getAvailableAmount() - withdrawAmount));
								creditUserAccount.setFreezeAmount(NumberUtils.scaleDouble(creditUserAccount.getFreezeAmount() + withdrawAmount));
								LOG.info("冻结后可用余额为"+creditUserAccount.getAvailableAmount());
								LOG.info("冻结后冻结金额为"+creditUserAccount.getFreezeAmount());
								creditUserAccountDao.update(creditUserAccount);
								LOG.info("借款用户提现订单状态为处理中,暂时冻结提现金额");
						 }
						 userCash.setState(UserCash.CASH_DOING);
						 userCash.setUpdateDate(new Date());
						 int updateState = userCashService.updateState(userCash);
						 if(updateState>0){
							 LOG.info("[提现状态更新为"+userCash.getState()+"成功]");
						 }
						 if(status.equals("AS")){
							 //N4.接收通知成功，通知对方服务器不在发送通知
							 result.put("respCode","00");
							 result.put("respMsg","成功");
							 String jsonString =JSON.toJSONString(result);
							 //对返回对方服务器消息进行加密
							 result =APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
							 //返回参数对方服务器，不在发送请求
							 return result;
						 }
					 }
					 else if(status.equals("S")){
						   //N2.用户账户解除冻结
						   CgbUserAccount userAccountInfo = cgbUserAccountService.get(userCash.getAccountId());
						   CreditUserAccount creditUserAccount = creditUserAccountService.get(userCash.getAccountId());
						   if(userAccountInfo!=null && status.equals("S")){
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
										userAccountInfo.setFreezeAmount(NumberUtils.scaleDouble(freezeAmount - withdrawAmount));
										userAccountInfo.setCashAmount(userAccountInfo.getCashAmount() + withdrawAmount);
										userAccountInfo.setCashCount(userAccountInfo.getCashCount() +1);
										userAccountInfo.setTotalAmount(NumberUtils.scaleDouble(totalAmount - withdrawAmount));
										cgbUserAccountDao.update(userAccountInfo);
										 //N3.投资用户记录交易流水
										CgbUserTransDetail userTransDetail = new CgbUserTransDetail();
										userTransDetail.setUserId(userCash.getUserId());
										userTransDetail.setAmount(userCash.getAmount());
										userTransDetail.setAccountId(userCash.getAccountId());
										userTransDetail.setAvaliableAmount(userAccountInfo
												.getAvailableAmount());
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
										 if(status.equals("S")){
											 // 发送提现申请微信、短信提醒
											 weixinSendTempMsgService.sendCashSuccessMsg(userCash);
											 //N4.接收通知成功，通知对方服务器不在发送通知
											 result.put("respCode","00");
											 result.put("respMsg","成功");
											 String jsonString =JSON.toJSONString(result);
											 //对返回对方服务器消息进行加密
											 result =APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
											 //返回参数对方服务器，不在发送请求
											 return result;
										 }
								}
						   }else if(creditUserAccount!=null && status.equals("S")){ 
								   LOG.info("[借款用户]"+userCash.getUserId()+"提现"+withdrawAmount);
								   double freezaAmount = creditUserAccount.getFreezeAmount();
								   double totalAmount = creditUserAccount.getTotalAmount();
								   creditUserAccount.setFreezeAmount(NumberUtils.scaleDouble(freezaAmount - withdrawAmount));
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
									if(status.equals("S")){
									 // 发送提现申请微信、短信提醒
									 weixinSendTempMsgService.sendCashSuccessMsg(userCash);
									 //N4.接收通知成功，通知对方服务器不在发送通知
									 result.put("respCode","00");
									 result.put("respMsg","成功");
									 String jsonString =JSON.toJSONString(result);
									 //对返回对方服务器消息进行加密
									 result =APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
									 //返回参数对方服务器，不在发送请求
									 return result;
									}
						   }
					     }
					     else if(status.equals("F")){
							   CgbUserAccount userAccountInfo = cgbUserAccountService.get(userCash.getAccountId());
							   CreditUserAccount creditUserAccount = creditUserAccountService.get(userCash.getAccountId());
							     if(creditUserAccount!=null){
							    	 LOG.info("[借款用户]提现失败资金解冻退回");
								     double freezaAmount = creditUserAccount.getFreezeAmount();
								     double availableAmount = creditUserAccount.getAvailableAmount();
							    	 creditUserAccount.setFreezeAmount(NumberUtils.scaleDouble(freezaAmount - withdrawAmount));
							    	 creditUserAccount.setAvailableAmount(NumberUtils.scaleDouble(availableAmount + withdrawAmount));
							    	 int i = creditUserAccountDao.update(creditUserAccount);
							    	 if(i>0){
							    		 LOG.info("借款户提现失败资金解冻成功");
							    	 }
							    	 
							     }else if(userAccountInfo!=null){
							    	 
									String accKey = "ACC"+userAccountInfo.getId();
									String lockAccValue = JedisUtils.lockWithTimeout(accKey, 10, 20000);//账户锁
									if(lockAccValue!=null && !lockAccValue.equals("")){
								    	 userCash.setState(UserCash.CASH_VERIFY_FAIL);
										 userCash.setUpdateDate(new Date());
										 int updateState = userCashService.updateState(userCash);
									     if(updateState>0){
										  LOG.info("[提现状态更新为"+userCash.getState()+"成功]");
										 }
									     double freezaAmount = userAccountInfo.getFreezeAmount();
									     double availableAmount = userAccountInfo.getAvailableAmount();
									     userAccountInfo.setFreezeAmount(NumberUtils.scaleDouble(freezaAmount - withdrawAmount));
									     userAccountInfo.setAvailableAmount(NumberUtils.scaleDouble(availableAmount + withdrawAmount));
									     int i = cgbUserAccountDao.update(userAccountInfo);
								    	 if(i>0){
								    		 LOG.info("出借户提现失败资金解冻成功");
								    		 LOG.info("账户锁释放开始======");
								    		 if(JedisUtils.releaseLock(accKey, lockAccValue)){
								    			 LOG.info("账户锁已释放");
												}
								    		 LOG.info("账户锁释放结束======");
								    	 }
									}

							     }
								 //N4.接收通知成功，通知对方服务器不在发送通知
								 result.put("respCode","00");
								 result.put("respMsg","成功");
								 String jsonString =JSON.toJSONString(result);
								 //对返回对方服务器消息进行加密
								 result =APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
								 //返回参数对方服务器，不在发送请求
								 return result;
						   }
				 }else{
					 LOG.info("未查询到订单号为"+orderId+"的提现记录");
				 }

			}else {
				 System.out.println("333333333333333");
					return result;

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

}
