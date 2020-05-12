package com.power.platform.activity.web;

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

import com.power.platform.activity.entity.ZtmgWechatReturningCash;
import com.power.platform.activity.service.ZtmgWechatReturningCashService;
import com.power.platform.common.utils.CommonStringUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.web.BaseController;
import com.power.platform.common.config.Global;
import com.power.platform.common.persistence.Page;

/**
 * 
 * class: ZtmgWechatReturningCashController <br>
 * description: 微信返现Controller <br>
 * author: Mr.Roy <br>
 * date: 2018年12月9日 上午11:13:31
 */
@Controller
@RequestMapping(value = "${adminPath}/activity/ztmgWechatReturningCash")
public class ZtmgWechatReturningCashController extends BaseController {

	@Autowired
	private ZtmgWechatReturningCashService ztmgWechatReturningCashService;

	@ModelAttribute
	public ZtmgWechatReturningCash get(@RequestParam(required = false) String id) {

		ZtmgWechatReturningCash entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = ztmgWechatReturningCashService.get(id);
		}
		if (entity == null) {
			entity = new ZtmgWechatReturningCash();
		}
		return entity;
	}

	@RequiresPermissions("activity:ztmgWechatReturningCash:view")
	@RequestMapping(value = { "list", "" })
	public String list(ZtmgWechatReturningCash ztmgWechatReturningCash, HttpServletRequest request, HttpServletResponse response, Model model) {

		Page<ZtmgWechatReturningCash> page = ztmgWechatReturningCashService.findPage(new Page<ZtmgWechatReturningCash>(request, response), ztmgWechatReturningCash);
		List<ZtmgWechatReturningCash> list = page.getList();
		for (int i = 0; i < list.size(); i++) {
			ZtmgWechatReturningCash entity = list.get(i);
			entity.setMobilePhone(CommonStringUtils.mobileEncrypt(entity.getMobilePhone()));
			entity.setRealName(CommonStringUtils.replaceNameX(entity.getRealName()));
		}
		model.addAttribute("page", page);
		return "modules/activity/wechatCash/ztmgWechatReturningCashList";
	}

	@RequiresPermissions("activity:ztmgWechatReturningCash:view")
	@RequestMapping(value = "form")
	public String form(ZtmgWechatReturningCash ztmgWechatReturningCash, Model model) {

		model.addAttribute("ztmgWechatReturningCash", ztmgWechatReturningCash);
		return "modules/activity/wechatCash/ztmgWechatReturningCashForm";
	}

	@RequiresPermissions("activity:ztmgWechatReturningCash:edit")
	@RequestMapping(value = "save")
	public String save(ZtmgWechatReturningCash ztmgWechatReturningCash, Model model, RedirectAttributes redirectAttributes) {

		if (!beanValidator(model, ztmgWechatReturningCash)) {
			return form(ztmgWechatReturningCash, model);
		}
		ztmgWechatReturningCashService.save(ztmgWechatReturningCash);
		addMessage(redirectAttributes, "保存微信返现成功");
		return "redirect:" + Global.getAdminPath() + "/activity/ztmgWechatReturningCash/?repage";
	}

	@RequiresPermissions("activity:ztmgWechatReturningCash:edit")
	@RequestMapping(value = "delete")
	public String delete(ZtmgWechatReturningCash ztmgWechatReturningCash, RedirectAttributes redirectAttributes) {

		ztmgWechatReturningCashService.delete(ztmgWechatReturningCash);
		addMessage(redirectAttributes, "删除微信返现成功");
		return "redirect:" + Global.getAdminPath() + "/activity/ztmgWechatReturningCash/?repage";
	}

	/**
	 * 批量微信返现
	 * 
	 * @param ztmgWechatReturningCash
	 * @param model
	 * @return
	 */
	@RequiresPermissions("activity:ztmgWechatReturningCash:view")
	@RequestMapping(value = "formall")
	public String formAll(ZtmgWechatReturningCash ztmgWechatReturningCash, Model model) {

		model.addAttribute("ztmgWechatReturningCash", ztmgWechatReturningCash);
		return "modules/activity/wechatCash/ztmgWechatReturningCashAllForm";
	}

	/**
	 * 批量微信返现
	 */
	@RequiresPermissions("activity:ztmgWechatReturningCash:edit")
	@RequestMapping(value = "saveall")
	public String saveAllweChat(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {

		String message = ztmgWechatReturningCashService.upload(file);
		addMessage(redirectAttributes, message);
		return "redirect:" + Global.getAdminPath() + "/activity/ztmgWechatReturningCash/?repage";
	}

}