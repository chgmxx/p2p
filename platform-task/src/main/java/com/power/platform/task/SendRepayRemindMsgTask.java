package com.power.platform.task;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.config.Global;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.common.utils.SendMailUtil;
import com.power.platform.credit.dao.userinfo.CreditUserInfoDao;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.plan.dao.WloanTermProjectPlanDao;
import com.power.platform.plan.entity.WloanTermProjectPlan;
import com.power.platform.plan.service.WloanTermProjectPlanService;
import com.power.platform.regular.dao.WloanSubjectDao;
import com.power.platform.regular.entity.WloanSubject;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.regular.service.WloanTermProjectService;
import com.power.platform.weixin.service.WeixinSendTempMsgService;

/**
 * 
 * 类: SendRepayRemindMsgTask <br>
 * 描述: 发送本月项目还款日(T-7、T-5、T-2)还款短消息提醒. <br>
 * 作者: Mr.Roy <br>
 * 时间: 2018年5月8日 下午2:45:17
 */
@Service
@Lazy(false)
public class SendRepayRemindMsgTask {

	private static final Logger log = Logger.getLogger(SendRepayRemindMsgTask.class);

	/**
	 * 风控同事杨辉邮箱.
	 */
	private static final String CRO_EMAIL = Global.getConfigUb("CRO_EMAIL");

	/**
	 * 前两天.
	 */
	public static final int NEGATIVE_NUMBER_2 = -2;
	/**
	 * 前五天.
	 */
	public static final int NEGATIVE_NUMBER_5 = -5;
	/**
	 * 前七天.
	 */
	public static final int NEGATIVE_NUMBER_7 = -7;

	@Resource
	private WloanTermProjectPlanDao wloanTermProjectPlanDao;
	@Autowired
	private WeixinSendTempMsgService weixinSendTempMsgService;
	@Autowired
	private WloanTermProjectService wloanTermProjectService;
	@Resource
	private CreditUserInfoDao creditUserInfoDao;
	@Resource
	private WloanSubjectDao wloanSubjectDao;

