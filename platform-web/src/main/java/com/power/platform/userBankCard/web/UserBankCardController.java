/**
 * Copyright &copy; 2012-2014 <a
 * href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.userBankCard.web;

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

import com.power.platform.common.config.Global;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.utils.CommonStringUtils;
import com.power.platform.common.web.BaseController;
import com.power.platform.userinfo.entity.UserBankCard;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.userinfo.service.UserBankCardService;
import com.power.platform.userinfo.service.UserInfoService;

/**
 * 客户银行卡Controller
 * 
 * @author Soler
 * @version 2015-12-18
 */
@Controller
@RequestMapping(value = "${adminPath}/bank/userBankCard")
public class UserBankCardController extends BaseController {

	@Autowired
	private UserBankCardService userBankCardService;
	
	@Autowired
	private UserInfoService userInfoService;

	@ModelAttribute
	public UserBankCard get(@RequestParam(required = false) String id) {

		UserBankCard entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = userBankCardService.get(id);
		}
		if (entity == null) {
			entity = new UserBankCard();
		}
		return entity;
	}

	@RequiresPermissions("bank:userBankCard:view")
	@RequestMapping(value = { "list", "" })
	public String list(UserBankCard userBankCard, HttpServletRequest request, HttpServletResponse response, Model model) {
		userBankCard.setState(UserBankCard.CERTIFY_YES);
		userBankCard.setIsDefault(UserBankCard.DEFAULT_YES);
		Page<UserBankCard> page = userBankCardService.findPage(new Page<UserBankCard>(request, response), userBankCard);
		
		List<UserBankCard> list = page.getList();
		for (int i = 0; i < list.size(); i++) {
			UserBankCard entity = list.get(i);
			UserInfo userInfo = entity.getUserInfo();
			if (userInfo != null) {
				userInfo.setName(CommonStringUtils.mobileEncrypt(userInfo.getName()));
				userInfo.setRealName(CommonStringUtils.replaceNameX(userInfo.getRealName()));
			}
			entity.setBankAccountNo(CommonStringUtils.idEncrypt(entity.getBankAccountNo()));
		}
		
		
		
		model.addAttribute("page", page);
		return "modules/bank/userBankCardList";
	}

	/**
	 * 
	 * 方法: addForm <br>
	 * 描述: 添加表单. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2015年12月23日 下午8:45:39
	 * 
	 * @param userBankCard
	 * @param model
	 * @return
	 */
	@RequiresPermissions("bank:userBankCard:view")
	@RequestMapping(value = "addForm")
	public String addForm(UserBankCard userBankCard, Model model) {

		model.addAttribute("userBankCard", userBankCard);
		return "modules/bank/userBankCardAddForm";
	}

	/**
	 * 
	 * 方法: updateForm <br>
	 * 描述: 更新表单. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2015年12月23日 下午8:45:59
	 * 
	 * @param userBankCard
	 * @param model
	 * @return
	 */
	@RequiresPermissions("bank:userBankCard:view")
	@RequestMapping(value = "updateForm")
	public String updateForm(UserBankCard userBankCard, Model model) {

		model.addAttribute("userBankCard", userBankCard);
		return "modules/bank/userBankCardUpdateForm";
	}

	/**
	 * 
	 * 方法: viewForm <br>
	 * 描述: 展示表单. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2015年12月23日 下午8:46:52
	 * 
	 * @param userBankCard
	 * @param model
	 * @return
	 */
	@RequiresPermissions("bank:userBankCard:view")
	@RequestMapping(value = "viewForm")
	public String viewForm(UserBankCard userBankCard, Model model) {

		model.addAttribute("userBankCard", userBankCard);
		return "modules/bank/userBankCardViewForm";
	}

	@RequiresPermissions("bank:userBankCard:edit")
	@RequestMapping(value = "save")
	public String save(UserBankCard userBankCard, Model model, RedirectAttributes redirectAttributes) {

		if (!beanValidator(model, userBankCard)) {
			return addForm(userBankCard, model);
		}
		userBankCardService.save(userBankCard);
		addMessage(redirectAttributes, "保存客户银行卡成功");
		return "redirect:" + Global.getAdminPath() + "/bank/userBankCard/?repage";
	}

	@RequiresPermissions("bank:userBankCard:edit")
	@RequestMapping(value = "delete")
	public String delete(UserBankCard userBankCard, RedirectAttributes redirectAttributes) {

		userBankCardService.delete(userBankCard);
		
		UserInfo userInfo = userInfoService.get(userBankCard.getUserId());
		userInfo.setBindBankCardState(UserInfo.BIND_CARD_NO);
		userInfo.setLlagreeNo(null);
		userInfoService.updateUserInfo(userInfo);
		addMessage(redirectAttributes, "删除客户银行卡成功");
		return "redirect:" + Global.getAdminPath() + "/bank/userBankCard/?repage";
	}

}