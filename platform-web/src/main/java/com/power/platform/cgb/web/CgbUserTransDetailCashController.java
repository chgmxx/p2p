/**
 * 银行托管-流水-Controller.
 */
package com.power.platform.cgb.web;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.power.platform.cgb.dao.CgbUserTransDetailDao;
import com.power.platform.cgb.entity.CgbUserTransDetail;
import com.power.platform.cgb.entity.CgbUserTransDetailExport;
import com.power.platform.cgb.service.CgbCheckAccountService;
import com.power.platform.cgb.service.CgbUserTransDetailService;
import com.power.platform.common.config.Global;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.utils.CommonStringUtils;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.utils.excel.ExportExcel;
import com.power.platform.common.web.BaseController;
import com.power.platform.credit.dao.userinfo.CreditUserInfoDao;
import com.power.platform.userinfo.entity.UserInfo;

/**
 * 银行托管-流水-Controller.
 * 
 * @author lance
 * @version 2017-10-26
 */
@Controller
@RequestMapping(value = "${adminPath}/cgb/cgbUserTransDetailCash")
public class CgbUserTransDetailCashController extends BaseController {

	private static final Logger log = LoggerFactory.getLogger(CgbUserTransDetailCashController.class);

	@Autowired
	private CgbUserTransDetailService cgbUserTransDetailService;
	@Resource
	private CreditUserInfoDao creditUserInfoDao;
	@Resource
	private CgbUserTransDetailDao cgbUserTransDetailDao;
	@Autowired
	private CgbCheckAccountService cgbCheckAccountService;

	@ModelAttribute
	public CgbUserTransDetail get(@RequestParam(required = false) String id) {

		CgbUserTransDetail entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = cgbUserTransDetailService.get(id);
		}
		if (entity == null) {
			entity = new CgbUserTransDetail();
		}
		return entity;
	}

	@RequiresPermissions("cgb:cgbUserTransDetailCash:view")
	@RequestMapping(value = { "list", "" })
	public String list(CgbUserTransDetail cgbUserTransDetail, HttpServletRequest request, HttpServletResponse response, Model model) {
		log.info("fn:list,客户提现流水列表查询 ...");
		Page<CgbUserTransDetail> page = cgbUserTransDetailService.findPage1(new Page<CgbUserTransDetail>(request, response), cgbUserTransDetail);
		List<CgbUserTransDetail> list = page.getList();
		for (int i = 0; i < list.size(); i++) {
			CgbUserTransDetail entity = list.get(i);
			UserInfo userInfo = entity.getUserInfo();
			if (userInfo != null) {
				userInfo.setName(CommonStringUtils.mobileEncrypt(userInfo.getName()));
				userInfo.setRealName(CommonStringUtils.replaceNameX(userInfo.getRealName()));
			}
		}
		model.addAttribute("page", page);
		return "modules/cgb/userTransDetail/cgbUserTransDetailCashList";
	}

	/**
	 * 
	 * 方法: exportTransDetail <br>
	 * 描述: 导出. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2018年1月16日 上午10:53:42
	 * 
	 * @param cgbUserTransDetail
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequiresPermissions("cgb:cgbUserTransDetailCash:view")
	@RequestMapping(value = "export", method = RequestMethod.POST)
	public String exportTransDetail(CgbUserTransDetail cgbUserTransDetail, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {

		try {
			String fileName = "出借人提现流水数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
			List<CgbUserTransDetail> list = cgbUserTransDetailDao.findList1(cgbUserTransDetail);
			new ExportExcel("出借人提现流水数据", CgbUserTransDetailExport.class).setDataList(list).write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出兑奖数据失败！失败信息：" + e.getMessage());
		}
		return "redirect:" + Global.getAdminPath() + "/cgb/cgbUserTransDetailCash/?repage";
	}

	@RequiresPermissions("cgb:cgbUserTransDetailCash:view")
	@RequestMapping(value = "form")
	public String form(CgbUserTransDetail cgbUserTransDetail, Model model) {

		model.addAttribute("cgbUserTransDetail", cgbUserTransDetail);
		return "modules/cgb/userTransDetail/cgbUserTransDetailForm";
	}

	@RequiresPermissions("cgb:cgbUserTransDetailCash:edit")
	@RequestMapping(value = "save")
	public String save(CgbUserTransDetail cgbUserTransDetail, Model model, RedirectAttributes redirectAttributes) {

		if (!beanValidator(model, cgbUserTransDetail)) {
			return form(cgbUserTransDetail, model);
		}
		cgbUserTransDetailService.save(cgbUserTransDetail);
		addMessage(redirectAttributes, "保存银行托管-流水成功");
		return "redirect:" + Global.getAdminPath() + "/cgb/cgbUserTransDetail/?repage";
	}

	@RequiresPermissions("cgb:cgbUserTransDetailCash:edit")
	@RequestMapping(value = "delete")
	public String delete(CgbUserTransDetail cgbUserTransDetail, RedirectAttributes redirectAttributes) {

		cgbUserTransDetailService.delete(cgbUserTransDetail);
		addMessage(redirectAttributes, "删除银行托管-流水成功");
		return "redirect:" + Global.getAdminPath() + "/cgb/cgbUserTransDetail/?repage";
	}

}