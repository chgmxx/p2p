package com.power.platform.proapproval;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.power.platform.common.config.Global;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.security.shiro.session.SessionUtils;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.utils.excel.ExportExcel;
import com.power.platform.common.web.BaseController;
import com.power.platform.proapproval.entity.ProjectApproval;
import com.power.platform.proapproval.service.ProjectApprovalService;
import com.power.platform.regular.service.WloanTermProjectService;
import com.power.platform.sms.service.SendSmsService;
import com.power.platform.sys.entity.User;
import com.power.platform.sys.service.SystemService;

/**
 * 定期项目审批信息Controller
 * 
 * @author jiajunfeng
 * @version 2016-08-17
 */

@Controller
@RequestMapping(value = "${adminPath}/approval/proinfo")
public class ProjectApprovalController extends BaseController {

	@Autowired
	private ProjectApprovalService projectApprovalService;

	@Autowired
	private WloanTermProjectService wloanTermProjectService;
	
	@Autowired
	private SendSmsService sendSmsService;

	@Autowired
	private SystemService systemService;

	@ModelAttribute
	public ProjectApproval get(@RequestParam(required = false) String id, @RequestParam(required = false) String projectid) {

		ProjectApproval entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = projectApprovalService.get(id);
		}
		if (entity == null) {
			entity = new ProjectApproval();
			if (StringUtils.isNotBlank(projectid)) {
				entity.setWloanTermProject(wloanTermProjectService.get(projectid));
				entity = projectApprovalService.getByProjectId(entity);
				if (entity == null) {
					entity = new ProjectApproval();
					entity.setWloanTermProject(wloanTermProjectService.get(projectid));
				}
			}
		}
		return entity;
	}

	@RequiresPermissions("projectApproval:projectApproval:view")
	@RequestMapping(value = { "list", "" })
	public String list(ProjectApproval projectApproval, HttpServletRequest request, HttpServletResponse response, Model model) {

		Page<ProjectApproval> page = projectApprovalService.findPage(new Page<ProjectApproval>(request, response), projectApproval);
		model.addAttribute("page", page);
		model.addAttribute("usertype", SessionUtils.getUser().getUserType());
		return "modules/proapproval/proapprovalList";
	}

	@RequiresPermissions("projectApproval:projectApproval:view")
	@RequestMapping(value = "exportProjectApprovalInfo", method = RequestMethod.POST)
	public String exportProjectInfoFile(ProjectApproval projectApproval, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {

		try {
			String fileName = "项目审批信息" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
			List<ProjectApproval> list = projectApprovalService.findList(projectApproval);
			new ExportExcel("项目审批信息", ProjectApproval.class).setDataList(list).write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出项目审批信息失败！失败信息：" + e.getMessage());
		}

		return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProject/?repage";
	}

	@RequiresPermissions("projectApproval:projectApproval:view")
	@RequestMapping(value = "form")
	public String form(ProjectApproval projectApproval, Model model) {

		model.addAttribute("projectApproval", projectApproval);
		model.addAttribute("usertype", SessionUtils.getUser().getUserType());

		String url = "";

		if (projectApproval.getState() == null || "".equals(projectApproval.getState())) {
			url = "modules/proapproval/proapprovalForm";
		}

		else if ("1".equals(projectApproval.getState())) {
			model.addAttribute("rcerUserName", systemService.getUser(projectApproval.getRcerUser()).getName());
			url = "modules/proapproval/proapprovalFormRclerk";
		}

		else if ("2".equals(projectApproval.getState())) {
			model.addAttribute("rcerUserName", systemService.getUser(projectApproval.getRcerUser()).getName());
			model.addAttribute("rclerkUserName", systemService.getUser(projectApproval.getRclerkUser()).getName());
			url = "modules/proapproval/proapprovalFormfinance";
		}

		else if ("6".equals(projectApproval.getState())) {
			model.addAttribute("rcerUserName", systemService.getUser(projectApproval.getRcerUser()).getName());
			model.addAttribute("rclerkUserName", systemService.getUser(projectApproval.getRclerkUser()).getName());
			model.addAttribute("financeUserName", systemService.getUser(projectApproval.getFinanceUser()).getName());
			url = "modules/proapproval/proapprovalFormrcermanager";
		}
		else if ("3".equals(projectApproval.getState())) {
			model.addAttribute("rcerUserName", systemService.getUser(projectApproval.getRcerUser()).getName());
			model.addAttribute("rclerkUserName", systemService.getUser(projectApproval.getRclerkUser()).getName());
			model.addAttribute("financeUserName", systemService.getUser(projectApproval.getFinanceUser()).getName());
			model.addAttribute("rcerManagerUserName", systemService.getUser(projectApproval.getRcerManagerUser()).getName());
			url = "modules/proapproval/proapprovalFormadmin";
		}
		else if ("4".equals(projectApproval.getState()) || "5".equals(projectApproval.getState())) {
			model.addAttribute("rcerUserName", systemService.getUser(projectApproval.getRcerUser()).getName());
			model.addAttribute("rclerkUserName", systemService.getUser(projectApproval.getRclerkUser()).getName());
			model.addAttribute("financeUserName", systemService.getUser(projectApproval.getFinanceUser()).getName());
			model.addAttribute("rcerManagerUserName", systemService.getUser(projectApproval.getRcerManagerUser()).getName());
			model.addAttribute("adminUserName", systemService.getUser(projectApproval.getAdminUser()).getName());
			url = "modules/proapproval/proapprovalFormrepay";
		}

		return url;
	}

	@RequiresPermissions("projectApproval:projectApproval:edit")
	@RequestMapping(value = "save")
	public String save(ProjectApproval projectApproval, Model model, RedirectAttributes redirectAttributes) {

		if (!beanValidator(model, projectApproval)) {
			return form(projectApproval, model);
		}
		
		try {
			// 需要发送的手机号
			List<String> mobiles = new ArrayList<String>();
			
			if (projectApproval.getState() == null || "".equals(projectApproval.getState())) {
				projectApproval.setState("1");
				projectApproval.setRcerUser(SessionUtils.getUser().getId());
				mobiles = getMobile("6");			// 下一级审批人员类型为 6
			}

			else if ("1".equals(projectApproval.getState())) {
				projectApproval.setRclerkUpdateDate(new Date());
				projectApproval.setState("2");
				projectApproval.setRclerkUser(SessionUtils.getUser().getId());
				mobiles = getMobile("7");			// 下一级审批人员类型为 7
			}

			else if ("2".equals(projectApproval.getState())) {
				projectApproval.setFinanceUpdateDate(new Date());
				projectApproval.setState("6");
				projectApproval.setFinanceUser(SessionUtils.getUser().getId());
				mobiles = getMobile("9");			// 下一级审批人员类型为9 风控经理
			}

			else if ("6".equals(projectApproval.getState())) {
				projectApproval.setRcerManagerUpdateDate(new Date());
				projectApproval.setState("3");
				projectApproval.setRcerManagerUser(SessionUtils.getUser().getId());
				mobiles = getMobile("1");			// 下一级审批人员位 admin
			}

			else if ("3".equals(projectApproval.getState())) {
				projectApproval.setState("4");
				projectApproval.setAdminUser(SessionUtils.getUser().getId());
				mobiles = getMobile("7");			// 下一级审批人员类型为 7
			}

			projectApproval.setRefuseUser("");
			projectApprovalService.save(projectApproval);
			addMessage(redirectAttributes, "放款申请信息审批成功");
			
			// 发送审批短信消息
			if(mobiles != null && mobiles.size() > 0){
				for (int i = 0; i < mobiles.size(); i++) {
					sendSmsService.directSendSMS(mobiles.get(i), "您好,项目\"" + wloanTermProjectService.get(projectApproval.getWloanTermProject().getId()).getName() + "\"有一笔新的放款申请,请登录管理后台进行审批。");
				}
			}
			
		} catch (Exception e) {
			addMessage(redirectAttributes, "放款申请信息审批失败");
			logger.info(e.getMessage());
		}
		
		return "redirect:" + Global.getAdminPath() + "/approval/proinfo/?repage";
	}

	
	private List<String> getMobile(String userType){
		List<String> result = new ArrayList<String>();
		User user = new User();
		// 查询 admin 用户
		if(userType.equals("1")){
			user = systemService.getUser("1");
			logger.info(user.getName() + "----------------------------------------------------------------------------");
			result.add(user.getMobile());
			return result;
		}

		// 查询其他用户
		List<User> users = systemService.findAllUser();
		for (int i = 0; i < users.size(); i++) {
			user = users.get(i);
			if(user.getUserType().equals(userType)){
				logger.info(user.getName() + "----------------------------------------------------------------------------");
				result.add(user.getMobile());
			}
		}
		return result;
	}
	
	
	@RequiresPermissions("projectApproval:projectApproval:edit")
	@RequestMapping(value = "refuse")
	public String refuse(ProjectApproval projectApproval, RedirectAttributes redirectAttributes) {
		try {
			List<String> mobiles = new ArrayList<String>();
			projectApproval.setState("");
			projectApproval.setRefuseUser(SessionUtils.getUser().getId());
			projectApproval.setRefuseDate(new Date());
			projectApprovalService.save(projectApproval);
			mobiles = getMobile("5");			// 审批信息被拒绝返回到专员，用户类型为 5
			// 发送审批短信消息
			if(mobiles != null && mobiles.size() > 0){
				for (int i = 0; i < mobiles.size(); i++) {
					sendSmsService.directSendSMS(mobiles.get(i), "您好,项目\"" + wloanTermProjectService.get(projectApproval.getWloanTermProject().getId()).getName() + "\"放款申请信息已被拒绝,请登录管理后台查看具体信息。");
				}
			}
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
		
		addMessage(redirectAttributes, "拒绝放款申请信息成功");
		return "redirect:" + Global.getAdminPath() + "/approval/proinfo/?repage";
	}

	@RequiresPermissions("projectApproval:projectApproval:edit")
	@RequestMapping(value = "delete")
	public String delete(ProjectApproval projectApproval, RedirectAttributes redirectAttributes) {

		projectApprovalService.delete(projectApproval);
		addMessage(redirectAttributes, "删除放款申请信息成功");
		return "redirect:" + Global.getAdminPath() + "/approval/proinfo/?repage";
	}

}