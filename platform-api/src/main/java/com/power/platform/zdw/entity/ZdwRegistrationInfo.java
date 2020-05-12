/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.zdw.entity;

import java.util.Date;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;

/**
 * 中登网登记信息Entity
 * 
 * @author Roy
 * @version 2019-07-15
 */
public class ZdwRegistrationInfo extends DataEntity<ZdwRegistrationInfo> {

	private static final long serialVersionUID = 1L;
	private String num; // 满标次数
	private String proIdList; // 满标的标的ID列表，逗号拼接
	private String checkInNo; // 初始登记编号，登记成功后入库保存
	private String modifyCode; // 本次登记修改码，登记成功后入库保存
	private String proveFilePath; // 证明文件，登记成功后入库保存
	private String status; // 00：登记成功，01：等待登记，02，登记失败

	/**
	 * 查询条件.
	 */
	private Date beginCreateDateTime;
	private Date endCreateDateTime;

	public ZdwRegistrationInfo() {

		super();
	}

	public ZdwRegistrationInfo(String id) {

		super(id);
	}

	@Length(min = 0, max = 11, message = "满标次数长度必须介于 0 和 11 之间")
	public String getNum() {

		return num;
	}

	public void setNum(String num) {

		this.num = num;
	}

	@Length(min = 0, max = 512, message = "满标的标的ID列表，逗号拼接长度必须介于 0 和 512 之间")
	public String getProIdList() {

		return proIdList;
	}

	public void setProIdList(String proIdList) {

		this.proIdList = proIdList;
	}

	@Length(min = 0, max = 64, message = "初始登记编号，登记成功后入库保存长度必须介于 0 和 64 之间")
	public String getCheckInNo() {

		return checkInNo;
	}

	public void setCheckInNo(String checkInNo) {

		this.checkInNo = checkInNo;
	}

	@Length(min = 0, max = 32, message = "本次登记修改码，登记成功后入库保存长度必须介于 0 和 32 之间")
	public String getModifyCode() {

		return modifyCode;
	}

	public void setModifyCode(String modifyCode) {

		this.modifyCode = modifyCode;
	}

	@Length(min = 0, max = 128, message = "证明文件，登记成功后入库保存长度必须介于 0 和 128 之间")
	public String getProveFilePath() {

		return proveFilePath;
	}

	public void setProveFilePath(String proveFilePath) {

		this.proveFilePath = proveFilePath;
	}

	@Length(min = 0, max = 2, message = "00：登记成功，01：等待登记，02，登记失败长度必须介于 0 和 2 之间")
	public String getStatus() {

		return status;
	}

	public void setStatus(String status) {

		this.status = status;
	}

	public Date getBeginCreateDateTime() {

		return beginCreateDateTime;
	}

	public void setBeginCreateDateTime(Date beginCreateDateTime) {

		this.beginCreateDateTime = beginCreateDateTime;
	}

	public Date getEndCreateDateTime() {

		return endCreateDateTime;
	}

	public void setEndCreateDateTime(Date endCreateDateTime) {

		this.endCreateDateTime = endCreateDateTime;
	}

}