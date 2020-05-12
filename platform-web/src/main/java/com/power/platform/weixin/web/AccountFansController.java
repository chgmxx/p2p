package com.power.platform.weixin.web;

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
import com.power.platform.weixin.entity.AccountFans;
import com.power.platform.weixin.service.AccountFansService;


/**
 * 微信粉丝Controller
 * @author lc
 *
 */
@Controller
@RequestMapping(value = "${adminPath}/wechat/accountfans")
public class AccountFansController extends BaseController{

	@Autowired
	private AccountFansService accountFansService;
	
	@ModelAttribute
	public AccountFans get(@RequestParam(required = false) String id) {
		AccountFans entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = accountFansService.get(id);
		}
		if (entity == null) {
			entity = new AccountFans();
		}
		return entity;
	}
	
	@RequiresPermissions("wechat:accountfans:view")
	@RequestMapping(value = { "list", "" })
	public String list(AccountFans accountFans, HttpServletRequest request, HttpServletResponse response, Model model){
		try {
			Page<AccountFans> page = accountFansService.findPage(new Page<AccountFans>(request, response), accountFans);
			model.addAttribute("page", page);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "modules/wechat/wxcms/accountFansList";
	}
	
	@RequiresPermissions("wechat:accountfans:view")
	@RequestMapping(value = "form")
	public String form(AccountFans accountFans, Model model) {
		model.addAttribute("accountFans", accountFans);
		return "modules/wechat/wxcms/accountFansForm";
	}
	
	
	@RequiresPermissions("wechat:accountfans:edit")
	@RequestMapping(value = "save")
	public String save(AccountFans accountFans,  Model model, RedirectAttributes redirectAttributes, HttpServletRequest request) {
		try {
			accountFansService.save(accountFans);
			addMessage(redirectAttributes, "保存成功");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:"+Global.getAdminPath()+"/wechat/accountfans/list";
	}
	
	@RequiresPermissions("wechat:accountfans:edit")
	@RequestMapping(value = "delete")
	public String delete(AccountFans accountFans,  RedirectAttributes redirectAttributes) {
		accountFansService.delete(accountFans);
		addMessage(redirectAttributes, "删除成功");
		return "redirect:"+Global.getAdminPath()+"/wechat/accountfans/list";
	}
	


}