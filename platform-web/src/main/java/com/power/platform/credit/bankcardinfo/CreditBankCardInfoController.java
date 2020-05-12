package com.power.platform.credit.bankcardinfo;

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
import com.power.platform.credit.entity.bankcardinfo.CreditBankCardInfo;
import com.power.platform.credit.service.bankcardinfo.CreditBankCardInfoService;


/**
 * 信贷银行卡Controller
 * @author nice
 * @version 2017-03-23
 */
@Controller
@RequestMapping(value = "${adminPath}/credit/bankcardinfo/creditBankCardInfo")
public class CreditBankCardInfoController extends BaseController {

	@Autowired
	private CreditBankCardInfoService creditBankCardInfoService;
	
	@ModelAttribute
	public CreditBankCardInfo get(@RequestParam(required=false) String id) {
		CreditBankCardInfo entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = creditBankCardInfoService.get(id);
		}
		if (entity == null){
			entity = new CreditBankCardInfo();
		}
		return entity;
	}
	
	@RequiresPermissions("credit:bankcardinfo:creditBankCardInfo:view")
	@RequestMapping(value = {"list", ""})
	public String list(CreditBankCardInfo creditBankCardInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<CreditBankCardInfo> page = creditBankCardInfoService.findPage(new Page<CreditBankCardInfo>(request, response), creditBankCardInfo); 
		model.addAttribute("page", page);
		return "modules/credit/bankcardinfo/creditBankCardInfoList";
	}

	@RequiresPermissions("credit:bankcardinfo:creditBankCardInfo:view")
	@RequestMapping(value = "form")
	public String form(CreditBankCardInfo creditBankCardInfo, Model model) {
		model.addAttribute("creditBankCardInfo", creditBankCardInfo);
		return "modules/credit/bankcardinfo/creditBankCardInfoForm";
	}

	@RequiresPermissions("credit:bankcardinfo:creditBankCardInfo:edit")
	@RequestMapping(value = "save")
	public String save(CreditBankCardInfo creditBankCardInfo, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, creditBankCardInfo)){
			return form(creditBankCardInfo, model);
		}
		creditBankCardInfoService.save(creditBankCardInfo);
		addMessage(redirectAttributes, "保存信贷银行卡成功");
		return "redirect:"+Global.getAdminPath()+"/credit/bankcardinfo/creditBankCardInfo/?repage";
	}
	
	@RequiresPermissions("credit:bankcardinfo:creditBankCardInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(CreditBankCardInfo creditBankCardInfo, RedirectAttributes redirectAttributes) {
		creditBankCardInfoService.delete(creditBankCardInfo);
		addMessage(redirectAttributes, "删除信贷银行卡成功");
		return "redirect:"+Global.getAdminPath()+"/credit/bankcardinfo/creditBankCardInfo/?repage";
	}

}