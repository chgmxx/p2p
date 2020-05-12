/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.regular.repayplan;

import java.util.Date;
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

import com.power.platform.cgb.dao.ZtmgReplaceRepayOrderHistoryDao;
import com.power.platform.cgb.entity.ZtmgReplaceRepayOrderHistory;
import com.power.platform.cgb.service.ZtmgReplaceRepayOrderHistoryService;
import com.power.platform.cgb.type.StatusEnum;
import com.power.platform.common.config.Global;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.web.BaseController;
import com.power.platform.plan.dao.WloanTermProjectPlanDao;
import com.power.platform.plan.entity.WloanTermProjectPlan;
import com.power.platform.plan.service.WloanTermProjectPlanService;
import com.power.platform.regular.dao.WloanTermProjectDao;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.regular.service.WloanTermProjectService;

/**
 * 代偿还款订单历史Controller
 * 
 * @author soler
 * @version 2019-08-01
 */
@Controller
@RequestMapping(value = "${adminPath}/replace/repay/ztmgReplaceRepayOrderHistory")
public class ZtmgReplaceRepayOrderHistoryController extends BaseController {

	@Autowired
	private WloanTermProjectPlanDao wloanTermProjectPlanDao;
	@Autowired
	private ZtmgReplaceRepayOrderHistoryDao ztmgReplaceRepayOrderHistoryDao;
	@Autowired
	private WloanTermProjectDao wloanTermProjectDao;

	/**
	 * 代偿切换，默认代偿还款人：山西文之泉教育科技有限公司.
	 */
	private static final String REPLACE_REPAY_ID = "8868943080664815351";

	/**
	 * 是否代偿切换，0：否.
	 */
	// private static final String FLAG_0 = "0";

	/**
	 * 是否代偿切换，1：是.
	 */
	private static final String FLAG_1 = "1";

	@Autowired
	private ZtmgReplaceRepayOrderHistoryService ztmgReplaceRepayOrderHistoryService;

