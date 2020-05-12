package com.power.platform.activity.entity;

import com.power.platform.common.persistence.DataEntity;

/**
 * 
 * 类: WeixinShareDetails <br>
 * 描述: 微信分享红包流水表. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年4月25日 下午6:01:52
 */
public class WeixinShareDetails extends DataEntity<Brokerage> {

	private static final long serialVersionUID = 1L;
	private String type;
	private double amount;
	private String userId;
	private String state;

	public String getUserId() {

		return userId;
	}

	public void setUserId(String userId) {

		this.userId = userId;
	}

	public String getType() {

		return type;
	}

	public void setType(String type) {

		this.type = type;
	}

	public String getState() {

		return state;
	}

	public void setState(String state) {

		this.state = state;
	}

	public double getAmount() {

		return amount;
	}

	public void setAmount(double amount) {

		this.amount = amount;
	}

}
