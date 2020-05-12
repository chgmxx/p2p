/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.ifcert.entity;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;

/**
 * 数据中心交易流水表Entity
 * 
 * @author Roy
 * @version 2019-05-16
 */
public class Transact extends DataEntity<Transact> {

	private static final long serialVersionUID = 1L;
	private String version; // 数据中心接口版本号
	private String sourceCode; // 平台编码
	private String transId; // 网贷机构交易流水号
	private String sourceProductCode; // 散标信息编号
	private String sourceProductName; // 散标名称
	private String finClaimId; // 债权编号
	private String transferId; // 转让信息编号
	private String replanId; // 还款计划编号
	private String transType; // 交易类型
	private String transMoney; // 交易金额（元）
	private String userIdcardHash; // 交易主体证件号HASH值
	private String transTime; // 交易发生时间
	private String batchNum; // 批次号
	private String sendTime; // 推送时间

	public Transact() {

		super();
	}

	public Transact(String id) {

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

	@Length(min = 0, max = 128, message = "网贷机构交易流水号长度必须介于 0 和 128 之间")
	public String getTransId() {

		return transId;
	}

	public void setTransId(String transId) {

		this.transId = transId;
	}

	@Length(min = 0, max = 128, message = "散标信息编号长度必须介于 0 和 128 之间")
	public String getSourceProductCode() {

		return sourceProductCode;
	}

	public void setSourceProductCode(String sourceProductCode) {

		this.sourceProductCode = sourceProductCode;
	}

	@Length(min = 0, max = 256, message = "散标名称长度必须介于 0 和 256 之间")
	public String getSourceProductName() {

		return sourceProductName;
	}

	public void setSourceProductName(String sourceProductName) {

		this.sourceProductName = sourceProductName;
	}

	@Length(min = 0, max = 128, message = "债权编号长度必须介于 0 和 128 之间")
	public String getFinClaimId() {

		return finClaimId;
	}

	public void setFinClaimId(String finClaimId) {

		this.finClaimId = finClaimId;
	}

	@Length(min = 0, max = 128, message = "转让信息编号长度必须介于 0 和 128 之间")
	public String getTransferId() {

		return transferId;
	}

	public void setTransferId(String transferId) {

		this.transferId = transferId;
	}

	@Length(min = 0, max = 128, message = "还款计划编号长度必须介于 0 和 128 之间")
	public String getReplanId() {

		return replanId;
	}

	public void setReplanId(String replanId) {

		this.replanId = replanId;
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

	@Length(min = 0, max = 64, message = "交易主体证件号HASH值长度必须介于 0 和 64 之间")
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