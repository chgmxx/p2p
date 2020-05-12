package com.power.platform.userinfo.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.common.utils.EncoderUtil;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.utils.Win11P2pConstants;
import com.power.platform.sys.type.UserStateType;
import com.power.platform.sys.type.UserType;
import com.power.platform.userinfo.dao.UserLoginDao;
import com.power.platform.userinfo.entity.Principal;
import com.power.platform.userinfo.entity.UserInfo;

@Service("userService")
@Transactional(readOnly = true)
public class UserService extends CrudService<UserInfo> {

	@Resource
	private UserLoginDao userLoginDao;
	
	protected CrudDao<UserInfo> getEntityDao() {
		return userLoginDao;
	}
	
	@Transactional
	public Map<String, Object> validateLogin(UserInfo userInfo, String ip) {
		System.out.println("进入validatefangfa");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if(StringUtils.isBlank(userInfo.getPwd()) || StringUtils.isBlank(userInfo.getName())){
			resultMap.put("rspMsg", "用户名密码不能为空!");
			return resultMap;
		}
		userInfo.setPwd( EncoderUtil.encrypt(userInfo.getPwd()) );
		List<UserInfo> users = userLoginDao.findList(userInfo);
		if( users != null && users.size() > 0 ){
			UserInfo existUser = users.get(0);
			System.out.println("existUser存在");
			//投资用户
			if(Integer.valueOf(UserType.BID) == existUser.getUserType()){
				//用户状态正常
				if(existUser.getState() == UserStateType.NORMAL){
					//修改最后登录时间和登录IP
					existUser.setId(existUser.getId());
					existUser.setLastLoginDate(new Date());
					existUser.setLastLoginIp(ip);
					userLoginDao.update(existUser);
					
					
					Principal principal = new Principal();
					
					principal.setUserInfo(userLoginDao.get(existUser.getId()));
					
					resultMap.put(Win11P2pConstants.SESSION_PRINCAL_KEY, principal);
				}else if(existUser.getState() == UserStateType.DELETED){
					System.out.println("用户已经被注销!");
					resultMap.put("rspMsg", "用户已经被注销!");
				}else{
					System.out.println("用户被禁用!");
					resultMap.put("rspMsg", "用户被禁用!");
				}
			}else{
				System.out.println("非投资用户!");
				resultMap.put("rspMsg", "非投资用户!");
			}
		}else{
			System.out.println("用户名或密码错误!");
			resultMap.put("rspMsg", "用户名或密码错误!");
		}
		return resultMap;
	}

}
