/**
 * Copyright &copy; 2012-2014 <a
 * href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.credit.userinfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

import com.power.platform.cgb.service.CgbUserBankCardService;
import com.power.platform.common.config.Global;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.utils.CommonStringUtils;
import com.power.platform.common.utils.EncoderUtil;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.web.BaseController;
import com.power.platform.credit.dao.userinfo.CreditUserAccountDao;
import com.power.platform.credit.dao.userinfo.CreditUserInfoDao;
import com.power.platform.credit.dao.ztmgLoanBasicInfo.ZtmgLoanBasicInfoDao;
import com.power.platform.credit.dao.ztmgLoanBasicInfo.ZtmgLoanShareholdersInfoDao;
import com.power.platform.credit.entity.annexfile.CreditAnnexFile;
import com.power.platform.credit.entity.supplierToMiddlemen.CreditSupplierToMiddlemen;
import com.power.platform.credit.entity.userinfo.CreditUserAccount;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.entity.ztmgLoanBasicInfo.ZtmgLoanBasicInfo;
import com.power.platform.credit.entity.ztmgLoanBasicInfo.ZtmgLoanShareholdersInfo;
import com.power.platform.credit.service.annexfile.CreditAnnexFileService;
import com.power.platform.credit.service.supplierToMiddlemen.CreditSupplierToMiddlemenService;
import com.power.platform.credit.service.userinfo.CreditUserInfoService;
import com.power.platform.lanmao.type.CreditUserOpenAccountEnum;
import com.power.platform.regular.dao.WloanTermProjectDao;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.regular.service.WloanSubjectService;
import com.power.platform.regular.service.WloanTermProjectService;

/**
 * 信贷用户Controller
 * 
 * @author nice
 * @version 2017-03-22
 */
@Controller
@RequestMapping(value = "${adminPath}/credit/userinfo/creditUserInfo")
public class CreditUserInfoController extends BaseController {

	@Autowired
	private CreditUserInfoService creditUserInfoService;
	@Autowired
	private CreditUserInfoDao creditUserInfoDao;
	@Autowired
	private CreditUserAccountDao creditUserAccountDao;
	@Autowired
	private CreditAnnexFileService creditAnnexFileService;
	@Autowired
	private CreditSupplierToMiddlemenService creditSupplierToMiddlemenService;
	@Autowired
	private CgbUserBankCardService cgbUserBankCardService;
	@Autowired
	private WloanSubjectService wloanSubjectService;
	@Resource
	private ZtmgLoanBasicInfoDao ztmgLoanBasicInfoDao;
	@Resource
	private ZtmgLoanShareholdersInfoDao ztmgLoanShareholdersInfoDao;
	@Autowired
	private WloanTermProjectDao wloanTermProjectDao;

