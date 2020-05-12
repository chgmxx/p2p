package com.power.platform.sms.entity;

import org.hibernate.validator.constraints.Length;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.power.platform.common.persistence.DataEntity;

/**
 * 
 * 类: SmsMsgHistory <br>
 * 描述: 短信消息验证码历史Entity. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年4月26日 下午6:36:57
 */
public class SmsMsgHistory extends DataEntity<SmsMsgHistory> {

	private static final long serialVersionUID = 1L;
	/**
	 * 移动电话.
	 */
	private String phone;
	/**
	 * 验证码.
	 */
	private String validateCode;
	/**
	 * 消息内容.
	 */
	private String msgContent;
	/**
	 * 创建时间.
	 */
	private Date createTime;
	/**
	 * 验证码历史类型.
	 */
	private Integer type;
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

	public SmsMsgHistory() {

		super();
	}

	public SmsMsgHistory(String id) {

		super(id);
	}

	@Length(min = 0, max = 255, message = "phone长度必须介于 0 和 255 之间")
	public String getPhone() {

		return phone;
	}

	public void setPhone(String phone) {

		this.phone = phone;
	}

	@Length(min = 0, max = 255, message = "validate_code长度必须介于 0 和 255 之间")
	public String getValidateCode() {

		return validateCode;
	}

	public void setValidateCode(String validateCode) {

		this.validateCode = validateCode;
	}

	@Length(min = 0, max = 255, message = "msg_content长度必须介于 0 和 255 之间")
	public String getMsgContent() {

		return msgContent;
	}

	public void setMsgContent(String msgContent) {

		this.msgContent = msgContent;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getCreateTime() {

		return createTime;
	}

	public void setCreateTime(Date createTime) {

		this.createTime = createTime;
	}

	public Integer getType() {

		return type;
	}

	public void setType(Integer type) {

		this.type = type;
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