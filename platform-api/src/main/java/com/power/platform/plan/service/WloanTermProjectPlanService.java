package com.power.platform.plan.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.service.CrudService;
import com.power.platform.common.utils.CalendarUtil;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.InterestUtils;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.plan.dao.WloanTermProjectPlanDao;
import com.power.platform.plan.dao.WloanTermUserPlanDao;
import com.power.platform.plan.entity.WloanTermProjectPlan;
import com.power.platform.regular.dao.WloanTermProjectDao;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.regular.service.WloanTermProjectService;

/**
 * 定期还款计划
 * 
 * @author wangjingsong
 *
 */
@Service("wloanTermProjectPlanService")
@Transactional(readOnly = true)
public class WloanTermProjectPlanService extends CrudService<WloanTermProjectPlan> {

	private static final Logger log = Logger.getLogger(WloanTermProjectPlanService.class);

	/**
	 * 1：还款中.
	 */
	public static final String WLOAN_TERM_PROJECT_PLAN_STATE_1 = "1";

	/**
	 * 2：还款成功.
	 */
	public static final String WLOAN_TERM_PROJECT_PLAN_STATE_2 = "2";

	/**
	 * 3：还款失败.
	 */
	public static final String WLOAN_TERM_PROJECT_PLAN_STATE_3 = "3";

	/**
	 * 4：流标.
	 */
	public static final String WLOAN_TERM_PROJECT_PLAN_STATE_4 = "4";

	/**
	 * 0：付息.
	 */
	public static final String WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_0 = "0";
	/**
	 * 1：还本付息.
	 */
	public static final String WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1 = "1";

	@Resource
	private WloanTermProjectPlanDao wloanTermProjectPlanDao;
	@Resource
	private WloanTermUserPlanDao wloanTermUserPlanDao;

	@Resource
	private WloanTermProjectDao wloanTermProjectDao;

	protected CrudDao<WloanTermProjectPlan> getEntityDao() {

		return wloanTermProjectPlanDao;
	}

	/**
	 * 
	 * methods: findCrePayPrincipalAndInterestPageZ <br>
	 * description: 围绕散标-增量-借款人还本付息交易流水. <br>
	 * author: Roy <br>
	 * date: 2019年6月3日 下午6:17:00
	 * 
	 * @param page
	 * @param proPlan
	 * @return
	 */
	public Page<WloanTermProjectPlan> findCrePayPrincipalAndInterestPageZ(Page<WloanTermProjectPlan> page, WloanTermProjectPlan proPlan) {

		proPlan.setPage(page);
		page.setList(wloanTermProjectPlanDao.findCrePayPrincipalAndInterestListZ(proPlan));
		return page;
	}

	/**
	 * 
	 * methods: findCrePayPrincipalAndInterestPage <br>
	 * description: 存量借款人还本付息记录交易流水. <br>
	 * author: Roy <br>
	 * date: 2019年5月30日 下午5:45:14
	 * 
	 * @param page
	 * @param proPlan
	 * @return
	 */
	public Page<WloanTermProjectPlan> findCrePayPrincipalAndInterestPage(Page<WloanTermProjectPlan> page, WloanTermProjectPlan proPlan) {

		proPlan.setPage(page);
		page.setList(wloanTermProjectPlanDao.findCrePayPrincipalAndInterestList(proPlan));
		return page;
	}

