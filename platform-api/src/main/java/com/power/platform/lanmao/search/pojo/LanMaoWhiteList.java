package com.power.platform.lanmao.search.pojo;

import java.util.Date;

public class LanMaoWhiteList {
	/**
	 * 查询查询
	 */
	private String platformUserNo;//台用户编号
	
	private String projectNo; // 标的号
	
	private String transactionType; // 交易类型：网银转账充值【ONLINE_RECHARGE】
	private Date startTime; // 开始时间（yyyyMMddHHmmss）
	private Date endTime; // 结束时间（yyyyMMddHHmmss）
	
	private String accountNo;//专户账户号
	private String requestNo;//请求流水号	
	private String bankcardNo; // 银行卡号（加白名单选填）
	private String userRole; // 用户角色
	
	public String getRequestNo() {
		return requestNo;
	}
	public void setRequestNo(String requestNo) {
		this.requestNo = requestNo;
	}
	public String getPlatformUserNo() {
		return platformUserNo;
	}
	public void setPlatformUserNo(String platformUserNo) {
		this.platformUserNo = platformUserNo;
	}
	public String getBankcardNo() {
		return bankcardNo;
	}
	public void setBankcardNo(String bankcardNo) {
		this.bankcardNo = bankcardNo;
	}
	public String getUserRole() {
		return userRole;
	}
	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}
	public String getProjectNo() {
		return projectNo;
	}
	public void setProjectNo(String projectNo) {
		this.projectNo = projectNo;
	}
	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	
	public String getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	
	
}
