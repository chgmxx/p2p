package com.power.platform.regular.web;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.power.platform.cgb.dao.CgbUserBankCardDao;
import com.power.platform.cgb.dao.CicmorganBankCodeDao;
import com.power.platform.cgb.entity.CgbUserBankCard;
import com.power.platform.cgb.entity.CicmorganBankCode;
import com.power.platform.common.config.Global;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.security.shiro.session.SessionUtils;
import com.power.platform.common.utils.CommonStringUtils;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.web.BaseController;
import com.power.platform.credit.dao.userinfo.CreditUserInfoDao;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.regular.entity.WloanSubject;
import com.power.platform.regular.service.WloanSubjectService;
import com.power.platform.userinfo.dao.UserBankCardDao;

/**
 * 
 * 类: WloanSubjectController <br>
 * 描述: 融资主体Controller. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2015年12月29日 上午11:50:29
 */
@Controller
@RequestMapping(value = "${adminPath}/wloan_subject/wloanSubject")
public class WloanSubjectController extends BaseController {

	@Autowired
	private WloanSubjectService wloanSubjectService;
	@Resource
	private CreditUserInfoDao creditUserInfoDao;
	@Resource
	private UserBankCardDao userBankCardDao;
	@Resource
	private CgbUserBankCardDao cgbUserBankCardDao;
	@Resource
	private CicmorganBankCodeDao cicmorganBankCodeDao;

