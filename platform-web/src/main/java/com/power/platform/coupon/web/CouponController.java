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
import com.power.platform.coupon.service.CouponInfoService;
import com.power.platform.sys.service.DictService;

/**
 * 浼樻儬鍒哥鐞�
 * @author wangjingsong
 */
@Controller
@RequestMapping(value="${adminPath}/coupon")
public class CouponController extends BaseController {
	
	@Autowired
	private CouponInfoService couponInfoService;
	@Autowired
	private DictService dictService;

	@ModelAttribute
	public CouponInfo get(@RequestParam(required=false) String id) {
		CouponInfo couponInfo = new CouponInfo();
		if (StringUtils.isNotBlank(id)){
			couponInfo = couponInfoService.get(id);
		}
		couponInfo.setCurrentUser(SessionUtils.getUser());
		return couponInfo;
	}
	
	@RequiresPermissions("coupon:info:view")
	@RequestMapping(value="list")
	public String list(CouponInfo couponInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<CouponInfo> page = couponInfoService.findPage(new Page<CouponInfo>(request, response), couponInfo); 
		model.addAttribute("page", page);
		return "modules/coupon/couponList";
	}
	
	@RequiresPermissions("coupon:info:view")
	@RequestMapping(value = "form")
	public String form(CouponInfo couponInfo, Model model) {
		model.addAttribute("couponInfo", couponInfo);
		return "modules/coupon/couponForm";
	}
	
	@RequiresPermissions("coupon:info:edit")
	@RequestMapping(value = "save")
	public String save(CouponInfo couponInfo, Model model, RedirectAttributes redirectAttributes) {
		couponInfo.setState("1");
		couponInfoService.save(couponInfo);
		addMessage(redirectAttributes, "淇濆瓨浼樻儬鍒镐俊鎭垚鍔�");
		return "redirect:"+Global.getAdminPath()+"/coupon/list?repage";
	}
	
	@RequiresPermissions("coupon:info:edit")
	@RequestMapping(value = "delete")
	public String delete(CouponInfo couponInfo, Model model, RedirectAttributes redirectAttributes) {
		couponInfoService.delete(couponInfo);
		addMessage(redirectAttributes, "鍒犻櫎浼樻儬鍒镐俊鎭垚鍔�");
		return "redirect:"+Global.getAdminPath()+"/coupon/list?repage";
	}
	
	
}
