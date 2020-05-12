package com.power.platform.lanmao.rw.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
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
import com.power.platform.common.config.Global;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.JedisUtils;
import com.power.platform.credit.dao.userinfo.CreditUserAccountDao;
import com.power.platform.credit.entity.userinfo.CreditUserAccount;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.service.userinfo.CreditUserAccountService;
import com.power.platform.credit.service.userinfo.CreditUserInfoService;
import com.power.platform.lanmao.common.AppUtil;
import com.power.platform.lanmao.rw.pojo.NotifyVo;
import com.power.platform.pay.recharge.entity.UserRecharge;
import com.power.platform.pay.recharge.service.NewRechargeService;
import com.power.platform.pay.recharge.service.UserRechargeService;
import com.power.platform.trandetail.entity.UserTransDetail;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.userinfo.service.UserInfoService;
import com.power.platform.weixin.service.WeixinSendTempMsgService;
 
/**
 * 懒猫 2.0 充值接口回调状态处理
 * @author google
 * 充值回调处理了四个逻辑：
 * 1、 出借人网银充值结果通知逻辑；
 * 2、 借款人网银充值结果通知逻辑；
 * 3、 出借人网银转账充值通知逻辑； 
 *             单获封装了此逻辑方法， ：
 *             1、 通知的流程号系统没有对应，需要根据流水号在平台插件转账充值流水记录；
 *             2、 通过用户平台编号， 通知结果以及充值金额调整出借人的账户可用余额和总金额的值；
 *             3、 成功， 可用余额 + 充值金额；总金额 + 充值金额；
 *             4、 失败， 只记录一条网银转账充值的失败流水， 用户账户不做任何操作；
 * 4、 借款人网银充值结果通知逻辑；
 * 			   逻辑同上；
 *
 */
@Service("lMRechargeNotifyService")
public class LMRechargeNotifyService {
	private static final Logger LOG = LoggerFactory.getLogger(LMRechargeNotifyService.class);

	@Autowired 
	private UserRechargeService userRechargeService;
	@Autowired
	private CgbUserBankCardService cgbUserBankCardService;
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
	private NewRechargeService newRechargeService;
	@Autowired
	private UserInfoService userInfoService;
	@Autowired
	private CreditUserInfoService creditUserInfoService;
	 
