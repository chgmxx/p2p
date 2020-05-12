/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.lanmao.entity;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;

/**
 * 批量交易日志Entity
 * 
 * @author Mr.yun.li
 * @version 2019-10-06
 */
public class AsyncTransactionLog extends DataEntity<AsyncTransactionLog> {

	private static final long serialVersionUID = 1L;
	private String asyncRequestNo; // 交易明细订单号
	private String freezeRequestNo; // 订单流水号，出借预处理号、还款预处理号完成失败定位以快速完成交易
	private String bizType; // 见【交易类型】
	private String bizOrigin; // 见【业务来源】
	private String createTime; // 交易发起时间
	private String transactionTime; // 交易完成时间
	private String status; // 交易状态，INIT表示处理中，SUCCESS表示成功，FAIL表示失败
	private String errorCode; // 错误码
	private String errorMessage; // 错误码描述

	public AsyncTransactionLog() {

		super();
	}

	public AsyncTransactionLog(String id) {

		super(id);
	}

	@Length(min = 0, max = 64, message = "交易明细订单号长度必须介于 0 和 64 之间")
	public String getAsyncRequestNo() {

		return asyncRequestNo;
	}

	public void setAsyncRequestNo(String asyncRequestNo) {

		this.asyncRequestNo = asyncRequestNo;
	}

	@Length(min = 0, max = 64, message = "订单流水号，出借预处理号、还款预处理号完成失败定位以快速完成交易长度必须介于 0 和 64 之间")
	public String getFreezeRequestNo() {

		return freezeRequestNo;
	}

	public void setFreezeRequestNo(String freezeRequestNo) {

		this.freezeRequestNo = freezeRequestNo;
	}

	@Length(min = 0, max = 16, message = "见【交易类型】长度必须介于 0 和 16 之间")
	public String getBizType() {

		return bizType;
	}

	public void setBizType(String bizType) {

		this.bizType = bizType;
	}

	@Length(min = 0, max = 16, message = "见【业务来源】长度必须介于 0 和 16 之间")
	public String getBizOrigin() {

		return bizOrigin;
	}

	public void setBizOrigin(String bizOrigin) {

		this.bizOrigin = bizOrigin;
	}

	public String getCreateTime() {

		return createTime;
	}

	public void setCreateTime(String createTime) {

		this.createTime = createTime;
	}

	public String getTransactionTime() {

		return transactionTime;
	}

	public void setTransactionTime(String transactionTime) {

		this.transactionTime = transactionTime;
	}

	@Length(min = 0, max = 16, message = "交易状态，INIT表示处理中，SUCCESS表示成功，FAIL表示失败长度必须介于 0 和 16 之间")
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