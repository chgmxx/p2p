/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.lanmao.entity;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;

/**
 * 用户账户订单Entity
 * 
 * @author Mr.yun.li
 * @version 2019-10-03
 */
public class UserAccountOrder extends DataEntity<UserAccountOrder> {

	private static final long serialVersionUID = 1L;
	private String userId; // 用户（出借人/借款人）帐号id
	private String accountId; // 用户（出借人/借款人）账户id
	private String transId; // 交易ID（客户还款计划订单号）
	private String userRole; // 用户角色，INVESTOR：出借人，BORROWERS：借款人
	private String bizType; // 交易类型，TENDER：出借，PRINCIPAL：本金，INCOME:利息，MARKETING：平台营销款，MISCARRY：流标
	private String inOutType; // 收支类型，IN：收入，OUT：支出
	private Double amount; // 交易金额
	private String status; // 订单状态，CONFIRMING：待确认，SUCCESS：成功，FAIL：失败

	public UserAccountOrder() {

		super();
	}

	public UserAccountOrder(String id) {

		super(id);
	}

	@Length(min = 0, max = 64, message = "用户（出借人/借款人）帐号id长度必须介于 0 和 64 之间")
	public String getUserId() {

		return userId;
	}

	public void setUserId(String userId) {

		this.userId = userId;
	}

	@Length(min = 0, max = 64, message = "用户（出借人/借款人）账户id长度必须介于 0 和 64 之间")
	public String getAccountId() {

		return accountId;
	}

	@Length(min = 0, max = 64, message = "交易ID（客户还款计划订单号）长度必须介于 0 和 64 之间")
	public String getTransId() {

		return transId;
	}

	public void setTransId(String transId) {

		this.transId = transId;
	}

	public void setAccountId(String accountId) {

		this.accountId = accountId;
	}

	@Length(min = 0, max = 16, message = "用户角色，INVESTOR：出借人，BORROWERS：借款人长度必须介于 0 和 16 之间")
	public String getUserRole() {

		return userRole;
	}

	public void setUserRole(String userRole) {

		this.userRole = userRole;
	}

	@Length(min = 0, max = 16, message = "交易类型，TENDER：出借，REPAYMENT：还款，INDIRECT_COMPENSATORY：间接代偿，MARKETING：平台营销款，MISCARRY：流标长度必须介于 0 和 16 之间")
	public String getBizType() {

		return bizType;
	}

	public void setBizType(String bizType) {

		this.bizType = bizType;
	}

	@Length(min = 0, max = 16, message = "收支类型，IN：收入，OUT：支出长度必须介于 0 和 16 之间")
	public String getInOutType() {

		return inOutType;
	}

	public void setInOutType(String inOutType) {

		this.inOutType = inOutType;
	}

	public Double getAmount() {

		return amount;
	}

	public void setAmount(Double amount) {

		this.amount = amount;
	}

	@Length(min = 0, max = 16, message = "订单状态，CONFIRMING：待确认，SUCCESS：成功，FAIL：失败长度必须介于 0 和 16 之间")
	public String getStatus() {

		return status;
	}

	public void setStatus(String status) {

		this.status = status;
	}

}