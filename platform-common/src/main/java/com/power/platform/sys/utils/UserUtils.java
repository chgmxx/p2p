/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.sys.utils;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.power.platform.common.security.Digests;
import com.power.platform.common.service.BaseService;
import com.power.platform.common.utils.CacheUtils;
import com.power.platform.common.utils.Encodes;
import com.power.platform.common.utils.SpringContextHolder;
import com.power.platform.sys.entity.Area;
import com.power.platform.sys.entity.Menu;
import com.power.platform.sys.entity.Office;
import com.power.platform.sys.entity.Role;
import com.power.platform.sys.entity.User;
import com.power.platform.sys.service.SystemService;

/**
 * 用户工具类
 * @author ThinkGem
 * @version 2013-12-05
 */
public class UserUtils {

	private static SystemService systemService = SpringContextHolder.getBean("systemService");

	public static final String USER_CACHE = "userCache";
	public static final String USER_CACHE_ID_ = "id_";
	public static final String USER_CACHE_LOGIN_NAME_ = "ln";
	public static final String USER_CACHE_LIST_BY_OFFICE_ID_ = "oid_";

	public static final String CACHE_ROLE_LIST = "roleList_";
	public static final String CACHE_ROLE_ALL_LIST = "roleAllList";
	public static final String CACHE_MENU_LIST = "menuList_";
	public static final String CACHE_MENU_ALL_LIST = "menuAllList";
	public static final String CACHE_AREA_LIST = "areaList_";
	public static final String CACHE_AREA_ALL_LIST = "areaAllList";
	public static final String CACHE_OFFICE_LIST = "officeList_";
	public static final String CACHE_OFFICE_ALL_LIST = "officeAllList";
	/**获取当前用户真实ip
	 * @param request
	 * @return ip
	 */
 	public static String getIpAddr(HttpServletRequest request) {  
	    String ip = request.getHeader("x-forwarded-for");  
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
	        ip = request.getHeader("Proxy-Client-IP");  
	    }  
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
	        ip = request.getHeader("WL-Proxy-Client-IP");  
	    }  
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
	        ip = request.getRemoteAddr();  
	    }  
	    return ip;  
	}  
	
	/**
	 * 根据ID获取用户
	 * @param id
	 * @return 取不到返回null
	 */
	public static User get(String id){
		User user = (User)CacheUtils.get(USER_CACHE, USER_CACHE_ID_ + id);
		if (user ==  null){
			User newuser=new User();
			newuser.setId(id);
			user = systemService.getDbUser(newuser);
			if (user == null){
				return null;
			}
			user.setRoleList(systemService.findRole(new Role(user)));
			CacheUtils.put(USER_CACHE, USER_CACHE_ID_ + user.getId(), user);
			CacheUtils.put(USER_CACHE, USER_CACHE_LOGIN_NAME_ + user.getLoginName(), user);
		}
		return user;
	}
	
	/**
	 * 根据登录名获取用户
	 * @param loginName
	 * @return 取不到返回null
	 */
	public static User getByLoginName(String loginName){
		User user = (User)CacheUtils.get(USER_CACHE, USER_CACHE_LOGIN_NAME_ + loginName);
		if (user == null){
			user = systemService.getUserByLoginName(loginName);
			if (user == null){
				return null;
			}
			user.setRoleList(systemService.findRole(new Role()));
			CacheUtils.put(USER_CACHE, USER_CACHE_ID_ + user.getId(), user);
			CacheUtils.put(USER_CACHE, USER_CACHE_LOGIN_NAME_ + user.getLoginName(), user);
		}
		return user;
	}
	
	/**
	 * 清除指定用户缓存
	 * @param user
	 */
	public static void clearCache(User user){
		CacheUtils.remove(USER_CACHE, USER_CACHE_ID_ + user.getId());
		CacheUtils.remove(USER_CACHE, USER_CACHE_LOGIN_NAME_ + user.getLoginName());
		CacheUtils.remove(USER_CACHE, USER_CACHE_LOGIN_NAME_ + user.getOldLoginName());
		if (user.getOffice() != null && user.getOffice().getId() != null){
			CacheUtils.remove(USER_CACHE, USER_CACHE_LIST_BY_OFFICE_ID_ + user.getOffice().getId());
		}
		
		CacheUtils.remove(USER_CACHE, CACHE_ROLE_LIST+USER_CACHE_ID_ + user.getId());
		CacheUtils.remove(USER_CACHE, CACHE_MENU_LIST+USER_CACHE_ID_ + user.getId());
		CacheUtils.remove(USER_CACHE, CACHE_AREA_LIST+USER_CACHE_ID_ + user.getId());
		CacheUtils.remove(USER_CACHE, CACHE_OFFICE_LIST+USER_CACHE_ID_ + user.getId());
	}
	
	/**
	 * 获取用户角色列表
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Role> getRoleList(String userId){
		List<Role> roleList = (List<Role>)CacheUtils.get(USER_CACHE,CACHE_ROLE_LIST+USER_CACHE_ID_+userId);
		if (roleList == null){
			User user = get(userId);
			if (user.isAdmin()){
				roleList = (List<Role>)CacheUtils.get(CACHE_ROLE_ALL_LIST);
				if(roleList == null){
					roleList = systemService.findAllRole();
					CacheUtils.put(CACHE_ROLE_ALL_LIST, roleList);
					return roleList;
				}
			}else{
				Role role = new Role();
				role.getSqlMap().put("dsf", BaseService.dataScopeFilter(user, "o", "u"));
				roleList = systemService.findRole(role);
			}
			CacheUtils.put(USER_CACHE,CACHE_ROLE_LIST+USER_CACHE_ID_+userId, roleList);
		}
		return roleList;
	}
	
	/**
	 * 获取用户角色列表
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Role> getRoleAllList(){
		List<Role> roleList = (List<Role>)CacheUtils.get(CACHE_ROLE_ALL_LIST);
		if (roleList == null){
			roleList = systemService.findAllRole();
			CacheUtils.put(CACHE_ROLE_ALL_LIST, roleList);
		}
		return roleList;
	}
	
	/**
	 * 获取用户授权菜单
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Menu> getMenuList(String userId){
		List<Menu> menuList = (List<Menu>)CacheUtils.get(USER_CACHE, CACHE_MENU_LIST+USER_CACHE_ID_+userId);
		if (menuList == null){
			User user = get(userId);
			if (user.isAdmin()){
				menuList = (List<Menu>)CacheUtils.get(CACHE_MENU_ALL_LIST);
			if(menuList == null){
					menuList = systemService.findAllMenu();
					CacheUtils.put(CACHE_MENU_ALL_LIST, menuList);
					return menuList;
				}
			}else{
				Menu m = new Menu();
				m.setUserId(user.getId());
				menuList = systemService.findMenuByUserId(m);
			}
			CacheUtils.put(USER_CACHE, CACHE_MENU_LIST+USER_CACHE_ID_+userId, menuList);
		}
		return menuList;
	}
	
	/**
	 * 获取用户授权的区域
	 * @return
	 */
	public static List<Area> getAreaList(){
		@SuppressWarnings("unchecked")
		List<Area> areaList = (List<Area>)CacheUtils.get(CACHE_AREA_ALL_LIST);
		if (areaList == null){
			areaList = systemService.findAllArea();
			CacheUtils.put(CACHE_AREA_ALL_LIST, areaList);
		}
		return areaList;
	}
	
	/**
	 * 获取用户有权限访问的部门
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Office> getOfficeList(String userId){
		List<Office> officeList = (List<Office>)CacheUtils.get(USER_CACHE, CACHE_OFFICE_LIST+USER_CACHE_ID_+userId);
		if (officeList == null){
			User user = get(userId);
			if (user.isAdmin()){
				officeList = (List<Office>)CacheUtils.get(CACHE_OFFICE_ALL_LIST);
				if(officeList == null){
					officeList = systemService.findAllOffice();
					CacheUtils.put(CACHE_OFFICE_ALL_LIST, officeList);
				}
				return officeList;
			}else{
				Office office = new Office();
				office.getSqlMap().put("dsf", BaseService.dataScopeFilter(user, "a", ""));
				officeList = systemService.findOffice(office);
			}
			CacheUtils.put(USER_CACHE, CACHE_OFFICE_LIST+USER_CACHE_ID_+userId, officeList);
		}
		return officeList;
	}

	/**
	 * 获取用户有权限访问的部门
	 * @return
	 */
	public static List<Office> getOfficeAllList(){
		@SuppressWarnings("unchecked")
		List<Office> officeList = (List<Office>)CacheUtils.get(CACHE_OFFICE_ALL_LIST);
		if (officeList == null){
			officeList = systemService.findAllOffice();
			CacheUtils.put(CACHE_OFFICE_ALL_LIST, officeList);
		}
		return officeList;
	}
	
	
	/**
	 * 生成安全的密码，生成随机的16位salt并经过1024次 sha-1 hash
	 */
	public static String entryptPassword(String plainPassword) {
		byte[] salt = Digests.generateSalt(SystemService.SALT_SIZE);
		byte[] hashPassword = Digests.sha1(plainPassword.getBytes(), salt, SystemService.HASH_INTERATIONS);
		return Encodes.encodeHex(salt)+Encodes.encodeHex(hashPassword);
	}
	
	/**
	 * 验证密码
	 * @param plainPassword 明文密码
	 * @param password 密文密码
	 * @return 验证成功返回true
	 */
	public static boolean validatePassword(String plainPassword, String password) {
		byte[] salt = Encodes.decodeHex(password.substring(0,16));
		byte[] hashPassword = Digests.sha1(plainPassword.getBytes(), salt, SystemService.HASH_INTERATIONS);
		return password.equals(Encodes.encodeHex(salt)+Encodes.encodeHex(hashPassword));
	}
	
}
