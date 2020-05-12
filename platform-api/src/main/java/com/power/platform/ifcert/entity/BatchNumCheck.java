/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.ifcert.entity;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;

/**
 * 批次数据状态信息表Entity
 * 
 * @author Roy
 * @version 2019-05-07
 */
public class BatchNumCheck extends DataEntity<BatchNumCheck> {

	private static final long serialVersionUID = 1L;
	private String batchNum; // 批次号
	private String createTime; // 创建时间
	private String infType; // 接口类型
	private String errorMessage; // 错误信息  failed：为入库失败，网贷机构需要将该批次号下面的数据重新报送，批次号可以和原来的一样,
								 //		  isNot：为批次号无效；
	private String status; // 该批次数据入库状态，00：成功，01：处理中，02：失败
	private String code; // 消息编码
	private String message; // 查询结果描述

	public BatchNumCheck() {

		super();
	}

	public BatchNumCheck(String id) {

		super(id);
	}

	@Length(min = 0, max = 256, message = "批次号长度必须介于 0 和 256 之间")
	public String getBatchNum() {

		return batchNum;
	}

	public void setBatchNum(String batchNum) {

		this.batchNum = batchNum;
	}

	@Length(min = 0, max = 40, message = "推送时间长度必须介于 0 和 40 之间")
	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	
	@Length(min = 0, max = 16, message = "接口类型长度必须介于 0 和 16 之间")
	public String getInfType() {

		return infType;
	}

	public void setInfType(String infType) {

		this.infType = infType;
	}

	@Length(min = 0, max = 16, message = "该批次封装数据条数长度必须介于 0 和 16 之间")
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Length(min = 0, max = 2, message = "该批次数据推送状态，00：成功，01：处理中，02：失败长度必须介于 0 和 2 之间")
	public String getStatus() {

		return status;
	}

	public void setStatus(String status) {

		this.status = status;
	}

	@Length(min = 0, max = 16, message = "该批次封装数据条数长度必须介于 0 和 16 之间")
	public String getCode() {

		return code;
	}

	public void setCode(String code) {

		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}