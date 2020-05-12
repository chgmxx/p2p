package com.power.platform.credit.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.power.platform.cache.Cache;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.MemCachedUtis;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.credit.dao.userinfo.CreditUserInfoDao;
import com.power.platform.credit.entity.apply.CreditUserApply;
import com.power.platform.credit.entity.collateral.CreditCollateralInfo;
import com.power.platform.credit.entity.info.CreditInfo;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.service.apply.CreditUserApplyService;
import com.power.platform.credit.service.collateral.CreditCollateralInfoService;
import com.power.platform.credit.service.userinfo.CreditUserInfoService;
import com.power.platform.plan.entity.WloanTermProjectPlan;
import com.power.platform.plan.service.WloanTermProjectPlanService;
import com.power.platform.regular.entity.WloanSubject;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.regular.service.WloanSubjectService;
import com.power.platform.regular.service.WloanTermProjectService;
import com.power.platform.userinfo.entity.Principal;

/**
 * 借款app 借款/还款项目
 * 
 * @author YHAGZALUN WO SJIAOSY
 *
 */
@Component
@Path("/creditproject")
@Service("creditProjectService")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class CreditProjectService {

	@Autowired
	private CreditUserInfoDao creditUserInfoDao;
	@Autowired
	private CreditUserApplyService creditUserApplyService;
	@Autowired
	private WloanTermProjectService wloanTermProjectService;
	@Autowired
	private WloanTermProjectPlanService wloanTermProjectPlanService;
	@Autowired
	private CreditCollateralInfoService creditCollateralInfoService;
	@Autowired
	private WloanSubjectService wloanSubjectService;
    @Autowired
    private CreditUserInfoService creditUserInfoService;
	
	/**
	 * 
	 * 方法: findCreditRepayList <br>
	 * 描述: 查询借款人还款计划列表. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年10月19日 上午11:42:41
	 * 
	 * @param token
	 * @return
	 */
	@POST
	@Path("/findCreditRepayList")
	public Map<String, Object> findCreditRepayList(@FormParam("token") String token,@FormParam("type")String type) {

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		CreditUserInfo userInfo = new CreditUserInfo();
		List<Map<String, Object>> repayList = new ArrayList<Map<String, Object>>();
		int totalPeriods = 0;
		// N1.判断必要参数是否为空
		if (StringUtils.isBlank(token)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		try {
			// N2.获取借款用户信息
			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			if (null != principal && null != principal.getCreditUserInfo()) {
				userInfo = creditUserInfoDao.get(principal.getCreditUserInfo().getId());
				if (userInfo != null) {
					// 根据借款人ID查询借款项目.
					WloanTermProject entity = new WloanTermProject();
					WloanSubject subject = new WloanSubject();
					if(type.equals("02")){
						subject.setLoanApplyId(userInfo.getId());
					}
					//subject.setLoanApplyId(userInfo.getId());
					List<WloanSubject> subjectList = wloanSubjectService.findList(subject);
					if (subjectList != null && subjectList.size() > 0) {
						for (WloanSubject wloanSubject : subjectList) {
							//获取所有融资主体 根据融资主体是否有借款户ID 有则进行循环 无则跳出本次循环
							CreditUserInfo creditUserInfo = creditUserInfoService.get(wloanSubject.getLoanApplyId());
							if(creditUserInfo==null){
								continue;
							}
							entity.setSubjectId(wloanSubject.getId());
							List<WloanTermProject> projectList = wloanTermProjectService.findList(entity);
							if (projectList != null && projectList.size() > 0) {
								for (WloanTermProject loanProject : projectList) {
									// N5.根据项目状态是否为[融资中/已放款]来查询项目还款记录
									if (loanProject.getState().equals(WloanTermProjectService.ONLINE) || loanProject.getState().equals(WloanTermProjectService.FULL) || loanProject.getState().equals(WloanTermProjectService.REPAYMENT) || loanProject.getState().equals(WloanTermProjectService.FINISH)) {

										Double dueRepayAmount = 0d;
										// N6.根据项目ID查询借款用户的项目还款计划
										WloanTermProjectPlan projectPlan = new WloanTermProjectPlan();
										projectPlan.setWloanTermProject(loanProject);
										List<WloanTermProjectPlan> planList = wloanTermProjectPlanService.findList(projectPlan);
										if (planList != null && planList.size() > 0) {
											totalPeriods = planList.size();
											for (int i = 0; i < planList.size(); i++) {
												WloanTermProjectPlan plan = planList.get(i);
												Map<String, Object> map = new HashMap<String, Object>();
												map.put("planId", plan.getId());
												map.put("projectName", plan.getWloanTermProject().getName());
												map.put("projectId", plan.getWloanTermProject().getId());
												map.put("repayAmount", NumberUtils.scaleDouble(plan.getInterest()));// 本期应还
												map.put("repayDate", DateUtils.formatDateTime(plan.getRepaymentDate()));
												map.put("planState", plan.getState());
												map.put("loanAmount", loanProject.getAmount());// 借款金额
												map.put("periods", i + 1);// 第N期数
												map.put("loanDate", DateUtils.formatDateTime(loanProject.getCreateDate()));// 借款时间
												if (plan.getState().equals("1")) {
													dueRepayAmount = dueRepayAmount + plan.getInterestTrue();
												}
												map.put("totalPeriods", totalPeriods);
												map.put("dueRepayAmount", NumberUtils.scaleDouble(dueRepayAmount));
												repayList.add(map);
											}
										}
									}
								}
							}
						}
						data.put("repayList", repayList);
						result.put("state", "0");
						result.put("message", "借款用户还款列表查询成功");
						result.put("data", data);
						return result;
					} else {
						result.put("state", "5");
						result.put("message", "借款用户暂未有还款项目");
						result.put("data", data);
						return result;
					}
				} else {
					result.put("state", "4");
					result.put("message", "系统超时");
					return result;
				}
			} else {
				result.put("state", "4");
				result.put("message", "系统超时");
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", "系统异常");
			result.put("state", "3");
			return result;
		}
	}

	/**
	 * 我的借款列表
	 * 
	 * @param from
	 * @param creditUserId
	 * @return
	 */
	@POST
	@Path("/getCreditLoanList")
	public Map<String, Object> getCreditLoanList(@FormParam("pageNo") Integer pageNo, @FormParam("pageSize") Integer pageSize, @FormParam("token") String token) {

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		CreditUserInfo userInfo = new CreditUserInfo();
		List<Map<String, Object>> applylist = new ArrayList<Map<String, Object>>();
		// N1.判断必要参数是否为空
		if (StringUtils.isBlank(token)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		try {
			// N2.获取借款用户信息
			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			if (null != principal && null != principal.getCreditUserInfo()) {
				userInfo = creditUserInfoDao.get(principal.getCreditUserInfo().getId());
				if (userInfo != null) {
					CreditUserApply creditUserApply = new CreditUserApply();
					creditUserApply.setReplaceUserId(userInfo.getId());
					Page<CreditUserApply> page = new Page<CreditUserApply>();
					page.setPageNo(pageNo);
					page.setPageSize(pageSize);
					Page<CreditUserApply> creditUserApplyPage = creditUserApplyService.findPage(page, creditUserApply);
					List<CreditUserApply> list = creditUserApplyPage.getList();
					if (list != null && list.size() > 0) {
						for (CreditUserApply userApply : list) {
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("id", userApply.getId());
							map.put("creditInfoId", userApply.getProjectDataInfo().getId());
							map.put("creditName", userApply.getProjectDataInfo().getName());
							map.put("amount", userApply.getAmount());
							map.put("state", userApply.getState());
							applylist.add(map);
						}
						data.put("applyList", applylist);
						data.put("pageNo", creditUserApplyPage.getPageNo());
						data.put("pageSize", creditUserApplyPage.getPageSize());
						data.put("totalCount", creditUserApplyPage.getCount());
						data.put("last", creditUserApplyPage.getLast());
						data.put("pageCount", creditUserApplyPage.getLast());
						result.put("state", "0");
						result.put("message", "借款用户借款列表查询成功");
						result.put("data", data);
						return result;
					} else {
						result.put("state", "0");
						result.put("message", "借款用户暂未借款");
						result.put("data", data);
						return result;
					}

				} else {
					result.put("state", "4");
					result.put("message", "系统超时");
					return result;
				}
			} else {
				result.put("state", "4");
				result.put("message", "系统超时");
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", "系统异常");
			result.put("state", "3");
			return result;
		}
	}

	/**
	 * 我的还款列表
	 * 
	 * @param token
	 * @return
	 */
	@POST
	@Path("/getCreditRepayList")
	public Map<String, Object> getCreditRepayList(@FormParam("token") String token) {

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		CreditUserInfo userInfo = new CreditUserInfo();
		List<Map<String, Object>> repayList = new ArrayList<Map<String, Object>>();
		int totalPeriods = 0;
		// N1.判断必要参数是否为空
		if (StringUtils.isBlank(token)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		try {
			// N2.获取借款用户信息
			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			if (null != principal && null != principal.getCreditUserInfo()) {
				userInfo = creditUserInfoDao.get(principal.getCreditUserInfo().getId());
				if (userInfo != null) {
					CreditUserApply creditUserApply = new CreditUserApply();
					creditUserApply.setCreditUserInfo(userInfo);
					// N3.获取借款用户借款列表
					List<CreditUserApply> list = creditUserApplyService.findList(creditUserApply);
					if (list != null && list.size() > 0) {
						for (CreditUserApply userApply : list) {
							// N4.根据借款ID查询项目列表
							WloanTermProject entity = new WloanTermProject();
							WloanSubject subject = new WloanSubject();
							subject.setLoanApplyId(userApply.getId());
							List<WloanSubject> subjectList = wloanSubjectService.findList(subject);
							if (subjectList != null && subjectList.size() > 0) {
								subject = subjectList.get(0);
								entity.setSubjectId(subject.getId());
								List<WloanTermProject> projectList = wloanTermProjectService.findList(entity);
								if (projectList != null && projectList.size() > 0) {
									for (WloanTermProject loanProject : projectList) {
										// N5.根据项目状态是否为[融资中/已放款]来查询项目还款记录
										if (loanProject.getState().equals(WloanTermProjectService.REPAYMENT) || loanProject.getState().equals(WloanTermProjectService.ONLINE)) {
											Double dueRepayAmount = 0d;
											// N6.根据项目ID查询借款用户的项目还款计划
											WloanTermProjectPlan projectPlan = new WloanTermProjectPlan();
											projectPlan.setWloanTermProject(loanProject);
											List<WloanTermProjectPlan> planList = wloanTermProjectPlanService.findList(projectPlan);
											if (planList != null && planList.size() > 0) {
												totalPeriods = planList.size();
												for (int i = 0; i < planList.size(); i++) {
													WloanTermProjectPlan plan = planList.get(i);
													Map<String, Object> map = new HashMap<String, Object>();
													map.put("planId", plan.getId());
													map.put("projectName", plan.getWloanTermProject().getName());
													map.put("projectId", plan.getWloanTermProject().getId());
													map.put("repayAmount", NumberUtils.scaleDouble(plan.getInterestTrue()));// 本期应还
													map.put("repayDate", DateUtils.formatDateTime(plan.getRepaymentDate()));
													map.put("planState", plan.getState());
													map.put("loanAmount", loanProject.getAmount());// 借款金额
													map.put("periods", i + 1);// 第N期数
													map.put("loanDate", DateUtils.formatDateTime(loanProject.getCreateDate()));// 借款时间
													if (plan.getState().equals("1")) {
														dueRepayAmount = dueRepayAmount + plan.getInterestTrue();
													}
													map.put("totalPeriods", totalPeriods);
													map.put("dueRepayAmount", NumberUtils.scaleDouble(dueRepayAmount));
													repayList.add(map);
												}
											}

										}
									}
								}
							}
						}
						data.put("repayList", repayList);
						result.put("state", "0");
						result.put("message", "借款用户还款列表查询成功");
						result.put("data", data);
						return result;
					} else {
						result.put("state", "0");
						result.put("message", "借款用户暂未有还款项目");
						result.put("data", data);
						return result;
					}

				} else {
					result.put("state", "4");
					result.put("message", "系统超时");
					return result;
				}
			} else {
				result.put("state", "4");
				result.put("message", "系统超时");
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", "系统异常");
			result.put("state", "3");
			return result;
		}
	}

	/**
	 * 我的还款计划详情
	 * 
	 * @param token
	 * @param planId
	 * @return
	 */
	@POST
	@Path("/getCreditRepayPlan")
	public Map<String, Object> getCreditRepayPlan(@FormParam("token") String token, @FormParam("planId") String planId) {

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		CreditUserInfo userInfo = new CreditUserInfo();
		// N1.判断必要参数是否为空
		if (StringUtils.isBlank(token)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		try {

			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			if (null != principal && null != principal.getCreditUserInfo()) {
				// N2.获取借款用户信息
				userInfo = creditUserInfoDao.get(principal.getCreditUserInfo().getId());
				if (userInfo != null) {
					// N3.根据还款计划ID查询还款详情
					WloanTermProjectPlan plan = wloanTermProjectPlanService.get(planId);
					if (plan != null) {
						data.put("planId", plan.getId());
						data.put("projectId", plan.getWloanTermProject().getId());
						data.put("porjectName", plan.getWloanTermProject().getName());
						data.put("RepayAmount", plan.getInterestTrue());
						data.put("repayDate", DateUtils.formatDateTime(plan.getRepaymentDate()));
						result.put("state", "0");
						result.put("message", "还款详情查询成功");
						result.put("data", data);
					} else {
						result.put("state", "0");
						result.put("message", "未查询到还款详情");
						result.put("data", null);
					}
					return result;
				} else {
					result.put("state", "4");
					result.put("message", "系统超时");
					return result;
				}
			} else {
				result.put("state", "4");
				result.put("message", "系统超时");
				result.put("data", "");
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", "系统异常");
			result.put("state", "3");
			result.put("data", null);
			return result;
		}
	}

}
