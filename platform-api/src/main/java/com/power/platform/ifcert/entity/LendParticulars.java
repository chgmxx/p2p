/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.ifcert.entity;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;

/**
 * 数据中心投资明细表Entity
 * 
 * @author Roy
 * @version 2019-05-17
 */
public class LendParticulars extends DataEntity<LendParticulars> {

	private static final long serialVersionUID = 1L;
	private String version; // 数据中心接口版本号
	private String sourceCode; // 平台编号
	private String transId; // 出借人每笔交易流水的唯一编号（出借记录ID）
	private String sourceFinancingCode; // 产品信息编号（标的编号）
	private String transType; // 交易类型
	private String transMoney; // 交易金额（元）
	private String userIdcardHash; // 出借用户证件号码Hash值
	private String transTime; // 交易发生时间
	private String batchNum; // 批次号
	private String sendTime; // 推送时间

	public LendParticulars() {

		super();
	}

	public LendParticulars(String id) {

		super(id);
	}

	@Length(min = 0, max = 20, message = "数据中心接口版本号长度必须介于 0 和 20 之间")
	public String getVersion() {

		return version;
	}

	public void setVersion(String version) {

		this.version = version;
	}

	@Length(min = 0, max = 64, message = "平台编号长度必须介于 0 和 64 之间")
	public String getSourceCode() {

		return sourceCode;
	}

	public void setSourceCode(String sourceCode) {

		this.sourceCode = sourceCode;
	}

	@Length(min = 0, max = 128, message = "出借人每笔交易流水的唯一编号（出借记录ID）长度必须介于 0 和 128 之间")
	public String getTransId() {

		return transId;
	}

	public void setTransId(String transId) {

		this.transId = transId;
	}

	@Length(min = 0, max = 128, message = "产品信息编号（标的编号）长度必须介于 0 和 128 之间")
	public String getSourceFinancingCode() {

		return sourceFinancingCode;
	}

	public void setSourceFinancingCode(String sourceFinancingCode) {

		this.sourceFinancingCode = sourceFinancingCode;
	}

	@Length(min = 0, max = 20, message = "交易类型长度必须介于 0 和 20 之间")
	public String getTransType() {

		return transType;
	}

	public void setTransType(String transType) {

		this.transType = transType;
	}

	@Length(min = 0, max = 32, message = "交易金额（元）长度必须介于 0 和 32 之间")
	public String getTransMoney() {

		return transMoney;
	}

	public void setTransMoney(String transMoney) {

		this.transMoney = transMoney;
	}

	@Length(min = 0, max = 64, message = "出借用户证件号码Hash值长度必须介于 0 和 64 之间")
	public String getUserIdcardHash() {

		return userIdcardHash;
	}

	public void setUserIdcardHash(String userIdcardHash) {

		this.userIdcardHash = userIdcardHash;
	}

	@Length(min = 0, max = 80, message = "交易发生时间长度必须介于 0 和 80 之间")
	public String getTransTime() {

		return transTime;
	}

	public void setTransTime(String transTime) {

		this.transTime = transTime;
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