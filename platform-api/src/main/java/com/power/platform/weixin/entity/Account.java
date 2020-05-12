package com.power.platform.weixin.entity;

import com.power.platform.common.persistence.DataEntity;


/**
 * 微信公众账号
 */
public class Account extends DataEntity<Account>{

	private String name;//名称
	private String account;//账号
	private String appid;//appid
	private String appsecret;//appsecret
	private String url;//验证时用的url
	private String token;//token
	
	//ext
	private Integer msgcount;//自动回复消息条数;默认是5条
	
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public String getAppsecret() {
		return appsecret;
	}
	public void setAppsecret(String appsecret) {
		this.appsecret = appsecret;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public Integer getMsgcount() {
		return msgcount;
	}
	public void setMsgcount(Integer msgcount) {
		this.msgcount = msgcount;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}