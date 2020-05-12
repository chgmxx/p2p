package com.power.platform.task.lanmao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.cgb.dao.ZtmgOrderInfoDao;
import com.power.platform.cgb.entity.ZtmgOrderInfo;
import com.power.platform.cgb.service.ZtmgOrderInfoService;
import com.power.platform.common.config.Global;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.credit.dao.apply.CreditUserApplyDao;
import com.power.platform.credit.dao.userinfo.CreditUserAccountDao;
import com.power.platform.credit.dao.userinfo.CreditUserInfoDao;
import com.power.platform.credit.entity.userinfo.CreditUserAccount;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.service.apply.CreditUserApplyService;
import com.power.platform.credit.service.basicinfo.CreditBasicInfoService;
import com.power.platform.credit.service.userinfo.CreditUserInfoService;
import com.power.platform.lanmao.dao.AsyncTransactionLogDao;
import com.power.platform.lanmao.dao.LmTransactionDao;
import com.power.platform.lanmao.entity.AsyncTransactionLog;
import com.power.platform.lanmao.entity.LmTransaction;
import com.power.platform.lanmao.trade.pojo.AsyncTransaction;
import com.power.platform.lanmao.trade.pojo.SyncTransaction;
import com.power.platform.lanmao.trade.pojo.SyncTransactionDetail;
import com.power.platform.lanmao.trade.pojo.UserAuthorization;
import com.power.platform.lanmao.trade.service.LanMaoAsyncTransactionService;
import com.power.platform.lanmao.trade.service.LanMaoAuthorizationService;
import com.power.platform.lanmao.trade.service.LanMaoProjectService;
import com.power.platform.lanmao.type.AsyncTransactionLogStatusEnum;
import com.power.platform.lanmao.type.BizOriginEnum;
import com.power.platform.lanmao.type.BizTypeEnum;
import com.power.platform.lanmao.type.BusinessStatusEnum;
import com.power.platform.lanmao.type.BusinessTypeEnum;
import com.power.platform.lanmao.type.ConfirmTradeTypEnum;
import com.power.platform.lanmao.type.ProjectStatusEnum;
import com.power.platform.lanmao.type.ServiceNameEnum;
import com.power.platform.plan.dao.WloanTermProjectPlanDao;
import com.power.platform.plan.dao.WloanTermUserPlanDao;
import com.power.platform.plan.entity.WloanTermProjectPlan;
import com.power.platform.plan.entity.WloanTermUserPlan;
import com.power.platform.plan.service.WloanTermProjectPlanService;
import com.power.platform.plan.service.WloanTermUserPlanService;
import com.power.platform.regular.dao.WloanTermInvestDao;
import com.power.platform.regular.dao.WloanTermProjectDao;
import com.power.platform.regular.entity.WloanSubject;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.regular.service.WloanSubjectService;
import com.power.platform.regular.service.WloanTermInvestService;
import com.power.platform.regular.service.WloanTermProjectService;
import com.power.platform.task.pojo.RepayAvailableAmount;
import com.power.platform.userinfo.dao.UserInfoDao;
import com.power.platform.weixin.service.WeixinSendTempMsgService;

/**
 * 
 * class: AutoRepaymentAsyncTransactionTask <br>
 * description: 自动还款批量交易任务调度，懒猫2.0系统 <br>
 * author: Roy <br>
 * date: 2019年10月11日 下午2:48:23
 */
@Service
@Lazy(false)
public class AutoRepaymentAsyncTransactionTask {

	private static final Logger logger = LoggerFactory.getLogger(AutoRepaymentAsyncTransactionTask.class);

	/**
	 * 风控手机.
	 */
	private static final String CRO_MOBILE_PHONE = Global.getConfigUb("CRO_MOBILE_PHONE");
	/**
	 * 风控同事姓名.
	 */
	private static final String CRO_NAME = Global.getConfigUb("CRO_NAME");
	/**
	 * 财务手机.
	 */
	private static final String CFO_MOBILE_PHONE = Global.getConfigUb("CFO_MOBILE_PHONE");
	/**
	 * 财务同事姓名.
	 */
	private static final String CFO_NAME = Global.getConfigUb("CFO_NAME");

	@Autowired
	private WloanTermProjectService wloanTermProjectService;
	@Autowired
	private WloanTermProjectDao wloanTermProjectDao;
	@Autowired
	private WloanTermProjectPlanService wloanTermProjectPlanService;
	@Autowired
	private WloanTermProjectPlanDao wloanTermProjectPlanDao;
	@Autowired
	private WloanTermUserPlanDao wloanTermUserPlanDao;
	@Autowired
	private WloanTermInvestDao wloanTermInvestDao;
	@Autowired
	private WloanTermInvestService wloanTermInvestService;
	@Autowired
	private CreditUserApplyService creditUserApplyService;
	@Autowired
	private CreditUserInfoService creditUserInfoService;
	@Autowired
	private CreditBasicInfoService creditBasicInfoService;
	@Autowired
	private WloanSubjectService wloanSubjectService;
	@Autowired
	private CreditUserInfoDao creditUserInfoDao;
	@Autowired
	private CreditUserAccountDao creditUserAccountDao;
	@Autowired
	private CreditUserApplyDao creditUserApplyDao;
	@Autowired
	private UserInfoDao userInfoDao;
	@Autowired
	private LanMaoAuthorizationService lanMaoAuthorizationService;
	@Autowired
	private LanMaoAsyncTransactionService lanMaoAsyncTransactionService;
	@Autowired
	private LmTransactionDao lmTransactionDao;
	@Autowired
	private ZtmgOrderInfoDao ztmgOrderInfoDao;
	@Autowired
	private AsyncTransactionLogDao asyncTransactionLogDao;
	@Autowired
	private WeixinSendTempMsgService weixinSendTempMsgService;
	@Autowired
	private LanMaoProjectService lanMaoProjectService;

