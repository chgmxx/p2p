/**
 * 银行托管-流水-Controller.
 */
package com.power.platform.cgb.web;

import java.text.DecimalFormat;
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
import com.power.platform.cgb.entity.CgbCheckAccount;
import com.power.platform.cgb.entity.CgbUserTransDetail;
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
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.userinfo.entity.UserInfo;

/**
 * 银行托管-流水-Controller.
 * 
 * @author lance
 * @version 2017-10-26
 */
@Controller
@RequestMapping(value = "${adminPath}/cgb/cgbUserTransDetail")
public class CgbUserTransDetailController extends BaseController {

	private static final Logger log = LoggerFactory.getLogger(CgbUserTransDetailController.class);

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

	@RequiresPermissions("cgb:cgbUserTransDetail:view")
	@RequestMapping(value = { "list", "" })
	public String list(CgbUserTransDetail cgbUserTransDetail, HttpServletRequest request, HttpServletResponse response, Model model) {

		String transDetailRadioType = cgbUserTransDetail.getTransDetailRadioType();
		if (null == transDetailRadioType) {
			Page<CgbUserTransDetail> page = cgbUserTransDetailService.findPage(new Page<CgbUserTransDetail>(request, response), cgbUserTransDetail);
			List<CgbUserTransDetail> list = page.getList();
			for (CgbUserTransDetail entity : list) {
				// 金额.
				entity.setAmountStr(new DecimalFormat("0.00").format(entity.getAmount()));
				// 可用余额.
				entity.setAvaliableAmountStr(new DecimalFormat("0.00").format(entity.getAvaliableAmount()));
				UserInfo userInfo = entity.getUserInfo();
				if (userInfo != null) {
					userInfo.setName(CommonStringUtils.mobileEncrypt(userInfo.getName()));
					userInfo.setRealName(CommonStringUtils.replaceNameX(userInfo.getRealName()));
				}
			}
			model.addAttribute("page", page);
		} else if (CgbUserTransDetail.TRANS_DETAIL_RADIO_TYPE_1.equals(transDetailRadioType)) {
			log.info("单选按钮事件：出借人.");
			Page<CgbUserTransDetail> page = cgbUserTransDetailService.findPage(new Page<CgbUserTransDetail>(request, response), cgbUserTransDetail);
			List<CgbUserTransDetail> list = page.getList();
			for (CgbUserTransDetail entity : list) {
				// 金额.
				entity.setAmountStr(new DecimalFormat("0.00").format(entity.getAmount()));
				// 可用余额.
				entity.setAvaliableAmountStr(new DecimalFormat("0.00").format(entity.getAvaliableAmount()));
				UserInfo userInfo = entity.getUserInfo();
				if (userInfo != null) {
					userInfo.setName(CommonStringUtils.mobileEncrypt(userInfo.getName()));
					userInfo.setRealName(CommonStringUtils.replaceNameX(userInfo.getRealName()));
				}
			}
			model.addAttribute("page", page);
		} else if (CgbUserTransDetail.TRANS_DETAIL_RADIO_TYPE_2.equals(transDetailRadioType)) {
			log.info("单选按钮事件：借款人.");
			Page<CgbUserTransDetail> page = cgbUserTransDetailService.findCreditPage(new Page<CgbUserTransDetail>(request, response), cgbUserTransDetail);
			List<CgbUserTransDetail> list = page.getList();
			for (CgbUserTransDetail entity : list) {
				// 金额.
				entity.setAmountStr(new DecimalFormat("0.00").format(entity.getAmount()));
				// 可用余额.
				entity.setAvaliableAmountStr(new DecimalFormat("0.00").format(entity.getAvaliableAmount()));
				CreditUserInfo creditUserInfo = entity.getCreditUserInfo();
				if (creditUserInfo != null) {
					creditUserInfo.setName(CommonStringUtils.replaceNameX(creditUserInfo.getName()));
					creditUserInfo.setPhone(CommonStringUtils.mobileEncrypt(creditUserInfo.getPhone()));
				}
			}
			model.addAttribute("page", page);
			return "modules/cgb/userTransDetail/cgbCreditUserTransDetailList";
		}
		return "modules/cgb/userTransDetail/cgbUserTransDetailList";
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
	@RequiresPermissions("cgb:cgbUserTransDetail:view")
	@RequestMapping(value = "export", method = RequestMethod.POST)
	public String exportTransDetail(CgbUserTransDetail cgbUserTransDetail, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {

		String transDetailRadioType = cgbUserTransDetail.getTransDetailRadioType();
		try {
			if (CgbUserTransDetail.TRANS_DETAIL_RADIO_TYPE_1.equals(transDetailRadioType)) {
				log.info("单选按钮事件：出借人.");
				String fileName = "平台流水【出借人】" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
				Page<CgbUserTransDetail> initPage = new Page<CgbUserTransDetail>();
				initPage.setOrderBy("a.trans_date DESC");
				cgbUserTransDetail.setPage(initPage);
				List<CgbUserTransDetail> list = cgbUserTransDetailDao.findList(cgbUserTransDetail);
				for (CgbUserTransDetail entity : list) {
					// 金额.
					entity.setAmountStr(new DecimalFormat("0.00").format(entity.getAmount()));
					// 可用余额.
					entity.setAvaliableAmountStr(new DecimalFormat("0.00").format(entity.getAvaliableAmount()));
				}
				new ExportExcel("平台流水【出借人】", CgbUserTransDetail.class).setDataList(list).write(response, fileName).dispose();
				return null;
			} else if (CgbUserTransDetail.TRANS_DETAIL_RADIO_TYPE_2.equals(transDetailRadioType)) {
				log.info("单选按钮事件：借款人.");
				String fileName = "平台流水【借款人】" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
				Page<CgbUserTransDetail> initPage = new Page<CgbUserTransDetail>();
				initPage.setOrderBy("a.trans_date DESC");
				cgbUserTransDetail.setPage(initPage);
				List<CgbUserTransDetail> list = cgbUserTransDetailDao.findCreditList(cgbUserTransDetail);
				for (CgbUserTransDetail entity : list) {
					// 金额.
					entity.setAmountStr(new DecimalFormat("0.00").format(entity.getAmount()));
					// 可用余额.
					entity.setAvaliableAmountStr(new DecimalFormat("0.00").format(entity.getAvaliableAmount()));
				}
				new ExportExcel("平台流水【借款人】", CgbUserTransDetail.class).setDataList(list).write(response, fileName).dispose();
				return null;
			}

		} catch (Exception e) {
			addMessage(redirectAttributes, "导出失败！失败信息：" + e.getMessage());
		}

		return "redirect:" + adminPath + "/cgb/cgbUserTransDetail/list?repage";
	}

	@RequiresPermissions("cgb:cgbUserTransDetail:view")
	@RequestMapping(value = "form")
	public String form(CgbUserTransDetail cgbUserTransDetail, Model model) {

		model.addAttribute("cgbUserTransDetail", cgbUserTransDetail);
		return "modules/cgb/userTransDetail/cgbUserTransDetailForm";
	}

	@RequiresPermissions("cgb:cgbUserTransDetail:edit")
	@RequestMapping(value = "save")
	public String save(CgbUserTransDetail cgbUserTransDetail, Model model, RedirectAttributes redirectAttributes) {

		if (!beanValidator(model, cgbUserTransDetail)) {
			return form(cgbUserTransDetail, model);
		}
		cgbUserTransDetailService.save(cgbUserTransDetail);
		addMessage(redirectAttributes, "保存银行托管-流水成功");
		return "redirect:" + Global.getAdminPath() + "/cgb/cgbUserTransDetail/?repage";
	}

	@RequiresPermissions("cgb:cgbUserTransDetail:edit")
	@RequestMapping(value = "delete")
	public String delete(CgbUserTransDetail cgbUserTransDetail, RedirectAttributes redirectAttributes) {

		cgbUserTransDetailService.delete(cgbUserTransDetail);
		addMessage(redirectAttributes, "删除银行托管-流水成功");
		return "redirect:" + Global.getAdminPath() + "/cgb/cgbUserTransDetail/?repage";
	}

	/**
	 * 导出对账结果
	 * 
	 * @param userTransDetail
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("cgb:cgbUserTransDetail:view")
	@RequestMapping(value = "checkaccount")
	public String checkAccount(CgbUserTransDetail cgbUserTransDetail, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {

		try {
			String fileName = "存管宝账户对账问题用户.xlsx";
			List<CgbCheckAccount> list = cgbCheckAccountService.findAllList();
			new ExportExcel("存管宝账户对账", CgbCheckAccount.class).setDataList(list).write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出对账结果失败！失败信息：" + e.getMessage());
		}
		return "redirect:" + Global.getAdminPath() + "/cgb/cgbUserTransDetail/?repage";
	}

}