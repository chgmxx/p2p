/**
 * Copyright &copy; 2012-2014 <a
 * href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.withdraw.web;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.power.platform.cache.Cache;
import com.power.platform.common.config.Global;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.web.BaseController;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.MemCachedUtis;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.pay.cash.entity.UserCash;
import com.power.platform.pay.cash.service.UserCashService;
import com.power.platform.pay.service.LLPayService;
import com.power.platform.trandetail.entity.UserTransDetail;
import com.power.platform.trandetail.service.UserTransDetailService;
import com.power.platform.userinfo.entity.Principal;
import com.power.platform.userinfo.entity.UserAccountInfo;
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
@RequestMapping(value = "${adminPath}/withdraw/withdraw")
public class WithdrawController extends BaseController {

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

		userCash.setFrom(1);
		Page<UserCash> page = userCashService.findPage(new Page<UserCash>(request, response), userCash);
//		List<UserCash> list = page.getList();
//		for (int i = 0; i < list.size(); i++) {
//			UserCash entity = list.get(i);
//			UserInfo userInfo = entity.getUserInfo();
//			if (userInfo != null) {
//				userInfo.setName(CommonStringUtils.mobileEncrypt(userInfo.getName()));
//				userInfo.setRealName(CommonStringUtils.replaceNameX(userInfo.getRealName()));
//			}
//		}
		model.addAttribute("page", page);
		return "modules/withdraw/withdrawList";
	}

	/**
	 * 提现通过
	 * 
	 * @param userInfo
	 * @param model
	 * @return
	 */
	@RequiresPermissions("withdraw:withdraw:edit")
	@RequestMapping(value = "approved")
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public String approved(UserCash userCash, RedirectAttributes redirectAttributes) {

		try {
			// UserInfo userInfo = userInfoService.get(userCash.getUserId());
			// UserBankCard userBankCard =
			// userBankCardService.getBankCardInfoByUserId(userInfo.getId());
			UserAccountInfo userAccountInfo = userAccountInfoService.getUserAccountInfo(userCash.getUserId());
			double amount = userCash.getAmount();
			double freezeAmount = userAccountInfo.getFreezeAmount();
			double totalAmount = userAccountInfo.getTotalAmount();
			if (amount > freezeAmount) {
				addMessage(redirectAttributes, "用户冻结金额小于提现金额，通过失败");
				return "redirect:" + Global.getAdminPath() + "/withdraw/withdraw/?repage";
			}
			// double feeAmount = userCash.getFeeAmount();
			/*
			 * Map<String, Object> resultMap =
			 * llPayService.goCashPay(userInfo.getRealName(),
			 * userBankCard.getBankAccountNo(), userCash.getCityCode(),null,
			 * userCash.getBrabankName(), userInfo.getCertificateNo(),
			 * userCash.getAmount()-feeAmount, "用户提现", userCash.getSn(),null);
			 * String ret_code = (String) resultMap.get("ret_code");
			 * System.out.println("ret_code=" +ret_code);
			 * if (ret_code != "0000" && !"0000".equals(ret_code)) {
			 * throw new Exception("提现通过失败");
			 * }
			 */
			userCash.setState(UserCash.CASH_DOING);
			userAccountInfo.setFreezeAmount(NumberUtils.scaleDouble(freezeAmount - amount));
			userAccountInfo.setCashAmount(userAccountInfo.getCashAmount() + amount);
			userAccountInfo.setCashCount(userAccountInfo.getCashCount() + 1);
			userAccountInfo.setTotalAmount(NumberUtils.scaleDouble(totalAmount - amount));
			userAccountInfoService.save(userAccountInfo);
			userCash.setFrom(1);
			userCash.setUpdateDate(new Date());
			userCashService.save(userCash);
			// 3.资金流水表更新状态
			UserTransDetail userTransDetail = new UserTransDetail();
			userTransDetail.setUserId(userCash.getUserId());
			userTransDetail.setAmount(userCash.getAmount());
			userTransDetail.setAccountId(userCash.getAccountId());
			userTransDetail.setAvaliableAmount(userAccountInfo.getAvailableAmount());
			userTransDetail.setTransDate(new Date());
			userTransDetail.setBeginTransDate(userCash.getBeginDate());
			userTransDetail.setInOutType(UserTransDetail.TRANS_TYPE_OUT);
			userTransDetail.setRemarks("用户提现");
			userTransDetail.setState(UserTransDetail.TRANS_STATE_SUCCESS);
			userTransDetail.setTrustType(UserTransDetail.TRANS_CASH);
			userTransDetail.setId(IdGen.uuid());
			userTransDetail.setTransId(userCash.getId());
			userTransDetailService.insert(userTransDetail);

			// 发送提现申请微信、短信提醒
			weixinSendTempMsgService.sendCashSuccessMsg(userCash);
		} catch (Exception e) {
			e.printStackTrace();
			addMessage(redirectAttributes, "用户提现提交连连失败");
			return "redirect:" + Global.getAdminPath() + "/withdraw/withdraw/?repage";
		}
		addMessage(redirectAttributes, "用户提现提交连连成功");
		return "redirect:" + Global.getAdminPath() + "/withdraw/withdraw/?repage";
	}

	/**
	 * 提现回绝
	 * 
	 * @param userInfo
	 * @param model
	 * @return
	 */
	@RequiresPermissions("withdraw:withdraw:edit")
	@RequestMapping(value = "refused")
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public String refused(UserCash userCash, RedirectAttributes redirectAttributes) {

		try {
			UserAccountInfo userAccountInfo = userAccountInfoService.getUserAccountInfo(userCash.getUserId());
			double amount = userCash.getAmount();
			double freezenAmount = userAccountInfo.getFreezeAmount();
			double availableAmount = userAccountInfo.getAvailableAmount();
			if (amount > freezenAmount) {
				addMessage(redirectAttributes, "用户冻结金额小于提现金额，处理失败");
				return "redirect:" + Global.getAdminPath() + "/withdraw/withdraw/?repage";
			}

			// 可用余额增加
			userAccountInfo.setAvailableAmount(NumberUtils.scaleDouble(availableAmount + amount));
			// 冻结金额减少
			userAccountInfo.setFreezeAmount(NumberUtils.scaleDouble(freezenAmount - amount));
			userAccountInfoService.save(userAccountInfo);
			Cache cache = MemCachedUtis.getMemCached();
			Map<String, String> cacheLoginedUser = cache.get("cacheLoginedUser");
			String token = cacheLoginedUser.get(userCash.getUserId());
			if (token != null) {
				Principal principal = cache.get(token);
				if (principal != null) {
					principal.setUserAccountInfo(userAccountInfo);
					cache.set(token, 1200, principal);
				}
			}

			userCash.setState(UserCash.CASH_VERIFY_FAIL);
			userCash.setUpdateDate(new Date());
			userCashService.updateState(userCash);
			// 记录流水
			// 3.资金流水表更新状态
			UserTransDetail userTransDetail = new UserTransDetail();
			userTransDetail.setUserId(userCash.getUserId());
			userTransDetail.setAmount(userCash.getAmount());
			userTransDetail.setAccountId(userCash.getAccountId());
			userTransDetail.setAvaliableAmount(userAccountInfo.getAvailableAmount());
			userTransDetail.setTransDate(userCash.getBeginDate());
			userTransDetail.setBeginTransDate(userCash.getBeginDate());
			userTransDetail.setInOutType(UserTransDetail.TRANS_TYPE_IN);
			userTransDetail.setRemarks("用户提现");
			userTransDetail.setState(UserTransDetail.TRANS_STATE_FAIL);
			userTransDetail.setTrustType(UserTransDetail.TRANS_CASH);
			userTransDetail.setId(IdGen.uuid());
			userTransDetail.setTransId(userCash.getId());
			userTransDetailService.insert(userTransDetail);
		} catch (Exception e) {
			e.printStackTrace();
			addMessage(redirectAttributes, "用户提现拒绝失败");
			return "redirect:" + Global.getAdminPath() + "/withdraw/withdraw/?repage";
		}
		addMessage(redirectAttributes, "用户提现拒绝成功");
		return "redirect:" + Global.getAdminPath() + "/withdraw/withdraw/?repage";
	}

}