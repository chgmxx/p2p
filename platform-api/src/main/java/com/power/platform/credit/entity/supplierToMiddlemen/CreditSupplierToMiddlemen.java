package com.power.platform.credit.entity.supplierToMiddlemen;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.regular.entity.WloanSubject;

/**
 * 
 * 类: CreditSupplierToMiddlemen <br>
 * 描述: 借代中间表Entity. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2018年1月9日 下午4:35:00
 */
public class CreditSupplierToMiddlemen extends DataEntity<CreditSupplierToMiddlemen> {

	private static final long serialVersionUID = 1L;
	private String supplierId; // 供应商ID（供应商-借款户）
	private String middlemenId; // 中间商ID（核心企业-代偿户）

	private CreditUserInfo supplierUser; // 供应商.
	private CreditUserInfo middlemenUser; // 中间商（核心企业）.
	private WloanSubject wloanSubject;

	public CreditSupplierToMiddlemen() {

		super();
	}

	public CreditSupplierToMiddlemen(String id) {

		super(id);
	}

	@Length(min = 1, max = 64, message = "供应商ID（供应商-借款户）长度必须介于 1 和 64 之间")
	public String getSupplierId() {

		return supplierId;
	}

	public void setSupplierId(String supplierId) {

		this.supplierId = supplierId;
	}

	@Length(min = 1, max = 64, message = "中间商ID（核心企业-代偿户）长度必须介于 1 和 64 之间")
	public String getMiddlemenId() {

		return middlemenId;
	}

	public void setMiddlemenId(String middlemenId) {

		this.middlemenId = middlemenId;
	}

	public CreditUserInfo getSupplierUser() {

		return supplierUser;
	}

	public void setSupplierUser(CreditUserInfo supplierUser) {

		this.supplierUser = supplierUser;
	}

	public CreditUserInfo getMiddlemenUser() {

		return middlemenUser;
	}

	public void setMiddlemenUser(CreditUserInfo middlemenUser) {

		this.middlemenUser = middlemenUser;
	}

	public WloanSubject getWloanSubject() {
		return wloanSubject;
	}

	public void setWloanSubject(WloanSubject wloanSubject) {
		this.wloanSubject = wloanSubject;
	}

	
}