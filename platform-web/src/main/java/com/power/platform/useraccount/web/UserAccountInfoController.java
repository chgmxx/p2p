/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.useraccount.web;

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
import com.power.platform.common.web.BaseController;
import com.power.platform.common.utils.CommonStringUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.userinfo.entity.UserAccountInfo;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.userinfo.service.UserAccountInfoService;

/**
 * 账户管理Controller
 * @author jiajunfeng
 * @version 2015-12-18
 */
@Controller
@RequestMapping(value = "${adminPath}/useraccount/userAccountInfo")
public class UserAccountInfoController extends BaseController {

	@Autowired
	private UserAccountInfoService userAccountInfoService;
	
	@ModelAttribute
	public UserAccountInfo get(@RequestParam(required=false) String id) {
		UserAccountInfo entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = userAccountInfoService.get(id);
		}
		if (entity == null){
			entity = new UserAccountInfo();
		}
		return entity;
	}
	
	@RequiresPermissions("useraccount:userAccountInfo:view")
	@RequestMapping(value = {"list", ""})
	public String list(UserAccountInfo userAccountInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<UserAccountInfo> page = userAccountInfoService.findPage(new Page<UserAccountInfo>(request, response), userAccountInfo); 

		List<UserAccountInfo> list = page.getList();
		for (int i = 0; i < list.size(); i++) {
			UserAccountInfo entity = list.get(i);
			UserInfo userInfo = entity.getUserInfo();
			if (userInfo != null) {
				userInfo.setName(CommonStringUtils.mobileEncrypt(userInfo.getName()));
				userInfo.setRealName(CommonStringUtils.replaceNameX(userInfo.getRealName()));
			}
		}

		model.addAttribute("page", page);
		return "modules/useraccount/userAccountInfoList";
	}

	@RequiresPermissions("useraccount:userAccountInfo:view")
	@RequestMapping(value = "form")
	public String form(UserAccountInfo userAccountInfo, Model model) {
		model.addAttribute("userAccountInfo", userAccountInfo);
		return "modules/useraccount/userAccountInfoForm";
	}

	@RequiresPermissions("useraccount:userAccountInfo:edit")
	@RequestMapping(value = "save")
	public String save(UserAccountInfo userAccountInfo, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, userAccountInfo)){
			return form(userAccountInfo, model);
		}
		userAccountInfoService.save(userAccountInfo);
		addMessage(redirectAttributes, "保存账户管理成功");
		return "redirect:"+Global.getAdminPath()+"/useraccount/userAccountInfo/?repage";
	}
	
	@RequiresPermissions("useraccount:userAccountInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(UserAccountInfo userAccountInfo, RedirectAttributes redirectAttributes) {
		userAccountInfoService.delete(userAccountInfo);
		addMessage(redirectAttributes, "删除账户管理成功");
		return "redirect:"+Global.getAdminPath()+"/useraccount/userAccountInfo/?repage";
	}

}