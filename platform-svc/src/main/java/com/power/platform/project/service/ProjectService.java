	package com.power.platform.project.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.power.platform.common.config.Global;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.utils.CommonStringUtils;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.utils.Util;
import com.power.platform.credit.dao.apply.CreditUserApplyDao;
import com.power.platform.credit.dao.supplierToMiddlemen.CreditSupplierToMiddlemenDao;
import com.power.platform.credit.dao.userinfo.CreditUserInfoDao;
import com.power.platform.credit.dao.ztmgLoanBasicInfo.ZtmgLoanBasicInfoDao;
import com.power.platform.credit.dao.ztmgLoanBasicInfo.ZtmgLoanShareholdersInfoDao;
import com.power.platform.credit.entity.annexfile.CreditAnnexFile;
import com.power.platform.credit.entity.apply.CreditUserApply;
import com.power.platform.credit.entity.supplierToMiddlemen.CreditSupplierToMiddlemen;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.entity.ztmgLoanBasicInfo.ZtmgLoanBasicInfo;
import com.power.platform.credit.entity.ztmgLoanBasicInfo.ZtmgLoanShareholdersInfo;
import com.power.platform.credit.entity.ztmgLoanBasicInfo.pojo.CreditAnnexFilePojo;
import com.power.platform.credit.service.annexfile.CreditAnnexFileService;
import com.power.platform.credit.service.basicinfo.CreditBasicInfoService;
import com.power.platform.credit.service.userinfo.CreditUserInfoService;
import com.power.platform.pay.utils.IndustryUtils;
import com.power.platform.plan.entity.WloanTermProjectPlan;
import com.power.platform.plan.service.WloanTermProjectPlanService;
import com.power.platform.plan.service.WloanTermUserPlanService;
import com.power.platform.regular.dao.WloanTermInvestDao;
import com.power.platform.regular.dao.WloanTermProjectDao;
import com.power.platform.regular.entity.WloanSubject;
import com.power.platform.regular.entity.WloanTermInvest;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.regular.service.WGuaranteeCompanyService;
import com.power.platform.regular.service.WloanSubjectService;
import com.power.platform.regular.service.WloanTermInvestService;
import com.power.platform.regular.service.WloanTermProjectService;
import com.power.platform.sys.entity.AnnexFile;
import com.power.platform.sys.service.AnnexFileService;

@Path("/project")
@Service("projectService")
@Produces(MediaType.APPLICATION_JSON)
public class ProjectService {

	Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	private WloanTermProjectService wloanTermProjectService;
	@Autowired
	private WloanTermInvestService wloanTermInvestService;
	@Autowired
	private WloanTermProjectPlanService projectRepayPlan;
	@Autowired
	private WGuaranteeCompanyService wGuaranteeCompanyService;
	@Autowired
	private AnnexFileService annexFileService;
	@Autowired
	private WloanTermUserPlanService wloanTermUserPlanService;
	@Autowired
	private WloanTermProjectPlanService wloanTermProjectPlanService;
	@Resource
	private WloanTermInvestDao wloanTermInvestDao;
	@Resource
	private WloanTermProjectDao wloanTermProjectDao;
	@Autowired
	private CreditBasicInfoService creditBasicInfoService;
	@Resource
	private CreditUserInfoDao creditUserInfoDao;
	@Autowired
	private WloanSubjectService wloanSubjectService;
	@Autowired
	private CreditAnnexFileService creditAnnexFileService;
	@Resource
	private CreditUserApplyDao creditUserApplyDao;
	@Resource
	private CreditUserInfoService creditUserInfoService;
	@Resource
	private ZtmgLoanBasicInfoDao ztmgLoanBasicInfoDao;
	@Resource
	private ZtmgLoanShareholdersInfoDao ztmgLoanShareholdersInfoDao;
	@Resource
	private CreditSupplierToMiddlemenDao creditSupplierToMiddlemenDao;

