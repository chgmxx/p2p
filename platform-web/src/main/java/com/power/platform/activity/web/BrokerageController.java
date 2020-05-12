package com.power.platform.activity.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.power.platform.activity.entity.Brokerage;
import com.power.platform.activity.service.BrokerageService;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.web.BaseController;

/**
 * 用户佣金管理Controller
 * 
 * @author lc
 * @version 2016-03-23
 */
@Controller
@RequestMapping(value = "${adminPath}/brokerage")
public class BrokerageController extends BaseController {

	@Autowired
	private BrokerageService brokerageService;

	@ModelAttribute
	public Brokerage get(@RequestParam(required = false) String id) {

		Brokerage entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = brokerageService.get(id);
		}
		if (entity == null) {
			entity = new Brokerage();
		}
		return entity;
	}

	@RequiresPermissions("levelDistribution:view")
	@RequestMapping(value = { "list", "" })
	public String list(Brokerage brokerage, HttpServletRequest request, HttpServletResponse response, Model model) {

		Page<Brokerage> page = brokerageService.findPage(new Page<Brokerage>(request, response), brokerage);
		model.addAttribute("page", page);
		return "modules/brokerage/brokerageList";
	}

}