package com.power.platform.report.invest;

import java.util.List;

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
import com.power.platform.common.security.shiro.session.SessionUtils;
import com.power.platform.common.utils.CommonStringUtils;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.excel.ExportExcel;
import com.power.platform.common.web.BaseController;
import com.power.platform.regular.entity.WloanSubject;
import com.power.platform.regular.entity.WloanTermInvest;
import com.power.platform.regular.service.WloanSubjectService;
import com.power.platform.regular.service.WloanTermInvestService;
import com.power.platform.userinfo.entity.UserInfo;

/**
 * 
 * 类: InvestController <br>
 * 描述: 投资汇总表. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年10月12日 下午2:57:28
 */
@Controller
@RequestMapping(value = "${adminPath}/report/invest")
public class InvestController extends BaseController {

	@Autowired
	private WloanTermInvestService wloanTermInvestService;
	@Autowired
	private WloanSubjectService wloanSubjectService;

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

	@RequiresPermissions("report:invest:view")
	@RequestMapping(value = { "list", "" })
	public String list(WloanTermInvest wloanTermInvest, HttpServletRequest request, HttpServletResponse response, Model model) {

		Page<WloanTermInvest> page = wloanTermInvestService.findPage(new Page<WloanTermInvest>(request, response), wloanTermInvest);
		for (WloanTermInvest entity : page.getList()) {
			WloanSubject wloanSubject = wloanSubjectService.get(entity.getWloanTermProject().getSubjectId());
			entity.setWloanSubject(wloanSubject);
			UserInfo userInfo = entity.getUserInfo();
			if (userInfo != null) {
				userInfo.setName(CommonStringUtils.mobileEncrypt(userInfo.getName()));
				userInfo.setRealName(CommonStringUtils.replaceNameX(userInfo.getRealName()));
			}
		}
		model.addAttribute("page", page);
		return "modules/report/investList";
	}

	@RequiresPermissions("report:invest:view")
	@RequestMapping(value = "export", method = RequestMethod.POST)
	public String exportFile(WloanTermInvest wloanTermInvest, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {

		try {
			String fileName = "投资汇总数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
			List<WloanTermInvest> list = wloanTermInvestService.findList(wloanTermInvest);
			for (WloanTermInvest entity : list) {
				WloanSubject wloanSubject = wloanSubjectService.get(entity.getWloanTermProject().getSubjectId());
				entity.setWloanSubject(wloanSubject);
			}
			new ExportExcel("投资汇总数据", WloanTermInvest.class).setDataList(list).write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出投资汇总数据失败！失败信息：" + e.getMessage());
		}
		return "redirect:" + adminPath + "/report/cash/list?repage";
	}

}