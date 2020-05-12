package com.power.platform.current.moment;

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
import com.power.platform.current.entity.moment.WloanCurrentMomentInvest;
import com.power.platform.current.service.moment.WloanCurrentMomentInvestService;

/**
 * 投资用户剩余资金信息Controller
 * @author Mr.Jia
 * @version 2016-01-14
 */
@Controller
@RequestMapping(value = "${adminPath}/current/wloanCurrentMomentInvest")
public class WloanCurrentMomentInvestController extends BaseController {

	@Autowired
	private WloanCurrentMomentInvestService wloanCurrentMomentInvestService;
	
	@ModelAttribute
	public WloanCurrentMomentInvest get(@RequestParam(required=false) String id) {
		WloanCurrentMomentInvest entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = wloanCurrentMomentInvestService.get(id);
		}
		if (entity == null){
			entity = new WloanCurrentMomentInvest();
		}
		return entity;
	}
	
	@RequiresPermissions("current:wloanCurrentMomentInvest:view")
	@RequestMapping(value = {"list", ""})
	public String list(WloanCurrentMomentInvest wloanCurrentMomentInvest, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<WloanCurrentMomentInvest> page = wloanCurrentMomentInvestService.findPage(new Page<WloanCurrentMomentInvest>(request, response), wloanCurrentMomentInvest); 
		model.addAttribute("page", page);
		return "modules/current/moment/wloanCurrentMomentInvestList";
	}

	@RequiresPermissions("current:wloanCurrentMomentInvest:view")
	@RequestMapping(value = "form")
	public String form(WloanCurrentMomentInvest wloanCurrentMomentInvest, Model model) {
		model.addAttribute("wloanCurrentMomentInvest", wloanCurrentMomentInvest);
		return "modules/current/moment/wloanCurrentMomentInvestForm";
	}

	@RequiresPermissions("current:wloanCurrentMomentInvest:edit")
	@RequestMapping(value = "save")
	public String save(WloanCurrentMomentInvest wloanCurrentMomentInvest, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, wloanCurrentMomentInvest)){
			return form(wloanCurrentMomentInvest, model);
		}
		wloanCurrentMomentInvestService.save(wloanCurrentMomentInvest);
		addMessage(redirectAttributes, "保存投资用户剩余资金信息成功");
		return "redirect:"+Global.getAdminPath()+"/current/wloanCurrentMomentInvest/?repage";
	}
	
	@RequiresPermissions("current:wloanCurrentMomentInvest:edit")
	@RequestMapping(value = "delete")
	public String delete(WloanCurrentMomentInvest wloanCurrentMomentInvest, RedirectAttributes redirectAttributes) {
		wloanCurrentMomentInvestService.delete(wloanCurrentMomentInvest);
		addMessage(redirectAttributes, "删除投资用户剩余资金信息成功");
		return "redirect:"+Global.getAdminPath()+"/current/wloanCurrentMomentInvest/?repage";
	}

}