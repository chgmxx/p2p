package com.power.platform.activity.entity;

import com.power.platform.common.persistence.DataEntity;
import com.power.platform.userinfo.entity.UserInfo;

/**
 * 
 * 类: Brokerage <br>
 * 描述: 佣金流向记录表. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年4月25日 下午6:00:10
 */
public class Brokerage extends DataEntity<Brokerage> {

	private static final long serialVersionUID = 1L;

	/**
	 * 客户ID.
	 */
	private String userId;
	/**
	 * 推荐客户ID.
	 */
	private String fromUserId;
	/**
	 * 佣金金额.
	 */
	private Double amount;
	private UserInfo userInfo;
	private UserInfo fromUserInfo;

	/**
	 * 佣金列表返沪数据.
	 */
	// 移动电话.
	private String mobilePhone;
	// 真是姓名.
	private String realName;

	public UserInfo getUserInfo() {

		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {

		this.userInfo = userInfo;
	}

	public UserInfo getFromUserInfo() {

		return fromUserInfo;
	}

	public void setFromUserInfo(UserInfo fromUserInfo) {

		this.fromUserInfo = fromUserInfo;
	}

	public String getUserId() {

		return userId;
	}

	public void setUserId(String userId) {

		this.userId = userId;
	}

	public String getFromUserId() {

		return fromUserId;
	}

	public void setFromUserId(String fromUserId) {

		this.fromUserId = fromUserId;
	}

	public Double getAmount() {

		return amount;
	}

	public void setAmount(Double amount) {

		this.amount = amount;
	}

	public String getMobilePhone() {

		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {

		this.mobilePhone = mobilePhone;
	}

	public String getRealName() {

		return realName;
	}

	public void setRealName(String realName) {

		this.realName = realName;
	}

}