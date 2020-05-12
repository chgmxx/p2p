package com.power.platform.regular.repayplan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.power.platform.common.config.Global;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.utils.CommonStringUtils;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.SendMailUtil;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.utils.excel.ExportExcel;
import com.power.platform.common.web.BaseController;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.service.userinfo.CreditUserInfoService;
import com.power.platform.plan.dao.WloanTermProjectPlanDao;
import com.power.platform.plan.dao.WloanTermUserPlanDao;
import com.power.platform.plan.entity.WloanTermProjectPlan;
import com.power.platform.plan.entity.WloanTermProjectPlanPoJo;
import com.power.platform.plan.entity.WloanTermUserPlan;
import com.power.platform.plan.service.WloanTermProjectPlanService;
import com.power.platform.plan.service.WloanTermUserPlanService;
import com.power.platform.regular.entity.WloanSubject;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.regular.service.WloanSubjectService;
import com.power.platform.regular.service.WloanTermProjectService;
import com.power.platform.userinfo.entity.UserInfo;

/**
 * 
 * 类: WloanTermProjectPlanController <br>
 * 描述: 项目还款计划. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2017年12月13日 上午10:35:26
 */
@Controller
@RequestMapping(value = "${adminPath}/wloanproject/wloanTermProjectPlan")
public class WloanTermProjectPlanController extends BaseController {

	private static final Logger log = LoggerFactory.getLogger(WloanTermProjectPlanController.class);

	/**
	 * 连连-还款导出.
	 */
	private static final String EXPORT_FLAG_1 = "1";
	/**
	 * 存管-还款导出.
	 */
	private static final String EXPORT_FLAG_2 = "2";

	@Autowired
	private WloanTermProjectPlanService wloanTermProjectPlanService;
	@Autowired
	private WloanTermProjectService wloanTermProjectService;
	@Autowired
	private WloanTermUserPlanService wloanTermUserPlanService;
	@Resource
	private WloanTermProjectPlanDao wloanTermProjectPlanDao;
	@Resource
	private WloanTermUserPlanDao wloanTermUserPlanDao;
	@Autowired
	private CreditUserInfoService creditUserInfoService;
	@Autowired
	private WloanSubjectService wloanSubjectService;

