package com.power.platform.regular;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSON;
import com.power.platform.cache.Cache;
import com.power.platform.cgbpay.config.ServerURLConfig;
import com.power.platform.common.config.Global;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.security.shiro.session.SessionUtils;
import com.power.platform.common.utils.CommonStringUtils;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.MemCachedUtis;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.common.utils.PdfUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.utils.Util;
import com.power.platform.common.utils.excel.ExportExcel;
import com.power.platform.common.web.BaseController;
import com.power.platform.credit.dao.apply.CreditUserApplyDao;
import com.power.platform.credit.dao.ztmgLoanBasicInfo.ZtmgLoanBasicInfoDao;
import com.power.platform.credit.dao.ztmgLoanBasicInfo.ZtmgLoanShareholdersInfoDao;
import com.power.platform.credit.entity.annexfile.CreditAnnexFile;
import com.power.platform.credit.entity.apply.CreditUserApply;
import com.power.platform.credit.entity.info.CreditInfo;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.entity.ztmgLoanBasicInfo.ZtmgLoanBasicInfo;
import com.power.platform.credit.entity.ztmgLoanBasicInfo.ZtmgLoanShareholdersInfo;
import com.power.platform.credit.entity.ztmgLoanBasicInfo.pojo.CreditAnnexFilePojo;
import com.power.platform.credit.entity.ztmgLoanBasicInfo.pojo.ShareholdersInfoPojo;
import com.power.platform.credit.entity.ztmgLoanBasicInfo.pojo.ZtmgLoanApplyAndBasicInfoPojo;
import com.power.platform.credit.service.annexfile.CreditAnnexFileService;
import com.power.platform.credit.service.apply.CreditUserApplyService;
import com.power.platform.credit.service.info.CreditInfoService;
import com.power.platform.credit.service.userinfo.CreditUserInfoService;
import com.power.platform.ifcert.creditor.service.CreditorDataAccessService;
import com.power.platform.ifcert.scatterInvest.service.ScatterInvestDataAccessService;
import com.power.platform.ifcert.status.service.ScatterInvestStatusDataAccessService;
import com.power.platform.ifcert.userInfo.service.IfcertUserInfoDataAccessService;
import com.power.platform.pay.service.LLPayService;
import com.power.platform.plan.entity.WloanTermProjectPlan;
import com.power.platform.plan.service.WloanTermProjectPlanService;
import com.power.platform.proapproval.entity.ProjectApproval;
import com.power.platform.proapproval.service.ProjectApprovalService;
import com.power.platform.regular.dao.WloanSubjectDao;
import com.power.platform.regular.dao.WloanTermInvestDao;
import com.power.platform.regular.dao.WloanTermProjectDao;
import com.power.platform.regular.entity.WGuaranteeCompany;
import com.power.platform.regular.entity.WloanSubject;
import com.power.platform.regular.entity.WloanTermDoc;
import com.power.platform.regular.entity.WloanTermInvest;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.regular.service.WGuaranteeCompanyService;
import com.power.platform.regular.service.WloanSubjectService;
import com.power.platform.regular.service.WloanTermDocService;
import com.power.platform.regular.service.WloanTermInvestService;
import com.power.platform.regular.service.WloanTermProjectService;
import com.power.platform.sms.service.SendSmsService;
import com.power.platform.sys.entity.AnnexFile;
import com.power.platform.sys.entity.Area;
import com.power.platform.sys.entity.User;
import com.power.platform.sys.service.AnnexFileService;
import com.power.platform.sys.service.AreaService;
import com.power.platform.sys.service.SystemService;

/**
 * 
 * 类: WloanTermProjectController <br>
 * 描述: 定期项目信息Controller. <br>
 * 作者: Mr.Roy <br>
 * 时间: 2018年5月4日 下午1:32:52
 */
@Controller
@RequestMapping(value = "${adminPath}/wloanproject/wloanTermProject")
public class WloanTermProjectController extends BaseController {

	@Autowired
	private WloanTermProjectService wloanTermProjectService;
	@Resource
	private WloanTermProjectDao wloanTermProjectDao;
	@Autowired
	private WGuaranteeCompanyService wGuaranteeCompanyService;
	@Autowired
	private WloanSubjectService wloanSubjectService;
	@Autowired
	private WloanTermDocService wloanTermDocService;
	@Autowired
	private WloanTermProjectPlanService wloanTermProjectPlanService;
	@Autowired
	private AnnexFileService annexFileService;
	@Autowired
	private WloanTermInvestService wloanTermInvestService;
	@Autowired
	private LLPayService llPayService;
	@Autowired
	private SendSmsService sendSmsService;
	@Autowired
	private ProjectApprovalService projectApprovalService;
	@Autowired
	private SystemService systemService;
	@Autowired
	private AreaService areaService;
	@Autowired
	private CreditUserApplyService creditUserApplyService;
	@Resource
	private CreditUserApplyDao creditUserApplyDao;
	@Autowired
	private CreditInfoService creditInfoService;
	@Autowired
	private CreditUserInfoService creditUserInfoService;
	@Resource
	private WloanTermInvestDao wloanTermInvestDao;
	@Resource
	private ZtmgLoanBasicInfoDao ztmgLoanBasicInfoDao;
	@Resource
	private ZtmgLoanShareholdersInfoDao ztmgLoanShareholdersInfoDao;
	@Autowired
	private CreditAnnexFileService creditAnnexFileService;
	@Autowired
	private CreditorDataAccessService creditorDataAccessService;
	@Autowired
	private ScatterInvestStatusDataAccessService scatterInvestStatusDataAccessService;
	@Autowired
	private ScatterInvestDataAccessService scatterInvestDataAccessService;
	@Autowired
	private IfcertUserInfoDataAccessService ifcertUserInfoDataAccessService;

	private static final Logger logger = Logger.getLogger(WloanTermProjectController.class);

