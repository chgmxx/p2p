package com.power.platform.credit.coinsuranceinfo;

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
import com.power.platform.credit.entity.censusinfo.CreditCensusInfo;
import com.power.platform.credit.entity.coinsuranceinfo.CreditCoinsuranceInfo;
import com.power.platform.credit.service.coinsuranceinfo.CreditCoinsuranceInfoService;


/**
 * 信贷联保Controller
 * @author nice
 * @version 2017-03-23
 */
@Controller
@RequestMapping(value = "${adminPath}/credit/coinsuranceinfo/creditCoinsuranceInfo")
public class CreditCoinsuranceInfoController extends BaseController {

	@Autowired
	private CreditCoinsuranceInfoService creditCoinsuranceInfoService;
	
	@ModelAttribute
	public CreditCoinsuranceInfo get(@RequestParam(required=false) String id) {
		CreditCoinsuranceInfo entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = creditCoinsuranceInfoService.get(id);
		}
		if (entity == null){
			entity = new CreditCoinsuranceInfo();
		}
		return entity;
	}
	
	@RequiresPermissions("credit:coinsuranceinfo:creditCoinsuranceInfo:view")
	@RequestMapping(value = {"list", ""})
	public String list(CreditCoinsuranceInfo creditCoinsuranceInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<CreditCoinsuranceInfo> page = creditCoinsuranceInfoService.findPage1(new Page<CreditCoinsuranceInfo>(request, response), creditCoinsuranceInfo); 
		
		List<CreditCoinsuranceInfo> list = page.getList();
		for(CreditCoinsuranceInfo coinsuranceInfo : list){
			String[] urls = coinsuranceInfo.getCreditAnnexFile().getUrl().split(",");
			coinsuranceInfo.setImgList(Arrays.asList(urls));
		}
		
		model.addAttribute("page", page);
		return "modules/credit/coinsuranceinfo/creditCoinsuranceInfoList";
	}

	@RequiresPermissions("credit:coinsuranceinfo:creditCoinsuranceInfo:view")
	@RequestMapping(value = "form")
	public String form(CreditCoinsuranceInfo creditCoinsuranceInfo, Model model) {
		model.addAttribute("creditCoinsuranceInfo", creditCoinsuranceInfo);
		return "modules/credit/coinsuranceinfo/creditCoinsuranceInfoForm";
	}

	@RequiresPermissions("credit:coinsuranceinfo:creditCoinsuranceInfo:edit")
	@RequestMapping(value = "save")
	public String save(CreditCoinsuranceInfo creditCoinsuranceInfo, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, creditCoinsuranceInfo)){
			return form(creditCoinsuranceInfo, model);
		}
		creditCoinsuranceInfoService.save(creditCoinsuranceInfo);
		addMessage(redirectAttributes, "保存信贷联保成功");
		return "redirect:"+Global.getAdminPath()+"/credit/coinsuranceinfo/creditCoinsuranceInfo/?repage";
	}
	
	@RequiresPermissions("credit:coinsuranceinfo:creditCoinsuranceInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(CreditCoinsuranceInfo creditCoinsuranceInfo, RedirectAttributes redirectAttributes) {
		creditCoinsuranceInfoService.delete(creditCoinsuranceInfo);
		addMessage(redirectAttributes, "删除信贷联保成功");
		return "redirect:"+Global.getAdminPath()+"/credit/coinsuranceinfo/creditCoinsuranceInfo/?repage";
	}

}