package com.power.platform.regular.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
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
import com.power.platform.common.security.shiro.session.SessionUtils;
import com.power.platform.common.utils.CommonStringUtils;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.common.utils.excel.ExportExcel;
import com.power.platform.common.web.BaseController;
import com.power.platform.plan.entity.WloanTermUserPlan;
import com.power.platform.plan.service.WloanTermUserPlanService;
import com.power.platform.regular.dao.WloanTermInvestDao;
import com.power.platform.regular.entity.WloanTermInvest;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.regular.service.WloanTermInvestService;
import com.power.platform.regular.service.WloanTermProjectService;
import com.power.platform.regular.web.pojo.InvestInfoPojo;
import com.power.platform.userinfo.entity.UserInfo;

/**
 * 
 * 类: WloanTermInvestController <br>
 * 描述: 定期融资投资表Controller. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年1月5日 下午2:37:21
 */
@Controller
@RequestMapping(value = "${adminPath}/wloan_term_invest/wloanTermInvest")
public class WloanTermInvestController extends BaseController {

	@Autowired
	private WloanTermInvestService wloanTermInvestService;
	@Autowired
	private WloanTermUserPlanService wloanTermUserPlanService;
	@Autowired
	private WloanTermProjectService wloanTermProjectService;
	@Resource
	private WloanTermInvestDao wloanTermInvestDao;

