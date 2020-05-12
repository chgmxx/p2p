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

import com.power.platform.bouns.entity.UserBounsPoint;
import com.power.platform.bouns.services.UserBounsPointService;
import com.power.platform.common.config.Global;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.utils.CommonStringUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.web.BaseController;
import com.power.platform.userinfo.entity.UserInfo;


/**
 * 用户积分信息Controller
 * @author Mr.Jia
 * @version 2016-12-13
 */
@Controller
@RequestMapping(value = "${adminPath}/bouns/userBounsPoint")
public class UserBounsPointController extends BaseController {

	@Autowired
	private UserBounsPointService userBounsPointService;
	
	@ModelAttribute
	public UserBounsPoint get(@RequestParam(required=false) String id) {
		UserBounsPoint entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = userBounsPointService.get(id);
		}
		if (entity == null){
			entity = new UserBounsPoint();
		}
		return entity;
	}
	
	@RequiresPermissions("bouns:userBounsPoint:view")
	@RequestMapping(value = {"list", ""})
	public String list(UserBounsPoint userBounsPoint, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<UserBounsPoint> page = userBounsPointService.findPage(new Page<UserBounsPoint>(request, response), userBounsPoint); 
		List<UserBounsPoint> list = page.getList();
		for (int i = 0; i < list.size(); i++) {
			UserBounsPoint entity = list.get(i);
			UserInfo userInfo = entity.getUserInfo();
			if (userInfo != null) {
				userInfo.setName(CommonStringUtils.mobileEncrypt(userInfo.getName()));
				userInfo.setRealName(CommonStringUtils.replaceNameX(userInfo.getRealName()));
			}
		}
		model.addAttribute("page", page);
		return "modules/bouns/userBounsPointList";
	}

	@RequiresPermissions("bouns:userBounsPoint:view")
	@RequestMapping(value = "form")
	public String form(UserBounsPoint userBounsPoint, Model model) {
		model.addAttribute("userBounsPoint", userBounsPoint);
		return "modules/bouns/userBounsPointForm";
	}

	@RequiresPermissions("bouns:userBounsPoint:edit")
	@RequestMapping(value = "save")
	public String save(UserBounsPoint userBounsPoint, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, userBounsPoint)){
			return form(userBounsPoint, model);
		}
		userBounsPointService.save(userBounsPoint);
		addMessage(redirectAttributes, "保存用户积分信息成功");
		return "redirect:"+Global.getAdminPath()+"/bouns/userBounsPoint/?repage";
	}
	
	@RequiresPermissions("bouns:userBounsPoint:edit")
	@RequestMapping(value = "delete")
	public String delete(UserBounsPoint userBounsPoint, RedirectAttributes redirectAttributes) {
		userBounsPointService.delete(userBounsPoint);
		addMessage(redirectAttributes, "删除用户积分信息成功");
		return "redirect:"+Global.getAdminPath()+"/bouns/userBounsPoint/?repage";
	}

}