	/**
	 * 
	 * 方法: initCgbWloanTermProjectPlan <br>
	 * 描述: 存管保项目还款计划初始化. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年12月4日 下午4:50:37
	 * 
	 * @param project
	 * @return
	 */
	@Transactional(readOnly = false)
	public String initCgbWloanTermProjectPlan(WloanTermProject project) {

		try {
			Date loanDate = project.getLoanDate();
			log.info("放款日期：" + loanDate);
			/**
			 * 每日利息(按日计息).
			 */
			// 每日利息，保留两位小数.
			Double dayInterestScaleDouble = InterestUtils.getDayInterestFormat(project.getAmount(), project.getAnnualRate());
			// 每日利息，原始小数位.
			Double dayInterest = InterestUtils.getDayInterest(project.getAmount(), project.getAnnualRate());
			// 项目期限.
			int span = project.getSpan();
			String spanStr = String.valueOf(span);
			log.info("项目期限：" + spanStr);
			/**
			 * span-30.
			 */
			if (spanStr.equals(WloanTermProjectService.SPAN_30)) {
				WloanTermProjectPlan entity = new WloanTermProjectPlan();
				// 主键.
				entity.setId(IdGen.uuid());
				// 子订单号，用于还款.
				entity.setSubOrderId(IdGen.uuid());
				// 项目信息.
				entity.setWloanTermProject(project);
				// 还款状态.
				entity.setState(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_STATE_1);
				// 还款利息，保留两位小数.
				entity.setInterest(NumberUtils.scaleDouble(dayInterestScaleDouble * 30) + project.getAmount());
				// 还款利息，原始小数位.
				entity.setInterestTrue((dayInterest * 30) + project.getAmount());
				// 还款类型.
				entity.setPrincipal(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1);
				// 还款日期.
				entity.setRepaymentDate(DateUtils.getShortDateOfString(DateUtils.getSpecifiedMonthAfter(DateUtils.formatDateTime(loanDate), 1)));
				int insertProjectPlanFlag = wloanTermProjectPlanDao.insert(entity);
				if (insertProjectPlanFlag == 1) {
					log.info("【项目还款计划】-第1期创建成功.");
					if (WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1.equals(entity.getPrincipal())) {
						project.setEndDate(entity.getRepaymentDate());
						int updateProjectInfo = wloanTermProjectDao.update(project);
						if (updateProjectInfo == 1) {
							log.info("【项目还款计划】-项目信息更新成功.");
						} else {
							log.info("【项目还款计划】-项目信息更新失败.");
						}
					}
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
						/**
						 * 创建项目还款计划.
						 */
						WloanTermProjectPlan entity = new WloanTermProjectPlan();
						// 主键.
						entity.setId(IdGen.uuid());
						// 子订单号，用于还款.
						entity.setSubOrderId(IdGen.uuid());
						// 项目信息.
						entity.setWloanTermProject(project);
						// 还款状态.
						entity.setState(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_STATE_1);
						// 还款利息，保留两位小数.
						entity.setInterest(NumberUtils.scaleDouble(dayInterestScaleDouble * day));
						// 还款利息，原始小数位.
						entity.setInterestTrue((dayInterest * day));
						// 还款类型.
						entity.setPrincipal(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_0);
						// 还款日期.
						entity.setRepaymentDate(DateUtils.getShortDateOfString(CalendarUtil.getNextFewMonthFifteen(loanDate, v)));
						int insertProjectPlanFlag = wloanTermProjectPlanDao.insert(entity);
						if (insertProjectPlanFlag == 1) {
							log.info("【项目还款计划】-第" + v + "期创建成功.");
						} else {
							log.info("【项目还款计划】-第" + v + "期创建失败.");
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
							/**
							 * 创建项目还款计划.
							 */
							WloanTermProjectPlan entity = new WloanTermProjectPlan();
							// 主键.
							entity.setId(IdGen.uuid());
							// 子订单号，用于还款.
							entity.setSubOrderId(IdGen.uuid());
							// 项目信息.
							entity.setWloanTermProject(project);
							// 还款状态.
							entity.setState(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_STATE_1);
							// 还款利息，保留两位小数.
							entity.setInterest(NumberUtils.scaleDouble(dayInterestScaleDouble * day));
							// 还款利息，原始小数位.
							entity.setInterestTrue((dayInterest * day));
							// 还款类型.
							entity.setPrincipal(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_0);
							// 还款日期.
							entity.setRepaymentDate(DateUtils.getShortDateOfString(CalendarUtil.getNextFewMonthFifteen(loanDate, v)));
							int insertProjectPlanFlag = wloanTermProjectPlanDao.insert(entity);
							if (insertProjectPlanFlag == 1) {
								log.info("【项目还款计划】-第" + v + "期创建成功.");
							} else {
								log.info("【项目还款计划】-第" + v + "期创建失败.");
							}
						} else { // 最后一期还款.
							if (span == 0) {
								break;
							}
							log.info("第" + v + "期开始时间：" + CalendarUtil.getNextFewMonthFifteen(loanDate, v - 1));
							log.info("第" + v + "期还款天数：" + span);
							log.info("第" + v + "期还款日期：" + CalendarUtil.getSpecifiedMonthAfter(CalendarUtil.getNextFewMonthFifteen(loanDate, v - 1), span));
							/**
							 * 创建项目还款计划.
							 */
							WloanTermProjectPlan entity = new WloanTermProjectPlan();
							// 主键.
							entity.setId(IdGen.uuid());
							// 子订单号，用于还款.
							entity.setSubOrderId(IdGen.uuid());
							// 项目信息.
							entity.setWloanTermProject(project);
							// 还款状态.
							entity.setState(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_STATE_1);
							// 还款利息，保留两位小数.
							entity.setInterest(NumberUtils.scaleDouble(dayInterestScaleDouble * span) + project.getAmount());
							// 还款利息，原始小数位.
							entity.setInterestTrue((dayInterest * span) + project.getAmount());
							// 还款类型.
							entity.setPrincipal(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1);
							// 还款日期.
							entity.setRepaymentDate(DateUtils.getShortDateOfString(CalendarUtil.getSpecifiedMonthAfter(CalendarUtil.getNextFewMonthFifteen(loanDate, v - 1), span)));
							int insertProjectPlanFlag = wloanTermProjectPlanDao.insert(entity);
							if (insertProjectPlanFlag == 1) {
								log.info("【项目还款计划】-第" + v + "期创建成功.");
							} else {
								log.info("【项目还款计划】-第" + v + "期创建失败.");
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
						/**
						 * 创建项目还款计划.
						 */
						WloanTermProjectPlan entity = new WloanTermProjectPlan();
						// 主键.
						entity.setId(IdGen.uuid());
						// 子订单号，用于还款.
						entity.setSubOrderId(IdGen.uuid());
						// 项目信息.
						entity.setWloanTermProject(project);
						// 还款状态.
						entity.setState(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_STATE_1);
						// 还款利息，保留两位小数.
						entity.setInterest(NumberUtils.scaleDouble(dayInterestScaleDouble * day));
						// 还款利息，原始小数位.
						entity.setInterestTrue((dayInterest * day));
						// 还款类型.
						entity.setPrincipal(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_0);
						// 还款日期.
						entity.setRepaymentDate(DateUtils.getShortDateOfString(CalendarUtil.getNextFewMonthFifteen(loanDate, v)));
						int insertProjectPlanFlag = wloanTermProjectPlanDao.insert(entity);
						if (insertProjectPlanFlag == 1) {
							log.info("【项目还款计划】-第" + v + "期创建成功.");
						} else {
							log.info("【项目还款计划】-第" + v + "期创建失败.");
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
							/**
							 * 创建项目还款计划.
							 */
							WloanTermProjectPlan entity = new WloanTermProjectPlan();
							// 主键.
							entity.setId(IdGen.uuid());
							// 子订单号，用于还款.
							entity.setSubOrderId(IdGen.uuid());
							// 项目信息.
							entity.setWloanTermProject(project);
							// 还款状态.
							entity.setState(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_STATE_1);
							// 还款利息，保留两位小数.
							entity.setInterest(NumberUtils.scaleDouble(dayInterestScaleDouble * day));
							// 还款利息，原始小数位.
							entity.setInterestTrue((dayInterest * day));
							// 还款类型.
							entity.setPrincipal(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_0);
							// 还款日期.
							entity.setRepaymentDate(DateUtils.getShortDateOfString(CalendarUtil.getNextFewMonthFifteen(loanDate, v)));
							int insertProjectPlanFlag = wloanTermProjectPlanDao.insert(entity);
							if (insertProjectPlanFlag == 1) {
								log.info("【项目还款计划】-第" + v + "期创建成功.");
							} else {
								log.info("【项目还款计划】-第" + v + "期创建失败.");
							}
						} else { // 最后一期还款.
							if (span == 0) {
								break;
							}
							log.info("第" + v + "期开始时间：" + CalendarUtil.getNextFewMonthFifteen(loanDate, v - 1));
							log.info("第" + v + "期还款天数：" + span);
							log.info("第" + v + "期还款日期：" + CalendarUtil.getSpecifiedMonthAfter(CalendarUtil.getNextFewMonthFifteen(loanDate, v - 1), span));
							/**
							 * 创建项目还款计划.
							 */
							WloanTermProjectPlan entity = new WloanTermProjectPlan();
							// 主键.
							entity.setId(IdGen.uuid());
							// 子订单号，用于还款.
							entity.setSubOrderId(IdGen.uuid());
							// 项目信息.
							entity.setWloanTermProject(project);
							// 还款状态.
							entity.setState(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_STATE_1);
							// 还款利息，保留两位小数.
							entity.setInterest(NumberUtils.scaleDouble(dayInterestScaleDouble * span) + project.getAmount());
							// 还款利息，原始小数位.
							entity.setInterestTrue((dayInterest * span) + project.getAmount());
							// 还款类型.
							entity.setPrincipal(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1);
							// 还款日期.
							entity.setRepaymentDate(DateUtils.getShortDateOfString(CalendarUtil.getSpecifiedMonthAfter(CalendarUtil.getNextFewMonthFifteen(loanDate, v - 1), span)));
							int insertProjectPlanFlag = wloanTermProjectPlanDao.insert(entity);
							if (insertProjectPlanFlag == 1) {
								log.info("【项目还款计划】-第" + v + "期创建成功.");
							} else {
								log.info("【项目还款计划】-第" + v + "期创建失败.");
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
						/**
						 * 创建项目还款计划.
						 */
						WloanTermProjectPlan entity = new WloanTermProjectPlan();
						// 主键.
						entity.setId(IdGen.uuid());
						// 子订单号，用于还款.
						entity.setSubOrderId(IdGen.uuid());
						// 项目信息.
						entity.setWloanTermProject(project);
						// 还款状态.
						entity.setState(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_STATE_1);
						// 还款利息，保留两位小数.
						entity.setInterest(NumberUtils.scaleDouble(dayInterestScaleDouble * day));
						// 还款利息，原始小数位.
						entity.setInterestTrue((dayInterest * day));
						// 还款类型.
						entity.setPrincipal(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_0);
						// 还款日期.
						entity.setRepaymentDate(DateUtils.getShortDateOfString(CalendarUtil.getNextFewMonthFifteen(loanDate, v)));
						int insertProjectPlanFlag = wloanTermProjectPlanDao.insert(entity);
						if (insertProjectPlanFlag == 1) {
							log.info("【项目还款计划】-第" + v + "期创建成功.");
						} else {
							log.info("【项目还款计划】-第" + v + "期创建失败.");
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
							/**
							 * 创建项目还款计划.
							 */
							WloanTermProjectPlan entity = new WloanTermProjectPlan();
							// 主键.
							entity.setId(IdGen.uuid());
							// 子订单号，用于还款.
							entity.setSubOrderId(IdGen.uuid());
							// 项目信息.
							entity.setWloanTermProject(project);
							// 还款状态.
							entity.setState(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_STATE_1);
							// 还款利息，保留两位小数.
							entity.setInterest(NumberUtils.scaleDouble(dayInterestScaleDouble * day));
							// 还款利息，原始小数位.
							entity.setInterestTrue((dayInterest * day));
							// 还款类型.
							entity.setPrincipal(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_0);
							// 还款日期.
							entity.setRepaymentDate(DateUtils.getShortDateOfString(CalendarUtil.getNextFewMonthFifteen(loanDate, v)));
							int insertProjectPlanFlag = wloanTermProjectPlanDao.insert(entity);
							if (insertProjectPlanFlag == 1) {
								log.info("【项目还款计划】-第" + v + "期创建成功.");
							} else {
								log.info("【项目还款计划】-第" + v + "期创建失败.");
							}
						} else { // 最后一期还款.
							if (span == 0) {
								break;
							}
							log.info("第" + v + "期开始时间：" + CalendarUtil.getNextFewMonthFifteen(loanDate, v - 1));
							log.info("第" + v + "期还款天数：" + span);
							log.info("第" + v + "期还款日期：" + CalendarUtil.getSpecifiedMonthAfter(CalendarUtil.getNextFewMonthFifteen(loanDate, v - 1), span));
							/**
							 * 创建项目还款计划.
							 */
							WloanTermProjectPlan entity = new WloanTermProjectPlan();
							// 主键.
							entity.setId(IdGen.uuid());
							// 子订单号，用于还款.
							entity.setSubOrderId(IdGen.uuid());
							// 项目信息.
							entity.setWloanTermProject(project);
							// 还款状态.
							entity.setState(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_STATE_1);
							// 还款利息，保留两位小数.
							entity.setInterest(NumberUtils.scaleDouble(dayInterestScaleDouble * span) + project.getAmount());
							// 还款利息，原始小数位.
							entity.setInterestTrue((dayInterest * span) + project.getAmount());
							// 还款类型.
							entity.setPrincipal(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1);
							// 还款日期.
							entity.setRepaymentDate(DateUtils.getShortDateOfString(CalendarUtil.getSpecifiedMonthAfter(CalendarUtil.getNextFewMonthFifteen(loanDate, v - 1), span)));
							int insertProjectPlanFlag = wloanTermProjectPlanDao.insert(entity);
							if (insertProjectPlanFlag == 1) {
								log.info("【项目还款计划】-第" + v + "期创建成功.");
							} else {
								log.info("【项目还款计划】-第" + v + "期创建失败.");
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
	 * 
	 * methods: initWloanTermProjectPlan <br>
	 * description: 标的创建，初始化还款计划 <br>
	 * author: Roy <br>
	 * date: 2019年10月14日 下午7:16:29
	 * 
	 * @param wloanTermProject
	 * @return
	 */
	@Transactional(readOnly = false)
	public String initWloanTermProjectPlan(WloanTermProject wloanTermProject) {

		try {
			// 计算还款期数
			Integer sum = wloanTermProject.getSpan() / 30;
			// 计算每期（30天一期）月利息保留两位小数
			Double spanMoney = InterestUtils.getMonthInterestFormat(wloanTermProject.getAmount(), wloanTermProject.getAnnualRate());// 四舍五入
			logger.info("保留两位小数，每期利息:{}", spanMoney);
			// 计算每期（30天一期）月利息保留原始小数位
			Double spanMoneyTrue = InterestUtils.getMonthInterest(wloanTermProject.getAmount(), wloanTermProject.getAnnualRate());// 未四舍五入
			logger.info("保留原始小数位，每期利息:{}", spanMoneyTrue);

			// 保存每期还款计划
			for (int i = 0; i < sum; i++) {
				WloanTermProjectPlan wloanTermProjectPlan = new WloanTermProjectPlan();
				wloanTermProjectPlan.setId(IdGen.uuid()); // 主键.
				wloanTermProjectPlan.setSubOrderId(IdGen.uuid()); // 子订单号，用于还款.
				wloanTermProjectPlan.setOrderStatus("FALSE"); // 还款按钮标识，防止用户二次批量交易还款至存管行
				wloanTermProjectPlan.setWloanTermProject(wloanTermProject);
				if (Integer.valueOf(wloanTermProject.getState()) >= Integer.valueOf(WloanTermProjectService.ONLINE)) {
					if (sum == 1) { // 只有一期
						wloanTermProjectPlan.setInterest(NumberUtils.add(spanMoney, wloanTermProject.getAmount()));
						wloanTermProjectPlan.setInterestTrue(NumberUtils.add(spanMoneyTrue, wloanTermProject.getAmount()));
						wloanTermProjectPlan.setPrincipal(WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1);
					} else {
						if (i == (sum - 1)) {
							// 最后一期加上本金
							wloanTermProjectPlan.setInterest(NumberUtils.add(spanMoney, wloanTermProject.getAmount()));
							wloanTermProjectPlan.setInterestTrue(NumberUtils.add(spanMoneyTrue, wloanTermProject.getAmount()));
							wloanTermProjectPlan.setPrincipal(WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1);
						} else {
							wloanTermProjectPlan.setInterest(spanMoney);
							wloanTermProjectPlan.setInterestTrue(spanMoneyTrue);
							wloanTermProjectPlan.setPrincipal(WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_0);
						}
					}
				} else {
					if (i == (sum - 1)) {
						// 最后一期加上本金
						wloanTermProjectPlan.setInterest(NumberUtils.add(spanMoney, wloanTermProject.getAmount()));
						wloanTermProjectPlan.setInterestTrue(NumberUtils.add(spanMoneyTrue, wloanTermProject.getAmount()));
						wloanTermProjectPlan.setPrincipal(WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1);
					} else {
						wloanTermProjectPlan.setInterest(spanMoney);
						wloanTermProjectPlan.setInterestTrue(spanMoneyTrue);
						wloanTermProjectPlan.setPrincipal(WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_0);
					}
				}

				wloanTermProjectPlan.setState(WLOAN_TERM_PROJECT_PLAN_STATE_1);
				logger.info("标的流标日期:{}", DateUtils.formatDateTime(wloanTermProject.getLoanDate()));
				wloanTermProjectPlan.setRepaymentDate(DateUtils.getShortDateOfString(DateUtils.getSpecifiedMonthAfter(DateUtils.formatDateTime(wloanTermProject.getLoanDate()), i + 1)));
				int insertProPlanFlag = wloanTermProjectPlanDao.insert(wloanTermProjectPlan);
				logger.info("标的还款计划插入:{}", insertProPlanFlag == 1 ? "成功" : "失败");

				if (WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1.equals(wloanTermProjectPlan.getPrincipal())) { // 最后一期，还本付息
					// 更新标的结束日期
					wloanTermProject.setEndDate(wloanTermProjectPlan.getRepaymentDate());
					int updateProFlag = wloanTermProjectDao.update(wloanTermProject);
					logger.info("最后一期，标的还款计划生成成功，更新标的结束日期:{}", updateProFlag == 1 ? "成功" : "失败");
				}
			}
			return "SUCCESS";
		} catch (Exception e) {
			e.printStackTrace();
			return "ERROR";
		}
	}

	/**
	 * 根据定期项目主体id查询还款计划
	 * 
	 * @param projectId
	 *            定期项目主体id
	 * @return List<WloanTermProjectPlan> 还款计划list集合
	 */
	public List<WloanTermProjectPlan> findListByProjectId(String projectId) {

		WloanTermProjectPlan wloanTermProjectPlan = new WloanTermProjectPlan();
		WloanTermProject wloanTermProject = new WloanTermProject();
		wloanTermProject.setId(projectId);
		wloanTermProjectPlan.setWloanTermProject(wloanTermProject);
		List<WloanTermProjectPlan> list = new ArrayList<WloanTermProjectPlan>();
		list = wloanTermProjectPlanDao.findList(wloanTermProjectPlan);
		return list;
	}

	@Transactional(readOnly = false)
	public void deleteByProjectId(String projectId) {

		wloanTermProjectPlanDao.deleteByProjectId(projectId);
	}

	public List<WloanTermProjectPlan> findListRefund(String state) {

		return wloanTermProjectPlanDao.findListRefund(state);
	}

	@Transactional(readOnly = false)
	public boolean updateWloanTermProjectPlanState(String id, String state) {

		try {
			wloanTermProjectPlanDao.updateWloanTermProjectPlanState(id, state);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * 
	 * methods: newInitWloanTermProjectPlan <br>
	 * description: 初始化标的还款计划 <br>
	 * author: Roy <br>
	 * date: 2019年10月14日 下午7:10:44
	 * 
	 * @param wloanTermProject
	 * @return
	 */
	@Transactional(readOnly = false)
	public String newInitWloanTermProjectPlan(WloanTermProject wloanTermProject) {

		try {
			// 计算还款期数
			Integer sum = wloanTermProject.getSpan() / 30;
			// 计算每期（30天一期）月利息保留两位小数
			Double spanMoney = InterestUtils.getMonthInterestFormat(wloanTermProject.getAmount(), wloanTermProject.getAnnualRate());// 四舍五入
			logger.info("保留两位小数，每期利息:{}", spanMoney);
			// 计算每期（30天一期）月利息保留原始小数位
			Double spanMoneyTrue = InterestUtils.getMonthInterest(wloanTermProject.getAmount(), wloanTermProject.getAnnualRate());// 未四舍五入
			logger.info("保留原始小数位，每期利息:{}", spanMoneyTrue);

			// 保存每期还款计划
			for (int i = 0; i < sum; i++) {
				WloanTermProjectPlan wloanTermProjectPlan = new WloanTermProjectPlan();
				wloanTermProjectPlan.setId(IdGen.uuid()); // 主键.
				wloanTermProjectPlan.setSubOrderId(IdGen.uuid()); // 子订单号，用于还款.
				wloanTermProjectPlan.setOrderStatus("FALSE"); // 还款按钮标识，防止用户二次批量交易还款至存管行
				wloanTermProjectPlan.setWloanTermProject(wloanTermProject);
				if (Integer.valueOf(wloanTermProject.getState()) >= Integer.valueOf(WloanTermProjectService.ONLINE)) {
					if (sum == 1) { // 只有一期
						wloanTermProjectPlan.setInterest(NumberUtils.add(spanMoney, wloanTermProject.getAmount()));
						wloanTermProjectPlan.setInterestTrue(NumberUtils.add(spanMoneyTrue, wloanTermProject.getAmount()));
						wloanTermProjectPlan.setPrincipal(WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1);
					} else {
						if (i == (sum - 1)) {
							// 最后一期加上本金
							wloanTermProjectPlan.setInterest(NumberUtils.add(spanMoney, wloanTermProject.getAmount()));
							wloanTermProjectPlan.setInterestTrue(NumberUtils.add(spanMoneyTrue, wloanTermProject.getAmount()));
							wloanTermProjectPlan.setPrincipal(WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1);
						} else {
							wloanTermProjectPlan.setInterest(spanMoney);
							wloanTermProjectPlan.setInterestTrue(spanMoneyTrue);
							wloanTermProjectPlan.setPrincipal(WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_0);
						}
					}
				} else {
					if (i == (sum - 1)) {
						// 最后一期加上本金
						wloanTermProjectPlan.setInterest(NumberUtils.add(spanMoney, wloanTermProject.getAmount()));
						wloanTermProjectPlan.setInterestTrue(NumberUtils.add(spanMoneyTrue, wloanTermProject.getAmount()));
						wloanTermProjectPlan.setPrincipal(WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1);
					} else {
						wloanTermProjectPlan.setInterest(spanMoney);
						wloanTermProjectPlan.setInterestTrue(spanMoneyTrue);
						wloanTermProjectPlan.setPrincipal(WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_0);
					}
				}

				wloanTermProjectPlan.setState(WLOAN_TERM_PROJECT_PLAN_STATE_1);
				logger.info("标的满标日期:{}", DateUtils.formatDateTime(wloanTermProject.getFullDate()));
				wloanTermProjectPlan.setRepaymentDate(DateUtils.getShortDateOfString(DateUtils.getSpecifiedMonthAfter(DateUtils.formatDateTime(wloanTermProject.getFullDate()), i + 1)));
				int insertProPlanFlag = wloanTermProjectPlanDao.insert(wloanTermProjectPlan);
				logger.info("标的还款计划插入:{}", insertProPlanFlag == 1 ? "成功" : "失败");

				if (WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1.equals(wloanTermProjectPlan.getPrincipal())) { // 最后一期，还本付息
					// 更新标的结束日期
					wloanTermProject.setEndDate(wloanTermProjectPlan.getRepaymentDate());
					int updateProFlag = wloanTermProjectDao.update(wloanTermProject);
					logger.info("最后一期，标的还款计划生成成功，更新标的结束日期:{}", updateProFlag == 1 ? "成功" : "失败");
				}
			}
			return "SUCCESS";
		} catch (Exception e) {
			e.printStackTrace();
			return "ERROR";
		}
	}
}
