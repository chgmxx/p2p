package com.power.platform.bouns.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.power.platform.bouns.entity.UserConsigneeAddress;
import com.power.platform.cache.Cache;
import com.power.platform.common.persistence.DataEntity;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.JedisUtils;
import com.power.platform.common.utils.MemCachedUtis;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.userinfo.entity.Principal;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.userinfo.service.UserInfoService;



/**
 * 用户积分系统接口信息
 * @author Mr.Jia
 * @version 2016-12-14
 */
@Component
@Path("/userConsignee")
@Service("userConsigneeAddressSvcService")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class UserConsigneeAddressSvcService {
	
	@Autowired
	private UserConsigneeAddressService userConsigneeAddressService;
	@Autowired
	private UserInfoService userInfoService;
	
	
	/**
	 * 收货地址列表接口
	 * @param from
	 * @param token
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@POST
	@Path("/addressList")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> addressListInfo( @FormParam("from")String from, @FormParam("token")String token,
			@FormParam("pageNo") String pageNo, @FormParam("pageSize") String pageSize ){
		Map<String, Object> result = new HashMap<String, Object>();
		
		try {
			//判断必要参数是否为空
			if (StringUtils.isBlank(token) || StringUtils.isBlank(from) || StringUtils.isBlank(pageNo) || StringUtils.isBlank(pageSize)) {
				result.put("state", "1");
				throw new Exception("缺少参数必要参数");
			}
			
			// 从缓存获取用户信息
			String jedisUserId = JedisUtils.get(token);
			
			Page<UserConsigneeAddress> page = new Page<UserConsigneeAddress>();
			page.setPageNo(Integer.valueOf(pageNo));
			page.setPageSize(Integer.valueOf(pageSize));
			page.setOrderBy("a.update_date, a.is_default DESC");
			
			UserConsigneeAddress userConsigneeAddress = new UserConsigneeAddress();
			
			List<UserConsigneeAddress> listAddress = null;
			List<Map<String, Object>> data = new ArrayList<Map<String,Object>>();
			if (!StringUtils.isBlank(jedisUserId)) {
				UserInfo userInfo = userInfoService.getCgb(jedisUserId);
				if(userInfo==null){
					userInfo = userInfoService.get(jedisUserId);
				}
				if (userInfo != null) {
					userConsigneeAddress.setUserId(userInfo.getId());
					Page<UserConsigneeAddress> consigneeAddresss = userConsigneeAddressService.findPage(page, userConsigneeAddress);
					listAddress = consigneeAddresss.getList();
					
					for (int i = 0; i < listAddress.size(); i++) {
						Map<String, Object> map = new HashMap<String, Object>();
						userConsigneeAddress = listAddress.get(i);
						map.put("id", userConsigneeAddress.getId());
						map.put("name", userConsigneeAddress.getUsername());
						map.put("isDefault", userConsigneeAddress.getIsDefault());
						map.put("provinceCode", userConsigneeAddress.getProvinceCode());
						map.put("province", userConsigneeAddress.getProvince().getName());
						map.put("cityCode", userConsigneeAddress.getCityCode());
						map.put("city", userConsigneeAddress.getCity().getName());
						map.put("address", userConsigneeAddress.getAddress());
						map.put("mobile", userConsigneeAddress.getMobile());
						data.add(map);
					}
					
					result.put("count", consigneeAddresss.getCount());
					result.put("lastPage", consigneeAddresss.getLast());
					result.put("pageNo", consigneeAddresss.getPageNo());
					result.put("pageSize", consigneeAddresss.getPageSize());
				}
			}
			
			result.put("state","0");
			result.put("message", "用户收货地址信息查询成功");
			result.put("data", data);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", "系统异常");
			result.put("state", "2");
			return result;
		}
	}
	
	
	
	/**
	 * 添加或者修改收货地址
	 * @param from
	 * @param token
	 * @param name
	 * @param mobile
	 * @param provinceCode
	 * @param cityCode
	 * @param address
	 * @param isDefault
	 * @param id
	 * @return
	 */
	@POST
	@Path("/addNewAddress")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> addNewAddressInfo( @FormParam("from")String from, @FormParam("token")String token,
			@FormParam("name") String name, @FormParam("mobile") String mobile,
			@FormParam("provinceCode") String provinceCode, @FormParam("cityCode") String cityCode,
			@FormParam("address") String address, @FormParam("isDefault") String isDefault,
			@FormParam("id") String id){
		
		Map<String, Object> result = new HashMap<String, Object>();
		String message = "系统异常";
		try {
			//判断必要参数是否为空

			if (StringUtils.isBlank(token) || StringUtils.isBlank(from) 
					|| StringUtils.isBlank(name) || StringUtils.isBlank(mobile)
					|| StringUtils.isBlank(provinceCode) || StringUtils.isBlank(cityCode)
					|| StringUtils.isBlank(address) || StringUtils.isBlank(isDefault) ) {
				result.put("state", "1");
				throw new Exception("缺少参数必要参数");
			}
			
			// 从缓存获取用户信息
			String jedisUserId = JedisUtils.get(token);
			
			UserConsigneeAddress userConsigneeAddress = null;
			
			if (!StringUtils.isBlank(jedisUserId)) {
				UserInfo userInfo = userInfoService.getCgb(jedisUserId);
				if(userInfo==null){
					userInfo = userInfoService.get(jedisUserId);
				}
				if (userInfo != null) {
					
					//判断是否已经存在八个可用地址
					UserConsigneeAddress ucheckAddress = new UserConsigneeAddress();
					ucheckAddress.setUserId(userInfo.getId());
					ucheckAddress.setIsDefault("0");
					List<UserConsigneeAddress> checkAddressList = userConsigneeAddressService.findList(userConsigneeAddress);
					if(checkAddressList!=null && checkAddressList.size()>8){
						throw new Exception("收货地址最多只能录入八个");
					}
					
					// 判断要添加或者修改的是否是默认地址
					if(UserConsigneeAddressService.IS_DEFAULT_ADDRESS_YES.equals(isDefault)){
						// 将已存在的默认地址改为非默认地址
						userConsigneeAddress = new UserConsigneeAddress();
						userConsigneeAddress.setUserId(userInfo.getId());
						userConsigneeAddress.setIsDefault(isDefault);
						List<UserConsigneeAddress> exitsAddressList = userConsigneeAddressService.findList(userConsigneeAddress);
						if (exitsAddressList != null && exitsAddressList.size() > 0) {
							userConsigneeAddress = exitsAddressList.get(0);
							userConsigneeAddress.setIsDefault(UserConsigneeAddressService.IS_DEFAULT_ADDRESS_NO);
							userConsigneeAddressService.update(userConsigneeAddress);
						}
						
					}
					
					
					// 添加或者修改地址
					if(StringUtils.isBlank(id)){
						// 添加地址
						userConsigneeAddress = new UserConsigneeAddress();
						userConsigneeAddress.setId(IdGen.uuid());
						userConsigneeAddress.setUserId(userInfo.getId());
						userConsigneeAddress.setUsername(name);
						userConsigneeAddress.setProvinceCode(provinceCode);
						userConsigneeAddress.setCityCode(cityCode);
						userConsigneeAddress.setAddress(address);
						userConsigneeAddress.setMobile(mobile);
						userConsigneeAddress.setIsDefault(isDefault);
						userConsigneeAddress.setCreateDate(new Date());
						userConsigneeAddress.setUpdateDate(new Date());
						int insertResult = userConsigneeAddressService.insert(userConsigneeAddress);
						if(insertResult > 0){
							message = "添加地址信息成功";
						}
					} else {
						// 修改已存在地址
						userConsigneeAddress = userConsigneeAddressService.get(id);
						userConsigneeAddress.setUsername(name);
						userConsigneeAddress.setProvinceCode(provinceCode);
						userConsigneeAddress.setCityCode(cityCode);
						userConsigneeAddress.setAddress(address);
						userConsigneeAddress.setIsDefault(isDefault);
						userConsigneeAddress.setMobile(mobile);
						userConsigneeAddress.setUpdateDate(new Date());
						int updateResult = userConsigneeAddressService.update(userConsigneeAddress);
						if(updateResult > 0){
							message = "修改地址信息成功";
						}
					}
				}
			}
			
			result.put("state","0");
			result.put("message", message);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", e.getMessage());
			result.put("state", "2");
			return result;
		}
	}
	
	
	
	/**
	 * 获取单个地址详细信息
	 * @param from
	 * @param token
	 * @param id
	 * @return
	 */
	@POST
	@Path("/getOneAddress")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> getOneAddressInfo( @FormParam("from")String from, @FormParam("token")String token,
			@FormParam("id") String id){
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			//判断必要参数是否为空
			if (StringUtils.isBlank(token) || StringUtils.isBlank(from) || StringUtils.isBlank(id) ) {
				result.put("state", "1");
				throw new Exception("缺少参数必要参数");
			}
			
			// 从缓存获取用户信息
			String jedisUserId = JedisUtils.get(token);
			
			UserConsigneeAddress userConsigneeAddress = null;
			
			if (!StringUtils.isBlank(jedisUserId)) {
				UserInfo userInfo = userInfoService.getCgb(jedisUserId);
				if(userInfo==null){
					userInfo = userInfoService.get(jedisUserId);
				}
				if (userInfo != null) {
					userConsigneeAddress = userConsigneeAddressService.get(id);
					map.put("id", userConsigneeAddress.getId());
					map.put("name", userConsigneeAddress.getUsername());
					map.put("isDefault", userConsigneeAddress.getIsDefault());
					map.put("provinceCode", userConsigneeAddress.getProvinceCode());
					map.put("province", userConsigneeAddress.getProvince().getName());
					map.put("cityCode", userConsigneeAddress.getCityCode());
					map.put("city", userConsigneeAddress.getCity().getName());
					map.put("address", userConsigneeAddress.getAddress());
					map.put("mobile", userConsigneeAddress.getMobile());
					map.put("provinceCode", userConsigneeAddress.getProvinceCode());
					map.put("cityCode", userConsigneeAddress.getCityCode());
				}
			}
			
			result.put("state","0");
			result.put("message", "收货地址信息查询成功");
			result.put("data", map);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", "系统异常");
			result.put("state", "2");
			return result;
		}
	}
	
	
	
	
	/**
	 * 删除一个地址信息
	 * @param from
	 * @param token
	 * @param id
	 * @return
	 */
	@POST
	@Path("/deleteOneAddress")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> deleteOneAddressInfo( @FormParam("from")String from, @FormParam("token")String token,
			@FormParam("id") String id){
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			//判断必要参数是否为空
			if (StringUtils.isBlank(token) || StringUtils.isBlank(from) || StringUtils.isBlank(id) ) {
				result.put("state", "1");
				throw new Exception("缺少参数必要参数");
			}
			// 从缓存获取用户信息
			String jedisUserId = JedisUtils.get(token);
			
			UserConsigneeAddress userConsigneeAddress = null;
			if (!StringUtils.isBlank(jedisUserId)) {
				UserInfo userInfo = userInfoService.getCgb(jedisUserId);
				if(userInfo==null){
					userInfo = userInfoService.get(jedisUserId);
				}
				if (userInfo != null) {
					userConsigneeAddress = userConsigneeAddressService.get(id);
					if (userConsigneeAddress != null) {
						// 判断删除的是否是默认地址（如果是修改其中一个地址为新的默认地址）
						if(userConsigneeAddress.getIsDefault().equals(UserConsigneeAddressService.IS_DEFAULT_ADDRESS_YES)){
							UserConsigneeAddress userConsigneeAddressNew = new UserConsigneeAddress();
							userConsigneeAddressNew.setUserId(userInfo.getId());
							List<UserConsigneeAddress> list = userConsigneeAddressService.findList(userConsigneeAddressNew);
							if(list != null && list.size() > 0){
								userConsigneeAddressNew = list.get(0);
								userConsigneeAddressNew.setIsDefault(UserConsigneeAddressService.IS_DEFAULT_ADDRESS_YES);
								userConsigneeAddressService.update(userConsigneeAddressNew);
							}
						}
						userConsigneeAddress.setDelFlag(DataEntity.DEL_FLAG_DELETE);
						userConsigneeAddressService.delete(userConsigneeAddress);
					}
				}
			}
			
			result.put("state","0");
			result.put("message", "地址删除成功");
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", "系统异常");
			result.put("state", "2");
			return result;
		}
	}
	
	
	
	/**
	 * 设置某个地址为默认地址
	 * @param from
	 * @param token
	 * @param id
	 * @return
	 */
	@POST
	@Path("/setOneAddressDefault")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> setOneAddressDefault( @FormParam("from")String from, @FormParam("token")String token,
			@FormParam("id") String id){
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			//判断必要参数是否为空
			if (StringUtils.isBlank(token) || StringUtils.isBlank(from) || StringUtils.isBlank(id) ) {
				result.put("state", "1");
				throw new Exception("缺少参数必要参数");
			}
			// 从缓存获取用户信息
			String jedisUserId = JedisUtils.get(token);
			
			UserConsigneeAddress userConsigneeAddress = null;
			if (!StringUtils.isBlank(jedisUserId)) {
				UserInfo userInfo = userInfoService.getCgb(jedisUserId);
				if(userInfo==null){
					userInfo = userInfoService.get(jedisUserId);
				}
				if (userInfo != null) {
					// 将已存在的默认地址改为非默认地址
					userConsigneeAddress = new UserConsigneeAddress();
					userConsigneeAddress.setUserId(userInfo.getId());
					userConsigneeAddress.setIsDefault(UserConsigneeAddressService.IS_DEFAULT_ADDRESS_YES);
					List<UserConsigneeAddress> exitsAddressList = userConsigneeAddressService.findList(userConsigneeAddress);
					if (exitsAddressList != null && exitsAddressList.size() > 0) {
						userConsigneeAddress = exitsAddressList.get(0);
						userConsigneeAddress.setIsDefault(UserConsigneeAddressService.IS_DEFAULT_ADDRESS_NO);
						userConsigneeAddressService.update(userConsigneeAddress);
					}
					
					
					// 设置选中的地址为默认地址
					userConsigneeAddress = userConsigneeAddressService.get(id);
					userConsigneeAddress.setIsDefault(UserConsigneeAddressService.IS_DEFAULT_ADDRESS_YES);
					userConsigneeAddressService.update(userConsigneeAddress);
				}
			}
			
			result.put("state","0");
			result.put("message", "设置默认地址成功");
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", "系统异常");
			result.put("state", "2");
			return result;
		}
	}
}
