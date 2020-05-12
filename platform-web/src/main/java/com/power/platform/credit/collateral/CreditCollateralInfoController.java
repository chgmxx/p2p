package com.power.platform.credit.collateral;

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
import com.power.platform.credit.entity.collateral.CreditCollateralInfo;
import com.power.platform.credit.service.collateral.CreditCollateralInfoService;


/**
 * 抵押物信息Controller
 * @author nice
 * @version 2017-05-10
 */
@Controller
@RequestMapping(value = "${adminPath}/credit/collateral/creditCollateralInfo")
public class CreditCollateralInfoController extends BaseController {

	@Autowired
	private CreditCollateralInfoService creditCollateralInfoService;
	
	@ModelAttribute
	public CreditCollateralInfo get(@RequestParam(required=false) String id) {
		CreditCollateralInfo entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = creditCollateralInfoService.get(id);
		}
		if (entity == null){
			entity = new CreditCollateralInfo();
		}
		return entity;
	}
	
	@RequiresPermissions("collateral:creditCollateralInfo:view")
	@RequestMapping(value = {"list", ""})
	public String list(CreditCollateralInfo creditCollateralInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<CreditCollateralInfo> page = creditCollateralInfoService.findPage(new Page<CreditCollateralInfo>(request, response), creditCollateralInfo); 
		model.addAttribute("page", page);
		return "modules/credit/collateral/creditCollateralInfoList";
	}

	@RequiresPermissions("collateral:creditCollateralInfo:view")
	@RequestMapping(value = "form")
	public String form(CreditCollateralInfo creditCollateralInfo, Model model) {
		model.addAttribute("creditCollateralInfo", creditCollateralInfo);
		return "modules/credit/collateral/creditCollateralInfoForm";
	}

	@RequiresPermissions("collateral:creditCollateralInfo:edit")
	@RequestMapping(value = "save")
	public String save(CreditCollateralInfo creditCollateralInfo, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, creditCollateralInfo)){
			return form(creditCollateralInfo, model);
		}
		creditCollateralInfoService.save(creditCollateralInfo);
		addMessage(redirectAttributes, "保存抵押物信息成功");
		return "redirect:"+Global.getAdminPath()+"/collateral/creditCollateralInfo/?repage";
	}
	
	@RequiresPermissions("collateral:creditCollateralInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(CreditCollateralInfo creditCollateralInfo, RedirectAttributes redirectAttributes) {
		creditCollateralInfoService.delete(creditCollateralInfo);
		addMessage(redirectAttributes, "删除抵押物信息成功");
		return "redirect:"+Global.getAdminPath()+"/collateral/creditCollateralInfo/?repage";
	}

}