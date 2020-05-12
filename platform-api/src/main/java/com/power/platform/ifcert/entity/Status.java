/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.ifcert.entity;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;

/**
 * 国家应急中心散标状态信息Entity
 * 
 * @author Roy
 * @version 2019-05-13
 */
public class Status extends DataEntity<Status> {

	private static final long serialVersionUID = 1L;
	private String version; // 数据中心接口版本号
	private String sourceCode; // 平台编码
	private String sourceProductCode; // 散标信息编号
	private String productStatus; // 散标状态编码
	private String productDate; // 散标状态更新时间
	private String batchNum; // 批次号
	private String sendTime; // 推送时间

	public Status() {

		super();
	}

	public Status(String id) {

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

	@Length(min = 0, max = 128, message = "散标信息编号长度必须介于 0 和 128 之间")
	public String getSourceProductCode() {

		return sourceProductCode;
	}

	public void setSourceProductCode(String sourceProductCode) {

		this.sourceProductCode = sourceProductCode;
	}

	@Length(min = 0, max = 20, message = "散标状态编码长度必须介于 0 和 20 之间")
	public String getProductStatus() {

		return productStatus;
	}

	public void setProductStatus(String productStatus) {

		this.productStatus = productStatus;
	}

	@Length(min = 0, max = 40, message = "散标状态更新时间长度必须介于 0 和 40 之间")
	public String getProductDate() {

		return productDate;
	}

	public void setProductDate(String productDate) {

		this.productDate = productDate;
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