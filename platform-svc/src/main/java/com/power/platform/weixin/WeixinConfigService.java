package com.power.platform.weixin;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.power.platform.weixin.menu.Button;
import com.power.platform.weixin.menu.CommonButton;
import com.power.platform.weixin.menu.MenuManager;
import com.power.platform.weixin.utils.JsonUtils;
import com.power.platform.weixin.utils.WeixinUtil;
import com.power.platform.weixin.utils.mapTest;



/**
 * 微信接口
 * @author timefiles
 *
 */
@Path("wxconfig")
@Service("acceccTokenService")
@Produces({ MediaType.APPLICATION_JSON})
public class WeixinConfigService {

	@Autowired
	private MenuManager menuManager;
	
	/**
	 * 微信获取token
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/getWeixinToken")
	public String getWeixinToken() throws Exception{
		return WeixinUtil.getAccessToken();
	}
	
	/**
	 * 微信获取js加载所需参数
	 * @param htmlUrl 为前台要使用微信js的页面的html
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/getWeixinJsParam")
	public Map<String, Object> getWeixinJsParam(@FormParam("htmlUrl")String htmlUrl) throws Exception{
		Map<String, Object> map =new HashMap<String, Object>();
		String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token="+WeixinUtil.getAccessToken()+"&type=jsapi";
		HttpClient httpClient = new HttpClient();
		GetMethod getMethod = new GetMethod(url);
		int statusCode = httpClient.executeMethod(getMethod);
		if (statusCode != HttpStatus.SC_OK) {
			System.err.println("Method failed: " + getMethod.getStatusLine());
		}
		@SuppressWarnings("unchecked")
		Map<String,Object> resultMap = (Map<String,Object>)JsonUtils.jsonToMap(getMethod.getResponseBodyAsString());
		System.out.println(resultMap.toString());
		String ticket = (String)resultMap.get("ticket");
		System.out.println("ticket____________________" + ticket);
	
		Map<String,String> signMap = mapTest.sign(ticket, htmlUrl);
		map.put("signature", signMap.get("signature"));
		map.put("nonceStr", signMap.get("nonceStr"));
		map.put("timestamp", signMap.get("timestamp"));
		map.put("appid", WeixinUtil.WEIXIN_APP_ID);
		
		return map;
	}
	
	
	
	@GET
	@Path("/createMenu")
	public String createMenu() {
		String message = "同步菜单信息数据失败！同步自定义菜单URL地址不正确。";
		try {
	        String url = WeixinUtil.menu_create_url.replace("ACCESS_TOKEN", WeixinUtil.getAccessToken());
	        JSONObject jsonObject = new JSONObject();
	        ObjectMapper mapper = new ObjectMapper();
	        jsonObject = WeixinUtil.httpRequest(url, "POST", mapper.writeValueAsString(menuManager.getMenu()));
	        for( Button button : menuManager.getMenu().getButton() ) {
	        	if (button instanceof CommonButton) {
	        		CommonButton commonButton = (CommonButton)button;
	        		System.out.println(commonButton.getUrl());
				}
	        }   
	        if (jsonObject != null) {
	            if (0 == jsonObject.getIntValue("errcode")) {
	                message = "同步菜单信息数据成功！";
	            } else {
	                message = "同步菜单信息数据失败！错误码为：" + jsonObject.getIntValue("errcode") + "错误信息为：" + jsonObject.getString("errmsg");
	            }
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
        return message;
    }
}
