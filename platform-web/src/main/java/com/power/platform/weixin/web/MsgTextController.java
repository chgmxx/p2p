package com.power.platform.weixin.web;

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
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.web.BaseController;
import com.power.platform.weixin.entity.MsgText;
import com.power.platform.weixin.service.MsgBaseService;
import com.power.platform.weixin.service.MsgTextService;

/**
 * 文本消息Controller
 * @author lc
 *
 */
@Controller
@RequestMapping(value = "${adminPath}/wechat/msgtext")
public class MsgTextController extends BaseController{

	@Autowired
	private MsgTextService msgTextService;
	
	@Autowired
	private MsgBaseService msgBaseService;
	
	@ModelAttribute
	public MsgText get(@RequestParam(required = false) String id) {
		MsgText entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = msgTextService.get(id);
		}
		if (entity == null) {
			entity = new MsgText();
		}
		return entity;
	}
	
	
	@RequiresPermissions("wechat:msgtext:view")
	@RequestMapping(value = { "list", "" })
	public String list(MsgText msgText, HttpServletRequest request, HttpServletResponse response, Model model ){
		Page<MsgText> page = msgTextService.findPage(new Page<MsgText>(request, response), msgText);
		model.addAttribute("page",page);
		return "modules/wechat/wxcms/msgTextList";
	}
	
	@RequiresPermissions("wechat:msgtext:view")
	@RequestMapping(value = "form")
	public String form(MsgText msgText, Model model) {
		model.addAttribute("msgText", msgText);
		return "modules/wechat/wxcms/msgTextForm";
	}
	
	@RequiresPermissions("wechat:msgtext:edit")
	@RequestMapping(value = "save")
	public String save(MsgText msgText, Model model, RedirectAttributes redirectAttributes) {
		try {
			msgTextService.saveMsgTextAndBase(msgText);
			addMessage(redirectAttributes, "保存成功");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:"+Global.getAdminPath()+"/wechat/msgtext/list";
	}
	
	@RequiresPermissions("wechat:msgtext:edit")
	@RequestMapping(value = "delete")
	public String delete(MsgText msgText, RedirectAttributes redirectAttributes) {
		msgTextService.delete(msgText);
		addMessage(redirectAttributes, "删除文本信息成功");
		return "redirect:"+Global.getAdminPath()+"/wechat/msgtext/list";
	}

}

