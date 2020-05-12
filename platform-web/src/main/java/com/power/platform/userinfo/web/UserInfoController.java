package com.power.platform.userinfo.web;

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
import com.power.platform.common.web.BaseController;
import com.power.platform.common.security.shiro.session.SessionUtils;
import com.power.platform.common.utils.CommonStringUtils;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.utils.excel.ExportExcel;
import com.power.platform.history.entity.ZtmgModifyMobilephoneHistory;
import com.power.platform.history.service.ZtmgModifyMobilephoneHistoryService;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.userinfo.service.UserInfoService;

/**
 * 用户信息管理Controller
 * 
 * @author jiajunfeng
 * @version 2015-12-16
 */
@Controller
@RequestMapping(value = "${adminPath}/userinfo/userInfo")
public class UserInfoController extends BaseController {

	@Autowired
	private ZtmgModifyMobilephoneHistoryService ztmgModifyMobilephoneHistoryService;
	@Autowired
	private UserInfoService userInfoService;

	@ModelAttribute
	public UserInfo get(@RequestParam(required = false) String id) {

		UserInfo entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = userInfoService.get(id);
		}
		if (entity == null) {
			entity = new UserInfo();
		}
		return entity;
	}

	@RequiresPermissions("userinfo:userInfo:view")
	@RequestMapping(value = { "list", "" })
	public String list(UserInfo userInfo, HttpServletRequest request, HttpServletResponse response, Model model) {

		//解决查询条件未输入但有空值传入的bug
		if(userInfo.getPartnerForm()!=null){
			if(userInfo.getPartnerForm().getPlatformName().equals("")){
				userInfo.setPartnerForm(null);;
			}
		}

		Page<UserInfo> page = userInfoService.findPage(new Page<UserInfo>(request, response), userInfo);
		
		List<UserInfo> list = page.getList();
		for (int i = 0; i < list.size(); i++) {
			UserInfo entity = list.get(i); // 实体类
			entity.setName(CommonStringUtils.mobileEncrypt(entity.getName()));
			entity.setRecommendUserPhone(CommonStringUtils.mobileEncrypt(entity.getRecommendUserPhone()));
			entity.setRealName(CommonStringUtils.replaceNameX(entity.getRealName()));
			entity.setCertificateNo(CommonStringUtils.idEncrypt(entity.getCertificateNo()));
		}

		model.addAttribute("page", page);
		return "modules/userinfo/userInfoList";
	}

	@RequiresPermissions("userinfo:userInfo:view")
	@RequestMapping(value = "form")
	public String form(UserInfo userInfo, Model model) {

		model.addAttribute("userInfo", userInfo);
		return "modules/userinfo/userInfoForm";
	}

	/**
	 * 
	 * 方法: modifyMobilephoneForm <br>
	 * 描述: 更换手机表单. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年11月11日 上午11:30:15
	 * 
	 * @param userInfo
	 * @param model
	 * @return
	 */
	@RequiresPermissions("userinfo:userInfo:view")
	@RequestMapping(value = "modifyMobilephoneForm")
	public String modifyMobilephoneForm(UserInfo userInfo, Model model) {

		// 原手机号码.
		userInfo.setOldMobilephone(userInfo.getName());
		model.addAttribute("userInfo", userInfo);
		return "modules/userinfo/modifyMobilephoneForm";
	}

	/**
	 * 
	 * 方法: modifySave <br>
	 * 描述: 更换手机. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年11月11日 下午3:10:00
	 * 
	 * @param userInfo
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("userinfo:userInfo:edit")
	@RequestMapping(value = "modifySave")
	public String modifySave(UserInfo userInfo, Model model, RedirectAttributes redirectAttributes) {

		if (!beanValidator(model, userInfo)) {
			return form(userInfo, model);
		}

		// 保存更换手机操作历史.
		ZtmgModifyMobilephoneHistory ztmgModifyMobilephoneHistory = new ZtmgModifyMobilephoneHistory();
		ztmgModifyMobilephoneHistory.setId(IdGen.uuid());
		ztmgModifyMobilephoneHistory.setOldmobilephone(userInfo.getOldMobilephone());
		ztmgModifyMobilephoneHistory.setNewmobilephone(userInfo.getName());
		ztmgModifyMobilephoneHistory.setRemarks("更换手机号码");
		ztmgModifyMobilephoneHistory.setCreateBy(SessionUtils.getUser());
		ztmgModifyMobilephoneHistory.setCreateDate(new Date());
		ztmgModifyMobilephoneHistory.setUpdateBy(SessionUtils.getUser());
		ztmgModifyMobilephoneHistory.setUpdateDate(new Date());
		ztmgModifyMobilephoneHistoryService.insertZtmgModifyMobilephoneHistory(ztmgModifyMobilephoneHistory);
		
		// 更换手机.
		
		userInfoService.save(userInfo);
		addMessage(redirectAttributes, "更换手机号码成功");
		return "redirect:" + Global.getAdminPath() + "/userinfo/userInfo/?repage";
	}

	@RequiresPermissions("userinfo:userInfo:edit")
	@RequestMapping(value = "save")
	public String save(UserInfo userInfo, Model model, RedirectAttributes redirectAttributes) {

		if (!beanValidator(model, userInfo)) {
			return form(userInfo, model);
		}
		userInfoService.save(userInfo);
		addMessage(redirectAttributes, "保存用户信息管理成功");
		return "redirect:" + Global.getAdminPath() + "/userinfo/userInfo/?repage";
	}

	@RequiresPermissions("userinfo:userInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(UserInfo userInfo, RedirectAttributes redirectAttributes) {

		userInfoService.delete(userInfo);
		addMessage(redirectAttributes, "删除用户信息管理成功");
		return "redirect:" + Global.getAdminPath() + "/userinfo/userInfo/?repage";
	}

	/**
	 * 
	 * 方法: exportUserInfo <br>
	 * 描述: 导出客户信息. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年6月29日 下午4:20:11
	 * 
	 * @param userInfo
	 * @param request
	 * @param response
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("userinfo:userInfo:view")
	@RequestMapping(value = "exportUserInfo", method = RequestMethod.POST)
	public String exportUserInfo(UserInfo userInfo, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {

		try {
			String fileName = "客户基本信息" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
			List<UserInfo> list = userInfoService.findList(userInfo);
			List<UserInfo> newliList = new ArrayList<UserInfo>();
			UserInfo parent = null;
			for(UserInfo user:list){
				if(null != user.getRecommendUserId() && !"".equals(user.getRecommendUserId())){
					parent = userInfoService.get(user.getRecommendUserId());
					if(null != parent){
						user.setParentPhone(parent.getName());
					}
				}else {
					user.setParentPhone(" ");
				}
				newliList.add(user);
			}
			new ExportExcel("客户基本信息", UserInfo.class).setDataList(newliList).write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出客户基本信息失败！失败信息：" + e.getMessage());
		}

		return "redirect:" + Global.getAdminPath() + "/userinfo/userInfo/?repage";
	}

}