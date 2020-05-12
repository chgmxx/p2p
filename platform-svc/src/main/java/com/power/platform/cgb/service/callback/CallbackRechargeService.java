package com.power.platform.cgb.service.callback;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
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
import com.power.platform.cgb.entity.CgbUserBankCard;
import com.power.platform.cgb.entity.CgbUserTransDetail;
import com.power.platform.cgb.service.CgbUserAccountService;
import com.power.platform.cgb.service.CgbUserBankCardService;
import com.power.platform.cgb.service.CgbUserTransDetailService;
import com.power.platform.common.config.Global;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.JedisUtils;
import com.power.platform.credit.dao.userinfo.CreditUserAccountDao;
import com.power.platform.credit.entity.userinfo.CreditUserAccount;
import com.power.platform.credit.service.userinfo.CreditUserAccountService;
import com.power.platform.pay.recharge.entity.UserRecharge;
import com.power.platform.pay.recharge.service.NewRechargeService;
import com.power.platform.pay.recharge.service.UserRechargeService;
import com.power.platform.trandetail.entity.UserTransDetail;
import com.power.platform.trandetail.service.UserTransDetailService;
import com.power.platform.userinfo.service.UserAccountInfoService;
import com.power.platform.userinfo.service.UserBankCardService;
import com.power.platform.weixin.service.WeixinSendTempMsgService;

