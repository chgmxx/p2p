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
import com.power.platform.zdw.entity.ZdwProOrderInfo;
import com.power.platform.zdw.service.ZdwProOrderInfoService;

/**
 * 中等网满标落单Controller
 * 
 * @author Roy
 * @version 2019-07-12
 */
@Controller
@RequestMapping(value = "${adminPath}/zdw/register/zdwProOrderInfo")
public class ZdwProOrderInfoController extends BaseController {

	@Autowired
	private ZdwProOrderInfoService zdwProOrderInfoService;

	@ModelAttribute
	public ZdwProOrderInfo get(@RequestParam(required = false) String id) {

		ZdwProOrderInfo entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = zdwProOrderInfoService.get(id);
		}
		if (entity == null) {
			entity = new ZdwProOrderInfo();
		}
		return entity;
	}

	@RequiresPermissions("zdw:register:zdwProOrderInfo:view")
	@RequestMapping(value = { "list", "" })
	public String list(ZdwProOrderInfo zdwProOrderInfo, HttpServletRequest request, HttpServletResponse response, Model model) {

		Page<ZdwProOrderInfo> page = zdwProOrderInfoService.findPage(new Page<ZdwProOrderInfo>(request, response), zdwProOrderInfo);
		model.addAttribute("page", page);
		return "modules/credit/zdw/proOrderList/zdwProOrderInfoList";
	}

	@RequiresPermissions("zdw:register:zdwProOrderInfo:view")
	@RequestMapping(value = "form")
	public String form(ZdwProOrderInfo zdwProOrderInfo, Model model) {

		model.addAttribute("zdwProOrderInfo", zdwProOrderInfo);
		return "modules/zdw/register/zdwProOrderInfoForm";
	}

	@RequiresPermissions("zdw:register:zdwProOrderInfo:edit")
	@RequestMapping(value = "save")
	public String save(ZdwProOrderInfo zdwProOrderInfo, Model model, RedirectAttributes redirectAttributes) {

		if (!beanValidator(model, zdwProOrderInfo)) {
			return form(zdwProOrderInfo, model);
		}
		zdwProOrderInfoService.save(zdwProOrderInfo);
		addMessage(redirectAttributes, "保存中等网满标落单成功");
		return "redirect:" + Global.getAdminPath() + "/zdw/register/zdwProOrderInfo/?repage";
	}

	@RequiresPermissions("zdw:register:zdwProOrderInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(ZdwProOrderInfo zdwProOrderInfo, RedirectAttributes redirectAttributes) {

		zdwProOrderInfoService.delete(zdwProOrderInfo);
		addMessage(redirectAttributes, "删除中等网满标落单成功");
		return "redirect:" + Global.getAdminPath() + "/zdw/register/zdwProOrderInfo/?repage";
	}

}