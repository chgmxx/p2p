package com.power.platform.activity.web;

import java.util.ArrayList;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.power.platform.activity.dao.ZtmgPartnerPlatformDao;
import com.power.platform.activity.entity.LevelDistribution;
import com.power.platform.activity.entity.ZtmgPartnerPlatform;
import com.power.platform.activity.service.LevelDistributionService;
import com.power.platform.activity.service.ZtmgPartnerPlatformService;
import com.power.platform.common.config.Global;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.security.shiro.session.SessionUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.web.BaseController;
import com.power.platform.regular.entity.WloanTermInvest;
import com.power.platform.regular.service.WloanTermInvestService;
import com.power.platform.trandetail.entity.UserTransDetail;
import com.power.platform.trandetail.service.UserTransDetailService;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.userinfo.service.UserInfoService;

/**
 * 
 * 类: ZtmgPartnerPlatformController <br>
 * 描述: ZTMG合作方信息Controller. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年6月23日 下午3:22:03
 */
@Controller
@RequestMapping(value = "${adminPath}/partner/ztmgPartnerPlatform")
public class ZtmgPartnerPlatformController extends BaseController {

	@Autowired
	private ZtmgPartnerPlatformService ztmgPartnerPlatformService;
	@Resource
	private ZtmgPartnerPlatformDao ztmgPartnerPlatformDao;
	
	@Autowired
	private UserInfoService userInfoService;
	@Autowired
	private LevelDistributionService levelDistributionService;
	@Autowired
	private WloanTermInvestService wloanTermInvestService;
	@Autowired
	private UserTransDetailService userTransDetailService;

