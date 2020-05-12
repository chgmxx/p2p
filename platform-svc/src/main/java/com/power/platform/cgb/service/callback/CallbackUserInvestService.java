package com.power.platform.cgb.service.callback;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import com.power.platform.activity.entity.AUserAwardsHistory;
import com.power.platform.activity.service.AUserAwardsHistoryService;
import com.power.platform.bouns.dao.UserBounsHistoryDao;
import com.power.platform.bouns.dao.UserBounsPointDao;
import com.power.platform.bouns.entity.UserBounsHistory;
import com.power.platform.bouns.entity.UserBounsPoint;
import com.power.platform.bouns.services.UserBounsHistoryService;
import com.power.platform.bouns.services.UserBounsPointService;
import com.power.platform.cgb.dao.CgbUserAccountDao;
import com.power.platform.cgb.entity.CgbUserAccount;
import com.power.platform.cgb.entity.CgbUserTransDetail;
import com.power.platform.cgb.service.CgbUserAccountService;
import com.power.platform.cgb.service.CgbUserTransDetailService;
import com.power.platform.common.config.Global;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.JedisUtils;
import com.power.platform.more.stationletter.entity.StationLetter;
import com.power.platform.more.stationletter.service.StationLettersService;
import com.power.platform.plan.service.WloanTermUserPlanService;
import com.power.platform.regular.dao.WloanTermProjectDao;
import com.power.platform.regular.entity.WloanTermInvest;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.regular.service.NewInvestService;
import com.power.platform.regular.service.UserInvestWebService;
import com.power.platform.regular.service.WloanTermInvestService;
import com.power.platform.regular.service.WloanTermProjectService;
import com.power.platform.trandetail.service.UserTransDetailService;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.userinfo.service.UserAccountInfoService;
import com.power.platform.userinfo.service.UserInfoService;
import com.power.platform.weixin.service.WeixinSendTempMsgService;

/**
 * 投资回调
 * 
 * @author YHAGZALUN WO SJIAOSY
 *
 */
