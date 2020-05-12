/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.credit.entity.voucher;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;


/**
 * 发票信息Entity
 * @author jice
 * @version 2018-06-20
 */
public class CreditVoucherInfo extends DataEntity<CreditVoucherInfo> {
	
	private static final long serialVersionUID = 1L;
	private String userId;		// user_id
	private String phone;		// 电话
	private String bankName;		// 开户行
	private String bankNo;		// 开户账号
	private String toName;		// 收件人姓名
	private String toPhone;		// 收件人电话
	private String toAddr;		// 收件人地址
	
	public CreditVoucherInfo() {
		super();
	}

	public CreditVoucherInfo(String id){
		super(id);
	}
	
	@Length(min=0, max=255, message="电话长度必须介于 0 和 255 之间")
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	@Length(min=0, max=255, message="开户行长度必须介于 0 和 255 之间")
	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	
	@Length(min=0, max=255, message="开户账号长度必须介于 0 和 255 之间")
	public String getBankNo() {
		return bankNo;
	}

	public void setBankNo(String bankNo) {
		this.bankNo = bankNo;
	}
	
	@Length(min=0, max=255, message="收件人姓名长度必须介于 0 和 255 之间")
	public String getToName() {
		return toName;
	}

	public void setToName(String toName) {
		this.toName = toName;
	}
	
	@Length(min=0, max=255, message="收件人电话长度必须介于 0 和 255 之间")
	public String getToPhone() {
		return toPhone;
	}

	public void setToPhone(String toPhone) {
		this.toPhone = toPhone;
	}
	
	@Length(min=0, max=255, message="收件人地址长度必须介于 0 和 255 之间")
	public String getToAddr() {
		return toAddr;
	}

	public void setToAddr(String toAddr) {
		this.toAddr = toAddr;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
}