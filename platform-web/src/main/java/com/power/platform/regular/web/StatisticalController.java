package com.power.platform.regular.web;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.power.platform.common.security.shiro.session.SessionUtils;
import com.power.platform.common.utils.CommonStringUtils;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.common.utils.excel.ExportExcel;
import com.power.platform.common.web.BaseController;
import com.power.platform.pay.recharge.dao.UserRechargeDao;
import com.power.platform.pay.recharge.entity.UserRecharge;
import com.power.platform.plan.service.WloanTermUserPlanService;
import com.power.platform.regular.dao.WloanTermInvestDao;
import com.power.platform.regular.entity.WloanTermInvest;
import com.power.platform.regular.entity.WloanTermInvestExport;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.regular.service.WloanTermInvestService;
import com.power.platform.userinfo.dao.UserInfoDao;
import com.power.platform.userinfo.entity.UserInfo;

/**
 * 
 * 类: StatisticalController <br>
 * 描述: 运营统计数据. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年7月12日 上午10:02:25
 */
@Controller
@RequestMapping(value = "${adminPath}/statistical")
public class StatisticalController extends BaseController {

	@Autowired
	private WloanTermInvestService wloanTermInvestService;
	@Autowired
	private WloanTermUserPlanService wloanTermUserPlanService;
	@Resource
	private WloanTermInvestDao wloanTermInvestDao;
	@Resource
	private UserInfoDao userInfoDao;
	@Resource
	private UserRechargeDao userRechargeDao;

