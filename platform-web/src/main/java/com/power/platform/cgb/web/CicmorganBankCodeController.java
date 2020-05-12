/**
 * 银行编码对照Controller.
 */
package com.power.platform.cgb.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.power.platform.cgb.entity.CicmorganBankCode;
import com.power.platform.cgb.service.CicmorganBankCodeService;
import com.power.platform.common.config.Global;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.web.BaseController;

/**
 * 银行编码对照Controller.
 * 
 * @author lance
 * @version 2017-11-28
 */
@Controller
@RequestMapping(value = "${adminPath}/cgb/cicmorganBankCode")
public class CicmorganBankCodeController extends BaseController {

	@Autowired
	private CicmorganBankCodeService cicmorganBankCodeService;

	@ModelAttribute
	public CicmorganBankCode get(@RequestParam(required = false) String id) {

		CicmorganBankCode entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = cicmorganBankCodeService.get(id);
		}
		if (entity == null) {
			entity = new CicmorganBankCode();
		}
		return entity;
	}

	@RequiresPermissions("cgb:cicmorganBankCode:view")
	@RequestMapping(value = { "list", "" })
	public String list(CicmorganBankCode cicmorganBankCode, HttpServletRequest request, HttpServletResponse response, Model model) {

		Page<CicmorganBankCode> page = cicmorganBankCodeService.findPage(new Page<CicmorganBankCode>(request, response), cicmorganBankCode);
		model.addAttribute("page", page);
		return "modules/cgb/cicmorganBankCode/cicmorganBankCodeList";
	}

	@RequiresPermissions("cgb:cicmorganBankCode:view")
	@RequestMapping(value = "form")
	public String form(CicmorganBankCode cicmorganBankCode, Model model) {

		model.addAttribute("cicmorganBankCode", cicmorganBankCode);
		return "modules/cgb/cicmorganBankCode/cicmorganBankCodeForm";
	}

	@RequiresPermissions("cgb:cicmorganBankCode:edit")
	@RequestMapping(value = "save")
	public String save(CicmorganBankCode cicmorganBankCode, Model model, RedirectAttributes redirectAttributes) {

		if (!beanValidator(model, cicmorganBankCode)) {
			return form(cicmorganBankCode, model);
		}
		cicmorganBankCodeService.save(cicmorganBankCode);
		addMessage(redirectAttributes, "保存银行编码对照成功");
		return "redirect:" + Global.getAdminPath() + "/cgb/cicmorganBankCode/?repage";
	}

	@RequiresPermissions("cgb:cicmorganBankCode:edit")
	@RequestMapping(value = "delete")
	public String delete(CicmorganBankCode cicmorganBankCode, RedirectAttributes redirectAttributes) {

		cicmorganBankCodeService.delete(cicmorganBankCode);
		addMessage(redirectAttributes, "删除银行编码对照成功");
		return "redirect:" + Global.getAdminPath() + "/cgb/cicmorganBankCode/?repage";
	}

}