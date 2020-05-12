package com.power.platform.p2peye.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.power.platform.cache.Cache;
import com.power.platform.common.utils.EncoderUtil;
import com.power.platform.common.utils.JedisUtils;
import com.power.platform.common.utils.MemCachedUtis;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.userinfo.dao.UserLoginDao;
import com.power.platform.userinfo.entity.Principal;
import com.power.platform.userinfo.entity.UserInfo;


@Path("/p2peye")
@Service("tokenRestService")
@Produces(MediaType.APPLICATION_JSON)
public class TokenService {
	
	@Autowired
	private UserLoginDao userLoginDao;
	
	/**
	 * 获取token
	 * 
	 * @param username
	 * @param password
	 * @return map
	 */
	@POST
	@Path("/getToken")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> getToken(@FormParam("username") String username, @FormParam("password") String password,  @Context HttpServletRequest servletrequest) {
			
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		Principal principal = new Principal();
		String token = null;
		// 判断必要参数是否为空
		if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
			map.put("result", "-1");
			data.put("token", token);
			map.put("data", data);
			return map;
		}
		Date time = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhhmmss");
		UserInfo userInfo = new UserInfo();
		userInfo.setName(username);
		userInfo.setPwd(EncoderUtil.encrypt(password));
		try {
			List<UserInfo> userList = userLoginDao.findList(userInfo);
			if (userList != null && userList.size() > 0) {
				UserInfo user = userList.get(0);

						// 生成token
						token = EncoderUtil.encrypt(sdf.format(time) + username+password).replace("+", "a");
						// 获取缓存
						JedisUtils.set(token, user.getId(), 1200);
						
						data.put("token", token);
						map.put("result", "1");
						map.put("data", data);
						return map;
						
			} else {
				map.put("result", "-1");
				data.put("token", token);
				map.put("data", data);
				return map;
			}
		} catch (Exception e) {
			// TODO: handle exception
			map.put("result", "-1");
			data.put("token", token);
			map.put("data", data);
			return map;
		}
	}
}
