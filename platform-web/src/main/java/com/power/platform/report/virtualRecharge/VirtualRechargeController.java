package com.power.platform.report.virtualRecharge;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.power.platform.activity.dao.ZtmgWechatReturningCashDao;
import com.power.platform.activity.entity.ZtmgWechatReturningCash;
import com.power.platform.activity.service.ZtmgWechatReturningCashService;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.utils.CommonStringUtils;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.excel.ExportExcel;
import com.power.platform.common.web.BaseController;

/**
 * 
 * 类: VirtualRechargeController <br>
 * 描述: 虚拟充值Controller，列表展示及Excel导出功能. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年9月7日 上午9:57:53
 */
@Controller
@RequestMapping(value = "${adminPath}/report/virtualRecharge")
public class VirtualRechargeController extends BaseController {

	@Autowired
	private ZtmgWechatReturningCashService ztmgWechatReturningCashService;
	@Resource
	private ZtmgWechatReturningCashDao ztmgWechatReturningCashDao;

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

	@RequiresPermissions("report:virtualRecharge:view")
	@RequestMapping(value = { "list", "" })
	public String list(ZtmgWechatReturningCash ztmgWechatReturningCash, HttpServletRequest request, HttpServletResponse response, Model model) {

		Page<ZtmgWechatReturningCash> page = ztmgWechatReturningCashService.findExcelReportPage(new Page<ZtmgWechatReturningCash>(request, response), ztmgWechatReturningCash);
		List<ZtmgWechatReturningCash> list = page.getList();
		for (int i = 0; i < list.size(); i++) {
			ZtmgWechatReturningCash entity = list.get(i);
			entity.setMobilePhone(CommonStringUtils.mobileEncrypt(entity.getMobilePhone()));
			entity.setRealName(CommonStringUtils.replaceNameX(entity.getRealName()));
		}
		model.addAttribute("page", page);
		return "modules/report/virtualRechargeList";
	}

	@RequiresPermissions("report:virtualRecharge:view")
	@RequestMapping(value = "export", method = RequestMethod.POST)
	public String exportFile(ZtmgWechatReturningCash ztmgWechatReturningCash, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {

		try {
			String fileName = "虚拟充值数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
			List<ZtmgWechatReturningCash> findExcelReportList = ztmgWechatReturningCashDao.findExcelReportList(ztmgWechatReturningCash);
			new ExportExcel("虚拟充值数据", ZtmgWechatReturningCash.class).setDataList(findExcelReportList).write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出虚拟充值数据失败！失败信息：" + e.getMessage());
		}
		return "redirect:" + adminPath + "/report/virtualRecharge/list?repage";
	}

}