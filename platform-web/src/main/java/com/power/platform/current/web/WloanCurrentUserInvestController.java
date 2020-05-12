package com.power.platform.current.web;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
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
import com.power.platform.common.web.BaseController;
import com.power.platform.current.entity.WloanCurrentUserInvest;
import com.power.platform.current.entity.invest.WloanCurrentProjectInvest;
import com.power.platform.current.entity.moment.WloanCurrentMomentInvest;
import com.power.platform.current.service.invest.WloanCurrentProjectInvestService;
import com.power.platform.current.service.invest.WloanCurrentUserInvestService;
import com.power.platform.current.service.moment.WloanCurrentMomentInvestService;
import com.power.platform.pay.utils.LLPayUtil;

/**
 * 
 * 类: WloanCurrentUserInvestController <br>
 * 描述: 活期客户投资Controller. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年1月13日 下午4:17:27
 */
@Controller
@RequestMapping(value = "${adminPath}/wloan_current_user_invest/wloanCurrentUserInvest")
public class WloanCurrentUserInvestController extends BaseController {

	@Autowired
	private WloanCurrentUserInvestService wloanCurrentUserInvestService;

	@Autowired
	private WloanCurrentMomentInvestService wloanCurrentMomentInvestService;

	@Autowired
	private WloanCurrentProjectInvestService wloanCurrentProjectInvestService;

