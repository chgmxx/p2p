package com.power.platform.userinfo.web;

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

import com.power.platform.cache.Cache;
import com.power.platform.common.config.Global;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.web.BaseController;
import com.power.platform.common.utils.CommonStringUtils;
import com.power.platform.common.utils.MemCachedUtis;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.sms.entity.SmsMsgHistory;
import com.power.platform.sms.entity.SmsRejectHistory;
import com.power.platform.sms.service.SmsRejectService;
import com.power.platform.sms.service.SmsSendMsgService;

/**
 * 
 * class: UserSmsHistoryController <br>
 * description: 用户验证码查询列表 <br>
 * author: Mr.Roy <br>
 * date: 2018年12月9日 上午11:16:02
 */
@Controller
@RequestMapping(value = "${adminPath}/usersms")
public class UserSmsHistoryController extends BaseController {

	@Autowired
	private SmsSendMsgService smsSendMsgService;
	@Autowired
	private SmsRejectService smsRejectService;

	@ModelAttribute
	public SmsMsgHistory get(@RequestParam(required = false) String id) {

		SmsMsgHistory entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = smsSendMsgService.get(id);
		}
		if (entity == null) {
			entity = new SmsMsgHistory();
		}
		return entity;
	}

	@RequiresPermissions("usersms:usersms:view")
	@RequestMapping(value = { "list", "" })
	public String list(SmsMsgHistory smsMsgHistory, HttpServletRequest request, HttpServletResponse response, Model model) {

		Page<SmsMsgHistory> page = smsSendMsgService.findPage(new Page<SmsMsgHistory>(request, response), smsMsgHistory);
		List<SmsMsgHistory> list = page.getList();
		for (int i = 0; i < list.size(); i++) {
			SmsMsgHistory entity = list.get(i);
			entity.setPhone(CommonStringUtils.mobileEncrypt(entity.getPhone()));
		}

		model.addAttribute("page", page);
		return "modules/msgcode/userMsgHistoryList";
	}

	/**
	 * 被屏蔽手机号列表
	 * 
	 * @param smsRejectHistory
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequiresPermissions("usersms:usersms:view")
	@RequestMapping(value = "rejectList")
	public String rejectList(SmsRejectHistory smsRejectHistory, HttpServletRequest request, HttpServletResponse response, Model model) {

		Page<SmsRejectHistory> page = smsRejectService.findPage(new Page<SmsRejectHistory>(request, response), smsRejectHistory);
		model.addAttribute("page", page);
		return "modules/msgcode/userMsgRejectList";
	}

	@RequiresPermissions("usersms:usersms:edit")
	@RequestMapping(value = "rejectDelete")
	public String rejectDelete(SmsRejectHistory smsRejectHistory, RedirectAttributes redirectAttributes) {

		// 缓存中保存客户短信验证码.
		Cache smCache;
		try {
			smsRejectHistory = smsRejectService.get(smsRejectHistory.getId());
			smCache = MemCachedUtis.getMemCached();
			if (StringUtils.isNotBlank(smsRejectHistory.getType())) {
				if (smsRejectHistory.getType() == "0" || "0".equals(smsRejectHistory.getType())) {
					smCache.delete(smsRejectHistory.getPhone() + "times");
				} else {
					smCache.delete(smsRejectHistory.getIp());
				}
			}
			smsRejectService.delete(smsRejectHistory);
			addMessage(redirectAttributes, "解除手机号屏蔽成功");
			return "redirect:" + Global.getAdminPath() + "/usersms/rejectList?repage";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:" + Global.getAdminPath() + "/usersms/rejectList?repage";
	}

	@RequiresPermissions("usersms:usersms:view")
	@RequestMapping(value = "form")
	public String form(SmsMsgHistory smsMsgHistory, Model model) {

		model.addAttribute("SmsMsgHistory", smsMsgHistory);
		return "modules/msgcode/userMsgHistoryForm";
	}

	@RequiresPermissions("usersms:usersms:edit")
	@RequestMapping(value = "save")
	public String save(SmsMsgHistory smsMsgHistory, Model model, RedirectAttributes redirectAttributes) {

		if (!beanValidator(model, smsMsgHistory)) {
			return form(smsMsgHistory, model);
		}
		smsSendMsgService.save(smsMsgHistory);
		addMessage(redirectAttributes, "保存用户信息管理成功");
		return "redirect:" + Global.getAdminPath() + "/usersms/?repage";
	}

	@RequiresPermissions("usersms:usersms:edit")
	@RequestMapping(value = "delete")
	public String delete(SmsMsgHistory smsMsgHistory, RedirectAttributes redirectAttributes) {

		smsSendMsgService.delete(smsMsgHistory);
		addMessage(redirectAttributes, "删除用户信息管理成功");
		return "redirect:" + Global.getAdminPath() + "/usersms/?repage";
	}

}