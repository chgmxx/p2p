/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.credit.entity.voucher;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;


/**
 * 开票详情Entity
 * @author jice
 * @version 2018-06-25
 */
public class CreditVoucherInfoDetail extends DataEntity<CreditVoucherInfoDetail> {
	
	private static final long serialVersionUID = 1L;
	private String applyId;		// 融资申请id
	private String title;		// 抬头
	private String number;		// 发票税号
	private String addr;		// 地址
	private String phone;		// 电话
	private String bankName;		// 开户行
	private String bankNo;		// 开户账号
	private String toName;		// 收件人姓名
	private String toPhone;		// 收件人电话
	private String toAddr;		// 收件人地址
	private String state;		//申请状态
	
	public CreditVoucherInfoDetail() {
		super();
	}

	public CreditVoucherInfoDetail(String id){
		super(id);
	}

	@Length(min=0, max=64, message="融资申请id长度必须介于 0 和 64 之间")
	public String getApplyId() {
		return applyId;
	}

	public void setApplyId(String applyId) {
		this.applyId = applyId;
	}
	
	@Length(min=0, max=255, message="抬头长度必须介于 0 和 255 之间")
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	@Length(min=0, max=255, message="发票税号长度必须介于 0 和 255 之间")
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}
	
	@Length(min=0, max=255, message="地址长度必须介于 0 和 255 之间")
	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
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

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
}