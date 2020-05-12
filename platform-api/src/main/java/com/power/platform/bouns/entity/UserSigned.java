package com.power.platform.bouns.entity;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;
import com.power.platform.userinfo.entity.UserInfo;

import java.util.Date;

/**
 * 
 * 类: UserSigned <br>
 * 描述: 客户签到Entity. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年12月13日 下午3:53:27
 */
public class UserSigned extends DataEntity<UserSigned> {

	private static final long serialVersionUID = 1L;
	private String userId; // 客户ID
	private Integer continuousTime; // 连续签到次数
	private Date beginCreateDate; // 开始 创建日期
	private Date endCreateDate; // 结束 创建日期
	private String beginDate; // 签到开始时间.
	private String endDate; // 签到结束时间.
	private UserInfo userInfo; // 客户信息.

	public UserSigned() {

		super();
	}

	public UserSigned(String id) {

		super(id);
	}

	@Length(min = 0, max = 64, message = "客户ID长度必须介于 0 和 64 之间")
	public String getUserId() {

		return userId;
	}

	public void setUserId(String userId) {

		this.userId = userId;
	}

	@Length(min = 0, max = 11, message = "连续签到次数长度必须介于 0 和 11 之间")
	public Integer getContinuousTime() {

		return continuousTime;
	}

	public void setContinuousTime(Integer continuousTime) {

		this.continuousTime = continuousTime;
	}

	public Date getBeginCreateDate() {

		return beginCreateDate;
	}

	public void setBeginCreateDate(Date beginCreateDate) {

		this.beginCreateDate = beginCreateDate;
	}

	public Date getEndCreateDate() {

		return endCreateDate;
	}

	public void setEndCreateDate(Date endCreateDate) {

		this.endCreateDate = endCreateDate;
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

	public UserInfo getUserInfo() {

		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {

		this.userInfo = userInfo;
	}

}