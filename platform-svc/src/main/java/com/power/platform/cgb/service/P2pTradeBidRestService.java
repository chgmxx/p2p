package com.power.platform.cgb.service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.cunguanbao.api.utils.APIUtils;
import com.power.platform.activity.dao.AVouchersDicDao;
import com.power.platform.activity.dao.UserVouchersHistoryDao;
import com.power.platform.activity.entity.AVouchersDic;
import com.power.platform.activity.entity.UserVouchersHistory;
import com.power.platform.activity.service.AUserAwardsHistoryService;
import com.power.platform.cgb.dao.CgbUserAccountDao;
import com.power.platform.cgb.dao.CgbUserTransDetailDao;
import com.power.platform.cgb.dao.ZtmgOrderInfoDao;
import com.power.platform.cgb.entity.CgbUserAccount;
import com.power.platform.cgb.entity.CgbUserTransDetail;
import com.power.platform.cgb.entity.ZtmgOrderInfo;
import com.power.platform.common.config.Global;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.JedisUtils;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.credit.dao.apply.CreditUserApplyDao;
import com.power.platform.credit.dao.userinfo.CreditUserAccountDao;
import com.power.platform.credit.dao.userinfo.CreditUserInfoDao;
import com.power.platform.credit.entity.userinfo.CreditUserAccount;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.service.apply.CreditUserApplyService;
import com.power.platform.plan.dao.WloanTermProjectPlanDao;
import com.power.platform.plan.dao.WloanTermUserPlanDao;
import com.power.platform.plan.entity.WloanTermProjectPlan;
import com.power.platform.plan.entity.WloanTermUserPlan;
import com.power.platform.plan.service.WloanTermProjectPlanService;
import com.power.platform.plan.service.WloanTermUserPlanService;
import com.power.platform.regular.dao.WloanTermInvestDao;
import com.power.platform.regular.entity.WloanSubject;
import com.power.platform.regular.entity.WloanTermInvest;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.regular.service.WloanSubjectService;
import com.power.platform.regular.service.WloanTermInvestService;
import com.power.platform.regular.service.WloanTermProjectService;
import com.power.platform.trandetail.dao.UserTransDetailDao;
import com.power.platform.userinfo.dao.UserAccountInfoDao;
import com.power.platform.userinfo.dao.UserInfoDao;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.weixin.service.WeixinSendTempMsgService;

/**
 * 
 * 类: P2pTradeBidRestService <br>
 * 描述: 网贷资金存管，交易类异步通知地址服务. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2017年5月22日 上午11:31:32
 */
@Path("/p2p/trade/bid")
@Service("p2pTradeBidRestService")
@Produces(MediaType.APPLICATION_JSON)
public class P2pTradeBidRestService {

	private static final Logger log = LoggerFactory.getLogger(P2pTradeBidRestService.class);

	/**
	 * 商户自己的RSA公钥.
	 */
	private static final String MERCHANT_RSA_PUBLIC_KEY = Global.getConfig("merchantRsaPublicKey");
	/**
	 * 商户自己的RSA私钥.
	 */
	private static final String MERCHANT_RSA_PRIVATE_KEY = Global.getConfig("merchantRsaPrivateKey");

	@Autowired
	private WloanTermProjectService wloanTermProjectService;
	@Autowired
	private WloanTermProjectPlanService wloanTermProjectPlanService;
	@Autowired
	private WloanTermUserPlanService wloanTermUserPlanService;
	@Autowired
	private WloanSubjectService wloanSubjectService;
	@Autowired
	private CreditUserApplyService creditUserApplyService;
	@Resource
	private CreditUserApplyDao creditUserApplyDao;
	@Resource
	private WloanTermInvestDao wloanTermInvestDao;
	@Resource
	private UserTransDetailDao userTransDetailDao;
	@Resource
	private UserAccountInfoDao userAccountInfoDao;
	@Resource
	private UserInfoDao userInfoDao;
	@Resource
	private WloanTermProjectPlanDao wloanTermProjectPlanDao;
	@Resource
	private WloanTermUserPlanDao wloanTermUserPlanDao;
	@Resource
	private CreditUserInfoDao creditUserInfoDao;
	@Resource
	private CreditUserAccountDao creditUserAccountDao;
	@Resource
	private CgbUserTransDetailDao cgbUserTransDetailDao;
	@Resource
	private CgbUserAccountDao cgbUserAccountDao;
	@Autowired
	private WeixinSendTempMsgService weixinSendTempMsgService;
	@Resource
	private ZtmgOrderInfoDao ztmgOrderInfoDao;
	@Resource
	private UserVouchersHistoryDao userVouchersHistoryDao;
	@Autowired
	private AVouchersDicDao aVouchersDicDao;

