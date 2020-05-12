/**
 */
package com.power.platform.current.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.power.platform.common.config.Global;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.security.shiro.session.SessionUtils;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.PdfUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.web.BaseController;
import com.power.platform.current.entity.WloanCurrentProject;
import com.power.platform.current.entity.invest.WloanCurrentProjectInvest;
import com.power.platform.current.entity.redeem.WloanCurrentUserRedeem;
import com.power.platform.current.service.WloanCurrentProjectService;
import com.power.platform.current.service.invest.WloanCurrentProjectInvestService;
import com.power.platform.current.service.redeem.WloanCurrentUserRedeemService;
import com.power.platform.regular.entity.WGuaranteeCompany;
import com.power.platform.regular.entity.WloanSubject;
import com.power.platform.trandetail.service.UserTransDetailService;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.userinfo.service.UserInfoService;


/**
 * 活期赎回Controller
 * @author yb
 * @version 2016-01-13
 */
@Controller
@RequestMapping(value = "${adminPath}/redeem/wloanCurrentUserRedeem")
public class WloanCurrentUserRedeemController extends BaseController {

	@Autowired
	private WloanCurrentUserRedeemService wloanCurrentUserRedeemService;
	@Autowired
	private WloanCurrentProjectInvestService wloanCurrentProjectInvestService;
	@Autowired
	private WloanCurrentProjectService wloanCurrentProjectService;
	@Autowired
	private UserInfoService userInfoService;
	@Autowired
	private UserTransDetailService userTransDetailService;
	
