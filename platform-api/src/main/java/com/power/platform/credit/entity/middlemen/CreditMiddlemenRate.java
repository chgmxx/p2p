/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.credit.entity.middlemen;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;


/**
 * 项目期限和利率Entity
 * @author yb
 * @version 2018-04-20
 */
public class CreditMiddlemenRate extends DataEntity<CreditMiddlemenRate> {
	
	private static final long serialVersionUID = 1L;
	private String creditUserId;		// 核心企业ID
	private String span;		// 项目期限
	private String rate;		// 利率
	private String serviceRate; //服务费率
	private CreditUserInfo userInfo;
	
	public CreditMiddlemenRate() {
		super();
	}

	public CreditMiddlemenRate(String id){
		super(id);
	}

	@Length(min=1, max=64, message="核心企业ID长度必须介于 1 和 64 之间")
	public String getCreditUserId() {
		return creditUserId;
	}

	public void setCreditUserId(String creditUserId) {
		this.creditUserId = creditUserId;
	}
	
	@Length(min=1, max=64, message="项目期限长度必须介于 1 和 64 之间")
	public String getSpan() {
		return span;
	}

	public void setSpan(String span) {
		this.span = span;
	}
	
	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public CreditUserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(CreditUserInfo userInfo) {
		this.userInfo = userInfo;
	}

	public String getServiceRate() {
		return serviceRate;
	}

	public void setServiceRate(String serviceRate) {
		this.serviceRate = serviceRate;
	}
	
	
	
}