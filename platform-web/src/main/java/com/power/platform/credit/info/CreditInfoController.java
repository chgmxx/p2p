/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.credit.info;

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
import com.power.platform.credit.entity.info.CreditInfo;
import com.power.platform.credit.service.info.CreditInfoService;


/**
 * 借款资料Controller
 * @author yb
 * @version 2017-12-11
 */
@Controller
@RequestMapping(value = "${adminPath}/credit/creditinfo/creditInfo")
public class CreditInfoController extends BaseController {

	@Autowired
	private CreditInfoService creditInfoService;
	
	@ModelAttribute
	public CreditInfo get(@RequestParam(required=false) String id) {
		CreditInfo entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = creditInfoService.get(id);
		}
		if (entity == null){
			entity = new CreditInfo();
		}
		return entity;
	}
	
	@RequiresPermissions("credit:creditinfo:creditInfo:view")
	@RequestMapping(value = {"list", ""})
	public String list(CreditInfo creditInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<CreditInfo> page = creditInfoService.findPage(new Page<CreditInfo>(request, response), creditInfo); 
		model.addAttribute("page", page);
		return "modules/credit/creditinfo/creditInfoList";
	}

	@RequiresPermissions("credit:creditinfo:creditInfo:view")
	@RequestMapping(value = "form")
	public String form(CreditInfo creditInfo, Model model) {
		model.addAttribute("creditInfo", creditInfo);
		return "modules/credit/creditinfo/creditInfoForm";
	}

	@RequiresPermissions("credit:creditinfo:creditInfo:edit")
	@RequestMapping(value = "save")
	public String save(CreditInfo creditInfo, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, creditInfo)){
			return form(creditInfo, model);
		}
		creditInfoService.save(creditInfo);
		addMessage(redirectAttributes, "保存借款资料成功");
		return "redirect:"+Global.getAdminPath()+"/credit/creditinfo/creditInfo/?repage";
	}
	
	@RequiresPermissions("credit:creditinfo:creditInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(CreditInfo creditInfo, RedirectAttributes redirectAttributes) {
		creditInfoService.delete(creditInfo);
		addMessage(redirectAttributes, "删除借款资料成功");
		return "redirect:"+Global.getAdminPath()+"/credit/creditinfo/creditInfo/?repage";
	}

}