/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.credit.entity.info;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;


/**
 * 借款资料Entity
 * @author yb
 * @version 2017-12-11
 */
public class CreditInfo extends DataEntity<CreditInfo> {
	
	private static final long serialVersionUID = 1L;
	private String creditUserId;		// 借款人ID
	private String name;		// 资料名称
	private String remark;		// 备注
	
	public CreditInfo() {
		super();
	}

	public CreditInfo(String id){
		super(id);
	}

	@Length(min=1, max=64, message="借款人ID长度必须介于 1 和 64 之间")
	public String getCreditUserId() {
		return creditUserId;
	}

	public void setCreditUserId(String creditUserId) {
		this.creditUserId = creditUserId;
	}
	
	@Length(min=0, max=255, message="资料名称长度必须介于 0 和 255 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Length(min=0, max=255, message="备注长度必须介于 0 和 255 之间")
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}