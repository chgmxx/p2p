package com.power.platform.cms.entity;


import java.util.Date;

import com.power.platform.common.persistence.DataEntity;

/*
 * 用户留言
 * */
public class LeaveMessage extends DataEntity<LeaveMessage> {
	
	private static final long serialVersionUID = 1L;
	
//	private String id;
	private String name;
	private String mobile;
	private String bussinessName;
	private String message;
	private Date date;
	public LeaveMessage() {
		super();
		// TODO Auto-generated constructor stub
	}
	public LeaveMessage(String name, String mobile, String bussinessName,
			String message, Date date) {
		super();
		this.name = name;
		this.mobile = mobile;
		this.bussinessName = bussinessName;
		this.message = message;
		this.date = date;
	}
	public LeaveMessage(String id) {
		super(id);
		// TODO Auto-generated constructor stub
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getBussinessName() {
		return bussinessName;
	}
	public void setBussinessName(String bussinessName) {
		this.bussinessName = bussinessName;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date2) {
		this.date = date2;
	}
	@Override
	public String toString() {
		return "LeaveMessage [name=" + name + ", mobile=" + mobile
				+ ", bussinessName=" + bussinessName + ", message=" + message
				+ ", date=" + date + "]";
	}
	
	
}
