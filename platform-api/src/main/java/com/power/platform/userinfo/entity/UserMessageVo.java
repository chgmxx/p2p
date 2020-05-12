package com.power.platform.userinfo.entity;

import java.util.Date;

public class UserMessageVo {
	private String id;

    private String receiverId;

    private String receiverName;

    private String senderId;

    private String senderName;

    private Integer senderType;

    private String title;

    private String body;

    private Integer state;

    private String sendTime;
    
    private String stateClass;
    
    private String opClass;

	 
    

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(String receiverId) {
		this.receiverId = receiverId;
	}

	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}
	 
	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public Integer getSenderType() {
		return senderType;
	}

	public void setSenderType(Integer senderType) {
		this.senderType = senderType;
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

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	

	public String getSendTime() {
		return sendTime;
	}

	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}

	public String getStateClass() {
		return stateClass;
	}

	public void setStateClass(String stateClass) {
		this.stateClass = stateClass;
	}

	public String getOpClass() {
		return opClass;
	}

	public void setOpClass(String opClass) {
		this.opClass = opClass;
	}
    
    

}
