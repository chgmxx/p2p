/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.sys.web;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.common.collect.Maps;
import com.power.platform.cgb.entity.CgbUserBankCard;
import com.power.platform.cgb.service.CgbUserBankCardService;
import com.power.platform.common.security.shiro.FormAuthenticationFilter;
import com.power.platform.common.security.shiro.session.SessionDAO;
import com.power.platform.common.utils.CacheUtils;
import com.power.platform.common.utils.CookieUtils;
import com.power.platform.common.utils.EncoderUtil;
import com.power.platform.common.utils.JedisUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.web.BaseController;
import com.power.platform.credit.dao.userinfo.CreditUserInfoDao;
import com.power.platform.credit.dao.userinfo.CreditUserOperatorDao;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.entity.userinfo.CreditUserOperator;
import com.power.platform.lanmao.account.service.PersonBindCardService;
import com.power.platform.lanmao.type.CreditUserOpenAccountEnum;

/**
 * 登录Controller
 * 
 * @author ThinkGem
 * @version 2013-5-31
 */
@Controller
public class LoginController extends BaseController {
	private final static Logger logger = LoggerFactory.getLogger(LoginController.class);

	@Autowired
	private SessionDAO sessionDAO;
	@Autowired
	private CreditUserInfoDao creditUserInfoDao;
	@Autowired
	private CreditUserOperatorDao creditUserOperatorDao;
	@Autowired
	private CgbUserBankCardService cgbUserBankCardService;

