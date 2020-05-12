package com.power.platform.sms.entity;

import org.hibernate.validator.constraints.Length;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.power.platform.common.persistence.DataEntity;

/**
 * 
 * 类: SmsRejectHistory <br>
 * 描述: 短信验证码屏蔽Entity. <br>
 */
public class SmsRejectHistory extends DataEntity<SmsRejectHistory> {

	private static final long serialVersionUID = 1L;
	/**
	 * 移动电话.
	 */
	private String phone;

	/**
	 * 屏蔽类型. 0 手机 1ip
	 */
	private String type; 

	
	/**
	 * 创建时间.
	 */
	private Date createTime;
	/**
	 * 发送次数.
	 */
	private Integer times;
	/**
	 * IP.
	 */
	private String ip;
	/**
	 * 开始时间(查询).
	 */
	private String beginCreateTime;
	/**
	 * 结束时间(查询).
	 */
	private String endCreateTime;

	public SmsRejectHistory() {

		super();
	}

	public SmsRejectHistory(String id) {

		super(id);
	}

	@Length(min = 0, max = 255, message = "phone长度必须介于 0 和 255 之间")
	public String getPhone() {

		return phone;
	}

	public void setPhone(String phone) {

		this.phone = phone;
	}

	
	public Integer getTimes() {
		return times;
	}

	public void setTimes(Integer times) {
		this.times = times;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getCreateTime() {

		return createTime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setCreateTime(Date createTime) {

		this.createTime = createTime;
	}

	@Length(min = 0, max = 64, message = "ip长度必须介于 0 和 64 之间")
	public String getIp() {

		return ip;
	}

	public void setIp(String ip) {

		this.ip = ip;
	}

	public String getBeginCreateTime() {

		return beginCreateTime;
	}

	public void setBeginCreateTime(String beginCreateTime) {

		this.beginCreateTime = beginCreateTime;
	}

	public String getEndCreateTime() {

		return endCreateTime;
	}

	public void setEndCreateTime(String endCreateTime) {

		this.endCreateTime = endCreateTime;
	}

}