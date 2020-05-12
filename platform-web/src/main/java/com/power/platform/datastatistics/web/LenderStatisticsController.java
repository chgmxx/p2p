
package com.power.platform.datastatistics.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.power.platform.common.config.Global;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.utils.excel.ExportExcel;
import com.power.platform.common.web.BaseController;
import com.power.platform.data.entity.LenderStatistics;
import com.power.platform.data.service.LenderStatisticsService;
import com.power.platform.regular.dao.WloanTermInvestDao;
import com.power.platform.regular.entity.WloanTermInvest;
import com.power.platform.regular.service.WloanTermInvestService;;
/**
 * 出借人信息统计查询-Controller.
 * 
 * @author Yangzf
 * @version 2019-11-19
 */
@Controller
@RequestMapping(value = "${adminPath}/data/search/lenderStatistics")
public class LenderStatisticsController extends BaseController {

	@Autowired
	private WloanTermInvestService wloanTermInvestService;
	@Autowired
	private WloanTermInvestDao wloanTermInvestDao;
	@Autowired
	private LenderStatisticsService lenderStatisticsService;

	
	@ModelAttribute
	public LenderStatistics get(@RequestParam(required = false) String id) {

		LenderStatistics entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = lenderStatisticsService.get(id);
		}
		if (entity == null) {
			entity = new LenderStatistics();
		}
		return entity;
	}
//	@RequiresPermissions("lenderStatistics:search:data:view")
//	@RequestMapping(value = { "list", "" })
//	public String list(WloanTermInvest wloanTermInvest, HttpServletRequest request, HttpServletResponse response, Model model) {
//
//		LenderStatistics lenderStatistics = new LenderStatistics();
//		Page<WloanTermInvest> page = wloanTermInvestService.findInvestPage(new Page<WloanTermInvest>(request, response),wloanTermInvest);
//		for (WloanTermInvest entity : page.getList()) {
//			if (entity.getLenderStatistics() != null) {
//				String registrationTime  = entity.getLenderStatistics().getRegistrationTime().replace(".0",""); // 注册时间
//				entity.getLenderStatistics().setRegistrationTime(registrationTime);
//				String firstLendingTime  = entity.getLenderStatistics().getFirstLendingTime().replace(".0",""); // 首次出借时间
//				entity.getLenderStatistics().setFirstLendingTime(firstLendingTime);
////				String annualizedRate  = entity.getLenderStatistics().getAnnualizedRate()+"%"; // 年化收益率
////				entity.getLenderStatistics().setAnnualizedRate(annualizedRate);
//			}
//		}
//		model.addAttribute("page", page);
//		return "modules/data/search/lenderStatistics";
//	}
	
	@RequiresPermissions("lenderStatistics:search:data:view")
	@RequestMapping(value = { "list", "" })
	public String list(LenderStatistics lenderStatistics, HttpServletRequest request, HttpServletResponse response, Model model) {

		Page<LenderStatistics> page = lenderStatisticsService.findPage(new Page<LenderStatistics>(request, response),lenderStatistics);
//		for (WloanTermInvest entity : page.getList()) {
//			if (entity.getLenderStatistics() != null) {
//				String registrationTime  = entity.getLenderStatistics().getRegistrationTime().replace(".0",""); // 注册时间
//				entity.getLenderStatistics().setRegistrationTime(registrationTime);
//				String firstLendingTime  = entity.getLenderStatistics().getFirstLendingTime().replace(".0",""); // 首次出借时间
//				entity.getLenderStatistics().setFirstLendingTime(firstLendingTime);
////				String annualizedRate  = entity.getLenderStatistics().getAnnualizedRate()+"%"; // 年化收益率
////				entity.getLenderStatistics().setAnnualizedRate(annualizedRate);
//			}
//		}
		model.addAttribute("page", page);
		return "modules/data/search/lenderStatistics";
	}

	/**
	 * 描述: 导出出借人信息EXCEL文件. <br>
	 * @return
	 */
	@RequiresPermissions("lenderStatistics:search:data:view")
	@RequestMapping(value = "exportLenderStatistics", method = RequestMethod.POST)
	public String exportLenderStatistics(LenderStatistics lenderStatistics, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {

		try {
			lenderStatistics = new LenderStatistics();
			String fileName = "出借人信息" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
			List<LenderStatistics> lender = new ArrayList<LenderStatistics>();
			List<LenderStatistics> lenderList = lenderStatisticsService.findList(lenderStatistics);
			for (LenderStatistics ls : lenderList) {
				if(ls.getTotalBalance()!=null&&!"".equals(ls.getTotalBalance())) {
					lender.add(ls);
				}
			}
			new ExportExcel("出借人信息", LenderStatistics.class).setDataList(lender).write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出出借人信息失败！失败信息：" + e.getMessage());
		}

		return "redirect:" + Global.getAdminPath() + "/data/search/lenderStatistics";
	}
	
	/*@RequiresPermissions("lenderStatistics:search:data:view")
	@RequestMapping(value = "exportLenderStatistics", method = RequestMethod.POST)
	public String exportLenderStatistics(WloanTermInvest wloanTermInvest, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {

		try {
			String fileName = "出借人信息" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
			List<LenderStatistics> lender = new ArrayList<LenderStatistics>();
			List<WloanTermInvest> list = wloanTermInvestDao.findInvestPage(wloanTermInvest);
			if(list!=null&&list.size()>0) {
				for (WloanTermInvest entity : list) {
					if (entity.getLenderStatistics() != null) {
						LenderStatistics lenderStatistics = new LenderStatistics();
						lenderStatistics.setLenderId(entity.getLenderStatistics().getLenderId());
						lenderStatistics.setRegistrationTime(entity.getLenderStatistics().getRegistrationTime().replace(".0",""));
						lenderStatistics.setFirstLendingTime(entity.getLenderStatistics().getFirstLendingTime().replace(".0",""));
						lenderStatistics.setTotalInterestReceived(entity.getLenderStatistics().getTotalInterestReceived());
						lenderStatistics.setAnnualizedRate(entity.getLenderStatistics().getAnnualizedRate());
						lender.add(lenderStatistics);
					}
				}
				new ExportExcel("出借人信息", LenderStatistics.class).setDataList(lender).write(response, fileName).dispose();
				return null;
			}
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出出借人信息失败！失败信息：" + e.getMessage());
		}

		return "redirect:" + Global.getAdminPath() + "/data/search/lenderStatistics";
	}*/
}