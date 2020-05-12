/**
 * Copyright &copy; 2012-2014 <a
 * href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.bouns.web;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.power.platform.activity.entity.AVouchersDic;
import com.power.platform.activity.service.AVouchersDicService;
import com.power.platform.bouns.entity.AwardInfo;
import com.power.platform.bouns.services.AwardInfoService;
import com.power.platform.common.config.Global;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.web.BaseController;

/**
 * 奖品信息Controller
 * 
 * @author yb
 * @version 2016-12-13
 */
@Controller
@RequestMapping(value = "${adminPath}/award/awardInfo")
public class AwardInfoController extends BaseController {

	@Autowired
	private AwardInfoService awardInfoService;
	@Autowired
	private AVouchersDicService aVouchersDicService;

	@ModelAttribute
	public AwardInfo get(@RequestParam(required = false) String id) {

		AwardInfo entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = awardInfoService.get(id);
		}
		if (entity == null) {
			entity = new AwardInfo();
		}
		return entity;
	}

	@RequiresPermissions("award:awardInfo:view")
	@RequestMapping(value = { "list", "" })
	public String list(AwardInfo awardInfo, HttpServletRequest request, HttpServletResponse response, Model model) {

		Page<AwardInfo> page = awardInfoService.findPage(new Page<AwardInfo>(request, response), awardInfo);

		List<AwardInfo> awardInfoList = page.getList();
		for (AwardInfo awardInfo1 : awardInfoList) {
			awardInfo1.setImgWebList(imgUrl(awardInfo1.getImgWeb()));
			awardInfo1.setImgWapList(imgUrl(awardInfo1.getImgWap()));
		}
		model.addAttribute("page", page);
		return "modules/bouns/awardInfoList";
	}

	@RequiresPermissions("award:awardInfo:view")
	@RequestMapping(value = "form")
	public String form(AwardInfo awardInfo, Model model) {

		// 抵用券类型数据.
		List<AVouchersDic> vouchersDics = aVouchersDicService.findAllList();
		for (AVouchersDic aVouchersDic : vouchersDics) {
			aVouchersDic.setAmountStr(NumberUtils.scaleDoubleStr(aVouchersDic.getAmount()));
			aVouchersDic.setLimitAmountStr(NumberUtils.scaleDoubleStr(aVouchersDic.getLimitAmount()));
		}
		awardInfo.setVouchersDics(vouchersDics);
		// 奖品信息.
		model.addAttribute("awardInfo", awardInfo);
		return "modules/bouns/awardInfoForm";
	}

	@RequiresPermissions("award:awardInfo:edit")
	@RequestMapping(value = "save")
	public String save(AwardInfo awardInfo, Model model, RedirectAttributes redirectAttributes) {

		if (!beanValidator(model, awardInfo)) {
			return form(awardInfo, model);
		}
		awardInfo.setCreatTime(new Date());
		awardInfo.setUpdateTime(new Date());
		awardInfoService.save(awardInfo);
		addMessage(redirectAttributes, "保存奖品信息成功");
		return "redirect:" + Global.getAdminPath() + "/award/awardInfo/?repage";
	}

	@RequiresPermissions("award:awardInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(AwardInfo awardInfo, RedirectAttributes redirectAttributes) {

		awardInfoService.delete(awardInfo);
		addMessage(redirectAttributes, "删除奖品信息成功");
		return "redirect:" + Global.getAdminPath() + "/award/awardInfo/?repage";
	}

	/**
	 * 拆分图片URL
	 * 
	 * @param urlStr
	 * @return
	 */
	public List<String> imgUrl(String urlStr) {

		List<String> urlList = null;
		urlList = new ArrayList<String>();
		if (urlStr != null && urlStr != "") {
			String urlArr[] = urlStr.split("\\|");
			for (int i = 1; i < urlArr.length; i++) {
				urlList.add(urlArr[i]);
			}
		}
		return urlList;
	}

}