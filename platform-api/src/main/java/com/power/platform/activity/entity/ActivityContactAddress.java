/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.activity.entity;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;

/**
 * 用户活动联系地址Entity
 * 
 * @author Roy
 * @version 2019-08-14
 */
public class ActivityContactAddress extends DataEntity<ActivityContactAddress> {

	private static final long serialVersionUID = 1L;
	private String userId; // 用户id
	private String province; // 省份
	private String city; // 地级市
	private String county; // 市、县级市、区
	private String street; // 街道
	private String name; // 姓名
	private String mobilePhone; // 移动电话

	public ActivityContactAddress() {

		super();
	}

	public ActivityContactAddress(String id) {

		super(id);
	}

	public String getUserId() {

		return userId;
	}

	public void setUserId(String userId) {

		this.userId = userId;
	}

	@Length(min = 0, max = 32, message = "省份长度必须介于 0 和 32 之间")
	public String getProvince() {

		return province;
	}

	public void setProvince(String province) {

		this.province = province;
	}

	@Length(min = 0, max = 32, message = "地级市长度必须介于 0 和 32 之间")
	public String getCity() {

		return city;
	}

	public void setCity(String city) {

		this.city = city;
	}

	@Length(min = 0, max = 32, message = "市、县级市、区长度必须介于 0 和 32 之间")
	public String getCounty() {

		return county;
	}

	public void setCounty(String county) {

		this.county = county;
	}

	@Length(min = 0, max = 64, message = "街道长度必须介于 0 和 64 之间")
	public String getStreet() {

		return street;
	}

	public void setStreet(String street) {

		this.street = street;
	}

	@Length(min = 0, max = 32, message = "姓名长度必须介于 0 和 32 之间")
	public String getName() {

		return name;
	}

	public void setName(String name) {

		this.name = name;
	}

	@Length(min = 0, max = 32, message = "移动电话长度必须介于 0 和 32 之间")
	public String getMobilePhone() {

		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {

		this.mobilePhone = mobilePhone;
	}

}