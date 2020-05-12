/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.ifcert.entity;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;

/**
 * 数据中心产品配置表Entity
 * 
 * @author Roy
 * @version 2019-05-17
 */
public class LendProductConfig extends DataEntity<LendProductConfig> {

	private static final long serialVersionUID = 1L;
	private String version; // 数据中心接口版本号
	private String sourceCode; // 平台编码
	private String configId; // 产品配置编号（标的ID）
	private String finClaimId; // 初始债权（出借信息ID）
	private String sourceFinancingCode; // 产品信息编号（标的编号）
	private String userIdcardHash; // 出借用户证件号Hash值
	private String sourceProductCode; // 散标信息编号
	private String batchNum; // 批次号
	private String sendTime; // 推送时间

	public LendProductConfig() {

		super();
	}

	public LendProductConfig(String id) {

		super(id);
	}

	public String getSourceProductCode() {

		return sourceProductCode;
	}

	public void setSourceProductCode(String sourceProductCode) {

		this.sourceProductCode = sourceProductCode;
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

	@Length(min = 0, max = 128, message = "产品配置编号（标的ID）长度必须介于 0 和 128 之间")
	public String getConfigId() {

		return configId;
	}

	public void setConfigId(String configId) {

		this.configId = configId;
	}

	@Length(min = 0, max = 128, message = "初始债权（出借信息ID）长度必须介于 0 和 128 之间")
	public String getFinClaimId() {

		return finClaimId;
	}

	public void setFinClaimId(String finClaimId) {

		this.finClaimId = finClaimId;
	}

	@Length(min = 0, max = 128, message = "产品信息编号（标的编号）长度必须介于 0 和 128 之间")
	public String getSourceFinancingCode() {

		return sourceFinancingCode;
	}

	public void setSourceFinancingCode(String sourceFinancingCode) {

		this.sourceFinancingCode = sourceFinancingCode;
	}

	@Length(min = 0, max = 64, message = "出借用户证件号Hash值长度必须介于 0 和 64 之间")
	public String getUserIdcardHash() {

		return userIdcardHash;
	}

	public void setUserIdcardHash(String userIdcardHash) {

		this.userIdcardHash = userIdcardHash;
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