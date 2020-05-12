/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.credit.middlemen;

import java.util.List;

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
import com.power.platform.credit.entity.middlemen.CreditMiddlemenRate;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.service.middlemen.CreditMiddlemenRateService;
import com.power.platform.credit.service.userinfo.CreditUserInfoService;


/**
 * 项目期限和利率Controller
 * @author yb
 * @version 2018-04-20
 */
@Controller
@RequestMapping(value = "${adminPath}/credit/middlemen/creditMiddlemenRate")
public class CreditMiddlemenRateController extends BaseController {

	@Autowired
	private CreditMiddlemenRateService creditMiddlemenRateService;
	@Autowired
	private CreditUserInfoService creditUserInfoService;
	
	@ModelAttribute
	public CreditMiddlemenRate get(@RequestParam(required=false) String id) {
		CreditMiddlemenRate entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = creditMiddlemenRateService.get(id);
		}
		if (entity == null){
			entity = new CreditMiddlemenRate();
		}
		return entity;
	}
	
	@RequiresPermissions("credit:middlemen:creditMiddlemenRate:view")
	@RequestMapping(value = {"list", ""})
	public String list(CreditMiddlemenRate creditMiddlemenRate, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<CreditMiddlemenRate> page = creditMiddlemenRateService.findPage(new Page<CreditMiddlemenRate>(request, response), creditMiddlemenRate); 
		model.addAttribute("page", page);
		
		//查询所有核心企业
		CreditUserInfo userInfo = new CreditUserInfo();
		userInfo.setCreditUserType("11");
		List<CreditUserInfo> middlemenList = creditUserInfoService.findList(userInfo);
		
		model.addAttribute("middlemenList", middlemenList);
		
		return "modules/credit/middlemen/creditMiddlemenRateList";
	}

	@RequiresPermissions("credit:middlemen:creditMiddlemenRate:view")
	@RequestMapping(value = "form")
	public String form(CreditMiddlemenRate creditMiddlemenRate, Model model) {
		model.addAttribute("creditMiddlemenRate", creditMiddlemenRate);
		
		//查询所有核心企业
		CreditUserInfo userInfo = new CreditUserInfo();
		userInfo.setCreditUserType("11");
		List<CreditUserInfo> middlemenList = creditUserInfoService.findList(userInfo);
		
		model.addAttribute("middlemenList", middlemenList);
		
		return "modules/credit/middlemen/creditMiddlemenRateForm";
	}

	@RequiresPermissions("credit:middlemen:creditMiddlemenRate:edit")
	@RequestMapping(value = "save")
	public String save(CreditMiddlemenRate creditMiddlemenRate, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, creditMiddlemenRate)){
			return form(creditMiddlemenRate, model);
		}
		creditMiddlemenRateService.save(creditMiddlemenRate);
		addMessage(redirectAttributes, "保存项目期限和利率成功");
		return "redirect:"+Global.getAdminPath()+"/credit/middlemen/creditMiddlemenRate/?repage";
	}
	
	@RequiresPermissions("credit:middlemen:creditMiddlemenRate:edit")
	@RequestMapping(value = "delete")
	public String delete(CreditMiddlemenRate creditMiddlemenRate, RedirectAttributes redirectAttributes) {
		creditMiddlemenRateService.delete(creditMiddlemenRate);
		addMessage(redirectAttributes, "删除项目期限和利率成功");
		return "redirect:"+Global.getAdminPath()+"/credit/middlemen/creditMiddlemenRate/?repage";
	}

}