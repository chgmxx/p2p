/**
 * Copyright &copy; 2012-2016 <a
 * href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.credit.entity.electronic;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;

/**
 * 电子签章Entity
 * 
 * @author jice
 * @version 2018-03-20
 */
public class ElectronicSign extends DataEntity<ElectronicSign> {

	private static final long serialVersionUID = 1L;
	private String userId; // 供应商ID
	private String signId; // 签章id
	private String type; // 签章类型（1：个人章，2：公司章）.

	/**
	 * 1：个人章.
	 */
	public static final String ELECTRONIC_SIGN_TYPE_1 = "1";

	/**
	 * 2：公司章.
	 */
	public static final String ELECTRONIC_SIGN_TYPE_2 = "2";

	public ElectronicSign() {

		super();
	}

	public ElectronicSign(String id) {

		super(id);
	}

	@Length(min = 1, max = 64, message = "供应商ID长度必须介于 1 和 64 之间")
	public String getUserId() {

		return userId;
	}

	public void setUserId(String userId) {

		this.userId = userId;
	}

	@Length(min = 1, max = 64, message = "签章id长度必须介于 1 和 64 之间")
	public String getSignId() {

		return signId;
	}

	public void setSignId(String signId) {

		this.signId = signId;
	}

	public String getType() {

		return type;
	}

	public void setType(String type) {

		this.type = type;
	}

	public static long getSerialversionuid() {

		return serialVersionUID;
	}

}