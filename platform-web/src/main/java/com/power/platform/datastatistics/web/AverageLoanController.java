
package com.power.platform.datastatistics.web;

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

import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.web.BaseController;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.service.userinfo.CreditUserInfoService;
import com.power.platform.data.entity.DataStatistics;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.regular.service.WloanTermProjectService;;
/**
 * 期数、利率平均数统计-Controller.
 * 
 * @author Yangzf
 * @version 2019-11-18
 */
@Controller
@RequestMapping(value = "${adminPath}/data/search/averageLoan")
public class AverageLoanController extends BaseController {

	@Autowired
	private CreditUserInfoService creditUserInfoService;
	@Autowired
	private WloanTermProjectService wloanTermProjectService;
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
	@RequiresPermissions("averageLoan:search:data:view")
	@RequestMapping(value = { "list", "" })
	public String list(CreditUserInfo creditUserInfo, HttpServletRequest request, HttpServletResponse response, Model model) {

		DataStatistics dataStatistics = new DataStatistics();
//		creditUserInfo.setId("6809349449100994498");
		String entTimeStr = null;
		if(creditUserInfo.getEndRepaymentDate()!=null) {
			Date entTime = creditUserInfo.getEndRepaymentDate();
			entTimeStr = DateUtils.getDate(entTime,"yyyy-MM-dd");
			entTimeStr = entTimeStr + " 23:59:59";
		} else {
			entTimeStr = DateUtils.getDate(new Date(),"yyyy-MM-dd HH:mm:ss");
		}
		Double totalSpan = 0D;
		//查询去重的标的
		List<WloanTermProject> projectList = wloanTermProjectService.searchDistinct(entTimeStr);
		if(projectList!=null&&projectList.size()>0) {
			for (WloanTermProject wloanTermProject : projectList) {
				//查询区间用户发布的标的
				List<WloanTermProject> pList = wloanTermProjectService.searchBySubjectId(wloanTermProject.getSubjectId(),entTimeStr);
				int span = 0;
				if(pList!=null&&pList.size()>0) {
					for (WloanTermProject wt : pList) {
						span+= wt.getSpan();
					}
					totalSpan += NumberUtils.scaleDouble((Double.valueOf(span)/pList.size()));
				}
			}
		}
		System.out.println("totalSpan="+totalSpan);
		WloanTermProject wloanTermProject = wloanTermProjectService.searchAverageData(entTimeStr);
		if(wloanTermProject!=null) {
			dataStatistics = wloanTermProject.getDataStatistics();
//			String averageLoanPeriod = dataStatistics.getAverageLoanPeriod();
//			if(dataStatistics.getLoanSupplierCount()==0||dataStatistics.getLoanSupplierCount()==null) {
//				dataStatistics.setAverageLoanPeriod("0");
//			} else {
//				dataStatistics.setAverageLoanPeriod(NumberUtils.scaleDouble((Double.valueOf(averageLoanPeriod)/dataStatistics.getLoanSupplierCount()))+"");
//			}
			if(dataStatistics.getLoanSupplierCount()==0||dataStatistics.getLoanSupplierCount()==null) {
				dataStatistics.setAverageLoanPeriod("0");
			} else {
				dataStatistics.setAverageLoanPeriod(NumberUtils.scaleDouble((totalSpan/dataStatistics.getLoanSupplierCount()))+"");
			}
			if(dataStatistics.getLoanProjectCount()==0||dataStatistics.getLoanProjectCount()==null) {
				dataStatistics.setAverageProjectPeriod("0");
			} else {
				dataStatistics.setAverageProjectPeriod(NumberUtils.scaleDouble((totalSpan/dataStatistics.getLoanProjectCount()))+"");
			}
			if(dataStatistics.getAverageLoanInterestRate()==null) {
				dataStatistics.setAverageLoanInterestRate("0");
			}
			model.addAttribute("result", dataStatistics);
		}else{			
			model.addAttribute("message", "查询不到数据");
			model.addAttribute("result", dataStatistics);
		}
		return "modules/data/search/averageLoan";
	}

}