	@ModelAttribute
	public ZtmgPartnerPlatform get(@RequestParam(required = false) String id) {

		ZtmgPartnerPlatform entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = ztmgPartnerPlatformService.get(id);
		}
		if (entity == null) {
			entity = new ZtmgPartnerPlatform();
		}
		return entity;
	}

	@RequiresPermissions("partner:ztmgPartnerPlatform:view")
	@RequestMapping(value = { "list", "" })
	public String list(ZtmgPartnerPlatform ztmgPartnerPlatform, HttpServletRequest request, HttpServletResponse response, Model model) {

		Page<ZtmgPartnerPlatform> page = ztmgPartnerPlatformService.findPage1(new Page<ZtmgPartnerPlatform>(request, response), ztmgPartnerPlatform);
		model.addAttribute("page", page);
		return "modules/activity/partner/ztmgPartnerPlatformList";
	}
	
	
	@RequiresPermissions("partner:ztmgPartnerPlatform:view")
	@RequestMapping(value = "list2")
	public String list2(ZtmgPartnerPlatform ztmgPartnerPlatform,HttpServletRequest request, HttpServletResponse response, Model model) {

		Page<ZtmgPartnerPlatform> page = ztmgPartnerPlatformService.findPage1(new Page<ZtmgPartnerPlatform>(request, response), ztmgPartnerPlatform);
		List<ZtmgPartnerPlatform> newList = new ArrayList<ZtmgPartnerPlatform>();
		if(page!=null){
			List<ZtmgPartnerPlatform> list = page.getList();
			if(list!=null && list.size()>0){
				for (ZtmgPartnerPlatform ztmgPartner : list) {
					ZtmgPartnerPlatform ztmgPartnerPlatform1 = new ZtmgPartnerPlatform();
					List<LevelDistribution> levelDistributionList = levelDistributionService.findListByParentId(ztmgPartner.getId());
					if(levelDistributionList!=null && levelDistributionList.size()>0){
						int investUser = 0;
						double sumMoney  = 0d;
						for (LevelDistribution levelDistribution2 : levelDistributionList) {
							List<WloanTermInvest> investList = wloanTermInvestService.findWloanTermInvestExists(levelDistribution2.getUserId());;
							if(investList!=null && investList.size()>0){
								investUser++;
								for (int i = 0; i < investList.size(); i++) {
									sumMoney = userTransDetailService.findCountAmount(investList.get(i).getUserId(),9);
								}
								
							}
						}
						ztmgPartnerPlatform1.setInvestUser(investUser);//投资人数
						ztmgPartnerPlatform1.setSumMoney(sumMoney);//投资
					}else{
						ztmgPartnerPlatform1.setInvestUser(0);//投资人数
						ztmgPartnerPlatform1.setSumMoney(0d);//投资
					}
					ztmgPartnerPlatform1.setId(ztmgPartner.getId());
					ztmgPartnerPlatform1.setRegistUser(levelDistributionList.size());//注册人数
					ztmgPartnerPlatform1.setPlatformName(ztmgPartner.getPlatformName());
					newList.add(ztmgPartnerPlatform1);
				}
			}
		}
		page.setList(newList);
		model.addAttribute("page", page);
		return "modules/activity/ztmgpartnerplatforminfo/ztmgPartnerPlatformList";
	}
	
	
	@RequiresPermissions("partner:ztmgPartnerPlatform:view")
	@RequestMapping(value = "listForBrokerage")
	public String listForBrokerage(ZtmgPartnerPlatform ztmgPartnerPlatform,HttpServletRequest request, HttpServletResponse response, Model model) {

		Page<ZtmgPartnerPlatform> page = new Page<ZtmgPartnerPlatform>();
		ztmgPartnerPlatform.setPage(page);
		String id = request.getParameter("id");
		page.setList(ztmgPartnerPlatformService.findListForBrokerage(id));
		model.addAttribute("page", page);
		return "modules/activity/ztmgpartnerplatforminfo/ztmgPartnerPlatformList2";
	}
	
	@RequiresPermissions("partner:ztmgPartnerPlatform:view")
	@RequestMapping(value = "listForRegist")
	public String listForRegist(ZtmgPartnerPlatform ztmgPartnerPlatform,HttpServletRequest request, HttpServletResponse response, Model model) {

		Page<UserInfo> page = new Page<UserInfo>(request, response,30);
		page.setOrderBy("a.register_date DESC");
		UserInfo userInfo = new UserInfo();
		userInfo.setPage(page);
		userInfo.setRecommendUserId(ztmgPartnerPlatform.getId());
		page.setList(userInfoService.findListForRegist(userInfo));
		model.addAttribute("page", page);
		return "modules/activity/ztmgpartnerplatforminfo/registUserInfoList";
	}
	

	@RequiresPermissions("partner:ztmgPartnerPlatform:view")
	@RequestMapping(value = "viewForm")
	public String viewForm(ZtmgPartnerPlatform ztmgPartnerPlatform, Model model) {

		model.addAttribute("ztmgPartnerPlatform", ztmgPartnerPlatform);
		return "modules/activity/partner/ztmgPartnerPlatformViewForm";
	}

	@RequiresPermissions("partner:ztmgPartnerPlatform:view")
	@RequestMapping(value = "updateForm")
	public String updateForm(ZtmgPartnerPlatform ztmgPartnerPlatform, Model model) {

		model.addAttribute("ztmgPartnerPlatform", ztmgPartnerPlatform);
		return "modules/activity/partner/ztmgPartnerPlatformUpdateForm";
	}

	@RequiresPermissions("partner:ztmgPartnerPlatform:view")
	@RequestMapping(value = "addForm")
	public String addForm(ZtmgPartnerPlatform ztmgPartnerPlatform, Model model) {

		model.addAttribute("ztmgPartnerPlatform", ztmgPartnerPlatform);
		return "modules/activity/partner/ztmgPartnerPlatformAddForm";
	}

	@RequiresPermissions("partner:ztmgPartnerPlatform:edit")
	@RequestMapping(value = "updateSave")
	public String updateSave(ZtmgPartnerPlatform ztmgPartnerPlatform, Model model, RedirectAttributes redirectAttributes) {

		// 更新时间.
		ztmgPartnerPlatform.setUpdateDate(new Date());
		// 更新人.
		ztmgPartnerPlatform.setUpdateBy(SessionUtils.getUser());
		int flag = ztmgPartnerPlatformDao.update(ztmgPartnerPlatform);
		if (flag == 1) {
			addMessage(redirectAttributes, "修改【中投摩根】合作方信息成功");
		} else {
			addMessage(redirectAttributes, "修改【中投摩根】合作方信息失败");
		}
		return "redirect:" + Global.getAdminPath() + "/partner/ztmgPartnerPlatform/?repage";
	}

	@RequiresPermissions("partner:ztmgPartnerPlatform:edit")
	@RequestMapping(value = "addSave")
	public String addSave(ZtmgPartnerPlatform ztmgPartnerPlatform, Model model, RedirectAttributes redirectAttributes) {

		// 主键ID.
		ztmgPartnerPlatform.setId(IdGen.uuid());
		// 创建时间.
		ztmgPartnerPlatform.setCreateDate(new Date());
		// 创建人.
		ztmgPartnerPlatform.setCreateBy(SessionUtils.getUser());
		// 更新时间.
		ztmgPartnerPlatform.setUpdateDate(new Date());
		// 更新人.
		ztmgPartnerPlatform.setUpdateBy(SessionUtils.getUser());
		int flag = ztmgPartnerPlatformDao.insert(ztmgPartnerPlatform);
		if (flag == 1) {
			addMessage(redirectAttributes, "新增【中投摩根】合作方信息成功");
		} else {
			addMessage(redirectAttributes, "新增【中投摩根】合作方信息失败");
		}
		return "redirect:" + Global.getAdminPath() + "/partner/ztmgPartnerPlatform/?repage";
	}

	@RequiresPermissions("partner:ztmgPartnerPlatform:edit")
	@RequestMapping(value = "delete")
	public String delete(ZtmgPartnerPlatform ztmgPartnerPlatform, RedirectAttributes redirectAttributes) {

		ztmgPartnerPlatformService.delete(ztmgPartnerPlatform);
		addMessage(redirectAttributes, "删除【中投摩根】合作方信息成功");
		return "redirect:" + Global.getAdminPath() + "/partner/ztmgPartnerPlatform/?repage";
	}

}