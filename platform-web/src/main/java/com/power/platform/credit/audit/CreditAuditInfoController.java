/**
 * Copyright &copy; 2012-2014 <a
 * href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.credit.audit;

import java.util.Date;

import javax.annotation.Resource;
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
import com.power.platform.credit.dao.apply.CreditUserApplyDao;
import com.power.platform.credit.dao.audit.CreditAuditInfoDao;
import com.power.platform.credit.entity.apply.CreditUserApply;
import com.power.platform.credit.entity.audit.CreditAuditInfo;
import com.power.platform.credit.service.audit.CreditAuditInfoService;

/**
 * 借款审核信息Controller
 * 
 * @author Roy
 * @version 2019-01-16
 */
@Controller
@RequestMapping(value = "${adminPath}/loan/audit/creditAuditInfo")
public class CreditAuditInfoController extends BaseController {

	@Autowired
	private CreditAuditInfoService creditAuditInfoService;
	@Resource
	private CreditAuditInfoDao creditAuditInfoDao;
	@Autowired
	private CreditUserApplyDao creditUserApplyDao;

	@ModelAttribute
	public CreditAuditInfo get(@RequestParam(required = false) String id) {

		CreditAuditInfo entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = creditAuditInfoService.get(id);
		}
		if (entity == null) {
			entity = new CreditAuditInfo();
		}
		return entity;
	}

	@RequiresPermissions("loan:audit:creditAuditInfo:view")
	@RequestMapping(value = { "list", "" })
	public String list(CreditAuditInfo creditAuditInfo, HttpServletRequest request, HttpServletResponse response, Model model) {

		Page<CreditAuditInfo> page = creditAuditInfoService.findPage(new Page<CreditAuditInfo>(request, response), creditAuditInfo);
		model.addAttribute("page", page);
		return "modules/loan/audit/creditAuditInfoList";
	}

	@RequiresPermissions("loan:audit:creditAuditInfo:view")
	@RequestMapping(value = "form")
	public String form(CreditAuditInfo creditAuditInfo, Model model) {

		model.addAttribute("creditAuditInfo", creditAuditInfo);
		return "modules/loan/audit/creditAuditInfoForm";
	}

	@RequiresPermissions("loan:audit:creditAuditInfo:view")
	@RequestMapping(value = "auditForm")
	public String auditForm(CreditAuditInfo creditAuditInfo, Model model) {

		model.addAttribute("creditAuditInfo", creditAuditInfo);
		return "modules/credit/userApply/audit";
	}

	@RequiresPermissions("loan:audit:creditAuditInfo:edit")
	@RequestMapping(value = "auditSave")
	public String auditSave(CreditAuditInfo creditAuditInfo, Model model, RedirectAttributes redirectAttributes) {

		// 借款申请审批信息.
		CreditAuditInfo entity = creditAuditInfoDao.get(creditAuditInfo.getId());
		if (entity != null) {
			creditAuditInfo.setUpdateDate(new Date());
			int updateFlag = creditAuditInfoDao.update(creditAuditInfo);
			if (updateFlag == 1) {
				logger.info("更新借款审核信息成功");
				addMessage(redirectAttributes, "更新借款审核信息成功");
			} else {
				logger.info("更新借款审核信息失败");
				addMessage(redirectAttributes, "更新借款审核信息失败");
			}
		} else {
			creditAuditInfo.setCreateDate(new Date());
			creditAuditInfo.setUpdateDate(new Date());
			int insertFlag = creditAuditInfoDao.insert(creditAuditInfo);
			if (insertFlag == 1) {
				logger.info("保存借款审核信息成功");
				addMessage(redirectAttributes, "保存借款审核信息成功");
			} else {
				logger.info("保存借款审核信息失败");
				addMessage(redirectAttributes, "保存借款审核信息失败");
			}
		}
		// 借款申请
		CreditUserApply creditUserApply = creditUserApplyDao.get(creditAuditInfo.getId());
		if (creditUserApply != null) {
			creditUserApply.setState(creditAuditInfo.getStatus()); // 通过/驳回
			int updateFlag = creditUserApplyDao.update(creditUserApply);
			if (updateFlag == 1) {
				logger.info("更新借款申请信息成功");
			} else {
				logger.info("更新借款申请信息失败");
			}
		}

		model.addAttribute("creditAuditInfo", creditAuditInfo);
		return "modules/credit/userApply/pass";
	}

	@RequiresPermissions("loan:audit:creditAuditInfo:edit")
	@RequestMapping(value = "save")
	public String save(CreditAuditInfo creditAuditInfo, Model model, RedirectAttributes redirectAttributes) {

		if (!beanValidator(model, creditAuditInfo)) {
			return form(creditAuditInfo, model);
		}
		creditAuditInfoService.save(creditAuditInfo);
		addMessage(redirectAttributes, "保存借款审核信息成功");
		return "redirect:" + Global.getAdminPath() + "/loan/audit/creditAuditInfo/?repage";
	}

	@RequiresPermissions("loan:audit:creditAuditInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(CreditAuditInfo creditAuditInfo, RedirectAttributes redirectAttributes) {

		creditAuditInfoService.delete(creditAuditInfo);
		addMessage(redirectAttributes, "删除借款审核信息成功");
		return "redirect:" + Global.getAdminPath() + "/loan/audit/creditAuditInfo/?repage";
	}

}