	@Resource
	private PersonBindCardService personBindCardService;
	/**
	 * 登录
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "${adminPath}/newlogin", method = RequestMethod.POST)
	public String logins(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {

		String loginName = (String) request.getParameter("username");
		String loginPwd = (String) request.getParameter("password");
		String message = "";
		Date time = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhhmmss");
		Map<String, String> data = new HashMap<String, String>();
		if (!StringUtils.isBlank(loginName) && !StringUtils.isBlank(loginPwd)) {
			// 查询主账户
			CreditUserInfo credituser = new CreditUserInfo();
			credituser.setPhone(loginName);
			credituser.setPwd(EncoderUtil.encrypt(loginPwd));
			List<CreditUserInfo> credituserList = creditUserInfoDao.findList(credituser);
			// 查询操作表
			CreditUserOperator creditOperator = new CreditUserOperator();
			creditOperator.setPhone(loginName);
			creditOperator.setPassword(EncoderUtil.encrypt(loginPwd));
			List<CreditUserOperator> creditOperatorList = creditUserOperatorDao.findList(creditOperator);
			CreditUserInfo creditUserInfo = null;
			if (credituserList != null && credituserList.size() > 0) {// 供
				creditUserInfo = credituserList.get(0);
				logger.info("登录用户信息：{}， {}", creditUserInfo.getOpenAccountState(), creditUserInfo.getIsActivate());
//				if(creditUserInfo!=null&&CreditUserOpenAccountEnum.OPEN_ACCOUNT_STATE_1.getValue().equals(creditUserInfo.getOpenAccountState())&&"FALSE".equals(creditUserInfo.getIsActivate())) {
//					// 测试数据
////					creditUserInfo.setId("100101");
//					// 生成token
//					String token = EncoderUtil.encrypt(sdf.format(time) + creditUserInfo.getId()).replace("+", "a");
//					Map<String, String> cacheLoginedUser = JedisUtils.getMap("cacheLoginedUser");
//					// 系统没有登录用户（一般不会进该方法）
//					if (cacheLoginedUser == null) {
//						cacheLoginedUser = new HashMap<String, String>();
//					}
//					String isexitToken = cacheLoginedUser.get(creditUserInfo.getId());
//					if (isexitToken != null && isexitToken != "") {
//						// 不等于null 获取到原来的token，并且移除
//						JedisUtils.del(isexitToken);
//					}
//					cacheLoginedUser.put(creditUserInfo.getId(), token);
//					JedisUtils.setMap("cacheLoginedUser", cacheLoginedUser, 1200);
//					// 设置缓存
//					String a = JedisUtils.set(token, creditUserInfo.getId(), 1200);
//					//企业激活--构造数据
////					data = personBindCardService.activateStockedUser(creditUserInfo.getId());
//					
//					message = "企业激活";
//					model.addAttribute(FormAuthenticationFilter.DEFAULT_MESSAGE_PARAM, message);
//					model.addAttribute("token",token);
//					return "modules/sys/memberActivation";
//				}
				
				
				message = "企业管理用户登录成功";
				model.addAttribute("id", creditUserInfo.getId());
				model.addAttribute("creditUserInfo", creditUserInfo);
				model.addAttribute("userName", creditUserInfo.getEnterpriseFullName() == null ? "未开户" : creditUserInfo.getEnterpriseFullName());
				if (creditUserInfo.getCreditUserType().equals("11")) {
					model.addAttribute("limit", "1");// 核心企业
					if (CreditUserOpenAccountEnum.OPEN_ACCOUNT_STATE_1.getValue().equals(creditUserInfo.getOpenAccountState())) {
						model.addAttribute("isOpeningAnAccount", "1"); // 已开户.
					} else {
						model.addAttribute("isOpeningAnAccount", "2"); // 未开户.
					}
					model.addAttribute("creditUserInfo", creditUserInfo);

					// CgbUserBankCard userBankCard = cgbUserBankCardService.findByUserId1(creditUserInfo.getId());
					// if (userBankCard != null) {
					// if (userBankCard.getState() != null) {
					// if (userBankCard.getState().equals(CgbUserBankCard.CERTIFY_NO)) { // 未认证开户.
					// model.addAttribute("isOpeningAnAccount", "2"); // 未开户.
					// model.addAttribute("creditUserInfo", creditUserInfo);
					// } else if (userBankCard.getState().equals(CgbUserBankCard.CERTIFY_YES)) { // 已认证开户.
					// model.addAttribute("isOpeningAnAccount", "1"); // 已开户.
					// model.addAttribute("creditUserInfo", creditUserInfo);
					// } else {
					// model.addAttribute("isOpeningAnAccount", "2"); // 未开户.
					// model.addAttribute("creditUserInfo", creditUserInfo);
					// }
					// } else {
					// model.addAttribute("isOpeningAnAccount", "2"); // 未开户.
					// model.addAttribute("creditUserInfo", creditUserInfo);
					// }
					// } else {
					// model.addAttribute("isOpeningAnAccount", "2"); // 未开户.
					// model.addAttribute("creditUserInfo", creditUserInfo);
					// }
				} else if (creditUserInfo.getCreditUserType().equals("05")) {
					model.addAttribute("limit", "5");// 平台户  用户id , 账户id， 银行卡cgb_user_bank
					if (CreditUserOpenAccountEnum.OPEN_ACCOUNT_STATE_1.getValue().equals(creditUserInfo.getOpenAccountState())) {
						model.addAttribute("isOpeningAnAccount", "1"); // 已开户.
					} else {
						model.addAttribute("isOpeningAnAccount", "2"); // 未开户.
					}
					model.addAttribute("creditUserInfo", creditUserInfo);
					// CgbUserBankCard userBankCard = cgbUserBankCardService.findByUserId1(creditUserInfo.getId());
					// if (userBankCard != null) {
					// if (userBankCard.getState() != null) {
					// if (userBankCard.getState().equals(CgbUserBankCard.CERTIFY_NO)) { // 未认证开户.
					// model.addAttribute("isOpeningAnAccount", "2"); // 未开户.
					// model.addAttribute("creditUserInfo", creditUserInfo);
					// } else if (userBankCard.getState().equals(CgbUserBankCard.CERTIFY_YES)) { // 已认证开户.
					// model.addAttribute("isOpeningAnAccount", "1"); // 已开户.
					// model.addAttribute("creditUserInfo", creditUserInfo);
					// } else {
					// model.addAttribute("isOpeningAnAccount", "2"); // 未开户.
					// model.addAttribute("creditUserInfo", creditUserInfo);
					// }
					// } else {
					// model.addAttribute("isOpeningAnAccount", "2"); // 未开户.
					// model.addAttribute("creditUserInfo", creditUserInfo);
					// }
					// } else {
					// model.addAttribute("isOpeningAnAccount", "2"); // 未开户.
					// model.addAttribute("creditUserInfo", creditUserInfo);
					// }
					// 15 房产抵押  属于安心投的借款人 ；02 核心 企业下属供应商 ；
				} else if (creditUserInfo.getCreditUserType().equals("02") || creditUserInfo.getCreditUserType().equals("15")) {
					model.addAttribute("limit", "2");// 供应商
					if (CreditUserOpenAccountEnum.OPEN_ACCOUNT_STATE_1.getValue().equals(creditUserInfo.getOpenAccountState())) {
						model.addAttribute("isOpeningAnAccount", "1"); // 已开户.
					} else {
						model.addAttribute("isOpeningAnAccount", "2"); // 未开户.
					}
					model.addAttribute("creditUserInfo", creditUserInfo);
					// CgbUserBankCard userBankCard = cgbUserBankCardService.findByUserId1(creditUserInfo.getId());
					// if (userBankCard != null) {
					// if (userBankCard.getState() != null) {
					// if (userBankCard.getState().equals(CgbUserBankCard.CERTIFY_NO)) { // 未认证开户.
					// model.addAttribute("isOpeningAnAccount", "2"); // 未开户.
					// model.addAttribute("creditUserInfo", creditUserInfo);
					// } else if (userBankCard.getState().equals(CgbUserBankCard.CERTIFY_YES)) { // 已认证开户.
					// model.addAttribute("isOpeningAnAccount", "1"); // 已开户.
					// model.addAttribute("creditUserInfo", creditUserInfo);
					// } else {
					// model.addAttribute("isOpeningAnAccount", "2"); // 未开户.
					// model.addAttribute("creditUserInfo", creditUserInfo);
					// }
					// } else {
					// model.addAttribute("isOpeningAnAccount", "2"); // 未开户.
					// model.addAttribute("creditUserInfo", creditUserInfo);
					// }
					// } else {
					// model.addAttribute("isOpeningAnAccount", "2"); // 未开户.
					// model.addAttribute("creditUserInfo", creditUserInfo);
					// }
				}
				if (creditUserInfo.getFirstLogin() == null || creditUserInfo.getFirstLogin() == 0) {
					model.addAttribute("creditUser", creditUserInfo);
					return "modules/sys/updatePwd";
				} else {
					return "modules/sys/common";
				}

			} else if (creditOperatorList != null && creditOperatorList.size() > 0) {// 企业操作人
				if (creditOperatorList.get(0).getState().equals("0")) {
					message = "此账户暂不可用";
					model.addAttribute(FormAuthenticationFilter.DEFAULT_MESSAGE_PARAM, message);
					return "modules/sys/sysLogin";
				} else {
					message = "企业操作用户登录成功";
					model.addAttribute("id", creditOperatorList.get(0).getCreditUserId());
					CreditUserInfo userInfo = creditUserInfoDao.get(creditOperatorList.get(0).getCreditUserId());
					if (userInfo != null) {
						model.addAttribute("userName", userInfo.getEnterpriseFullName() == null ? "未开户" : userInfo.getEnterpriseFullName());
					}
					model.addAttribute("limit", "3");// 操作员
					if (userInfo.getFirstLogin() == null || userInfo.getFirstLogin() == 0) {
						model.addAttribute("creditUser", userInfo);
						return "modules/sys/updatePwd";
					} else {
						model.addAttribute("creditUserInfo", userInfo);
						return "modules/sys/common";
					}
				}
			} else {
				message = "用户或密码错误, 请重试";
				model.addAttribute(FormAuthenticationFilter.DEFAULT_MESSAGE_PARAM, message);
				return "modules/sys/sysLogin";
			}
		} else {
			return "modules/sys/sysLogin";
		}
	}

	@RequestMapping(value = "${adminPath}/company", method = RequestMethod.POST)
	public String company(HttpServletRequest request, HttpServletResponse response, Model model) {

		return "modules/sys/companyInfo";
	}

	/**
	 * 管理登录
	 */
	@RequestMapping(value = "${adminPath}/login", method = RequestMethod.GET)
	public String login(HttpServletRequest request, HttpServletResponse response, Model model) {

		return "modules/sys/sysLogin";
	}

