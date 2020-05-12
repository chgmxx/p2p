package com.power.platform.lanmao.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.power.platform.cgb.dao.ZtmgOrderInfoDao;
import com.power.platform.cgb.entity.ZtmgOrderInfo;
import com.power.platform.cgb.service.ZtmgOrderInfoService;
import com.power.platform.common.config.Global;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.common.web.BaseController;
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
import com.power.platform.userinfo.dao.UserInfoDao;

/**
 * 
 * class: LanMaoRepaymentController <br>
 * description: 懒猫还款确认，借款人自动授权预处理 <br>
 * author: Roy <br>
 * date: 2019年10月3日 下午9:40:04
 */
@Controller
@RequestMapping(value = "${adminPath}/lm/p2p/repayment")
public class LanMaoRepaymentController extends BaseController {

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
	private LanMaoProjectService lanMaoProjectService;

	/**
	 * 
	 * methods: repay <br>
	 * description: 还款，主要用于借款人还款给出借人的业务场景； <br>
	 * author: Roy <br>
	 * date: 2019年10月3日 下午4:14:13
	 * 
	 * @param repaymentDate
	 *            还款日期
	 * @param ip
	 *            IP
	 * @param projectPlanId
	 *            标的还款计划id
	 * @param proid
	 *            标的id
	 * @param type
	 *            标的产品类型
	 * @param model
	 * @param redirectAttributes
	 * @return
	 * @throws Exception
	 */
	@RequiresPermissions("lm:repayment:edit")
	@RequestMapping(value = "repayment")
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public synchronized String repayment(Date repaymentDate, String ip, String projectPlanId, String proid, String type, Model model, RedirectAttributes redirectAttributes) throws Exception {

		logger.info("懒猫借款人还款 ...start...");

		Double amount = 0D; // 本期还款合计总额
		Double userPlanRepayAmount = 0D; // 本期用户还款计划还款合计总额
		Double availableAmount = 0D; // 借款人可用余额
		String repayType = ""; // 还款类型
		String requestNo = ""; // 授权预处理，请求流水号
		String subOrderId = "";
		String sourcePlatformUserNo = ""; // 出款方平台用户编号
		String projectNo = ""; // 标的号，平台的标的编号

		try {
			// 标的还款计划
			WloanTermProjectPlan projectPlan = wloanTermProjectPlanService.get(projectPlanId);
			if (null != projectPlan) {
				// 防止操作人员二次批量还款交易
				if ("TRUE".equals(projectPlan.getOrderStatus())) {
					addMessage(redirectAttributes, "该期标的还款，已在存管行完成落单，系统定时轮询上账，请耐心等待 ......");
					if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) {
						return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findAxtUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
					} else if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2)) {
						return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
					}
				}
				// requestNo = projectPlan.getId(); // 标的还款计划，经测试，该流水号无效，流水号需要保证唯一
				requestNo = IdGen.uuid();
				subOrderId = projectPlan.getSubOrderId();
				amount = NumberUtils.scaleDouble(projectPlan.getInterest());
				logger.info("本期标的还款计划，还款金额:{}", amount);
				repayType = projectPlan.getPrincipal();
			} else {
				logger.info("该批次标的还款计划不存在......");
				addMessage(redirectAttributes, "该批次标的还款计划不存在......");
				if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) {
					return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findAxtUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
				} else if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2)) {
					return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
				}
			}
			// 标的详情
			WloanTermProject loanProject = wloanTermProjectService.get(proid);
			if (null != loanProject) {
				projectNo = loanProject.getSn();
				WloanSubject wloanSubject = wloanSubjectService.get(loanProject.getSubjectId());
				if (null != wloanSubject) { // 融资主体.
					// 借款人.
					CreditUserInfo creditUserInfo = creditUserInfoService.get(wloanSubject.getLoanApplyId());
					if (null != creditUserInfo) {
						sourcePlatformUserNo = creditUserInfo.getId();
						CreditUserAccount creditUserAccount = creditUserAccountDao.get(creditUserInfo.getAccountId());
						if (null != creditUserAccount) { // 借款人账户.
							// 可用余额.
							availableAmount = NumberUtils.scaleDouble(creditUserAccount.getAvailableAmount());
							if (null != availableAmount) {
								if (availableAmount < amount) {
									logger.info("借款人账户余额不足......");
									addMessage(redirectAttributes, "借款人账户余额不足......");
									if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) {
										return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findAxtUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
									} else if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2)) {
										return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
									}
								}
							} else {
								logger.info("借款人账户余额不足......");
								addMessage(redirectAttributes, "借款人账户余额不足......");
								if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) {
									return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findAxtUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
								} else if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2)) {
									return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
								}
							}
						}
					}
				} else {
					addMessage(redirectAttributes, "融资主体不存在......");
					if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) {
						return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findAxtUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
					} else if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2)) {
						return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
					}
				}
			} else {
				addMessage(redirectAttributes, "该标的不存在......");
				if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) {
					return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findAxtUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
				} else if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2)) {
					return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
				}
			}

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
				std.setSourcePlatformUserNo(sourcePlatformUserNo); // N 出款方用户编号
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

			// 还款授权预处理
			UserAuthorization userAuthorization = new UserAuthorization();
			userAuthorization.setRequestNo(requestNo); // Y 授权预处理，请求流水号
			userAuthorization.setPlatformUserNo(sourcePlatformUserNo); // Y 平台用户编号
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
				lt.setPlatformUserNo(sourcePlatformUserNo); // Y 平台用户编号
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
					lt.setPlatformUserNo(sourcePlatformUserNo);
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
							lt.setPlatformUserNo(sourcePlatformUserNo);
							lt.setCode(asyncTransactionMap.get("code"));
							lt.setStatus(asyncTransactionMap.get("status"));
							lt.setRemarks("借款人还款，最后一期还款，截标");
							lt.setProjectNo(projectNo);// 标的编号
							currentTimeMillis = currentTimeMillis + 1000;
							lt.setCreateDate(new Date(currentTimeMillis));
							lt.setUpdateDate(new Date(currentTimeMillis));
							int insertLmtFlag = lmTransactionDao.insert(lt);
							logger.info("借款人还款，最后一期还款，变更标的记录留存:{}", insertLmtFlag == 1 ? "成功" : "失败");
						} else {
							logger.info("借款人还款，最后一期还款，标的生命周期已结束，截标失败......");
							// 平台留存
							lt = new LmTransaction();
							lt.setId(IdGen.uuid());
							lt.setServiceName(ServiceNameEnum.MODIFY_PROJECT.getValue());
							lt.setBatchNo(batchNo); // 批次号
							lt.setRequestNo(batchNo);
							lt.setPlatformUserNo(sourcePlatformUserNo);
							lt.setCode(asyncTransactionMap.get("code"));
							lt.setStatus(asyncTransactionMap.get("status"));
							lt.setErrorCode(asyncTransactionMap.get("errorCode"));
							lt.setErrorMessage(asyncTransactionMap.get("errorMessage"));
							lt.setRemarks("借款人还款，最后一期还款，截标");
							lt.setProjectNo(projectNo);// 标的编号
							currentTimeMillis = currentTimeMillis + 1000;
							lt.setCreateDate(new Date(currentTimeMillis));
							lt.setUpdateDate(new Date(currentTimeMillis));
							int insertLmtFlag = lmTransactionDao.insert(lt);
							logger.info("借款人还款，最后一期还款，变更标的记录留存:{}", insertLmtFlag == 1 ? "成功" : "失败");
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
					logger.info("懒猫借款人还款 ...end...");
					addMessage(redirectAttributes, "还款确认，批量交易成功......");
					if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) {
						return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findAxtUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
					} else {
						return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
					}
				} else {
					lt = new LmTransaction();
					lt.setId(IdGen.uuid());
					lt.setServiceName(ServiceNameEnum.ASYNC_TRANSACTION.getValue());
					lt.setBatchNo(batchNo); // 批次号
					lt.setRequestNo(requestNo); // Y 授权预处理，请求流水号
					lt.setPlatformUserNo(sourcePlatformUserNo);
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
					logger.info("懒猫借款人还款 ...end...");
					addMessage(redirectAttributes, "还款确认，批量交易失败，errorCode:" + asyncTransactionMap.get("errorCode") + "，errorMessage:" + asyncTransactionMap.get("errorMessage"));
					if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) {
						return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findAxtUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
					} else {
						return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
					}
				}
			} else {
				logger.info("懒猫借款人授权预处理:{}", BusinessStatusEnum.INIT.getValue());
				lt = new LmTransaction();
				lt.setId(IdGen.uuid());
				lt.setServiceName(ServiceNameEnum.USER_AUTO_PRE_TRANSACTION.getValue());
				lt.setBatchNo(batchNo); // 批次号
				lt.setRequestNo(requestNo); // Y 授权预处理，请求流水号
				lt.setPlatformUserNo(sourcePlatformUserNo); // Y 平台用户编号
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
				logger.info("懒猫借款人还款 ...end...");
				addMessage(redirectAttributes, "借款人授权预处理INIT，errorCode:" + userAutoPreTransactionMap.get("errorCode") + "，errorMessage:" + userAutoPreTransactionMap.get("errorMessage"));
				if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) {
					return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findAxtUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
				} else {
					return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) {
			return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findAxtUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
		} else {
			return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
		}
	}
}
