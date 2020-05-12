package com.power.platform.weixin.web;

import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.power.platform.common.config.Global;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.web.BaseController;
import com.power.platform.weixin.entity.Account;
import com.power.platform.weixin.service.AccountService;
import com.power.platform.weixin.spring.SpringFreemarkerContextPathUtil;

/**
 * URL Token Controller
 * @author lc
 *
 */
@Controller
@RequestMapping(value = "${adminPath}/wechat/cms")
public class WeChatCmsController extends BaseController{

	@Autowired
	private AccountService accountService;
 
	
	
	@ModelAttribute
	public Account get(@RequestParam(required = false) String id) {

		Account entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = accountService.get(id);
		}
		if (entity == null) {
			entity = new Account();
		}
		return entity;
	}
	
	
	@RequiresPermissions("wechat:token:view")
	@RequestMapping(value = { "list", "" })
	public String list(Account account, HttpServletRequest request, HttpServletResponse response, Model model, String save) {
		try {
			List<Account> accounts = accountService.findList(account);
			if(!CollectionUtils.isEmpty(accounts)){
				model.addAttribute("account",accounts.get(0));
			}else{
				model.addAttribute("account",new Account());
			}
			model.addAttribute("cur_nav", "urltoken");
			if(save != null){
				model.addAttribute("successflag",true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "modules/wechat/wxcms/urltoken";
	}
	
	@RequiresPermissions("wechat:token:edit")
	@RequestMapping(value = "save")
	public String save(Account account, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
		
		try {
			String path = SpringFreemarkerContextPathUtil.getBasePath(request);
			String url = request.getScheme() + "://" + request.getServerName() + path + Global.getAdminPath() + "/wxapi/message";
			account.setUrl(url);
			if(account.getId() == null){    //新增
				account.setToken(UUID.randomUUID().toString().replace("-", ""));
			}
			accountService.save(account);
		} catch (Exception e) {
			e.printStackTrace();
		}
		addMessage(redirectAttributes, "已成功获取URL 和 Token，请填写到微信平台中");
		return "redirect:"+Global.getAdminPath()+"/wechat/cms/list?save="+true;
	}
 
 
	
}
