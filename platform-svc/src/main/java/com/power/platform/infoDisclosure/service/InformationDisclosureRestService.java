package com.power.platform.infoDisclosure.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.IdcardUtils;
import com.power.platform.common.utils.JedisUtils;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.plan.dao.WloanTermProjectPlanDao;
import com.power.platform.plan.dao.WloanTermUserPlanDao;
import com.power.platform.plan.entity.WloanTermUserPlan;
import com.power.platform.plan.service.WloanTermUserPlanService;
import com.power.platform.regular.dao.WloanSubjectDao;
import com.power.platform.regular.dao.WloanTermInvestDao;
import com.power.platform.regular.dao.WloanTermProjectDao;
import com.power.platform.regular.entity.WloanSubject;
import com.power.platform.regular.entity.WloanTermInvest;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.regular.service.WloanTermInvestService;
import com.power.platform.regular.service.WloanTermProjectService;
import com.power.platform.userinfo.dao.UserInfoDao;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.utils.CreateSupplyChainPdfContract;

@Path("/cicmorgan/information/disclosure")
@Service("informationDisclosureRestService")
@Produces(MediaType.APPLICATION_JSON)
public class InformationDisclosureRestService {

	private static final Logger log = LoggerFactory.getLogger(InformationDisclosureRestService.class);

	@Autowired
	private WloanTermProjectService wloanTermProjectService;
	@Resource
	private WloanTermProjectDao wloanTermProjectDao;
	@Resource
	private WloanSubjectDao wloanSubjectDao;
	@Resource
	private WloanTermProjectPlanDao wloanTermProjectPlanDao;
	@Resource
	private WloanTermInvestDao wloanTermInvestDao;
	@Resource
	private UserInfoDao userInfoDao;
	@Resource
	private WloanTermUserPlanDao wloanTermUserPlanDao;

	/**
	 * 1：出借状态成功，第一次改版之后.
	 */
	private static final String INVEST_STATE_1 = "1";
	/**
	 * 9：出借状态成功，第一次改版之前.
	 */
	private static final String INVEST_STATE_9 = "9";

	/**
	 * 30：项目期限30天.
	 */
	private static final Integer SPAN_30 = 30;
	/**
	 * 90：项目期限90天.
	 */
	private static final Integer SPAN_90 = 90;
	/**
	 * 180：项目期限180天.
	 */
	private static final Integer SPAN_180 = 180;
	/**
	 * 360：项目期限360天.
	 */
	private static final Integer SPAN_360 = 360;

