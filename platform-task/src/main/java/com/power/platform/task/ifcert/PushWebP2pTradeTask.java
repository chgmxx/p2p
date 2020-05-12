package com.power.platform.task.ifcert;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cert.open.CertToolV1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.power.platform.cgb.dao.ZtmgOrderInfoDao;
import com.power.platform.cgb.entity.CgbUserTransDetail;
import com.power.platform.cgb.entity.ZtmgOrderInfo;
import com.power.platform.cgb.service.CgbUserTransDetailService;
import com.power.platform.cgb.service.ZtmgOrderInfoService;
import com.power.platform.cgbpay.config.ServerURLConfig;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.ifcert.dao.IfCertUserInfoDao;
import com.power.platform.ifcert.entity.IfCertUserInfo;
import com.power.platform.ifcert.lendparticulars.service.LendParticularsDataAccessService;
import com.power.platform.ifcert.transact.service.TransactDataAccessService;
import com.power.platform.ifcert.userInfo.service.IfcertUserInfoDataAccessService;
import com.power.platform.ifcert.utils.HashAndSaltUtil;
import com.power.platform.plan.dao.WloanTermProjectPlanDao;
import com.power.platform.plan.dao.WloanTermUserPlanDao;
import com.power.platform.plan.entity.WloanTermProjectPlan;
import com.power.platform.plan.entity.WloanTermUserPlan;
import com.power.platform.plan.service.WloanTermProjectPlanService;
import com.power.platform.regular.dao.WloanSubjectDao;
import com.power.platform.regular.entity.WloanSubject;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.userinfo.dao.UserInfoDao;
import com.power.platform.userinfo.entity.UserInfo;

/**
 * 
 * class: PushWebP2pTradeTask <br>
 * description: 推送交易流水、投资明细. <br>
 * author: Roy <br>
 * date: 2019年7月23日 下午4:17:09
 */
@Service
@Lazy(false)
public class PushWebP2pTradeTask {

	public static final Logger log = LoggerFactory.getLogger(PushWebP2pTradeTask.class);

	@Autowired
	private CgbUserTransDetailService cgbUserTransDetailService;
	@Autowired
	private TransactDataAccessService transactDataAccessService;
	@Autowired
	private IfCertUserInfoDao ifCertUserInfoDao;
	@Autowired
	private WloanSubjectDao wloanSubjectDao;
	@Autowired
	private IfcertUserInfoDataAccessService ifcertUserInfoDataAccessService;
	@Autowired
	private ZtmgOrderInfoDao ztmgOrderInfoDao;
	@Autowired
	private WloanTermProjectPlanDao wloanTermProjectPlanDao;
	@Autowired
	private WloanTermUserPlanDao wloanTermUserPlanDao;
	@Autowired
	private LendParticularsDataAccessService lendParticularsDataAccessService;
	@Autowired
	private UserInfoDao userInfoDao;
	/**
	 * 国家应急中心工具包.
	 */
	public static CertToolV1 tool = new CertToolV1();

