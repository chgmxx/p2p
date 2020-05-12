/**
 * Copyright &copy; 2012-2014 <a
 * href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.userBankCard.web;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
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
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.properties.WebSiteProperties;
import com.power.platform.common.web.BaseController;
import com.power.platform.userinfo.entity.UserBankCard;
import com.power.platform.userinfo.entity.UserBankCardHistory;
import com.power.platform.userinfo.service.UserBankCardHistoryService;
import com.power.platform.userinfo.service.UserBankCardService;

/**
 * 
 * 类: UserBankCardHistoryController <br>
 * 描述: 客户银行卡更换历史Controller. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2015年12月21日 下午5:18:16
 */
@Controller
@RequestMapping(value = "${adminPath}/bank/userBankCardHistory")
public class UserBankCardHistoryController extends BaseController {

	private static final Logger logger = Logger.getLogger(UserBankCardHistoryController.class);

	/**
	 * 客户银行卡信息.
	 */
	@Autowired
	private UserBankCardService userBankCardService;
	/**
	 * 平台全局properties文件.
	 */
	@Autowired
	private WebSiteProperties webSiteProperties;
	/**
	 * 客户银行卡更换信息.
	 */
	@Autowired
	private UserBankCardHistoryService userBankCardHistoryService;

	@ModelAttribute
	public UserBankCardHistory get(@RequestParam(required = false) String id) {

		UserBankCardHistory entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = userBankCardHistoryService.get(id);
		}
		if (entity == null) {
			entity = new UserBankCardHistory();
		}
		return entity;
	}

	@RequiresPermissions("bank:userBankCardHistory:view")
	@RequestMapping(value = { "list", "" })
	public String list(UserBankCardHistory userBankCardHistory, HttpServletRequest request, HttpServletResponse response, Model model) {

		Page<UserBankCardHistory> page = userBankCardHistoryService.findPage(new Page<UserBankCardHistory>(request, response), userBankCardHistory);
		model.addAttribute("page", page);
		return "modules/bank/userBankCardHistoryList";
	}

	/**
	 * 
	 * 方法: addForm <br>
	 * 描述: 添加表单. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2015年12月23日 下午9:20:43
	 * 
	 * @param userBankCardHistory
	 * @param model
	 * @return
	 */
	@RequiresPermissions("bank:userBankCardHistory:view")
	@RequestMapping(value = "addForm")
	public String addForm(UserBankCardHistory userBankCardHistory, Model model) {

		model.addAttribute("userBankCardHistory", userBankCardHistory);
		return "modules/bank/userBankCardHistoryAddForm";
	}

	/**
	 * 
	 * 方法: updateForm <br>
	 * 描述: 更新表单. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2015年12月23日 下午9:20:31
	 * 
	 * @param userBankCardHistory
	 * @param model
	 * @return
	 */
	@RequiresPermissions("bank:userBankCardHistory:view")
	@RequestMapping(value = "updateForm")
	public String updateForm(UserBankCardHistory userBankCardHistory, Model model) {

		// 展示图片的基路径.
		String baseUrl = webSiteProperties.getDocDownloadUrl();

		model.addAttribute("baseUrl", baseUrl);
		model.addAttribute("userBankCardHistory", userBankCardHistory);
		return "modules/bank/userBankCardHistoryUpdateForm";
	}

	/**
	 * 
	 * 方法: viewForm <br>
	 * 描述: 展示表单. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2015年12月23日 下午9:21:23
	 * 
	 * @param userBankCardHistory
	 * @param model
	 * @return
	 */
	@RequiresPermissions("bank:userBankCardHistory:view")
	@RequestMapping(value = "viewForm")
	public String viewForm(UserBankCardHistory userBankCardHistory, Model model) {

		// 展示图片的基路径.
		String baseUrl = webSiteProperties.getDocDownloadUrl();

		model.addAttribute("baseUrl", baseUrl);
		model.addAttribute("userBankCardHistory", userBankCardHistory);
		return "modules/bank/userBankCardHistoryViewForm";
	}

	/**
	 * 
	 * 方法: approved <br>
	 * 描述: 审核通过. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2015年12月23日 下午12:44:53
	 * 
	 * @param userBankCardHistory
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("bank:userBankCardHistory:edit")
	@RequestMapping(value = "approved")
	public String approved(UserBankCardHistory userBankCardHistory, Model model, RedirectAttributes redirectAttributes) {

		// 新的银行卡号码.
		String newBankCardNo = com.power.platform.common.utils.StringUtils.replaceBlanK(userBankCardHistory.getNewBankCardNo());

		if (!beanValidator(model, userBankCardHistory)) {
			return addForm(userBankCardHistory, model);
		}
		if (UserBankCardHistoryService.REPLACE_IDENTITY_CARD_STATE_1.equals(userBankCardHistory.getState())) {
			logger.info("web-erp-fn:approved,{审核中}");
		} else if (UserBankCardHistoryService.REPLACE_IDENTITY_CARD_STATE_2.equals(userBankCardHistory.getState())) {
			logger.info("web-erp-fn:approved,{审核成功}");
			// 1>-审核通过.
			int state = userBankCardHistoryService.updateUserBankCardHistoryInfo(userBankCardHistory); // 审核通过，更新数据.
			if (state == 1) {
				logger.info("fn:approved,{ 审核通过，更新银行卡更换数据成功}");
			} else {
				logger.info("fn:approved,{ 审核通过，更新银行卡更换数据失败}");
			}
			// 1.1>-逻辑删除旧的银行卡信息.
			UserBankCard userBankCard = userBankCardService.getBankCardInfoByUserId(userBankCardHistory.getUserId());
			userBankCardService.delete(userBankCard);
			// 1.2>-添加更换后的银行卡信息.
			UserBankCard entity = new UserBankCard(); // 重新插入新的银行卡信息.
			entity.setId(IdGen.uuid());
			entity.setUserId(userBankCard.getUserId());
			entity.setAccountId(userBankCard.getAccountId());
			//entity.setLlAgreeNo(IdGen.uuid());
			//entity.setIdentityCardNo(userBankCard.getIdentityCardNo());
			entity.setBankAccountNo(newBankCardNo);
			entity.setBankNo("");
			//entity.setSn(IdGen.uuid());
			entity.setBindDate(new Date());
			entity.setState(UserBankCardService.USER_BANK_CARD_STATE_0);
			entity.setCreateDate(new Date());
			entity.setUpdateDate(new Date());
			entity.setIsDefault(UserBankCardService.USER_BANK_CARD_IS_DEFAULT_1);
			int flag = userBankCardService.insertBankCardInfo(entity);
			if (flag == 1) {
				logger.info("fn:approved,{添加银行卡信息成功}");
			} else {
				logger.info("fn:approved,{添加银行卡信息失败}");
			}
			userBankCardService.insertBankCardInfo(entity);
		} else if (UserBankCardHistoryService.REPLACE_IDENTITY_CARD_STATE_3.equals(userBankCardHistory.getState())) {
			logger.info("web-erp-fn:approved,{审核失败}");
		} else {
		}
		addMessage(redirectAttributes, "更换银行卡神审核成功！");
		return "redirect:" + Global.getAdminPath() + "/bank/userBankCardHistory/?repage";
	}

	@RequiresPermissions("bank:userBankCardHistory:edit")
	@RequestMapping(value = "save")
	public String save(UserBankCardHistory userBankCardHistory, Model model, RedirectAttributes redirectAttributes) {

		if (!beanValidator(model, userBankCardHistory)) {
			return addForm(userBankCardHistory, model);
		}
		userBankCardHistoryService.save(userBankCardHistory);
		addMessage(redirectAttributes, "保存更换银行卡成功");
		return "redirect:" + Global.getAdminPath() + "/bank/userBankCardHistory/?repage";
	}

	@RequiresPermissions("bank:userBankCardHistory:edit")
	@RequestMapping(value = "delete")
	public String delete(UserBankCardHistory userBankCardHistory, RedirectAttributes redirectAttributes) {

		userBankCardHistoryService.delete(userBankCardHistory);
		addMessage(redirectAttributes, "删除更换银行卡成功");
		return "redirect:" + Global.getAdminPath() + "/bank/userBankCardHistory/?repage";
	}

}