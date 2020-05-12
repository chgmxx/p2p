
package com.power.platform.datastatistics.web;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
import com.power.platform.lanmao.search.pojo.LanMaoUserInfo;
import com.power.platform.lanmao.search.pojo.LanMaoWhiteList;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.regular.service.WloanTermProjectService;;
/**
 * 数据统计-Controller.
 * 
 * @author Yangzf
 * @version 2019-11-14
 */
@Controller
@RequestMapping(value = "${adminPath}/data/search/dataStatistics")
public class DataStatisticsController extends BaseController {

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
	@RequiresPermissions("dataStatistics:search:data:view")
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
		CreditUserInfo userInfo = new CreditUserInfo();
		userInfo.setCreditUserType("11");
		List<CreditUserInfo> middlemenList = creditUserInfoService.findList(userInfo);
		CreditUserInfo axtUserInfo = new CreditUserInfo();
		axtUserInfo.setId("10000");
		axtUserInfo.setEnterpriseFullName("安心投");
		middlemenList.add(axtUserInfo);
		model.addAttribute("middlemenList", middlemenList);
		if(creditUserInfo.getId()==null &&middlemenList!=null&&middlemenList.size()>0) {
			creditUserInfo.setId(middlemenList.get(0).getId());
		}
		if(creditUserInfo.getId()!=null && !"".equals(creditUserInfo.getId())) {
			WloanTermProject wloanTermProject = null;
			if("10000".equals(creditUserInfo.getId())) {
				wloanTermProject = wloanTermProjectService.searchAxtData(entTimeStr);
			} else {
				wloanTermProject = wloanTermProjectService.searchData(creditUserInfo.getId(),entTimeStr);
			}
			if(wloanTermProject!=null) {
				dataStatistics = wloanTermProject.getDataStatistics();
				if(dataStatistics!=null) {
					if(dataStatistics.getLoanPrincipal()==null) {
						dataStatistics.setLoanPrincipal("0");
					}
					if(dataStatistics.getAmountToPaid()==null) {
						dataStatistics.setAmountToPaid("0");
					}
					if(dataStatistics.getLoanAmount()==null) {
						dataStatistics.setLoanAmount("0");
					}
					if(dataStatistics.getRepaymentAmount()==null) {
						dataStatistics.setRepaymentAmount("0");
					}
				}
			}
//			model.addAttribute("message", result.get("errorMessage"));
			model.addAttribute("result", dataStatistics);
			
		}else{	
			
			model.addAttribute("message", "请选择核心企业或者安心投");
			model.addAttribute("result", dataStatistics);
		}
		return "modules/data/search/dataStatistics";
	}

}