	/**
	 * 
	 * methods: pushLendParticularsInvCashBack <br>
	 * description: 投资明细-出借人出借返现-实时推送（每30分钟执行）. <br>
	 * author: Roy <br>
	 * date: 2019年7月26日 上午11:02:18
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	@Scheduled(cron = "0 0/30 * * * ?")
	public void pushLendParticularsInvCashBack() {

		if (ServerURLConfig.IS_REAL_TIME_PUSH) {
			Map<String, Object> cashBackResult = new HashMap<String, Object>();
			try {
				boolean flag = true;
				int pageNo = 1;
				int pageSize = 3000;
				long currentTimeMillis = System.currentTimeMillis();
				while (flag) {
					Page<CgbUserTransDetail> page = new Page<CgbUserTransDetail>();
					page.setPageNo(pageNo);
					page.setPageSize(pageSize);
					CgbUserTransDetail cutd = new CgbUserTransDetail();
					cutd.setTrustType(CgbUserTransDetailService.TRUST_TYPE_10); // 抵用券.
					cutd.setBeginTransDate(DateUtils.calendarDate_Negative(new Date(), -1)); // 当前时间的前一天.
					cutd.setEndTransDate(new Date()); // 结束于当前查询时间.
					List<Integer> states = new ArrayList<Integer>();
					states.add(CgbUserTransDetailService.TRUST_STATE_1); // 处理中.
					states.add(CgbUserTransDetailService.TRUST_STATE_2); // 成功.
					cutd.setStates(states);
					Page<CgbUserTransDetail> cutdPage = cgbUserTransDetailService.findPage(page, cutd);
					log.info("投资明细-出借返现-当前页:{}", pageNo);
					log.info("投资明细-出借返现-最后页:{}", cutdPage.getLast());
					List<CgbUserTransDetail> cutdList = cutdPage.getList();
					System.out.println("投资明细-出借返现-集合大小：" + cutdList.size());
					if (cutdList != null && cutdList.size() > 0) {
						currentTimeMillis = currentTimeMillis + 1;
						cashBackResult = lendParticularsDataAccessService.pushLendParticulars(cutdList, currentTimeMillis);
						log.info("投资明细-出借返现接口-响应信息：{}", JSON.toJSONString(cashBackResult));
						pageNo = pageNo + 1; // next page.
						if (pageNo > cutdPage.getLast()) {
							flag = false;
						}
					} else {
						flag = false;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			log.warn("实时推送未开启，请联系程序猿小哥哥 ...");
		}
	}

	/**
	 * 
	 * methods: pushLendParticularsInvTakeBackInterestAndPrincipal <br>
	 * description: 投资明细-出借人收回利息/收回本息-实时推送（每晚23:00执行）. <br>
	 * author: Roy <br>
	 * date: 2019年7月25日 下午4:54:46
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	@Scheduled(cron = "0 5 23 * * ?")
	public void pushLendParticularsInvTakeBackInterestAndPrincipal() {

		if (ServerURLConfig.IS_REAL_TIME_PUSH) {
			// 付息结果集.
			Map<String, Object> takeBackInterestResult = new HashMap<String, Object>();
			// 还本结果集.
			Map<String, Object> takeBackPrincipalResult = new HashMap<String, Object>();
			long currentTimeMillis = System.currentTimeMillis();
			try {
				// 出借人收回利息，用户出借还款计划.
				List<WloanTermUserPlan> interestUserPlanList = new ArrayList<WloanTermUserPlan>();
				// 出借人收回本金，用户出借还款计划.
				List<WloanTermUserPlan> principalUserPlanList = new ArrayList<WloanTermUserPlan>();
				// 查询落单且完成还款的订单.
				ZtmgOrderInfo zoi = new ZtmgOrderInfo();
				zoi.setState(ZtmgOrderInfoService.STATE_2); // 已还.
				zoi.setBeginUpdateDate(DateUtils.getStartTimeEveryday()); // 每天开始时间.
				zoi.setEndUpdateDate(DateUtils.getNowTime()); // 查询时间.
				List<ZtmgOrderInfo> zoiList = ztmgOrderInfoDao.findList(zoi);
				for (int i = 0; i < zoiList.size(); i++) {
					ZtmgOrderInfo z = zoiList.get(i);
					WloanTermProjectPlan interestProPlan = wloanTermProjectPlanDao.get(z.getOrderId());
					if (interestProPlan != null) { // 付息.
						if (WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_0.equals(interestProPlan.getPrincipal())) {
							WloanTermUserPlan userPlan = new WloanTermUserPlan();
							userPlan.setRepaymentDate(interestProPlan.getRepaymentDate()); // 该笔标的付息的还款日期.
							WloanTermProject project = new WloanTermProject(); // 标的信息，id.
							project.setId(interestProPlan.getProjectId());
							userPlan.setWloanTermProject(project);
							List<WloanTermUserPlan> interestUserPlans = wloanTermUserPlanDao.findList(userPlan);
							log.info("第{}笔散标付息订单，{}条用户出借还款计划！", (i + 1), interestUserPlans.size());
							for (WloanTermUserPlan interestUserPlan : interestUserPlans) {
								interestUserPlanList.add(interestUserPlan); // 添加客户收回利息还款计划.
							}
						}
					} else {
						WloanTermProjectPlan principalProPlan = wloanTermProjectPlanDao.getBySubOrderId(z.getOrderId());
						if (principalProPlan != null) { // 还本付息.
							if (WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1.equals(principalProPlan.getPrincipal())) {
								WloanTermUserPlan userPlan = new WloanTermUserPlan();
								userPlan.setRepaymentDate(principalProPlan.getRepaymentDate()); // 该笔标的还本的还款日期.
								WloanTermProject project = new WloanTermProject(); // 标的信息，id.
								project.setId(principalProPlan.getProjectId());
								userPlan.setWloanTermProject(project);
								List<WloanTermUserPlan> principalUserPlans = wloanTermUserPlanDao.findList(userPlan);
								log.info("第{}笔散标还本订单，{}条用户出借还款计划！", (i + 1), principalUserPlans.size());
								for (WloanTermUserPlan principalUserPlan : principalUserPlans) {
									principalUserPlanList.add(principalUserPlan); // 添加客户收回本金还款计划.
								}
							}
						}
					}
				}
				// 交易流水-出借人收回利息-实时.
				if (interestUserPlanList != null && interestUserPlanList.size() > 0) {
					currentTimeMillis = currentTimeMillis + 1;
					takeBackInterestResult = lendParticularsDataAccessService.pushLendParticularsInvTakeBackInterest(interestUserPlanList, currentTimeMillis);
					log.info("推送出借人收回利息-投资明细-实时，响应信息：{}", JSON.toJSONString(takeBackInterestResult));
				}
				// 交易流水-出借人收回本金-实时.
				if (principalUserPlanList != null && principalUserPlanList.size() > 0) {
					currentTimeMillis = currentTimeMillis + 1;
					takeBackPrincipalResult = lendParticularsDataAccessService.pushLendParticularsInvTakeBackPrincipal(principalUserPlanList, currentTimeMillis);
					log.info("推送出借人收回本息-投资明细-实时，响应信息：{}", JSON.toJSONString(takeBackPrincipalResult));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			log.warn("实时推送未开启，请联系程序猿小哥哥 ...");
		}
	}

	/**
	 * 
	 * methods: pushLendParticularsInvWithdraw <br>
	 * description: 投资明细-推送出借人提现明细-实时（每30分钟执行一次）辅推出借人用户信息. <br>
	 * author: Roy <br>
	 * date: 2019年7月25日 下午3:28:56
	 */
	@Scheduled(cron = "0 0/30 * * * ?")
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void pushLendParticularsInvWithdraw() {

		if (ServerURLConfig.IS_REAL_TIME_PUSH) {
			// 结果集.
			Map<String, Object> pushInvWithdrawResult = new HashMap<String, Object>();
			Map<String, Object> pushUserResult = new HashMap<String, Object>();
			try {
				boolean flag = true;
				int pageNo = 1;
				int pageSize = 3000;
				long currentTimeMillis = System.currentTimeMillis();
				while (flag) {
					Page<CgbUserTransDetail> page = new Page<CgbUserTransDetail>();
					page.setPageNo(pageNo);
					page.setPageSize(pageSize);
					CgbUserTransDetail cutd = new CgbUserTransDetail();
					cutd.setTrustType(CgbUserTransDetailService.TRUST_TYPE_1); // 提现.
					cutd.setBeginTransDate(DateUtils.calendarDate_Negative(new Date(), -1)); // 当前时间的前一天.
					cutd.setEndTransDate(new Date()); // 结束于当前查询时间.
					cutd.setState(CgbUserTransDetailService.TRUST_STATE_2);
					Page<CgbUserTransDetail> cutdPage = cgbUserTransDetailService.findPage(page, cutd);
					List<CgbUserTransDetail> cutdList = cutdPage.getList();
					log.info("出借人提现交易流水数量：{}", cutdList.size());
					log.info("当前页码：{}", pageNo);
					log.info("最后页码：{}", cutdPage.getLast());
					if (cutdList != null && cutdList.size() > 0) {
						currentTimeMillis = currentTimeMillis + 5;
						// 判断出借人用户信息是否推送.
						List<String> userIdList = new ArrayList<String>();
						for (int i = 0; i < cutdList.size(); i++) {
							CgbUserTransDetail creUserTrade = cutdList.get(i);
							IfCertUserInfo ifCertUserInfo = new IfCertUserInfo();
							// 用户信息.
							UserInfo user = userInfoDao.getCgb(creUserTrade.getUserId());
							if (user != null) {
								ifCertUserInfo.setUserIdcardHash(HashAndSaltUtil.tool.idCardHash(user.getCertificateNo()));
							}
							List<IfCertUserInfo> ifCertUserInfos = ifCertUserInfoDao.findList(ifCertUserInfo);
							if (ifCertUserInfos != null && ifCertUserInfos.size() > 0) {
								log.info("第{}条用户信息已推送，userIdcardHash:{}", i + 1, ifCertUserInfo.getUserIdcardHash());
							} else {
								if (user != null) {
									userIdList.add(user.getId()); // 出借人用户信息主键ID.
									log.info("第{}条用户信息待推送，userIdcardHash:{}", i + 1, ifCertUserInfo.getUserIdcardHash());
								}
							}
						}
						// 推送当前批次出借人用户信息.
						if (userIdList != null && userIdList.size() > 0) {
							pushUserResult = ifcertUserInfoDataAccessService.pushInvestUserInfo(userIdList);
							log.info("推送出借人用户信息-实时，pushUserResult:{}", pushUserResult);
						}
						// 推送当前批次出借人提现投资明细.
						pushInvWithdrawResult = lendParticularsDataAccessService.pushLendParticulars(cutdList, currentTimeMillis);
						log.info("推送出借人提现-投资明细，pushInvRechargeResult = {}", JSON.toJSONString(pushInvWithdrawResult));
						pageNo = pageNo + 1; // next page.
						if (pageNo > cutdPage.getLast()) {
							flag = false;
						}
					} else {
						flag = false;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			log.warn("实时推送未开启，请联系程序猿小哥哥 ...");
		}
	}

	/**
	 * 
	 * methods: pushLendParticularsInvRecharge <br>
	 * description: 投资明细-推送出借人充值明细-实时（每30分钟执行一次）辅推出借人用户信息. <br>
	 * author: Roy <br>
	 * date: 2019年7月25日 下午3:28:08
	 */
	@Scheduled(cron = "0 0/30 * * * ?")
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void pushLendParticularsInvRecharge() {

		if (ServerURLConfig.IS_REAL_TIME_PUSH) {
			// 结果集.
			Map<String, Object> pushInvRechargeResult = new HashMap<String, Object>();
			Map<String, Object> pushUserResult = new HashMap<String, Object>();
			try {
				boolean flag = true;
				int pageNo = 1;
				int pageSize = 3000;
				long currentTimeMillis = System.currentTimeMillis();
				while (flag) {
					Page<CgbUserTransDetail> page = new Page<CgbUserTransDetail>();
					page.setPageNo(pageNo);
					page.setPageSize(pageSize);
					CgbUserTransDetail cutd = new CgbUserTransDetail();
					cutd.setTrustType(CgbUserTransDetailService.TRUST_TYPE_0); // 充值.
					cutd.setBeginTransDate(DateUtils.calendarDate_Negative(new Date(), -1)); // 当前时间的前一天.
					cutd.setEndTransDate(new Date()); // 结束于当前查询时间.
					cutd.setState(CgbUserTransDetailService.TRUST_STATE_2);
					Page<CgbUserTransDetail> cutdPage = cgbUserTransDetailService.findPage(page, cutd);
					List<CgbUserTransDetail> cutdList = cutdPage.getList();
					log.info("出借人充值交易流水数量：{}", cutdList.size());
					log.info("当前页码：{}", pageNo);
					log.info("最后页码：{}", cutdPage.getLast());
					if (cutdList != null && cutdList.size() > 0) {
						currentTimeMillis = currentTimeMillis + 3;
						// 判断出借人用户信息是否推送.
						List<String> userIdList = new ArrayList<String>();
						for (int i = 0; i < cutdList.size(); i++) {
							CgbUserTransDetail creUserTrade = cutdList.get(i);
							IfCertUserInfo ifCertUserInfo = new IfCertUserInfo();
							// 用户信息.
							UserInfo user = userInfoDao.getCgb(creUserTrade.getUserId());
							if (user != null) {
								ifCertUserInfo.setUserIdcardHash(HashAndSaltUtil.tool.idCardHash(user.getCertificateNo()));
							}
							List<IfCertUserInfo> ifCertUserInfos = ifCertUserInfoDao.findList(ifCertUserInfo);
							if (ifCertUserInfos != null && ifCertUserInfos.size() > 0) {
								log.info("第{}条用户信息已推送，userIdcardHash:{}", i + 1, ifCertUserInfo.getUserIdcardHash());
							} else {
								if (user != null) {
									userIdList.add(user.getId()); // 出借人用户信息主键ID.
									log.info("第{}条用户信息待推送，userIdcardHash:{}", i + 1, ifCertUserInfo.getUserIdcardHash());
								}
							}
						}
						// 推送当前批次出借人用户信息.
						if (userIdList != null && userIdList.size() > 0) {
							pushUserResult = ifcertUserInfoDataAccessService.pushInvestUserInfo(userIdList);
							log.info("推送出借人用户信息-实时，pushUserResult:{}", pushUserResult);
						}
						// 推送当前批次出借人充值投资明细.
						pushInvRechargeResult = lendParticularsDataAccessService.pushLendParticulars(cutdList, currentTimeMillis);
						log.info("推送出借人充值-投资明细，pushInvRechargeResult = {}", JSON.toJSONString(pushInvRechargeResult));
						pageNo = pageNo + 1; // next page.
						if (pageNo > cutdPage.getLast()) {
							flag = false;
						}
					} else {
						flag = false;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			log.warn("实时推送未开启，请联系程序猿小哥哥 ...");
		}
	}

	/**
	 * 
	 * methods: pushTransactInvTakeBackInterestAndPrincipal <br>
	 * description: 交易流水-出借人收回利息/本息-实时（每天23点执行）. <br>
	 * author: Roy <br>
	 * date: 2019年7月24日 下午8:51:48
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	@Scheduled(cron = "0 15 23 * * ?")
	public void pushTransactInvTakeBackInterestAndPrincipal() {

		if (ServerURLConfig.IS_REAL_TIME_PUSH) {
			// 付息结果集.
			Map<String, Object> takeBackInterestResult = new HashMap<String, Object>();
			// 还本结果集.
			Map<String, Object> takeBackPrincipalResult = new HashMap<String, Object>();
			long currentTimeMillis = System.currentTimeMillis();
			try {
				// 出借人收回利息，用户出借还款计划.
				List<WloanTermUserPlan> interestUserPlanList = new ArrayList<WloanTermUserPlan>();
				// 出借人收回本金，用户出借还款计划.
				List<WloanTermUserPlan> principalUserPlanList = new ArrayList<WloanTermUserPlan>();
				// 查询落单且完成还款的订单.
				ZtmgOrderInfo zoi = new ZtmgOrderInfo();
				zoi.setState(ZtmgOrderInfoService.STATE_2); // 已还.
				zoi.setBeginUpdateDate(DateUtils.getStartTimeEveryday()); // 每天开始时间.
				zoi.setEndUpdateDate(DateUtils.getNowTime()); // 查询时间.
				List<ZtmgOrderInfo> zoiList = ztmgOrderInfoDao.findList(zoi);
				for (int i = 0; i < zoiList.size(); i++) {
					ZtmgOrderInfo z = zoiList.get(i);
					WloanTermProjectPlan interestProPlan = wloanTermProjectPlanDao.get(z.getOrderId());
					if (interestProPlan != null) { // 付息.
						if (WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_0.equals(interestProPlan.getPrincipal())) {
							WloanTermUserPlan userPlan = new WloanTermUserPlan();
							userPlan.setRepaymentDate(interestProPlan.getRepaymentDate()); // 该笔标的付息的还款日期.
							WloanTermProject project = new WloanTermProject(); // 标的信息，id.
							project.setId(interestProPlan.getProjectId());
							userPlan.setWloanTermProject(project);
							List<WloanTermUserPlan> interestUserPlans = wloanTermUserPlanDao.findList(userPlan);
							log.info("第{}笔散标付息订单，{}条用户出借还款计划！", (i + 1), interestUserPlans.size());
							for (WloanTermUserPlan interestUserPlan : interestUserPlans) {
								interestUserPlanList.add(interestUserPlan); // 添加客户收回利息还款计划.
							}
						}
					} else {
						WloanTermProjectPlan principalProPlan = wloanTermProjectPlanDao.getBySubOrderId(z.getOrderId());
						if (principalProPlan != null) { // 还本付息.
							if (WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1.equals(principalProPlan.getPrincipal())) {
								WloanTermUserPlan userPlan = new WloanTermUserPlan();
								userPlan.setRepaymentDate(principalProPlan.getRepaymentDate()); // 该笔标的还本的还款日期.
								WloanTermProject project = new WloanTermProject(); // 标的信息，id.
								project.setId(principalProPlan.getProjectId());
								userPlan.setWloanTermProject(project);
								List<WloanTermUserPlan> principalUserPlans = wloanTermUserPlanDao.findList(userPlan);
								log.info("第{}笔散标还本订单，{}条用户出借还款计划！", (i + 1), principalUserPlans.size());
								for (WloanTermUserPlan principalUserPlan : principalUserPlans) {
									principalUserPlanList.add(principalUserPlan); // 添加客户收回本金还款计划.
								}
							}
						}
					}
				}
				// 交易流水-出借人收回利息-实时.
				if (interestUserPlanList != null && interestUserPlanList.size() > 0) {
					currentTimeMillis = currentTimeMillis + 1;
					takeBackInterestResult = transactDataAccessService.pushTransactInvTakeBackInterest(interestUserPlanList, currentTimeMillis);
					log.info("推送出借人收回利息-交易流水-实时，响应信息：{}", JSON.toJSONString(takeBackInterestResult));
				}
				// 交易流水-出借人收回本金-实时.
				if (principalUserPlanList != null && principalUserPlanList.size() > 0) {
					currentTimeMillis = currentTimeMillis + 1;
					takeBackPrincipalResult = transactDataAccessService.pushTransactInvTakeBackPrincipal(principalUserPlanList, currentTimeMillis);
					log.info("推送出借人收回本息-交易流水-实时，响应信息：{}", JSON.toJSONString(takeBackPrincipalResult));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			log.warn("实时推送未开启，请联系程序猿小哥哥 ...");
		}
	}

	/**
	 * 
	 * methods: borrowerPayInterestAndPrincipal <br>
	 * description: 交易流水-借款人付息/还本付息-实时（每天23点执行）. <br>
	 * author: Roy <br>
	 * date: 2019年7月24日 下午5:38:11
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	@Scheduled(cron = "0 10 23 * * ?")
	public void pushTransactBorrowerPayInterestAndPrincipal() {

		if (ServerURLConfig.IS_REAL_TIME_PUSH) {
			// 付息结果集.
			Map<String, Object> payInterestResult = new HashMap<String, Object>();
			// 还本结果集.
			Map<String, Object> payPrincipalResult = new HashMap<String, Object>();
			long currentTimeMillis = System.currentTimeMillis();
			try {
				// 付息项目还款计划列表.
				List<WloanTermProjectPlan> interestProPlanList = new ArrayList<WloanTermProjectPlan>();
				List<WloanTermProjectPlan> principalProPlanList = new ArrayList<WloanTermProjectPlan>();
				// 查询落单且完成还款的订单.
				ZtmgOrderInfo zoi = new ZtmgOrderInfo();
				zoi.setState(ZtmgOrderInfoService.STATE_2); // 已还.
				zoi.setBeginUpdateDate(DateUtils.getStartTimeEveryday()); // 每天开始时间.
				zoi.setEndUpdateDate(DateUtils.getNowTime()); // 查询时间.
				List<ZtmgOrderInfo> zoiList = ztmgOrderInfoDao.findList(zoi);
				for (int i = 0; i < zoiList.size(); i++) {
					ZtmgOrderInfo z = zoiList.get(i);
					WloanTermProjectPlan interestProPlan = wloanTermProjectPlanDao.get(z.getOrderId());
					if (interestProPlan != null) { // 付息.
						if (WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_0.equals(interestProPlan.getPrincipal())) {
							interestProPlanList.add(interestProPlan);
						}
					} else {
						WloanTermProjectPlan principalProPlan = wloanTermProjectPlanDao.getBySubOrderId(z.getOrderId());
						if (principalProPlan != null) { // 还本付息.
							if (WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1.equals(principalProPlan.getPrincipal())) {
								principalProPlanList.add(principalProPlan);
							}
						}
					}
				}
				// 借款人付息交易流水.
				if (interestProPlanList != null && interestProPlanList.size() > 0) {
					currentTimeMillis = currentTimeMillis + 1;
					payInterestResult = transactDataAccessService.pushTransactCrePayInterestInfo(interestProPlanList, currentTimeMillis);
					log.info("推送借款人付息交易流水-实时，响应信息：{}", JSON.toJSONString(payInterestResult));
				}
				// 借款人还本交易流水.
				if (principalProPlanList != null && principalProPlanList.size() > 0) {
					currentTimeMillis = currentTimeMillis + 1;
					payPrincipalResult = transactDataAccessService.pushTransactCrePayPrincipalInfo(principalProPlanList, currentTimeMillis);
					log.info("推送借款人还本付息交易流水-实时，响应信息：{}", JSON.toJSONString(payPrincipalResult));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			log.warn("实时推送未开启，请联系程序猿小哥哥 ...");
		}
	}

	/**
	 * 
	 * methods: borrowerWithdraw <br>
	 * description: 交易流水-借款人提现（每30分钟推送一次）-辅推借款人用户信息. <br>
	 * author: Roy <br>
	 * date: 2019年7月23日 下午8:27:27
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	@Scheduled(cron = "0 0/30 * * * ?")
	public void pushTransactBorrowerWithdraw() {

		if (ServerURLConfig.IS_REAL_TIME_PUSH) {
			// 结果集.
			Map<String, Object> pushBorrowerWithdrawResult = new HashMap<String, Object>();
			try {
				boolean flag = true;
				int pageNo = 1;
				int pageSize = 3000;
				long currentTimeMillis = System.currentTimeMillis();
				while (flag) {
					Page<CgbUserTransDetail> page = new Page<CgbUserTransDetail>();
					page.setPageNo(pageNo);
					page.setPageSize(pageSize);
					CgbUserTransDetail cutd = new CgbUserTransDetail();
					cutd.setTrustType(CgbUserTransDetailService.TRUST_TYPE_1); // 提现.
					cutd.setBeginTransDate(DateUtils.calendarDate_Negative(new Date(), -1)); // 当前时间的前一天.
					cutd.setEndTransDate(new Date()); // 结束于当前查询时间.
					cutd.setState(CgbUserTransDetailService.TRUST_STATE_2);
					Page<CgbUserTransDetail> cutdPage = cgbUserTransDetailService.findCreditPage(page, cutd);
					List<CgbUserTransDetail> cutdList = cutdPage.getList();
					log.info("借款人提现交易流水数量：{}", cutdList.size());
					log.info("当前页码：{}", pageNo);
					log.info("最后页码：{}", cutdPage.getLast());
					if (cutdList != null && cutdList.size() > 0) {
						currentTimeMillis = currentTimeMillis + 9;
						// 判断借款用户信息是否推送.
						List<String> subIdList = new ArrayList<String>();
						for (int i = 0; i < cutdList.size(); i++) {
							CgbUserTransDetail creUserTrade = cutdList.get(i);
							IfCertUserInfo ifCertUserInfo = new IfCertUserInfo();
							List<WloanSubject> subjects = wloanSubjectDao.getByLoanApplyId(creUserTrade.getCreditUserInfo().getId());
							if (subjects != null && subjects.size() > 0) {
								ifCertUserInfo.setUserIdcardHash(tool.idCardHash(StringUtils.replaceBlanK(subjects.get(0).getBusinessNo())));
							}
							List<IfCertUserInfo> ifCertUserInfos = ifCertUserInfoDao.findList(ifCertUserInfo);
							if (ifCertUserInfos != null && ifCertUserInfos.size() > 0) {
								log.info("第{}条用户信息已推送，userIdcardHash:{}", i + 1, ifCertUserInfo.getUserIdcardHash());
							} else {
								if (subjects != null && subjects.size() > 0) {
									subIdList.add(subjects.get(0).getId()); // 融资主体主键ID.
									log.info("第{}条用户信息待推送，userIdcardHash:{}", i + 1, ifCertUserInfo.getUserIdcardHash());
								}
							}
						}
						// 推送当前批次借款用户信息.
						if (subIdList != null && subIdList.size() > 0) {
							Map<String, Object> pushUserResult = ifcertUserInfoDataAccessService.pushCreUserInfoC(subIdList);
							log.info("推送借款用户信息-实时，pushUserResult:{}", pushUserResult);
						}
						// 推送当前批次借款用户提现交易流水.
						pushBorrowerWithdrawResult = transactDataAccessService.pushTransactCreWithdrawInfo(cutdList, currentTimeMillis);
						log.info("推送借款人提现交易流水-实时-pushBorrowerWithdrawResult：{}", JSON.toJSONString(pushBorrowerWithdrawResult));
						pageNo = pageNo + 1; // next page.
						if (pageNo > cutdPage.getLast()) {
							flag = false;
						}
					} else {
						flag = false;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			log.warn("实时推送未开启，请联系程序猿小哥哥 ...");
		}
	}

	/**
	 * 
	 * methods: borrowerRecharge <br>
	 * description: 交易流水-借款人充值（每30分钟推送一次）-辅推借款人用户信息. <br>
	 * author: Roy <br>
	 * date: 2019年7月23日 下午4:22:23
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	@Scheduled(cron = "0 0/30 * * * ?")
	public void pushTransactBorrowerRecharge() {

		if (ServerURLConfig.IS_REAL_TIME_PUSH) {
			// 结果集.
			Map<String, Object> pushBorrowerRechargeResult = new HashMap<String, Object>();
			try {
				boolean flag = true;
				int pageNo = 1;
				int pageSize = 3000;
				long currentTimeMillis = System.currentTimeMillis();
				while (flag) {
					Page<CgbUserTransDetail> page = new Page<CgbUserTransDetail>();
					page.setPageNo(pageNo);
					page.setPageSize(pageSize);
					CgbUserTransDetail cutd = new CgbUserTransDetail();
					cutd.setTrustType(CgbUserTransDetailService.TRUST_TYPE_0); // 充值.
					// DateUtils.beforeMinutes30(new Date()); // 开始时间于前30分钟.
					cutd.setBeginTransDate(DateUtils.calendarDate_Negative(new Date(), -1)); // 当前时间的前一天.
					cutd.setEndTransDate(new Date()); // 结束于当前查询时间.
					cutd.setState(CgbUserTransDetailService.TRUST_STATE_2);
					Page<CgbUserTransDetail> cutdPage = cgbUserTransDetailService.findCreditPage(page, cutd);
					List<CgbUserTransDetail> cutdList = cutdPage.getList();
					log.info("借款人充值交易流水数量：{}", cutdList.size());
					log.info("当前页码：{}", pageNo);
					log.info("最后页码：{}", cutdPage.getLast());
					if (cutdList != null && cutdList.size() > 0) {
						currentTimeMillis = currentTimeMillis + 7;
						// 判断借款用户信息是否推送.
						List<String> subIdList = new ArrayList<String>();
						for (int i = 0; i < cutdList.size(); i++) {
							CgbUserTransDetail creUserTrade = cutdList.get(i);
							IfCertUserInfo ifCertUserInfo = new IfCertUserInfo();
							List<WloanSubject> subjects = wloanSubjectDao.getByLoanApplyId(creUserTrade.getCreditUserInfo().getId());
							if (subjects != null && subjects.size() > 0) {
								ifCertUserInfo.setUserIdcardHash(tool.idCardHash(StringUtils.replaceBlanK(subjects.get(0).getBusinessNo())));
							}
							List<IfCertUserInfo> ifCertUserInfos = ifCertUserInfoDao.findList(ifCertUserInfo);
							if (ifCertUserInfos != null && ifCertUserInfos.size() > 0) {
								log.info("第{}条用户信息已推送，userIdcardHash:{}", i + 1, ifCertUserInfo.getUserIdcardHash());
							} else {
								if (subjects != null && subjects.size() > 0) {
									subIdList.add(subjects.get(0).getId()); // 融资主体主键ID.
									log.info("第{}条用户信息待推送，userIdcardHash:{}", i + 1, ifCertUserInfo.getUserIdcardHash());
								}
							}
						}
						// 推送当前批次借款用户信息.
						if (subIdList != null && subIdList.size() > 0) {
							Map<String, Object> pushUserResult = ifcertUserInfoDataAccessService.pushCreUserInfoC(subIdList);
							log.info("推送借款用户信息-实时，pushUserResult:{}", pushUserResult);
						}
						// 推送当前批次借款用户充值交易流水.
						pushBorrowerRechargeResult = transactDataAccessService.pushTransactCreRechargeInfo(cutdList, currentTimeMillis);
						log.info("推送借款人充值交易流水-实时-pushBorrowerRechargeResult：{}", JSON.toJSONString(pushBorrowerRechargeResult));
						pageNo = pageNo + 1; // next page.
						if (pageNo > cutdPage.getLast()) {
							flag = false;
						}
					} else {
						flag = false;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			log.warn("实时推送未开启，请联系程序猿小哥哥 ...");
		}
	}

}
