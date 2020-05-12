package com.power.platform.plan.service;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.cms.dao.NoticeDao;
import com.power.platform.cms.entity.Notice;
import com.power.platform.cms.entity.NoticePojo;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.service.CrudService;
import com.power.platform.common.utils.CalendarUtil;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.InterestUtils;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.plan.dao.WloanTermUserPlanDao;
import com.power.platform.plan.entity.WloanTermUserPlan;
import com.power.platform.regular.dao.WloanTermInvestDao;
import com.power.platform.regular.entity.WloanTermInvest;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.regular.service.WloanTermProjectService;
import com.power.platform.trandetail.entity.UserTransDetail;
import com.power.platform.trandetail.service.UserTransDetailService;
import com.power.platform.userinfo.entity.UserAccountInfo;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.userinfo.service.UserAccountInfoService;
import com.power.platform.userinfo.service.UserInfoService;
import com.power.platform.weixin.service.WeixinSendTempMsgService;

@Transactional(readOnly = false)
@Service("wloanTermUserPlanService")
public class WloanTermUserPlanService extends CrudService<WloanTermUserPlan> {

	private static final Logger log = Logger.getLogger(WloanTermUserPlanService.class);

	/**
	 * 还款每30天为一期.
	 */
	private static final Integer SPAN_30 = 30;

	/**
	 * 还款计划状态，1：还本付息.
	 */
	public static final String WLOAN_TERM_USER_PLAN_PRINCIPAL_1 = "1";
	/**
	 * 还款计划状态，2：付息.
	 */
	public static final String WLOAN_TERM_USER_PLAN_PRINCIPAL_2 = "2";
	/**
	 * 还款计划状态，1：初始化.
	 */
	public static final String WLOAN_TERM_USER_PLAN_STATE_1 = "1";
	/**
	 * 还款计划状态，2：正在还款.
	 */
	public static final String WLOAN_TERM_USER_PLAN_STATE_2 = "2";
	/**
	 * 还款计划状态，3：已经还款.
	 */
	public static final String WLOAN_TERM_USER_PLAN_STATE_3 = "3";
	/**
	 * 还款计划状态，4：还款失败.
	 */
	public static final String WLOAN_TERM_USER_PLAN_STATE_4 = "4";
	/**
	 * 还款计划状态，5：流标.
	 */
	public static final String WLOAN_TERM_USER_PLAN_STATE_5 = "5";

	/**
	 * NOTICE_TYPE_2：公告.
	 */
	private static final Integer NOTICE_TYPE_2 = 2;

	/**
	 * NOTICE_STATE_1：上线.
	 */
	private static final Integer NOTICE_STATE_1 = 1;

	@Resource
	private WloanTermUserPlanDao wloanTermUserPlanDao;

	@Resource
	private UserTransDetailService userTransDetailService;

	@Resource
	private UserAccountInfoService userAccountInfoService;

	@Resource
	private UserInfoService userInfoService;

	/*
	 * @Resource
	 * private WloanTermWrepayService wloanTermWrepayService;
	 */

	@Resource
	private WloanTermProjectService wloanTermProjectService;

	@Resource
	private WloanTermInvestDao wloanTermInvestDao;

	@Resource
	private WeixinSendTempMsgService weixinSendTempMsgService;

	@Resource
	private NoticeDao noticeDao;

	protected CrudDao<WloanTermUserPlan> getEntityDao() {

		return wloanTermUserPlanDao;
	}

	/**
	 * 
	 * 方法: findDueDatePage <br>
	 * 描述: 客户还款计划列表，便于客服跟踪到期项目的还款客户. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年6月13日 下午3:00:55
	 * 
	 * @param page
	 * @param wloanTermUserPlan
	 * @return
	 */
	public Page<WloanTermUserPlan> findDueDatePage(Page<WloanTermUserPlan> page, WloanTermUserPlan wloanTermUserPlan) {

		wloanTermUserPlan.setPage(page);
		page.setList(wloanTermUserPlanDao.findDueDateList(wloanTermUserPlan));
		return page;
	}

	/**
	 * 
	 * 出借人付息还本存量数据
	 */
	public Page<WloanTermUserPlan> findUserPlanList(Page<WloanTermUserPlan> page, WloanTermUserPlan wloanTermUserPlan) {

		wloanTermUserPlan.setPage(page);
		page.setList(wloanTermUserPlanDao.findUserPlanList(wloanTermUserPlan));
		return page;
	}

	// 增量-出借人还本付息-投资明细.
	public Page<WloanTermUserPlan> findUserPlanListZ(Page<WloanTermUserPlan> page, WloanTermUserPlan wloanTermUserPlan) {

		wloanTermUserPlan.setPage(page);
		page.setList(wloanTermUserPlanDao.findUserPlanListZ(wloanTermUserPlan));
		return page;
	}
	
	// 采用标的id补推还本付息流水.
	public List<WloanTermUserPlan> fillPushUserPlanList(WloanTermUserPlan wloanTermUserPlan) {
		return wloanTermUserPlanDao.fillPushUserPlanList(wloanTermUserPlan);
	}

	/**
	 * 
	 * 方法: findUserRepayPlanStatistical <br>
	 * 描述: 分页查询用户的还款计划. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2018年2月2日 上午11:27:53
	 * 
	 * @param page
	 * @param wloanTermUserPlan
	 * @return
	 */
	public Page<WloanTermUserPlan> findUserRepayPlanStatistical(Page<WloanTermUserPlan> page, WloanTermUserPlan wloanTermUserPlan) {

		wloanTermUserPlan.setPage(page);
		page.setList(wloanTermUserPlanDao.findUserRepayPlanStatistical(wloanTermUserPlan));
		return page;
	}

	/**
	 * 
	 * 方法: findListByProjectId <br>
	 * 描述: 根据项目ID，查询客户还款计划列表. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年11月7日 上午11:20:18
	 * 
	 * @param projectId
	 * @return
	 */
	public List<WloanTermUserPlan> findListByProjectId(String projectId) {

		WloanTermUserPlan wloanTermUserPlan = new WloanTermUserPlan();
		WloanTermProject wloanTermProject = new WloanTermProject();
		wloanTermProject.setId(projectId);
		wloanTermUserPlan.setWloanTermProject(wloanTermProject);
		List<WloanTermUserPlan> list = wloanTermUserPlanDao.findList(wloanTermUserPlan);
		return list;
	}

	public String initWloanTermUserPlan(WloanTermInvest wloanTermInvest) {

		try {

			WloanTermProject wloanTermProject = wloanTermProjectService.get(wloanTermInvest.getWloanTermProject().getId());
			// 投资总利息
			Double wloanTermInerest = wloanTermInvest.getInterest();
			// 计算还款期数
			Integer sum = wloanTermProject.getSpan() / 30;
			// 计算每期还款（30天一期）利息
			Double spanMoney = InterestUtils.getMonthInterestFormat(wloanTermInvest.getAmount(), wloanTermProject.getAnnualRate());
			Double spanMoneyTrue = InterestUtils.getMonthInterest(wloanTermInvest.getAmount(), wloanTermProject.getAnnualRate());// 未四舍五入
			WloanTermUserPlan wloanTermUserPlan = null;

			// 保存每期还款计划
			for (int i = 1; i <= sum; i++) {
				wloanTermUserPlan = new WloanTermUserPlan();
				wloanTermUserPlan.setWloanTermProject(wloanTermProject);
				wloanTermUserPlan.setUserInfo(wloanTermInvest.getUserInfo());
				wloanTermUserPlan.setId(IdGen.uuid());
				// wloanTermUserPlan.setRepaymentDate(DateUtils.getDateOfString(DateUtils.getSpecifiedMonthAfter(DateUtils.formatDateTime(wloanTermProject.getFullDate()),i)));
				wloanTermUserPlan.setRepaymentDate(DateUtils.getShortDateOfString(DateUtils.getSpecifiedMonthAfter(DateUtils.formatDateTime(wloanTermProject.getLoanDate()), i)));
				wloanTermUserPlan.setState(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_2);
				wloanTermUserPlan.setWloanTermInvest(wloanTermInvest);

				// 如果只有一期
				if (1 == sum) {
					wloanTermUserPlan.setInterest(wloanTermInvest.getAmount() + wloanTermInvest.getInterest());
					wloanTermUserPlan.setInterestTrue(wloanTermInvest.getAmount() + wloanTermInvest.getInterest());
					wloanTermUserPlan.setPrincipal("1");
				} else {
					// 最后一期的还款金额为 本金 +[投资总利息-每期利息*(还款期数-1)]
					if (i == sum) {
						Double lastSpanMoney = wloanTermInvest.getAmount() + (wloanTermInerest - spanMoney * (sum - 1));
						wloanTermUserPlan.setInterest(lastSpanMoney);
						wloanTermUserPlan.setInterestTrue(lastSpanMoney);
						wloanTermUserPlan.setPrincipal("1");
					} else {
						wloanTermUserPlan.setInterest(spanMoney);
						wloanTermUserPlan.setInterestTrue(spanMoneyTrue);
						wloanTermUserPlan.setPrincipal("2");
					}
				}

				wloanTermUserPlanDao.insert(wloanTermUserPlan);
			}
			return "SUCCESS";
		} catch (Exception e) {
			e.printStackTrace();
			return "ERROR";
		}
	}

