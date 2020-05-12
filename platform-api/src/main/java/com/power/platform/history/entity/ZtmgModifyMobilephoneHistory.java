package com.power.platform.history.entity;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;

/**
 * 历史记录表(更换客户手机)Entity
 * 
 * @author Roy
 * @version 2016-11-11
 */
public class ZtmgModifyMobilephoneHistory extends DataEntity<ZtmgModifyMobilephoneHistory> {

	private static final long serialVersionUID = 1L;
	private String oldmobilephone; // 旧手机号码
	private String newmobilephone; // 新手机号码

	public ZtmgModifyMobilephoneHistory() {

		super();
	}

	public ZtmgModifyMobilephoneHistory(String id) {

		super(id);
	}

	@Length(min = 0, max = 11, message = "旧手机号码长度必须介于 0 和 11 之间")
	public String getOldmobilephone() {

		return oldmobilephone;
	}

	public void setOldmobilephone(String oldmobilephone) {

		this.oldmobilephone = oldmobilephone;
	}

	@Length(min = 0, max = 11, message = "新手机号码长度必须介于 0 和 11 之间")
	public String getNewmobilephone() {

		return newmobilephone;
	}

	public void setNewmobilephone(String newmobilephone) {

		this.newmobilephone = newmobilephone;
	}

}