	@ModelAttribute
	public CreditUserInfo get(@RequestParam(required = false) String id) {

		CreditUserInfo entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = creditUserInfoService.get(id);
		}
		if (entity == null) {
			entity = new CreditUserInfo();
		}
		return entity;
	}

	@RequiresPermissions("credit:userinfo:creditUserInfo:view")
	@RequestMapping(value = { "list", "" })
	public String list(CreditUserInfo creditUserInfo, HttpServletRequest request, HttpServletResponse response, Model model) {

		Page<CreditUserInfo> pages = new Page<CreditUserInfo>(request, response);
		pages.setOrderBy("register_date desc");

		// 供应商条件删选---所属企业
		if (creditUserInfo.getId() != null && creditUserInfo.getCreditUserType().equals("02")) {
			// 查询中间表
			CreditSupplierToMiddlemen supplierToMiddlemen = new CreditSupplierToMiddlemen();
			supplierToMiddlemen.setMiddlemenId(creditUserInfo.getId());
			List<CreditSupplierToMiddlemen> list = creditSupplierToMiddlemenService.findList(supplierToMiddlemen);
			List<String> supplierIdList = new ArrayList<String>();
			if (list != null) {
				for (CreditSupplierToMiddlemen creditSupplierToMiddlemen : list) {
					supplierIdList.add(creditSupplierToMiddlemen.getSupplierId());
				}
			}
			creditUserInfo.setId("");
			creditUserInfo.setPwd("");
			creditUserInfo.setSupplierIdList(supplierIdList);
		} else {
			creditUserInfo.setSupplierIdList(null);
		}

		Page<CreditUserInfo> page = creditUserInfoService.findPage(pages, creditUserInfo);
		List<CreditUserInfo> list = page.getList();
		WloanTermProject wloanTermProject = null;
		for (int i = 0; i < list.size(); i++) {
			CreditUserInfo entity = list.get(i);
			entity.setPhone(CommonStringUtils.mobileEncrypt(entity.getPhone()));
			// 统计在贷余额
			wloanTermProject = new WloanTermProject();
			wloanTermProject.setState(WloanTermProjectService.REPAYMENT);
			wloanTermProject.setReplaceRepayId(entity.getId());
			List<WloanTermProject> projectList = wloanTermProjectDao.findList(wloanTermProject);
			Double inTheLoanBalance = 0D;
			for (WloanTermProject pro : projectList) {
				inTheLoanBalance = NumberUtils.add(inTheLoanBalance, pro.getCurrentAmount());
			}
			entity.setInTheLoanBalance(NumberUtils.scaleDoubleStr(inTheLoanBalance));
		}
		model.addAttribute("page", page);
		model.addAttribute("creditUserType", creditUserInfo.getCreditUserType());

		// 查询所有核心企业
		CreditUserInfo userInfo = new CreditUserInfo();
		userInfo.setCreditUserType("11");
		List<CreditUserInfo> middlemenList = creditUserInfoService.findList(userInfo);
		model.addAttribute("middlemenList", middlemenList);
		if (creditUserInfo.getCreditUserType() != null) {
			if (creditUserInfo.getCreditUserType().equals("11")) {
				return "modules/credit/userinfo/creditUserInfoList1";
			} else {
				return "modules/credit/userinfo/creditUserInfoList";
			}
		} else {
			return "modules/credit/userinfo/creditUserInfoList";
		}

	}

	@RequiresPermissions("credit:userinfo:creditUserInfo:view")
	@RequestMapping(value = "form")
	public String form(CreditUserInfo creditUserInfo, Model model) {

		if (creditUserInfo != null) {
			String userId = creditUserInfo.getId();
			CreditAnnexFile annexFile = new CreditAnnexFile();
			annexFile.setOtherId(userId);
			annexFile.setType("30");
			List<CreditAnnexFile> list = creditAnnexFileService.findList(annexFile);
			if (list != null && list.size() > 0) {
				CreditAnnexFile creditAnnexFile = list.get(0);
				creditUserInfo.setAnnexFile(creditAnnexFile);
			}
		}
		model.addAttribute("creditUserInfo", creditUserInfo);
		return "modules/credit/userinfo/creditUserInfoForm";
	}

	/**
	 * 核心企业添加---跳转
	 * 
	 * @param creditUserInfo
	 * @param model
	 * @return
	 */
	@RequiresPermissions("credit:userinfo:creditUserInfo:view")
	@RequestMapping(value = "add")
	public String add(CreditUserInfo creditUserInfo, Model model) {

		model.addAttribute("creditUserInfo", creditUserInfo);
		return "modules/credit/userinfo/creditUserInfoAdd";
	}

	/**
	 * 核心企业添加---新增
	 * 
	 * @param creditUserInfo
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("credit:userinfo:creditUserInfo:edit")
	@RequestMapping(value = "middlemenSave")
	public String middlemenSave(CreditUserInfo creditUserInfo, Model model, RedirectAttributes redirectAttributes) {

		if (!beanValidator(model, creditUserInfo)) {
			return form(creditUserInfo, model);
		}
		// N1.查询手机号是否已经存在
		List<CreditUserInfo> list = creditUserInfoService.findList(creditUserInfo);
		if (list != null && list.size() > 0) {
			addMessage(redirectAttributes, creditUserInfo.getPhone() + "已经存在");
			return "redirect:" + Global.getAdminPath() + "/credit/userinfo/creditUserInfo/?repage";
		} else {
			String userId = String.valueOf(IdGen.randomLong());
			String userAccountId = String.valueOf(IdGen.randomLong());
			// N2.新增creditUserInfo
			creditUserInfo.setId(userId);
			creditUserInfo.setAccountId(userAccountId);
			creditUserInfo.setPwd(EncoderUtil.encrypt(creditUserInfo.getPwd()));
			creditUserInfo.setCreditUserType(CreditUserInfo.CREDIT_USER_TYPE_11);
			creditUserInfo.setCreditScore(creditUserInfo.getCreditScore());// 企业简称
			creditUserInfo.setEnterpriseFullName(creditUserInfo.getEnterpriseFullName());
			creditUserInfo.setRegisterDate(new Date());
			creditUserInfo.setLastLoginDate(new Date());
			// 新增代偿户/借款户，初始化默认开户状态，0：未开户
			creditUserInfo.setOpenAccountState(CreditUserOpenAccountEnum.OPEN_ACCOUNT_STATE_0.getValue()); // 未开户
			creditUserInfo.setIsCreateBasicInfo(CreditUserInfo.IS_CREATE_BASIC_INFO_2); // 未完善基本信息
			creditUserInfo.setState(CreditUserInfo.CREDIT_USER_NORMAL); // 正常
			int i = creditUserInfoDao.insert(creditUserInfo);
			if (i > 0) {
				// N3.新增借款用户账户
				CreditUserAccount userAccount = new CreditUserAccount();
				userAccount.setId(userAccountId);
				userAccount.setCreditUserId(userId);
				userAccount.setTotalAmount(0d);
				userAccount.setAvailableAmount(0d);
				userAccount.setRechargeAmount(0d);
				userAccount.setWithdrawAmount(0d);
				userAccount.setRepayAmount(0d);
				userAccount.setSurplusAmount(0d);
				userAccount.setFreezeAmount(0d);
				int j = creditUserAccountDao.insert(userAccount);
				if (j > 0) {
					addMessage(redirectAttributes, "新增核心企业用户成功");
				}
			}
			// N3.附件表增加核心企业logo
			if (creditUserInfo.getAnnexFile() != null) {
				CreditAnnexFile annexFile = new CreditAnnexFile();
				annexFile.setOtherId(userId); // 资料信息ID.
				annexFile.setUrl(creditUserInfo.getAnnexFile().getUrl()); // 图片保存路径.
				annexFile.setType("30"); // 类型---企业logo
				annexFile.setRemark(creditUserInfo.getAnnexFile().getRemark()); // 备注.
				int tag = creditAnnexFileService.insertCreditAnnexFile(annexFile, 0, IdGen.uuid());
				if (tag == 1) {
					System.out.println("PATH:" + creditUserInfo.getAnnexFile().getUrl() + ", save success.");
				}
			}

		}
		return "redirect:" + Global.getAdminPath() + "/credit/userinfo/creditUserInfo/?repage";
	}

	@RequiresPermissions("credit:userinfo:creditUserInfo:edit")
	@RequestMapping(value = "save")
	public String save(CreditUserInfo creditUserInfo, Model model, RedirectAttributes redirectAttributes) {

		if (!beanValidator(model, creditUserInfo)) {
			return form(creditUserInfo, model);
		}
		// N3.附件表增加核心企业logo
		if (creditUserInfo.getAnnexFile() != null) {
			// 先删除
			creditAnnexFileService.deleteCreditAnnexFileById(creditUserInfo.getAnnexFile().getId());
			// 再添加
			CreditAnnexFile annexFile = new CreditAnnexFile();
			annexFile.setOtherId(creditUserInfo.getId()); // 资料信息ID.
			annexFile.setUrl(creditUserInfo.getAnnexFile().getUrl()); // 图片保存路径.
			annexFile.setType("30"); // 类型---企业logo
			annexFile.setRemark(creditUserInfo.getAnnexFile().getRemark()); // 备注.
			int tag = creditAnnexFileService.insertCreditAnnexFile(annexFile, 0, IdGen.uuid());
			if (tag == 1) {
				System.out.println("PATH:" + creditUserInfo.getAnnexFile().getUrl() + ", save success.");
			}
		}

		// 新增代偿户/借款户，初始化默认开户状态，0：未开户
		if (creditUserInfo.getIsNewRecord()) {
			creditUserInfo.setOpenAccountState(CreditUserOpenAccountEnum.OPEN_ACCOUNT_STATE_0.getValue()); // 未开户
		}
		creditUserInfoService.save(creditUserInfo);
		addMessage(redirectAttributes, "保存信贷用户成功");
		return "redirect:" + Global.getAdminPath() + "/credit/userinfo/creditUserInfo/?creditUserType=11";
	}

	@RequiresPermissions("credit:userinfo:creditUserInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(CreditUserInfo creditUserInfo, RedirectAttributes redirectAttributes) {

		creditUserInfoService.delete(creditUserInfo);
		addMessage(redirectAttributes, "删除信贷用户成功");
		return "redirect:" + Global.getAdminPath() + "/credit/userinfo/creditUserInfo/?repage";
	}

	@RequiresPermissions("credit:userinfo:creditUserInfo:edit")
	@RequestMapping(value = "deleteSupplierBank")
	public String deleteSupplierBank(CreditUserInfo creditUserInfo, RedirectAttributes redirectAttributes) {

		String userId = creditUserInfo.getId();
		// 删除银行卡信息
		cgbUserBankCardService.deleteBankByUserId(userId);
		// 删除融资主体
		wloanSubjectService.deleteWloanSubjectByUserId(userId);
		// 删除开户图片
		creditAnnexFileService.deleteCreditAnnexFileByUserId(userId);
		// creditUserInfoService.delete(creditUserInfo);
		addMessage(redirectAttributes, "删除信贷用户成功");
		return "redirect:" + Global.getAdminPath() + "/credit/userinfo/creditUserInfo/?repage";
	}

	/**
	 * 
	 * 方法: creditUserZtmgLoanBasicInfo <br>
	 * 描述: 借款人基本信息. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年8月27日 下午3:57:03
	 * 
	 * @param creditUserInfo
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "creditUserZtmgLoanBasicInfo")
	public String creditUserZtmgLoanBasicInfo(CreditUserInfo creditUserInfo, Model model) {

		String creditUserId = creditUserInfo.getId();
		model.addAttribute("creditUserId", creditUserId);
		ZtmgLoanBasicInfo ztmgLoanBasicInfo = new ZtmgLoanBasicInfo();
		ztmgLoanBasicInfo.setCreditUserId(creditUserId);
		// 根据借款人查询借款人基本信息.
		ZtmgLoanBasicInfo ztmgLoanBasicInfoEntity = ztmgLoanBasicInfoDao.findByCreditUserId(ztmgLoanBasicInfo);
		if (null != ztmgLoanBasicInfoEntity) {
			// 信用承诺书.
			String declarationFilePath = ztmgLoanBasicInfoEntity.getDeclarationFilePath();
			if (null != declarationFilePath) {
				ztmgLoanBasicInfoEntity.setDeclarationFilePath(declarationFilePath.split("data")[1]);
			}
			model.addAttribute("ztmgLoanBasicInfo", ztmgLoanBasicInfoEntity);
			// 根据借款人基本信息查询股东信息.
			ZtmgLoanShareholdersInfo entity = new ZtmgLoanShareholdersInfo();
			entity.setLoanBasicId(ztmgLoanBasicInfoEntity.getId());
			List<ZtmgLoanShareholdersInfo> ztmgLoanShareholdersInfos = ztmgLoanShareholdersInfoDao.findListByLoanBasicInfoId(entity);
			model.addAttribute("ztmgLoanShareholdersInfos", ztmgLoanShareholdersInfos);
		} else {
			model.addAttribute("ztmgLoanBasicInfo", ztmgLoanBasicInfoEntity);
			List<ZtmgLoanShareholdersInfo> ztmgLoanShareholdersInfos = new ArrayList<ZtmgLoanShareholdersInfo>();
			model.addAttribute("ztmgLoanShareholdersInfos", ztmgLoanShareholdersInfos);
		}
		return "modules/credit/userinfo/creditUserZtmgLoanBasicInfo";
	}

}