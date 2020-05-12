package com.power.platform.wexperience.web;

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
import com.power.platform.common.web.BaseController;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.wexperience.entity.WexperienceMoney;
import com.power.platform.wexperience.service.WexperienceMoneyService;

/**
 * 体验金信息Controller
 * @author Mr.Jia
 * @version 2016-01-25
 */
@Controller
@RequestMapping(value = "${adminPath}/wexperience/wexperienceMoney")
public class WexperienceMoneyController extends BaseController {

	@Autowired
	private WexperienceMoneyService wexperienceMoneyService;
	
	@ModelAttribute
	public WexperienceMoney get(@RequestParam(required=false) String id) {
		WexperienceMoney entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = wexperienceMoneyService.get(id);
		}
		if (entity == null){
			entity = new WexperienceMoney();
		}
		return entity;
	}
	
	@RequiresPermissions("wexperience:wexperienceMoney:view")
	@RequestMapping(value = {"list", ""})
	public String list(WexperienceMoney wexperienceMoney, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<WexperienceMoney> page = wexperienceMoneyService.findPage(new Page<WexperienceMoney>(request, response), wexperienceMoney); 
		model.addAttribute("page", page);
		return "modules/wexperience/wexperienceMoneyList";
	}

	@RequiresPermissions("wexperience:wexperienceMoney:view")
	@RequestMapping(value = "form")
	public String form(WexperienceMoney wexperienceMoney, Model model) {
		model.addAttribute("wexperienceMoney", wexperienceMoney);
		return "modules/wexperience/wexperienceMoneyForm";
	}

	@RequiresPermissions("wexperience:wexperienceMoney:edit")
	@RequestMapping(value = "save")
	public String save(WexperienceMoney wexperienceMoney, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, wexperienceMoney)){
			return form(wexperienceMoney, model);
		}
		wexperienceMoneyService.save(wexperienceMoney);
		addMessage(redirectAttributes, "保存体验金信息成功");
		return "redirect:"+Global.getAdminPath()+"/wexperience/wexperienceMoney/?repage";
	}
	
	@RequiresPermissions("wexperience:wexperienceMoney:edit")
	@RequestMapping(value = "delete")
	public String delete(WexperienceMoney wexperienceMoney, RedirectAttributes redirectAttributes) {
		wexperienceMoneyService.delete(wexperienceMoney);
		addMessage(redirectAttributes, "删除体验金信息成功");
		return "redirect:"+Global.getAdminPath()+"/wexperience/wexperienceMoney/?repage";
	}

}