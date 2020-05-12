/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.ifcert.entity;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;

/**
 * 数据中心初始债权表Entity
 * 
 * @author Roy
 * @version 2019-05-14
 */
public class Creditor extends DataEntity<Creditor> {

	private static final long serialVersionUID = 1L;
	private String version; // 数据中心接口版本号
	private String sourceCode; // 平台编号
	private String finClaimid; // 初始债权编号
	private String sourceProductCode; // 散标信息编号
	private String userIdcardHash; // 出借人userIdcardHash
	private String invAmount; // 出借金额（元）
	private String invRate; // 出借预期年化利率
	private String invTime; // 出借开始计息的时间
	private String redpackage; // 出借红包[满减(元)]
	private String lockTime; // 债权转让，锁定截至时间
	private String batchNum; // 批次号
	private String sendTime; // 推送时间

	public Creditor() {

		super();
	}

	public Creditor(String id) {

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

	@Length(min = 0, max = 128, message = "初始债权编号长度必须介于 0 和 128 之间")
	public String getFinClaimid() {

		return finClaimid;
	}

	public void setFinClaimid(String finClaimid) {

		this.finClaimid = finClaimid;
	}

	@Length(min = 0, max = 128, message = "散标信息编号长度必须介于 0 和 128 之间")
	public String getSourceProductCode() {

		return sourceProductCode;
	}

	public void setSourceProductCode(String sourceProductCode) {

		this.sourceProductCode = sourceProductCode;
	}

	@Length(min = 0, max = 64, message = "出借人userIdcardHash长度必须介于 0 和 64 之间")
	public String getUserIdcardHash() {

		return userIdcardHash;
	}

	public void setUserIdcardHash(String userIdcardHash) {

		this.userIdcardHash = userIdcardHash;
	}

	@Length(min = 0, max = 20, message = "出借金额（元）长度必须介于 0 和 20 之间")
	public String getInvAmount() {

		return invAmount;
	}

	public void setInvAmount(String invAmount) {

		this.invAmount = invAmount;
	}

	@Length(min = 0, max = 20, message = "出借预期年化利率长度必须介于 0 和 20 之间")
	public String getInvRate() {

		return invRate;
	}

	public void setInvRate(String invRate) {

		this.invRate = invRate;
	}

	@Length(min = 0, max = 32, message = "出借开始计息的时间长度必须介于 0 和 32 之间")
	public String getInvTime() {

		return invTime;
	}

	public void setInvTime(String invTime) {

		this.invTime = invTime;
	}

	@Length(min = 0, max = 20, message = "出借红包[满减(元)]长度必须介于 0 和 20 之间")
	public String getRedpackage() {

		return redpackage;
	}

	public void setRedpackage(String redpackage) {

		this.redpackage = redpackage;
	}

	@Length(min = 0, max = 32, message = "债权转让，锁定截至时间长度必须介于 0 和 32 之间")
	public String getLockTime() {

		return lockTime;
	}

	public void setLockTime(String lockTime) {

		this.lockTime = lockTime;
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