package com.power.platform.weixin.entity;

import com.power.platform.common.persistence.DataEntity;

/**
 * 图文消息
 * 
 *
 */
public class MsgNews extends DataEntity<MsgNews>{

	private String title;//标题
	private String author;//作者
	private String brief;//简介
	private String description;//描述
	private String picpath;//封面图片
	private String picdir;//封面图片绝对目录
	private Integer showpic;//是否显示图片
	private String url;//图文消息原文链接
	private String fromurl;//外部链接
	private MsgBase msgBase;
	
	//以下属性为非数据库字段
	private String createTimeStr;
	
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getBrief() {
		return brief;
	}
	public void setBrief(String brief) {
		this.brief = brief;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getPicpath() {
		return picpath;
	}
	public void setPicpath(String picpath) {
		this.picpath = picpath;
	}
	
	public String getPicdir() {
		return picdir;
	}
	public void setPicdir(String picdir) {
		this.picdir = picdir;
	}
	public Integer getShowpic() {
		return showpic;
	}
	public void setShowpic(Integer showpic) {
		this.showpic = showpic;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getFromurl() {
		return fromurl;
	}
	public void setFromurl(String fromurl) {
		this.fromurl = fromurl;
	}
	 
	public String getCreateTimeStr() {
		return createTimeStr;
	}
	public void setCreateTimeStr(String createTimeStr) {
		this.createTimeStr = createTimeStr;
	}
	public MsgBase getMsgBase() {
		return msgBase;
	}
	public void setMsgBase(MsgBase msgBase) {
		this.msgBase = msgBase;
	}
	
}