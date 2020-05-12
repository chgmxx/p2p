/**
 * 银行托管-账户-Controller.
 */
package com.power.platform.lanmao.web;

import java.text.SimpleDateFormat;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.power.platform.common.web.BaseController;
import com.power.platform.lanmao.search.pojo.LanMaoWhiteList;
import com.power.platform.lanmao.search.service.LanMaoReconciliationService;;
/**
 * 银行托管-账户-Controller.
 * 
 * @author lance
 * @version 2017-10-26
 */
@Controller
@RequestMapping(value = "${adminPath}/lanMao/fileDown/fileDown")
public class LanMaoFileDownController extends BaseController {

	private static final Logger LOG = LoggerFactory.getLogger(LanMaoFileDownController.class);
	@Autowired
	private LanMaoReconciliationService reconciliationService;
	

	@ModelAttribute
	public LanMaoWhiteList get(@RequestParam(required = false) String id) {

		LanMaoWhiteList lanMaoWhiteList = new LanMaoWhiteList();
		
		return lanMaoWhiteList;
	}
	/**
	 * @Description: 对账文件确认
	 * @param lanMaoWhiteList
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequiresPermissions("lanMao:fileDown:fileDown:view")
	@RequestMapping(value = { "list", "" })
	public String list(LanMaoWhiteList lanMaoWhiteList, HttpServletRequest request, HttpServletResponse response, Model model) {
		String time = null;
		if(lanMaoWhiteList.getStartTime()!=null) {
			time = new SimpleDateFormat("yyyyMMdd").format(lanMaoWhiteList.getStartTime());
        }
		if(time != null) {
		  Map<String, Object> result = reconciliationService.confirmCheckFile(time);
		  if("SUCCESS".equals(result.get("status"))) {
		   model.addAttribute("message", result.get("status"));
		   model.addAttribute("code", result.get("code"));
		  }else {
		   model.addAttribute("message", result.get("errorMessage"));
		   model.addAttribute("code", result.get("errorCode"));
		  }
	  }
	  else {
	   model.addAttribute("message", "请选择时间");
	  }
		return "modules/lanMao/fileDown/fileDown";
	}

	/**
	 * @Description: 对账文件下载
	 * @param lanMaoWhiteList
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequiresPermissions("lanMao:fileDown:fileDown:view")
	 @RequestMapping(value = "down")
	 public String down(LanMaoWhiteList lanMaoWhiteList, HttpServletRequest request, HttpServletResponse response, Model model) {
		String time = null;
		if(lanMaoWhiteList.getEndTime()!=null) {
			time = new SimpleDateFormat("yyyyMMdd").format(lanMaoWhiteList.getEndTime());
        }
		
		Map<String, Object> result = reconciliationService.downLoadCheckFile(time);
	    model.addAttribute("message2", lanMaoWhiteList.getPlatformUserNo());  
	    model.addAttribute("result", "对账下载完成"); 
	    return "modules/lanMao/fileDown/fileDown";
	 }
}