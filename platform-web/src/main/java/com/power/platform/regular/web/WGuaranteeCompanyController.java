package com.power.platform.regular.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.power.platform.common.persistence.Page;
import com.power.platform.common.security.shiro.session.SessionUtils;
import com.power.platform.common.utils.CommonStringUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.web.BaseController;
import com.power.platform.regular.entity.WGuaranteeCompany;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.regular.service.WGuaranteeCompanyService;
import com.power.platform.regular.service.WloanTermProjectService;
import com.power.platform.sys.entity.AnnexFile;
import com.power.platform.sys.service.AnnexFileService;

/**
 * 
 * class: WGuaranteeCompanyController <br>
 * description: 担保公司Controller <br>
 * author: Mr.Roy <br>
 * date: 2018年12月9日 下午12:02:58
 */
@Controller
@RequestMapping(value = "${adminPath}/pro/wguarantee")
public class WGuaranteeCompanyController extends BaseController {

	@Autowired
	private WGuaranteeCompanyService wGuaranteeCompanyService;

	@Autowired
	private AnnexFileService annexFileService;

	@Autowired
	private WloanTermProjectService wloanTermProjectService;

	@ModelAttribute
	public WGuaranteeCompany get(@RequestParam(required = false) String id) {

		WGuaranteeCompany wGuaranteeCompany = new WGuaranteeCompany();
		if (StringUtils.isNotBlank(id)) {
			wGuaranteeCompany = wGuaranteeCompanyService.get(id);
		}
		wGuaranteeCompany.setCurrentUser(SessionUtils.getUser());
		return wGuaranteeCompany;
	}

	@RequiresPermissions("pro:wguaranteecompany:view")
	@RequestMapping(value = { "list", "" })
	public String list(WGuaranteeCompany wGuaranteeCompany, HttpServletRequest request, HttpServletResponse response, Model model) {

		Page<WGuaranteeCompany> page = wGuaranteeCompanyService.findPage(new Page<WGuaranteeCompany>(request, response), wGuaranteeCompany);
		List<WGuaranteeCompany> list = page.getList();
		for (int i = 0; i < list.size(); i++) {
			WGuaranteeCompany entity = list.get(i);
			entity.setCorporation(CommonStringUtils.replaceNameX(entity.getCorporation()));;
			entity.setPhone(CommonStringUtils.mobileEncrypt(entity.getPhone()));
			entity.setBusinessNo(CommonStringUtils.idEncrypt(entity.getBusinessNo()));
			entity.setOrganNo(CommonStringUtils.idEncrypt(entity.getOrganNo()));
			entity.setTaxCode(CommonStringUtils.idEncrypt(entity.getTaxCode()));
		}
		model.addAttribute("page", page);
		model.addAttribute("wGuaranteeCompany", wGuaranteeCompany);
		return "modules/regular/wGuaranteeCompanyList";
	}

	@RequiresPermissions("pro:wguaranteecompany:view")
	@RequestMapping(value = "form")
	public String form(WGuaranteeCompany wGuaranteeCompany, Model model, HttpServletRequest request) {

		String flag = request.getParameter("flag");
		String form = "wGuaranteeCompanyForm";
		if (StringUtils.isNotBlank(flag) && flag.equals("view")) {
			List<AnnexFile> annexFileList = annexFileService.findAnnexFileMap(wGuaranteeCompany.getId());
			model.addAttribute("annexFileList", annexFileList);
			form = "wGuaranteeCompanyView";
		}
		wGuaranteeCompany.setBriefInfo(StringEscapeUtils.unescapeHtml(wGuaranteeCompany.getBriefInfo()));
		wGuaranteeCompany.setGuaranteeCase(StringEscapeUtils.unescapeHtml(wGuaranteeCompany.getGuaranteeCase()));
		wGuaranteeCompany.setGuaranteeScheme(StringEscapeUtils.unescapeHtml(wGuaranteeCompany.getGuaranteeScheme()));
		model.addAttribute("wGuaranteeCompany", wGuaranteeCompany);
		return "modules/regular/" + form;
	}

	@RequiresPermissions("pro:wguaranteecompany:edit")
	@RequestMapping(value = "save")
	public String save(WGuaranteeCompany wGuaranteeCompany, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request) {

		try {
			if (!beanValidator(model, wGuaranteeCompany)) {
				return form(wGuaranteeCompany, model, request);
			}
			if (wGuaranteeCompany.getUser() == null) {
				wGuaranteeCompany.setUser(SessionUtils.getUser());
			}
			// wGuaranteeCompanyService.save(wGuaranteeCompany);
			wGuaranteeCompanyService.saveWGuaranteeCompany(wGuaranteeCompany);
			addMessage(redirectAttributes, "保存" + StringUtils.abbr(wGuaranteeCompany.getName(), 50) + "成功");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:" + adminPath + "/pro/wguarantee/";
	}

	@RequiresPermissions("pro:wguaranteecompany:edit")
	@RequestMapping(value = "delete")
	public Object delete(String id, @RequestParam(required = false) Boolean isRe, RedirectAttributes redirectAttributes) {

		try {
			List<WloanTermProject> wList = wloanTermProjectService.findListByCompanyId(id);
			if (wList != null && wList.size() > 0) {
				addMessage(redirectAttributes, "当前担保公司已被使用，不能删除");
			} else {
				WGuaranteeCompany wGuaranteeCompany = wGuaranteeCompanyService.get(id);
				wGuaranteeCompanyService.deleteWGuaranteeCompany(wGuaranteeCompany);
				addMessage(redirectAttributes, (isRe != null && isRe ? "" : "删除") + "成功");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:" + adminPath + "/pro/wguarantee/";
	}

	@RequiresPermissions("pro:wguaranteecompany:edit")
	@RequestMapping(value = "datum")
	public String datum(WGuaranteeCompany wGuaranteeCompany, Model model) {

		List<AnnexFile> annexFileList = annexFileService.findAnnexFileMap(wGuaranteeCompany.getId());
		model.addAttribute("annexFileList", annexFileList);
		model.addAttribute("wGuaranteeCompany", wGuaranteeCompany);
		model.addAttribute("returnUrl", "/pro/wguarantee/datum?id=" + wGuaranteeCompany.getId());
		return "modules/regular/wGuaranteeCompanyDatum";
	}

}
