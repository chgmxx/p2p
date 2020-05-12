/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.credit.zdw;

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

import com.power.platform.common.config.Global;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.web.BaseController;
import com.power.platform.zdw.entity.ZdwRegistrationInfo;
import com.power.platform.zdw.service.ZdwRegistrationInfoService;

/**
 * 中登网登记信息Controller
 * 
 * @author Roy
 * @version 2019-07-15
 */
@Controller
@RequestMapping(value = "${adminPath}/zdw/register/zdwRegistrationInfo")
public class ZdwRegistrationInfoController extends BaseController {

	@Autowired
	private ZdwRegistrationInfoService zdwRegistrationInfoService;

	@ModelAttribute
	public ZdwRegistrationInfo get(@RequestParam(required = false) String id) {

		ZdwRegistrationInfo entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = zdwRegistrationInfoService.get(id);
		}
		if (entity == null) {
			entity = new ZdwRegistrationInfo();
		}
		return entity;
	}

	@RequiresPermissions("zdw:register:zdwRegistrationInfo:view")
	@RequestMapping(value = { "list", "" })
	public String list(ZdwRegistrationInfo zdwRegistrationInfo, HttpServletRequest request, HttpServletResponse response, Model model) {

		Page<ZdwRegistrationInfo> page = zdwRegistrationInfoService.findPage(new Page<ZdwRegistrationInfo>(request, response), zdwRegistrationInfo);
		model.addAttribute("page", page);
		return "modules/credit/zdw/registrationList/zdwRegistrationInfoList";
	}

	@RequiresPermissions("zdw:register:zdwRegistrationInfo:view")
	@RequestMapping(value = "form")
	public String form(ZdwRegistrationInfo zdwRegistrationInfo, Model model) {

		model.addAttribute("zdwRegistrationInfo", zdwRegistrationInfo);
		return "modules/zdw/register/zdwRegistrationInfoForm";
	}

	@RequiresPermissions("zdw:register:zdwRegistrationInfo:edit")
	@RequestMapping(value = "save")
	public String save(ZdwRegistrationInfo zdwRegistrationInfo, Model model, RedirectAttributes redirectAttributes) {

		if (!beanValidator(model, zdwRegistrationInfo)) {
			return form(zdwRegistrationInfo, model);
		}
		zdwRegistrationInfoService.save(zdwRegistrationInfo);
		addMessage(redirectAttributes, "保存中登网登记信息成功");
		return "redirect:" + Global.getAdminPath() + "/zdw/register/zdwRegistrationInfo/?repage";
	}

	@RequiresPermissions("zdw:register:zdwRegistrationInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(ZdwRegistrationInfo zdwRegistrationInfo, RedirectAttributes redirectAttributes) {

		zdwRegistrationInfoService.delete(zdwRegistrationInfo);
		addMessage(redirectAttributes, "删除中登网登记信息成功");
		return "redirect:" + Global.getAdminPath() + "/zdw/register/zdwRegistrationInfo/?repage";
	}

}