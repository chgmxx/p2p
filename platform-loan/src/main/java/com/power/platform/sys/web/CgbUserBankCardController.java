/**
 * 银行托管-银行卡-Controller.
 */
package com.power.platform.sys.web;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.power.platform.cgb.entity.CgbUserBankCard;
import com.power.platform.cgb.service.CgbUserBankCardService;
import com.power.platform.common.config.Global;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.web.BaseController;
import com.power.platform.credit.dao.userinfo.CreditUserInfoDao;

/**
 * 银行托管-银行卡-Controller.
 * 
 * @author lance
 * @version 2017-10-26
 */
@Controller
@RequestMapping(value = "${adminPath}/cgb/cgbUserBankCard")
public class CgbUserBankCardController extends BaseController {

	@Autowired
	private CgbUserBankCardService cgbUserBankCardService;
	@Resource
	private CreditUserInfoDao creditUserInfoDao;

	@ModelAttribute
	public CgbUserBankCard get(@RequestParam(required = false) String id) {

		CgbUserBankCard entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = cgbUserBankCardService.get(id);
		}
		if (entity == null) {
			entity = new CgbUserBankCard();
		}
		return entity;
	}

	@RequiresPermissions("cgb:cgbUserBankCard:view")
	@RequestMapping(value = { "list", "" })
	public String list(CgbUserBankCard cgbUserBankCard, HttpServletRequest request, HttpServletResponse response, Model model) {

		String bankCardRadioType = cgbUserBankCard.getBankCardRadioType();
		if (null == bankCardRadioType) {
			Page<CgbUserBankCard> page = cgbUserBankCardService.findPage(new Page<CgbUserBankCard>(request, response), cgbUserBankCard);
			model.addAttribute("page", page);
		} else if (CgbUserBankCard.BANK_CARD_RADIO_TYPE_1.equals(bankCardRadioType)) { // 出借人.
			Page<CgbUserBankCard> page = cgbUserBankCardService.findPage(new Page<CgbUserBankCard>(request, response), cgbUserBankCard);
			model.addAttribute("page", page);
		} else if (CgbUserBankCard.BANK_CARD_RADIO_TYPE_2.equals(bankCardRadioType)) { // 借款人.
			Page<CgbUserBankCard> page = cgbUserBankCardService.findCreditPage(new Page<CgbUserBankCard>(request, response), cgbUserBankCard);
			model.addAttribute("page", page);
			return "modules/cgb/userBankCard/cgbUserBankCardCreditList";
		}

		return "modules/cgb/userBankCard/cgbUserBankCardList";
	}

	@RequiresPermissions("cgb:cgbUserBankCard:view")
	@RequestMapping(value = "form")
	public String form(CgbUserBankCard cgbUserBankCard, Model model) {

		model.addAttribute("cgbUserBankCard", cgbUserBankCard);
		return "modules/cgb/userBankCard/cgbUserBankCardForm";
	}

	@RequiresPermissions("cgb:cgbUserBankCard:edit")
	@RequestMapping(value = "save")
	public String save(CgbUserBankCard cgbUserBankCard, Model model, RedirectAttributes redirectAttributes) {

		if (!beanValidator(model, cgbUserBankCard)) {
			return form(cgbUserBankCard, model);
		}
		cgbUserBankCardService.save(cgbUserBankCard);
		addMessage(redirectAttributes, "保存银行托管-银行卡成功");
		return "redirect:" + Global.getAdminPath() + "/cgb/cgbUserBankCard/?repage";
	}

	@RequiresPermissions("cgb:cgbUserBankCard:edit")
	@RequestMapping(value = "delete")
	public String delete(CgbUserBankCard cgbUserBankCard, RedirectAttributes redirectAttributes) {

		cgbUserBankCardService.delete(cgbUserBankCard);
		addMessage(redirectAttributes, "删除银行托管-银行卡成功");
		return "redirect:" + Global.getAdminPath() + "/cgb/cgbUserBankCard/?repage";
	}

}