package com.power.platform.cms.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.power.platform.common.persistence.DataEntity;
import com.power.platform.common.utils.IdGen;
import com.power.platform.sys.entity.User;
/**
 * 公告Entity
 * @author lc
 *
 */
public class Notice extends DataEntity<Notice>{

	private static final long serialVersionUID = 1L;
	private String title;
	private String text;
	private Integer state;
	private String head;
	private String sources;
	private Date sourcesDate;
	private String logopath;
	private Integer type;
	private User user;		
	private Integer orderSum;
	private String from;
	private String bannerType;

	
	public Notice(){
	}
	public Notice(String id){
		this.id = id;
	}
	public Notice(Integer type){
		this.type = type;
	}
	
	
	public void prePersist(){
		this.id = IdGen.uuid();
		this.createDate = new Date();
	}
	 
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
	public String getHead() {
		return head;
	}
	public void setHead(String head) {
		this.head = head;
	}
	public String getSources() {
		return sources;
	}
	public void setSources(String sources) {
		this.sources = sources;
	}
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	public Date getSourcesDate() {
		return sourcesDate;
	}
	public void setSourcesDate(Date sourcesDate) {
		this.sourcesDate = sourcesDate;
	}
	public String getLogopath() {
		return logopath;
	}
	public void setLogopath(String logopath) {
		this.logopath = logopath;
	}
	public Integer getOrderSum() {
		return orderSum;
	}
	public void setOrderSum(Integer orderSum) {
		this.orderSum = orderSum;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getBannerType() {
		return bannerType;
	}
	public void setBannerType(String bannerType) {
		this.bannerType = bannerType;
	}
	
}
