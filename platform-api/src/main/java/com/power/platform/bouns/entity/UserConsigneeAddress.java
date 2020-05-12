package com.power.platform.bouns.entity;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;
import com.power.platform.sys.entity.Area;
import com.power.platform.userinfo.entity.UserInfo;


/**
 * 用户收货地址Entity
 * @author Mr.Jia
 * @version 2016-12-13
 */
public class UserConsigneeAddress extends DataEntity<UserConsigneeAddress> {
	
	private static final long serialVersionUID = 1L;
	private String userId;			// 用户id
	private String username;		// 收货人姓名
	private String isDefault;		// 是否默认地址
	private String provinceCode;	// 省份
	private String cityCode;		// 城市编码
	private String address;			// 详细地址
	private String mobile;			// 收货手机号
	
	
	
	private UserInfo userInfo;
	private Area province;			// 注册地址
	private Area city;				// 注册地址
	
	public Area getProvince() {
		return province;
	}

	public void setProvince(Area province) {
		this.province = province;
	}

	public Area getCity() {
		return city;
	}

	public void setCity(Area city) {
		this.city = city;
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}
	
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

	public UserConsigneeAddress() {
		super();
	}

	public UserConsigneeAddress(String id){
		super(id);
	}

	@Length(min=0, max=64, message="用户id长度必须介于 0 和 64 之间")
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	@Length(min=0, max=12, message="收货人姓名长度必须介于 0 和 12 之间")
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	@Length(min=0, max=1, message="是否默认地址长度必须介于 0 和 1 之间")
	public String getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(String isDefault) {
		this.isDefault = isDefault;
	}
	
	@Length(min=0, max=255, message="省份长度必须介于 0 和 255 之间")
	public String getProvinceCode() {
		return provinceCode;
	}

	public void setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
	}
	
	@Length(min=0, max=255, message="城市编码长度必须介于 0 和 255 之间")
	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}
	
	@Length(min=0, max=155, message="详细地址长度必须介于 0 和 155 之间")
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
}