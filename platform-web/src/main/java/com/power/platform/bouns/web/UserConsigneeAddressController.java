package com.power.platform.bouns.web;

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

import com.power.platform.bouns.entity.UserConsigneeAddress;
import com.power.platform.bouns.services.UserConsigneeAddressService;
import com.power.platform.common.config.Global;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.utils.CommonStringUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.web.BaseController;
import com.power.platform.userinfo.entity.UserInfo;

/**
 * 用户收货地址Controller
 * @author Mr.Jia
 * @version 2016-12-13
 */
@Controller
@RequestMapping(value = "${adminPath}/bouns/userConsigneeAddress")
public class UserConsigneeAddressController extends BaseController {

	@Autowired
	private UserConsigneeAddressService userConsigneeAddressService;
	
	@ModelAttribute
	public UserConsigneeAddress get(@RequestParam(required=false) String id) {
		UserConsigneeAddress entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = userConsigneeAddressService.get(id);
		}
		if (entity == null){
			entity = new UserConsigneeAddress();
		}
		return entity;
	}
	
	@RequiresPermissions("bouns:userConsigneeAddress:view")
	@RequestMapping(value = {"list", ""})
	public String list(UserConsigneeAddress userConsigneeAddress, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<UserConsigneeAddress> page = userConsigneeAddressService.findPage(new Page<UserConsigneeAddress>(request, response), userConsigneeAddress); 
		List<UserConsigneeAddress> list = page.getList();
		for (int i = 0; i < list.size(); i++) {
			UserConsigneeAddress entity = list.get(i);
			UserInfo userInfo = entity.getUserInfo();
			if (userInfo != null) {
				userInfo.setName(CommonStringUtils.mobileEncrypt(userInfo.getName()));
				userInfo.setRealName(CommonStringUtils.replaceNameX(userInfo.getRealName()));
			}
			entity.setUsername(CommonStringUtils.replaceNameX(entity.getUsername()));
			entity.setMobile(CommonStringUtils.mobileEncrypt(entity.getMobile()));
		}
		model.addAttribute("page", page);
		return "modules/bouns/userConsigneeAddressList";
	}

	@RequiresPermissions("bouns:userConsigneeAddress:view")
	@RequestMapping(value = "form")
	public String form(UserConsigneeAddress userConsigneeAddress, Model model) {
		model.addAttribute("userConsigneeAddress", userConsigneeAddress);
		return "modules/bouns/userConsigneeAddressForm";
	}

	@RequiresPermissions("bouns:userConsigneeAddress:edit")
	@RequestMapping(value = "save")
	public String save(UserConsigneeAddress userConsigneeAddress, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, userConsigneeAddress)){
			return form(userConsigneeAddress, model);
		}
		userConsigneeAddressService.save(userConsigneeAddress);
		addMessage(redirectAttributes, "保存用户收货地址成功");
		return "redirect:"+Global.getAdminPath()+"/bouns/userConsigneeAddress/?repage";
	}
	
	@RequiresPermissions("bouns:userConsigneeAddress:edit")
	@RequestMapping(value = "delete")
	public String delete(UserConsigneeAddress userConsigneeAddress, RedirectAttributes redirectAttributes) {
		userConsigneeAddressService.delete(userConsigneeAddress);
		addMessage(redirectAttributes, "删除用户收货地址成功");
		return "redirect:"+Global.getAdminPath()+"/bouns/userConsigneeAddress/?repage";
	}

}