	@ModelAttribute
	public ZtmgReplaceRepayOrderHistory get(@RequestParam(required = false) String id) {

		ZtmgReplaceRepayOrderHistory entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = ztmgReplaceRepayOrderHistoryService.get(id);
		}
		if (entity == null) {
			entity = new ZtmgReplaceRepayOrderHistory();
		}
		return entity;
	}

	@RequiresPermissions("replace:repay:ztmgReplaceRepayOrderHistory:view")
	@RequestMapping(value = { "list", "" })
	public String list(ZtmgReplaceRepayOrderHistory ztmgReplaceRepayOrderHistory, HttpServletRequest request, HttpServletResponse response, Model model) {

		try {
			// 代偿切换.
			if (FLAG_1.equals(ztmgReplaceRepayOrderHistory.getFlag())) {
				logger.info("代偿切换,flag:{}", ztmgReplaceRepayOrderHistory.getFlag());

				if (null == ztmgReplaceRepayOrderHistory.getRepayDate()) { // 还款日为null.
					model.addAttribute("message", "必填项【还款日期】，请正确录入！！！");
					Page<ZtmgReplaceRepayOrderHistory> page = ztmgReplaceRepayOrderHistoryService.findPage(new Page<ZtmgReplaceRepayOrderHistory>(request, response), ztmgReplaceRepayOrderHistory);
					model.addAttribute("page", page);
					return "modules/regular/repayplan/ztmgReplaceRepayOrderHistoryList";
				}

				String projectSn = StringUtils.replaceBlanK(ztmgReplaceRepayOrderHistory.getProjectSn());
				logger.info("项目编号：{}，还款日期：{}", projectSn, DateUtils.formatDate(ztmgReplaceRepayOrderHistory.getRepayDate(), "yyyy-MM-dd"));
				if (DateUtils.isSameDate(new Date(), ztmgReplaceRepayOrderHistory.getRepayDate())) { // 当天还款有效.

					if (StringUtils.isBlank(projectSn)) { // 项目编号校验.
						model.addAttribute("message", "必填项【项目编号】，请正确录入！！！");
						Page<ZtmgReplaceRepayOrderHistory> page = ztmgReplaceRepayOrderHistoryService.findPage(new Page<ZtmgReplaceRepayOrderHistory>(request, response), ztmgReplaceRepayOrderHistory);
						model.addAttribute("page", page);
						return "modules/regular/repayplan/ztmgReplaceRepayOrderHistoryList";
					}

					WloanTermProjectPlan proPlan = new WloanTermProjectPlan();
					WloanTermProject wloanTermProject = new WloanTermProject();
					wloanTermProject.setSn(projectSn);
					proPlan.setWloanTermProject(wloanTermProject); // 项目编号.
					proPlan.setRepaymentDate(ztmgReplaceRepayOrderHistory.getRepayDate()); // 还款日期.
					WloanTermProjectPlan proPlans = wloanTermProjectPlanDao.findProPlanByProSnAndRepaymentDate(proPlan);
					ZtmgReplaceRepayOrderHistory zrroh = null;
					if (null != proPlans) {
						logger.info("开始切换 ...");
						// 散标信息变更.
						WloanTermProject project = wloanTermProjectDao.get(proPlans.getWloanTermProject() == null ? "" : proPlans.getWloanTermProject().getId());
						if (null != project) {
							// 判断该项目产品类型：如果是，供应链类，无需代偿切换.
							if (WloanTermProjectService.PROJECT_PRODUCT_TYPE_2.equals(project.getProjectProductType())) {
								model.addAttribute("message", "该项目产品类型为：供应链类，无需代偿切换！！！");
								Page<ZtmgReplaceRepayOrderHistory> page = ztmgReplaceRepayOrderHistoryService.findPage(new Page<ZtmgReplaceRepayOrderHistory>(request, response), ztmgReplaceRepayOrderHistory);
								model.addAttribute("page", page);
								return "modules/regular/repayplan/ztmgReplaceRepayOrderHistoryList";
							}
							// 留存切换历史.
							zrroh = new ZtmgReplaceRepayOrderHistory();
							zrroh.setId(IdGen.uuid());
							zrroh.setProName(proPlans.getWloanTermProject() == null ? "" : proPlans.getWloanTermProject().getName());
							zrroh.setProSn(proPlans.getWloanTermProject() == null ? "" : proPlans.getWloanTermProject().getSn());
							zrroh.setSubName(proPlans.getWloanSubject() == null ? "" : proPlans.getWloanSubject().getCompanyName());
							zrroh.setGrantAmount(proPlans.getWloanTermProject() == null ? "" : NumberUtils.scaleDoubleStr(proPlans.getWloanTermProject().getCurrentAmount()));
							zrroh.setRepayPlanId(proPlans.getId());
							zrroh.setRepayAmount(NumberUtils.scaleDoubleStr(proPlans.getInterest()));
							zrroh.setRepayType(proPlans.getPrincipal());
							zrroh.setRepayDate(proPlans.getRepaymentDate());
							zrroh.setCancelDate(proPlans.getWloanTermProject() == null ? null : proPlans.getWloanTermProject().getLoanDate());
							zrroh.setGrantDate(proPlans.getWloanTermProject() == null ? null : proPlans.getWloanTermProject().getRealLoanDate());
							zrroh.setStatus(StatusEnum.STATUS_ENUM_AS.getValue());
							zrroh.setCreateDate(new Date());
							zrroh.setUpdateDate(new Date());
							zrroh.setRemark("安心投类，代偿还款切换");
							List<ZtmgReplaceRepayOrderHistory> zrrohs = ztmgReplaceRepayOrderHistoryDao.findReplaceRepayOrderListByProSnAndRepayDate(zrroh);
							if (zrrohs != null && zrrohs.size() > 0) {
								logger.info("代偿切换记录：{}", zrrohs.size());
								Page<ZtmgReplaceRepayOrderHistory> page = ztmgReplaceRepayOrderHistoryService.findPage(new Page<ZtmgReplaceRepayOrderHistory>(request, response), ztmgReplaceRepayOrderHistory);
								model.addAttribute("page", page);
								model.addAttribute("message", "代偿切换失败，代偿历史已存在，请勿重复代偿切换！！！");
								return "modules/regular/repayplan/ztmgReplaceRepayOrderHistoryList";
							} else {
								// 散标产品类型切换为：供应链类.
								project.setProjectProductType(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2);
								project.setIsReplaceRepay(WloanTermProject.IS_REPLACE_REPAY_1);
								project.setReplaceRepayId(REPLACE_REPAY_ID); // 代偿还款人ID，默认是文之泉.
								int updateFlag = wloanTermProjectDao.update(project);
								if (updateFlag == 1) {
									logger.info("山西文之泉教育科技有限公司，间接代偿，切换成功！");
									int insertFlag = ztmgReplaceRepayOrderHistoryDao.insert(zrroh);
									if (insertFlag == 1) {
										logger.info("安心投类，代偿切换历史入库成功！");
										model.addAttribute("message", "代偿切换成功！！！");
									} else {
										logger.info("安心投类，代偿切换历史入库失败！");
									}
								}
							}
						}
					} else {
						model.addAttribute("message", "切换失败，该【项目编号】不属于当日还款计划队列，请正确录入！！！");
						Page<ZtmgReplaceRepayOrderHistory> page = ztmgReplaceRepayOrderHistoryService.findPage(new Page<ZtmgReplaceRepayOrderHistory>(request, response), ztmgReplaceRepayOrderHistory);
						model.addAttribute("page", page);
						return "modules/regular/repayplan/ztmgReplaceRepayOrderHistoryList";
					}

					Page<ZtmgReplaceRepayOrderHistory> page = ztmgReplaceRepayOrderHistoryService.findPage(new Page<ZtmgReplaceRepayOrderHistory>(request, response), ztmgReplaceRepayOrderHistory);
					model.addAttribute("page", page);
					return "modules/regular/repayplan/ztmgReplaceRepayOrderHistoryList";
				} else {
					model.addAttribute("message", "只支持当日代偿，切换失败，请录入正确的【还款日期】！！！");
					Page<ZtmgReplaceRepayOrderHistory> page = ztmgReplaceRepayOrderHistoryService.findPage(new Page<ZtmgReplaceRepayOrderHistory>(request, response), ztmgReplaceRepayOrderHistory);
					model.addAttribute("page", page);
					return "modules/regular/repayplan/ztmgReplaceRepayOrderHistoryList";
				}
			}
			// 正常查询.
			logger.info("代偿历史查询,flag:{}", ztmgReplaceRepayOrderHistory.getFlag());
			// 1）查询‘受理成功’的记录，是否完成还款
			ZtmgReplaceRepayOrderHistory entity = new ZtmgReplaceRepayOrderHistory();
			entity.setStatus(StatusEnum.STATUS_ENUM_AS.getValue());
			List<ZtmgReplaceRepayOrderHistory> list = ztmgReplaceRepayOrderHistoryDao.findList(entity);
			for (int i = 0; i < list.size(); i++) {
				ZtmgReplaceRepayOrderHistory z = list.get(i);
				WloanTermProjectPlan proPlan = wloanTermProjectPlanDao.get(z.getRepayPlanId());
				if (proPlan != null) {
					if (WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_STATE_2.equals(proPlan.getState())) { // 还款成功.
						// 2）代偿切换，还原-散标信息变更.
						WloanTermProject project = wloanTermProjectDao.get(proPlan.getProjectId());
						// 散标产品类型切换为：安心投类.
						project.setProjectProductType(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1);
						project.setIsReplaceRepay(WloanTermProject.IS_REPLACE_REPAY_0); // 是否代偿：否.
						project.setReplaceRepayId(null); // 代偿人为null.
						int updateFlag = wloanTermProjectDao.update(project);
						if (updateFlag == 1) { // 代偿切换，项目还原成功.
							logger.info("代偿切换，还原成功！！！");
							// 代偿记录，变更.
							z.setStatus(StatusEnum.STATUS_ENUM_S.getValue()); // 还款成功.
							z.setUpdateDate(new Date()); // 更新时间.
							int zUpdateFlag = ztmgReplaceRepayOrderHistoryDao.update(z);
							if (zUpdateFlag == 1) {
								logger.info("代偿记录，变更状态成功！！！");
							} else {
								logger.info("代偿记录，变更状态失败！！！");
							}
						} else {
							logger.info("代偿切换，还原失败！！！");
						}
					}
				}
			}
			Page<ZtmgReplaceRepayOrderHistory> page = ztmgReplaceRepayOrderHistoryService.findPage(new Page<ZtmgReplaceRepayOrderHistory>(request, response), ztmgReplaceRepayOrderHistory);
			model.addAttribute("page", page);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "modules/regular/repayplan/ztmgReplaceRepayOrderHistoryList";
	}

	@RequiresPermissions("replacerepay:orderhistory:ztmgReplaceRepayOrderHistory:view")
	@RequestMapping(value = "form")
	public String form(ZtmgReplaceRepayOrderHistory ztmgReplaceRepayOrderHistory, Model model) {

		model.addAttribute("ztmgReplaceRepayOrderHistory", ztmgReplaceRepayOrderHistory);
		return "modules/replacerepay/orderhistory/ztmgReplaceRepayOrderHistoryForm";
	}

	@RequiresPermissions("replacerepay:orderhistory:ztmgReplaceRepayOrderHistory:edit")
	@RequestMapping(value = "save")
	public String save(ZtmgReplaceRepayOrderHistory ztmgReplaceRepayOrderHistory, Model model, RedirectAttributes redirectAttributes) {

		if (!beanValidator(model, ztmgReplaceRepayOrderHistory)) {
			return form(ztmgReplaceRepayOrderHistory, model);
		}
		ztmgReplaceRepayOrderHistoryService.save(ztmgReplaceRepayOrderHistory);
		addMessage(redirectAttributes, "保存代偿还款订单历史成功");
		return "redirect:" + Global.getAdminPath() + "/replacerepay/orderhistory/ztmgReplaceRepayOrderHistory/?repage";
	}

	@RequiresPermissions("replacerepay:orderhistory:ztmgReplaceRepayOrderHistory:edit")
	@RequestMapping(value = "delete")
	public String delete(ZtmgReplaceRepayOrderHistory ztmgReplaceRepayOrderHistory, RedirectAttributes redirectAttributes) {

		ztmgReplaceRepayOrderHistoryService.delete(ztmgReplaceRepayOrderHistory);
		addMessage(redirectAttributes, "删除代偿还款订单历史成功");
		return "redirect:" + Global.getAdminPath() + "/replacerepay/orderhistory/ztmgReplaceRepayOrderHistory/?repage";
	}

}