	/**
	 * 首页项目信息---旧
	 * 
	 * @param from
	 * @return
	 */
	@POST
	@Path("/index")
	public Map<String, Object> index(@FormParam("from") String from) {

		Map<String, Object> result = new HashMap<String, Object>();
		WloanTermProject project = new WloanTermProject();
		List<String> stateItem = new ArrayList<String>();
		stateItem.add(WloanTermProjectService.ONLINE);
		stateItem.add(WloanTermProjectService.FULL);
		stateItem.add(WloanTermProjectService.REPAYMENT);
		stateItem.add(WloanTermProjectService.FINISH);
		project.setStateItem(stateItem);
		project.setProjectType(WloanTermProjectService.PROJECT_TYPE_2);
		Page<WloanTermProject> page = new Page<WloanTermProject>();
		page.setPageNo(1);
		page.setPageSize(1);
		page.setOrderBy("state, online_date DESC");
		List<WloanTermProject> list = null;
		List<String> dataList = new ArrayList<String>();
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			list = wloanTermProjectService.findPage(page, project).getList();
			if (list != null && list.size() > 0) {
				project = list.get(0);
				map.put("projectid", project.getId());
				String[] labels = project.getLabel().split(",");
				if (labels.length > 0) {
					for (int i = 0; i < labels.length; i++) {
						dataList.add(labels[i]);
					}
				}
				map.put("label", dataList);
				map.put("name", project.getName());
				map.put("span", project.getSpan());
				map.put("rate", project.getAnnualRate());
				map.put("amount", project.getAmount());
				map.put("currentamount", project.getCurrentAmount());
				map.put("balanceamount", project.getAmount() - project.getCurrentAmount());
				map.put("percentage", project.getCurrentAmount() / project.getAmount() * 100 + "%");
				map.put("prostate", project.getState());
				map.put("loandate", project.getLoanDate() == null ? null : DateUtils.formatDate(project.getLoanDate(), "yyyy/MM/dd HH:mm:ss"));
				map.put("sn", project.getSn());
			}
			result.put("state", "0");
			result.put("message", "接口调用成功");
			result.put("data", map);
		} catch (Exception e) {
			result.put("state", "1");
			result.put("message", "系统异常");
			result.put("data", null);
		}
		return result;
	}

	/**
	 * 首页项目信息---新
	 * 
	 * @param from
	 * @param projectType
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@POST
	@Path("/indexH5")
	public Map<String, Object> indexH5(@FormParam("from") String from, @FormParam("projecttype") String projectType, @FormParam("pageNo") Integer pageNo, @FormParam("pageSize") Integer pageSize) {

		Map<String, Object> result = new HashMap<String, Object>();
		WloanTermProject project = new WloanTermProject();
		List<String> stateItem = new ArrayList<String>();
		stateItem.add(WloanTermProjectService.ONLINE);
		// stateItem.add(WloanTermProjectService.FULL);
		// stateItem.add(WloanTermProjectService.REPAYMENT);
		// stateItem.add(WloanTermProjectService.FINISH);
		project.setStateItem(stateItem);
		if (!StringUtils.isBlank(projectType)) {
			project.setProjectType(projectType);
		}
		Page<WloanTermProject> page = new Page<WloanTermProject>();
		page.setPageNo(1);
		page.setPageSize(20);
		page.setOrderBy("state, online_date DESC");
		List<WloanTermProject> list = null;
		Map<String, Object> data = new HashMap<String, Object>();
		List<Map<String, Object>> projectList = new ArrayList<Map<String, Object>>();
		try {
			list = wloanTermProjectService.findPage(page, project).getList();
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					project = list.get(i);
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("projectid", project.getId());
					map.put("label", project.getLabel());
					map.put("name", project.getName());
					map.put("span", project.getSpan());
					map.put("rate", project.getAnnualRate());
					map.put("amount", project.getAmount());
					map.put("currentamount", project.getCurrentAmount());
					map.put("balanceamount", project.getAmount() - project.getCurrentAmount());
					map.put("percentage", project.getCurrentAmount() / project.getAmount() * 100 + "%");
					map.put("prostate", project.getState());
					map.put("loandate", project.getLoanDate() == null ? null : DateUtils.formatDate(project.getLoanDate(), "yyyy/MM/dd HH:mm:ss"));
					map.put("projectType", project.getProjectType());
					map.put("projectProductType", project.getProjectProductType());
					map.put("sn", project.getSn());
					// 获取核心企业简介
					String middlemenId = project.getReplaceRepayId();
					if (middlemenId != null && !middlemenId.equals("")) {
						CreditUserInfo creditUserInfo = creditUserInfoService.get(middlemenId);
						if (creditUserInfo != null) {
							map.put("creditName", creditUserInfo.getEnterpriseFullName());
						} else {
							map.put("creditName", null);
						}
						CreditAnnexFile annexFile = new CreditAnnexFile();
						annexFile.setOtherId(middlemenId);
						annexFile.setType("30");
						List<CreditAnnexFile> annexFileList = creditAnnexFileService.findList(annexFile);
						if (annexFileList != null && annexFileList.size() > 0) {
							map.put("creditUrl", annexFileList.get(0).getRemark() + "?middlemenId=" + creditUserInfo.getId());
						} else {
							map.put("creditUrl", null);
						}
					} else {
						map.put("creditName", null);
						map.put("creditUrl", null);
					}
					projectList.add(map);
				}
			}
			data.put("projectList", projectList);
			result.put("state", "0");
			result.put("message", "接口调用成功");
			result.put("data", data);
		} catch (Exception e) {
			result.put("state", "1");
			result.put("message", "系统异常");
			result.put("data", null);
		}
		return result;
	}

	/**
	 * 
	 * 方法: getProjectList <br>
	 * 描述: 项目列表. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年12月29日 上午11:14:06
	 * 
	 * @param from
	 *            请求来源
	 * @param pageNo
	 *            页码
	 * @param pageSize
	 *            页面大小
	 * @param span
	 *            期限
	 * @param ratebegin
	 *            起投金额
	 * @param rateend
	 *            最大投资金额
	 * @param state
	 *            项目流转状态
	 * @param projectsn
	 *            项目名称
	 * @param projectType
	 *            标的类型
	 * @param projectProductType
	 *            标的产品类型
	 * @param orderBy
	 *            排序规则，0：综合排序，1：期限排序，2：利率排序
	 * @return
	 */
	@POST
	@Path("/getProjectList")
	public Map<String, Object> getProjectList(@FormParam("from") Integer from, @FormParam("pageNo") Integer pageNo, @FormParam("pageSize") Integer pageSize, @FormParam("span") Integer span, @FormParam("ratebegin") Double ratebegin, @FormParam("rateend") Double rateend, @FormParam("state") String state, @FormParam("projectsn") String projectsn, @FormParam("projectType") String projectType, @FormParam("projectProductType") String projectProductType, @FormParam("orderby") String orderBy) {

		Map<String, Object> result = new HashMap<String, Object>();
		List<String> stateLists = new ArrayList<String>();

		WloanTermProject wloanTermProject = new WloanTermProject();
		stateLists.add(WloanTermProjectService.PUBLISH);
		wloanTermProject.setStateItem(stateLists);

		// 标的类型.
		if (!StringUtils.isBlank(projectType)) {
			wloanTermProject.setProjectType(projectType); // 1：其它，2新手标的，2：推荐标的.
		}

		// 标的产品类型.
		if (!StringUtils.isBlank(projectProductType)) {
			wloanTermProject.setProjectProductType(projectProductType); // 1：安心投类，2：供应链类.
		}

		// 最新项目公告(最新要发布项目)
		Page<WloanTermProject> page = new Page<WloanTermProject>();
		if (!StringUtils.isBlank(orderBy)) {
			if (orderBy.equals("0")) {
				// 综合排序
				page.setOrderBy("a.state asc, a.online_date desc");
			} else if (orderBy.equals("1")) {
				// 期限排序
				page.setOrderBy("a.span,a.state,  a.online_date");
			} else if (orderBy.equals("2")) {
				// 利率排序
				page.setOrderBy("a.annual_rate,a.state,  a.online_date");
			}
		} else {
			page.setOrderBy("a.state asc, a.online_date desc");
		}
		page.setPageNo(pageNo);
		page.setPageSize(pageSize);

		logger.info("span " + span);

		List<WloanTermProject> list = new ArrayList<WloanTermProject>();
		if (span != null && span != 0) {
			if (span == 30) {
				if (from == 1) {
					wloanTermProject.setSpan(Integer.valueOf(span));
				} else {
					wloanTermProject.setSpans(Integer.valueOf(span * 3));
				}
			} else {
				wloanTermProject.setSpan(Integer.valueOf(span));
			}
		}

		// if (page.getPageNo() == 1) {
		// Page<WloanTermProject> pages = wloanTermProjectService.findPage(page, wloanTermProject);
		// if (pages != null && pages.getList().size() > 0) {
		// list.add(pages.getList().get(0));
		// page.setPageSize(pageSize - 1 == 0 ? 1 : pageSize - 1);
		// }
		// }

		if (!StringUtils.isBlank(state)) {
			stateLists.clear();
			stateLists.add(state);
			wloanTermProject.setStateItem(stateLists);
		} else {
			// 设置多个状态（上线、满标、还款中、已还完）
			stateLists.clear();
			stateLists.add(WloanTermProjectService.PUBLISH);
			stateLists.add(WloanTermProjectService.ONLINE);
			stateLists.add(WloanTermProjectService.FULL);
			stateLists.add(WloanTermProjectService.REPAYMENT);
			stateLists.add(WloanTermProjectService.FINISH);
			wloanTermProject.setStateItem(stateLists);
		}
		if (ratebegin != null && ratebegin > 0) {
			wloanTermProject.setMinAnnualRate(Double.valueOf(ratebegin));
			wloanTermProject.setMaxAnnualRate(Double.valueOf(rateend));
		}
		if (!StringUtils.isBlank(projectsn)) {
			wloanTermProject.setName(projectsn);
		}

		/**
		 * 其他项目（可投资、满标、还款中、已完成项目）
		 */
		// page.setOrderBy("a.state, a.online_date DESC");
		Page<WloanTermProject> pagepro = wloanTermProjectService.findPage(page, wloanTermProject);
		List<WloanTermProject> lists = pagepro.getList();

		for (int i = 0; i < lists.size(); i++) {
			// 即将上线的项目，判断上线时间，没到当天00:00，不展示.
			if (WloanTermProjectService.PUBLISH.equals(lists.get(i).getState())) {
				if (DateUtils.compareDateByDay(DateUtils.formatDate(new Date(), "yyyy-MM-dd"), DateUtils.formatDate(lists.get(i).getOnlineDate(), "yyyy-MM-dd")) == -1) {
					// 不满足展示条件.
				} else {
					list.add(lists.get(i));
				}
			} else {
				list.add(lists.get(i));
			}
		}

		WloanTermProject project = new WloanTermProject();
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			project = list.get(i);
			Double interestRateIncrease = project.getInterestRateIncrease();
			if (null == interestRateIncrease) {
				map.put("interestRateIncrease", "0.00"); // 加息利率.
			} else {
				map.put("interestRateIncrease", NumberUtils.scaleDoubleStr(interestRateIncrease)); // 加息利率.
			}
			map.put("projectid", project.getId());
			map.put("sn", project.getSn());
			map.put("minAmount", project.getMinAmount());
			map.put("maxAmount", project.getMaxAmount());
			map.put("stepAmount", project.getStepAmount());
			map.put("name", project.getName());
			map.put("rate", project.getAnnualRate());
			map.put("span", project.getSpan());
			map.put("amount", NumberUtils.scaleDouble(project.getAmount()));
			map.put("balanceamount", project.getAmount() - project.getCurrentAmount());
			map.put("currentamount", NumberUtils.scaleDouble(project.getCurrentAmount()));
			map.put("countdowndate", project.getOnlineDate() == null ? null : DateUtils.formatDate(project.getOnlineDate(), "yyyy/MM/dd HH:mm:ss")); // 投资倒计时
			map.put("loandate", project.getLoanDate() == null ? null : DateUtils.formatDate(project.getLoanDate(), "yyyy/MM/dd HH:mm:ss"));

			map.put("percentage", project.getCurrentAmount() == 0d ? "0.00%" : (NumberUtils.scaleDouble(project.getCurrentAmount() / project.getAmount() * 100) + "%"));

			// 标的类型.
			if (project.getProjectType().equals(WloanTermProjectService.PROJECT_TYPE_1)) {
				map.put("projectType", "其它标的");
			} else if (project.getProjectType().equals(WloanTermProjectService.PROJECT_TYPE_2)) {
				map.put("projectType", "新手标的");
			} else if (project.getProjectType().equals(WloanTermProjectService.PROJECT_TYPE_3)) {
				map.put("projectType", "推荐标的");
			}

			// 标的产品类型.
			if (project.getProjectProductType().equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) {
				map.put("projectProductType", WloanTermProjectService.PROJECT_PRODUCT_TYPE_1);
			} else if (project.getProjectProductType().equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2)) {
				map.put("projectProductType", WloanTermProjectService.PROJECT_PRODUCT_TYPE_2);
			}

			// 获取核心企业简介
			String middlemenId = project.getReplaceRepayId();
			if (middlemenId != null) {
				CreditUserInfo creditUserInfo = creditUserInfoService.get(middlemenId);
				if (creditUserInfo != null) {
					map.put("creditName", creditUserInfo.getEnterpriseFullName());
				} else {
					map.put("creditName", null);
				}
				CreditAnnexFile annexFile = new CreditAnnexFile();
				annexFile.setOtherId(middlemenId);
				annexFile.setType("30");
				List<CreditAnnexFile> annexFileList = creditAnnexFileService.findList(annexFile);
				if (annexFileList != null && annexFileList.size() > 0) {
					map.put("creditUrl", annexFileList.get(0).getRemark() + "?middlemenId=" + middlemenId);
				} else {
					map.put("creditUrl", null);
				}
			} else {
				map.put("creditName", null);
				map.put("creditUrl", null);
			}
			map.put("prostate", project.getState());
			map.put("isNewType", project.getProjectType()); // 0-否 1-是
			map.put("label", project.getLabel());
			data.add(map);
		}

		result.put("state", "0");
		result.put("message", "接口调用成功");
		result.put("totalCount", pagepro.getCount());
		result.put("pageNo", pagepro.getPageNo());
		result.put("pageSize", pagepro.getPageSize());
		result.put("last", pagepro.getLast());
		result.put("pramaspan", span);
		result.put("pageCount", pagepro.getLast());
		result.put("data", data);

		return result;
	}

	@POST
	@Path("/getProjectListWap")
	public Map<String, Object> getProjectListWap(@FormParam("from") Integer from, @FormParam("pageNo") Integer pageNo, @FormParam("pageSize") Integer pageSize, @FormParam("projectProductType") String projectProductType) {

		Map<String, Object> result = new HashMap<String, Object>();// 结果
		Map<String, Object> data = new HashMap<String, Object>();// 结果

		// 项目状态列表
		List<String> stateLists = new ArrayList<String>();

		WloanTermProject wloanTermProject = new WloanTermProject();

		// // 标的类型.
		// if (!StringUtils.isBlank(projectType)) {
		// wloanTermProject.setProjectType(projectType); // 1：其它，2新手标的，3：推荐标的.
		// }

		// 标的产品类型.
		if (!StringUtils.isBlank(projectProductType)) {
			wloanTermProject.setProjectProductType(projectProductType); // 1：安心投类，2：供应链类.
		}

		// 最新项目公告(最新要发布项目)
		Page<WloanTermProject> page = new Page<WloanTermProject>();
		// 排序
		page.setOrderBy("a.state,a.online_date");

		Page<WloanTermProject> pagepro = new Page<WloanTermProject>();

		List<WloanTermProject> listZC = new ArrayList<WloanTermProject>();// 正常结果集
		List<WloanTermProject> listTJ = new ArrayList<WloanTermProject>();// 推荐结果集
		// List<WloanTermProject> listYX = new ArrayList<WloanTermProject>();//优选结果集

		// 暂时保留注释
		// if (page.getPageNo() == 1) {
		// Page<WloanTermProject> pages = wloanTermProjectService.findPage(page, wloanTermProject);
		// if (pages != null && pages.getList().size() > 0) {
		// list.add(pages.getList().get(0));
		// page.setPageSize(pageSize - 1 == 0 ? 1 : pageSize - 1);
		// }
		// }

		// 设置多个状态（上线、满标、还款中、已还完）
		// 推荐标
		wloanTermProject.setProjectType("3");
		stateLists.clear();
		stateLists.add(WloanTermProjectService.PUBLISH);
		stateLists.add(WloanTermProjectService.ONLINE);
		wloanTermProject.setStateItem(stateLists);
		pagepro = wloanTermProjectService.findPage(page, wloanTermProject);
		listTJ = pagepro.getList();
		for (WloanTermProject tjProject : listTJ) {
			Double interestRateIncrease = tjProject.getInterestRateIncrease();
			if (null == interestRateIncrease) {
				tjProject.setInterestRateIncrease(0D); // 加息利率.
			}
		}

		// 优选标
		// wloanTermProject.setProjectType(null);
		// stateLists.clear();
		// stateLists.add(WloanTermProjectService.ONLINE);
		// stateLists.add(WloanTermProjectService.FULL);
		// stateLists.add(WloanTermProjectService.REPAYMENT);
		// stateLists.add(WloanTermProjectService.FINISH);
		// wloanTermProject.setStateItem(stateLists);
		// pagepro = wloanTermProjectService.findPage(page, wloanTermProject);
		// listYX = pagepro.getList();

		// 正常标
		wloanTermProject.setProjectType(null);
		stateLists.clear();
		stateLists.add(WloanTermProjectService.PUBLISH);
		stateLists.add(WloanTermProjectService.ONLINE);
		stateLists.add(WloanTermProjectService.FULL);
		stateLists.add(WloanTermProjectService.REPAYMENT);
		stateLists.add(WloanTermProjectService.FINISH);
		wloanTermProject.setStateItem(stateLists);
		// 分页
		page.setPageNo(pageNo);
		page.setPageSize(pageSize);
		pagepro = wloanTermProjectService.findPage(page, wloanTermProject);
		listZC = pagepro.getList();
		WloanTermProject project = null;
		for (int i = 0; i < listZC.size(); i++) {
			project = listZC.get(i);
			Double interestRateIncrease = project.getInterestRateIncrease();
			if (null == interestRateIncrease) {
				project.setInterestRateIncrease(0D); // 加息利率.
			}
			if ("3".equals(project.getProjectType())) {
				if ("3".equals(project.getState()) || "4".equals(project.getState())) {
					listZC.remove(project);
					i--;
				}
			}
		}

		data.put("listTJ", listTJ);
		data.put("listZC", listZC);
		// data.put("listYX", listYX);

		result.put("state", "0");
		result.put("message", "接口调用成功");
		result.put("totalCount", pagepro.getCount());
		result.put("pageNo", pagepro.getPageNo());
		result.put("pageSize", pagepro.getPageSize());
		result.put("last", pagepro.getLast());
		result.put("pageCount", pagepro.getLast());
		result.put("data", data);

		return result;
	}

	@POST
	@Path("/getNewProjectListWap")
	public Map<String, Object> getNewProjectListWap(@FormParam("from") Integer from, @FormParam("pageNo") Integer pageNo, @FormParam("pageSize") Integer pageSize, @FormParam("projectProductType") String projectProductType) {

		Map<String, Object> result = new HashMap<String, Object>();// 结果
		Map<String, Object> data = new HashMap<String, Object>();// 结果

		// 项目状态列表
		List<String> stateLists = new ArrayList<String>();

		WloanTermProject wloanTermProject = new WloanTermProject();

		// // 标的类型.
		// if (!StringUtils.isBlank(projectType)) {
		// wloanTermProject.setProjectType(projectType); // 1：其它，2新手标的，3：推荐标的.
		// }

		// 标的产品类型.
		if (!StringUtils.isBlank(projectProductType)) {
			wloanTermProject.setProjectProductType(projectProductType); // 1：安心投类，2：供应链类.
		}
		// wloanTermProject.setProjectProductType(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2);

		// 最新项目公告(最新要发布项目)
		Page<WloanTermProject> page = new Page<WloanTermProject>();
		// 排序
		page.setPageNo(1);
		page.setPageSize(2);
		page.setOrderBy("a.state,a.online_date desc");

		Page<WloanTermProject> pagepro = new Page<WloanTermProject>();

		// List<WloanTermProject> listZC = new ArrayList<WloanTermProject>();//正常结果集
		// List<WloanTermProject> listTJ = new ArrayList<WloanTermProject>();//推荐结果集
		List<WloanTermProject> listYX = new ArrayList<WloanTermProject>();// 优选结果集

		// 暂时保留注释
		// if (page.getPageNo() == 1) {
		// Page<WloanTermProject> pages = wloanTermProjectService.findPage(page, wloanTermProject);
		// if (pages != null && pages.getList().size() > 0) {
		// list.add(pages.getList().get(0));
		// page.setPageSize(pageSize - 1 == 0 ? 1 : pageSize - 1);
		// }
		// }

		// 设置多个状态（上线、满标、还款中、已还完）
		// 推荐标
		// wloanTermProject.setProjectType("3");
		// stateLists.clear();
		// stateLists.add(WloanTermProjectService.PUBLISH);
		// stateLists.add(WloanTermProjectService.ONLINE);
		// wloanTermProject.setStateItem(stateLists);
		// pagepro = wloanTermProjectService.findPage(page, wloanTermProject);
		// listTJ = pagepro.getList();

		// 优选标
		wloanTermProject.setProjectType(null);
		stateLists.clear();
		stateLists.add(WloanTermProjectService.ONLINE);
		stateLists.add(WloanTermProjectService.FULL);
		stateLists.add(WloanTermProjectService.REPAYMENT);
		stateLists.add(WloanTermProjectService.FINISH);
		wloanTermProject.setStateItem(stateLists);
		// 不包含推荐标的，包含新手标的和其它标的.
		List<String> projectTypeList = new ArrayList<String>();
		projectTypeList.add(WloanTermProjectService.PROJECT_TYPE_1);
		projectTypeList.add(WloanTermProjectService.PROJECT_TYPE_2);
		wloanTermProject.setProjectTypeItem(projectTypeList);
		pagepro = wloanTermProjectService.findPage(page, wloanTermProject);
		listYX = pagepro.getList();
		for (WloanTermProject yxProject : listYX) {
			Double interestRateIncrease = yxProject.getInterestRateIncrease();
			if (null == interestRateIncrease) {
				yxProject.setInterestRateIncrease(0D); // 加息利率.
			}
		}

		// //正常标
		// wloanTermProject.setProjectType(null);
		// stateLists.clear();
		// stateLists.add(WloanTermProjectService.PUBLISH);
		// stateLists.add(WloanTermProjectService.ONLINE);
		// stateLists.add(WloanTermProjectService.FULL);
		// stateLists.add(WloanTermProjectService.REPAYMENT);
		// stateLists.add(WloanTermProjectService.FINISH);
		// wloanTermProject.setStateItem(stateLists);
		// 分页
		// page.setPageNo(pageNo);
		// page.setPageSize(pageSize);
		// pagepro = wloanTermProjectService.findPage(page, wloanTermProject);
		// listZC = pagepro.getList();
		// WloanTermProject project = null;
		// for(int i = 0;i<listZC.size();i++){
		// project = listZC.get(i);
		// if("3".equals(project.getProjectType())){
		// if("3".equals(project.getState()) || "4".equals(project.getState())){
		// listZC.remove(project);
		// i--;
		// }
		// }
		// }

		// data.put("listTJ", listTJ);
		// data.put("listZC", listZC);
		data.put("listYX", listYX);

		result.put("state", "0");
		result.put("message", "接口调用成功");
		result.put("totalCount", pagepro.getCount());
		result.put("pageNo", pagepro.getPageNo());
		result.put("pageSize", pagepro.getPageSize());
		result.put("last", pagepro.getLast());
		result.put("pageCount", pagepro.getLast());
		result.put("data", data);

		return result;
	}

	/**
	 * 获取单个项目信息
	 * 
	 * @param from
	 * @param projectid
	 * @return
	 */
	@POST
	@Path("/getProjectInfo")
	public Map<String, Object> getProjectInfo(@FormParam("from") Integer from, @FormParam("projectid") String projectid) {

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();

		WloanTermProject project = null;
		if (!StringUtils.isBlank(projectid)) {
			try {
				project = wloanTermProjectService.get(projectid);
				/**
				 * 借款人信息（姓名 年龄 婚姻状况 学历 性别）.
				 */
				data.put("borrowerName", null);
				data.put("borrowerAge", null);
				data.put("borrowerMaritalStatus", null);
				data.put("borrowerEducationStatus", null);
				data.put("borrowerIdCard", null);
				data.put("borrowerGender", null);

				data.put("id", project.getId());
				data.put("name", project.getName()); // 项目名称
				data.put("rate", project.getAnnualRate()); // 预期年化收益
				data.put("span", project.getSpan()); // 项目期限
				data.put("amount", project.getAmount()); // 融资金额
				data.put("minamount", project.getMinAmount()); // 起投金额
				data.put("maxamount", project.getMaxAmount()); // 最大投资金额
				data.put("stepamount", project.getStepAmount()); // 递增金额
				data.put("currentamount", project.getCurrentAmount()); // 当前投资金额
				data.put("balanceamount", NumberUtils.scaleDoubleStr(project.getAmount() - project.getCurrentAmount())); // 可投余额
				data.put("percentage", project.getCurrentAmount() == 0d ? "0.00%" : (NumberUtils.scaleDouble(project.getCurrentAmount() / project.getAmount() * 100) + "%")); // 投资百分比
				data.put("isNewType", project.getProjectType()); // 标的类型，1：其它，2：新手标的，3：推荐标的.
				data.put("projectProductType", project.getProjectProductType()); // 标的产品类型，1：安心投类，2：供应链类.

				data.put("countdowndate", project.getOnlineDate() == null ? null : DateUtils.formatDate(project.getOnlineDate(), "yyyy/MM/dd HH:mm:ss")); // 投资倒计时
				data.put("loandate", DateUtils.formatDate(project.getLoanDate(), "yyyy/MM/dd HH:mm:ss")); // 放款日期
				data.put("endDate", DateUtils.formatDate(project.getEndDate(), "yyyy-MM-dd")); // 项目结束日期
				data.put("proState", project.getState()); // 项目流转状态.
				data.put("isCanUseCoupon", project.getIsCanUseCoupon()); // 是否可用抵用券
				data.put("isCanUsePlusCoupon", project.getIsCanUsePlusCoupon()); // 是否可用加息券
				data.put("repaytype", project.getRepayType().equals("1") ? "一次性还本付息" : "分期付息到期还本"); // 还款方式.
				data.put("sourceOfRepayment", project.getSourceOfRepayment()); // 还款来源.
				data.put("sn", project.getSn()); // 编号.

				// 承诺函.
				List<CreditAnnexFile> commitments = new ArrayList<CreditAnnexFile>();
				// 营业执照.
				List<CreditAnnexFile> businessLicenses = new ArrayList<CreditAnnexFile>();
				// 银行许可证.
				List<CreditAnnexFile> bankPermitCerts = new ArrayList<CreditAnnexFile>();

				WloanSubject wloanSubject = project.getWloanSubject();
				if (wloanSubject != null) { // 融资主体.
					// 企业介绍（主体、个人）.
					data.put("briefinfo", wloanSubject.getBriefInfo());
					// 企业名称（借款方名称）.
					data.put("borrowerCompanyName", wloanSubject.getCompanyName());
					// 代偿方用户ID.
					String replaceRepayId = project.getReplaceRepayId();
					WloanSubject entity = new WloanSubject();
					entity.setLoanApplyId(replaceRepayId);
					List<WloanSubject> replaceRepays = wloanSubjectService.findList(entity); // 查询代偿方融资主体.
					if (replaceRepays != null && replaceRepays.size() > 0) {
						WloanSubject replaceRepay = replaceRepays.get(0);
						if (null != replaceRepay) {
							// 企业名称（代偿方名称）.
							data.put("replaceRepayCompanyName", replaceRepay.getCompanyName());
						} else {
							data.put("replaceRepayCompanyName", null);
						}
					} else {
						data.put("replaceRepayCompanyName", null);
					}

					// 借款申请ID.
					String creditUserApplyId = project.getCreditUserApplyId();
					CreditUserApply creditUserApply = creditUserApplyDao.get(creditUserApplyId);
					if (null == creditUserApply) {
						String projectDataId = project.getProjectDataId(); // 借款资料ID.
						// 借款资料ID.
						data.put("creditInfoId", projectDataId);
						if (projectDataId != null) { // 非空判断.
							if (projectDataId.equals("")) {
								data.put("commitments", commitments);
							} else {
								// 借款资料承诺函.
								CreditAnnexFile commitment = new CreditAnnexFile();
								commitment.setOtherId(projectDataId);
								commitment.setType("7");
								commitments = creditAnnexFileService.findCreditAnnexFileList(commitment);
								for (CreditAnnexFile commitmentEntity : commitments) {
									commitmentEntity.setUrl(Global.getConfig("credit_file_path") + commitmentEntity.getUrl());
								}
								data.put("commitments", commitments);
							} 
						} else {
							data.put("commitments", commitments);
						}
					} else {
						// 借款资料ID.
						String projectDataId = creditUserApply.getProjectDataId();
						// 借款资料ID.
						data.put("creditInfoId", projectDataId);
						if (projectDataId != null) { // 非空判断.
							if (projectDataId.equals("")) {
								data.put("commitments", commitments);
							} else {
								// 借款资料承诺函.
								CreditAnnexFile commitment = new CreditAnnexFile();
								commitment.setOtherId(projectDataId);
								commitment.setType("7");
								commitments = creditAnnexFileService.findCreditAnnexFileList(commitment);
								for (CreditAnnexFile commitmentEntity : commitments) {
									commitmentEntity.setUrl(Global.getConfig("credit_file_path") + commitmentEntity.getUrl());
								}
								data.put("commitments", commitments);
							}
						} else {
							data.put("commitments", commitments);
						}
					}

					String loanApplyId = wloanSubject.getLoanApplyId(); // 借款方用户ID.
					if (loanApplyId != null) { // 非空判断.
						if (loanApplyId.equals("")) {
							data.put("businessLicenses", businessLicenses);
							data.put("bankPermitCerts", bankPermitCerts);
						} else {
							// 借款方开户资料，营业执照.
							CreditAnnexFile businessLicense = new CreditAnnexFile();
							businessLicense.setOtherId(loanApplyId);
							businessLicense.setType(CreditAnnexFileService.CREDIT_PROJECT_DATA_TYPE_8);
							businessLicenses = creditAnnexFileService.findCreditAnnexFileList(businessLicense);
							for (CreditAnnexFile businessLicenseEntity : businessLicenses) {
								businessLicenseEntity.setUrl(Global.getConfig("credit_file_path") + businessLicenseEntity.getUrl());
							}
							data.put("businessLicenses", businessLicenses);
							// 借款方开户资料，营业执照.
							CreditAnnexFile bankPermitCert = new CreditAnnexFile();
							bankPermitCert.setOtherId(loanApplyId);
							bankPermitCert.setType(CreditAnnexFileService.CREDIT_PROJECT_DATA_TYPE_9);
							bankPermitCerts = creditAnnexFileService.findCreditAnnexFileList(bankPermitCert);
							for (CreditAnnexFile bankPermitCertEntity : bankPermitCerts) {
								bankPermitCertEntity.setUrl(Global.getConfig("credit_file_path") + bankPermitCertEntity.getUrl());
							}
							data.put("bankPermitCerts", bankPermitCerts);
						}
					} else {
						data.put("businessLicenses", businessLicenses);
						data.put("bankPermitCerts", bankPermitCerts);
					}
				} else {
					data.put("briefinfo", null);
					data.put("borrowerCompanyName", null);
					data.put("replaceRepayCompanyName", null);
					data.put("commitments", commitments);
					data.put("businessLicenses", businessLicenses);
					data.put("bankPermitCerts", bankPermitCerts);
				}
				if (project.getWgCompany() != null) { // 担保公司.
					data.put("wgcompany", project.getWgCompany().getName()); // 担保公司名称
					data.put("industry", project.getWgCompany().getIndustry()); // 所属行业
					data.put("guaranteecase", project.getWgCompany().getGuaranteeCase()); // 担保情况
				} else {
					data.put("wgcompany", null); // 担保公司名称
					data.put("industry", null); // 所属行业
					data.put("guaranteecase", null); // 担保情况
				}
				if (project.getArea() != null) { // 地理位置.
					data.put("locus", project.getArea().getName()); // 所在地
				} else {
					data.put("locus", null); // 所在地
				}

				// 下拉加载更多(项目信息)
				data.put("projectcase", project.getProjectCase()); // 项目介绍
				data.put("purpose", project.getPurpose()); // 借款用途

				// 风控信息
				data.put("guaranteescheme", project.getGuaranteeScheme()); // 担保方案

				// 借款资质文件(doc_id 对应附件信息)
				Map<String, Object> docImgList = null;
				if (project.getDocId() != null && project.getDocId().length() > 0) {
					docImgList = getImgList(project.getDocId());
					data.put("docimgs", docImgList.get("zizhiFile"));
					// 风控文件(wgcompany 对应的附件信息)
					data.put("wgimglist", docImgList.get("projectFile"));
					// 项目照片
					data.put("proimg", docImgList.get("fengKongFile")); // 项目图片
				}
				// 投资人总数
				WloanTermInvest invest = new WloanTermInvest();
				project.setName("");
				invest.setWloanTermProject(project);
				List<WloanTermInvest> bidlist = wloanTermInvestService.findList(invest);
				if (bidlist != null && bidlist.size() > 0) {
					data.put("bidtotal", bidlist.size());
				} else {
					data.put("bidtotal", 0);
				}

				// 获取核心企业简介
				String middlemenId = project.getReplaceRepayId();
				if (middlemenId != null) {
					CreditUserInfo creditUserInfo = creditUserInfoService.get(middlemenId);
					if (creditUserInfo != null) {
						data.put("creditName", creditUserInfo.getEnterpriseFullName());
					} else {
						data.put("creditName", "");
					}
					CreditAnnexFile annexFile = new CreditAnnexFile();
					annexFile.setOtherId(middlemenId);
					annexFile.setType("30");
					List<CreditAnnexFile> annexFileList = creditAnnexFileService.findList(annexFile);
					if (annexFileList != null && annexFileList.size() > 0) {
						data.put("creditUrl", annexFileList.get(0).getRemark() + "?middlemenId=" + middlemenId);
					} else {
						data.put("creditUrl", null);
					}
				} else {
					data.put("creditName", null);
					data.put("creditUrl", null);
				}

				result.put("state", "0");
				result.put("message", "项目信息查询成功");
				result.put("data", data);
			} catch (Exception e) {
				e.printStackTrace();
				result.put("state", "1");
				result.put("message", "系统异常");
				result.put("data", null);
				return result;
			}
		} else {
			result.put("state", "2");
			result.put("message", "项目Id为空，缺少参数");
			result.put("data", null);
		}
		return result;
	}

	/**
	 * 查询项目风控信息
	 * 
	 * @param from
	 * @param projectid
	 * @return
	 */
	/*
	 * @POST
	 * 
	 * @Path("/getProjectRiskInfo")
	 * public Map<String, Object> getProjectRiskInfo(@FormParam("from")Integer
	 * from, @FormParam("projectid")String projectid) {
	 * Map<String, Object> result = new HashMap<String, Object>();
	 * WloanTermProject project = null;
	 * if (!StringUtils.isBlank(projectid)) {
	 * project = wloanTermProjectService.get(projectid);
	 * } else {
	 * result.put("state", 2);
	 * result.put("message", "项目Id为空，查询失败！");
	 * result.put("data", null);
	 * return result;
	 * }
	 * 
	 * List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	 * if (project != null) {
	 * WGuaranteeCompany wgCompany =
	 * wGuaranteeCompanyService.get(project.getWgCompany());
	 * Map<String, Object> map = new HashMap<String, Object>();
	 * map.put("name", wgCompany.getName());
	 * map.put("guarantee_case", wgCompany.getGuaranteeCase());
	 * map.put("brief_info", wgCompany.getBriefInfo());
	 * map.put("guarantee_scheme", wgCompany.getGuaranteeScheme());
	 * list.add(map);
	 * }
	 * 
	 * result.put("state", 0);
	 * result.put("message", "查询成功");
	 * result.put("data", list);
	 * return result;
	 * }
	 */

	/**
	 * 获取定期项目投资记录信息
	 * 
	 * @param from
	 * @param projectid
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@POST
	@Path("/getProjectBidList")
	public Map<String, Object> getProjectBidList(@FormParam("from") Integer from, @FormParam("projectid") String projectid, @FormParam("pageNo") Integer pageNo, @FormParam("pageSize") Integer pageSize) {

		Map<String, Object> result = new HashMap<String, Object>();
		WloanTermProject project = new WloanTermProject();
		if (StringUtils.isBlank(projectid)) {
			result.put("state", "2");
			result.put("message", "参数projectid为空");
			result.put("data", null);
			return result;
		}

		Page<WloanTermInvest> page = new Page<WloanTermInvest>();
		page.setPageNo(pageNo);
		page.setPageSize(pageSize);
		page.setOrderBy("a.create_date DESC");
		try {
			project = wloanTermProjectService.get(projectid);
			Map<String, Object> data = new HashMap<String, Object>();
			if (project != null) {
				WloanTermInvest invest = new WloanTermInvest();
				invest.setWloanTermProject(project);
				invest.setState(WloanTermInvestService.WLOAN_TERM_INVEST_STATE_1);
				Page<WloanTermInvest> investPage = wloanTermInvestService.findPage(page, invest);
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				List<WloanTermInvest> investList = investPage.getList();
				if (investList != null && investList.size() > 0) {
					for (int i = 0; i < investList.size(); i++) {
						Map<String, Object> map = new HashMap<String, Object>();
						invest = investList.get(i);
						map.put("name", Util.hideString(invest.getUserInfo().getName(), 3, 4));
						map.put("amount", invest.getAmount());
						map.put("createdate", DateUtils.formatDateTime(invest.getCreateDate()));
						list.add(map);
					}
				}
				data.put("bidlist", list);
				data.put("pageNo", investPage.getPageNo());
				data.put("pageSize", investPage.getPageSize());
				data.put("totalCount", investPage.getCount());
				data.put("last", investPage.getLast());
				data.put("pageCount", investPage.getLast());
			}

			result.put("state", "0");
			result.put("message", "查找成功");
			result.put("data", data);
		} catch (Exception e) {
			logger.info(this.getClass().getName() + " : 没有id为 " + projectid + " 的项目");
			result.put("state", "1");
			result.put("message", "系统异常");
			result.put("data", null);
			return result;
		}
		return result;
	}

	/**
	 * 获取还款计划信息
	 * 
	 * @param from
	 * @param projectid
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@POST
	@Path("/getProjectRepayPlanList")
	public Map<String, Object> getProjectRepayPlanList(@FormParam("from") Integer from, @FormParam("projectid") String projectid, @FormParam("pageNo") Integer pageNo, @FormParam("pageSize") Integer pageSize) {

		Map<String, Object> result = new HashMap<String, Object>();
		WloanTermProject project = new WloanTermProject();
		Double terminallyAmount = 0d;
		Double totalPlanAmount = 0d;
		Double loanAmount = 0d;
		if (StringUtils.isBlank(projectid)) {
			result.put("state", "2");
			result.put("message", "参数projectid为空");
			result.put("data", null);
			return result;
		}

		try {
			project = wloanTermProjectService.get(projectid);
			Page<WloanTermProjectPlan> page = new Page<WloanTermProjectPlan>();
			page.setPageNo(pageNo);
			page.setPageSize(pageSize);

			WloanTermProjectPlan plan = new WloanTermProjectPlan();
			if (project != null) {
				plan.setWloanTermProject(project);
				loanAmount = project.getAmount();
			} else {
				result.put("state", "1");
				result.put("message", "没有projectid为" + projectid + "的项目");
				result.put("data", null);
				return result;
			}

			Page<WloanTermProjectPlan> planPage = projectRepayPlan.findPage(page, plan);
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			List<WloanTermProjectPlan> planList = planPage.getList();
			if (planList != null && planList.size() > 0) {
				for (int i = 0; i < planList.size(); i++) {
					Map<String, Object> map = new HashMap<String, Object>();
					plan = planList.get(i);
					map.put("repaydate", DateUtils.formatDate(plan.getRepaymentDate(), "yyyy-MM-dd"));
					map.put("amount", plan.getInterest());
					if (plan.getPrincipal() == WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_0) {
						terminallyAmount = plan.getInterest();
					} else {
						terminallyAmount = plan.getInterest() - loanAmount;
					}
					totalPlanAmount = totalPlanAmount + plan.getInterest();
					map.put("repaysort", (planPage.getPageNo() - 1) * planPage.getPageSize() + i + 1);
					map.put("planstate", plan.getState());
					list.add(map);
				}
			}

			Map<String, Object> data = new HashMap<String, Object>();
			data.put("repayplanlist", list);
			data.put("pageNo", planPage.getPageNo());
			data.put("pageSize", planPage.getPageSize());
			data.put("totalCount", planPage.getCount());
			data.put("last", planPage.getLast());
			data.put("pageCount", planPage.getLast());
			data.put("terminallyAmount", NumberUtils.scaleDouble(terminallyAmount));// 每期应还利息
			data.put("totalInterest", NumberUtils.scaleDouble(totalPlanAmount - loanAmount));// 总利息

			result.put("state", "0");
			result.put("message", "查找成功");
			result.put("data", data);
		} catch (Exception e) {
			logger.info(this.getClass().getName() + " : 没有id为 " + projectid + " 的项目");
			result.put("state", "1");
			result.put("message", "系统异常");
			result.put("data", null);
			return result;
		}
		return result;
	}

	/**
	 * 根据other_id 获取文件图片信息
	 * 
	 * @param otherid
	 * @return 图片路径url集合
	 */
	public Map<String, Object> getImgList(String otherid) {

		AnnexFile annexFile = new AnnexFile();
		annexFile.setOtherId(otherid);
		List<AnnexFile> list = annexFileService.findAnnexFilesByWloanTermDoc(annexFile);
		Map<String, Object> urlList = new HashMap<String, Object>();
		List<String> zizhiList = new ArrayList<String>();
		List<String> fengKongList = new ArrayList<String>();
		List<String> projectList = new ArrayList<String>();
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				annexFile = list.get(i);
				String[] urlarr = annexFile.getUrl().split("\\|");
				for (int j = 1; j < urlarr.length; j++) {
					String headerStr = urlarr[j].split("/")[0];
					String url = "";
					if (headerStr.equals("2015") || headerStr.equals("2016")) {
						url = Global.getConfig("img_old_path") + urlarr[j];
					} else {
						url = Global.getConfig("img_new_path") + urlarr[j];
					}

					if (annexFile.getType().equals("1")) { // 借款资质
						zizhiList.add(url);
					} else if (annexFile.getType().equals("3")) { // 风控文件
						fengKongList.add(url);
					} else { // 项目文件
						projectList.add(url);
					}
				}
			}
		}
		urlList.put("zizhiFile", zizhiList);
		urlList.put("fengKongFile", fengKongList);
		urlList.put("projectFile", projectList);
		return urlList;
	}

	public String getFullImgPath(String img) {

		String headerStr = img.split("/")[0];
		if (headerStr.equals("2015") || headerStr.equals("2016")) {
			img = Global.getConfig("img_old_path") + img;
		} else {
			img = Global.getConfig("img_new_path") + img;
		}

		return img;
	}

	/**
	 * 根据标的信息生成个人还款计划 (导入数据用，其他慎用)
	 * 
	 * @param from
	 * @return
	 */
	@POST
	@Path("/userplan")
	public Map<String, Object> userPlan() {

		Map<String, Object> result = new HashMap<String, Object>();
		WloanTermInvest invest = new WloanTermInvest();
		List<WloanTermInvest> invests = wloanTermInvestDao.findAllList(invest);
		logger.info("invest list size" + invests.size());
		logger.info(30 / 30);
		for (int i = 0; i < invests.size(); i++) {
			wloanTermUserPlanService.initWloanTermUserPlan(invests.get(i));
		}
		result.put("state", "0");
		result.put("message", "接口调用成功");
		result.put("data", 123);
		return result;
	}

	/**
	 * 根据项目信息生成项目还款计划 (导入数据用，其他慎用)
	 * 
	 * @param from
	 * @return
	 */
	@POST
	@Path("/projectplan")
	public Map<String, Object> projectPlan() {

		Map<String, Object> result = new HashMap<String, Object>();
		WloanTermProject project = new WloanTermProject();
		List<WloanTermProject> projects = wloanTermProjectDao.findAllList(project);
		logger.info("invest list size" + projects.size());
		logger.info(30 / 30);
		for (int i = 0; i < projects.size(); i++) {
			wloanTermProjectPlanService.initWloanTermProjectPlan(projects.get(i));
		}
		result.put("state", "0");
		result.put("message", "接口调用成功");
		result.put("data", 123);
		return result;
	}

	@POST
	@Path("/getMTHData")
	public Map<String, Object> getMTHData(@FormParam("from") String from, @FormParam("middlemenId") String middlemenId) {

		Map<String, Object> result = new HashMap<String, Object>();
		WloanTermProject project = new WloanTermProject();

		int totalNum = 0;// 承付项目总数
		Double totalAmount = 0d;// 累计数

		int finishNum = 0;// 已完成项目数
		Double finishAmount = 0d;// 累计金额

		int onlineNum = 0;// 付款中项目
		Double onlineAmount = 0d;// 累计金额
		try {
			List<String> stateItem = new ArrayList<String>();
			stateItem.add(WloanTermProjectService.ONLINE);
			stateItem.add(WloanTermProjectService.FULL);
			stateItem.add(WloanTermProjectService.REPAYMENT);
			stateItem.add(WloanTermProjectService.FINISH);
			project.setStateItem(stateItem);
			project.setReplaceRepayId(middlemenId);
			project.setProjectProductType(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2);
			List<WloanTermProject> list = wloanTermProjectService.findList(project);
			if (list != null && list.size() > 0) {
				for (WloanTermProject wloanTermProject : list) {
					if (wloanTermProject.getState().equals(WloanTermProjectService.ONLINE) || wloanTermProject.getState().equals(WloanTermProjectService.FULL) || wloanTermProject.getState().equals(WloanTermProjectService.REPAYMENT)) {
						onlineNum = onlineNum + 1;
						onlineAmount = onlineAmount + wloanTermProject.getCurrentAmount();
						totalAmount = totalAmount + wloanTermProject.getCurrentAmount();
					} else if (wloanTermProject.getState().equals(WloanTermProjectService.FINISH)) {
						finishNum = finishNum + 1;
						finishAmount = finishAmount + wloanTermProject.getCurrentAmount();
						totalAmount = totalAmount + wloanTermProject.getCurrentAmount();
					}
				}
				totalNum = list.size();
				result.put("state", "0");
				result.put("message", "接口调用成功");
				result.put("totalNum", totalNum);
				result.put("totalAmount", NumberUtils.scaleDouble(totalAmount));
				result.put("onlineNum", onlineNum);
				result.put("onlineAmount", NumberUtils.scaleDouble(onlineAmount));
				result.put("finishAmount", NumberUtils.scaleDouble(finishAmount));
				result.put("finishNum", finishNum);
			} else {
				result.put("state", "0");
				result.put("message", "接口调用成功");
				result.put("totalNum", "0");
				result.put("totalAmount", "0");
				result.put("onlineNum", "0");
				result.put("onlineAmount", "0");
				result.put("finishAmount", "0");
				result.put("finishNum", "0");
			}
		} catch (Exception e) {
			result.put("state", "1");
			result.put("message", "系统异常");
			result.put("data", null);
		}
		return result;
	}

	/**
	 * 改版---分屏
	 * 
	 * @param from
	 * @param projectid
	 * @return
	 */
	@POST
	@Path("/getProjectInfoWap")
	public Map<String, Object> getProjectInfoWap(@FormParam("from") Integer from, @FormParam("projectid") String projectid) {

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();

		WloanTermProject project = null;
		if (!StringUtils.isBlank(projectid)) {
			try {
				project = wloanTermProjectService.get(projectid);

				Double interestRateIncrease = project.getInterestRateIncrease();
				if (null == interestRateIncrease) {
					data.put("interestRateIncrease", "0.00"); // 加息利率.
				} else {
					data.put("interestRateIncrease", NumberUtils.scaleDoubleStr(interestRateIncrease)); // 加息利率.
				}
				data.put("guaranteescheme", project.getGuaranteeScheme()); // 担保方案
				data.put("maxamount", project.getMaxAmount()); // 最大投资金额
				data.put("stepamount", project.getStepAmount()); // 递增金额
				data.put("currentamount", project.getCurrentAmount()); // 当前投资金额
				data.put("isNewType", project.getProjectType()); // 标的类型，1：其它，2：新手标的，3：推荐标的.
				data.put("projectProductType", project.getProjectProductType()); // 标的产品类型，1：安心投类，2：供应链类.
				data.put("countdowndate", project.getOnlineDate() == null ? null : DateUtils.formatDate(project.getOnlineDate(), "yyyy-MM-dd HH:mm:ss")); // 投资倒计时
				if (project.getEndDate() != null) {
					data.put("endDate", DateUtils.formatDate(project.getEndDate(), "yyyy-MM-dd")); // 项目结束日期
				} else {
					data.put("endDate", null); // 项目结束日期
				}
				data.put("proState", project.getState()); // 项目流转状态.
				data.put("isCanUseCoupon", project.getIsCanUseCoupon()); // 是否可用抵用券
				data.put("isCanUsePlusCoupon", project.getIsCanUsePlusCoupon()); // 是否可用加息券
				data.put("sourceOfRepayment", project.getSourceOfRepayment()); // 还款来源.
				data.put("repaymentGuaranteeMeasures", project.getRepaymentGuaranteeMeasures());// 还款措施
				data.put("sn", project.getSn()); // 编号.

				data.put("businessFinancialSituation", project.getBusinessFinancialSituation());// 经营财务情况.
				data.put("abilityToRepaySituation", project.getAbilityToRepaySituation()); // 还款能力情况.
				data.put("platformOverdueSituation", project.getPlatformOverdueSituation()); // 平台逾期情况.
				data.put("litigationSituation", project.getLitigationSituation()); // 涉诉情况.
				data.put("administrativePunishmentSituation", project.getAdministrativePunishmentSituation()); // 受行政处罚情况.
				data.put("compensatoryRepayId", project.getReplaceRepayId()); // 代偿方帐号唯一标识.

				/*
				 * =========================项目信息-公共部分=====================
				 */
				data.put("rate", project.getAnnualRate()); // 预期出借利率
				data.put("balanceamount", NumberUtils.scaleDoubleStr(project.getAmount() - project.getCurrentAmount())); // 剩余余额
				data.put("span", project.getSpan()); // 出借期限
				data.put("minamount", project.getMinAmount()); // 最低出借金额
				data.put("repaytype", project.getRepayType().equals("1") ? "一次性还本付息" : "按月付息到期还本"); // 还款方式.
				if (project.getState().equals("5") || project.getState().equals("6")) {
					data.put("loandate", DateUtils.formatDate(project.getFullDate(), "yyyy-MM-dd HH:mm:ss")); // （起息时间）放款日期
				} else {
					data.put("loandate", DateUtils.formatDate(project.getLoanDate(), "yyyy-MM-dd HH:mm:ss")); // （起息时间）放款日期
				}
				// 募集进度
				data.put("percentage", project.getCurrentAmount() == 0d ? "0.00%" : (NumberUtils.scaleDouble(project.getCurrentAmount() / project.getAmount() * 100) + "%"));

				/*
				 * 项目介绍
				 */
				data.put("projectName", project.getName()); // 项目名称
				WloanSubject wloanSubject = project.getWloanSubject();
				if (wloanSubject != null) { // 融资主体.

					/*
					 * ===============项目介绍=====================
					 */
					// 借款方
					data.put("borrowerCompanyName", wloanSubject.getCompanyName().substring(0, 3) + "XXXXX" + wloanSubject.getCompanyName().substring(wloanSubject.getCompanyName().length() - 4));
					// 付款方
					String replaceRepayId = project.getReplaceRepayId();
					if (StringUtils.isBlank(replaceRepayId)) {
						data.put("replaceRepayCompanyName", null);
					} else {
						WloanSubject entity = new WloanSubject();
						entity.setLoanApplyId(replaceRepayId);
						List<WloanSubject> replaceRepays = wloanSubjectService.findList(entity); // 查询代偿方融资主体.
						if (replaceRepays != null && replaceRepays.size() > 0) {
							WloanSubject replaceRepay = replaceRepays.get(0);
							if (null != replaceRepay) {
								// 企业名称（代偿方名称）.
								data.put("replaceRepayCompanyName", replaceRepay.getCompanyName());
							}
						} else {
							data.put("replaceRepayCompanyName", null);
						}
					}

					// 项目金额
					data.put("amount", project.getAmount()); // 项目金额
					data.put("purpose", project.getPurpose()); // 项目用途
					data.put("projectcase", project.getProjectCase()); // 项目简介
					// =====================================================================
					/*
					 * ===============借款方信息==========================
					 */
					if (wloanSubject.getCompanyName() != null) {
						if (wloanSubject.getCompanyName().length() > 7) {
							data.put("borrowerCompanyName", wloanSubject.getCompanyName().substring(0, 3) + "XXXXX" + wloanSubject.getCompanyName().substring(wloanSubject.getCompanyName().length() - 4));// 公司名称
						} else {
							data.put("borrowerCompanyName", "XXXXXXXXXX");// 公司名称

						}
					} else {
						data.put("borrowerCompanyName", null);// 公司名称
					}
					data.put("borrowerRegisterAmount", wloanSubject.getRegisterAmount());// 注册资本
					data.put("borrowerRegistAddress", wloanSubject.getRegistAddress());// 注册地址
					data.put("borrowerRegisterDate", wloanSubject.getRegisterDate());// 成立时间
					if (wloanSubject.getAgentPersonName() != null) {
						data.put("agentPersonName", wloanSubject.getAgentPersonName().substring(0, 1) + "XX");// 法人代表
					} else {
						data.put("agentPersonName", null);// 法人代表
					}

					// ========根据借款人查询借款人基本信息.

					ZtmgLoanBasicInfo ztmgLoanBasicInfo = new ZtmgLoanBasicInfo();
					ztmgLoanBasicInfo.setCreditUserId(wloanSubject.getLoanApplyId());
					ZtmgLoanBasicInfo ztmgLoanBasicInfoEntity = ztmgLoanBasicInfoDao.findByCreditUserId(ztmgLoanBasicInfo);
					if (null != ztmgLoanBasicInfoEntity) {
						// 信用承诺书.
						String decFilePath = ztmgLoanBasicInfoEntity.getDeclarationFilePath();
						if (null != decFilePath) {
							boolean contains = decFilePath.contains("data");
							if (contains) {
								ztmgLoanBasicInfoEntity.setDeclarationFilePath(decFilePath.split("data")[1]);
							}
						}
						// 根据借款人基本信息查询股东信息.
						ZtmgLoanShareholdersInfo ztmgLoanShareholdersInfo = new ZtmgLoanShareholdersInfo();
						ztmgLoanShareholdersInfo.setLoanBasicId(ztmgLoanBasicInfoEntity.getId());
						List<ZtmgLoanShareholdersInfo> ztmgLoanShareholdersInfos = ztmgLoanShareholdersInfoDao.findListByLoanBasicInfoId(ztmgLoanShareholdersInfo);
						// 借款人股东信息.
						ztmgLoanBasicInfoEntity.setZtmgLoanShareholdersInfos(ztmgLoanShareholdersInfos);
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
						// 借款人征信报告.
						ztmgLoanBasicInfoEntity.setCreditAnnexFilePojosList(creditAnnexFilePojos);
						ztmgLoanBasicInfoEntity.setIndustry(IndustryUtils.getindustryName(ztmgLoanBasicInfoEntity.getIndustry()));
						// 实缴资本(元).
						if (StringUtils.isBlank(ztmgLoanBasicInfoEntity.getContributedCapital())) {
							ztmgLoanBasicInfoEntity.setContributedCapital("0");
						}
						// 注册资本(元).
						if (StringUtils.isBlank(ztmgLoanBasicInfoEntity.getRegisteredCapital())) {
							ztmgLoanBasicInfoEntity.setRegisteredCapital("0");
						}
						// 注册地址信息脱敏
						if (ztmgLoanBasicInfoEntity.getRegisteredAddress() != null) {
							if (ztmgLoanBasicInfoEntity.getRegisteredAddress().length() > 6) {
								String registeredAddressSubStr = ztmgLoanBasicInfoEntity.getRegisteredAddress().substring(0, 6);
								ztmgLoanBasicInfoEntity.setRegisteredAddress(registeredAddressSubStr.concat("XXXXXXXXX"));
							} else {
								ztmgLoanBasicInfoEntity.setRegisteredAddress(CommonStringUtils.replaceNameX(ztmgLoanBasicInfoEntity.getRegisteredAddress()));
							}
						}
						// 法人脱敏
						ztmgLoanBasicInfoEntity.setOperName(CommonStringUtils.replaceNameX(ztmgLoanBasicInfoEntity.getOperName()));
						// 借款人基本信息.
						data.put("ztmgLoanBasicInfoEntity", ztmgLoanBasicInfoEntity);
						if (StringUtils.isBlank(ztmgLoanBasicInfoEntity.getProvince()) && StringUtils.isBlank(ztmgLoanBasicInfoEntity.getCity())) {
							data.put("address", "XXXXX");// 注册地址
						} else {
							data.put("address", ztmgLoanBasicInfoEntity.getProvince() + ztmgLoanBasicInfoEntity.getCity() + "XXXXX");// 注册地址
						}
					} else {
						data.put("address", null);// 注册地址
						data.put("ztmgLoanBasicInfoEntity", null);
					}

				}

				/**
				 * 借款用户
				 */
				String creditUserId = wloanSubject.getLoanApplyId();
				if (creditUserId != null) {
					CreditSupplierToMiddlemen entity = new CreditSupplierToMiddlemen();
					entity.setSupplierId(creditUserId);
					List<CreditSupplierToMiddlemen> creditSupplierToMiddlemen = creditSupplierToMiddlemenDao.findCreditSupplierToMiddlemens(entity);
					if (creditSupplierToMiddlemen != null && creditSupplierToMiddlemen.size() > 0) {
						CreditUserInfo creditUser = creditUserInfoDao.get(creditSupplierToMiddlemen.get(0).getMiddlemenId());
						if (creditUser.getLevel() != null && !creditUser.getLevel().equals("")) {
							data.put("level", creditUser.getLevel());
						} else {
							data.put("level", "0");
						}
					} else {
						data.put("level", "0");
					}
				} else {
					data.put("level", "0");
				}

				/*
				 * =============出借记录=============================
				 */
				WloanTermInvest invest = new WloanTermInvest();
				project.setName("");
				invest.setWloanTermProject(project);
				invest.setState(WloanTermInvestService.WLOAN_TERM_INVEST_STATE_1);
				List<WloanTermInvest> bidlist = wloanTermInvestService.findList(invest);
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				if (bidlist != null && bidlist.size() > 0) {
					for (WloanTermInvest wloanTermInvest : bidlist) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("userPhone", Util.hideString(wloanTermInvest.getUserInfo().getName(), 3, 4));
						map.put("investAmount", wloanTermInvest.getAmount());
						map.put("investDate", DateUtils.formatDate(wloanTermInvest.getCreateDate(), "yyyy-MM-dd"));
						list.add(map);
					}
					data.put("bidList", list);// 出借记录
				} else {
					data.put("bidList", bidlist);// 出借记录
				}

				// =======================核心企业简介================
				// 获取核心企业简介
				String middlemenId = project.getReplaceRepayId();
				if (middlemenId != null) {
					CreditUserInfo creditUserInfo = creditUserInfoService.get(middlemenId);
					if (creditUserInfo != null) {

						if (creditUserInfo.getEnterpriseFullName() != null) {
							if (creditUserInfo.getEnterpriseFullName().length() > 7) {
								data.put("creditName", creditUserInfo.getEnterpriseFullName().substring(0, 3) + "XXXXX" + creditUserInfo.getEnterpriseFullName().substring(creditUserInfo.getEnterpriseFullName().length() - 4));
							} else {
								data.put("creditName", "XXXXXXXXXX");
							}
						} else {
							data.put("creditName", null);
						}
					} else {
						data.put("creditName", "");
					}
					CreditAnnexFile annexFile = new CreditAnnexFile();
					annexFile.setOtherId(middlemenId);
					annexFile.setType("30");
					List<CreditAnnexFile> annexFileList = creditAnnexFileService.findList(annexFile);
					if (annexFileList != null && annexFileList.size() > 0) {
						data.put("creditUrl", annexFileList.get(0).getRemark() + "?middlemenId=" + middlemenId);
					} else {
						data.put("creditUrl", null);
					}
				} else {
					data.put("creditName", null);
					data.put("creditUrl", null);
				}

				result.put("state", "0");
				result.put("message", "项目信息查询成功");
				result.put("data", data);
			} catch (Exception e) {
				e.printStackTrace();
				result.put("state", "1");
				result.put("message", "系统异常");
				result.put("data", null);
				return result;
			}
		} else {
			result.put("state", "2");
			result.put("message", "项目Id为空，缺少参数");
			result.put("data", null);
		}
		return result;
	}

	/**
	 * 项目详情---风控情况
	 * 
	 * @param from
	 * @param projectid
	 * @return
	 */
	@POST
	@Path("/getProjectInfoAnnex")
	public Map<String, Object> getProjectInfoAnnex(@FormParam("from") Integer from, @FormParam("projectid") String projectid) {

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();

		WloanTermProject project = null;
		if (!StringUtils.isBlank(projectid)) {
			try {
				project = wloanTermProjectService.get(projectid);
				if (project != null) {
					data.put("guaranteescheme", project.getGuaranteeScheme());

					WloanSubject wloanSubject = project.getWloanSubject();
					// 承诺函.
					List<CreditAnnexFile> commitments = new ArrayList<CreditAnnexFile>();
					// 营业执照.
					List<CreditAnnexFile> businessLicenses = new ArrayList<CreditAnnexFile>();
					// 银行许可证.
					List<CreditAnnexFile> bankPermitCerts = new ArrayList<CreditAnnexFile>();

					if (wloanSubject != null) { // 融资主体.
						// 企业介绍（主体、个人）.
						data.put("briefinfo", wloanSubject.getBriefInfo());
						// 企业名称（借款方名称）.
						if (wloanSubject.getCompanyName() != null) {
							if (wloanSubject.getCompanyName().length() > 7) {
								data.put("borrowerCompanyName", wloanSubject.getCompanyName().substring(0, 3) + "XXXXX" + wloanSubject.getCompanyName().substring(wloanSubject.getCompanyName().length() - 4));// 公司名称
							} else {
								data.put("borrowerCompanyName", "XXXXXXXXXX");// 公司名称

							}
						} else {
							data.put("borrowerCompanyName", null);// 公司名称
						}

						// 借款申请ID.
						String creditUserApplyId = project.getCreditUserApplyId();
						CreditUserApply creditUserApply = creditUserApplyDao.get(creditUserApplyId);
						if (null == creditUserApply) {
							String projectDataId = project.getProjectDataId(); // 借款资料ID.
							// 借款资料ID.
							data.put("creditInfoId", projectDataId);
							if (projectDataId != null) { // 非空判断.
								if (projectDataId.equals("")) {
									data.put("commitments", commitments);
								} else {
									// 借款资料
									CreditAnnexFile commitment = new CreditAnnexFile();
									commitment.setOtherId(projectDataId);
									commitments = creditAnnexFileService.findCreditAnnexFileList(commitment);
									for (CreditAnnexFile commitmentEntity : commitments) {
										if (CreditAnnexFileService.CREDIT_PROJECT_DATA_TYPE_7.equals(commitmentEntity.getType())) {
											// PDF.
											commitmentEntity.setUrl(Global.getConfig("pdf_show_path") + commitmentEntity.getUrl());
										} else {
											// IMAGE.
											commitmentEntity.setUrl(Global.getConfig("credit_file_path") + commitmentEntity.getUrl());
										}
									}
									data.put("commitments", commitments);
								}
							} else {
								data.put("commitments", commitments);
							}
						} else {
							// 借款资料ID.
							String projectDataId = creditUserApply.getProjectDataId();
							// 借款资料ID.
							data.put("creditInfoId", projectDataId);
							if (projectDataId != null) { // 非空判断.
								if (projectDataId.equals("")) {
									data.put("commitments", commitments);
								} else {
									// 借款资料
									CreditAnnexFile commitment = new CreditAnnexFile();
									commitment.setOtherId(projectDataId);
									commitments = creditAnnexFileService.findCreditAnnexFileList(commitment);
									for (CreditAnnexFile commitmentEntity : commitments) {
										if (CreditAnnexFileService.CREDIT_PROJECT_DATA_TYPE_7.equals(commitmentEntity.getType())) {
											// PDF.
											commitmentEntity.setUrl(Global.getConfig("pdf_show_path") + commitmentEntity.getUrl());
										} else {
											// IMAGE.
											commitmentEntity.setUrl(Global.getConfig("credit_file_path") + commitmentEntity.getUrl());
										}
									}
									data.put("commitments", commitments);
								}
							} else {
								data.put("commitments", commitments);
							}
						}

						String loanApplyId = wloanSubject.getLoanApplyId(); // 借款方用户ID.
						if (loanApplyId != null) { // 非空判断.
							if (loanApplyId.equals("")) {
								data.put("businessLicenses", businessLicenses);
								data.put("bankPermitCerts", bankPermitCerts);
							} else {
								// 借款方开户资料，营业执照.
								CreditAnnexFile businessLicense = new CreditAnnexFile();
								businessLicense.setOtherId(loanApplyId);
								businessLicense.setType(CreditAnnexFileService.CREDIT_PROJECT_DATA_TYPE_8);
								businessLicenses = creditAnnexFileService.findCreditAnnexFileList(businessLicense);
								for (CreditAnnexFile businessLicenseEntity : businessLicenses) {
									businessLicenseEntity.setUrl(Global.getConfig("credit_file_path") + businessLicenseEntity.getUrl());
								}
								data.put("businessLicenses", businessLicenses);
								// 借款方开户资料，营业执照.
								CreditAnnexFile bankPermitCert = new CreditAnnexFile();
								bankPermitCert.setOtherId(loanApplyId);
								bankPermitCert.setType(CreditAnnexFileService.CREDIT_PROJECT_DATA_TYPE_9);
								bankPermitCerts = creditAnnexFileService.findCreditAnnexFileList(bankPermitCert);
								for (CreditAnnexFile bankPermitCertEntity : bankPermitCerts) {
									bankPermitCertEntity.setUrl(Global.getConfig("credit_file_path") + bankPermitCertEntity.getUrl());
								}
								data.put("bankPermitCerts", bankPermitCerts);
							}
						} else {
							data.put("businessLicenses", businessLicenses);
							data.put("bankPermitCerts", bankPermitCerts);
						}
					} else {
						data.put("briefinfo", null);
						data.put("borrowerCompanyName", null);
						data.put("replaceRepayCompanyName", null);
						data.put("commitments", commitments);
						data.put("businessLicenses", businessLicenses);
						data.put("bankPermitCerts", bankPermitCerts);
					}
					if (project.getWgCompany() != null) { // 担保公司.
						if (project.getWgCompany().getName() != null) {
							if (project.getWgCompany().getName().length() > 7) {
								data.put("wgcompany", project.getWgCompany().getName().substring(0, 3) + "XXXXX" + project.getWgCompany().getName().substring(project.getWgCompany().getName().length() - 4)); // 担保公司名称
							} else {
								data.put("wgcompany", "XXXXXXXXXX"); // 担保公司名称
							}
						} else {
							data.put("wgcompany", null); // 担保公司名称
						}
						data.put("industry", project.getWgCompany().getIndustry()); // 所属行业
						data.put("guaranteecase", project.getWgCompany().getGuaranteeCase()); // 担保情况
					} else {
						data.put("wgcompany", null); // 担保公司名称
						data.put("industry", null); // 所属行业
						data.put("guaranteecase", null); // 担保情况
					}
					if (project.getArea() != null) { // 地理位置.
						data.put("locus", project.getArea().getName()); // 所在地
					} else {
						data.put("locus", null); // 所在地
					}

					// 风控信息
					data.put("guaranteescheme", project.getGuaranteeScheme()); // 担保方案

					// 借款资质文件(doc_id 对应附件信息)
					Map<String, Object> docImgList = null;
					if (project.getDocId() != null && project.getDocId().length() > 0) {
						docImgList = getImgList(project.getDocId());
						data.put("docimgs", docImgList.get("zizhiFile"));
						// 风控文件(wgcompany 对应的附件信息)
						data.put("wgimglist", docImgList.get("projectFile"));
						// 项目照片
						data.put("proimg", docImgList.get("fengKongFile")); // 项目图片
					} else {
						data.put("docimgs", null);
						// 风控文件(wgcompany 对应的附件信息)
						data.put("wgimglist", null);
						// 项目照片
						data.put("proimg", null); // 项目图片
					}
					// 投资人总数
					WloanTermInvest invest = new WloanTermInvest();
					project.setName("");
					invest.setWloanTermProject(project);
					List<WloanTermInvest> bidlist = wloanTermInvestService.findList(invest);
					if (bidlist != null && bidlist.size() > 0) {
						data.put("bidtotal", bidlist.size());
					} else {
						data.put("bidtotal", 0);
					}

					// 获取核心企业简介
					String middlemenId = project.getReplaceRepayId();
					if (middlemenId != null) {
						CreditUserInfo creditUserInfo = creditUserInfoService.get(middlemenId);
						if (creditUserInfo != null) {
							if (creditUserInfo.getEnterpriseFullName() != null) {
								if (creditUserInfo.getEnterpriseFullName().length() > 7) {
									data.put("creditName", creditUserInfo.getEnterpriseFullName().substring(0, 3) + "XXXXX" + creditUserInfo.getEnterpriseFullName().substring(creditUserInfo.getEnterpriseFullName().length() - 4));
								} else {
									data.put("creditName", "XXXXXXXXXX");
								}
							} else {
								data.put("creditName", null);
							}
						} else {
							data.put("creditName", "");
						}
						CreditAnnexFile annexFile = new CreditAnnexFile();
						annexFile.setOtherId(middlemenId);
						annexFile.setType("30");
						List<CreditAnnexFile> annexFileList = creditAnnexFileService.findList(annexFile);
						if (annexFileList != null && annexFileList.size() > 0) {
							data.put("creditUrl", annexFileList.get(0).getRemark() + "?middlemenId=" + middlemenId);
						} else {
							data.put("creditUrl", null);
						}
					} else {
						data.put("creditName", null);
						data.put("creditUrl", null);
					}

					result.put("state", "0");
					result.put("message", "项目详情[风控情况]成功");
					result.put("data", data);
				} else {
					result.put("state", "3");
					result.put("message", "查无此项目");
					result.put("data", null);
				}
			} catch (Exception e) {
				e.printStackTrace();
				result.put("state", "1");
				result.put("message", "系统异常");
				result.put("data", null);
				return result;
			}
		} else {
			result.put("state", "2");
			result.put("message", "项目Id为空，缺少参数");
			result.put("data", null);
		}
		return result;
	}

	/**
	 * 获取股东信息
	 * 
	 * @param from
	 * @param projectid
	 * @return
	 */
	@POST
	@Path("/getZtmgLoanBasicInfo")
	public Map<String, Object> getZtmgLoanBasicInfo(@FormParam("from") Integer from, @FormParam("projectid") String projectid) {

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();

		WloanTermProject project = null;
		if (!StringUtils.isBlank(projectid)) {
			try {
				project = wloanTermProjectService.get(projectid);
				WloanSubject wloanSubject = project.getWloanSubject();
				if (wloanSubject != null) {// 融资主体.
					ZtmgLoanBasicInfo ztmgLoanBasicInfo = new ZtmgLoanBasicInfo();
					ztmgLoanBasicInfo.setCreditUserId(wloanSubject.getLoanApplyId());
					ZtmgLoanBasicInfo ztmgLoanBasicInfoEntity = ztmgLoanBasicInfoDao.findByCreditUserId(ztmgLoanBasicInfo);
					if (null != ztmgLoanBasicInfoEntity) {
						// 根据借款人基本信息查询股东信息.
						ZtmgLoanShareholdersInfo ztmgLoanShareholdersInfo = new ZtmgLoanShareholdersInfo();
						ztmgLoanShareholdersInfo.setLoanBasicId(ztmgLoanBasicInfoEntity.getId());
						List<ZtmgLoanShareholdersInfo> ztmgLoanShareholdersInfos = ztmgLoanShareholdersInfoDao.findListByLoanBasicInfoId(ztmgLoanShareholdersInfo);
						List<ZtmgLoanShareholdersInfo> resultList = new ArrayList<ZtmgLoanShareholdersInfo>();
						if (ztmgLoanShareholdersInfos != null && ztmgLoanShareholdersInfos.size() > 0) {
							for (ZtmgLoanShareholdersInfo shareholders : ztmgLoanShareholdersInfos) {
								String shareholdersTypeName = "";
								String shareholdersCertTypeName = "";
								if (shareholders.getShareholdersType().equals("SHAREHOLDERS_TYPE_01")) {
									shareholdersTypeName = "自然人";
								} else if (shareholders.getShareholdersType().equals("SHAREHOLDERS_TYPE_02")) {
									shareholdersTypeName = "法人";
								}

								if (shareholders.getShareholdersCertType().equals("SHAREHOLDERS_CERT_TYPE_01")) {
									shareholdersCertTypeName = "居民身份证";
								} else if (shareholders.getShareholdersCertType().equals("SHAREHOLDERS_CERT_TYPE_02")) {
									shareholdersCertTypeName = "营业执照";
								}
								shareholders.setShareholdersType(shareholdersTypeName);
								shareholders.setShareholdersCertType(shareholdersCertTypeName);
								if (shareholders.getShareholdersName().length() > 4) {
									// 按公司名字处理
									shareholders.setShareholdersName(shareholders.getShareholdersName().substring(0, 3) + "XXXXX" + shareholders.getShareholdersName().substring(shareholders.getShareholdersName().length() - 2));
								} else {
									// 按人名处理
									shareholders.setShareholdersName(shareholders.getShareholdersName().substring(0, 1) + "XX");
								}
								resultList.add(shareholders);
							}
						}
						// 借款人股东信息.
						data.put("shareholdersList", resultList);
					} else {
						data.put("shareholdersList", null);
					}
				}
				result.put("state", "0");
				result.put("message", "股东信息查询成功");
				result.put("data", data);
			} catch (Exception e) {
				e.printStackTrace();
				result.put("state", "1");
				result.put("message", "系统异常");
				result.put("data", null);
				return result;
			}
		} else {
			result.put("state", "2");
			result.put("message", "项目Id为空，缺少参数");
			result.put("data", null);
		}
		return result;
	}

}
