package com.power.platform.weixin.entity;


import com.power.platform.common.persistence.DataEntity;

public class AccountErWeiMa extends DataEntity<AccountErWeiMa>{

	private String channelCode;			//渠道编号
	private String channelName;			//渠道名称
	private String fileUrl;				//生成二维码地址
	private String fileName;   
	private String ticket;
	
	public String getTicket() {
		return ticket;
	}
	public void setTicket(String ticket) {
		this.ticket = ticket;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getChannelCode() {
		return channelCode;
	}
	public void setChannelCode(String channelCode) { 
		this.channelCode = channelCode;
	}
	public String getChannelName() {
		return channelName;
	}
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}
	public String getFileUrl() {
		return fileUrl;
	}
	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}
	
	
	
	
	
	
	
}