	@ModelAttribute
	public WloanCurrentUserInvest get(@RequestParam(required = false) String id) {

		WloanCurrentUserInvest entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = wloanCurrentUserInvestService.get(id);
		}
		if (entity == null) {
			entity = new WloanCurrentUserInvest();
		}
		entity.setCurrentUser(SessionUtils.getUser());
		return entity;
	}

	@RequiresPermissions("wloan_current_user_invest:wloanCurrentUserInvest:view")
	@RequestMapping(value = { "list", "" })
	public String list(WloanCurrentUserInvest wloanCurrentUserInvest, HttpServletRequest request, HttpServletResponse response, Model model) {

		Page<WloanCurrentUserInvest> page = wloanCurrentUserInvestService.findPage(new Page<WloanCurrentUserInvest>(request, response), wloanCurrentUserInvest);
		model.addAttribute("page", page);
		return "modules/current/wloan_current_user_invest/wloanCurrentUserInvestList";
	}

	@RequiresPermissions("wloan_current_user_invest:wloanCurrentUserInvest:view")
	@RequestMapping(value = "moneyFlows")
	public String moneyFlows(WloanCurrentUserInvest wloanCurrentUserInvest, HttpServletRequest request, HttpServletResponse response, Model model) {

		// 客户投资记录表ID.
		String userInvest = wloanCurrentUserInvest.getId();

		/**
		 * 客户活期投资剩余金额临时存储表.
		 */
		WloanCurrentMomentInvest wloanCurrentMomentInvest = new WloanCurrentMomentInvest();
		wloanCurrentMomentInvest.setUserInvest(userInvest);
		Page<WloanCurrentMomentInvest> momentInvests = wloanCurrentMomentInvestService.findPage(new Page<WloanCurrentMomentInvest>(), wloanCurrentMomentInvest);
		model.addAttribute("momentInvests", momentInvests);

		/**
		 * 客户活期项目实际投资记录列表.
		 */
		WloanCurrentProjectInvest wloanCurrentProjectInvest = new WloanCurrentProjectInvest();
		wloanCurrentProjectInvest.setUserInvest(userInvest);
		Page<WloanCurrentProjectInvest> projectInvests = wloanCurrentProjectInvestService.findPage(new Page<WloanCurrentProjectInvest>(), wloanCurrentProjectInvest);
		model.addAttribute("projectInvests", projectInvests);

		return "modules/current/wloan_current_user_invest/wloanCurrentUserInvestMoneyFlows";
	}

	/**
	 * 
	 * 方法: addForm <br>
	 * 描述: 添加表单. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年1月13日 下午4:45:28
	 * 
	 * @param wloanCurrentUserInvest
	 * @param model
	 * @return
	 */
	@RequiresPermissions("wloan_current_user_invest:wloanCurrentUserInvest:view")
	@RequestMapping(value = "addForm")
	public String addForm(WloanCurrentUserInvest wloanCurrentUserInvest, Model model) {

		wloanCurrentUserInvest.setRemarks("活期投资");
		model.addAttribute("wloanCurrentUserInvest", wloanCurrentUserInvest);
		return "modules/current/wloan_current_user_invest/wloanCurrentUserInvestAddForm";
	}

	/**
	 * 
	 * 方法: updateForm <br>
	 * 描述: 更新表单. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年1月13日 下午4:45:41
	 * 
	 * @param wloanCurrentUserInvest
	 * @param model
	 * @return
	 */
	@RequiresPermissions("wloan_current_user_invest:wloanCurrentUserInvest:view")
	@RequestMapping(value = "updateForm")
	public String updateForm(WloanCurrentUserInvest wloanCurrentUserInvest, Model model) {

		model.addAttribute("wloanCurrentUserInvest", wloanCurrentUserInvest);
		return "modules/current/wloan_current_user_invest/wloanCurrentUserInvestUpdateForm";
	}

	/**
	 * 
	 * 方法: viewForm <br>
	 * 描述: 查看表单. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年1月13日 下午4:56:55
	 * 
	 * @param wloanCurrentUserInvest
	 * @param model
	 * @return
	 */
	@RequiresPermissions("wloan_current_user_invest:wloanCurrentUserInvest:view")
	@RequestMapping(value = "viewForm")
	public String viewForm(WloanCurrentUserInvest wloanCurrentUserInvest, Model model) {

		model.addAttribute("wloanCurrentUserInvest", wloanCurrentUserInvest);
		return "modules/current/wloan_current_user_invest/wloanCurrentUserInvestViewForm";
	}

	/**
	 * 
	 * 方法: addSave <br>
	 * 描述: 添加保存. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年1月13日 下午4:45:54
	 * 
	 * @param wloanCurrentUserInvest
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("wloan_current_user_invest:wloanCurrentUserInvest:edit")
	@RequestMapping(value = "addSave")
	public String addSave(HttpServletRequest request, WloanCurrentUserInvest wloanCurrentUserInvest, Model model, RedirectAttributes redirectAttributes) {

		// 客户投资记录初始化.
		wloanCurrentUserInvest.setBidDate(new Date()); // 投资日期.
		wloanCurrentUserInvest.setState(WloanCurrentUserInvestService.WLOAN_CURRENT_USER_INVEST_STATE_1); // 数据状态，待投资.
		wloanCurrentUserInvest.setBidState(WloanCurrentUserInvestService.WLOAN_CURRENT_USER_INVEST_BID_STATE_1); // 投资状态，投资成功.
		wloanCurrentUserInvest.setIp(LLPayUtil.getIpAddr(request)); // 客户IP地址.
		wloanCurrentUserInvest.setOnLineAmount(wloanCurrentUserInvest.getAmount()); // 在投金额，用于计算利息及赎回.

		wloanCurrentUserInvestService.save(wloanCurrentUserInvest);
		addMessage(redirectAttributes, "保存活期客户投资成功");
		return "redirect:" + Global.getAdminPath() + "/wloan_current_user_invest/wloanCurrentUserInvest/?repage";
	}

	/**
	 * 
	 * 方法: updateSave <br>
	 * 描述: 更新保存. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年1月13日 下午4:46:08
	 * 
	 * @param wloanCurrentUserInvest
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("wloan_current_user_invest:wloanCurrentUserInvest:edit")
	@RequestMapping(value = "updateSave")
	public String updateSave(WloanCurrentUserInvest wloanCurrentUserInvest, Model model, RedirectAttributes redirectAttributes) {

		wloanCurrentUserInvestService.save(wloanCurrentUserInvest);
		addMessage(redirectAttributes, "更新活期客户投资成功");
		return "redirect:" + Global.getAdminPath() + "/wloan_current_user_invest/wloanCurrentUserInvest/?repage";
	}

	@RequiresPermissions("wloan_current_user_invest:wloanCurrentUserInvest:edit")
	@RequestMapping(value = "delete")
	public String delete(WloanCurrentUserInvest wloanCurrentUserInvest, RedirectAttributes redirectAttributes) {

		wloanCurrentUserInvestService.delete(wloanCurrentUserInvest);
		addMessage(redirectAttributes, "删除活期客户投资成功");
		return "redirect:" + Global.getAdminPath() + "/wloan_current_user_invest/wloanCurrentUserInvest/?repage";
	}

}