@Component
@Path("/callbackrecharge")
@Service("callbackRechargeService")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class CallbackRechargeService {

	private static final Logger LOG = LoggerFactory.getLogger(CallbackRechargeService.class);

	@Autowired
	private NewRechargeService newRechargeService;
	@Autowired
	private UserAccountInfoService userAccountInfoService;
	@Autowired
	private UserRechargeService userRechargeService;
	@Autowired
	private UserTransDetailService userTransDetailService;
	@Resource
	private WeixinSendTempMsgService weixinSendTempMsgService;
	@Autowired
	private CreditUserAccountService creditUserAccountService;
	@Autowired
	private UserBankCardService userBankCardService;
	@Autowired
	private CgbUserAccountService cgbUserAccountService;
	@Autowired
	private CgbUserTransDetailService cgbUserTransDetailService;
	@Autowired
	private CgbUserBankCardService cgbUserBankCardService;
	@Autowired
	private CgbUserAccountDao cgbUserAccountDao;
	@Autowired
	private CreditUserAccountDao creditUserAccountDao;

	// 存管系统为你分配的，用来对AES私钥加密，同时生成签名
	private static final String merchantRsaPublicKey = Global.getConfig("merchantRsaPublicKey");

	// 商户自己的RSA私钥
	private static final String merchantRsaPrivateKey = Global.getConfig("merchantRsaPrivateKey");

	/**
	 * 
	 * methods: offlineRechargeWebNotify <br>
	 * description: 转账充值. <br>
	 * author: Roy <br>
	 * date: 2019年4月1日 下午3:29:14
	 * 
	 * @param tm
	 * @param data
	 * @return
	 * @throws IOException
	 */
	@POST
	@Path("/offlineRechargeWebNotify")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, String> offlineRechargeWebNotify(@FormParam("tm") String tm, @FormParam("data") String data) throws IOException {

		String jsonRet;
		Map<String, String> result = new HashMap<String, String>();

		try {
			// 对通知数据进行解密
			jsonRet = APIUtils.decryptDataBySSL(tm, data, merchantRsaPrivateKey);
			Map<String, String> map = JSON.parseObject(jsonRet, new TypeReference<Map<String, String>>() {
			});
			String signRet = (String) map.get("signature");
			map.remove("signature");
			// 校验验密
			boolean verifyRet = APIUtils.verify(merchantRsaPublicKey, signRet, map, "RSA");
			// 验密成功，进行业务处理
			if (verifyRet) {
				String orderId = (String) map.get("orderId");
				String status = (String) map.get("status");
				LOG.info("订单号：" + orderId + "，状态：" + status);
				// N1.用户更新充值表状态
				UserRecharge userRecharge = userRechargeService.getById(orderId);
				// 用户银行卡相关信息
				LOG.info("充值用户ID为：" + userRecharge.getUserId());
				CgbUserBankCard userBankCard = cgbUserBankCardService.findByUserId(userRecharge.getUserId());
				if (userRecharge != null) {
					// N2.用户账户增加充值金额
					Double rechargeAmount = Double.valueOf(map.get("amount")) / 100;
					LOG.info("充值金额：" + rechargeAmount);
					CgbUserAccount userAccountInfo = cgbUserAccountService.get(userRecharge.getAccountId());
					if (userAccountInfo != null && status.equals("S")) {
						String accKey = "ACC" + userAccountInfo.getId();
						String lockAccValue = JedisUtils.lockWithTimeout(accKey, 10000, 2000);// 账户锁
						LOG.info("===============加锁开始====" + System.currentTimeMillis());
						LOG.info("===========钥匙==========" + lockAccValue);
						if (lockAccValue != null) {
							if (status.equals("S")) {
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
								// N5.接收通知成功，通知对方服务器不在发送通知
								result.put("respCode", "00");
								result.put("respMsg", "成功");
								String jsonString = JSON.toJSONString(result);
								// 对返回对方服务器消息进行加密
								result = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
								// 返回参数对方服务器，不在发送请求
								return result;
							}
						} else {
							LOG.info("锁被占用");
						}
					} else if (status.equals("S")) {
						CreditUserAccount creditAccount = creditUserAccountService.get(userRecharge.getAccountId());
						userRecharge.setState(UserRecharge.RECHARGE_SUCCESS);
						if (userBankCard != null) {
							userRecharge.setBankAccount(userBankCard.getBankAccountNo());
						}
						userRecharge.setUpdateDate(new Date());
						int updateState = userRechargeService.updateState(userRecharge);
						if (updateState == 1) {
							LOG.info("转账充值订单号：" + orderId + "，充值更新成功！");
						} else {
							LOG.info("转账充值订单号：" + orderId + "，充值更新失败！");
						}
						if (creditAccount != null && userRecharge.getState().equals(UserRecharge.RECHARGE_SUCCESS)) {
							LOG.info("[借款用户]：" + userRecharge.getUserId() + "，充值：" + rechargeAmount);
							creditAccount.setTotalAmount(creditAccount.getTotalAmount() + rechargeAmount);
							creditAccount.setAvailableAmount(creditAccount.getAvailableAmount() + rechargeAmount);
							creditAccount.setRechargeAmount(creditAccount.getRechargeAmount() + rechargeAmount);
							int update = creditUserAccountDao.update(creditAccount);
							if (update == 1) {
								LOG.info("转账充值订单号：" + orderId + "，借款企业账户更新成功！");
							} else {
								LOG.info("转账充值订单号：" + orderId + "，借款企业账户更新失败！");
							}
							// 资金流水表更新状态
							CgbUserTransDetail userTransDetail = new CgbUserTransDetail();
							userTransDetail.setUserId(userRecharge.getUserId());
							userTransDetail.setAmount(rechargeAmount);
							userTransDetail.setAccountId(userRecharge.getAccountId());
							userTransDetail.setAvaliableAmount(creditAccount.getAvailableAmount());
							userTransDetail.setTransDate(new Date());
							userTransDetail.setBeginTransDate(new Date());
							userTransDetail.setInOutType(UserTransDetail.TRANS_TYPE_IN);
							userTransDetail.setRemarks("借款企业用户转账充值");
							userTransDetail.setState(UserTransDetail.TRANS_STATE_SUCCESS);
							userTransDetail.setTrustType(UserTransDetail.TRANS_RECHARGE);
							userTransDetail.setId(IdGen.uuid());
							userTransDetail.setTransId(userRecharge.getId());
							int insert = cgbUserTransDetailService.insert(userTransDetail);
							if (insert == 1) {
								LOG.info("转账充值订单号：" + orderId + "，借款企业转账充值流水新增成功！");
							} else {
								LOG.info("转账充值订单号：" + orderId + "，借款企业转账充值流水新增失败！");
							}
						}
						// N5.接收通知成功，通知对方服务器不在发送通知
						result.put("respCode", "00");
						result.put("respMsg", "成功");
						String jsonString = JSON.toJSONString(result);
						// 对返回对方服务器消息进行加密
						result = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
						// 返回参数对方服务器，不在发送请求
						return result;
					} else if (status.equals("F")) {
						userRecharge.setState(UserRecharge.RECHARGE_FAIL);
						if (userBankCard != null) {
							userRecharge.setBankAccount(userBankCard.getBankAccountNo());
						}
						userRecharge.setUpdateDate(new Date());
						int updateStateFlag = userRechargeService.updateState(userRecharge);
						if (updateStateFlag == 1) {
							LOG.info("借款企业，充值订单更新成功");
						} else {
							LOG.info("借款企业，充值订单更新失败");
						}
						// N5.接收通知成功，通知对方服务器不在发送通知
						result.put("respCode", "00");
						result.put("respMsg", "成功");
						String jsonString = JSON.toJSONString(result);
						// 对返回对方服务器消息进行加密
						result = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
						// 返回参数对方服务器，不在发送请求
						return result;
					}
				}
			} else {
				LOG.info("验密失败 ...");
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 充值回调接口
	 * 
	 * @param tm
	 * @param data
	 * @return
	 * @throws IOException
	 */
	@POST
	@Path("/rechargeWebNotify")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, String> notify(@FormParam("tm") String tm, @FormParam("data") String data) throws IOException {

		String jsonRet;
		Map<String, String> result = new HashMap<String, String>();

		try {
			// 对通知数据进行解密
			jsonRet = APIUtils.decryptDataBySSL(tm, data, merchantRsaPrivateKey);
			Map<String, String> map = JSON.parseObject(jsonRet, new TypeReference<Map<String, String>>() {});
			String signRet = (String) map.get("signature");
			map.remove("signature");

			// 校验验密
			boolean verifyRet = APIUtils.verify(merchantRsaPublicKey, signRet, map, "RSA");
			// 验密成功，进行业务处理
			if (verifyRet) {
				String orderId = (String) map.get("orderId");
				String status = (String) map.get("status");
				System.out.println("充值回调订单号" + orderId);
				// N1.用户更新充值表状态
				UserRecharge userRecharge = userRechargeService.getById(orderId);
				// 用户银行卡相关信息
				LOG.info("充值用户ID为" + userRecharge.getUserId());
				LOG.info("根据用户ID查询银行卡信息开始");
				CgbUserBankCard userBankCard = cgbUserBankCardService.findByUserId(userRecharge.getUserId());
				LOG.info("根据用户ID查询银行卡信息结束");
				if (userRecharge != null) {
					LOG.info("订单状态为" + status);
					// N2.用户账户增加充值金额
					Double rechargeAmount = Double.valueOf(map.get("amount")) / 100;
					LOG.info("充值金额" + rechargeAmount);
					CgbUserAccount userAccountInfo = cgbUserAccountService.get(userRecharge.getAccountId());

					if (userAccountInfo != null && status.equals("S")) {
						String accKey = "ACC" + userAccountInfo.getId();
						String lockAccValue = JedisUtils.lockWithTimeout(accKey, 10000, 2000);// 账户锁

						LOG.info("===============加锁开始====" + System.currentTimeMillis());
						LOG.info("===========钥匙==========" + lockAccValue);
						if (lockAccValue != null) {
							if (status.equals("S")) {
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
								// N5.接收通知成功，通知对方服务器不在发送通知
								result.put("respCode", "00");
								result.put("respMsg", "成功");
								String jsonString = JSON.toJSONString(result);
								// 对返回对方服务器消息进行加密
								result = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
								// 返回参数对方服务器，不在发送请求
								return result;
							}
						} else {
							LOG.info("锁被占用");
						}
					} else if (status.equals("S")) {
						CreditUserAccount creditAccount = creditUserAccountService.get(userRecharge.getAccountId());
						if (status.equals("S")) {
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
							LOG.info("[借款用户]" + userRecharge.getUserId() + "充值" + map.get("amount"));
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
							cgbUserTransDetailService.insert(userTransDetail);
						}
						// N5.接收通知成功，通知对方服务器不在发送通知
						result.put("respCode", "00");
						result.put("respMsg", "成功");
						String jsonString = JSON.toJSONString(result);
						// 对返回对方服务器消息进行加密
						result = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
						// 返回参数对方服务器，不在发送请求
						return result;
					} else if (status.equals("F")) {
						if (status.equals("F")) {
							userRecharge.setState(UserRecharge.RECHARGE_FAIL);
						}
						if (userBankCard != null) {
							userRecharge.setBankAccount(userBankCard.getBankAccountNo());
							// userRecharge.setBank(userBankCard.getBankNo());
						}
						userRecharge.setUpdateDate(new Date());
						int updateState = userRechargeService.updateState(userRecharge);
						// N5.接收通知成功，通知对方服务器不在发送通知
						result.put("respCode", "00");
						result.put("respMsg", "成功");
						String jsonString = JSON.toJSONString(result);
						// 对返回对方服务器消息进行加密
						result = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
						// 返回参数对方服务器，不在发送请求
						return result;
					}
				} else {
					LOG.info("未查询到订单号为" + orderId + "的充值记录");
				}

			} else {
				System.out.println("333333333333333");
				return result;

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 大额充值回调接口
	 * 
	 * @param tm
	 * @param data
	 * @return
	 * @throws IOException
	 */
	@POST
	@Path("/largeRechargeWebNotify")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, String> largeRechargeWebNotify(@FormParam("tm") String tm, @FormParam("data") String data) throws IOException {

		String jsonRet;
		Map<String, String> result = new HashMap<String, String>();

		try {
			// 对通知数据进行解密
			jsonRet = APIUtils.decryptDataBySSL(tm, data, merchantRsaPrivateKey);
			Map<String, String> map = JSON.parseObject(jsonRet, new TypeReference<Map<String, String>>() {
			});
			String signRet = (String) map.get("signature");
			map.remove("signature");

			// 校验验密
			boolean verifyRet = APIUtils.verify(merchantRsaPublicKey, signRet, map, "RSA");
			// 验密成功，进行业务处理
			if (verifyRet) {
				String orderId = (String) map.get("orderId");
				String status = (String) map.get("status");
				System.out.println("充值回调订单号" + orderId);
				// N1.用户更新充值表状态
				UserRecharge userRecharge = userRechargeService.getById(orderId);
				// 用户银行卡相关信息
				LOG.info("充值用户ID为" + userRecharge.getUserId());
				LOG.info("根据用户ID查询银行卡信息开始");
				CgbUserBankCard userBankCard = cgbUserBankCardService.findByUserId(userRecharge.getUserId());
				LOG.info("根据用户ID查询银行卡信息结束");
				if (userRecharge != null) {
					LOG.info("订单状态为" + status);
					// N2.用户账户增加充值金额
					Double rechargeAmount = Double.valueOf(map.get("amount")) / 100;
					LOG.info("充值金额" + rechargeAmount);
					CgbUserAccount userAccountInfo = cgbUserAccountService.get(userRecharge.getAccountId());

					if (userAccountInfo != null && status.equals("S")) {
						String accKey = "ACC" + userAccountInfo.getId();
						String lockAccValue = JedisUtils.lockWithTimeout(accKey, 10000, 2000);// 账户锁

						LOG.info("===============加锁开始====" + System.currentTimeMillis());
						LOG.info("===========钥匙==========" + lockAccValue);
						if (lockAccValue != null) {
							if (status.equals("S")) {
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
								// N5.接收通知成功，通知对方服务器不在发送通知
								result.put("respCode", "00");
								result.put("respMsg", "成功");
								String jsonString = JSON.toJSONString(result);
								// 对返回对方服务器消息进行加密
								result = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
								// 返回参数对方服务器，不在发送请求
								return result;
							}
						} else {
							LOG.info("锁被占用");
						}
					}
					// else if(status.equals("S")){
					// CreditUserAccount creditAccount = creditUserAccountService.get(userRecharge.getAccountId());
					// if(status.equals("S")){
					// userRecharge.setState(UserRecharge.RECHARGE_SUCCESS);
					// }
					// if(userBankCard!=null){
					// userRecharge.setBankAccount(userBankCard.getBankAccountNo());
					// }
					// userRecharge.setUpdateDate(new Date());
					// int updateState = userRechargeService.updateState(userRecharge);
					// if(updateState>0){
					// LOG.info("充值订单号为"+orderId+"状态更新成功");
					// }
					// if(creditAccount!=null && userRecharge.getState()==UserRecharge.RECHARGE_SUCCESS){
					// LOG.info("[借款用户]"+userRecharge.getUserId()+"充值"+map.get("amount"));
					// creditAccount.setTotalAmount(creditAccount.getTotalAmount()+rechargeAmount);
					// creditAccount.setAvailableAmount(creditAccount.getAvailableAmount()+rechargeAmount);
					// creditAccount.setRechargeAmount(creditAccount.getRechargeAmount()+rechargeAmount);
					// creditUserAccountDao.update(creditAccount);
					// //资金流水表更新状态
					// CgbUserTransDetail userTransDetail = new CgbUserTransDetail();
					// userTransDetail.setUserId(userRecharge.getUserId());
					// userTransDetail.setAmount(rechargeAmount);
					// userTransDetail.setAccountId(userRecharge.getAccountId());
					// userTransDetail.setAvaliableAmount(creditAccount.getAvailableAmount());
					// userTransDetail.setTransDate(new Date());
					// userTransDetail.setBeginTransDate(new Date());
					// userTransDetail.setInOutType(UserTransDetail.TRANS_TYPE_IN);
					// userTransDetail.setRemarks("借款用户充值");
					// userTransDetail.setState(UserTransDetail.TRANS_STATE_SUCCESS);
					// userTransDetail.setTrustType(UserTransDetail.TRANS_RECHARGE);
					// userTransDetail.setId(IdGen.uuid());
					// userTransDetail.setTransId(userRecharge.getId());
					// cgbUserTransDetailService.insert(userTransDetail);
					// }
					// //N5.接收通知成功，通知对方服务器不在发送通知
					// result.put("respCode","00");
					// result.put("respMsg","成功");
					// String jsonString =JSON.toJSONString(result);
					// //对返回对方服务器消息进行加密
					// result =APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
					// //返回参数对方服务器，不在发送请求
					// return result;
					// }
					else if (status.equals("F")) {
						if (status.equals("F")) {
							userRecharge.setState(UserRecharge.RECHARGE_FAIL);
						}
						if (userBankCard != null) {
							userRecharge.setBankAccount(userBankCard.getBankAccountNo());
							// userRecharge.setBank(userBankCard.getBankNo());
						}
						userRecharge.setUpdateDate(new Date());
						int updateState = userRechargeService.updateState(userRecharge);
						// N5.接收通知成功，通知对方服务器不在发送通知
						result.put("respCode", "00");
						result.put("respMsg", "成功");
						String jsonString = JSON.toJSONString(result);
						// 对返回对方服务器消息进行加密
						result = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
						// 返回参数对方服务器，不在发送请求
						return result;
					}
				} else {
					LOG.info("未查询到订单号为" + orderId + "的充值记录");
				}

			} else {
				System.out.println("验密失败");
				return result;

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

}
