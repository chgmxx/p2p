package com.power.platform.activity.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.base.Joiner;
import com.power.platform.activity.entity.AVouchersDic;
import com.power.platform.activity.pojo.Span;
import com.power.platform.activity.service.AVouchersDicService;
import com.power.platform.activity.service.UserVouchersHistoryService;
import com.power.platform.common.config.Global;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.security.shiro.session.SessionUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.common.web.BaseController;

/**
 * 
 * 类: AVouchersDicController <br>
 * 描述: 抵用券/代金券字典数据Controller. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年4月19日 上午9:47:24
 */
@Controller
@RequestMapping(value = "${adminPath}/activity/aVouchersDic")
public class AVouchersDicController extends BaseController {

	@Autowired
	private AVouchersDicService aVouchersDicService;

	@ModelAttribute
	public AVouchersDic get(@RequestParam(required = false) String id) {

		AVouchersDic entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = aVouchersDicService.get(id);
		}
		if (entity == null) {
			entity = new AVouchersDic();
		}
		return entity;
	}

	@RequiresPermissions("activity:aVouchersDic:view")
	@RequestMapping(value = { "list", "" })
	public String list(AVouchersDic aVouchersDic, HttpServletRequest request, HttpServletResponse response, Model model) {

		Page<AVouchersDic> page = aVouchersDicService.findPage(new Page<AVouchersDic>(request, response), aVouchersDic);
		List<AVouchersDic> list = page.getList();
		for (AVouchersDic entity : list) {
			entity.setAmountStr(NumberUtils.scaleDoubleStr(entity.getAmount()));
			entity.setLimitAmountStr(NumberUtils.scaleDoubleStr(entity.getLimitAmount()));
		}
		model.addAttribute("page", page);
		return "modules/activity/vouchers_dic/aVouchersDicList";
	}

	@RequiresPermissions("activity:aVouchersDic:view")
	@RequestMapping(value = "addForm")
	public String addForm(AVouchersDic aVouchersDic, Model model) {

		// 项目期限集合.
		List<Span> spans = new ArrayList<Span>();
		Span span_1 = new Span(UserVouchersHistoryService.SPAN_1, "通用");
		spans.add(span_1);
		Span span_30 = new Span(UserVouchersHistoryService.SPAN_30, "30天");
		spans.add(span_30);
		Span span_60 = new Span(UserVouchersHistoryService.SPAN_60, "60天");
		spans.add(span_60);
		Span span_90 = new Span(UserVouchersHistoryService.SPAN_90, "90天");
		spans.add(span_90);
		Span span_120 = new Span(UserVouchersHistoryService.SPAN_120, "120天");
		spans.add(span_120);
		Span span_180 = new Span(UserVouchersHistoryService.SPAN_180, "180天");
		spans.add(span_180);
		Span span_360 = new Span(UserVouchersHistoryService.SPAN_360, "360天");
		spans.add(span_360);
		model.addAttribute("spans", spans);
		model.addAttribute("aVouchersDic", aVouchersDic);
		return "modules/activity/vouchers_dic/aVouchersDicAddForm";
	}

	@RequiresPermissions("activity:aVouchersDic:edit")
	@RequestMapping(value = "addSave")
	public String addSave(AVouchersDic aVouchersDic, Model model, RedirectAttributes redirectAttributes) {

		if (!beanValidator(model, aVouchersDic)) {
			return addForm(aVouchersDic, model);
		}

		// 判断是否要重复的字典数据，如果重复拒绝新增.
//		List<AVouchersDic> list = aVouchersDicService.findAllList();
//		if (list != null && list.size() > 0) {
//			for (AVouchersDic entity : list) {
//				Double historyAmount = entity.getAmount();
//				Double newAmount = aVouchersDic.getAmount();
//				if ((historyAmount - newAmount) == 0) {
//					addMessage(redirectAttributes, "重复字典数据，拒绝新增");
//					return "redirect:" + Global.getAdminPath() + "/activity/aVouchersDic/?repage";
//				}
//			}
//		}

		// 唯一标识.
		aVouchersDic.setId(IdGen.uuid());
		// 创建人.
		aVouchersDic.setCreateBy(SessionUtils.getUser());
		// 创建时间.
		aVouchersDic.setCreateDate(new Date());
		// 更新人.
		aVouchersDic.setUpdateBy(SessionUtils.getUser());
		// 更新时间.
		aVouchersDic.setUpdateDate(new Date());
		// 项目期限集合.
		List<String> spanIdList = aVouchersDic.getSpanIdList();
		String spanStr = Joiner.on(",").join(spanIdList);
		aVouchersDic.setSpans(spanStr);

		int flag = aVouchersDicService.insertAVouchersDic(aVouchersDic);
		if (flag == 1) {
			addMessage(redirectAttributes, "新增抵用券类型成功");
		} else {
			addMessage(redirectAttributes, "新增抵用券类型失败");
		}
		return "redirect:" + Global.getAdminPath() + "/activity/aVouchersDic/?repage";
	}

	@RequiresPermissions("activity:aVouchersDic:view")
	@RequestMapping(value = "updateForm")
	public String updateForm(AVouchersDic aVouchersDic, Model model) {

		// 项目期限集合.
		List<Span> spans = new ArrayList<Span>();
		Span span_1 = new Span(UserVouchersHistoryService.SPAN_1, "通用");
		spans.add(span_1);
		Span span_30 = new Span(UserVouchersHistoryService.SPAN_30, "30天");
		spans.add(span_30);
		Span span_60 = new Span(UserVouchersHistoryService.SPAN_60, "60天");
		spans.add(span_60);
		Span span_90 = new Span(UserVouchersHistoryService.SPAN_90, "90天");
		spans.add(span_90);
		Span span_120 = new Span(UserVouchersHistoryService.SPAN_120, "120天");
		spans.add(span_120);
		Span span_180 = new Span(UserVouchersHistoryService.SPAN_180, "180天");
		spans.add(span_180);
		Span span_360 = new Span(UserVouchersHistoryService.SPAN_360, "360天");
		spans.add(span_360);
		List<Span> spanList = aVouchersDic.getSpanList();
		String spanStr = aVouchersDic.getSpans();
		if (spanStr != null) {
			List<String> asList = Arrays.asList(spanStr.split(","));
			for (String span : asList) {
				if (span.equals(UserVouchersHistoryService.SPAN_1)) {
					Span span_list_1 = new Span(UserVouchersHistoryService.SPAN_1, "通用");
					spanList.add(span_list_1);
				} else if (span.equals(UserVouchersHistoryService.SPAN_30)) {
					Span span_list_30 = new Span(UserVouchersHistoryService.SPAN_30, "30天");
					spanList.add(span_list_30);
				} else if (span.equals(UserVouchersHistoryService.SPAN_60)) {
					Span span_list_60 = new Span(UserVouchersHistoryService.SPAN_60, "60天");
					spanList.add(span_list_60);
				} else if (span.equals(UserVouchersHistoryService.SPAN_90)) {
					Span span_list_90 = new Span(UserVouchersHistoryService.SPAN_90, "90天");
					spanList.add(span_list_90);
				} else if (span.equals(UserVouchersHistoryService.SPAN_120)) {
					Span span_list_120 = new Span(UserVouchersHistoryService.SPAN_120, "120天");
					spanList.add(span_list_120);
				} else if (span.equals(UserVouchersHistoryService.SPAN_180)) {
					Span span_list_180 = new Span(UserVouchersHistoryService.SPAN_180, "180天");
					spanList.add(span_list_180);
				} else if (span.equals(UserVouchersHistoryService.SPAN_360)) {
					Span span_list_360 = new Span(UserVouchersHistoryService.SPAN_360, "360天");
					spanList.add(span_list_360);
				}
			}
		}

		model.addAttribute("spans", spans);
		model.addAttribute("aVouchersDic", aVouchersDic);
		return "modules/activity/vouchers_dic/aVouchersDicUpdateForm";
	}

	@RequiresPermissions("activity:aVouchersDic:edit")
	@RequestMapping(value = "updateSave")
	public String updateSave(AVouchersDic aVouchersDic, Model model, RedirectAttributes redirectAttributes) {

		if (!beanValidator(model, aVouchersDic)) {
			return updateForm(aVouchersDic, model);
		}

		aVouchersDic.setUpdateBy(SessionUtils.getUser());
		aVouchersDic.setUpdateDate(new Date());
		// 项目期限集合.
		List<String> spanIdList = aVouchersDic.getSpanIdList();
		String spanStr = Joiner.on(",").join(spanIdList);
		aVouchersDic.setSpans(spanStr);

		int flag = aVouchersDicService.updateAVouchersDic(aVouchersDic);
		if (flag == 1) {
			addMessage(redirectAttributes, "修改抵用券类型成功");
		} else {
			addMessage(redirectAttributes, "修改抵用券类型失败");
		}

		return "redirect:" + Global.getAdminPath() + "/activity/aVouchersDic/?repage";
	}

	@SuppressWarnings("static-access")
	@RequiresPermissions("activity:aVouchersDic:edit")
	@RequestMapping(value = "delete")
	public String delete(AVouchersDic aVouchersDic, RedirectAttributes redirectAttributes) {

		String state = aVouchersDic.getState();
		if (aVouchersDicService.A_VOUCHERS_DIC_STATE_2.equals(state)) {
			addMessage(redirectAttributes, "字典数据使用中，禁止删除");
			return "redirect:" + Global.getAdminPath() + "/activity/aVouchersDic/?repage";
		}

		if (aVouchersDicService.A_VOUCHERS_DIC_STATE_1.equals(state)) {
			aVouchersDicService.delete(aVouchersDic);
			addMessage(redirectAttributes, "删除抵用券类型成功");
		}

		return "redirect:" + Global.getAdminPath() + "/activity/aVouchersDic/?repage";
	}
	
	@ResponseBody
	@RequestMapping(value = "vouchersInfoByVouchersId")
	public Map<String, AVouchersDic> vouchersInfoByVouchersId(String vouchersId, Model model) {

		Map<String, AVouchersDic> params = new HashMap<String, AVouchersDic>();
		AVouchersDic entity = aVouchersDicService.get(vouchersId);
		if (entity != null) { // 抵用券类型.
			entity.setAmountStr(NumberUtils.scaleDoubleStr(entity.getAmount()));
			entity.setLimitAmountStr(NumberUtils.scaleDoubleStr(entity.getLimitAmount()));
		}
		params.put("aVouchersDic", entity);
		return params;
	}

}