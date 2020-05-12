/**
 * 银行编码对照Entity.
 */
package com.power.platform.cgb.entity;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;

/**
 * 银行编码对照Entity.
 * 
 * @author lance
 * @version 2017-11-28
 */
public class CicmorganBankCode extends DataEntity<CicmorganBankCode> {

	private static final long serialVersionUID = 1L;
	private String bankName; // 银行名称
	private String bankCode; // 银行编码
	private String remark; // 备注

	public CicmorganBankCode() {

		super();
	}

	public CicmorganBankCode(String id) {

		super(id);
	}

	@Length(min = 0, max = 128, message = "银行名称长度必须介于 0 和 128 之间")
	public String getBankName() {

		return bankName;
	}

	public void setBankName(String bankName) {

		this.bankName = bankName;
	}

	@Length(min = 0, max = 64, message = "银行编码长度必须介于 0 和 64 之间")
	public String getBankCode() {

		return bankCode;
	}

	public void setBankCode(String bankCode) {

		this.bankCode = bankCode;
	}

	@Length(min = 0, max = 255, message = "备注长度必须介于 0 和 255 之间")
	public String getRemark() {

		return remark;
	}

	public void setRemark(String remark) {

		this.remark = remark;
	}

}