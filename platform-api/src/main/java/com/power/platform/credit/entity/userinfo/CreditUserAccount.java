package com.power.platform.credit.entity.userinfo;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;

/**
 * 类: CreditUserAccount <br>
 * 描述: 借款人账户信息. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2017年6月26日 上午11:52:19
 */
public class CreditUserAccount extends DataEntity<CreditUserAccount> {

	private static final long serialVersionUID = 1L;
	private String creditUserId; // 借款用户ID
	private Double totalAmount; // 账户总额
	private String borrowingTotalAmount; // 借款总额.
	private Double availableAmount; // 可用金额
	private Double repayAmount; // 已还金额
	private Double surplusAmount; // 待还金额
	private Double freezeAmount; // 冻结金额
	private Double rechargeAmount; // 充值金额
	private Double withdrawAmount; // 提现金额
	private String remark; // 备注

	private CreditUserInfo creditUserInfo; // 借款人帐号.
	private String totalAmountStr; // 账户总额.
	private String availableAmountStr; // 可用金额.
	private String freezeAmountStr; // 冻结金额
	private String rechargeAmountStr; // 充值金额
	private String withdrawAmountStr; // 提现金额
	private String surplusAmountStr; // 待还金额
	private String repayAmountStr; // 已还金额

	public CreditUserAccount() {

		super();
	}

	public CreditUserAccount(String id) {

		super(id);
	}

	@Length(min = 1, max = 64, message = "借款用户ID长度必须介于 1 和 64 之间")
	public String getCreditUserId() {

		return creditUserId;
	}

	public void setCreditUserId(String creditUserId) {

		this.creditUserId = creditUserId;
	}

	public Double getTotalAmount() {

		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {

		this.totalAmount = totalAmount;
	}

	public String getBorrowingTotalAmount() {

		return borrowingTotalAmount;
	}

	public void setBorrowingTotalAmount(String borrowingTotalAmount) {

		this.borrowingTotalAmount = borrowingTotalAmount;
	}

	public Double getAvailableAmount() {

		return availableAmount;
	}

	public void setAvailableAmount(Double availableAmount) {

		this.availableAmount = availableAmount;
	}

	public Double getRepayAmount() {

		return repayAmount;
	}

	public void setRepayAmount(Double repayAmount) {

		this.repayAmount = repayAmount;
	}

	public Double getSurplusAmount() {

		return surplusAmount;
	}

	public void setSurplusAmount(Double surplusAmount) {

		this.surplusAmount = surplusAmount;
	}

	public Double getFreezeAmount() {

		return freezeAmount;
	}

	public void setFreezeAmount(Double freezeAmount) {

		this.freezeAmount = freezeAmount;
	}

	public Double getRechargeAmount() {

		return rechargeAmount;
	}

	public void setRechargeAmount(Double rechargeAmount) {

		this.rechargeAmount = rechargeAmount;
	}

	public Double getWithdrawAmount() {

		return withdrawAmount;
	}

	public void setWithdrawAmount(Double withdrawAmount) {

		this.withdrawAmount = withdrawAmount;
	}

	@Length(min = 0, max = 255, message = "备注长度必须介于 0 和 255 之间")
	public String getRemark() {

		return remark;
	}

	public void setRemark(String remark) {

		this.remark = remark;
	}

	public CreditUserInfo getCreditUserInfo() {

		return creditUserInfo;
	}

	public void setCreditUserInfo(CreditUserInfo creditUserInfo) {

		this.creditUserInfo = creditUserInfo;
	}

	public String getTotalAmountStr() {

		return totalAmountStr;
	}

	public void setTotalAmountStr(String totalAmountStr) {

		this.totalAmountStr = totalAmountStr;
	}

	public String getAvailableAmountStr() {

		return availableAmountStr;
	}

	public void setAvailableAmountStr(String availableAmountStr) {

		this.availableAmountStr = availableAmountStr;
	}

	public String getFreezeAmountStr() {

		return freezeAmountStr;
	}

	public void setFreezeAmountStr(String freezeAmountStr) {

		this.freezeAmountStr = freezeAmountStr;
	}

	public String getRechargeAmountStr() {

		return rechargeAmountStr;
	}

	public void setRechargeAmountStr(String rechargeAmountStr) {

		this.rechargeAmountStr = rechargeAmountStr;
	}

	public String getWithdrawAmountStr() {

		return withdrawAmountStr;
	}

	public void setWithdrawAmountStr(String withdrawAmountStr) {

		this.withdrawAmountStr = withdrawAmountStr;
	}

	public String getSurplusAmountStr() {

		return surplusAmountStr;
	}

	public void setSurplusAmountStr(String surplusAmountStr) {

		this.surplusAmountStr = surplusAmountStr;
	}

	public String getRepayAmountStr() {

		return repayAmountStr;
	}

	public void setRepayAmountStr(String repayAmountStr) {

		this.repayAmountStr = repayAmountStr;
	}

}