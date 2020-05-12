package com.power.platform.sys.web.credit;

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
import com.power.platform.common.utils.EncoderUtil;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.web.BaseController;
import com.power.platform.credit.entity.userinfo.CreditUserOperator;
import com.power.platform.credit.service.userinfo.CreditUserOperatorService;


/**
 * 借款端，操作人Controller
 * @author yb
 * @version 2018-03-08
 */
@Controller
@RequestMapping(value = "${adminPath}/credit/cuoperator/creditUserOperator")
public class CreditUserOperatorController extends BaseController {

	@Autowired
	private CreditUserOperatorService creditUserOperatorService;
	
	@ModelAttribute
	public CreditUserOperator get(@RequestParam(required=false) String id) {
		CreditUserOperator entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = creditUserOperatorService.get(id);
		}
		if (entity == null){
			entity = new CreditUserOperator();
		} 
		return entity;
	}
	
	@RequiresPermissions("credit:cuoperator:creditUserOperator:view")
	@RequestMapping(value = {"list", ""})
	public String list(CreditUserOperator creditUserOperator, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<CreditUserOperator> page = creditUserOperatorService.findPage(new Page<CreditUserOperator>(request, response), creditUserOperator); 
		model.addAttribute("page", page);
		model.addAttribute("id", creditUserOperator.getCreditUserId());
		return "modules/user/operatorList";
	}

	@RequiresPermissions("credit:cuoperator:creditUserOperator:view")
	@RequestMapping(value = "form")
	public String form(CreditUserOperator creditUserOperator, Model model) {
		model.addAttribute("creditUserOperator", creditUserOperator);
		return "modules/user/creditUserOperatorForm";
	}
	
	@RequiresPermissions("credit:cuoperator:creditUserOperator:view")
	@RequestMapping(value = "update")
	public String update(CreditUserOperator creditUserOperator, Model model) {
		creditUserOperator.setPassword(null);
		model.addAttribute("creditUserOperator", creditUserOperator);
		return "modules/user/creditUserOperatorUpdate";
	}

	@RequiresPermissions("credit:cuoperator:creditUserOperator:edit")
	@RequestMapping(value = "save")
	public String save(CreditUserOperator creditUserOperator, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, creditUserOperator)){
			return form(creditUserOperator, model);
		}
		System.out.println(creditUserOperator.getPhone());
		List<CreditUserOperator> list = creditUserOperatorService.findByPhone(creditUserOperator);
		if(list!=null && list.size()>0){
			addMessage(redirectAttributes, "不能重复添加同一操作人员");
			return "redirect:"+Global.getAdminPath()+"/credit/cuoperator/creditUserOperator/list?creditUserId="+creditUserOperator.getCreditUserId();
		}
		creditUserOperator.setPassword(EncoderUtil.encrypt(creditUserOperator.getPassword()));
		creditUserOperatorService.save(creditUserOperator);
		addMessage(redirectAttributes, "添加操作人员成功");
		return "redirect:"+Global.getAdminPath()+"/credit/cuoperator/creditUserOperator/list?creditUserId="+creditUserOperator.getCreditUserId();
	}
	
	@RequiresPermissions("credit:cuoperator:creditUserOperator:edit")
	@RequestMapping(value = "toUpdate")
	public String toUpdate(CreditUserOperator creditUserOperator, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, creditUserOperator)){
			return form(creditUserOperator, model);
		}
		creditUserOperator.setPassword(EncoderUtil.encrypt(creditUserOperator.getPassword()));
		creditUserOperatorService.save(creditUserOperator);
		addMessage(redirectAttributes, "修改操作人员成功");
		return "redirect:"+Global.getAdminPath()+"/credit/cuoperator/creditUserOperator/list?creditUserId="+creditUserOperator.getCreditUserId();
	}
	
	
	@RequiresPermissions("credit:cuoperator:creditUserOperator:edit")
	@RequestMapping(value = "delete")
	public String delete(CreditUserOperator creditUserOperator, RedirectAttributes redirectAttributes) {
		creditUserOperatorService.delete(creditUserOperator);
		addMessage(redirectAttributes, "删除借款端，操作人成功");
		return "redirect:"+Global.getAdminPath()+"/credit/cuoperator/creditUserOperator/?repage";
	}

}