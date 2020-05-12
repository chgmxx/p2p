package com.power.platform.task;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
import com.power.platform.plan.dao.WloanTermProjectPlanDao;
import com.power.platform.plan.dao.WloanTermUserPlanDao;
import com.power.platform.plan.entity.WloanTermProjectPlan;
import com.power.platform.plan.entity.WloanTermUserPlan;
import com.power.platform.plan.service.WloanTermProjectPlanService;
import com.power.platform.plan.service.WloanTermUserPlanService;
import com.power.platform.regular.dao.WloanTermInvestDao;
import com.power.platform.regular.entity.WloanSubject;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.regular.service.WloanSubjectService;
import com.power.platform.regular.service.WloanTermProjectService;
import com.power.platform.task.pojo.NoticeContentPojo;
import com.power.platform.trandetail.dao.UserTransDetailDao;
import com.power.platform.userinfo.dao.UserAccountInfoDao;
import com.power.platform.userinfo.dao.UserInfoDao;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.weixin.service.WeixinSendTempMsgService;

@Service
@Lazy(false)
public class ProjectRepayPlanTask {

	private static final Logger log = Logger.getLogger(ProjectRepayPlanTask.class);

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
	@Resource
	private CreditUserApplyDao creditUserApplyDao;
	@Resource
	private WloanTermInvestDao wloanTermInvestDao;
	@Resource
	private AVouchersDicDao aVouchersDicDao;
	@Resource
	private AUserAwardsHistoryDao aUserAwardsHistoryDao;
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
	private NoticeDao noticeDao;

	/**
	 * 
	 * 方法: callbackRepayPlan <br>
	 * 描述: 还款（借款户还款/代偿户还款）. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2018年2月6日 下午6:11:43
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void callbackRepayPlan() {

		try {
			/**
			 * 封装未还订单查询.
			 */
			ZtmgOrderInfo ztmgOrderInfo = new ZtmgOrderInfo();
			ztmgOrderInfo.setState(ZtmgOrderInfoService.STATE_1);
			List<ZtmgOrderInfo> list = ztmgOrderInfoDao.findList(ztmgOrderInfo);

			// 没有待还项目计划.
			if (list != null && list.size() == 0) {
				log.info(this.getClass() + "，没有待还项目计划.");
				return; // 结束当前方法.
			}