	public void updateWloanTermUserPlanStateById(String id, String state) {

		wloanTermUserPlanDao.updateWloanTermUserPlanStateById(id, state);
	}

	public void updateWloanTermUserPlanStateByProjectId(String wloanTermProjectId, String state) {

		wloanTermUserPlanDao.updateWloanTermUserPlanStateByProjectId(wloanTermProjectId, state);

	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	synchronized public boolean repaymentWloanTermUserPlan(String wloanTermProjectId, String ip, Date repaymentDate) throws Exception {

		WloanTermUserPlan entity = new WloanTermUserPlan();
		entity.setState("2");
		WloanTermProject termProject = new WloanTermProject();
		termProject.setId(wloanTermProjectId);
		entity.setWloanTermProject(termProject);
		entity.setRepaymentDate(repaymentDate);

		List<WloanTermUserPlan> planList = wloanTermUserPlanDao.findList(entity);

		if (planList != null) {
			UserTransDetail transDates = new UserTransDetail();
			transDates.setTransDate(new Date());
			UserTransDetail transDetailPrin = null;
			UserTransDetail transDetail = null;
			UserAccountInfo accountInfo = null;
			UserInfo userInfo = null;
			WloanTermProject wloanTermProject = wloanTermProjectService.get(wloanTermProjectId);
			// 还款总额.
			Double repayTotalAmount = 0D;
			// 还款类型.
			String repayType = "";
			for (WloanTermUserPlan wloanTermUserPlan : planList) {

				userInfo = userInfoService.get(wloanTermUserPlan.getUserInfo().getId());
				if (userInfo == null) {
					continue;
				}
				accountInfo = userAccountInfoService.getUserAccountInfo(userInfo.getId());
				transDetail = new UserTransDetail();
				transDetailPrin = new UserTransDetail();
				// 某一投资项目个人投资总额
				double principalAmount = wloanTermInvestDao.findInvestAmountByProjectAndUser(wloanTermProjectId, userInfo.getId(), wloanTermUserPlan.getWloanTermInvest().getId());
				// 还款总额.
				repayTotalAmount = NumberUtils.scaleDouble(repayTotalAmount + wloanTermUserPlan.getInterest());
				// 归还利息
				if (wloanTermUserPlan.getPrincipal().equals("2")) {
					repayType = "2";
					// 累积收益
					accountInfo.setTotalInterest(accountInfo.getTotalInterest() + wloanTermUserPlan.getInterest());
					// 定期待收收益
					accountInfo.setRegularDueInterest(accountInfo.getRegularDueInterest() - wloanTermUserPlan.getInterest());
					// 定期累积收益
					accountInfo.setRegularTotalInterest(accountInfo.getRegularTotalInterest() + wloanTermUserPlan.getInterest());
					// 可用余额 = 可用余额 + 还款利息
					accountInfo.setAvailableAmount(accountInfo.getAvailableAmount() + wloanTermUserPlan.getInterest());
				} else { // 归还本息
					repayType = "1";
					// 归还利息 = 归还本息 - 投资本金
					double backInterestAmount = wloanTermUserPlan.getInterest() - principalAmount;
					// 待收本金 = 待收本金 - 归还本金
					accountInfo.setRegularDuePrincipal(accountInfo.getRegularDuePrincipal() - principalAmount);
					// 待收收益 = 待收收益 + （归还本息 - 投资本金）
					accountInfo.setTotalInterest(accountInfo.getTotalInterest() + backInterestAmount);
					// 定期待收收益 = 定期待收收益 - （归还本息 - 投资本金）
					accountInfo.setRegularDueInterest(accountInfo.getRegularDueInterest() - backInterestAmount);
					// 定期累积收益 = 定期待收收益 + （归还本息 - 投资本金）
					accountInfo.setRegularTotalInterest(accountInfo.getRegularTotalInterest() + backInterestAmount);

					// 可用余额 = 可用余额 + 还款利息
					accountInfo.setAvailableAmount(accountInfo.getAvailableAmount() + wloanTermUserPlan.getInterest() - principalAmount);

					WloanTermProject wloanTermProjectEnd = wloanTermProjectService.get(wloanTermUserPlan.getWloanTermProject().getId());
					wloanTermProjectEnd.setEndDate(new Date());
					wloanTermProjectEnd.setState("7");
					wloanTermProjectService.save(wloanTermProjectEnd);

				}

				userAccountInfoService.save(accountInfo);

				// 交易流水
				transDetail.setTransId(wloanTermUserPlan.getId());
				transDetail.setAccountId(userInfo.getAccountId());
				transDetail.setUserId(userInfo.getId());
				transDetail.setUserInfo(userInfo);
				transDetail.setTransDate(new Date(transDates.getTransDate().getTime() + 1000));
				transDates.setTransDate(transDetail.getTransDate());
				transDetail.setAmount(wloanTermUserPlan.getInterest());
				transDetail.setTrustType(4);

				if (wloanTermUserPlan.getPrincipal().equals("1")) {
					transDetail.setAmount(wloanTermUserPlan.getInterest() - principalAmount);
				}

				transDetail.setRemarks("归还" + wloanTermProject.getName() + "利息");
				transDetail.setInOutType(1);
				transDetail.setState(2);
				transDetail.setAvaliableAmount(accountInfo.getAvailableAmount());
				userTransDetailService.save(transDetail);

				if (wloanTermUserPlan.getPrincipal().equals("1")) {
					// 可用余额 = 可用余额 + 还款利息
					accountInfo.setAvailableAmount(accountInfo.getAvailableAmount() + principalAmount);
					userAccountInfoService.save(accountInfo);

					transDetailPrin.setTransId(wloanTermUserPlan.getId());
					transDetailPrin.setAccountId(userInfo.getAccountId());
					transDetailPrin.setUserId(userInfo.getId());
					transDetailPrin.setUserInfo(userInfo);
					transDetailPrin.setTransDate(new Date(transDetail.getTransDate().getTime() + 1000));
					transDates.setTransDate(transDetail.getTransDate());
					transDetailPrin.setTrustType(5);
					transDetailPrin.setAmount(principalAmount);
					transDetailPrin.setRemarks("归还" + wloanTermProject.getName() + "本金");
					transDetailPrin.setInOutType(1);
					transDetailPrin.setState(2);
					transDetailPrin.setAvaliableAmount(accountInfo.getAvailableAmount());
					transDates.setTransDate(transDetailPrin.getTransDate());
					userTransDetailService.save(transDetailPrin);
				}
				wloanTermUserPlanDao.updateWloanTermUserPlanStateById(wloanTermUserPlan.getId(), WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_3);
				// 发送微信、短信还款提醒
				weixinSendTempMsgService.sendRepayInfoMsg(wloanTermUserPlan);
			}
			/**
			 * 发布还款公告.
			 */
			Notice notice = new Notice();
			notice.setId(IdGen.uuid());
			if (repayType.equals(WLOAN_TERM_USER_PLAN_PRINCIPAL_1)) { // 还本付息.
				notice.setTitle("项目编号：" + wloanTermProject.getSn() + "还本付息公告！");
			} else if (repayType.equals(WLOAN_TERM_USER_PLAN_PRINCIPAL_2)) { // 付息.
				notice.setTitle("项目编号：" + wloanTermProject.getSn() + "付息公告！");
			} else {
				notice.setTitle("项目编号：" + wloanTermProject.getSn() + "付息公告！");
			}
			notice.setText(NoticePojo.createNoticeContent(repayType, wloanTermProject.getName(), wloanTermProject.getSn(), new DecimalFormat("0.00").format(repayTotalAmount)));
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
			return true;
		}
		return false;
	}

	public double findCurrentTotal(WloanTermUserPlan wloanTermUserPlan) {

		return wloanTermUserPlanDao.findCurrentTotal(wloanTermUserPlan);
	}

	public List<WloanTermUserPlan> findinterestCount(WloanTermUserPlan wloanTermUserPlan) {

		return wloanTermUserPlanDao.findinterestCount(wloanTermUserPlan);

	}

	/**
	 * 用户还款计划生成
	 * 
	 * @param wloanTermInvest
	 * @return
	 */
	@Transactional(readOnly = false)
	public String initCgbWloanTermUserPlan(WloanTermInvest wloanTermInvest) {

		WloanTermProject project = wloanTermProjectService.get(wloanTermInvest.getWloanTermProject().getId());

		try {
			Date loanDate = project.getLoanDate();
			log.info("放款日期：" + loanDate);
			/**
			 * 每日利息(按日计息).
			 */
			// 每日利息，保留两位小数.
			Double dayInterestScaleDouble = InterestUtils.getDayInterestFormat(wloanTermInvest.getAmount(), project.getAnnualRate());
			// 每日利息，原始小数位.
			Double dayInterest = InterestUtils.getDayInterest(wloanTermInvest.getAmount(), project.getAnnualRate());

			log.info("每日利息，保留两位小数" + dayInterestScaleDouble);
			log.info("每日利息，原始小数位" + dayInterest);

			// 项目期限.
			int span = project.getSpan();
			String spanStr = String.valueOf(span);
			log.info("项目期限：" + spanStr);
			/**
			 * span-30.
			 */
			if (spanStr.equals(WloanTermProjectService.SPAN_30)) {
				WloanTermUserPlan entity = new WloanTermUserPlan();
				entity.setId(IdGen.uuid());
				entity.setUserInfo(wloanTermInvest.getUserInfo());
				entity.setWloanTermProject(project);
				entity.setState(WLOAN_TERM_USER_PLAN_STATE_2);
				entity.setWloanTermInvest(wloanTermInvest);
				entity.setInterest(NumberUtils.scaleDouble(dayInterestScaleDouble * 30) + wloanTermInvest.getAmount());
				entity.setInterestTrue((dayInterest * 30) + wloanTermInvest.getAmount());
				entity.setPrincipal(WLOAN_TERM_USER_PLAN_PRINCIPAL_1);
				// 还款日期.
				entity.setRepaymentDate(DateUtils.getShortDateOfString(DateUtils.getSpecifiedMonthAfter(DateUtils.formatDateTime(loanDate), 1)));
				int insertUserPlanFlag = wloanTermUserPlanDao.insert(entity);
				if (insertUserPlanFlag == 1) {
					log.info("【项目还款计划】-第1期创建成功.");
				} else {
					log.info("【项目还款计划】-第1期创建失败.");
				}
			} else
			/**
			 * span-90.
			 */
			if (spanStr.equals(WloanTermProjectService.SPAN_90)) {
				// 终止条件.
				int v = 1;
				while (v <= 4) {
					System.out.println("v = " + v);
					if (v == 1) { // 第一个月天数.
						// 开始时间-放款日期，到下月还款日期15号，还款天数.
						int day = CalendarUtil.differentDaysByMillisecond(loanDate, DateUtils.getShortDateOfString(CalendarUtil.getNextFewMonthFifteen(loanDate, v)));
						log.info("第" + v + "期还款天数：" + day);
						log.info("第" + v + "期开始时间：" + DateUtils.getDate(loanDate, "yyyy-MM-dd"));
						span = span - day;
						log.info("第" + v + "期剩余还款天数：" + span);
						log.info("第" + v + "期还款日期：" + CalendarUtil.getNextFewMonthFifteen(loanDate, v));
						// 创建用户还款计划
						WloanTermUserPlan entity = new WloanTermUserPlan();
						entity.setId(IdGen.uuid());
						entity.setUserInfo(wloanTermInvest.getUserInfo());
						entity.setWloanTermProject(project);
						entity.setState(WLOAN_TERM_USER_PLAN_STATE_2);
						entity.setWloanTermInvest(wloanTermInvest);
						entity.setInterest(NumberUtils.scaleDouble(dayInterestScaleDouble * day));
						entity.setInterestTrue((dayInterest * day));
						entity.setPrincipal(WLOAN_TERM_USER_PLAN_PRINCIPAL_2);
						// 还款日期.
						entity.setRepaymentDate(DateUtils.getShortDateOfString(CalendarUtil.getNextFewMonthFifteen(loanDate, v)));
						int insertUserPlanFlag = wloanTermUserPlanDao.insert(entity);
						if (insertUserPlanFlag == 1) {
							log.info("【个人还款计划】-第" + v + "期创建成功.");
						} else {
							log.info("【个人还款计划】-第" + v + "期创建失败.");
						}
					} else {
						// 每月的天数，即为还款天数.
						int day = CalendarUtil.getDaysOfMonth(DateUtils.getShortDateOfString(CalendarUtil.getNextFewMonthFifteen(loanDate, v - 1)));
						// System.out.println("第" + v + "个月天数：" + day);
						if (span >= day) { // 剩余还款天数，是否满足本期还款天数.
							log.info("第" + v + "期开始日期：" + CalendarUtil.getNextFewMonthFifteen(loanDate, v - 1));
							log.info("第" + v + "期还款天数：" + day);
							span = span - day;
							log.info("第" + v + "期剩余还款天数：" + span);
							log.info("第" + v + "期还款日期：" + CalendarUtil.getNextFewMonthFifteen(loanDate, v));
							// 创建用户还款计划
							WloanTermUserPlan entity = new WloanTermUserPlan();
							entity.setId(IdGen.uuid());
							entity.setUserInfo(wloanTermInvest.getUserInfo());
							entity.setWloanTermProject(project);
							entity.setState(WLOAN_TERM_USER_PLAN_STATE_2);
							entity.setWloanTermInvest(wloanTermInvest);
							entity.setInterest(NumberUtils.scaleDouble(dayInterestScaleDouble * day));
							entity.setInterestTrue((dayInterest * day));
							entity.setPrincipal(WLOAN_TERM_USER_PLAN_PRINCIPAL_2);
							// 还款日期.
							entity.setRepaymentDate(DateUtils.getShortDateOfString(CalendarUtil.getNextFewMonthFifteen(loanDate, v)));
							int insertUserPlanFlag = wloanTermUserPlanDao.insert(entity);
							if (insertUserPlanFlag == 1) {
								log.info("【个人还款计划】-第" + v + "期创建成功.");
							} else {
								log.info("【个人还款计划】-第" + v + "期创建失败.");
							}
						} else { // 最后一期还款.
							if (span == 0) {
								break;
							}
							log.info("第" + v + "期开始时间：" + CalendarUtil.getNextFewMonthFifteen(loanDate, v - 1));
							log.info("第" + v + "期还款天数：" + span);
							log.info("第" + v + "期还款日期：" + CalendarUtil.getSpecifiedMonthAfter(CalendarUtil.getNextFewMonthFifteen(loanDate, v - 1), span));
							// 创建用户还款计划
							WloanTermUserPlan entity = new WloanTermUserPlan();
							entity.setId(IdGen.uuid());
							entity.setUserInfo(wloanTermInvest.getUserInfo());
							entity.setWloanTermProject(project);
							entity.setState(WLOAN_TERM_USER_PLAN_STATE_2);
							entity.setWloanTermInvest(wloanTermInvest);
							entity.setInterest(NumberUtils.scaleDouble(dayInterestScaleDouble * span) + wloanTermInvest.getAmount());
							entity.setInterestTrue((dayInterest * span) + wloanTermInvest.getAmount());
							entity.setPrincipal(WLOAN_TERM_USER_PLAN_PRINCIPAL_1);
							// 还款日期.
							entity.setRepaymentDate(DateUtils.getShortDateOfString(CalendarUtil.getSpecifiedMonthAfter(CalendarUtil.getNextFewMonthFifteen(loanDate, v - 1), span)));
							int insertUserPlanFlag = wloanTermUserPlanDao.insert(entity);
							if (insertUserPlanFlag == 1) {
								log.info("【个人还款计划】-第" + v + "期创建成功.");
							} else {
								log.info("【个人还款计划】-第" + v + "期创建失败.");
							}
							span = span - span;
							log.info("第" + v + "期剩余还款天数：" + span);
						}
					} // --.
					++v; // 终止条件.
				}
			} else
			/**
			 * span-180.
			 */
			if (spanStr.equals(WloanTermProjectService.SPAN_180)) {
				// 终止条件.
				int v = 1;
				while (v <= 8) {
					System.out.println("v = " + v);
					if (v == 1) { // 第一个月天数.
						// 开始时间-放款日期，到下月还款日期15号，还款天数.
						int day = CalendarUtil.differentDaysByMillisecond(loanDate, DateUtils.getShortDateOfString(CalendarUtil.getNextFewMonthFifteen(loanDate, v)));
						log.info("第" + v + "期还款天数：" + day);
						log.info("第" + v + "期开始时间：" + DateUtils.getDate(loanDate, "yyyy-MM-dd"));
						span = span - day;
						log.info("第" + v + "期剩余还款天数：" + span);
						log.info("第" + v + "期还款日期：" + CalendarUtil.getNextFewMonthFifteen(loanDate, v));
						// 创建用户还款计划
						WloanTermUserPlan entity = new WloanTermUserPlan();
						entity.setId(IdGen.uuid());
						entity.setUserInfo(wloanTermInvest.getUserInfo());
						entity.setWloanTermProject(project);
						entity.setState(WLOAN_TERM_USER_PLAN_STATE_2);
						entity.setWloanTermInvest(wloanTermInvest);
						entity.setInterest(NumberUtils.scaleDouble(dayInterestScaleDouble * day));
						entity.setInterestTrue((dayInterest * day));
						entity.setPrincipal(WLOAN_TERM_USER_PLAN_PRINCIPAL_2);
						// 还款日期.
						entity.setRepaymentDate(DateUtils.getShortDateOfString(CalendarUtil.getNextFewMonthFifteen(loanDate, v)));
						int insertUserPlanFlag = wloanTermUserPlanDao.insert(entity);
						if (insertUserPlanFlag == 1) {
							log.info("【个人还款计划】-第" + v + "期创建成功.");
						} else {
							log.info("【个人还款计划】-第" + v + "期创建失败.");
						}
					} else {
						// 每月的天数，即为还款天数.
						int day = CalendarUtil.getDaysOfMonth(DateUtils.getShortDateOfString(CalendarUtil.getNextFewMonthFifteen(loanDate, v - 1)));
						// System.out.println("第" + v + "个月天数：" + day);
						if (span >= day) { // 剩余还款天数，是否满足本期还款天数.
							log.info("第" + v + "期开始日期：" + CalendarUtil.getNextFewMonthFifteen(loanDate, v - 1));
							log.info("第" + v + "期还款天数：" + day);
							span = span - day;
							log.info("第" + v + "期剩余还款天数：" + span);
							log.info("第" + v + "期还款日期：" + CalendarUtil.getNextFewMonthFifteen(loanDate, v));
							// 创建用户还款计划
							WloanTermUserPlan entity = new WloanTermUserPlan();
							entity.setId(IdGen.uuid());
							entity.setUserInfo(wloanTermInvest.getUserInfo());
							entity.setWloanTermProject(project);
							entity.setState(WLOAN_TERM_USER_PLAN_STATE_2);
							entity.setWloanTermInvest(wloanTermInvest);
							entity.setInterest(NumberUtils.scaleDouble(dayInterestScaleDouble * day));
							entity.setInterestTrue((dayInterest * day));
							entity.setPrincipal(WLOAN_TERM_USER_PLAN_PRINCIPAL_2);
							// 还款日期.
							entity.setRepaymentDate(DateUtils.getShortDateOfString(CalendarUtil.getNextFewMonthFifteen(loanDate, v)));
							int insertUserPlanFlag = wloanTermUserPlanDao.insert(entity);
							if (insertUserPlanFlag == 1) {
								log.info("【个人还款计划】-第" + v + "期创建成功.");
							} else {
								log.info("【个人还款计划】-第" + v + "期创建失败.");
							}
						} else { // 最后一期还款.
							if (span == 0) {
								break;
							}
							log.info("第" + v + "期开始时间：" + CalendarUtil.getNextFewMonthFifteen(loanDate, v - 1));
							log.info("第" + v + "期还款天数：" + span);
							log.info("第" + v + "期还款日期：" + CalendarUtil.getSpecifiedMonthAfter(CalendarUtil.getNextFewMonthFifteen(loanDate, v - 1), span));
							// 创建用户还款计划
							WloanTermUserPlan entity = new WloanTermUserPlan();
							entity.setId(IdGen.uuid());
							entity.setUserInfo(wloanTermInvest.getUserInfo());
							entity.setWloanTermProject(project);
							entity.setState(WLOAN_TERM_USER_PLAN_STATE_2);
							entity.setWloanTermInvest(wloanTermInvest);
							entity.setInterest(NumberUtils.scaleDouble(dayInterestScaleDouble * span) + wloanTermInvest.getAmount());
							entity.setInterestTrue((dayInterest * span) + wloanTermInvest.getAmount());
							entity.setPrincipal(WLOAN_TERM_USER_PLAN_PRINCIPAL_1);
							// 还款日期.
							entity.setRepaymentDate(DateUtils.getShortDateOfString(CalendarUtil.getSpecifiedMonthAfter(CalendarUtil.getNextFewMonthFifteen(loanDate, v - 1), span)));
							int insertUserPlanFlag = wloanTermUserPlanDao.insert(entity);
							if (insertUserPlanFlag == 1) {
								log.info("【个人还款计划】-第" + v + "期创建成功.");
							} else {
								log.info("【个人还款计划】-第" + v + "期创建失败.");
							}
							span = span - span;
							log.info("第" + v + "期剩余还款天数：" + span);
						}
					} // --.
					++v; // 终止条件.
				}
			} else
			/**
			 * span-360.
			 */
			if (spanStr.equals(WloanTermProjectService.SPAN_360)) {
				// 终止条件.
				int v = 1;
				while (v <= 14) {
					System.out.println("v = " + v);
					if (v == 1) { // 第一个月天数.
						// 开始时间-放款日期，到下月还款日期15号，还款天数.
						int day = CalendarUtil.differentDaysByMillisecond(loanDate, DateUtils.getShortDateOfString(CalendarUtil.getNextFewMonthFifteen(loanDate, v)));
						log.info("第" + v + "期还款天数：" + day);
						log.info("第" + v + "期开始时间：" + DateUtils.getDate(loanDate, "yyyy-MM-dd"));
						span = span - day;
						log.info("第" + v + "期剩余还款天数：" + span);
						log.info("第" + v + "期还款日期：" + CalendarUtil.getNextFewMonthFifteen(loanDate, v));
						// 创建用户还款计划
						WloanTermUserPlan entity = new WloanTermUserPlan();
						entity.setId(IdGen.uuid());
						entity.setUserInfo(wloanTermInvest.getUserInfo());
						entity.setWloanTermProject(project);
						entity.setState(WLOAN_TERM_USER_PLAN_STATE_2);
						entity.setWloanTermInvest(wloanTermInvest);
						entity.setInterest(NumberUtils.scaleDouble(dayInterestScaleDouble * day));
						entity.setInterestTrue((dayInterest * day));
						entity.setPrincipal(WLOAN_TERM_USER_PLAN_PRINCIPAL_2);
						// 还款日期.
						entity.setRepaymentDate(DateUtils.getShortDateOfString(CalendarUtil.getNextFewMonthFifteen(loanDate, v)));
						int insertUserPlanFlag = wloanTermUserPlanDao.insert(entity);
						if (insertUserPlanFlag == 1) {
							log.info("【个人还款计划】-第" + v + "期创建成功.");
						} else {
							log.info("【个人还款计划】-第" + v + "期创建失败.");
						}
					} else {
						// 每月的天数，即为还款天数.
						int day = CalendarUtil.getDaysOfMonth(DateUtils.getShortDateOfString(CalendarUtil.getNextFewMonthFifteen(loanDate, v - 1)));
						// System.out.println("第" + v + "个月天数：" + day);
						if (span >= day) { // 剩余还款天数，是否满足本期还款天数.
							log.info("第" + v + "期开始日期：" + CalendarUtil.getNextFewMonthFifteen(loanDate, v - 1));
							log.info("第" + v + "期还款天数：" + day);
							span = span - day;
							log.info("第" + v + "期剩余还款天数：" + span);
							log.info("第" + v + "期还款日期：" + CalendarUtil.getNextFewMonthFifteen(loanDate, v));
							// 创建用户还款计划
							WloanTermUserPlan entity = new WloanTermUserPlan();
							entity.setId(IdGen.uuid());
							entity.setUserInfo(wloanTermInvest.getUserInfo());
							entity.setWloanTermProject(project);
							entity.setState(WLOAN_TERM_USER_PLAN_STATE_2);
							entity.setWloanTermInvest(wloanTermInvest);
							entity.setInterest(NumberUtils.scaleDouble(dayInterestScaleDouble * day));
							entity.setInterestTrue((dayInterest * day));
							entity.setPrincipal(WLOAN_TERM_USER_PLAN_PRINCIPAL_2);
							// 还款日期.
							entity.setRepaymentDate(DateUtils.getShortDateOfString(CalendarUtil.getNextFewMonthFifteen(loanDate, v)));
							int insertUserPlanFlag = wloanTermUserPlanDao.insert(entity);
							if (insertUserPlanFlag == 1) {
								log.info("【个人还款计划】-第" + v + "期创建成功.");
							} else {
								log.info("【个人还款计划】-第" + v + "期创建失败.");
							}
						} else { // 最后一期还款.
							if (span == 0) {
								break;
							}
							log.info("第" + v + "期开始时间：" + CalendarUtil.getNextFewMonthFifteen(loanDate, v - 1));
							log.info("第" + v + "期还款天数：" + span);
							log.info("第" + v + "期还款日期：" + CalendarUtil.getSpecifiedMonthAfter(CalendarUtil.getNextFewMonthFifteen(loanDate, v - 1), span));
							// 创建用户还款计划
							WloanTermUserPlan entity = new WloanTermUserPlan();
							entity.setId(IdGen.uuid());
							entity.setUserInfo(wloanTermInvest.getUserInfo());
							entity.setWloanTermProject(project);
							entity.setState(WLOAN_TERM_USER_PLAN_STATE_2);
							entity.setWloanTermInvest(wloanTermInvest);
							entity.setInterest(NumberUtils.scaleDouble(dayInterestScaleDouble * span) + wloanTermInvest.getAmount());
							entity.setInterestTrue((dayInterest * span) + wloanTermInvest.getAmount());
							entity.setPrincipal(WLOAN_TERM_USER_PLAN_PRINCIPAL_1);
							// 还款日期.
							entity.setRepaymentDate(DateUtils.getShortDateOfString(CalendarUtil.getSpecifiedMonthAfter(CalendarUtil.getNextFewMonthFifteen(loanDate, v - 1), span)));
							int insertUserPlanFlag = wloanTermUserPlanDao.insert(entity);
							if (insertUserPlanFlag == 1) {
								log.info("【个人还款计划】-第" + v + "期创建成功.");
							} else {
								log.info("【个人还款计划】-第" + v + "期创建失败.");
							}
							span = span - span;
							log.info("第" + v + "期剩余还款天数：" + span);
						}
					} // --.
					++v; // 终止条件.
				}
			}

			return "SUCCESS";
		} catch (Exception e) {
			e.printStackTrace();
			return "ERROR";
		}
	}

	@Transactional(readOnly = false)
	public int deleteByWloanTermInvestId(String wloanTermInvestId) {

		// TODO Auto-generated method stub
		return wloanTermUserPlanDao.deleteByWloanTermInvestId(wloanTermInvestId);
	}

	public List<WloanTermUserPlan> findNewInterestCount(WloanTermUserPlan userPlan) {

		// TODO Auto-generated method stub
		return wloanTermUserPlanDao.findNewInterestCount(userPlan);
	}

	/**
	 * 
	 * methods: initInvUserPlan <br>
	 * description: 初始化出借人还款计划（每30天为一期，计算月利息 * 期数）. <br>
	 * author: Roy <br>
	 * date: 2019年7月23日 上午11:10:36
	 * 
	 * @param wloanTermInvest
	 * @return
	 */
	@Transactional(readOnly = false)
	public String initInvUserPlan(WloanTermInvest wloanTermInvest) {

		try {
			WloanTermProject project = wloanTermProjectService.get(wloanTermInvest.getWloanTermProject().getId());
			if (project != null) {
				if (project.getSpan() >= SPAN_30) { // 散标期限大于等于30天.
					if (project.getSpan() % SPAN_30 == 0) { // 散标每期周期为30天.
						Integer num = project.getSpan() / SPAN_30;
						WloanTermUserPlan userPlan = null;
						for (int i = 1; i <= num; i++) {
							userPlan = new WloanTermUserPlan();
							userPlan.setId(IdGen.uuid()); // 主键ID.
							userPlan.setWloanTermProject(project); // 项目信息.
							userPlan.setUserInfo(wloanTermInvest.getUserInfo()); // 用户信息.
							// userPlan.setProjectId(project.getId()); // 散标ID.
							userPlan.setRepaymentDate(DateUtils.getShortDateOfString(DateUtils.getSpecifiedMonthAfter(DateUtils.formatDateTime(new Date()), i))); // 还款日.
							if (i == num) {
								// 更新标的结束日期.
								project.setEndDate(DateUtils.getShortDateOfString(DateUtils.getSpecifiedMonthAfter(DateUtils.formatDateTime(new Date()), i)));
								wloanTermProjectService.updateProState(project); // --.
								userPlan.setPrincipal(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_PRINCIPAL_1); // 还本付息.
								userPlan.setInterest(NumberUtils.add(wloanTermInvest.getAmount(), InterestUtils.getMonthInterestFormat(wloanTermInvest.getAmount(), project.getAnnualRate()))); // 月利息保留两位小数.
								userPlan.setInterestTrue(NumberUtils.add(wloanTermInvest.getAmount(), InterestUtils.getMonthInterest(wloanTermInvest.getAmount(), project.getAnnualRate())));
							} else {
								userPlan.setPrincipal(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_PRINCIPAL_2); // 付息.
								userPlan.setInterest(InterestUtils.getMonthInterestFormat(wloanTermInvest.getAmount(), project.getAnnualRate())); // 月利息保留两位小数.
								userPlan.setInterestTrue(InterestUtils.getMonthInterest(wloanTermInvest.getAmount(), project.getAnnualRate())); // 四舍五入之前月利息.
							}
							userPlan.setState(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_2); // 正在还款.
							// userPlan.setWloanTermInvestId(wloanTermInvest.getId()); // 出借记录ID.
							userPlan.setWloanTermInvest(wloanTermInvest); // 出借记录信息.
							int insertFlag = wloanTermUserPlanDao.insert(userPlan);
							if (insertFlag == 1) {
								log.info("出借人出借还款计划插入成功！");
							} else {
								log.warn("出借人出借还款计划插入失败！");
							}
						}
					} else {
						return "ERROR";
					}
				} else {
					return "ERROR";
				}
			} else {
				return "ERROR";
			}
			return "SUCCESS";
		} catch (Exception e) {
			e.printStackTrace();
			return "ERROR";
		}
	}

	public String newInitWloanTermUserPlan(WloanTermInvest wloanTermInvest) {

		try {

			WloanTermProject wloanTermProject = wloanTermProjectService.get(wloanTermInvest.getWloanTermProject().getId());
			// 投资总利息
			Double wloanTermInerest = wloanTermInvest.getInterest();
			// 计算还款期数
			Integer sum = wloanTermProject.getSpan() / 30;
			// 计算每期还款（30天一期）利息
			Double spanMoney = InterestUtils.getMonthInterestFormat(wloanTermInvest.getAmount(), wloanTermProject.getAnnualRate());
			Double spanMoneyTrue = InterestUtils.getMonthInterest(wloanTermInvest.getAmount(), wloanTermProject.getAnnualRate());// 未四舍五入
			WloanTermUserPlan wloanTermUserPlan = null;

			// 保存每期还款计划
			for (int i = 1; i <= sum; i++) {
				wloanTermUserPlan = new WloanTermUserPlan();
				wloanTermUserPlan.setWloanTermProject(wloanTermProject);
				wloanTermUserPlan.setUserInfo(wloanTermInvest.getUserInfo());
				wloanTermUserPlan.setId(IdGen.uuid());
				wloanTermUserPlan.setRepaymentDate(DateUtils.getShortDateOfString(DateUtils.getSpecifiedMonthAfter(DateUtils.formatDateTime(new Date()), i)));
				wloanTermUserPlan.setState(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_2);
				wloanTermUserPlan.setWloanTermInvest(wloanTermInvest);

				// 如果只有一期
				if (1 == sum) {
					wloanTermUserPlan.setInterest(wloanTermInvest.getAmount() + wloanTermInvest.getInterest());
					wloanTermUserPlan.setInterestTrue(wloanTermInvest.getAmount() + wloanTermInvest.getInterest());
					wloanTermUserPlan.setPrincipal("1");
				} else {
					// 最后一期的还款金额为 本金 +[投资总利息-每期利息*(还款期数-1)]
					if (i == sum) {
						Double lastSpanMoney = wloanTermInvest.getAmount() + (wloanTermInerest - spanMoney * (sum - 1));
						wloanTermUserPlan.setInterest(lastSpanMoney);
						wloanTermUserPlan.setInterestTrue(lastSpanMoney);
						wloanTermUserPlan.setPrincipal("1");
					} else {
						wloanTermUserPlan.setInterest(spanMoney);
						wloanTermUserPlan.setInterestTrue(spanMoneyTrue);
						wloanTermUserPlan.setPrincipal("2");
					}
				}

				wloanTermUserPlanDao.insert(wloanTermUserPlan);
			}
			return "SUCCESS";
		} catch (Exception e) {
			e.printStackTrace();
			return "ERROR";
		}
	}

	/**
	 * 用户还款计划生成---新
	 * 
	 * @param wloanTermInvest
	 * @return
	 */
	@Transactional(readOnly = false)
	public String newInitCgbWloanTermUserPlan(WloanTermInvest wloanTermInvest) {

		WloanTermProject project = wloanTermProjectService.get(wloanTermInvest.getWloanTermProject().getId());

		try {
			Date loanDate = new Date();
			log.info("放款日期：" + loanDate);
			/**
			 * 每日利息(按日计息).
			 */
			// 每日利息，保留两位小数.
			Double dayInterestScaleDouble = InterestUtils.getDayInterestFormat(wloanTermInvest.getAmount(), project.getAnnualRate());
			// 每日利息，原始小数位.
			Double dayInterest = InterestUtils.getDayInterest(wloanTermInvest.getAmount(), project.getAnnualRate());

			log.info("每日利息，保留两位小数" + dayInterestScaleDouble);
			log.info("每日利息，原始小数位" + dayInterest);

			// 项目期限.
			int span = project.getSpan();
			String spanStr = String.valueOf(span);
			log.info("项目期限：" + spanStr);
			/**
			 * span-30.
			 */
			if (spanStr.equals(WloanTermProjectService.SPAN_30)) {
				WloanTermUserPlan entity = new WloanTermUserPlan();
				entity.setId(IdGen.uuid());
				entity.setUserInfo(wloanTermInvest.getUserInfo());
				entity.setWloanTermProject(project);
				entity.setState(WLOAN_TERM_USER_PLAN_STATE_2);
				entity.setWloanTermInvest(wloanTermInvest);
				entity.setInterest(NumberUtils.scaleDouble(dayInterestScaleDouble * 30) + wloanTermInvest.getAmount());
				entity.setInterestTrue((dayInterest * 30) + wloanTermInvest.getAmount());
				entity.setPrincipal(WLOAN_TERM_USER_PLAN_PRINCIPAL_1);
				// 还款日期.
				entity.setRepaymentDate(DateUtils.getShortDateOfString(DateUtils.getSpecifiedMonthAfter(DateUtils.formatDateTime(loanDate), 1)));
				int insertUserPlanFlag = wloanTermUserPlanDao.insert(entity);
				if (insertUserPlanFlag == 1) {
					log.info("【项目还款计划】-第1期创建成功.");
				} else {
					log.info("【项目还款计划】-第1期创建失败.");
				}
			} else
			/**
			 * span-90.
			 */
			if (spanStr.equals(WloanTermProjectService.SPAN_90)) {
				// 终止条件.
				int v = 1;
				while (v <= 4) {
					System.out.println("v = " + v);
					if (v == 1) { // 第一个月天数.
						// 开始时间-放款日期，到下月还款日期15号，还款天数.
						int day = CalendarUtil.differentDaysByMillisecond(loanDate, DateUtils.getShortDateOfString(CalendarUtil.getNextFewMonthFifteen(loanDate, v)));
						log.info("第" + v + "期还款天数：" + day);
						log.info("第" + v + "期开始时间：" + DateUtils.getDate(loanDate, "yyyy-MM-dd"));
						span = span - day;
						log.info("第" + v + "期剩余还款天数：" + span);
						log.info("第" + v + "期还款日期：" + CalendarUtil.getNextFewMonthFifteen(loanDate, v));
						// 创建用户还款计划
						WloanTermUserPlan entity = new WloanTermUserPlan();
						entity.setId(IdGen.uuid());
						entity.setUserInfo(wloanTermInvest.getUserInfo());
						entity.setWloanTermProject(project);
						entity.setState(WLOAN_TERM_USER_PLAN_STATE_2);
						entity.setWloanTermInvest(wloanTermInvest);
						entity.setInterest(NumberUtils.scaleDouble(dayInterestScaleDouble * day));
						entity.setInterestTrue((dayInterest * day));
						entity.setPrincipal(WLOAN_TERM_USER_PLAN_PRINCIPAL_2);
						// 还款日期.
						entity.setRepaymentDate(DateUtils.getShortDateOfString(CalendarUtil.getNextFewMonthFifteen(loanDate, v)));
						int insertUserPlanFlag = wloanTermUserPlanDao.insert(entity);
						if (insertUserPlanFlag == 1) {
							log.info("【个人还款计划】-第" + v + "期创建成功.");
						} else {
							log.info("【个人还款计划】-第" + v + "期创建失败.");
						}
					} else {
						// 每月的天数，即为还款天数.
						int day = CalendarUtil.getDaysOfMonth(DateUtils.getShortDateOfString(CalendarUtil.getNextFewMonthFifteen(loanDate, v - 1)));
						// System.out.println("第" + v + "个月天数：" + day);
						if (span >= day) { // 剩余还款天数，是否满足本期还款天数.
							log.info("第" + v + "期开始日期：" + CalendarUtil.getNextFewMonthFifteen(loanDate, v - 1));
							log.info("第" + v + "期还款天数：" + day);
							span = span - day;
							log.info("第" + v + "期剩余还款天数：" + span);
							log.info("第" + v + "期还款日期：" + CalendarUtil.getNextFewMonthFifteen(loanDate, v));
							// 创建用户还款计划
							WloanTermUserPlan entity = new WloanTermUserPlan();
							entity.setId(IdGen.uuid());
							entity.setUserInfo(wloanTermInvest.getUserInfo());
							entity.setWloanTermProject(project);
							entity.setState(WLOAN_TERM_USER_PLAN_STATE_2);
							entity.setWloanTermInvest(wloanTermInvest);
							entity.setInterest(NumberUtils.scaleDouble(dayInterestScaleDouble * day));
							entity.setInterestTrue((dayInterest * day));
							entity.setPrincipal(WLOAN_TERM_USER_PLAN_PRINCIPAL_2);
							// 还款日期.
							entity.setRepaymentDate(DateUtils.getShortDateOfString(CalendarUtil.getNextFewMonthFifteen(loanDate, v)));
							int insertUserPlanFlag = wloanTermUserPlanDao.insert(entity);
							if (insertUserPlanFlag == 1) {
								log.info("【个人还款计划】-第" + v + "期创建成功.");
							} else {
								log.info("【个人还款计划】-第" + v + "期创建失败.");
							}
						} else { // 最后一期还款.
							if (span == 0) {
								break;
							}
							log.info("第" + v + "期开始时间：" + CalendarUtil.getNextFewMonthFifteen(loanDate, v - 1));
							log.info("第" + v + "期还款天数：" + span);
							log.info("第" + v + "期还款日期：" + CalendarUtil.getSpecifiedMonthAfter(CalendarUtil.getNextFewMonthFifteen(loanDate, v - 1), span));
							// 创建用户还款计划
							WloanTermUserPlan entity = new WloanTermUserPlan();
							entity.setId(IdGen.uuid());
							entity.setUserInfo(wloanTermInvest.getUserInfo());
							entity.setWloanTermProject(project);
							entity.setState(WLOAN_TERM_USER_PLAN_STATE_2);
							entity.setWloanTermInvest(wloanTermInvest);
							entity.setInterest(NumberUtils.scaleDouble(dayInterestScaleDouble * span) + wloanTermInvest.getAmount());
							entity.setInterestTrue((dayInterest * span) + wloanTermInvest.getAmount());
							entity.setPrincipal(WLOAN_TERM_USER_PLAN_PRINCIPAL_1);
							// 还款日期.
							entity.setRepaymentDate(DateUtils.getShortDateOfString(CalendarUtil.getSpecifiedMonthAfter(CalendarUtil.getNextFewMonthFifteen(loanDate, v - 1), span)));
							int insertUserPlanFlag = wloanTermUserPlanDao.insert(entity);
							if (insertUserPlanFlag == 1) {
								log.info("【个人还款计划】-第" + v + "期创建成功.");
							} else {
								log.info("【个人还款计划】-第" + v + "期创建失败.");
							}
							span = span - span;
							log.info("第" + v + "期剩余还款天数：" + span);
						}
					} // --.
					++v; // 终止条件.
				}
			} else
			/**
			 * span-180.
			 */
			if (spanStr.equals(WloanTermProjectService.SPAN_180)) {
				// 终止条件.
				int v = 1;
				while (v <= 8) {
					System.out.println("v = " + v);
					if (v == 1) { // 第一个月天数.
						// 开始时间-放款日期，到下月还款日期15号，还款天数.
						int day = CalendarUtil.differentDaysByMillisecond(loanDate, DateUtils.getShortDateOfString(CalendarUtil.getNextFewMonthFifteen(loanDate, v)));
						log.info("第" + v + "期还款天数：" + day);
						log.info("第" + v + "期开始时间：" + DateUtils.getDate(loanDate, "yyyy-MM-dd"));
						span = span - day;
						log.info("第" + v + "期剩余还款天数：" + span);
						log.info("第" + v + "期还款日期：" + CalendarUtil.getNextFewMonthFifteen(loanDate, v));
						// 创建用户还款计划
						WloanTermUserPlan entity = new WloanTermUserPlan();
						entity.setId(IdGen.uuid());
						entity.setUserInfo(wloanTermInvest.getUserInfo());
						entity.setWloanTermProject(project);
						entity.setState(WLOAN_TERM_USER_PLAN_STATE_2);
						entity.setWloanTermInvest(wloanTermInvest);
						entity.setInterest(NumberUtils.scaleDouble(dayInterestScaleDouble * day));
						entity.setInterestTrue((dayInterest * day));
						entity.setPrincipal(WLOAN_TERM_USER_PLAN_PRINCIPAL_2);
						// 还款日期.
						entity.setRepaymentDate(DateUtils.getShortDateOfString(CalendarUtil.getNextFewMonthFifteen(loanDate, v)));
						int insertUserPlanFlag = wloanTermUserPlanDao.insert(entity);
						if (insertUserPlanFlag == 1) {
							log.info("【个人还款计划】-第" + v + "期创建成功.");
						} else {
							log.info("【个人还款计划】-第" + v + "期创建失败.");
						}
					} else {
						// 每月的天数，即为还款天数.
						int day = CalendarUtil.getDaysOfMonth(DateUtils.getShortDateOfString(CalendarUtil.getNextFewMonthFifteen(loanDate, v - 1)));
						// System.out.println("第" + v + "个月天数：" + day);
						if (span >= day) { // 剩余还款天数，是否满足本期还款天数.
							log.info("第" + v + "期开始日期：" + CalendarUtil.getNextFewMonthFifteen(loanDate, v - 1));
							log.info("第" + v + "期还款天数：" + day);
							span = span - day;
							log.info("第" + v + "期剩余还款天数：" + span);
							log.info("第" + v + "期还款日期：" + CalendarUtil.getNextFewMonthFifteen(loanDate, v));
							// 创建用户还款计划
							WloanTermUserPlan entity = new WloanTermUserPlan();
							entity.setId(IdGen.uuid());
							entity.setUserInfo(wloanTermInvest.getUserInfo());
							entity.setWloanTermProject(project);
							entity.setState(WLOAN_TERM_USER_PLAN_STATE_2);
							entity.setWloanTermInvest(wloanTermInvest);
							entity.setInterest(NumberUtils.scaleDouble(dayInterestScaleDouble * day));
							entity.setInterestTrue((dayInterest * day));
							entity.setPrincipal(WLOAN_TERM_USER_PLAN_PRINCIPAL_2);
							// 还款日期.
							entity.setRepaymentDate(DateUtils.getShortDateOfString(CalendarUtil.getNextFewMonthFifteen(loanDate, v)));
							int insertUserPlanFlag = wloanTermUserPlanDao.insert(entity);
							if (insertUserPlanFlag == 1) {
								log.info("【个人还款计划】-第" + v + "期创建成功.");
							} else {
								log.info("【个人还款计划】-第" + v + "期创建失败.");
							}
						} else { // 最后一期还款.
							if (span == 0) {
								break;
							}
							log.info("第" + v + "期开始时间：" + CalendarUtil.getNextFewMonthFifteen(loanDate, v - 1));
							log.info("第" + v + "期还款天数：" + span);
							log.info("第" + v + "期还款日期：" + CalendarUtil.getSpecifiedMonthAfter(CalendarUtil.getNextFewMonthFifteen(loanDate, v - 1), span));
							// 创建用户还款计划
							WloanTermUserPlan entity = new WloanTermUserPlan();
							entity.setId(IdGen.uuid());
							entity.setUserInfo(wloanTermInvest.getUserInfo());
							entity.setWloanTermProject(project);
							entity.setState(WLOAN_TERM_USER_PLAN_STATE_2);
							entity.setWloanTermInvest(wloanTermInvest);
							entity.setInterest(NumberUtils.scaleDouble(dayInterestScaleDouble * span) + wloanTermInvest.getAmount());
							entity.setInterestTrue((dayInterest * span) + wloanTermInvest.getAmount());
							entity.setPrincipal(WLOAN_TERM_USER_PLAN_PRINCIPAL_1);
							// 还款日期.
							entity.setRepaymentDate(DateUtils.getShortDateOfString(CalendarUtil.getSpecifiedMonthAfter(CalendarUtil.getNextFewMonthFifteen(loanDate, v - 1), span)));
							int insertUserPlanFlag = wloanTermUserPlanDao.insert(entity);
							if (insertUserPlanFlag == 1) {
								log.info("【个人还款计划】-第" + v + "期创建成功.");
							} else {
								log.info("【个人还款计划】-第" + v + "期创建失败.");
							}
							span = span - span;
							log.info("第" + v + "期剩余还款天数：" + span);
						}
					} // --.
					++v; // 终止条件.
				}
			} else
			/**
			 * span-360.
			 */
			if (spanStr.equals(WloanTermProjectService.SPAN_360)) {
				// 终止条件.
				int v = 1;
				while (v <= 14) {
					System.out.println("v = " + v);
					if (v == 1) { // 第一个月天数.
						// 开始时间-放款日期，到下月还款日期15号，还款天数.
						int day = CalendarUtil.differentDaysByMillisecond(loanDate, DateUtils.getShortDateOfString(CalendarUtil.getNextFewMonthFifteen(loanDate, v)));
						log.info("第" + v + "期还款天数：" + day);
						log.info("第" + v + "期开始时间：" + DateUtils.getDate(loanDate, "yyyy-MM-dd"));
						span = span - day;
						log.info("第" + v + "期剩余还款天数：" + span);
						log.info("第" + v + "期还款日期：" + CalendarUtil.getNextFewMonthFifteen(loanDate, v));
						// 创建用户还款计划
						WloanTermUserPlan entity = new WloanTermUserPlan();
						entity.setId(IdGen.uuid());
						entity.setUserInfo(wloanTermInvest.getUserInfo());
						entity.setWloanTermProject(project);
						entity.setState(WLOAN_TERM_USER_PLAN_STATE_2);
						entity.setWloanTermInvest(wloanTermInvest);
						entity.setInterest(NumberUtils.scaleDouble(dayInterestScaleDouble * day));
						entity.setInterestTrue((dayInterest * day));
						entity.setPrincipal(WLOAN_TERM_USER_PLAN_PRINCIPAL_2);
						// 还款日期.
						entity.setRepaymentDate(DateUtils.getShortDateOfString(CalendarUtil.getNextFewMonthFifteen(loanDate, v)));
						int insertUserPlanFlag = wloanTermUserPlanDao.insert(entity);
						if (insertUserPlanFlag == 1) {
							log.info("【个人还款计划】-第" + v + "期创建成功.");
						} else {
							log.info("【个人还款计划】-第" + v + "期创建失败.");
						}
					} else {
						// 每月的天数，即为还款天数.
						int day = CalendarUtil.getDaysOfMonth(DateUtils.getShortDateOfString(CalendarUtil.getNextFewMonthFifteen(loanDate, v - 1)));
						// System.out.println("第" + v + "个月天数：" + day);
						if (span >= day) { // 剩余还款天数，是否满足本期还款天数.
							log.info("第" + v + "期开始日期：" + CalendarUtil.getNextFewMonthFifteen(loanDate, v - 1));
							log.info("第" + v + "期还款天数：" + day);
							span = span - day;
							log.info("第" + v + "期剩余还款天数：" + span);
							log.info("第" + v + "期还款日期：" + CalendarUtil.getNextFewMonthFifteen(loanDate, v));
							// 创建用户还款计划
							WloanTermUserPlan entity = new WloanTermUserPlan();
							entity.setId(IdGen.uuid());
							entity.setUserInfo(wloanTermInvest.getUserInfo());
							entity.setWloanTermProject(project);
							entity.setState(WLOAN_TERM_USER_PLAN_STATE_2);
							entity.setWloanTermInvest(wloanTermInvest);
							entity.setInterest(NumberUtils.scaleDouble(dayInterestScaleDouble * day));
							entity.setInterestTrue((dayInterest * day));
							entity.setPrincipal(WLOAN_TERM_USER_PLAN_PRINCIPAL_2);
							// 还款日期.
							entity.setRepaymentDate(DateUtils.getShortDateOfString(CalendarUtil.getNextFewMonthFifteen(loanDate, v)));
							int insertUserPlanFlag = wloanTermUserPlanDao.insert(entity);
							if (insertUserPlanFlag == 1) {
								log.info("【个人还款计划】-第" + v + "期创建成功.");
							} else {
								log.info("【个人还款计划】-第" + v + "期创建失败.");
							}
						} else { // 最后一期还款.
							if (span == 0) {
								break;
							}
							log.info("第" + v + "期开始时间：" + CalendarUtil.getNextFewMonthFifteen(loanDate, v - 1));
							log.info("第" + v + "期还款天数：" + span);
							log.info("第" + v + "期还款日期：" + CalendarUtil.getSpecifiedMonthAfter(CalendarUtil.getNextFewMonthFifteen(loanDate, v - 1), span));
							// 创建用户还款计划
							WloanTermUserPlan entity = new WloanTermUserPlan();
							entity.setId(IdGen.uuid());
							entity.setUserInfo(wloanTermInvest.getUserInfo());
							entity.setWloanTermProject(project);
							entity.setState(WLOAN_TERM_USER_PLAN_STATE_2);
							entity.setWloanTermInvest(wloanTermInvest);
							entity.setInterest(NumberUtils.scaleDouble(dayInterestScaleDouble * span) + wloanTermInvest.getAmount());
							entity.setInterestTrue((dayInterest * span) + wloanTermInvest.getAmount());
							entity.setPrincipal(WLOAN_TERM_USER_PLAN_PRINCIPAL_1);
							// 还款日期.
							entity.setRepaymentDate(DateUtils.getShortDateOfString(CalendarUtil.getSpecifiedMonthAfter(CalendarUtil.getNextFewMonthFifteen(loanDate, v - 1), span)));
							int insertUserPlanFlag = wloanTermUserPlanDao.insert(entity);
							if (insertUserPlanFlag == 1) {
								log.info("【个人还款计划】-第" + v + "期创建成功.");
							} else {
								log.info("【个人还款计划】-第" + v + "期创建失败.");
							}
							span = span - span;
							log.info("第" + v + "期剩余还款天数：" + span);
						}
					} // --.
					++v; // 终止条件.
				}
			}

			return "SUCCESS";
		} catch (Exception e) {
			e.printStackTrace();
			return "ERROR";
		}
	}

	/**
	 * @description:查询用户还款计划
	 * @param userId
	 * @param startRepaymentDate
	 * @param endRepaymentDate
	 * @return
	 */
	public List<WloanTermUserPlan> findPlan(String userId, String startRepaymentDate, String endRepaymentDate) {
		
		return wloanTermUserPlanDao.findPlan(userId,startRepaymentDate,endRepaymentDate);
	}

}