@Component
@Path("/callbackinvest")
@Service("callbackUserInvestService")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class CallbackUserInvestService {

	private static final Logger LOG = LoggerFactory.getLogger(CallbackUserInvestService.class);

	// 存管系统为你分配的，用来对AES私钥加密，同时生成签名
	private static final String merchantRsaPublicKey = Global.getConfig("merchantRsaPublicKey");

	// 商户自己的RSA私钥
	private static final String merchantRsaPrivateKey = Global.getConfig("merchantRsaPrivateKey");

	@Autowired
	private WloanTermInvestService wloanTermInvestService;
	@Autowired
	private UserAccountInfoService userAccountInfoService;
	@Autowired
	private UserInfoService userInfoService;
	@Autowired
	private CgbUserAccountService cgbUserAccountService;
	@Autowired
	private CgbUserTransDetailService cgbUserTransDetailService;
	@Autowired
	private UserBounsPointService userBounsPointService;
	@Autowired
	private UserBounsHistoryService userBounsHistoryService;
	@Autowired
	private CgbUserAccountDao cgbUserAccountDao;
	@Autowired
	private WeixinSendTempMsgService weixinSendTempMsgService;
	@Autowired
	private WloanTermProjectService wloanTermProjectService;
	@Autowired
	private AUserAwardsHistoryService aUserAwardsHistoryService;
	@Autowired
	private WloanTermProjectDao wloanTermProjectDao;
	@Autowired
	private WloanTermUserPlanService wloanTermUserPlanService;
	@Autowired
	private UserBounsPointDao userBounsPointDao;
	@Autowired
	private StationLettersService stationLettersService;
	@Autowired
	private UserBounsHistoryDao userBounsHistoryDao;
	@Autowired
	private UserInvestWebService userInvestWebService;

	/**
	 * 投资回调接口
	 * 
	 * @param tm
	 * @param data
	 * @return
	 * @throws IOException
	 */
	@POST
	@Path("/investCreateWebNotify")
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized Map<String, String> notify(@FormParam("tm") String tm, @FormParam("data") String data) throws IOException {

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
				LOG.info("投标订单号为" + orderId);
				LOG.info("订单状态为" + status);
				WloanTermInvest wloanTermInvest = wloanTermInvestService.get(orderId);
				if (wloanTermInvest != null) {
					LOG.info("查询出借结果Id为" + wloanTermInvest.getId());
					Double interest = wloanTermInvest.getInterest();
					Double vouAmount = wloanTermInvest.getVoucherAmount() == null ? 0d : wloanTermInvest.getVoucherAmount();
					Double amount = wloanTermInvest.getAmount();
					LOG.info("出借金额为" + amount + "=====抵用券金额为" + vouAmount);
					WloanTermProject project = wloanTermProjectService.get(wloanTermInvest.getWloanTermProject().getId());
					// 对投标记录进行状态变更
					if (status.equals("S")) {
						// 投标成功
						wloanTermInvest.setState(NewInvestService.WLOAN_TERM_INVEST_STATE_1);
						int i = wloanTermInvestService.updateWloanTermInvest(wloanTermInvest);
						if (i > 0) {
							LOG.info("[用户投标成功]" + (String) map.get("orderId") + "状态更新成功");
							CgbUserAccount userAccount = cgbUserAccountService.get(wloanTermInvest.getUserInfo().getAccountId());

							/**
							 * 更改账户信息
							 * 1、账户总额（有抵用券加上抵用券金额）
							 * 2、可用金额（可用金额 - 投资金额）4900
							 * 3、定期代收本金（ + 投资金额）5000
							 * 4、定期代收收益（ + 投资利息）
							 * 5、定期投资总金额（ + 投资金额）5000
							 * 6、定期累计收益（不动，还款的时候在添加）
							 */
							LOG.info(this.getClass().getName() + "——————更改账户信息开始");
							userAccount.setTotalAmount(userAccount.getTotalAmount() + vouAmount + interest); // 账户总额
							userAccount.setAvailableAmount(userAccount.getAvailableAmount() + vouAmount);// 可用余额
							userAccount.setFreezeAmount(userAccount.getFreezeAmount() - amount); // 冻结余额
							userAccount.setRegularDuePrincipal(userAccount.getRegularDuePrincipal() + amount); // 定期代收本金
							userAccount.setRegularDueInterest(userAccount.getRegularDueInterest() + interest); // 定期代收收益
							userAccount.setRegularTotalAmount(userAccount.getRegularTotalAmount() + amount); // 定期投资总额
							int updateAccount = cgbUserAccountService.updateUserAccountInfo(userAccount);
							if (updateAccount == 1) {
								LOG.info(this.getClass().getName() + "——————更改账户信息成功");
							}

							LOG.info("=====记录客户出借交易流水开始======"); // 保存客户流水记录
							CgbUserTransDetail userTransDetail = new CgbUserTransDetail();
							userTransDetail.setId(IdGen.uuid()); // 主键ID.
							userTransDetail.setTransId(orderId); // 客户投资记录ID.
							userTransDetail.setUserId(wloanTermInvest.getUserInfo().getId()); // 客户账号ID.
							userTransDetail.setAccountId(wloanTermInvest.getUserInfo().getAccountId()); // 客户账户ID.
							userTransDetail.setTransDate(new Date()); // 投资交易时间.
							userTransDetail.setTrustType(UserTransDetailService.trust_type3); // 定期投资.
							userTransDetail.setAmount(amount); // 投资交易金额.
							userTransDetail.setAvaliableAmount(userAccount.getAvailableAmount()); // 当前可用余额.
							userTransDetail.setInOutType(UserTransDetailService.out_type); // 投资支出.
							userTransDetail.setRemarks("出借"); // 备注信息.
							userTransDetail.setState(UserTransDetailService.tran_type2); // 流水状态，成功.
							int userTransDetailFlag = cgbUserTransDetailService.insert(userTransDetail);
							if (userTransDetailFlag == 1) {
								LOG.info(this.getClass().getName() + "——————保存客户出借流水记录成功");
								// 记录抵用券流水
								//AUserAwardsHistory aUserAwardsHistory = aUserAwardsHistoryService.findByBidId(wloanTermInvest.getId());
								AUserAwardsHistory  aUserAwards = new  AUserAwardsHistory();
								aUserAwards.setBidId(wloanTermInvest.getId());
								List<AUserAwardsHistory> aUserAwardsHistoryList = aUserAwardsHistoryService.findVouchers(aUserAwards);
								if(aUserAwardsHistoryList!=null && aUserAwardsHistoryList.size()>0){
									for (AUserAwardsHistory aUserAwardsHistory2 : aUserAwardsHistoryList) {
											// 保存客户使用抵用券流水记录
											LOG.info(this.getClass().getName() + "——————保存客户使用抵用券流水记录开始");
											CgbUserTransDetail userTransDetail1 = new CgbUserTransDetail();
											userTransDetail1.setId(IdGen.uuid()); // 主键ID.
											userTransDetail1.setTransId(aUserAwardsHistory2.getId()); // 客户抵用券记录ID.
											userTransDetail1.setUserId(wloanTermInvest.getUserInfo().getId()); // 客户账号ID.
											userTransDetail1.setAccountId(wloanTermInvest.getUserInfo().getAccountId()); // 客户账户ID.
											userTransDetail1.setTransDate(new Date()); // 抵用券使用时间.
											userTransDetail1.setTrustType(UserTransDetailService.trust_type10); // 优惠券.
											userTransDetail1.setAmount(aUserAwardsHistory2.getaVouchersDic().getAmount()); // 优惠券金额.
											userTransDetail1.setAvaliableAmount(userAccount.getAvailableAmount()); // 当前可用余额.
											userTransDetail1.setInOutType(UserTransDetailService.in_type); // 优惠券收入.
											userTransDetail1.setRemarks("抵用券"); // 备注信息.
											userTransDetail1.setState(UserTransDetailService.tran_type2); // 流水状态，成功.
											int userVoucherDetailFlag = cgbUserTransDetailService.insert(userTransDetail1);
											if (userVoucherDetailFlag == 1) {
												LOG.info(this.getClass().getName() + "——————保存客户使用抵用券流水记录成功");
											}
									}
								}
							}
							
							
							// 发送站内信
							StationLetter letter = new StationLetter();
							letter.setUserId(wloanTermInvest.getUserInfo().getId());
							letter.setLetterType(StationLettersService.LETTER_TYPE_WLOAN);
							letter.setTitle("您已成功向" + project.getName() + "项目出借资金:" + amount + ",借款周期:" + project.getSpan() + "天。");
							letter.setBody("感谢您对平台一如既往的支持！市场有风险，出借需谨慎");
							letter.setState(StationLettersService.LETTER_STATE_UNREAD);
							letter.setSendTime(new Date());
							stationLettersService.save(letter);
							

							weixinSendTempMsgService.sendInvestInfoMsg(wloanTermInvest, project);
							// 接收通知成功，通知对方服务器不在发送通知
							result.put("respCode", "00");
							result.put("respMsg", "成功");
							String jsonString = JSON.toJSONString(result);
							// 对返回对方服务器消息进行加密
							result = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);

							// 返回参数对方服务器，不在发送请求
							return result;
						}
					} else if (status.equals("F")) {
						// 投标失败
						wloanTermInvest.setState(NewInvestService.WLOAN_TERM_INVEST_STATE_2);
						int i = wloanTermInvestService.updateWloanTermInvest(wloanTermInvest);
						if (i > 0) {
							LOG.info("[用户投标失败]" + (String) map.get("orderId") + "状态更新成功");
						}
						// 账户退回投资金额
						LOG.info(this.getClass().getName() + "——————更改账户信息退回投资金额开始");
						UserInfo userInfo = userInfoService.getCgb(wloanTermInvest.getUserId());
						CgbUserAccount userAccount = cgbUserAccountService.get(userInfo.getAccountId());
						
						userAccount.setAvailableAmount(userAccount.getAvailableAmount() + amount); // 可用余额
						userAccount.setFreezeAmount(userAccount.getFreezeAmount() - amount); // 冻结余额
						
						int updateAccount = cgbUserAccountService.updateUserAccountInfo(userAccount);
						if (updateAccount == 1) {
							LOG.info(this.getClass().getName() + "——————更改账户信息退回投资金额成功");
							
							WloanTermProject wloanTermProject = wloanTermProjectService.get(wloanTermInvest.getWloanTermProject().getId());
							if(wloanTermProject!=null){
								//更改项目融资金额
								wloanTermProject.setCurrentAmount(wloanTermProject.getCurrentAmount()-amount);
								wloanTermProject.setCurrentRealAmount(wloanTermProject.getCurrentRealAmount()-amount);
								int updateProject = wloanTermProjectDao.update(wloanTermProject);
								if(updateProject > 0){
									LOG.info(this.getClass().getName() + "——————更改项目融资金额成功");
								}
								//删除投资生成的个人还款计划
								int deleteUserPlan = wloanTermUserPlanService.deleteByWloanTermInvestId(wloanTermInvest.getId());
								if(deleteUserPlan>0){
									LOG.info(this.getClass().getName() + "——————删除客户还款计划成功");
								}
							}
							//用户所得积分减去
							UserBounsHistory  userBounsHistory = new UserBounsHistory();
							userBounsHistory.setTransId(wloanTermInvest.getId());
							List<UserBounsHistory> userBouns = userBounsHistoryService.findList(userBounsHistory);
							if(userBouns!=null){
								for (UserBounsHistory userBounsHistory2 : userBouns) {
									 Double point = userBounsHistory2.getAmount();
									 UserBounsPoint userPoint = userBounsPointService.getUserBounsPoint(wloanTermInvest.getUserId());
									 if(userPoint!=null){
										 userPoint.setScore((int) (userPoint.getScore() - point));
										 int u = userBounsPointDao.update(userPoint);
										 if( u>0 ){
											 LOG.info("积分消减");
											 userBounsHistoryDao.delete(userBounsHistory2.getId());
										 }
									 }
								}
							}
							
							// 接收通知成功，通知对方服务器不在发送通知
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
					LOG.info("未查询订单号为" + orderId + "的出借信息");
				}
			} else {
				return result;

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 出借回调接口
	 * 
	 * @param tm
	 * @param data
	 * @return
	 * @throws IOException
	 */
	@POST
	@Path("/userInvestCreateWebNotify")
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized Map<String, String> investNotify(@FormParam("tm") String tm, @FormParam("data") String data) throws IOException {

		String jsonRet;
		Map<String, String> result = new HashMap<String, String>();
		String ordKey = "";
		String lockOrdValue = "";//订单锁
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
				LOG.info("出借订单号为" + orderId);
				LOG.info("订单状态为" + status);
				WloanTermInvest wloanTermInvest = wloanTermInvestService.get(orderId);
				if (wloanTermInvest != null) {
					//N1.出借订单状态变更
					if(status.equals("S")){
						//出借成功
						
						//订单加锁
						ordKey = "ORD"+orderId;
						lockOrdValue = JedisUtils.lockWithTimeout(ordKey, 10000, 3000);//订单锁
						if(lockOrdValue!=null){
//							//先查询此笔订单的状态
							WloanTermInvest resultInvest = wloanTermInvestService.get(orderId);
							if(resultInvest.getState()!=null){
								if(resultInvest.getState().equals(WloanTermInvestService.WLOAN_TERM_INVEST_STATE_1) ||
										resultInvest.getState().equals(WloanTermInvestService.WLOAN_TERM_INVEST_STATE_2)){
									LOG.info("=====此笔订单已处理完成=====");
									// 接收通知成功，通知对方服务器不在发送通知
									result.put("respCode", "00");
									result.put("respMsg", "成功");
									String jsonString = JSON.toJSONString(result);
									// 对返回对方服务器消息进行加密
									result = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
									// 返回参数对方服务器，不在发送请求
									return result;
								}
							}
							LOG.info("===========出借成功业务处理开始=========");
							int resultVal = userInvestWebService.createUserInvest(orderId);
							LOG.info("===========出借成功业务处理结束=======结果="+resultVal);
							if(resultVal>0){
								//释放订单锁
								if(JedisUtils.releaseLock(ordKey, lockOrdValue)){
									LOG.info("订单锁已释放");
									LOG.info("订单锁已解锁====="+System.currentTimeMillis());
								}
								// 接收通知成功，通知对方服务器不在发送通知
								result.put("respCode", "00");
								result.put("respMsg", "成功");
								String jsonString = JSON.toJSONString(result);
								// 对返回对方服务器消息进行加密
								result = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
								// 返回参数对方服务器，不在发送请求
								return result;
							}
						}
					}else if(status.equals("F")){
						//出借失败
						wloanTermInvest.setState(NewInvestService.WLOAN_TERM_INVEST_STATE_2);
						wloanTermInvest.setRemarks("出借申请失败");
						int i = wloanTermInvestService.updateWloanTermInvest(wloanTermInvest);
						if (i > 0) {
							LOG.info("[用户出借失败]" + (String) map.get("orderId") + "状态更新成功");
						}
						// 接收通知成功，通知对方服务器不在发送通知
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
				return result;

			}
		} catch (Exception e) {
			//释放订单锁
			if(JedisUtils.releaseLock(ordKey, lockOrdValue)){
				LOG.info("订单锁已释放");
				LOG.info("订单锁已解锁====="+System.currentTimeMillis());
			}
			e.printStackTrace();
		}
		return result;
	}

	
	/**
	 * 出借回调接口---新
	 * 
	 * @param tm
	 * @param data
	 * @return
	 * @throws IOException
	 */
	@POST
	@Path("/newUserInvestCreateWebNotify")
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized Map<String, String> newUserInvestCreateWebNotify(@FormParam("tm") String tm, @FormParam("data") String data) throws IOException {

		String jsonRet;
		Map<String, String> result = new HashMap<String, String>();
		String ordKey = "";
		String lockOrdValue = "";//订单锁
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
				LOG.info("出借订单号为" + orderId);
				LOG.info("订单状态为" + status);
				WloanTermInvest wloanTermInvest = wloanTermInvestService.get(orderId);
				if (wloanTermInvest != null) {
					//N1.出借订单状态变更
					if(status.equals("S")){
						//出借成功
						
						//订单加锁
						ordKey = "ORD"+orderId;
						lockOrdValue = JedisUtils.lockWithTimeout(ordKey, 10000, 3000);//订单锁
						if(lockOrdValue!=null){
//							//先查询此笔订单的状态
							WloanTermInvest resultInvest = wloanTermInvestService.get(orderId);
							if(resultInvest.getState()!=null){
								if(resultInvest.getState().equals(WloanTermInvestService.WLOAN_TERM_INVEST_STATE_1) ||
										resultInvest.getState().equals(WloanTermInvestService.WLOAN_TERM_INVEST_STATE_2)){
									LOG.info("=====此笔订单已处理完成=====");
									// 接收通知成功，通知对方服务器不在发送通知
									result.put("respCode", "00");
									result.put("respMsg", "成功");
									String jsonString = JSON.toJSONString(result);
									// 对返回对方服务器消息进行加密
									result = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
									// 返回参数对方服务器，不在发送请求
									return result;
								}
							}
							LOG.info("===========出借成功业务处理开始=========");
							int resultVal = userInvestWebService.newCreateUserInvest(orderId);
							LOG.info("===========出借成功业务处理结束=======结果="+resultVal);
							if(resultVal>0){
								//释放订单锁
								if(JedisUtils.releaseLock(ordKey, lockOrdValue)){
									LOG.info("订单锁已释放");
									LOG.info("订单锁已解锁====="+System.currentTimeMillis());
								}
								// 接收通知成功，通知对方服务器不在发送通知
								result.put("respCode", "00");
								result.put("respMsg", "成功");
								String jsonString = JSON.toJSONString(result);
								// 对返回对方服务器消息进行加密
								result = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
								// 返回参数对方服务器，不在发送请求
								return result;
							}
						}
					}else if(status.equals("F")){
						//出借失败
						wloanTermInvest.setState(NewInvestService.WLOAN_TERM_INVEST_STATE_2);
						wloanTermInvest.setRemarks("出借申请失败");
						int i = wloanTermInvestService.updateWloanTermInvest(wloanTermInvest);
						if (i > 0) {
							LOG.info("[用户出借失败]" + (String) map.get("orderId") + "状态更新成功");
						}
						// 接收通知成功，通知对方服务器不在发送通知
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
				return result;

			}
		} catch (Exception e) {
			//释放订单锁
			if(JedisUtils.releaseLock(ordKey, lockOrdValue)){
				LOG.info("订单锁已释放");
				LOG.info("订单锁已解锁====="+System.currentTimeMillis());
			}
			e.printStackTrace();
		}
		return result;
	}
	
	
	/**
	 * 出借回调接口---2_2_1版本
	 * 
	 * @param tm
	 * @param data
	 * @return
	 * @throws IOException
	 */
	@POST
	@Path("/newUserInvestCreateWebNotify2_2_1")
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized Map<String, String> newUserInvestCreateWebNotify2_2_1(@FormParam("tm") String tm, @FormParam("data") String data) throws IOException {

		String jsonRet;
		Map<String, String> result = new HashMap<String, String>();
		String ordKey = "";
		String lockOrdValue = "";//订单锁
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
				LOG.info("出借订单号为" + orderId);
				LOG.info("订单状态为" + status);
				WloanTermInvest wloanTermInvest = wloanTermInvestService.get(orderId);
				if (wloanTermInvest != null) {
					//N1.出借订单状态变更
					if(status.equals("S")){
						//出借成功
						
						//订单加锁
						ordKey = "ORD"+orderId;
						lockOrdValue = JedisUtils.lockWithTimeout(ordKey, 10000, 3000);//订单锁
						if(lockOrdValue!=null){
//							//先查询此笔订单的状态
							WloanTermInvest resultInvest = wloanTermInvestService.get(orderId);
							if(resultInvest.getState()!=null){
								if(resultInvest.getState().equals(WloanTermInvestService.WLOAN_TERM_INVEST_STATE_1) ||
										resultInvest.getState().equals(WloanTermInvestService.WLOAN_TERM_INVEST_STATE_2)){
									LOG.info("=====此笔订单已处理完成=====");
									// 接收通知成功，通知对方服务器不在发送通知
									result.put("respCode", "00");
									result.put("respMsg", "成功");
									String jsonString = JSON.toJSONString(result);
									// 对返回对方服务器消息进行加密
									result = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
									// 返回参数对方服务器，不在发送请求
									return result;
								}
							}
							LOG.info("===========出借成功业务处理开始=========");
							int resultVal = userInvestWebService.newCreateUserInvest2_2_1(orderId);
							LOG.info("===========出借成功业务处理结束=======结果="+resultVal);
							if(resultVal>0){
								//释放订单锁
								if(JedisUtils.releaseLock(ordKey, lockOrdValue)){
									LOG.info("订单锁已释放");
									LOG.info("订单锁已解锁====="+System.currentTimeMillis());
								}
								// 接收通知成功，通知对方服务器不在发送通知
								result.put("respCode", "00");
								result.put("respMsg", "成功");
								String jsonString = JSON.toJSONString(result);
								// 对返回对方服务器消息进行加密
								result = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
								// 返回参数对方服务器，不在发送请求
								return result;
							}
						}
					}else if(status.equals("F")){
						//出借失败
						wloanTermInvest.setState(NewInvestService.WLOAN_TERM_INVEST_STATE_2);
						wloanTermInvest.setRemarks("出借申请失败");
						int i = wloanTermInvestService.updateWloanTermInvest(wloanTermInvest);
						if (i > 0) {
							LOG.info("[用户出借失败]" + (String) map.get("orderId") + "状态更新成功");
						}
						// 接收通知成功，通知对方服务器不在发送通知
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
				return result;

			}
		} catch (Exception e) {
			//释放订单锁
			if(JedisUtils.releaseLock(ordKey, lockOrdValue)){
				LOG.info("订单锁已释放");
				LOG.info("订单锁已解锁====="+System.currentTimeMillis());
			}
			e.printStackTrace();
		}
		return result;
	}
}
