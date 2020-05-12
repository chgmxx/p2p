package com.power.platform.current.invest;

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
import com.power.platform.current.entity.WloanCurrentUserInvest;
import com.power.platform.current.entity.invest.WloanCurrentProjectInvest;
import com.power.platform.current.service.invest.WloanCurrentProjectInvestService;
import com.power.platform.current.service.invest.WloanCurrentUserInvestService;

/**
 * 活期项目投资Controller
 * @author Mr.Jia
 * @version 2016-01-14
 */
@Controller
@RequestMapping(value = "${adminPath}/current/invest/wloanCurrentProjectInvest")
public class WloanCurrentProjectInvestController extends BaseController {

	@Autowired
	private WloanCurrentProjectInvestService wloanCurrentProjectInvestService;
	@Autowired
	private WloanCurrentUserInvestService wloanCurrentUserInvestService;
	
	@ModelAttribute
	public WloanCurrentProjectInvest get(@RequestParam(required=false) String id) {
		WloanCurrentProjectInvest entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = wloanCurrentProjectInvestService.get(id);
		}
		if (entity == null){
			entity = new WloanCurrentProjectInvest();
		}
		return entity;
	}
	
	@RequiresPermissions("current:invest:wloanCurrentProjectInvest:view")
	@RequestMapping(value = {"list", ""})
	public String list(WloanCurrentProjectInvest wloanCurrentProjectInvest, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<WloanCurrentProjectInvest> page = wloanCurrentProjectInvestService.findPage(new Page<WloanCurrentProjectInvest>(request, response), wloanCurrentProjectInvest); 
		model.addAttribute("page", page);
		return "modules/current/invest/wloanCurrentProjectInvestList";
	}

	@RequiresPermissions("current:invest:wloanCurrentProjectInvest:view")
	@RequestMapping(value = "form")
	public String form(WloanCurrentProjectInvest wloanCurrentProjectInvest, Model model) {
		model.addAttribute("wloanCurrentProjectInvest", wloanCurrentProjectInvest);
		return "modules/current/invest/wloanCurrentProjectInvestForm";
	}

	@RequiresPermissions("current:invest:wloanCurrentProjectInvest:edit")
	@RequestMapping(value = "save")
	public String save(WloanCurrentProjectInvest wloanCurrentProjectInvest, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, wloanCurrentProjectInvest)){
			return form(wloanCurrentProjectInvest, model);
		}
		wloanCurrentProjectInvestService.save(wloanCurrentProjectInvest);
		addMessage(redirectAttributes, "保存活期项目投资成功");
		return "redirect:"+Global.getAdminPath()+"/current/invest/wloanCurrentProjectInvest/?repage";
	}
	
	@RequiresPermissions("current:invest:wloanCurrentProjectInvest:edit")
	@RequestMapping(value = "delete")
	public String delete(WloanCurrentProjectInvest wloanCurrentProjectInvest, RedirectAttributes redirectAttributes) {
		wloanCurrentProjectInvestService.delete(wloanCurrentProjectInvest);
		addMessage(redirectAttributes, "删除活期项目投资成功");
		return "redirect:"+Global.getAdminPath()+"/current/invest/wloanCurrentProjectInvest/?repage";
	}

	
	
	/**
	 * 查看真实投资来源
	 * @param wloanCurrentProjectInvest
	 * @param model
	 * @return
	 */
	@RequiresPermissions("current:invest:wloanCurrentProjectInvest:view")
	@RequestMapping(value = "findCome")
	public String findCome(WloanCurrentProjectInvest wloanCurrentProjectInvest, Model model) {
		WloanCurrentUserInvest wloanCurrentUserInvest = wloanCurrentUserInvestService.get(wloanCurrentProjectInvest.getUserInvest());
		model.addAttribute("wloanCurrentUserInvest", wloanCurrentUserInvest);
		model.addAttribute("wloanCurrentProjectInvest", wloanCurrentProjectInvest);
		return "modules/current/invest/wloanCurrentUserInvestViewForm";
	}
}