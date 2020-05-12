/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.lanmao.entity;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;

/**
 * 懒猫交易留存Entity
 * 
 * @author Mr.yun.li
 * @version 2019-09-23
 */
public class LmTransaction extends DataEntity<LmTransaction> {

	private static final long serialVersionUID = 1L;
	private String serviceName; // 接口名称
	private String batchNo; // 批次号
	private String requestNo; // 请求流水号
	private String projectNo; // 标的编号
	private String tradeType; // 交易类型
	private String auditStatus; // 审核状态  AUDIT:审核中,PASSED:审核通过,BACK:审核回退,REFUSED:审核拒绝
	private String code; // 调用状态，0：成功，1：失败
	private String status; // 业务处理状态，SUCCESS：成功，PROCESSING：处理中，INIT：处理失败
	public static final String PROCESSING = "PROCESSING"; // 处理中
	public static final String SUCCESS = "SUCCESS"; // 成功
	public static final String INIT = "INIT"; // 处理失败
	private String accessType;//鉴权通过类型
	private String reviewStatus;//修改后审核状态；AUDIT 表示审核中---企业信息修改
	
	
	private String errorCode; // 错误码
	private String errorMessage; // 错误码描述

	private String platformUserNo; // 平台用户编号
	private String sourcePlatformUserNo; // 出款方用户编号
	private String targetPlatformUserNo; // 收款方用户编号
	private String originalFreezeRequestNo; // 原冻结的请求流水号，用户资金解冻

	public LmTransaction() {

		super();
	}

	public LmTransaction(String id) {

		super(id);
	}

	@Length(min = 0, max = 64, message = "接口名称长度必须介于 0 和 64 之间")
	public String getServiceName() {

		return serviceName;
	}

	public void setServiceName(String serviceName) {

		this.serviceName = serviceName;
	}

	@Length(min = 0, max = 64, message = "批次号长度必须介于 0 和 64 之间")
	public String getBatchNo() {

		return batchNo;
	}

	public void setBatchNo(String batchNo) {

		this.batchNo = batchNo;
	}

	@Length(min = 0, max = 64, message = "请求流水号长度必须介于 0 和 64 之间")
	public String getRequestNo() {

		return requestNo;
	}

	public void setRequestNo(String requestNo) {

		this.requestNo = requestNo;
	}

	@Length(min = 0, max = 64, message = "标的编号长度必须介于 0 和 64 之间")
	public String getProjectNo() {

		return projectNo;
	}

	public void setProjectNo(String projectNo) {

		this.projectNo = projectNo;
	}

	@Length(min = 0, max = 64, message = "交易类型长度必须介于 0 和 64 之间")
	public String getTradeType() {

		return tradeType;
	}

	public void setTradeType(String tradeType) {

		this.tradeType = tradeType;
	}
	
	@Length(min = 0, max = 64, message = "交易类型长度必须介于 0 和 64 之间")
	public String getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(String auditStatus) {
		this.auditStatus = auditStatus;
	}

	@Length(min = 0, max = 16, message = "调用状态，0：成功，1：失败长度必须介于 0 和 16 之间")
	public String getCode() {

		return code;
	}

	public void setCode(String code) {

		this.code = code;
	}

	@Length(min = 0, max = 16, message = "业务处理状态，SUCCESS：成功，FAIL：失败长度必须介于 0 和 16 之间")
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

	public String getPlatformUserNo() {

		return platformUserNo;
	}

	public void setPlatformUserNo(String platformUserNo) {

		this.platformUserNo = platformUserNo;
	}

	public String getSourcePlatformUserNo() {

		return sourcePlatformUserNo;
	}

	public void setSourcePlatformUserNo(String sourcePlatformUserNo) {

		this.sourcePlatformUserNo = sourcePlatformUserNo;
	}

	public String getTargetPlatformUserNo() {

		return targetPlatformUserNo;
	}

	public void setTargetPlatformUserNo(String targetPlatformUserNo) {

		this.targetPlatformUserNo = targetPlatformUserNo;
	}

	public String getOriginalFreezeRequestNo() {

		return originalFreezeRequestNo;
	}

	public void setOriginalFreezeRequestNo(String originalFreezeRequestNo) {

		this.originalFreezeRequestNo = originalFreezeRequestNo;
	}

	public String getAccessType() {
		return accessType;
	}

	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}

	public String getReviewStatus() {
		return reviewStatus;
	}

	public void setReviewStatus(String reviewStatus) {
		this.reviewStatus = reviewStatus;
	}

}