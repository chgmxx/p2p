/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.credit.entity.pack;

import java.util.Date;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.power.platform.common.persistence.DataEntity;



/**
 * 合同Entity
 * @author jice
 * @version 2018-03-14
 */
public class CreditPack extends DataEntity<CreditPack> {
	
	private static final long serialVersionUID = 1L;
	private String creditInfoId;		// creditInfoId
	private String coreName;		// 核心企业名称
	private String loanName;		// 供应商名称
	private String name;		// 合同名称
	private String no;		// 合同编号
	private String money;		// 合同金额
	private String type;		// 合同类型
	private Date userdDate;		// 合同有效期
	private Date signDate;		// 合同签订日期
	
	public CreditPack() {
		super();
	}

	public CreditPack(String id){
		super(id);
	}

	@Length(min=0, max=64, message="creditInfoId长度必须介于 0 和 64 之间")
	public String getCreditInfoId() {
		return creditInfoId;
	}

	public void setCreditInfoId(String creditInfoId) {
		this.creditInfoId = creditInfoId;
	}
	
	@Length(min=0, max=255, message="核心企业名称长度必须介于 0 和 255 之间")
	public String getCoreName() {
		return coreName;
	}

	public void setCoreName(String coreName) {
		this.coreName = coreName;
	}
	
	public String getLoanName() {
		return loanName;
	}

	public void setLoanName(String loanName) {
		this.loanName = loanName;
	}
	
	@Length(min=0, max=255, message="合同名称长度必须介于 0 和 255 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Length(min=0, max=255, message="合同编号长度必须介于 0 和 255 之间")
	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}
	
	@Length(min=0, max=255, message="合同金额长度必须介于 0 和 255 之间")
	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}
	
	@Length(min=0, max=64, message="合同类型长度必须介于 0 和 64 之间")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getUserdDate() {
		return userdDate;
	}

	public void setUserdDate(Date userdDate) {
		this.userdDate = userdDate;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getSignDate() {
		return signDate;
	}

	public void setSignDate(Date signDate) {
		this.signDate = signDate;
	}
	
}