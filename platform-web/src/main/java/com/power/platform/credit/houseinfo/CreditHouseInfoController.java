package com.power.platform.credit.houseinfo;

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
import com.power.platform.credit.entity.addressinfo.CreditAddressInfo;
import com.power.platform.credit.entity.houseinfo.CreditHouseInfo;
import com.power.platform.credit.service.houseinfo.CreditHouseInfoService;


/**
 * 信贷房产信息Controller
 * @author nice
 * @version 2017-03-23
 */
@Controller
@RequestMapping(value = "${adminPath}/credit/houseinfo/creditHouseInfo")
public class CreditHouseInfoController extends BaseController {

	@Autowired
	private CreditHouseInfoService creditHouseInfoService;
	
	@ModelAttribute
	public CreditHouseInfo get(@RequestParam(required=false) String id) {
		CreditHouseInfo entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = creditHouseInfoService.get(id);
		}
		if (entity == null){
			entity = new CreditHouseInfo();
		}
		return entity;
	}
	
	@RequiresPermissions("credit:houseinfo:creditHouseInfo:view")
	@RequestMapping(value = {"list", ""})
	public String list(CreditHouseInfo creditHouseInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<CreditHouseInfo> page = creditHouseInfoService.findPage1(new Page<CreditHouseInfo>(request, response), creditHouseInfo); 
		
		List<CreditHouseInfo> list = page.getList();
		for(CreditHouseInfo houseInfo : list){
			String[] urls = houseInfo.getCreditAnnexFile().getUrl().split(",");
			houseInfo.setImgList(Arrays.asList(urls));
		}
		
		model.addAttribute("page", page);
		return "modules/credit/houseinfo/creditHouseInfoList";
	}

	@RequiresPermissions("credit:houseinfo:creditHouseInfo:view")
	@RequestMapping(value = "form")
	public String form(CreditHouseInfo creditHouseInfo, Model model) {
		model.addAttribute("creditHouseInfo", creditHouseInfo);
		return "modules/credit/houseinfo/creditHouseInfoForm";
	}

	@RequiresPermissions("credit:houseinfo:creditHouseInfo:edit")
	@RequestMapping(value = "save")
	public String save(CreditHouseInfo creditHouseInfo, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, creditHouseInfo)){
			return form(creditHouseInfo, model);
		}
		creditHouseInfoService.save(creditHouseInfo);
		addMessage(redirectAttributes, "保存信贷房产信息成功");
		return "redirect:"+Global.getAdminPath()+"/credit/houseinfo/creditHouseInfo/?repage";
	}
	
	@RequiresPermissions("credit:houseinfo:creditHouseInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(CreditHouseInfo creditHouseInfo, RedirectAttributes redirectAttributes) {
		creditHouseInfoService.delete(creditHouseInfo);
		addMessage(redirectAttributes, "删除信贷房产信息成功");
		return "redirect:"+Global.getAdminPath()+"/credit/houseinfo/creditHouseInfo/?repage";
	}

}