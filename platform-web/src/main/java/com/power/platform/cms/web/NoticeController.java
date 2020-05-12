/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.cms.web;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.power.platform.cms.entity.Notice;
import com.power.platform.cms.service.NoticeService;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.security.shiro.session.SessionUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.web.BaseController;

/**
 * 公告管理Controller
 * @author lc
 * @version 2015-12-16
 */
@Controller
@RequestMapping(value = "${adminPath}/cms/notice")
public class NoticeController extends BaseController {

	@Autowired 
	private NoticeService noticeService;
	

	@ModelAttribute
	public Notice get(@RequestParam(required=false) String id) {
		Notice notice = new Notice();
		if (StringUtils.isNotBlank(id)){
			try {
				notice = noticeService.get(id);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		notice.setText(StringEscapeUtils.unescapeHtml(notice.getText()));
		notice.setCurrentUser(SessionUtils.getUser());
		return notice;
	}
	
	@RequiresPermissions("cms:notice:view")
	@RequestMapping(value = {"list", ""})
	public String list(Notice notice, HttpServletRequest request, HttpServletResponse response, Model model) {
		
		Page<Notice> page = noticeService.findPage(new Page<Notice>(request, response), notice);
        model.addAttribute("page", page);
        model.addAttribute("notice", notice);
        String pjsp = "";
        switch (notice.getType()) {
        	case 0:
				pjsp = "wapBannerList";
				break;
			case 1:
				pjsp = "bannerList";
				break;
			case 2:
				pjsp = "noticeList";
				break;
			case 3:
				pjsp = "newsList";
				break;
			case 4:
				pjsp = "linksList";
				break;
			case 5:
				pjsp = "cooperativeList";
				break;
			case 6:
				pjsp = "weChatList";
				break;
			case 7:
				pjsp = "educationList";
				break;
			default:
				break;
		}
		return "modules/cms/"+pjsp;
	}
 
	
	@RequiresPermissions("cms:notice:view")
	@RequestMapping(value = "form")
	public String form(Notice notice, String viewType, Model model) {
		notice.setText(StringEscapeUtils.unescapeHtml(notice.getText()));
		String pjsp = "";
		 switch (notice.getType()) {
			case 0:
				pjsp = "bannerForm";
				break;
			case 1:
				pjsp = "bannerForm";
				break;
			case 2:
				pjsp = "noticeForm";
				break;
			case 3:
				pjsp = "newsForm";
				break;
			case 4:
				pjsp = "linksForm";
				break;
			case 5:
				pjsp = "cooperativeForm";
				break;
			case 6:
				pjsp = "weChatForm";
				break;
			case 7:
				pjsp = "educationForm";
				break;
			default:
				break;
		 }
		 if(viewType!=null && viewType.equals("1")){
			 pjsp = pjsp+"Detail";
		 }
		return "modules/cms/"+pjsp;
	}
	
	
	@RequiresPermissions("cms:notice:edit")
	@RequestMapping(value = "save")
	public String save(Notice notice, Model model, RedirectAttributes redirectAttributes) {
			if (!beanValidator(model, notice)){
				return form(notice, "0", model);
			}
			
			if (StringUtils.isEmpty(notice.getId())) {
				notice.setState(1);
			}
			
			if (notice.getUser()==null) {
				notice.setUser(SessionUtils.getUser());
			}
			noticeService.save(notice);
			addMessage(redirectAttributes, "保存"+StringUtils.abbr(notice.getTitle(),50) + "成功");
		return "redirect:" + adminPath + "/cms/notice/?type="+notice.getType();
	}
	
	@RequiresPermissions("cms:notice:edit")
	@RequestMapping(value = "edit")
	public String edit(Notice notice, RedirectAttributes redirectAttributes) {
		if (notice.getState()==1) {
			notice.setState(0);
		} else{
			notice.setState(1);
		}
		noticeService.updateNoticeStatus(notice, true);
		addMessage(redirectAttributes, (notice.getState() == 0? "下线":"上线")+"成功");
		return "redirect:" + adminPath + "/cms/notice/?type="+notice.getType();
	}
	
	@RequiresPermissions("cms:notice:edit")
	@RequestMapping(value = "delete")
	public String delete(Notice notice, @RequestParam(required=false) Boolean isRe, RedirectAttributes redirectAttributes) {
		noticeService.delete(notice);
		addMessage(redirectAttributes, (isRe!=null&&isRe?"":"删除")+"成功");
		return "redirect:" + adminPath + "/cms/notice/?type="+notice.getType();
	}
	
	
}
