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
public class BatchNum extends DataEntity<BatchNum> {

	private static final long serialVersionUID = 1L;
	private String batchNum; // 批次号
	private String sendTime; // 推送时间
	private String infType; // 接口类型
	private String totalNum; // 该批次封装数据条数
	private String status; // 该批次数据推送状态，00：成功，01：处理中，02：失败
	private String code; // 数据中心消息编码

	public BatchNum() {

		super();
	}

	public BatchNum(String id) {

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
	public String getSendTime() {

		return sendTime;
	}

	public void setSendTime(String sendTime) {

		this.sendTime = sendTime;
	}

	@Length(min = 0, max = 16, message = "接口类型长度必须介于 0 和 16 之间")
	public String getInfType() {

		return infType;
	}

	public void setInfType(String infType) {

		this.infType = infType;
	}

	@Length(min = 0, max = 16, message = "该批次封装数据条数长度必须介于 0 和 16 之间")
	public String getTotalNum() {

		return totalNum;
	}

	public void setTotalNum(String totalNum) {

		this.totalNum = totalNum;
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

}