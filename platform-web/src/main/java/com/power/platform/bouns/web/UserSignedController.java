package com.power.platform.bouns.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.power.platform.bouns.entity.UserSigned;
import com.power.platform.bouns.services.UserSignedService;
import com.power.platform.common.config.Global;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.utils.CommonStringUtils;
import com.power.platform.common.web.BaseController;
import com.power.platform.userinfo.entity.UserInfo;

/**
 * 
 * 类: UserSignedController <br>
 * 描述: 客户签到Controller. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年12月13日 下午4:00:08
 */
@Controller
@RequestMapping(value = "${adminPath}/bouns/userSigned")
public class UserSignedController extends BaseController {

	@Autowired
	private UserSignedService userSignedService;

	@ModelAttribute
	public UserSigned get(@RequestParam(required = false) String id) {

		UserSigned entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = userSignedService.get(id);
		}
		if (entity == null) {
			entity = new UserSigned();
		}
		return entity;
	}

	@RequiresPermissions("bouns:userSigned:view")
	@RequestMapping(value = { "list", "" })
	public String list(UserSigned userSigned, HttpServletRequest request, HttpServletResponse response, Model model) {

		Page<UserSigned> page = userSignedService.findPage(new Page<UserSigned>(request, response), userSigned);
		List<UserSigned> list = page.getList();
		for (int i = 0; i < list.size(); i++) {
			UserSigned entity = list.get(i);
			UserInfo userInfo = entity.getUserInfo();
			if (userInfo != null) {
				userInfo.setName(CommonStringUtils.mobileEncrypt(userInfo.getName()));
				userInfo.setRealName(CommonStringUtils.replaceNameX(userInfo.getRealName()));
			}
		}
		model.addAttribute("page", page);
		return "modules/bouns/userSignedList";
	}

	@RequiresPermissions("bouns:userSigned:view")
	@RequestMapping(value = "form")
	public String form(UserSigned userSigned, Model model) {

		model.addAttribute("userSigned", userSigned);
		return "modules/bouns/userSignedForm";
	}

	@RequiresPermissions("bouns:userSigned:edit")
	@RequestMapping(value = "save")
	public String save(UserSigned userSigned, Model model, RedirectAttributes redirectAttributes) {

		if (!beanValidator(model, userSigned)) {
			return form(userSigned, model);
		}
		userSignedService.save(userSigned);
		addMessage(redirectAttributes, "保存客户签到成功");
		return "redirect:" + Global.getAdminPath() + "/bouns/userSigned/?repage";
	}

	@RequiresPermissions("bouns:userSigned:edit")
	@RequestMapping(value = "delete")
	public String delete(UserSigned userSigned, RedirectAttributes redirectAttributes) {

		userSignedService.delete(userSigned);
		addMessage(redirectAttributes, "删除客户签到成功");
		return "redirect:" + Global.getAdminPath() + "/bouns/userSigned/?repage";
	}

}