package com.power.platform.report.cash;

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

import com.power.platform.common.persistence.Page;
import com.power.platform.common.utils.CommonStringUtils;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.excel.ExportExcel;
import com.power.platform.common.web.BaseController;
import com.power.platform.pay.cash.dao.UserCashDao;
import com.power.platform.pay.cash.entity.UserCash;
import com.power.platform.pay.cash.service.UserCashService;
import com.power.platform.userinfo.entity.UserInfo;

/**
 * 
 * 类: CashController <br>
 * 描述: 提现Controller，列表展示及Excel导出功能. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年9月7日 上午9:52:29
 */
@Controller
@RequestMapping(value = "${adminPath}/report/cash")
public class CashController extends BaseController {

	@Autowired
	private UserCashService userCashService;
	@Resource
	private UserCashDao userCashDao;

	@ModelAttribute
	public UserCash get(@RequestParam(required = false) String id) {

		UserCash entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = userCashService.get(id);
		}
		if (entity == null) {
			entity = new UserCash();
		}
		return entity;
	}

	@RequiresPermissions("report:cash:view")
	@RequestMapping(value = { "list", "" })
	public String list(UserCash userCash, HttpServletRequest request, HttpServletResponse response, Model model) {

		Page<UserCash> page = userCashService.findExcelReportPage(new Page<UserCash>(request, response), userCash);
		List<UserCash> list = page.getList();
		for (int i = 0; i < list.size(); i++) {
			UserCash entity = list.get(i);
			UserInfo userInfo = entity.getUserInfo();
			if (userInfo != null) {
				userInfo.setName(CommonStringUtils.mobileEncrypt(userInfo.getName()));
				userInfo.setRealName(CommonStringUtils.replaceNameX(userInfo.getRealName()));
			}
		}
		model.addAttribute("page", page);
		return "modules/report/cashList";
	}

	@RequiresPermissions("report:cash:view")
	@RequestMapping(value = "export", method = RequestMethod.POST)
	public String exportFile(UserCash userCash, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {

		try {
			String fileName = "提现数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
			List<UserCash> list = userCashDao.findExcelReportList(userCash);
			new ExportExcel("提现数据", UserCash.class).setDataList(list).write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出提现数据失败！失败信息：" + e.getMessage());
		}
		return "redirect:" + adminPath + "/report/cash/list?repage";
	}

}