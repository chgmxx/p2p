package com.power.platform.cgb.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.power.platform.activity.service.RedPacketService;
import com.power.platform.cache.Cache;
import com.power.platform.common.exception.WinException;
import com.power.platform.common.utils.MemCachedUtis;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.userinfo.entity.Principal;
import com.power.platform.userinfo.entity.UserInfo;


/**
 * 返利API
 * @author YHAGZALUN WO SJIAOSY
 *
 */
@Component
@Path("/redpacket")
@Service("cGBRedPacketService")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class CGBRedPacketService {
	
	private static final Logger LOG = LoggerFactory.getLogger(CGBRedPacketService.class);

	
	@Autowired
	private RedPacketService redPacketService;
	
	
    /**
     * 返利API
     * @param from
     * @param token
     * @param bizType 8001-佣金    8003-红包
     * @param amount 金额
     * @return
     */
	@POST
	@Path("/giveRedPacket")
	@Produces(MediaType.APPLICATION_JSON)
	public  Map<String, Object> giveRedPacket(@FormParam("from")String from, @FormParam("token")String token,@FormParam("bizType")String bizType,
			@FormParam("amount")Double amount){
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		try {
			//判断必要参数是否为空
			if (StringUtils.isBlank(from) || StringUtils.isBlank(token) ||  StringUtils.isBlank(bizType) || amount== null ) {
				result.put("state", "2");
				throw new Exception("缺少参数必要参数");
			}
			
			// 从缓存获取用户信息
			Cache cache =  MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			UserInfo user = principal.getUserInfo();
			
			Map<?, ?> map = new HashMap<String, Object>();
			if ( user == null ) {
				throw new Exception("用户登录信息错误，请重新登录");
			} else {
				//map = redPacketService.giveRedPacket(user.getName(),bizType,amount);
			}
			result.put("state","0");
			result.put("message", "返利信息生成成功");
			result.put("data", map);
			return result;
		} catch (WinException e) {
			e.printStackTrace();
			result.put("message", e.getMessage());
			result.put("data", null);
			result.put("state", "3");
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", "系统异常");
			result.put("data", null);
			result.put("state", "3");
			return result;
		}
	}
	
}
