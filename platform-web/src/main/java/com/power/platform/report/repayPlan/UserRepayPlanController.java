package com.power.platform.report.repayPlan;

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

import com.power.platform.common.persistence.Page;
import com.power.platform.common.utils.CommonStringUtils;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.excel.ExportExcel;
import com.power.platform.common.web.BaseController;
import com.power.platform.plan.dao.WloanTermUserPlanDao;
import com.power.platform.plan.entity.WloanTermUserPlan;
import com.power.platform.plan.service.WloanTermUserPlanService;
import com.power.platform.userinfo.entity.UserInfo;

@Controller
@RequestMapping(value = "${adminPath}/report/userRepayPlan")
public class UserRepayPlanController extends BaseController {

	@Autowired
	private WloanTermUserPlanService wloanTermUserPlanService;
	@Resource
	private WloanTermUserPlanDao wloanTermUserPlanDao;

	@ModelAttribute
	public WloanTermUserPlan get(@RequestParam(required = false) String id) {

		WloanTermUserPlan entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = wloanTermUserPlanService.get(id);
		}
		if (entity == null) {
			entity = new WloanTermUserPlan();
		}
		return entity;
	}

	@RequiresPermissions("report:userRepayPlan:view")
	@RequestMapping(value = { "list", "" })
	public String list(WloanTermUserPlan wloanTermUserPlan, HttpServletRequest request, HttpServletResponse response, Model model) {

		Page<WloanTermUserPlan> page = wloanTermUserPlanService.findDueDatePage(new Page<WloanTermUserPlan>(request, response), wloanTermUserPlan);
		List<WloanTermUserPlan> list = page.getList();
		for (int i = 0; i < list.size(); i++) {
			WloanTermUserPlan entity = list.get(i);
			UserInfo userInfo = entity.getUserInfo();
			if (userInfo != null) {
				userInfo.setName(CommonStringUtils.mobileEncrypt(userInfo.getName()));
				userInfo.setRealName(CommonStringUtils.replaceNameX(userInfo.getRealName()));
			}
		}
		model.addAttribute("page", page);
		return "modules/report/userRepayPlanList";
	}

	@RequiresPermissions("report:userRepayPlan:view")
	@RequestMapping(value = "export", method = RequestMethod.POST)
	public String exportFile(WloanTermUserPlan wloanTermUserPlan, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {

		try {

			String fileName = "客户还款计划" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
			List<WloanTermUserPlan> list = wloanTermUserPlanDao.findDueDateList(wloanTermUserPlan);
			new ExportExcel("客户还款计划", WloanTermUserPlan.class).setDataList(list).write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出失败！失败信息：" + e.getMessage());
			e.printStackTrace();
		}
		return "redirect:" + adminPath + "/report/userRepayPlan/list?repage";
	}

}
