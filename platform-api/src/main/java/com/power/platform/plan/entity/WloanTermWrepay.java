package com.power.platform.plan.entity;

import java.util.Date;

import com.power.platform.common.persistence.DataEntity;
import com.power.platform.regular.entity.WloanTermInvest;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.userinfo.entity.UserAccountInfo;
import com.power.platform.userinfo.entity.UserInfo;

public class WloanTermWrepay extends DataEntity<WloanTermWrepay> {

	private static final long serialVersionUID = 1L;

	private WloanTermProject wloanTermProject; // 定期项目主体
	
	private WloanTermUserPlan wloanTermUserPlan;   //个人还款计划
	
	private WloanTermInvest wloanTermInvest;       //投资表
	
	private UserInfo userInfo;					//用户
	
	private UserAccountInfo accountInfo;    //账户表
	
	private double amount;				//金额
	
	private String principal;		// 类型 1还本付息 2只还利息

	private Double interest;		// 应还金额，利息四舍五入后
	
	private Double interestTrue;    //应还金额，利息四舍五入前
	
	private Integer state; 		//状态 1：已还			
	
	private String sn;
	
	private double feeAmount;
	
	private String ip;
	
	private Date repaymentDate;   //还款日期

	
	public WloanTermProject getWloanTermProject() {
		return wloanTermProject;
	}

	public void setWloanTermProject(WloanTermProject wloanTermProject) {
		this.wloanTermProject = wloanTermProject;
	}

	public WloanTermUserPlan getWloanTermUserPlan() {
		return wloanTermUserPlan;
	}

	public void setWloanTermUserPlan(WloanTermUserPlan wloanTermUserPlan) {
		this.wloanTermUserPlan = wloanTermUserPlan;
	}

	public WloanTermInvest getWloanTermInvest() {
		return wloanTermInvest;
	}

	public void setWloanTermInvest(WloanTermInvest wloanTermInvest) {
		this.wloanTermInvest = wloanTermInvest;
	}


	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

	public UserAccountInfo getAccountInfo() {
		return accountInfo;
	}

	public void setAccountInfo(UserAccountInfo accountInfo) {
		this.accountInfo = accountInfo;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getPrincipal() {
		return principal;
	}

	public void setPrincipal(String principal) {
		this.principal = principal;
	}

	public Double getInterest() {
		return interest;
	}

	public void setInterest(Double interest) {
		this.interest = interest;
	}

	public Double getInterestTrue() {
		return interestTrue;
	}

	public void setInterestTrue(Double interestTrue) {
		this.interestTrue = interestTrue;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public double getFeeAmount() {
		return feeAmount;
	}

	public void setFeeAmount(double feeAmount) {
		this.feeAmount = feeAmount;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Date getRepaymentDate() {
		return repaymentDate;
	}

	public void setRepaymentDate(Date repaymentDate) {
		this.repaymentDate = repaymentDate;
	}
	
	
	
	

	
}
