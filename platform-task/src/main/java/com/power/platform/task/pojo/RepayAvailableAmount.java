package com.power.platform.task.pojo;

/**
 * 
 * 类: RepayAvailableAmount <br>
 * 描述: 可用偿还金额. <br>
 * 作者: Mr.li <br>
 * 时间: 2018年11月23日 上午11:52:57
 */
public class RepayAvailableAmount {

	// 用户账户ID.
	public String userAccountId;
	// 可用余额.
	public Double availableAmount;

	public String getUserAccountId() {

		return userAccountId;
	}

	public void setUserAccountId(String userAccountId) {

		this.userAccountId = userAccountId;
	}

	public Double getAvailableAmount() {

		return availableAmount;
	}

	public void setAvailableAmount(Double availableAmount) {

		this.availableAmount = availableAmount;
	}

	@Override
	public String toString() {

		return "RepayAvailableAmount [userAccountId=" + userAccountId + ", availableAmount=" + availableAmount + "]";
	}

}
