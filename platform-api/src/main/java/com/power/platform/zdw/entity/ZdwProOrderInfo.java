/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.zdw.entity;

import org.hibernate.validator.constraints.Length;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.power.platform.common.persistence.DataEntity;

/**
 * 中等网满标落单Entity
 * 
 * @author Roy
 * @version 2019-07-12
 */
public class ZdwProOrderInfo extends DataEntity<ZdwProOrderInfo> {

	private static final long serialVersionUID = 1L;
	private String proId; // 散标ID
	private String proNo; // 散标编号
	private String status; // 00：登记成功，01：等待登记，02，登记失败
	private Date fullDate; // 满标时间

	public ZdwProOrderInfo() {

		super();
	}

	public ZdwProOrderInfo(String id) {

		super(id);
	}

	@Length(min = 0, max = 64, message = "散标ID长度必须介于 0 和 64 之间")
	public String getProId() {

		return proId;
	}

	public void setProId(String proId) {

		this.proId = proId;
	}

	@Length(min = 0, max = 32, message = "散标编号长度必须介于 0 和 32 之间")
	public String getProNo() {

		return proNo;
	}

	public void setProNo(String proNo) {

		this.proNo = proNo;
	}

	@Length(min = 0, max = 2, message = "00：登记成功，01：等待登记，02，登记失败长度必须介于 0 和 2 之间")
	public String getStatus() {

		return status;
	}

	public void setStatus(String status) {

		this.status = status;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getFullDate() {

		return fullDate;
	}

	public void setFullDate(Date fullDate) {

		this.fullDate = fullDate;
	}

}