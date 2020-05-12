package com.power.platform.weixin.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.power.platform.common.config.Global;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.web.BaseController;
import com.power.platform.weixin.entity.AccountMenu;
import com.power.platform.weixin.service.AccountMenuGroupService;
import com.power.platform.weixin.service.AccountMenuService;

/**
 * 微信菜单controller
 * @author lc
 *
 */
@Controller
@RequestMapping(value = "${adminPath}/wechat/accountmenu")
public class AccountMenuController extends BaseController{

	@Autowired
	private AccountMenuGroupService accountMenuGroupService;
	
	@Autowired
	private AccountMenuService accountMenuService;
	
	
	@ModelAttribute
	public AccountMenu get(@RequestParam(required = false) String id) {
		AccountMenu entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = accountMenuService.get(id);
		}
		if (entity == null) {
			entity = new AccountMenu();
		}
		return entity;
	}
/*	@RequiresPermissions("wechat:accountmenu:view")
	@RequestMapping(value = { "list", "" })
	public String list(AccountMenu accountMenu , HttpServletRequest request, HttpServletResponse response, Model model){
		 
		try {
			Page<AccountMenu> page = accountMenuService.findPage(new Page<AccountMenu>(request, response), accountMenu);
			model.addAttribute("page", page);
			model.addAttribute("accountMenuGroup", accountMenu.getAccountMenuGroup());	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "modules/wechat/wxcms/accountMenuList";
	}*/
	
	@RequiresPermissions("wechat:accountmenu:view")
	@RequestMapping(value = "form")
	public String form(AccountMenu accountMenu, String gid,  Model model) {
		
		/*AccountMenuGroup accountMenuGroup = accountMenuGroupService.get(gid);
		accountMenu.setAccountMenuGroup(accountMenuGroup);*/
		accountMenu.setParentid("0");
		List<AccountMenu> menuList = accountMenuService.parentMenuList(accountMenu);
		
		model.addAttribute("menuList", menuList);
		model.addAttribute("accountMenu", accountMenu);
		//model.addAttribute("accountMenuGroup", accountMenu.getAccountMenuGroup());
		return "modules/wechat/wxcms/accountMenuForm";
	}
	
	@RequiresPermissions("wechat:accountmenu:edit")
	@RequestMapping(value = "save")
	public String save(AccountMenu accountMenu,  Model model, RedirectAttributes redirectAttributes, HttpServletRequest request) {
		try {
			 
			accountMenuService.save(accountMenu);
			System.out.println(accountMenu.getAccountMenuGroup().getId());
			addMessage(redirectAttributes, "保存成功");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:"+Global.getAdminPath()+"/wechat/accountmenugroup/form?id="+accountMenu.getAccountMenuGroup().getId();
	}
	
	@RequiresPermissions("wechat:accountmenu:edit")
	@RequestMapping(value = "delete")
	public String delete(AccountMenu accountMenu,  RedirectAttributes redirectAttributes) {
		String Id = "";
		try {
			Id = accountMenu.getAccountMenuGroup().getId();
			accountMenuService.delete(accountMenu);
			addMessage(redirectAttributes, "删除菜单组成功");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:"+Global.getAdminPath()+"/wechat/accountmenugroup/form?id="+Id;
	}

}