	@ModelAttribute
	public WloanSubject get(@RequestParam(required = false) String id) {

		WloanSubject entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = wloanSubjectService.get(id);
		}
		if (entity == null) {
			entity = new WloanSubject();
		}
		return entity;
	}

	/**
	 * 
	 * 方法: cgbPersonalList <br>
	 * 描述: 存管保个人融资主体列表. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年11月30日 上午9:59:14
	 * 
	 * @param wloanSubject
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequiresPermissions("wloan_subject:wloanSubject:view")
	@RequestMapping(value = "cgbPersonalList")
	public String cgbPersonalList(WloanSubject wloanSubject, HttpServletRequest request, HttpServletResponse response, Model model) {

		String type = wloanSubject.getType();
		if (StringUtils.isBlank(type)) {

			// 由于列表页按照融资类别进行分类，默认进来是个人列表页.
			wloanSubject.setType(WloanSubjectService.WLOAN_SUBJECT_TYPE_1);
			wloanSubject.setBeginCreateDate(DateUtils.getDateOfString("2017-12-21"));
			Page<WloanSubject> page = wloanSubjectService.findPage(new Page<WloanSubject>(request, response), wloanSubject);
			List<WloanSubject> list = page.getList();
			for (int i = 0; i < list.size(); i++) {
				WloanSubject entity = list.get(i);
				entity.setLoanUser(CommonStringUtils.replaceNameX(entity.getLoanUser()));
				entity.setCorporationCertNo(CommonStringUtils.idEncrypt(entity.getCorporationCertNo()));
				entity.setAgentPersonName(CommonStringUtils.replaceNameX(entity.getAgentPersonName()));
				entity.setAgentPersonPhone(CommonStringUtils.mobileEncrypt(entity.getAgentPersonPhone()));
				entity.setBusinessNo(CommonStringUtils.idEncrypt(entity.getBusinessNo()));
				entity.setBankPermitCertNo(CommonStringUtils.idEncrypt(entity.getBankPermitCertNo()));
			}
			model.addAttribute("page", page);
		} else {

			if (type.equals(WloanSubjectService.WLOAN_SUBJECT_TYPE_1)) {
				wloanSubject.setBeginCreateDate(DateUtils.getDateOfString("2017-12-21"));
				Page<WloanSubject> page = wloanSubjectService.findPage(new Page<WloanSubject>(request, response), wloanSubject);
				List<WloanSubject> list = page.getList();
				for (int i = 0; i < list.size(); i++) {
					WloanSubject entity = list.get(i);
					entity.setLoanUser(CommonStringUtils.replaceNameX(entity.getLoanUser()));
					entity.setCorporationCertNo(CommonStringUtils.idEncrypt(entity.getCorporationCertNo()));
					entity.setAgentPersonName(CommonStringUtils.replaceNameX(entity.getAgentPersonName()));
					entity.setAgentPersonPhone(CommonStringUtils.mobileEncrypt(entity.getAgentPersonPhone()));
					entity.setBusinessNo(CommonStringUtils.idEncrypt(entity.getBusinessNo()));
					entity.setBankPermitCertNo(CommonStringUtils.idEncrypt(entity.getBankPermitCertNo()));
				}
				model.addAttribute("page", page);
				return "modules/regular/wloan_subject/cgbWloanSubjectPersonalList";
			} else if (type.equals(WloanSubjectService.WLOAN_SUBJECT_TYPE_2)) {
				wloanSubject.setBeginCreateDate(DateUtils.getDateOfString("2017-12-21"));
				Page<WloanSubject> page = wloanSubjectService.findPage(new Page<WloanSubject>(request, response), wloanSubject);
				List<WloanSubject> list = page.getList();
				for (int i = 0; i < list.size(); i++) {
					WloanSubject entity = list.get(i);
					entity.setLoanUser(CommonStringUtils.replaceNameX(entity.getLoanUser()));
					entity.setCorporationCertNo(CommonStringUtils.idEncrypt(entity.getCorporationCertNo()));
					entity.setAgentPersonName(CommonStringUtils.replaceNameX(entity.getAgentPersonName()));
					entity.setAgentPersonPhone(CommonStringUtils.mobileEncrypt(entity.getAgentPersonPhone()));
					entity.setBusinessNo(CommonStringUtils.idEncrypt(entity.getBusinessNo()));
					entity.setBankPermitCertNo(CommonStringUtils.idEncrypt(entity.getBankPermitCertNo()));
				}
				model.addAttribute("page", page);
				return "modules/regular/wloan_subject/cgbWloanSubjectEnterpriseList";
			}
		}

		return "modules/regular/wloan_subject/cgbWloanSubjectPersonalList";
	}

	/**
	 * 
	 * 方法: cgbEnterpriseList <br>
	 * 描述: 存管保企业融资主体列表. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年11月30日 上午11:21:08
	 * 
	 * @param wloanSubject
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequiresPermissions("wloan_subject:wloanSubject:view")
	@RequestMapping(value = "cgbEnterpriseList")
	public String cgbEnterpriseList(WloanSubject wloanSubject, HttpServletRequest request, HttpServletResponse response, Model model) {

		wloanSubject.setType(WloanSubjectService.WLOAN_SUBJECT_TYPE_2);
		wloanSubject.setBeginCreateDate(DateUtils.getDateOfString("2017-11-01"));
		Page<WloanSubject> page = wloanSubjectService.findPage(new Page<WloanSubject>(request, response), wloanSubject);
		model.addAttribute("page", page);
		return "modules/regular/wloan_subject/cgbWloanSubjectEnterpriseList";
	}

	/**
	 * 
	 * 方法: personalList <br>
	 * 描述: 个人列表页. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2015年12月30日 下午4:30:53
	 * 
	 * @param wloanSubject
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequiresPermissions("wloan_subject:wloanSubject:view")
	@RequestMapping(value = "personalList")
	public String personalList(WloanSubject wloanSubject, HttpServletRequest request, HttpServletResponse response, Model model) {

		wloanSubject.setType(WloanSubjectService.WLOAN_SUBJECT_TYPE_1);
		Page<WloanSubject> page = wloanSubjectService.findPage(new Page<WloanSubject>(request, response), wloanSubject);
		model.addAttribute("page", page);
		return "modules/regular/wloan_subject/wloanSubjectPersonalList";
	}

	/**
	 * 
	 * 方法: enterpriseList <br>
	 * 描述: 企业列表页. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2015年12月30日 下午4:31:49
	 * 
	 * @param wloanSubject
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequiresPermissions("wloan_subject:wloanSubject:view")
	@RequestMapping(value = "enterpriseList")
	public String enterpriseList(WloanSubject wloanSubject, HttpServletRequest request, HttpServletResponse response, Model model) {

		wloanSubject.setType(WloanSubjectService.WLOAN_SUBJECT_TYPE_2);
		Page<WloanSubject> page = wloanSubjectService.findPage(new Page<WloanSubject>(request, response), wloanSubject);
		model.addAttribute("page", page);
		return "modules/regular/wloan_subject/wloanSubjectEnterpriseList";
	}

	/**
	 * 
	 * 方法: list <br>
	 * 描述: 默认列表页. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2015年12月30日 下午4:32:43
	 * 
	 * @param wloanSubject
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequiresPermissions("wloan_subject:wloanSubject:view")
	@RequestMapping(value = { "list", "" })
	public String list(WloanSubject wloanSubject, HttpServletRequest request, HttpServletResponse response, Model model) {

		String type = wloanSubject.getType();
		if (StringUtils.isBlank(type)) {

			// 由于列表页按照融资类别进行分类，默认进来是个人列表页.
			wloanSubject.setType(WloanSubjectService.WLOAN_SUBJECT_TYPE_1);
			Page<WloanSubject> page = wloanSubjectService.findPage(new Page<WloanSubject>(request, response), wloanSubject);
			model.addAttribute("page", page);
		} else {

			if (type.equals(WloanSubjectService.WLOAN_SUBJECT_TYPE_1)) {

				Page<WloanSubject> page = wloanSubjectService.findPage(new Page<WloanSubject>(request, response), wloanSubject);
				model.addAttribute("page", page);
				return "modules/regular/wloan_subject/wloanSubjectPersonalList";
			} else if (type.equals(WloanSubjectService.WLOAN_SUBJECT_TYPE_2)) {

				Page<WloanSubject> page = wloanSubjectService.findPage(new Page<WloanSubject>(request, response), wloanSubject);
				model.addAttribute("page", page);
				return "modules/regular/wloan_subject/wloanSubjectEnterpriseList";
			}
		}

		return "modules/regular/wloan_subject/wloanSubjectPersonalList";
	}

	/**
	 * 
	 * 方法: addForm <br>
	 * 描述: 新增. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2015年12月29日 下午1:21:03
	 * 
	 * @param wloanSubject
	 * @param model
	 * @return
	 */
	@RequiresPermissions("wloan_subject:wloanSubject:view")
	@RequestMapping(value = "addForm")
	public String addForm(WloanSubject wloanSubject, Model model) {

		model.addAttribute("wloanSubject", wloanSubject);
		return "modules/regular/wloan_subject/wloanSubjectAddForm";
	}

	/**
	 * 
	 * 方法: cgbWloanSubjectForm <br>
	 * 描述: 存管保融资主体表单页. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年11月29日 上午10:58:43
	 * 
	 * @param wloanSubject
	 * @param model
	 * @return
	 */
	@RequiresPermissions("wloan_subject:wloanSubject:view")
	@RequestMapping(value = "cgbWloanSubjectForm")
	public String cgbWloanSubjectForm(WloanSubject wloanSubject, Model model) {

		/**
		 * 银行编码对照表.
		 */
		CicmorganBankCode cicmorganBankCode = new CicmorganBankCode();
		List<CicmorganBankCode> cicmorganBankCodes = cicmorganBankCodeDao.findList(cicmorganBankCode);
		model.addAttribute("cicmorganBankCodes", cicmorganBankCodes);
		/**
		 * 借款人列表.
		 */
		CreditUserInfo entity = new CreditUserInfo();
		List<CreditUserInfo> creditUserInfos = creditUserInfoDao.findList(entity);
		model.addAttribute("creditUserInfos", creditUserInfos);
		/**
		 * 融资主体.
		 */
		model.addAttribute("wloanSubject", wloanSubject);
		return "modules/regular/wloan_subject/cgbWloanSubjectForm";
	}

	@RequiresPermissions("wloan_subject:wloanSubject:view")
	@RequestMapping(value = "cgbWloanSubjectViewForm")
	public String cgbWloanSubjectViewForm(WloanSubject wloanSubject, Model model) {

		/**
		 * 银行编码对照表.
		 */
		CicmorganBankCode cicmorganBankCode = new CicmorganBankCode();
		List<CicmorganBankCode> cicmorganBankCodes = cicmorganBankCodeDao.findList(cicmorganBankCode);
		model.addAttribute("cicmorganBankCodes", cicmorganBankCodes);
		/**
		 * 借款人列表.
		 */
		CreditUserInfo entity = new CreditUserInfo();
		List<CreditUserInfo> creditUserInfos = creditUserInfoDao.findList(entity);
		model.addAttribute("creditUserInfos", creditUserInfos);
		/**
		 * 融资主体.
		 */
		model.addAttribute("wloanSubject", wloanSubject);
		return "modules/regular/wloan_subject/cgbWloanSubjectViewForm";
	}

	@RequiresPermissions("wloan_subject:wloanSubject:view")
	@RequestMapping(value = "cgbProjectWloanSubjectViewForm")
	public String cgbProjectWloanSubjectViewForm(WloanSubject wloanSubject, Model model) {

		/**
		 * 银行编码对照表.
		 */
		CicmorganBankCode cicmorganBankCode = new CicmorganBankCode();
		List<CicmorganBankCode> cicmorganBankCodes = cicmorganBankCodeDao.findList(cicmorganBankCode);
		model.addAttribute("cicmorganBankCodes", cicmorganBankCodes);
		/**
		 * 借款人列表.
		 */
		CreditUserInfo entity = new CreditUserInfo();
		List<CreditUserInfo> creditUserInfos = creditUserInfoDao.findList(entity);
		model.addAttribute("creditUserInfos", creditUserInfos);
		/**
		 * 融资主体.
		 */
		model.addAttribute("wloanSubject", wloanSubject);
		return "modules/regular/wloan_term_project/cgbProjectWloanSubjectViewForm";
	}

	/**
	 * 
	 * 方法: selectLoanApplyId <br>
	 * 描述: 借款人信息查询. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年12月1日 上午11:55:50
	 * 
	 * @param id
	 * @param model
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "selectLoanApplyId")
	public Map<String, String> selectLoanApplyId(String id, Model model) {

		Map<String, String> params = new HashMap<String, String>();

		// 借款人信息.
		CreditUserInfo creditUserInfo = creditUserInfoDao.get(id);
		if (null != creditUserInfo) {
			// 手机号码.
			params.put("loanPhone", creditUserInfo.getPhone());
			// 姓名.
			params.put("loanUser", creditUserInfo.getName());
			// 身份证号码.
			params.put("loanIdCard", creditUserInfo.getCertificateNo());
			// 开户信息.
			CgbUserBankCard entity = new CgbUserBankCard();
			entity.setUserId(creditUserInfo.getId());
			entity.setState(CgbUserBankCard.CERTIFY_YES);
			CgbUserBankCard cgbUserBankCard = cgbUserBankCardDao.getUserBankCardByCreditUserIdAndState(entity);
			if (null != cgbUserBankCard) {
				// 借款人银行卡号码.
				params.put("loanBankNo", cgbUserBankCard.getBankAccountNo());
				// 借款人银行预留手机.
				params.put("loanBankPhone", cgbUserBankCard.getBankCardPhone());
				// 借款人银行卡开户名称.
				params.put("loanBankName", cgbUserBankCard.getBankName());
				// 借款人银行卡银行编码.
				params.put("loanBankCode", cgbUserBankCard.getBankNo());
			} else {
				// 借款人银行卡号码.
				params.put("loanBankNo", null);
				// 借款人银行预留手机.
				params.put("loanBankPhone", null);
				// 借款人银行卡开户名称.
				params.put("loanBankName", null);
				// 借款人银行卡银行编码.
				params.put("loanBankCode", null);
			}
		}
		return params;
	}

	/**
	 * 
	 * 方法: selectCicmorganBankCodeId <br>
	 * 描述: 银行编码对照表查询. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年12月1日 上午11:56:39
	 * 
	 * @param id
	 * @param model
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "selectCicmorganBankCodeId")
	public Map<String, String> selectCicmorganBankCodeId(String id, Model model) {

		Map<String, String> params = new HashMap<String, String>();

		CicmorganBankCode cicmorganBankCode = cicmorganBankCodeDao.get(id);
		if (null != cicmorganBankCode) {
			// 开户行.
			params.put("bankName", cicmorganBankCode.getBankName());
			// 银行编码.
			params.put("bankCode", cicmorganBankCode.getBankCode());
		}
		return params;
	}

	/**
	 * 
	 * 方法: updateForm <br>
	 * 描述: 修改. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2015年12月29日 下午1:23:57
	 * 
	 * @param wloanSubject
	 * @param model
	 * @return
	 */
	@RequiresPermissions("wloan_subject:wloanSubject:view")
	@RequestMapping(value = "updateForm")
	public String updateForm(WloanSubject wloanSubject, Model model) {

		model.addAttribute("wloanSubject", wloanSubject);
		return "modules/regular/wloan_subject/wloanSubjectUpdateForm";
	}

	/**
	 * 
	 * 方法: cgbWloanSubjectEntrustedInfo <br>
	 * 描述: 受托人信息展示. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年11月30日 下午2:00:07
	 * 
	 * @param wloanSubject
	 * @param model
	 * @return
	 */
	@RequiresPermissions("wloan_subject:wloanSubject:view")
	@RequestMapping(value = "cgbWloanSubjectEntrustedInfo")
	public String cgbWloanSubjectEntrustedInfo(WloanSubject wloanSubject, Model model) {

		// 融资主体.
		model.addAttribute("wloanSubject", wloanSubject);
		return "modules/regular/wloan_subject/cgbWloanSubjectEntrustedInfo";
	}

	/**
	 * 
	 * 方法: viewForm <br>
	 * 描述: 查看. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2015年12月29日 下午7:29:55
	 * 
	 * @param wloanSubject
	 * @param model
	 * @return
	 */
	@RequiresPermissions("wloan_subject:wloanSubject:view")
	@RequestMapping(value = "viewForm")
	public String viewForm(WloanSubject wloanSubject, Model model) {

		model.addAttribute("wloanSubject", wloanSubject);
		return "modules/regular/wloan_subject/wloanSubjectViewForm";
	}

	/**
	 * 
	 * 方法: addSave <br>
	 * 描述: 新增保存. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2015年12月29日 下午1:21:22
	 * 
	 * @param wloanSubject
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("wloan_subject:wloanSubject:edit")
	@RequestMapping(value = "addSave")
	public String addSave(WloanSubject wloanSubject, Model model, RedirectAttributes redirectAttributes) {

		if (!beanValidator(model, wloanSubject)) {
			return addForm(wloanSubject, model);
		}
		// 定期融资主体新增详情.
		wloanSubject.setId(IdGen.uuid());
		wloanSubject.setCreateBy(SessionUtils.getUser());
		wloanSubject.setCreateDate(new Date());
		wloanSubject.setUpdateBy(SessionUtils.getUser());
		wloanSubject.setUpdateDate(new Date());
		int flag = wloanSubjectService.insertWloanSubject(wloanSubject);
		if (flag == 1) {
			addMessage(redirectAttributes, "新增融资主体保存成功");
		} else {
			addMessage(redirectAttributes, "新增融资主体保存失败");
		}
		return "redirect:" + Global.getAdminPath() + "/wloan_subject/wloanSubject/?repage";
	}

	/**
	 * 
	 * 方法: cgbAddSave <br>
	 * 描述: 存管保新增保存方法. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年11月30日 上午10:55:11
	 * 
	 * @param wloanSubject
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("wloan_subject:wloanSubject:edit")
	@RequestMapping(value = "cgbWloanSubjectSave")
	public String cgbWloanSubjectSave(WloanSubject wloanSubject, Model model, RedirectAttributes redirectAttributes) {

		if (!beanValidator(model, wloanSubject)) {
			return cgbWloanSubjectForm(wloanSubject, model);
		}

		if (wloanSubject.getIsNewRecord()) {
			// 定期融资主体新增详情.
			wloanSubject.setId(IdGen.uuid());
			wloanSubject.setCreateBy(SessionUtils.getUser());
			wloanSubject.setCreateDate(new Date());
			wloanSubject.setUpdateBy(SessionUtils.getUser());
			wloanSubject.setUpdateDate(new Date());
			// 判断融资类别，返回不同的列表页.
			String type = wloanSubject.getType();
			if (StringUtils.isBlank(type)) {
			} else {
				if (type.equals(WloanSubjectService.WLOAN_SUBJECT_TYPE_1)) {

					int flag = wloanSubjectService.insertWloanSubject(wloanSubject);
					if (flag == 1) {
						addMessage(redirectAttributes, "新增主体保存成功");
					} else {
						addMessage(redirectAttributes, "新增主体保存失败");
					}
					return "redirect:" + Global.getAdminPath() + "/wloan_subject/wloanSubject/cgbPersonalList?repage";
				} else if (type.equals(WloanSubjectService.WLOAN_SUBJECT_TYPE_2)) {

					int flag = wloanSubjectService.insertWloanSubject(wloanSubject);
					if (flag == 1) {
						addMessage(redirectAttributes, "新增主体保存成功");
					} else {
						addMessage(redirectAttributes, "新增主体保存失败");
					}
					return "redirect:" + Global.getAdminPath() + "/wloan_subject/wloanSubject/cgbEnterpriseList?repage";
				}
			}
		} else {
			// 定期融资主体修改详情.
			wloanSubject.setUpdateBy(SessionUtils.getUser());
			wloanSubject.setUpdateDate(new Date());
			String type = wloanSubject.getType();
			if (StringUtils.isBlank(type)) {
			} else {
				if (type.equals(WloanSubjectService.WLOAN_SUBJECT_TYPE_1)) {
					int flag = wloanSubjectService.updateWloanSubject(wloanSubject);
					if (flag == 1) {
						addMessage(redirectAttributes, "修改主体保存成功");
					} else {
						addMessage(redirectAttributes, "修改主体保存失败");
					}
					return "redirect:" + Global.getAdminPath() + "/wloan_subject/wloanSubject/cgbPersonalList?repage";
				} else if (type.equals(WloanSubjectService.WLOAN_SUBJECT_TYPE_2)) {

					int flag = wloanSubjectService.updateWloanSubject(wloanSubject);
					if (flag == 1) {
						addMessage(redirectAttributes, "修改主体保存成功");
					} else {
						addMessage(redirectAttributes, "修改主体保存失败");
					}
					return "redirect:" + Global.getAdminPath() + "/wloan_subject/wloanSubject/cgbEnterpriseList?repage";
				}
			}
		}

		return "redirect:" + Global.getAdminPath() + "/wloan_subject/wloanSubject/cgbPersonalList?repage";
	}

	/**
	 * 
	 * 方法: updateSave <br>
	 * 描述: 修改保存. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2015年12月29日 下午1:25:23
	 * 
	 * @param wloanSubject
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("wloan_subject:wloanSubject:edit")
	@RequestMapping(value = "updateSave")
	public String updateSave(WloanSubject wloanSubject, Model model, RedirectAttributes redirectAttributes) {

		if (!beanValidator(model, wloanSubject)) {
			return updateForm(wloanSubject, model);
		}
		// 定期融资主体修改详情.
		wloanSubject.setUpdateBy(SessionUtils.getUser());
		wloanSubject.setUpdateDate(new Date());

		// 判断融资类别，返回不同的列表页.
		String type = wloanSubject.getType();
		if (StringUtils.isBlank(type)) {

		} else {
			if (type.equals(WloanSubjectService.WLOAN_SUBJECT_TYPE_1)) {

				int flag = wloanSubjectService.updateWloanSubject(wloanSubject);
				if (flag == 1) {
					addMessage(redirectAttributes, "修改融资主体保存成功");
				} else {
					addMessage(redirectAttributes, "修改融资主体保存失败");
				}
				return "redirect:" + Global.getAdminPath() + "/wloan_subject/wloanSubject/personalList?repage";
			} else if (type.equals(WloanSubjectService.WLOAN_SUBJECT_TYPE_2)) {

				int flag = wloanSubjectService.updateWloanSubject(wloanSubject);
				if (flag == 1) {
					addMessage(redirectAttributes, "修改融资主体保存成功");
				} else {
					addMessage(redirectAttributes, "修改融资主体保存失败");
				}
				return "redirect:" + Global.getAdminPath() + "/wloan_subject/wloanSubject/enterpriseList?repage";
			}
		}

		return "redirect:" + Global.getAdminPath() + "/wloan_subject/wloanSubject/?repage";
	}

	@RequiresPermissions("wloan_subject:wloanSubject:edit")
	@RequestMapping(value = "delete")
	public String delete(WloanSubject wloanSubject, RedirectAttributes redirectAttributes) {

		// 判断融资项目是否使用到融资主体.
		List<WloanSubject> list = wloanSubjectService.isExistWloanSubjectAndWloanTermProject(wloanSubject);
		if (list != null && list.size() > 0) {
			addMessage(redirectAttributes, "当前融资主体已被使用");
		} else {
			wloanSubjectService.delete(wloanSubject);
			addMessage(redirectAttributes, "删除融资主体成功");
		}

		// 返回相应列表.
		if (WloanSubjectService.WLOAN_SUBJECT_TYPE_1.equals(wloanSubject.getType())) {
			return "redirect:" + Global.getAdminPath() + "/wloan_subject/wloanSubject/personalList?repage";
		} else if (WloanSubjectService.WLOAN_SUBJECT_TYPE_2.equals(wloanSubject.getType())) {
			return "redirect:" + Global.getAdminPath() + "/wloan_subject/wloanSubject/enterpriseList?repage";
		}

		return "redirect:" + Global.getAdminPath() + "/wloan_subject/wloanSubject/?repage";
	}

	@RequiresPermissions("wloan_subject:wloanSubject:edit")
	@RequestMapping(value = "cgbDelete")
	public String cgbDelete(WloanSubject wloanSubject, RedirectAttributes redirectAttributes) {

		// 判断融资项目是否使用到融资主体.
		List<WloanSubject> list = wloanSubjectService.isExistWloanSubjectAndWloanTermProject(wloanSubject);
		if (list != null && list.size() > 0) {
			addMessage(redirectAttributes, "当前主体已被使用");
		} else {
			wloanSubjectService.delete(wloanSubject);
			addMessage(redirectAttributes, "删除主体成功");
		}

		// 返回相应列表.
		if (WloanSubjectService.WLOAN_SUBJECT_TYPE_1.equals(wloanSubject.getType())) {
			return "redirect:" + Global.getAdminPath() + "/wloan_subject/wloanSubject/cgbPersonalList?repage";
		} else if (WloanSubjectService.WLOAN_SUBJECT_TYPE_2.equals(wloanSubject.getType())) {
			return "redirect:" + Global.getAdminPath() + "/wloan_subject/wloanSubject/cgbEnterpriseList?repage";
		}

		return "redirect:" + Global.getAdminPath() + "/wloan_subject/wloanSubject/cgbPersonalList?repage";
	}

}