package com.power.platform.weixin.web;

import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.power.platform.common.utils.StringUtils;
import com.power.platform.weixin.entity.MsgBase;
import com.power.platform.weixin.entity.MsgNews;
import com.power.platform.weixin.entity.MsgText;
import com.power.platform.weixin.service.MsgBaseService;
import com.power.platform.weixin.service.MsgNewsService;
import com.power.platform.weixin.service.MsgTextService;

 
/**
 * 消息管理Controller
 * @author lc
 *
 */
@Controller
@RequestMapping(value = "${adminPath}/wechat/msgbase")
public class MsgBaseController{

	@Autowired
	private MsgBaseService msgBaseService;
	
	@Autowired
	private MsgNewsService msgNewsService;
	
	@Autowired
	private MsgTextService msgTextService;

	@ModelAttribute
	public MsgBase get(@RequestParam(required = false) String id) {
		MsgBase entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = msgBaseService.get(id);
		}
		if (entity == null) {
			entity = new MsgBase();
		}
		return entity;
	}
	

	@RequiresPermissions("wechat:msgnews:view")
	@RequestMapping(value = { "menuMsgs", "" })
	public String menuMsgs(Model model){
		//获取所有的图文消息;
		List<MsgNews> newsList = msgNewsService.findList(new MsgNews());
		//获取所有的文本消息;
		List<MsgText> textList = msgTextService.findList(new MsgText());
		
		
		model.addAttribute("textList", textList);
		model.addAttribute("newsList", newsList);
		return "modules/wechat/wxcms/menuMsgs";
	}


}