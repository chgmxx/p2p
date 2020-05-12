package com.power.platform.report.loanTermProject;

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
import com.power.platform.regular.dao.WloanTermProjectDao;
import com.power.platform.regular.entity.WloanSubject;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.regular.service.WloanTermProjectService;

/**
 * 
 * 类: LoanTermProjectController <br>
 * 描述: 放款记录报表，列表展示及Excel导出功能. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年10月11日 下午1:35:10
 */
@Controller
@RequestMapping(value = "${adminPath}/report/loanTermProject")
public class LoanTermProjectController extends BaseController {

	@Autowired
	private WloanTermProjectService wloanTermProjectService;
	@Resource
	private WloanTermProjectDao wloanTermProjectDao;

	@ModelAttribute
	public WloanTermProject get(@RequestParam(required = false) String id) {

		WloanTermProject entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = wloanTermProjectService.get(id);
		}
		if (entity == null) {
			entity = new WloanTermProject();
		}
		return entity;
	}

	@RequiresPermissions("report:loanTermProject:view")
	@RequestMapping(value = { "list", "" })
	public String list(WloanTermProject wloanTermProject, HttpServletRequest request, HttpServletResponse response, Model model) {

		Page<WloanTermProject> page = wloanTermProjectService.findExcelReportPage(new Page<WloanTermProject>(request, response), wloanTermProject);
		for (WloanTermProject entity : page.getList()) {
			WloanSubject wloanSubject = entity.getWloanSubject();
			if (wloanSubject != null) {
				wloanSubject.setLoanUser(CommonStringUtils.replaceNameX(wloanSubject.getLoanUser()));
			}
		}
		model.addAttribute("page", page);
		return "modules/report/loanTermProjectList";
	}

	@RequiresPermissions("report:loanTermProject:view")
	@RequestMapping(value = "export", method = RequestMethod.POST)
	public String exportFile(WloanTermProject wloanTermProject, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {

		try {
			String fileName = "项目放款数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
//			for (WloanTermProject entity : page.getList()) {
//				entity.setRealLoanAmount(entity.getAmount() - entity.getFeeRate() - entity.getMarginPercentage());
//			}
			List<WloanTermProject> list = wloanTermProjectDao.findExcelReportList(wloanTermProject);
			new ExportExcel("项目放款数据", WloanTermProject.class).setDataList(list).write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出项目放款数据失败！失败信息：" + e.getMessage());
		}
		return "redirect:" + adminPath + "/report/loanTermProject?repage";
	}

}