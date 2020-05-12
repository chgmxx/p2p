package com.power.platform.weixin;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.power.filter.utils.QRCodeUtils;
import com.power.platform.common.utils.MemCachedUtis;
import com.power.platform.userinfo.entity.Principal;

@Path("weixin")
@Service("weixinRestService")
@Produces(MediaType.APPLICATION_JSON)
public class WeixinRestService {
	
	public static final String WEIXIN_SHARE_URL="http://www.baidu.com";
	
	/**
	 * 生产二维码
	 * @param phone
	 * @param from
	 * @return
	 */
	@GET
	@Path("/createQRCode")
	public Map<String, Object> createQRCode(@FormParam("from") String from,@FormParam("token") String token) {
		Map<String, Object> result = new HashMap<String, Object>();
		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(from) || StringUtils.isBlank(token)) {
			result.put("state", "1");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}
		try {
			Principal principal= MemCachedUtis.getMemCached().get(token);
			QRCodeUtils.initRrCode(WEIXIN_SHARE_URL, principal.getUserInfo().getId());
			String name ="static/image/"+principal.getUserInfo().getId()+".png";
			result.put("state", "0");
			result.put("message", "二维码生成成功！");
			result.put("codeUrl", name);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "2");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}
	}
	

}
