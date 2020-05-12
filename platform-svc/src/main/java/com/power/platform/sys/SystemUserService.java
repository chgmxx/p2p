package com.power.platform.sys;

import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.sys.entity.User;
import com.power.platform.sys.service.SystemService;

@Path("/sys")
@Service("systemUserService")
@Produces(MediaType.APPLICATION_JSON)
public class SystemUserService {
	
	@Autowired
	private SystemService systemService;
	
	/**
	 * 系统用户信息
	 * 
	 * @param from
	 * @return
	 */
	@POST
	@Path("/getSysUserInfo")
	public Map<String, Object> getSysUserInfo(@FormParam("username") String username) {

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> map = new HashMap<String, Object>();
		// 判断必要参数是否为空
		if (StringUtils.isBlank(username)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		
		User user = systemService.getLoginUser(username);
		if(null != user){
			map.put("mobile", user.getMobile());
			result.put("state", "0");
			result.put("message", "获取用户信息成功");
			result.put("data", map);
		}else{
			result.put("state", "1");
			result.put("message", "无此系统用户");
		}
		return result;
	}

}