	public String pressLMRechargeNotify(NotifyVo input) { 
		Map<String, String> result = new HashMap<String, String>();
		JSONObject respData = JSON.parseObject(input.getRespData());
		String platformNo = input.getPlatformNo();
		String platformUserNo = respData.getString("platformUserNo");
		String orderId = respData.getString("requestNo");
		String code = respData.getString("code");
		String status = respData.getString("status");
		String amount = AppUtil.CheckStringByDefault(respData.getString("amount"), "0.0");
		String commission = respData.getString("commission");
		String rechargeWay = AppUtil.CheckStringByDefault(respData.getString("rechargeWay"), "");
		System.out.println("充值回调订单号" + orderId);
		// N1.用户更新充值表状态
		UserRecharge userRecharge = userRechargeService.getById(orderId);
		LOG.info(">>>>>>>>>>>>>>>" + userRecharge);
		// 判断当前充值流水是否已经处理状态, 防止异步通知重做相关业务
		if (userRecharge != null && userRecharge.getState() == UserRecharge.RECHARGE_DOING) {
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
			// 用户银行卡相关信息
			LOG.info("充值用户ID为" + userRecharge.getUserId());
			LOG.info("根据用户ID查询银行卡信息开始");
			CgbUserBankCard userBankCard = cgbUserBankCardService.findByUserId(userRecharge.getUserId());
			LOG.info("根据用户ID查询银行卡信息结束");
			LOG.info("订单状态为" + status);
			// N2.用户账户增加充值金额
			Double rechargeAmount = Double.valueOf(Double.valueOf(amount));
			LOG.info("充值金额" + rechargeAmount);
			String rechargeStatus = AppUtil.CheckStringByDefault( respData.getString("rechargeStatus"), "FAIL");
			// 账户信息存在， 并是充值成功
			if (status.equals("SUCCESS") && "SUCCESS".equals(rechargeStatus)) {// 出借人回调流程
				CgbUserAccount userAccountInfo = cgbUserAccountService.get(userRecharge.getAccountId());
				if(userAccountInfo != null) { // 出借人
					// 添加同当前用户当天快捷充值的累计值
					if(Objects.equals("SWIFT", rechargeWay)) {// 仅对快捷充值作限制
						if(!StringUtils.isBlank(platformUserNo)) {
							//在redis里添加当前用户充值累加记录
							StringBuilder sb = new StringBuilder("swiftrecharge_");
							sb.append(platformUserNo);
							sb.append("_");
							sb.append(DateUtils.getDateByymd());
							String maxByDay = AppUtil.CheckStringByDefault(JedisUtils.get(sb.toString()), "0.0");
							if(Objects.equals("0.0", maxByDay)) {//当天第一笔充值
								String setredisKey = JedisUtils.set(sb.toString(), amount,  DateUtils.getSeconds());
								
							}else {
								Double preRecharge = Double.valueOf(maxByDay);
								Double _amount = Double.valueOf(amount);
								
								String setredisKey = JedisUtils.set(sb.toString(), String.valueOf(preRecharge + _amount),  DateUtils.getSeconds());
							}
						}
					}
					String accKey = "ACC" + orderId;
					String lockAccValue = JedisUtils.lockWithTimeout(accKey, 10000, 2000);// 账户锁
					LOG.info("===============加锁开始====" + System.currentTimeMillis());
					LOG.info("===========钥匙==========" + lockAccValue);
					if (lockAccValue != null) {
						if (rechargeStatus.equals("SUCCESS")) {
							userRecharge.setState(UserRecharge.RECHARGE_SUCCESS);
						}
						if (userBankCard != null) {
							userRecharge.setBankAccount(userBankCard.getBankAccountNo());
						}
						userRecharge.setUpdateDate(new Date());
						int updateState = userRechargeService.updateState(userRecharge);
						if (updateState > 0) {
							LOG.info("充值订单号为" + orderId + "状态更新成功");
							// 账户增加金额
							LOG.info("[投资用户]" + userRecharge.getUserId() + "充值" + rechargeAmount);
							userAccountInfo.setTotalAmount(userAccountInfo.getTotalAmount() + rechargeAmount);
							userAccountInfo.setAvailableAmount(userAccountInfo.getAvailableAmount() + rechargeAmount);
							userAccountInfo.setRechargeAmount(userAccountInfo.getRechargeAmount() + rechargeAmount);
							userAccountInfo.setRechargeCount(userAccountInfo.getRechargeCount() + 1);
							cgbUserAccountDao.update(userAccountInfo);
							// 资金流水表更新状态
							CgbUserTransDetail userTransDetail = new CgbUserTransDetail();
							userTransDetail.setUserId(userRecharge.getUserId());
							userTransDetail.setAmount(userRecharge.getAmount());
							userTransDetail.setAccountId(userRecharge.getAccountId());
							userTransDetail.setAvaliableAmount(userAccountInfo.getAvailableAmount());
							userTransDetail.setTransDate(new Date());
							userTransDetail.setBeginTransDate(new Date());
							userTransDetail.setInOutType(UserTransDetail.TRANS_TYPE_IN);
							userTransDetail.setRemarks("投资用户充值");
							userTransDetail.setState(UserTransDetail.TRANS_STATE_SUCCESS);
							userTransDetail.setTrustType(UserTransDetail.TRANS_RECHARGE);
							userTransDetail.setId(IdGen.uuid());
							userTransDetail.setTransId(userRecharge.getId());
							int i = cgbUserTransDetailService.insert(userTransDetail);
							if (i > 0) {
								LOG.info("用户充值交易流水记录成功");
								// N4.充值成功发送微信、短信提醒
								weixinSendTempMsgService.sendUserRechargeMsg(userRecharge);

								LOG.info("账户锁释放开始======");
								if (JedisUtils.releaseLock(accKey, lockAccValue)) {
									LOG.info("账户锁已释放");
									LOG.info("=============释放锁结束====" + System.currentTimeMillis());
								}
								LOG.info("账户锁释放结束======");
							}
							return "SUCCESS";
						}
					} else {
						LOG.info("锁被占用");
						return "FAIL";
					}
				}
				
				CreditUserAccount creditAccount = creditUserAccountService.get(userRecharge.getAccountId());
				if(creditAccount != null ) {
					String accKey = "ACC" + orderId;
					String lockAccValue = JedisUtils.lockWithTimeout(accKey, 10000, 2000);// 账户锁

					LOG.info("===============加锁开始====" + System.currentTimeMillis());
					LOG.info("===========钥匙==========" + lockAccValue);
					if (lockAccValue != null) {
						if (rechargeStatus.equals("SUCCESS")) {
							userRecharge.setState(UserRecharge.RECHARGE_SUCCESS);
						}
						if (userBankCard != null) {
							userRecharge.setBankAccount(userBankCard.getBankAccountNo());
						}
						userRecharge.setUpdateDate(new Date());
						int updateState = userRechargeService.updateState(userRecharge);
						if (updateState > 0) {
							LOG.info("充值订单号为" + orderId + "状态更新成功");
						}
						if (creditAccount != null && userRecharge.getState() == UserRecharge.RECHARGE_SUCCESS) {
							LOG.info("[借款用户]" + userRecharge.getUserId() + "充值" + amount);
							creditAccount.setTotalAmount(creditAccount.getTotalAmount() + rechargeAmount);
							creditAccount.setAvailableAmount(creditAccount.getAvailableAmount() + rechargeAmount);
							creditAccount.setRechargeAmount(creditAccount.getRechargeAmount() + rechargeAmount);
							creditUserAccountDao.update(creditAccount);
							// 资金流水表更新状态
							CgbUserTransDetail userTransDetail = new CgbUserTransDetail();
							userTransDetail.setUserId(userRecharge.getUserId());
							userTransDetail.setAmount(rechargeAmount);
							userTransDetail.setAccountId(userRecharge.getAccountId());
							userTransDetail.setAvaliableAmount(creditAccount.getAvailableAmount());
							userTransDetail.setTransDate(new Date());
							userTransDetail.setBeginTransDate(new Date());
							userTransDetail.setInOutType(UserTransDetail.TRANS_TYPE_IN);
							userTransDetail.setRemarks("借款用户充值");
							userTransDetail.setState(UserTransDetail.TRANS_STATE_SUCCESS);
							userTransDetail.setTrustType(UserTransDetail.TRANS_RECHARGE);
							userTransDetail.setId(IdGen.uuid());
							userTransDetail.setTransId(userRecharge.getId());
							int i = cgbUserTransDetailService.insert(userTransDetail);
							if (i > 0) {
								LOG.info("用户充值交易流水记录成功");
								// N4.充值成功发送微信、短信提醒
//								weixinSendTempMsgService.sendUserRechargeMsg(userRecharge);

								LOG.info("账户锁释放开始======");
								if (JedisUtils.releaseLock(accKey, lockAccValue)) {
									LOG.info("账户锁已释放");
									LOG.info("=============释放锁结束====" + System.currentTimeMillis());
								}
								LOG.info("账户锁释放结束======");
							}
						}
					}else {
						LOG.info("锁被占用");
						return "FAIL";
					}
					// N5.接收通知成功，通知对方服务器不在发送通知
					return "SUCCESS";
				} 
				
			}else if(status.equals("SUCCESS") && "FAIL".equals(rechargeStatus)) {
				LOG.info("充值异步通知结果：{} ， 充值失败！", rechargeStatus);
				userRecharge.setState(UserRecharge.RECHARGE_FAIL);
				if (userBankCard != null) {
					userRecharge.setBankAccount(userBankCard.getBankAccountNo());
				}
				userRecharge.setUpdateDate(new Date());
				int updateState = userRechargeService.updateState(userRecharge);
				// N5.接收通知成功，通知对方服务器不在发送通知
				result.put("respCode", "11");
				result.put("respMsg", "充值结果为失败");
				return "SUCCESS";
			}else if(status.equals("SUCCESS") && "PENDDING".equals(rechargeStatus)) {
				LOG.info("充值异步通知中间状态：{} ， 支付中...... ！, 平台状态不作响应。", rechargeStatus);
			}
		} else { // 这个分支处理转账充值的逻辑
			 
//			String rechargeWay = AppUtil.CheckStringByDefault(respData.getString("rechargeWay"), "");
			if("ONLINE_RECHARGE".equals(rechargeWay.toUpperCase())) {
				LOG.info("未查询到订单号为" + orderId + "的充值记录");
				try {
					return transferRecharge("localhost", platformNo, respData);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace(); 
				}
			}else {
				LOG.debug("》》》》》》》》》》》》》 重复的充值异步通知，不予以处理.......");
			}
		}
		return "FAIL";
	}
	