	@ModelAttribute
	public WloanTermInvest get(@RequestParam(required = false) String id) {

		WloanTermInvest entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = wloanTermInvestService.get(id);
		}
		if (entity == null) {
			entity = new WloanTermInvest();
		}
		entity.setCurrentUser(SessionUtils.getUser());
		return entity;
	}

	/**
	 * 
	 * 方法: findInvestByProId <br>
	 * 描述: 查询项目出借详情. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年8月9日 上午9:43:16
	 * 
	 * @param projectId
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequiresPermissions("wloan_term_invest:wloanTermInvest:view")
	@RequestMapping(value = "findInvestByProId")
	public String findInvestByProId(String projectId, HttpServletRequest request, HttpServletResponse response, Model model) {

		try {
			List<WloanTermInvest> investList = wloanTermInvestDao.findProjectInvestNumbers(projectId);
			for (int i = 0; i < investList.size(); i++) {
				WloanTermInvest entity = investList.get(i);
				UserInfo userInfo = entity.getUserInfo();
				if (userInfo != null) {
					userInfo.setName(CommonStringUtils.mobileEncrypt(userInfo.getName()));
					userInfo.setRealName(CommonStringUtils.replaceNameX(userInfo.getRealName()));
					userInfo.setCertificateNo(CommonStringUtils.idEncrypt(userInfo.getCertificateNo()));
				}
			}
			model.addAttribute("projectId", projectId);
			model.addAttribute("investList", investList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "modules/regular/wloan_term_invest/projectInvestInfoList";
	}

	/**
	 * 
	 * 方法: exportInvestInfo <br>
	 * 描述: 导出出借详情. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年8月22日 下午2:28:47
	 * 
	 * @param projectId
	 * @param request
	 * @param response
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "exportInvestInfo", method = RequestMethod.POST)
	public String exportInvestInfo(String projectId, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {

		String fileName = "ChuJieXiangQing_" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";

		WloanTermProject project = wloanTermProjectService.get(projectId);
		String excelTitle = "出借详情";
		if (project != null) {
			excelTitle = "出借详情_" + project.getName() + "_" + project.getSn();
		}

		try {
			List<WloanTermInvest> investList = wloanTermInvestDao.findProjectInvestNumbers(projectId);
			List<InvestInfoPojo> investInfoPojos = new ArrayList<InvestInfoPojo>();
			for (WloanTermInvest wloanTermInvest : investList) {
				InvestInfoPojo investInfoPojo = new InvestInfoPojo();
				if (wloanTermInvest.getUserInfo() != null) {
					investInfoPojo.setName(wloanTermInvest.getUserInfo().getRealName());
					investInfoPojo.setMobilePhone(wloanTermInvest.getUserInfo().getName());
					investInfoPojo.setIdCardCode(wloanTermInvest.getUserInfo().getCertificateNo());
				}
				investInfoPojo.setInvestAmount(NumberUtils.scaleDoubleStr(wloanTermInvest.getAmount()));
				investInfoPojo.setInvestDateTime(wloanTermInvest.getBeginDate());
				investInfoPojos.add(investInfoPojo);
			}
			new ExportExcel(excelTitle, InvestInfoPojo.class).setDataList(investInfoPojos).write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			addMessage(redirectAttributes, "导出出借详情失败！失败信息：" + e.getMessage());
		}
		return "modules/regular/wloan_term_invest/projectInvestInfoList";
	}

	@RequiresPermissions("wloan_term_invest:wloanTermInvest:view")
	@RequestMapping(value = { "list", "" })
	public String list(WloanTermInvest wloanTermInvest, HttpServletRequest request, HttpServletResponse response, Model model) {

		List<String> stateList = new ArrayList<String>();
		wloanTermInvest.setStateItem(stateList);
		stateList.add("1");
		stateList.add("3");
		stateList.add("9");
		wloanTermInvest.setStateItem(stateList);

		Page<WloanTermInvest> page = wloanTermInvestService.findPage(new Page<WloanTermInvest>(request, response), wloanTermInvest);
		List<WloanTermInvest> list = page.getList();
		for (WloanTermInvest entity : list) {
			// 出借金额.
			entity.setAmount(NumberUtils.scaleDouble(entity.getAmount()));
			// 预期收益.
			entity.setInterest(NumberUtils.scaleDouble(entity.getInterest()));
			UserInfo userInfo = entity.getUserInfo();
			if (userInfo != null) {
				userInfo.setName(CommonStringUtils.mobileEncrypt(userInfo.getName()));
				userInfo.setRealName(CommonStringUtils.replaceNameX(userInfo.getRealName()));
			}
		}

		model.addAttribute("page", page);
		return "modules/regular/wloan_term_invest/wloanTermInvestList";
	}

	/**
	 * 
	 * 方法: addForm <br>
	 * 描述: 新增表单. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年1月5日 上午11:04:26
	 * 
	 * @param wloanTermInvest
	 * @param model
	 * @return
	 */
	@RequiresPermissions("wloan_term_invest:wloanTermInvest:view")
	@RequestMapping(value = "addForm")
	public String addForm(WloanTermInvest wloanTermInvest, Model model) {

		wloanTermInvest.setRemarks("定期投资");
		model.addAttribute("wloanTermInvest", wloanTermInvest);
		return "modules/regular/wloan_term_invest/wloanTermInvestAddForm";
	}

	/**
	 * 
	 * 方法: updateForm <br>
	 * 描述: 修改表单. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年1月5日 上午11:04:37
	 * 
	 * @param wloanTermInvest
	 * @param model
	 * @return
	 */
	@RequiresPermissions("wloan_term_invest:wloanTermInvest:view")
	@RequestMapping(value = "updateForm")
	public String updateForm(WloanTermInvest wloanTermInvest, Model model) {

		model.addAttribute("wloanTermInvest", wloanTermInvest);
		return "modules/regular/wloan_term_invest/wloanTermInvestUpdateForm";
	}

	/**
	 * 
	 * 方法: viewForm <br>
	 * 描述: 展示. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年1月5日 上午11:49:10
	 * 
	 * @param wloanTermInvest
	 * @param model
	 * @return
	 */
	@RequiresPermissions("wloan_term_invest:wloanTermInvest:view")
	@RequestMapping(value = "viewForm")
	public String viewForm(WloanTermInvest wloanTermInvest, Model model) {

		model.addAttribute("wloanTermInvest", wloanTermInvest);
		return "modules/regular/wloan_term_invest/wloanTermInvestViewForm";
	}

	/**
	 * 
	 * 方法: addSave <br>
	 * 描述: 新增保存. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年1月5日 上午11:06:11
	 * 
	 * @param wloanTermInvest
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("wloan_term_invest:wloanTermInvest:edit")
	@RequestMapping(value = "addSave")
	public String addSave(WloanTermInvest wloanTermInvest, Model model, RedirectAttributes redirectAttributes) {

		wloanTermInvest.setCreateDate(new Date());
		wloanTermInvest.preInsert();
		wloanTermInvestService.save(wloanTermInvest);
		try {
			wloanTermUserPlanService.initWloanTermUserPlan(wloanTermInvest);
		} catch (Exception e) {
			e.printStackTrace();
		}
		addMessage(redirectAttributes, "更新投资保存成功");
		return "redirect:" + Global.getAdminPath() + "/wloan_term_invest/wloanTermInvest/?repage";
	}

	/**
	 * 
	 * 方法: updateSave <br>
	 * 描述: 更新保存. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年1月5日 上午11:05:49
	 * 
	 * @param wloanTermInvest
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("wloan_term_invest:wloanTermInvest:edit")
	@RequestMapping(value = "updateSave")
	public String updateSave(WloanTermInvest wloanTermInvest, Model model, RedirectAttributes redirectAttributes) {

		wloanTermInvestService.save(wloanTermInvest);
		addMessage(redirectAttributes, "更新投资保存成功");
		return "redirect:" + Global.getAdminPath() + "/wloan_term_invest/wloanTermInvest/?repage";
	}

	@RequiresPermissions("wloan_term_invest:wloanTermInvest:edit")
	@RequestMapping(value = "delete")
	public String delete(WloanTermInvest wloanTermInvest, RedirectAttributes redirectAttributes) {

		wloanTermInvestService.delete(wloanTermInvest);
		addMessage(redirectAttributes, "删除投资记录成功");
		return "redirect:" + Global.getAdminPath() + "/wloan_term_invest/wloanTermInvest/?repage";
	}

	@RequestMapping(value = "getInvestRepayPlan")
	public String getInvestRepayPlan(WloanTermInvest wloanTermInvest, Model model) {

		WloanTermUserPlan wloanTermUserPlan = new WloanTermUserPlan();
		wloanTermUserPlan.setWloanTermInvest(wloanTermInvest);

		Page<WloanTermUserPlan> page = wloanTermUserPlanService.findPage(new Page<WloanTermUserPlan>(), wloanTermUserPlan);

		List<WloanTermUserPlan> list = page.getList();
		for (int i = 0; i < list.size(); i++) {
			WloanTermUserPlan entity = list.get(i);
			UserInfo userInfo = entity.getUserInfo();
			if (userInfo != null) {
				userInfo.setRealName(CommonStringUtils.replaceNameX(userInfo.getRealName()));
			}
		}
		model.addAttribute("page", page);

		return "/modules/regular/wloan_term_invest/wloanTermUserRepayPlan";
	}

	@RequiresPermissions("wloan_term_invest:wloanTermInvest:view")
	@RequestMapping(value = "export", method = RequestMethod.POST)
	public String exportFile(WloanTermInvest wloanTermInvest, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {

		try {
			String fileName = "投资列表数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
			List<String> stateList = new ArrayList<String>();
			wloanTermInvest.setStateItem(stateList);
			stateList.add("1");
			stateList.add("3");
			stateList.add("9");
			wloanTermInvest.setStateItem(stateList);
			List<WloanTermInvest> list = wloanTermInvestService.findList(wloanTermInvest);
			List<WloanTermInvest> newList = new ArrayList<WloanTermInvest>();
			if (list != null && list.size() > 0) {
				for (WloanTermInvest invest : list) {
					invest.setInterest(NumberUtils.scaleDouble(invest.getInterest()));
					newList.add(invest);
				}
			}
			new ExportExcel("投资列表数据", WloanTermInvest.class).setDataList(newList).write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出投资列表数据失败！失败信息：" + e.getMessage());
		}
		return "redirect:" + Global.getAdminPath() + "/wloan_term_invest/wloanTermInvest/?repage";
	}

}