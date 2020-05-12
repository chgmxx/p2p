/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.lanmao.entity;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;

/**
 * 懒猫黑白名单Entity
 * 
 * @author Mr.yun.li
 * @version 2019-09-27
 */
public class CgbBigrechargeWhiteRecord extends DataEntity<CgbBigrechargeWhiteRecord> {

	private static final long serialVersionUID = 1L;
	private String platformId; // 平台编号
	private String userId; // 用户编号
	private String requestNo; // 请求流水号
	private String realName; // 用户姓名
	private String bankNo; // 银行卡号
	private String bankCode; // 银行编号
	private String status; // 状态（0:白名单；1黑名单；2灰名单）
	private String operationDesc; // 操作描述

	private String userRole; // 用户角色，INVESTOR：出借人，BORROWERS：借款人，COLLABORATOR：合作机构

	private String description; //描述
	public CgbBigrechargeWhiteRecord() {

		super();
	}

	public CgbBigrechargeWhiteRecord(String id) {

		super(id);
	}

	@Length(min = 0, max = 64, message = "平台编号长度必须介于 0 和 64 之间")
	public String getPlatformId() {

		return platformId;
	}

	public void setPlatformId(String platformId) {

		this.platformId = platformId;
	}

	@Length(min = 0, max = 64, message = "用户编号长度必须介于 0 和 64 之间")
	public String getUserId() {

		return userId;
	}

	public void setUserId(String userId) {

		this.userId = userId;
	}

	@Length(min = 0, max = 32, message = "用户姓名长度必须介于 0 和 32 之间")
	public String getRealName() {

		return realName;
	}

	public void setRealName(String realName) {

		this.realName = realName;
	}

	@Length(min = 0, max = 50, message = "银行卡号长度必须介于 0 和 50 之间")
	public String getBankNo() {

		return bankNo;
	}

	public void setBankNo(String bankNo) {

		this.bankNo = bankNo;
	}

	@Length(min = 0, max = 32, message = "银行编号长度必须介于 0 和 32 之间")
	public String getBankCode() {

		return bankCode;
	}

	public void setBankCode(String bankCode) {

		this.bankCode = bankCode;
	}

	@Length(min = 0, max = 1, message = "状态（0:白名单；2黑名单；3灰名单）长度必须介于 0 和 1 之间")
	public String getStatus() {

		return status;
	}

	public void setStatus(String status) {

		this.status = status;
	}

	@Length(min = 0, max = 64, message = "操作描述长度必须介于 0 和 64 之间")
	public String getOperationDesc() {

		return operationDesc;
	}

	public void setOperationDesc(String operationDesc) {

		this.operationDesc = operationDesc;
	}

	public String getUserRole() {

		return userRole;
	}

	public void setUserRole(String userRole) {

		this.userRole = userRole;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRequestNo() {
		return requestNo;
	}

	public void setRequestNo(String requestNo) {
		this.requestNo = requestNo;
	}

}