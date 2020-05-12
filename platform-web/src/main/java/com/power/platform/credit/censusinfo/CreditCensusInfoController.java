package com.power.platform.credit.censusinfo;

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
import com.power.platform.credit.entity.basicinfo.CreditBasicInfo;
import com.power.platform.credit.entity.censusinfo.CreditCensusInfo;
import com.power.platform.credit.service.censusinfo.CreditCensusInfoService;


/**
 * 信贷人口普查Controller
 * @author nice
 * @version 2017-03-23
 */
@Controller
@RequestMapping(value = "${adminPath}/credit/censusinfo/creditCensusInfo")
public class CreditCensusInfoController extends BaseController {

	@Autowired
	private CreditCensusInfoService creditCensusInfoService;
	
	@ModelAttribute
	public CreditCensusInfo get(@RequestParam(required=false) String id) {
		CreditCensusInfo entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = creditCensusInfoService.get(id);
		}
		if (entity == null){
			entity = new CreditCensusInfo();
		}
		return entity;
	}
	
	@RequiresPermissions("credit:censusinfo:creditCensusInfo:view")
	@RequestMapping(value = {"list", ""})
	public String list(CreditCensusInfo creditCensusInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<CreditCensusInfo> page = creditCensusInfoService.findPage1(new Page<CreditCensusInfo>(request, response), creditCensusInfo); 
		
		List<CreditCensusInfo> list = page.getList();
		for(CreditCensusInfo censusInfo : list){
			String[] urls = censusInfo.getCreditAnnexFile().getUrl().split(",");
			censusInfo.setImgList(Arrays.asList(urls));
		}
		
		model.addAttribute("page", page);
		return "modules/credit/censusinfo/creditCensusInfoList";
	}

	@RequiresPermissions("credit:censusinfo:creditCensusInfo:view")
	@RequestMapping(value = "form")
	public String form(CreditCensusInfo creditCensusInfo, Model model) {
		model.addAttribute("creditCensusInfo", creditCensusInfo);
		return "modules/credit/censusinfo/creditCensusInfoForm";
	}

	@RequiresPermissions("credit:censusinfo:creditCensusInfo:edit")
	@RequestMapping(value = "save")
	public String save(CreditCensusInfo creditCensusInfo, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, creditCensusInfo)){
			return form(creditCensusInfo, model);
		}
		creditCensusInfoService.save(creditCensusInfo);
		addMessage(redirectAttributes, "保存信贷人口普查成功");
		return "redirect:"+Global.getAdminPath()+"/credit/censusinfo/creditCensusInfo/?repage";
	}
	
	@RequiresPermissions("credit:censusinfo:creditCensusInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(CreditCensusInfo creditCensusInfo, RedirectAttributes redirectAttributes) {
		creditCensusInfoService.delete(creditCensusInfo);
		addMessage(redirectAttributes, "删除信贷人口普查成功");
		return "redirect:"+Global.getAdminPath()+"/credit/censusinfo/creditCensusInfo/?repage";
	}

}