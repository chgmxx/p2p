package com.power.platform.current.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
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
import com.power.platform.common.security.shiro.session.SessionUtils;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.PdfUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.web.BaseController;
import com.power.platform.current.entity.WloanCurrentPool;
import com.power.platform.current.entity.WloanCurrentProject;
import com.power.platform.current.entity.WloanCurrentUserInvest;
import com.power.platform.current.entity.invest.WloanCurrentProjectInvest;
import com.power.platform.current.entity.moment.WloanCurrentMomentInvest;
import com.power.platform.current.service.WloanCurrentPoolService;
import com.power.platform.current.service.WloanCurrentProjectService;
import com.power.platform.current.service.invest.WloanCurrentProjectInvestService;
import com.power.platform.current.service.invest.WloanCurrentUserInvestService;
import com.power.platform.current.service.moment.WloanCurrentMomentInvestService;
import com.power.platform.regular.entity.WGuaranteeCompany;
import com.power.platform.regular.entity.WloanSubject;
import com.power.platform.regular.entity.WloanTermDoc;
import com.power.platform.regular.service.WGuaranteeCompanyService;
import com.power.platform.regular.service.WloanSubjectService;
import com.power.platform.regular.service.WloanTermDocService;
import com.power.platform.sys.entity.AnnexFile;
import com.power.platform.sys.service.AnnexFileService;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.userinfo.service.UserInfoService;

/**
 * 活期融资项目Controller
 * @author Mr.Jia
 * @version 2016-01-12
 */
@Controller
@RequestMapping(value = "${adminPath}/current/project/wloanCurrentProject")
public class WloanCurrentProjectController extends BaseController {
	
	@Autowired
	private WloanCurrentProjectService wloanCurrentProjectService;
	@Autowired
	private WGuaranteeCompanyService wGuaranteeCompanyService;
	@Autowired
	private WloanSubjectService wloanSubjectService;
	@Autowired
	private WloanTermDocService wloanTermDocService;
	@Autowired
	private WloanCurrentPoolService wloanCurrentPoolService;
	@Autowired
	private AnnexFileService annexFileService;
	@Autowired
	private WloanCurrentUserInvestService wloanCurrentUserInvestService;
	@Autowired
	private WloanCurrentMomentInvestService wloanCurrentMomentInvestService;
	@Autowired
	private WloanCurrentProjectInvestService wloanCurrentProjectInvestService;
	@Autowired
	private UserInfoService userInfoService;
	
	
	private static final Logger logger = Logger.getLogger(WloanCurrentProjectController.class);
	
