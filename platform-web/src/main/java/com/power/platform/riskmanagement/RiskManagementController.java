/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.riskmanagement;

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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.power.platform.common.config.Global;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.security.shiro.session.SessionUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.utils.UploadUtils;
import com.power.platform.common.web.BaseController;
import com.power.platform.regular.entity.WloanTermDoc;
import com.power.platform.regular.service.WloanTermDocService;
import com.power.platform.riskmanagement.entity.RiskManagement;
import com.power.platform.riskmanagement.service.RiskManagementService;
import com.power.platform.sys.entity.AnnexFile;
import com.power.platform.sys.service.AnnexFileService;


/**
 * 风控企业信息Controller
 * @author yb
 * @version 2016-10-11
 */
@Controller
@RequestMapping(value = "${adminPath}/riskmanagement/riskManagementMessage")
public class RiskManagementController extends BaseController {

	@Autowired
	private RiskManagementService riskManagementService;
	/**
	 * 附件Service.
	 */
	@Autowired
	private AnnexFileService annexFileService;
	
	@ModelAttribute
	public RiskManagement get(@RequestParam(required=false) String id) {
		RiskManagement entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = riskManagementService.get(id);
		}
		if (entity == null){
			entity = new RiskManagement();
		}
		return entity;
	}
	
	@RequiresPermissions("riskmanagement:riskManagementMessage:view")
	@RequestMapping(value = {"list", ""})
	public String list(RiskManagement riskManagement, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<RiskManagement> page = riskManagementService.findPage(new Page<RiskManagement>(request, response), riskManagement); 
		model.addAttribute("page", page);
		model.addAttribute("usertype", SessionUtils.getUser().getUserType());
		return "modules/riskmanagement/riskManagementList";
	}

	@RequiresPermissions("riskmanagement:riskManagementMessage:view")
	@RequestMapping(value = "form")
	public String form(RiskManagement riskManagement, Model model) {
		model.addAttribute("riskManagement", riskManagement);
		model.addAttribute("usertype", SessionUtils.getUser().getUserType());
		return "modules/riskmanagement/riskManagementForm";
	}

	
	@RequiresPermissions("riskmanagement:riskManagementMessage:edit")
	@RequestMapping(value = "delete")
	public String delete(RiskManagement riskManagement, RedirectAttributes redirectAttributes) {
		riskManagementService.delete(riskManagement);
		addMessage(redirectAttributes, "删除风控企业信息成功");
		return "redirect:"+Global.getAdminPath()+"/riskmanagement/riskManagementMessage/?repage";
	}

	/**
	 * 保存企业信息
	 * @param riskManagement
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("riskmanagement:riskManagementMessage:edit")
	@RequestMapping(value = "save")
	public String save(RiskManagement riskManagement, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, riskManagement)){
			return form(riskManagement, model);
		}
		riskManagement.setState(riskManagementService.STATE_1);
		riskManagementService.save(riskManagement);
		addMessage(redirectAttributes, "保存风控企业信息成功");
		return "redirect:"+Global.getAdminPath()+"/riskmanagement/riskManagementMessage/?repage";
	}
	
	/**
	 * 风控档案管理
	 * @param riskManagement
	 * @param model
	 * @return
	 */
	@RequiresPermissions("riskmanagement:riskManagementMessage:view")
	@RequestMapping(value = "formControl")
	public String formControl(RiskManagement riskManagement, Model model) {
		
		//风控信息档案表
		riskManagement.setDocUrl(riskManagement.getDocUrl().replace("|", ""));
		model.addAttribute("riskManagement", riskManagement);
		model.addAttribute("usertype", SessionUtils.getUser().getUserType());
		//附件表
		AnnexFile annexFile = new AnnexFile();
		annexFile.setDictType(RiskManagementService.RISK_MANAGEMENT); // 资料类别.
		annexFile.setType(RiskManagementService.RISK_MANAGEMENT_TYPE0);//风控档案
		annexFile.setOtherId(riskManagement.getId()); // 风控档案主键ID.
		annexFile.setTitle(riskManagement.getCompanyName()); // 名称.
		annexFile.setReturnUrl("/riskmanagement/riskManagementMessage/formControl?id=" + riskManagement.getId()); // 回调URL.
		model.addAttribute("annexFile", annexFile);
		//风控信息列表
		List<AnnexFile> annexFiles = annexFileService.findAnnexFilesByWloanTermDoc(annexFile);
		for (AnnexFile aFile : annexFiles) {
			aFile.setDictType(RiskManagementService.RISK_MANAGEMENT); // 资料类别.
			aFile.setOtherId(riskManagement.getId()); // 风控档案主键ID.
			aFile.setTitle(riskManagement.getCompanyName()); // 名称.
			aFile.setReturnUrl("/riskmanagement/riskManagementMessage/formControl?id=" + riskManagement.getId()); // 回调URL.
		}
		model.addAttribute("annexFiles", annexFiles);
		
		return "modules/riskmanagement/riskManagementControl";
	}
	
	/**
	 * 审批跳转页面
	 * @param riskManagement
	 * @param model
	 * @return
	 */
	@RequiresPermissions("riskmanagement:riskManagementMessage:view")
	@RequestMapping(value = "formCheck")
	public String formCheck(RiskManagement riskManagement, Model model) {
		model.addAttribute("riskManagement", riskManagement);
		model.addAttribute("usertype", SessionUtils.getUser().getUserType());
		model.addAttribute("username", SessionUtils.getUser().getName());
		return "modules/riskmanagement/riskManagementFormCheck";
	}
	
	
	/**
	 * 审核通过
	 * @param riskManagement
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("riskmanagement:riskManagementMessage:edit")
	@RequestMapping(value = "pass")
	public String pass(RiskManagement riskManagement, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request) {
		if (!beanValidator(model, riskManagement)){
			return form(riskManagement, model);
		}
		//填写意见
		String note = request.getParameter("checkNote");
		
		//风控专员
		if(SessionUtils.getUser().getUserType().equals("5")){
			riskManagement.setCheckUser1(SessionUtils.getUser().getName());
			riskManagement.setState(riskManagementService.STATE_2);
			riskManagement.setCheckNote1(note);
			riskManagement.setCheckDate1(new Date());
		}
		//风控经理
		else if(SessionUtils.getUser().getUserType().equals("9") && riskManagement.getCheckUser1() != null){
			riskManagement.setCheckUser2(SessionUtils.getUser().getName());
			riskManagement.setState(riskManagementService.STATE_3);
			riskManagement.setCheckNote2(note);
			riskManagement.setCheckDate2(new Date());
		}
		//总经理
		else if(SessionUtils.getUser().getUserType().equals("1") && riskManagement.getCheckUser2() != null){
			riskManagement.setCheckUser3(SessionUtils.getUser().getName());
			riskManagement.setState(riskManagementService.STATE_4);
			riskManagement.setCheckNote3(note);
			riskManagement.setCheckDate3(new Date());
		}
		riskManagementService.save(riskManagement);
		addMessage(redirectAttributes, "[风控企业信息]审批通过");
		return "redirect:"+Global.getAdminPath()+"/riskmanagement/riskManagementMessage/?repage";
	}
	
	/**
	 * 审核拒绝
	 * @param riskManagement
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("riskmanagement:riskManagementMessage:edit")
	@RequestMapping(value = "refuse")
	public String refuse(RiskManagement riskManagement, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request) {
		if (!beanValidator(model, riskManagement)){
			return form(riskManagement, model);
		}
		//填写意见
		String note = request.getParameter("checkNote");
		
		riskManagement.setState(riskManagementService.STATE_0);
		//风控专员
		if(SessionUtils.getUser().getUserType().equals("5")){
			riskManagement.setCheckUser1(SessionUtils.getUser().getName());
			riskManagement.setCheckNote1(note);
			riskManagement.setCheckDate1(new Date());
		}
		//风控经理
		else if(SessionUtils.getUser().getUserType().equals("9") && riskManagement.getCheckUser1() != null){
			riskManagement.setCheckUser2(SessionUtils.getUser().getName());
			riskManagement.setCheckNote2(note);
			riskManagement.setCheckDate2(new Date());
		}
		//总经理
		else if(SessionUtils.getUser().getUserType().equals("1") && riskManagement.getCheckUser2() != null){
			riskManagement.setCheckUser3(SessionUtils.getUser().getName());
			riskManagement.setCheckNote3(note);
			riskManagement.setCheckDate3(new Date());
		}
		riskManagementService.save(riskManagement);
		addMessage(redirectAttributes, "[风控企业信息]审批拒绝");
		return "redirect:"+Global.getAdminPath()+"/riskmanagement/riskManagementMessage/?repage";
	}
	
	/**
	 * 提交跳转页面
	 * @param riskManagement
	 * @param model
	 * @return
	 */
	@RequiresPermissions("riskmanagement:riskManagementMessage:view")
	@RequestMapping(value = "formApproval")
	public String formApproval(RiskManagement riskManagement, Model model) {
		model.addAttribute("riskManagement", riskManagement);
		model.addAttribute("usertype", SessionUtils.getUser().getUserType());
		model.addAttribute("username", SessionUtils.getUser().getName());
		return "modules/riskmanagement/riskManagementFormApproval";
	}
	
	/**
	 * 审批情况详情页面
	 * @param riskManagement
	 * @param model
	 * @return
	 */
	@RequiresPermissions("riskmanagement:riskManagementMessage:view")
	@RequestMapping(value = "checkMessage")
	public String checkMessage(RiskManagement riskManagement, Model model) {
		model.addAttribute("riskManagement", riskManagement);
		model.addAttribute("usertype", SessionUtils.getUser().getUserType());
		model.addAttribute("username", SessionUtils.getUser().getName());
		return "modules/riskmanagement/riskManagementCheckMessage";
	}
	
	/**
	 * 签约
	 * @param riskManagement
	 * @param model
	 * @return
	 */
	@RequiresPermissions("riskmanagement:riskManagementMessage:view")
	@RequestMapping(value = "sign")
	public String sign(RiskManagement riskManagement, Model model) {
		
		//风控信息档案表
		model.addAttribute("riskManagement", riskManagement);
		model.addAttribute("usertype", SessionUtils.getUser().getUserType());
		//附件表
		AnnexFile annexFile = new AnnexFile();
		annexFile.setDictType(RiskManagementService.RISK_MANAGEMENT); // 资料类别.
		annexFile.setType(RiskManagementService.RISK_MANAGEMENT_TYPE1);//签约档案
		annexFile.setOtherId(riskManagement.getId()); // 风控档案主键ID.
		annexFile.setTitle(riskManagement.getCompanyName()); // 名称.
		annexFile.setReturnUrl("/riskmanagement/riskManagementMessage/sign?id=" + riskManagement.getId()); // 回调URL.
		model.addAttribute("annexFile", annexFile);
		//风控信息列表
		List<AnnexFile> annexFiles = annexFileService.findAnnexFilesByWloanTermDoc(annexFile);
		for (AnnexFile aFile : annexFiles) {
			aFile.setDictType(RiskManagementService.RISK_MANAGEMENT); // 资料类别.
			aFile.setOtherId(riskManagement.getId()); // 风控档案主键ID.
			aFile.setTitle(riskManagement.getCompanyName()); // 名称.
			aFile.setReturnUrl("/riskmanagement/riskManagementMessage/sign?id=" + riskManagement.getId()); // 回调URL.
		}
		model.addAttribute("annexFiles", annexFiles);
		
		return "modules/riskmanagement/riskManagementSign";
	}

}