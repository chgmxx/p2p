/**
 * 银行托管-账户-Controller.
 */
package com.power.platform.lanmao.web;

import java.text.DecimalFormat;




import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.cunguanbao.api.utils.APIUtils;
import com.power.platform.cgb.dao.CgbUserAccountDao;
import com.power.platform.cgb.entity.CgbUserAccount;
import com.power.platform.cgb.service.CgbUserAccountService;
import com.power.platform.common.config.Global;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.utils.excel.ExportExcel;
import com.power.platform.common.web.BaseController;
import com.power.platform.sys.utils.HttpUtil;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.lanmao.search.service.LanMaoSearchOneTransactionDataService;
import com.power.platform.lanmao.search.pojo.LanMaoUserInfo;
/**
 * 银行托管-账户-Controller.
 * 
 * @author lance
 * @version 2017-10-26
 */
@Controller
@RequestMapping(value = "${adminPath}/lanMao/search/searchOneTransaction")
public class LanMaoSearchOneTransactionController extends BaseController {

	@Autowired
	private CgbUserAccountService cgbUserAccountService;
	@Autowired
	private LanMaoSearchOneTransactionDataService oneTransactionDataService;

	@ModelAttribute
	public CgbUserAccount get(@RequestParam(required = false) String id) {

		CgbUserAccount entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = cgbUserAccountService.get(id);
		}
		if (entity == null) {
			entity = new CgbUserAccount();
		}
		return entity;
	}

	@RequiresPermissions("search:searchOneTransaction:view")
	@RequestMapping(value = { "search", "" })
	public String search(String requestNo, String transactionType,  HttpServletRequest request, HttpServletResponse response, Model model) {

		Map<String, Object> result = null;
		result = oneTransactionDataService.searchOneTransaction(requestNo, transactionType);
		model.addAttribute("result", result);
		return "modules/search/searchProject";
	}

}