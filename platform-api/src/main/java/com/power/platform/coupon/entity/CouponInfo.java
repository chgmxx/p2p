package com.power.platform.coupon.entity;

import com.power.platform.common.persistence.DataEntity;

public class CouponInfo extends DataEntity<CouponInfo> {

	private static final long serialVersionUID = 1L;
	// 状态 1可修改删除 2已经在使用，不可更改
	private String state;
	// 类型 1抵用券 2现金券
	private String type;
	// 有限时间
	private Integer overdue;
	// 金额
	private Integer amount;
	// 起投金额
	private Integer limitMoney;

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getOverdue() {
		return overdue;
	}

	public void setOverdue(Integer overdue) {
		this.overdue = overdue;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public Integer getLimitMoney() {
		return limitMoney;
	}

	public void setLimitMoney(Integer limitMoney) {
		this.limitMoney = limitMoney;
	}

}
