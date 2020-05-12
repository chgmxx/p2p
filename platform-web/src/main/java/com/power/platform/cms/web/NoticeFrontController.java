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
@RequestMapping(value = "/news")
public class NoticeFrontController extends BaseController {

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
	
	
	@RequestMapping(value = "newsdetail")
	public String newsdetail(@RequestParam(required=true) String id,HttpServletRequest request, HttpServletResponse response, Model model) {
		Notice cmsNotice = noticeService.get(id);
		cmsNotice.setText(StringEscapeUtils.unescapeHtml(cmsNotice.getText()));
		model.addAttribute("notice", cmsNotice);
		return "modules/cms/newsdetail";
	}
	
	
}
