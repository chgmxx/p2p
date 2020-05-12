package com.power.platform.weixin.api;


import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.power.platform.weixin.entity.MsgNews;
import com.power.platform.weixin.service.MsgNewsService;
import com.power.platform.weixin.utils.HttpRequestDeviceUtils;


/**
 * 手机微信页面
 * @author lc
 *
 */
@Controller
@RequestMapping(value = "${adminPath}/wechat/wxweb")
public class WxWebController {
	
	@Autowired
	private MsgNewsService msgNewsService;
	
	@RequestMapping(value = "newsread")
	public String newsread(HttpServletRequest request, String id, Model model){
		MsgNews news = msgNewsService.get(id);
		news.setDescription(StringEscapeUtils.unescapeHtml(news.getDescription()));
		model.addAttribute("news", news);
		if(!HttpRequestDeviceUtils.isMobileDevice(request)){
			return "modules/wechat/wxweb/newsReadPc";
		}
		return "modules/wechat/wxweb/newsReadMobile";
	}
	
	/*@RequestMapping(value = "/msg/newsList")
	public ModelAndView pageWebNewsList(HttpServletRequest request,MsgNews searchEntity, Pagination<MsgNews> page){
		ModelAndView mv = new ModelAndView("wxweb/newsListMobile");
		List<MsgNewsVO> pageList = msgNewsService.pageWebNewsList(searchEntity,page);
		mv.addObject("pageList", pageList);
		return mv;
	}*/
	
	/*@RequestMapping(value = "/jssdk")
	public ModelAndView jssdk(HttpServletRequest request,String api){
		ModelAndView mv = new ModelAndView("wxweb/jssdk");
		if(!StringUtils.isBlank(api)){
			mv.addObject("api", api);
		}
		return mv;
	}
	
	@RequestMapping(value = "/sendmsg")
	public ModelAndView sendmsg(HttpServletRequest request){
		ModelAndView mv = new ModelAndView("wxweb/sendmsg");
		//拦截器已经处理了缓存,这里直接取
		String openid = WxMemoryCacheClient.getOpenid(request.getSession().getId());
		mv.addObject("openid", openid);
		return mv;
	}*/
	
}