	/**
	 * 获取主题方案
	 */
	@RequestMapping(value = "/theme/{theme}")
	public String getThemeInCookie(@PathVariable String theme, HttpServletRequest request, HttpServletResponse response) {

		if (StringUtils.isNotBlank(theme)) {
			CookieUtils.setCookie(response, "theme", theme);
		} else {
			theme = CookieUtils.getCookie(request, "theme");
		}
		return "redirect:" + request.getParameter("url");
	}

	/**
	 * 是否是验证码登录
	 * 
	 * @param useruame
	 *            用户名
	 * @param isFail
	 *            计数加1
	 * @param clean
	 *            计数清零
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static boolean isValidateCodeLogin(String useruame, boolean isFail, boolean clean) {

		Map<String, Integer> loginFailMap = (Map<String, Integer>) CacheUtils.get("loginFailMap");
		if (loginFailMap == null) {
			loginFailMap = Maps.newHashMap();
			CacheUtils.put("loginFailMap", loginFailMap);
		}
		Integer loginFailNum = loginFailMap.get(useruame);
		if (loginFailNum == null) {
			loginFailNum = 0;
		}
		if (isFail) {
			loginFailNum++;
			loginFailMap.put(useruame, loginFailNum);
		}
		if (clean) {
			loginFailMap.remove(useruame);
		}
		return loginFailNum >= 3;
	}
}
