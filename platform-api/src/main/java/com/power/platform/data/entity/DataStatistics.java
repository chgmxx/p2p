package com.power.platform.data.entity;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;

/**
 * 类: DataStatistics <br>
 * 描述: 资产端对象. <br>
 * 作者: Yangzf <br>
 * 时间: 2019年11月17日 上午11:52:19
 */
public class DataStatistics extends DataEntity<DataStatistics> {

	private static final long serialVersionUID = 1L;
	private Integer loanSupplierCount; // 在贷供应商数量
	private Integer totalSupplierCount; // 累计供应商数量
	private Integer loanProjectCount; // 在贷项目数量
	private Integer totalProjectCount; // 累计项目数量
	private String loanPrincipal; // 在贷本金
	private String amountToPaid; // 待还金额
	
	private Integer loanAmountCount; // 累计放款笔数
	private Integer repaymentAmountCount; // 累计还款笔数
	private String loanAmount; // 累计放款金额
	private String repaymentAmount; // 累计还款金额
	
	
	private String averageLoanPeriod; // 平均借款期数--按照在贷供应商数量
	private String averageProjectPeriod; // 平均借款期数--按照在贷项目数量
	private String averageLoanInterestRate; // 平均在贷利率
	
	public DataStatistics() {

		super();
	}

	public DataStatistics(String id) {

		super(id);
	}

	public Integer getLoanSupplierCount() {
		return loanSupplierCount;
	}

	public void setLoanSupplierCount(Integer loanSupplierCount) {
		this.loanSupplierCount = loanSupplierCount;
	}

	public Integer getTotalSupplierCount() {
		return totalSupplierCount;
	}

	public void setTotalSupplierCount(Integer totalSupplierCount) {
		this.totalSupplierCount = totalSupplierCount;
	}

	public Integer getLoanProjectCount() {
		return loanProjectCount;
	}

	public void setLoanProjectCount(Integer loanProjectCount) {
		this.loanProjectCount = loanProjectCount;
	}

	public Integer getTotalProjectCount() {
		return totalProjectCount;
	}

	public void setTotalProjectCount(Integer totalProjectCount) {
		this.totalProjectCount = totalProjectCount;
	}

	public String getLoanPrincipal() {
		return loanPrincipal;
	}

	public void setLoanPrincipal(String loanPrincipal) {
		this.loanPrincipal = loanPrincipal;
	}

	public String getAmountToPaid() {
		return amountToPaid;
	}

	public void setAmountToPaid(String amountToPaid) {
		this.amountToPaid = amountToPaid;
	}

	public Integer getLoanAmountCount() {
		return loanAmountCount;
	}

	public void setLoanAmountCount(Integer loanAmountCount) {
		this.loanAmountCount = loanAmountCount;
	}

	public Integer getRepaymentAmountCount() {
		return repaymentAmountCount;
	}

	public void setRepaymentAmountCount(Integer repaymentAmountCount) {
		this.repaymentAmountCount = repaymentAmountCount;
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

	public String getAverageLoanPeriod() {
		return averageLoanPeriod;
	}

	public void setAverageLoanPeriod(String averageLoanPeriod) {
		this.averageLoanPeriod = averageLoanPeriod;
	}

	public String getAverageProjectPeriod() {
		return averageProjectPeriod;
	}

	public void setAverageProjectPeriod(String averageProjectPeriod) {
		this.averageProjectPeriod = averageProjectPeriod;
	}

	public String getAverageLoanInterestRate() {
		return averageLoanInterestRate;
	}

	public void setAverageLoanInterestRate(String averageLoanInterestRate) {
		this.averageLoanInterestRate = averageLoanInterestRate;
	}

	
}