	/**
	 * 
	 * 方法: callbackCancel <br>
	 * 描述: 流标，异步通知地址. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年7月31日 上午9:02:07
	 * 
	 * @param tm
	 * @param data
	 * @return
	 * @throws IOException
	 */
	@POST
	@Path("/callbackCancel")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> callbackCancel(@FormParam("tm") String tm, @FormParam("data") String data) throws IOException {

		String jsonRet;
		// 最后一期还款日期.
		Map<String, String> result = new HashMap<String, String>();
		try {
			// 对通知数据进行解密.
			jsonRet = APIUtils.decryptDataBySSL(tm, data, MERCHANT_RSA_PRIVATE_KEY);
			Map<String, String> map = JSON.parseObject(jsonRet, new TypeReference<Map<String, String>>() {
			});
			// merchantId(由存管银行分配给网贷平台的唯一的商户编码).
			String merchantId = (String) map.get("merchantId");
			log.info("fn:callbackCancel-merchantId(商户ID) = " + merchantId);
			// orderId(由网贷平台生成的唯一的交易流水号)，如果没有生成，返回标的ID.
			String orderId = (String) map.get("orderId");
			log.info("fn:callbackCancel-orderId(标的ID) = " + orderId);
			// 备注.
			String remark = (String) map.get("remark");
			log.info("fn:callbackCancel-remark(备注) = " + remark);
			// 签名，附加说明.
			String signature = (String) map.get("signature");
			log.info("fn:callbackCancel-signature(签名) = " + signature);
			map.remove("signature");

			// 校验验密.
			boolean verifyRet = APIUtils.verify(MERCHANT_RSA_PUBLIC_KEY, signature, map, "RSA");
			// 验密成功，进行业务处理.
			if (verifyRet) {
				log.info("fn:callbackCancel-验密成功，进行业务处理.");
				// S = 成功，F = 失败.
				String status = (String) map.get("status");
				if (status.equals("S")) { // 成功.
					/**
					 * 查询标的是否更改状态为：流标.
					 */
					WloanTermProject project = wloanTermProjectService.get(orderId);
					if (project != null) {
						log.info("fn:callbackCancel-标的流转状态-" + project.getState());
						// 如果标的状态更新为流标，则不做逻辑处理，响应存管方不进行再次通知.
						if (WloanTermProjectService.P2P_TRADE_BID_CANCEL.equals(project.getState())) {
							// 响应对方服务器不在发送通知.
							result.put("respCode", "00");
							result.put("respMsg", "成功");
							String jsonString = JSON.toJSONString(result);
							// 对返回对方服务器消息进行加密.
							result = APIUtils.encryptDataBySSL(jsonString, MERCHANT_RSA_PUBLIC_KEY);
							return result;
						} else {
							/**
							 * 当前标的的所有出借记录.
							 */
							WloanTermInvest wloanTermInvest = new WloanTermInvest();
							wloanTermInvest.setState(WloanTermInvestService.WLOAN_TERM_INVEST_STATE_1); // 出借状态，1：出借成功.
							WloanTermProject wloanTermProject = new WloanTermProject();
							wloanTermProject.setId(orderId); // 标的ID.
							wloanTermInvest.setWloanTermProject(wloanTermProject);
							List<WloanTermInvest> investList = wloanTermInvestDao.findList(wloanTermInvest);
							int timeout = 1000 * investList.size();
							log.info("fn:callbackCancel-订单锁超时时间-" + timeout);
							/**
							 * 订单加锁.
							 */
							String ordKey = "ORD" + orderId;
							String ordRetIdentifier = JedisUtils.lockWithTimeout(ordKey, 1000, timeout);
							log.info("fn:callbackCancel-订单锁身份牌-" + ordRetIdentifier);
							if (ordRetIdentifier != null) {
								log.info("fn:callbackCancel-订单加锁成功.");
								/**
								 * 查询标的是否更改状态为：流标.
								 */
								WloanTermProject newProject = wloanTermProjectService.get(orderId);
								// 如果标的状态更新为流标，则不做逻辑处理，响应存管方不进行再次通知.
								if (WloanTermProjectService.P2P_TRADE_BID_CANCEL.equals(newProject.getState())) {
									// 响应对方服务器不在发送通知.
									result.put("respCode", "00");
									result.put("respMsg", "成功");
									String jsonString = JSON.toJSONString(result);
									// 对返回对方服务器消息进行加密.
									result = APIUtils.encryptDataBySSL(jsonString, MERCHANT_RSA_PUBLIC_KEY);
									return result;
								}
								/**
								 * 标的信息-更新为流标.
								 */
								project.setState(WloanTermProjectService.P2P_TRADE_BID_CANCEL);
								project.setUpdateDate(new Date());
								wloanTermProjectService.updateProState(project);
								log.info("fn:callbackCancel-标的状态-流标.");
								// a变量-区分时间.
								int a = 0;
								for (WloanTermInvest invest : investList) { // 遍历当前标的的出借记录.
									UserInfo userInfo = invest.getUserInfo();
									if (null != userInfo) { // 出借人帐号.
										CgbUserAccount userAccountInfo = cgbUserAccountDao.getUserAccountInfo(userInfo.getId());
										if (null != userAccountInfo) { // 出借人账户.
											/**
											 * 账户加锁.
											 */
											String accKey = "ACC" + userAccountInfo.getId();
											String accRetIdentifier = JedisUtils.lockWithTimeout(accKey, 4000, 4000);
											if (accRetIdentifier != null) {
												log.info("fn:callbackCancel-账户加锁成功.");
												/**
												 * 账户变更，计入流水-出借.
												 */
												// 出借金额.
												Double investAmount = invest.getAmount();
												// 出借总收益.
												Double investInterest = invest.getInterest();
												// 可用余额(+).
												Double availableAmount = userAccountInfo.getAvailableAmount();
												availableAmount = NumberUtils.scaleDouble(availableAmount + investAmount);
												userAccountInfo.setAvailableAmount(availableAmount);
												// 待收本金(-).
												Double regularDuePrincipal = userAccountInfo.getRegularDuePrincipal();
												regularDuePrincipal = NumberUtils.scaleDouble(regularDuePrincipal - investAmount);
												userAccountInfo.setRegularDuePrincipal(regularDuePrincipal);
												// 待收收益(-).
												Double regularDueInterest = userAccountInfo.getRegularDueInterest();
												regularDueInterest = NumberUtils.scaleDouble(regularDueInterest - investInterest);
												userAccountInfo.setRegularDueInterest(regularDueInterest);
												// 累计出借(-).
												Double regularTotalAmount = userAccountInfo.getRegularTotalAmount();
												regularTotalAmount = NumberUtils.scaleDouble(regularTotalAmount - investAmount);
												userAccountInfo.setRegularTotalAmount(regularTotalAmount);
												// 账户总额(-)，只用减掉出借的总收益即可，因为出借金额流标后，待收本金减，可用余额加，一减一加等于不变.
												Double totalAmount = userAccountInfo.getTotalAmount();
												totalAmount = NumberUtils.scaleDouble(totalAmount - investInterest);
												userAccountInfo.setTotalAmount(totalAmount);
												int investUserAccountInfoFlag = cgbUserAccountDao.update(userAccountInfo);
												if (investUserAccountInfoFlag == 1) {
													log.info("fn:callbackCancel-出借人账户更新成功.");
													/**
													 * 计入流水.
													 */
													CgbUserTransDetail investUserTransDetail = new CgbUserTransDetail();
													investUserTransDetail.setId(IdGen.uuid()); // 主键ID.
													investUserTransDetail.setTransId(invest.getId()); // 出借记录唯一标识.
													investUserTransDetail.setUserId(userInfo.getId()); // 客户帐号ID.
													investUserTransDetail.setAccountId(userAccountInfo.getId()); // 客户账户ID.
													int x = ++a;
													investUserTransDetail.setTransDate(new Date(System.currentTimeMillis() + 1000 * x)); // 交易时间.
													investUserTransDetail.setTrustType(CgbUserTransDetailService.TRUST_TYPE_3); // 定期出借.
													investUserTransDetail.setAmount(investAmount); // 出借金额.
													investUserTransDetail.setAvaliableAmount(availableAmount); // 当前可用余额.
													investUserTransDetail.setInOutType(CgbUserTransDetailService.IN_TYPE_1); // 收入.
													investUserTransDetail.setRemarks("流标-出借金额退款"); // 备注信息.
													investUserTransDetail.setState(CgbUserTransDetailService.TRUST_STATE_2); // 流水状态，成功.
													int investUserTransDetailFlag = cgbUserTransDetailDao.insert(investUserTransDetail);
													if (investUserTransDetailFlag == 1) { // 计入流水成功.
														log.info("fn:callbackCancel-出借流标计入流水成功.");
														// 短消息提醒.
														weixinSendTempMsgService.cgbSendBidCancelMsg(invest);
													} else {
														log.info("fn:callbackCancel-出借流标计入流水失败.");
													}
												} else {
													log.info("fn:callbackCancel-出借人账户更新失败.");
												}
												/**
												 * 该笔出借，使用的抵用券记录.
												 */
//												UserVouchersHistory userVouchersHistory = new UserVouchersHistory();
//												userVouchersHistory.setBidId(invest.getId()); // 出借记录唯一标识.
//												List<UserVouchersHistory> vouchersList = userVouchersHistoryDao.findList(userVouchersHistory);
//												for (UserVouchersHistory vouchers : vouchersList) {
//													int y = ++a;
//													String vouchersValue = vouchers.getValue();// 抵用券金额.
//													AVouchersDic vouchersDic = aVouchersDicDao.get(vouchers.getAwardId());
//													/**
//													 * 新增抵用券.
//													 */
//													UserVouchersHistory newVouchers = new UserVouchersHistory();
//													String newVouchersId = IdGen.uuid();
//													newVouchers.setId(newVouchersId);
//													newVouchers.setAwardId(vouchers.getAwardId());
//													newVouchers.setUserId(vouchers.getUserId());
//													newVouchers.setOverdueDate(DateUtils.getSpecifiedMonthAfter(new Date(), vouchersDic.getOverdueDays()));
//													newVouchers.setState(AUserAwardsHistoryService.A_USER_AWARDS_HISTORY_STATE_1);
//													newVouchers.setType(AUserAwardsHistoryService.COUPONS_TYPE_1);
//													newVouchers.setValue(vouchersValue);
//													newVouchers.setCreateDate(new Date(System.currentTimeMillis() + 1000 * y));
//													newVouchers.setUpdateDate(new Date(System.currentTimeMillis() + 1000 * y));
//													newVouchers.setRemark(vouchers.getRemark());
//													newVouchers.setSpans(vouchers.getSpans());
//													newVouchers.setOverdueDays(vouchers.getOverdueDays());
//													newVouchers.setLimitAmount(vouchers.getLimitAmount());
//													int newVouchersFlag = userVouchersHistoryDao.insert(newVouchers);
//													if (newVouchersFlag == 1) {
//														log.info("fn:callbackCancel-新增抵用券成功.");
//														/**
//														 * 出借账户变更，(可用余额 -抵用券金额).
//														 */
//														CgbUserAccount vouchersUserAccountInfo = cgbUserAccountDao.getUserAccountInfo(userInfo.getId());
//														Double vouchersAmount = Double.valueOf(vouchersValue); // 抵用券金额.
//														// 可用余额(-).
//														Double vouchersAvailableAmount = vouchersUserAccountInfo.getAvailableAmount();
//														vouchersAvailableAmount = NumberUtils.scaleDouble(vouchersAvailableAmount - vouchersAmount);
//														vouchersUserAccountInfo.setAvailableAmount(vouchersAvailableAmount);
//														// 账户总额(-).
//														Double vouchersTotalAmount = vouchersUserAccountInfo.getTotalAmount();
//														vouchersTotalAmount = NumberUtils.scaleDouble(vouchersTotalAmount - vouchersAmount);
//														vouchersUserAccountInfo.setTotalAmount(vouchersTotalAmount);
//														int vouchersUserAccountInfoFlag = cgbUserAccountDao.update(vouchersUserAccountInfo);
//														if (vouchersUserAccountInfoFlag == 1) {
//															log.info("fn:callbackCancel-出借人账户更新成功.");
//															/**
//															 * 计入流水.
//															 */
//															CgbUserTransDetail vouchersUserTransDetail = new CgbUserTransDetail();
//															vouchersUserTransDetail.setId(IdGen.uuid()); // 主键ID.
//															vouchersUserTransDetail.setTransId(newVouchersId); // 出借记录唯一标识.
//															vouchersUserTransDetail.setUserId(userInfo.getId()); // 客户帐号ID.
//															vouchersUserTransDetail.setAccountId(userAccountInfo.getId()); // 客户账户ID.
//															vouchersUserTransDetail.setTransDate(new Date(System.currentTimeMillis() + 1000 * y)); // 交易时间.
//															vouchersUserTransDetail.setTrustType(CgbUserTransDetailService.TRUST_TYPE_10); // 抵用券.
//															vouchersUserTransDetail.setAmount(vouchersAmount); // 抵用券金额.
//															vouchersUserTransDetail.setAvaliableAmount(vouchersAvailableAmount); // 当前可用余额.
//															vouchersUserTransDetail.setInOutType(CgbUserTransDetailService.OUT_TYPE_2); // 支出.
//															vouchersUserTransDetail.setRemarks("流标-抵用券退款"); // 备注信息.
//															vouchersUserTransDetail.setState(CgbUserTransDetailService.TRUST_STATE_2); // 流水状态，成功.
//															int vouchersUserTransDetailFlag = cgbUserTransDetailDao.insert(vouchersUserTransDetail);
//															if (vouchersUserTransDetailFlag == 1) { // 计入流水成功.
//																log.info("fn:callbackCancel-流标-抵用券退款计入流水成功.");
//															} else {
//																log.info("fn:callbackCancel-流标-抵用券退款计入流水失败.");
//															}
//														} else {
//															log.info("fn:callbackCancel-出借人账户更新失败.");
//														}
//													} else {
//														log.info("fn:callbackCancel-新增抵用券失败.");
//													}
//												}
												/**
												 * 释放账户锁.
												 */
												boolean accReleaseLock = JedisUtils.releaseLock(accKey, accRetIdentifier);
												if (accReleaseLock) {
													log.info("fn:callbackCancel-释放账户锁成功.");
												} else {
													log.info("fn:callbackCancel-释放账户锁失败.");
												}
											}
										}
									}
								}
								/**
								 * 释放订单锁.
								 */
								boolean releaseLock = JedisUtils.releaseLock(ordKey, ordRetIdentifier);
								if (releaseLock) {
									log.info("fn:callbackCancel-释放订单锁成功.");
								} else {
									log.info("fn:callbackCancel-释放订单锁失败.");
								}
								// 响应对方服务器不在发送通知.
								result.put("respCode", "00");
								result.put("respMsg", "成功");
								String jsonString = JSON.toJSONString(result);
								// 对返回对方服务器消息进行加密.
								result = APIUtils.encryptDataBySSL(jsonString, MERCHANT_RSA_PUBLIC_KEY);
								return result;
							} else {
								// 响应对方服务器不在发送通知.
								result.put("respCode", "00");
								result.put("respMsg", "成功");
								String jsonString = JSON.toJSONString(result);
								// 对返回对方服务器消息进行加密.
								result = APIUtils.encryptDataBySSL(jsonString, MERCHANT_RSA_PUBLIC_KEY);
								return result;
							}
						}
					}
				}
			} else {
				log.info("fn:callbackCancel-验密失败，请联系开发人员，定位失败原因.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * 方法: callbackReplaceRepay <br>
	 * 描述: 代偿还款回调地址接口. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2018年2月6日 下午4:57:27
	 * 
	 * @param tm
	 * @param data
	 * @return
	 * @throws IOException
	 */
	@POST
	@Path("/callbackReplaceRepay")
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized Map<String, String> callbackReplaceRepay(@FormParam("tm") String tm, @FormParam("data") String data) throws IOException {

		String jsonRet;
		Map<String, String> result = new HashMap<String, String>();
		try {
			// 对通知数据进行解密.
			jsonRet = APIUtils.decryptDataBySSL(tm, data, MERCHANT_RSA_PRIVATE_KEY);
			Map<String, String> map = JSON.parseObject(jsonRet, new TypeReference<Map<String, String>>() {
			});
			String merchantId = (String) map.get("merchantId");
			log.info("fn:callbackReplaceRepay-merchantId(商户ID) = " + merchantId);
			String orderId = (String) map.get("orderId");
			log.info("fn:callbackReplaceRepay-orderId(项目还款-id/subOrderId) = " + orderId);
			String status = (String) map.get("status");
			String remark = (String) map.get("remark");
			log.info("fn:callbackReplaceRepay-remark(备注) = " + remark);
			String signature = (String) map.get("signature");
			log.info("fn:callbackReplaceRepay-signature(签名) = " + signature);
			map.remove("signature");
			// 校验验密.
			boolean verifyRet = APIUtils.verify(MERCHANT_RSA_PUBLIC_KEY, signature, map, "RSA");
			if (verifyRet) { // 验密成功.
				if (status.equals("S")) {
					log.info("fn:callbackReplaceRepay-status(状态)：" + status + " = 成功");
					// 业务订单信息.
					ZtmgOrderInfo entity = new ZtmgOrderInfo();
					entity.setId(IdGen.uuid());
					entity.setMerchantId(merchantId);
					entity.setOrderId(orderId);
					entity.setStatus(status);
					entity.setSignature(signature);
					entity.setType(ZtmgOrderInfoService.TYPE_2); // 代偿户.
					entity.setState(ZtmgOrderInfoService.STATE_1); // 未还.
					entity.setCreateDate(new Date());
					entity.setUpdateDate(new Date());
					entity.setRemarks(remark);
					int flag = ztmgOrderInfoDao.insert(entity);
					if (flag == 1) { // 新增成功.
						log.info("fn:callbackReplaceRepay-中投摩根业务订单新增成功！");
					} else { // 新增失败.
						log.info("fn:callbackReplaceRepay-中投摩根业务订单新增失败！");
					}

					/**
					 * 接收通知成功，通知对方服务器不在发送通知.
					 */
					result.put("respCode", "00");
					result.put("respMsg", "成功");
					String jsonString = JSON.toJSONString(result);
					result = APIUtils.encryptDataBySSL(jsonString, MERCHANT_RSA_PUBLIC_KEY);
					return result;
				} else if (status.equals("F")) {
					log.info("fn:callbackReplaceRepay-status(状态)：" + status + " = 失败");
					/**
					 * 接收通知成功，通知对方服务器不在发送通知.
					 */
					result.put("respCode", "00");
					result.put("respMsg", "成功");
					String jsonString = JSON.toJSONString(result);
					result = APIUtils.encryptDataBySSL(jsonString, MERCHANT_RSA_PUBLIC_KEY);
					return result;
				}
			} else { // 验密失败.
				log.info("验密失败，请联系资金存管方，定位失败原因.");
				/**
				 * 接收通知成功，通知对方服务器不在发送通知.
				 */
				result.put("respCode", "00");
				result.put("respMsg", "成功");
				String jsonString = JSON.toJSONString(result);
				result = APIUtils.encryptDataBySSL(jsonString, MERCHANT_RSA_PUBLIC_KEY);
				return result;
			}
		} catch (Exception e) {
			log.info("fn:callbackReplaceRepay-异常信息：" + e.getMessage());
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * 方法: callbackEntrustedWithdraw <br>
	 * 描述: 受托支付提现回调地址. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年12月6日 下午2:11:48
	 * 
	 * @param tm
	 * @param data
	 * @return
	 * @throws IOException
	 */
	@POST
	@Path("/callbackEntrustedWithdraw")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> callbackEntrustedWithdraw(@FormParam("tm") String tm, @FormParam("data") String data) throws IOException {

		String jsonRet;
		// 最后一期还款日期.
		Map<String, String> result = new HashMap<String, String>();
		try {
			// 对通知数据进行解密.
			jsonRet = APIUtils.decryptDataBySSL(tm, data, MERCHANT_RSA_PRIVATE_KEY);
			Map<String, String> map = JSON.parseObject(jsonRet, new TypeReference<Map<String, String>>() {
			});
			// 由存管银行分配给网贷平台的唯一的商户编码.
			String merchantId = (String) map.get("merchantId");
			log.info("fn:callbackEntrustedWithdraw-merchantId(商户编码) = " + merchantId);
			// 由网贷平台生成的唯一的交易流水号.
			String orderId = (String) map.get("orderId");
			log.info("fn:callbackEntrustedWithdraw-orderId(项目ID) = " + orderId);
			// S = 成功，F = 失败，AS = 受理成功.
			String status = (String) map.get("status");
			if (status.equals("S")) {
				log.info("fn:callbackEntrustedWithdraw-status(状态) = 成功");
			} else if (status.equals("F")) {
				log.info("fn:callbackEntrustedWithdraw-status(状态) = 失败");
			} else if (status.equals("AS")) {
				log.info("fn:callbackEntrustedWithdraw-status(状态) = 受理成功");
			}
			// 金额，单位（分）.
			String amount = (String) map.get("amount");
			log.info("fn:callbackEntrustedWithdraw-amount(受托支付提现金额) = " + amount);
			// 备注.
			String remark = (String) map.get("remark");
			log.info("fn:callbackEntrustedWithdraw-remark(备注) = " + remark);
			// 签名.
			String signature = (String) map.get("signature");
			log.info("fn:callbackEntrustedWithdraw-signature(签名) = " + signature);
			map.remove("signature");

			// 校验验密.
			boolean verifyRet = APIUtils.verify(MERCHANT_RSA_PUBLIC_KEY, signature, map, "RSA");
			// 验密成功，进行业务处理
			if (verifyRet) {
				log.info("fn:callbackEntrustedWithdraw-验密成功，进行业务处理.");
				if (status.equals("S") || status.equals("AS")) { // 成功/受理成功.
					// 项目详情.
					WloanTermProject wloanTermProject = wloanTermProjectService.get(orderId);
					if (null != wloanTermProject) {
						// 更新受托支付提现，0：否，1：是.
						wloanTermProject.setIsEntrustedWithdraw(WloanTermProjectService.IS_ENTRUSTED_WITHDRAW_1);
						wloanTermProject.setUpdateDate(new Date());
						wloanTermProjectService.updateProState(wloanTermProject);
						// 融资主体.
						WloanSubject wloanSubject = wloanSubjectService.get(wloanTermProject.getSubjectId());
						if (null != wloanSubject) {
							/**
							 * 借款人账户变更.
							 */
							CreditUserInfo creditUserInfo = creditUserInfoDao.get(wloanSubject.getLoanApplyId());
							if (null != creditUserInfo) { // 借款人帐号信息.
								CreditUserAccount creditUserAccount = creditUserAccountDao.get(creditUserInfo.getAccountId());
								if (null != creditUserAccount) { // 借款人账户信息.
									Double withdrawAmount = Double.valueOf(amount) / 100;
									// 可用余额.
									Double availableAmount = creditUserAccount.getAvailableAmount();
									if (availableAmount >= withdrawAmount) { // 可用余额必须足额.
										availableAmount = availableAmount - withdrawAmount;
										creditUserAccount.setAvailableAmount(availableAmount);
										// 账户总额.
										Double totalAmount = creditUserAccount.getTotalAmount();
										totalAmount = totalAmount - withdrawAmount;
										creditUserAccount.setTotalAmount(totalAmount);
										// 融资金额(借款总额).
										String borrowingTotalAmount = creditUserAccount.getBorrowingTotalAmount();
										Double borrowingTotalAmountDou = Double.valueOf(borrowingTotalAmount);
										borrowingTotalAmountDou = borrowingTotalAmountDou - withdrawAmount;
										String borrowingTotalAmountStr = NumberUtils.scaleDoubleStr(borrowingTotalAmountDou);
										creditUserAccount.setBorrowingTotalAmount(borrowingTotalAmountStr);
										int creditUserAccountFlag = creditUserAccountDao.update(creditUserAccount);
										if (creditUserAccountFlag == 1) { // 借款人账户更新.
											log.info("fn:callbackEntrustedWithdraw-借款人账户更新成功");
											/**
											 * 计入流水.
											 */
											CgbUserTransDetail userTransDetail = new CgbUserTransDetail();
											userTransDetail.setId(IdGen.uuid()); // 主键.
											userTransDetail.setTransId(orderId); // 项目ID(受托支付提现流水号).
											userTransDetail.setUserId(creditUserInfo.getId()); // 客户帐号.
											userTransDetail.setAccountId(creditUserAccount.getId()); // 客户账户.
											userTransDetail.setTransDate(new Date()); // 交易时间.
											userTransDetail.setTrustType(CgbUserTransDetailService.TRUST_TYPE_12); // 受托支付提现.
											userTransDetail.setAmount(withdrawAmount); // 金额.
											userTransDetail.setAvaliableAmount(creditUserAccount.getAvailableAmount()); // 可用余额.
											userTransDetail.setInOutType(CgbUserTransDetailService.OUT_TYPE_2); // 支出.
											userTransDetail.setRemarks("受托支付提现"); // 备注信息.
											userTransDetail.setState(CgbUserTransDetailService.TRUST_STATE_2); // 成功.
											int userTransDetailFlag = cgbUserTransDetailDao.insert(userTransDetail);
											if (userTransDetailFlag == 1) {
												log.info("fn:callbackEntrustedWithdraw-受托支付提现流水添加成功");
												// 接收通知成功，通知对方服务器不在发送通知.
												result.put("respCode", "00");
												result.put("respMsg", "成功");
												String jsonString = JSON.toJSONString(result);
												// 对返回对方服务器消息进行加密.
												result = APIUtils.encryptDataBySSL(jsonString, MERCHANT_RSA_PUBLIC_KEY);
											} else {
												log.info("fn:callbackEntrustedWithdraw-受托支付提现流水添加失败");
											}
										} else {
											log.info("fn:callbackEntrustedWithdraw-借款人账户更新失败");
										}
									} else { // 可用余额不足.
										// 非00情况，存管系统会进行重试.
										result.put("respCode", "01");
										result.put("respMsg", "可用余额不足");
										String jsonString = JSON.toJSONString(result);
										// 对返回对方服务器消息进行加密
										result = APIUtils.encryptDataBySSL(jsonString, MERCHANT_RSA_PUBLIC_KEY);
										// 返回参数对方服务器，不在发送请求.
										return result;
									}
								}
							}
						}
					}
				}
				// 返回参数对方服务器，不在发送请求.
				return result;
			} else {
				log.info("fn:callbackEntrustedWithdraw-验密失败，请联系资金存管方，定位失败原因.");
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * 方法: callbackRepay <br>
	 * 描述: 还款回调地址接口. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2018年2月6日 下午5:22:55
	 * 
	 * @param tm
	 * @param data
	 * @return
	 * @throws IOException
	 */
	@POST
	@Path("/callbackRepay")
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized Map<String, String> callbackRepay(@FormParam("tm") String tm, @FormParam("data") String data) throws IOException {

		String jsonRet;
		Map<String, String> result = new HashMap<String, String>();
		try {
			// 对通知数据进行解密.
			jsonRet = APIUtils.decryptDataBySSL(tm, data, MERCHANT_RSA_PRIVATE_KEY);
			Map<String, String> map = JSON.parseObject(jsonRet, new TypeReference<Map<String, String>>() {
			});
			String merchantId = (String) map.get("merchantId");
			log.info("fn:callbackRepay-merchantId(商户ID) = " + merchantId);
			String orderId = (String) map.get("orderId");
			log.info("fn:callbackRepay-orderId(项目还款-id/subOrderId) = " + orderId);
			String status = (String) map.get("status");
			String remark = (String) map.get("remark");
			log.info("fn:callbackRepay-remark(备注) = " + remark);
			String signature = (String) map.get("signature");
			log.info("fn:callbackRepay-signature(签名) = " + signature);
			map.remove("signature");
			// 校验验密.
			boolean verifyRet = APIUtils.verify(MERCHANT_RSA_PUBLIC_KEY, signature, map, "RSA");
			if (verifyRet) { // 验密成功.
				if (status.equals("S")) {
					log.info("fn:callbackRepay-status(状态)：" + status + " = 成功");
					// 业务订单信息.
					ZtmgOrderInfo entity = new ZtmgOrderInfo();
					entity.setId(IdGen.uuid());
					entity.setMerchantId(merchantId);
					entity.setOrderId(orderId);
					entity.setStatus(status);
					entity.setSignature(signature);
					entity.setType(ZtmgOrderInfoService.TYPE_1); // 借款户.
					entity.setState(ZtmgOrderInfoService.STATE_1); // 未还.
					entity.setCreateDate(new Date());
					entity.setUpdateDate(new Date());
					entity.setRemarks(remark);
					int flag = ztmgOrderInfoDao.insert(entity);
					if (flag == 1) { // 新增成功.
						log.info("fn:callbackRepay-中投摩根业务订单新增成功！");
					} else { // 新增失败.
						log.info("fn:callbackRepay-中投摩根业务订单新增失败！");
					}

					/**
					 * 接收通知成功，通知对方服务器不在发送通知.
					 */
					result.put("respCode", "00");
					result.put("respMsg", "成功");
					String jsonString = JSON.toJSONString(result);
					result = APIUtils.encryptDataBySSL(jsonString, MERCHANT_RSA_PUBLIC_KEY);
					return result;
				} else if (status.equals("F")) {
					log.info("fn:callbackRepay-status(状态)：" + status + " = 失败");
					/**
					 * 接收通知成功，通知对方服务器不在发送通知.
					 */
					result.put("respCode", "00");
					result.put("respMsg", "成功");
					String jsonString = JSON.toJSONString(result);
					result = APIUtils.encryptDataBySSL(jsonString, MERCHANT_RSA_PUBLIC_KEY);
					return result;
				}
			} else { // 验密失败.
				log.info("验密失败，请联系资金存管方，定位失败原因.");
				/**
				 * 接收通知成功，通知对方服务器不在发送通知.
				 */
				result.put("respCode", "00");
				result.put("respMsg", "成功");
				String jsonString = JSON.toJSONString(result);
				result = APIUtils.encryptDataBySSL(jsonString, MERCHANT_RSA_PUBLIC_KEY);
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * 方法: callbackCreate <br>
	 * 描述: 标的报备回调地址接口. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年6月21日 下午3:33:23
	 * 
	 * @param tm
	 * @param data
	 * @return
	 * @throws IOException
	 */
	@POST
	@Path("/callbackCreate")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> callbackCreate(@FormParam("tm") String tm, @FormParam("data") String data) throws IOException {

		String jsonRet;
		// 最后一期还款日期.
		Map<String, String> result = new HashMap<String, String>();
		try {
			// 对通知数据进行解密.
			jsonRet = APIUtils.decryptDataBySSL(tm, data, MERCHANT_RSA_PRIVATE_KEY);
			Map<String, String> map = JSON.parseObject(jsonRet, new TypeReference<Map<String, String>>() {
			});
			String merchantId = (String) map.get("merchantId");
			log.info("fn:callbackCreate-merchantId(商户ID) = " + merchantId);
			String bidId = (String) map.get("bidId");
			log.info("fn:callbackCreate-bidId(项目ID) = " + bidId);
			String bidStatus = (String) map.get("bidStatus");
			if (bidStatus.equals("W")) {
				log.info("fn:callbackCreate-bidStatus(标的状态)：" + bidStatus + " = 等待中");
			} else if (bidStatus.equals("II")) {
				log.info("fn:callbackCreate-bidStatus(标的状态)：" + bidStatus + " = 投资中");
			} else if (bidStatus.equals("GI")) {
				log.info("fn:callbackCreate-bidStatus(标的状态)：" + bidStatus + " = 放款中");
			} else if (bidStatus.equals("GD")) {
				log.info("fn:callbackCreate-bidStatus(标的状态)：" + bidStatus + " = 已放款");
			} else if (bidStatus.equals("CI")) {
				log.info("fn:callbackCreate-bidStatus(标的状态)：" + bidStatus + " = 流标中");
			} else if (bidStatus.equals("CD")) {
				log.info("fn:callbackCreate-bidStatus(标的状态)：" + bidStatus + " = 已流标");
			}
			String bankAuditStatus = (String) map.get("bankAuditStatus");
			if (bankAuditStatus.equals("N")) {
				log.info("fn:callbackCreate-bankAuditStatus(标的在银行审核状态)：" + bidStatus + " = 无需报备审核");
			} else if (bankAuditStatus.equals("S")) {
				log.info("fn:callbackCreate-bankAuditStatus(标的在银行审核状态)：" + bidStatus + " = 成功");
			} else if (bankAuditStatus.equals("F")) {
				log.info("fn:callbackCreate-bankAuditStatus(标的在银行审核状态)：" + bidStatus + " = 失败");
			} else if (bankAuditStatus.equals("W")) {
				log.info("fn:callbackCreate-bankAuditStatus(标的在银行审核状态)：" + bidStatus + " = 等待报备中");
			}
			String remark = (String) map.get("remark");
			log.info("fn:callbackCreate-remark(备注) = " + remark);
			String signature = (String) map.get("signature");
			log.info("fn:callbackCreate-signature(签名) = " + signature);
			map.remove("signature");

			// 校验验密
			boolean verifyRet = APIUtils.verify(MERCHANT_RSA_PUBLIC_KEY, signature, map, "RSA");
			// 验密成功，进行业务处理
			if (verifyRet) {
				log.info("fn:callbackCreate-验密成功，进行业务处理.");
				String status = (String) map.get("bankAuditStatus");
				if (status.equals("S")) { // 标的在银行审核状态成功.
					// 项目发布.
					WloanTermProject wloanTermProject = wloanTermProjectService.get(bidId);
					wloanTermProject.setState(WloanTermProjectService.PUBLISH);
					wloanTermProject.setUpdateDate(new Date());
					// 该项目还款计划.
					List<WloanTermProjectPlan> plans = wloanTermProjectPlanService.findListByProjectId(bidId);
					if (plans != null) {
						if (plans.size() > 0) {
							log.info("fn:callbackCreate-项目审核（发布）成功.");
							// 接收通知成功，通知对方服务器不在发送通知
							result.put("respCode", "00");
							result.put("respMsg", "成功");
							String jsonString = JSON.toJSONString(result);
							// 对返回对方服务器消息进行加密
							result = APIUtils.encryptDataBySSL(jsonString, MERCHANT_RSA_PUBLIC_KEY);
						} else {
							String projectRepayPlanType = wloanTermProject.getProjectRepayPlanType();
							if (WloanTermProjectService.PROJECT_REPAY_PLAN_TYPE_0.equals(projectRepayPlanType)) { // 旧版.
								String flag = wloanTermProjectPlanService.initWloanTermProjectPlan(wloanTermProject);
								if (flag.equals("SUCCESS")) {
									log.info("fn:callbackCreate-项目审核（发布）成功，生成还款计划.");
									// 该项目还款计划.
									List<WloanTermProjectPlan> planList = wloanTermProjectPlanService.findListByProjectId(bidId);
									if (planList != null && planList.size() > 0) {
										log.info("fn:callbackCreate-该项目还款笔数 = " + planList.size());
										WloanTermProjectPlan projectPlan = planList.get(planList.size() - 1); // 最后一期还款.
										wloanTermProject.setEndDate(projectPlan.getRepaymentDate()); // 项目结束时间.
										wloanTermProjectService.updateProState(wloanTermProject); // 变更项目状态.
									}
									// 接收通知成功，通知对方服务器不在发送通知
									result.put("respCode", "00");
									result.put("respMsg", "成功");
									String jsonString = JSON.toJSONString(result);
									// 对返回对方服务器消息进行加密
									result = APIUtils.encryptDataBySSL(jsonString, MERCHANT_RSA_PUBLIC_KEY);
								}
							} else if (WloanTermProjectService.PROJECT_REPAY_PLAN_TYPE_1.equals(projectRepayPlanType)) {
								String flag = wloanTermProjectPlanService.initCgbWloanTermProjectPlan(wloanTermProject);
								if (flag.equals("SUCCESS")) {
									log.info("fn:callbackCreate-项目审核（发布）成功，生成还款计划.");
									// 该项目还款计划.
									List<WloanTermProjectPlan> planList = wloanTermProjectPlanService.findListByProjectId(bidId);
									if (planList != null && planList.size() > 0) {
										log.info("fn:callbackCreate-该项目还款笔数 = " + planList.size());
										WloanTermProjectPlan projectPlan = planList.get(planList.size() - 1); // 最后一期还款.
										wloanTermProject.setEndDate(projectPlan.getRepaymentDate()); // 项目结束时间.
										wloanTermProjectService.updateProState(wloanTermProject); // 变更项目状态.
									}
									// 接收通知成功，通知对方服务器不在发送通知
									result.put("respCode", "00");
									result.put("respMsg", "成功");
									String jsonString = JSON.toJSONString(result);
									// 对返回对方服务器消息进行加密
									result = APIUtils.encryptDataBySSL(jsonString, MERCHANT_RSA_PUBLIC_KEY);
								}
							}
						}
					}
				}
				// 返回参数对方服务器，不在发送请求.
				return result;
			} else {
				log.info("fn:callbackCreate-验密失败，请联系资金存管方，定位失败原因.");
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * 方法: callbackGrant <br>
	 * 描述: 放款回调地址接口. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年5月24日 上午9:15:15
	 * 
	 * @param tm
	 * @param data
	 * @return
	 * @throws IOException
	 */
	@POST
	@Path("/callbackGrant")
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized Map<String, String> callbackGrant(@FormParam("tm") String tm, @FormParam("data") String data) throws IOException {

		String jsonRet;
		Map<String, String> result = new HashMap<String, String>();
		try {
			// 对通知数据进行解密.
			jsonRet = APIUtils.decryptDataBySSL(tm, data, MERCHANT_RSA_PRIVATE_KEY);
			Map<String, String> map = JSON.parseObject(jsonRet, new TypeReference<Map<String, String>>() {
			});
			String merchantId = (String) map.get("merchantId");
			log.info("fn:callbackGrant-merchantId(商户ID) = " + merchantId);
			String orderId = (String) map.get("orderId");
			log.info("fn:callbackGrant-orderId(项目ID) = " + orderId);
			String status = (String) map.get("status");
			if (status.equals("S")) {
				log.info("fn:callbackGrant-status(状态)：" + status + " = 成功");
			} else if (status.equals("F")) {
				log.info("fn:callbackGrant-status(状态)：" + status + " = 失败");
			}
			String remark = (String) map.get("remark");
			log.info("fn:callbackGrant-remark(备注) = " + remark);
			String signature = (String) map.get("signature");
			log.info("fn:callbackGrant-signature(签名) = " + signature);
			map.remove("signature");

			// 校验验密
			boolean verifyRet = APIUtils.verify(MERCHANT_RSA_PUBLIC_KEY, signature, map, "RSA");
			// 验密成功，进行业务处理
			if (verifyRet) {
				log.info("fn:callbackGrant-验密成功，进行业务处理.");
				if (status.equals("S")) { // 成功.
					WloanTermProject wloanTermProject = wloanTermProjectService.get(orderId);
					if (null != wloanTermProject) { // 项目详情.
						/**
						 * 计算客户每期的还款金额-等于-项目每期的还款金额.
						 */
						// 代还金额（放款金额 + 利息）.
						Double surplusAmount = 0d;
						List<WloanTermProjectPlan> projectRepayPlans = wloanTermProjectPlanService.findListByProjectId(orderId);
						for (WloanTermProjectPlan projectRepayPlan : projectRepayPlans) { // 项目还款计划列表.
							log.info("fn:callbackGrant-【项目还款计划】-还款日期：" + DateUtils.formatDate(projectRepayPlan.getRepaymentDate()) + "，还款金额：" + projectRepayPlan.getInterest() + "元.");
							log.info("fn:callbackGrant-【项目还款计划】-项目ID：" + orderId);
							Double interest = 0D;
							WloanTermUserPlan entity = new WloanTermUserPlan();
							entity.setProjectId(orderId);
							entity.setRepaymentDate(projectRepayPlan.getRepaymentDate());
							List<WloanTermUserPlan> userRepayPlans = wloanTermUserPlanDao.findUserRepayPlans(entity);
							for (WloanTermUserPlan userRepayPlan : userRepayPlans) {
								interest = interest + userRepayPlan.getInterest();
							}
							log.info("fn:callbackGrant-【项目还款计划】-应还金额：" + NumberUtils.scaleDouble(interest) + "元.");
							projectRepayPlan.setInterest(NumberUtils.scaleDouble(interest));
							// 代还金额+.
							surplusAmount = NumberUtils.scaleDouble(surplusAmount + interest);
							int projectRepayPlanFlag = wloanTermProjectPlanDao.update(projectRepayPlan);
							if (projectRepayPlanFlag == 1) {
								log.info("fn:callbackGrant-【项目还款计划】-更新成功.");
							} else {
								log.info("fn:callbackGrant-【项目还款计划】-更新失败.");
							}
						}

						// 实际投资金额.
						Double currentAmount = wloanTermProject.getCurrentRealAmount();
						WloanSubject wloanSubject = wloanSubjectService.get(wloanTermProject.getSubjectId());
						if (null != wloanSubject) { // 融资主体.
							/**
							 * 借款人账户变更.
							 */
							CreditUserInfo creditUserInfo = creditUserInfoDao.get(wloanSubject.getLoanApplyId());
							if (null != creditUserInfo) { // 借款人帐号信息.
								CreditUserAccount creditUserAccount = creditUserAccountDao.get(creditUserInfo.getAccountId());
								if (null != creditUserAccount) { // 借款人账户信息.
									// 账户总额+.
									Double totalAmount = creditUserAccount.getTotalAmount();
									totalAmount = totalAmount + currentAmount;
									creditUserAccount.setTotalAmount(totalAmount);
									// 可用余额+.
									Double availableAmount = creditUserAccount.getAvailableAmount();
									availableAmount = availableAmount + currentAmount;
									creditUserAccount.setAvailableAmount(availableAmount);
									// 融资金额(借款总额)+.
									String borrowingTotalAmount = creditUserAccount.getBorrowingTotalAmount();
									if (null == borrowingTotalAmount) {
										String financingAmount = NumberUtils.scaleDoubleStr(currentAmount);
										creditUserAccount.setBorrowingTotalAmount(financingAmount);
									} else {
										Double borrowingTotalAmountDou = Double.valueOf(borrowingTotalAmount);
										borrowingTotalAmountDou = borrowingTotalAmountDou + currentAmount;
										String borrowingTotalAmountStr = NumberUtils.scaleDoubleStr(borrowingTotalAmountDou);
										creditUserAccount.setBorrowingTotalAmount(borrowingTotalAmountStr);
									}
									// 代还金额+(代还金额 + 代还利息).
									Double oldSurplusAmount = creditUserAccount.getSurplusAmount();
									creditUserAccount.setSurplusAmount(NumberUtils.scaleDouble(oldSurplusAmount + surplusAmount));
								}
								int creditUserAccountFlag = creditUserAccountDao.update(creditUserAccount);
								if (creditUserAccountFlag == 1) { // 借款人账户更新.
									log.info("fn:grant-借款人账户更新成功");
									/**
									 * 计入流水.
									 */
									CgbUserTransDetail userTransDetail = new CgbUserTransDetail();
									userTransDetail.setId(IdGen.uuid()); // 主键.
									userTransDetail.setTransId(orderId); // 项目ID(放款单号).
									userTransDetail.setUserId(creditUserInfo.getId()); // 客户帐号.
									userTransDetail.setAccountId(creditUserAccount.getId()); // 客户账户.
									userTransDetail.setTransDate(new Date()); // 交易时间.
									userTransDetail.setTrustType(CgbUserTransDetailService.TRUST_TYPE_11); // 放款.
									userTransDetail.setAmount(currentAmount); // 金额.
									userTransDetail.setAvaliableAmount(creditUserAccount.getAvailableAmount()); // 可用余额.
									userTransDetail.setInOutType(CgbUserTransDetailService.IN_TYPE_1); // 收入.
									userTransDetail.setRemarks("放款"); // 备注信息.
									userTransDetail.setState(CgbUserTransDetailService.TRUST_STATE_2); // 成功.
									int userTransDetailFlag = cgbUserTransDetailDao.insert(userTransDetail);
									if (userTransDetailFlag == 1) {
										log.info("fn:grant-放款流水添加成功");
										// 放款短消息发送.
										weixinSendTempMsgService.cgbSendGrantInfoMsg(creditUserInfo, currentAmount);
										// 接收通知成功，通知对方服务器不在发送通知
										result.put("respCode", "00");
										result.put("respMsg", "成功");
										String jsonString = JSON.toJSONString(result);
										// 对返回对方服务器消息进行加密
										result = APIUtils.encryptDataBySSL(jsonString, MERCHANT_RSA_PUBLIC_KEY);
									} else {
										log.info("fn:grant-放款流水添加失败");
									}
								} else {
									log.info("fn:grant-借款人账户更新失败");
								}
							}
						}
					}
				}
				// 返回参数对方服务器，不在发送请求.
				return result;
			} else {
				log.info("验密失败，请联系资金存管方，定位失败原因.");
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
