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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.ibm.icu.text.SimpleDateFormat;
import com.power.platform.common.config.Global;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.utils.CommonStringUtils;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.utils.excel.ExportExcel;
import com.power.platform.common.web.BaseController;
import com.power.platform.credit.entity.apply.CreditUserApply;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.regular.entity.WloanSubject;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.regular.entity.WloanTermProject2;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.userinfo.entity.UserLog;
import com.power.platform.userinfo.service.UserLogService;

@Controller
@RequestMapping(value = "${adminPath}/userlog")
public class UserLogController  extends BaseController{
	@Autowired
	private UserLogService userLogService;

	
	@ModelAttribute
	public UserLog get(@RequestParam(required = false) String id) {

		UserLog entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = userLogService.get(id);
		}
		if (entity == null) {
			entity = new UserLog();
		}
		return entity;
	}
	@RequiresPermissions("userlog:userlog:view")
	@RequestMapping(value = { "list", "" })
	public String list(UserLog userlog, HttpServletRequest request, HttpServletResponse response, Model model) {

		//解决查询条件未输入但有空值传入的bug
//		if(userInfo.getPartnerForm()!=null){
//			if(userInfo.getPartnerForm().getPlatformName().equals("")){
//				userInfo.setPartnerForm(null);;
//			}
//		}
		Page<UserLog> page = userLogService.findPage(new Page<UserLog>(request, response), userlog);
		
//		List<UserInfo> list = page.getList();
//		for (int i = 0; i < list.size(); i++) {
//			UserInfo entity = list.get(i); // 实体类
//			entity.setName(CommonStringUtils.mobileEncrypt(entity.getName()));
//			entity.setRecommendUserPhone(CommonStringUtils.mobileEncrypt(entity.getRecommendUserPhone()));
//			entity.setRealName(CommonStringUtils.replaceNameX(entity.getRealName()));
//			entity.setCertificateNo(CommonStringUtils.idEncrypt(entity.getCertificateNo()));
//		}

		model.addAttribute("page", page);
		return "modules/userinfo/userLogList";
	}
	@RequiresPermissions("apply:creditUserApply:view")
	@RequestMapping(value = "exportLogList", method = RequestMethod.POST)
	public String exportLogList(UserLog userlog, HttpServletRequest request, HttpServletResponse response) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fileName = null;
		if(userlog.getBeginDate() == null && userlog.getEndDate() != null)
		    fileName = "截止"+sdf.format(userlog.getEndDate())+"日志数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
		else if(userlog.getBeginDate() != null && userlog.getEndDate() == null)
			fileName = sdf.format(userlog.getBeginDate())+"之后日志数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
		else if(userlog.getBeginDate() != null && userlog.getEndDate() != null)
			fileName = sdf.format(userlog.getBeginDate())+"-"+sdf.format(userlog.getEndDate())+"之后日志数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
		else
			fileName ="日志数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";	
		try {
		List<UserLog> logs = userLogService.findList(userlog);
		for(UserLog userLog2 : logs) {
			if(userLog2.getBeginDate()==null)
				userLog2.setBeginDate(userLog2.getCreateDate());
			if(userLog2.getType().equals("1"))
				userLog2.setType("登录");
			else if(userLog2.getType().equals("2"))
				userLog2.setType("开户");
			else if(userLog2.getType().equals("3"))
				userLog2.setType("充值（转账）");
			else if(userLog2.getType().equals("4"))
				userLog2.setType("充值（网银）");
			else if(userLog2.getType().equals("5"))
				userLog2.setType("提现");
			else 
				userLog2.setType("未知");
		}
		new ExportExcel("日志数据", UserLog.class).setDataList(logs).write(response, fileName).dispose();		
		return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		String returnUrl = "";
		returnUrl = "redirect:" + Global.getAdminPath() + "/userlog/list";
		return returnUrl;
	}

}
