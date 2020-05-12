/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.ifcert.entity;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;

/**
 * 数据中心还款计划表Entity
 * 
 * @author Roy
 * @version 2019-05-13
 */
public class RepayPlan extends DataEntity<RepayPlan> {

	private static final long serialVersionUID = 1L;
	private String version; // 数据中心接口版本号
	private String sourceCode; // 平台编码
	private String sourceProductCode; // 标的编号
	private String userIdcardHash; // 法人userIdCardHash
	private String totalIssue; // 分期还款总期数
	private String issue; // 当前第几期
	private String replanId; // 还款唯一标识以保证与流水一致
	private String curFund; // 本条记录还本
	private String curInterest; // 本条记录付息
	private String curServiceCharge; // 本条记录应还服务费
	private String repayTime; // 还款日期
	private String batchNum; // 批次号
	private String sendTime; // 推送时间

	public RepayPlan() {

		super();
	}

	public RepayPlan(String id) {

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

	@Length(min = 0, max = 128, message = "标的编号长度必须介于 0 和 128 之间")
	public String getSourceProductCode() {

		return sourceProductCode;
	}

	public void setSourceProductCode(String sourceProductCode) {

		this.sourceProductCode = sourceProductCode;
	}

	@Length(min = 0, max = 64, message = "法人userIdCardHash长度必须介于 0 和 64 之间")
	public String getUserIdcardHash() {

		return userIdcardHash;
	}

	public void setUserIdcardHash(String userIdcardHash) {

		this.userIdcardHash = userIdcardHash;
	}

	@Length(min = 0, max = 12, message = "分期还款总期数长度必须介于 0 和 12 之间")
	public String getTotalIssue() {

		return totalIssue;
	}

	public void setTotalIssue(String totalIssue) {

		this.totalIssue = totalIssue;
	}

	@Length(min = 0, max = 2, message = "当前第几期长度必须介于 0 和 2 之间")
	public String getIssue() {

		return issue;
	}

	public void setIssue(String issue) {

		this.issue = issue;
	}

	@Length(min = 0, max = 128, message = "还款唯一标识以保证与流水一致长度必须介于 0 和 128 之间")
	public String getReplanId() {

		return replanId;
	}

	public void setReplanId(String replanId) {

		this.replanId = replanId;
	}

	@Length(min = 0, max = 40, message = "本条记录还本长度必须介于 0 和 40 之间")
	public String getCurFund() {

		return curFund;
	}

	public void setCurFund(String curFund) {

		this.curFund = curFund;
	}

	@Length(min = 0, max = 40, message = "本条记录付息长度必须介于 0 和 40 之间")
	public String getCurInterest() {

		return curInterest;
	}

	public void setCurInterest(String curInterest) {

		this.curInterest = curInterest;
	}

	@Length(min = 0, max = 40, message = "本条记录应还服务费长度必须介于 0 和 40 之间")
	public String getCurServiceCharge() {

		return curServiceCharge;
	}

	public void setCurServiceCharge(String curServiceCharge) {

		this.curServiceCharge = curServiceCharge;
	}

	@Length(min = 0, max = 32, message = "还款日期长度必须介于 0 和 32 之间")
	public String getRepayTime() {

		return repayTime;
	}

	public void setRepayTime(String repayTime) {

		this.repayTime = repayTime;
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