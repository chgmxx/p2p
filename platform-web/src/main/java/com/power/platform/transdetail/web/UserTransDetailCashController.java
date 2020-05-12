package com.power.platform.transdetail.web;

import java.util.List;

import javax.annotation.Resource;
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
import com.power.platform.common.web.BaseController;
import com.power.platform.common.utils.CommonStringUtils;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.utils.excel.ExportExcel;
import com.power.platform.trandetail.dao.UserTransDetailDao;
import com.power.platform.trandetail.entity.UserTransDetail;
import com.power.platform.trandetail.service.UserTransDetailService;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.userinfo.service.UserCheckAccountService;

/**
 * 
 * class: UserTransDetailCashController <br>
 * description: 客户流水记录Controller <br>
 * author: Mr.Roy <br>
 * date: 2018年12月9日 上午11:23:53
 */
@Controller
@RequestMapping(value = "${adminPath}/transdetail/userTransDetailCash")
public class UserTransDetailCashController extends BaseController {

	@Autowired
	private UserTransDetailService userTransDetailService;
	@Autowired
	private UserCheckAccountService userCheckAccountService;
	@Resource
	private UserTransDetailDao userTransDetailDao;
	
	@ModelAttribute
	public UserTransDetail get(@RequestParam(required=false) String id) {
		UserTransDetail entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = userTransDetailService.get(id);
		}
		if (entity == null){
			entity = new UserTransDetail();
		}
		return entity;
	}
	
	@RequiresPermissions("transdetail:userTransDetailCash:view")
	@RequestMapping(value = {"list", ""})
	public String list(UserTransDetail userTransDetail, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<UserTransDetail> page = userTransDetailService.findPage1(new Page<UserTransDetail>(request, response), userTransDetail); 
		List<UserTransDetail> list = page.getList();
		for (int i = 0; i < list.size(); i++) {
			UserTransDetail entity = list.get(i);
			UserInfo userInfo = entity.getUserInfo();
			if (userInfo != null) {
				userInfo.setName(CommonStringUtils.mobileEncrypt(userInfo.getName()));
				userInfo.setRealName(CommonStringUtils.replaceNameX(userInfo.getRealName()));
			}
		}
		model.addAttribute("page", page);
		return "modules/transdetail/userTransDetailCashList";
	}
	
	@RequiresPermissions("transdetail:userTransDetailCash:view")
	@RequestMapping(value = "export", method = RequestMethod.POST)
	public String exportFile(UserTransDetail userTransDetail, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {

		try {
			String fileName = "客户提现流水数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
			List<UserTransDetail> list = userTransDetailDao.findList1(userTransDetail);
			new ExportExcel("客户提现流水数据", UserTransDetail.class).setDataList(list).write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出兑奖数据失败！失败信息：" + e.getMessage());
		}
		return "redirect:" + adminPath + "/transdetail/userTransDetailCash/list?repage";
	}

	@RequiresPermissions("transdetail:userTransDetailCash:view")
	@RequestMapping(value = "form")
	public String form(UserTransDetail userTransDetail, Model model) {
		model.addAttribute("userTransDetail", userTransDetail);
		return "modules/transdetail/userTransDetailForm";
	}

	@RequiresPermissions("transdetail:userTransDetailCash:edit")
	@RequestMapping(value = "save")
	public String save(UserTransDetail userTransDetail, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, userTransDetail)){
			return form(userTransDetail, model);
		}
		userTransDetailService.save(userTransDetail);
		addMessage(redirectAttributes, "保存客户流水记录成功");
		return "redirect:"+Global.getAdminPath()+"/transdetail/userTransDetailCash/?repage";
	}
	
	@RequiresPermissions("transdetail:userTransDetailCash:edit")
	@RequestMapping(value = "delete")
	public String delete(UserTransDetail userTransDetail, RedirectAttributes redirectAttributes) {
		userTransDetailService.delete(userTransDetail);
		addMessage(redirectAttributes, "删除客户流水记录成功");
		return "redirect:"+Global.getAdminPath()+"/transdetail/userTransDetailCash/?repage";
	}
	
}