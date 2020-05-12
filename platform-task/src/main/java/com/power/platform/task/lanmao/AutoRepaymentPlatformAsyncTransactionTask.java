package com.power.platform.task.lanmao;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.activity.dao.AUserAwardsHistoryDao;
import com.power.platform.activity.dao.AVouchersDicDao;
import com.power.platform.cgb.dao.CgbUserAccountDao;
import com.power.platform.cgb.dao.CgbUserTransDetailDao;
import com.power.platform.cgb.dao.ZtmgOrderInfoDao;
import com.power.platform.cgb.entity.CgbUserAccount;
import com.power.platform.cgb.entity.CgbUserTransDetail;
import com.power.platform.cgb.entity.ZtmgOrderInfo;
import com.power.platform.cgb.service.CgbUserTransDetailService;
import com.power.platform.cgb.service.ZtmgOrderInfoService;
import com.power.platform.cms.dao.NoticeDao;
import com.power.platform.cms.entity.Notice;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.credit.dao.apply.CreditUserApplyDao;
import com.power.platform.credit.dao.userinfo.CreditUserAccountDao;
import com.power.platform.credit.dao.userinfo.CreditUserInfoDao;
import com.power.platform.credit.entity.userinfo.CreditUserAccount;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.service.apply.CreditUserApplyService;
import com.power.platform.lanmao.dao.UserAccountOrderDao;
import com.power.platform.lanmao.entity.UserAccountOrder;
import com.power.platform.lanmao.type.BizTypeEnum;
import com.power.platform.lanmao.type.InOutTypeEnum;
import com.power.platform.lanmao.type.StatusEnum;
import com.power.platform.lanmao.type.UserRoleEnum;
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
import com.power.platform.regular.service.WloanTermProjectService;
import com.power.platform.task.pojo.NoticeContentPojo;
import com.power.platform.trandetail.dao.UserTransDetailDao;
import com.power.platform.userinfo.dao.UserAccountInfoDao;
import com.power.platform.userinfo.dao.UserInfoDao;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.weixin.service.WeixinSendTempMsgService;

/**
 * 
 * class: AutoRepaymentPlatformAsyncTransactionTask <br>
 * description: 自动还款平台批量交易任务调度 <br>
 * author: Roy <br>
 * date: 2019年10月12日 上午10:48:16
 */
@Service
@Lazy(false)
public class AutoRepaymentPlatformAsyncTransactionTask {

	private static final Logger logger = LoggerFactory.getLogger(AutoRepaymentPlatformAsyncTransactionTask.class);

	/**
	 * NOTICE_TYPE_2：公告.
	 */
	private static final Integer NOTICE_TYPE_2 = 2;

	/**
	 * NOTICE_STATE_1：上线.
	 */
	private static final Integer NOTICE_STATE_1 = 1;

	/**
	 * 付息.
	 */
	private static final String REPAY_TYPE_1 = "1";

	/**
	 * 还本.
	 */
	private static final String REPAY_TYPE_2 = "2";

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
	@Autowired
	private CreditUserApplyDao creditUserApplyDao;
	@Autowired
	private WloanTermInvestDao wloanTermInvestDao;
	@Autowired
	private AVouchersDicDao aVouchersDicDao;
	@Autowired
	private AUserAwardsHistoryDao aUserAwardsHistoryDao;
	@Autowired
	private UserTransDetailDao userTransDetailDao;
	@Autowired
	private UserAccountInfoDao userAccountInfoDao;
	@Autowired
	private UserInfoDao userInfoDao;
	@Autowired
	private WloanTermProjectPlanDao wloanTermProjectPlanDao;
	@Autowired
	private WloanTermUserPlanDao wloanTermUserPlanDao;
	@Autowired
	private CreditUserInfoDao creditUserInfoDao;
	@Autowired
	private CreditUserAccountDao creditUserAccountDao;
	@Autowired
	private CgbUserTransDetailDao cgbUserTransDetailDao;
	@Autowired
	private CgbUserAccountDao cgbUserAccountDao;
	@Autowired
	private WeixinSendTempMsgService weixinSendTempMsgService;
	@Autowired
	private ZtmgOrderInfoDao ztmgOrderInfoDao;
	@Autowired
	private NoticeDao noticeDao;
	@Autowired
	private UserAccountOrderDao userAccountOrderDao;

	/**
	 * 
	 * methods: autoRepaymentTransaction <br>
	 * description: 自动还款交易（借款人还款/代偿人） <br>
	 * author: Roy <br>
	 * date: 2019年10月12日 上午10:56:29
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	@Scheduled(cron = "0 0/3 * * * ?")
	public void platformAutoRepaymentTransaction() {

		logger.info("平台自动还款交易开始...start...");
		try {
			/**
			 * 封装未还订单查询.
			 */
			ZtmgOrderInfo ztmgOrderInfo = new ZtmgOrderInfo();
			ztmgOrderInfo.setState(ZtmgOrderInfoService.STATE_1);
			List<ZtmgOrderInfo> list = ztmgOrderInfoDao.findList(ztmgOrderInfo);

			// 没有待还项目计划.
			if (list != null && list.size() == 0) {
				logger.info("平台自动还款，没有待还标的计划......");
				return; // 结束当前方法.
			}

			// 当前订单的实例.
			ZtmgOrderInfo model = list.get(0); // 每次取订单列表的第一条，执行还款操作.

			// 订单号.
			String orderId = model.getOrderId();

