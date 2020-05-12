/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.ifcert.entity;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;

/**
 * 国家应急中心散标信息Entity
 * 
 * @author Roy
 * @version 2019-05-09
 */
public class ScatterInvest extends DataEntity<ScatterInvest> {

	private static final long serialVersionUID = 1L;
	private String version; // version
	private String sourceCode; // sourcecode
	private String productStartTime; // productstarttime
	private String productName; // productname
	private String sourceProductCode; // sourceproductcode
	private String userIdcardHash; // useridcardhash
	private String loanUse; // loanuse
	private String loanDescribe; // loandescribe
	private String loanRate; // loanrate
	private String amount; // amount
	private String surplusAmount; // surplusamount
	private String term; // term
	private String payType; // paytype
	private String serviceCost; // servicecost
	private String loanType; // loantype
	private String securityType; // securitytype
	private String securityCompanyAmount; // securitycompanyamount
	private String securityCompanyName; // securitycompanyname
	private String securityCompanyIdcard; // securitycompanyidcard
	private String isFinancingAssure; // isfinancingassure
	private String securityamount; // securityamount
	private String projectSource; // projectsource
	private String batchNum; // batchnum
	private String sentTime; // sendtime

	public ScatterInvest() {

		super();
	}

	public ScatterInvest(String id) {

		super(id);
	}

	@Length(min = 0, max = 20, message = "version长度必须介于 0 和 20 之间")
	public String getVersion() {

		return version;
	}

	public void setVersion(String version) {

		this.version = version;
	}

	@Length(min = 0, max = 64, message = "sourcecode长度必须介于 0 和 64 之间")
	public String getSourceCode() {

		return sourceCode;
	}

	public void setSourceCode(String sourceCode) {

		this.sourceCode = sourceCode;
	}

	@Length(min = 0, max = 40, message = "productstarttime长度必须介于 0 和 40 之间")
	public String getProductStartTime() {

		return productStartTime;
	}

	public void setProductStartTime(String productStartTime) {

		this.productStartTime = productStartTime;
	}

	@Length(min = 0, max = 256, message = "productname长度必须介于 0 和 256 之间")
	public String getProductName() {

		return productName;
	}

	public void setProductName(String productName) {

		this.productName = productName;
	}

	@Length(min = 0, max = 128, message = "sourceproductcode长度必须介于 0 和 128 之间")
	public String getSourceProductCode() {

		return sourceProductCode;
	}

	public void setSourceProductCode(String sourceProductCode) {

		this.sourceProductCode = sourceProductCode;
	}

	@Length(min = 0, max = 64, message = "useridcardhash长度必须介于 0 和 64 之间")
	public String getUserIdcardHash() {

		return userIdcardHash;
	}

	public void setUserIdcardHash(String userIdcardHash) {

		this.userIdcardHash = userIdcardHash;
	}

	@Length(min = 0, max = 10, message = "loanuse长度必须介于 0 和 10 之间")
	public String getLoanUse() {

		return loanUse;
	}

	public void setLoanUse(String loanUse) {

		this.loanUse = loanUse;
	}

	@Length(min = 0, max = 2000, message = "loandescribe长度必须介于 0 和 2000 之间")
	public String getLoanDescribe() {

		return loanDescribe;
	}

	public void setLoanDescribe(String loanDescribe) {

		this.loanDescribe = loanDescribe;
	}

	@Length(min = 0, max = 32, message = "loanrate长度必须介于 0 和 32 之间")
	public String getLoanRate() {

		return loanRate;
	}

	public void setLoanRate(String loanRate) {

		this.loanRate = loanRate;
	}

	@Length(min = 0, max = 32, message = "amount长度必须介于 0 和 32 之间")
	public String getAmount() {

		return amount;
	}

	public void setAmount(String amount) {

		this.amount = amount;
	}

	@Length(min = 0, max = 32, message = "surplusamount长度必须介于 0 和 32 之间")
	public String getSurplusAmount() {

		return surplusAmount;
	}

	public void setSurplusAmount(String surplusAmount) {

		this.surplusAmount = surplusAmount;
	}

	@Length(min = 0, max = 5, message = "term长度必须介于 0 和 5 之间")
	public String getTerm() {

		return term;
	}

	public void setTerm(String term) {

		this.term = term;
	}

	@Length(min = 0, max = 5, message = "paytype长度必须介于 0 和 5 之间")
	public String getPayType() {

		return payType;
	}

	public void setPayType(String payType) {

		this.payType = payType;
	}

	@Length(min = 0, max = 32, message = "servicecost长度必须介于 0 和 32 之间")
	public String getServiceCost() {

		return serviceCost;
	}

	public void setServiceCost(String serviceCost) {

		this.serviceCost = serviceCost;
	}

	@Length(min = 0, max = 64, message = "loantype长度必须介于 0 和 64 之间")
	public String getLoanType() {

		return loanType;
	}

	public void setLoanType(String loanType) {

		this.loanType = loanType;
	}

	@Length(min = 0, max = 50, message = "securitytype长度必须介于 0 和 50 之间")
	public String getSecurityType() {

		return securityType;
	}

	public void setSecurityType(String securityType) {

		this.securityType = securityType;
	}

	@Length(min = 0, max = 10, message = "securitycompanyamount长度必须介于 0 和 10 之间")
	public String getSecurityCompanyAmount() {

		return securityCompanyAmount;
	}

	public void setSecurityCompanyAmount(String securityCompanyAmount) {

		this.securityCompanyAmount = securityCompanyAmount;
	}

	@Length(min = 0, max = 255, message = "securitycompanyname长度必须介于 0 和 255 之间")
	public String getSecurityCompanyName() {

		return securityCompanyName;
	}

	public void setSecurityCompanyName(String securityCompanyName) {

		this.securityCompanyName = securityCompanyName;
	}

	@Length(min = 0, max = 64, message = "securitycompanyidcard长度必须介于 0 和 64 之间")
	public String getSecurityCompanyIdcard() {

		return securityCompanyIdcard;
	}

	public void setSecurityCompanyIdcard(String securityCompanyIdcard) {

		this.securityCompanyIdcard = securityCompanyIdcard;
	}

	@Length(min = 0, max = 1, message = "isfinancingassure长度必须介于 0 和 1 之间")
	public String getIsFinancingAssure() {

		return isFinancingAssure;
	}

	public void setIsFinancingAssure(String isFinancingAssure) {

		this.isFinancingAssure = isFinancingAssure;
	}

	@Length(min = 0, max = 32, message = "securityamount长度必须介于 0 和 32 之间")
	public String getSecurityamount() {

		return securityamount;
	}

	public void setSecurityamount(String securityamount) {

		this.securityamount = securityamount;
	}

	@Length(min = 0, max = 128, message = "projectsource长度必须介于 0 和 128 之间")
	public String getProjectSource() {

		return projectSource;
	}

	public void setProjectSource(String projectSource) {

		this.projectSource = projectSource;
	}

	@Length(min = 0, max = 256, message = "batchnum长度必须介于 0 和 256 之间")
	public String getBatchNum() {

		return batchNum;
	}

	public void setBatchNum(String batchNum) {

		this.batchNum = batchNum;
	}

	@Length(min = 0, max = 40, message = "sendtime长度必须介于 0 和 40 之间")
	public String getSentTime() {

		return sentTime;
	}

	public void setSentTime(String sentTime) {

		this.sentTime = sentTime;
	}

}