	/**  参数为通知回调对象，
	 * 	requestNo	Y	S	50	请求流水号
		code	Y	E		调用状态（0为调用成功、1为失败，返回1时请看【调用失败错误码】及错误码描述）
		status	Y	E		业务处理状态（处理失败INIT；处理成功SUCCESS），平台可根据非SUCCESS状态做相应处理，处理失败时可参考错误码及描述
		errorCode	N	S	50	错误码、若支付失败时，显示存管错误码信息
		errorMessage 	N	S	50	错误码描述，若支付失败时，显示存管错误码描述
		rechargeStatus	Y	E		充值状态；SUCCESS支付成功
		platformUserNo	Y	S	50	平台用户编号
		amount	Y	A		充值金额
		rechargeWay	Y	E		见【支付方式】ONLINE_RECHARGE
		createTime	Y	T		交易发起时间
		transactionTime	N	T		交易完成时间
		bankcardNo	N	S	50	银行卡号
	 * @throws Exception 
	 * @throws NumberFormatException 
	*/
	private String transferRecharge(String ip, String platformNo, JSONObject respData) throws NumberFormatException, Exception {
		//  参数容错处理
		String platformUserNo = respData.getString("platformUserNo");
		String orderId = respData.getString("requestNo");
		String code = AppUtil.CheckStringByDefault(respData.getString("code"), "1");
		String status = AppUtil.CheckStringByDefault(respData.getString("status"), "INIT"); 
		String amount = AppUtil.CheckStringByDefault(respData.getString("amount"), "0");
		String rechargeStatus = AppUtil.CheckStringByDefault(respData.getString("rechargeStatus"), "FAIL"); 
//		CgbUserAccount userAccountInfo = cgbUserAccountService.getUserAccountInfo(platformUserNo);
		UserInfo userInfo = userInfoService.getCgb(platformUserNo);
		if(userInfo != null) {
		//  插入转账充值流水 Double amount, String ip, String orderId, String userId, String accountId
			newRechargeService.largeInsertRecharge(Double.valueOf(amount), ip, orderId, platformUserNo, userInfo.getAccountId());
		}else {
			CreditUserInfo credidUserInfo = creditUserInfoService.get(platformUserNo);
			//  插入转账充值流水 Double amount, String ip, String orderId, String userId, String accountId
			newRechargeService.largeInsertRecharge(Double.valueOf(amount), ip, orderId, platformUserNo, credidUserInfo.getAccountId());
		}
		
		// 查询出转账的充值流水信息
		UserRecharge userRecharge = userRechargeService.getById(orderId);
		CgbUserBankCard userBankCard = cgbUserBankCardService.findByUserId(userRecharge.getUserId());
		//  操作用户账户,判断操作结果
		// code = 0 && status == SUCCESS && rechargeStatus == SUCCESS 代表充值成功， 操作账号，
		// 否则 只更新userRecharge记录
		if( Integer.valueOf(code) == 0 && "SUCCESS".equals(status.toUpperCase()) && 
				"SUCCESS".equals(rechargeStatus.toUpperCase())) {
			userRecharge.setState(UserRecharge.RECHARGE_SUCCESS);
			userRecharge.setBankAccount(userBankCard.getBankAccountNo());
			userRecharge.setUpdateDate(new Date());
			int updateState = userRechargeService.updateState(userRecharge);
			if( updateState > 0) {
				// 根据用户编号查询出用户账户信息
				return OperationAccount(Double.valueOf(amount), platformUserNo, 
									orderId, platformUserNo, userRecharge);
			}
			return "FAIL";
		} else {
			// 处理失败
			userRecharge.setState(UserRecharge.RECHARGE_FAIL);
			userRecharge.setBankAccount(userBankCard.getBankAccountNo());
			userRecharge.setUpdateDate(new Date());
			int updateState = userRechargeService.updateState(userRecharge);
			return "FAIL";
		}
		
		
	}
	/**
	 *    操作不同用户账户信息
	 * @param amount
	 * @param platformUserNo
	 */
	private String OperationAccount(Double amount, 
									String platformUserNo, 
									String requestNo, 
									String accountId,
									UserRecharge userRecharge) {
		// 根据平台用户编号查出用户信息，判断是出借人还是借款人
		UserInfo userinfo = userInfoService.getCgb(platformUserNo);
		if(userinfo != null) {
			CgbUserAccount userAccountInfo = cgbUserAccountService.get(userinfo.getAccountId());
			String accKey = "ACC" + userAccountInfo.getId();
			String lockAccValue = JedisUtils.lockWithTimeout(accKey, 10000, 2000);// 账户锁
			LOG.info("===============加锁开始====" + System.currentTimeMillis());
			LOG.info("===========钥匙==========" + lockAccValue);
			if (lockAccValue != null) {
				LOG.info("[投资用户]" + userAccountInfo.getUserId() + "转账充值" + amount);
				userAccountInfo.setTotalAmount(userAccountInfo.getTotalAmount() + amount);
				userAccountInfo.setAvailableAmount(userAccountInfo.getAvailableAmount() + amount);
				userAccountInfo.setRechargeAmount(userAccountInfo.getRechargeAmount() + amount);
				userAccountInfo.setRechargeCount(userAccountInfo.getRechargeCount() + 1);
				cgbUserAccountDao.update(userAccountInfo);
				LOG.info("[投资用户] ,  转账充值金额更新成功！！！，可用金额为： " + (userAccountInfo.getAvailableAmount() + amount));
				// 资金流水表更新状态
				CgbUserTransDetail userTransDetail = new CgbUserTransDetail();
				userTransDetail.setUserId(platformUserNo);
				userTransDetail.setAmount(amount);
				userTransDetail.setAccountId(userAccountInfo.getId());
				userTransDetail.setAvaliableAmount(userAccountInfo.getAvailableAmount());
				userTransDetail.setTransDate(new Date());
				userTransDetail.setBeginTransDate(new Date());
				userTransDetail.setInOutType(UserTransDetail.TRANS_TYPE_IN);
				userTransDetail.setRemarks("投资用户转账充值");
				userTransDetail.setState(UserTransDetail.TRANS_STATE_SUCCESS);
				userTransDetail.setTrustType(UserTransDetail.TRANS_RECHARGE);
				userTransDetail.setId(IdGen.uuid());
				userTransDetail.setTransId(requestNo);
				int i = cgbUserTransDetailService.insert(userTransDetail);
				if (i > 0) {
					LOG.info("用户转账充值交易流水记录成功");
					// N4.充值成功发送微信、短信提醒
					//  查询出转账充值的流水信息
					weixinSendTempMsgService.sendUserRechargeMsg(userRecharge);
					LOG.info("账户锁释放开始======");
					if (JedisUtils.releaseLock(accKey, lockAccValue)) {
						LOG.info("账户锁已释放");
						LOG.info("=============释放锁结束====" + System.currentTimeMillis());
					}
					LOG.info("账户锁释放结束======");
				}
				return "SUCCESS";
			}else {
				LOG.info("锁被占用");
				return "FAIL";
			}
		}else {
			CreditUserInfo creditUserInfo = creditUserInfoService.get(platformUserNo);
			CreditUserAccount creditAccount = creditUserAccountService.get(creditUserInfo.getAccountId());
			// 账户接口同步加锁处理
			String accKey = "ACC" + creditAccount.getId();
			String lockAccValue = JedisUtils.lockWithTimeout(accKey, 10000, 2000);// 账户锁
			LOG.info("===============加锁开始====" + System.currentTimeMillis());
			LOG.info("===========钥匙==========" + lockAccValue);
			if (lockAccValue != null) {

				LOG.info("[借款用户]" + platformUserNo + "转账充值" + amount);
				creditAccount.setTotalAmount(creditAccount.getTotalAmount() + amount);
				creditAccount.setAvailableAmount(creditAccount.getAvailableAmount() + amount);
				creditAccount.setRechargeAmount(creditAccount.getRechargeAmount() + amount);
				creditUserAccountDao.update(creditAccount);
				LOG.info("[借款用户] ,  转账充值金额更新成功！！！，可用金额为： " + (creditAccount.getAvailableAmount() + amount));
				// 插入转账充值流水
				CgbUserTransDetail userTransDetail = new CgbUserTransDetail();
				userTransDetail.setUserId(platformUserNo);
				userTransDetail.setAmount(amount);
				userTransDetail.setAccountId(creditAccount.getId());// 账转充值没有充值记录ID
				userTransDetail.setAvaliableAmount(creditAccount.getAvailableAmount());
				userTransDetail.setTransDate(new Date());
				userTransDetail.setBeginTransDate(new Date());
				userTransDetail.setInOutType(UserTransDetail.TRANS_TYPE_IN);
				userTransDetail.setRemarks("借款用户转账充值");
				userTransDetail.setState(UserTransDetail.TRANS_STATE_SUCCESS);
				userTransDetail.setTrustType(UserTransDetail.TRANS_RECHARGE);
				userTransDetail.setId(IdGen.uuid());
				userTransDetail.setTransId(requestNo);
				int i = cgbUserTransDetailService.insert(userTransDetail);
				if (i > 0) {
					LOG.info("用户转账充值交易流水记录成功");
					// N4.充值成功发送微信、短信提醒
//					weixinSendTempMsgService.sendUserRechargeMsg(userRecharge);
					LOG.info("账户锁释放开始======");
					if (JedisUtils.releaseLock(accKey, lockAccValue)) {
						LOG.info("账户锁已释放");
						LOG.info("=============释放锁结束====" + System.currentTimeMillis());
					}
					LOG.info("账户锁释放结束======");
				}
				return "SUCCESS";
			} else {
				LOG.info("锁被占用");
				return "FAIL";
			}
		}
	}
}