			if (model.getType().equals(ZtmgOrderInfoService.TYPE_1)) { // 借款户.
				logger.info("借款户还款...start...");
				if (ZtmgOrderInfoService.STATUS_S.equals(model.getStatus())) { // 创建订单成功
					/**
					 * 获取该批次的项目还款计划id/subOrderId.
					 */
					WloanTermProjectPlan repayTheInterest = wloanTermProjectPlanDao.get(orderId); // 付息订单
					if (null != repayTheInterest) { // 1）获取付息订单
						// 还本付息
						if (WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1.equals(repayTheInterest.getPrincipal())) {
							// 还本订单
							String subOrderId = repayTheInterest.getSubOrderId();
							ZtmgOrderInfo subZtmgOrderInfo = new ZtmgOrderInfo();
							subZtmgOrderInfo.setOrderId(subOrderId);
							ZtmgOrderInfo repayTheCapitalZtmgOrderInfo = ztmgOrderInfoDao.findByOrderId(subZtmgOrderInfo);
							if (null != repayTheInterest.getWloanTermProject()) {
								WloanTermProject wloanTermProject = wloanTermProjectService.get(repayTheInterest.getWloanTermProject().getId());
								if (null != wloanTermProject) {// 标的详情
									Double repayTotalAmount = 0D; // 本期还本总额
									/**
									 * 出借人账户更新.
									 */
									// 客户还款计划查询封装.
									WloanTermUserPlan entity = new WloanTermUserPlan();
									entity.setState(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_2);
									entity.setWloanTermProject(wloanTermProject);
									entity.setRepaymentDate(repayTheInterest.getRepaymentDate());
									List<WloanTermUserPlan> userPlanList = wloanTermUserPlanDao.findList(entity);
									logger.info("出借人收回利息:{}笔......", userPlanList.size());
									long currentTimeMillis = System.currentTimeMillis();
									WloanTermInvest invest = null;
									UserInfo userInfo = null;
									for (WloanTermUserPlan userPlan : userPlanList) {
										currentTimeMillis = currentTimeMillis + 1000;
										invest = userPlan.getWloanTermInvest(); // 出借订单
										userInfo = userPlan.getUserInfo(); // 出借人帐号信息
										String userPlanId = userPlan.getId();
										// 还本付息总额.
										Double repayAmount = userPlan.getInterest();
										// 出借金额.
										Double investAmount = wloanTermInvestDao.findInvestAmountByProjectAndUser(wloanTermProject.getId(), userInfo != null ? userInfo.getId() : "", invest != null ? invest.getId() : "");
										// 付息.
										Double income = NumberUtils.scaleDouble(NumberUtils.subtract(repayAmount, investAmount));
										// 还款总额.
										repayTotalAmount = NumberUtils.scaleDouble(NumberUtils.add(repayTotalAmount, income));
										if (null != invest) { // 投资记录.
											CgbUserAccount userAccountInfo = null;
											if (null != userInfo) { // 出借人帐号.
												String userId = userInfo.getId();
												userAccountInfo = cgbUserAccountDao.getUserAccountInfo(userId);
												if (null != userAccountInfo) { // 出借人账户.
													UserAccountOrder uao = null;
													CgbUserTransDetail userTransDetail = null;
													String accountId = userAccountInfo.getId();
													// 可用余额+，定期代收收益-，定期总收益+.
													Double oldAvailableAmount = userAccountInfo.getAvailableAmount(); // 获取出借人账户可用余额
													int updateIncomeByIdFlag = cgbUserAccountDao.updateIncomeById(income, oldAvailableAmount, accountId);
													if (updateIncomeByIdFlag == 1) { // 账户更新成功
														// 平台流水
														userTransDetail = new CgbUserTransDetail();
														userTransDetail.setId(IdGen.uuid()); // 主键ID.
														userTransDetail.setTransId(userPlanId); // 付息外部子订单号.
														userTransDetail.setUserId(userId); // 客户帐号ID.
														userTransDetail.setAccountId(accountId); // 客户账户ID.
														userTransDetail.setTransDate(new Date(currentTimeMillis)); // 交易时间.
														userTransDetail.setTrustType(CgbUserTransDetailService.TRUST_TYPE_4); // 还利息.
														userTransDetail.setAmount(income); // 利息.
														oldAvailableAmount = NumberUtils.add(oldAvailableAmount, income);
														userTransDetail.setAvaliableAmount(oldAvailableAmount); // 当前可用余额.
														userTransDetail.setInOutType(CgbUserTransDetailService.IN_TYPE_1); // 收入.
														if (WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1.equals(repayTheInterest.getPrincipal())) {
															userTransDetail.setRemarks("最后一期还息"); // 备注信息.
														} else {
															userTransDetail.setRemarks("还息"); // 备注信息.
														}
														userTransDetail.setState(CgbUserTransDetailService.TRUST_STATE_2); // 流水状态，成功.
														int userTransDetailFlag = cgbUserTransDetailDao.insert(userTransDetail);
														if (userTransDetailFlag == 1) {
															logger.info("客户收回利息，平台流水插入成功......");
															if (null != repayTheCapitalZtmgOrderInfo) { // 还本订单
																if (ZtmgOrderInfoService.STATE_1.equals(repayTheCapitalZtmgOrderInfo.getState())) { // 未还-不执行最终操作.
																	// 付息消息提醒.
																	weixinSendTempMsgService.cgbSendRepayInfoMsg(userPlan, WeixinSendTempMsgService.IS_REPAY_TYPE_1);
																} else if (repayTheCapitalZtmgOrderInfo.getState().equals(ZtmgOrderInfoService.STATE_2)) { // 已还-执行最终操作.
																	userPlan.setState(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_3);
																	int modifyUserPlanStateFlag = wloanTermUserPlanDao.modifyWloanTermUserPlanState(userPlan);
																	if (modifyUserPlanStateFlag == 1) { // 更新成功.
																		logger.info("客户还款计划状态-更新成功......");
																		// 付息消息提醒.
																		weixinSendTempMsgService.cgbSendRepayInfoMsg(userPlan, WeixinSendTempMsgService.IS_REPAY_TYPE_1);
																	} else { // 失败.
																		logger.info("客户还款计划状态-更新失败......");
																	}
																}
															}
														} else {
															logger.info("客户收回利息，平台流水插入失败......");
														}
													} else { // 账户更新失败，记录失败订单，定时轮询上账
														uao = new UserAccountOrder();
														uao.setId(IdGen.uuid());
														uao.setUserId(userId);
														uao.setAccountId(accountId);
														uao.setTransId(userPlanId);
														uao.setUserRole(UserRoleEnum.INVESTOR.getValue()); // 出借人
														uao.setBizType(BizTypeEnum.INCOME.getValue()); // 利息
														uao.setInOutType(InOutTypeEnum.IN.getValue()); // 收入
														uao.setAmount(income); // 交易金额
														uao.setStatus(StatusEnum.FAIL.getValue());
														uao.setCreateDate(new Date(currentTimeMillis));
														uao.setUpdateDate(new Date(currentTimeMillis));
														int insertAccountOrderFlag = userAccountOrderDao.insert(uao);
														logger.info("账户更新失败，创建账户交易订单:{}", insertAccountOrderFlag == 1 ? "成功" : "失败");
													}
												} else {
													logger.info("出借人账户不存在......");
												}
											} else {
												logger.info("出借人帐号不存在......");
											}
										} else {
											logger.info("该笔出借订单不存在，出借人付息订单号:{}", userPlan.getId());
										}
									}
									WloanSubject wloanSubject = wloanSubjectService.get(wloanTermProject.getSubjectId());
									logger.info("最后一期付息，还款总额:{}", NumberUtils.scaleDoubleStr(repayTotalAmount));
									if (null != wloanSubject) { // 融资主体.
										/**
										 * 借款人账户变更.
										 */
										CreditUserInfo creditUserInfo = creditUserInfoDao.get(wloanSubject.getLoanApplyId());
										if (null != creditUserInfo) { // 借款人帐号.
											CreditUserAccount creditUserAccount = creditUserAccountDao.get(creditUserInfo.getAccountId());
											if (null != creditUserAccount) { // 借款人账户.
												// 账户总额-.
												Double totalAmount = creditUserAccount.getTotalAmount();
												totalAmount = NumberUtils.scaleDouble(totalAmount - repayTotalAmount);
												creditUserAccount.setTotalAmount(totalAmount);
												// 可用余额-.
												Double availableAmount = creditUserAccount.getAvailableAmount();
												availableAmount = NumberUtils.scaleDouble(availableAmount - repayTotalAmount);
												creditUserAccount.setAvailableAmount(availableAmount);
												// 待还金额-.
												Double surplusAmount = creditUserAccount.getSurplusAmount();
												surplusAmount = NumberUtils.scaleDouble(surplusAmount - repayTotalAmount);
												creditUserAccount.setSurplusAmount(surplusAmount);
												// 已还金额+.
												Double repayAmount = creditUserAccount.getRepayAmount();
												repayAmount = NumberUtils.scaleDouble(repayAmount + repayTotalAmount);
												creditUserAccount.setRepayAmount(repayAmount);
												// 更新日期.
												creditUserAccount.setUpdateDate(new Date(currentTimeMillis));
												int creditUserAccountFlag = creditUserAccountDao.update(creditUserAccount);
												if (creditUserAccountFlag == 1) { // 借款人账户更新成功.
													logger.info("借款人账户更新成功......");
													/**
													 * 计入流水.
													 */
													CgbUserTransDetail userTransDetail = new CgbUserTransDetail();
													userTransDetail.setId(IdGen.uuid()); // 主键ID.
													userTransDetail.setTransId(orderId); // 付息主订单号.
													userTransDetail.setUserId(creditUserInfo.getId()); // 客户帐号ID.
													userTransDetail.setAccountId(creditUserAccount.getId()); // 客户账户ID.
													userTransDetail.setTransDate(new Date(currentTimeMillis)); // 交易时间.
													userTransDetail.setTrustType(CgbUserTransDetailService.TRUST_TYPE_4); // 还利息.
													userTransDetail.setAmount(repayTotalAmount); // 利息.
													userTransDetail.setAvaliableAmount(availableAmount); // 当前可用余额.
													userTransDetail.setInOutType(CgbUserTransDetailService.OUT_TYPE_2); // 支出.
													userTransDetail.setRemarks("借款人还息"); // 备注信息.
													userTransDetail.setState(CgbUserTransDetailService.TRUST_STATE_2); // 流水状态，成功.
													int userTransDetailFlag = cgbUserTransDetailDao.insert(userTransDetail);
													if (userTransDetailFlag == 1) { // 借款人账户变更，计入流水.
														logger.info("借款人还息计入流水成功......");
													} else {
														logger.info("借款人还息计入流水失败......");
													}
												} else {
													logger.info("借款人账户更新失败......");
												}
											} else {
												logger.info("借款人账户不存在......");
											}
										} else {
											logger.info("借款人帐号不存在......");
										}
									} else {
										logger.info("融资主体不存在......");
									}

									/**
									 * 订单变更状态-已还.
									 */
									model.setState(ZtmgOrderInfoService.STATE_2);
									model.setUpdateDate(new Date(currentTimeMillis));
									int flag = ztmgOrderInfoDao.update(model);
									if (flag == 1) { // 成功.
										logger.info("订单变更状态成功......");
										if (null != repayTheCapitalZtmgOrderInfo) { // 还本订单
											if (repayTheCapitalZtmgOrderInfo.getState().equals(ZtmgOrderInfoService.STATE_1)) { // 未还-不执行最终操作.
												/**
												 * 发布还款公告.
												 */
												Notice notice = new Notice();
												notice.setId(IdGen.uuid());
												notice.setTitle("项目编号：" + wloanTermProject.getSn() + "付息公告！");
												notice.setText(NoticeContentPojo.createNoticeContent(REPAY_TYPE_1, wloanTermProject.getName(), wloanTermProject.getSn(), new DecimalFormat("0.00").format(repayTotalAmount)));
												notice.setCreateDate(new Date());
												notice.setUpdateDate(new Date());
												notice.setState(NOTICE_STATE_1);
												notice.setType(NOTICE_TYPE_2);
												int noticeFlag = noticeDao.insert(notice);
												if (noticeFlag == 1) {
													logger.info("最后一期付息公告发布成功......");
												} else {
													logger.info("最后一期付息公告发布失败......");
												}
											} else if (repayTheCapitalZtmgOrderInfo.getState().equals(ZtmgOrderInfoService.STATE_2)) { // 已还-可执行最终操作.
												/**
												 * 项目变更状态-已结束.
												 */
												// 最后一期还款，变更项目状态为-已结束.
												wloanTermProject.setState(WloanTermProjectService.FINISH); // 已完成(已结束).
												wloanTermProject.setUpdateDate(new Date()); // 更新时间.
												wloanTermProjectService.updateProState(wloanTermProject);
												logger.info("该标的还款完成-标的生命周期已结束......");
												/**
												 * 发布还款公告.
												 */
												Notice notice = new Notice();
												notice.setId(IdGen.uuid());
												notice.setTitle("项目编号：" + wloanTermProject.getSn() + "付息公告！");
												notice.setText(NoticeContentPojo.createNoticeContent(REPAY_TYPE_1, wloanTermProject.getName(), wloanTermProject.getSn(), new DecimalFormat("0.00").format(repayTotalAmount)));
												notice.setCreateDate(new Date());
												notice.setUpdateDate(new Date());
												notice.setState(NOTICE_STATE_1);
												notice.setType(NOTICE_TYPE_2);
												int noticeFlag = noticeDao.insert(notice);
												if (noticeFlag == 1) {
													logger.info("最后一期还本公告发布成功......");
												} else {
													logger.info("最后一期还本公告发布失败......");
												}
											}
										}
									} else { // 失败.
										logger.info("fn:repay-订单变更状态失败.");
									}
								} else {
									logger.info("fn:repay-项目不存在.");
								}
							} else {
								logger.info("fn:repay-该项目不存在.");
							}
						} else { // 正常付息逻辑.
							if (null != repayTheInterest.getWloanTermProject()) {
								WloanTermProject wloanTermProject = wloanTermProjectService.get(repayTheInterest.getWloanTermProject().getId());
								if (null != wloanTermProject) {// 项目详情.
									// 还款总额.
									Double repayTotalAmount = 0D;
									/**
									 * 出借人账户更新.
									 */
									// 客户还款计划查询封装.
									WloanTermUserPlan entity = new WloanTermUserPlan();
									entity.setState(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_2);
									entity.setWloanTermProject(wloanTermProject);
									entity.setRepaymentDate(repayTheInterest.getRepaymentDate());
									List<WloanTermUserPlan> userPlanList = wloanTermUserPlanDao.findList(entity);
									logger.info("出借人收回利息:{}笔......", userPlanList.size());
									long currentTimeMillis = System.currentTimeMillis();
									WloanTermInvest invest = null;
									UserInfo userInfo = null;
									for (WloanTermUserPlan userPlan : userPlanList) {
										currentTimeMillis = currentTimeMillis + 1000;
										invest = userPlan.getWloanTermInvest(); // 出借订单
										userInfo = userPlan.getUserInfo(); // 出借人帐号信息
										String userPlanId = userPlan.getId();
										// 付息总额.
										Double income = NumberUtils.scaleDouble(userPlan.getInterest());
										// 还款总额.
										repayTotalAmount = NumberUtils.scaleDouble(NumberUtils.add(repayTotalAmount, income));
										if (null != invest) { // 投资记录.
											CgbUserAccount userAccountInfo = null;
											if (null != userInfo) { // 出借人帐号.
												String userId = userInfo.getId();
												userAccountInfo = cgbUserAccountDao.getUserAccountInfo(userId);
												if (null != userAccountInfo) { // 出借人账户.
													UserAccountOrder uao = null;
													CgbUserTransDetail userTransDetail = null;
													String accountId = userAccountInfo.getId();
													// 可用余额+，定期代收收益-，定期总收益+.
													Double oldAvailableAmount = userAccountInfo.getAvailableAmount(); // 获取出借人账户可用余额
													int updateIncomeByIdFlag = cgbUserAccountDao.updateIncomeById(income, oldAvailableAmount, accountId);
													if (updateIncomeByIdFlag == 1) { // 账户更新成功
														// 平台流水
														userTransDetail = new CgbUserTransDetail();
														userTransDetail.setId(IdGen.uuid()); // 主键ID.
														userTransDetail.setTransId(userPlanId); // 付息外部子订单号.
														userTransDetail.setUserId(userId); // 客户帐号ID.
														userTransDetail.setAccountId(accountId); // 客户账户ID.
														userTransDetail.setTransDate(new Date(currentTimeMillis)); // 交易时间.
														userTransDetail.setTrustType(CgbUserTransDetailService.TRUST_TYPE_4); // 还利息.
														userTransDetail.setAmount(income); // 利息.
														oldAvailableAmount = NumberUtils.add(oldAvailableAmount, income);
														userTransDetail.setAvaliableAmount(oldAvailableAmount); // 当前可用余额.
														userTransDetail.setInOutType(CgbUserTransDetailService.IN_TYPE_1); // 收入.
														if (WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1.equals(repayTheInterest.getPrincipal())) {
															userTransDetail.setRemarks("最后一期还息"); // 备注信息.
														} else {
															userTransDetail.setRemarks("还息"); // 备注信息.
														}
														userTransDetail.setState(CgbUserTransDetailService.TRUST_STATE_2); // 流水状态，成功.
														int userTransDetailFlag = cgbUserTransDetailDao.insert(userTransDetail);
														if (userTransDetailFlag == 1) {
															logger.info("客户收回利息，平台流水插入成功......");
															// 客户还款计划，更新状态为已还款
															userPlan.setState(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_3);
															int modifyUserPlanStateFlag = wloanTermUserPlanDao.modifyWloanTermUserPlanState(userPlan);
															if (modifyUserPlanStateFlag == 1) { // 更新成功.
																logger.info("客户收回利息，客户还款计划状态-更新成功......");
																// 付息消息提醒.
																weixinSendTempMsgService.cgbSendRepayInfoMsg(userPlan, WeixinSendTempMsgService.IS_REPAY_TYPE_1);
															} else { // 失败.
																logger.info("客户收回利息，客户还款计划状态-更新失败......");
															}
														} else {
															logger.info("客户收回利息，平台流水插入失败......");
														}
													} else { // 账户更新失败，记录失败订单，定时轮询上账
														uao = new UserAccountOrder();
														uao.setId(IdGen.uuid());
														uao.setUserId(userId);
														uao.setAccountId(accountId);
														uao.setTransId(userPlanId);
														uao.setUserRole(UserRoleEnum.INVESTOR.getValue()); // 出借人
														uao.setBizType(BizTypeEnum.INCOME.getValue()); // 利息
														uao.setInOutType(InOutTypeEnum.IN.getValue()); // 收入
														uao.setAmount(income); // 交易金额
														uao.setStatus(StatusEnum.FAIL.getValue());
														uao.setCreateDate(new Date(currentTimeMillis));
														uao.setUpdateDate(new Date(currentTimeMillis));
														int insertAccountOrderFlag = userAccountOrderDao.insert(uao);
														logger.info("账户更新失败，创建账户交易订单:{}", insertAccountOrderFlag == 1 ? "成功" : "失败");
													}
												} else {
													logger.info("出借人账户不存在......");
												}
											} else {
												logger.info("出借人帐号不存在......");
											}
										} else {
											logger.info("该笔出借订单不存在，出借人付息订单号:{}", userPlan.getId());
										}
									}
									WloanSubject wloanSubject = wloanSubjectService.get(wloanTermProject.getSubjectId());
									logger.info("最后一期付息，还款总额:{}", NumberUtils.scaleDoubleStr(repayTotalAmount));
									if (null != wloanSubject) { // 融资主体.
										/**
										 * 借款人账户变更.
										 */
										CreditUserInfo creditUserInfo = creditUserInfoDao.get(wloanSubject.getLoanApplyId());
										if (null != creditUserInfo) { // 借款人帐号.
											CreditUserAccount creditUserAccount = creditUserAccountDao.get(creditUserInfo.getAccountId());
											if (null != creditUserAccount) { // 借款人账户.
												// 账户总额-.
												Double totalAmount = creditUserAccount.getTotalAmount();
												totalAmount = NumberUtils.scaleDouble(totalAmount - repayTotalAmount);
												creditUserAccount.setTotalAmount(totalAmount);
												// 可用余额-.
												Double availableAmount = creditUserAccount.getAvailableAmount();
												availableAmount = NumberUtils.scaleDouble(availableAmount - repayTotalAmount);
												creditUserAccount.setAvailableAmount(availableAmount);
												// 待还金额-.
												Double surplusAmount = creditUserAccount.getSurplusAmount();
												surplusAmount = NumberUtils.scaleDouble(surplusAmount - repayTotalAmount);
												creditUserAccount.setSurplusAmount(surplusAmount);
												// 已还金额+.
												Double repayAmount = creditUserAccount.getRepayAmount();
												repayAmount = NumberUtils.scaleDouble(repayAmount + repayTotalAmount);
												creditUserAccount.setRepayAmount(repayAmount);
												// 更新日期.
												creditUserAccount.setUpdateDate(new Date(currentTimeMillis));
												int creditUserAccountFlag = creditUserAccountDao.update(creditUserAccount);
												if (creditUserAccountFlag == 1) { // 借款人账户更新成功.
													logger.info("借款人账户更新成功......");
													/**
													 * 计入流水.
													 */
													CgbUserTransDetail userTransDetail = new CgbUserTransDetail();
													userTransDetail.setId(IdGen.uuid()); // 主键ID.
													userTransDetail.setTransId(orderId); // 付息主订单号.
													userTransDetail.setUserId(creditUserInfo.getId()); // 客户帐号ID.
													userTransDetail.setAccountId(creditUserAccount.getId()); // 客户账户ID.
													userTransDetail.setTransDate(new Date(currentTimeMillis)); // 交易时间.
													userTransDetail.setTrustType(CgbUserTransDetailService.TRUST_TYPE_4); // 还利息.
													userTransDetail.setAmount(repayTotalAmount); // 利息.
													userTransDetail.setAvaliableAmount(availableAmount); // 当前可用余额.
													userTransDetail.setInOutType(CgbUserTransDetailService.OUT_TYPE_2); // 支出.
													userTransDetail.setRemarks("借款人还息"); // 备注信息.
													userTransDetail.setState(CgbUserTransDetailService.TRUST_STATE_2); // 流水状态，成功.
													int userTransDetailFlag = cgbUserTransDetailDao.insert(userTransDetail);
													if (userTransDetailFlag == 1) { // 借款人账户变更，计入流水.
														logger.info("借款人还息计入流水成功......");
														// 标的还款计划状态更新
														repayTheInterest.setState(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_STATE_2);
														int modifyProjectPlanStateFlag = wloanTermProjectPlanDao.modifyWLoanTermProjectPlanState(repayTheInterest);
														if (modifyProjectPlanStateFlag == 1) { // 更新成功.
															logger.info("标的还款计划更新成功......");
														} else { // 失败.
															logger.info("标的还款计划更新失败......");
														}
													} else {
														logger.info("借款人还息计入流水失败......");
													}
												} else {
													logger.info("借款人账户更新失败......");
												}
											} else {
												logger.info("借款人账户不存在......");
											}
										} else {
											logger.info("借款人帐号不存在......");
										}
									} else {
										logger.info("融资主体不存在......");
									}

									/**
									 * 订单变更状态-已还.
									 */
									model.setState(ZtmgOrderInfoService.STATE_2);
									model.setUpdateDate(new Date());
									int flag = ztmgOrderInfoDao.update(model);
									if (flag == 1) { // 成功.
										logger.info("付息订单变更状态成功......");
										/**
										 * 发布还款公告.
										 */
										Notice notice = new Notice();
										notice.setId(IdGen.uuid());
										notice.setTitle("项目编号：" + wloanTermProject.getSn() + "付息公告！");
										notice.setText(NoticeContentPojo.createNoticeContent(REPAY_TYPE_1, wloanTermProject.getName(), wloanTermProject.getSn(), new DecimalFormat("0.00").format(repayTotalAmount)));
										notice.setCreateDate(new Date());
										notice.setUpdateDate(new Date());
										notice.setState(NOTICE_STATE_1);
										notice.setType(NOTICE_TYPE_2);
										int noticeFlag = noticeDao.insert(notice);
										if (noticeFlag == 1) {
											logger.info("付息公告发布成功......");
										} else {
											logger.info("付息公告发布失败......");
										}
									} else {
										logger.info("付息订单变更状态失败......");
									}
								} else {
									logger.info("标的不存在......");
								}
							} else {
								logger.info("标的不存在......");
							}
						}
					} else { // 还本.
						WloanTermProjectPlan repayTheCapital = wloanTermProjectPlanDao.getBySubOrderId(orderId);
						if (null != repayTheCapital) {
							if (WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1.equals(repayTheCapital.getPrincipal())) { // 还本付息
								/**
								 * 拿付息订单.
								 */
								String repayTheCapitalId = repayTheCapital.getId();
								ZtmgOrderInfo repayTheInterestOrderInfo = new ZtmgOrderInfo();
								repayTheInterestOrderInfo.setOrderId(repayTheCapitalId);
								ZtmgOrderInfo repayTheInterestZtmgOrderInfo = ztmgOrderInfoDao.findByOrderId(repayTheInterestOrderInfo);
								if (null != repayTheCapital.getWloanTermProject()) {
									WloanTermProject project = wloanTermProjectService.get(repayTheCapital.getWloanTermProject().getId());
									if (null != project) { // 项目详情.
										/**
										 * 出借人账户更新.
										 */
										// 客户还款计划查询封装.
										WloanTermUserPlan entity = new WloanTermUserPlan();
										entity.setState(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_2);
										entity.setWloanTermProject(project);
										entity.setRepaymentDate(repayTheCapital.getRepaymentDate());
										List<WloanTermUserPlan> userPlanList = wloanTermUserPlanDao.findList(entity);
										logger.info("出借人收回本金:{}笔......", userPlanList.size());
										long currentTimeMillis = System.currentTimeMillis();
										WloanTermInvest wloanTermInvest = null;
										UserInfo userInfo = null;
										for (WloanTermUserPlan userPlan : userPlanList) {
											currentTimeMillis = currentTimeMillis + 1000; 
											wloanTermInvest = userPlan.getWloanTermInvest();
											String userPlanId = userPlan.getId();
											if (null != wloanTermInvest) { // 投资记录.
												Double principal = wloanTermInvest.getAmount(); // 出借金额
												userInfo = userPlan.getUserInfo();
												if (null != userInfo) { // 出借人帐号.
													String userId = userInfo.getId();
													CgbUserAccount userAccountInfo = cgbUserAccountDao.getUserAccountInfo(userId);
													if (null != userAccountInfo) { // 出借人账户.
														UserAccountOrder uao = null;
														CgbUserTransDetail userTransDetail = null;
														String accountId = userAccountInfo.getId();
														// 可用余额+，待收本金-，定期交易.
														Double oldAvailableAmount = userAccountInfo.getAvailableAmount();
														int userAccountInfoFlag = cgbUserAccountDao.updatePrincipalById(principal, oldAvailableAmount, accountId);
														if (userAccountInfoFlag == 1) { // 出借人账户更新成功.
															logger.info("出借人账户更新成功......");
															// 出借人收回本金流水
															userTransDetail = new CgbUserTransDetail();
															userTransDetail.setId(IdGen.uuid()); // 主键ID.
															userTransDetail.setTransId(userPlanId); // 与付息共用一个还款订单号.
															userTransDetail.setUserId(userId); // 客户帐号ID.
															userTransDetail.setAccountId(userAccountInfo.getId()); // 客户账户ID.
															userTransDetail.setTransDate(new Date(currentTimeMillis)); // 交易时间.
															userTransDetail.setTrustType(CgbUserTransDetailService.TRUST_TYPE_5); // 还本金.
															userTransDetail.setAmount(principal); // 还本金.
															oldAvailableAmount = NumberUtils.add(oldAvailableAmount, principal);
															userTransDetail.setAvaliableAmount(oldAvailableAmount); // 当前可用余额.
															userTransDetail.setInOutType(CgbUserTransDetailService.IN_TYPE_1); // 收入.
															userTransDetail.setRemarks("最后一期还本"); // 备注信息.
															userTransDetail.setState(CgbUserTransDetailService.TRUST_STATE_2); // 流水状态，成功.
															int userTransDetailFlag = cgbUserTransDetailDao.insert(userTransDetail);
															if (userTransDetailFlag == 1) { // 计入流水成功.
																logger.info("出借人收回本金，插入流水成功......");
																// 判断付息订单是否已还
																if (null != repayTheInterestZtmgOrderInfo) {
																	if (ZtmgOrderInfoService.STATE_1.equals(repayTheInterestZtmgOrderInfo.getState())) { // 未还-不执行最终操作.
																		// 还本消息提醒.
																		weixinSendTempMsgService.cgbSendRepayInfoMsg(userPlan, WeixinSendTempMsgService.IS_REPAY_TYPE_2);
																	} else if (ZtmgOrderInfoService.STATE_2.equals(repayTheInterestZtmgOrderInfo.getState())) { // 已还-执行最终操作.
																		// 客户还款计划状态更新.
																		userPlan.setState(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_3);
																		int modifyUserPlanStateFlag = wloanTermUserPlanDao.modifyWloanTermUserPlanState(userPlan);
																		if (modifyUserPlanStateFlag == 1) { // 更新成功.
																			logger.info("客户收回本金，还款计划状态-更新成功......");
																			// 还本消息提醒.
																			weixinSendTempMsgService.cgbSendRepayInfoMsg(userPlan, WeixinSendTempMsgService.IS_REPAY_TYPE_2);
																		} else { // 失败.
																			logger.info("客户收回本金，还款计划状态-更新失败......");
																		}
																	}
																}
															} else {
																logger.info("出借人收回本金，插入流水失败......");
															}
														} else { // 账户更新失败，记录失败订单，定时轮询上账
															uao = new UserAccountOrder();
															uao.setId(IdGen.uuid());
															uao.setUserId(userId);
															uao.setAccountId(accountId);
															uao.setTransId(userPlanId);
															uao.setUserRole(UserRoleEnum.INVESTOR.getValue()); // 出借人
															uao.setBizType(BizTypeEnum.PRINCIPAL.getValue()); // 本金
															uao.setInOutType(InOutTypeEnum.IN.getValue()); // 收入
															uao.setAmount(principal); // 交易金额
															uao.setStatus(StatusEnum.FAIL.getValue());
															uao.setCreateDate(new Date(currentTimeMillis));
															uao.setUpdateDate(new Date(currentTimeMillis));
															int insertAccountOrderFlag = userAccountOrderDao.insert(uao);
															logger.info("账户更新失败，创建账户交易订单:{}", insertAccountOrderFlag == 1 ? "成功" : "失败");
														}
													} else {
														logger.info("fn:repay-出借人账户不存在.");
													}
												} else {
													logger.info("fn:repay-出借人帐号不存在.");
												}
											} else {
												logger.info("fn:repay-该笔投资记录不存在.");
											}
										}
										/**
										 * 借款人账户变更.
										 */
										// 实际筹集金额(也就是放款金额，同时也是本次借款人的还款金额).
										Double currentAmount = project.getCurrentRealAmount();
										logger.info("借款人还本金总额:{}", NumberUtils.scaleDouble(currentAmount));
										WloanSubject wloanSubject = wloanSubjectService.get(project.getSubjectId());
										if (null != wloanSubject) { // 融资主体.
											CreditUserInfo creditUserInfo = creditUserInfoDao.get(wloanSubject.getLoanApplyId());
											if (null != creditUserInfo) { // 借款人帐号.
												CreditUserAccount creditUserAccount = creditUserAccountDao.get(creditUserInfo.getAccountId());
												if (null != creditUserAccount) { // 借款人账户.
													// 账户总额-.
													Double totalAmount = creditUserAccount.getTotalAmount();
													totalAmount = NumberUtils.scaleDouble(totalAmount - currentAmount);
													creditUserAccount.setTotalAmount(totalAmount);
													// 可用余额-.
													Double availableAmount = creditUserAccount.getAvailableAmount();
													availableAmount = NumberUtils.scaleDouble(availableAmount - currentAmount);
													creditUserAccount.setAvailableAmount(availableAmount);
													// 待还金额-.
													Double surplusAmount = creditUserAccount.getSurplusAmount();
													surplusAmount = NumberUtils.scaleDouble(surplusAmount - currentAmount);
													creditUserAccount.setSurplusAmount(surplusAmount);
													// 已还金额+.
													Double repayAmount = creditUserAccount.getRepayAmount();
													repayAmount = NumberUtils.scaleDouble(repayAmount + currentAmount);
													creditUserAccount.setRepayAmount(repayAmount);
													// 更新日期.
													creditUserAccount.setUpdateDate(new Date(currentTimeMillis));
													int creditUserAccountFlag = creditUserAccountDao.update(creditUserAccount);
													CgbUserTransDetail userTransDetail = null;
													if (creditUserAccountFlag == 1) { // 借款人账户更新成功.
														logger.info("借款人账户更新成功......");
														// 借款人还本流水
														userTransDetail = new CgbUserTransDetail();
														userTransDetail.setId(IdGen.uuid()); // 主键ID.
														userTransDetail.setTransId(orderId); // 还本主订单号.
														userTransDetail.setUserId(creditUserInfo.getId()); // 客户帐号ID.
														userTransDetail.setAccountId(creditUserAccount.getId()); // 客户账户ID.
														userTransDetail.setTransDate(new Date(currentTimeMillis)); // 交易时间.
														userTransDetail.setTrustType(CgbUserTransDetailService.TRUST_TYPE_5); // 还本金.
														userTransDetail.setAmount(currentAmount); // 还本.
														userTransDetail.setAvaliableAmount(availableAmount); // 当前可用余额.
														userTransDetail.setInOutType(CgbUserTransDetailService.OUT_TYPE_2); // 支出.
														userTransDetail.setRemarks("借款人还本"); // 备注信息.
														userTransDetail.setState(CgbUserTransDetailService.TRUST_STATE_2); // 流水状态，成功.
														int userTransDetailFlag = cgbUserTransDetailDao.insert(userTransDetail);
														if (userTransDetailFlag == 1) { // 借款人账户变更，计入流水.
															logger.info("借款人还本流水插入成功......");
															// 标的还款计划更新
															repayTheCapital.setState(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_STATE_2);
															int modifyProjectPlanStateFlag = wloanTermProjectPlanDao.modifyWLoanTermProjectPlanState(repayTheCapital);
															if (modifyProjectPlanStateFlag == 1) { // 更新成功.
																logger.info("标的还本付息，还款计划状态-更新成功......");
															} else { // 失败.
																logger.info("标的还本付息，还款计划状态-更新失败......");
															}
														} else {
															logger.info("借款人还本流水插入失败......");
														}
													} else { // 失败.
														logger.info("借款人账户更新失败......");
													}
												}
											} else {
												logger.info("借款人帐号不存在......");
											}
										} else {
											logger.info("融资主体不存在......");
										}

										/**
										 * 订单变更状态-已还.
										 */
										model.setState(ZtmgOrderInfoService.STATE_2);
										model.setUpdateDate(new Date());
										int flag = ztmgOrderInfoDao.update(model);
										if (flag == 1) { // 成功.
											logger.info("借款人还本订单变更状态成功......");
											if (null != repayTheInterestZtmgOrderInfo) {
												if (repayTheInterestZtmgOrderInfo.getState().equals(ZtmgOrderInfoService.STATE_1)) { // 未还-不执行最终操作.
													/**
													 * 发布还款公告.
													 */
													Notice notice = new Notice();
													notice.setId(IdGen.uuid());
													notice.setTitle("项目编号：" + project.getSn() + "还本公告！");
													notice.setText(NoticeContentPojo.createNoticeContent(REPAY_TYPE_2, project.getName(), project.getSn(), new DecimalFormat("0.00").format(currentAmount)));
													notice.setCreateDate(new Date());
													notice.setUpdateDate(new Date());
													notice.setState(NOTICE_STATE_1);
													notice.setType(NOTICE_TYPE_2);
													int noticeFlag = noticeDao.insert(notice);
													if (noticeFlag == 1) {
														logger.info("借款人还本公告发布成功......");
													} else {
														logger.info("借款人还本公告发布失败......");
													}
												} else if (repayTheInterestZtmgOrderInfo.getState().equals(ZtmgOrderInfoService.STATE_2)) { // 已还-可执行最终操作.
													/**
													 * 项目变更状态-已结束.
													 */
													// 最后一期还款，变更项目状态为-已结束.
													project.setState(WloanTermProjectService.FINISH); // 已完成(已结束).
													project.setUpdateDate(new Date()); // 更新时间.
													wloanTermProjectService.updateProState(project);
													logger.info("fn:repay-该项目还款完成-项目（已结束）.");
													/**
													 * 发布还款公告.
													 */
													Notice notice = new Notice();
													notice.setId(IdGen.uuid());
													notice.setTitle("项目编号：" + project.getSn() + "还本公告！");
													notice.setText(NoticeContentPojo.createNoticeContent(REPAY_TYPE_2, project.getName(), project.getSn(), new DecimalFormat("0.00").format(currentAmount)));
													notice.setCreateDate(new Date());
													notice.setUpdateDate(new Date());
													notice.setState(NOTICE_STATE_1);
													notice.setType(NOTICE_TYPE_2);
													int noticeFlag = noticeDao.insert(notice);
													if (noticeFlag == 1) {
														logger.info("借款人还本公告发布成功......");
													} else {
														logger.info("借款人还本公告发布失败......");
													}
												}
											}
										} else { // 失败.
											logger.info("借款人还本订单变更状态失败......");
										}
									} else {
										logger.info("该标的不存在......");
									}
								}
							}
						} else {
							logger.info("该标的还款计划不存在......");
						}
					}
				} else {
					logger.info("失败的订单，请联系开发人员查找原因......");
				}
				logger.info("借款户还款...end...");
			} else if (model.getType().equals(ZtmgOrderInfoService.TYPE_2)) { // 代偿户.
				logger.info("代偿户间接代偿...start...");
				// 还款成功.
				if (model.getStatus().equals(ZtmgOrderInfoService.STATUS_S)) {
					/**
					 * 获取该批次的项目还款计划id/subOrderId.
					 */
					// 付息.
					WloanTermProjectPlan repayTheInterest = wloanTermProjectPlanDao.get(orderId);
					if (null != repayTheInterest) { // 还息.
						if (WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1.equals(repayTheInterest.getPrincipal())) { // 最后一期还本付息.
							// 获取还本订单
							String subOrderId = repayTheInterest.getSubOrderId();
							ZtmgOrderInfo subZtmgOrderInfo = new ZtmgOrderInfo();
							subZtmgOrderInfo.setOrderId(subOrderId);
							ZtmgOrderInfo repayTheCapitalZtmgOrderInfo = ztmgOrderInfoDao.findByOrderId(subZtmgOrderInfo);

							if (null != repayTheInterest.getWloanTermProject()) {
								WloanTermProject wloanTermProject = wloanTermProjectService.get(repayTheInterest.getWloanTermProject().getId());
								if (null != wloanTermProject) {// 项目详情.
									// 还款总额.
									Double repayTotalAmount = 0D;
									/**
									 * 出借人账户更新.
									 */
									// 客户还款计划查询封装.
									WloanTermUserPlan entity = new WloanTermUserPlan();
									entity.setState(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_2);
									entity.setWloanTermProject(wloanTermProject);
									entity.setRepaymentDate(repayTheInterest.getRepaymentDate());
									List<WloanTermUserPlan> userPlanList = wloanTermUserPlanDao.findList(entity);
									logger.info("出借人收回利息:{}笔......", userPlanList.size());
									long currentTimeMillis = System.currentTimeMillis();
									WloanTermInvest invest = null;
									UserInfo userInfo = null;
									for (WloanTermUserPlan userPlan : userPlanList) {
										currentTimeMillis = currentTimeMillis + 1000;
										invest = userPlan.getWloanTermInvest(); // 出借订单
										userInfo = userPlan.getUserInfo(); // 出借人帐号信息
										String userPlanId = userPlan.getId();
										// 还本付息总额.
										Double repayAmount = userPlan.getInterest();
										// 出借金额.
										Double investAmount = wloanTermInvestDao.findInvestAmountByProjectAndUser(wloanTermProject.getId(), userInfo != null ? userInfo.getId() : "", invest != null ? invest.getId() : "");
										// 付息.
										Double income = NumberUtils.scaleDouble(NumberUtils.subtract(repayAmount, investAmount));
										// 还款总额.
										repayTotalAmount = NumberUtils.scaleDouble(NumberUtils.add(repayTotalAmount, income));
										if (null != invest) { // 出借订单
											CgbUserAccount userAccountInfo = null;
											if (null != userInfo) { // 出借人帐号.
												String userId = userInfo.getId();
												userAccountInfo = cgbUserAccountDao.getUserAccountInfo(userId);
												if (null != userAccountInfo) { // 出借人账户.
													UserAccountOrder uao = null;
													CgbUserTransDetail userTransDetail = null;
													String accountId = userAccountInfo.getId();
													// 可用余额+，定期代收收益-，定期总收益+.
													Double oldAvailableAmount = userAccountInfo.getAvailableAmount(); // 获取出借人账户可用余额
													int updateIncomeByIdFlag = cgbUserAccountDao.updateIncomeById(income, oldAvailableAmount, accountId);
													if (updateIncomeByIdFlag == 1) { // 出借人账户更新成功.
														logger.info("fn:replaceRepay-出借人账户更新成功.");
														//
														userTransDetail = new CgbUserTransDetail();
														userTransDetail.setId(IdGen.uuid()); // 主键ID.
														userTransDetail.setTransId(userPlanId); // 付息外部子订单号.
														userTransDetail.setUserId(userId); // 客户帐号ID.
														userTransDetail.setAccountId(accountId); // 客户账户ID.
														userTransDetail.setTransDate(new Date(currentTimeMillis)); // 交易时间.
														userTransDetail.setTrustType(CgbUserTransDetailService.TRUST_TYPE_4); // 还利息.
														userTransDetail.setAmount(income); // 利息.
														oldAvailableAmount = NumberUtils.add(oldAvailableAmount, income);
														userTransDetail.setAvaliableAmount(oldAvailableAmount); // 当前可用余额.
														userTransDetail.setInOutType(CgbUserTransDetailService.IN_TYPE_1); // 收入.
														if (WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1.equals(repayTheInterest.getPrincipal())) {
															userTransDetail.setRemarks("最后一期还息"); // 备注信息.
														} else {
															userTransDetail.setRemarks("还息"); // 备注信息.
														}
														userTransDetail.setState(CgbUserTransDetailService.TRUST_STATE_2); // 流水状态，成功.
														int userTransDetailFlag = cgbUserTransDetailDao.insert(userTransDetail);
														if (userTransDetailFlag == 1) { // 计入流水成功.
															logger.info("客户收回利息，平台流水插入成功......");
															// 还本订单
															if (null != repayTheCapitalZtmgOrderInfo) { // 还本订单
																if (ZtmgOrderInfoService.STATE_1.equals(repayTheCapitalZtmgOrderInfo.getState())) {
																	// 付息消息提醒.
																	weixinSendTempMsgService.cgbSendRepayInfoMsg(userPlan, WeixinSendTempMsgService.IS_REPAY_TYPE_1);
																} else if (ZtmgOrderInfoService.STATE_2.equals(repayTheCapitalZtmgOrderInfo.getState())) {
																	// 客户还款计划状态更改为已还款.
																	userPlan.setState(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_3);
																	int modifyUserPlanStateFlag = wloanTermUserPlanDao.modifyWloanTermUserPlanState(userPlan);
																	if (modifyUserPlanStateFlag == 1) { // 更新成功.
																		logger.info("客户还款计划状态-更新成功......");
																		// 付息消息提醒.
																		weixinSendTempMsgService.cgbSendRepayInfoMsg(userPlan, WeixinSendTempMsgService.IS_REPAY_TYPE_1);
																	} else { // 失败.
																		logger.info("客户还款计划状态-更新失败......");
																	}
																}
															}
														} else {
															logger.info("客户收回利息，平台流水插入失败......");
														}
													} else { // 账户更新失败，记录失败订单，定时轮询上账
														uao = new UserAccountOrder();
														uao.setId(IdGen.uuid());
														uao.setUserId(userId);
														uao.setAccountId(accountId);
														uao.setTransId(userPlanId);
														uao.setUserRole(UserRoleEnum.INVESTOR.getValue()); // 出借人
														uao.setBizType(BizTypeEnum.INCOME.getValue()); // 利息
														uao.setInOutType(InOutTypeEnum.IN.getValue()); // 收入
														uao.setAmount(income); // 交易金额
														uao.setStatus(StatusEnum.FAIL.getValue());
														uao.setCreateDate(new Date(currentTimeMillis));
														uao.setUpdateDate(new Date(currentTimeMillis));
														int insertAccountOrderFlag = userAccountOrderDao.insert(uao);
														logger.info("账户更新失败，创建账户交易订单:{}", insertAccountOrderFlag == 1 ? "成功" : "失败");
													}
												} else {
													logger.info("出借人账户不存在......");
												}
											} else {
												logger.info("出借人帐号不存在......");
											}
										} else {
											logger.info("该笔出借订单不存在，出借人付息订单号:{}", userPlanId);
										}
									}

									// 还款总额.
									logger.info("最后一期付息，还款总额:{}", NumberUtils.scaleDoubleStr(repayTotalAmount));

									// 代偿人账户变更
									CreditUserInfo creditUserInfo = creditUserInfoDao.get(wloanTermProject.getReplaceRepayId());
									if (null != creditUserInfo) { // 代偿人帐号.
										CreditUserAccount creditUserAccount = creditUserAccountDao.get(creditUserInfo.getAccountId());
										if (null != creditUserAccount) { // 代偿人账户.
											// 账户总额-.
											Double totalAmount = creditUserAccount.getTotalAmount();
											totalAmount = NumberUtils.scaleDouble(totalAmount - repayTotalAmount);
											creditUserAccount.setTotalAmount(totalAmount);
											// 可用余额-.
											Double availableAmount = creditUserAccount.getAvailableAmount();
											availableAmount = NumberUtils.scaleDouble(availableAmount - repayTotalAmount);
											creditUserAccount.setAvailableAmount(availableAmount);
											// 已还金额+.
											Double repayAmount = creditUserAccount.getRepayAmount();
											repayAmount = NumberUtils.scaleDouble(repayAmount + repayTotalAmount);
											creditUserAccount.setRepayAmount(repayAmount);
											// 更新日期.
											creditUserAccount.setUpdateDate(new Date());
											int creditUserAccountFlag = creditUserAccountDao.update(creditUserAccount);
											if (creditUserAccountFlag == 1) { // 代偿人账户更新成功.
												logger.info("fn:replaceRepay-代偿人账户更新成功.");
												/**
												 * 计入流水-代偿还款（间接代偿）.
												 */
												CgbUserTransDetail userTransDetail = new CgbUserTransDetail();
												userTransDetail.setId(IdGen.uuid()); // 主键ID.
												userTransDetail.setTransId(orderId); // 付息主订单号.
												userTransDetail.setUserId(creditUserInfo.getId()); // 客户帐号ID.
												userTransDetail.setAccountId(creditUserAccount.getId()); // 客户账户ID.
												userTransDetail.setTransDate(new Date(currentTimeMillis)); // 交易时间.
												userTransDetail.setTrustType(CgbUserTransDetailService.TRUST_TYPE_13); // 代偿还款.
												userTransDetail.setAmount(repayTotalAmount); // 利息.
												userTransDetail.setAvaliableAmount(availableAmount); // 当前可用余额.
												userTransDetail.setInOutType(CgbUserTransDetailService.OUT_TYPE_2); // 支出.
												userTransDetail.setRemarks("代偿还款"); // 备注信息.
												userTransDetail.setState(CgbUserTransDetailService.TRUST_STATE_2); // 流水状态，成功.
												int userTransDetailFlag = cgbUserTransDetailDao.insert(userTransDetail);
												if (userTransDetailFlag == 1) { // 代偿人账户变更，计入流水.
													logger.info("代偿人最后一期付息流水插入成功......");
												} else {
													logger.info("代偿人最后一期付息流水插入失败......");
												}
											} else { // 失败.
												logger.info("代偿人账户更新失败......");
											}
										} else {
											logger.info("代偿人账户不存在......");
										}
									} else {
										logger.info("代偿人帐号不存在......");
									}

									// 借款人账户变更
									WloanSubject wloanSubject = wloanSubjectService.get(wloanTermProject.getSubjectId());
									if (null != wloanSubject) { // 融资主体.
										CreditUserInfo borrowingUserInfo = creditUserInfoDao.get(wloanSubject.getLoanApplyId());
										if (null != borrowingUserInfo) { // 借款人帐号信息.
											CreditUserAccount borrowingUserAccount = creditUserAccountDao.get(borrowingUserInfo.getAccountId());
											if (null != borrowingUserAccount) { // 借款人账户信息.
												// 待还金额-(待还金额 - 还款金额).
												Double oldSurplusAmount = borrowingUserAccount.getSurplusAmount();
												borrowingUserAccount.setSurplusAmount(NumberUtils.scaleDouble(oldSurplusAmount - repayTotalAmount));
												// 已还金额+(已还金额 + 还款金额).
												Double repayAmount = borrowingUserAccount.getRepayAmount();
												borrowingUserAccount.setRepayAmount(NumberUtils.scaleDouble(repayAmount + repayTotalAmount));
												int borrowingUserAccountFlag = creditUserAccountDao.update(borrowingUserAccount);
												if (borrowingUserAccountFlag == 1) { // 借款人账户更新.
													logger.info("借款户账户变更成功......");
													/**
													 * 1.交易类型-代偿还款-流水.
													 */
													CgbUserTransDetail userTransDetail_I = new CgbUserTransDetail();
													userTransDetail_I.setId(IdGen.uuid()); // 主键ID.
													userTransDetail_I.setTransId(orderId); // 付息主订单号.
													userTransDetail_I.setUserId(borrowingUserInfo.getId()); // 客户帐号ID.
													userTransDetail_I.setAccountId(borrowingUserAccount.getId()); // 客户账户ID.
													userTransDetail_I.setTransDate(new Date(currentTimeMillis)); // 交易时间.
													userTransDetail_I.setTrustType(CgbUserTransDetailService.TRUST_TYPE_13); // 代偿还款.
													userTransDetail_I.setAmount(repayTotalAmount); // 利息.
													// 当前可用余额+代偿金额.
													userTransDetail_I.setAvaliableAmount(NumberUtils.scaleDouble(borrowingUserAccount.getAvailableAmount() + repayTotalAmount));
													userTransDetail_I.setInOutType(CgbUserTransDetailService.IN_TYPE_1); // 收入.
													userTransDetail_I.setRemarks("代偿还款"); // 备注信息.
													userTransDetail_I.setState(CgbUserTransDetailService.TRUST_STATE_2); // 流水状态，成功.
													int userTransDetail_I_Flag = cgbUserTransDetailDao.insert(userTransDetail_I);
													if (userTransDetail_I_Flag == 1) {
														logger.info("代偿人间接代偿借款人最后一期付息流水插入成功......");
													} else {
														logger.info("代偿人间接代偿借款人最后一期付息流水插入失败......");
													}
													/**
													 * 2.交易类型-还本/付息-流水.
													 */
													currentTimeMillis = currentTimeMillis + 1000;
													CgbUserTransDetail userTransDetail = new CgbUserTransDetail();
													userTransDetail.setId(IdGen.uuid()); // 主键ID.
													userTransDetail.setTransId(orderId); // 付息主订单号.
													userTransDetail.setUserId(borrowingUserInfo.getId()); // 客户帐号ID.
													userTransDetail.setAccountId(borrowingUserInfo.getId()); // 客户账户ID.
													userTransDetail.setTransDate(new Date(currentTimeMillis)); // 交易时间.
													userTransDetail.setTrustType(CgbUserTransDetailService.TRUST_TYPE_4); // 还利息.
													userTransDetail.setAmount(repayTotalAmount); // 利息.
													userTransDetail.setAvaliableAmount(NumberUtils.scaleDouble(borrowingUserAccount.getAvailableAmount())); // 当前可用余额.
													userTransDetail.setInOutType(CgbUserTransDetailService.OUT_TYPE_2); // 支出.
													userTransDetail.setRemarks("借款人还息"); // 备注信息.
													userTransDetail.setState(CgbUserTransDetailService.TRUST_STATE_2); // 流水状态，成功.
													int userTransDetailFlag = cgbUserTransDetailDao.insert(userTransDetail);
													if (userTransDetailFlag == 1) {
														logger.info("借款人最后一期付息流水插入成功......");
													} else {
														logger.info("借款人最后一期付息流水插入失败......");
													}
												} else {
													logger.info("借款户账户变更失败......");
												}
											}
										} else {
											logger.info("借款人账户不存在......");
										}
									} else {
										logger.info("借款人帐号不存在......");
									}

									/**
									 * 订单变更状态-已还.
									 */
									model.setState(ZtmgOrderInfoService.STATE_2);
									model.setUpdateDate(new Date());
									int flag = ztmgOrderInfoDao.update(model);
									if (flag == 1) { // 成功.
										logger.info("间接代偿，最后一期付息订单变更状态成功......");

										if (null != repayTheCapitalZtmgOrderInfo) {
											if (ZtmgOrderInfoService.STATE_1.equals(repayTheCapitalZtmgOrderInfo.getState())) {
												/**
												 * 发布还款公告.
												 */
												Notice notice = new Notice();
												notice.setId(IdGen.uuid());
												notice.setTitle("项目编号：" + wloanTermProject.getSn() + "付息公告！");
												notice.setText(NoticeContentPojo.createNoticeContent(REPAY_TYPE_1, wloanTermProject.getName(), wloanTermProject.getSn(), new DecimalFormat("0.00").format(repayTotalAmount)));
												notice.setCreateDate(new Date());
												notice.setUpdateDate(new Date());
												notice.setState(NOTICE_STATE_1);
												notice.setType(NOTICE_TYPE_2);
												int noticeFlag = noticeDao.insert(notice);
												if (noticeFlag == 1) {
													logger.info("间接代偿，最后一期付息公告发布成功......");
												} else {
													logger.info("间接代偿，最后一期付息公告发布失败......");
												}
											} else if (ZtmgOrderInfoService.STATE_2.equals(repayTheCapitalZtmgOrderInfo.getState())) {
												/**
												 * 项目变更状态-已结束.
												 */
												// 最后一期还款，变更项目状态为-已结束.
												wloanTermProject.setState(WloanTermProjectService.FINISH); // 已完成(已结束).
												wloanTermProject.setUpdateDate(new Date()); // 更新时间.
												wloanTermProjectService.updateProState(wloanTermProject);
												logger.info("间接代偿-该标的生命周期（已结束）......");
												/**
												 * 发布还款公告.
												 */
												Notice notice = new Notice();
												notice.setId(IdGen.uuid());
												notice.setTitle("项目编号：" + wloanTermProject.getSn() + "付息公告！");
												notice.setText(NoticeContentPojo.createNoticeContent(REPAY_TYPE_1, wloanTermProject.getName(), wloanTermProject.getSn(), new DecimalFormat("0.00").format(repayTotalAmount)));
												notice.setCreateDate(new Date());
												notice.setUpdateDate(new Date());
												notice.setState(NOTICE_STATE_1);
												notice.setType(NOTICE_TYPE_2);
												int noticeFlag = noticeDao.insert(notice);
												if (noticeFlag == 1) {
													logger.info("间接代偿，最后一期付息公告发布成功......");
												} else {
													logger.info("间接代偿，最后一期付息公告发布失败......");
												}
											}
										}
									} else { // 失败.
										logger.info("间接代偿，最后一期付息订单变更状态失败......");
									}
								} else {
									logger.info("该标的不存在......");
								}
							} else {
								logger.info("该标的不存在......");
							}
						} else { // 正常付息逻辑.
							if (null != repayTheInterest.getWloanTermProject()) {
								WloanTermProject wloanTermProject = wloanTermProjectService.get(repayTheInterest.getWloanTermProject().getId());
								if (null != wloanTermProject) {// 项目详情.
									// 还款总额.
									Double repayTotalAmount = 0D;
									/**
									 * 出借人账户更新.
									 */
									// 客户还款计划查询封装.
									WloanTermUserPlan entity = new WloanTermUserPlan();
									entity.setState(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_2);
									entity.setWloanTermProject(wloanTermProject);
									entity.setRepaymentDate(repayTheInterest.getRepaymentDate());
									List<WloanTermUserPlan> userPlanList = wloanTermUserPlanDao.findList(entity);
									logger.info("出借人收回利息:{}笔......", userPlanList.size());
									long currentTimeMillis = System.currentTimeMillis();
									WloanTermInvest invest = null;
									UserInfo userInfo = null;
									for (WloanTermUserPlan userPlan : userPlanList) {
										currentTimeMillis = currentTimeMillis + 1000;
										invest = userPlan.getWloanTermInvest(); // 出借订单
										userInfo = userPlan.getUserInfo(); // 出借人帐号信息
										String userPlanId = userPlan.getId();
										// 付息总额.
										Double income = NumberUtils.scaleDouble(userPlan.getInterest());
										// 还款总额.
										repayTotalAmount = NumberUtils.scaleDouble(NumberUtils.add(repayTotalAmount, income));
										if (null != invest) { // 出借订单
											userInfo = userPlan.getUserInfo();
											if (null != userInfo) { // 出借人帐号.
												String userId = userInfo.getId();
												CgbUserAccount userAccountInfo = cgbUserAccountDao.getUserAccountInfo(userId);
												if (null != userAccountInfo) { // 出借人账户.
													UserAccountOrder uao = null;
													CgbUserTransDetail userTransDetail = null;
													String accountId = userAccountInfo.getId();
													// 可用余额+，定期代收收益-，定期总收益+.
													Double oldAvailableAmount = userAccountInfo.getAvailableAmount(); // 获取出借人账户可用余额
													int updateIncomeByIdFlag = cgbUserAccountDao.updateIncomeById(income, oldAvailableAmount, accountId);
													if (updateIncomeByIdFlag == 1) { // 出借人账户更新成功.
														logger.info("出借人账户更新成功......");
														userTransDetail = new CgbUserTransDetail();
														userTransDetail.setId(IdGen.uuid()); // 主键ID.
														userTransDetail.setTransId(userPlanId); // 付息外部子订单号.
														userTransDetail.setUserId(userId); // 客户帐号ID.
														userTransDetail.setAccountId(accountId); // 客户账户ID.
														userTransDetail.setTransDate(new Date(currentTimeMillis)); // 交易时间.
														userTransDetail.setTrustType(CgbUserTransDetailService.TRUST_TYPE_4); // 还利息.
														userTransDetail.setAmount(income); // 利息.
														oldAvailableAmount = NumberUtils.add(oldAvailableAmount, income);
														userTransDetail.setAvaliableAmount(oldAvailableAmount); // 当前可用余额.
														userTransDetail.setInOutType(CgbUserTransDetailService.IN_TYPE_1); // 收入.
														if (WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1.equals(repayTheInterest.getPrincipal())) {
															userTransDetail.setRemarks("最后一期还息"); // 备注信息.
														} else {
															userTransDetail.setRemarks("还息"); // 备注信息.
														}
														userTransDetail.setState(CgbUserTransDetailService.TRUST_STATE_2); // 流水状态，成功.
														int userTransDetailFlag = cgbUserTransDetailDao.insert(userTransDetail);
														if (userTransDetailFlag == 1) { // 计入流水成功.
															logger.info("出借人收回利息插入流水成功......");
															// 客户还款计划，状态变更为已还款
															userPlan.setState(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_3);
															int modifyUserPlanStateFlag = wloanTermUserPlanDao.modifyWloanTermUserPlanState(userPlan);
															if (modifyUserPlanStateFlag == 1) { // 更新成功.
																logger.info("客户还款计划状态-更新成功......");
																// 付息消息提醒.
																weixinSendTempMsgService.cgbSendRepayInfoMsg(userPlan, WeixinSendTempMsgService.IS_REPAY_TYPE_1);
															} else { // 失败.
																logger.info("客户还款计划状态-更新失败......");
															}
														} else {
															logger.info("出借人收回利息插入流水失败......");
														}
													} else { // 账户更新失败，记录失败订单，定时轮询上账
														uao = new UserAccountOrder();
														uao.setId(IdGen.uuid());
														uao.setUserId(userId);
														uao.setAccountId(accountId);
														uao.setTransId(userPlanId);
														uao.setUserRole(UserRoleEnum.INVESTOR.getValue()); // 出借人
														uao.setBizType(BizTypeEnum.INCOME.getValue()); // 利息
														uao.setInOutType(InOutTypeEnum.IN.getValue()); // 收入
														uao.setAmount(income); // 交易金额
														uao.setStatus(StatusEnum.FAIL.getValue());
														uao.setCreateDate(new Date(currentTimeMillis));
														uao.setUpdateDate(new Date(currentTimeMillis));
														int insertAccountOrderFlag = userAccountOrderDao.insert(uao);
														logger.info("账户更新失败，创建账户交易订单:{}", insertAccountOrderFlag == 1 ? "成功" : "失败");
													}
												} else {
													logger.info("出借人账户不存在......");
												}
											} else {
												logger.info("出借人帐号不存在......");
											}
										} else {
											logger.info("该笔出借订单不存在，出借人付息订单号:{}", userPlan.getId());
										}
									}

									// 还款总额.
									logger.info("最后一期付息，还款总额:{}", NumberUtils.scaleDoubleStr(repayTotalAmount));

									// 代偿人账户变更.
									CreditUserInfo creditUserInfo = creditUserInfoDao.get(wloanTermProject.getReplaceRepayId());
									if (null != creditUserInfo) { // 代偿人帐号.
										CreditUserAccount creditUserAccount = creditUserAccountDao.get(creditUserInfo.getAccountId());
										if (null != creditUserAccount) { // 代偿人账户.
											// 账户总额-.
											Double totalAmount = creditUserAccount.getTotalAmount();
											totalAmount = NumberUtils.scaleDouble(totalAmount - repayTotalAmount);
											creditUserAccount.setTotalAmount(totalAmount);
											// 可用余额-.
											Double availableAmount = creditUserAccount.getAvailableAmount();
											availableAmount = NumberUtils.scaleDouble(availableAmount - repayTotalAmount);
											creditUserAccount.setAvailableAmount(availableAmount);
											// 已还金额+.
											Double repayAmount = creditUserAccount.getRepayAmount();
											repayAmount = NumberUtils.scaleDouble(repayAmount + repayTotalAmount);
											creditUserAccount.setRepayAmount(repayAmount);
											// 更新日期.
											creditUserAccount.setUpdateDate(new Date());
											int creditUserAccountFlag = creditUserAccountDao.update(creditUserAccount);
											if (creditUserAccountFlag == 1) { // 代偿人账户更新成功.
												logger.info("间接代偿付息，代偿人账户更新成功......");
												// 代偿人付息计入流水
												CgbUserTransDetail userTransDetail = new CgbUserTransDetail();
												userTransDetail.setId(IdGen.uuid()); // 主键ID.
												userTransDetail.setTransId(orderId); // 付息主订单号.
												userTransDetail.setUserId(creditUserInfo.getId()); // 客户帐号ID.
												userTransDetail.setAccountId(creditUserAccount.getId()); // 客户账户ID.
												userTransDetail.setTransDate(new Date(currentTimeMillis)); // 交易时间.
												userTransDetail.setTrustType(CgbUserTransDetailService.TRUST_TYPE_13); // 代偿还款.
												userTransDetail.setAmount(repayTotalAmount); // 利息.
												userTransDetail.setAvaliableAmount(availableAmount); // 当前可用余额.
												userTransDetail.setInOutType(CgbUserTransDetailService.OUT_TYPE_2); // 支出.
												userTransDetail.setRemarks("代偿还款"); // 备注信息.
												userTransDetail.setState(CgbUserTransDetailService.TRUST_STATE_2); // 流水状态，成功.
												int userTransDetailFlag = cgbUserTransDetailDao.insert(userTransDetail);
												if (userTransDetailFlag == 1) { // 代偿人账户变更，计入流水.
													logger.info("代偿人间接代偿付息流水插入成功......");
													/**
													 * 项目还款计划状态更新.
													 */
													repayTheInterest.setState(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_STATE_2);
													int modifyProjectPlanStateFlag = wloanTermProjectPlanDao.modifyWLoanTermProjectPlanState(repayTheInterest);
													if (modifyProjectPlanStateFlag == 1) { // 更新成功.
														logger.info("该期间接代偿，付息标的还款计划已还款，更新成功......");
													} else { // 失败.
														logger.info("该期间接代偿，付息标的还款计划已还款，更新失败......");
													}
												} else {
													logger.info("代偿人间接代偿付息流水插入失败......");
												}
											} else { // 失败.
												logger.info("间接代偿付息，代偿人账户更新失败......");
											}
										} else {
											logger.info("代偿人账户不存在......");
										}
									} else {
										logger.info("代偿人帐号不存在......");
									}

									// 借款人账户变更
									WloanSubject wloanSubject = wloanSubjectService.get(wloanTermProject.getSubjectId());
									if (null != wloanSubject) { // 融资主体.
										CreditUserInfo borrowingUserInfo = creditUserInfoDao.get(wloanSubject.getLoanApplyId());
										if (null != borrowingUserInfo) { // 借款人帐号信息.
											CreditUserAccount borrowingUserAccount = creditUserAccountDao.get(borrowingUserInfo.getAccountId());
											if (null != borrowingUserAccount) { // 借款人账户信息.
												// 待还金额-(待还金额 - 还款金额).
												Double oldSurplusAmount = borrowingUserAccount.getSurplusAmount();
												borrowingUserAccount.setSurplusAmount(NumberUtils.scaleDouble(oldSurplusAmount - repayTotalAmount));
												// 已还金额+(已还金额 + 还款金额).
												Double repayAmount = borrowingUserAccount.getRepayAmount();
												borrowingUserAccount.setRepayAmount(NumberUtils.scaleDouble(repayAmount + repayTotalAmount));
												int borrowingUserAccountFlag = creditUserAccountDao.update(borrowingUserAccount);
												if (borrowingUserAccountFlag == 1) { // 借款人账户更新.
													logger.info("间接代偿，借款人付息，账户变更成功......");
													/**
													 * 1.交易类型-代偿还款-流水.
													 */
													CgbUserTransDetail userTransDetail_I = new CgbUserTransDetail();
													userTransDetail_I.setId(IdGen.uuid()); // 主键ID.
													userTransDetail_I.setTransId(orderId); // 付息主订单号.
													userTransDetail_I.setUserId(borrowingUserInfo.getId()); // 客户帐号ID.
													userTransDetail_I.setAccountId(borrowingUserAccount.getId()); // 客户账户ID.
													userTransDetail_I.setTransDate(new Date(currentTimeMillis)); // 交易时间.
													userTransDetail_I.setTrustType(CgbUserTransDetailService.TRUST_TYPE_13); // 代偿还款.
													userTransDetail_I.setAmount(repayTotalAmount); // 利息.
													// 当前可用余额+代偿金额.
													userTransDetail_I.setAvaliableAmount(NumberUtils.scaleDouble(borrowingUserAccount.getAvailableAmount() + repayTotalAmount));
													userTransDetail_I.setInOutType(CgbUserTransDetailService.IN_TYPE_1); // 收入.
													userTransDetail_I.setRemarks("代偿还款"); // 备注信息.
													userTransDetail_I.setState(CgbUserTransDetailService.TRUST_STATE_2); // 流水状态，成功.
													int userTransDetail_I_Flag = cgbUserTransDetailDao.insert(userTransDetail_I);
													if (userTransDetail_I_Flag == 1) {
														logger.info("间接代偿，代偿人代偿还款借款人付息流水插入成功......");
													} else {
														logger.info("间接代偿，代偿人代偿还款借款人付息流水插入失败......");
													}
													/**
													 * 2.交易类型-还本/付息-流水.
													 */
													currentTimeMillis = currentTimeMillis + 1000;
													CgbUserTransDetail userTransDetail = new CgbUserTransDetail();
													userTransDetail.setId(IdGen.uuid()); // 主键ID.
													userTransDetail.setTransId(orderId); // 付息主订单号.
													userTransDetail.setUserId(borrowingUserInfo.getId()); // 客户帐号ID.
													userTransDetail.setAccountId(borrowingUserInfo.getId()); // 客户账户ID.
													userTransDetail.setTransDate(new Date(currentTimeMillis)); // 交易时间.
													userTransDetail.setTrustType(CgbUserTransDetailService.TRUST_TYPE_4); // 还利息.
													userTransDetail.setAmount(repayTotalAmount); // 利息.
													userTransDetail.setAvaliableAmount(NumberUtils.scaleDouble(borrowingUserAccount.getAvailableAmount())); // 当前可用余额.
													userTransDetail.setInOutType(CgbUserTransDetailService.OUT_TYPE_2); // 支出.
													userTransDetail.setRemarks("借款人还息"); // 备注信息.
													userTransDetail.setState(CgbUserTransDetailService.TRUST_STATE_2); // 流水状态，成功.
													int userTransDetailFlag = cgbUserTransDetailDao.insert(userTransDetail);
													if (userTransDetailFlag == 1) {
														logger.info("间接代偿，借款人付息流水插入成功......");
													} else {
														logger.info("间接代偿，借款人付息流水插入失败......");
													}
												} else {
													logger.info("间接代偿，借款人付息，账户变更失败......");
												}
											}
										} else {
											logger.info("借款人帐号不存在......");
										}
									} else {
										logger.info("借款人账户不存在......");
									}

									/**
									 * 订单变更状态-已还.
									 */
									model.setState(ZtmgOrderInfoService.STATE_2);
									model.setUpdateDate(new Date());
									int flag = ztmgOrderInfoDao.update(model);
									if (flag == 1) { // 成功.
										logger.info("间接代偿，付息订单变更状态成功......");
										/**
										 * 发布还款公告.
										 */
										Notice notice = new Notice();
										notice.setId(IdGen.uuid());
										notice.setTitle("项目编号：" + wloanTermProject.getSn() + "付息公告！");
										notice.setText(NoticeContentPojo.createNoticeContent(REPAY_TYPE_1, wloanTermProject.getName(), wloanTermProject.getSn(), new DecimalFormat("0.00").format(repayTotalAmount)));
										notice.setCreateDate(new Date());
										notice.setUpdateDate(new Date());
										notice.setState(NOTICE_STATE_1);
										notice.setType(NOTICE_TYPE_2);
										int noticeFlag = noticeDao.insert(notice);
										if (noticeFlag == 1) {
											logger.info("间接代偿，付息公告发布成功......");
										} else {
											logger.info("间接代偿，付息公告发布失败......");
										}
									} else { // 失败.
										logger.info("间接代偿，付息订单变更状态失败......");
									}
								} else {
									logger.info("该标的不存在......");
								}
							} else {
								logger.info("该标的不存在......");
							}
						}
					} else { // 还本.
						WloanTermProjectPlan repayTheCapital = wloanTermProjectPlanDao.getBySubOrderId(orderId);
						if (null != repayTheCapital) {
							if (WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1.equals(repayTheCapital.getPrincipal())) {

								// 获取付息订单
								String repayTheCapitalId = repayTheCapital.getId();
								ZtmgOrderInfo repayTheInterestOrderInfo = new ZtmgOrderInfo();
								repayTheInterestOrderInfo.setOrderId(repayTheCapitalId);
								ZtmgOrderInfo repayTheInterestZtmgOrderInfo = ztmgOrderInfoDao.findByOrderId(repayTheInterestOrderInfo);

								if (null != repayTheCapital.getWloanTermProject()) {
									WloanTermProject project = wloanTermProjectService.get(repayTheCapital.getWloanTermProject().getId());
									if (null != project) { // 项目详情.
										/**
										 * 出借人账户更新.
										 */
										// 客户还款计划查询封装.
										WloanTermUserPlan entity = new WloanTermUserPlan();
										entity.setState(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_2);
										entity.setWloanTermProject(project);
										entity.setRepaymentDate(repayTheCapital.getRepaymentDate());
										List<WloanTermUserPlan> userPlanList = wloanTermUserPlanDao.findList(entity);
										logger.info("出借人收回本金:{}笔......", userPlanList.size());
										long currentTimeMillis = System.currentTimeMillis();
										WloanTermInvest wloanTermInvest = null;
										UserInfo userInfo = null;
										for (WloanTermUserPlan userPlan : userPlanList) {
											currentTimeMillis = currentTimeMillis + 1000;
											wloanTermInvest = userPlan.getWloanTermInvest();
											String userPlanId = userPlan.getId();
											if (null != wloanTermInvest) { // 投资记录.
												Double principal = wloanTermInvest.getAmount(); // 出借金额
												userInfo = userPlan.getUserInfo();
												if (null != userInfo) { // 出借人帐号.
													String userId = userInfo.getId();
													CgbUserAccount userAccountInfo = cgbUserAccountDao.getUserAccountInfo(userId);
													if (null != userAccountInfo) { // 出借人账户.
														UserAccountOrder uao = null;
														CgbUserTransDetail userTransDetail = null;
														String accountId = userAccountInfo.getId();
														// 可用余额+，待收本金-，定期交易.
														Double oldAvailableAmount = userAccountInfo.getAvailableAmount();
														int userAccountInfoFlag = cgbUserAccountDao.updatePrincipalById(principal, oldAvailableAmount, accountId);
														if (userAccountInfoFlag == 1) { // 出借人账户更新成功.
															logger.info("出借人账户更新成功......");
															// 出借人收回本金流水
															userTransDetail = new CgbUserTransDetail();
															userTransDetail.setId(IdGen.uuid()); // 主键ID.
															userTransDetail.setTransId(userPlanId); // 还本外部子订单号.
															userTransDetail.setUserId(userId); // 客户帐号ID.
															userTransDetail.setAccountId(accountId); // 客户账户ID.
															userTransDetail.setTransDate(new Date(currentTimeMillis)); // 交易时间.
															userTransDetail.setTrustType(CgbUserTransDetailService.TRUST_TYPE_5); // 还本金.
															userTransDetail.setAmount(principal); // 还本金.
															oldAvailableAmount = NumberUtils.add(oldAvailableAmount, principal);
															userTransDetail.setAvaliableAmount(oldAvailableAmount); // 当前可用余额.
															userTransDetail.setInOutType(CgbUserTransDetailService.IN_TYPE_1); // 收入.
															userTransDetail.setRemarks("最后一期还本"); // 备注信息.
															userTransDetail.setState(CgbUserTransDetailService.TRUST_STATE_2); // 流水状态，成功.
															int userTransDetailFlag = cgbUserTransDetailDao.insert(userTransDetail);
															if (userTransDetailFlag == 1) { // 计入流水成功.
																logger.info("出借人收回本金，插入流水成功......");
																if (null != repayTheInterestZtmgOrderInfo) {
																	if (ZtmgOrderInfoService.STATE_1.equals(repayTheInterestZtmgOrderInfo.getState())) { // 未还-不执行最终操作.
																		// 还本消息提醒.
																		weixinSendTempMsgService.cgbSendRepayInfoMsg(userPlan, WeixinSendTempMsgService.IS_REPAY_TYPE_2);
																	} else if (ZtmgOrderInfoService.STATE_2.equals(repayTheInterestZtmgOrderInfo.getState())) { // 已还-执行最终操作.
																		// 客户还款计划状态更新.
																		userPlan.setState(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_3);
																		int modifyUserPlanStateFlag = wloanTermUserPlanDao.modifyWloanTermUserPlanState(userPlan);
																		if (modifyUserPlanStateFlag == 1) { // 更新成功.
																			logger.info("客户收回本金，还款计划状态-更新成功......");
																			// 还本消息提醒.
																			weixinSendTempMsgService.cgbSendRepayInfoMsg(userPlan, WeixinSendTempMsgService.IS_REPAY_TYPE_2);
																		} else { // 失败.
																			logger.info("客户收回本金，还款计划状态-更新失败......");
																		}
																	}
																}
															} else {
																logger.info("出借人收回本金，插入流水失败......");
															}
														} else { // 账户更新失败，记录失败订单，定时轮询上账
															uao = new UserAccountOrder();
															uao.setId(IdGen.uuid());
															uao.setUserId(userId);
															uao.setAccountId(accountId);
															uao.setTransId(userPlanId);
															uao.setUserRole(UserRoleEnum.INVESTOR.getValue()); // 出借人
															uao.setBizType(BizTypeEnum.PRINCIPAL.getValue()); // 本金
															uao.setInOutType(InOutTypeEnum.IN.getValue()); // 收入
															uao.setAmount(principal); // 交易金额
															uao.setStatus(StatusEnum.FAIL.getValue());
															uao.setCreateDate(new Date(currentTimeMillis));
															uao.setUpdateDate(new Date(currentTimeMillis));
															int insertAccountOrderFlag = userAccountOrderDao.insert(uao);
															logger.info("账户更新失败，创建账户交易订单:{}", insertAccountOrderFlag == 1 ? "成功" : "失败");
														}
													} else {
														logger.info("出借人账户不存在......");
													}
												} else {
													logger.info("出借人帐号不存在......");
												}
											} else {
												logger.info("该笔出借订单不存在，还本客户还款计划订单号:{}", userPlanId);
											}
										}

										// 实际筹集金额(也就是放款金额，同时也是本次借款人的还款金额).
										Double currentAmount = project.getCurrentRealAmount();
										logger.info("间接代偿，借款人还本金总额:{}", NumberUtils.scaleDouble(currentAmount));

										/**
										 * 代偿人账户变更.
										 */
										CreditUserInfo creditUserInfo = creditUserInfoDao.get(project.getReplaceRepayId());
										if (null != creditUserInfo) { // 代偿人帐号.
											CreditUserAccount creditUserAccount = creditUserAccountDao.get(creditUserInfo.getAccountId());
											if (null != creditUserAccount) { // 代偿人账户.
												// 账户总额-.
												Double totalAmount = creditUserAccount.getTotalAmount();
												totalAmount = NumberUtils.scaleDouble(totalAmount - currentAmount);
												creditUserAccount.setTotalAmount(totalAmount);
												// 可用余额-.
												Double availableAmount = creditUserAccount.getAvailableAmount();
												availableAmount = NumberUtils.scaleDouble(availableAmount - currentAmount);
												creditUserAccount.setAvailableAmount(availableAmount);
												// 已还金额+.
												Double repayAmount = creditUserAccount.getRepayAmount();
												repayAmount = NumberUtils.scaleDouble(repayAmount + currentAmount);
												creditUserAccount.setRepayAmount(repayAmount);
												// 更新日期.
												creditUserAccount.setUpdateDate(new Date());
												int creditUserAccountFlag = creditUserAccountDao.update(creditUserAccount);
												if (creditUserAccountFlag == 1) { // 代偿人账户更新成功.
													logger.info("间接代偿，代偿人账户更新成功......");
													/**
													 * 计入流水.
													 */
													CgbUserTransDetail userTransDetail = new CgbUserTransDetail();
													userTransDetail.setId(IdGen.uuid()); // 主键ID.
													userTransDetail.setTransId(orderId); // 还本主订单号.
													userTransDetail.setUserId(creditUserInfo.getId()); // 客户帐号ID.
													userTransDetail.setAccountId(creditUserAccount.getId()); // 客户账户ID.
													userTransDetail.setTransDate(new Date(currentTimeMillis)); // 交易时间.
													userTransDetail.setTrustType(CgbUserTransDetailService.TRUST_TYPE_13); // 代偿还款.
													userTransDetail.setAmount(currentAmount); // 还本.
													userTransDetail.setAvaliableAmount(availableAmount); // 当前可用余额.
													userTransDetail.setInOutType(CgbUserTransDetailService.OUT_TYPE_2); // 支出.
													userTransDetail.setRemarks("代偿还款"); // 备注信息.
													userTransDetail.setState(CgbUserTransDetailService.TRUST_STATE_2); // 流水状态，成功.
													int userTransDetailFlag = cgbUserTransDetailDao.insert(userTransDetail);
													if (userTransDetailFlag == 1) { // 代偿人账户变更，计入流水.
														logger.info("间接代偿-代偿人代偿还款流水插入成功......");
														/**
														 * 项目还款计划状态更新.
														 */
														repayTheCapital.setState(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_STATE_2);
														int modifyProjectPlanStateFlag = wloanTermProjectPlanDao.modifyWLoanTermProjectPlanState(repayTheCapital);
														if (modifyProjectPlanStateFlag == 1) { // 更新成功.
															logger.info("该标的还款计划状态-更新成功......");
														} else { // 失败.
															logger.info("该标的还款计划状态-更新失败......");
														}
													} else {
														logger.info("间接代偿-代偿人代偿还款流水插入失败......");
													}
												} else { // 失败.
													logger.info("间接代偿，代偿人账户更新失败......");
												}
											}
										} else {
											logger.info("代偿人帐号不存在......");
										}

										/**
										 * 借款人账户变更.
										 */
										WloanSubject wloanSubject = wloanSubjectService.get(project.getSubjectId());
										if (null != wloanSubject) { // 融资主体.
											CreditUserInfo borrowingUserInfo = creditUserInfoDao.get(wloanSubject.getLoanApplyId());
											if (null != borrowingUserInfo) { // 借款人帐号信息.
												CreditUserAccount borrowingUserAccount = creditUserAccountDao.get(borrowingUserInfo.getAccountId());
												if (null != borrowingUserAccount) { // 借款人账户信息.
													// 待还金额-(待还金额 - 还款金额).
													Double oldSurplusAmount = borrowingUserAccount.getSurplusAmount();
													borrowingUserAccount.setSurplusAmount(NumberUtils.scaleDouble(oldSurplusAmount - currentAmount));
													// 已还金额+(已还金额 + 还款金额).
													Double repayAmount = borrowingUserAccount.getRepayAmount();
													borrowingUserAccount.setRepayAmount(NumberUtils.scaleDouble(repayAmount + currentAmount));
													int borrowingUserAccountFlag = creditUserAccountDao.update(borrowingUserAccount);
													if (borrowingUserAccountFlag == 1) { // 借款人账户更新.
														logger.info("借款户账户变更成功......");
														/**
														 * 1.交易类型-代偿还款-流水.
														 */
														CgbUserTransDetail userTransDetail_I = new CgbUserTransDetail();
														userTransDetail_I.setId(IdGen.uuid()); // 主键ID.
														userTransDetail_I.setTransId(orderId); // 付息主订单号.
														userTransDetail_I.setUserId(borrowingUserInfo.getId()); // 客户帐号ID.
														userTransDetail_I.setAccountId(borrowingUserAccount.getId()); // 客户账户ID.
														userTransDetail_I.setTransDate(new Date(currentTimeMillis)); // 交易时间.
														userTransDetail_I.setTrustType(CgbUserTransDetailService.TRUST_TYPE_13); // 代偿还款.
														userTransDetail_I.setAmount(currentAmount); // 本金.
														// 当前可用余额+代偿金额.
														userTransDetail_I.setAvaliableAmount(NumberUtils.scaleDouble(borrowingUserAccount.getAvailableAmount() + currentAmount));
														userTransDetail_I.setInOutType(CgbUserTransDetailService.IN_TYPE_1); // 收入.
														userTransDetail_I.setRemarks("代偿还款"); // 备注信息.
														userTransDetail_I.setState(CgbUserTransDetailService.TRUST_STATE_2); // 流水状态，成功.
														int userTransDetail_I_Flag = cgbUserTransDetailDao.insert(userTransDetail_I);
														if (userTransDetail_I_Flag == 1) {
															logger.info("间接代偿-借款人代偿还款流水计入成功......");
														} else {
															logger.info("间接代偿-借款人代偿还款流水计入失败......");
														}
														/**
														 * 2.交易类型-还本/付息-流水.
														 */
														currentTimeMillis = currentTimeMillis + 1000;
														CgbUserTransDetail userTransDetail = new CgbUserTransDetail();
														userTransDetail.setId(IdGen.uuid()); // 主键ID.
														userTransDetail.setTransId(orderId); // 付息主订单号.
														userTransDetail.setUserId(borrowingUserInfo.getId()); // 客户帐号ID.
														userTransDetail.setAccountId(borrowingUserInfo.getId()); // 客户账户ID.
														userTransDetail.setTransDate(new Date(currentTimeMillis)); // 交易时间.
														userTransDetail.setTrustType(CgbUserTransDetailService.TRUST_TYPE_5); // 还本.
														userTransDetail.setAmount(currentAmount); // 本金.
														userTransDetail.setAvaliableAmount(NumberUtils.scaleDouble(borrowingUserAccount.getAvailableAmount())); // 当前可用余额.
														userTransDetail.setInOutType(CgbUserTransDetailService.OUT_TYPE_2); // 支出.
														userTransDetail.setRemarks("借款人还本"); // 备注信息.
														userTransDetail.setState(CgbUserTransDetailService.TRUST_STATE_2); // 流水状态，成功.
														int userTransDetailFlag = cgbUserTransDetailDao.insert(userTransDetail);
														if (userTransDetailFlag == 1) {
															logger.info("间接代偿-借款人还本流水计入成功......");
														} else {
															logger.info("间接代偿-借款人还本流水计入失败......");
														}
													} else {
														logger.info("借款户账户变更失败......");
													}
												} else {
													logger.info("借款人账户不存在......");
												}
											} else {
												logger.info("借款人帐号不存在......");
											}
										} else {
											logger.info("该标的融资主体不存在......");
										}

										/**
										 * 订单变更状态-已还.
										 */
										model.setState(ZtmgOrderInfoService.STATE_2);
										model.setUpdateDate(new Date());
										int flag = ztmgOrderInfoDao.update(model);
										if (flag == 1) { // 成功.
											logger.info("间接代偿-还本订单变更状态成功......");
											if (null != repayTheInterestZtmgOrderInfo) {
												// 非NUll执行.
												if (ZtmgOrderInfoService.STATE_1.equals(repayTheInterestZtmgOrderInfo.getState())) { // 未还-不执行最终操作.
													/**
													 * 发布还款公告.
													 */
													Notice notice = new Notice();
													notice.setId(IdGen.uuid());
													notice.setTitle("项目编号：" + project.getSn() + "还本公告！");
													notice.setText(NoticeContentPojo.createNoticeContent(REPAY_TYPE_2, project.getName(), project.getSn(), new DecimalFormat("0.00").format(currentAmount)));
													notice.setCreateDate(new Date());
													notice.setUpdateDate(new Date());
													notice.setState(NOTICE_STATE_1);
													notice.setType(NOTICE_TYPE_2);
													int noticeFlag = noticeDao.insert(notice);
													if (noticeFlag == 1) {
														logger.info("间接代偿，还本公告发布成功......");
													} else {
														logger.info("间接代偿，还本公告发布失败......");
													}
												} else if (ZtmgOrderInfoService.STATE_2.equals(repayTheInterestZtmgOrderInfo.getState())) { // 已还-可执行最终操作.
													// 最后一期还款，变更项目状态为-已结束.
													project.setState(WloanTermProjectService.FINISH); // 已完成(已结束).
													project.setUpdateDate(new Date()); // 更新时间.
													wloanTermProjectService.updateProState(project);
													logger.info("fn:replaceRepay-该项目还款完成-项目（已结束）.");
													/**
													 * 发布还款公告.
													 */
													Notice notice = new Notice();
													notice.setId(IdGen.uuid());
													notice.setTitle("项目编号：" + project.getSn() + "还本公告！");
													notice.setText(NoticeContentPojo.createNoticeContent(REPAY_TYPE_2, project.getName(), project.getSn(), new DecimalFormat("0.00").format(currentAmount)));
													notice.setCreateDate(new Date());
													notice.setUpdateDate(new Date());
													notice.setState(NOTICE_STATE_1);
													notice.setType(NOTICE_TYPE_2);
													int noticeFlag = noticeDao.insert(notice);
													if (noticeFlag == 1) {
														logger.info("间接代偿，还本公告发布成功......");
													} else {
														logger.info("间接代偿，还本公告发布失败......");
													}
												}
											}
										} else { // 失败.
											logger.info("间接代偿-还本订单变更状态失败......");
										}
									} else {
										logger.info("该标的不存在......");
									}
								}
							}
						} else {
							logger.info("该标的还款计划不存在......");
						}
					}
				} else {
					logger.info("失败的订单，请联系开发人员查找原因......");
				}
				logger.info("代偿户间接代偿...end...");
			}
			logger.info("平台自动还款交易结束...end...");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("平台自动还款交易结束...end...");
		}
	}
}
