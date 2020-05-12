package com.power.platform.activity.web;

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

import com.power.platform.activity.entity.LevelDistribution;
import com.power.platform.activity.service.LevelDistributionService;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.web.BaseController;
import com.power.platform.common.utils.CommonStringUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.userinfo.entity.UserInfo;

/**
 * 三级分销客户关系管理Controller
 * 
 * @author lc
 * @version 2016-03-23
 */
@Controller
@RequestMapping(value = "${adminPath}/levelDistribution")
public class LevelDistributionController extends BaseController {

	@Autowired
	private LevelDistributionService levelDistributionService;

	@ModelAttribute
	public LevelDistribution get(@RequestParam(required = false) String id) {

		LevelDistribution entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = levelDistributionService.get(id);
		}
		if (entity == null) {
			entity = new LevelDistribution();
		}
		return entity;
	}

	@RequiresPermissions("levelDistribution:view")
	@RequestMapping(value = { "list", "" })
	public String list(LevelDistribution levelDistribution, HttpServletRequest request, HttpServletResponse response, Model model) {

		Page<LevelDistribution> page = levelDistributionService.findPage(new Page<LevelDistribution>(request, response), levelDistribution);
		List<LevelDistribution> list = page.getList();
		for (int i = 0; i < list.size(); i++) {
			LevelDistribution entity = list.get(i);
			UserInfo userInfo = entity.getUserInfo();
			if (userInfo != null) {
				userInfo.setName(CommonStringUtils.mobileEncrypt(userInfo.getName()));
				userInfo.setRealName(CommonStringUtils.replaceNameX(userInfo.getRealName()));
			}
			UserInfo parentUserInfo = entity.getParentUserInfo();
			if (parentUserInfo != null) {
				parentUserInfo.setName(CommonStringUtils.mobileEncrypt(parentUserInfo.getName()));
				parentUserInfo.setRealName(CommonStringUtils.replaceNameX(parentUserInfo.getRealName()));
			}
		}
		model.addAttribute("page", page);
		return "modules/levelDistribution/levelDistributionList";
	}

	/*
	 * @RequiresPermissions("levelDistribution:view")
	 * 
	 * @RequestMapping(value = "form")
	 * public String form(LevelDistribution levelDistribution, Model model) {
	 * model.addAttribute("levelDistribution", levelDistribution);
	 * return "modules/levelDistribution/levelDistributionForm";
	 * }
	 * 
	 * @RequiresPermissions("levelDistribution:edit")
	 * 
	 * @RequestMapping(value = "save")
	 * public String save(LevelDistribution levelDistribution, Model model,
	 * RedirectAttributes redirectAttributes) {
	 * if (!beanValidator(model, levelDistribution)){
	 * return form(levelDistribution, model);
	 * }
	 * levelDistributionService.save(levelDistribution);
	 * addMessage(redirectAttributes, "保存用户信息管理成功");
	 * return "redirect:"+Global.getAdminPath()+"/levelDistribution/?repage";
	 * }
	 * 
	 * @RequiresPermissions("levelDistribution:edit")
	 * 
	 * @RequestMapping(value = "delete")
	 * public String delete(LevelDistribution levelDistribution,
	 * RedirectAttributes redirectAttributes) {
	 * levelDistributionService.delete(levelDistribution);
	 * addMessage(redirectAttributes, "删除用户信息管理成功");
	 * return "redirect:"+Global.getAdminPath()+"/levelDistribution/?repage";
	 * }
	 */

}