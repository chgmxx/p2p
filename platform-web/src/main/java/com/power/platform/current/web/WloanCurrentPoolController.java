package com.power.platform.current.web;

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
import com.power.platform.current.entity.WloanCurrentPool;
import com.power.platform.current.service.WloanCurrentPoolService;

/**
 * 活期融资资金池Controller
 * @author Mr.Jia
 * @version 2016-01-12
 */
@Controller
@RequestMapping(value = "${adminPath}/current/pool/wloanCurrentPool")
public class WloanCurrentPoolController extends BaseController {

	@Autowired
	private WloanCurrentPoolService wloanCurrentPoolService;
	
	@ModelAttribute
	public WloanCurrentPool get(@RequestParam(required=false) String id) {
		WloanCurrentPool entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = wloanCurrentPoolService.get(id);
		}
		if (entity == null){
			entity = new WloanCurrentPool();
		}
		return entity;
	}
	
	@RequiresPermissions("current:pool:wloanCurrentPool:view")
	@RequestMapping(value = {"list", ""})
	public String list(WloanCurrentPool wloanCurrentPool, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<WloanCurrentPool> page = wloanCurrentPoolService.findPage(new Page<WloanCurrentPool>(request, response), wloanCurrentPool); 
		model.addAttribute("page", page);
		return "modules/current/pool/wloanCurrentPoolList";
	}

	@RequiresPermissions("current:pool:wloanCurrentPool:view")
	@RequestMapping(value = "form")
	public String form(WloanCurrentPool wloanCurrentPool, Model model) {
		model.addAttribute("wloanCurrentPool", wloanCurrentPool);
		return "modules/current/pool/wloanCurrentPoolForm";
	}

	@RequiresPermissions("current:pool:wloanCurrentPool:edit")
	@RequestMapping(value = "save")
	public String save(WloanCurrentPool wloanCurrentPool, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, wloanCurrentPool)){
			return form(wloanCurrentPool, model);
		}
		wloanCurrentPoolService.save(wloanCurrentPool);
		addMessage(redirectAttributes, "保存活期融资资金池成功");
		return "redirect:"+Global.getAdminPath()+"/current/pool/wloanCurrentPool/?repage";
	}
	
	@RequiresPermissions("current:pool:wloanCurrentPool:edit")
	@RequestMapping(value = "delete")
	public String delete(WloanCurrentPool wloanCurrentPool, RedirectAttributes redirectAttributes) {
		wloanCurrentPoolService.delete(wloanCurrentPool);
		addMessage(redirectAttributes, "删除活期融资资金池成功");
		return "redirect:"+Global.getAdminPath()+"/current/pool/wloanCurrentPool/?repage";
	}

}