			// 当前订单的实例.
			ZtmgOrderInfo model = list.get(0); // 每次取订单列表的第一条，执行还款操作.
			// 订单号.
			String orderId = model.getOrderId();
			/**
			 * 判断是借款户还款/代偿户还款.
			 */
			if (model.getType().equals(ZtmgOrderInfoService.TYPE_1)) { // 借款户.
				log.info(this.getClass() + "，repay start.");
				if (model.getStatus().equals(ZtmgOrderInfoService.STATUS_S)) {
					/**
					 * 获取该批次的项目还款计划id/subOrderId.
					 */
					// 付息.
					WloanTermProjectPlan repayTheInterest = wloanTermProjectPlanDao.get(orderId);
					if (null != repayTheInterest) { // 还息.
						if (WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1.equals(repayTheInterest.getPrincipal())) { // 最后一期还本付息.

							/**
							 * 拿还本订单.
							 */
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
									log.info("fn:repay-出借人还款笔数：" + userPlanList.size() + "笔.");
									int a = 0;
									for (WloanTermUserPlan userPlan : userPlanList) {
										// 还本付息总额.
										Double repayAmount = userPlan.getInterest();
										// 投资金额.
										Double investAmount = wloanTermInvestDao.findInvestAmountByProjectAndUser(wloanTermProject.getId(), userPlan.getUserInfo().getId(), userPlan.getWloanTermInvest().getId());
										// 还息.
										Double repayInterest = repayAmount - investAmount;
										// 还款总额.
										repayTotalAmount = repayTotalAmount + repayInterest;
										if (null != userPlan.getWloanTermInvest()) { // 投资记录.
											UserInfo userInfo = userPlan.getUserInfo();
											if (null != userInfo) { // 出借人帐号.
												CgbUserAccount userAccountInfo = cgbUserAccountDao.getUserAccountInfo(userInfo.getId());
												if (null != userAccountInfo) { // 出借人账户.
													// 可用余额+.
													Double availableAmount = userAccountInfo.getAvailableAmount();
													availableAmount = NumberUtils.scaleDouble(availableAmount + repayInterest);
													userAccountInfo.setAvailableAmount(availableAmount);
													// 定期代收收益-.
													Double regularDueInterest = userAccountInfo.getRegularDueInterest();
													regularDueInterest = NumberUtils.scaleDouble(regularDueInterest - repayInterest);
													userAccountInfo.setRegularDueInterest(regularDueInterest);
													// 定期总收益+.
													Double regularTotalInterest = userAccountInfo.getRegularTotalInterest();
													regularTotalInterest = NumberUtils.scaleDouble(regularTotalInterest + repayInterest);
													userAccountInfo.setRegularTotalInterest(regularTotalInterest);
													int userAccountInfoFlag = cgbUserAccountDao.update(userAccountInfo);
													if (userAccountInfoFlag == 1) { // 出借人账户更新成功.
														log.info("fn:repay-出借人账户更新成功.");
														/**
														 * 计入流水.
														 */
														CgbUserTransDetail userTransDetail = new CgbUserTransDetail();
														userTransDetail.setId(IdGen.uuid()); // 主键ID.
														userTransDetail.setTransId(userPlan.getId()); // 付息外部子订单号.
														userTransDetail.setUserId(userInfo.getId()); // 客户帐号ID.
														userTransDetail.setAccountId(userAccountInfo.getId()); // 客户账户ID.
														userTransDetail.setTransDate(new Date(System.currentTimeMillis() + 1000 * ++a)); // 交易时间.
														userTransDetail.setTrustType(CgbUserTransDetailService.TRUST_TYPE_4); // 还利息.
														userTransDetail.setAmount(repayInterest); // 利息.
														userTransDetail.setAvaliableAmount(availableAmount); // 当前可用余额.
														userTransDetail.setInOutType(CgbUserTransDetailService.IN_TYPE_1); // 收入.
														if (WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1.equals(repayTheInterest.getPrincipal())) {
															userTransDetail.setRemarks("最后一期还息"); // 备注信息.
														} else {
															userTransDetail.setRemarks("还息"); // 备注信息.
														}
														userTransDetail.setState(CgbUserTransDetailService.TRUST_STATE_2); // 流水状态，成功.
														int userTransDetailFlag = cgbUserTransDetailDao.insert(userTransDetail);
														if (userTransDetailFlag == 1) { // 计入流水成功.
															log.info("fn:repay-还息计入流水成功.");
															/**
															 * 判断还本订单是否已还.
															 */
															if (null == repayTheCapitalZtmgOrderInfo) { // NULL不执行.
															} else {
																if (repayTheCapitalZtmgOrderInfo.getState().equals(ZtmgOrderInfoService.STATE_1)) { // 未还-不执行最终操作.
																	// 付息消息提醒.
																	weixinSendTempMsgService.cgbSendRepayInfoMsg(userPlan, WeixinSendTempMsgService.IS_REPAY_TYPE_1);
																} else if (repayTheCapitalZtmgOrderInfo.getState().equals(ZtmgOrderInfoService.STATE_2)) { // 已还-执行最终操作.
																	/**
																	 * 客户还款计划，
																	 * 状态更改为已还款.
																	 */
																	userPlan.setState(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_3);
																	int modifyUserPlanStateFlag = wloanTermUserPlanDao.modifyWloanTermUserPlanState(userPlan);
																	if (modifyUserPlanStateFlag == 1) { // 更新成功.
																		log.info("fn:repay-客户还款计划状态-更新成功.");
																		// 付息消息提醒.
																		weixinSendTempMsgService.cgbSendRepayInfoMsg(userPlan, WeixinSendTempMsgService.IS_REPAY_TYPE_1);
																	} else { // 失败.
																		log.info("fn:repay-客户还款计划状态-更新失败.");
																	}
																}
															}
														} else {
															log.info("fn:repay-还息计入流水失败.");
														}
													} else {
														log.info("fn:repay-出借人账户更新 失败.");
													}
												} else {
													log.info("fn:repay-出借人账户不存在.");
												}
											} else {
												log.info("fn:repay-出借人帐号不存在.");
											}
										} else {
											log.info("fn:repay-该投资记录不存在.");
										}
									}
									WloanSubject wloanSubject = wloanSubjectService.get(wloanTermProject.getSubjectId());
									// 还款总额.
									log.info("fn:repay-还款总额：" + repayTotalAmount);
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
												creditUserAccount.setUpdateDate(new Date());
												int creditUserAccountFlag = creditUserAccountDao.update(creditUserAccount);
												if (creditUserAccountFlag == 1) { // 借款人账户更新成功.
													log.info("fn:repay-借款人账户更新成功.");
													/**
													 * 计入流水.
													 */
													CgbUserTransDetail userTransDetail = new CgbUserTransDetail();
													userTransDetail.setId(IdGen.uuid()); // 主键ID.
													userTransDetail.setTransId(orderId); // 付息主订单号.
													userTransDetail.setUserId(creditUserInfo.getId()); // 客户帐号ID.
													userTransDetail.setAccountId(creditUserAccount.getId()); // 客户账户ID.
													userTransDetail.setTransDate(new Date(System.currentTimeMillis() + 1000 * ++a)); // 交易时间.
													userTransDetail.setTrustType(CgbUserTransDetailService.TRUST_TYPE_4); // 还利息.
													userTransDetail.setAmount(repayTotalAmount); // 利息.
													userTransDetail.setAvaliableAmount(availableAmount); // 当前可用余额.
													userTransDetail.setInOutType(CgbUserTransDetailService.OUT_TYPE_2); // 支出.
													userTransDetail.setRemarks("借款人还息"); // 备注信息.
													userTransDetail.setState(CgbUserTransDetailService.TRUST_STATE_2); // 流水状态，成功.
													int userTransDetailFlag = cgbUserTransDetailDao.insert(userTransDetail);
													if (userTransDetailFlag == 1) { // 借款人账户变更，计入流水.
														log.info("fn:repay-借款人还息计入流水成功.");
													} else {
														log.info("fn:repay-借款人还息计入流水失败.");
													}
												} else { // 失败.
													log.info("fn:repay-借款人账户更新失败.");
												}
											} else {
												log.info("fn:repay-借款人账户不存在.");
											}
										} else {
											log.info("fn:repay-借款人帐号不存在.");
										}
									} else {
										log.info("fn:repay-融资主体不存在.");
									}

									/**
									 * 订单变更状态-已还.
									 */
									model.setState(ZtmgOrderInfoService.STATE_2);
									model.setUpdateDate(new Date());
									int flag = ztmgOrderInfoDao.update(model);
									if (flag == 1) { // 成功.
										log.info("fn:repay-订单变更状态成功.");
										if (null == repayTheCapitalZtmgOrderInfo) { // NULL不执行.
										} else { // 非NUll执行.
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
													log.info(this.getClass() + "：发布公告成功");
												} else {
													log.info(this.getClass() + "：发布公告失败");
												}
											} else if (repayTheCapitalZtmgOrderInfo.getState().equals(ZtmgOrderInfoService.STATE_2)) { // 已还-可执行最终操作.
												/**
												 * 项目变更状态-已结束.
												 */
												// 最后一期还款，变更项目状态为-已结束.
												wloanTermProject.setState(WloanTermProjectService.FINISH); // 已完成(已结束).
												wloanTermProject.setUpdateDate(new Date()); // 更新时间.
												wloanTermProjectService.updateProState(wloanTermProject);
												log.info("fn:repay-该项目还款完成-项目（已结束）.");
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
													log.info(this.getClass() + "：发布公告成功");
												} else {
													log.info(this.getClass() + "：发布公告失败");
												}
											}
										}
									} else { // 失败.
										log.info("fn:repay-订单变更状态失败.");
									}
								} else {
									log.info("fn:repay-项目不存在.");
								}
							} else {
								log.info("fn:repay-该项目不存在.");
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
									log.info("fn:repay-出借人还款笔数：" + userPlanList.size() + "笔.");
									int b = 0;
									for (WloanTermUserPlan userPlan : userPlanList) {
										// 还款总额.
										Double repayAmount = userPlan.getInterest();
										repayTotalAmount = repayTotalAmount + repayAmount;
										if (null != userPlan.getWloanTermInvest()) { // 投资记录.
											UserInfo userInfo = userPlan.getUserInfo();
											if (null != userInfo) { // 出借人帐号.
												CgbUserAccount userAccountInfo = cgbUserAccountDao.getUserAccountInfo(userInfo.getId());
												if (null != userAccountInfo) { // 出借人账户.
													// 可用余额+.
													Double availableAmount = userAccountInfo.getAvailableAmount();
													availableAmount = NumberUtils.scaleDouble(availableAmount + repayAmount);
													userAccountInfo.setAvailableAmount(availableAmount);
													// 定期代收收益-.
													Double regularDueInterest = userAccountInfo.getRegularDueInterest();
													regularDueInterest = NumberUtils.scaleDouble(regularDueInterest - repayAmount);
													userAccountInfo.setRegularDueInterest(regularDueInterest);
													// 定期总收益+.
													Double regularTotalInterest = userAccountInfo.getRegularTotalInterest();
													regularTotalInterest = NumberUtils.scaleDouble(regularTotalInterest + repayAmount);
													userAccountInfo.setRegularTotalInterest(regularTotalInterest);
													int userAccountInfoFlag = cgbUserAccountDao.update(userAccountInfo);
													if (userAccountInfoFlag == 1) { // 出借人账户更新成功.
														log.info("fn:repay-出借人账户更新成功.");
														/**
														 * 计入流水.
														 */
														CgbUserTransDetail userTransDetail = new CgbUserTransDetail();
														userTransDetail.setId(IdGen.uuid()); // 主键ID.
														userTransDetail.setTransId(userPlan.getId()); // 付息外部子订单号.
														userTransDetail.setUserId(userInfo.getId()); // 客户帐号ID.
														userTransDetail.setAccountId(userAccountInfo.getId()); // 客户账户ID.
														userTransDetail.setTransDate(new Date(System.currentTimeMillis() + 1000 * ++b)); // 交易时间.
														userTransDetail.setTrustType(CgbUserTransDetailService.TRUST_TYPE_4); // 还利息.
														userTransDetail.setAmount(repayAmount); // 利息.
														userTransDetail.setAvaliableAmount(availableAmount); // 当前可用余额.
														userTransDetail.setInOutType(CgbUserTransDetailService.IN_TYPE_1); // 收入.
														if (WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1.equals(repayTheInterest.getPrincipal())) {
															userTransDetail.setRemarks("最后一期还息"); // 备注信息.
														} else {
															userTransDetail.setRemarks("还息"); // 备注信息.
														}
														userTransDetail.setState(CgbUserTransDetailService.TRUST_STATE_2); // 流水状态，成功.
														int userTransDetailFlag = cgbUserTransDetailDao.insert(userTransDetail);
														if (userTransDetailFlag == 1) { // 计入流水成功.
															log.info("fn:repay-还息计入流水成功.");
															/**
															 * 客户还款计划，
															 * 状态更改为已还款.
															 */
															userPlan.setState(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_3);
															int modifyUserPlanStateFlag = wloanTermUserPlanDao.modifyWloanTermUserPlanState(userPlan);
															if (modifyUserPlanStateFlag == 1) { // 更新成功.
																log.info("fn:repay-客户还款计划状态-更新成功.");
																// 付息消息提醒.
																weixinSendTempMsgService.cgbSendRepayInfoMsg(userPlan, WeixinSendTempMsgService.IS_REPAY_TYPE_1);
															} else { // 失败.
																log.info("fn:repay-客户还款计划状态-更新失败.");
															}
														} else {
															log.info("fn:repay-还息计入流水失败.");
														}
													} else {
														log.info("fn:repay-出借人账户更新 失败.");
													}
												} else {
													log.info("fn:repay-出借人账户不存在.");
												}
											} else {
												log.info("fn:repay-出借人帐号不存在.");
											}
										} else {
											log.info("fn:repay-该投资记录不存在.");
										}
									}
									WloanSubject wloanSubject = wloanSubjectService.get(wloanTermProject.getSubjectId());
									// 还款总额.
									log.info("fn:repay-还款总额：" + repayTotalAmount);
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
												creditUserAccount.setUpdateDate(new Date());
												int creditUserAccountFlag = creditUserAccountDao.update(creditUserAccount);
												if (creditUserAccountFlag == 1) { // 借款人账户更新成功.
													log.info("fn:repay-借款人账户更新成功.");
													/**
													 * 计入流水.
													 */
													CgbUserTransDetail userTransDetail = new CgbUserTransDetail();
													userTransDetail.setId(IdGen.uuid()); // 主键ID.
													userTransDetail.setTransId(orderId); // 付息主订单号.
													userTransDetail.setUserId(creditUserInfo.getId()); // 客户帐号ID.
													userTransDetail.setAccountId(creditUserAccount.getId()); // 客户账户ID.
													userTransDetail.setTransDate(new Date(System.currentTimeMillis() + 1000 * ++b)); // 交易时间.
													userTransDetail.setTrustType(CgbUserTransDetailService.TRUST_TYPE_4); // 还利息.
													userTransDetail.setAmount(repayTotalAmount); // 利息.
													userTransDetail.setAvaliableAmount(availableAmount); // 当前可用余额.
													userTransDetail.setInOutType(CgbUserTransDetailService.OUT_TYPE_2); // 支出.
													userTransDetail.setRemarks("借款人还息"); // 备注信息.
													userTransDetail.setState(CgbUserTransDetailService.TRUST_STATE_2); // 流水状态，成功.
													int userTransDetailFlag = cgbUserTransDetailDao.insert(userTransDetail);
													if (userTransDetailFlag == 1) { // 借款人账户变更，计入流水.
														log.info("fn:repay-借款人还息计入流水成功.");
														/**
														 * 项目还款计划状态更新.
														 */
														repayTheInterest.setState(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_STATE_2);
														int modifyProjectPlanStateFlag = wloanTermProjectPlanDao.modifyWLoanTermProjectPlanState(repayTheInterest);
														if (modifyProjectPlanStateFlag == 1) { // 更新成功.
															log.info("fn:repay-项目还款计划状态-更新成功.");
														} else { // 失败.
															log.info("fn:repay-项目还款计划状态-更新失败.");
														}
													} else {
														log.info("fn:repay-借款人还息计入流水失败.");
													}
												} else { // 失败.
													log.info("fn:repay-借款人账户更新成功.");
												}
											} else {
												log.info("fn:repay-借款人账户不存在.");
											}
										} else {
											log.info("fn:repay-借款人帐号不存在.");
										}
									} else {
										log.info("fn:repay-融资主体不存在.");
									}

									/**
									 * 订单变更状态-已还.
									 */
									model.setState(ZtmgOrderInfoService.STATE_2);
									model.setUpdateDate(new Date());
									int flag = ztmgOrderInfoDao.update(model);
									if (flag == 1) { // 成功.
										log.info("fn:repay-订单变更状态成功.");
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
											log.info(this.getClass() + "：发布公告成功");
										} else {
											log.info(this.getClass() + "：发布公告失败");
										}
									} else { // 失败.
										log.info("fn:repay-订单变更状态失败.");
									}
								} else {
									log.info("fn:repay-项目不存在.");
								}
							} else {
								log.info("fn:repay-该项目不存在.");
							}
						}
					} else { // 还本.
						WloanTermProjectPlan repayTheCapital = wloanTermProjectPlanDao.getBySubOrderId(orderId);
						if (null != repayTheCapital) {
							if (WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1.equals(repayTheCapital.getPrincipal())) {

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
										log.info("fn:repay-出借人还款笔数：" + userPlanList.size() + "笔.");
										int c = 119;
										for (WloanTermUserPlan userPlan : userPlanList) {
											if (null != userPlan.getWloanTermInvest()) { // 投资记录.
												// 投资金额.
												Double investAmount = userPlan.getWloanTermInvest().getAmount();
												UserInfo userInfo = userPlan.getUserInfo();
												if (null != userInfo) { // 出借人帐号.
													CgbUserAccount userAccountInfo = cgbUserAccountDao.getUserAccountInfo(userInfo.getId());
													if (null != userAccountInfo) { // 出借人账户.
														// 可用余额+.
														Double availableAmount = userAccountInfo.getAvailableAmount();
														availableAmount = NumberUtils.scaleDouble(availableAmount + investAmount);
														userAccountInfo.setAvailableAmount(availableAmount);
														// 定期代收本金-.
														Double regularDuePrincipal = userAccountInfo.getRegularDuePrincipal();
														regularDuePrincipal = NumberUtils.scaleDouble(regularDuePrincipal - investAmount);
														userAccountInfo.setRegularDuePrincipal(regularDuePrincipal);
														int userAccountInfoFlag = cgbUserAccountDao.update(userAccountInfo);
														if (userAccountInfoFlag == 1) { // 出借人账户更新成功.
															log.info("fn:repay-出借人账户更新成功.");
															/**
															 * 计入流水.
															 */
															CgbUserTransDetail userTransDetail = new CgbUserTransDetail();
															userTransDetail.setId(IdGen.uuid()); // 主键ID.
															userTransDetail.setTransId(userPlan.getId().substring(0, userPlan.getId().length() - 1)); // 还本外部子订单号.
															userTransDetail.setUserId(userInfo.getId()); // 客户帐号ID.
															userTransDetail.setAccountId(userAccountInfo.getId()); // 客户账户ID.
															userTransDetail.setTransDate(new Date(System.currentTimeMillis() + 1000 * ++c)); // 交易时间.
															userTransDetail.setTrustType(CgbUserTransDetailService.TRUST_TYPE_5); // 还本金.
															userTransDetail.setAmount(investAmount); // 还本金.
															userTransDetail.setAvaliableAmount(availableAmount); // 当前可用余额.
															userTransDetail.setInOutType(CgbUserTransDetailService.IN_TYPE_1); // 收入.
															userTransDetail.setRemarks("最后一期还本"); // 备注信息.
															userTransDetail.setState(CgbUserTransDetailService.TRUST_STATE_2); // 流水状态，成功.
															int userTransDetailFlag = cgbUserTransDetailDao.insert(userTransDetail);
															if (userTransDetailFlag == 1) { // 计入流水成功.
																log.info("fn:repay-出借人还本计入流水成功.");
																/**
																 * 判断付息订单是否已还.
																 */
																if (null == repayTheInterestZtmgOrderInfo) { // NULL不执行.
																} else {
																	if (repayTheInterestZtmgOrderInfo.getState().equals(ZtmgOrderInfoService.STATE_1)) { // 未还-不执行最终操作.
																		// 还本消息提醒.
																		weixinSendTempMsgService.cgbSendRepayInfoMsg(userPlan, WeixinSendTempMsgService.IS_REPAY_TYPE_2);
																	} else if (repayTheInterestZtmgOrderInfo.getState().equals(ZtmgOrderInfoService.STATE_2)) { // 已还-执行最终操作.
																		// 客户还款计划状态更新.
																		userPlan.setState(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_3);
																		int modifyUserPlanStateFlag = wloanTermUserPlanDao.modifyWloanTermUserPlanState(userPlan);
																		if (modifyUserPlanStateFlag == 1) { // 更新成功.
																			log.info("fn:repay-客户还款计划状态-更新成功.");
																			// 还本消息提醒.
																			weixinSendTempMsgService.cgbSendRepayInfoMsg(userPlan, WeixinSendTempMsgService.IS_REPAY_TYPE_2);
																		} else { // 失败.
																			log.info("fn:repay-客户还款计划状态-更新失败.");
																		}
																	}
																}
															} else {
																log.info("fn:repay-还息计入流水失败.");
															}
														} else {
															log.info("fn:repay-出借人账户更新 失败.");
														}
													} else {
														log.info("fn:repay-出借人账户不存在.");
													}
												} else {
													log.info("fn:repay-出借人帐号不存在.");
												}
											} else {
												log.info("fn:repay-该笔投资记录不存在.");
											}
										}
										/**
										 * 借款人账户变更.
										 */
										// 实际筹集金额(也就是放款金额，同时也是本次借款人的还款金额).
										Double currentAmount = project.getCurrentRealAmount();
										log.info("fn:repay-UB-【还本】还款总额：" + NumberUtils.scaleDouble(currentAmount));
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
													creditUserAccount.setUpdateDate(new Date());
													int creditUserAccountFlag = creditUserAccountDao.update(creditUserAccount);
													if (creditUserAccountFlag == 1) { // 借款人账户更新成功.
														log.info("fn:repay-借款人账户更新成功.");
														/**
														 * 计入流水.
														 */
														CgbUserTransDetail userTransDetail = new CgbUserTransDetail();
														userTransDetail.setId(IdGen.uuid()); // 主键ID.
														userTransDetail.setTransId(orderId); // 还本主订单号.
														userTransDetail.setUserId(creditUserInfo.getId()); // 客户帐号ID.
														userTransDetail.setAccountId(creditUserAccount.getId()); // 客户账户ID.
														userTransDetail.setTransDate(new Date(System.currentTimeMillis() + 1000 * ++c)); // 交易时间.
														userTransDetail.setTrustType(CgbUserTransDetailService.TRUST_TYPE_5); // 还本金.
														userTransDetail.setAmount(currentAmount); // 还本.
														userTransDetail.setAvaliableAmount(availableAmount); // 当前可用余额.
														userTransDetail.setInOutType(CgbUserTransDetailService.OUT_TYPE_2); // 支出.
														userTransDetail.setRemarks("借款人还本"); // 备注信息.
														userTransDetail.setState(CgbUserTransDetailService.TRUST_STATE_2); // 流水状态，成功.
														int userTransDetailFlag = cgbUserTransDetailDao.insert(userTransDetail);
														if (userTransDetailFlag == 1) { // 借款人账户变更，计入流水.
															log.info("fn:repay-借款人还本计入流水成功.");
															/**
															 * 项目还款计划状态更新.
															 */
															repayTheCapital.setState(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_STATE_2);
															int modifyProjectPlanStateFlag = wloanTermProjectPlanDao.modifyWLoanTermProjectPlanState(repayTheCapital);
															if (modifyProjectPlanStateFlag == 1) { // 更新成功.
																log.info("fn:repay-项目还款计划状态-更新成功.");
															} else { // 失败.
																log.info("fn:repay-项目还款计划状态-更新失败.");
															}
														} else {
															log.info("fn:repay-借款人还息计入流水失败.");
														}
													} else { // 失败.
														log.info("fn:repay-借款人账户更新成功.");
													}
												}
											} else {
												log.info("fn:repay-借款人帐号不存在.");
											}
										} else {
											log.info("fn:repay-融资主体不存在.");
										}

										/**
										 * 订单变更状态-已还.
										 */
										model.setState(ZtmgOrderInfoService.STATE_2);
										model.setUpdateDate(new Date());
										int flag = ztmgOrderInfoDao.update(model);
										if (flag == 1) { // 成功.
											log.info("fn:repay-订单变更状态成功.");
											if (null == repayTheInterestZtmgOrderInfo) { // NULL不执行.
											} else { // 非NUll执行.
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
														log.info(this.getClass() + "：发布公告成功");
													} else {
														log.info(this.getClass() + "：发布公告失败");
													}
												} else if (repayTheInterestZtmgOrderInfo.getState().equals(ZtmgOrderInfoService.STATE_2)) { // 已还-可执行最终操作.
													/**
													 * 项目变更状态-已结束.
													 */
													// 最后一期还款，变更项目状态为-已结束.
													project.setState(WloanTermProjectService.FINISH); // 已完成(已结束).
													project.setUpdateDate(new Date()); // 更新时间.
													wloanTermProjectService.updateProState(project);
													log.info("fn:repay-该项目还款完成-项目（已结束）.");
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
														log.info(this.getClass() + "：发布公告成功");
													} else {
														log.info(this.getClass() + "：发布公告失败");
													}
												}
											}
										} else { // 失败.
											log.info("fn:repay-订单变更状态失败.");
										}
									} else {
										log.info("fn:repay-该项目不存在.");
									}
								}
							}
						} else {
							log.info("fn:repay-该项目还款计划不存在.");
						}
					}
				}
				log.info(this.getClass() + "，repay end.");
			} else if (model.getType().equals(ZtmgOrderInfoService.TYPE_2)) { // 代偿户.
				log.info(this.getClass() + "，replace repay start.");
				// 还款成功.
				if (model.getStatus().equals(ZtmgOrderInfoService.STATUS_S)) {
					/**
					 * 获取该批次的项目还款计划id/subOrderId.
					 */
					// 付息.
					WloanTermProjectPlan repayTheInterest = wloanTermProjectPlanDao.get(orderId);
					if (null != repayTheInterest) { // 还息.
						if (WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1.equals(repayTheInterest.getPrincipal())) { // 最后一期还本付息.

							/**
							 * 拿还本订单.
							 */
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
									log.info("fn:replaceRepay-出借人还款笔数：" + userPlanList.size() + "笔.");
									int a = 0;
									for (WloanTermUserPlan userPlan : userPlanList) {
										// 还本付息总额.
										Double repayAmount = userPlan.getInterest();
										// 投资金额.
										Double investAmount = wloanTermInvestDao.findInvestAmountByProjectAndUser(wloanTermProject.getId(), userPlan.getUserInfo().getId(), userPlan.getWloanTermInvest().getId());
										// 还息.
										Double repayInterest = repayAmount - investAmount;
										// 还款总额.
										repayTotalAmount = repayTotalAmount + repayInterest;
										if (null != userPlan.getWloanTermInvest()) { // 投资记录.
											UserInfo userInfo = userPlan.getUserInfo();
											if (null != userInfo) { // 出借人帐号.
												CgbUserAccount userAccountInfo = cgbUserAccountDao.getUserAccountInfo(userInfo.getId());
												if (null != userAccountInfo) { // 出借人账户.
													// 可用余额+.
													Double availableAmount = userAccountInfo.getAvailableAmount();
													availableAmount = NumberUtils.scaleDouble(availableAmount + repayInterest);
													userAccountInfo.setAvailableAmount(availableAmount);
													// 定期代收收益-.
													Double regularDueInterest = userAccountInfo.getRegularDueInterest();
													regularDueInterest = NumberUtils.scaleDouble(regularDueInterest - repayInterest);
													userAccountInfo.setRegularDueInterest(regularDueInterest);
													// 定期总收益+.
													Double regularTotalInterest = userAccountInfo.getRegularTotalInterest();
													regularTotalInterest = NumberUtils.scaleDouble(regularTotalInterest + repayInterest);
													userAccountInfo.setRegularTotalInterest(regularTotalInterest);
													int userAccountInfoFlag = cgbUserAccountDao.update(userAccountInfo);
													if (userAccountInfoFlag == 1) { // 出借人账户更新成功.
														log.info("fn:replaceRepay-出借人账户更新成功.");
														/**
														 * 计入流水.
														 */
														CgbUserTransDetail userTransDetail = new CgbUserTransDetail();
														userTransDetail.setId(IdGen.uuid()); // 主键ID.
														userTransDetail.setTransId(userPlan.getId()); // 付息外部子订单号.
														userTransDetail.setUserId(userInfo.getId()); // 客户帐号ID.
														userTransDetail.setAccountId(userAccountInfo.getId()); // 客户账户ID.
														userTransDetail.setTransDate(new Date(System.currentTimeMillis() + 1000 * ++a)); // 交易时间.
														userTransDetail.setTrustType(CgbUserTransDetailService.TRUST_TYPE_4); // 还利息.
														userTransDetail.setAmount(repayInterest); // 利息.
														userTransDetail.setAvaliableAmount(availableAmount); // 当前可用余额.
														userTransDetail.setInOutType(CgbUserTransDetailService.IN_TYPE_1); // 收入.
														if (WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1.equals(repayTheInterest.getPrincipal())) {
															userTransDetail.setRemarks("最后一期还息"); // 备注信息.
														} else {
															userTransDetail.setRemarks("还息"); // 备注信息.
														}
														userTransDetail.setState(CgbUserTransDetailService.TRUST_STATE_2); // 流水状态，成功.
														int userTransDetailFlag = cgbUserTransDetailDao.insert(userTransDetail);
														if (userTransDetailFlag == 1) { // 计入流水成功.
															log.info("fn:replaceRepay-出借人还息计入流水成功.");
															/**
															 * 判断还本订单是否已还.
															 */
															if (null == repayTheCapitalZtmgOrderInfo) { // NULL不执行.
															} else { // 非NULL执行最终操作.
																if (repayTheCapitalZtmgOrderInfo.getState().equals(ZtmgOrderInfoService.STATE_1)) { // 未还-不执行最终操作.
																	// 付息消息提醒.
																	weixinSendTempMsgService.cgbSendRepayInfoMsg(userPlan, WeixinSendTempMsgService.IS_REPAY_TYPE_1);
																} else if (repayTheCapitalZtmgOrderInfo.getState().equals(ZtmgOrderInfoService.STATE_2)) { // 已还-执行最终操作.
																	// 客户还款计划状态更改为已还款.
																	userPlan.setState(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_3);
																	int modifyUserPlanStateFlag = wloanTermUserPlanDao.modifyWloanTermUserPlanState(userPlan);
																	if (modifyUserPlanStateFlag == 1) { // 更新成功.
																		log.info("fn:replaceRepay-客户还款计划状态-更新成功.");
																		// 付息消息提醒.
																		weixinSendTempMsgService.cgbSendRepayInfoMsg(userPlan, WeixinSendTempMsgService.IS_REPAY_TYPE_1);
																	} else { // 失败.
																		log.info("fn:replaceRepay-客户还款计划状态-更新失败.");
																	}
																}
															}
														} else {
															log.info("fn:replaceRepay-出借人还息计入流水失败.");
														}
													} else {
														log.info("fn:replaceRepay-出借人账户更新 失败.");
													}
												} else {
													log.info("fn:replaceRepay-出借人账户不存在.");
												}
											} else {
												log.info("fn:replaceRepay-出借人帐号不存在.");
											}
										} else {
											log.info("fn:replaceRepay-该投资记录不存在.");
										}
									}

									if (wloanTermProject.getIsReplaceRepay().equals(WloanTermProject.IS_REPLACE_REPAY_1)) { // 是否代偿还款标识.
										log.info("fn:replaceRepay-是代偿还款");
									} else if (wloanTermProject.getIsReplaceRepay().equals(WloanTermProject.IS_REPLACE_REPAY_0)) { // 是否代偿还款标识.
										log.info("fn:replaceRepay-非代偿还款");
									}

									// 还款总额.
									log.info("fn:replaceRepay-还款总额：" + repayTotalAmount);

									/**
									 * 借款人账户变更.
									 */
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
													log.info("fn:replaceRepay-借款户账户变更成功");
												} else {
													log.info("fn:replaceRepay-借款户账户变更失败");
												}
											}
										}
									}

									/**
									 * 代偿人账户变更.
									 */
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
												log.info("fn:replaceRepay-代偿人账户更新成功.");
												/**
												 * 计入流水.
												 */
												CgbUserTransDetail userTransDetail = new CgbUserTransDetail();
												userTransDetail.setId(IdGen.uuid()); // 主键ID.
												userTransDetail.setTransId(orderId); // 付息主订单号.
												userTransDetail.setUserId(creditUserInfo.getId()); // 客户帐号ID.
												userTransDetail.setAccountId(creditUserAccount.getId()); // 客户账户ID.
												userTransDetail.setTransDate(new Date(System.currentTimeMillis() + 1000 * ++a)); // 交易时间.
												userTransDetail.setTrustType(CgbUserTransDetailService.TRUST_TYPE_4); // 还利息.
												userTransDetail.setAmount(repayTotalAmount); // 利息.
												userTransDetail.setAvaliableAmount(availableAmount); // 当前可用余额.
												userTransDetail.setInOutType(CgbUserTransDetailService.OUT_TYPE_2); // 支出.
												userTransDetail.setRemarks("代偿人还息"); // 备注信息.
												userTransDetail.setState(CgbUserTransDetailService.TRUST_STATE_2); // 流水状态，成功.
												int userTransDetailFlag = cgbUserTransDetailDao.insert(userTransDetail);
												if (userTransDetailFlag == 1) { // 代偿人账户变更，计入流水.
													log.info("fn:replaceRepay-代偿人还息计入流水成功.");
												} else {
													log.info("fn:replaceRepay-代偿人还息计入流水失败.");
												}
											} else { // 失败.
												log.info("fn:replaceRepay-代偿人账户更新失败.");
											}
										} else {
											log.info("fn:replaceRepay-代偿人账户不存在.");
										}
									} else {
										log.info("fn:replaceRepay-代偿人帐号不存在.");
									}

									/**
									 * 订单变更状态-已还.
									 */
									model.setState(ZtmgOrderInfoService.STATE_2);
									model.setUpdateDate(new Date());
									int flag = ztmgOrderInfoDao.update(model);
									if (flag == 1) { // 成功.
										log.info("fn:replaceRepay-订单变更状态成功.");
										if (null == repayTheCapitalZtmgOrderInfo) { // NULL不执行.
										} else { // 非NUll执行.
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
													log.info(this.getClass() + "：发布公告成功");
												} else {
													log.info(this.getClass() + "：发布公告失败");
												}
											} else if (repayTheCapitalZtmgOrderInfo.getState().equals(ZtmgOrderInfoService.STATE_2)) { // 已还-可执行最终操作.
												/**
												 * 项目变更状态-已结束.
												 */
												// 最后一期还款，变更项目状态为-已结束.
												wloanTermProject.setState(WloanTermProjectService.FINISH); // 已完成(已结束).
												wloanTermProject.setUpdateDate(new Date()); // 更新时间.
												wloanTermProjectService.updateProState(wloanTermProject);
												log.info("fn:fn:replaceRepay-该项目还款完成-项目（已结束）.");
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
													log.info(this.getClass() + "：发布公告成功");
												} else {
													log.info(this.getClass() + "：发布公告失败");
												}
											}
										}
									} else { // 失败.
										log.info("fn:replaceRepay-订单变更状态失败.");
									}
								} else {
									log.info("fn:replaceRepay-项目不存在.");
								}
							} else {
								log.info("fn:replaceRepay-该项目不存在.");
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
									log.info("fn:replaceRepay-出借人还款笔数：" + userPlanList.size() + "笔.");
									int b = 0;
									for (WloanTermUserPlan userPlan : userPlanList) {
										// 还款总额.
										Double repayAmount = userPlan.getInterest();
										repayTotalAmount = repayTotalAmount + repayAmount;
										if (null != userPlan.getWloanTermInvest()) { // 投资记录.
											UserInfo userInfo = userPlan.getUserInfo();
											if (null != userInfo) { // 出借人帐号.
												CgbUserAccount userAccountInfo = cgbUserAccountDao.getUserAccountInfo(userInfo.getId());
												if (null != userAccountInfo) { // 出借人账户.
													// 可用余额+.
													Double availableAmount = userAccountInfo.getAvailableAmount();
													availableAmount = NumberUtils.scaleDouble(availableAmount + repayAmount);
													userAccountInfo.setAvailableAmount(availableAmount);
													// 定期代收收益-.
													Double regularDueInterest = userAccountInfo.getRegularDueInterest();
													regularDueInterest = NumberUtils.scaleDouble(regularDueInterest - repayAmount);
													userAccountInfo.setRegularDueInterest(regularDueInterest);
													// 定期总收益+.
													Double regularTotalInterest = userAccountInfo.getRegularTotalInterest();
													regularTotalInterest = NumberUtils.scaleDouble(regularTotalInterest + repayAmount);
													userAccountInfo.setRegularTotalInterest(regularTotalInterest);
													int userAccountInfoFlag = cgbUserAccountDao.update(userAccountInfo);
													if (userAccountInfoFlag == 1) { // 出借人账户更新成功.
														log.info("fn:replaceRepay-出借人账户更新成功.");
														/**
														 * 计入流水.
														 */
														CgbUserTransDetail userTransDetail = new CgbUserTransDetail();
														userTransDetail.setId(IdGen.uuid()); // 主键ID.
														userTransDetail.setTransId(userPlan.getId()); // 付息外部子订单号.
														userTransDetail.setUserId(userInfo.getId()); // 客户帐号ID.
														userTransDetail.setAccountId(userAccountInfo.getId()); // 客户账户ID.
														userTransDetail.setTransDate(new Date(System.currentTimeMillis() + 1000 * ++b)); // 交易时间.
														userTransDetail.setTrustType(CgbUserTransDetailService.TRUST_TYPE_4); // 还利息.
														userTransDetail.setAmount(repayAmount); // 利息.
														userTransDetail.setAvaliableAmount(availableAmount); // 当前可用余额.
														userTransDetail.setInOutType(CgbUserTransDetailService.IN_TYPE_1); // 收入.
														if (WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1.equals(repayTheInterest.getPrincipal())) {
															userTransDetail.setRemarks("最后一期还息"); // 备注信息.
														} else {
															userTransDetail.setRemarks("还息"); // 备注信息.
														}
														userTransDetail.setState(CgbUserTransDetailService.TRUST_STATE_2); // 流水状态，成功.
														int userTransDetailFlag = cgbUserTransDetailDao.insert(userTransDetail);
														if (userTransDetailFlag == 1) { // 计入流水成功.
															log.info("fn:replaceRepay-出借人还息计入流水成功.");
															/**
															 * 客户还款计划，
															 * 状态更改为已还款.
															 */
															userPlan.setState(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_3);
															int modifyUserPlanStateFlag = wloanTermUserPlanDao.modifyWloanTermUserPlanState(userPlan);
															if (modifyUserPlanStateFlag == 1) { // 更新成功.
																log.info("fn:replaceRepay-客户还款计划状态-更新成功.");
																// 付息消息提醒.
																weixinSendTempMsgService.cgbSendRepayInfoMsg(userPlan, WeixinSendTempMsgService.IS_REPAY_TYPE_1);
															} else { // 失败.
																log.info("fn:replaceRepay-客户还款计划状态-更新失败.");
															}
														} else {
															log.info("fn:replaceRepay-出借人还息计入流水失败.");
														}
													} else {
														log.info("fn:replaceRepay-出借人账户更新 失败.");
													}
												} else {
													log.info("fn:replaceRepay-出借人账户不存在.");
												}
											} else {
												log.info("fn:replaceRepay-出借人帐号不存在.");
											}
										} else {
											log.info("fn:replaceRepay-该投资记录不存在.");
										}
									}

									if (wloanTermProject.getIsReplaceRepay().equals(WloanTermProject.IS_REPLACE_REPAY_1)) { // 是否代偿还款标识.
										log.info("fn:replaceRepay-是代偿还款");
									} else if (wloanTermProject.getIsReplaceRepay().equals(WloanTermProject.IS_REPLACE_REPAY_0)) { // 是否代偿还款标识.
										log.info("fn:replaceRepay-非代偿还款");
									}

									// 还款总额.
									log.info("fn:replaceRepay-还款总额：" + repayTotalAmount);

									/**
									 * 借款人账户变更.
									 */
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
													log.info("fn:replaceRepay-借款户账户变更成功");
												} else {
													log.info("fn:replaceRepay-借款户账户变更失败");
												}
											}
										}
									}

									/**
									 * 代偿人账户变更.
									 */
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
												log.info("fn:replaceRepay-代偿人账户更新成功.");
												/**
												 * 计入流水.
												 */
												CgbUserTransDetail userTransDetail = new CgbUserTransDetail();
												userTransDetail.setId(IdGen.uuid()); // 主键ID.
												userTransDetail.setTransId(orderId); // 付息主订单号.
												userTransDetail.setUserId(creditUserInfo.getId()); // 客户帐号ID.
												userTransDetail.setAccountId(creditUserAccount.getId()); // 客户账户ID.
												userTransDetail.setTransDate(new Date(System.currentTimeMillis() + 1000 * ++b)); // 交易时间.
												userTransDetail.setTrustType(CgbUserTransDetailService.TRUST_TYPE_4); // 还利息.
												userTransDetail.setAmount(repayTotalAmount); // 利息.
												userTransDetail.setAvaliableAmount(availableAmount); // 当前可用余额.
												userTransDetail.setInOutType(CgbUserTransDetailService.OUT_TYPE_2); // 支出.
												userTransDetail.setRemarks("代偿人还息"); // 备注信息.
												userTransDetail.setState(CgbUserTransDetailService.TRUST_STATE_2); // 流水状态，成功.
												int userTransDetailFlag = cgbUserTransDetailDao.insert(userTransDetail);
												if (userTransDetailFlag == 1) { // 代偿人账户变更，计入流水.
													log.info("fn:replaceRepay-代偿人还息计入流水成功.");
													/**
													 * 项目还款计划状态更新.
													 */
													repayTheInterest.setState(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_STATE_2);
													int modifyProjectPlanStateFlag = wloanTermProjectPlanDao.modifyWLoanTermProjectPlanState(repayTheInterest);
													if (modifyProjectPlanStateFlag == 1) { // 更新成功.
														log.info("fn:replaceRepay-项目还款计划状态-更新成功.");
													} else { // 失败.
														log.info("fn:replaceRepay-项目还款计划状态-更新失败.");
													}
												} else {
													log.info("fn:replaceRepay-代偿人还息计入流水失败.");
												}
											} else { // 失败.
												log.info("fn:replaceRepay-代偿人账户更新成功.");
											}
										} else {
											log.info("fn:replaceRepay-代偿人账户不存在.");
										}
									} else {
										log.info("fn:replaceRepay-代偿人帐号不存在.");
									}

									/**
									 * 订单变更状态-已还.
									 */
									model.setState(ZtmgOrderInfoService.STATE_2);
									model.setUpdateDate(new Date());
									int flag = ztmgOrderInfoDao.update(model);
									if (flag == 1) { // 成功.
										log.info("fn:replaceRepay-订单变更状态成功.");
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
											log.info(this.getClass() + "：发布公告成功");
										} else {
											log.info(this.getClass() + "：发布公告失败");
										}
									} else { // 失败.
										log.info("fn:replaceRepay-订单变更状态失败.");
									}
								} else {
									log.info("fn:replaceRepay-项目不存在.");
								}
							} else {
								log.info("fn:replaceRepay-该项目不存在.");
							}
						}
					} else { // 还本.
						WloanTermProjectPlan repayTheCapital = wloanTermProjectPlanDao.getBySubOrderId(orderId);
						if (null != repayTheCapital) {
							if (WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1.equals(repayTheCapital.getPrincipal())) {

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
										log.info("fn:replaceRepay-出借人还款笔数：" + userPlanList.size() + "笔.");
										int c = 119;
										for (WloanTermUserPlan userPlan : userPlanList) {
											if (null != userPlan.getWloanTermInvest()) { // 投资记录.
												// 投资金额.
												Double investAmount = userPlan.getWloanTermInvest().getAmount();
												UserInfo userInfo = userPlan.getUserInfo();
												if (null != userInfo) { // 出借人帐号.
													CgbUserAccount userAccountInfo = cgbUserAccountDao.getUserAccountInfo(userInfo.getId());
													if (null != userAccountInfo) { // 出借人账户.
														// 可用余额+.
														Double availableAmount = userAccountInfo.getAvailableAmount();
														availableAmount = NumberUtils.scaleDouble(availableAmount + investAmount);
														userAccountInfo.setAvailableAmount(availableAmount);
														// 定期代收本金-.
														Double regularDuePrincipal = userAccountInfo.getRegularDuePrincipal();
														regularDuePrincipal = NumberUtils.scaleDouble(regularDuePrincipal - investAmount);
														userAccountInfo.setRegularDuePrincipal(regularDuePrincipal);
														int userAccountInfoFlag = cgbUserAccountDao.update(userAccountInfo);
														if (userAccountInfoFlag == 1) { // 出借人账户更新成功.
															log.info("fn:replaceRepay-出借人账户更新成功.");
															/**
															 * 计入流水.
															 */
															CgbUserTransDetail userTransDetail = new CgbUserTransDetail();
															userTransDetail.setId(IdGen.uuid()); // 主键ID.
															userTransDetail.setTransId(userPlan.getId().substring(0, userPlan.getId().length() - 1)); // 还本外部子订单号.
															userTransDetail.setUserId(userInfo.getId()); // 客户帐号ID.
															userTransDetail.setAccountId(userAccountInfo.getId()); // 客户账户ID.
															userTransDetail.setTransDate(new Date(System.currentTimeMillis() + 1000 * ++c)); // 交易时间.
															userTransDetail.setTrustType(CgbUserTransDetailService.TRUST_TYPE_5); // 还本金.
															userTransDetail.setAmount(investAmount); // 还本金.
															userTransDetail.setAvaliableAmount(availableAmount); // 当前可用余额.
															userTransDetail.setInOutType(CgbUserTransDetailService.IN_TYPE_1); // 收入.
															userTransDetail.setRemarks("最后一期还本"); // 备注信息.
															userTransDetail.setState(CgbUserTransDetailService.TRUST_STATE_2); // 流水状态，成功.
															int userTransDetailFlag = cgbUserTransDetailDao.insert(userTransDetail);
															if (userTransDetailFlag == 1) { // 计入流水成功.
																log.info("fn:replaceRepay-出借人还息计入流水成功.");
																/**
																 * 判断付息订单是否已还.
																 */
																if (null == repayTheInterestZtmgOrderInfo) { // NULL不执行.
																} else {
																	if (repayTheInterestZtmgOrderInfo.getState().equals(ZtmgOrderInfoService.STATE_1)) { // 未还-不执行最终操作.
																		// 还本消息提醒.
																		weixinSendTempMsgService.cgbSendRepayInfoMsg(userPlan, WeixinSendTempMsgService.IS_REPAY_TYPE_2);
																	} else if (repayTheInterestZtmgOrderInfo.getState().equals(ZtmgOrderInfoService.STATE_2)) { // 已还-执行最终操作.
																		// 客户还款计划状态更新.
																		userPlan.setState(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_3);
																		int modifyUserPlanStateFlag = wloanTermUserPlanDao.modifyWloanTermUserPlanState(userPlan);
																		if (modifyUserPlanStateFlag == 1) { // 更新成功.
																			log.info("fn:replaceRepay-客户还款计划状态-更新成功.");
																			// 还本消息提醒.
																			weixinSendTempMsgService.cgbSendRepayInfoMsg(userPlan, WeixinSendTempMsgService.IS_REPAY_TYPE_2);
																		} else { // 失败.
																			log.info("fn:replaceRepay-客户还款计划状态-更新失败.");
																		}
																	}
																}
															} else {
																log.info("fn:replaceRepay-出借人还息计入流水失败.");
															}
														} else {
															log.info("fn:replaceRepay-出借人账户更新 失败.");
														}
													} else {
														log.info("fn:replaceRepay-出借人账户不存在.");
													}
												} else {
													log.info("fn:replaceRepay-出借人帐号不存在.");
												}
											} else {
												log.info("fn:replaceRepay-该笔投资记录不存在.");
											}
										}

										if (project.getIsReplaceRepay().equals(WloanTermProject.IS_REPLACE_REPAY_1)) { // 是否代偿还款标识.
											log.info("fn:replaceRepay-是代偿还款");
										} else if (project.getIsReplaceRepay().equals(WloanTermProject.IS_REPLACE_REPAY_0)) { // 是否代偿还款标识.
											log.info("fn:replaceRepay-非代偿还款");
										}

										// 实际筹集金额(也就是放款金额，同时也是本次借款人的还款金额).
										Double currentAmount = project.getCurrentRealAmount();
										log.info("fn:replaceRepay-UB-【还本】还款总额：" + NumberUtils.scaleDouble(currentAmount));

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
														log.info("fn:replaceRepay-借款户账户变更成功");
													} else {
														log.info("fn:replaceRepay-借款户账户变更失败");
													}
												}
											}
										}

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
													log.info("fn:replaceRepay-代偿人账户更新成功.");
													/**
													 * 计入流水.
													 */
													CgbUserTransDetail userTransDetail = new CgbUserTransDetail();
													userTransDetail.setId(IdGen.uuid()); // 主键ID.
													userTransDetail.setTransId(orderId); // 还本主订单号.
													userTransDetail.setUserId(creditUserInfo.getId()); // 客户帐号ID.
													userTransDetail.setAccountId(creditUserAccount.getId()); // 客户账户ID.
													userTransDetail.setTransDate(new Date(System.currentTimeMillis() + 1000 * ++c)); // 交易时间.
													userTransDetail.setTrustType(CgbUserTransDetailService.TRUST_TYPE_5); // 还本金.
													userTransDetail.setAmount(currentAmount); // 还本.
													userTransDetail.setAvaliableAmount(availableAmount); // 当前可用余额.
													userTransDetail.setInOutType(CgbUserTransDetailService.OUT_TYPE_2); // 支出.
													userTransDetail.setRemarks("代偿人还本"); // 备注信息.
													userTransDetail.setState(CgbUserTransDetailService.TRUST_STATE_2); // 流水状态，成功.
													int userTransDetailFlag = cgbUserTransDetailDao.insert(userTransDetail);
													if (userTransDetailFlag == 1) { // 代偿人账户变更，计入流水.
														log.info("fn:replaceRepay-代偿人还本计入流水成功.");
														/**
														 * 项目还款计划状态更新.
														 */
														repayTheCapital.setState(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_STATE_2);
														int modifyProjectPlanStateFlag = wloanTermProjectPlanDao.modifyWLoanTermProjectPlanState(repayTheCapital);
														if (modifyProjectPlanStateFlag == 1) { // 更新成功.
															log.info("fn:replaceRepay-项目还款计划状态-更新成功.");
														} else { // 失败.
															log.info("fn:replaceRepay-项目还款计划状态-更新失败.");
														}
													} else {
														log.info("fn:replaceRepay-代偿人还息计入流水失败.");
													}
												} else { // 失败.
													log.info("fn:replaceRepay-代偿人账户更新成功.");
												}
											}
										} else {
											log.info("fn:replaceRepay-代偿人帐号不存在.");
										}

										/**
										 * 订单变更状态-已还.
										 */
										model.setState(ZtmgOrderInfoService.STATE_2);
										model.setUpdateDate(new Date());
										int flag = ztmgOrderInfoDao.update(model);
										if (flag == 1) { // 成功.
											log.info("fn:replaceRepay-订单变更状态成功.");
											if (null == repayTheInterestZtmgOrderInfo) { // NULL不执行.
											} else { // 非NUll执行.
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
														log.info(this.getClass() + "：发布公告成功");
													} else {
														log.info(this.getClass() + "：发布公告失败");
													}
												} else if (repayTheInterestZtmgOrderInfo.getState().equals(ZtmgOrderInfoService.STATE_2)) { // 已还-可执行最终操作.
													// 最后一期还款，变更项目状态为-已结束.
													project.setState(WloanTermProjectService.FINISH); // 已完成(已结束).
													project.setUpdateDate(new Date()); // 更新时间.
													wloanTermProjectService.updateProState(project);
													log.info("fn:replaceRepay-该项目还款完成-项目（已结束）.");
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
														log.info(this.getClass() + "：发布公告成功");
													} else {
														log.info(this.getClass() + "：发布公告失败");
													}
												}
											}
										} else { // 失败.
											log.info("fn:replaceRepay-订单变更状态失败.");
										}
									} else {
										log.info("fn:replaceRepay-该项目不存在.");
									}
								}
							}
						} else {
							log.info("fn:replaceRepay-该项目还款计划不存在.");
						}
					}
				} else {
					log.info(this.getClass() + "，失败的订单，请联系开发人员查找原因.");
				}
				log.info(this.getClass() + "，replace repay end.");
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}

}
