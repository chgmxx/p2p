/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.zdw.entity;

import org.hibernate.validator.constraints.Length;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.power.platform.common.persistence.DataEntity;

/**
 * 中登网应收账款和转让记录登记列表Entity
 * 
 * @author Roy
 * @version 2019-07-07
 */
public class ZdwSearchRegisterInfo extends DataEntity<ZdwSearchRegisterInfo> {

	private static final long serialVersionUID = 1L;
	private String queryPerson; // 查询人企业名称
	private String guarantorCompanyName; // 担保人企业名称
	private Date queryDateTime; // 查询时间
	private String queryProveNo; // 查询证明编号
	private String queryProveFilePath; // 查询证明文件路径
	private String no; // 序号
	private String registerProveNo; // 登记证明编号
	private Date registerDateTime; // 登记时间
	private Date registerExpireDateTime; // 登记到期日
	private String registerType; // 登记种类
	private String pledgeeName; // 质权人名称
	private String registerProveFilePath; // 登记证明文件，多个文件逗号拼接

	public ZdwSearchRegisterInfo() {

		super();
	}

	public ZdwSearchRegisterInfo(String id) {

		super(id);
	}

	@Length(min = 0, max = 64, message = "查询人企业名称长度必须介于 0 和 64 之间")
	public String getQueryPerson() {

		return queryPerson;
	}

	public void setQueryPerson(String queryPerson) {

		this.queryPerson = queryPerson;
	}

	@Length(min = 0, max = 64, message = "担保人企业名称长度必须介于 0 和 64 之间")
	public String getGuarantorCompanyName() {

		return guarantorCompanyName;
	}

	public void setGuarantorCompanyName(String guarantorCompanyName) {

		this.guarantorCompanyName = guarantorCompanyName;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getQueryDateTime() {

		return queryDateTime;
	}

	public void setQueryDateTime(Date queryDateTime) {

		this.queryDateTime = queryDateTime;
	}

	@Length(min = 0, max = 64, message = "查询证明编号长度必须介于 0 和 64 之间")
	public String getQueryProveNo() {

		return queryProveNo;
	}

	public void setQueryProveNo(String queryProveNo) {

		this.queryProveNo = queryProveNo;
	}

	@Length(min = 0, max = 128, message = "查询证明文件路径长度必须介于 0 和 128 之间")
	public String getQueryProveFilePath() {

		return queryProveFilePath;
	}

	public void setQueryProveFilePath(String queryProveFilePath) {

		this.queryProveFilePath = queryProveFilePath;
	}

	@Length(min = 0, max = 64, message = "序号长度必须介于 0 和 64 之间")
	public String getNo() {

		return no;
	}

	public void setNo(String no) {

		this.no = no;
	}

	@Length(min = 0, max = 64, message = "登记证明编号长度必须介于 0 和 64 之间")
	public String getRegisterProveNo() {

		return registerProveNo;
	}

	public void setRegisterProveNo(String registerProveNo) {

		this.registerProveNo = registerProveNo;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getRegisterDateTime() {

		return registerDateTime;
	}

	public void setRegisterDateTime(Date registerDateTime) {

		this.registerDateTime = registerDateTime;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getRegisterExpireDateTime() {

		return registerExpireDateTime;
	}

	public void setRegisterExpireDateTime(Date registerExpireDateTime) {

		this.registerExpireDateTime = registerExpireDateTime;
	}

	@Length(min = 0, max = 32, message = "登记种类长度必须介于 0 和 32 之间")
	public String getRegisterType() {

		return registerType;
	}

	public void setRegisterType(String registerType) {

		this.registerType = registerType;
	}

	@Length(min = 0, max = 128, message = "质权人名称长度必须介于 0 和 128 之间")
	public String getPledgeeName() {

		return pledgeeName;
	}

	public void setPledgeeName(String pledgeeName) {

		this.pledgeeName = pledgeeName;
	}

	@Length(min = 0, max = 512, message = "登记证明文件，多个文件逗号拼接长度必须介于 0 和 512 之间")
	public String getRegisterProveFilePath() {

		return registerProveFilePath;
	}

	public void setRegisterProveFilePath(String registerProveFilePath) {

		this.registerProveFilePath = registerProveFilePath;
	}

}