	@ModelAttribute
	public WloanTermProject get(@RequestParam(required = false) String id) {

		WloanTermProject entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = wloanTermProjectService.get(id);
		}
		if (entity == null) {
			entity = new WloanTermProject();
		}
		return entity;
	}

	@RequiresPermissions("wloanproject:wloanTermProject:view")
	@RequestMapping(value = { "list", "" })
	public String list(WloanTermProject wloanTermProject, HttpServletRequest request, HttpServletResponse response, Model model) {

		if (null == wloanTermProject.getProjectProductType()) { // 标的产品类型为Null（安心投）.
			wloanTermProject.setProjectProductType(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1);
		}
		Page<WloanTermProject> page = wloanTermProjectService.findPage(new Page<WloanTermProject>(request, response), wloanTermProject);
		for (WloanTermProject entity : page.getList()) {
			if (entity.getWloanSubject() != null) {
				String creditUserId = entity.getWloanSubject().getLoanApplyId(); // 借款人ID.
				entity.getWloanSubject().setLoanPhone(CommonStringUtils.mobileEncrypt(entity.getWloanSubject().getLoanPhone()));
				entity.getWloanSubject().setLoanUser(CommonStringUtils.replaceNameX(entity.getWloanSubject().getLoanUser()));
				CreditUserInfo creditUserInfo = creditUserInfoService.get(creditUserId);
				if (creditUserInfo != null) {
					entity.setLoanUserName(creditUserInfo.getName());
				}
			}
		}
		model.addAttribute("page", page);
		model.addAttribute("usertype", SessionUtils.getUser().getUserType());

		return "modules/regular/wloan_term_project/wloanTermProjectList";
	}

	/**
	 * 
	 * 方法: creditUserLoanList <br>
	 * 描述: 借款人项目列表. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年6月22日 下午3:14:46
	 * 
	 * @param wloanTermProject
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequiresPermissions("wloanproject:wloanTermProject:view")
	@RequestMapping(value = "creditUserLoanList")
	public String creditUserLoanList(WloanTermProject wloanTermProject, HttpServletRequest request, HttpServletResponse response, Model model) {

		if (null == wloanTermProject.getProjectProductType()) { // 标的产品类型为Null（供应链）.
			wloanTermProject.setProjectProductType(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2);
		}
		Page<WloanTermProject> page = wloanTermProjectService.findPage(new Page<WloanTermProject>(request, response), wloanTermProject);
		for (WloanTermProject entity : page.getList()) {
			if (entity.getWloanSubject() != null) {
				String creditUserId = entity.getWloanSubject().getLoanApplyId(); // 借款人ID.
				entity.getWloanSubject().setLoanPhone(CommonStringUtils.mobileEncrypt(entity.getWloanSubject().getLoanPhone()));
				entity.getWloanSubject().setLoanUser(CommonStringUtils.replaceNameX(entity.getWloanSubject().getLoanUser()));
				CreditUserInfo creditUserInfo = creditUserInfoService.get(creditUserId);
				if (creditUserInfo != null) {
					entity.setLoanUserName(creditUserInfo.getName());
				}
			}
		}
		model.addAttribute("page", page);
		model.addAttribute("usertype", SessionUtils.getUser().getUserType());

		return "modules/regular/wloan_term_project/wloanTermProjectCreditUserLoanList";
	}

	@RequiresPermissions("wloanproject:wloanTermProject:view")
	@RequestMapping(value = "form")
	public String form(WloanTermProject wloanTermProject, Model model) {

		// 经营财务情况.
		wloanTermProject.setBusinessFinancialSituation("良好");
		// 还款能力情况.
		wloanTermProject.setAbilityToRepaySituation("暂无变化");
		// 平台逾期情况.
		wloanTermProject.setPlatformOverdueSituation("历史逾期0次，逾期金额0.00万元");
		// 涉诉情况.
		wloanTermProject.setLitigationSituation("无涉诉");
		// 受行政处罚情况.
		wloanTermProject.setAdministrativePunishmentSituation("无");

		// 担保机构.
		List<WGuaranteeCompany> wgCompanys = wGuaranteeCompanyService.findWGuaranteeCompanyListByCache();
		model.addAttribute("wgCompanys", wgCompanys);

		// 融资主体.
		WloanSubject wloanSubject = new WloanSubject();
		wloanSubject.setDelFlag(WloanSubject.DEL_FLAG_NORMAL);
		wloanSubject.setBeginCreateDate(DateUtils.getDateOfString("2017-11-01"));
		List<WloanSubject> wloanSubjects = wloanSubjectService.findList(wloanSubject);
		List<WloanSubject> wSubjects = new ArrayList<WloanSubject>();
		for (WloanSubject entity : wloanSubjects) {
			String creditUserId = entity.getLoanApplyId(); // 借款人ID.
			CreditUserInfo creditUserInfo = creditUserInfoService.get(creditUserId);
			if (null != creditUserInfo) {
				if (null != creditUserInfo.getCreditUserType()) {
					if (creditUserInfo.getCreditUserType().equals(CreditUserInfo.CREDIT_USER_TYPE_02)) { // 判断借款人账户类型，是否为借款户.
						wSubjects.add(entity);
					}
				}
			}
		}
		model.addAttribute("wSubjects", wSubjects);

		// 融资档案.
		WloanTermDoc wloanTermDoc = new WloanTermDoc();
		wloanTermDoc.setDelFlag(WloanTermDoc.DEL_FLAG_NORMAL);
		// wloanTermDoc.setBeginCreateDate(DateUtils.getDateOfString("2018-01-12"));
		List<WloanTermDoc> wloanDocs = wloanTermDocService.findList(wloanTermDoc);
		model.addAttribute("wloanDocs", wloanDocs);

		// 项目信息.
		model.addAttribute("wloanTermProject", wloanTermProject);

		return "modules/regular/wloan_term_project/wloanTermProjectForm";
	}

	/**
	 * 
	 * 方法: creditUserLoanForm <br>
	 * 描述: 借款人项目FORM. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年6月22日 下午2:57:41
	 * 
	 * @param wloanTermProject
	 * @param model
	 * @return
	 */
	@RequiresPermissions("wloanproject:wloanTermProject:view")
	@RequestMapping(value = "creditUserLoanForm")
	public String creditUserLoanForm(WloanTermProject wloanTermProject, Model model) {

		// 经营财务情况.
		wloanTermProject.setBusinessFinancialSituation("良好");
		// 还款能力情况.
		wloanTermProject.setAbilityToRepaySituation("暂无变化");
		// 平台逾期情况.
		wloanTermProject.setPlatformOverdueSituation("历史逾期0次，逾期金额0.00万元");
		// 涉诉情况.
		wloanTermProject.setLitigationSituation("无涉诉");
		// 受行政处罚情况.
		wloanTermProject.setAdministrativePunishmentSituation("无");

		model.addAttribute("wloanTermProject", wloanTermProject);
		// 融资主体.
		WloanSubject wloanSubject = new WloanSubject();
		wloanSubject.setDelFlag(WloanSubject.DEL_FLAG_NORMAL);
		wloanSubject.setBeginCreateDate(DateUtils.getDateOfString("2017-11-01"));
		List<WloanSubject> wloanSubjects = wloanSubjectService.findList(wloanSubject);
		List<WloanSubject> wSubjects = new ArrayList<WloanSubject>();
		for (WloanSubject entity : wloanSubjects) {
			String creditUserId = entity.getLoanApplyId(); // 借款人ID.
			CreditUserInfo creditUserInfo = creditUserInfoService.get(creditUserId);
			if (null != creditUserInfo) {
				if (null != creditUserInfo.getCreditUserType()) {
					if (creditUserInfo.getCreditUserType().equals(CreditUserInfo.CREDIT_USER_TYPE_02)) { // 判断借款人账户类型，是否为借款户.
						wSubjects.add(entity);
					}
				}
			}
		}
		model.addAttribute("wSubjects", wSubjects);
		// 融资档案.
		WloanTermDoc wloanTermDoc = new WloanTermDoc();
		wloanTermDoc.setDelFlag(WloanSubject.DEL_FLAG_NORMAL);
		List<WloanTermDoc> wloanDocs = wloanTermDocService.findList(wloanTermDoc);
		model.addAttribute("wloanDocs", wloanDocs);
		// 借款人资料列表.
		CreditInfo creditInfo = new CreditInfo();
		creditInfo.setDelFlag(CreditInfo.DEL_FLAG_NORMAL);
		List<CreditInfo> creditDocs = creditInfoService.findList(creditInfo);
		model.addAttribute("creditDocs", creditDocs);
		return "modules/regular/wloan_term_project/wloanTermProjectCreditUserLoanForm";
	}

	/**
	 * 
	 * 方法: isEntrustedPay <br>
	 * 描述: 受托支付标识，0：否，1：是. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年12月4日 上午9:22:49
	 * 
	 * @param isEntrustedPay
	 * @param model
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "isEntrustedPay")
	public Map<String, List<WloanSubject>> isEntrustedPay(String isEntrustedPay, Model model) {

		Map<String, List<WloanSubject>> params = new HashMap<String, List<WloanSubject>>();
		// 受托支付标识，0：否，1：是.
		if (WloanSubjectService.IS_ENTRUSTED_PAY_0.equals(isEntrustedPay)) {
			WloanSubject wloanSubject = new WloanSubject();
			wloanSubject.setIsEntrustedPay(WloanSubjectService.IS_ENTRUSTED_PAY_0);
			wloanSubject.setDelFlag(WloanSubject.DEL_FLAG_NORMAL);
			wloanSubject.setBeginCreateDate(DateUtils.getDateOfString("2017-11-01"));
			List<WloanSubject> wloanSubjects = wloanSubjectService.findList(wloanSubject);
			List<WloanSubject> wSubjects = new ArrayList<WloanSubject>();
			for (WloanSubject entity : wloanSubjects) {
				String creditUserId = entity.getLoanApplyId(); // 借款人ID.
				CreditUserInfo creditUserInfo = creditUserInfoService.get(creditUserId);
				if (null != creditUserInfo) {
					if (null != creditUserInfo.getCreditUserType()) {
						if (creditUserInfo.getCreditUserType().equals(CreditUserInfo.CREDIT_USER_TYPE_02)) { // 判断借款人账户类型，是否为借款户.
							wSubjects.add(entity);
						} else if (creditUserInfo.getCreditUserType().equals(CreditUserInfo.CREDIT_USER_TYPE_15)) {
							wSubjects.add(entity);
						}
					}
				}
			}
			params.put("wSubjects", wSubjects);
		} else if (WloanSubjectService.IS_ENTRUSTED_PAY_1.equals(isEntrustedPay)) {
			WloanSubject wloanSubject = new WloanSubject();
			wloanSubject.setIsEntrustedPay(WloanSubjectService.IS_ENTRUSTED_PAY_1);
			wloanSubject.setDelFlag(WloanSubject.DEL_FLAG_NORMAL);
			wloanSubject.setBeginCreateDate(DateUtils.getDateOfString("2017-11-01"));
			List<WloanSubject> wloanSubjects = wloanSubjectService.findList(wloanSubject);
			List<WloanSubject> wSubjects = new ArrayList<WloanSubject>();
			for (WloanSubject entity : wloanSubjects) {
				String creditUserId = entity.getLoanApplyId(); // 借款人ID.
				CreditUserInfo creditUserInfo = creditUserInfoService.get(creditUserId);
				if (null != creditUserInfo) {
					if (null != creditUserInfo.getCreditUserType()) {
						if (creditUserInfo.getCreditUserType().equals(CreditUserInfo.CREDIT_USER_TYPE_02)) { // 判断借款人账户类型，是否为借款户.
							wSubjects.add(entity);
						} else if (creditUserInfo.getCreditUserType().equals(CreditUserInfo.CREDIT_USER_TYPE_15)) {
							wSubjects.add(entity);
						}
					}
				}
			}
			params.put("wSubjects", wSubjects);
		}

		return params;
	}

	/**
	 * 
	 * methods: projectSnExist <br>
	 * description: 项目编号是否存在. <br>
	 * author: Mr.Roy <br>
	 * date: 2019年1月10日 上午9:50:58
	 * 
	 * @param sn
	 * @param model
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "findProSnExist")
	public String findProSnExist(String sn, Model model) {

		WloanTermProject entity = new WloanTermProject();
		entity.setSn(sn);
		List<WloanTermProject> list = wloanTermProjectDao.findProSnExist(entity);
		if (list != null && list.size() > 0) {
			return "false";
		} else {
			return "true";
		}
	}

	/**
	 * 
	 * 方法: isReplaceRepay <br>
	 * 描述: 是否代偿还款，0：否，1：是. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年12月13日 下午9:49:28
	 * 
	 * @param isReplaceRepay
	 * @param model
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "isReplaceRepay")
	public Map<String, List<CreditUserInfo>> isReplaceRepay(String isReplaceRepay, Model model) {

		Map<String, List<CreditUserInfo>> params = new HashMap<String, List<CreditUserInfo>>();
		if (isReplaceRepay.equals(WloanTermProject.IS_REPLACE_REPAY_0)) {// 代偿还款：否.
			params.put("creditUserInfos", null);
		} else if (isReplaceRepay.equals(WloanTermProject.IS_REPLACE_REPAY_1)) {// 代偿还款：是.
			CreditUserInfo entity = new CreditUserInfo();
			entity.setCreditUserType(CreditUserInfo.CREDIT_USER_TYPE_11);// 代偿户.
			List<CreditUserInfo> creditUserInfos = creditUserInfoService.findList(entity);
			params.put("creditUserInfos", creditUserInfos);
		}
		return params;
	}

	@ResponseBody
	@RequestMapping(value = "creditApplyByReplaceRepayUser")
	public Map<String, CreditUserApply> creditApplyByReplaceRepayUser(String creditApplyId, Model model) {

		Map<String, CreditUserApply> params = new HashMap<String, CreditUserApply>();

		CreditUserApply creditUserApply = creditUserApplyDao.get(creditApplyId);
		params.put("creditUserApply", creditUserApply);
		return params;
	}

	/**
	 * 
	 * 方法: creditUserApplysByWloanSubjectId <br>
	 * 描述: 融资主体-联动-借款申请. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2018年3月21日 上午11:29:55
	 * 
	 * @param wloanSubjectId
	 * @param model
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "creditUserApplysByWloanSubjectId")
	public Map<String, ZtmgLoanApplyAndBasicInfoPojo> creditUserApplysByWloanSubjectId(String wloanSubjectId, Model model) {

		Map<String, ZtmgLoanApplyAndBasicInfoPojo> params = new HashMap<String, ZtmgLoanApplyAndBasicInfoPojo>();
		ZtmgLoanApplyAndBasicInfoPojo ztmgLoanApplyAndBasicInfoPojo = new ZtmgLoanApplyAndBasicInfoPojo();
		// 融资主体.
		WloanSubject wloanSubject = wloanSubjectService.get(wloanSubjectId);
		if (null != wloanSubject) {
			// 借款人(供应商).
			String creditUserId = wloanSubject.getLoanApplyId();
			// 借款申请实体类.
			CreditUserApply entity = new CreditUserApply();
			entity.setCreditSupplyId(creditUserId);
			List<String> stateItem = new ArrayList<String>();
			stateItem.add(CreditUserApplyService.CREDIT_USER_APPLY_STATE_2);
			stateItem.add(CreditUserApplyService.CREDIT_USER_APPLY_STATE_4);
			stateItem.add(CreditUserApplyService.CREDIT_USER_APPLY_STATE_5);
			entity.setStateItem(stateItem);
			// 借款申请列表.
			List<CreditUserApply> creditUserApplys = creditUserApplyDao.findList(entity);
			// 借款人申请.
			ztmgLoanApplyAndBasicInfoPojo.setCreditUserApplys(creditUserApplys);

			/**
			 * 同时联动借款人基本信息.
			 */
			// 根据借款人查询借款人基本信息.
			ZtmgLoanBasicInfo ztmgLoanBasicInfo = new ZtmgLoanBasicInfo();
			ztmgLoanBasicInfo.setCreditUserId(creditUserId);
			ZtmgLoanBasicInfo ztmgLoanBasicInfoEntity = ztmgLoanBasicInfoDao.findByCreditUserId(ztmgLoanBasicInfo);
			if (null != ztmgLoanBasicInfoEntity) {
				// 根据借款人基本信息查询股东信息.
				ZtmgLoanShareholdersInfo ztmgLoanShareholdersInfo = new ZtmgLoanShareholdersInfo();
				ztmgLoanShareholdersInfo.setLoanBasicId(ztmgLoanBasicInfoEntity.getId());
				List<ZtmgLoanShareholdersInfo> ztmgLoanShareholdersInfos = ztmgLoanShareholdersInfoDao.findListByLoanBasicInfoId(ztmgLoanShareholdersInfo);
				List<ShareholdersInfoPojo> shareholdersInfoPojos = new ArrayList<ShareholdersInfoPojo>();
				for (ZtmgLoanShareholdersInfo zLoanShareholdersInfo : ztmgLoanShareholdersInfos) {
					ShareholdersInfoPojo shareholdersInfoPojo = new ShareholdersInfoPojo();
					shareholdersInfoPojo.setShareholdersType(zLoanShareholdersInfo.getShareholdersType()); // 股东类型.
					shareholdersInfoPojo.setShareholdersCertType(zLoanShareholdersInfo.getShareholdersCertType()); // 股东证件类型.
					shareholdersInfoPojo.setShareholdersName(zLoanShareholdersInfo.getShareholdersName()); // 股东名称.
					shareholdersInfoPojos.add(shareholdersInfoPojo);
				}
				String shareholdersInfoPojoJsons = JSON.toJSONString(shareholdersInfoPojos);
				// 借款人股东信息.
				ztmgLoanBasicInfoEntity.setShareholdersJsonArrayStr(shareholdersInfoPojoJsons);
				/**
				 * 征信报告.
				 */
				CreditAnnexFile annexFile = new CreditAnnexFile();
				annexFile.setType(CreditAnnexFileService.CREDIT_ANNEX_FILE_TYPE_18);
				annexFile.setOtherId(ztmgLoanBasicInfo.getCreditUserId());
				List<CreditAnnexFile> creditAnnexFileList = creditAnnexFileService.findCreditAnnexFileList(annexFile);
				List<CreditAnnexFilePojo> creditAnnexFilePojos = new ArrayList<CreditAnnexFilePojo>();
				for (CreditAnnexFile creditAnnexFile : creditAnnexFileList) {
					CreditAnnexFilePojo creditAnnexFilePojo = new CreditAnnexFilePojo();
					creditAnnexFilePojo.setId(creditAnnexFile.getId());
					creditAnnexFilePojo.setOtherId(creditAnnexFile.getOtherId());
					creditAnnexFilePojo.setUrl(creditAnnexFile.getUrl());
					creditAnnexFilePojo.setType(creditAnnexFile.getType());
					creditAnnexFilePojos.add(creditAnnexFilePojo);
				}
				String creditAnnexFilePojoJsons = JSON.toJSONString(creditAnnexFilePojos);
				// 借款人征信报告.
				ztmgLoanBasicInfoEntity.setCreditAnnexFileJsonArrayStr(creditAnnexFilePojoJsons);
			}
			// 借款人基本信息.
			ztmgLoanApplyAndBasicInfoPojo.setZtmgLoanBasicInfo(ztmgLoanBasicInfoEntity);

			params.put("ztmgLoanApplyAndBasicInfoPojo", ztmgLoanApplyAndBasicInfoPojo);
		} else {
			params.put("ztmgLoanApplyAndBasicInfoPojo", ztmgLoanApplyAndBasicInfoPojo);
		}
		return params;
	}

	/**
	 * 
	 * 方法: creditInfosByWloanSubjectId <br>
	 * 描述: 融资主体-联动-借款资料. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年12月13日 下午8:09:35
	 * 
	 * @param wloanSubjectId
	 * @param model
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "creditInfosByWloanSubjectId")
	public Map<String, List<CreditInfo>> creditInfosByWloanSubjectId(String wloanSubjectId, Model model) {

		Map<String, List<CreditInfo>> params = new HashMap<String, List<CreditInfo>>();
		// 融资主体.
		WloanSubject wloanSubject = wloanSubjectService.get(wloanSubjectId);
		if (null != wloanSubject) {
			String creditUserId = wloanSubject.getLoanApplyId(); // 借款人（法人）帐号，主键ID.
			// 借款资料.
			CreditInfo creditInfo = new CreditInfo();
			creditInfo.setDelFlag(CreditInfo.DEL_FLAG_NORMAL);// 删除标识，正常.
			creditInfo.setCreditUserId(creditUserId); // 借款人（法人）帐号，主键ID.
			List<CreditInfo> creditInfos = creditInfoService.findList(creditInfo);
			params.put("creditInfos", creditInfos);
		} else {
			params.put("creditInfos", null);
		}
		return params;
	}

	/**
	 * 
	 * 方法: selectSubjects <br>
	 * 描述: 选择融资主体，回显融资金额. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年6月23日 下午4:44:13
	 * 
	 * @param id
	 * @param model
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "selectSubjects")
	public Map<String, String> selectSubjects(String id, Model model) {

		Map<String, String> params = new HashMap<String, String>();

		// 融资主体.
		WloanSubject wloanSubject = wloanSubjectService.get(id);
		if (wloanSubject != null) {
			// 借款申请ID.
			String loanApplyId = wloanSubject.getLoanApplyId();
			// 借款申请.
			CreditUserApply creditUserApply = creditUserApplyDao.get(loanApplyId);
			if (creditUserApply != null) {
				// 借款金额.
				params.put("amount", creditUserApply.getAmount());
				// 借款期限.
				params.put("span", creditUserApply.getSpan());
				// 资金用途.
				params.put("borrPurpose", creditUserApply.getBorrPurpose());
				return params;
			}
		}
		return params;
	}

	/**
	 * 查看融资主体详情
	 * 
	 * @param wloanTermProject
	 * @param model
	 * @return
	 */
	@RequiresPermissions("wloanproject:wloanTermProject:view")
	@RequestMapping(value = "subjectViewForm")
	public String subjectViewForm(WloanTermProject wloanTermProject, Model model) {

		WloanSubject wloanSubject = wloanTermProject.getWloanSubject();
		model.addAttribute("wloanSubject", wloanSubject);
		model.addAttribute("wloanTermProject", wloanTermProject);
		return "modules/regular/wloan_term_project/wloanSubjectViewForm";
	}

	/**
	 * 查看担保机构详情
	 * 
	 * @param wloanTermProject
	 * @param model
	 * @return
	 */
	@RequiresPermissions("wloanproject:wloanTermProject:view")
	@RequestMapping(value = "guarViewForm")
	public String guarViewForm(WloanTermProject wloanTermProject, Model model) {

		WGuaranteeCompany wCompany = wloanTermProject.getWgCompany();
		wCompany.setBriefInfo(StringEscapeUtils.unescapeHtml(wCompany.getBriefInfo()));
		wCompany.setGuaranteeCase(StringEscapeUtils.unescapeHtml(wCompany.getGuaranteeCase()));
		wCompany.setGuaranteeScheme(StringEscapeUtils.unescapeHtml(wCompany.getGuaranteeScheme()));

		model.addAttribute("wGuaranteeCompany", wCompany);
		model.addAttribute("wloanTermProject", wloanTermProject);
		return "modules/regular/wloan_term_project/wGuaranteeCompanyView";
	}

	/**
	 * 查看融资档案详情
	 * 
	 * @param wloanTermProject
	 * @param model
	 * @return
	 */
	@RequiresPermissions("wloanproject:wloanTermProject:view")
	@RequestMapping(value = "docViewForm")
	public String docViewForm(WloanTermProject wloanTermProject, Model model) {

		WloanTermDoc wloanTermDoc = wloanTermProject.getWloanTermDoc();
		if (wloanTermDoc != null) {
			AnnexFile annexFile = new AnnexFile();
			annexFile.setDictType(WloanTermDocService.WLOAN_TERM_DOC_DIC_TYPE); // 资料类别.
			annexFile.setOtherId(wloanTermDoc.getId()); // 定期融资档案主键ID.
			annexFile.setTitle(wloanTermDoc.getName()); // 名称.
			annexFile.setReturnUrl("/wloan_term_doc/wloanTermDoc/manageForm?id=" + wloanTermDoc.getId()); // 回调URL.
			model.addAttribute("annexFile", annexFile);
			List<AnnexFile> annexFiles = annexFileService.findAnnexFilesByWloanTermDoc(annexFile);
			model.addAttribute("annexFiles", annexFiles);
			model.addAttribute("wloanTermDoc", wloanTermDoc);
		}
		model.addAttribute("wloanTermProject", wloanTermProject);
		return "modules/regular/wloan_term_project/docViewForm";
	}

	/**
	 * 复核跳转方法
	 * 
	 * @param wloanTermProject
	 * @param model
	 * @return
	 */
	@RequiresPermissions("wloanproject:wloanTermProject:view")
	@RequestMapping(value = "check")
	public String check(WloanTermProject wloanTermProject, Model model) {

		model.addAttribute("wloanTermProject", wloanTermProject);
		model.addAttribute("usertype", SessionUtils.getUser().getUserType());
		return "modules/regular/wloan_term_project/wloanTermProjectCheck";
	}

	/**
	 * 
	 * 方法: creditUserLoanCheck <br>
	 * 描述: 审核. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年6月22日 下午3:35:58
	 * 
	 * @param wloanTermProject
	 * @param model
	 * @return
	 */
	@RequiresPermissions("wloanproject:wloanTermProject:view")
	@RequestMapping(value = "creditUserLoanCheck")
	public String creditUserLoanCheck(WloanTermProject wloanTermProject, Model model) {

		WloanTermDoc wloanTermDoc = new WloanTermDoc();
		wloanTermDoc.setDelFlag(WloanSubject.DEL_FLAG_NORMAL);
		List<WloanTermDoc> wloanDocs = wloanTermDocService.findList(wloanTermDoc);
		// 借款人资料列表.
		CreditInfo creditInfo = new CreditInfo();
		creditInfo.setDelFlag(CreditInfo.DEL_FLAG_NORMAL);
		List<CreditInfo> creditDocs = creditInfoService.findList(creditInfo);
		// 借款资料.
		CreditInfo projectData = creditInfoService.get(wloanTermProject.getProjectDataId());
		// 借款申请详情.
		CreditUserApply creditUserApply = creditUserApplyDao.get(wloanTermProject.getCreditUserApplyId());

		model.addAttribute("creditUserApply", creditUserApply); // 借款申请.
		model.addAttribute("projectData", projectData); // 项目资料.
		model.addAttribute("creditDocs", creditDocs); // 资料列表.
		model.addAttribute("wloanDocs", wloanDocs); // 融资档案.
		model.addAttribute("wloanTermProject", wloanTermProject); // 项目信息.
		model.addAttribute("usertype", SessionUtils.getUser().getUserType()); // 后台用户业务类型.
		return "modules/regular/wloan_term_project/wloanTermProjectCreditUserLoanCheck";
	}

	/**
	 * 修改项目状态方法
	 * 安心投
	 * @param wloanTermProject
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("wloanproject:wloanTermProject:edit")
	@RequestMapping(value = "toBeCheck")
	@Transactional(rollbackFor = Exception.class)
	public String toBeCheck(WloanTermProject wloanTermProject, Model model, RedirectAttributes redirectAttributes) {

		if (!beanValidator(model, wloanTermProject)) {
			return form(wloanTermProject, model);
		}

		String message = "";

		// 提交审核（提交审核）
		if (wloanTermProject.getState().equals(WloanTermProjectService.DRAFT)) {
			wloanTermProject.setState(WloanTermProjectService.CHECK);
			wloanTermProject.setUpdateById(SessionUtils.getUser().getId());
			wloanTermProject.setUpdateDate(new Date());
			message = "定期项目信息提交审核成功";
		}

		// 撤销
		else if (wloanTermProject.getState().equals(WloanTermProjectService.CANCLE)) {
			wloanTermProject.setState(WloanTermProjectService.CANCLE);
			wloanTermProject.setUpdateById(SessionUtils.getUser().getId());
			wloanTermProject.setUpdateDate(new Date());
			message = "定期项目撤销成功";
		}

		// 通过审核（发布）
		else if (wloanTermProject.getState().equals(WloanTermProjectService.CHECK)) {
			wloanTermProject.setState(WloanTermProjectService.PUBLISH);
			wloanTermProject.setUpdateById(SessionUtils.getUser().getId());
			wloanTermProject.setUpdateDate(new Date());
			String result = wloanTermProjectPlanService.initWloanTermProjectPlan(wloanTermProject);
			if (result.equals("SUCCESS")) {
				message = "定期项目信息审核（发布）成功,生成还款计划成功";
			}
			if(ServerURLConfig.IS_REAL_TIME_PUSH) {
				//TODO
				//4.1.3 散标状态-（发布）
				//时时推送散标状态
				Map<String, Object> map = scatterInvestStatusDataAccessService.pushScatterInvestStatus(wloanTermProject.getId());
				logger.info("时时推送散标状态:"+map.get("respMsg").toString());
				
				//TODO
				//4.1.2 散标信息
				//时时推送散标信息
				List<String> projectIdList = new ArrayList<String>();
				projectIdList.add(wloanTermProject.getId());
				Map<String, Object> results = scatterInvestDataAccessService.pushScatterInvestInfo(projectIdList);
				logger.info("时时推散标信息:"+results.get("respMsg").toString());
				
				//TODO
				//4.1.1 用户信息
				// 时时推送借款用户信息
				WloanTermProject wtp = wloanTermProjectDao.get(wloanTermProject.getId());
				List<String> subIdList = new ArrayList<String>();
				subIdList.add(wtp.getSubjectId());
				Map<String, Object> pushUserResult = ifcertUserInfoDataAccessService.pushCreUserInfoC(subIdList);
				logger.info("推送借款用户信息-实时："+ pushUserResult);
			}
		}

		// 发布 -->> 上线
		else if (wloanTermProject.getState().equals(WloanTermProjectService.PUBLISH)) {
			wloanTermProject.setState(WloanTermProjectService.ONLINE);
			wloanTermProject.setUpdateById(SessionUtils.getUser().getId());
			wloanTermProject.setUpdateDate(new Date());
			message = "定期项目信息发布（上线）成功";
			
			if(ServerURLConfig.IS_REAL_TIME_PUSH) {
				//TODO
				//4.1.3 散标状态-（上线）
				//时时推送散标状态
				Map<String, Object> map = scatterInvestStatusDataAccessService.pushScatterInvestStatus(wloanTermProject.getId());
				logger.info("时时推送散标状态:"+map.get("respMsg").toString());
			}
			
		}

		// 上线 -->> 满标（切标）
		else if (wloanTermProject.getState().equals(WloanTermProjectService.ONLINE)) {
			try {
				wloanTermProject.setState(WloanTermProjectService.FULL);
				wloanTermProject.setUpdateById(SessionUtils.getUser().getId());
				wloanTermProject.setUpdateDate(new Date());
				wloanTermProject.setFullDate(new Date());
				// 切标时，金额同步（项目融资金额等于实际融资金额）.
				wloanTermProject.setAmount(wloanTermProject.getCurrentRealAmount());
				int updateWloanTermProject = wloanTermProjectService.updateWloanTermProject(wloanTermProject);
				if (updateWloanTermProject == 1) {
					message = "项目【切标】成功";
				} else {
					message = "项目【切标】失败";
				}
				addMessage(redirectAttributes, message);
				return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProject/?repage";
			} catch (Exception e) {
				logger.error(e.getCause());
				message = "项目【切标】失败";
				logger.info(message);
				return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProject/?repage";
			}
		}

		// 满标 -->> 放款（放款）
		else if (wloanTermProject.getState().equals(WloanTermProjectService.FULL)) {
			try {
				wloanTermProject.setState(WloanTermProjectService.REPAYMENT);
				wloanTermProject.setUpdateById(SessionUtils.getUser().getId());
				wloanTermProject.setUpdateDate(new Date());
				wloanTermProject.setRealLoanDate(new Date());
				/*
				 * wloanTermProject.setEndDate(DateUtils.getSpecifiedMonthAfter(
				 * wloanTermProject.getRealLoanDate(),
				 * wloanTermProject.getSpan()));
				 * System.out.println(wloanTermProject.getEndDate() +
				 * "--------------------------------项目结束日期");
				 */
				String contractUrl = productControct(wloanTermProject);
				wloanTermProject.setContractUrl(contractUrl);
				logger.info("生成合同路径：" + wloanTermProject.getContractUrl());
				message = "定期项目放款成功";
				// 向借款人打款
				WloanSubject wloanSubject = wloanSubjectService.get(wloanTermProject.getSubjectId());
				String cashier = wloanSubject.getCashierUser(); // 收款人姓名
				String cashierIdCard = wloanSubject.getCashierIdCard();// 身份证号
				String cashierBankNo = wloanSubject.getCashierBankNo();// 银行卡号
				String cashierBankAdderss = wloanSubject.getCashierBankAdderss();// 开户行
				String cashierBankCode = wloanSubject.getCashierBankCode();
				String areaId = wloanSubject.getLocus();// 开户城市
				Area area = new Area();
				area.setId(areaId);
				area = areaService.get(area);
				String cashierBankCityCode = area.getCityCode();
				String cashierBankProvinceCode = area.getParent().getCityCode();
				// 筹集金额
				double currentAmount = wloanTermProject.getCurrentAmount();
				// 保证金
				double marginPercentage = wloanTermProject.getMarginPercentage();
				// 手续费率
				double feeRate = wloanTermProject.getFeeRate();
				// 真正放款金额
				double realAmount = currentAmount - marginPercentage - feeRate;
				realAmount = NumberUtils.scaleDouble(realAmount);
				wloanTermProject.setCurrentRealAmount(realAmount);
				String noOrder = "L" + DateUtils.getCurrentDateTimeStr();
				Map<String, Object> resultMap = llPayService.goCashPay(cashier, cashierBankNo, cashierBankCityCode, cashierBankProvinceCode, cashierBankAdderss, cashierIdCard, realAmount, "借款人放款", noOrder, cashierBankCode);
				// 放款失败，抛出异常
				String ret_code = (String) resultMap.get("ret_code");
				System.out.println("放款返回码为：---------" + ret_code);
				if (ret_code != "0000" && !"0000".equals(ret_code)) {
					wloanTermProject.setState(WloanTermProjectService.FULL);
					throw new Exception("放款失败");
				}
				message = "定期项目放款成功";
				wloanTermProjectService.updateWloanTermProject(wloanTermProject);
				wloanTermProjectService.updateProState(wloanTermProject);

				Cache cache = MemCachedUtis.getMemCached();
				cache.set("projectId", wloanTermProject.getId());

				// 修改放款申请信息状态为完成
				ProjectApproval approval = new ProjectApproval();
				approval.setWloanTermProject(wloanTermProject);
				approval = projectApprovalService.getByProjectId(approval);
				if (approval != null) {
					approval.setState("5");
					projectApprovalService.save(approval);
					List<User> users = systemService.findAllUser();
					for (int i = 0; i < users.size(); i++) {
						User user = users.get(i);
						if (user.getUserType().equals("6")) {
							sendSmsService.directSendSMS(user.getMobile(), "您好,项目\"" + wloanTermProjectService.get(approval.getWloanTermProject().getId()).getName() + "\"已经完成放款，请您知悉。");

						}
					}
				}
				addMessage(redirectAttributes, message);
				
