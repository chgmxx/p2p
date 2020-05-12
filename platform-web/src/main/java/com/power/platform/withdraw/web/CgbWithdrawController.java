/**
 * Copyright &copy; 2012-2014 <a
 * href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.withdraw.web;

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
import org.springframework.web.bind.annotation.RequestParam;

import com.power.platform.common.persistence.Page;
import com.power.platform.common.web.BaseController;
import com.power.platform.common.utils.CommonStringUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.pay.cash.entity.UserCash;
import com.power.platform.pay.cash.service.UserCashService;
import com.power.platform.pay.service.LLPayService;
import com.power.platform.trandetail.service.UserTransDetailService;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.userinfo.service.UserAccountInfoService;
import com.power.platform.userinfo.service.UserBankCardService;
import com.power.platform.userinfo.service.UserInfoService;
import com.power.platform.weixin.service.WeixinSendTempMsgService;

/**
 * 提现管理Controller
 * 
 * @author caozhi
 * @version 2016-06-06
 */
@Controller
@RequestMapping(value = "${adminPath}/withdraw/cgbwithdraw")
public class CgbWithdrawController extends BaseController {

	@Autowired
	private UserCashService userCashService;

	@Autowired
	private UserBankCardService userBankCardService;

	@Autowired
	private UserInfoService userInfoService;

	@Autowired
	private UserAccountInfoService userAccountInfoService;

	@Autowired
	private UserTransDetailService userTransDetailService;

	@Resource
	private WeixinSendTempMsgService weixinSendTempMsgService;

	@Autowired
	private LLPayService llPayService;

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

	/**
	 * 提现列表
	 * 
	 * @param userCash
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequiresPermissions("withdraw:withdraw:view")
	@RequestMapping(value = { "list", "" })
	public String list(UserCash userCash, HttpServletRequest request, HttpServletResponse response, Model model) {

		userCash.setFrom(2);
		Page<UserCash> page = userCashService.findPage(new Page<UserCash>(request, response), userCash);
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
		return "modules/withdraw/CgbWithdrawList";
	}

}