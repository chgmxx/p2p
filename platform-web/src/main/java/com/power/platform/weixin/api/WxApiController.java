package com.power.platform.weixin.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.power.platform.common.config.Global;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.utils.DateUtil;
import com.power.platform.common.web.BaseController;
import com.power.platform.weixin.api.process.ErrCode;
import com.power.platform.weixin.api.process.MpAccount;
import com.power.platform.weixin.api.process.MsgXmlUtil;
import com.power.platform.weixin.api.process.WxApiClient;
import com.power.platform.weixin.api.process.WxMemoryCacheClient;
import com.power.platform.weixin.api.service.MyService;
import com.power.platform.weixin.api.vo.MsgRequest;
import com.power.platform.weixin.api.vo.TemplateMessage;
import com.power.platform.weixin.entity.AccountFans;
import com.power.platform.weixin.entity.AccountMenuGroup;
import com.power.platform.weixin.service.AccountMenuGroupService;
import com.power.platform.weixin.utils.SignUtil;

/**
 * 微信与开发者服务器交互接口
 * @author lc
 *
 */
@Controller
@RequestMapping(value = "${adminPath}/wxapi")
public class WxApiController extends BaseController {
	
		@Autowired
		private MyService myService;

		@Autowired
		private AccountMenuGroupService accountMenuGroupService;
		
		@RequestMapping(value = { "list", "" })
		public String list(AccountMenuGroup accountMenuGroup, HttpServletRequest request, HttpServletResponse response, Model model){
			Page<AccountMenuGroup> page = accountMenuGroupService.findPage(new Page<AccountMenuGroup>(request, response), accountMenuGroup);
			model.addAttribute("page", page);
			return "modules/wechat/wxcms/accountMenuGroupList";
		}
	
		//创建微信公众账号菜单
		@RequestMapping(value = "publishMenu")
		public String publishMenu(RedirectAttributes redirectAttributes, String gid, Model model) {
			JSONObject rstObj = null;
			MpAccount mpAccount = WxMemoryCacheClient.getSingleMpAccount();
			if(mpAccount != null){
				rstObj = myService.publishMenu(gid, mpAccount);
				if(rstObj != null){//成功，更新菜单组
					if(rstObj.containsKey("menu_id")){
						addMessage(redirectAttributes, "创建菜单成功");
						return "redirect:"+Global.getAdminPath()+"/wxapi/list?repage";
					}else if(rstObj.containsKey("errcode") && rstObj.getInt("errcode") == 0){
						addMessage(redirectAttributes, "创建菜单成功");
						return "redirect:"+Global.getAdminPath()+"/wxapi/list?repage";
					}
				}
			}  
			String failureMsg = "创建菜单失败，请检查菜单：可创建最多3个一级菜单，每个一级菜单下可创建最多5个二级菜单。";
			if(rstObj != null){
				failureMsg += ErrCode.errMsg(rstObj.getInt("errcode"));
			}
			addMessage(redirectAttributes, failureMsg);	
			return "redirect:"+Global.getAdminPath()+"/wxapi/list?repage";
		}
	
	
		//删除微信公众账号菜单
		@RequestMapping(value = "deleteMenu")
		public String deleteMenu(RedirectAttributes redirectAttributes, Model model) {
			JSONObject rstObj = null;
			MpAccount mpAccount = WxMemoryCacheClient.getSingleMpAccount();//获取缓存中的唯一账号
			if(mpAccount != null){
				rstObj = myService.deleteMenu(mpAccount);
				if(rstObj != null && rstObj.getInt("errcode") == 0){
					addMessage(redirectAttributes, "删除菜单成功");
					return "redirect:" + Global.getAdminPath() + "/wechat/accountmenugroup/list?repage";
				}
			} 
			String failureMsg = "删除菜单失败";
			if(rstObj != null){
				failureMsg += ErrCode.errMsg(rstObj.getInt("errcode"));
			}
			addMessage(redirectAttributes, failureMsg);
			return "redirect:"+Global.getAdminPath()+"/wechat/accountmenugroup/list?repage";
		}
		
		//获取用户列表
		@RequestMapping(value = "syncAccountFansList")
		public String syncAccountFansList(Model model,  RedirectAttributes redirectAttributes){
			MpAccount mpAccount = WxMemoryCacheClient.getSingleMpAccount();//获取缓存中的唯一账号
			
			if(mpAccount != null){
				boolean flag = myService.syncAccountFansList(mpAccount);
				if(flag){
					addMessage(redirectAttributes, "获取用户列表成功");
					return "redirect:"+Global.getAdminPath()+"/wechat/accountfans/list";
				}
			}
			addMessage(redirectAttributes, "获取用户列表失败");
			return "redirect:"+Global.getAdminPath()+"/wechat/accountfans/list";
		}
	
		//根据用户的ID更新用户信息
		@RequestMapping(value = "syncAccountFans")
		public String syncAccountFans(String openId, Model model,  RedirectAttributes redirectAttributes){
			MpAccount mpAccount = WxMemoryCacheClient.getSingleMpAccount();//获取缓存中的唯一账号
			if(mpAccount != null){
				AccountFans fans = myService.syncAccountFans(openId,mpAccount,true);//同时更新数据库
				if(fans != null){
					model.addAttribute("fans", fans);
					return "modules/wechat/wxcms/accountFansInfo";
				}
			}
			addMessage(redirectAttributes, "获取用户列表失败");
			return "redirect:"+Global.getAdminPath()+"/wechat/accountfans/list";
		}
	