	/**
	 * 
	 * 方法: sendRepayRemindMsg <br>
	 * 描述: 发送本月项目还款日(T-7、T-5、T-2)还款短消息提醒. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年5月11日 上午9:15:56
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	@Scheduled(cron = "0 0 10 * * ?")
	public void sendRepayRemindMsg() {

		try {
			Calendar c = Calendar.getInstance();
			Date nowDateTime = c.getTime(); // 当前时间.
			/**
			 * 本月待还项目列表.
			 */
			WloanTermProjectPlan wloanTermProjectPlan = new WloanTermProjectPlan(); // 项目还款计划.
			WloanTermProject wloanTermProject = new WloanTermProject(); // 项目联查条件.
			wloanTermProject.setState(WloanTermProjectService.REPAYMENT); // 标的流转状态：还款中.
			wloanTermProjectPlan.setWloanTermProject(wloanTermProject);
			wloanTermProjectPlan.setState(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_STATE_1); // 还款计划状态：还款中.
			wloanTermProjectPlan.setBeginDate(DateUtils.dayOfMonth_Start() + " 00:00:00"); // 还款日期开始时间.
			wloanTermProjectPlan.setEndDate(DateUtils.dayOfMonth_End() + " 23:59:59"); // 还款日期结束时间.
			List<WloanTermProjectPlan> projectPlans = wloanTermProjectPlanDao.findList(wloanTermProjectPlan);
			log.info(this.getClass() + "-还款计划 sum = " + projectPlans.size());
			for (WloanTermProjectPlan projectPlan : projectPlans) { // 遍历本月份的还款计划.
				// 消息主体封装.
				String messageSubject = "中投摩根平台借款催收邮件";
				String repaymentDate = DateUtils.getDate(projectPlan.getRepaymentDate(), "yyyy年MM月dd日"); // 还款日.
				StringBuffer sms_message = new StringBuffer();
				StringBuffer email_message = new StringBuffer();
				if (projectPlan.getPrincipal().equals(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_0)) { // 付息.
					// 短信.
					sms_message.append("尊敬的借款人：您的借款项目-").append(projectPlan.getWloanTermProject().getName() + "(" + projectPlan.getWloanTermProject().getSn() + ")");
					sms_message.append("将于" + repaymentDate).append("应还利息合计").append(NumberUtils.scaleDoubleStr(projectPlan.getInterest()) + "，");
					sms_message.append("请在收到短信此日17时前存足余额。如已充值请忽略此短信，谢谢！");
					// 邮件.
					email_message.append("尊敬的借款人：\n您的借款项目-").append(projectPlan.getWloanTermProject().getName() + "(" + projectPlan.getWloanTermProject().getSn() + ")");
					email_message.append("将于" + repaymentDate).append("应还利息合计").append(NumberUtils.scaleDoubleStr(projectPlan.getInterest()) + "，");
					email_message.append("请在收到邮件此日17时前存足余额。如已充值请忽略此邮件，谢谢！");
				} else if (projectPlan.getPrincipal().equals(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1)) { // 还本付息.
					// 短信.
					sms_message.append("尊敬的借款人：您的借款项目-").append(projectPlan.getWloanTermProject().getName() + "(" + projectPlan.getWloanTermProject().getSn() + ")");
					sms_message.append("将于" + repaymentDate).append("应还本息合计").append(NumberUtils.scaleDoubleStr(projectPlan.getInterest()) + "，");
					sms_message.append("请在收到短信此日17时前存足余额。如已充值请忽略此短信，谢谢！");
					// 邮件.
					email_message.append("尊敬的借款人：\n您的借款项目-").append(projectPlan.getWloanTermProject().getName() + "(" + projectPlan.getWloanTermProject().getSn() + ")");
					email_message.append("将于" + repaymentDate).append("应还本息合计").append(NumberUtils.scaleDoubleStr(projectPlan.getInterest()) + "，");
					email_message.append("请在收到邮件此日17时前存足余额。如已充值请忽略此邮件，谢谢！");
				}
				// 项目.
				WloanTermProject project = wloanTermProjectService.get(projectPlan.getProjectId());
				if (null != project) { // 项目非NULL判断.
					if (WloanTermProjectService.PROJECT_PRODUCT_TYPE_1.equals(project.getProjectProductType())) {
						WloanSubject wloanSubject = wloanSubjectDao.get(project.getSubjectId());
						if (null != wloanSubject) { // 融资主体.
							String email = wloanSubject.getEmail(); // 邮箱.
							CreditUserInfo creditUserInfo = creditUserInfoDao.get(wloanSubject.getLoanApplyId());
							if (null != creditUserInfo) { // 借款企业帐号非NULL判断.
								Date calendarDateNegative2 = DateUtils.calendarDate_Negative(projectPlan.getRepaymentDate(), NEGATIVE_NUMBER_2); // 还款日前两天.
								if (DateUtils.isSameDate(calendarDateNegative2, nowDateTime)) { // 比较两个时间是否为同一天.
									if (!StringUtils.isBlank(email)) {
										log.info(this.getClass() + "手机-" + creditUserInfo.getPhone() + "-邮件-" + email_message.toString());
										SendMailUtil.ztmgSendRepayRemindEmailMsg(email, CRO_EMAIL, messageSubject, email_message.toString());
									}
//									weixinSendTempMsgService.ztmgSendRepayRemindMsg(creditUserInfo.getPhone(), sms_message.toString());
								}
								Date calendarDateNegative5 = DateUtils.calendarDate_Negative(projectPlan.getRepaymentDate(), NEGATIVE_NUMBER_5); // 还款日前五天.
								if (DateUtils.isSameDate(calendarDateNegative5, nowDateTime)) { // 比较两个时间是否为同一天.
									if (!StringUtils.isBlank(email)) {
										log.info(this.getClass() + "手机-" + creditUserInfo.getPhone() + "-邮件-" + email_message.toString());
										SendMailUtil.ztmgSendRepayRemindEmailMsg(email, CRO_EMAIL, messageSubject, email_message.toString());
									}
//									weixinSendTempMsgService.ztmgSendRepayRemindMsg(creditUserInfo.getPhone(), sms_message.toString());
								}
								Date calendarDateNegative7 = DateUtils.calendarDate_Negative(projectPlan.getRepaymentDate(), NEGATIVE_NUMBER_7); // 还款日前七天.
								if (DateUtils.isSameDate(calendarDateNegative7, nowDateTime)) { // 比较两个时间是否为同一天.
									if (!StringUtils.isBlank(email)) {
										log.info(this.getClass() + "手机-" + creditUserInfo.getPhone() + "-邮件-" + email_message.toString());
										SendMailUtil.ztmgSendRepayRemindEmailMsg(email, CRO_EMAIL, messageSubject, email_message.toString());
									}
//									weixinSendTempMsgService.ztmgSendRepayRemindMsg(creditUserInfo.getPhone(), sms_message.toString());
								}
							} else {
								log.info(this.getClass() + "-借款企业帐号不存在-");
							}
						} else {
							log.info(this.getClass() + "-融资主体不存在-");
						}
					} else if (WloanTermProjectService.PROJECT_PRODUCT_TYPE_2.equals(project.getProjectProductType())) {
						List<WloanSubject> wloanSubjects = wloanSubjectDao.getByLoanApplyId(project.getReplaceRepayId());
						if (null != wloanSubjects && wloanSubjects.size() >= 1) {
							WloanSubject wloanSubject = wloanSubjects.get(0);
							if (null != wloanSubject) { // 融资主体.
								String email = wloanSubject.getEmail(); // 邮箱.
								CreditUserInfo creditUserInfo = creditUserInfoDao.get(project.getReplaceRepayId()); // 核心企业.
								if (null != creditUserInfo) { // 核心企业帐号非NULL判断.
									Date calendarDateNegative2 = DateUtils.calendarDate_Negative(projectPlan.getRepaymentDate(), NEGATIVE_NUMBER_2); // 还款日前两天.
									if (DateUtils.isSameDate(calendarDateNegative2, nowDateTime)) { // 比较两个时间是否为同一天.
										if (!StringUtils.isBlank(email)) {
											log.info(this.getClass() + "手机-" + creditUserInfo.getPhone() + "-邮件-" + email_message.toString());
											SendMailUtil.ztmgSendRepayRemindEmailMsg(email, CRO_EMAIL, messageSubject, email_message.toString());
										}
//										weixinSendTempMsgService.ztmgSendRepayRemindMsg(creditUserInfo.getPhone(), sms_message.toString());
									}
									Date calendarDateNegative5 = DateUtils.calendarDate_Negative(projectPlan.getRepaymentDate(), NEGATIVE_NUMBER_5); // 还款日前五天.
									if (DateUtils.isSameDate(calendarDateNegative5, nowDateTime)) { // 比较两个时间是否为同一天.
										if (!StringUtils.isBlank(email)) {
											log.info(this.getClass() + "手机-" + creditUserInfo.getPhone() + "-邮件-" + email_message.toString());
											SendMailUtil.ztmgSendRepayRemindEmailMsg(email, CRO_EMAIL, messageSubject, email_message.toString());
										}
//										weixinSendTempMsgService.ztmgSendRepayRemindMsg(creditUserInfo.getPhone(), sms_message.toString());
									}
									Date calendarDateNegative7 = DateUtils.calendarDate_Negative(projectPlan.getRepaymentDate(), NEGATIVE_NUMBER_7); // 还款日前七天.
									if (DateUtils.isSameDate(calendarDateNegative7, nowDateTime)) { // 比较两个时间是否为同一天.
										if (!StringUtils.isBlank(email)) {
											log.info(this.getClass() + "手机-" + creditUserInfo.getPhone() + "-邮件-" + email_message.toString());
											SendMailUtil.ztmgSendRepayRemindEmailMsg(email, CRO_EMAIL, messageSubject, email_message.toString());
										}
//										weixinSendTempMsgService.ztmgSendRepayRemindMsg(creditUserInfo.getPhone(), sms_message.toString());
									}
								} else {
									log.info(this.getClass() + "-核心企业帐号不存在-");
								}
							} else {
								log.info(this.getClass() + "-融资主体不存在-");
							}
						} else {
							log.info(this.getClass() + "-融资主体不存在-");
						}
					}
				} else {
					log.info(this.getClass() + "-项目不存在-");
				}
			}
		} catch (Exception e) {
			log.info(this.getClass() + "-Message-" + e.getMessage());
			e.printStackTrace();
		}
	}
}
