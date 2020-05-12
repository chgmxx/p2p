package com.power.platform.more.stationletter.entity;

import java.util.Date;

import com.power.platform.common.persistence.DataEntity;

/**
 * 站内信Entity
 * @author Mr.Jia
 * @version 2016-06-06
 */
public class StationLetter extends DataEntity<StationLetter> {
	
	private static final long serialVersionUID = 1L;
	private String id;				// 主键ID
	private String userId;			// 用户ID
	private String letterType;		// 信件类型（1、投资  2、还款 3、充值 4、提现）
	private String title;			// 信件标题
	private String body;			// 信件内容
	private String state;			// 信件状态（1、未读 2、已读）
	private Date   sendTime;		// 发送时间
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getLetterType() {
		return letterType;
	}
	public void setLetterType(String letterType) {
		this.letterType = letterType;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public Date getSendTime() {
		return sendTime;
	}
	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}
}