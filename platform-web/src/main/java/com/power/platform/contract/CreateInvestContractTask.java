package com.power.platform.contract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.power.platform.cache.Cache;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.MemCachedUtis;
import com.power.platform.common.utils.PdfUtils;
import com.power.platform.common.utils.Util;
import com.power.platform.plan.entity.WloanTermUserPlan;
import com.power.platform.plan.service.WloanTermUserPlanService;
import com.power.platform.regular.entity.WGuaranteeCompany;
import com.power.platform.regular.entity.WloanSubject;
import com.power.platform.regular.entity.WloanTermInvest;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.regular.service.WGuaranteeCompanyService;
import com.power.platform.regular.service.WloanSubjectService;
import com.power.platform.regular.service.WloanTermInvestService;
import com.power.platform.regular.service.WloanTermProjectService;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.userinfo.service.UserInfoService;

/**
 * 放款之后生成用户投资合同
 * 
 * @author Jia
 */
@Service("createInvestContractTask")
@Lazy(false)
public class CreateInvestContractTask {

	private static final Logger logger = Logger.getLogger(CreateInvestContractTask.class);

	@Autowired
	private WloanSubjectService wloanSubjectService;
	@Autowired
	private WGuaranteeCompanyService wGuaranteeCompanyService;
	@Autowired
	private WloanTermUserPlanService wloanTermUserPlanService;
	@Autowired
	private WloanTermProjectService wloanTermProjectService;
	@Autowired
	private WloanTermInvestService wloanTermInvestService;
	@Autowired
	private UserInfoService userInfoService;

	@Scheduled(cron = "0 */5 * * * ?")
	public void runJob() {

		try {
			logger.info("开始生成合同----------------------------------");

			Cache cache = MemCachedUtis.getMemCached();
			// cache.set("projectId", "c2483f225abf4add9a55f4f538c08b9e");

			String project_id = cache.get("projectId");
			System.out.println("===========================================project_id : " + project_id);

			// 为null，直接抛异常
			if (project_id == null || "".equals(project_id)) {
				throw new Exception("没有需要生成合同的项目================");
			}

			// 根据项目id查找项目信息
			WloanTermProject project = wloanTermProjectService.get(project_id);
			if (project == null) {
				throw new Exception("项目查找异常=========================");
			}

			// 根据项目信息查找项目投资记录
			WloanTermInvest invest = new WloanTermInvest();
			invest.setWloanTermProject(project);
			List<WloanTermInvest> list = wloanTermInvestService.findList(invest);
			System.out.println(list.size() + "：===================== 条投资记录");

			if (list == null || list.size() <= 0) {
				throw new Exception("投资记录查找异常=========================");
			}

			UserInfo userInfo = new UserInfo();
			for (int i = 0; i < list.size(); i++) {
				invest = list.get(i);
				userInfo = userInfoService.get(invest.getUserInfo().getId());
				String contract_path = createContractPdfPath(userInfo, project, invest, list);
				System.out.println("合同路径是：=========================" + contract_path);
				invest.setContractPdfPath(contract_path.split("data")[1]);
				wloanTermInvestService.updateWloanTermInvest(invest);
			}

			cache.set("projectId", "");
		} catch (Exception e) {
			// e.printStackTrace();
			logger.info(e.getMessage() + "===============================");
		}

	}

