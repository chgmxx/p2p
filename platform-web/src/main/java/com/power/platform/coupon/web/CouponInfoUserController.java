package com.power.platform.coupon.web;

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

import com.power.platform.common.config.Global;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.security.shiro.session.SessionUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.web.BaseController;
import com.power.platform.coupon.entity.CouponInfo;
import com.power.platform.coupon.entity.CouponInfoUser;
import com.power.platform.coupon.service.CouponInfoService;
import com.power.platform.coupon.service.CouponInfoUserService;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.userinfo.service.UserInfoService;

/**
 * 客户优惠券信息Controller
 * @author Mr.Jia
 * @version 2016-01-21
 */
@Controller
@RequestMapping(value = "${adminPath}/coupon/couponInfoUser")
public class CouponInfoUserController extends BaseController {

	@Autowired
	private CouponInfoUserService couponInfoUserService;
	@Autowired
	private UserInfoService userInfoService; 
	@Autowired
	private CouponInfoService couponInfoService;
	
	/**
	 * @param id
	 * @return
	 */
	@ModelAttribute
	public CouponInfoUser get(@RequestParam(required=false) String id) {
		CouponInfoUser couponInfoUser = new CouponInfoUser();
		if (StringUtils.isNotBlank(id)){
			couponInfoUser = couponInfoUserService.get(id);
		}
		couponInfoUser.setCurrentUser(SessionUtils.getUser());
		return couponInfoUser;
	}
	
	@RequiresPermissions("coupon:couponInfoUser:view")
	@RequestMapping(value = {"list", ""})
	public String list(CouponInfoUser couponInfoUser, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<CouponInfoUser> page = couponInfoUserService.findPage(new Page<CouponInfoUser>(request, response), couponInfoUser); 
		model.addAttribute("page", page);
		return "modules/coupon/couponInfoUserList";
	}

	/**
	 * 去优惠券发送页面
	 * @param couponInfoUser
	 * @param model
	 * @return
	 */
	@RequiresPermissions("coupon:couponInfoUser:view")
	@RequestMapping(value = "form")
	public String form(CouponInfoUser couponInfoUser, Model model, HttpServletRequest request, HttpServletResponse response) {
		String couponInfoId=request.getParameter("couponInfoId");
		UserInfo userInfo =null;
		if(null!=couponInfoUser.getUserInfo()){
			userInfo=couponInfoUser.getUserInfo();
		}else{
			userInfo =new UserInfo();
		}
		Page<UserInfo> page = userInfoService.findPage(new Page<UserInfo>(request, response), userInfo); 
		model.addAttribute("page", page);
		if(StringUtils.isBlank(couponInfoId)){
			couponInfoId=couponInfoUser.getCouponInfo().getId();
		}
		CouponInfo couponInfo  =couponInfoService.get(couponInfoId);
		model.addAttribute("couponInfo",couponInfo );
		return "modules/coupon/couponInfoUserForm";
	}

	@RequiresPermissions("coupon:couponInfoUser:edit")
	@RequestMapping(value = "save")
	public String save(CouponInfoUser couponInfoUser, Model model, RedirectAttributes redirectAttributes,HttpServletRequest request, HttpServletResponse response) {
		String couponInfoId=request.getParameter("couponInfoId");
		String userInfoIds=request.getParameter("userInfoIds");
		couponInfoUserService.saveCouponInfoUser(userInfoIds, couponInfoId, couponInfoUser.getCurrentUser());
		addMessage(redirectAttributes, "保存客户优惠券信息成功");
		return "redirect:"+Global.getAdminPath()+"/coupon/couponInfoUser/?repage";
	}
	
	@RequiresPermissions("coupon:couponInfoUser:edit")
	@RequestMapping(value = "delete")
	public String delete(CouponInfoUser couponInfoUser, RedirectAttributes redirectAttributes) {
		couponInfoUserService.delete(couponInfoUser);
		addMessage(redirectAttributes, "删除客户优惠券信息成功");
		return "redirect:"+Global.getAdminPath()+"/coupon/couponInfoUser/?repage";
	}

}