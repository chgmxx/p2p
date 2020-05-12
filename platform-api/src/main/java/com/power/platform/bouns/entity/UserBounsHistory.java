package com.power.platform.bouns.entity;

import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;
import com.power.platform.userinfo.entity.UserInfo;

/**
 * 
 * class: UserBounsHistory <br>
 * description: 用户积分历史明细 <br>
 * author: Mr.Roy <br>
 * date: 2018年12月3日 上午11:34:52
 */
public class UserBounsHistory extends DataEntity<UserBounsHistory> {

	private static final long serialVersionUID = 1L;
	private String userId; // 用户id
	private Double amount; // 积分值
	private String bounsType; // 积分类型（0-投资,1-注册,2-邀请好友,3-签到）
	private String transId;
	private UserInfo userInfo;
	private AwardInfo awardInfo;

	private String beginCreateDate;
	private String endCreateDate;
	private List<String> typeList;

	private String currentAmount; // 当前剩余积分
	private String registerDate; // 注册时间
	

	public UserBounsHistory() {

		super();
	}

	public UserBounsHistory(String id) {

		super(id);
	}

	@Length(min = 0, max = 64, message = "用户id长度必须介于 0 和 64 之间")
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

	@Length(min = 0, max = 11, message = "积分值长度必须介于 0 和 11 之间")
	public Double getAmount() {

		return amount;
	}

	public void setAmount(Double amount) {

		this.amount = amount;
	}

	@Length(min = 0, max = 1, message = "积分类型（0-投资,1-注册,2-邀请好友,3-签到）长度必须介于 0 和 1 之间")
	public String getBounsType() {

		return bounsType;
	}

	public void setBounsType(String bounsType) {

		this.bounsType = bounsType;
	}

	public String getTransId() {

		return transId;
	}

	public void setTransId(String transId) {

		this.transId = transId;
	}

	public AwardInfo getAwardInfo() {

		return awardInfo;
	}

	public void setAwardInfo(AwardInfo awardInfo) {

		this.awardInfo = awardInfo;
	}

	public String getBeginCreateDate() {

		return beginCreateDate;
	}

	public void setBeginCreateDate(String beginCreateDate) {

		this.beginCreateDate = beginCreateDate;
	}

	public String getEndCreateDate() {

		return endCreateDate;
	}

	public void setEndCreateDate(String endCreateDate) {

		this.endCreateDate = endCreateDate;
	}

	public List<String> getTypeList() {

		return typeList;
	}

	public void setTypeList(List<String> typeList) {

		this.typeList = typeList;
	}

	public String getCurrentAmount() {

		return currentAmount;
	}

	public void setCurrentAmount(String currentAmount) {

		this.currentAmount = currentAmount;
	}

	
	public String getRegisterDate() {
	
		return registerDate;
	}

	
	public void setRegisterDate(String registerDate) {
	
		this.registerDate = registerDate;
	}
	

}