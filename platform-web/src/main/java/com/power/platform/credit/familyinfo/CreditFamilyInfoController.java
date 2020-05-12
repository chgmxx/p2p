package com.power.platform.credit.familyinfo;

import java.util.Arrays;
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
import com.power.platform.credit.entity.familyinfo.CreditFamilyInfo;
import com.power.platform.credit.service.familyinfo.CreditFamilyInfoService;


/**
 * 信贷家庭信息Controller
 * @author nice
 * @version 2017-03-23
 */
@Controller
@RequestMapping(value = "${adminPath}/credit/familyinfo/creditFamilyInfo")
public class CreditFamilyInfoController extends BaseController {

	@Autowired
	private CreditFamilyInfoService creditFamilyInfoService;
	
	@ModelAttribute
	public CreditFamilyInfo get(@RequestParam(required=false) String id) {
		CreditFamilyInfo entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = creditFamilyInfoService.get(id);
		}
		if (entity == null){
			entity = new CreditFamilyInfo();
		}
		return entity;
	}
	
	@RequiresPermissions("credit:familyinfo:creditFamilyInfo:view")
	@RequestMapping(value = {"list", ""})
	public String list(CreditFamilyInfo creditFamilyInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<CreditFamilyInfo> page = creditFamilyInfoService.findPage1(new Page<CreditFamilyInfo>(request, response), creditFamilyInfo); 
		
		List<CreditFamilyInfo> list = page.getList();
		for(CreditFamilyInfo familyInfo : list){
			String[] urls = familyInfo.getCreditAnnexFile().getUrl().split(",");
			familyInfo.setImgList(Arrays.asList(urls));
		}
		
		model.addAttribute("page", page);
		return "modules/credit/familyinfo/creditFamilyInfoList";
	}

	@RequiresPermissions("credit:familyinfo:creditFamilyInfo:view")
	@RequestMapping(value = "form")
	public String form(CreditFamilyInfo creditFamilyInfo, Model model) {
		model.addAttribute("creditFamilyInfo", creditFamilyInfo);
		return "modules/credit/familyinfo/creditFamilyInfoForm";
	}

	@RequiresPermissions("credit:familyinfo:creditFamilyInfo:edit")
	@RequestMapping(value = "save")
	public String save(CreditFamilyInfo creditFamilyInfo, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, creditFamilyInfo)){
			return form(creditFamilyInfo, model);
		}
		creditFamilyInfoService.save(creditFamilyInfo);
		addMessage(redirectAttributes, "保存信贷家庭信息成功");
		return "redirect:"+Global.getAdminPath()+"/credit/familyinfo/creditFamilyInfo/?repage";
	}
	
	@RequiresPermissions("credit:familyinfo:creditFamilyInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(CreditFamilyInfo creditFamilyInfo, RedirectAttributes redirectAttributes) {
		creditFamilyInfoService.delete(creditFamilyInfo);
		addMessage(redirectAttributes, "删除信贷家庭信息成功");
		return "redirect:"+Global.getAdminPath()+"/credit/familyinfo/creditFamilyInfo/?repage";
	}

}