	@ModelAttribute
	public WloanCurrentProject get(@RequestParam(required=false) String id) {
		WloanCurrentProject entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = wloanCurrentProjectService.get(id);
		}
		if (entity == null){
			entity = new WloanCurrentProject();
		}
		return entity;
	}
	
	@RequiresPermissions("current:project:wloanCurrentProject:view")
	@RequestMapping(value = {"list", ""})
	public String list(WloanCurrentProject wloanCurrentProject, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<WloanCurrentProject> page = wloanCurrentProjectService.findPage(new Page<WloanCurrentProject>(request, response), wloanCurrentProject); 
		
		boolean isCanForward = false;
		// 判断是否有暂停中项目，如果有，则放款中项目可以进行转入
		if (page != null ) {
			if ( page.getList() != null && page.getList().size() > 0 ) {
				for (int i = 0; i < page.getList().size(); i++) {
					if ( WloanCurrentProjectService.PAUSE.equals(page.getList().get(i).getState())) {
						isCanForward = true;
						model.addAttribute("isCanForward", isCanForward);
					}
				}
			}
		}
		model.addAttribute("isCanForward", isCanForward);
		model.addAttribute("page", page);
		model.addAttribute("usertype", SessionUtils.getUser().getUserType());
		return "modules/current/project/wloanCurrentProjectList";
	}

	@RequiresPermissions("current:project:wloanCurrentProject:view")
	@RequestMapping(value = "form")
	public String form(WloanCurrentProject wloanCurrentProject, Model model) {
		
		WloanSubject wloanSubject = new WloanSubject();
		List<WloanSubject> wSubjects = wloanSubjectService.findList(wloanSubject);
		
		WGuaranteeCompany wGuaranteeCompany = new WGuaranteeCompany();
		List<WGuaranteeCompany> wgCompanys = wGuaranteeCompanyService.findList(wGuaranteeCompany);
		
		WloanTermDoc wloanTermDoc = new WloanTermDoc();
		List<WloanTermDoc> wloanDocs = wloanTermDocService.findList(wloanTermDoc);
		
		model.addAttribute("wloanDocs",wloanDocs);
		model.addAttribute("wgCompanys",wgCompanys);
		model.addAttribute("wSubjects",wSubjects);
		model.addAttribute("wloanCurrentProject", wloanCurrentProject);
		return "modules/current/project/wloanCurrentProjectForm";
	}

	@RequiresPermissions("current:project:wloanCurrentProject:edit")
	@RequestMapping(value = "save")
	public String save(WloanCurrentProject wloanCurrentProject, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, wloanCurrentProject)){
			return form(wloanCurrentProject, model);
		}
		WloanCurrentPool wloanCurrentPool = new WloanCurrentPool();
		List<WloanCurrentPool> poolList = wloanCurrentPoolService.findList(wloanCurrentPool);
		
		if ( poolList != null && poolList.size() > 0 ) {
			wloanCurrentProject.setAmmualRate(poolList.get(0).getAnnualRate());
			wloanCurrentProjectService.save(wloanCurrentProject);
			addMessage(redirectAttributes, "保存活期融资项目成功");
		} else {
			addMessage(redirectAttributes, "保存活期融资项目失败，请先添加活期资金池信息");
		}
		return "redirect:"+Global.getAdminPath()+"/current/project/wloanCurrentProject/?repage";
	}
	
	@RequiresPermissions("current:project:wloanCurrentProject:edit")
	@RequestMapping(value = "delete")
	public String delete(WloanCurrentProject wloanCurrentProject, RedirectAttributes redirectAttributes) {
		wloanCurrentProjectService.delete(wloanCurrentProject);
		addMessage(redirectAttributes, "删除活期融资项目成功");
		return "redirect:"+Global.getAdminPath()+"/current/project/wloanCurrentProject/?repage";
	}

	/**
	 * 提交审核
	 * @param wloanTermProject
	 * @param model
	 * @return
	 */
	@RequiresPermissions("current:project:wloanCurrentProject:view")
	@RequestMapping(value = "check")
	public String check(WloanCurrentProject wloanCurrentProject, Model model) {
		/**
		 * 上线状态的项目
		 * 	1、获取资金池可放款金额（用户投资表里待投资的总金额）（包括拆分过的数据）
		 * 	2、获取中间表拆分的数据
		 * 	3、可用金额 - 拆分的数据
		 */
		if ( wloanCurrentProject.getState().equals(WloanCurrentProjectService.ONLINE) ) {
			Double userInvestAmountTotal = 0.00;
			WloanCurrentUserInvest wloanCurrentUserInvest = new WloanCurrentUserInvest();
			wloanCurrentUserInvest.setState(WloanCurrentUserInvestService.WLOAN_CURRENT_USER_INVEST_STATE_1);
			List<WloanCurrentUserInvest> userInvestList = wloanCurrentUserInvestService.findList(wloanCurrentUserInvest);
			if ( userInvestList != null && userInvestList.size() > 0 ) {
				for (int i = 0; i < userInvestList.size(); i++) {
					Double userInvestAmount = userInvestList.get(i).getOnLineAmount();
					if ( userInvestAmount != null  ) {
						userInvestAmountTotal += userInvestAmount;
					}
				}
			}
			WloanCurrentMomentInvest entityMomentInvest = new WloanCurrentMomentInvest();
			entityMomentInvest.setState(WloanCurrentMomentInvestService.WLOAN_CURRENT_MOMENT_INVEST_STATE_WAIT);
			List<WloanCurrentMomentInvest> momentList = wloanCurrentMomentInvestService.findList(entityMomentInvest);
			if ( momentList != null && momentList.size() > 0 ) {
				for (int i = 0; i < momentList.size(); i++) {
					Double momentAmount = momentList.get(i).getAmount();
					if ( momentAmount != null ) {
						userInvestAmountTotal += momentAmount;
					}
				}
			}
			model.addAttribute("pool_aivalidate_amount", userInvestAmountTotal);
			model.addAttribute("feeamount", wloanCurrentProject.getAmount() * wloanCurrentProject.getFeeRate() / 100 );		// 手续费
			model.addAttribute("maxrepayamount", wloanCurrentProject.getAmount() - (wloanCurrentProject.getCurrentRealAmount() == null ? 0.00 : wloanCurrentProject.getCurrentRealAmount()));		// 项目待放款金额
		} else {
			model.addAttribute("pool_aivalidate_amount", 0.00);
			model.addAttribute("feeamount", wloanCurrentProject.getAmount() * wloanCurrentProject.getFeeRate() / 100 );		// 手续费
			model.addAttribute("maxrepayamount", wloanCurrentProject.getAmount() - (wloanCurrentProject.getCurrentRealAmount() == null ? 0.00 : wloanCurrentProject.getCurrentRealAmount()) );		// 项目待放款金额
		}
		
		
		model.addAttribute("wloanCurrentProject", wloanCurrentProject);
		model.addAttribute("usertype", SessionUtils.getUser().getUserType());
		return "modules/current/project/wloanCurrentProjectFormCheck";
	}
	
	
	/**
	 * 提交审核
	 * @param wloanTermProject
	 * @param model
	 * @return
	 */
	@RequiresPermissions("current:project:wloanCurrentProject:view")
	@RequestMapping(value = "toForwardThis")
	public String toForwardThis(WloanCurrentProject wloanCurrentProject, Model model) {
		/** 转到转入页面， 将暂停中项目投资记录转入该项目下  */
		
		// 获取待转入债权份额
		WloanCurrentProject wloanCurrentProjectTo = new WloanCurrentProject();
		wloanCurrentProjectTo.setState(WloanCurrentProjectService.PAUSE);
		List<WloanCurrentProject> waitList = wloanCurrentProjectService.findList(wloanCurrentProjectTo);
		Double waitAmountDouble = 0d;
		if ( waitList != null && waitList.size() > 0 ) {
			for (int i = 0; i < waitList.size(); i++) {
				waitAmountDouble += waitList.get(i).getCurrentRealAmount();
			}
			// 待转入债权份额
			model.addAttribute("waitAmountDouble", waitAmountDouble);
		}
		model.addAttribute("wloanCurrentProject", wloanCurrentProject);
		return "modules/current/project/wloanCurrentProjectFormForward";
	}
	
	
	/**
	 * 项目审核、上线、放款、结束
	 * @param wloanTermProject
	 * @param model
	 * @return
	 */
	@RequiresPermissions("current:project:wloanCurrentProject:view")
	@RequestMapping(value = "toBeCheck")
	public String toBeCheck(WloanCurrentProject wloanCurrentProject, Model model, 
			HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, wloanCurrentProject)){
			return form(wloanCurrentProject, model);
		}
		String message = "";
		wloanCurrentProject.setUpdateBy(SessionUtils.getUser());
		wloanCurrentProject.setUpdateDate(new Date());
		
		// 提交审核（提交审核）
		if (wloanCurrentProject.getState().equals(WloanCurrentProjectService.DRAFT)) {
			// 验证是否具有审核权限
			if (!SessionUtils.getUser().getUserType().equals("4")) {
				return form(wloanCurrentProject, model);
			} else {
				wloanCurrentProject.setState(WloanCurrentProjectService.CHECKED);
				wloanCurrentProjectService.updateState(wloanCurrentProject);
				message = "定期项目信息提交审核成功";
			}
		}
		
		// 项目上线
		else if (wloanCurrentProject.getState().equals(WloanCurrentProjectService.CHECKED)) {
			/** 上线设置上线时间、结束时间  */
			wloanCurrentProject.setOnlineDate(new Date());
			String endDateString = DateUtils.getSpecifiedMonthAfter(DateUtils.formatDate(wloanCurrentProject.getOnlineDate(), "yyyy-MM-dd"), 
					wloanCurrentProject.getSpan() / 30);
			wloanCurrentProject.setEndDate( DateUtils.getShortDateOfString(endDateString) );
			wloanCurrentProject.setState(WloanCurrentProjectService.ONLINE);
			wloanCurrentProjectService.updateState(wloanCurrentProject);
			message = "定期项目上线成功";
		}
		
		// 项目放款
		else if ( wloanCurrentProject.getState().equals(WloanCurrentProjectService.ONLINE) ){
			String repayAmountString = request.getParameter("repayamount");
			
			// 本次放款金额
			Double repayAmountDouble = Double.valueOf(repayAmountString);
			
			/*放款  1、 先对中间表数据进行放款, 2、 中间表金额小于放款额度，则继续从投资表进行放款  */
			// 判断是否扣除保证金
			List<String> investList = null;
			try {
				investList = wloanCurrentProjectService.repay(wloanCurrentProject, repayAmountDouble);
				
				if ( investList != null && investList.size() > 0 ) {
					WloanCurrentProject wloanCurrentProjectNow = wloanCurrentProjectService.get(wloanCurrentProject.getId());
					WloanCurrentProjectInvest wloanCurrentProjectInvest = new WloanCurrentProjectInvest();
					UserInfo userInfo = new UserInfo();
					for (int i = 0; i < investList.size(); i++) {
						wloanCurrentProjectInvest = wloanCurrentProjectInvestService.get(investList.get(i));
						userInfo = userInfoService.get(wloanCurrentProjectInvest.getUserid());
						String contractUrlString = createContractPdfPath(userInfo, wloanCurrentProjectNow, wloanCurrentProjectInvest );
						
						wloanCurrentProjectInvest.setContractUrl(contractUrlString);
						wloanCurrentProjectInvestService.save(wloanCurrentProjectInvest);
					}
					
					/**
					 * 判断项目是否全额放款，如果是，生成项目合同，如果不是，不生成
					 */
					if ( wloanCurrentProjectNow.getAmount().equals(wloanCurrentProjectNow.getCurrentRealAmount()) ) {
						String projectContractUrlString = createProjectContractUrl(wloanCurrentProjectNow);
						wloanCurrentProjectNow.setContractUrl(projectContractUrlString);
						wloanCurrentProjectService.save(wloanCurrentProjectNow);
					}
					message = "放款成功";
				} else {
					message = "放款失败";
				}
			} catch (Exception e) {
				message = e.getMessage();
			}
		}
		
		model.addAttribute("wloanCurrentProject", wloanCurrentProject);
		model.addAttribute("usertype", SessionUtils.getUser().getUserType());
		addMessage(redirectAttributes, message);
		return "redirect:"+Global.getAdminPath()+"/current/project/wloanCurrentProject/?repage";
	}
	

	/**
	 * 暂停融资项目债权转入方法
	 * @param wloanTermProject {要转入的项目}
	 * @param model				
	 * @return
	 */
	@RequiresPermissions("current:project:wloanCurrentProject:view")
	@RequestMapping(value = "toBeForwardThis")
	public String toBeForwardThis(WloanCurrentProject wloanCurrentProject, Model model, 
			HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, wloanCurrentProject)){
			return form(wloanCurrentProject, model);
		}
		String message = "";
		logger.info("执行债权转让方法");
		
		try {
			List<String> returnList = wloanCurrentProjectService.makeOver(wloanCurrentProject);
			
			// 新的记录生成合同(个人合同)
			if ( returnList != null && returnList.size() > 0 ) {
				// 开始生成合同
				WloanCurrentProject wloanCurrentProjectNow = wloanCurrentProjectService.get(wloanCurrentProject.getId());
				WloanCurrentProjectInvest wloanCurrentProjectInvest = new WloanCurrentProjectInvest();
				UserInfo userInfo = new UserInfo();
				for (int i = 0; i < returnList.size(); i++) {
					wloanCurrentProjectInvest = wloanCurrentProjectInvestService.get(returnList.get(i));
					userInfo = userInfoService.get(wloanCurrentProjectInvest.getUserid());
					String contractUrlString = createContractPdfPath(userInfo, wloanCurrentProjectNow, wloanCurrentProjectInvest );
					
					wloanCurrentProjectInvest.setContractUrl(contractUrlString);
					wloanCurrentProjectInvestService.save(wloanCurrentProjectInvest);
				}
				
				/**
				 * 判断项目是否全额放款，如果是，生成项目合同，如果不是，不生成
				 */
				if ( wloanCurrentProjectNow.getAmount().equals(wloanCurrentProjectNow.getCurrentRealAmount()) ) {
					String projectContractUrlString = createProjectContractUrl(wloanCurrentProjectNow);
					wloanCurrentProjectNow.setContractUrl(projectContractUrlString);
					wloanCurrentProjectService.save(wloanCurrentProjectNow);
				}
			}
			
			// 判断项目是否全额转入如果是生成合同，否则不操作
			message = "项目债权转入成功";
		} catch (Exception e) {
			message = e.getMessage();
		}
		
		model.addAttribute("wloanCurrentProject", wloanCurrentProject);
		model.addAttribute("usertype", SessionUtils.getUser().getUserType());
		addMessage(redirectAttributes, message);
		return "redirect:"+Global.getAdminPath()+"/current/project/wloanCurrentProject/?repage";
	}
	
	
	/**
	 * 查看融资主体详情
	 * @param wloanTermProject
	 * @param model
	 * @return
	 */
	@RequiresPermissions("current:project:wloanCurrentProject:view")
	@RequestMapping(value = "subjectViewForm")
	public String subjectViewForm(WloanCurrentProject wloanCurrentProject, Model model) {
		WloanSubject wloanSubject = wloanCurrentProject.getWloanSubject();
		model.addAttribute("wloanSubject", wloanSubject);
		model.addAttribute("wloanCurrentProject", wloanCurrentProject);
		return "modules/current/project/wloanSubjectViewForm";
	}
	
	/**
	 * 查看担保机构详情
	 * @param wloanTermProject
	 * @param model
	 * @return
	 */
	@RequiresPermissions("current:project:wloanCurrentProject:view")
	@RequestMapping(value = "guarViewForm")
	public String guarViewForm(WloanCurrentProject wloanCurrentProject, Model model) {
		WGuaranteeCompany wCompany = wloanCurrentProject.getWgCompany();
		wCompany.setBriefInfo(StringEscapeUtils.unescapeHtml(wCompany.getBriefInfo()));
		wCompany.setGuaranteeCase(StringEscapeUtils.unescapeHtml(wCompany.getGuaranteeCase()));
		wCompany.setGuaranteeScheme(StringEscapeUtils.unescapeHtml(wCompany.getGuaranteeScheme()));
		
		model.addAttribute("wGuaranteeCompany", wCompany);
		model.addAttribute("wloanCurrentProject", wloanCurrentProject);
		return "modules/current/project/wGuaranteeCompanyView";
	}
	
	/**
	 * 查看融资档案详情
	 * @param wloanTermProject
	 * @param model
	 * @return
	 */
	@RequiresPermissions("current:project:wloanCurrentProject:view")
	@RequestMapping(value = "docViewForm")
	public String docViewForm(WloanCurrentProject wloanCurrentProject, Model model) {
		WloanTermDoc wloanTermDoc = wloanCurrentProject.getWloanTermDoc();
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
		model.addAttribute("wloanCurrentProject", wloanCurrentProject);
		return "modules/current/project/docViewForm";
	}
	
	
	
	/**
	 * 放款时生成活期投资合同（个人合同）
	 * @param userInfo
	 * @param wloanTermProject
	 * @return
	 */
	public String createContractPdfPath(UserInfo userInfo, WloanCurrentProject wloanCurrentProject, 
			WloanCurrentProjectInvest wloanCurrentProjectInvest) {
		// 四方合同存储路径.
		String contractPdfPath = "";
		WloanSubject wloanSubject = wloanCurrentProject.getWloanSubject();
		WGuaranteeCompany wGuaranteeCompany = wloanCurrentProject.getWgCompany();

		// 模版名称.
		String templateName = "pdf_template.pdf";
		// PDF(Key:Value).
		Map<String, String> map = new HashMap<String, String>();
		map.put("contract_no", DateUtils.getDateStr()); // 合同编号.
		if (wloanSubject != null) { // 融资主体.
			map.put("name", wloanSubject.getCompanyName()); // 乙方（借款人）.
			map.put("card_id", wloanSubject.getLoanIdCard()); // 身份证号码.
			map.put("bottom_name", wloanSubject.getCompanyName()); // 乙方（借款人）.
		}

		if (wGuaranteeCompany != null) { // 担保机构.
			map.put("third_name", wGuaranteeCompany.getName()); // 丙方（担保人）.
			map.put("legal_person", wGuaranteeCompany.getCorporation()); // 法人代表.
			map.put("residence", wGuaranteeCompany.getAddress()); // 住所.
			map.put("telphone", wGuaranteeCompany.getPhone()); // 电话.
			map.put("bottom_third_name", wGuaranteeCompany.getName()); // 丙方（担保人）.
		}

		if (wloanCurrentProject != null) { // 定期融资项目.
			map.put("project_name", wloanCurrentProject.getName()); // 借款项目名称.
			map.put("project_no", wloanCurrentProject.getSn()); // 借款项目编号.
			map.put("rmb", wloanCurrentProject.getAmount().toString()); // 借款总额.
			map.put("rmd_da", PdfUtils.change(wloanCurrentProject.getAmount())); // 借款总额大写.
			map.put("uses", wloanCurrentProject.getPurpose()); // 借款用途.
			map.put("lend_date", DateUtils.getDate(new Date(), "yyyy-MM-dd")); // 借款日期.
			map.put("term_date", wloanCurrentProject.getSpan().toString()); // 借款期限.
			map.put("back_date", ""); // 还本日期.
			map.put("year_interest", wloanCurrentProject.getAmmualRate().toString()); // 年利率.
			map.put("interest_sum", ""); // 利息总额.

		}
		map.put("service_no", ""); // 借款服务合同编号.
		map.put("guarantee_no", ""); // 连带责任保证担保涵编号.
		map.put("sign_date", DateUtils.getDate(new Date(), "yyyy年MM月dd日")); // 签订合同日期.

		// 投资title.
		String title = "出借人本金利息表";
		// 投资列表Title.
		String[] rowTitle = new String[] { "投资人", "投资金额", "年利率" };
		// 投资详细信息
		ArrayList<String[]> dataList = new ArrayList<String[]>();
		String[] strings = null;
		strings = new String[rowTitle.length];
		strings[0] = userInfo.getRealName().toString();
		strings[1] = wloanCurrentProjectInvest.getAmount().toString();
		strings[2] = wloanCurrentProject.getAmmualRate().toString();
		dataList.add(strings);
		try {
			contractPdfPath = PdfUtils.createPdfByTemplate(templateName, map, title, rowTitle, dataList, null, null);
			logger.info("fn:createContractPdfPath,{生成活期投资合同成功}");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("fn:createContractPdfPath,{异常：" + e.getMessage() + "}");
		}
		return contractPdfPath;
	}
	
	
	/**
	 * 生成项目合同
	 */
	public String createProjectContractUrl( WloanCurrentProject wloanCurrentProject ) {
		// 四方合同存储路径.
		String contractPdfPath = "";
		WloanSubject wloanSubject = wloanCurrentProject.getWloanSubject();
		WGuaranteeCompany wGuaranteeCompany = wloanCurrentProject.getWgCompany();

		// 模版名称.
		String templateName = "pdf_template.pdf";
		// PDF(Key:Value).
		Map<String, String> map = new HashMap<String, String>();
		map.put("contract_no", DateUtils.getDateStr()); // 合同编号.
		if (wloanSubject != null) { // 融资主体.
			map.put("name", wloanSubject.getCompanyName()); // 乙方（借款人）.
			map.put("card_id", wloanSubject.getLoanIdCard()); // 身份证号码.
			map.put("bottom_name", wloanSubject.getCompanyName()); // 乙方（借款人）.
		}

		if (wGuaranteeCompany != null) { // 担保机构.
			map.put("third_name", wGuaranteeCompany.getName()); // 丙方（担保人）.
			map.put("legal_person", wGuaranteeCompany.getCorporation()); // 法人代表.
			map.put("residence", wGuaranteeCompany.getAddress()); // 住所.
			map.put("telphone", wGuaranteeCompany.getPhone()); // 电话.
			map.put("bottom_third_name", wGuaranteeCompany.getName()); // 丙方（担保人）.
		}

		if (wloanCurrentProject != null) { // 定期融资项目.
			map.put("project_name", wloanCurrentProject.getName()); // 借款项目名称.
			map.put("project_no", wloanCurrentProject.getSn()); // 借款项目编号.
			map.put("rmb", wloanCurrentProject.getAmount().toString()); // 借款总额.
			map.put("rmd_da", PdfUtils.change(wloanCurrentProject.getAmount())); // 借款总额大写.
			map.put("uses", wloanCurrentProject.getPurpose()); // 借款用途.
			map.put("lend_date", DateUtils.getDate(new Date(), "yyyy-MM-dd")); // 借款日期.
			map.put("term_date", wloanCurrentProject.getSpan().toString()); // 借款期限.
			map.put("back_date", ""); // 还本日期.
			map.put("year_interest", wloanCurrentProject.getAmmualRate().toString()); // 年利率.
			map.put("interest_sum", ""); // 利息总额.

		}
		map.put("service_no", ""); // 借款服务合同编号.
		map.put("guarantee_no", ""); // 连带责任保证担保涵编号.
		map.put("sign_date", DateUtils.getDate(new Date(), "yyyy年MM月dd日")); // 签订合同日期.

		// 投资title.
		String title = "出借人本金利息表";
		WloanCurrentProjectInvest entity = new WloanCurrentProjectInvest();
		entity.setProjectId( wloanCurrentProject.getId() );
		List<WloanCurrentProjectInvest> investList = wloanCurrentProjectInvestService.findList(entity);
		
		
		// 投资列表Title.
		String[] rowTitle = new String[] { "投资人", "投资金额", "日期" };
		// 投资详细信息
		ArrayList<String[]> dataList = new ArrayList<String[]>();
		String[] strings = null;
		strings = new String[rowTitle.length];
		
		if ( investList != null && investList.size() > 0 ) {
			for (int i = 0; i < investList.size(); i++) {
				entity = investList.get(i);
				UserInfo userInfo = userInfoService.get(entity.getUserid());
				strings[0] = userInfo.getRealName().toString();
				strings[1] = entity.getAmount().toString();
				strings[2] = DateUtils.formatDate(entity.getBidDate(),"yyyy-MM-dd");
				dataList.add(strings);
			}
		}
		
		try {
			contractPdfPath = PdfUtils.createPdfByTemplate(templateName, map, title, rowTitle, dataList, null, null);
			logger.info("fn:createContractPdfPath,{生成活期投资合同成功}");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("fn:createContractPdfPath,{异常：" + e.getMessage() + "}");
		}
		return contractPdfPath;
	}
}