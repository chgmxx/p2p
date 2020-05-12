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
import com.power.platform.lanmao.search.service.LanMaoSearchUserInfoDataService;
import com.power.platform.lanmao.search.pojo.LanMaoWhiteList;
import com.power.platform.lanmao.search.pojo.LanMaoUserInfo;;
/**
 * 银行托管-账户-Controller.
 * 
 * @author lance
 * @version 2017-10-26
 */
@Controller
@RequestMapping(value = "${adminPath}/lanMao/search/searchUserInfo")
public class LanMaoSearchUserInfoController extends BaseController {

	@Autowired
	private CgbUserAccountService cgbUserAccountService;
	@Autowired
	private LanMaoSearchUserInfoDataService searchUserInfoDataService;

	@ModelAttribute
	public LanMaoWhiteList get(@RequestParam(required = false) String id) {

		LanMaoWhiteList lanMaoWhiteList = new LanMaoWhiteList();
		
		
		
		return lanMaoWhiteList;
	}
	@RequiresPermissions("lanMao:search:searchUserInfo:view")
	@RequestMapping(value = { "list", "" })
	public String list(LanMaoWhiteList lanMaoWhiteList, HttpServletRequest request, HttpServletResponse response, Model model) {

		Map<String, Object> result = null;
//		LanMaoWhiteList lanMaoWhiteList2 = new LanMaoWhiteList();
//		lanMaoWhiteList2.setPlatformUserNo("1509788789721742866");
		LanMaoUserInfo userInfo = new LanMaoUserInfo();
		if(lanMaoWhiteList.getPlatformUserNo()!=null && lanMaoWhiteList.getPlatformUserNo()!="") {
		result = searchUserInfoDataService.searchUserInfo(lanMaoWhiteList.getPlatformUserNo());
		if(result.get("code").equals("调用成功")) {
		model.addAttribute("result", result);	
		}else {
			model.addAttribute("message", result.get("errorMessage"));
			model.addAttribute("result", userInfo);
		}
		}else{			
			model.addAttribute("message", "请输入平台用户编号");
			model.addAttribute("result", userInfo);
		}
		return "modules/lanMao/search/searchUserInfo";
	}

}