//				if(ServerURLConfig.IS_REAL_TIME_PUSH) {
//					//TODO
//					//4.1.3 散标状态-（放款）
//					//时时推送散标状态
//					Map<String, Object> map = scatterInvestStatusDataAccessService.pushScatterInvestStatus(wloanTermProject.getId());
//					logger.info("时时推送散标状态:"+map.get("respMsg").toString());
//					
//					//TODO
//					//4.1.5 初始债权
//					//时时推送初始债权
//					//上报初始债权数据触发时间：初始债权对应的散标放款成功之后上报该数据。
//					List<String> projectIdList = new ArrayList<String>();
//					projectIdList.add(wloanTermProject.getId());
//					Map<String, Object> results = creditorDataAccessService.pushCreditorInfo(projectIdList);
//					logger.info("时时推送散标状态:"+results.get("respMsg").toString());
//				}
				
				
				
			} catch (Exception e) {
				logger.error(e.getCause());
				message = "定期项目放款失败";
				addMessage(redirectAttributes, message);
			}
			return "redirect:" + Global.getAdminPath() + "/approval/proinfo/?repage";
		}
		wloanTermProjectService.updateProState(wloanTermProject);
		addMessage(redirectAttributes, message);
		return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProject/?repage";
	}

	/**
	 * 
	 * 方法: creditUserLoanToBeCheck <br>
	 * 描述: 借款人项目节点流转. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年6月22日 下午3:41:08
	 * 供应链
	 * @param wloanTermProject
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("wloanproject:wloanTermProject:edit")
	@RequestMapping(value = "creditUserLoanToBeCheck")
	@Transactional(rollbackFor = Exception.class)
	public String creditUserLoanToBeCheck(WloanTermProject wloanTermProject, Model model, RedirectAttributes redirectAttributes) {

		if (!beanValidator(model, wloanTermProject)) {
			return form(wloanTermProject, model);
		}

		String message = "";

		// 提交审核（提交审核）
		if (wloanTermProject.getState().equals(WloanTermProjectService.DRAFT)) {
			wloanTermProject.setState(WloanTermProjectService.CHECK);
			wloanTermProject.setUpdateById(SessionUtils.getUser().getId());
			wloanTermProject.setUpdateDate(new Date());
			message = "定期项目信息提交审核成功";
		}

		// 撤销
		else if (wloanTermProject.getState().equals(WloanTermProjectService.CANCLE)) {
			wloanTermProject.setState(WloanTermProjectService.CANCLE);
			wloanTermProject.setUpdateById(SessionUtils.getUser().getId());
			wloanTermProject.setUpdateDate(new Date());
			message = "定期项目撤销成功";
		}

		// 通过审核（发布）
		else if (wloanTermProject.getState().equals(WloanTermProjectService.CHECK)) {
			wloanTermProject.setState(WloanTermProjectService.PUBLISH);
			wloanTermProject.setUpdateById(SessionUtils.getUser().getId());
			wloanTermProject.setUpdateDate(new Date());
			String result = wloanTermProjectPlanService.initWloanTermProjectPlan(wloanTermProject);
			if (result.equals("SUCCESS")) {
				message = "定期项目信息审核（发布）成功,生成还款计划成功";
			}
			if(ServerURLConfig.IS_REAL_TIME_PUSH) {
				//TODO
				//4.1.3 散标状态-（发布）
				//时时推送散标状态
				Map<String, Object> map = scatterInvestStatusDataAccessService.pushScatterInvestStatus(wloanTermProject.getId());
				logger.info("时时推送散标状态:"+map.get("respMsg").toString());
				
				//TODO
				//4.1.2 散标信息
				//时时推送散标信息
				List<String> projectIdList = new ArrayList<String>();
				projectIdList.add(wloanTermProject.getId());
				Map<String, Object> results = scatterInvestDataAccessService.pushScatterInvestInfo(projectIdList);
				logger.info("时时推送散标信息:"+results.get("respMsg").toString());
				
				
				//TODO
				//4.1.1 用户信息
				// 时时推送借款用户信息
				WloanTermProject wtp = wloanTermProjectDao.get(wloanTermProject.getId());
				List<String> subIdList = new ArrayList<String>();
				subIdList.add(wtp.getSubjectId());
				Map<String, Object> pushUserResult = ifcertUserInfoDataAccessService.pushCreUserInfoC(subIdList);
				logger.info("推送借款用户信息-实时："+ pushUserResult);
			}
		}

		// 发布 -->> 上线
		else if (wloanTermProject.getState().equals(WloanTermProjectService.PUBLISH)) {
			wloanTermProject.setState(WloanTermProjectService.ONLINE);
			wloanTermProject.setUpdateById(SessionUtils.getUser().getId());
			wloanTermProject.setUpdateDate(new Date());
			wloanTermProject.setOnlineDate(new Date());
			// WloanSubject entity =
			// wloanSubjectService.get(wloanTermProject.getSubjectId());
			// if (wloanSubjectService.get(wloanTermProject.getSubjectId()) !=
			// null) {
			// CreditUserApply creditUserApply =
			// creditUserApplyService.get(entity.getLoanApplyId());
			// if (creditUserApply != null) {
			// // 同步更新借款申请的上线时间.
			// creditUserApply.setOnlineDate(new Date());
			// // 借款申请，状态为融资中.
			// creditUserApply.setState(CreditUserApplyService.CREDIT_USER_APPLY_STATE_6);
			// creditUserApplyDao.update(creditUserApply);
			// }
			// }
			message = "定期项目信息发布（上线）成功";
			if(ServerURLConfig.IS_REAL_TIME_PUSH) {
				//TODO
				//4.1.3 散标状态-（上线）
				//时时推送散标状态
				Map<String, Object> map = scatterInvestStatusDataAccessService.pushScatterInvestStatus(wloanTermProject.getId());
				logger.info("时时推送散标状态:"+map.get("respMsg").toString());
			}
		}

		// 上线 -->> 满标（切标）
		else if (wloanTermProject.getState().equals(WloanTermProjectService.ONLINE)) {
			try {
				/**
				 * 切标之前，判断投资笔数总额是否等于融资金额.
				 */
				List<WloanTermInvest> list = wloanTermInvestDao.findProjectInvestNumbers(wloanTermProject.getId());
				Double investAmount = 0D;
				for (WloanTermInvest entity : list) { // 遍历所有投资记录.
					investAmount = investAmount + entity.getAmount();
				}

				if (investAmount < wloanTermProject.getCurrentRealAmount()) { // 切标：放款金额不等于投资成功笔数总额.
					addMessage(redirectAttributes, "放款总额不等于投资成功笔数总额");
					return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProject/creditUserLoanList?repage";
				}

				// 满标.
				wloanTermProject.setState(WloanTermProjectService.FULL);
				// 更新人.
				wloanTermProject.setUpdateById(SessionUtils.getUser().getId());
				// 更新时间.
				wloanTermProject.setUpdateDate(new Date());
				// 满标时间.
				wloanTermProject.setFullDate(new Date());
				// 切标时，金额同步（项目融资金额等于实际融资金额）.
				wloanTermProject.setAmount(wloanTermProject.getCurrentRealAmount());
				int updateWloanTermProject = wloanTermProjectService.updateWloanTermProject(wloanTermProject);
				if (updateWloanTermProject == 1) {
					message = "项目【切标】成功";
				} else {
					message = "项目【切标】失败";
				}
				addMessage(redirectAttributes, message);
				return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProject/creditUserLoanList?repage";
			} catch (Exception e) {
				logger.error(e.getCause());
				message = "项目【切标】失败";
				logger.info(message);
				return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProject/creditUserLoanList?repage";
			}
		}
		// 满标 -->> 放款（放款）
		else if (wloanTermProject.getState().equals(WloanTermProjectService.FULL)) {
			try {
				wloanTermProject.setState(WloanTermProjectService.REPAYMENT);
				wloanTermProject.setUpdateById(SessionUtils.getUser().getId());
				wloanTermProject.setUpdateDate(new Date());
				wloanTermProject.setRealLoanDate(new Date());
				/*
				 * wloanTermProject.setEndDate(DateUtils.getSpecifiedMonthAfter(
				 * wloanTermProject.getRealLoanDate(),
				 * wloanTermProject.getSpan()));
				 * System.out.println(wloanTermProject.getEndDate() +
				 * "--------------------------------项目结束日期");
				 */
				String contractUrl = productControct(wloanTermProject);
				wloanTermProject.setContractUrl(contractUrl);
				logger.info("生成合同路径：" + wloanTermProject.getContractUrl());
				message = "定期项目放款成功";
				// 向借款人打款
				WloanSubject wloanSubject = wloanSubjectService.get(wloanTermProject.getSubjectId());
				;
				String cashier = wloanSubject.getCashierUser(); // 收款人姓名
				String cashierIdCard = wloanSubject.getCashierIdCard();// 身份证号
				String cashierBankNo = wloanSubject.getCashierBankNo();// 银行卡号
				String cashierBankAdderss = wloanSubject.getCashierBankAdderss();// 开户行
				String cashierBankCode = wloanSubject.getCashierBankCode();
				String areaId = wloanSubject.getLocus();// 开户城市
				Area area = new Area();
				area.setId(areaId);
				area = areaService.get(area);
				String cashierBankCityCode = area.getCityCode();
				String cashierBankProvinceCode = area.getParent().getCityCode();
				// 筹集金额
				double currentAmount = wloanTermProject.getCurrentAmount();
				// 保证金
				double marginPercentage = wloanTermProject.getMarginPercentage();
				// 手续费率
				double feeRate = wloanTermProject.getFeeRate();
				// 真正放款金额
				double realAmount = currentAmount - marginPercentage - feeRate;
				realAmount = NumberUtils.scaleDouble(realAmount);
				wloanTermProject.setCurrentRealAmount(realAmount);
				String noOrder = "L" + DateUtils.getCurrentDateTimeStr();
				Map<String, Object> resultMap = llPayService.goCashPay(cashier, cashierBankNo, cashierBankCityCode, cashierBankProvinceCode, cashierBankAdderss, cashierIdCard, realAmount, "借款人放款", noOrder, cashierBankCode);
				// 放款失败，抛出异常
				String ret_code = (String) resultMap.get("ret_code");
				System.out.println("放款返回码为：---------" + ret_code);
				if (ret_code != "0000" && !"0000".equals(ret_code)) {
					wloanTermProject.setState(WloanTermProjectService.FULL);
					throw new Exception("放款失败");
				}
				message = "定期项目放款成功";
				wloanTermProjectService.updateWloanTermProject(wloanTermProject);
				wloanTermProjectService.updateProState(wloanTermProject);

				Cache cache = MemCachedUtis.getMemCached();
				cache.set("projectId", wloanTermProject.getId());

				// 修改放款申请信息状态为完成
				ProjectApproval approval = new ProjectApproval();
				approval.setWloanTermProject(wloanTermProject);
				approval = projectApprovalService.getByProjectId(approval);
				if (approval != null) {
					approval.setState("5");
					projectApprovalService.save(approval);
					List<User> users = systemService.findAllUser();
					for (int i = 0; i < users.size(); i++) {
						User user = users.get(i);
						if (user.getUserType().equals("6")) {
							sendSmsService.directSendSMS(user.getMobile(), "您好,项目\"" + wloanTermProjectService.get(approval.getWloanTermProject().getId()).getName() + "\"已经完成放款，请您知悉。");

						}
					}
				}
				addMessage(redirectAttributes, message);
//				if(ServerURLConfig.IS_REAL_TIME_PUSH) {
//					//TODO
//					//4.1.3 散标状态-（放款）
//					//时时推送散标状态
//					Map<String, Object> map = scatterInvestStatusDataAccessService.pushScatterInvestStatus(wloanTermProject.getId());
//					logger.info("时时推送散标状态:"+map.get("respMsg").toString());
//					
//					//TODO
//					//4.1.5 初始债权
//					//时时推送初始债权
//					//上报初始债权数据触发时间：初始债权对应的散标放款成功之后上报该数据。
//					List<String> projectIdList = new ArrayList<String>();
//					projectIdList.add(wloanTermProject.getId());
//					Map<String, Object> results = creditorDataAccessService.pushCreditorInfo(projectIdList);
//					logger.info("时时推送散标状态:"+results.get("respMsg").toString());
//				}
				
			} catch (Exception e) {
				logger.error(e.getCause());
				message = "定期项目放款失败";
				addMessage(redirectAttributes, message);
			}
			return "redirect:" + Global.getAdminPath() + "/approval/proinfo/?repage";
		}
		wloanTermProjectService.updateProState(wloanTermProject);
		addMessage(redirectAttributes, message);
		return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProject/creditUserLoanList?repage";
	}

	@RequiresPermissions("wloanproject:wloanTermProject:edit")
	@RequestMapping(value = "save")
	public String save(WloanTermProject wloanTermProject, Model model, RedirectAttributes redirectAttributes) {

		if (!beanValidator(model, wloanTermProject)) {
			return form(wloanTermProject, model);
		}

		/**
		 * 更新借款人基本信息.
		 */
		ZtmgLoanBasicInfo ztmgLoanBasicInfo = wloanTermProject.getZtmgLoanBasicInfo();
		if (ztmgLoanBasicInfo != null) {
			if (!StringUtils.isBlank(ztmgLoanBasicInfo.getId())) { // 修改.
				ZtmgLoanBasicInfo ztmgLoanBasicInfoEntity = ztmgLoanBasicInfoDao.get(ztmgLoanBasicInfo.getId());
				ztmgLoanBasicInfoEntity.setCreditInformation(ztmgLoanBasicInfo.getCreditInformation()); // 征信信息.
				ztmgLoanBasicInfoEntity.setIndustry(ztmgLoanBasicInfo.getIndustry()); // 所属行业.
				ztmgLoanBasicInfoEntity.setContributedCapital(ztmgLoanBasicInfo.getContributedCapital()); // 实缴资本.
				ztmgLoanBasicInfoEntity.setAnnualRevenue(ztmgLoanBasicInfo.getAnnualRevenue()); // 上年营业收入.
				ztmgLoanBasicInfoEntity.setLiabilities(ztmgLoanBasicInfo.getLiabilities()); // 负债.
				ztmgLoanBasicInfoEntity.setOtherCreditInformation(ztmgLoanBasicInfo.getOtherCreditInformation()); // 其它借款信息.
				ztmgLoanBasicInfoEntity.setUpdateDate(new Date());
				ztmgLoanBasicInfoEntity.setRemark("借款人基本信息");
				int ztmgLoanBasicInfoUpdateFlag = ztmgLoanBasicInfoDao.update(ztmgLoanBasicInfoEntity);
				if (ztmgLoanBasicInfoUpdateFlag == 1) {
					logger.info(this.getClass() + "更新借款人基本信息成功");
				} else {
					logger.info(this.getClass() + "更新借款人基本信息失败");
				}
			}
		}

		if (null == wloanTermProject.getState()) { // 空判断.
			wloanTermProject.setState(WloanTermProjectService.DRAFT); // 草稿.
		} else {
			if (wloanTermProject.getState().equals(WloanTermProjectService.CANCLE)) { // 撤销状态判断.
				wloanTermProject.setState(WloanTermProjectService.DRAFT); // 草稿.
			}
		}

		wloanTermProject.setDelFlag(WloanTermProject.DEL_FLAG_NORMAL);
		if (wloanTermProject.getCreateById() == null) {
			wloanTermProject.setCreateById(SessionUtils.getUser().getId());
			wloanTermProject.setCreateDate(new Date());
		}
		wloanTermProject.setUpdateById(SessionUtils.getUser().getId());
		wloanTermProject.setUpdateDate(new Date());

		logger.info("发布日期" + wloanTermProject.getPublishDate());
		logger.info("上线日期" + wloanTermProject.getOnlineDate());
		wloanTermProject.setCurrentAmount(0d);
		wloanTermProject.setCurrentRealAmount(0d);

		if (wloanTermProject.getIsNewRecord()) { // 新记录，默认为false.
			wloanTermProject.setId(IdGen.uuid());
			int insertFlag = wloanTermProjectDao.insert(wloanTermProject);
			if (insertFlag == 1) {
				addMessage(redirectAttributes, "创建安心投项目成功");
			} else {
				addMessage(redirectAttributes, "创建安心投项目失败");
			}
		} else {
			int updateFlag = wloanTermProjectDao.update(wloanTermProject);
			if (updateFlag == 1) {
				addMessage(redirectAttributes, "修改安心投项目成功");
			} else {
				addMessage(redirectAttributes, "修改安心投项目失败");
			}
		}
		return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProject/?repage";
	}

	/**
	 * 
	 * 方法: creditUserLoanSave <br>
	 * 描述: 借款人项目SAVA. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年6月22日 下午3:24:20
	 * 
	 * @param wloanTermProject
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("wloanproject:wloanTermProject:edit")
	@RequestMapping(value = "creditUserLoanSave")
	public String creditUserLoanSave(WloanTermProject wloanTermProject, Model model, RedirectAttributes redirectAttributes) {

		if (!beanValidator(model, wloanTermProject)) {
			return form(wloanTermProject, model);
		}

		/**
		 * 更新借款人基本信息.
		 */
		ZtmgLoanBasicInfo ztmgLoanBasicInfo = wloanTermProject.getZtmgLoanBasicInfo();
		if (ztmgLoanBasicInfo != null) {
			if (!StringUtils.isBlank(ztmgLoanBasicInfo.getId())) { // 修改.
				ZtmgLoanBasicInfo ztmgLoanBasicInfoEntity = ztmgLoanBasicInfoDao.get(ztmgLoanBasicInfo.getId());
				ztmgLoanBasicInfoEntity.setCreditInformation(ztmgLoanBasicInfo.getCreditInformation()); // 征信信息.
				ztmgLoanBasicInfoEntity.setIndustry(ztmgLoanBasicInfo.getIndustry()); // 所属行业.
				ztmgLoanBasicInfoEntity.setContributedCapital(ztmgLoanBasicInfo.getContributedCapital()); // 实缴资本.
				ztmgLoanBasicInfoEntity.setAnnualRevenue(ztmgLoanBasicInfo.getAnnualRevenue()); // 上年营业收入.
				ztmgLoanBasicInfoEntity.setLiabilities(ztmgLoanBasicInfo.getLiabilities()); // 负债.
				ztmgLoanBasicInfoEntity.setOtherCreditInformation(ztmgLoanBasicInfo.getOtherCreditInformation()); // 其它借款信息.
				ztmgLoanBasicInfoEntity.setUpdateDate(new Date());
				ztmgLoanBasicInfoEntity.setRemark("借款人基本信息");
				int ztmgLoanBasicInfoUpdateFlag = ztmgLoanBasicInfoDao.update(ztmgLoanBasicInfoEntity);
				if (ztmgLoanBasicInfoUpdateFlag == 1) {
					logger.info(this.getClass() + "更新借款人基本信息成功");
				} else {
					logger.info(this.getClass() + "更新借款人基本信息失败");
				}
			}
		}

		if (null == wloanTermProject.getState()) { // 空判断.
			wloanTermProject.setState(WloanTermProjectService.DRAFT); // 草稿.
		} else {
			if (wloanTermProject.getState().equals(WloanTermProjectService.CANCLE)) { // 撤销状态判断.
				wloanTermProject.setState(WloanTermProjectService.DRAFT); // 草稿.
			}
		}

		wloanTermProject.setDelFlag(WloanTermProject.DEL_FLAG_NORMAL);
		if (wloanTermProject.getCreateById() == null) {
			wloanTermProject.setCreateById(SessionUtils.getUser().getId());
			wloanTermProject.setCreateDate(new Date());
		}
		wloanTermProject.setUpdateById(SessionUtils.getUser().getId());
		wloanTermProject.setUpdateDate(new Date());

		logger.info("发布日期" + wloanTermProject.getPublishDate());
		logger.info("上线日期" + wloanTermProject.getOnlineDate());
		wloanTermProject.setCurrentAmount(0d);
		wloanTermProject.setCurrentRealAmount(0d);

		if (wloanTermProject.getIsNewRecord()) { // 新记录，默认为false.
			wloanTermProject.setId(IdGen.uuid());
			int insertFlag = wloanTermProjectDao.insert(wloanTermProject);
			if (insertFlag == 1) {
				addMessage(redirectAttributes, "创建供应链项目成功");
			} else {
				addMessage(redirectAttributes, "创建供应链项目失败");
			}
		} else {
			int updateFlag = wloanTermProjectDao.update(wloanTermProject);
			if (updateFlag == 1) {
				addMessage(redirectAttributes, "修改供应链项目成功");
			} else {
				addMessage(redirectAttributes, "修改供应链项目失败");
			}
		}
		return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProject/creditUserLoanList?repage";
	}

	@RequiresPermissions("wloanproject:wloanTermProject:edit")
	@RequestMapping(value = "delete")
	public String delete(WloanTermProject wloanTermProject, RedirectAttributes redirectAttributes) {

		if (wloanTermProject.getProjectProductType().equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) { // 安心投.
			wloanTermProjectService.delete(wloanTermProject);
			addMessage(redirectAttributes, "删除定期项目信息成功");
			return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProject/?repage";
		}
		wloanTermProjectService.delete(wloanTermProject);
		addMessage(redirectAttributes, "删除定期项目信息成功");
		return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProject/creditUserLoanList?repage";
	}

	/**
	 * 生成项目合同（项目放款时）
	 * 
	 * @param wloanTermProject
	 * @return
	 */
	private String productControct(WloanTermProject wloanTermProject) {

		String resultURL = "";
		String templateName = "pdf_template.pdf";

		/**
		 * 查找借款人信息
		 */
		WloanSubject wloanSubject = wloanTermProject.getWloanSubject();

		/**
		 * 查找担保人信息
		 */
		WGuaranteeCompany wCompany = wloanTermProject.getWgCompany();

		wCompany.setBriefInfo(StringEscapeUtils.unescapeHtml(wCompany.getBriefInfo()));
		wCompany.setGuaranteeCase(StringEscapeUtils.unescapeHtml(wCompany.getGuaranteeCase()));
		wCompany.setGuaranteeScheme(StringEscapeUtils.unescapeHtml(wCompany.getGuaranteeScheme()));

		Map<String, String> data = new HashMap<String, String>();
		data.put("contract_no", DateUtils.getDateStr()); // 合同编号
		data.put("name", wloanSubject.getCompanyName()); // 乙方（借款人）
		data.put("card_id", wloanSubject.getLoanIdCard()); // 身份证号码
		data.put("third_name", wCompany.getBriefName()); // 丙方（担保人）
		data.put("legal_person", wCompany.getCorporation()); // 法人代表
		data.put("residence", wCompany.getAddress()); // 住所
		data.put("telphone", wCompany.getPhone()); // 电话

		data.put("project_name", wloanTermProject.getName()); // 借款项目名称
		data.put("project_no", wloanTermProject.getSn()); // 借款项目编号
		data.put("service_no", ""); // 《借款服务合同》编号
		data.put("guarantee_no", ""); // 《担保函》编号
		data.put("rmb", wloanTermProject.getAmount().toString()); // 借款金额（小写）
		data.put("rmd_da", PdfUtils.change(wloanTermProject.getAmount())); // 借款金额（大写）
		data.put("uses", wloanTermProject.getPurpose()); // 借款用途
		data.put("lend_date", DateUtils.formatDate(wloanTermProject.getRealLoanDate(), "yyyy-MM-dd")); // 借款日期
		data.put("term_date", wloanTermProject.getSpan().toString()); // 借款期限
		data.put("back_date", DateUtils.getSpecifiedMonthAfter(DateUtils.formatDate(wloanTermProject.getLoanDate(), "yyyy-MM-dd"), wloanTermProject.getSpan() / 30)); // 还本时间
		data.put("year_interest", wloanTermProject.getAnnualRate().toString()); // 年利率
		data.put("interest_sum", String.valueOf(NumberUtils.scaleDouble(wloanTermProject.getAmount() * wloanTermProject.getAnnualRate() * wloanTermProject.getSpan() / (365 * 100)))); // 利息总额

		data.put("bottom_name", wloanSubject.getCompanyName()); // 乙方（借款人）
		data.put("bottom_third_name", wCompany.getBriefName()); // 丙方（担保人）
		data.put("sign_date", DateUtils.formatDate(wloanTermProject.getRealLoanDate(), "yyyy年MM月dd日")); // 合同签订日期

		/**
		 * 查找项目投资记录
		 */
		WloanTermInvest wloanTermInvest = new WloanTermInvest();
		wloanTermInvest.setWloanTermProject(wloanTermProject);
		List<WloanTermInvest> investList = wloanTermInvestService.findList(wloanTermInvest);
		String title = "出借人本金利息表";

		String[] rowTitle = new String[] { "编号", "出借人", "身份证号", "出借金额", "利息总额" };

		List<String[]> dataList = new ArrayList<String[]>();
		String[] strings = null;
		for (int i = 0; i < investList.size(); i++) {
			WloanTermInvest invest = investList.get(i);
			strings = new String[rowTitle.length];
			strings[0] = String.valueOf(i + 1);
			strings[1] = invest.getUserInfo().getRealName() == null ? Util.hideString(invest.getUserInfo().getName(), 3, 4) : Util.hideString(invest.getUserInfo().getRealName(), 1, 1);
			strings[2] = invest.getUserInfo().getCertificateNo() == null ? "****************" : Util.hideString(invest.getUserInfo().getCertificateNo(), 5, 8);
			strings[3] = String.valueOf(invest.getAmount());
			strings[4] = String.valueOf(invest.getInterest());
			dataList.add(strings);
		}
		try {
			resultURL = PdfUtils.createPdfByTemplate(templateName, data, title, rowTitle, dataList, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultURL;
	}

	/**
	 * 
	 * 方法: exportProjectInfoFile <br>
	 * 描述: 导出项目信息EXCEL文件. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年6月22日 下午4:29:08
	 * 
	 * @param wloanTermProject
	 * @param request
	 * @param response
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("wloanproject:wloanTermProject:view")
	@RequestMapping(value = "exportProjectInfo", method = RequestMethod.POST)
	public String exportProjectInfoFile(WloanTermProject wloanTermProject, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {

		if (wloanTermProject.getProjectProductType().equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) {
			try {
				String fileName = "项目信息【安心投】" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
				List<WloanTermProject> list = wloanTermProjectService.findList(wloanTermProject);
				for (WloanTermProject entity : list) {
					if (entity.getWloanSubject() != null) {
						String creditUserId = entity.getWloanSubject().getLoanApplyId(); // 借款人ID.
						CreditUserInfo creditUserInfo = creditUserInfoService.get(creditUserId);
						if (creditUserInfo != null) {
							entity.setLoanUserName(creditUserInfo.getName());
						}
					}
				}
				new ExportExcel("项目信息【安心投】", WloanTermProject.class).setDataList(list).write(response, fileName).dispose();
				return null;
			} catch (Exception e) {
				addMessage(redirectAttributes, "导出项目信息【安心投】失败！失败信息：" + e.getMessage());
			}
		} else if (wloanTermProject.getProjectProductType().equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2)) {
			try {
				String fileName = "项目信息【供应链】" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
				List<WloanTermProject> list = wloanTermProjectService.findList(wloanTermProject);
				for (WloanTermProject entity : list) {
					if (entity.getWloanSubject() != null) {
						String creditUserId = entity.getWloanSubject().getLoanApplyId(); // 借款人ID.
						CreditUserInfo creditUserInfo = creditUserInfoService.get(creditUserId);
						if (creditUserInfo != null) {
							entity.setLoanUserName(creditUserInfo.getName());
						}
					}
				}
				new ExportExcel("项目信息【供应链】", WloanTermProject.class).setDataList(list).write(response, fileName).dispose();
				return null;
			} catch (Exception e) {
				addMessage(redirectAttributes, "导出项目信息【供应链】失败！失败信息：" + e.getMessage());
			}

		}

		return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProject/?repage";
	}

	/**
	 * 
	 * 方法: exportRepayPlanInfo <br>
	 * 描述: 导出还款计划EXCEL文件. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年6月22日 下午4:30:11
	 * 
	 * @param wloanTermProject
	 * @param request
	 * @param response
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("wloanproject:wloanTermProject:view")
	@RequestMapping(value = "exportRepayPlanInfo", method = RequestMethod.POST)
	public String exportRepayPlanInfo(WloanTermProject wloanTermProject, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {

		if (wloanTermProject.getProjectProductType().equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) {
			try {
				String fileName = "还款计划【安心投项目】" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
				List<WloanTermProject> list = wloanTermProjectService.findList(wloanTermProject);
				// 遍历项目列表，追加还款计划.
				List<WloanTermProjectPlan> plans = new ArrayList<WloanTermProjectPlan>();
				for (WloanTermProject model : list) {
					List<WloanTermProjectPlan> plan = wloanTermProjectPlanService.findListByProjectId(model.getId());
					if (model.getState().equals(WloanTermProjectService.REPAYMENT) || model.getState().equals(WloanTermProjectService.FINISH)) { // 还款中/已完成.
						for (WloanTermProjectPlan entity : plan) {
							if (entity.getWloanTermProject() != null) {
								// 融资主体.
								WloanSubject wloanSubject = wloanSubjectService.get(entity.getWloanTermProject().getSubjectId());
								if (wloanSubject != null) {
									// 借款人信息.
									CreditUserInfo creditUserInfo = creditUserInfoService.get(wloanSubject.getLoanApplyId());
									if (creditUserInfo != null) {
										if (entity.getPrincipal().equals(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1)) { // 判断是否是最后一期还款.
											Double interest = entity.getInterest(); // 还款金额（本息）.
											Double amount = model.getCurrentRealAmount(); // 项目真实放款金额.
											for (int i = 1; i < 3; i++) {
												if (i == 1) { // 利息.
													WloanTermProjectPlan newPlan = new WloanTermProjectPlan();
													newPlan.setWloanTermProject(model);
													newPlan.setWloanSubject(wloanSubject);
													newPlan.setCreditUserInfo(creditUserInfo);
													newPlan.setPrincipal(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_0);
													newPlan.setRepaymentDate(entity.getRepaymentDate());
													newPlan.setInterest(NumberUtils.scaleDouble(interest - amount));
													newPlan.setState(entity.getState());
													plans.add(newPlan);
												}
												if (i == 2) { // 本金.
													WloanTermProjectPlan newPlan = new WloanTermProjectPlan();
													newPlan.setWloanTermProject(model);
													newPlan.setWloanSubject(wloanSubject);
													newPlan.setCreditUserInfo(creditUserInfo);
													newPlan.setPrincipal(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1);
													newPlan.setRepaymentDate(entity.getRepaymentDate());
													newPlan.setInterest(NumberUtils.scaleDouble(amount));
													newPlan.setState(entity.getState());
													plans.add(newPlan);
												}
											}
										} else if (entity.getPrincipal().equals(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_0)) {
											entity.setWloanSubject(wloanSubject);
											entity.setCreditUserInfo(creditUserInfo);
											plans.add(entity);
										}
									} else {
										plans.add(entity);
									}
								}
							}
						}
					} else if (model.getState().equals(WloanTermProjectService.ONLINE) || model.getState().equals(WloanTermProjectService.FULL)) {
						for (WloanTermProjectPlan entity : plan) {
							plans.add(entity);
						}
					}
				}
				new ExportExcel("还款计划【安心投项目】", WloanTermProjectPlan.class).setDataList(plans).write(response, fileName).dispose();
				return null;
			} catch (Exception e) {
				addMessage(redirectAttributes, "导出还款计划【安心投项目】】失败！失败信息：" + e.getMessage());
			}
		} else if (wloanTermProject.getProjectProductType().equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2)) {
			try {
				String fileName = "还款计划【供应链项目】" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
				List<WloanTermProject> list = wloanTermProjectService.findList(wloanTermProject);
				// 遍历项目列表，追加还款计划.
				List<WloanTermProjectPlan> plans = new ArrayList<WloanTermProjectPlan>();
				for (WloanTermProject model : list) {
					List<WloanTermProjectPlan> plan = wloanTermProjectPlanService.findListByProjectId(model.getId());
					if (model.getState().equals(WloanTermProjectService.REPAYMENT) || model.getState().equals(WloanTermProjectService.FINISH)) { // 还款中/已完成.
						for (WloanTermProjectPlan entity : plan) {
							if (entity.getWloanTermProject() != null) {
								// 融资主体.
								WloanSubject wloanSubject = wloanSubjectService.get(entity.getWloanTermProject().getSubjectId());
								if (wloanSubject != null) {
									// 借款人信息.
									CreditUserInfo creditUserInfo = creditUserInfoService.get(wloanSubject.getLoanApplyId());
									if (creditUserInfo != null) {
										if (entity.getPrincipal().equals(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1)) { // 判断是否是最后一期还款.
											Double interest = entity.getInterest(); // 还款金额（本息）.
											Double amount = model.getCurrentRealAmount(); // 项目真实放款金额.
											for (int i = 1; i < 3; i++) {
												if (i == 1) { // 利息.
													WloanTermProjectPlan newPlan = new WloanTermProjectPlan();
													newPlan.setWloanTermProject(model);
													newPlan.setWloanSubject(wloanSubject);
													newPlan.setCreditUserInfo(creditUserInfo);
													newPlan.setPrincipal(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_0);
													newPlan.setRepaymentDate(entity.getRepaymentDate());
													newPlan.setInterest(NumberUtils.scaleDouble(interest - amount));
													newPlan.setState(entity.getState());
													plans.add(newPlan);
												}
												if (i == 2) { // 本金.
													WloanTermProjectPlan newPlan = new WloanTermProjectPlan();
													newPlan.setWloanTermProject(model);
													newPlan.setWloanSubject(wloanSubject);
													newPlan.setCreditUserInfo(creditUserInfo);
													newPlan.setPrincipal(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1);
													newPlan.setRepaymentDate(entity.getRepaymentDate());
													newPlan.setInterest(NumberUtils.scaleDouble(amount));
													newPlan.setState(entity.getState());
													plans.add(newPlan);
												}
											}
										} else if (entity.getPrincipal().equals(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_0)) {
											entity.setWloanSubject(wloanSubject);
											entity.setCreditUserInfo(creditUserInfo);
											plans.add(entity);
										}
									} else {
										plans.add(entity);
									}
								}
							}
						}
					} else if (model.getState().equals(WloanTermProjectService.ONLINE) || model.getState().equals(WloanTermProjectService.FULL)) {
						for (WloanTermProjectPlan entity : plan) {
							plans.add(entity);
						}
					}
				}
				new ExportExcel("还款计划【供应链项目】", WloanTermProjectPlan.class).setDataList(plans).write(response, fileName).dispose();
				return null;
			} catch (Exception e) {
				addMessage(redirectAttributes, "导出还款计划【供应链项目】失败！失败信息：" + e.getMessage());
			}
		}
		return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProject/?repage";
	}
}