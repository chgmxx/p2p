package com.power.platform.bouns.entity;

import com.power.platform.common.persistence.DataEntity;
import com.power.platform.userinfo.entity.UserInfo;


/**
 * 用户积分信息Entity
 * @author Mr.Jia
 * @version 2016-12-13
 */
public class UserBounsPoint extends DataEntity<UserBounsPoint> {
	
	private static final long serialVersionUID = 1L;
	
	private String userId;
	private UserInfo userInfo; // 客户信息.
	private Integer score;		// 积分总数
	
	public UserBounsPoint() {
		super();
	}

	public UserBounsPoint(String id){
		super(id);
	}
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}
	
	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	@Override
	public String toString() {
		return "UserBounsPoint [userId=" + userId + ", score=" + score + "]";
	}
	
	
}