	/**
	 * 
	 * @param userInfo
	 * @param wloanTermProject
	 * @return
	 */
	public String createContractPdfPath(UserInfo userInfo, WloanTermProject project, WloanTermInvest invest, List<WloanTermInvest> list) {

		// 四方合同存储路径.
		String contractPdfPath = "";

		/**
		 * 融资主体.
		 */
		String subjectId = project.getSubjectId();// 融资主体ID.
		WloanSubject wloanSubject = wloanSubjectService.get(subjectId);

		/**
		 * 担保机构.
		 */
		String guaranteeId = project.getGuaranteeId();
		WGuaranteeCompany wGuaranteeCompany = wGuaranteeCompanyService.get(guaranteeId);

		// 模版名称.
		String templateName = "pdf_template.pdf";
		// PDF(Key:Value).
		Map<String, String> map = new HashMap<String, String>();
		map.put("contract_no", DateUtils.getDateStr()); // 合同编号.
		if (wloanSubject != null) { // 融资主体.
			map.put("name", Util.hideString(wloanSubject.getLoanUser() == null ? "**" : wloanSubject.getLoanUser(), 1, 1)); // 乙方（借款人）.
			map.put("card_id", Util.hideString(wloanSubject.getLoanIdCard() == null ? "**********" : wloanSubject.getLoanIdCard(), 6, 8)); // 身份证号码.
			map.put("bottom_name", Util.hideString(wloanSubject.getLoanUser() == null ? "**" : wloanSubject.getLoanUser(), 1, 1)); // 乙方（借款人）.
		}

		if (wGuaranteeCompany != null) { // 担保机构.
			map.put("third_name", wGuaranteeCompany.getName()); // 丙方（担保人）.
			map.put("legal_person", wGuaranteeCompany.getCorporation()); // 法人代表.
			map.put("residence", wGuaranteeCompany.getAddress()); // 住所.
			map.put("telphone", wGuaranteeCompany.getPhone()); // 电话.
			map.put("bottom_third_name", wGuaranteeCompany.getName()); // 丙方（担保人）.
		}

		if (project != null) { // 定期融资项目.
			map.put("project_name", project.getName()); // 借款项目名称.
			map.put("project_no", project.getSn()); // 借款项目编号.
			map.put("rmb", project.getAmount().toString()); // 借款总额.
			map.put("rmd_da", PdfUtils.change(project.getAmount())); // 借款总额大写.
			map.put("uses", project.getPurpose()); // 借款用途.
			map.put("lend_date", DateUtils.getDate(project.getLoanDate(), "yyyy-MM-dd")); // 借款日期.
			map.put("term_date", project.getSpan().toString()); // 借款期限.
			map.put("back_date", DateUtils.getSpecifiedMonthAfter(DateUtils.formatDateTime(project.getLoanDate()), project.getSpan() / 30)); // 还本日期.
			map.put("year_interest", project.getAnnualRate().toString()); // 年利率.
			map.put("interest_sum", invest.getInterest().toString()); // 利息总额.

		}
		map.put("service_no", ""); // 借款服务合同编号.
		map.put("guarantee_no", ""); // 连带责任保证担保涵编号.
		map.put("sign_date", DateUtils.getDate(invest.getBeginDate(), "yyyy年MM月dd日")); // 签订合同日期.

		/**
		 * 客户投资还款计划.
		 */
		WloanTermUserPlan entity = new WloanTermUserPlan();
		entity.setWloanTermProject(project);
		entity.setWloanTermInvest(invest);
		List<WloanTermUserPlan> WloanTermUserPlanList = wloanTermUserPlanService.findList(entity);
		// 还款计划title.
		String title = "还款计划表（投资者本人）";
		// 还款计划rowTitle.
		String[] rowTitle = new String[] { "还款日期", "类型", "本金/利息" };
		// 还款计划rowData.
		ArrayList<String[]> dataList = new ArrayList<String[]>();
		String[] strings = null;
		for (WloanTermUserPlan wloanTermUserPlan : WloanTermUserPlanList) {
			strings = new String[rowTitle.length];
			strings[0] = DateUtils.getDate(wloanTermUserPlan.getRepaymentDate(), "yyyy年MM月dd日");
			if (WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_PRINCIPAL_1.equals(wloanTermUserPlan.getPrincipal())) {
				strings[1] = "还本付息";
			} else if (WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_PRINCIPAL_2.equals(wloanTermUserPlan.getPrincipal())) {
				strings[1] = "付息";
			}
			strings[2] = wloanTermUserPlan.getInterest().toString();
			dataList.add(strings);
		}

		List<String[]> investList = new ArrayList<String[]>();
		String[] investArr = null;
		WloanTermInvest userInvest = null;
		for (int i = 0; i < list.size(); i++) {
			userInvest = list.get(i);

			investArr = new String[5];
			investArr[0] = Integer.toString(i + 1);
			if (userInvest.getUserId() == userInfo.getId() || userInvest.getUserId().equals(userInfo.getId())) {
				investArr[1] = userInvest.getUserInfo().getRealName();
				investArr[2] = userInvest.getUserInfo().getCertificateNo();
			} else {
				investArr[1] = Util.hideString(userInvest.getUserInfo().getRealName(), 1, 2);
				investArr[2] = Util.hideString(userInvest.getUserInfo().getCertificateNo(), 6, 8);
			}
			investArr[3] = Double.toString(userInvest.getAmount());
			investArr[4] = Double.toString(userInvest.getInterest());

			investList.add(investArr);
		}

		try {
			contractPdfPath = PdfUtils.createPdfByTemplate(templateName, map, title, rowTitle, dataList, null, investList);
			logger.info("fn:createContractPdfPath,{生成四方合同成功}");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("fn:createContractPdfPath,{异常：" + e.getMessage() + "}");
		}

		return contractPdfPath;
	}
}
