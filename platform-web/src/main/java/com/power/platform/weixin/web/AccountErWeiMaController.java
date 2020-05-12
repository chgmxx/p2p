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
import com.power.platform.weixin.api.process.MpAccount;
import com.power.platform.weixin.api.process.WxApiClient;
import com.power.platform.weixin.api.process.WxMemoryCacheClient;
import com.power.platform.weixin.entity.AccountErWeiMa;
import com.power.platform.weixin.service.AccountErWeiMaService;
import com.power.platform.weixin.utils.UploadUtil;

/**
 * 微信二维码Controller
 * @author lc
 * @version 2016-01-13
 */
@Controller
@RequestMapping(value = "${adminPath}/wechat/erweima")
public class AccountErWeiMaController extends BaseController {

	@Autowired
	private AccountErWeiMaService accountErWeiMaService;

	@ModelAttribute
	public AccountErWeiMa get(@RequestParam(required = false) String id) {

		AccountErWeiMa entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = accountErWeiMaService.get(id);
		}
		if (entity == null) {
			entity = new AccountErWeiMa();
		}
		return entity;
	}
	
	@RequiresPermissions("wechat:erweima:view")
	@RequestMapping(value = { "list", "" })
	public String list(AccountErWeiMa accountErWeiMa, HttpServletRequest request, HttpServletResponse response, Model model) {
		try {
			Page<AccountErWeiMa> page = accountErWeiMaService.findPage(new Page<AccountErWeiMa>(request, response), accountErWeiMa);
			model.addAttribute("page", page);	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "modules/wechat/wxcms/accountErweimaList";
	}
	
	@RequiresPermissions("wechat:erweima:view")
	@RequestMapping(value = "form")
	public String form(AccountErWeiMa accountErWeiMa, Model model, String type) {
		model.addAttribute("accountErWeiMa", accountErWeiMa);
		model.addAttribute("type", type);
		return "modules/wechat/wxcms/accountErweimaForm";
	}
	
	@RequiresPermissions("wechat:erweima:edit")
	@RequestMapping(value = "save")
	public String save(AccountErWeiMa accountErWeiMa, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, accountErWeiMa)){
			return form(accountErWeiMa, model, "0");
		}
	
		try {
			MpAccount mpAccount = WxMemoryCacheClient.getSingleMpAccount();//获取缓存中的唯一账号
			byte[] qrcode = WxApiClient.createQRCodeLimit(accountErWeiMa.getChannelCode(), mpAccount);
			String url = UploadUtil.byteToImg(Global.getUserfilesBaseDir(), qrcode);
			accountErWeiMa.setFileUrl(url);
			accountErWeiMaService.save(accountErWeiMa);
			addMessage(redirectAttributes, "生成二维码成功");
		} catch (Exception e) {
			e.printStackTrace();
			addMessage(redirectAttributes, "生成二维码失败");
		}
		
		return "redirect:"+Global.getAdminPath()+"/wechat/erweima/list";
	}
	
	@RequiresPermissions("wechat:erweima:edit")
	@RequestMapping(value = "delete")
	public String delete(AccountErWeiMa accountErWeiMa, RedirectAttributes redirectAttributes) {
		accountErWeiMaService.delete(accountErWeiMa);
		addMessage(redirectAttributes, "删除二维码成功");
		return "redirect:"+Global.getAdminPath()+"/wechat/erweima/list";
	} 
	
}
