/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.ifcert.entity;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;

/**
 * 数据中心产品信息表Entity
 * 
 * @author Roy
 * @version 2019-05-17
 */
public class LendProduct extends DataEntity<LendProduct> {

	private static final long serialVersionUID = 1L;
	private String version; // 数据中心接口版本号
	private String sourceCode; // 平台编码
	private String sourceFinancingCode; // 产品信息编号
	private String financingStartTime; // 发布时间
	private String productName; // 产品名称
	private String rate; // 预期年化利率
	private String minRate; // 最小预期年化利率
	private String maxRate; // 最大预期年化利率
	private String term; // 产品期限（服务期限）天
	private String batchNum; // 批次号
	private String sendTime; // 推送时间

	public LendProduct() {

		super();
	}

	public LendProduct(String id) {

		super(id);
	}

	@Length(min = 0, max = 20, message = "数据中心接口版本号长度必须介于 0 和 20 之间")
	public String getVersion() {

		return version;
	}

	public void setVersion(String version) {

		this.version = version;
	}

	@Length(min = 0, max = 64, message = "平台编码长度必须介于 0 和 64 之间")
	public String getSourceCode() {

		return sourceCode;
	}

	public void setSourceCode(String sourceCode) {

		this.sourceCode = sourceCode;
	}

	@Length(min = 0, max = 128, message = "产品信息编号长度必须介于 0 和 128 之间")
	public String getSourceFinancingCode() {

		return sourceFinancingCode;
	}

	public void setSourceFinancingCode(String sourceFinancingCode) {

		this.sourceFinancingCode = sourceFinancingCode;
	}

	@Length(min = 0, max = 40, message = "发布时间长度必须介于 0 和 40 之间")
	public String getFinancingStartTime() {

		return financingStartTime;
	}

	public void setFinancingStartTime(String financingStartTime) {

		this.financingStartTime = financingStartTime;
	}

	@Length(min = 0, max = 255, message = "产品名称长度必须介于 0 和 255 之间")
	public String getProductName() {

		return productName;
	}

	public void setProductName(String productName) {

		this.productName = productName;
	}

	@Length(min = 0, max = 32, message = "预期年化利率长度必须介于 0 和 32 之间")
	public String getRate() {

		return rate;
	}

	public void setRate(String rate) {

		this.rate = rate;
	}

	@Length(min = 0, max = 32, message = "最小预期年化利率长度必须介于 0 和 32 之间")
	public String getMinRate() {

		return minRate;
	}

	public void setMinRate(String minRate) {

		this.minRate = minRate;
	}

	@Length(min = 0, max = 32, message = "最大预期年化利率长度必须介于 0 和 32 之间")
	public String getMaxRate() {

		return maxRate;
	}

	public void setMaxRate(String maxRate) {

		this.maxRate = maxRate;
	}

	@Length(min = 0, max = 10, message = "产品期限（服务期限）天长度必须介于 0 和 10 之间")
	public String getTerm() {

		return term;
	}

	public void setTerm(String term) {

		this.term = term;
	}

	@Length(min = 0, max = 256, message = "批次号长度必须介于 0 和 256 之间")
	public String getBatchNum() {

		return batchNum;
	}

	public void setBatchNum(String batchNum) {

		this.batchNum = batchNum;
	}

	@Length(min = 0, max = 40, message = "推送时间长度必须介于 0 和 40 之间")
	public String getSendTime() {

		return sendTime;
	}

	public void setSendTime(String sendTime) {

		this.sendTime = sendTime;
	}

}