	@ModelAttribute
	public WloanCurrentUserRedeem get(@RequestParam(required=false) String id) {
		WloanCurrentUserRedeem entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = wloanCurrentUserRedeemService.get(id);
		}
		if (entity == null){
			entity = new WloanCurrentUserRedeem();
		}
		return entity;
	}
	
	@RequiresPermissions("redeem:wloanCurrentUserRedeem:view")
	@RequestMapping(value = {"list", ""})
	public String list(WloanCurrentUserRedeem wloanCurrentUserRedeem, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<WloanCurrentUserRedeem> page = wloanCurrentUserRedeemService.findPage(new Page<WloanCurrentUserRedeem>(request, response), wloanCurrentUserRedeem); 
		model.addAttribute("page", page);
		model.addAttribute("usertype", SessionUtils.getUser().getUserType());
		return "modules/current/redeem/wloanCurrentUserRedeemList";
	}

	@RequiresPermissions("redeem:wloanCurrentUserRedeem:view")
	@RequestMapping(value = "form")
	public String form(WloanCurrentUserRedeem wloanCurrentUserRedeem, Model model) {
		model.addAttribute("wloanCurrentUserRedeem", wloanCurrentUserRedeem);
		return "modules/current/redeem/wloanCurrentUserRedeemForm";
	}

	@RequiresPermissions("redeem:wloanCurrentUserRedeem:edit")
	@RequestMapping(value = "save")
	public String save(WloanCurrentUserRedeem wloanCurrentUserRedeem, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, wloanCurrentUserRedeem)){
			return form(wloanCurrentUserRedeem, model);
		}
		wloanCurrentUserRedeemService.save(wloanCurrentUserRedeem);
		addMessage(redirectAttributes, "保存活期赎回成功");
		return "redirect:"+Global.getAdminPath()+"/redeem/wloanCurrentUserRedeem/?repage";
	}
	
	@RequiresPermissions("redeem:wloanCurrentUserRedeem:edit")
	@RequestMapping(value = "delete")
	public String delete(WloanCurrentUserRedeem wloanCurrentUserRedeem, RedirectAttributes redirectAttributes) {
		wloanCurrentUserRedeemService.delete(wloanCurrentUserRedeem);
		addMessage(redirectAttributes, "删除活期赎回成功");
		return "redirect:"+Global.getAdminPath()+"/redeem/wloanCurrentUserRedeem/?repage";
	}
	
	/**
	 * 活期赎回---审核
	 * @param wloanTermProject
	 * @param model
	 * @return
	 */
	@RequiresPermissions("redeem:wloanCurrentUserRedeem:view")
	@RequestMapping(value = "check")
	public String check(WloanCurrentUserRedeem wloanCurrentUserRedeem, Model model) {
		model.addAttribute("wloanCurrentUserRedeem", wloanCurrentUserRedeem);
		model.addAttribute("usertype", SessionUtils.getUser().getUserType());
		return "modules/current/redeem/wloanCurrentUserRedeemCheck";
	}
	
	/**
	 * 活期赎回---审核处理
	 * @param wloanCurrentUserRedeem
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("redeem:wloanCurrentUserRedeem:edit")
	@RequestMapping(value = "tocheck")
	public String tocheck(WloanCurrentUserRedeem wloanCurrentUserRedeem, Model model, RedirectAttributes redirectAttributes,HttpServletRequest request) {
		if (!beanValidator(model, wloanCurrentUserRedeem)){
			return form(wloanCurrentUserRedeem, model);
		}
		logger.info("[活期赎回]审核开始");
		String[] InvestIds = wloanCurrentUserRedeemService.redeem(wloanCurrentUserRedeem,StringUtils. getRemoteAddr(request));
		logger.info("[活期赎回]审核结束");
		//更新投资合同
		String InverstId="";
		String contractPdfPath = "";
		WloanCurrentProjectInvest loanCurrentProjectInvest = new WloanCurrentProjectInvest();
		WloanCurrentProject loanCurrentProject = new WloanCurrentProject();
		if(InvestIds.length>0 && InvestIds!=null)
		{
			for(int i=0; i<InvestIds.length;i++)
			{
				InverstId = InvestIds[i];
				if(InvestIds[i].equals("暂未活期融资投资记录")){
					continue;
				}
				if(InverstId.equals("")){
					continue;
				}
				logger.info("[投资记录ID]"+InverstId);
				loanCurrentProjectInvest = wloanCurrentProjectInvestService.get(InverstId);
				logger.info("[查询活期项目投资记录]结束");
				loanCurrentProject = wloanCurrentProjectService.get(loanCurrentProjectInvest.getProjectId());
				logger.info("[查询活期项目记录]结束");
				logger.info("[生成投资合同]开始");
				contractPdfPath = createContractPdfPath(userInfoService.get(Global.getConfig("redeemUserId")), loanCurrentProject, loanCurrentProjectInvest);
				logger.info("[生成投资合同]结束"+contractPdfPath);
				//更新
				logger.info("开始更新[陈]投资合同");
				loanCurrentProjectInvest.setContractUrl(contractPdfPath);
				wloanCurrentProjectInvestService.save(loanCurrentProjectInvest);
				logger.info("更新[陈]投资合同结束");
			}
			addMessage(redirectAttributes, "活期赎回审核成功");
		}
		else
		{
			addMessage(redirectAttributes, "活期赎回审核失败,陈炜玲账户余额不足");
		}
		return "redirect:"+Global.getAdminPath()+"/redeem/wloanCurrentUserRedeem/?repage";
	}
	
	/**
	 * 生成活期投资合同（个人合同）
	 * @param userInfo
	 * @param wloanTermProject
	 * @return
	 */
	public String createContractPdfPath(UserInfo userInfo, WloanCurrentProject wloanCurrentProject, 
			WloanCurrentProjectInvest wloanCurrentProjectInvest) {
		logger.info("生成活期投资合同（个人合同）");
		// 四方合同存储路径.
		String contractPdfPath = "";
		WloanSubject wloanSubject = wloanCurrentProject.getWloanSubject();
		WGuaranteeCompany wGuaranteeCompany = wloanCurrentProject.getWgCompany();

		// 模版名称.
		String templateName = "pdf_template.pdf";
		// PDF(Key:Value).
		Map<String, String> map = new HashMap<String, String>();
		map.put("contract_no", DateUtils.getDateStr()); // 合同编号.
		logger.info("合同编号."+DateUtils.getDateStr());
		if (wloanSubject != null) { // 融资主体.
			map.put("name", wloanSubject.getCompanyName()); // 乙方（借款人）.
			logger.info("乙方（借款人）."+wloanSubject.getCompanyName());
			map.put("card_id", wloanSubject.getLoanIdCard()); // 身份证号码.
			logger.info("身份证号码."+wloanSubject.getLoanIdCard());
			map.put("bottom_name", wloanSubject.getCompanyName()); // 乙方（借款人）.
			logger.info("乙方（借款人）."+wloanSubject.getCompanyName());
		}

		if (wGuaranteeCompany != null) { // 担保机构.
			map.put("third_name", wGuaranteeCompany.getName()); // 丙方（担保人）.
			logger.info("丙方（担保人）."+wGuaranteeCompany.getName());
			map.put("legal_person", wGuaranteeCompany.getCorporation()); // 法人代表.
			logger.info("法人代表."+wGuaranteeCompany.getCorporation());
			map.put("residence", wGuaranteeCompany.getAddress()); // 住所.
			logger.info("住所."+wGuaranteeCompany.getAddress());
			map.put("telphone", wGuaranteeCompany.getPhone()); // 电话.
			logger.info("电话."+wGuaranteeCompany.getPhone());
			map.put("bottom_third_name", wGuaranteeCompany.getName()); // 丙方（担保人）.
			logger.info("丙方（担保人）."+wGuaranteeCompany.getName());
		}

		if (wloanCurrentProject != null) { // 定期融资项目.
			map.put("project_name", wloanCurrentProject.getName()); // 借款项目名称.
			logger.info("借款项目名称."+wloanCurrentProject.getName());
			map.put("project_no", wloanCurrentProject.getSn()); // 借款项目编号.
			logger.info("借款项目编号."+wloanCurrentProject.getSn());
			map.put("rmb", wloanCurrentProject.getAmount().toString()); // 借款总额.
			logger.info("借款总额."+wloanCurrentProject.getAmount().toString());
			map.put("rmd_da", PdfUtils.change(wloanCurrentProject.getAmount())); // 借款总额大写.
			logger.info("借款总额大写."+PdfUtils.change(wloanCurrentProject.getAmount()));
			map.put("uses", wloanCurrentProject.getPurpose()); // 借款用途.
			logger.info("借款用途."+wloanCurrentProject.getPurpose());
			map.put("lend_date", DateUtils.getDate(new Date(), "yyyy-MM-dd")); // 借款日期.
			logger.info("借款日期."+DateUtils.getDate(new Date(), "yyyy-MM-dd"));
			map.put("term_date", wloanCurrentProject.getSpan().toString()); // 借款期限.
			logger.info("借款期限."+wloanCurrentProject.getSpan().toString());
			map.put("back_date", ""); // 还本日期.
			map.put("year_interest", wloanCurrentProject.getAmmualRate().toString()); // 年利率.
			logger.info("年利率."+wloanCurrentProject.getAmmualRate().toString());
			map.put("interest_sum", ""); // 利息总额.

		}
		map.put("service_no", ""); // 借款服务合同编号.
		map.put("guarantee_no", ""); // 连带责任保证担保涵编号.
		map.put("sign_date", DateUtils.getDate(new Date(), "yyyy年MM月dd日")); // 签订合同日期.
		logger.info(" 签订合同日期."+DateUtils.getDate(new Date(), "yyyy年MM月dd日"));

		// 投资title.
		String title = "出借人本金利息表";
		// 投资列表Title.
		String[] rowTitle = new String[] { "投资人", "投资金额", "年利率" };
		// 投资详细信息
		ArrayList<String[]> dataList = new ArrayList<String[]>();
		String[] strings = null;
		strings = new String[rowTitle.length];
		//strings[0] = userInfo.getRealName();
		//strings[1] = String.valueOf(wloanCurrentProjectInvest.getAmount());
		//strings[2] = String.valueOf(wloanCurrentProject.getAmmualRate());
		dataList.add(strings);
		logger.info("strings[0]"+strings[0]);
		logger.info("strings[1]"+strings[1]);
		logger.info("strings[2]"+strings[2]);
		logger.info("[正式开始生成合同]");
		try {
			contractPdfPath = PdfUtils.createPdfByTemplate(templateName, map, title, rowTitle, dataList, null, null);
			logger.info("fn:createContractPdfPath,{生成活期投资合同成功}");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("fn:createContractPdfPath,{异常：" + e.getMessage() + "}");
		}
		logger.info("[生成合同结束]");
		return contractPdfPath;
	}

}