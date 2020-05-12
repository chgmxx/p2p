/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.credit.entity.bankcardinfo;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;


/**
 * 信贷银行卡Entity
 * @author nice
 * @version 2017-03-23
 */
public class CreditBankCardInfo extends DataEntity<CreditBankCardInfo> {
	
	private static final long serialVersionUID = 1L;
	private String creditUserId;		// 外键ID
	private String bankCardNo;		// 银行卡号
	private String bankName;		// 开户行
	private String mobile;		// 银行预留手机
	private String remark;		// 备注
	
	private CreditUserInfo creditUserInfo;
	
	public CreditBankCardInfo() {
		super();
	}

	public CreditBankCardInfo(String id){
		super(id);
	}

	@Length(min=0, max=64, message="外键ID长度必须介于 0 和 64 之间")
	public String getCreditUserId() {
		return creditUserId;
	}

	public void setCreditUserId(String creditUserId) {
		this.creditUserId = creditUserId;
	}
	
	@Length(min=0, max=64, message="银行卡号长度必须介于 0 和 64 之间")
	public String getBankCardNo() {
		return bankCardNo;
	}

	public void setBankCardNo(String bankCardNo) {
		this.bankCardNo = bankCardNo;
	}
	
	@Length(min=0, max=255, message="开户行长度必须介于 0 和 255 之间")
	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	
	@Length(min=0, max=11, message="银行预留手机长度必须介于 0 和 11 之间")
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	@Length(min=0, max=255, message="备注长度必须介于 0 和 255 之间")
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public CreditUserInfo getCreditUserInfo() {
		return creditUserInfo;
	}

	public void setCreditUserInfo(CreditUserInfo creditUserInfo) {
		this.creditUserInfo = creditUserInfo;
	}
	
	
	
}