/**
 * Copyright &copy; 2012-2014 <a
 * href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.bouns.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.power.platform.bouns.dao.UserAwardDao;
import com.power.platform.bouns.entity.UserAward;
import com.power.platform.bouns.services.UserAwardService;
import com.power.platform.common.config.Global;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.utils.CommonStringUtils;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.utils.excel.ExportExcel;
import com.power.platform.common.web.BaseController;
import com.power.platform.sys.service.AreaService;
import com.power.platform.userinfo.entity.UserInfo;

/**
 * 用户兑换奖品Controller
 * 
 * @author yb
 * @version 2016-12-13
 */
@Controller
@RequestMapping(value = "${adminPath}/useraward/userAward")
public class UserAwardController extends BaseController {

	@Autowired
	private UserAwardService userAwardService;

	@Autowired
	private AreaService areaService;

	@Autowired
	private UserAwardDao userAwardDao;

	@ModelAttribute
	public UserAward get(@RequestParam(required = false) String id) {

		UserAward entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = userAwardService.get(id);
		}
		if (entity == null) {
			entity = new UserAward();
		}
		return entity;
	}

	@RequiresPermissions("useraward:userAward:view")
	@RequestMapping(value = { "list", "" })
	public String list(UserAward userAward, HttpServletRequest request, HttpServletResponse response, Model model) {

		Page<UserAward> page = new Page<UserAward>();
		if (userAward != null && userAward.getAwardGetType() != null && !userAward.getAwardGetType().equals("")) {
			if (userAward.getAwardGetType().equals("0")) {
				page = userAwardService.findPage0(new Page<UserAward>(request, response), userAward);
				List<UserAward> list = page.getList();
				for (int i = 0; i < list.size(); i++) {
					UserAward entity = list.get(i);
					UserInfo userInfo = entity.getUserInfo();
					if (userInfo != null) {
						userInfo.setName(CommonStringUtils.mobileEncrypt(userInfo.getName()));
						userInfo.setRealName(CommonStringUtils.replaceNameX(userInfo.getRealName()));
					}
				}
			} else if (userAward.getAwardGetType().equals("1")) {
				page = userAwardService.findPage1(new Page<UserAward>(request, response), userAward);
				List<UserAward> list = page.getList();
				for (int i = 0; i < list.size(); i++) {
					UserAward entity = list.get(i);
					UserInfo userInfo = entity.getUserInfo();
					if (userInfo != null) {
						userInfo.setName(CommonStringUtils.mobileEncrypt(userInfo.getName()));
						userInfo.setRealName(CommonStringUtils.replaceNameX(userInfo.getRealName()));
					}
				}
			}
		} else {
			page = userAwardService.findPage(new Page<UserAward>(request, response), userAward);
			List<UserAward> list = page.getList();
			for (int i = 0; i < list.size(); i++) {
				UserAward entity = list.get(i);
				UserInfo userInfo = entity.getUserInfo();
				if (userInfo != null) {
					userInfo.setName(CommonStringUtils.mobileEncrypt(userInfo.getName()));
					userInfo.setRealName(CommonStringUtils.replaceNameX(userInfo.getRealName()));
				}
			}
		}
		model.addAttribute("page", page);
		return "modules/bouns/userAwardList";
	}

	@RequiresPermissions("useraward:userAward:view")
	@RequestMapping(value = "export", method = RequestMethod.POST)
	public String exportFile(UserAward userAward, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {

		try {
			String fileName = "兑奖数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
			List<UserAward> findList = new ArrayList<UserAward>();
			if (userAward != null && userAward.getAwardGetType() != null && !userAward.getAwardGetType().equals("")) {
				if (userAward.getAwardGetType().equals("0")) {
					findList = userAwardDao.findNeedAmount0(userAward);
				} else if (userAward.getAwardGetType().equals("1")) {
					findList = userAwardDao.findNeedAmount1(userAward);
				}
			} else {
				findList = userAwardDao.findList(userAward);
			}
			new ExportExcel("兑奖数据", UserAward.class).setDataList(findList).write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出兑奖数据失败！失败信息：" + e.getMessage());
		}
		return "redirect:" + adminPath + "/useraward/userAward/list?repage";
	}

	@RequiresPermissions("useraward:userAward:view")
	@RequestMapping(value = "form")
	public String form(UserAward userAward, Model model) {

		model.addAttribute("userAward", userAward);
		return "modules/bouns/userAwardForm";
	}

	@RequiresPermissions("useraward:userAward:edit")
	@RequestMapping(value = "save")
	public String save(UserAward userAward, Model model, RedirectAttributes redirectAttributes) {

		if (!beanValidator(model, userAward)) {
			return form(userAward, model);
		}
		if (null != userAward) {
			userAward.setUpdateTime(new Date());
		}
		userAwardService.save(userAward);
		addMessage(redirectAttributes, "保存用户兑换奖品成功");
		return "redirect:" + Global.getAdminPath() + "/useraward/userAward/?repage";
	}

	@RequiresPermissions("useraward:userAward:edit")
	@RequestMapping(value = "delete")
	public String delete(UserAward userAward, RedirectAttributes redirectAttributes) {

		userAwardService.delete(userAward);
		addMessage(redirectAttributes, "删除用户兑换奖品成功");
		return "redirect:" + Global.getAdminPath() + "/useraward/userAward/?repage";
	}

}