		/**
		 * 发送客服消息
		 * @param openId ： 粉丝的openid
		 * @param content ： 消息内容
		 * @return
		 */
		@RequestMapping(value = "sendCustomTextMsg", method = RequestMethod.POST)
		public void sendCustomTextMsg(HttpServletRequest request,HttpServletResponse response,String openid){
			MpAccount mpAccount = WxMemoryCacheClient.getSingleMpAccount();//获取缓存中的唯一账号
			String content = "微信派官方测试客服消息";
			JSONObject result = WxApiClient.sendCustomTextMessage(openid, content, mpAccount);
			try {
				if(result.getInt("errcode") != 0){
					response.getWriter().write("send failure");
				}else{
					response.getWriter().write("send success");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		/**
		 * GET请求：进行URL、Tocken 认证；
		 * 1. 将token、timestamp、nonce三个参数进行字典序排序
		 * 2. 将三个参数字符串拼接成一个字符串进行sha1加密
		 * 3. 开发者获得加密后的字符串可与signature对比，标识该请求来源于微信
		 */
		@RequestMapping(value = "message",  method = RequestMethod.GET)
		public @ResponseBody String doGet(HttpServletRequest request,@PathVariable String account) {
			//如果是多账号，根据url中的account参数获取对应的MpAccount处理即可
			MpAccount mpAccount = WxMemoryCacheClient.getSingleMpAccount();//获取缓存中的唯一账号
			if(mpAccount != null){
				String token = mpAccount.getToken();//获取token，进行验证；
				String signature = request.getParameter("signature");// 微信加密签名
				String timestamp = request.getParameter("timestamp");// 时间戳
				String nonce = request.getParameter("nonce");// 随机数
				String echostr = request.getParameter("echostr");// 随机字符串
				
				// 校验成功返回  echostr，成功成为开发者；否则返回error，接入失败
				if (SignUtil.validSign(signature, token, timestamp, nonce)) {
					return echostr;
				}
			}
			return "error";
		}
		
		/**
		 * POST 请求：进行消息处理；
		 * */
		@RequestMapping(value = "message", method = RequestMethod.POST)
		public @ResponseBody String doPost(HttpServletRequest request,@PathVariable String account,HttpServletResponse response) {
			//处理用户和微信公众账号交互消息
			MpAccount mpAccount = WxMemoryCacheClient.getSingleMpAccount();
			try {
				MsgRequest msgRequest = MsgXmlUtil.parseXml(request);//获取发送的消息
				return myService.processMsg(msgRequest,mpAccount);
			} catch (Exception e) {
				e.printStackTrace();
				return "error";
			}
		}
	
		//获取openid
		@RequestMapping(value = "oauthOpenid")
		public ModelAndView oauthOpenid(HttpServletRequest request){
			MpAccount mpAccount = WxMemoryCacheClient.getSingleMpAccount();//获取缓存中的唯一账号
			if(mpAccount != null){
				ModelAndView mv = new ModelAndView("wxweb/oauthOpenid");
				//拦截器已经处理了缓存,这里直接取
				String openid = WxMemoryCacheClient.getOpenid(request.getSession().getId());
				AccountFans fans = myService.syncAccountFans(openid,mpAccount,false);//同时更新数据库
				mv.addObject("openid", openid);
				mv.addObject("fans", fans);
				return mv;
			}else{
				ModelAndView mv = new ModelAndView("common/failureMobile");
				mv.addObject("message", "OAuth获取openid失败");
				return mv;
			}
		}
		/**
		 * 发送模板消息
		 * @param openId
		 * @param content
		 * @return
		 */
		@RequestMapping(value = "sendTemplateMessage", method = RequestMethod.POST)
		public void sendTemplateMessage(HttpServletRequest request, HttpServletResponse response, String openid){
			MpAccount mpAccount = WxMemoryCacheClient.getSingleMpAccount();//获取缓存中的唯一账号
			TemplateMessage tplMsg = new TemplateMessage();
			
			tplMsg.setOpenid(openid);
			//微信公众号号的template id，开发者自行处理参数
			tplMsg.setTemplateId("Wyme6_kKUqv4iq7P4d2NVldw3YxZIql4sL2q8CUES_Y"); 
			
			tplMsg.setUrl("https://www.cicmorgan.com");
			Map<String, String> dataMap = new HashMap<String,String>();
			dataMap.put("first", "微信派官方微信模板消息测试");
			dataMap.put("keyword1", "时间：" + DateUtil.COMMON.getDateText(new Date()));
			dataMap.put("keyword2", "关键字二：你好");
			dataMap.put("remark", "备注：感谢您的来访");
			tplMsg.setDataMap(dataMap);
			
			JSONObject result = WxApiClient.sendTemplateMessage(tplMsg, mpAccount);
			try {
				if(result.getInt("errcode") != 0){
					response.getWriter().write("send failure");
				}else{
					response.getWriter().write("send success");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//以根据openid群发文本消息为例
		@RequestMapping(value = "massSendTextMsg", method = RequestMethod.POST)
		public void massSendTextMsg(HttpServletResponse response,String openid,String content){
			content = "群发文本消息";
			MpAccount mpAccount = WxMemoryCacheClient.getSingleMpAccount();//获取缓存中的唯一账号
			String rstMsg = "根据openid群发文本消息失败";
			if(mpAccount != null && !StringUtils.isBlank(openid)){
				List<String> openidList = new ArrayList<String>();
				openidList.add(openid);
				//根据openid群发文本消息
				JSONObject result = WxApiClient.massSendTextByOpenIds(openidList, content, mpAccount);
				
				try {
					if(result.getInt("errcode") != 0){
						response.getWriter().write("send failure");
					}else{
						response.getWriter().write("send success");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			ModelAndView mv = new ModelAndView("common/failure");
			mv.addObject("failureMsg", rstMsg);
		}
	
}




