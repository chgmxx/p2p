package com.power.platform.activity.entity;

import com.power.platform.common.persistence.DataEntity;

/**
 * 
 * 类: WeixinShareRelation <br>
 * 描述: 微信公众号分享关注关系表. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年4月25日 下午6:05:00
 */
public class WeixinShareRelation extends DataEntity<WeixinShareRelation> {

	private static final long serialVersionUID = 1L;
	/**
	 * 推荐人的手机号.
	 */
	private String phone;
	/**
	 * 奖励金额.
	 */
	private double amount;
	/**
	 * 推荐人的用户ID.
	 */
	private String reUserID;
	/**
	 * 推荐人OPENID.
	 */
	private String recommendUser;
	/**
	 * 被推荐人OPENID.
	 */
	private String toRecommendUser;
	/**
	 * 是否发放红包， 1：是，2：否.
	 */
	private String isGrantMoney;

	public String getPhone() {

		return phone;
	}

	public void setPhone(String phone) {

		this.phone = phone;
	}

	public double getAmount() {

		return amount;
	}

	public void setAmount(double amount) {

		this.amount = amount;
	}

	public String getRecommendUser() {

		return recommendUser;
	}

	public void setRecommendUser(String recommendUser) {

		this.recommendUser = recommendUser;
	}

	public String getToRecommendUser() {

		return toRecommendUser;
	}

	public void setToRecommendUser(String toRecommendUser) {

		this.toRecommendUser = toRecommendUser;
	}

	public String getIsGrantMoney() {

		return isGrantMoney;
	}

	public void setIsGrantMoney(String isGrantMoney) {

		this.isGrantMoney = isGrantMoney;
	}

	public String getReUserID() {

		return reUserID;
	}

	public void setReUserID(String reUserID) {

		this.reUserID = reUserID;
	}

}
