package com.power.platform.activity.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.power.platform.common.persistence.DataEntity;
import com.power.platform.userinfo.entity.UserInfo;

/**
 * 
 * 类: LevelDistribution <br>
 * 描述: 三级分销，三级关系表. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年4月25日 下午5:59:32
 */
public class LevelDistribution extends DataEntity<LevelDistribution> {

	private static final long serialVersionUID = 1L;

	/**
	 * 客户ID，子.
	 */
	private String userId;

	/**
	 * 二级分销客户，父.
	 */
	private String parentId;

	/**
	 * 一级分销客户id，爷.
	 */
	private String grandpaId;

	/**
	 * 邀请码(推广链接中的推广编号[唯一标识]).
	 */
	private String inviteCode;

	/**
	 * 客户类型.
	 */
	private String type;
	private UserInfo userInfo;
	private UserInfo parentUserInfo;
	private UserInfo grandpaUserInfo;

	/**
	 * 接口返回数据.
	 */
	// 移动电话.
	private String mobilePhone;
	// 真实姓名.
	private String realName;
	// 注册时间.
	private Date registerDate;

	/**
	 * 2016年9月份加息团活动.
	 */
	// 开始日期.
	private String beginDate;
	// 结束日期.
	private String endDate;

	public UserInfo getUserInfo() {

		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {

		this.userInfo = userInfo;
	}

	public UserInfo getParentUserInfo() {

		return parentUserInfo;
	}

	public void setParentUserInfo(UserInfo parentUserInfo) {

		this.parentUserInfo = parentUserInfo;
	}

	public UserInfo getGrandpaUserInfo() {

		return grandpaUserInfo;
	}

	public void setGrandpaUserInfo(UserInfo grandpaUserInfo) {

		this.grandpaUserInfo = grandpaUserInfo;
	}

	public String getUserId() {

		return userId;
	}

	public void setUserId(String userId) {

		this.userId = userId;
	}

	public String getParentId() {

		return parentId;
	}

	public void setParentId(String parentId) {

		this.parentId = parentId;
	}

	public String getGrandpaId() {

		return grandpaId;
	}

	public void setGrandpaId(String grandpaId) {

		this.grandpaId = grandpaId;
	}

	public String getInviteCode() {

		return inviteCode;
	}

	public void setInviteCode(String inviteCode) {

		this.inviteCode = inviteCode;
	}

	public String getType() {

		return type;
	}

	public void setType(String type) {

		this.type = type;
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

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getRegisterDate() {

		return registerDate;
	}

	public void setRegisterDate(Date registerDate) {

		this.registerDate = registerDate;
	}

	public String getBeginDate() {

		return beginDate;
	}

	public void setBeginDate(String beginDate) {

		this.beginDate = beginDate;
	}

	public String getEndDate() {

		return endDate;
	}

	public void setEndDate(String endDate) {

		this.endDate = endDate;
	}

}