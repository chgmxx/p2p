package com.power.platform.lanmao.search.pojo;

import java.util.Date;

public class LanMaoProject {
	/**
	 * 查询单个标的信息-明细
	 */
	private String code; // 调用状态(0 为调用成功、1 为失败,返回 1 时请看【调用失败错误码】及错 误码描述)
	private String status; // 业务处理状态(处理失败 INIT; 处理成功 SUCCESS),平台可根据非 SUCCESS 状态做相应处理,处理失败时可参考错误码及描述。
	private String errorCode; // 错误码
	private String errorMessage; // 错误码描述
	private String platformUserNo; // 借款方平台用户编号
	private String projectNo; // 标的号
	private String projectAmount; // 标的金额
	private String projectName; // 标的名称
	private String projectType; // 见【标的类型】
	private String projectPeriod; // 标的期限(单位:天)
	private String projectProperties; // 标的属性(STOCK 为存量标的,NEW 为新增标的)
	private String annualInterestRate; // 年化利率
	private String repaymentWay; // 见【还款方式】
	private String projectStatus; // 见【标的状态】
	private String loanAmount; // 已出借确认金额
	private String repaymentAmount; // 已还款确认本金
	private String income; // 已还利息
	private String tenderAmount; // 已出借金额
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getPlatformUserNo() {
		return platformUserNo;
	}
	public void setPlatformUserNo(String platformUserNo) {
		this.platformUserNo = platformUserNo;
	}
	public String getProjectNo() {
		return projectNo;
	}
	public void setProjectNo(String projectNo) {
		this.projectNo = projectNo;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getProjectType() {
		return projectType;
	}
	public void setProjectType(String projectType) {
		this.projectType = projectType;
	}
	public String getProjectPeriod() {
		return projectPeriod;
	}
	public void setProjectPeriod(String projectPeriod) {
		this.projectPeriod = projectPeriod;
	}
	public String getProjectProperties() {
		return projectProperties;
	}
	public void setProjectProperties(String projectProperties) {
		this.projectProperties = projectProperties;
	}
	public String getRepaymentWay() {
		return repaymentWay;
	}
	public void setRepaymentWay(String repaymentWay) {
		this.repaymentWay = repaymentWay;
	}
	public String getProjectStatus() {
		return projectStatus;
	}
	public void setProjectStatus(String projectStatus) {
		this.projectStatus = projectStatus;
	}
	public String getProjectAmount() {
		return projectAmount;
	}
	public void setProjectAmount(String projectAmount) {
		this.projectAmount = projectAmount;
	}
	public String getAnnualInterestRate() {
		return annualInterestRate;
	}
	public void setAnnualInterestRate(String annualInterestRate) {
		this.annualInterestRate = annualInterestRate;
	}
	public String getLoanAmount() {
		return loanAmount;
	}
	public void setLoanAmount(String loanAmount) {
		this.loanAmount = loanAmount;
	}
	public String getRepaymentAmount() {
		return repaymentAmount;
	}
	public void setRepaymentAmount(String repaymentAmount) {
		this.repaymentAmount = repaymentAmount;
	}
	public String getIncome() {
		return income;
	}
	public void setIncome(String income) {
		this.income = income;
	}
	public String getTenderAmount() {
		return tenderAmount;
	}
	public void setTenderAmount(String tenderAmount) {
		this.tenderAmount = tenderAmount;
	}
	



	
	
	
	
}
