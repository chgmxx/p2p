/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.coupon.entity;

import java.util.Date;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.power.platform.common.persistence.DataEntity;
import com.power.platform.userinfo.entity.UserInfo;

/**
 * 客户优惠券信息Entity
 * @author Mr.Jia
 * @version 2016-01-21
 */
public class CouponInfoUser extends DataEntity<CouponInfoUser> {
	
	private static final long serialVersionUID = 1L;
	private CouponInfo couponInfo;		// 优惠券
	private UserInfo userInfo ;		// 用户
	private Date endDate;		// 到期时间
	private String state;		// 状态 1可用 2已经3到期
	
	public CouponInfoUser() {
		super();
	}

	public CouponInfoUser(String id){
		super(id);
	}
	
	public CouponInfo getCouponInfo() {
		return couponInfo;
	}

	public void setCouponInfo(CouponInfo couponInfo) {
		this.couponInfo = couponInfo;
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	@Length(min=0, max=255, message="状态 1可用 2已经3到期长度必须介于 0 和 255 之间")
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	public static final String KEYONG_STATE="1";
	public static final String YIYONG_STATE="2";
	public static final String END_DATE_STATE="3";
	
	
}