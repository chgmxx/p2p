package com.power.platform.activity.web;

import java.util.Date;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.power.platform.activity.entity.ARateCouponDic;
import com.power.platform.activity.entity.UserRateCouponHistory;
import com.power.platform.activity.service.ARateCouponDicService;
import com.power.platform.activity.service.UserRateCouponHistoryService;
import com.power.platform.common.config.Global;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.security.shiro.session.SessionUtils;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.web.BaseController;
import com.power.platform.userinfo.dao.UserInfoDao;
import com.power.platform.userinfo.entity.UserInfo;

@Controller
@RequestMapping(value = "${adminPath}/activity/userRateCouponHistory")
public class UserRateCouponHistoryController extends BaseController {

	@Autowired
	private UserRateCouponHistoryService userRateCouponHistoryService;
	@Autowired
	private ARateCouponDicService aRateCouponDicService;
	@Resource
	private UserInfoDao userInfoDao;

	@ModelAttribute
	public UserRateCouponHistory get(@RequestParam(required = false) String id) {

		UserRateCouponHistory entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = userRateCouponHistoryService.get(id);
		}
		if (entity == null) {
			entity = new UserRateCouponHistory();
		}
		return entity;
	}

	@RequiresPermissions("activity:userRateCouponHistory:view")
	@RequestMapping(value = { "list", "" })
	public String list(UserRateCouponHistory userRateCouponHistory, HttpServletRequest request, HttpServletResponse response, Model model) {

		Page<UserRateCouponHistory> page = userRateCouponHistoryService.findRateCouponPage(new Page<UserRateCouponHistory>(request, response), userRateCouponHistory);
		model.addAttribute("page", page);
		return "modules/activity/rateCoupon/userRateCouponHistoryList";
	}

	@RequiresPermissions("activity:userRateCouponHistory:view")
	@RequestMapping(value = "rechargeForm")
	public String rechargeForm(UserRateCouponHistory userRateCouponHistory, Model model) {

		// 获取加息券全部字典数据.
		List<ARateCouponDic> rateCouponDics = aRateCouponDicService.findAllList();
		userRateCouponHistory.setRateCouponDics(rateCouponDics);

		model.addAttribute("userRateCouponHistory", userRateCouponHistory);
		return "modules/activity/rateCoupon/userRateCouponHistoryRechargeForm";
	}

	@RequiresPermissions("activity:userRateCouponHistory:view")
	@RequestMapping(value = "viewForm")
	public String viewForm(UserRateCouponHistory userRateCouponHistory, Model model) {

		model.addAttribute("userRateCouponHistory", userRateCouponHistory);
		return "modules/activity/rateCoupon/userRateCouponHistoryViewForm";
	}

	@RequiresPermissions("activity:userRateCouponHistory:edit")
	@RequestMapping(value = "save")
	public String save(UserRateCouponHistory userRateCouponHistory, Model model, RedirectAttributes redirectAttributes) {

		if (!beanValidator(model, userRateCouponHistory)) {
			return rechargeForm(userRateCouponHistory, model);
		}

		// 根据手机号码，查询客户账号信息.
		String phone = userRateCouponHistory.getUserInfo().getName();
		UserInfo userInfo = userInfoDao.getUserInfoByPhone(phone);
		if (null == userInfo) {
			addMessage(redirectAttributes, "加息券券充值失败，无效的手机，查无此人");
			return "redirect:" + Global.getAdminPath() + "/activity/userRateCouponHistory/?repage";
		}

		// 根据加息券ID，查询加息券字典数据信息.
		String awardId = userRateCouponHistory.getAwardId();
		ARateCouponDic rateCouponDic = aRateCouponDicService.get(awardId);

		// 客户账号ID.
		userRateCouponHistory.setUserId(userInfo.getId());
		// 逾期时间.
		Date overdueDate = DateUtils.getSpecifiedMonthAfter(new Date(), rateCouponDic.getOverdueDays());
		userRateCouponHistory.setOverdueDate(overdueDate);
		// value.
		userRateCouponHistory.setValue(rateCouponDic.getRate().toString());
		// 类型.
		userRateCouponHistory.setType(UserRateCouponHistoryService.USER_RATE_COUPON_HISTORY_TYPE_2);
		// 创建人.
		userRateCouponHistory.setCreateBy(SessionUtils.getUser());
		// 修改人.
		userRateCouponHistory.setUpdateBy(SessionUtils.getUser());
		userRateCouponHistoryService.save(userRateCouponHistory);
		addMessage(redirectAttributes, "加息券充值成功");
		return "redirect:" + Global.getAdminPath() + "/activity/userRateCouponHistory/?repage";
	}
	
	/**
	 * 批量充值
	 * @param file
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("activity:userRateCouponHistory:edit")
	@RequestMapping(value = "saveall")
	public String saveAll(@RequestParam("file")MultipartFile file, RedirectAttributes redirectAttributes) {

		String message = userRateCouponHistoryService.upload(file);
		addMessage(redirectAttributes, message);
		return "redirect:" + Global.getAdminPath() + "/activity/userRateCouponHistory/?repage";
	}
	
	/**
	 * 批量充值页面
	 * @param userRateCouponHistory
	 * @param model
	 * @return
	 */
	@RequiresPermissions("activity:userRateCouponHistory:view")
	@RequestMapping(value = "rechargeAllForm")
	public String rechargeAllForm(UserRateCouponHistory userRateCouponHistory, Model model) {

		// 获取加息券全部字典数据.
		List<ARateCouponDic> rateCouponDics = aRateCouponDicService.findAllList();
		userRateCouponHistory.setRateCouponDics(rateCouponDics);

		model.addAttribute("userRateCouponHistory", userRateCouponHistory);
		return "modules/activity/rateCoupon/userRateCouponHistoryRechargeAllForm";
	}

	@RequiresPermissions("activity:userRateCouponHistory:edit")
	@RequestMapping(value = "delete")
	public String delete(UserRateCouponHistory userRateCouponHistory, RedirectAttributes redirectAttributes) {

		userRateCouponHistoryService.delete(userRateCouponHistory);
		addMessage(redirectAttributes, "删除加息券成功");
		return "redirect:" + Global.getAdminPath() + "/activity/userRateCouponHistory/?repage";
	}

}