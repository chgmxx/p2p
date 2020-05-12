/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.userinfo.entity;

import com.power.platform.common.persistence.DataEntity;

/**
 * 账户管理Entity
 * @author jiajunfeng
 * @version 2015-12-18
 */
public class UserAccountInfo extends DataEntity<UserAccountInfo> {
	
	private static final long serialVersionUID = 1L;
	private String userId;
	private Double totalAmount;						// 账户总额
	private Double availableAmount;					// 可用金额
	private Double cashAmount;						// 提现金额
	private Integer cashCount;						// 提现总额
	private Double rechargeAmount;					// 充值总额
	private Integer rechargeCount;					// 充值总次数
	private Double freezeAmount;					// 冻结金额
	private Double totalInterest;					// 总收益
	private Double currentAmount;					// 活期投资金额
	private Double regularDuePrincipal;				// 定期待收本金
	private Double regularDueInterest;				// 定期待收收益
	private Double regularTotalAmount;				// 定期投资总金额
	private Double regularTotalInterest;			// 定期累计收益
	private Double currentTotalInterest;			// 活期总收益
	private Double currentTotalAmount;				// 活期累计投资金额
	private Double currentYesterdayInterest;		// 活期昨日收益
	private Double reguarYesterdayInterest;			// 定期昨日收益
	private Double commission;						// 客户总佣金
	
	private UserInfo userInfo;						//	用户
	
	public UserInfo getUserInfo() {
		return userInfo;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Double getCommission() {
		return commission;
	}

	public void setCommission(Double commission) {
		this.commission = commission;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

	public UserAccountInfo() {
		super();
	}

	public UserAccountInfo(String id){
		super(id);
	}

						
	public Double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}
	
	public Double getAvailableAmount() {
		return availableAmount;
	}

	public void setAvailableAmount(Double availableAmount) {
		this.availableAmount = availableAmount;
	}
	
	public Double getCashAmount() {
		return cashAmount;
	}

	public void setCashAmount(Double cashAmount) {
		this.cashAmount = cashAmount;
	}
	
	public Integer getCashCount() {
		return cashCount;
	}

	public void setCashCount(Integer cashCount) {
		this.cashCount = cashCount;
	}
	
	public Double getRechargeAmount() {
		return rechargeAmount;
	}

	public void setRechargeAmount(Double rechargeAmount) {
		this.rechargeAmount = rechargeAmount;
	}
	
	public Integer getRechargeCount() {
		return rechargeCount;
	}

	public void setRechargeCount(Integer rechargeCount) {
		this.rechargeCount = rechargeCount;
	}
	
	public Double getFreezeAmount() {
		return freezeAmount;
	}

	public void setFreezeAmount(Double freezeAmount) {
		this.freezeAmount = freezeAmount;
	}
	
	public Double getTotalInterest() {
		return totalInterest;
	}

	public void setTotalInterest(Double totalInterest) {
		this.totalInterest = totalInterest;
	}
	
	public Double getCurrentAmount() {
		return currentAmount;
	}

	public void setCurrentAmount(Double currentAmount) {
		this.currentAmount = currentAmount;
	}
	
	public Double getRegularDuePrincipal() {
		return regularDuePrincipal;
	}

	public void setRegularDuePrincipal(Double regularDuePrincipal) {
		this.regularDuePrincipal = regularDuePrincipal;
	}
	
	public Double getRegularDueInterest() {
		return regularDueInterest;
	}

	public void setRegularDueInterest(Double regularDueInterest) {
		this.regularDueInterest = regularDueInterest;
	}
	
	public Double getRegularTotalAmount() {
		return regularTotalAmount;
	}

	public void setRegularTotalAmount(Double regularTotalAmount) {
		this.regularTotalAmount = regularTotalAmount;
	}
	
	public Double getRegularTotalInterest() {
		return regularTotalInterest;
	}

	public void setRegularTotalInterest(Double regularTotalInterest) {
		this.regularTotalInterest = regularTotalInterest;
	}
	
	public Double getCurrentTotalInterest() {
		return currentTotalInterest;
	}

	public void setCurrentTotalInterest(Double currentTotalInterest) {
		this.currentTotalInterest = currentTotalInterest;
	}
	
	public Double getCurrentTotalAmount() {
		return currentTotalAmount;
	}

	public void setCurrentTotalAmount(Double currentTotalAmount) {
		this.currentTotalAmount = currentTotalAmount;
	}
	
	public Double getCurrentYesterdayInterest() {
		return currentYesterdayInterest;
	}

	public void setCurrentYesterdayInterest(Double currentYesterdayInterest) {
		this.currentYesterdayInterest = currentYesterdayInterest;
	}
	
	public Double getReguarYesterdayInterest() {
		return reguarYesterdayInterest;
	}

	public void setReguarYesterdayInterest(Double reguarYesterdayInterest) {
		this.reguarYesterdayInterest = reguarYesterdayInterest;
	}
	
}