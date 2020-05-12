package com.power.platform.credit.supplierToMiddlemen;

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
import com.power.platform.credit.entity.supplierToMiddlemen.CreditSupplierToMiddlemen;
import com.power.platform.credit.service.supplierToMiddlemen.CreditSupplierToMiddlemenService;

/**
 * 
 * 类: CreditSupplierToMiddlemenController <br>
 * 描述: 借代中间表Controller. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2018年1月10日 上午10:44:10
 */
@Controller
@RequestMapping(value = "${adminPath}/credit/creditSupplierToMiddlemen")
public class CreditSupplierToMiddlemenController extends BaseController {

	@Autowired
	private CreditSupplierToMiddlemenService creditSupplierToMiddlemenService;

	@ModelAttribute
	public CreditSupplierToMiddlemen get(@RequestParam(required = false) String id) {

		CreditSupplierToMiddlemen entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = creditSupplierToMiddlemenService.get(id);
		}
		if (entity == null) {
			entity = new CreditSupplierToMiddlemen();
		}
		return entity;
	}

	@RequiresPermissions("credit:creditSupplierToMiddlemen:view")
	@RequestMapping(value = { "list", "" })
	public String list(CreditSupplierToMiddlemen creditSupplierToMiddlemen, HttpServletRequest request, HttpServletResponse response, Model model) {

		Page<CreditSupplierToMiddlemen> page = creditSupplierToMiddlemenService.findPage(new Page<CreditSupplierToMiddlemen>(request, response), creditSupplierToMiddlemen);
		model.addAttribute("page", page);
		return "modules/credit/supplierToMiddlemen/creditSupplierToMiddlemenList";
	}

	@RequiresPermissions("credit:creditSupplierToMiddlemen:view")
	@RequestMapping(value = "form")
	public String form(CreditSupplierToMiddlemen creditSupplierToMiddlemen, Model model) {

		model.addAttribute("creditSupplierToMiddlemen", creditSupplierToMiddlemen);
		return "modules/credit/supplierToMiddlemen/creditSupplierToMiddlemenForm";
	}

	@RequiresPermissions("credit:creditSupplierToMiddlemen:edit")
	@RequestMapping(value = "save")
	public String save(CreditSupplierToMiddlemen creditSupplierToMiddlemen, Model model, RedirectAttributes redirectAttributes) {

		if (!beanValidator(model, creditSupplierToMiddlemen)) {
			return form(creditSupplierToMiddlemen, model);
		}
		creditSupplierToMiddlemenService.save(creditSupplierToMiddlemen);
		addMessage(redirectAttributes, "保存借代中间表成功");
		return "redirect:" + Global.getAdminPath() + "/credit/creditSupplierToMiddlemen/?repage";
	}

	@RequiresPermissions("credit:creditSupplierToMiddlemen:edit")
	@RequestMapping(value = "delete")
	public String delete(CreditSupplierToMiddlemen creditSupplierToMiddlemen, RedirectAttributes redirectAttributes) {

		creditSupplierToMiddlemenService.delete(creditSupplierToMiddlemen);
		addMessage(redirectAttributes, "删除借代中间表成功");
		return "redirect:" + Global.getAdminPath() + "/credit/creditSupplierToMiddlemen/?repage";
	}

}