/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.credit.zdw;

import java.io.File;
import java.util.List;

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
import com.power.platform.zdw.entity.Search;
import com.power.platform.zdw.entity.User;
import com.power.platform.zdw.entity.ZdwSearchRegisterInfo;
import com.power.platform.zdw.service.ZdwLoginService;
import com.power.platform.zdw.service.ZdwSearchRegisterInfoService;
import com.power.platform.zdw.service.ZdwSearchService;

/**
 * 中登网应收账款和转让记录登记列表Controller
 * 
 * @author Roy
 * @version 2019-07-07
 */
@Controller
@RequestMapping(value = "${adminPath}/zdw/register/zdwSearchRegisterInfo")
public class ZdwSearchRegisterInfoController extends BaseController {

	// 文件地址前缀，用于删除.
	private static final String DEL_PATH = "/data/";

	@Autowired
	private ZdwSearchRegisterInfoService zdwSearchRegisterInfoService;
	@Autowired
	private ZdwSearchService zdwSearchService;
	@Autowired
	private ZdwLoginService zdwLoginService;

	@ModelAttribute
	public ZdwSearchRegisterInfo get(@RequestParam(required = false) String id) {

		ZdwSearchRegisterInfo entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = zdwSearchRegisterInfoService.get(id);
		}
		if (entity == null) {
			entity = new ZdwSearchRegisterInfo();
		}
		return entity;
	}

	@RequiresPermissions("zdw:register:zdwSearchRegisterInfo:view")
	@RequestMapping(value = { "list", "" })
	public String list(ZdwSearchRegisterInfo zdwSearchRegisterInfo, HttpServletRequest request, HttpServletResponse response, Model model) {

		Search s = new Search(null, null);
		s.setGuarantor(zdwSearchRegisterInfo != null ? zdwSearchRegisterInfo.getGuarantorCompanyName() : "");

		// 查询前将历史数据进行删除操作.
		ZdwSearchRegisterInfo entity = new ZdwSearchRegisterInfo();
		entity.setGuarantorCompanyName(zdwSearchRegisterInfo != null ? zdwSearchRegisterInfo.getGuarantorCompanyName() : "");
		List<ZdwSearchRegisterInfo> list = zdwSearchRegisterInfoService.findList(entity);
		for (int i = 0; i < list.size(); i++) {
			// 测试环境文件地址.
			// File file = new File(list.get(i).getRegisterProveFilePath());
			// 生产环境文件地址.
			File file = new File(DEL_PATH + list.get(i).getRegisterProveFilePath());
			if (file.delete()) {
				logger.info("File delete success.");
			} else {
				logger.info("File delete failure.");
			}
			zdwSearchRegisterInfoService.delete(list.get(i));
		}

		// 中登网按主体查询，按担保人名称查询，应收账款质押和转让登记业务.
		String searchResult = zdwSearchService.getReport(s);
		if ("false".equals(searchResult)) {
			model.addAttribute("message", "中登网查询，验证码识别失败！");
		}

		logger.info("中登网按主体查询，按担保人名称查询，应收账款质押和转让登记业务：{}", searchResult);
		if ("重新登录".equals(searchResult)) { // 重新登录.
			User user = new User(null, null);
			String loginResult = zdwLoginService.login(user);
			if ("登录成功".equals(loginResult)) {
				logger.info("中登网重新登陆，结果：{}", loginResult);
				// 登陆成功后进行查询.
				String resultMessage = zdwSearchService.getReport(s);
				logger.info("中登网担保人机构名称查询，结果：{}", resultMessage);
			} else if ("登录其他错误".equals(loginResult)) {
				logger.info("中登网重新登陆，结果：{}", loginResult);
			} else if ("登录异常".equals(loginResult)) {
				logger.info("中登网重新登陆，结果：{}", loginResult);
			} else if ("false".equals(loginResult)) {
				model.addAttribute("message", "中登网登陆，验证码识别失败！");
			}
		} else if ("未知异常".equals(searchResult)) { // 未知异常.
			User user = new User(null, null);
			String loginResult = zdwLoginService.login(user);
			if ("登录成功".equals(loginResult)) {
				logger.info("中登网重新登陆，结果：{}", loginResult);
				// 登陆成功后进行查询.
				String resultMessage = zdwSearchService.getReport(s);
				logger.info("中登网担保人机构名称查询，结果：{}", resultMessage);
			} else if ("登录其他错误".equals(loginResult)) {
				logger.info("中登网重新登陆，结果：{}", loginResult);
			} else if ("登录异常".equals(loginResult)) {
				logger.info("中登网重新登陆，结果：{}", loginResult);
			} else if ("false".equals(loginResult)) {
				model.addAttribute("message", "中登网登陆，验证码识别失败！");
			}
		}
		Page<ZdwSearchRegisterInfo> page = zdwSearchRegisterInfoService.findPage(new Page<ZdwSearchRegisterInfo>(request, response), zdwSearchRegisterInfo);
		model.addAttribute("page", page);
		model.addAttribute("message", "中登网查询成功！");
		return "/modules/credit/zdw/registerList/zdwSearchRegisterInfoList";
	}

	@RequiresPermissions("zdw:register:zdwSearchRegisterInfo:view")
	@RequestMapping(value = "form")
	public String form(ZdwSearchRegisterInfo zdwSearchRegisterInfo, Model model) {

		model.addAttribute("zdwSearchRegisterInfo", zdwSearchRegisterInfo);
		return "modules/zdw/register/zdwSearchRegisterInfoForm";
	}

	@RequiresPermissions("zdw:register:zdwSearchRegisterInfo:edit")
	@RequestMapping(value = "save")
	public String save(ZdwSearchRegisterInfo zdwSearchRegisterInfo, Model model, RedirectAttributes redirectAttributes) {

		if (!beanValidator(model, zdwSearchRegisterInfo)) {
			return form(zdwSearchRegisterInfo, model);
		}
		zdwSearchRegisterInfoService.save(zdwSearchRegisterInfo);
		addMessage(redirectAttributes, "保存中登网应收账款和转让记录登记列表成功");
		return "redirect:" + Global.getAdminPath() + "/zdw/register/zdwSearchRegisterInfo/?repage";
	}

	@RequiresPermissions("zdw:register:zdwSearchRegisterInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(ZdwSearchRegisterInfo zdwSearchRegisterInfo, RedirectAttributes redirectAttributes) {

		zdwSearchRegisterInfoService.delete(zdwSearchRegisterInfo);
		addMessage(redirectAttributes, "删除中登网应收账款和转让记录登记列表成功");
		return "redirect:" + Global.getAdminPath() + "/zdw/register/zdwSearchRegisterInfo/?repage";
	}

}