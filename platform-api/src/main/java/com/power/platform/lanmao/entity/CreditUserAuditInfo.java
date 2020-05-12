/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.lanmao.entity;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;

/**
 * 开户审核记录Entity
 * 
 * @author Mr.yun.li
 * @version 2019-09-28
 */
public class CreditUserAuditInfo extends DataEntity<CreditUserAuditInfo> {

	private static final long serialVersionUID = 1L;
	private String platformUserNo; // 平台用户编号
	private String auditStatus; // 审核状态
	private String userRole; // 用户角色
	private String bankcardNo; // 银行对公账户
	private String bankcode; // 银行编码
	private String remark; // 备注
	private String code; // 调用状态
	private String status; // 业务处理状态
	private String errorCode; // 错误码
	private String errorMessage; // 错误码描述

	public CreditUserAuditInfo() {

		super();
	}

	public CreditUserAuditInfo(String id) {

		super(id);
	}

	@Length(min = 0, max = 64, message = "平台用户编号长度必须介于 0 和 64 之间")
	public String getPlatformUserNo() {

		return platformUserNo;
	}

	public void setPlatformUserNo(String platformUserNo) {

		this.platformUserNo = platformUserNo;
	}

	@Length(min = 0, max = 16, message = "审核状态长度必须介于 0 和 16 之间")
	public String getAuditStatus() {

		return auditStatus;
	}

	public void setAuditStatus(String auditStatus) {

		this.auditStatus = auditStatus;
	}

	@Length(min = 0, max = 16, message = "用户角色长度必须介于 0 和 16 之间")
	public String getUserRole() {

		return userRole;
	}

	public void setUserRole(String userRole) {

		this.userRole = userRole;
	}

	@Length(min = 0, max = 32, message = "银行对公账户长度必须介于 0 和 32 之间")
	public String getBankcardNo() {

		return bankcardNo;
	}

	public void setBankcardNo(String bankcardNo) {

		this.bankcardNo = bankcardNo;
	}

	@Length(min = 0, max = 16, message = "银行编码长度必须介于 0 和 16 之间")
	public String getBankcode() {

		return bankcode;
	}

	public void setBankcode(String bankcode) {

		this.bankcode = bankcode;
	}

	@Length(min = 0, max = 128, message = "备注长度必须介于 0 和 128 之间")
	public String getRemark() {

		return remark;
	}

	public void setRemark(String remark) {

		this.remark = remark;
	}

	@Length(min = 0, max = 16, message = "调用状态长度必须介于 0 和 16 之间")
	public String getCode() {

		return code;
	}

	public void setCode(String code) {

		this.code = code;
	}

	@Length(min = 0, max = 16, message = "业务处理状态长度必须介于 0 和 16 之间")
	public String getStatus() {

		return status;
	}

	public void setStatus(String status) {

		this.status = status;
	}

	@Length(min = 0, max = 16, message = "错误码长度必须介于 0 和 16 之间")
	public String getErrorCode() {

		return errorCode;
	}

	public void setErrorCode(String errorCode) {

		this.errorCode = errorCode;
	}

	@Length(min = 0, max = 16, message = "错误码描述长度必须介于 0 和 16 之间")
	public String getErrorMessage() {

		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {

		this.errorMessage = errorMessage;
	}

}