	/**
	 * 
	 * methods: uvCounter <br>
	 * description: 2018年度战报用户访问量计数. <br>
	 * author: Mr.Roy <br>
	 * date: 2019年1月24日 下午2:35:18
	 * 
	 * @param from
	 * @param token
	 * @param request
	 * @return
	 */
	@POST
	@Path("/uvCounter")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> uvCounter(@FormParam("from") String from, @FormParam("token") String token, @Context HttpServletRequest request) {

		Map<String, Object> result = new HashMap<String, Object>();

		if (StringUtils.isBlank(from) || StringUtils.isBlank(token)) {
			log.info("fn:uvCounter，缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}

		try {
			String userId = JedisUtils.get(token);
			log.info("fn:uvCounter，token = " + token + "，userId = " + userId);
			if (StringUtils.isNotBlank(userId)) {
				UserInfo userInfo = userInfoDao.get(userId);
				if (userInfo != null) {
					Integer uvCounter = userInfo.getUvCounter();
					userInfo.setUvCounter(Integer.sum(uvCounter, 1));
					int updateFlag = userInfoDao.update(userInfo);
					if (updateFlag == 1) {
						log.info("fn:uvCounter，UV统计成功！");
					} else {
						log.info("fn:uvCounter，UV统计失败！");
					}
				} else {
					UserInfo cgbUserInfo = userInfoDao.getCgb(userId);
					if (cgbUserInfo != null) {
						Integer uvCounter = cgbUserInfo.getUvCounter();
						cgbUserInfo.setUvCounter(Integer.sum(uvCounter, 1));
						int updateFlag = userInfoDao.update(cgbUserInfo);
						if (updateFlag == 1) {
							log.info("fn:uvCounter，UV统计成功！");
						} else {
							log.info("fn:uvCounter，UV统计失败！");
						}
					}
				}
			} else {
				log.info("fn:uvCounter，系统超时！");
				result.put("state", "4");
				result.put("message", "系统超时！");
				return result;
			}

			log.info("fn:uvCounter，UV统计成功！");
			result.put("state", "0");
			result.put("message", "接口调用成功！");
		} catch (Exception e) {
			e.printStackTrace();
			log.info("fn:uvCounter，系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			return result;
		}

		return result;
	}

	/**
	 * 
	 * methods: personalAnnualReport_2018 <br>
	 * description: 2018年度，个人报告. <br>
	 * author: Mr.Roy <br>
	 * date: 2019年1月23日 下午3:06:44
	 * 
	 * @param from
	 * @param token
	 * @param request
	 * @return
	 */
	@POST
	@Path("/personalAnnualReport_2018")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> personalAnnualReport_2018(@FormParam("from") String from, @FormParam("token") String token, @Context HttpServletRequest request) {

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		if (StringUtils.isBlank(from) || StringUtils.isBlank(token)) {
			log.info("fn:personalAnnualReport_2018，缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}

		log.info("fn:personalAnnualReport_2018，from = " + from + "，token = " + token);

		try {
			String userId = JedisUtils.get(token);
			if (StringUtils.isNotBlank(userId)) {
				// 中投摩根已陪伴您走过了多少天.
				UserInfo userInfo = userInfoDao.get(userId);
				if (userInfo != null) {
					long t = DateUtils.parseDate("2019-01-28 23:59:59").getTime() - userInfo.getRegisterDate().getTime();
					data.put("myRegisterDays", t / (24 * 60 * 60 * 1000));
				} else {
					UserInfo cgbUserInfo = userInfoDao.getCgb(userId);
					if (cgbUserInfo != null) {
						long t = DateUtils.parseDate("2019-01-28 23:59:59").getTime() - cgbUserInfo.getRegisterDate().getTime();
						data.put("myRegisterDays", t / (24 * 60 * 60 * 1000));
					}
				}
				// 用户累计出借笔数.
				WloanTermInvest invest = new WloanTermInvest();
				List<String> stateItem = new ArrayList<String>();
				stateItem.add(WloanTermInvestService.WLOAN_TERM_INVEST_STATE_1);
				stateItem.add(WloanTermInvestService.WLOAN_TERM_INVEST_STATE_9);
				invest.setStateItem(stateItem);
				invest.setBeginBeginDate(DateUtils.parseDate("2018-01-01 00:00:00"));
				invest.setEndBeginDate(DateUtils.parseDate("2019-01-28 23:59:59"));
				invest.setUserId(userId);
				WloanTermProject wloanTermProject = new WloanTermProject();
				List<String> projectStateItem = new ArrayList<String>();
				projectStateItem.add(WloanTermProjectService.ONLINE);
				projectStateItem.add(WloanTermProjectService.FULL);
				projectStateItem.add(WloanTermProjectService.REPAYMENT);
				projectStateItem.add(WloanTermProjectService.FINISH);
				wloanTermProject.setStateItem(projectStateItem);
				invest.setWloanTermProject(wloanTermProject);
				data.put("countNum", wloanTermInvestDao.findCountNumByInvest(invest));
				// 用户累计出借金额.
				data.put("sumAmount", NumberUtils.scaleDoubleStr(wloanTermInvestDao.findSumAmountByInvest(invest) == null ? 0D : wloanTermInvestDao.findSumAmountByInvest(invest)));
				// 1）安心投.
				wloanTermProject.setProjectProductType(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1);
				invest.setWloanTermProject(wloanTermProject);
				// 安心投累计出借笔数.
				data.put("countANum", wloanTermInvestDao.findCountNumByInvest(invest));
				// 安心投累计出借金额.
				data.put("sumAAmount", NumberUtils.scaleDoubleStr(wloanTermInvestDao.findSumAmountByInvest(invest) == null ? 0D : wloanTermInvestDao.findSumAmountByInvest(invest)));
				// 2）供应链.
				wloanTermProject.setProjectProductType(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2);
				invest.setWloanTermProject(wloanTermProject);
				// 供应链累计出借笔数.
				data.put("countGNum", wloanTermInvestDao.findCountNumByInvest(invest));
				// 供应链累计出借金额.
				data.put("sumGAmount", NumberUtils.scaleDoubleStr(wloanTermInvestDao.findSumAmountByInvest(invest) == null ? 0D : wloanTermInvestDao.findSumAmountByInvest(invest)));
				// 用户累计赚取(n-1)期利息.
				WloanTermUserPlan wloanTermUserPlan = new WloanTermUserPlan();
				wloanTermUserPlan.setUserId(userId);
				wloanTermUserPlan.setState(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_3);
				wloanTermUserPlan.setPrincipal(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_PRINCIPAL_2);
				wloanTermUserPlan.setBeginDate(DateUtils.parseDate("2018-01-01 00:00:00"));
				wloanTermUserPlan.setEndDate(DateUtils.parseDate("2019-01-28 23:59:59"));
				double sumBeforeInterest = NumberUtils.scaleDouble(wloanTermUserPlanDao.findSumInterestByPlan(wloanTermUserPlan) == null ? 0D : wloanTermUserPlanDao.findSumInterestByPlan(wloanTermUserPlan));
				data.put("sumBeforeInterest", NumberUtils.scaleDoubleStr(sumBeforeInterest));
				// 用户累计赚取本息.
				wloanTermUserPlan.setPrincipal(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_PRINCIPAL_1);
				double sumPrincipalAndInterest = NumberUtils.scaleDouble(wloanTermUserPlanDao.findSumInterestByPlan(wloanTermUserPlan) == null ? 0D : wloanTermUserPlanDao.findSumInterestByPlan(wloanTermUserPlan));
				data.put("sumPrincipalAndInterest", NumberUtils.scaleDoubleStr(sumPrincipalAndInterest));
				// 用户累计本金.
				double sumPrincipal = NumberUtils.scaleDouble(wloanTermUserPlanDao.findSumPrincipalByPlan(wloanTermUserPlan) == null ? 0D : wloanTermUserPlanDao.findSumPrincipalByPlan(wloanTermUserPlan));
				data.put("sumPrincipal", NumberUtils.scaleDoubleStr(sumPrincipal));
				// 用户累计赚取收益.
				double sumAInterest = NumberUtils.subtract(sumPrincipalAndInterest, sumPrincipal); // 最后一(n)期利息.
				double sumInterest = NumberUtils.add(sumAInterest, sumBeforeInterest); // 为用户赚取的总收益.
				data.put("sumInterest", NumberUtils.scaleDoubleStr(sumInterest));
			} else {
				log.info("fn:personalAnnualReport_2018，系统超时！");
				result.put("state", "4");
				result.put("message", "系统超时！");
				result.put("data", null);
				return result;
			}

			log.info("fn:personalAnnualReport_2018，接口调用成功！");
			result.put("state", "0");
			result.put("message", "接口调用成功！");
			result.put("data", data);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("fn:personalAnnualReport_2018，系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

		return result;
	}

	/**
	 * 
	 * methods: platformAnnualReport_2018 <br>
	 * description: 2018年度，平台报告. <br>
	 * author: Mr.Roy <br>
	 * date: 2019年1月23日 上午10:12:26
	 * 
	 * @param from
	 * @param request
	 * @return
	 */
	@POST
	@Path("/platformAnnualReport_2018")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> platformAnnualReport_2018(@FormParam("from") String from, @Context HttpServletRequest request) {

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		if (StringUtils.isBlank(from)) {
			log.info("fn:platformAnnualReport_2018，缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}

		log.info("fn:platformAnnualReport_2018，from = " + from);

		try {
			// 平台安全运营天数.
			String onlineDateStr = "2015-03-20 00:00:00";
			Date date = DateUtils.parseDate(onlineDateStr);
			long t = DateUtils.parseDate("2019-01-28 23:59:59").getTime() - date.getTime();
			data.put("onlineDays", t / (24 * 60 * 60 * 1000));
			WloanTermInvest invest = new WloanTermInvest();
			List<String> stateItem = new ArrayList<String>();
			stateItem.add(WloanTermInvestService.WLOAN_TERM_INVEST_STATE_1);
			stateItem.add(WloanTermInvestService.WLOAN_TERM_INVEST_STATE_3);
			stateItem.add(WloanTermInvestService.WLOAN_TERM_INVEST_STATE_9);
			invest.setStateItem(stateItem);
			invest.setEndBeginDate(DateUtils.parseDate("2019-01-28 23:59:59"));
			// 为用户累计赚取收益.
			double totalInterest = wloanTermInvestDao.findTotalInterestByPlatform(invest);
			data.put("totalInterest", CreateSupplyChainPdfContract.fmtMicrometer(NumberUtils.scaleDoubleStr(totalInterest)));
			log.info("fn:platformAnnualReport_2018，接口调用成功！");
			result.put("state", "0");
			result.put("message", "接口调用成功！");
			result.put("data", data);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("fn:platformAnnualReport_2018，系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

		return result;
	}

	/**
	 * 
	 * 方法: investPeopleDataOverview <br>
	 * 描述: 出借人概况. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年11月13日 下午4:09:11
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@POST
	@Path("/investPeopleDataOverview")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> investPeopleDataOverview(@FormParam("startTime") String startTime, @FormParam("endTime") String endTime) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 数据域.
		Map<String, Object> data = new HashMap<String, Object>();

		// 男性数量.
		Double male_num = 0D;
		// 女性数量.
		Double female_num = 0D;
		// 25岁.
		Double age_25 = 0D;
		// 26-30岁.
		Double age_26_30 = 0D;
		// 31-35岁.
		Double age_31_35 = 0D;
		// 36-40岁.
		Double age_36_40 = 0D;
		// 41-45岁.
		Double age_41_45 = 0D;
		// 46-50岁.
		Double age_46_50 = 0D;
		// 51岁.
		Double age_51 = 0D;
		// 在贷本金余额.
		Double loanAllUserTotalPrincipalAmount = 0D;
		// 前十大借款人在贷本金余额.
		Double loanUserStayStillTotalAmount = 0D;
		// 所有借款人待还总金额.
		Double loanAllUserStayStillTotalAmount = 0D;
		try {
			// 在贷本金余额列表.
			List<WloanSubject> loanUserTotalPrincipalAmountList = wloanSubjectDao.findLoanUserTotalPrincipalAmountList();
			for (WloanSubject wloanSubject : loanUserTotalPrincipalAmountList) {
				if (wloanSubject.getWloanTermProject() != null) {
					wloanSubject.getWloanTermProject().getCurrentAmount();
					if (wloanSubject.getWloanTermProject().getCurrentAmount() != null) {
						loanAllUserTotalPrincipalAmount = loanAllUserTotalPrincipalAmount + wloanSubject.getWloanTermProject().getCurrentAmount();
					}
				}
			}
			// 前十大借款人在贷本金余额.
			List<WloanSubject> sortLoanUserTotalAmountList = new ArrayList<WloanSubject>();
			if (loanUserTotalPrincipalAmountList.size() > 10) {
				for (int i = 0; i < 10; i++) {
					sortLoanUserTotalAmountList.add(loanUserTotalPrincipalAmountList.get(i));
				}
			} else {
				for (int i = 0; i < loanUserTotalPrincipalAmountList.size(); i++) {
					sortLoanUserTotalAmountList.add(loanUserTotalPrincipalAmountList.get(i));
				}
			}
			// 前十大借款人待还(本金)总金额.
			for (WloanSubject wloanSubject : sortLoanUserTotalAmountList) {
				if (wloanSubject.getWloanTermProject() != null) {
					if (wloanSubject.getWloanTermProject().getCurrentAmount() != null) {
						loanUserStayStillTotalAmount = NumberUtils.scaleDouble(loanUserStayStillTotalAmount) + NumberUtils.scaleDouble(wloanSubject.getWloanTermProject().getCurrentAmount());
					}
				}
			}
			// 前十大借款人待还总额为零时，过滤查询所有借款人的待还总额.
			if (loanUserStayStillTotalAmount.equals(0D)) {
				// 前十大借款人待还金额占比（%）.
				data.put("theTopTenStayStillTotalAmountPercentage", "0.00");
				// 其它占比（%）.
				data.put("otherTheTopTenStayStillTotalAmountPercentage", "100.00");
			} else {
				// 所有借款人待还总金额.
				for (WloanSubject wloanSubject : sortLoanUserTotalAmountList) {
					// 借款人待还总额.
					Double stayStillTotalAmount = wloanSubjectDao.findLoanUserStayStillTotalAmount(wloanSubject.getId());
					if (stayStillTotalAmount != null) {
						loanAllUserStayStillTotalAmount = NumberUtils.scaleDouble(loanAllUserStayStillTotalAmount) + NumberUtils.scaleDouble(stayStillTotalAmount);
					}
				}
				if (loanAllUserTotalPrincipalAmount.equals(0D)) {
					// 前十大借款人待还金额占比（%）.
					data.put("theTopTenStayStillTotalAmountPercentage", "0.00");
					// 其它占比（%）.
					data.put("otherTheTopTenStayStillTotalAmountPercentage", "100.00");
				} else {
					// 前十大借款人待还金额占比（%）.
					data.put("theTopTenStayStillTotalAmountPercentage", NumberUtils.scaleDoubleStr(loanUserStayStillTotalAmount / loanAllUserTotalPrincipalAmount * 100));
					// 其它占比（%）.
					data.put("otherTheTopTenStayStillTotalAmountPercentage", NumberUtils.scaleDoubleStr(100 - (loanUserStayStillTotalAmount / loanAllUserTotalPrincipalAmount * 100)));
				}
			}
			// 最大单一借款人待还金额占比（%）：指在平台撮合的项目中，借款最多一户借款人的借款余额占总借款余额的比例。
			if (sortLoanUserTotalAmountList.size() > 0) {
				WloanSubject wloanSubject = sortLoanUserTotalAmountList.get(0); // 最大单一借款人.
				if (wloanSubject != null) {
					if (wloanSubject.getWloanTermProject() != null) {
						if (wloanSubject.getWloanTermProject().getCurrentAmount() != null) {
							if (wloanSubject.getWloanTermProject().getCurrentAmount().equals(0D)) { // 最大单一借款人待还金额为零.
								// 最大单一借款人待还金额占比（%）.
								data.put("theBiggestStayStillTotalAmountPercentage", "0.00");
								// 其它占比（%）.
								data.put("otherTheBiggestStayStillTotalAmountPercentage", "100.00");
							} else {
								if (loanAllUserTotalPrincipalAmount.equals(0D)) {
									// 最大单一借款人待还金额占比（%）.
									data.put("theBiggestStayStillTotalAmountPercentage", "0.00");
									// 其它占比（%）.
									data.put("otherTheBiggestStayStillTotalAmountPercentage", "100.00");
								} else {
									// 最大单一借款人待还金额占比（%）.
									data.put("theBiggestStayStillTotalAmountPercentage", NumberUtils.scaleDoubleStr(wloanSubject.getWloanTermProject().getCurrentAmount() / loanAllUserTotalPrincipalAmount * 100));
									// 其它占比（%）.
									data.put("otherTheBiggestStayStillTotalAmountPercentage", NumberUtils.scaleDoubleStr(100 - (wloanSubject.getWloanTermProject().getCurrentAmount() / loanAllUserTotalPrincipalAmount * 100)));
								}
							}
						} else {
							// 最大单一借款人待还金额占比（%）.
							data.put("theBiggestStayStillTotalAmountPercentage", "0.00");
							// 其它占比（%）.
							data.put("otherTheBiggestStayStillTotalAmountPercentage", "100.00");
						}
					}
				}
			} else {
				// 最大单一借款人待还金额占比（%）.
				data.put("theBiggestStayStillTotalAmountPercentage", "0.00");
				// 其它占比（%）.
				data.put("otherTheBiggestStayStillTotalAmountPercentage", "100.00");
			}
			// 累计出借人数量.
			List<WloanTermInvest> loanUserInfoTotalNumbers = wloanTermInvestDao.findLoanUserInfoTotalNumbers();
			for (WloanTermInvest wloanTermInvest : loanUserInfoTotalNumbers) {
				String certificateNo = wloanTermInvest.getUserInfo().getCertificateNo(); // 身份证号码.
				if (certificateNo == null) {
				} else if (certificateNo.equals("")) {
				} else {
					// 性别.
					String gender = IdcardUtils.getGenderByIdCard(certificateNo); // 性别(M-男，F-女，N-未知).
					if (gender.equals("M")) {
						male_num = male_num + 1;
					}
					if (gender.equals("F")) {
						female_num = female_num + 1;
					}
					// 年龄.
					int age = IdcardUtils.getAgeByIdCard(certificateNo); // 年龄.
					if (age <= 25) {
						age_25 = age_25 + 1;
					} else if (age >= 26 && age <= 30) {
						age_26_30 = age_26_30 + 1;
					} else if (age >= 31 && age <= 35) {
						age_31_35 = age_31_35 + 1;
					} else if (age >= 36 && age <= 40) {
						age_36_40 = age_36_40 + 1;
					} else if (age >= 41 && age <= 45) {
						age_41_45 = age_41_45 + 1;
					} else if (age >= 46 && age <= 50) {
						age_46_50 = age_46_50 + 1;
					} else {
						age_51 = age_51 + 1;
					}
				}
			}
			// 男性数量.
			data.put("male_num", male_num);
			// 男性数量占比.
			Double male_num_percentage = NumberUtils.scaleDouble(male_num / loanUserInfoTotalNumbers.size() * 100);
			data.put("male_num_percentage", NumberUtils.scaleDoubleStr(male_num_percentage));
			// 女性数量.
			data.put("female_num", female_num);
			Double female_num_percentage = NumberUtils.scaleDouble(100D) - male_num_percentage;
			// 女性数量占比.
			data.put("female_num_percentage", NumberUtils.scaleDoubleStr(female_num_percentage));

			// 25岁占比.
			Double age_25_percentage = NumberUtils.scaleDouble(age_25 / loanUserInfoTotalNumbers.size() * 100);
			data.put("age_25_percentage", NumberUtils.scaleDoubleStr(age_25_percentage));
			// 26-30岁占比.
			Double age_26_30_percentage = NumberUtils.scaleDouble(age_26_30 / loanUserInfoTotalNumbers.size() * 100);
			data.put("age_26_30_percentage", NumberUtils.scaleDoubleStr(age_26_30 / loanUserInfoTotalNumbers.size() * 100));
			// 31-35岁占比.
			Double age_31_35_percentage = NumberUtils.scaleDouble(age_31_35 / loanUserInfoTotalNumbers.size() * 100);
			data.put("age_31_35_percentage", NumberUtils.scaleDoubleStr(age_31_35_percentage));
			// 36-40岁占比.
			Double age_36_40_percentage = NumberUtils.scaleDouble(age_36_40 / loanUserInfoTotalNumbers.size() * 100);
			data.put("age_36_40_percentage", NumberUtils.scaleDoubleStr(age_36_40_percentage));
			// 41-45岁占比.
			Double age_41_45_percentage = NumberUtils.scaleDouble(age_41_45 / loanUserInfoTotalNumbers.size() * 100);
			data.put("age_41_45_percentage", NumberUtils.scaleDoubleStr(age_41_45_percentage));
			// 46-50岁占比.
			Double age_46_50_percentage = NumberUtils.scaleDouble(age_46_50 / loanUserInfoTotalNumbers.size() * 100);
			data.put("age_46_50_percentage", NumberUtils.scaleDoubleStr(age_46_50_percentage));
			Double age_51_percentage = NumberUtils.scaleDouble(100D) - NumberUtils.scaleDouble(age_25_percentage + age_26_30_percentage + age_31_35_percentage + age_36_40_percentage + age_41_45_percentage + age_46_50_percentage);
			data.put("age_51_percentage", NumberUtils.scaleDoubleStr(age_51_percentage));
		} catch (Exception e) {
			e.printStackTrace();
		}

		log.info(this.getClass() + "出借人概况，接口请求成功");
		result.put("state", "0");
		result.put("message", "出借人概况，接口请求成功");
		result.put("data", data);
		return result;
	}

	/**
	 * 
	 * 方法: platformDataOverview <br>
	 * 描述: 平台数据概况. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年11月13日 下午1:18:44
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@POST
	@Path("/platformDataOverview")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> platformDataOverview(@FormParam("startTime") String startTime, @FormParam("endTime") String endTime) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 数据域.
		Map<String, Object> data = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(startTime) || StringUtils.isBlank(endTime)) {
			log.info(this.getClass() + "缺少必要参数");
			result.put("state", "2");
			result.put("message", "缺少必要参数.");
			result.put("data", null);
			return result;
		}

		startTime = startTime.concat(" 00:00:00");
		endTime = endTime.concat(" 23:59:59");

		// 借贷本金余额.
		Double loanTotalAvailableAmount = 0D;
		try {
			// 累计借贷金额（万元）出借人出借总额
			// Double investTotalAmount_ =
			// wloanTermInvestDao.findInvestTotalAmount();
			Double investTotalAmount = wloanTermInvestDao.findInvestTotalAmountByMonth(endTime);
			if (null == investTotalAmount) {
				investTotalAmount = 0D;
			}
			// BigDecimal investTotalAmountBd = new BigDecimal(investTotalAmount
			// / 10000);
			BigDecimal investTotalAmountBd = new BigDecimal(NumberUtils.scaleDouble(investTotalAmount));
			data.put("investTotalAmount", CreateSupplyChainPdfContract.fmtMicrometer(CreateSupplyChainPdfContract.formatToString(investTotalAmountBd)));
			/**
			 * 借贷总额.
			 */
			WloanTermProject project = new WloanTermProject();
			// 上线开始时间.
			project.setBeginTimeFromOnline("2015-03-20 00:00:00");
			// 上线结束时间.
			project.setEndTimeToOnline(endTime);
			// project.setEndTimeToOnline("2018-10-31 23:59:59");
			// 项目状态，4：上线，5：满标，6：还款中，7：已完成.
			List<String> stateItem = new ArrayList<String>();
			stateItem.add(WloanTermProjectService.ONLINE);
			stateItem.add(WloanTermProjectService.FULL);
			stateItem.add(WloanTermProjectService.REPAYMENT);
			stateItem.add(WloanTermProjectService.FINISH);
			project.setStateItem(stateItem);
			List<WloanTermProject> projects = wloanTermProjectDao.findList(project);
			/**
			 * 借贷 本金余额.
			 */
			WloanTermProject projectA = new WloanTermProject();
			// 上线开始时间.
			projectA.setBeginTimeFromOnline("2015-03-20 00:00:00");
			// 上线结束时间.
			projectA.setEndTimeToOnline(endTime);
			// 项目状态，4：上线，5：满标，6：还款中，7：已完成.
			List<String> stateItemA = new ArrayList<String>();
			stateItemA.add(WloanTermProjectService.ONLINE);
			stateItemA.add(WloanTermProjectService.FULL);
			stateItemA.add(WloanTermProjectService.REPAYMENT);
			projectA.setStateItem(stateItemA);
			List<WloanTermProject> projectAs = wloanTermProjectDao.findList(projectA);
			for (WloanTermProject wloanTermProject : projectAs) {
				loanTotalAvailableAmount = loanTotalAvailableAmount + wloanTermProject.getCurrentAmount();
			}
			// 借贷本金余额.
			// BigDecimal loanTotalAvailableAmountBd = new
			// BigDecimal(loanTotalAvailableAmount / 10000);
			BigDecimal loanTotalAvailableAmountBd = new BigDecimal(NumberUtils.scaleDouble(loanTotalAvailableAmount));
			data.put("loanTotalAvailableAmount", CreateSupplyChainPdfContract.fmtMicrometer(CreateSupplyChainPdfContract.formatToString(loanTotalAvailableAmountBd)));
			// 借贷本息余额.
			// Double loanUserTotalPrincipalAndInterestAmount =
			// wloanSubjectDao.findLoanUserTotalPrincipalAndInterestAmount();
			Double loanUserTotalPrincipalAndInterestAmount = wloanSubjectDao.findLoanUserTotalPrincipalAndInterestAmountByMonth(endTime);
			// 利息余额.
			if (loanUserTotalPrincipalAndInterestAmount == null) {
				loanUserTotalPrincipalAndInterestAmount = 0D;
			}
			Double loanUserTotalInterestAmount = NumberUtils.scaleDouble(loanUserTotalPrincipalAndInterestAmount - loanTotalAvailableAmount);
			// BigDecimal loanUserTotalInterestAmountBd = new
			// BigDecimal(loanUserTotalInterestAmount / 10000);
			BigDecimal loanUserTotalInterestAmountBd = new BigDecimal(NumberUtils.scaleDouble(loanUserTotalInterestAmount));
			data.put("loanUserTotalInterestAmount", CreateSupplyChainPdfContract.fmtMicrometer(CreateSupplyChainPdfContract.formatToString(loanUserTotalInterestAmountBd)));
			// 借款累计笔数.
			data.put("loanTotalNumbers", String.valueOf(projects.size()));
			// 累计算为用户赚取（元）.
			// Double interestTotalAmount =
			// wloanTermInvestDao.findInterestTotalAmount();
			Double interestTotalAmount = wloanTermInvestDao.findInterestTotalAmountByMonth(endTime);
			data.put("interestTotalAmount", CreateSupplyChainPdfContract.fmtMicrometer(NumberUtils.scaleDoubleStr(interestTotalAmount)));
			// 累计出借人数量（人）.
			// List<WloanTermInvest> loanUserInfoTotalNumbers =
			// wloanTermInvestDao.findLoanUserInfoTotalNumbers();
			List<WloanTermInvest> loanUserInfoTotalNumbers = wloanTermInvestDao.findLoanUserInfoTotalNumbersByMonth(endTime);
			data.put("loanUserInfoTotalNumbers", loanUserInfoTotalNumbers.size());
			// 人均累计出借总额.
			BigDecimal investPerCapitaTotalAmountBd = new BigDecimal(investTotalAmount / loanUserInfoTotalNumbers.size());
			data.put("investPerCapitaTotalAmount", CreateSupplyChainPdfContract.fmtMicrometer(CreateSupplyChainPdfContract.formatToString(investPerCapitaTotalAmountBd)));
			// 累计借款人数量（融资主体为借款人）.
			// List<String> findLoanUserTotalNumbers =
			// wloanSubjectDao.findLoanUserTotalNumbers();
			List<String> findLoanUserTotalNumbersByMonth = wloanSubjectDao.findLoanUserTotalNumbersByMonth(endTime);
			if (findLoanUserTotalNumbersByMonth.isEmpty() == true && findLoanUserTotalNumbersByMonth.size() == 0) {
				data.put("loanPerCapitaTotalAmount", 0);
			} else {
				data.put("loanUserTotalNumbers", findLoanUserTotalNumbersByMonth.size());
				// 人均借款金额.
				BigDecimal loanPerCapitaTotalAmountBd = new BigDecimal(investTotalAmount / findLoanUserTotalNumbersByMonth.size());
				data.put("loanPerCapitaTotalAmount", CreateSupplyChainPdfContract.fmtMicrometer(CreateSupplyChainPdfContract.formatToString(loanPerCapitaTotalAmountBd)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		log.info(this.getClass() + "平台数据概况，接口请求成功");
		result.put("state", "0");
		result.put("message", "平台数据概况，接口请求成功");
		result.put("data", data);
		return result;
	}

	/**
	 * 
	 * 方法: thisMonthDataInfo <br>
	 * 描述: 本月数据概况. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年11月1日 上午9:32:02
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@POST
	@Path("/thisMonthDataInfo")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> thisMonthDataInfo(@FormParam("startTime") String startTime, @FormParam("endTime") String endTime) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 数据域.
		Map<String, Object> data = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(startTime) || StringUtils.isBlank(endTime)) {
			log.info(this.getClass() + "缺少必要参数");
			result.put("state", "2");
			result.put("message", "缺少必要参数.");
			result.put("data", null);
			return result;
		}

		startTime = startTime.concat(" 00:00:00");
		endTime = endTime.concat(" 23:59:59");

		// 成交金额（元）.
		Double loanInvestTotalAmount = 0D;
		// 30天项目，成交金额（元）.
		Double loanInvestTotalAmountSpan30 = 0D;
		// 90天项目，成交金额（元）.
		Double loanInvestTotalAmountSpan90 = 0D;
		// 180天项目，成交金额（元）.
		Double loanInvestTotalAmountSpan180 = 0D;
		// 360天项目，成交金额（元）.
		Double loanInvestTotalAmountSpan360 = 0D;
		// 出借笔数.
		int investTotalCount = 0;
		// 30天出借笔数.
		int investTotalCount30 = 0;
		// 90天出借笔数.
		int investTotalCount90 = 0;
		// 180天出借笔数.
		int investTotalCount180 = 0;
		// 360天出借笔数.
		int investTotalCount360 = 0;

		try {
			WloanTermInvest loanTermInvest = new WloanTermInvest();
			List<String> stateItem = new ArrayList<String>();
			stateItem.add(INVEST_STATE_1);
			stateItem.add(INVEST_STATE_9);
			loanTermInvest.setStateItem(stateItem); // 出借成功状态标识.
			loanTermInvest.setBeginBeginDate(DateUtils.parseDate(startTime));
			loanTermInvest.setEndBeginDate(DateUtils.parseDate(endTime));
			List<WloanTermInvest> investList = wloanTermInvestDao.findList(loanTermInvest);
			List<String> userIdStrings = new ArrayList<String>();
			for (WloanTermInvest invest : investList) {
				userIdStrings.add(invest.getUserId());
				loanInvestTotalAmount = loanInvestTotalAmount + invest.getAmount();
				investTotalCount = investTotalCount + 1;
				if (null != invest.getWloanTermProject()) {
					if (SPAN_30.equals(invest.getWloanTermProject().getSpan())) {
						loanInvestTotalAmountSpan30 = loanInvestTotalAmountSpan30 + invest.getAmount();
						investTotalCount30 = investTotalCount30 + 1;
					} else if (SPAN_90.equals(invest.getWloanTermProject().getSpan())) {
						loanInvestTotalAmountSpan90 = loanInvestTotalAmountSpan90 + invest.getAmount();
						investTotalCount90 = investTotalCount90 + 1;
					} else if (SPAN_180.equals(invest.getWloanTermProject().getSpan())) {
						loanInvestTotalAmountSpan180 = loanInvestTotalAmountSpan180 + invest.getAmount();
						investTotalCount180 = investTotalCount180 + 1;
					} else if (SPAN_360.equals(invest.getWloanTermProject().getSpan())) {
						loanInvestTotalAmountSpan360 = loanInvestTotalAmountSpan360 + invest.getAmount();
						investTotalCount360 = investTotalCount360 + 1;
					}
				}
			}

			// 成交额（元）.
			data.put("loanInvestTotalAmount", NumberUtils.scaleDoubleStr(loanInvestTotalAmount));
			// 30天项目，成交金额（元）.
			data.put("loanInvestTotalAmountSpan30", NumberUtils.scaleDoubleStr(loanInvestTotalAmountSpan30));
			// 90天项目，成交金额（元）.
			data.put("loanInvestTotalAmountSpan90", NumberUtils.scaleDoubleStr(loanInvestTotalAmountSpan90));
			// 180天项目，成交金额（元）.
			data.put("loanInvestTotalAmountSpan180", NumberUtils.scaleDoubleStr(loanInvestTotalAmountSpan180));
			// 360天项目，成交金额（元）.
			data.put("loanInvestTotalAmountSpan360", NumberUtils.scaleDoubleStr(loanInvestTotalAmountSpan360));
			// 30天成交额百分比.
			Double turnoverPercentageSpan30 = 0D;
			// 90天成交额百分比.
			Double turnoverPercentageSpan90 = 0D;
			// 180天成交额百分比.
			Double turnoverPercentageSpan180 = 0D;
			// 360天成交额百分比.
			Double turnoverPercentageSpan360 = 0D;
			if (NumberUtils.scaleDouble(loanInvestTotalAmount) > 0D) {
				// 30天成交额百分比.
				turnoverPercentageSpan30 = NumberUtils.scaleDouble(NumberUtils.scaleDouble(loanInvestTotalAmountSpan30) / NumberUtils.scaleDouble(loanInvestTotalAmount) * 100);
				// 90天成交额百分比.
				turnoverPercentageSpan90 = NumberUtils.scaleDouble(NumberUtils.scaleDouble(loanInvestTotalAmountSpan90) / NumberUtils.scaleDouble(loanInvestTotalAmount) * 100);
				// 180天成交额百分比.
				turnoverPercentageSpan180 = NumberUtils.scaleDouble(NumberUtils.scaleDouble(loanInvestTotalAmountSpan180) / NumberUtils.scaleDouble(loanInvestTotalAmount) * 100);
				// 360天成交额百分比.
				turnoverPercentageSpan360 = NumberUtils.scaleDouble(100D - turnoverPercentageSpan30 - turnoverPercentageSpan90 - turnoverPercentageSpan180);
			}
			// 30天成交额占比.
			data.put("turnoverPercentageSpan30", turnoverPercentageSpan30);
			// 90天成交额占比.
			data.put("turnoverPercentageSpan90", turnoverPercentageSpan90);
			// 180天成交额占比.
			data.put("turnoverPercentageSpan180", turnoverPercentageSpan180);
			// 360天成交额占比.
			data.put("turnoverPercentageSpan360", turnoverPercentageSpan360);

			// 出借人数.
			data.put("investPeopleTotalCount", pastLeep(userIdStrings));
			// 出借笔数.
			data.put("investTotalCount", investTotalCount);
			// 30天出借笔数.
			data.put("investTotalCount30", investTotalCount30);
			// 90天出借笔数.
			data.put("investTotalCount90", investTotalCount90);
			// 180天出借笔数.
			data.put("investTotalCount180", investTotalCount180);
			// 360天出借笔数.
			data.put("investTotalCount360", investTotalCount360);
			// 30天出借笔数百分比.
			Double investNumberPercentageSpan30 = 0D;
			// 90天出借笔数百分比.
			Double investNumberPercentageSpan90 = 0D;
			// 180天出借笔数百分比.
			Double investNumberPercentageSpan180 = 0D;
			// 360天出借笔数百分比.
			Double investNumberPercentageSpan360 = 0D;
			if (investTotalCount > 0) {
				// 30天出借笔数百分比.
				investNumberPercentageSpan30 = NumberUtils.scaleDouble(NumberUtils.scaleDouble((double) investTotalCount30) / NumberUtils.scaleDouble((double) investTotalCount) * 100);
				// 90天出借笔数百分比.
				investNumberPercentageSpan90 = NumberUtils.scaleDouble(NumberUtils.scaleDouble((double) investTotalCount90) / NumberUtils.scaleDouble((double) investTotalCount) * 100);
				// 180天出借笔数百分比.
				investNumberPercentageSpan180 = NumberUtils.scaleDouble(NumberUtils.scaleDouble((double) investTotalCount180) / NumberUtils.scaleDouble((double) investTotalCount) * 100);
				// 360天出借笔数百分比.
				investNumberPercentageSpan360 = NumberUtils.scaleDouble(100D - investNumberPercentageSpan30 - investNumberPercentageSpan90 - investNumberPercentageSpan180);
			}
			// 30天出借笔数百分比.
			data.put("investNumberPercentageSpan30", investNumberPercentageSpan30);
			// 90天出借笔数百分比.
			data.put("investNumberPercentageSpan90", investNumberPercentageSpan90);
			// 180天出借笔数百分比.
			data.put("investNumberPercentageSpan180", investNumberPercentageSpan180);
			// 360天出借笔数百分比.
			data.put("investNumberPercentageSpan360", investNumberPercentageSpan360);

			log.info(this.getClass() + "本月数据概况，接口请求成功");
			result.put("state", "0");
			result.put("message", "本月数据概况，接口请求成功");
			result.put("data", data);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 
	 * 方法: queryOnlineDateByDays <br>
	 * 描述: 自中投摩根成立至今，过去的天数. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2018年4月4日 下午8:08:20
	 * 
	 * @return
	 */
	@POST
	@Path("/queryOnlineDateByDays")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> queryOnlineDateByDays() {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 数据域.
		Map<String, Object> data = new HashMap<String, Object>();

		// 成立时间.
		String onlineDateStr = "2015-03-20 00:00:00";

		// 过去的天数.
		long pastDays = DateUtils.pastDays(DateUtils.parseDate(onlineDateStr));
		// 字符串过去的天数.
		String pastDaysStr = String.valueOf(pastDays);

		// 过去的天数.
		data.put("pastDays", pastDays);
		data.put("pastDay_a", pastDaysStr.substring(0, 1));
		data.put("pastDay_b", pastDaysStr.substring(1, 2));
		data.put("pastDay_c", pastDaysStr.substring(2, 3));
		data.put("pastDay_d", pastDaysStr.substring(3, 4));

		log.info(this.getClass() + "获取过去的天数，数据请求成功");
		result.put("state", "0");
		result.put("message", "接口请求成功");
		result.put("data", data);

		return result;
	}

	/**
	 * 
	 * 方法: queryLoanUserInfo <br>
	 * 描述: 出借人情况. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2018年4月4日 下午5:38:49
	 * 
	 * @return
	 */
	@POST
	@Path("/queryLoanUserInfo")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> queryLoanUserInfo() {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 数据域.
		Map<String, Object> data = new HashMap<String, Object>();

		// 男性数量.
		Double male_num = 0D;
		// 女性数量.
		Double female_num = 0D;
		// 25岁.
		Double age_25 = 0D;
		// 26-30岁.
		Double age_26_30 = 0D;
		// 31-35岁.
		Double age_31_35 = 0D;
		// 36-40岁.
		Double age_36_40 = 0D;
		// 41-45岁.
		Double age_41_45 = 0D;
		// 46-50岁.
		Double age_46_50 = 0D;
		// 51岁.
		Double age_51 = 0D;

		// 累计借款总额.
		Double loanTotalAmount = 0D;
		// 最大十户出借总额.
		Double theTopTenInvestTotalAmount = 0D;

		try {

			/**
			 * 借贷总额.
			 */
			WloanTermProject project = new WloanTermProject();
			// 上线开始时间.
			project.setBeginTimeFromOnline("2015-03-20 00:00:00");
			// 上线结束时间.
			project.setEndTimeToOnline(DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
			// 项目状态，4：上线，5：满标，6：还款中，7：已完成.
			List<String> stateItem = new ArrayList<String>();
			stateItem.add(WloanTermProjectService.ONLINE);
			stateItem.add(WloanTermProjectService.FULL);
			stateItem.add(WloanTermProjectService.REPAYMENT);
			stateItem.add(WloanTermProjectService.FINISH);
			project.setStateItem(stateItem);
			List<WloanTermProject> projects = wloanTermProjectDao.findList(project);
			for (WloanTermProject wloanTermProject : projects) {
				loanTotalAmount = loanTotalAmount + wloanTermProject.getCurrentAmount();
			}
			// 累计借款金额.
			BigDecimal loanTotalAmountBd = new BigDecimal(loanTotalAmount);
			data.put("loanTotalAmount", CreateSupplyChainPdfContract.fmtMicrometer(CreateSupplyChainPdfContract.formatToString(loanTotalAmountBd)));
			// 累计为出借人赚钱的收益总额.
			Double interestTotalAmount = wloanTermInvestDao.findInterestTotalAmount();
			data.put("interestTotalAmount", CreateSupplyChainPdfContract.fmtMicrometer(NumberUtils.scaleDoubleStr(interestTotalAmount)));
			// 累计出借人出借总额.
			Double investTotalAmount = wloanTermInvestDao.findInvestTotalAmount();
			data.put("investTotalAmount", CreateSupplyChainPdfContract.fmtMicrometer(NumberUtils.scaleDoubleStr(investTotalAmount)));
			// 当前出借人数量.
			Integer nowLoanUserInfoNumbers = wloanTermInvestDao.findNowLoanUserInfoNumbers();
			data.put("nowLoanUserInfoNumbers", nowLoanUserInfoNumbers);
			// 累计出借人数量.
			List<WloanTermInvest> loanUserInfoTotalNumbers = wloanTermInvestDao.findLoanUserInfoTotalNumbers();
			data.put("loanUserInfoTotalNumbers", loanUserInfoTotalNumbers.size());
			// 人均累计出借总额.
			BigDecimal investPerCapitaTotalAmountBd = new BigDecimal(investTotalAmount / loanUserInfoTotalNumbers.size());
			data.put("investPerCapitaTotalAmount", CreateSupplyChainPdfContract.fmtMicrometer(CreateSupplyChainPdfContract.formatToString(investPerCapitaTotalAmountBd)));
			for (WloanTermInvest wloanTermInvest : loanUserInfoTotalNumbers) {
				String certificateNo = wloanTermInvest.getUserInfo().getCertificateNo(); // 身份证号码.
				if (certificateNo == null) {
				} else if (certificateNo.equals("")) {
				} else {
					// 性别.
					String gender = IdcardUtils.getGenderByIdCard(certificateNo); // 性别(M-男，F-女，N-未知).
					if (gender.equals("M")) {
						male_num = male_num + 1;
					}
					if (gender.equals("F")) {
						female_num = female_num + 1;
					}
					// 年龄.
					int age = IdcardUtils.getAgeByIdCard(certificateNo); // 年龄.
					if (age <= 25) {
						age_25 = age_25 + 1;
					} else if (age >= 26 && age <= 30) {
						age_26_30 = age_26_30 + 1;
					} else if (age >= 31 && age <= 35) {
						age_31_35 = age_31_35 + 1;
					} else if (age >= 36 && age <= 40) {
						age_36_40 = age_36_40 + 1;
					} else if (age >= 41 && age <= 45) {
						age_41_45 = age_41_45 + 1;
					} else if (age >= 46 && age <= 50) {
						age_46_50 = age_46_50 + 1;
					} else {
						age_51 = age_51 + 1;
					}
				}
			}
			// 男性数量.
			data.put("male_num", male_num);
			// 男性数量占比.
			Double male_num_percentage = NumberUtils.scaleDouble(male_num / loanUserInfoTotalNumbers.size() * 100);
			data.put("male_num_percentage", male_num_percentage);
			// 女性数量.
			data.put("female_num", female_num);
			Double female_num_percentage = NumberUtils.scaleDouble(100D) - male_num_percentage;
			// 女性数量占比.
			data.put("female_num_percentage", NumberUtils.scaleDouble(female_num_percentage));

			// 25岁占比.
			Double age_25_percentage = NumberUtils.scaleDouble(age_25 / loanUserInfoTotalNumbers.size() * 100);
			data.put("age_25_percentage", age_25_percentage);
			// 26-30岁占比.
			Double age_26_30_percentage = NumberUtils.scaleDouble(age_26_30 / loanUserInfoTotalNumbers.size() * 100);
			data.put("age_26_30_percentage", NumberUtils.scaleDouble(age_26_30 / loanUserInfoTotalNumbers.size() * 100));
			// 31-35岁占比.
			Double age_31_35_percentage = NumberUtils.scaleDouble(age_31_35 / loanUserInfoTotalNumbers.size() * 100);
			data.put("age_31_35_percentage", age_31_35_percentage);
			// 36-40岁占比.
			Double age_36_40_percentage = NumberUtils.scaleDouble(age_36_40 / loanUserInfoTotalNumbers.size() * 100);
			data.put("age_36_40_percentage", age_36_40_percentage);
			// 41-45岁占比.
			Double age_41_45_percentage = NumberUtils.scaleDouble(age_41_45 / loanUserInfoTotalNumbers.size() * 100);
			data.put("age_41_45_percentage", age_41_45_percentage);
			// 46-50岁占比.
			Double age_46_50_percentage = NumberUtils.scaleDouble(age_46_50 / loanUserInfoTotalNumbers.size() * 100);
			data.put("age_46_50_percentage", age_46_50_percentage);
			Double age_51_percentage = NumberUtils.scaleDouble(100D) - NumberUtils.scaleDouble(age_25_percentage + age_26_30_percentage + age_31_35_percentage + age_36_40_percentage + age_41_45_percentage + age_46_50_percentage);
			data.put("age_51_percentage", NumberUtils.scaleDouble(age_51_percentage));
			// 出借人出借总额倒序列表.
			List<WloanTermInvest> loanUserInvestList = wloanTermInvestDao.findLoanUserInvestList();
			// 最大单户出借余额占比.
			WloanTermInvest theFirstWloanTermInvest = loanUserInvestList.get(0);
			if (theFirstWloanTermInvest != null) {
				data.put("theFirstInvestTotalAmountPercentage", NumberUtils.scaleDoubleStr(theFirstWloanTermInvest.getAmount() / investTotalAmount * 100));
			} else {
				data.put("theFirstInvestTotalAmountPercentage", "0.00");
			}
			// 最大十户出借余额占比.
			for (int i = 0; i < 10; i++) {
				WloanTermInvest wloanTermInvest = loanUserInvestList.get(i);
				if (wloanTermInvest != null) {
					theTopTenInvestTotalAmount = theTopTenInvestTotalAmount + wloanTermInvest.getAmount();
				}
			}
			data.put("theTopTenInvestTotalAmountPercentage", NumberUtils.scaleDoubleStr(theTopTenInvestTotalAmount / investTotalAmount * 100));

		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "接口异常");
			result.put("data", data);
			return result;
		}

		log.info(this.getClass() + "借款代偿信息，数据请求成功");
		result.put("state", "0");
		result.put("message", "接口请求成功");
		result.put("data", data);
		return result;
	}

	/**
	 * 
	 * 方法: queryLoanReplaceRepayInfo <br>
	 * 描述: 查询借款代偿信息. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2018年3月28日 下午2:37:31
	 * 
	 * @return
	 */
	@POST
	@Path("/queryLoanReplaceRepayInfo")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> queryLoanReplaceRepayInfo() {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 数据域.
		Map<String, Object> data = new HashMap<String, Object>();

		// 累计代偿金额.
		data.put("theCumulativeReplaceRepayAmount", "0.00");
		// 累计代偿笔数.
		data.put("theCumulativeReplaceRepayNumbers", "0");

		log.info(this.getClass() + "借款代偿信息，数据请求成功");
		result.put("state", "0");
		result.put("message", "接口请求成功");
		result.put("data", data);

		return result;
	}

	/**
	 * 
	 * 方法: queryLoanOverdueInfo <br>
	 * 描述: 查询借款逾期信息. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2018年3月28日 下午2:35:34
	 * 
	 * @return
	 */
	@POST
	@Path("/queryLoanOverdueInfo")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> queryLoanOverdueInfo() {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 数据域.
		Map<String, Object> data = new HashMap<String, Object>();

		try {

			// 逾期金额.
			data.put("overdueAmount", "0.00");
			// 逾期笔数.
			data.put("overdueNumbers", "0");
			// 逾期90天金额.
			data.put("overdue90DaysAmount", "0.00");
			// 逾期90天笔数.
			data.put("overdue90DaysNumbers", "0");
			// 金额逾期率.
			data.put("amountOverdueRate", "0.00");
			// 项目逾期率.
			data.put("projectOverdueRate", "0.00");
			// 项目分级逾期率90天.
			data.put("projectClassificationOverdueRate90", "0.00");
			// 项目分级逾期率180天.
			data.put("projectClassificationOverdueRate180", "0.00");
			// 项目分级逾期率360天.
			data.put("projectClassificationOverdueRate360", "0.00");
			// 金额分级逾期率90天.
			data.put("amountClassificationOverdueRate90", "0.00");
			// 金额分级逾期率180天.
			data.put("amountClassificationOverdueRate180", "0.00");
			// 金额分级逾期率360天.
			data.put("amountClassificationOverdueRate360", "0.00");

			log.info(this.getClass() + "借款逾期信息，数据请求成功");
			result.put("state", "0");
			result.put("message", "接口请求成功");
			result.put("data", data);

		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "接口异常");
			result.put("data", data);
			return result;
		}

		return result;
	}

	/**
	 * 
	 * 方法: queryLoanPeopletInfo <br>
	 * 描述: 查询借款人信息. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2018年3月28日 下午2:33:30
	 * 
	 * @return
	 */
	@POST
	@Path("/queryLoanPeopletInfo")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> queryLoanPeopletInfo() {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 数据域.
		Map<String, Object> data = new HashMap<String, Object>();

		// 所有借款人待还总金额.
		Double loanAllUserStayStillTotalAmount = 0D;
		// 所有借款人待还总金额.
		// Double theBiggestLoanAllUserStayStillTotalAmount = 0D;
		// 前十大借款人在贷本金余额.
		Double loanUserStayStillTotalAmount = 0D;
		// 全部借款人的借款总额.
		Double loanAllUserTotalAmount = 0D;
		// 在贷本金余额.
		Double loanAllUserTotalPrincipalAmount = 0D;

		try {

			// 累计借款人数量（融资主体为借款人）.
			List<String> findLoanUserTotalNumbers = wloanSubjectDao.findLoanUserTotalNumbers();
			data.put("loanUserTotalNumbers", findLoanUserTotalNumbers.size());
			// 当前借款人数量.
			List<String> findNowLoanUserTotalNumbers = wloanSubjectDao.findNowLoanUserTotalNumbers();
			data.put("nowLoanUserTotalNumbers", findNowLoanUserTotalNumbers.size());
			// 全部借款人的借款总额列表.
			List<WloanSubject> loanUserTotalAmountList = wloanSubjectDao.findLoanUserTotalAmountList();
			// 全部借款人的借款总额.
			for (WloanSubject wloanSubject : loanUserTotalAmountList) {
				if (wloanSubject.getWloanTermProject() != null) {
					wloanSubject.getWloanTermProject().getCurrentRealAmount();
					if (wloanSubject.getWloanTermProject().getCurrentRealAmount() != null) {
						loanAllUserTotalAmount = loanAllUserTotalAmount + wloanSubject.getWloanTermProject().getCurrentRealAmount();
					}
				}
			}
			// 在贷本金余额列表.
			List<WloanSubject> loanUserTotalPrincipalAmountList = wloanSubjectDao.findLoanUserTotalPrincipalAmountList();
			for (WloanSubject wloanSubject : loanUserTotalPrincipalAmountList) {
				if (wloanSubject.getWloanTermProject() != null) {
					wloanSubject.getWloanTermProject().getCurrentAmount();
					if (wloanSubject.getWloanTermProject().getCurrentAmount() != null) {
						loanAllUserTotalPrincipalAmount = loanAllUserTotalPrincipalAmount + wloanSubject.getWloanTermProject().getCurrentAmount();
					}
				}
			}
			// 累计借贷金额（万元）出借人出借总额
			Double investTotalAmount = wloanTermInvestDao.findInvestTotalAmount();
			// 全部借款人人均借款总额.
			BigDecimal loanAllUserTotalAmountBd = new BigDecimal(NumberUtils.scaleDouble(investTotalAmount / loanUserTotalAmountList.size()));
			data.put("loanAllUserPerCapitaTotalAmount", CreateSupplyChainPdfContract.fmtMicrometer(CreateSupplyChainPdfContract.formatToString(loanAllUserTotalAmountBd)));
			// 前十大借款人在贷本金余额.
			List<WloanSubject> sortLoanUserTotalAmountList = new ArrayList<WloanSubject>();
			if (loanUserTotalPrincipalAmountList.size() > 10) {
				for (int i = 0; i < 10; i++) {
					sortLoanUserTotalAmountList.add(loanUserTotalPrincipalAmountList.get(i));
				}
			} else {
				for (int i = 0; i < loanUserTotalPrincipalAmountList.size(); i++) {
					sortLoanUserTotalAmountList.add(loanUserTotalPrincipalAmountList.get(i));
				}
			}

			// 前十大借款人待还(本金)总金额.
			for (WloanSubject wloanSubject : sortLoanUserTotalAmountList) {
				// 借款人待还总额.
				// Double stayStillTotalAmount =
				// wloanSubjectDao.findLoanUserStayStillTotalAmount(wloanSubject.getId());
				if (wloanSubject.getWloanTermProject() != null) {
					if (wloanSubject.getWloanTermProject().getCurrentAmount() != null) {
						loanUserStayStillTotalAmount = NumberUtils.scaleDouble(loanUserStayStillTotalAmount) + NumberUtils.scaleDouble(wloanSubject.getWloanTermProject().getCurrentAmount());
					}
				}
			}
			data.put("loanUserStayStillTotalAmount", NumberUtils.scaleDouble(loanUserStayStillTotalAmount));
			if (loanUserStayStillTotalAmount.equals(0D)) { // 前十大借款人待还总额为零时，过滤查询所有借款人的待还总额.
				// 前十大借款人待还金额占比（%）.
				data.put("theTopTenStayStillTotalAmountPercentage", "0.00");
				// 其它占比（%）.
				data.put("otherTheTopTenStayStillTotalAmountPercentage", "100.00");
			} else {
				// 所有借款人待还总金额.
				for (WloanSubject wloanSubject : sortLoanUserTotalAmountList) {
					// 借款人待还总额.
					Double stayStillTotalAmount = wloanSubjectDao.findLoanUserStayStillTotalAmount(wloanSubject.getId());
					if (stayStillTotalAmount != null) {
						loanAllUserStayStillTotalAmount = NumberUtils.scaleDouble(loanAllUserStayStillTotalAmount) + NumberUtils.scaleDouble(stayStillTotalAmount);
					}
				}
				data.put("loanAllUserStayStillTotalAmount", NumberUtils.scaleDouble(loanAllUserStayStillTotalAmount));
				if (loanAllUserTotalPrincipalAmount.equals(0D)) {
					// 前十大借款人待还金额占比（%）.
					data.put("theTopTenStayStillTotalAmountPercentage", "0.00");
					// 其它占比（%）.
					data.put("otherTheTopTenStayStillTotalAmountPercentage", "100.00");
				} else {
					// 前十大借款人待还金额占比（%）.
					data.put("theTopTenStayStillTotalAmountPercentage", NumberUtils.scaleDoubleStr(loanUserStayStillTotalAmount / loanAllUserTotalPrincipalAmount * 100));
					// 其它占比（%）.
					data.put("otherTheTopTenStayStillTotalAmountPercentage", NumberUtils.scaleDoubleStr(100 - (loanUserStayStillTotalAmount / loanAllUserTotalPrincipalAmount * 100)));
				}
			}

			// 最大单一借款人待还金额占比（%）：指在平台撮合的项目中，借款最多一户借款人的借款余额占总借款余额的比例。
			if (sortLoanUserTotalAmountList.size() > 0) {
				WloanSubject wloanSubject = sortLoanUserTotalAmountList.get(0); // 最大单一借款人.
				if (wloanSubject != null) {
					// 借款人待还总额.
					// Double stayStillTotalAmount =
					// wloanSubjectDao.findLoanUserStayStillTotalAmount(wloanSubject.getId());
					if (wloanSubject.getWloanTermProject() != null) {
						if (wloanSubject.getWloanTermProject().getCurrentAmount() != null) {
							if (wloanSubject.getWloanTermProject().getCurrentAmount().equals(0D)) { // 最大单一借款人待还金额为零.
								// 最大单一借款人待还金额占比（%）.
								data.put("theBiggestStayStillTotalAmountPercentage", "0.00");
								// 其它占比（%）.
								data.put("otherTheBiggestStayStillTotalAmountPercentage", "100.00");
							} else {
								if (loanAllUserTotalPrincipalAmount.equals(0D)) {
									// 最大单一借款人待还金额占比（%）.
									data.put("theBiggestStayStillTotalAmountPercentage", "0.00");
									// 其它占比（%）.
									data.put("otherTheBiggestStayStillTotalAmountPercentage", "100.00");
								} else {
									// 最大单一借款人待还金额占比（%）.
									data.put("theBiggestStayStillTotalAmountPercentage", NumberUtils.scaleDoubleStr(wloanSubject.getWloanTermProject().getCurrentAmount() / loanAllUserTotalPrincipalAmount * 100));
									// 其它占比（%）.
									data.put("otherTheBiggestStayStillTotalAmountPercentage", NumberUtils.scaleDoubleStr(100 - (wloanSubject.getWloanTermProject().getCurrentAmount() / loanAllUserTotalPrincipalAmount * 100)));
								}
							}
						} else {
							// 最大单一借款人待还金额占比（%）.
							data.put("theBiggestStayStillTotalAmountPercentage", "0.00");
							// 其它占比（%）.
							data.put("otherTheBiggestStayStillTotalAmountPercentage", "100.00");
						}
					}
				}
			} else {
				// 最大单一借款人待还金额占比（%）.
				data.put("theBiggestStayStillTotalAmountPercentage", "0.00");
				// 其它占比（%）.
				data.put("otherTheBiggestStayStillTotalAmountPercentage", "100.00");
			}

			log.info(this.getClass() + "借款人信息，数据请求成功");
			result.put("state", "0");
			result.put("message", "请求接口成功");
			result.put("data", data);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "接口异常");
			result.put("data", data);
			return result;
		}

		return result;
	}

	/**
	 * 
	 * 方法: queryLoanProjectInfo <br>
	 * 描述: 查询借款标的信息. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2018年3月28日 下午2:32:19
	 * 
	 * @return
	 */
	@POST
	@Path("/queryLoanProjectInfo")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> queryLoanProjectInfo() {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 数据域.
		Map<String, Object> data = new HashMap<String, Object>();

		// 累计借款总额.
		Double loanTotalAmount = 0D;
		// 借贷余额.
		Double loanTotalAvailableAmount = 0D;

		try {

			// 累计出借人出借总额.
			Double investTotalAmount = wloanTermInvestDao.findInvestTotalAmount();
			// BigDecimal investTotalAmountBd = new BigDecimal(investTotalAmount
			// / 10000);
			BigDecimal investTotalAmountBd = new BigDecimal(NumberUtils.scaleDouble(investTotalAmount));
			data.put("investTotalAmount", CreateSupplyChainPdfContract.fmtMicrometer(CreateSupplyChainPdfContract.formatToString(investTotalAmountBd)));

			/**
			 * 借贷总额.
			 */
			WloanTermProject project = new WloanTermProject();
			// 上线开始时间.
			project.setBeginTimeFromOnline("2015-03-20 00:00:00");
			// 上线结束时间.
			project.setEndTimeToOnline(DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
			// 项目状态，4：上线，5：满标，6：还款中，7：已完成.
			List<String> stateItem = new ArrayList<String>();
			stateItem.add(WloanTermProjectService.ONLINE);
			stateItem.add(WloanTermProjectService.FULL);
			stateItem.add(WloanTermProjectService.REPAYMENT);
			stateItem.add(WloanTermProjectService.FINISH);
			project.setStateItem(stateItem);
			List<WloanTermProject> projects = wloanTermProjectDao.findList(project);
			for (WloanTermProject wloanTermProject : projects) {
				loanTotalAmount = loanTotalAmount + wloanTermProject.getCurrentAmount();
			}
			// 累计借款金额.
			// BigDecimal loanTotalAmountBd = new BigDecimal(loanTotalAmount /
			// 10000);
			BigDecimal loanTotalAmountBd = new BigDecimal(NumberUtils.scaleDouble(loanTotalAmount));
			data.put("loanTotalAmount", CreateSupplyChainPdfContract.fmtMicrometer(CreateSupplyChainPdfContract.formatToString(loanTotalAmountBd)));
			// 借款累计笔数.
			data.put("loanTotalNumbers", String.valueOf(projects.size()));

			// 借贷本息余额.
			Double loanUserTotalPrincipalAndInterestAmount = wloanSubjectDao.findLoanUserTotalPrincipalAndInterestAmount();

			/**
			 * 借贷 本金余额.
			 */
			WloanTermProject projectA = new WloanTermProject();
			// 上线开始时间.
			projectA.setBeginTimeFromOnline("2015-03-20 00:00:00");
			// 上线结束时间.
			projectA.setEndTimeToOnline(DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
			// 项目状态，4：上线，5：满标，6：还款中，7：已完成.
			List<String> stateItemA = new ArrayList<String>();
			stateItemA.add(WloanTermProjectService.ONLINE);
			stateItemA.add(WloanTermProjectService.FULL);
			stateItemA.add(WloanTermProjectService.REPAYMENT);
			projectA.setStateItem(stateItemA);
			List<WloanTermProject> projectAs = wloanTermProjectDao.findList(projectA);
			for (WloanTermProject wloanTermProject : projectAs) {
				loanTotalAvailableAmount = loanTotalAvailableAmount + wloanTermProject.getCurrentAmount();
			}
			// 借贷本金余额.
			// BigDecimal loanTotalAvailableAmountBd = new
			// BigDecimal(loanTotalAvailableAmount / 10000);
			BigDecimal loanTotalAvailableAmountBd = new BigDecimal(NumberUtils.scaleDouble(loanTotalAvailableAmount));
			data.put("loanTotalAvailableAmount", CreateSupplyChainPdfContract.fmtMicrometer(CreateSupplyChainPdfContract.formatToString(loanTotalAvailableAmountBd)));
			
			//在贷本金
			WloanTermProject termProject = new WloanTermProject();
			// 上线开始时间.
			termProject.setBeginTimeFromOnline("2015-03-20 00:00:00");
			// 上线结束时间.
			termProject.setEndTimeToOnline(DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
			Double loanPrincipal = 0D;
			List<String> itemA = new ArrayList<String>();
			itemA.add(WloanTermProjectService.FULL);
			itemA.add(WloanTermProjectService.REPAYMENT);
			termProject.setStateItem(itemA);
			List<WloanTermProject> projectList = wloanTermProjectDao.findList(termProject);
			for (WloanTermProject wloanTermProject : projectList) {
				loanPrincipal = loanPrincipal + wloanTermProject.getCurrentAmount();
			}
			System.out.println("在贷本金loanPrincipal="+loanPrincipal);
			log.info("在贷本金loanPrincipal="+loanPrincipal);
			System.out.println("在贷本息和loanUserTotalPrincipalAndInterestAmount="+loanUserTotalPrincipalAndInterestAmount);
			log.info("在贷本息和loanUserTotalPrincipalAndInterestAmount="+loanUserTotalPrincipalAndInterestAmount);
			// 利息余额.
//			Double loanUserTotalInterestAmount = NumberUtils.scaleDouble(loanUserTotalPrincipalAndInterestAmount - loanTotalAvailableAmount);
			Double loanUserTotalInterestAmount = NumberUtils.scaleDouble(loanUserTotalPrincipalAndInterestAmount - loanPrincipal);
			// BigDecimal loanUserTotalInterestAmountBd = new
			// BigDecimal(loanUserTotalInterestAmount / 10000);
			BigDecimal loanUserTotalInterestAmountBd = new BigDecimal(NumberUtils.scaleDouble(loanUserTotalInterestAmount));
			data.put("loanUserTotalInterestAmount", CreateSupplyChainPdfContract.fmtMicrometer(CreateSupplyChainPdfContract.formatToString(loanUserTotalInterestAmountBd)));
			// 借款本金余额笔数.
			data.put("loanTotalAvailableNumbers", String.valueOf(projectAs.size()));

			// 关联关系借款金额.
			data.put("loanRelationalTotalAmount", "0.00");
			// 关联关系借款笔数.
			data.put("loanRelationalTotalNumbers", "0");

			log.info(this.getClass() + "借款标的信息，数据请求成功");
			result.put("state", "0");
			result.put("message", "接口请求成功");
			result.put("data", data);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "接口异常");
			result.put("data", data);
			return result;
		}

		return result;
	}

	/**
	 * 
	 * 方法: pastLeep <br>
	 * 描述: List<String>去重. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年11月13日 上午10:49:12
	 * 
	 * @param list
	 * @return
	 */
	public static int pastLeep(List<String> list) {

		int num = 0;
		List<String> listNew = new ArrayList<String>();
		Set<String> set = new HashSet<String>();
		for (String str : list) {
			if (set.add(str)) {
				listNew.add(str);
			}
		}
		num = listNew.size();
		return num;
	}

}