	@ModelAttribute
	public WloanTermInvest get(@RequestParam(required = false) String id) {

		WloanTermInvest entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = wloanTermInvestService.get(id);
		}
		if (entity == null) {
			entity = new WloanTermInvest();
		}
		entity.setCurrentUser(SessionUtils.getUser());
		return entity;
	}

	@RequiresPermissions("statistical:statistical:view")
	@RequestMapping(value = "detailList")
	public String detailList(WloanTermInvest wloanTermInvest, HttpServletRequest request, HttpServletResponse response, Model model) {

		if (WloanTermInvestService.USER_FLAG_ALL == Integer.valueOf(wloanTermInvest.getUserFlag())) { // 新老用户.
			List<WloanTermInvest> list = wloanTermInvestDao.findStatisticalAllList(wloanTermInvest);
			for (int i = 0; i < list.size(); i++) {
				WloanTermInvest wloanTermInvestEntity = list.get(i);
				if (wloanTermInvestEntity.getUserInfo() != null) {
					wloanTermInvestEntity.getUserInfo().setName(CommonStringUtils.mobileEncrypt(wloanTermInvestEntity.getUserInfo().getName()));
					wloanTermInvestEntity.getUserInfo().setRealName(CommonStringUtils.replaceNameX(wloanTermInvestEntity.getUserInfo().getRealName()));
				}
			}
			// 投资列表.
			model.addAttribute("list", list);
			// 用户类型.
			model.addAttribute("userType", "全部");
		} else if (WloanTermInvestService.USER_FLAG_NEW == Integer.valueOf(wloanTermInvest.getUserFlag())) { // 新用户.
			// 开始日期与结束日期，时间区间必须要选，如果没有选按当天时间区间进行查询.
			if (null == wloanTermInvest.getBeginBeginDate() || null == wloanTermInvest.getEndBeginDate()) {
				wloanTermInvest.setBeginBeginDate(DateUtils.parseDate(DateUtils.formatDate(new Date(), "yyyy-MM-dd") + " 00:00:00"));
				wloanTermInvest.setEndBeginDate(DateUtils.parseDate(DateUtils.formatDate(new Date(), "yyyy-MM-dd") + " 23:59:59"));
			}
			// 查询统计.
			List<WloanTermInvest> list = wloanTermInvestDao.findStatisticalNewUserList(wloanTermInvest);
			for (int i = 0; i < list.size(); i++) {
				WloanTermInvest wloanTermInvestEntity = list.get(i);
				if (wloanTermInvestEntity.getUserInfo() != null) {
					wloanTermInvestEntity.getUserInfo().setName(CommonStringUtils.mobileEncrypt(wloanTermInvestEntity.getUserInfo().getName()));
					wloanTermInvestEntity.getUserInfo().setRealName(CommonStringUtils.replaceNameX(wloanTermInvestEntity.getUserInfo().getRealName()));
				}
			}
			// 投资列表.
			model.addAttribute("list", list);
			// 用户类型.
			model.addAttribute("userType", "新用户");
		} else if (WloanTermInvestService.USER_FLAG_OLD == Integer.valueOf(wloanTermInvest.getUserFlag())) { // 老用户.
			// 开始日期与结束日期，时间区间必须要选，如果没有选按当天时间区间进行查询.
			if (null == wloanTermInvest.getBeginBeginDate() || null == wloanTermInvest.getEndBeginDate()) {
				wloanTermInvest.setBeginBeginDate(DateUtils.parseDate(DateUtils.formatDate(new Date(), "yyyy-MM-dd") + " 00:00:00"));
				wloanTermInvest.setEndBeginDate(DateUtils.parseDate(DateUtils.formatDate(new Date(), "yyyy-MM-dd") + " 23:59:59"));
			}
			// 查询统计.
			List<WloanTermInvest> list = wloanTermInvestDao.findStatisticalOldUserList(wloanTermInvest);
			for (int i = 0; i < list.size(); i++) {
				WloanTermInvest wloanTermInvestEntity = list.get(i);
				if (wloanTermInvestEntity.getUserInfo() != null) {
					wloanTermInvestEntity.getUserInfo().setName(CommonStringUtils.mobileEncrypt(wloanTermInvestEntity.getUserInfo().getName()));
					wloanTermInvestEntity.getUserInfo().setRealName(CommonStringUtils.replaceNameX(wloanTermInvestEntity.getUserInfo().getRealName()));
				}
			}
			// 投资列表.
			model.addAttribute("list", list);
			// 用户类型.
			model.addAttribute("userType", "老用户");
		}

		return "modules/statistical/statisticalDetailList";
	}

	@RequiresPermissions("statistical:statistical:view")
	@RequestMapping(value = "statistical_data_info")
	public String statisticalDataInfo(WloanTermInvest invest, HttpServletRequest request, HttpServletResponse response, Model model) {

		// 开始日期与结束日期，时间区间必须要选，如果没有选按当天时间区间进行查询.
		if (null == invest.getBeginBeginDate() || null == invest.getEndBeginDate()) {
			invest.setBeginBeginDate(DateUtils.parseDate(DateUtils.formatDate(new Date(), "yyyy-MM-dd") + " 00:00:00"));
			invest.setEndBeginDate(DateUtils.parseDate(DateUtils.formatDate(new Date(), "yyyy-MM-dd") + " 23:59:59"));
		}

		if (null == invest.getUserFlag() || WloanTermInvestService.USER_FLAG_ALL.equals(invest.getUserFlag())) { // 用户类型-全部.
			// 注册人数.
			List<UserInfo> userInfos = userInfoDao.findStatisticalAllList(invest);
			model.addAttribute("registeredCount", userInfos.size());

			// 充值总额.
			if (null == userRechargeDao.findUserRechargeTotalAmount(invest)) {
				model.addAttribute("userRechargeTotalAmount", "0.00");
			} else {
				model.addAttribute("userRechargeTotalAmount", new DecimalFormat("0.00").format(NumberUtils.scaleDouble(userRechargeDao.findUserRechargeTotalAmount(invest))));
			}

			// 用户类型.
			model.addAttribute("userType", "全部");

			// 融资总额.
			Double allUserFinancingTotalAmount = wloanTermInvestDao.findAllUserFinancingTotalAmount(invest);
			if (null == allUserFinancingTotalAmount) {
				model.addAttribute("financingAmount", "0.00");
			} else {
				model.addAttribute("financingAmount", new DecimalFormat("0.00").format(NumberUtils.scaleDouble(allUserFinancingTotalAmount)));
			}
			// 投资人次.
			Integer allUserInvestTotalCount = wloanTermInvestDao.findAllUserInvestTotalCount(invest);
			if (null == allUserInvestTotalCount) {
				model.addAttribute("investCount", "0");
			} else {
				model.addAttribute("investCount", new DecimalFormat("0").format(allUserInvestTotalCount));
			}
			// 投资人数.
			Integer allUserInvestPeopleTotalCount = wloanTermInvestDao.findAllUserInvestPeopleTotalCount(invest);
			if (null == allUserInvestPeopleTotalCount) {
				model.addAttribute("investPeopleCount", "0");
			} else {
				model.addAttribute("investPeopleCount", new DecimalFormat("0").format(allUserInvestPeopleTotalCount));
			}

			// 人均投资总额.
			if (allUserInvestPeopleTotalCount > 0) {
				model.addAttribute("perCapitaAmount", new DecimalFormat("0.00").format(NumberUtils.scaleDouble(allUserFinancingTotalAmount / allUserInvestPeopleTotalCount)));
			} else {
				model.addAttribute("perCapitaAmount", "0.00");
			}

			// 30天融资总额.
			WloanTermProject wloanTermProject = new WloanTermProject();
			wloanTermProject.setSpan(Integer.valueOf(WloanTermInvestService.SPAN_30));
			invest.setWloanTermProject(wloanTermProject);
			Double thirtyDaysFinancingAmount = wloanTermInvestDao.findAllUserFinancingTotalAmount(invest);
			if (null == thirtyDaysFinancingAmount) {
				model.addAttribute("thirtyDaysFinancingAmount", "0.00");
			} else {
				model.addAttribute("thirtyDaysFinancingAmount", new DecimalFormat("0.00").format(NumberUtils.scaleDouble(thirtyDaysFinancingAmount)));
			}
			// 30天投资人次.
			Integer thirtyDaysCount = wloanTermInvestDao.findAllUserInvestTotalCount(invest);
			if (null == thirtyDaysCount) {
				model.addAttribute("thirtyDaysCount", "0");
			} else {
				model.addAttribute("thirtyDaysCount", new DecimalFormat("0").format(thirtyDaysCount));
			}
			// 30天投资人数.
			Integer thirtyDaysPeopleCount = wloanTermInvestDao.findAllUserInvestPeopleTotalCount(invest);
			if (null == thirtyDaysPeopleCount) {
				model.addAttribute("thirtyDaysPeopleCount", "0");
			} else {
				model.addAttribute("thirtyDaysPeopleCount", new DecimalFormat("0").format(thirtyDaysPeopleCount));
			}

			// 90天融资总额.
			wloanTermProject.setSpan(Integer.valueOf(WloanTermInvestService.SPAN_90));
			invest.setWloanTermProject(wloanTermProject);
			Double ninetyDaysFinancingAmount = wloanTermInvestDao.findAllUserFinancingTotalAmount(invest);
			if (null == ninetyDaysFinancingAmount) {
				model.addAttribute("ninetyDaysFinancingAmount", "0.00");
			} else {
				model.addAttribute("ninetyDaysFinancingAmount", new DecimalFormat("0.00").format(NumberUtils.scaleDouble(ninetyDaysFinancingAmount)));
			}
			// 90天投资人次.
			Integer ninetyDaysCount = wloanTermInvestDao.findAllUserInvestTotalCount(invest);
			if (null == thirtyDaysCount) {
				model.addAttribute("ninetyDaysCount", "0");
			} else {
				model.addAttribute("ninetyDaysCount", new DecimalFormat("0").format(ninetyDaysCount));
			}
			// 90天投资人数.
			Integer ninetyDaysPeopleCount = wloanTermInvestDao.findAllUserInvestPeopleTotalCount(invest);
			if (null == ninetyDaysPeopleCount) {
				model.addAttribute("ninetyDaysPeopleCount", "0");
			} else {
				model.addAttribute("ninetyDaysPeopleCount", new DecimalFormat("0").format(ninetyDaysPeopleCount));
			}

			// 180天融资总额.
			wloanTermProject.setSpan(Integer.valueOf(WloanTermInvestService.SPAN_180));
			invest.setWloanTermProject(wloanTermProject);
			Double oneHundredAndEightyDaysFinancingAmount = wloanTermInvestDao.findAllUserFinancingTotalAmount(invest);
			if (null == oneHundredAndEightyDaysFinancingAmount) {
				model.addAttribute("oneHundredAndEightyDaysFinancingAmount", "0.00");
			} else {
				model.addAttribute("oneHundredAndEightyDaysFinancingAmount", new DecimalFormat("0.00").format(NumberUtils.scaleDouble(oneHundredAndEightyDaysFinancingAmount)));
			}
			// 180天投资人次.
			Integer oneHundredAndEightyDaysCount = wloanTermInvestDao.findAllUserInvestTotalCount(invest);
			if (null == thirtyDaysCount) {
				model.addAttribute("oneHundredAndEightyDaysCount", "0");
			} else {
				model.addAttribute("oneHundredAndEightyDaysCount", new DecimalFormat("0").format(oneHundredAndEightyDaysCount));
			}
			// 180天投资人数.
			Integer oneHundredAndEightyDaysPeopleCount = wloanTermInvestDao.findAllUserInvestPeopleTotalCount(invest);
			if (null == oneHundredAndEightyDaysPeopleCount) {
				model.addAttribute("oneHundredAndEightyDaysPeopleCount", "0");
			} else {
				model.addAttribute("oneHundredAndEightyDaysPeopleCount", new DecimalFormat("0").format(oneHundredAndEightyDaysPeopleCount));
			}

			// 360天融资总额.
			wloanTermProject.setSpan(Integer.valueOf(WloanTermInvestService.SPAN_360));
			invest.setWloanTermProject(wloanTermProject);
			Double threeHundredAndSixtyDaysFinancingAmount = wloanTermInvestDao.findAllUserFinancingTotalAmount(invest);
			if (null == threeHundredAndSixtyDaysFinancingAmount) {
				model.addAttribute("threeHundredAndSixtyDaysFinancingAmount", "0.00");
			} else {
				model.addAttribute("threeHundredAndSixtyDaysFinancingAmount", new DecimalFormat("0.00").format(NumberUtils.scaleDouble(threeHundredAndSixtyDaysFinancingAmount)));
			}
			// 360天投资人次.
			Integer threeHundredAndSixtyDaysCount = wloanTermInvestDao.findAllUserInvestTotalCount(invest);
			if (null == threeHundredAndSixtyDaysCount) {
				model.addAttribute("threeHundredAndSixtyDaysCount", "0");
			} else {
				model.addAttribute("threeHundredAndSixtyDaysCount", new DecimalFormat("0").format(threeHundredAndSixtyDaysCount));
			}
			// 360天投资人数.
			Integer threeHundredAndSixtyDaysPeopleCount = wloanTermInvestDao.findAllUserInvestPeopleTotalCount(invest);
			if (null == threeHundredAndSixtyDaysPeopleCount) {
				model.addAttribute("threeHundredAndSixtyDaysPeopleCount", "0");
			} else {
				model.addAttribute("threeHundredAndSixtyDaysPeopleCount", new DecimalFormat("0").format(threeHundredAndSixtyDaysPeopleCount));
			}
		} else if (WloanTermInvestService.USER_FLAG_NEW.equals(invest.getUserFlag())) { // 用户类型-新用户.
			// 注册人数.
			List<UserInfo> userInfos = userInfoDao.findStatisticalAllList(invest);
			model.addAttribute("registeredCount", userInfos.size());

			// 充值总额.
			if (null == userRechargeDao.findUserRechargeTotalAmount(invest)) {
				model.addAttribute("userRechargeTotalAmount", "0.00");
			} else {
				model.addAttribute("userRechargeTotalAmount", new DecimalFormat("0.00").format(NumberUtils.scaleDouble(userRechargeDao.findUserRechargeTotalAmount(invest))));
			}

			// 用户类型.
			model.addAttribute("userType", "新用户");

			// 融资总额.
			Double newUserFinancingTotalAmount = wloanTermInvestDao.findNewUserFinancingTotalAmount(invest);
			if (null == newUserFinancingTotalAmount) {
				model.addAttribute("financingAmount", "0.00");
			} else {
				model.addAttribute("financingAmount", new DecimalFormat("0.00").format(NumberUtils.scaleDouble(newUserFinancingTotalAmount)));
			}
			// 投资人次.
			Integer newUserInvestTotalCount = wloanTermInvestDao.findNewUserInvestTotalCount(invest);
			if (null == newUserInvestTotalCount) {
				model.addAttribute("investCount", "0");
			} else {
				model.addAttribute("investCount", new DecimalFormat("0").format(newUserInvestTotalCount));
			}
			// 投资人数.
			Integer newUserInvestPeopleTotalCount = wloanTermInvestDao.findNewUserInvestPeopleTotalCount(invest);
			if (null == newUserInvestPeopleTotalCount) {
				model.addAttribute("investPeopleCount", "0");
			} else {
				model.addAttribute("investPeopleCount", new DecimalFormat("0").format(newUserInvestPeopleTotalCount));
			}

			// 人均投资总额.
			if (newUserInvestPeopleTotalCount > 0) {
				model.addAttribute("perCapitaAmount", new DecimalFormat("0.00").format(NumberUtils.scaleDouble(newUserFinancingTotalAmount / newUserInvestPeopleTotalCount)));
			} else {
				model.addAttribute("perCapitaAmount", "0.00");
			}

			// 30天融资总额.
			WloanTermProject wloanTermProject = new WloanTermProject();
			wloanTermProject.setSpan(Integer.valueOf(WloanTermInvestService.SPAN_30));
			invest.setWloanTermProject(wloanTermProject);
			Double thirtyDaysFinancingAmount = wloanTermInvestDao.findNewUserFinancingTotalAmount(invest);
			if (null == thirtyDaysFinancingAmount) {
				model.addAttribute("thirtyDaysFinancingAmount", "0.00");
			} else {
				model.addAttribute("thirtyDaysFinancingAmount", new DecimalFormat("0.00").format(NumberUtils.scaleDouble(thirtyDaysFinancingAmount)));
			}
			// 30天投资人次.
			Integer thirtyDaysCount = wloanTermInvestDao.findNewUserInvestTotalCount(invest);
			if (null == thirtyDaysCount) {
				model.addAttribute("thirtyDaysCount", "0");
			} else {
				model.addAttribute("thirtyDaysCount", new DecimalFormat("0").format(thirtyDaysCount));
			}
			// 30天投资人数.
			Integer thirtyDaysPeopleCount = wloanTermInvestDao.findNewUserInvestPeopleTotalCount(invest);
			if (null == thirtyDaysPeopleCount) {
				model.addAttribute("thirtyDaysPeopleCount", "0");
			} else {
				model.addAttribute("thirtyDaysPeopleCount", new DecimalFormat("0").format(thirtyDaysPeopleCount));
			}

			// 90天融资总额.
			wloanTermProject.setSpan(Integer.valueOf(WloanTermInvestService.SPAN_90));
			invest.setWloanTermProject(wloanTermProject);
			Double ninetyDaysFinancingAmount = wloanTermInvestDao.findNewUserFinancingTotalAmount(invest);
			if (null == ninetyDaysFinancingAmount) {
				model.addAttribute("ninetyDaysFinancingAmount", "0.00");
			} else {
				model.addAttribute("ninetyDaysFinancingAmount", new DecimalFormat("0.00").format(NumberUtils.scaleDouble(ninetyDaysFinancingAmount)));
			}
			// 90天投资人次.
			Integer ninetyDaysCount = wloanTermInvestDao.findNewUserInvestTotalCount(invest);
			if (null == thirtyDaysCount) {
				model.addAttribute("ninetyDaysCount", "0");
			} else {
				model.addAttribute("ninetyDaysCount", new DecimalFormat("0").format(ninetyDaysCount));
			}
			// 90天投资人数.
			Integer ninetyDaysPeopleCount = wloanTermInvestDao.findNewUserInvestPeopleTotalCount(invest);
			if (null == ninetyDaysPeopleCount) {
				model.addAttribute("ninetyDaysPeopleCount", "0");
			} else {
				model.addAttribute("ninetyDaysPeopleCount", new DecimalFormat("0").format(ninetyDaysPeopleCount));
			}

			// 180天融资总额.
			wloanTermProject.setSpan(Integer.valueOf(WloanTermInvestService.SPAN_180));
			invest.setWloanTermProject(wloanTermProject);
			Double oneHundredAndEightyDaysFinancingAmount = wloanTermInvestDao.findNewUserFinancingTotalAmount(invest);
			if (null == ninetyDaysFinancingAmount) {
				model.addAttribute("oneHundredAndEightyDaysFinancingAmount", "0.00");
			} else {
				model.addAttribute("oneHundredAndEightyDaysFinancingAmount", new DecimalFormat("0.00").format(NumberUtils.scaleDouble(oneHundredAndEightyDaysFinancingAmount)));
			}
			// 180天投资人次.
			Integer oneHundredAndEightyDaysCount = wloanTermInvestDao.findNewUserInvestTotalCount(invest);
			if (null == thirtyDaysCount) {
				model.addAttribute("oneHundredAndEightyDaysCount", "0");
			} else {
				model.addAttribute("oneHundredAndEightyDaysCount", new DecimalFormat("0").format(oneHundredAndEightyDaysCount));
			}
			// 180天投资人数.
			Integer oneHundredAndEightyDaysPeopleCount = wloanTermInvestDao.findNewUserInvestPeopleTotalCount(invest);
			if (null == oneHundredAndEightyDaysPeopleCount) {
				model.addAttribute("oneHundredAndEightyDaysPeopleCount", "0");
			} else {
				model.addAttribute("oneHundredAndEightyDaysPeopleCount", new DecimalFormat("0").format(oneHundredAndEightyDaysPeopleCount));
			}

			// 360天融资总额.
			wloanTermProject.setSpan(Integer.valueOf(WloanTermInvestService.SPAN_360));
			invest.setWloanTermProject(wloanTermProject);
			Double threeHundredAndSixtyDaysFinancingAmount = wloanTermInvestDao.findNewUserFinancingTotalAmount(invest);
			if (null == threeHundredAndSixtyDaysFinancingAmount) {
				model.addAttribute("threeHundredAndSixtyDaysFinancingAmount", "0.00");
			} else {
				model.addAttribute("threeHundredAndSixtyDaysFinancingAmount", new DecimalFormat("0.00").format(NumberUtils.scaleDouble(threeHundredAndSixtyDaysFinancingAmount)));
			}
			// 360天投资人次.
			Integer threeHundredAndSixtyDaysCount = wloanTermInvestDao.findNewUserInvestTotalCount(invest);
			if (null == threeHundredAndSixtyDaysCount) {
				model.addAttribute("threeHundredAndSixtyDaysCount", "0");
			} else {
				model.addAttribute("threeHundredAndSixtyDaysCount", new DecimalFormat("0").format(threeHundredAndSixtyDaysCount));
			}
			// 360天投资人数.
			Integer threeHundredAndSixtyDaysPeopleCount = wloanTermInvestDao.findNewUserInvestPeopleTotalCount(invest);
			if (null == threeHundredAndSixtyDaysPeopleCount) {
				model.addAttribute("threeHundredAndSixtyDaysPeopleCount", "0");
			} else {
				model.addAttribute("threeHundredAndSixtyDaysPeopleCount", new DecimalFormat("0").format(threeHundredAndSixtyDaysPeopleCount));
			}
		} else if (WloanTermInvestService.USER_FLAG_OLD.equals(invest.getUserFlag())) { // 用户类型-老用户.
			// 注册人数.
			List<UserInfo> userInfos = userInfoDao.findStatisticalAllList(invest);
			model.addAttribute("registeredCount", userInfos.size());

			// 充值总额.
			if (null == userRechargeDao.findUserRechargeTotalAmount(invest)) {
				model.addAttribute("userRechargeTotalAmount", "0.00");
			} else {
				model.addAttribute("userRechargeTotalAmount", new DecimalFormat("0.00").format(NumberUtils.scaleDouble(userRechargeDao.findUserRechargeTotalAmount(invest))));
			}

			// 用户类型.
			model.addAttribute("userType", "老用户");

			// 融资总额.
			Double oldUserFinancingTotalAmount = wloanTermInvestDao.findOldUserFinancingTotalAmount(invest);
			if (null == oldUserFinancingTotalAmount) {
				model.addAttribute("financingAmount", "0.00");
			} else {
				model.addAttribute("financingAmount", new DecimalFormat("0.00").format(NumberUtils.scaleDouble(oldUserFinancingTotalAmount)));
			}
			// 投资人次.
			Integer oldUserInvestTotalCount = wloanTermInvestDao.findOldUserInvestTotalCount(invest);
			if (null == oldUserInvestTotalCount) {
				model.addAttribute("investCount", "0");
			} else {
				model.addAttribute("investCount", new DecimalFormat("0").format(oldUserInvestTotalCount));
			}
			// 投资人数.
			Integer oldUserInvestPeopleTotalCount = wloanTermInvestDao.findOldUserInvestPeopleTotalCount(invest);
			if (null == oldUserInvestPeopleTotalCount) {
				model.addAttribute("investPeopleCount", "0");
			} else {
				model.addAttribute("investPeopleCount", new DecimalFormat("0").format(oldUserInvestPeopleTotalCount));
			}

			// 人均投资总额.
			if (oldUserInvestPeopleTotalCount > 0) {
				model.addAttribute("perCapitaAmount", new DecimalFormat("0.00").format(NumberUtils.scaleDouble(oldUserFinancingTotalAmount / oldUserInvestPeopleTotalCount)));
			} else {
				model.addAttribute("perCapitaAmount", "0.00");
			}

			// 30天融资总额.
			WloanTermProject wloanTermProject = new WloanTermProject();
			wloanTermProject.setSpan(Integer.valueOf(WloanTermInvestService.SPAN_30));
			invest.setWloanTermProject(wloanTermProject);
			Double thirtyDaysFinancingAmount = wloanTermInvestDao.findOldUserFinancingTotalAmount(invest);
			if (null == thirtyDaysFinancingAmount) {
				model.addAttribute("thirtyDaysFinancingAmount", "0.00");
			} else {
				model.addAttribute("thirtyDaysFinancingAmount", new DecimalFormat("0.00").format(NumberUtils.scaleDouble(thirtyDaysFinancingAmount)));
			}
			// 30天投资人次.
			Integer thirtyDaysCount = wloanTermInvestDao.findOldUserInvestTotalCount(invest);
			if (null == thirtyDaysCount) {
				model.addAttribute("thirtyDaysCount", "0");
			} else {
				model.addAttribute("thirtyDaysCount", new DecimalFormat("0").format(thirtyDaysCount));
			}
			// 30天投资人数.
			Integer thirtyDaysPeopleCount = wloanTermInvestDao.findOldUserInvestPeopleTotalCount(invest);
			if (null == thirtyDaysPeopleCount) {
				model.addAttribute("thirtyDaysPeopleCount", "0");
			} else {
				model.addAttribute("thirtyDaysPeopleCount", new DecimalFormat("0").format(thirtyDaysPeopleCount));
			}

			// 90天融资总额.
			wloanTermProject.setSpan(Integer.valueOf(WloanTermInvestService.SPAN_90));
			invest.setWloanTermProject(wloanTermProject);
			Double ninetyDaysFinancingAmount = wloanTermInvestDao.findOldUserFinancingTotalAmount(invest);
			if (null == ninetyDaysFinancingAmount) {
				model.addAttribute("ninetyDaysFinancingAmount", "0.00");
			} else {
				model.addAttribute("ninetyDaysFinancingAmount", new DecimalFormat("0.00").format(NumberUtils.scaleDouble(ninetyDaysFinancingAmount)));
			}
			// 90天投资人次.
			Integer ninetyDaysCount = wloanTermInvestDao.findOldUserInvestTotalCount(invest);
			if (null == thirtyDaysCount) {
				model.addAttribute("ninetyDaysCount", "0");
			} else {
				model.addAttribute("ninetyDaysCount", new DecimalFormat("0").format(ninetyDaysCount));
			}
			// 90天投资人数.
			Integer ninetyDaysPeopleCount = wloanTermInvestDao.findOldUserInvestPeopleTotalCount(invest);
			if (null == ninetyDaysPeopleCount) {
				model.addAttribute("ninetyDaysPeopleCount", "0");
			} else {
				model.addAttribute("ninetyDaysPeopleCount", new DecimalFormat("0").format(ninetyDaysPeopleCount));
			}

			// 180天融资总额.
			wloanTermProject.setSpan(Integer.valueOf(WloanTermInvestService.SPAN_180));
			invest.setWloanTermProject(wloanTermProject);
			Double oneHundredAndEightyDaysFinancingAmount = wloanTermInvestDao.findOldUserFinancingTotalAmount(invest);
			if (null == ninetyDaysFinancingAmount) {
				model.addAttribute("oneHundredAndEightyDaysFinancingAmount", "0.00");
			} else {
				model.addAttribute("oneHundredAndEightyDaysFinancingAmount", new DecimalFormat("0.00").format(NumberUtils.scaleDouble(oneHundredAndEightyDaysFinancingAmount)));
			}
			// 180天投资人次.
			Integer oneHundredAndEightyDaysCount = wloanTermInvestDao.findOldUserInvestTotalCount(invest);
			if (null == thirtyDaysCount) {
				model.addAttribute("oneHundredAndEightyDaysCount", "0");
			} else {
				model.addAttribute("oneHundredAndEightyDaysCount", new DecimalFormat("0").format(oneHundredAndEightyDaysCount));
			}
			// 180天投资人数.
			Integer oneHundredAndEightyDaysPeopleCount = wloanTermInvestDao.findOldUserInvestPeopleTotalCount(invest);
			if (null == oneHundredAndEightyDaysPeopleCount) {
				model.addAttribute("oneHundredAndEightyDaysPeopleCount", "0");
			} else {
				model.addAttribute("oneHundredAndEightyDaysPeopleCount", new DecimalFormat("0").format(oneHundredAndEightyDaysPeopleCount));
			}

			// 360天融资总额.
			wloanTermProject.setSpan(Integer.valueOf(WloanTermInvestService.SPAN_360));
			invest.setWloanTermProject(wloanTermProject);
			Double threeHundredAndSixtyDaysFinancingAmount = wloanTermInvestDao.findOldUserFinancingTotalAmount(invest);
			if (null == threeHundredAndSixtyDaysFinancingAmount) {
				model.addAttribute("threeHundredAndSixtyDaysFinancingAmount", "0.00");
			} else {
				model.addAttribute("threeHundredAndSixtyDaysFinancingAmount", new DecimalFormat("0.00").format(NumberUtils.scaleDouble(threeHundredAndSixtyDaysFinancingAmount)));
			}
			// 360天投资人次.
			Integer threeHundredAndSixtyDaysCount = wloanTermInvestDao.findOldUserInvestTotalCount(invest);
			if (null == threeHundredAndSixtyDaysCount) {
				model.addAttribute("threeHundredAndSixtyDaysCount", "0");
			} else {
				model.addAttribute("threeHundredAndSixtyDaysCount", new DecimalFormat("0").format(threeHundredAndSixtyDaysCount));
			}
			// 360天投资人数.
			Integer threeHundredAndSixtyDaysPeopleCount = wloanTermInvestDao.findOldUserInvestPeopleTotalCount(invest);
			if (null == threeHundredAndSixtyDaysPeopleCount) {
				model.addAttribute("threeHundredAndSixtyDaysPeopleCount", "0");
			} else {
				model.addAttribute("threeHundredAndSixtyDaysPeopleCount", new DecimalFormat("0").format(threeHundredAndSixtyDaysPeopleCount));
			}
		}
		return "modules/statistical/operatingStatisticalDataInfoList";
	}

	/**
	 * 
	 * 方法: list <br>
	 * 描述: 运营数据统计. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年7月13日 上午11:21:12
	 * 
	 * @param wloanTermInvest
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequiresPermissions("statistical:statistical:view")
	@RequestMapping(value = { "list", "" })
	public String list(WloanTermInvest wloanTermInvest, HttpServletRequest request, HttpServletResponse response, Model model) {

		// 融资总额.
		Double financingAmount = 0d;
		// 30天(人次).
		Integer thirtyDaysCount = 0;
		// 30天融资金额.
		Double thirtyDaysFinancingAmount = 0d;
		// 90天(人次).
		Integer ninetyDaysCount = 0;
		// 90天融资金额.
		Double ninetyDaysFinancingAmount = 0d;
		// 180天(人次).
		Integer oneHundredAndEightyDaysCount = 0;
		// 180天融资金额.
		Double oneHundredAndEightyDaysFinancingAmount = 0d;
		// 360天(人次).
		Integer threeHundredAndSixtyDaysCount = 0;
		// 360天融资金额.
		Double threeHundredAndSixtyDaysFinancingAmount = 0d;
		// 人均投资额.
		Double perCapitaAmount = 0d;
		// 平台统计客户充值总额.
		Double userRechargeTotalAmount = 0d;
		// 总人数.
		TreeSet<String> allTreeSet = new TreeSet<String>();
		// 统计30天投资人数(使用TreeSet去重复).
		TreeSet<String> span30TreeSet = new TreeSet<String>();
		// 统计90天投资人数.
		TreeSet<String> span90TreeSet = new TreeSet<String>();
		// 统计180天投资人数.
		TreeSet<String> span180TreeSet = new TreeSet<String>();
		// 统计360天投资人数.
		TreeSet<String> span360TreeSet = new TreeSet<String>();

		if (null == wloanTermInvest.getUserFlag() || WloanTermInvestService.USER_FLAG_ALL == Integer.valueOf(wloanTermInvest.getUserFlag())) { // 新老用户.

			// 开始日期与结束日期，时间区间必须要选，如果没有选按当天时间区间进行查询.
			if (null == wloanTermInvest.getBeginBeginDate() || null == wloanTermInvest.getEndBeginDate()) {
				wloanTermInvest.setBeginBeginDate(DateUtils.parseDate(DateUtils.formatDate(new Date(), "yyyy-MM-dd") + " 00:00:00"));
				wloanTermInvest.setEndBeginDate(DateUtils.parseDate(DateUtils.formatDate(new Date(), "yyyy-MM-dd") + " 23:59:59"));
			}
			List<WloanTermInvest> list = wloanTermInvestDao.findStatisticalAllList(wloanTermInvest);
			for (WloanTermInvest entity : list) {
				// 融资总额.
				financingAmount += entity.getAmount();
				// 总人数.
				allTreeSet.add(entity.getUserInfo().getId());
				// 按投资期限统计.
				int span = entity.getWloanTermProject().getSpan();
				if (WloanTermInvestService.SPAN_30.equals(String.valueOf(span))) {
					thirtyDaysCount += 1;
					thirtyDaysFinancingAmount += entity.getAmount();
					// 统计30天投资人数.
					span30TreeSet.add(entity.getUserInfo().getId());
				} else if (WloanTermInvestService.SPAN_90.equals(String.valueOf(span))) { // 90天(人次).
					ninetyDaysCount += 1;
					ninetyDaysFinancingAmount += entity.getAmount();
					// 统计90天投资人数.
					span90TreeSet.add(entity.getUserInfo().getId());
				} else if (WloanTermInvestService.SPAN_180.equals(String.valueOf(span))) { // 180天(人次).
					oneHundredAndEightyDaysCount += 1;
					oneHundredAndEightyDaysFinancingAmount += entity.getAmount();
					// 统计180天投资人数.
					span180TreeSet.add(entity.getUserInfo().getId());
				} else if (WloanTermInvestService.SPAN_360.equals(String.valueOf(span))) { // 360天(人次).
					threeHundredAndSixtyDaysCount += 1;
					threeHundredAndSixtyDaysFinancingAmount += entity.getAmount();
					// 统计360天投资人数.
					span360TreeSet.add(entity.getUserInfo().getId());
				}
			}
			// 用户类型.
			model.addAttribute("userType", "全部");
			// 注册人数(人).
			List<UserInfo> userInfos = userInfoDao.findStatisticalAllList(wloanTermInvest);
			model.addAttribute("registeredCount", userInfos.size());
			// 平台统计客户充值总额.
			if (null == userRechargeDao.findUserRechargeTotalAmount(wloanTermInvest)) {
				model.addAttribute("userRechargeTotalAmount", new DecimalFormat("0.00").format(NumberUtils.scaleDouble(userRechargeTotalAmount)));
			} else {
				model.addAttribute("userRechargeTotalAmount", new DecimalFormat("0.00").format(NumberUtils.scaleDouble(userRechargeTotalAmount = userRechargeDao.findUserRechargeTotalAmount(wloanTermInvest))));
			}
			// 总人数.
			List<String> allList = new ArrayList<String>(allTreeSet);
			model.addAttribute("allPeopleCount", allList.size());
			if (list.size() > 0) {
				// 人均投资额.
				model.addAttribute("perCapitaAmount", perCapitaAmount = NumberUtils.scaleDouble(financingAmount / allList.size()));
			} else {
				// 人均投资额.
				model.addAttribute("perCapitaAmount", perCapitaAmount);
			}
		} else if (WloanTermInvestService.USER_FLAG_NEW == Integer.valueOf(wloanTermInvest.getUserFlag())) { // 新用户.

			// 开始日期与结束日期，时间区间必须要选，如果没有选按当天时间区间进行查询.
			if (null == wloanTermInvest.getBeginBeginDate() || null == wloanTermInvest.getEndBeginDate()) {
				wloanTermInvest.setBeginBeginDate(DateUtils.parseDate(DateUtils.formatDate(new Date(), "yyyy-MM-dd") + " 00:00:00"));
				wloanTermInvest.setEndBeginDate(DateUtils.parseDate(DateUtils.formatDate(new Date(), "yyyy-MM-dd") + " 23:59:59"));
			}
			// 查询统计.
			List<WloanTermInvest> list = wloanTermInvestDao.findStatisticalNewUserList(wloanTermInvest);
			for (WloanTermInvest entity : list) {
				// 融资总额.
				financingAmount += entity.getAmount();
				// 总人数.
				allTreeSet.add(entity.getUserInfo().getId());
				// 按投资期限统计.
				int span = entity.getWloanTermProject().getSpan();
				// 30天(人次).
				if (WloanTermInvestService.SPAN_30.equals(String.valueOf(span))) {
					thirtyDaysCount += 1;
					thirtyDaysFinancingAmount += entity.getAmount();
					// 统计30天投资人数.
					span30TreeSet.add(entity.getUserInfo().getId());
				} else if (WloanTermInvestService.SPAN_90.equals(String.valueOf(span))) { // 90天(人次).
					ninetyDaysCount += 1;
					ninetyDaysFinancingAmount += entity.getAmount();
					// 统计90天投资人数.
					span90TreeSet.add(entity.getUserInfo().getId());
				} else if (WloanTermInvestService.SPAN_180.equals(String.valueOf(span))) { // 180天(人次).
					oneHundredAndEightyDaysCount += 1;
					oneHundredAndEightyDaysFinancingAmount += entity.getAmount();
					// 统计180天投资人数.
					span180TreeSet.add(entity.getUserInfo().getId());
				} else if (WloanTermInvestService.SPAN_360.equals(String.valueOf(span))) { // 360天(人次).
					threeHundredAndSixtyDaysCount += 1;
					threeHundredAndSixtyDaysFinancingAmount += entity.getAmount();
					// 统计360天投资人数.
					span360TreeSet.add(entity.getUserInfo().getId());
				}
			}
			// 用户类型.
			model.addAttribute("userType", "新用户");
			// 注册人数(人).
			List<UserInfo> userInfos = userInfoDao.findStatisticalAllList(wloanTermInvest);
			model.addAttribute("registeredCount", userInfos.size());
			// 平台统计客户充值总额.
			if (null == userRechargeDao.findUserRechargeTotalAmount(wloanTermInvest)) {
				model.addAttribute("userRechargeTotalAmount", new DecimalFormat("0.00").format(NumberUtils.scaleDouble(userRechargeTotalAmount)));
			} else {
				model.addAttribute("userRechargeTotalAmount", new DecimalFormat("0.00").format(NumberUtils.scaleDouble(userRechargeTotalAmount = userRechargeDao.findUserRechargeTotalAmount(wloanTermInvest))));
			}
			// 总人数.
			List<String> allList = new ArrayList<String>(allTreeSet);
			model.addAttribute("allPeopleCount", allList.size());
			if (list.size() > 0) {
				// 人均投资额.
				model.addAttribute("perCapitaAmount", perCapitaAmount = NumberUtils.scaleDouble(financingAmount / allList.size()));
			} else {
				// 人均投资额.
				model.addAttribute("perCapitaAmount", perCapitaAmount);
			}
		} else if (WloanTermInvestService.USER_FLAG_OLD == Integer.valueOf(wloanTermInvest.getUserFlag())) { // 老用户.

			// 开始日期与结束日期，时间区间必须要选，如果没有选按当天时间区间进行查询.
			if (null == wloanTermInvest.getBeginBeginDate() || null == wloanTermInvest.getEndBeginDate()) {
				wloanTermInvest.setBeginBeginDate(DateUtils.parseDate(DateUtils.formatDate(new Date(), "yyyy-MM-dd") + " 00:00:00"));
				wloanTermInvest.setEndBeginDate(DateUtils.parseDate(DateUtils.formatDate(new Date(), "yyyy-MM-dd") + " 23:59:59"));
			}
			// 查询统计.
			List<WloanTermInvest> list = wloanTermInvestDao.findStatisticalOldUserList(wloanTermInvest);
			for (WloanTermInvest entity : list) {
				// 融资总额.
				financingAmount += entity.getAmount();
				// 总人数.
				allTreeSet.add(entity.getUserInfo().getId());
				// 按投资期限统计.
				int span = entity.getWloanTermProject().getSpan();
				// 30天(人次).
				if (WloanTermInvestService.SPAN_30.equals(String.valueOf(span))) {
					thirtyDaysCount += 1;
					thirtyDaysFinancingAmount += entity.getAmount();
					// 统计30天投资人数.
					span30TreeSet.add(entity.getUserInfo().getId());
				} else if (WloanTermInvestService.SPAN_90.equals(String.valueOf(span))) { // 90天(人次).
					ninetyDaysCount += 1;
					ninetyDaysFinancingAmount += entity.getAmount();
					// 统计90天投资人数.
					span90TreeSet.add(entity.getUserInfo().getId());
				} else if (WloanTermInvestService.SPAN_180.equals(String.valueOf(span))) { // 180天(人次).
					oneHundredAndEightyDaysCount += 1;
					oneHundredAndEightyDaysFinancingAmount += entity.getAmount();
					// 统计180天投资人数.
					span180TreeSet.add(entity.getUserInfo().getId());
				} else if (WloanTermInvestService.SPAN_360.equals(String.valueOf(span))) { // 360天(人次).
					threeHundredAndSixtyDaysCount += 1;
					threeHundredAndSixtyDaysFinancingAmount += entity.getAmount();
					// 统计360天投资人数.
					span360TreeSet.add(entity.getUserInfo().getId());
				}
			}
			// 用户类型.
			model.addAttribute("userType", "老用户");
			// 注册人数(人).
			List<UserInfo> userInfos = userInfoDao.findStatisticalAllList(wloanTermInvest);
			model.addAttribute("registeredCount", userInfos.size());
			// 平台统计客户充值总额.
			if (null == userRechargeDao.findUserRechargeTotalAmount(wloanTermInvest)) {
				model.addAttribute("userRechargeTotalAmount", new DecimalFormat("0.00").format(NumberUtils.scaleDouble(userRechargeTotalAmount)));
			} else {
				model.addAttribute("userRechargeTotalAmount", new DecimalFormat("0.00").format(NumberUtils.scaleDouble(userRechargeTotalAmount = userRechargeDao.findUserRechargeTotalAmount(wloanTermInvest))));
			}
			// 总人数.
			List<String> allList = new ArrayList<String>(allTreeSet);
			model.addAttribute("allPeopleCount", allList.size());
			if (list.size() > 0) {
				// 人均投资额.
				model.addAttribute("perCapitaAmount", perCapitaAmount = NumberUtils.scaleDouble(financingAmount / allList.size()));
			} else {
				// 人均投资额.
				model.addAttribute("perCapitaAmount", perCapitaAmount);
			}
		}

		// 用于转换成普通数字.
		// BigDecimal bd = new
		// BigDecimal(NumberUtils.scaleDouble(financingAmount));
		// 融资总额.
		model.addAttribute("financingAmount", new DecimalFormat("0.00").format(NumberUtils.scaleDouble(financingAmount)));
		// 30天(人次).
		model.addAttribute("thirtyDaysCount", thirtyDaysCount);
		// 30天(人数).
		List<String> span30List = new ArrayList<String>(span30TreeSet);
		model.addAttribute("thirtyDaysPeopleCount", span30List.size());
		// 30天融资金额.
		// BigDecimal bd30 = new
		// BigDecimal(NumberUtils.scaleDouble(thirtyDaysFinancingAmount));
		model.addAttribute("thirtyDaysFinancingAmount", new DecimalFormat("0.00").format(NumberUtils.scaleDouble(thirtyDaysFinancingAmount)));
		// 90天(人次).
		model.addAttribute("ninetyDaysCount", ninetyDaysCount);
		// 90天(人数).
		List<String> span90List = new ArrayList<String>(span90TreeSet);
		model.addAttribute("ninetyDaysPeopleCount", span90List.size());
		// 90天融资金额.
		// BigDecimal bd90 = new
		// BigDecimal(NumberUtils.scaleDouble(ninetyDaysFinancingAmount));
		model.addAttribute("ninetyDaysFinancingAmount", new DecimalFormat("0.00").format(NumberUtils.scaleDouble(ninetyDaysFinancingAmount)));
		// 180天(人次).
		model.addAttribute("oneHundredAndEightyDaysCount", oneHundredAndEightyDaysCount);
		// 180天(人数).
		List<String> span180List = new ArrayList<String>(span180TreeSet);
		model.addAttribute("oneHundredAndEightyDaysPeopleCount", span180List.size());
		// 180天融资金额.
		// BigDecimal bd180 = new
		// BigDecimal(NumberUtils.scaleDouble(oneHundredAndEightyDaysFinancingAmount));
		model.addAttribute("oneHundredAndEightyDaysFinancingAmount", new DecimalFormat("0.00").format(NumberUtils.scaleDouble(oneHundredAndEightyDaysFinancingAmount)));
		// 360天(人次).
		model.addAttribute("threeHundredAndSixtyDaysCount", threeHundredAndSixtyDaysCount);
		// 360天(人数).
		List<String> span360List = new ArrayList<String>(span360TreeSet);
		model.addAttribute("threeHundredAndSixtyDaysPeopleCount", span360List.size());
		// 360天融资金额.
		// BigDecimal bd360 = new
		// BigDecimal(NumberUtils.scaleDouble(threeHundredAndSixtyDaysFinancingAmount));
		model.addAttribute("threeHundredAndSixtyDaysFinancingAmount", new DecimalFormat("0.00").format(NumberUtils.scaleDouble(threeHundredAndSixtyDaysFinancingAmount)));
		return "modules/statistical/statisticalList";
	}

	@RequiresPermissions("statistical:statistical:view")
	@RequestMapping(value = "rechargeList")
	public String rechargeList(Date beginBeginDate, Date endBeginDate, HttpServletRequest request, HttpServletResponse response, Model model) {

		UserRecharge userRecharge = new UserRecharge();
		userRecharge.setBeginBeginDate(beginBeginDate);
		userRecharge.setEndBeginDate(endBeginDate);
		userRecharge.setState(3);
		List<UserRecharge> list = userRechargeDao.findList(userRecharge);
		for (int i = 0; i < list.size(); i++) {
			UserRecharge userRechargeEntity = list.get(i);
			if (userRechargeEntity.getUserInfo() != null) {
				userRechargeEntity.getUserInfo().setName(CommonStringUtils.mobileEncrypt(userRechargeEntity.getUserInfo().getName()));
				userRechargeEntity.getUserInfo().setRealName(CommonStringUtils.replaceNameX(userRechargeEntity.getUserInfo().getRealName()));
			}
		}

		model.addAttribute("rechargeList", list);
		return "modules/statistical/statisticalRechargeList";
	}

	/**
	 * 
	 * 方法: exportUserInfo <br>
	 * 描述: 导出客户信息. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年6月29日 下午4:20:11
	 * 
	 * @param userInfo
	 * @param request
	 * @param response
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("statistical:statistical:view")
	@RequestMapping(value = "export", method = RequestMethod.POST)
	public String exportUserInfo(WloanTermInvest wloanTermInvest, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes, Model model) {

		try {
			String fileName = "";
			List<WloanTermInvest> list = new ArrayList<WloanTermInvest>();
			if (WloanTermInvestService.USER_FLAG_ALL == Integer.valueOf(wloanTermInvest.getUserFlag())) { // 新老用户.
				fileName = "运营数据新老用户" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
				list = wloanTermInvestDao.findStatisticalAllList(wloanTermInvest);
				// 投资列表.
				model.addAttribute("list", list);
				// 用户类型.
				model.addAttribute("userType", "全部");
			} else if (WloanTermInvestService.USER_FLAG_NEW == Integer.valueOf(wloanTermInvest.getUserFlag())) { // 新用户.
				fileName = "运营数据新用户" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
				// 开始日期与结束日期，时间区间必须要选，如果没有选按当天时间区间进行查询.
				if (null == wloanTermInvest.getBeginBeginDate() || null == wloanTermInvest.getEndBeginDate()) {
					wloanTermInvest.setBeginBeginDate(DateUtils.parseDate(DateUtils.formatDate(new Date(), "yyyy-MM-dd") + " 00:00:00"));
					wloanTermInvest.setEndBeginDate(DateUtils.parseDate(DateUtils.formatDate(new Date(), "yyyy-MM-dd") + " 23:59:59"));
				}
				// 查询统计.
				list = wloanTermInvestDao.findStatisticalNewUserList(wloanTermInvest);
				// 投资列表.
				model.addAttribute("list", list);
				// 用户类型.
				model.addAttribute("userType", "新用户");
			} else if (WloanTermInvestService.USER_FLAG_OLD == Integer.valueOf(wloanTermInvest.getUserFlag())) { // 老用户.
				fileName = "运营数据老用户" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
				// 开始日期与结束日期，时间区间必须要选，如果没有选按当天时间区间进行查询.
				if (null == wloanTermInvest.getBeginBeginDate() || null == wloanTermInvest.getEndBeginDate()) {
					wloanTermInvest.setBeginBeginDate(DateUtils.parseDate(DateUtils.formatDate(new Date(), "yyyy-MM-dd") + " 00:00:00"));
					wloanTermInvest.setEndBeginDate(DateUtils.parseDate(DateUtils.formatDate(new Date(), "yyyy-MM-dd") + " 23:59:59"));
				}
				// 查询统计.
				list = wloanTermInvestDao.findStatisticalOldUserList(wloanTermInvest);
				// 投资列表.
				model.addAttribute("list", list);
				// 用户类型.
				model.addAttribute("userType", "老用户");
			}
			List<WloanTermInvestExport> newList = new ArrayList<WloanTermInvestExport>();
			for (WloanTermInvest wloanTermInvest2 : list) {
				WloanTermInvestExport export = new WloanTermInvestExport();
				export.setUserInfo(wloanTermInvest2.getUserInfo());
				export.setPartnerForm(wloanTermInvest2.getPartnerForm());
				export.setWloanTermProject(wloanTermInvest2.getWloanTermProject());
				export.setAmount(wloanTermInvest2.getAmount());
				export.setBeginDate(wloanTermInvest2.getBeginDate());
				newList.add(export);
			}
			new ExportExcel("运营数据统计", WloanTermInvestExport.class).setDataList(newList).write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出运营数据失败！失败信息：" + e.getMessage());
		}

		return "modules/statistical/statisticalDetailList";
	}
}