	@ModelAttribute
	public WloanTermProjectPlan get(@RequestParam(required = false) String id) {

		WloanTermProjectPlan entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = wloanTermProjectPlanService.get(id);
		}
		if (entity == null) {
			entity = new WloanTermProjectPlan();
		}
		return entity;
	}

	/**
	 * 
	 * 方法: findByProId <br>
	 * 描述: 供应链项目还款计划列表-单一项目. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年11月14日 上午9:20:36
	 * 
	 * @param wloanTermProjectPlan
	 * @param proid
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequiresPermissions("wloanproject:wloanTermProjectPlan:view")
	@RequestMapping(value = "findByProId")
	public String findByProId(WloanTermProjectPlan wloanTermProjectPlan, String proid, HttpServletRequest request, HttpServletResponse response, Model model) {

		try {
			log.info("项目还款计划列表-单一项目.");
			List<WloanTermProjectPlan> plans = wloanTermProjectPlanService.findListByProjectId(proid);
			WloanTermProject wloanTermProject = wloanTermProjectService.get(proid);
			model.addAttribute("surplusAmount", wloanTermProject.getState().equals(WloanTermProjectService.REPAYMENT) ? 0 : -1);
			model.addAttribute("proid", proid);
			model.addAttribute("plans", plans);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "modules/regular/repayplan/ubByProIdOnProjectPlanList";
	}

	/**
	 * 
	 * 方法: findAxtByProId <br>
	 * 描述: 安心投项目还款计划列表-单一项目. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2018年1月16日 上午10:05:36
	 * 
	 * @param wloanTermProjectPlan
	 * @param proid
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequiresPermissions("wloanproject:wloanTermProjectPlan:view")
	@RequestMapping(value = "findAxtByProId")
	public String findAxtByProId(WloanTermProjectPlan wloanTermProjectPlan, String proid, HttpServletRequest request, HttpServletResponse response, Model model) {

		try {
			log.info("安心投项目还款计划列表-单一项目.");
			List<WloanTermProjectPlan> plans = wloanTermProjectPlanService.findListByProjectId(proid);
			WloanTermProject wloanTermProject = wloanTermProjectService.get(proid);
			model.addAttribute("surplusAmount", wloanTermProject.getState().equals(WloanTermProjectService.REPAYMENT) ? 0 : -1);
			model.addAttribute("proid", proid);
			model.addAttribute("plans", plans);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "modules/regular/repayplan/ubAxtByProIdOnProjectPlanList";
	}

	/**
	 * 根据项目ID查询该项目还款计划
	 * 
	 * @param wloanTermProjectPlan
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequiresPermissions("wloanproject:wloanTermProjectPlan:view")
	@RequestMapping(value = "proid")
	public String findByProjectId(WloanTermProjectPlan wloanTermProjectPlan, String proid, HttpServletRequest request, HttpServletResponse response, Model model) {

		try {
			List<WloanTermProjectPlan> plans = wloanTermProjectPlanService.findListByProjectId(proid);
			WloanTermProject wloanTermProject = wloanTermProjectService.get(proid);
			model.addAttribute("surplusAmount", wloanTermProject.getState().equals(WloanTermProjectService.REPAYMENT) ? 0 : -1);
			model.addAttribute("proid", proid);
			model.addAttribute("plans", plans);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "modules/regular/repayplan/wloanOneProjectPlanList";
	}

	@RequiresPermissions("wloanproject:wloanTermProjectPlan:view")
	@RequestMapping(value = { "list", "" })
	public String list(WloanTermProjectPlan wloanTermProjectPlan, HttpServletRequest request, HttpServletResponse response, Model model) {

		wloanTermProjectPlan.setRepaymentDate(DateUtils.parseDate(DateUtils.formatDate(new Date(), "yyyy-MM-dd")));
		WloanTermProject wloanTermProject = new WloanTermProject();
		wloanTermProject.setState(WloanTermProjectService.REPAYMENT); // 标的流转状态-还款中.
		wloanTermProject.setProjectProductType(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1); // 标的产品类型-安心投标的.
		wloanTermProject.setOnlineDate(DateUtils.parseDate("2018-01-01")); // 小于项目上线日期.
		wloanTermProjectPlan.setWloanTermProject(wloanTermProject);
		Page<WloanTermProjectPlan> order_page = new Page<WloanTermProjectPlan>(request, response);
		order_page.setOrderBy(", a.project_id");

		Page<WloanTermProjectPlan> page = wloanTermProjectPlanService.findPage(order_page, wloanTermProjectPlan);
		model.addAttribute("page", page);
		return "modules/regular/repayplan/wloanTermProjectPlanList";
	}

	/**
	 * 
	 * 方法: exportRepayPlanInfo <br>
	 * 描述: 导出安心投全部项目. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2018年1月19日 下午4:54:24
	 * 
	 * @param wloanTermProjectPlan
	 * @param request
	 * @param response
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("wloanproject:wloanTermProjectPlan:view")
	@RequestMapping(value = "exportProjectRepayPlanList", method = RequestMethod.POST)
	public String exportProjectRepayPlanList(WloanTermProjectPlan wloanTermProjectPlan, String exportFlag, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {

		try {
			if (exportFlag.equals(EXPORT_FLAG_1)) {

				String fileName = "还款计划【安心投】" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
				wloanTermProjectPlan.setRepaymentDate(DateUtils.parseDate(DateUtils.formatDate(new Date(), "yyyy-MM-dd")));
				WloanTermProject wloanTermProject = new WloanTermProject();
				wloanTermProject.setState(WloanTermProjectService.REPAYMENT); // 标的流转状态-还款中.
				wloanTermProject.setProjectProductType(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1); // 标的产品类型-安心投标的.
				wloanTermProject.setOnlineDate(DateUtils.parseDate("2018-01-01")); // 小于项目上线日期.
				wloanTermProjectPlan.setWloanTermProject(wloanTermProject);
				Page<WloanTermProjectPlan> order_page = new Page<WloanTermProjectPlan>(request, response);
				order_page.setPageSize(3000); // 允许单页最大导出3000条数据
				order_page.setOrderBy(", a.project_id");
				List<WloanTermProjectPlan> proPlans = wloanTermProjectPlanService.findList(wloanTermProjectPlan);
				new ExportExcel("还款计划【安心投】", WloanTermProjectPlan.class).setDataList(proPlans).write(response, fileName).dispose();
				addMessage(redirectAttributes, "还款计划【安心投】导出成功！");
				return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/?repage";
			} else if (exportFlag.equals(EXPORT_FLAG_2)) {

				String fileName = "还款计划【安心投】" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
				// 用于区分还款中的记录和历史还款记录.
				String repayPlanRadioType = wloanTermProjectPlan.getRepayPlanRadioType();
				if (repayPlanRadioType == null || "".equals(repayPlanRadioType)) {
					// 开始时间 = 当前时间.
					// wloanTermProjectPlan.setRepaymentDate(DateUtils.parseDate(DateUtils.formatDate(new
					// Date(), "yyyy-MM-dd")));
					WloanTermProject wloanTermProject = new WloanTermProject();
					wloanTermProject.setState(WloanTermProjectService.REPAYMENT); // 标的流转状态-还款中.
					wloanTermProject.setProjectProductType(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1); // 标的产品类型-安心投标的.
					wloanTermProject.setRealLoanDate(DateUtils.parseDate("2018-01-01")); // 小于项目上线日期.
					wloanTermProjectPlan.setWloanTermProject(wloanTermProject);
					Page<WloanTermProjectPlan> order_page = new Page<WloanTermProjectPlan>();
					order_page.setPageSize(3000); // 允许单页最大导出3000条数据
					order_page.setOrderBy(", a.project_id");
					wloanTermProjectPlan.setPage(order_page);
					wloanTermProjectPlan.setState(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_STATE_1);
					List<WloanTermProjectPlan> proPlans = wloanTermProjectPlanService.findList(wloanTermProjectPlan);
					new ExportExcel("还款计划【安心投】", WloanTermProjectPlan.class).setDataList(proPlans).write(response, fileName).dispose();
					addMessage(redirectAttributes, "还款计划【安心投】导出成功！");
				} else if (WloanTermProjectPlan.REPAY_PLAN_RADIO_TYPE_1.equals(repayPlanRadioType)) {
					WloanTermProject wloanTermProject = new WloanTermProject();
					wloanTermProject.setState(WloanTermProjectService.REPAYMENT); // 标的流转状态-还款中.
					wloanTermProject.setProjectProductType(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1); // 标的产品类型，1：安心投类.
					wloanTermProject.setRealLoanDate(DateUtils.parseDate("2018-01-01")); // 大于真实放款日期.
					wloanTermProjectPlan.setWloanTermProject(wloanTermProject);
					Page<WloanTermProjectPlan> order_page = new Page<WloanTermProjectPlan>(request, response);
					order_page.setPageSize(3000); // 允许单页最大导出3000条数据
					order_page.setOrderBy(",w.real_loan_date");
					wloanTermProjectPlan.setPage(order_page);
					// 还款中的项目还款计划.
					wloanTermProjectPlan.setState(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_STATE_1);
					List<WloanTermProjectPlan> proPlans = wloanTermProjectPlanService.findList(wloanTermProjectPlan);
					new ExportExcel("还款计划【安心投】", WloanTermProjectPlan.class).setDataList(proPlans).write(response, fileName).dispose();
					addMessage(redirectAttributes, "还款计划【安心投】导出成功！");
				} else if (WloanTermProjectPlan.REPAY_PLAN_RADIO_TYPE_2.equals(repayPlanRadioType)) {
					WloanTermProject wloanTermProject = new WloanTermProject();
					wloanTermProject.setProjectProductType(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1); // 标的产品类型，1：安心投类.
					wloanTermProject.setRealLoanDate(DateUtils.parseDate("2018-01-01")); // 大于真实放款日期.
					wloanTermProjectPlan.setWloanTermProject(wloanTermProject);
					Page<WloanTermProjectPlan> order_page = new Page<WloanTermProjectPlan>(request, response);
					order_page.setPageSize(3000); // 允许单页最大导出3000条数据
					order_page.setOrderBy(",w.real_loan_date");
					wloanTermProjectPlan.setPage(order_page);
					List<WloanTermProjectPlan> proPlans = wloanTermProjectPlanService.findList(wloanTermProjectPlan);
					new ExportExcel("还款计划【安心投】", WloanTermProjectPlan.class).setDataList(proPlans).write(response, fileName).dispose();
					addMessage(redirectAttributes, "还款计划【安心投】导出成功！");
				}

				return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/axtProjectPlanList/?repage";
			}
		} catch (Exception e) {
			addMessage(redirectAttributes, "还款计划【安心投】-导出失败！失败信息：" + e.getMessage());
		}
		return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/axtProjectPlanList/?repage";
	}

	/**
	 * 
	 * 方法: projectPlanList <br>
	 * 描述: 供应链还款计划列表-所有项目. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年11月16日 上午10:06:51
	 * 
	 * @param wloanTermProjectPlan
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequiresPermissions("wloanproject:wloanTermProjectPlan:view")
	@RequestMapping(value = "projectPlanList")
	public String projectPlanList(WloanTermProjectPlan wloanTermProjectPlan, HttpServletRequest request, HttpServletResponse response, Model model) {

		// 用于区分还款中的记录和历史还款记录.
		String repayPlanRadioType = wloanTermProjectPlan.getRepayPlanRadioType();
		if (repayPlanRadioType == null || "".equals(repayPlanRadioType)) {
			// wloanTermProjectPlan.setRepaymentDate(DateUtils.parseDate(DateUtils.formatDate(new
			// Date(), "yyyy-MM-dd")));
			WloanTermProject wloanTermProject = new WloanTermProject();
			wloanTermProject.setState(WloanTermProjectService.REPAYMENT); // 标的流转状态-还款中.
			wloanTermProject.setProjectProductType(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2); // 标的产品类型-供应链标的.
			if (wloanTermProjectPlan.getWloanTermProject() != null) {
				if (wloanTermProjectPlan.getWloanTermProject().getReplaceRepayId() != null && !wloanTermProjectPlan.getWloanTermProject().getReplaceRepayId().equals("")) {
					wloanTermProject.setReplaceRepayId(wloanTermProjectPlan.getWloanTermProject().getReplaceRepayId());
				}
			}
			wloanTermProjectPlan.setWloanTermProject(wloanTermProject);
			Page<WloanTermProjectPlan> order_page = new Page<WloanTermProjectPlan>(request, response);
			order_page.setOrderBy(",w.real_loan_date");
			// 还款中的项目还款计划.
			wloanTermProjectPlan.setState(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_STATE_1);
			Page<WloanTermProjectPlan> page = wloanTermProjectPlanService.findPage(order_page, wloanTermProjectPlan);
			model.addAttribute("page", page);
		} else if (repayPlanRadioType.equals(WloanTermProjectPlan.REPAY_PLAN_RADIO_TYPE_1)) {
			// wloanTermProjectPlan.setRepaymentDate(DateUtils.parseDate(DateUtils.formatDate(new
			// Date(), "yyyy-MM-dd")));
			WloanTermProject wloanTermProject = new WloanTermProject();
			wloanTermProject.setState(WloanTermProjectService.REPAYMENT); // 标的流转状态-还款中.
			wloanTermProject.setProjectProductType(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2); // 标的产品类型-供应链标的.
			if (wloanTermProjectPlan.getWloanTermProject() != null) {
				if (wloanTermProjectPlan.getWloanTermProject().getReplaceRepayId() != null && !wloanTermProjectPlan.getWloanTermProject().getReplaceRepayId().equals("")) {
					wloanTermProject.setReplaceRepayId(wloanTermProjectPlan.getWloanTermProject().getReplaceRepayId());
				}
			}
			wloanTermProjectPlan.setWloanTermProject(wloanTermProject);
			Page<WloanTermProjectPlan> order_page = new Page<WloanTermProjectPlan>(request, response);
			order_page.setOrderBy(",w.real_loan_date");
			// 还款中的项目还款计划.
			wloanTermProjectPlan.setState(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_STATE_1);
			Page<WloanTermProjectPlan> page = wloanTermProjectPlanService.findPage(order_page, wloanTermProjectPlan);
			model.addAttribute("page", page);
		} else if (repayPlanRadioType.equals(WloanTermProjectPlan.REPAY_PLAN_RADIO_TYPE_2)) {
			// wloanTermProjectPlan.setRepaymentDate(DateUtils.parseDate(DateUtils.formatDate(new
			// Date(), "yyyy-MM-dd")));
			WloanTermProject wloanTermProject = new WloanTermProject();
			wloanTermProject.setProjectProductType(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2); // 标的产品类型-供应链标的.
			if (wloanTermProjectPlan.getWloanTermProject() != null) {
				if (wloanTermProjectPlan.getWloanTermProject().getReplaceRepayId() != null && !wloanTermProjectPlan.getWloanTermProject().getReplaceRepayId().equals("")) {
					wloanTermProject.setReplaceRepayId(wloanTermProjectPlan.getWloanTermProject().getReplaceRepayId());
				}
			}
			wloanTermProjectPlan.setWloanTermProject(wloanTermProject);
			Page<WloanTermProjectPlan> order_page = new Page<WloanTermProjectPlan>(request, response);
			order_page.setOrderBy(",w.real_loan_date");
			Page<WloanTermProjectPlan> page = wloanTermProjectPlanService.findPage(order_page, wloanTermProjectPlan);
			model.addAttribute("page", page);

			CreditUserInfo userInfo = new CreditUserInfo();
			userInfo.setCreditUserType("11");
			List<CreditUserInfo> middlemenList = creditUserInfoService.findList(userInfo);
			model.addAttribute("middlemenList", middlemenList);

			return "modules/regular/repayplan/ubProjectAllPlanList";
		}

		CreditUserInfo userInfo = new CreditUserInfo();
		userInfo.setCreditUserType("11");
		List<CreditUserInfo> middlemenList = creditUserInfoService.findList(userInfo);
		model.addAttribute("middlemenList", middlemenList);

		return "modules/regular/repayplan/ubProjectPlanList";
	}

	/**
	 * 
	 * 方法: axtProjectPlanList <br>
	 * 描述: 安心投还款计划列表-所有项目. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2018年1月16日 上午10:00:35
	 * 
	 * @param wloanTermProjectPlan
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequiresPermissions("wloanproject:wloanTermProjectPlan:view")
	@RequestMapping(value = "axtProjectPlanList")
	public String axtProjectPlanList(WloanTermProjectPlan wloanTermProjectPlan, HttpServletRequest request, HttpServletResponse response, Model model) {

		// 用于区分还款中的记录和历史还款记录.
		String repayPlanRadioType = wloanTermProjectPlan.getRepayPlanRadioType();
		if (repayPlanRadioType == null || "".equals(repayPlanRadioType)) {
			// wloanTermProjectPlan.setRepaymentDate(DateUtils.parseDate(DateUtils.formatDate(new
			// Date(), "yyyy-MM-dd")));
			WloanTermProject wloanTermProject = new WloanTermProject();
			wloanTermProject.setState(WloanTermProjectService.REPAYMENT); // 标的流转状态-还款中.
			wloanTermProject.setProjectProductType(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1); // 标的产品类型，1：安心投类.
			wloanTermProject.setRealLoanDate(DateUtils.parseDate("2018-01-01")); // 大于真实放款日期.
			wloanTermProjectPlan.setWloanTermProject(wloanTermProject);
			Page<WloanTermProjectPlan> order_page = new Page<WloanTermProjectPlan>(request, response);
			order_page.setOrderBy(",w.real_loan_date");
			// 还款中的项目还款计划.
			wloanTermProjectPlan.setState(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_STATE_1);
			Page<WloanTermProjectPlan> page = wloanTermProjectPlanService.findPage(order_page, wloanTermProjectPlan);
			model.addAttribute("page", page);
		} else if (WloanTermProjectPlan.REPAY_PLAN_RADIO_TYPE_1.equals(repayPlanRadioType)) {
			// wloanTermProjectPlan.setRepaymentDate(DateUtils.parseDate(DateUtils.formatDate(new
			// Date(), "yyyy-MM-dd")));
			WloanTermProject wloanTermProject = new WloanTermProject();
			wloanTermProject.setState(WloanTermProjectService.REPAYMENT); // 标的流转状态-还款中.
			wloanTermProject.setProjectProductType(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1); // 标的产品类型，1：安心投类.
			wloanTermProject.setRealLoanDate(DateUtils.parseDate("2018-01-01")); // 大于真实放款日期.
			wloanTermProjectPlan.setWloanTermProject(wloanTermProject);
			Page<WloanTermProjectPlan> order_page = new Page<WloanTermProjectPlan>(request, response);
			order_page.setOrderBy(",w.real_loan_date");
			// 还款中的项目还款计划.
			wloanTermProjectPlan.setState(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_STATE_1);
			Page<WloanTermProjectPlan> page = wloanTermProjectPlanService.findPage(order_page, wloanTermProjectPlan);
			model.addAttribute("page", page);
		} else if (WloanTermProjectPlan.REPAY_PLAN_RADIO_TYPE_2.equals(repayPlanRadioType)) {
			// wloanTermProjectPlan.setRepaymentDate(DateUtils.parseDate(DateUtils.formatDate(new
			// Date(), "yyyy-MM-dd")));
			WloanTermProject wloanTermProject = new WloanTermProject();
			wloanTermProject.setProjectProductType(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1); // 标的产品类型，1：安心投类.
			wloanTermProject.setRealLoanDate(DateUtils.parseDate("2018-01-01")); // 大于真实放款日期.
			wloanTermProjectPlan.setWloanTermProject(wloanTermProject);
			Page<WloanTermProjectPlan> order_page = new Page<WloanTermProjectPlan>(request, response);
			order_page.setOrderBy(",w.real_loan_date");
			Page<WloanTermProjectPlan> page = wloanTermProjectPlanService.findPage(order_page, wloanTermProjectPlan);
			model.addAttribute("page", page);
			return "modules/regular/repayplan/ubAxtProjectAllPlanList";
		}

		return "modules/regular/repayplan/ubAxtProjectPlanList";
	}

	@RequiresPermissions("wloanproject:wloanTermProjectPlan:view")
	@RequestMapping(value = "form")
	public String form(WloanTermProjectPlan wloanTermProjectPlan, Model model) {

		model.addAttribute("wloanTermProjectPlan", wloanTermProjectPlan);
		return "modules/regular/repayplan/wloanTermProjectPlanForm";
	}

	@RequiresPermissions("wloanproject:wloanTermProjectPlan:edit")
	@RequestMapping(value = "save")
	public String save(WloanTermProjectPlan wloanTermProjectPlan, Model model, RedirectAttributes redirectAttributes) {

		if (!beanValidator(model, wloanTermProjectPlan)) {
			return form(wloanTermProjectPlan, model);
		}
		wloanTermProjectPlanService.save(wloanTermProjectPlan);
		addMessage(redirectAttributes, "保存定期项目信息成功");
		return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/?repage";
	}

	@RequiresPermissions("wloanproject:WloanTermProjectPlan:edit")
	@RequestMapping(value = "delete")
	public String delete(WloanTermProjectPlan wloanTermProjectPlan, RedirectAttributes redirectAttributes) {

		wloanTermProjectPlanService.delete(wloanTermProjectPlan);
		addMessage(redirectAttributes, "删除定期项目信息成功");
		return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/?repage";
	}

	/**
	 * 
	 * 方法: userPlanListByProPlanId <br>
	 * 描述: 项目还款计划-客户还款计划列表. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年11月16日 上午10:11:26
	 * 
	 * @param projectPlanId
	 * @param proid
	 * @param viewType
	 * @param model
	 * @return
	 */
	@RequiresPermissions("wloanproject:wloanTermProjectPlan:view")
	@RequestMapping(value = "userPlanListByProPlanId")
	public String userPlanListByProPlanId(@RequestParam(required = false) String projectPlanId, String proid, String viewType, Model model) {

		try {
			WloanTermProjectPlan wloanTermProjectPlan = wloanTermProjectPlanService.get(projectPlanId);
			WloanTermProject wloanTermProject = wloanTermProjectService.get(wloanTermProjectPlan.getWloanTermProject().getId());
			WloanTermUserPlan entity = new WloanTermUserPlan();
			entity.setWloanTermProject(wloanTermProject);
			entity.setRepaymentDate(wloanTermProjectPlan.getRepaymentDate());
			List<WloanTermUserPlan> wloanTermUserPlanList = wloanTermUserPlanService.findList(entity);
			model.addAttribute("proid", wloanTermProject.getId());
			model.addAttribute("viewType", viewType);
			model.addAttribute("wloanTermProject", wloanTermProject);
			model.addAttribute("wloanTermProjectPlan", wloanTermProjectPlan);
			model.addAttribute("repaymentDate", DateUtils.getDate(DateUtils.formatDateTime(wloanTermProjectPlan.getRepaymentDate())));
			model.addAttribute("wloanTermUserPlanList", wloanTermUserPlanList);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "modules/regular/repayplan/ubUserPlanList";
	}

	@RequiresPermissions("wloanproject:wloanTermProjectPlan:view")
	@RequestMapping(value = "expireUserPlanList")
	public String expireUserPlanList(WloanTermUserPlan wloanTermUserPlan, Model model) {

		try {
			List<String> userIds = wloanTermUserPlanDao.findInvUserIdByUserPlans();
			logger.info("未结清用户列表:{}", userIds != null ? userIds.size() : 0);

			int dayI = 15;
			if (null != wloanTermUserPlan.getDayI()) {
				dayI = wloanTermUserPlan.getDayI();
			}

			String expireDate = DateUtils.formatDate(DateUtils.getSpecifiedMonthAfter(new Date(), dayI), "yyyy-MM-dd");
			logger.info("还款结清时间:{}", expireDate);

			List<WloanTermUserPlan> list = new ArrayList<WloanTermUserPlan>();
			WloanTermUserPlan queryUserPlan = null;
			for (String userId : userIds) {
				queryUserPlan = new WloanTermUserPlan();
				queryUserPlan.setUserId(userId);
				List<WloanTermUserPlan> invUserPlans = wloanTermUserPlanDao.findInvUserPlanByUserId(queryUserPlan);
				if (invUserPlans != null && invUserPlans.size() > 0) {
					WloanTermUserPlan userPlan = invUserPlans.get(0);
					if (DateUtils.compare_date_T(DateUtils.formatDate(userPlan.getRepaymentDate(), "yyyy-MM-dd"), expireDate)) {
						list.add(userPlan);
					} else {
						logger.info("该客户正常还款......");
					}
				}
			}

			Collections.sort(list, new Comparator<WloanTermUserPlan>() {

				@Override
				public int compare(WloanTermUserPlan o1, WloanTermUserPlan o2) {

					if (o1.getRepaymentDate().before(o2.getRepaymentDate())) {
						return -1;
					}
					if (o1.getRepaymentDate() == o1.getRepaymentDate()) {
						return 0;
					}
					return 1;
				}
			});

			model.addAttribute("wloanTermUserPlanList", list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "modules/regular/repayplan/expireUserPlanList";
	}

	/**
	 * 
	 * 方法: findUserPlanListByProPlanId <br>
	 * 描述: 查找供应链客户还款计划列表，根据项目还款计划ID. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年11月20日 上午11:39:16
	 * 
	 * @param projectPlanId
	 * @param proid
	 * @param viewType
	 * @param model
	 * @return
	 */
	@RequiresPermissions("wloanproject:wloanTermProjectPlan:view")
	@RequestMapping(value = "findUserPlanListByProPlanId")
	public String findUserPlanListByProPlanId(@RequestParam(required = false) String projectPlanId, String proid, Model model) {

		try {
			WloanTermProjectPlan wloanTermProjectPlan = wloanTermProjectPlanService.get(projectPlanId);
			WloanTermProject wloanTermProject = wloanTermProjectService.get(wloanTermProjectPlan.getWloanTermProject().getId());
			WloanTermUserPlan entity = new WloanTermUserPlan();
			entity.setWloanTermProject(wloanTermProject);
			entity.setRepaymentDate(wloanTermProjectPlan.getRepaymentDate());
			List<WloanTermUserPlan> wloanTermUserPlanList = wloanTermUserPlanService.findList(entity);
			for (int i = 0; i < wloanTermUserPlanList.size(); i++) {
				WloanTermUserPlan userPlanentity = wloanTermUserPlanList.get(i);
				UserInfo userInfo = userPlanentity.getUserInfo();
				if (userInfo != null) {
					userInfo.setName(CommonStringUtils.mobileEncrypt(userInfo.getName()));
					userInfo.setRealName(CommonStringUtils.replaceNameX(userInfo.getRealName()));
				}
			}
			model.addAttribute("proid", wloanTermProject.getId());
			if (DateUtils.compare_date(DateUtils.getDate(wloanTermProjectPlan.getRepaymentDate(), "yyyy-MM-dd HH:mm:ss"), DateUtils.getDate(new Date(), "yyyy-MM-dd HH:mm:ss"))) {
				model.addAttribute("viewType", "-1"); // 还款日还款.
			} else {
				model.addAttribute("viewType", "1"); // 该项目未到还款日.
			}
			model.addAttribute("wloanTermProject", wloanTermProject);
			model.addAttribute("wloanTermProjectPlan", wloanTermProjectPlan);
			model.addAttribute("repaymentDate", DateUtils.getDate(DateUtils.formatDateTime(wloanTermProjectPlan.getRepaymentDate())));
			model.addAttribute("wloanTermUserPlanList", wloanTermUserPlanList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "modules/regular/repayplan/ubUserPlanList";
	}

	/**
	 * 
	 * 方法: findAxtUserPlanListByProPlanId <br>
	 * 描述: 查找安心投客户还款计划列表，根据项目还款计划ID. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2018年1月16日 上午10:12:27
	 * 
	 * @param projectPlanId
	 * @param proid
	 * @param model
	 * @return
	 */
	@RequiresPermissions("wloanproject:wloanTermProjectPlan:view")
	@RequestMapping(value = "findAxtUserPlanListByProPlanId")
	public String findAxtUserPlanListByProPlanId(@RequestParam(required = false) String projectPlanId, String proid, Model model) {

		try {
			WloanTermProjectPlan wloanTermProjectPlan = wloanTermProjectPlanService.get(projectPlanId);
			WloanTermProject wloanTermProject = wloanTermProjectService.get(wloanTermProjectPlan.getWloanTermProject().getId());
			WloanTermUserPlan entity = new WloanTermUserPlan();
			entity.setWloanTermProject(wloanTermProject);
			entity.setRepaymentDate(wloanTermProjectPlan.getRepaymentDate());
			List<WloanTermUserPlan> wloanTermUserPlanList = wloanTermUserPlanService.findList(entity);
			for (int i = 0; i < wloanTermUserPlanList.size(); i++) {
				WloanTermUserPlan userPlanentity = wloanTermUserPlanList.get(i);
				UserInfo userInfo = userPlanentity.getUserInfo();
				if (userInfo != null) {
					userInfo.setName(CommonStringUtils.mobileEncrypt(userInfo.getName()));
					userInfo.setRealName(CommonStringUtils.replaceNameX(userInfo.getRealName()));
				}
			}
			model.addAttribute("proid", wloanTermProject.getId());
			if (DateUtils.compare_date(DateUtils.getDate(wloanTermProjectPlan.getRepaymentDate(), "yyyy-MM-dd HH:mm:ss"), DateUtils.getDate(new Date(), "yyyy-MM-dd HH:mm:ss"))) {
				model.addAttribute("viewType", "-1"); // 还款日还款.
			} else {
				model.addAttribute("viewType", "1"); // 该项目未到还款日.
			}
			model.addAttribute("wloanTermProject", wloanTermProject);
			model.addAttribute("wloanTermProjectPlan", wloanTermProjectPlan);
			model.addAttribute("repaymentDate", DateUtils.getDate(DateUtils.formatDateTime(wloanTermProjectPlan.getRepaymentDate())));
			model.addAttribute("wloanTermUserPlanList", wloanTermUserPlanList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "modules/regular/repayplan/ubAxtUserPlanList";
	}

	@RequiresPermissions("wloanproject:wloanTermProjectPlan:view")
	@RequestMapping(value = "fromWloanTermProjectPlanDetail")
	public String fromWloanTermProjectPlanDetail(@RequestParam(required = false) String projectPlanId, String proid, String viewType, Model model) {

		try {

			WloanTermProjectPlan wloanTermProjectPlan = wloanTermProjectPlanService.get(projectPlanId);

			WloanTermProject wloanTermProject = wloanTermProjectService.get(wloanTermProjectPlan.getWloanTermProject().getId());
			WloanTermUserPlan entity = new WloanTermUserPlan();
			entity.setWloanTermProject(wloanTermProject);
			entity.setRepaymentDate(wloanTermProjectPlan.getRepaymentDate());
			List<WloanTermUserPlan> wloanTermUserPlanList = wloanTermUserPlanService.findList(entity);
			for (int i = 0; i < wloanTermUserPlanList.size(); i++) {
				WloanTermUserPlan userPlanEntity = wloanTermUserPlanList.get(i);
				UserInfo userInfo = userPlanEntity.getUserInfo();
				if (userInfo != null) {
					userInfo.setName(CommonStringUtils.mobileEncrypt(userInfo.getName()));
					userInfo.setRealName(CommonStringUtils.replaceNameX(userInfo.getRealName()));
				}
			}

			/*
			 * //实际还款金额
			 * entity.setState("2");
			 * Double currentTotal =
			 * wloanTermUserPlanService.findCurrentTotal(entity);
			 * model.addAttribute("currentTotal", currentTotal);
			 */
			model.addAttribute("proid", wloanTermProject.getId());
			model.addAttribute("viewType", viewType);
			model.addAttribute("wloanTermProject", wloanTermProject);
			model.addAttribute("wloanTermProjectPlan", wloanTermProjectPlan);
			model.addAttribute("repaymentDate", DateUtils.getDate(DateUtils.formatDateTime(wloanTermProjectPlan.getRepaymentDate())));
			model.addAttribute("wloanTermUserPlanList", wloanTermUserPlanList);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "modules/regular/repayplan/wloanOneUserPlanDetailList";
	}

	@RequiresPermissions("wloanproject:wloanTermProjectPlan:edit")
	@RequestMapping(value = "returnWloanTermProjectPlan")
	synchronized public String returnWloanTermProjectPlan(Model model, Date repaymentDate, String ip, String projectPlanId, String proid, RedirectAttributes redirectAttributes) {

		try {
			String message = "还款成功";
			String state = "2";
			WloanTermProjectPlan wloanTermProjectPlan = wloanTermProjectPlanService.get(projectPlanId);
			boolean check = wloanTermUserPlanService.repaymentWloanTermUserPlan(wloanTermProjectPlan.getWloanTermProject().getId(), ip, repaymentDate);
			if (!check) {
				message = "还款失败";
				state = "3";
			} /*
			 * else{
			 * WloanTermProject wloanTermProject =
			 * wloanTermProjectService.get(wloanTermProjectPlan
			 * .getWloanTermProject().getId());
			 * WloanTermUserPlan entity = new WloanTermUserPlan();
			 * entity.setWloanTermProject(wloanTermProject);
			 * entity.setRepaymentDate(repaymentDate);
			 * List<WloanTermUserPlan> wloanTermUserPlanList =
			 * wloanTermUserPlanService.findList(entity);
			 * 
			 * model.addAttribute("proid", proid);
			 * model.addAttribute("viewType", 2);
			 * model.addAttribute("wloanTermProject", wloanTermProject);
			 * model.addAttribute("wloanTermProjectPlan", wloanTermProjectPlan);
			 * model.addAttribute("repaymentDate",
			 * DateUtils.getDate(DateUtils.formatDateTime(repaymentDate)));
			 * model.addAttribute("wloanTermUserPlanList",
			 * wloanTermUserPlanList);
			 * }
			 */
			else {
				wloanTermProjectPlanService.updateWloanTermProjectPlanState(projectPlanId, state);
			}
			addMessage(redirectAttributes, message);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// return "modules/regular/repayplan/wloanOneUserPlanDetailList";
		return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/proid?proid=" + proid;
	}

	/**
	 * 
	 * 方法: exportRepayPlanInfo <br>
	 * 描述: 导出还款计划【还款成功】. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年6月30日 下午3:23:19
	 * 
	 * @param wloanTermUserPlan
	 * @param projectPlanId
	 * @param request
	 * @param response
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("wloanproject:wloanTermProjectPlan:view")
	@RequestMapping(value = "exportRepayPlanInfo", method = RequestMethod.POST)
	public String exportRepayPlanInfo(WloanTermUserPlan wloanTermUserPlan, @RequestParam(required = false) String projectPlanId, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {

		try {
			WloanTermProjectPlan wloanTermProjectPlan = wloanTermProjectPlanService.get(projectPlanId);
			WloanTermProject wloanTermProject = wloanTermProjectService.get(wloanTermProjectPlan.getWloanTermProject().getId());
			wloanTermUserPlan.setWloanTermProject(wloanTermProject);
			wloanTermUserPlan.setRepaymentDate(wloanTermProjectPlan.getRepaymentDate());
			List<WloanTermUserPlan> list = wloanTermUserPlanService.findList(wloanTermUserPlan);
			String fileName = "客户还款计划" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
			new ExportExcel("客户还款计划", WloanTermUserPlan.class).setDataList(list).write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "Excel-导出失败！失败信息：" + e.getMessage());
		}

		return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/fromWloanTermProjectPlanDetail?projectPlanId=" + projectPlanId;
	}

	/**
	 * 
	 * 方法: exportRepayPlanInfo <br>
	 * 描述: 导出供应链全部项目. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2018年1月19日 下午4:54:24
	 * 
	 * @param wloanTermProjectPlan
	 * @param request
	 * @param response
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("wloanproject:wloanTermProjectPlan:view")
	@RequestMapping(value = "export", method = RequestMethod.POST)
	public String export(WloanTermProjectPlan wloanTermProjectPlan, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {

		try {

			String fileName = "还款计划【供应链】" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";

			// 用于区分还款中的记录和历史还款记录.
			String repayPlanRadioType = wloanTermProjectPlan.getRepayPlanRadioType();
			if (repayPlanRadioType == null || "".equals(repayPlanRadioType)) {
				// 开始时间 = 当前时间.
				// wloanTermProjectPlan.setRepaymentDate(DateUtils.parseDate(DateUtils.formatDate(new
				// Date(), "yyyy-MM-dd")));
				WloanTermProject wloanTermProject = new WloanTermProject();
				wloanTermProject.setState(WloanTermProjectService.REPAYMENT); // 标的流转状态-还款中.
				wloanTermProject.setProjectProductType(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2); // 标的产品类型-供应链标的.
				if (wloanTermProjectPlan.getWloanTermProject() != null) {
					if (wloanTermProjectPlan.getWloanTermProject().getReplaceRepayId() != null && !wloanTermProjectPlan.getWloanTermProject().getReplaceRepayId().equals("")) {
						wloanTermProject.setReplaceRepayId(wloanTermProjectPlan.getWloanTermProject().getReplaceRepayId());
					}
				}
				wloanTermProjectPlan.setWloanTermProject(wloanTermProject);
				// 还款中的项目还款计划.
				wloanTermProjectPlan.setState(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_STATE_1);
				Page<WloanTermProjectPlan> order_page = new Page<WloanTermProjectPlan>();
				order_page.setOrderBy(", a.project_id");
				wloanTermProjectPlan.setPage(order_page);
				List<WloanTermProjectPlan> proPlans = wloanTermProjectPlanService.findList(wloanTermProjectPlan);
				// 重新封装数据.
				List<WloanTermProjectPlanPoJo> pojoList = new ArrayList<WloanTermProjectPlanPoJo>();
				for (WloanTermProjectPlan projectPlan : proPlans) {
					WloanTermProjectPlanPoJo projectPlanPoJo = new WloanTermProjectPlanPoJo();
					projectPlanPoJo.setWloanSubject(projectPlan.getWloanSubject());
					projectPlanPoJo.setWloanTermProject(projectPlan.getWloanTermProject());
					projectPlanPoJo.setPrincipal(projectPlan.getPrincipal()); // 还款类型.
					projectPlanPoJo.setInterest(projectPlan.getInterest()); // 还款金额.
					projectPlanPoJo.setRepaymentDate(projectPlan.getRepaymentDate()); // 还款日期.
					projectPlanPoJo.setState(projectPlan.getState()); // 还款状态.
					pojoList.add(projectPlanPoJo);
				}
				new ExportExcel("还款计划【供应链】", WloanTermProjectPlanPoJo.class).setDataList(pojoList).write(response, fileName).dispose();
				return null;
			} else if (repayPlanRadioType.equals(WloanTermProjectPlan.REPAY_PLAN_RADIO_TYPE_1)) {
				// 开始时间 = 当前时间.
				// wloanTermProjectPlan.setRepaymentDate(DateUtils.parseDate(DateUtils.formatDate(new
				// Date(), "yyyy-MM-dd")));
				WloanTermProject wloanTermProject = new WloanTermProject();
				wloanTermProject.setState(WloanTermProjectService.REPAYMENT); // 标的流转状态-还款中.
				wloanTermProject.setProjectProductType(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2); // 标的产品类型-供应链标的.
				if (wloanTermProjectPlan.getWloanTermProject() != null) {
					if (wloanTermProjectPlan.getWloanTermProject().getReplaceRepayId() != null && !wloanTermProjectPlan.getWloanTermProject().getReplaceRepayId().equals("")) {
						wloanTermProject.setReplaceRepayId(wloanTermProjectPlan.getWloanTermProject().getReplaceRepayId());
					}
				}
				wloanTermProjectPlan.setWloanTermProject(wloanTermProject);
				// 还款中的项目还款计划.
				wloanTermProjectPlan.setState(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_STATE_1);
				Page<WloanTermProjectPlan> order_page = new Page<WloanTermProjectPlan>();
				order_page.setOrderBy(", a.project_id");
				wloanTermProjectPlan.setPage(order_page);
				List<WloanTermProjectPlan> proPlans = wloanTermProjectPlanService.findList(wloanTermProjectPlan);
				// 重新封装数据.
				List<WloanTermProjectPlanPoJo> pojoList = new ArrayList<WloanTermProjectPlanPoJo>();
				for (WloanTermProjectPlan projectPlan : proPlans) {
					WloanTermProjectPlanPoJo projectPlanPoJo = new WloanTermProjectPlanPoJo();
					projectPlanPoJo.setWloanSubject(projectPlan.getWloanSubject());
					projectPlanPoJo.setWloanTermProject(projectPlan.getWloanTermProject());
					projectPlanPoJo.setPrincipal(projectPlan.getPrincipal()); // 还款类型.
					projectPlanPoJo.setInterest(projectPlan.getInterest()); // 还款金额.
					projectPlanPoJo.setRepaymentDate(projectPlan.getRepaymentDate()); // 还款日期.
					projectPlanPoJo.setState(projectPlan.getState()); // 还款状态.
					pojoList.add(projectPlanPoJo);
				}
				new ExportExcel("还款计划【供应链】", WloanTermProjectPlanPoJo.class).setDataList(pojoList).write(response, fileName).dispose();
				return null;
			} else if (repayPlanRadioType.equals(WloanTermProjectPlan.REPAY_PLAN_RADIO_TYPE_2)) {
				// 开始时间 = 当前时间.
				// wloanTermProjectPlan.setRepaymentDate(DateUtils.parseDate(DateUtils.formatDate(new
				// Date(), "yyyy-MM-dd")));
				WloanTermProject wloanTermProject = new WloanTermProject();
				wloanTermProject.setProjectProductType(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2); // 标的产品类型-供应链标的.
				if (wloanTermProjectPlan.getWloanTermProject() != null) {
					if (wloanTermProjectPlan.getWloanTermProject().getReplaceRepayId() != null && !wloanTermProjectPlan.getWloanTermProject().getReplaceRepayId().equals("")) {
						wloanTermProject.setReplaceRepayId(wloanTermProjectPlan.getWloanTermProject().getReplaceRepayId());
					}
				}
				wloanTermProjectPlan.setWloanTermProject(wloanTermProject);
				Page<WloanTermProjectPlan> order_page = new Page<WloanTermProjectPlan>();
				order_page.setOrderBy(", a.project_id");
				wloanTermProjectPlan.setPage(order_page);
				List<WloanTermProjectPlan> proPlans = wloanTermProjectPlanService.findList(wloanTermProjectPlan);
				// 重新封装数据.
				List<WloanTermProjectPlanPoJo> pojoList = new ArrayList<WloanTermProjectPlanPoJo>();
				for (WloanTermProjectPlan projectPlan : proPlans) {
					WloanTermProjectPlanPoJo projectPlanPoJo = new WloanTermProjectPlanPoJo();
					projectPlanPoJo.setWloanSubject(projectPlan.getWloanSubject());
					projectPlanPoJo.setWloanTermProject(projectPlan.getWloanTermProject());
					projectPlanPoJo.setPrincipal(projectPlan.getPrincipal()); // 还款类型.
					projectPlanPoJo.setInterest(projectPlan.getInterest()); // 还款金额.
					projectPlanPoJo.setRepaymentDate(projectPlan.getRepaymentDate()); // 还款日期.
					projectPlanPoJo.setState(projectPlan.getState()); // 还款状态.
					pojoList.add(projectPlanPoJo);
				}
				new ExportExcel("还款计划【供应链】", WloanTermProjectPlanPoJo.class).setDataList(pojoList).write(response, fileName).dispose();
				return null;
			}
		} catch (Exception e) {
			addMessage(redirectAttributes, "还款计划【供应链】-导出失败！失败信息：" + e.getMessage());
		}
		addMessage(redirectAttributes, "还款计划【供应链】-导出成功！");
		return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/?repage";
	}

	/**
	 * 导出并发送邮件给供应商
	 * 
	 * @param wloanTermProjectPlan
	 * @param request
	 * @param response
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("wloanproject:wloanTermProjectPlan:view")
	@RequestMapping(value = "sendEmail", method = RequestMethod.POST)
	public String sendEmail(WloanTermProjectPlan wloanTermProjectPlan, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {

		try {
			String fileName = "还款计划【供应链】" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
			// String path = "D:/pdf/";
			String path = "/data/upload/paymentEmail/";
			String coreId = null;// 供应商id
			String coreEmail = null;// 供应商email
			String beginRepaymentDate = DateUtils.formatDate(wloanTermProjectPlan.getBeginRepaymentDate(), "yyyy年MM月dd日");
			String endRepaymentDate = DateUtils.formatDate(wloanTermProjectPlan.getEndRepaymentDate(), "yyyy年MM月dd日");

			// 开始时间 = 当前时间.
			wloanTermProjectPlan.setRepaymentDate(DateUtils.parseDate(DateUtils.formatDate(new Date(), "yyyy-MM-dd")));
			WloanTermProject wloanTermProject = new WloanTermProject();
			wloanTermProject.setState(WloanTermProjectService.REPAYMENT); // 标的流转状态-还款中.
			wloanTermProject.setProjectProductType(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2); // 标的产品类型-供应链标的.
			if (wloanTermProjectPlan.getWloanTermProject() != null) {
				if (wloanTermProjectPlan.getWloanTermProject().getReplaceRepayId() != null && !wloanTermProjectPlan.getWloanTermProject().getReplaceRepayId().equals("")) {
					wloanTermProject.setReplaceRepayId(wloanTermProjectPlan.getWloanTermProject().getReplaceRepayId());
					coreId = wloanTermProjectPlan.getWloanTermProject().getReplaceRepayId();
				}
			}
			wloanTermProjectPlan.setWloanTermProject(wloanTermProject);
			Page<WloanTermProjectPlan> order_page = new Page<WloanTermProjectPlan>();
			order_page.setOrderBy(", a.project_id");
			wloanTermProjectPlan.setPage(order_page);
			List<WloanTermProjectPlan> proPlans = wloanTermProjectPlanService.findList(wloanTermProjectPlan);
			new ExportExcel("还款计划【供应链】", WloanTermProjectPlanPoJo.class).setDataList(proPlans).writeFile(path + fileName).dispose();
			if (coreId != null) {
				WloanSubject wloanSubject = new WloanSubject();
				wloanSubject.setLoanApplyId(coreId);
				List<WloanSubject> list = wloanSubjectService.findList(wloanSubject);
				if (list != null && list.size() > 0) {
					coreEmail = list.get(0).getEmail();
					if (coreEmail != null) {
						String toMailAddr = coreEmail;
						// String toMailAddr = SendMailUtil.toMailAddrCore;
						String cc = SendMailUtil.toMailAddrCCCore;
						String subject = "中投摩根还款账单";
						String message = "尊敬的借款人：附件为您" + beginRepaymentDate + "到" + endRepaymentDate + "的还款账单。请知悉。";

						SendMailUtil.sendWithMsgAndAttachmentToCore(toMailAddr, cc, subject, message, path + fileName, fileName);

					}
				}

			}

			// return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "还款计划邮件-发送成功！失败信息：" + e.getMessage());
		}
		addMessage(redirectAttributes, "还款计划邮件-发送成功！");
		return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/projectPlanList";
	}

}