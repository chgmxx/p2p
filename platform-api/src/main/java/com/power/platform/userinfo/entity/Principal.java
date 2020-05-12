package com.power.platform.userinfo.entity;

import com.power.platform.cgb.entity.CgbUserAccount;
import com.power.platform.common.persistence.DataEntity;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;

public class Principal extends DataEntity<Principal> {
	
	private static final long serialVersionUID = 1L;
	private String id;
	private UserInfo userInfo;
	private UserAccountInfo userAccountInfo;
	private CreditUserInfo creditUserInfo;
	//银行存管
	private CgbUserAccount cgbUserAccount;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public UserInfo getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}
	public UserAccountInfo getUserAccountInfo() {
		return userAccountInfo;
	}
	public void setUserAccountInfo(UserAccountInfo userAccountInfo) {
		this.userAccountInfo = userAccountInfo;
	}
	public CreditUserInfo getCreditUserInfo() {
		return creditUserInfo;
	}
	public void setCreditUserInfo(CreditUserInfo creditUserInfo) {
		this.creditUserInfo = creditUserInfo;
	}
	public CgbUserAccount getCgbUserAccount() {
		return cgbUserAccount;
	}
	public void setCgbUserAccount(CgbUserAccount cgbUserAccount) {
		this.cgbUserAccount = cgbUserAccount;
	}
	
}