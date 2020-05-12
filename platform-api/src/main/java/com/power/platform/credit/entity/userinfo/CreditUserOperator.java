/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.credit.entity.userinfo;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;


/**
 * 借款端，操作人Entity
 * @author yb
 * @version 2018-03-08
 */
public class CreditUserOperator extends DataEntity<CreditUserOperator> {
	
	private static final long serialVersionUID = 1L;
	private String phone;		// 手机号
	private String name;		// 姓名
	private String password;		// 密码
	private String creditUserId;		// 所属企业
	private String state;		// 账户状态0-不可用1-可用
	
	public CreditUserOperator() {
		super();
	}

	public CreditUserOperator(String id){
		super(id);
	}

	@Length(min=1, max=64, message="手机号长度必须介于 1 和 64 之间")
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	@Length(min=0, max=64, message="姓名长度必须介于 0 和 64 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Length(min=1, max=255, message="密码长度必须介于 1 和 255 之间")
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	@Length(min=0, max=64, message="所属企业长度必须介于 0 和 64 之间")
	public String getCreditUserId() {
		return creditUserId;
	}

	public void setCreditUserId(String creditUserId) {
		this.creditUserId = creditUserId;
	}
	
	@Length(min=0, max=1, message="账户状态0-不可用1-可用长度必须介于 0 和 1 之间")
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
}