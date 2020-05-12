package com.power.platform.credit.addressinfo;

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
import com.power.platform.credit.service.addressinfo.CreditAddressInfoService;


/**
 * 信贷家庭住址Controller
 * @author nice
 * @version 2017-03-23
 */
@Controller
@RequestMapping(value = "${adminPath}/credit/addressinfo/creditAddressInfo")
public class CreditAddressInfoController extends BaseController {

	@Autowired
	private CreditAddressInfoService creditAddressInfoService;
	
	@ModelAttribute
	public CreditAddressInfo get(@RequestParam(required=false) String id) {
		CreditAddressInfo entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = creditAddressInfoService.get(id);
		}
		if (entity == null){
			entity = new CreditAddressInfo();
		}
		return entity;
	}
	
	@RequiresPermissions("credit:addressinfo:creditAddressInfo:view")
	@RequestMapping(value = {"list", ""})
	public String list(CreditAddressInfo creditAddressInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<CreditAddressInfo> page = creditAddressInfoService.findPage1(new Page<CreditAddressInfo>(request, response), creditAddressInfo); 
		
		List<CreditAddressInfo> list = page.getList();
		for(CreditAddressInfo addressInfo : list){
			String[] urls = addressInfo.getCreditAnnexFile().getUrl().split(",");
			addressInfo.setImgList(Arrays.asList(urls));
		}
		
		model.addAttribute("page", page);
		return "modules/credit/addressinfo/creditAddressInfoList";
	}

	@RequiresPermissions("credit:addressinfo:creditAddressInfo:view")
	@RequestMapping(value = "form")
	public String form(CreditAddressInfo creditAddressInfo, Model model) {
		model.addAttribute("creditAddressInfo", creditAddressInfo);
		return "modules/credit/addressinfo/creditAddressInfoForm";
	}

	@RequiresPermissions("credit:addressinfo:creditAddressInfo:edit")
	@RequestMapping(value = "save")
	public String save(CreditAddressInfo creditAddressInfo, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, creditAddressInfo)){
			return form(creditAddressInfo, model);
		}
		creditAddressInfoService.save(creditAddressInfo);
		addMessage(redirectAttributes, "保存信贷家庭住址成功");
		return "redirect:"+Global.getAdminPath()+"/credit/addressinfo/creditAddressInfo/?repage";
	}
	
	@RequiresPermissions("credit:addressinfo:creditAddressInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(CreditAddressInfo creditAddressInfo, RedirectAttributes redirectAttributes) {
		creditAddressInfoService.delete(creditAddressInfo);
		addMessage(redirectAttributes, "删除信贷家庭住址成功");
		return "redirect:"+Global.getAdminPath()+"/credit/addressinfo/creditAddressInfo/?repage";
	}

}