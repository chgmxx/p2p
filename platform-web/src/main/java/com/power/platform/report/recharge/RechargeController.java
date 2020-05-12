package com.power.platform.report.recharge;

import java.text.DecimalFormat;
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
import com.power.platform.credit.dao.userinfo.CreditUserInfoDao;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.pay.recharge.dao.UserRechargeDao;
import com.power.platform.pay.recharge.entity.UserRecharge;
import com.power.platform.pay.recharge.service.UserRechargeService;
import com.power.platform.userinfo.dao.UserInfoDao;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.userinfo.service.UserInfoService;

/**
 * 
 * 类: RechargeController <br>
 * 描述: 充值Controller，列表展示及Excel导出功能. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年9月7日 上午9:53:36
 */
@Controller
@RequestMapping(value = "${adminPath}/report/recharge")
public class RechargeController extends BaseController {

	@Autowired
	private UserRechargeService userRechargeService;
	@Autowired
	private UserInfoService userInfoService;
	@Resource
	private UserRechargeDao userRechargeDao;
	@Resource
	private CreditUserInfoDao creditUserInfoDao;
	@Resource
	private UserInfoDao userInfoDao;

	@ModelAttribute
	public UserRecharge get(@RequestParam(required = false) String id) {

		UserRecharge entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = userRechargeService.get(id);
		}
		if (entity == null) {
			entity = new UserRecharge();
		}
		return entity;
	}

	@RequiresPermissions("report:recharge:view")
	@RequestMapping(value = { "list", "" })
	public String list(UserRecharge userRecharge, HttpServletRequest request, HttpServletResponse response, Model model) {

		// 出借人.
		UserInfo userInfoByPhone = userInfoDao.getUserInfoByPhone(userRecharge.getPhone());
		if (userInfoByPhone != null) {
			userRecharge.setUserId(userInfoByPhone.getId());
		}
		// 借款人.
		CreditUserInfo creditUserInfoByPhone = creditUserInfoDao.getCreditUserInfoByPhone(userRecharge.getPhone());
		if (creditUserInfoByPhone != null) {
			userRecharge.setCreditUserId(creditUserInfoByPhone.getId());
		}
		Page<UserRecharge> page = userRechargeService.findExcelReportPage(new Page<UserRecharge>(request, response), userRecharge);
		// 分页数据，每页30条.
		for (UserRecharge entity : page.getList()) {
			// 格式化充值金额.
			entity.setFormatAmountStr(new DecimalFormat("0.00").format(entity.getAmount()));
			// 客户ID.
			String userId = entity.getUserId();
			// 出借人.
			UserInfo userInfo = userInfoService.get(userId);
			if (null == userInfo) {
				userInfo = userInfoService.getCgb(userId);
				// 借款人.
				if (null == userInfo) {
					CreditUserInfo creditUserInfo = creditUserInfoDao.get(userId);
					if (creditUserInfo != null) {
						entity.setUserTypeStr("<b style='color:#A67D3D;'>借款人</b>");
						UserInfo u = new UserInfo();
						u.setName(CommonStringUtils.mobileEncrypt(creditUserInfo.getPhone()));
						u.setRealName(CommonStringUtils.replaceNameX(creditUserInfo.getName()));
						entity.setUserInfo(u);
					}
				} else {
					entity.setUserTypeStr("<b>出借人</b>");
					userInfo.setName(CommonStringUtils.mobileEncrypt(userInfo.getName()));
					userInfo.setRealName(CommonStringUtils.replaceNameX(userInfo.getRealName()));
					entity.setUserInfo(userInfo);
				}
			} else {
				entity.setUserTypeStr("<b>出借人</b>");
				userInfo.setName(CommonStringUtils.mobileEncrypt(userInfo.getName()));
				userInfo.setRealName(CommonStringUtils.replaceNameX(userInfo.getRealName()));
				entity.setUserInfo(userInfo);
			}
		}
		model.addAttribute("page", page);
		return "modules/report/rechargeList";
	}

	@RequiresPermissions("report:recharge:view")
	@RequestMapping(value = "export", method = RequestMethod.POST)
	public String exportFile(UserRecharge userRecharge, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {

		try {

			// 出借人.
			UserInfo userInfoByPhone = userInfoDao.getUserInfoByPhone(userRecharge.getPhone());
			if (userInfoByPhone != null) {
				userRecharge.setUserId(userInfoByPhone.getId());
			}
			// 借款人.
			CreditUserInfo creditUserInfoByPhone = creditUserInfoDao.getCreditUserInfoByPhone(userRecharge.getPhone());
			if (creditUserInfoByPhone != null) {
				userRecharge.setCreditUserId(creditUserInfoByPhone.getId());
			}

			String fileName = "财务报表统计_充值" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
			List<UserRecharge> list = userRechargeDao.findExcelReportList(userRecharge);
			for (UserRecharge entity : list) {
				// 格式化充值金额.
				entity.setFormatAmountStr(new DecimalFormat("0.00").format(entity.getAmount()));
				// 客户ID.
				String userId = entity.getUserId();
				// 出借人.
				UserInfo userInfo = userInfoService.get(userId);
				if (null == userInfo) {
					userInfo = userInfoService.getCgb(userId);
					// 借款人.
					if (null == userInfo) {
						CreditUserInfo creditUserInfo = creditUserInfoDao.get(userId);
						if (creditUserInfo != null) {
							entity.setUserTypeStr("借款人");
							UserInfo u = new UserInfo();
							u.setName(creditUserInfo.getPhone());
							u.setRealName(creditUserInfo.getName());
							entity.setUserInfo(u);
						}
					} else {
						entity.setUserTypeStr("出借人");
						entity.setUserInfo(userInfo);
					}
				} else {
					entity.setUserTypeStr("出借人");
					entity.setUserInfo(userInfo);
				}
			}
			new ExportExcel("财务报表统计_充值", UserRecharge.class).setDataList(list).write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出失败！失败信息：" + e.getMessage());
		}
		return "redirect:" + adminPath + "/report/recharge/list?repage";
	}

}