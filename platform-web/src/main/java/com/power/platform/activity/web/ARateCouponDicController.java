package com.power.platform.activity.web;

import java.util.Date;
import java.util.List;

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

import com.power.platform.activity.entity.ARateCouponDic;
import com.power.platform.activity.service.ARateCouponDicService;
import com.power.platform.common.config.Global;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.security.shiro.session.SessionUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.web.BaseController;

/**
 * 
 * 类: ARateCouponDicController <br>
 * 描述: 加息券字典管理Controller. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年4月18日 上午10:49:18
 */
@Controller
@RequestMapping(value = "${adminPath}/activity/aRateCouponDic")
public class ARateCouponDicController extends BaseController {

	@Autowired
	private ARateCouponDicService aRateCouponDicService;

	@ModelAttribute
	public ARateCouponDic get(@RequestParam(required = false) String id) {

		ARateCouponDic entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = aRateCouponDicService.get(id);
		}
		if (entity == null) {
			entity = new ARateCouponDic();
		}
		return entity;
	}

	@RequiresPermissions("activity:aRateCouponDic:view")
	@RequestMapping(value = { "list", "" })
	public String list(ARateCouponDic aRateCouponDic, HttpServletRequest request, HttpServletResponse response, Model model) {

		Page<ARateCouponDic> page = aRateCouponDicService.findPage(new Page<ARateCouponDic>(request, response), aRateCouponDic);
		model.addAttribute("page", page);
		return "modules/activity/rate_coupon_dic/aRateCouponDicList";
	}

	@RequiresPermissions("activity:aRateCouponDic:view")
	@RequestMapping(value = "addForm")
	public String addForm(ARateCouponDic aRateCouponDic, Model model) {

		model.addAttribute("aRateCouponDic", aRateCouponDic);
		return "modules/activity/rate_coupon_dic/aRateCouponDicAddForm";
	}

	@RequiresPermissions("activity:aRateCouponDic:edit")
	@RequestMapping(value = "addSave")
	public String save(ARateCouponDic aRateCouponDic, Model model, RedirectAttributes redirectAttributes) {

		if (!beanValidator(model, aRateCouponDic)) {
			return addForm(aRateCouponDic, model);
		}

		// 判断是否要重复的字典数据，如果重复拒绝新增.
		List<ARateCouponDic> list = aRateCouponDicService.findAllList();
		if (list != null && list.size() > 0) {
			for (ARateCouponDic entity : list) {
				Double historyRate = entity.getRate();
				Double newRate = aRateCouponDic.getRate();
				if ((historyRate - newRate) == 0) {
					addMessage(redirectAttributes, "重复字典数据，拒绝新增");
					return "redirect:" + Global.getAdminPath() + "/activity/aRateCouponDic/?repage";
				}
			}
		}

		aRateCouponDic.setId(IdGen.uuid());
		aRateCouponDic.setCreateBy(SessionUtils.getUser());
		aRateCouponDic.setCreateDate(new Date());
		aRateCouponDic.setUpdateBy(SessionUtils.getUser());
		aRateCouponDic.setUpdateDate(new Date());

		int flag = aRateCouponDicService.insertARateCouponDic(aRateCouponDic);
		if (flag == 1) {
			addMessage(redirectAttributes, "新增加息券字典数据成功");
		} else {
			addMessage(redirectAttributes, "新增加息券字典数据失败");
		}

		return "redirect:" + Global.getAdminPath() + "/activity/aRateCouponDic/?repage";
	}

	@RequiresPermissions("activity:aRateCouponDic:view")
	@RequestMapping(value = "updateForm")
	public String updateForm(ARateCouponDic aRateCouponDic, Model model) {

		model.addAttribute("aRateCouponDic", aRateCouponDic);
		return "modules/activity/rate_coupon_dic/aRateCouponDicUpdateForm";
	}

	@RequiresPermissions("activity:aRateCouponDic:edit")
	@RequestMapping(value = "updateSave")
	public String updateSave(ARateCouponDic aRateCouponDic, Model model, RedirectAttributes redirectAttributes) {

		if (!beanValidator(model, aRateCouponDic)) {
			return updateForm(aRateCouponDic, model);
		}

		aRateCouponDic.setUpdateBy(SessionUtils.getUser());
		aRateCouponDic.setUpdateDate(new Date());

		int flag = aRateCouponDicService.updateARateCouponDic(aRateCouponDic);
		if (flag == 1) {
			addMessage(redirectAttributes, "修改加息券字典数据成功");
		} else {
			addMessage(redirectAttributes, "修改加息券字典数据失败");
		}

		return "redirect:" + Global.getAdminPath() + "/activity/aRateCouponDic/?repage";
	}

	@SuppressWarnings("static-access")
	@RequiresPermissions("activity:aRateCouponDic:edit")
	@RequestMapping(value = "delete")
	public String delete(ARateCouponDic aRateCouponDic, RedirectAttributes redirectAttributes) {

		String state = aRateCouponDic.getState();
		if (aRateCouponDicService.A_RATE_COUPON_DIC_STATE_2.equals(state)) {
			addMessage(redirectAttributes, "字典数据使用中，禁止删除");
			return "redirect:" + Global.getAdminPath() + "/activity/aRateCouponDic/?repage";
		}

		if (aRateCouponDicService.A_RATE_COUPON_DIC_STATE_1.equals(state)) {
			aRateCouponDicService.delete(aRateCouponDic);
			addMessage(redirectAttributes, "删除加息券字典数据成功");
		}

		return "redirect:" + Global.getAdminPath() + "/activity/aRateCouponDic/?repage";
	}

}