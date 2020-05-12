package com.power.platform.credit.basicinfo;

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
import com.power.platform.credit.service.basicinfo.CreditBasicInfoService;


/**
 * 信贷基本信息Controller
 * @author nice
 * @version 2017-03-23
 */
@Controller
@RequestMapping(value = "${adminPath}/credit/basicinfo/creditBasicInfo")
public class CreditBasicInfoController extends BaseController {

	@Autowired
	private CreditBasicInfoService creditBasicInfoService;
	
	@ModelAttribute
	public CreditBasicInfo get(@RequestParam(required=false) String id) {
		CreditBasicInfo entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = creditBasicInfoService.get(id);
		}
		if (entity == null){
			entity = new CreditBasicInfo();
		}
		return entity;
	}
	
	@RequiresPermissions("credit:basicinfo:creditBasicInfo:view")
	@RequestMapping(value = {"list", ""})
	public String list(CreditBasicInfo creditBasicInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<CreditBasicInfo> page = creditBasicInfoService.findPage(new Page<CreditBasicInfo>(request, response), creditBasicInfo); 
		
		List<CreditBasicInfo> list = page.getList();
		if(list!=null && list.size()>0){
			for(CreditBasicInfo basicInfo : list){
				if(basicInfo!=null){
					String[] urls = basicInfo.getCreditAnnexFile().getUrl().split(",");
					basicInfo.setImgList(Arrays.asList(urls));
				}
			}
		}
		model.addAttribute("page", page);
		return "modules/credit/basicinfo/creditBasicInfoList";
	}

	@RequiresPermissions("credit:basicinfo:creditBasicInfo:view")
	@RequestMapping(value = "form")
	public String form(CreditBasicInfo creditBasicInfo, Model model) {
		model.addAttribute("creditBasicInfo", creditBasicInfo);
		return "modules/credit/basicinfo/creditBasicInfoForm";
	}

	@RequiresPermissions("credit:basicinfo:creditBasicInfo:edit")
	@RequestMapping(value = "save")
	public String save(CreditBasicInfo creditBasicInfo, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, creditBasicInfo)){
			return form(creditBasicInfo, model);
		}
		creditBasicInfoService.save(creditBasicInfo);
		addMessage(redirectAttributes, "保存信贷基本信息成功");
		return "redirect:"+Global.getAdminPath()+"/credit/basicinfo/creditBasicInfo/?repage";
	}
	
	@RequiresPermissions("credit:basicinfo:creditBasicInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(CreditBasicInfo creditBasicInfo, RedirectAttributes redirectAttributes) {
		creditBasicInfoService.delete(creditBasicInfo);
		addMessage(redirectAttributes, "删除信贷基本信息成功");
		return "redirect:"+Global.getAdminPath()+"/credit/basicinfo/creditBasicInfo/?repage";
	}

}