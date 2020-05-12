package com.power.platform.credit.carinfo;

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
import com.power.platform.credit.entity.carinfo.CreditCarInfo;
import com.power.platform.credit.service.carinfo.CreditCarInfoService;


/**
 * 信贷车产Controller
 * @author nice
 * @version 2017-03-23
 */
@Controller
@RequestMapping(value = "${adminPath}/credit/carinfo/creditCarInfo")
public class CreditCarInfoController extends BaseController {

	@Autowired
	private CreditCarInfoService creditCarInfoService;
	
	@ModelAttribute
	public CreditCarInfo get(@RequestParam(required=false) String id) {
		CreditCarInfo entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = creditCarInfoService.get(id);
		}
		if (entity == null){
			entity = new CreditCarInfo();
		}
		return entity;
	}
	
	@RequiresPermissions("credit:carinfo:creditCarInfo:view")
	@RequestMapping(value = {"list", ""})
	public String list(CreditCarInfo creditCarInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<CreditCarInfo> page = creditCarInfoService.findPage(new Page<CreditCarInfo>(request, response), creditCarInfo); 
		
		List<CreditCarInfo> list = page.getList();
		for(CreditCarInfo carInfo : list){
			String[] urls = carInfo.getCreditAnnexFile().getUrl().split(",");
			carInfo.setImgList(Arrays.asList(urls));
		}
		
		
		model.addAttribute("page", page);
		return "modules/credit/carinfo/creditCarInfoList";
	}

	@RequiresPermissions("credit:carinfo:creditCarInfo:view")
	@RequestMapping(value = "form")
	public String form(CreditCarInfo creditCarInfo, Model model) {
		model.addAttribute("creditCarInfo", creditCarInfo);
		return "modules/credit/carinfo/creditCarInfoForm";
	}

	@RequiresPermissions("credit:carinfo:creditCarInfo:edit")
	@RequestMapping(value = "save")
	public String save(CreditCarInfo creditCarInfo, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, creditCarInfo)){
			return form(creditCarInfo, model);
		}
		creditCarInfoService.save(creditCarInfo);
		addMessage(redirectAttributes, "保存信贷车产成功");
		return "redirect:"+Global.getAdminPath()+"/credit/carinfo/creditCarInfo/?repage";
	}
	
	@RequiresPermissions("credit:carinfo:creditCarInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(CreditCarInfo creditCarInfo, RedirectAttributes redirectAttributes) {
		creditCarInfoService.delete(creditCarInfo);
		addMessage(redirectAttributes, "删除信贷车产成功");
		return "redirect:"+Global.getAdminPath()+"/credit/carinfo/creditCarInfo/?repage";
	}

}