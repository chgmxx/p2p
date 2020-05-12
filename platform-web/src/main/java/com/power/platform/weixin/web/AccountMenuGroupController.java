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
import com.power.platform.weixin.entity.AccountMenu;
import com.power.platform.weixin.entity.AccountMenuGroup;
import com.power.platform.weixin.service.AccountMenuGroupService;
import com.power.platform.weixin.service.AccountMenuService;
 

/**
 * 菜单组controller
 * @author lc
 *
 */
@Controller
@RequestMapping(value = "${adminPath}/wechat/accountmenugroup")
public class AccountMenuGroupController extends BaseController{

	@Autowired
	private AccountMenuGroupService accountMenuGroupService;
	
	@Autowired
	private AccountMenuService accountMenuService;
	
	
	@ModelAttribute
	public AccountMenuGroup get(@RequestParam(required = false) String id) {
		AccountMenuGroup entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = accountMenuGroupService.get(id);
		}
		if (entity == null) {
			entity = new AccountMenuGroup();
		}
		return entity;
	}
	
	
	
	@RequiresPermissions("wechat:accountmenugroup:view")
	@RequestMapping(value = { "list", "" })
	public String list(AccountMenuGroup accountMenuGroup, HttpServletRequest request, HttpServletResponse response, Model model){
		 
		Page<AccountMenuGroup> page = accountMenuGroupService.findPage(new Page<AccountMenuGroup>(request, response), accountMenuGroup);
		model.addAttribute("page", page);
	
		return "modules/wechat/wxcms/accountMenuGroupList";
	}
	
	@RequiresPermissions("wechat:accountmenugroup:view")
	@RequestMapping(value = "form")
	public String form(AccountMenuGroup accountMenuGroup, HttpServletRequest request, HttpServletResponse response, Model model) {
		
		try {
			if(accountMenuGroup.getId()!=null && !accountMenuGroup.getId().equals("")){
				AccountMenu accountMenu = new AccountMenu();
				accountMenu.setAccountMenuGroup(accountMenuGroup);
				Page<AccountMenu> page = accountMenuService.findPage(new Page<AccountMenu>(request, response), accountMenu);
				model.addAttribute("page", page);
			}
			model.addAttribute("accountMenuGroup", accountMenuGroup);	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "modules/wechat/wxcms/accountMenuList";
	}
	
	@RequiresPermissions("wechat:accountmenugroup:edit")
	@RequestMapping(value = "save")
	public String save(AccountMenuGroup accountMenuGroup,  Model model, RedirectAttributes redirectAttributes) {
		try {
			accountMenuGroupService.save(accountMenuGroup);
			addMessage(redirectAttributes, "保存成功");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:"+ Global.getAdminPath() +"/wechat/accountmenugroup/list";
	}
	
	@RequiresPermissions("wechat:accountmenugroup:edit")
	@RequestMapping(value = "delete")
	public String delete(AccountMenuGroup accountMenuGroup,  RedirectAttributes redirectAttributes) {
		accountMenuGroupService.delete(accountMenuGroup);
		addMessage(redirectAttributes, "删除菜单组成功");
		return "redirect:"+Global.getAdminPath()+"/wechat/accountmenugroup/list";
	}
	
 

}