	/**
	 * 
	 * methods: repayments <br>
	 * description: 还款，主要用于借款人还给出借人的业务场景 <br>
	 * author: Roy <br>
	 * date: 2019年10月11日 下午2:06:53
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	// 每五分钟触发一次
	// @Scheduled(cron = "0 0/5 * * * ?")
	// 凌晨五点触发
	@Scheduled(cron = "0 0 5 * * ?")
	public void repayments() {

		logger.info("自动还款，借款人还款开始...start...");
		try {

			// 项目还款计划.
			WloanTermProjectPlan wloanTermProjectPlan = new WloanTermProjectPlan();
			// 项目联查条件.
			WloanTermProject wloanTermProject = new WloanTermProject();
			// 标的流转状态：还款中.
			wloanTermProject.setState(WloanTermProjectService.REPAYMENT);
			// 标的产品类型：供应链标的.
			wloanTermProject.setProjectProductType(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1);
			wloanTermProjectPlan.setWloanTermProject(wloanTermProject);
			// 状态：还款中.
			wloanTermProjectPlan.setState(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_STATE_1);
			// 还款日期开始时间.
			wloanTermProjectPlan.setBeginDate(DateUtils.formatDate(new Date(), "yyyy-MM-dd") + " 00:00:00");
			// 还款日期结束时间.
			wloanTermProjectPlan.setEndDate(DateUtils.formatDate(new Date(), "yyyy-MM-dd") + " 23:59:59");
			// 当天的项目待还款计划列表.
			List<WloanTermProjectPlan> projectPlans = wloanTermProjectPlanDao.findList(wloanTermProjectPlan);

			// 没有项目待还款计划.
			if (projectPlans != null && projectPlans.size() == 0) {
				logger.info("自动还款，借款人还款-没有待还标的还款计划......");
				return; // 结束当前方法.
			}

			/**
			 * 封装保存当前批次还款中所有借款人的账户可用余额.
			 */
			// 1）本批次，借款人账户ID列表，未去重
			List<String> repayUserAccountIds = new ArrayList<String>();
			for (WloanTermProjectPlan projectPlan : projectPlans) {
				WloanTermProject loanProject = wloanTermProjectService.get(projectPlan.getWloanTermProject().getId());
				if (null != loanProject) {
					WloanSubject wloanSubject = wloanSubjectService.get(loanProject.getSubjectId());
					if (null != wloanSubject) {
						CreditUserInfo creditUserInfo = creditUserInfoService.get(wloanSubject.getLoanApplyId()); // 借款人帐号
						if (null != creditUserInfo) {
							CreditUserAccount creditUserAccount = creditUserAccountDao.get(creditUserInfo.getAccountId()); // 借款人账户
							if (null != creditUserAccount) {
								repayUserAccountIds.add(creditUserAccount.getId());
							}
						}
					}
				}
			}
			logger.info("本批次，借款人账户ID列表-未去重:{}", repayUserAccountIds.toString());
			// 2）本批次，借款人账户ID列表，去重后
			List<String> pastLeepUserAccountIds = pastLeep(repayUserAccountIds);
			logger.info("本批次，借款人账户ID列表-去重后:{}" + pastLeepUserAccountIds.toString());
			// 3）去重后的账户ID列表，添加对应的可用余额，操作借款人授权预处理时，校验账户余额是否充足
			List<RepayAvailableAmount> repayAvailableAmountList = new ArrayList<RepayAvailableAmount>();
			RepayAvailableAmount repayAvailableAmount = null;
			for (String accountId : pastLeepUserAccountIds) {
				repayAvailableAmount = new RepayAvailableAmount();
				CreditUserAccount creditUserAccount = creditUserAccountDao.get(accountId);
				if (creditUserAccount != null) {
					repayAvailableAmount.setUserAccountId(creditUserAccount.getId());
					repayAvailableAmount.setAvailableAmount(NumberUtils.scaleDouble(creditUserAccount.getAvailableAmount()));
					repayAvailableAmountList.add(repayAvailableAmount);
				}
			}
			logger.info("本批次，借款人账户ID及账户余额列表:{}" + repayAvailableAmountList.toString());

