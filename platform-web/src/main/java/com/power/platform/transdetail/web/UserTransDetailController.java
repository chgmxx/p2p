/**
 * Copyright &copy; 2012-2014 <a
 * href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.transdetail.web;

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
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.utils.excel.ExportExcel;
import com.power.platform.trandetail.entity.UserTransDetail;
import com.power.platform.trandetail.service.UserTransDetailService;
import com.power.platform.userinfo.entity.UserCheckAccount;
import com.power.platform.userinfo.entity.UserCheckOrder;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.userinfo.service.UserCheckAccountService;
import com.power.platform.userinfo.service.UserCheckOrderService;

/**
 * 客户流水记录Controller
 * 
 * @author soler
 * @version 2015-12-23
 */
@Controller
@RequestMapping(value = "${adminPath}/transdetail/userTransDetail")
public class UserTransDetailController extends BaseController {

	@Autowired
	private UserTransDetailService userTransDetailService;
	@Autowired
	private UserCheckAccountService userCheckAccountService;
	@Autowired
	private UserCheckOrderService userCheckOrderService;

	@ModelAttribute
	public UserTransDetail get(@RequestParam(required = false) String id) {

		UserTransDetail entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = userTransDetailService.get(id);
		}
		if (entity == null) {
			entity = new UserTransDetail();
		}
		return entity;
	}

	@RequiresPermissions("transdetail:userTransDetail:view")
	@RequestMapping(value = { "list", "" })
	public String list(UserTransDetail userTransDetail, HttpServletRequest request, HttpServletResponse response, Model model) {

		Page<UserTransDetail> page = userTransDetailService.findPage(new Page<UserTransDetail>(request, response), userTransDetail);
		List<UserTransDetail> list = page.getList();
		for (int i = 0; i < list.size(); i++) {
			UserTransDetail entity = list.get(i);
			UserInfo userInfo = entity.getUserInfo();
			if (userInfo != null) {
				userInfo.setName(CommonStringUtils.mobileEncrypt(userInfo.getName()));
				userInfo.setRealName(CommonStringUtils.replaceNameX(userInfo.getRealName()));
			}
		}

		model.addAttribute("page", page);
		return "modules/transdetail/userTransDetailList";
	}

	@RequiresPermissions("transdetail:userTransDetail:view")
	@RequestMapping(value = "form")
	public String form(UserTransDetail userTransDetail, Model model) {

		model.addAttribute("userTransDetail", userTransDetail);
		return "modules/transdetail/userTransDetailForm";
	}

	@RequiresPermissions("transdetail:userTransDetail:edit")
	@RequestMapping(value = "save")
	public String save(UserTransDetail userTransDetail, Model model, RedirectAttributes redirectAttributes) {

		if (!beanValidator(model, userTransDetail)) {
			return form(userTransDetail, model);
		}
		userTransDetailService.save(userTransDetail);
		addMessage(redirectAttributes, "保存客户流水记录成功");
		return "redirect:" + Global.getAdminPath() + "/transdetail/userTransDetail/?repage";
	}

	@RequiresPermissions("transdetail:userTransDetail:edit")
	@RequestMapping(value = "delete")
	public String delete(UserTransDetail userTransDetail, RedirectAttributes redirectAttributes) {

		userTransDetailService.delete(userTransDetail);
		addMessage(redirectAttributes, "删除客户流水记录成功");
		return "redirect:" + Global.getAdminPath() + "/transdetail/userTransDetail/?repage";
	}

	/**
	 * 导出客户交易流水
	 * 
	 * @param userTransDetail
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("transdetail:userTransDetail:view")
	@RequestMapping(value = "exportdetail")
	public String exportDetail(UserTransDetail userTransDetail, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {

		// String message = userTransDetailService.exportExcel(userTransDetail);

		try {
			String fileName = "交易流水【客户】" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
			userTransDetail.setState(UserTransDetail.TRANS_STATE_SUCCESS);
			Page<UserTransDetail> page = new Page<UserTransDetail>();
			page.setOrderBy("a.trans_date DESC");
			userTransDetail.setPage(page);
			List<UserTransDetail> list = userTransDetailService.findList(userTransDetail);
			new ExportExcel("交易流水【客户】", UserTransDetail.class).setDataList(list).write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "交易流水【客户】导出异常，失败信息：" + e.getMessage());
		}

		// addMessage(redirectAttributes, message);
		return "redirect:" + Global.getAdminPath() + "/transdetail/userTransDetail/?repage";
	}

	/**
	 * 导出对账结果
	 * 
	 * @param userTransDetail
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("transdetail:userTransDetail:view")
	@RequestMapping(value = "checkaccount")
	public String checkAccount(UserTransDetail userTransDetail, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {

		try {
			String fileName = "账户对账问题用户.xlsx";
			List<UserCheckAccount> list = userCheckAccountService.findAllList();
			new ExportExcel("账户对账", UserCheckAccount.class).setDataList(list).write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出对账结果失败！失败信息：" + e.getMessage());
		}

		// addMessage(redirectAttributes, message);
		return "redirect:" + Global.getAdminPath() + "/transdetail/userTransDetail/?repage";
	}

	/**
	 * 导出充值提现订单对账结果
	 * 
	 * @param userTransDetail
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("transdetail:userTransDetail:view")
	@RequestMapping(value = "checkorder")
	public String checkorder(UserTransDetail userTransDetail, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {

		try {
			String fileName = "充值提现订单对账.xlsx";
			List<UserCheckOrder> list = userCheckOrderService.findAllList();
			new ExportExcel("充值提现订单对账", UserCheckOrder.class).setDataList(list).write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出订单对账结果失败！失败信息：" + e.getMessage());
		}

		// addMessage(redirectAttributes, message);
		return "redirect:" + Global.getAdminPath() + "/transdetail/userTransDetail/?repage";
	}

}