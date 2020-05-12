package com.power.platform.weixin.web;


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

import com.power.platform.common.config.Global;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.web.BaseController;
import com.power.platform.weixin.entity.MsgNews;
import com.power.platform.weixin.service.MsgBaseService;
import com.power.platform.weixin.service.MsgNewsService;
import com.power.platform.weixin.spring.SpringFreemarkerContextPathUtil;

/**
 * 图文消息Controller
 * @author lc
 *
 */
@Controller
@RequestMapping(value = "${adminPath}/wechat/msgnews")
public class MsgNewsController extends BaseController{

	@Autowired
	private MsgNewsService msgNewsService;
	
	@Autowired
	private MsgBaseService msgBaseService;
	
	@ModelAttribute
	public MsgNews get(@RequestParam(required = false) String id) {
		MsgNews entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = msgNewsService.get(id);
		}
		if (entity == null) {
			entity = new MsgNews();
		}
		return entity;
	}
	
	
	@RequiresPermissions("wechat:msgnews:view")
	@RequestMapping(value = { "list", "" })
	public String list(MsgNews msgNews, HttpServletRequest request, HttpServletResponse response, Model model ){
		Page<MsgNews> page = msgNewsService.findPage(new Page<MsgNews>(request, response), msgNews);
		model.addAttribute("page",page);
		return "modules/wechat/wxcms/msgNewsList";
	}
	
	@RequiresPermissions("wechat:msgnews:view")
	@RequestMapping(value = "form")
	public String form(MsgNews msgNews,  Model model) {
		msgNews.setDescription(StringEscapeUtils.unescapeHtml(msgNews.getDescription()));
		model.addAttribute("msgNews", msgNews);
		return "modules/wechat/wxcms/msgNewsForm";
	}
	
	@RequiresPermissions("wechat:msgnews:view")
	@RequestMapping(value = "newsread")
	public String newsread(MsgNews msgNews,  Model model) {
		msgNews.setDescription(StringEscapeUtils.unescapeHtml(msgNews.getDescription()));
		model.addAttribute("msgNews", msgNews);
		return "modules/wechat/wxcms/newsread";
	}
	
	@RequiresPermissions("wechat:msgnews:edit")
	@RequestMapping(value = "save")
	public String save(MsgNews msgNews,  Model model, RedirectAttributes redirectAttributes, HttpServletRequest request) {
		try {
			String path = SpringFreemarkerContextPathUtil.getBasePath(request);
			String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + Global.getAdminPath();
			if(!StringUtils.isEmpty(msgNews.getFromurl())){
				String fromUrl = msgNews.getFromurl();
				if(!fromUrl.startsWith("http://")){
					msgNews.setFromurl("http://" + fromUrl);
				}
			}else{
				msgNews.setUrl(url + "/wechat/wxweb/newsread?id="+msgNews.getId()); //设置微信访问的url
			}
			msgNewsService.saveMsgNewsAndBase(msgNews);
			addMessage(redirectAttributes, "保存成功");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:"+Global.getAdminPath()+"/wechat/msgnews/list";
	}
	
	@RequiresPermissions("wechat:msgnews:edit")
	@RequestMapping(value = "delete")
	public String delete(MsgNews msgNews,  RedirectAttributes redirectAttributes) {
		msgNewsService.delete(msgNews);
		addMessage(redirectAttributes, "删除文本信息成功");
		return "redirect:"+Global.getAdminPath()+"/wechat/msgnews/list";
	}
	

}