			// 遍历标的还款计划
			for (WloanTermProjectPlan proPlan : projectPlans) {

				// 防止二次批量还款交易
				if ("TRUE".equals(proPlan.getOrderStatus())) {
					logger.info("自动还款，间接代偿确认，进入二次授权批量交易，结束本次任务......");
					// 结束本次循环.
					continue;
				}

				String projectPlanId = proPlan.getId(); // 标的还款计划ID
				String borrowersSourcePlatformUserNo = ""; // 借款（供应商）方平台用户编号
				String borrowersAccountId = ""; // 借款（供应商）方平台用户账户ID
				String projectNo = ""; // 标的号，平台的标的编号
				String requestNo = IdGen.uuid(); // 授权预处理，请求流水号，唯一标识
				String proid = proPlan.getWloanTermProject().getId(); // 标的ID
				Date repaymentDate = proPlan.getRepaymentDate(); // 本期标的还款日
				Double userPlanRepayAmount = 0D; // 本期用户还款计划还款合计总额
				Double proAamount = 0D; // 本期还款的标的募集金额
				String subOrderId = proPlan.getSubOrderId(); // 标的还款计划，还本金时用到
				logger.info("本期标的还款计划，还款金额:{}", NumberUtils.scaleDouble(proPlan.getInterest()));
				String repayType = proPlan.getPrincipal(); // 还款类型
				String repayCompanyName = ""; // 借款企业名称
				WloanTermProject repaymentProject = wloanTermProjectService.get(proid);
				if (null != repaymentProject) {
					proAamount = repaymentProject.getAmount();
					logger.info("本期还款中标的募集金额:{}", NumberUtils.scaleDoubleStr(proAamount));
					projectNo = repaymentProject.getSn(); // 标的编号
					WloanSubject wloanSubject = wloanSubjectService.get(repaymentProject.getSubjectId());
					if (null != wloanSubject) {
						borrowersSourcePlatformUserNo = wloanSubject.getLoanApplyId(); // 借款人
						CreditUserInfo creditUserInfo = creditUserInfoService.get(borrowersSourcePlatformUserNo); // 借款人帐号
						if (null != creditUserInfo) {
							repayCompanyName = creditUserInfo.getEnterpriseFullName();
							borrowersAccountId = creditUserInfo.getAccountId(); // 借款人账户ID
						}
					}

					/**
					 * 批量交易还款，数据封装
					 */
					String batchNo = IdGen.uuid(); // Y 批次号
					List<SyncTransaction> bizDetails = new ArrayList<SyncTransaction>(); // 交易明细
					List<AsyncTransactionLog> atls = new ArrayList<AsyncTransactionLog>(); // 批量交易订单日志
					// 客户还款计划查询封装.
					WloanTermUserPlan entity = new WloanTermUserPlan();
					entity.setState(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_2);
					WloanTermProject project = new WloanTermProject();
					project.setId(proid);
					entity.setWloanTermProject(project);
					entity.setRepaymentDate(repaymentDate);
					List<WloanTermUserPlan> userPlanList = wloanTermUserPlanDao.findList(entity);
					WloanTermUserPlan userPlan = null;
					SyncTransaction syncTransaction = null;
					SyncTransactionDetail std = null;
					LmTransaction lt = null;
					AsyncTransactionLog atl = null; // 批量交易日志，交易失败逻辑处理
					long currentTimeMillis = System.currentTimeMillis();
					// 本期用户还款计划业务明细
					for (int i = 0; i < userPlanList.size(); i++) {
						userPlan = userPlanList.get(i);
						String userPlanId = userPlan.getId();
						userPlanRepayAmount = NumberUtils.add(userPlanRepayAmount, userPlan.getInterest());
						String tradeRequestNo = IdGen.uuid();
						// Y 交易明细
						syncTransaction = new SyncTransaction();
						syncTransaction.setRequestNo(tradeRequestNo); // Y 交易明细订单号
						syncTransaction.setTradeType(BizTypeEnum.REPAYMENT.getValue()); // Y 交易类型
						syncTransaction.setProjectNo(projectNo); // N 标的编号
						syncTransaction.setSaleRequestNo(null); // N 债权出让请求流水号
						// Y 业务明细
						List<SyncTransactionDetail> details = new ArrayList<SyncTransactionDetail>();
						std = new SyncTransactionDetail();
						std.setBizType(BusinessTypeEnum.REPAYMENT.getValue()); // Y 业务类型
						std.setFreezeRequestNo(requestNo); // N 授权预处理，请求流水号
						std.setSourcePlatformUserNo(borrowersSourcePlatformUserNo); // N 出款方用户编号
						std.setTargetPlatformUserNo(userPlan.getUserInfo().getId()); // N 收款方用户编号
						if (WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_PRINCIPAL_2.equals(userPlan.getPrincipal())) {
							std.setAmount(NumberUtils.scaleDoubleStr(userPlan.getInterest())); // Y 本息和
							std.setIncome(NumberUtils.scaleDoubleStr(userPlan.getInterest())); // N 利息
							std.setShare(null); // N 债权份额（债权认购且需校验债权关系的必传）
							std.setCustomDefine(null); // N 平台商户自定义参数，平台交易时传入的自定义参数
							std.setRemark("付息");// N
						} else if (WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_PRINCIPAL_1.equals(userPlan.getPrincipal())) {
							std.setAmount(NumberUtils.scaleDoubleStr(userPlan.getInterest())); // Y 本息和
							std.setIncome(NumberUtils.scaleDoubleStr(NumberUtils.subtract(userPlan.getInterest(), userPlan.getWloanTermInvest().getAmount()))); // N 利息
							std.setShare(null); // N 债权份额（债权认购且需校验债权关系的必传）
							std.setCustomDefine(null); // N 平台商户自定义参数，平台交易时传入的自定义参数
							std.setRemark("还本付息");// N
						}
						details.add(std);
						syncTransaction.setDetails(details); // Y
						bizDetails.add(syncTransaction);
						// 批量交易日志，交易失败逻辑处理
						atl = new AsyncTransactionLog();
						atl.setId(IdGen.uuid());
						atl.setAsyncRequestNo(tradeRequestNo); // 异步通知时的交易明细订单号
						atl.setFreezeRequestNo(userPlanId); // 用户还款计划订单id
						atl.setBizType(BizTypeEnum.REPAYMENT.getValue()); // 交易类型：还款
						atl.setBizOrigin(BizOriginEnum.DISPERSION.getValue()); // 业务来源
						atl.setStatus(AsyncTransactionLogStatusEnum.INIT.getValue()); // 处理中
						currentTimeMillis = currentTimeMillis + 1000;
						atl.setCreateDate(new Date(currentTimeMillis));
						atl.setUpdateDate(new Date(currentTimeMillis));
						atls.add(atl);
					}
					logger.info("[还款]本期还款合计总额:{}", NumberUtils.scaleDoubleStr(userPlanRepayAmount));

					// 程序执行结束标志.
					boolean projectPlansFlag = false;
					// 校验借款人账户可用余额是否可以抵扣本期还款金额
					for (RepayAvailableAmount raa : repayAvailableAmountList) {
						if (raa.getUserAccountId().equals(borrowersAccountId)) { // 本期借款还款人
							if (NumberUtils.scaleDouble(raa.getAvailableAmount()) < NumberUtils.scaleDouble(userPlanRepayAmount)) { // 借款人账户可用余额小于本期还款金额
								projectPlansFlag = true;
								logger.info("[间接代偿]本期:{}还款，代偿人账户可用余额不足......", projectPlanId);
								// 风控短消息提醒.
								weixinSendTempMsgService.ztmgSendWarnInfoMsg(CRO_MOBILE_PHONE, CRO_NAME, repayCompanyName, WeixinSendTempMsgService.ZTMG_SEND_WARN_INFO_MSG_1);
								// 财务短消息提醒.
								weixinSendTempMsgService.ztmgSendWarnInfoMsg(CFO_MOBILE_PHONE, CFO_NAME, repayCompanyName, WeixinSendTempMsgService.ZTMG_SEND_WARN_INFO_MSG_2);
								break;
							} else {
								// 可用余额逻辑扣除还款金额.
								raa.setAvailableAmount(NumberUtils.scaleDouble(NumberUtils.scaleDouble(raa.getAvailableAmount()) - NumberUtils.scaleDouble(userPlanRepayAmount)));
								break;
							}
						}
					}

					if (projectPlansFlag) {
						// 结束本次循环.
						continue;
					}

					// 还款授权预处理
					UserAuthorization userAuthorization = new UserAuthorization();
					userAuthorization.setRequestNo(requestNo); // Y 授权预处理，请求流水号
					userAuthorization.setPlatformUserNo(borrowersSourcePlatformUserNo); // Y 平台用户编号
					userAuthorization.setOriginalRechargeNo(null); // N 关联充值请求流水号（原充值成功请求流水号）
					userAuthorization.setBizType(ConfirmTradeTypEnum.REPAYMENT.getValue());// Y 见【预处理业务类型】若传入关联请求流水号，则固定为TENDER
					userAuthorization.setAmount(NumberUtils.scaleDoubleStr(userPlanRepayAmount)); // Y 冻结金额，本次还款合计总额
					userAuthorization.setPreMarketingAmount(null); // N 预备使用的红包金额，只记录不冻结，仅限出借业务类型
					if (WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_0.equals(repayType)) {
						userAuthorization.setRemark("借款人付息");
					} else if (WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1.equals(repayType)) {
						userAuthorization.setRemark("借款人还本付息");
					}
					userAuthorization.setProjectNo(projectNo); // Y 标的号, 若传入关联充值请求流水号，则标的号固定为充值请求传入的标的号
					userAuthorization.setShare(null); // N 购买债转份额，业务类型为债权认购时，需要传此参数 。
					userAuthorization.setCreditsaleRequestNo(null);// N 债权出让请求流水号，只有债权认购业务需填此参数
					Map<String, String> userAutoPreTransactionMap = lanMaoAuthorizationService.userAutoPreTransaction(userAuthorization);
					// 调用成功且业务处理成功
					if (BusinessStatusEnum.SUCCESS.getValue().equals(userAutoPreTransactionMap.get("status")) && "0".equals(userAutoPreTransactionMap.get("code"))) {
						logger.info("懒猫借款人授权预处理:{}", BusinessStatusEnum.SUCCESS.getValue());
						lt = new LmTransaction();
						lt.setId(IdGen.uuid());
						lt.setServiceName(ServiceNameEnum.USER_AUTO_PRE_TRANSACTION.getValue());
						lt.setBatchNo(batchNo); // 批次号
						lt.setRequestNo(requestNo); // Y 授权预处理，请求流水号
						lt.setPlatformUserNo(borrowersSourcePlatformUserNo); // Y 平台用户编号
						lt.setTradeType(BizTypeEnum.REPAYMENT.getValue()); // 交易类型
						lt.setProjectNo(projectNo);// 标的编号
						lt.setCode(userAutoPreTransactionMap.get("code"));
						lt.setStatus(userAutoPreTransactionMap.get("status"));
						currentTimeMillis = currentTimeMillis + 1000;
						lt.setCreateDate(new Date(currentTimeMillis));
						lt.setUpdateDate(new Date(currentTimeMillis));
						int insertLtFlag = lmTransactionDao.insert(lt);
						logger.info("还款授权预处理留存插入:{}", insertLtFlag == 1 ? "成功" : "失败");
						/**
						 * 还款确认，批量交易
						 */
						AsyncTransaction asyncTransaction = new AsyncTransaction();
						asyncTransaction.setBatchNo(batchNo);
						asyncTransaction.setBizDetails(bizDetails);
						Map<String, String> asyncTransactionMap = lanMaoAsyncTransactionService.asyncTransaction(asyncTransaction);
						if (BusinessStatusEnum.SUCCESS.getValue().equals(asyncTransactionMap.get("status")) && "0".equals(asyncTransactionMap.get("code"))) {
							logger.info("还款确认，批量交易:{}", BusinessStatusEnum.SUCCESS.getValue());
							// 平台留存
							lt = new LmTransaction();
							lt.setId(IdGen.uuid());
							lt.setServiceName(ServiceNameEnum.ASYNC_TRANSACTION.getValue());
							lt.setBatchNo(batchNo); // 批次号
							lt.setPlatformUserNo(borrowersSourcePlatformUserNo);
							lt.setCode(asyncTransactionMap.get("code"));
							lt.setStatus(asyncTransactionMap.get("status"));
							lt.setRemarks("还款，批量交易");
							lt.setTradeType(BizTypeEnum.REPAYMENT.getValue());
							lt.setProjectNo(projectNo);// 标的编号
							currentTimeMillis = currentTimeMillis + 1000;
							lt.setCreateDate(new Date(currentTimeMillis));
							lt.setUpdateDate(new Date(currentTimeMillis));
							int insertCreditAssignmentFlag = lmTransactionDao.insert(lt);
							logger.info("还款，批量交易记录留存:{}", insertCreditAssignmentFlag == 1 ? "成功" : "失败");

							for (AsyncTransactionLog atlNext : atls) {
								int insertAtlNextFlag = asyncTransactionLogDao.insert(atlNext);
								logger.info("还款批量交易日志，插入:{}", insertAtlNextFlag == 1 ? "成功" : "失败");
							}

							// 业务订单信息，任务调度轮询还款
							ZtmgOrderInfo zoi = null;
							if (WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1.equals(repayType)) { // 还本付息
								// 标的状态，已截标
								// -----------------------------------------------------------------------------
								/**
								Map<String, Object> modifyProjectMap = lanMaoProjectService.modifyProject(batchNo, projectNo, ProjectStatusEnum.FINISH.getValue());
								if (BusinessStatusEnum.SUCCESS.getValue().equals(modifyProjectMap.get("status")) && "0".equals(modifyProjectMap.get("code"))) {
									logger.info("借款人还款，最后一期还款，标的生命周期已结束，截标成功......");
									// 平台留存
									lt = new LmTransaction();
									lt.setId(IdGen.uuid());
									lt.setServiceName(ServiceNameEnum.MODIFY_PROJECT.getValue());
									lt.setBatchNo(batchNo); // 批次号
									lt.setRequestNo(batchNo);
									lt.setPlatformUserNo(borrowersSourcePlatformUserNo);
									lt.setCode(asyncTransactionMap.get("code"));
									lt.setStatus(asyncTransactionMap.get("status"));
									lt.setRemarks("借款人最后一期还本付息，截标");
									lt.setProjectNo(projectNo);// 标的编号
									currentTimeMillis = currentTimeMillis + 1000;
									lt.setCreateDate(new Date(currentTimeMillis));
									lt.setUpdateDate(new Date(currentTimeMillis));
									int insertLmtFlag = lmTransactionDao.insert(lt);
									logger.info("借款人最后一期还本付息，截标，变更标的记录留存:{}", insertLmtFlag == 1 ? "成功" : "失败");
								} else {
									logger.info("借款人还款，最后一期还款，标的生命周期已结束，截标失败......");
									// 平台留存
									lt = new LmTransaction();
									lt.setId(IdGen.uuid());
									lt.setServiceName(ServiceNameEnum.MODIFY_PROJECT.getValue());
									lt.setBatchNo(batchNo); // 批次号
									lt.setRequestNo(batchNo);
									lt.setPlatformUserNo(borrowersSourcePlatformUserNo);
									lt.setCode(asyncTransactionMap.get("code"));
									lt.setStatus(asyncTransactionMap.get("status"));
									lt.setErrorCode(asyncTransactionMap.get("errorCode"));
									lt.setErrorMessage(asyncTransactionMap.get("errorMessage"));
									lt.setRemarks("借款人最后一期还本付息，截标");
									lt.setProjectNo(projectNo);// 标的编号
									currentTimeMillis = currentTimeMillis + 1000;
									lt.setCreateDate(new Date(currentTimeMillis));
									lt.setUpdateDate(new Date(currentTimeMillis));
									int insertLmtFlag = lmTransactionDao.insert(lt);
									logger.info("借款人最后一期还本付息，截标，变更标的记录留存:{}", insertLmtFlag == 1 ? "成功" : "失败");
								}
								*/
								// -----------------------------------------------------------------------------
								// 分两次还款.
								for (int i = 1; i < 3; i++) {
									zoi = new ZtmgOrderInfo();
									if (i == 1) { // 还息.
										zoi.setId(IdGen.uuid());
										zoi.setMerchantId(Global.getConfigLanMao("platformNo")); // 平台编号
										zoi.setOrderId(projectPlanId); // 标的当期还款计划id
										zoi.setStatus("S"); // 状态，成功
										zoi.setType(ZtmgOrderInfoService.TYPE_1); // 借款户.
										zoi.setState(ZtmgOrderInfoService.STATE_1); // 未还.
										currentTimeMillis = currentTimeMillis + 1000;
										zoi.setCreateDate(new Date(currentTimeMillis));
										zoi.setUpdateDate(new Date(currentTimeMillis));
										zoi.setRemarks("最后一期还款，付息");
										int zoiFlag = ztmgOrderInfoDao.insert(zoi);
										logger.info("最后一期还款，付息落单:{}", zoiFlag == 1 ? "成功" : "失败");
									} else if (i == 2) { // 还本
										currentTimeMillis = currentTimeMillis + 1000;
										zoi.setId(IdGen.uuid());
										zoi.setMerchantId(Global.getConfigLanMao("platformNo")); // 平台编号
										zoi.setOrderId(subOrderId); // 标的当前还款计划sub_order_id
										zoi.setStatus("S"); // 状态，成功
										zoi.setType(ZtmgOrderInfoService.TYPE_1); // 借款户.
										zoi.setState(ZtmgOrderInfoService.STATE_1); // 未还.
										currentTimeMillis = currentTimeMillis + 1000;
										zoi.setCreateDate(new Date(currentTimeMillis));
										zoi.setUpdateDate(new Date(currentTimeMillis));
										zoi.setRemarks("最后一期还款，还本");
										int zoiFlag = ztmgOrderInfoDao.insert(zoi);
										logger.info("最后一期还款，还本落单:{}", zoiFlag == 1 ? "成功" : "失败");
									}
								}
							} else if (WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_0.equals(repayType)) { // 付息
								zoi = new ZtmgOrderInfo();
								zoi.setId(IdGen.uuid());
								zoi.setMerchantId(Global.getConfigLanMao("platformNo")); // 平台编号
								zoi.setOrderId(projectPlanId); // 标的当期还款计划id
								zoi.setStatus("S"); // 状态，成功
								zoi.setType(ZtmgOrderInfoService.TYPE_1); // 借款户.
								zoi.setState(ZtmgOrderInfoService.STATE_1); // 未还.
								currentTimeMillis = currentTimeMillis + 1000;
								zoi.setCreateDate(new Date(currentTimeMillis));
								zoi.setUpdateDate(new Date(currentTimeMillis));
								zoi.setRemarks("还款，付息");
								int zoiFlag = ztmgOrderInfoDao.insert(zoi);
								logger.info("还款，付息落单:{}", zoiFlag == 1 ? "成功" : "失败");
							}
							int modifyOrderStatusFlag = wloanTermProjectPlanDao.modifyProjectPlanOrderStatus("TRUE", projectPlanId);
							logger.info("该期标的还款计划，已在存管行落单，订单状态更新:{}", modifyOrderStatusFlag == 1 ? "成功" : "失败");
							logger.info("自动还款，借款人还款结束...end...");
						} else {
							lt = new LmTransaction();
							lt.setId(IdGen.uuid());
							lt.setServiceName(ServiceNameEnum.ASYNC_TRANSACTION.getValue());
							lt.setBatchNo(batchNo); // 批次号
							lt.setRequestNo(requestNo); // Y 授权预处理，请求流水号
							lt.setPlatformUserNo(borrowersSourcePlatformUserNo);
							lt.setTradeType(BizTypeEnum.REPAYMENT.getValue()); // 交易类型
							lt.setProjectNo(projectNo);// 标的编号
							lt.setCode(userAutoPreTransactionMap.get("code"));
							lt.setStatus(userAutoPreTransactionMap.get("status"));
							lt.setErrorCode(userAutoPreTransactionMap.get("errorCode"));
							lt.setErrorMessage(userAutoPreTransactionMap.get("errorMessage"));
							currentTimeMillis = currentTimeMillis + 1000;
							lt.setCreateDate(new Date(currentTimeMillis));
							lt.setUpdateDate(new Date(currentTimeMillis));
							int insertAsyncTransactionFlag = lmTransactionDao.insert(lt);
							logger.info("还款，批量交易留存插入:{}", insertAsyncTransactionFlag == 1 ? "成功" : "失败");
							logger.info("自动还款，借款人还款结束...end...");
						}
					} else {
						logger.info("懒猫借款人授权预处理:{}", BusinessStatusEnum.INIT.getValue());
						lt = new LmTransaction();
						lt.setId(IdGen.uuid());
						lt.setServiceName(ServiceNameEnum.USER_AUTO_PRE_TRANSACTION.getValue());
						lt.setBatchNo(batchNo); // 批次号
						lt.setRequestNo(requestNo); // Y 授权预处理，请求流水号
						lt.setPlatformUserNo(borrowersSourcePlatformUserNo); // Y 平台用户编号
						lt.setTradeType(BizTypeEnum.REPAYMENT.getValue()); // 交易类型
						lt.setProjectNo(projectNo);// 标的编号
						lt.setCode(userAutoPreTransactionMap.get("code"));
						lt.setStatus(userAutoPreTransactionMap.get("status"));
						lt.setErrorCode(userAutoPreTransactionMap.get("errorCode"));
						lt.setErrorMessage(userAutoPreTransactionMap.get("errorMessage"));
						currentTimeMillis = currentTimeMillis + 1000;
						lt.setCreateDate(new Date(currentTimeMillis));
						lt.setUpdateDate(new Date(currentTimeMillis));
						int insertLtFlag = lmTransactionDao.insert(lt);
						logger.info("还款授权预处理留存插入:{}", insertLtFlag == 1 ? "成功" : "失败");
						logger.info("自动还款，借款人还款结束...end...");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("自动还款，借款人还款结束...end...");
		}
	}

	/**
	 * 
	 * methods: indirectCompensatory <br>
	 * description: 间接代偿，主要用于平台或担保机构发起代偿行为的场景，但是资金会经过借款人的账户 <br>
	 * 即先从平台代偿账户或担保机构代偿到借款人账户，再从借款人账户给到出借人账户 <br>
	 * author: Roy <br>
	 * date: 2019年10月11日 下午2:08:23
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	// 每五分钟触发一次
	// @Scheduled(cron = "0 0/5 * * * ?")
	// 凌晨五点触发
	@Scheduled(cron = "0 0 5 * * ?")
	public void indirectCompensatory() {

		logger.info("自动还款，代偿人间接代偿开始...start...");
		try {

			// 项目还款计划.
			WloanTermProjectPlan wloanTermProjectPlan = new WloanTermProjectPlan();
			// 项目联查条件.
			WloanTermProject wloanTermProject = new WloanTermProject();
			// 标的流转状态：还款中.
			wloanTermProject.setState(WloanTermProjectService.REPAYMENT);
			// 标的产品类型：供应链标的.
			wloanTermProject.setProjectProductType(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2);
			wloanTermProjectPlan.setWloanTermProject(wloanTermProject);
			// 状态：还款中.
			wloanTermProjectPlan.setState(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_STATE_1);
			// 还款日期开始时间.
			wloanTermProjectPlan.setBeginDate(DateUtils.formatDate(new Date(), "yyyy-MM-dd") + " 00:00:00");
			// 还款日期结束时间.
			wloanTermProjectPlan.setEndDate(DateUtils.formatDate(new Date(), "yyyy-MM-dd") + " 23:59:59");
			// 当天的项目待还款计划列表.
			List<WloanTermProjectPlan> projectPlans = wloanTermProjectPlanDao.findList(wloanTermProjectPlan);
			// 没有项目待还款计划.
			if (projectPlans != null && projectPlans.size() == 0) {
				logger.info("自动还款，代偿人间接代偿-没有待还标的还款计划......");
				return; // 结束当前方法.
			}

			/**
			 * 封装保存当前批次还款中所有代偿人的账户可用余额.
			 */
			// 1）本批次，代偿人账户ID列表，未去重
			List<String> repayUserAccountIds = new ArrayList<String>();
			for (WloanTermProjectPlan projectPlan : projectPlans) {
				WloanTermProject loanProject = wloanTermProjectService.get(projectPlan.getWloanTermProject().getId());
				if (null != loanProject) {
					CreditUserInfo creditUserInfo = creditUserInfoService.get(loanProject.getReplaceRepayId());
					if (null != creditUserInfo) {
						CreditUserAccount creditUserAccount = creditUserAccountDao.get(creditUserInfo.getAccountId());
						if (null != creditUserAccount) {
							repayUserAccountIds.add(creditUserAccount.getId());
						}
					}
				}
			}
			logger.info("本批次，代偿人账户ID列表-未去重:{}", repayUserAccountIds.toString());
			// 2）本批次，代偿人账户ID列表，去重后
			List<String> pastLeepUserAccountIds = pastLeep(repayUserAccountIds);
			logger.info("本批次，代偿人账户ID列表-去重后:{}", pastLeepUserAccountIds.toString());
			// 3）去重后的账户ID列表，添加对应的可用余额，操作代偿人授权预处理时，校验账户余额是否充足
			List<RepayAvailableAmount> repayAvailableAmountList = new ArrayList<RepayAvailableAmount>();
			RepayAvailableAmount repayAvailableAmount = null;
			for (String accountId : pastLeepUserAccountIds) {
				repayAvailableAmount = new RepayAvailableAmount();
				CreditUserAccount creditUserAccount = creditUserAccountDao.get(accountId);
				if (creditUserAccount != null) {
					repayAvailableAmount.setUserAccountId(creditUserAccount.getId());
					repayAvailableAmount.setAvailableAmount(NumberUtils.scaleDouble(creditUserAccount.getAvailableAmount()));
					repayAvailableAmountList.add(repayAvailableAmount);
				}
			}
			logger.info("本批次，代偿人账户ID及账户余额列表:{}", repayAvailableAmountList.toString());

			// 遍历标的还款计划
			for (WloanTermProjectPlan proPlan : projectPlans) {

				// 防止二次批量还款交易
				if ("TRUE".equals(proPlan.getOrderStatus())) {
					logger.info("自动还款，间接代偿确认，进入二次授权批量交易，结束本次任务......");
					// 结束本次循环.
					continue;
				}

				String projectPlanId = proPlan.getId(); // 标的还款计划ID
				String collaboratorSourcePlatformUserNo = ""; // 合作机构（核心企业）方平台用户编号
				String collaboratorAccountId = ""; // 合作机构（核心企业）方平台账户ID
				String borrowersSourcePlatformUserNo = ""; // 借款（供应商）方平台用户编号
				String projectNo = ""; // 标的号，平台的标的编号
				String requestNo = IdGen.uuid(); // 授权预处理，请求流水号，唯一标识
				String proid = proPlan.getWloanTermProject().getId(); // 标的ID
				Date repaymentDate = proPlan.getRepaymentDate(); // 本期标的还款日
				Double userPlanRepayAmount = 0D; // 本期用户还款计划还款合计总额
				Double proAamount = 0D; // 本期还款的标的募集金额
				String subOrderId = proPlan.getSubOrderId(); // 标的还款计划，还本金时用到
				logger.info("本期标的还款计划，还款金额:{}", NumberUtils.scaleDouble(proPlan.getInterest()));
				String repayType = proPlan.getPrincipal(); // 还款类型
				WloanTermProject repaymentProject = wloanTermProjectService.get(proid);
				if (null != repaymentProject) {
					proAamount = repaymentProject.getAmount();
					logger.info("本期还款中标的募集金额:{}", NumberUtils.scaleDoubleStr(proAamount));
					projectNo = repaymentProject.getSn(); // 标的编号
					collaboratorSourcePlatformUserNo = repaymentProject.getReplaceRepayId(); // 代偿人
					CreditUserInfo replaceRepayUserInfo = creditUserInfoService.get(collaboratorSourcePlatformUserNo);
					if (null != replaceRepayUserInfo) {
						collaboratorAccountId = replaceRepayUserInfo.getAccountId();
					}
					WloanSubject wloanSubject = wloanSubjectService.get(repaymentProject.getSubjectId());
					if (null != wloanSubject) {
						borrowersSourcePlatformUserNo = wloanSubject.getLoanApplyId(); // 借款人
					}

					if (WloanTermProject.IS_REPLACE_REPAY_1.equals(repaymentProject.getIsReplaceRepay())) { // 是否代偿，是.
						/**
						 * 间接代偿，数据封装
						 */
						String batchNo = IdGen.uuid(); // Y 批次号
						List<SyncTransaction> bizDetails = new ArrayList<SyncTransaction>(); // 交易明细
						List<AsyncTransactionLog> atls = new ArrayList<AsyncTransactionLog>(); // 批量交易订单日志
						// 查询本期标的还款的所有用户还款计划
						WloanTermUserPlan entity = new WloanTermUserPlan();
						entity.setState(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_2);
						WloanTermProject repayProject = new WloanTermProject();
						repayProject.setId(proid);
						entity.setWloanTermProject(repayProject);
						entity.setRepaymentDate(repaymentDate);
						List<WloanTermUserPlan> userPlanList = wloanTermUserPlanDao.findList(entity);
						WloanTermUserPlan userPlan = null;
						SyncTransaction syncTransaction = null;
						SyncTransactionDetail std = null;
						LmTransaction lt = null;
						AsyncTransactionLog atl = null; // 批量交易日志，交易失败逻辑处理
						long currentTimeMillis = System.currentTimeMillis();
						// 本期用户还款计划业务明细
						for (int i = 0; i < userPlanList.size(); i++) {
							userPlan = userPlanList.get(i);
							String userPlanId = userPlan.getId();
							userPlanRepayAmount = NumberUtils.add(userPlanRepayAmount, userPlan.getInterest());
							String tradeRequestNo = IdGen.uuid();
							// Y 交易明细
							syncTransaction = new SyncTransaction();
							syncTransaction.setRequestNo(tradeRequestNo); // Y 交易明细订单号
							syncTransaction.setTradeType(BizTypeEnum.INDIRECT_COMPENSATORY.getValue()); // Y 交易类型
							syncTransaction.setProjectNo(projectNo); // N 标的编号
							syncTransaction.setSaleRequestNo(null); // N 债权出让请求流水号
							// Y 业务明细
							List<SyncTransactionDetail> details = new ArrayList<SyncTransactionDetail>();
							// 间接代偿，业务明细最少两条
							for (int j = 0; j < 2; j++) {
								if (j == 0) { // 代偿人[代偿确认]借款人
									std = new SyncTransactionDetail();
									std.setBizType(BusinessTypeEnum.COMPENSATORY.getValue()); // Y 业务类型
									std.setFreezeRequestNo(requestNo); // N 授权预处理，请求流水号
									std.setSourcePlatformUserNo(collaboratorSourcePlatformUserNo); // N 出款方（代偿人）用户编号
									std.setTargetPlatformUserNo(borrowersSourcePlatformUserNo); // N 收款方（借款人）用户编号
									if (WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_PRINCIPAL_2.equals(userPlan.getPrincipal())) {
										std.setAmount(NumberUtils.scaleDoubleStr(userPlan.getInterest())); // Y 本息和
										std.setIncome(NumberUtils.scaleDoubleStr(userPlan.getInterest())); // N 利息
										std.setShare(null); // N 债权份额（债权认购且需校验债权关系的必传）
										std.setCustomDefine(null); // N 平台商户自定义参数，平台交易时传入的自定义参数
										std.setRemark("代偿人[代偿确认]借款人，付息");// N
									} else if (WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_PRINCIPAL_1.equals(userPlan.getPrincipal())) {
										std.setAmount(NumberUtils.scaleDoubleStr(userPlan.getInterest())); // Y 本息和
										std.setIncome(NumberUtils.scaleDoubleStr(NumberUtils.subtract(userPlan.getInterest(), userPlan.getWloanTermInvest().getAmount()))); // N 利息
										std.setShare(null); // N 债权份额（债权认购且需校验债权关系的必传）
										std.setCustomDefine(null); // N 平台商户自定义参数，平台交易时传入的自定义参数
										std.setRemark("代偿人[代偿确认]借款人，还本付息");// N
									}
									details.add(std);
								}
								if (j == 1) { // 借款人[代偿确认]出借人
									std = new SyncTransactionDetail();
									std.setBizType(BusinessTypeEnum.COMPENSATORY.getValue()); // Y 业务类型
									std.setFreezeRequestNo(requestNo); // N 授权预处理，请求流水号
									std.setSourcePlatformUserNo(borrowersSourcePlatformUserNo); // N 出款方（借款人）用户编号
									std.setTargetPlatformUserNo(userPlan.getUserInfo().getId()); // N 收款方（出借人）用户编号
									if (WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_PRINCIPAL_2.equals(userPlan.getPrincipal())) {
										std.setAmount(NumberUtils.scaleDoubleStr(userPlan.getInterest())); // Y 本息和
										std.setIncome(NumberUtils.scaleDoubleStr(userPlan.getInterest())); // N 利息
										std.setShare(null); // N 债权份额（债权认购且需校验债权关系的必传）
										std.setCustomDefine(null); // N 平台商户自定义参数，平台交易时传入的自定义参数
										std.setRemark("借款人[代偿确认]出借人，付息");// N
									} else if (WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_PRINCIPAL_1.equals(userPlan.getPrincipal())) {
										std.setAmount(NumberUtils.scaleDoubleStr(userPlan.getInterest())); // Y 本息和
										std.setIncome(NumberUtils.scaleDoubleStr(NumberUtils.subtract(userPlan.getInterest(), userPlan.getWloanTermInvest().getAmount()))); // N 利息
										std.setShare(null); // N 债权份额（债权认购且需校验债权关系的必传）
										std.setCustomDefine(null); // N 平台商户自定义参数，平台交易时传入的自定义参数
										std.setRemark("借款人[代偿确认]出借人，还本付息");// N
									}
									details.add(std);
								}
							}
							syncTransaction.setDetails(details); // Y
							bizDetails.add(syncTransaction);
							// 批量交易日志，交易失败逻辑处理
							atl = new AsyncTransactionLog();
							atl.setId(IdGen.uuid());
							atl.setAsyncRequestNo(tradeRequestNo); // 异步通知时的交易明细订单号
							atl.setFreezeRequestNo(userPlanId); // 用户还款计划订单id
							atl.setBizType(BizTypeEnum.INDIRECT_COMPENSATORY.getValue()); // 交易类型：还款
							atl.setBizOrigin(BizOriginEnum.DISPERSION.getValue()); // 业务来源
							atl.setStatus(AsyncTransactionLogStatusEnum.INIT.getValue()); // 处理中
							currentTimeMillis = currentTimeMillis + 1000;
							atl.setCreateDate(new Date(currentTimeMillis));
							atl.setUpdateDate(new Date(currentTimeMillis));
							atls.add(atl);
						}
						logger.info("[间接代偿]本期还款合计总额:{}", NumberUtils.scaleDoubleStr(userPlanRepayAmount));

						// 程序执行结束标志.
						boolean projectPlansFlag = false;
						// 校验代偿人账户可用余额是否可以抵扣本期还款金额
						for (RepayAvailableAmount raa : repayAvailableAmountList) {
							if (raa.getUserAccountId().equals(collaboratorAccountId)) { // 本期代偿还款人
								if (NumberUtils.scaleDouble(raa.getAvailableAmount()) < NumberUtils.scaleDouble(userPlanRepayAmount)) { // 代偿人账户可用余额小于本期还款金额
									projectPlansFlag = true;
									logger.info("[间接代偿]本期:{}还款，代偿人账户可用余额不足......", projectPlanId);
									// 风控短消息提醒.
									weixinSendTempMsgService.ztmgSendWarnInfoMsg(CRO_MOBILE_PHONE, CRO_NAME, replaceRepayUserInfo.getEnterpriseFullName(), WeixinSendTempMsgService.ZTMG_SEND_WARN_INFO_MSG_1);
									// 财务短消息提醒.
									weixinSendTempMsgService.ztmgSendWarnInfoMsg(CFO_MOBILE_PHONE, CFO_NAME, replaceRepayUserInfo.getEnterpriseFullName(), WeixinSendTempMsgService.ZTMG_SEND_WARN_INFO_MSG_2);
									break;
								} else {
									// 可用余额逻辑扣除还款金额.
									raa.setAvailableAmount(NumberUtils.scaleDouble(NumberUtils.scaleDouble(raa.getAvailableAmount()) - NumberUtils.scaleDouble(userPlanRepayAmount)));
									break;
								}
							}
						}

						if (projectPlansFlag) {
							// 结束本次循环.
							continue;
						}

						// 还款授权预处理
						UserAuthorization userAuthorization = new UserAuthorization();
						userAuthorization.setRequestNo(requestNo); // Y 授权预处理，请求流水号
						userAuthorization.setPlatformUserNo(collaboratorSourcePlatformUserNo); // Y 平台用户编号（代偿人）
						userAuthorization.setOriginalRechargeNo(null); // N 关联充值请求流水号（原充值成功请求流水号）
						userAuthorization.setBizType(ConfirmTradeTypEnum.COMPENSATORY.getValue());// Y 见【预处理业务类型】若传入关联请求流水号，则固定为TENDER
						userAuthorization.setAmount(NumberUtils.scaleDoubleStr(userPlanRepayAmount)); // Y 冻结金额，本次还款合计总额
						userAuthorization.setPreMarketingAmount(null); // N 预备使用的红包金额，只记录不冻结，仅限出借业务类型
						if (WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_0.equals(repayType)) {
							userAuthorization.setRemark("[间接代偿]代偿人付息");
						} else if (WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1.equals(repayType)) {
							userAuthorization.setRemark("[间接代偿]代偿人还本付息");
						}
						userAuthorization.setProjectNo(projectNo); // Y 标的号, 若传入关联充值请求流水号，则标的号固定为充值请求传入的标的号
						userAuthorization.setShare(null); // N 购买债转份额，业务类型为债权认购时，需要传此参数 。
						userAuthorization.setCreditsaleRequestNo(null);// N 债权出让请求流水号，只有债权认购业务需填此参数
						Map<String, String> userAutoPreTransactionMap = lanMaoAuthorizationService.userAutoPreTransaction(userAuthorization);
						// 调用成功且业务处理成功
						if (BusinessStatusEnum.SUCCESS.getValue().equals(userAutoPreTransactionMap.get("status")) && "0".equals(userAutoPreTransactionMap.get("code"))) {
							logger.info("懒猫代偿人间接代偿授权预处理:{}", BusinessStatusEnum.SUCCESS.getValue());
							lt = new LmTransaction();
							lt.setId(IdGen.uuid());
							lt.setServiceName(ServiceNameEnum.USER_AUTO_PRE_TRANSACTION.getValue());
							lt.setBatchNo(batchNo); // 批次号
							lt.setRequestNo(requestNo); // Y 授权预处理，请求流水号
							lt.setPlatformUserNo(collaboratorSourcePlatformUserNo); // Y 平台用户编号
							lt.setTradeType(BizTypeEnum.INDIRECT_COMPENSATORY.getValue()); // 交易类型
							lt.setProjectNo(projectNo);// 标的编号
							lt.setCode(userAutoPreTransactionMap.get("code"));
							lt.setStatus(userAutoPreTransactionMap.get("status"));
							currentTimeMillis = currentTimeMillis + 1000;
							lt.setCreateDate(new Date(currentTimeMillis));
							lt.setUpdateDate(new Date(currentTimeMillis));
							int insertLtFlag = lmTransactionDao.insert(lt);
							logger.info("间接代偿授权预处理留存插入:{}", insertLtFlag == 1 ? "成功" : "失败");
							/**
							 * 还款确认，批量交易
							 */
							AsyncTransaction asyncTransaction = new AsyncTransaction();
							asyncTransaction.setBatchNo(batchNo);
							asyncTransaction.setBizDetails(bizDetails);
							Map<String, String> asyncTransactionMap = lanMaoAsyncTransactionService.asyncTransaction(asyncTransaction);
							if (BusinessStatusEnum.SUCCESS.getValue().equals(asyncTransactionMap.get("status")) && "0".equals(asyncTransactionMap.get("code"))) {
								logger.info("间接代偿，批量交易:{}", BusinessStatusEnum.SUCCESS.getValue());
								// 平台留存
								lt = new LmTransaction();
								lt.setId(IdGen.uuid());
								lt.setServiceName(ServiceNameEnum.ASYNC_TRANSACTION.getValue());
								lt.setBatchNo(batchNo); // 批次号
								lt.setSourcePlatformUserNo(collaboratorSourcePlatformUserNo); // 出款方
								lt.setTargetPlatformUserNo(borrowersSourcePlatformUserNo); // 收款方
								lt.setCode(asyncTransactionMap.get("code"));
								lt.setStatus(asyncTransactionMap.get("status"));
								lt.setRemarks("间接代偿，还款，批量交易");
								lt.setTradeType(BizTypeEnum.INDIRECT_COMPENSATORY.getValue());
								lt.setProjectNo(projectNo);// 标的编号
								currentTimeMillis = currentTimeMillis + 1000;
								lt.setCreateDate(new Date(currentTimeMillis));
								lt.setUpdateDate(new Date(currentTimeMillis));
								int insertCreditAssignmentFlag = lmTransactionDao.insert(lt);
								logger.info("间接代偿，还款，批量交易记录留存:{}", insertCreditAssignmentFlag == 1 ? "成功" : "失败");

								// 批量交易，交易日志留存
								for (AsyncTransactionLog atlNext : atls) {
									int insertAtlNextFlag = asyncTransactionLogDao.insert(atlNext);
									logger.info("间接代偿，批量交易日志，插入:{}", insertAtlNextFlag == 1 ? "成功" : "失败");
								}

								// 业务订单信息，任务调度轮询还款
								ZtmgOrderInfo zoi = null;
								if (WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1.equals(repayType)) { // 还本付息
									// 标的状态，已截标
									// -----------------------------------------------------------------------------
									/**
									Map<String, Object> modifyProjectMap = lanMaoProjectService.modifyProject(batchNo, projectNo, ProjectStatusEnum.FINISH.getValue());
									if (BusinessStatusEnum.SUCCESS.getValue().equals(modifyProjectMap.get("status")) && "0".equals(modifyProjectMap.get("code"))) {
										logger.info("代偿人间接代偿，最后一期间接代偿，标的生命周期已结束，截标成功......");
										// 平台留存
										lt = new LmTransaction();
										lt.setId(IdGen.uuid());
										lt.setServiceName(ServiceNameEnum.MODIFY_PROJECT.getValue());
										lt.setBatchNo(batchNo); // 批次号
										lt.setRequestNo(batchNo);
										lt.setPlatformUserNo(borrowersSourcePlatformUserNo);
										lt.setCode(asyncTransactionMap.get("code"));
										lt.setStatus(asyncTransactionMap.get("status"));
										lt.setRemarks("代偿人间接代偿，最后一期间接代偿，截标");
										lt.setProjectNo(projectNo);// 标的编号
										currentTimeMillis = currentTimeMillis + 1000;
										lt.setCreateDate(new Date(currentTimeMillis));
										lt.setUpdateDate(new Date(currentTimeMillis));
										int insertLmtFlag = lmTransactionDao.insert(lt);
										logger.info("代偿人间接代偿，最后一期间接代偿，变更标的记录留存:{}", insertLmtFlag == 1 ? "成功" : "失败");
									} else {
										logger.info("代偿人间接代偿，最后一期间接代偿，标的生命周期已结束，截标失败......");
										// 平台留存
										lt = new LmTransaction();
										lt.setId(IdGen.uuid());
										lt.setServiceName(ServiceNameEnum.MODIFY_PROJECT.getValue());
										lt.setBatchNo(batchNo); // 批次号
										lt.setRequestNo(batchNo);
										lt.setPlatformUserNo(borrowersSourcePlatformUserNo);
										lt.setCode(asyncTransactionMap.get("code"));
										lt.setStatus(asyncTransactionMap.get("status"));
										lt.setErrorCode(asyncTransactionMap.get("errorCode"));
										lt.setErrorMessage(asyncTransactionMap.get("errorMessage"));
										lt.setRemarks("代偿人间接代偿，最后一期间接代偿，截标");
										lt.setProjectNo(projectNo);// 标的编号
										currentTimeMillis = currentTimeMillis + 1000;
										lt.setCreateDate(new Date(currentTimeMillis));
										lt.setUpdateDate(new Date(currentTimeMillis));
										int insertLmtFlag = lmTransactionDao.insert(lt);
										logger.info("代偿人间接代偿，最后一期间接代偿，变更标的记录留存:{}", insertLmtFlag == 1 ? "成功" : "失败");
									}
									*/
									// -----------------------------------------------------------------------------
									// 分两次还款.
									for (int i = 1; i < 3; i++) {
										zoi = new ZtmgOrderInfo();
										if (i == 1) { // 还息.
											zoi.setId(IdGen.uuid());
											zoi.setMerchantId(Global.getConfigLanMao("platformNo")); // 平台编号
											zoi.setOrderId(projectPlanId); // 标的当期还款计划id
											zoi.setStatus("S"); // 状态，成功
											zoi.setType(ZtmgOrderInfoService.TYPE_2); // 代偿户.
											zoi.setState(ZtmgOrderInfoService.STATE_1); // 未还.
											currentTimeMillis = currentTimeMillis + 1000;
											zoi.setCreateDate(new Date(currentTimeMillis));
											zoi.setUpdateDate(new Date(currentTimeMillis));
											zoi.setRemarks("最后一期间接代偿，付息");
											int zoiFlag = ztmgOrderInfoDao.insert(zoi);
											logger.info("最后一期间接代偿，付息落单:{}", zoiFlag == 1 ? "成功" : "失败");
										} else if (i == 2) { // 还本
											currentTimeMillis = currentTimeMillis + 1000;
											zoi.setId(IdGen.uuid());
											zoi.setMerchantId(Global.getConfigLanMao("platformNo")); // 平台编号
											zoi.setOrderId(subOrderId); // 标的当前还款计划sub_order_id
											zoi.setStatus("S"); // 状态，成功
											zoi.setType(ZtmgOrderInfoService.TYPE_2); // 代偿户.
											zoi.setState(ZtmgOrderInfoService.STATE_1); // 未还.
											currentTimeMillis = currentTimeMillis + 1000;
											zoi.setCreateDate(new Date(currentTimeMillis));
											zoi.setUpdateDate(new Date(currentTimeMillis));
											zoi.setRemarks("最后一期间接代偿，还本");
											int zoiFlag = ztmgOrderInfoDao.insert(zoi);
											logger.info("最后一期间接代偿，还本落单:{}", zoiFlag == 1 ? "成功" : "失败");
										}
									}
								} else if (WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_0.equals(repayType)) { // 付息
									zoi = new ZtmgOrderInfo();
									zoi.setId(IdGen.uuid());
									zoi.setMerchantId(Global.getConfigLanMao("platformNo")); // 平台编号
									zoi.setOrderId(projectPlanId); // 标的当期还款计划id
									zoi.setStatus("S"); // 状态，成功
									zoi.setType(ZtmgOrderInfoService.TYPE_2); // 代偿户.
									zoi.setState(ZtmgOrderInfoService.STATE_1); // 未还.
									currentTimeMillis = currentTimeMillis + 1000;
									zoi.setCreateDate(new Date(currentTimeMillis));
									zoi.setUpdateDate(new Date(currentTimeMillis));
									zoi.setRemarks("间接代偿，付息");
									int zoiFlag = ztmgOrderInfoDao.insert(zoi);
									logger.info("间接代偿，付息落单:{}", zoiFlag == 1 ? "成功" : "失败");
								}
								int modifyOrderStatusFlag = wloanTermProjectPlanDao.modifyProjectPlanOrderStatus("TRUE", projectPlanId);
								logger.info("该期标的还款计划，已在存管行落单，订单状态更新:{}", modifyOrderStatusFlag == 1 ? "成功" : "失败");
								logger.info("自动还款，代偿人间接代偿结束...end...");
							} else {
								lt = new LmTransaction();
								lt.setId(IdGen.uuid());
								lt.setServiceName(ServiceNameEnum.ASYNC_TRANSACTION.getValue());
								lt.setBatchNo(batchNo); // 批次号
								lt.setRequestNo(requestNo); // Y 授权预处理，请求流水号
								lt.setSourcePlatformUserNo(collaboratorSourcePlatformUserNo); // 出款方
								lt.setTargetPlatformUserNo(borrowersSourcePlatformUserNo); // 收款方
								lt.setTradeType(BizTypeEnum.INDIRECT_COMPENSATORY.getValue()); // 交易类型
								lt.setProjectNo(projectNo);// 标的编号
								lt.setCode(asyncTransactionMap.get("code"));
								lt.setStatus(asyncTransactionMap.get("status"));
								lt.setErrorCode(asyncTransactionMap.get("errorCode"));
								lt.setErrorMessage(asyncTransactionMap.get("errorMessage"));
								currentTimeMillis = currentTimeMillis + 1000;
								lt.setCreateDate(new Date(currentTimeMillis));
								lt.setUpdateDate(new Date(currentTimeMillis));
								int insertAsyncTransactionFlag = lmTransactionDao.insert(lt);
								logger.info("间接代偿，批量交易留存插入:{}", insertAsyncTransactionFlag == 1 ? "成功" : "失败");
								logger.info("自动还款，代偿人间接代偿结束...end...");
							}
						} else {
							logger.info("懒猫代偿人间接代偿授权预处理:{}", BusinessStatusEnum.INIT.getValue());
							lt = new LmTransaction();
							lt.setId(IdGen.uuid());
							lt.setServiceName(ServiceNameEnum.USER_AUTO_PRE_TRANSACTION.getValue());
							lt.setBatchNo(batchNo); // 批次号
							lt.setRequestNo(requestNo); // Y 授权预处理，请求流水号
							lt.setPlatformUserNo(collaboratorSourcePlatformUserNo); // Y 平台用户编号
							lt.setTradeType(BizTypeEnum.INDIRECT_COMPENSATORY.getValue()); // 交易类型
							lt.setProjectNo(projectNo);// 标的编号
							lt.setCode(userAutoPreTransactionMap.get("code"));
							lt.setStatus(userAutoPreTransactionMap.get("status"));
							lt.setErrorCode(userAutoPreTransactionMap.get("errorCode"));
							lt.setErrorMessage(userAutoPreTransactionMap.get("errorMessage"));
							currentTimeMillis = currentTimeMillis + 1000;
							lt.setCreateDate(new Date(currentTimeMillis));
							lt.setUpdateDate(new Date(currentTimeMillis));
							int insertLtFlag = lmTransactionDao.insert(lt);
							logger.info("间接代偿授权预处理留存插入:{}", insertLtFlag == 1 ? "成功" : "失败");
							logger.info("自动还款，代偿人间接代偿结束...end...");
						}
					} else if (WloanTermProject.IS_REPLACE_REPAY_0.equals(repaymentProject.getIsReplaceRepay())) { // 是否代偿，否.
						logger.info("本期还款标的不支持代偿业务......");
						// 结束本次循环.
						continue;
					}
				}
			}
			logger.info("自动还款，代偿人间接代偿结束...end...");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("自动还款，代偿人间接代偿结束...end...");
		}

	}

	/**
	 * 
	 * methods: pastLeep <br>
	 * description: String List 去重 <br>
	 * author: Roy <br>
	 * date: 2019年10月11日 下午3:38:06
	 * 
	 * @param list
	 * @return
	 */
	public static List<String> pastLeep(List<String> list) {

		List<String> listNew = new ArrayList<String>();
		Set<String> set = new HashSet<String>();
		for (String str : list) {
			if (set.add(str)) {
				listNew.add(str);
			}
		}
		return listNew;
	}

}
