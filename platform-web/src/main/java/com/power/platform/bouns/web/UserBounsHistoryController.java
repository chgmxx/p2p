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

import com.power.platform.bouns.entity.UserBounsHistory;
import com.power.platform.bouns.services.UserBounsHistoryService;
import com.power.platform.common.config.Global;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.utils.CommonStringUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.web.BaseController;
import com.power.platform.userinfo.entity.UserInfo;

/**
 * 用户积分历史明细Controller
 * @author Mr.Jia
 * @version 2016-12-13
 */
@Controller
@RequestMapping(value = "${adminPath}/bouns/userBounsHistory")
public class UserBounsHistoryController extends BaseController {

	@Autowired
	private UserBounsHistoryService userBounsHistoryService;
	
	@ModelAttribute
	public UserBounsHistory get(@RequestParam(required=false) String id) {
		UserBounsHistory entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = userBounsHistoryService.get(id);
		}
		if (entity == null){
			entity = new UserBounsHistory();
		}
		return entity;
	}
	
	@RequiresPermissions("bouns:userBounsHistory:view")
	@RequestMapping(value = {"list", ""})
	public String list(UserBounsHistory userBounsHistory, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<UserBounsHistory> page = userBounsHistoryService.findPage(new Page<UserBounsHistory>(request, response), userBounsHistory); 
		List<UserBounsHistory> list = page.getList();
		for (int i = 0; i < list.size(); i++) {
			UserBounsHistory entity= list.get(i);
			UserInfo userInfo = entity.getUserInfo();
			if (userInfo != null) {
				userInfo.setName(CommonStringUtils.mobileEncrypt(userInfo.getName()));
				userInfo.setRealName(CommonStringUtils.replaceNameX(userInfo.getRealName()));
			}
		}
		model.addAttribute("page", page);
		return "modules/bouns/userBounsHistoryList";
	}

	@RequiresPermissions("bouns:userBounsHistory:view")
	@RequestMapping(value = "form")
	public String form(UserBounsHistory userBounsHistory, Model model) {
		model.addAttribute("userBounsHistory", userBounsHistory);
		return "modules/bouns/userBounsHistoryForm";
	}

	@RequiresPermissions("bouns:userBounsHistory:edit")
	@RequestMapping(value = "save")
	public String save(UserBounsHistory userBounsHistory, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, userBounsHistory)){
			return form(userBounsHistory, model);
		}
		userBounsHistoryService.save(userBounsHistory);
		addMessage(redirectAttributes, "保存用户积分历史明细成功");
		return "redirect:"+Global.getAdminPath()+"/bouns/userBounsHistory/?repage";
	}
	
	@RequiresPermissions("bouns:userBounsHistory:edit")
	@RequestMapping(value = "delete")
	public String delete(UserBounsHistory userBounsHistory, RedirectAttributes redirectAttributes) {
		userBounsHistoryService.delete(userBounsHistory);
		addMessage(redirectAttributes, "删除用户积分历史明细成功");
		return "redirect:"+Global.getAdminPath()+"/bouns/userBounsHistory/?repage";
	}

}