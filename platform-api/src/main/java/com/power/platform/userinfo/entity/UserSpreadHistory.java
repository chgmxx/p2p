/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.userinfo.entity;

import com.power.platform.common.persistence.DataEntity;

import org.hibernate.validator.constraints.Length;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;


/**
 * spreadEntity
 * @author yb
 * @version 2015-12-18
 */
public class UserSpreadHistory extends DataEntity<UserSpreadHistory> {
	
	private static final long serialVersionUID = 1L;
	private UserInfo user;		// 用户ID
	private String recomType;		// 推荐类型
	private Date createTime;		// 创建时间
	private String opernname;		// opernname
	private Date beginCreateTime;		// 开始 创建时间
	private Date endCreateTime;		// 结束 创建时间
	
	public UserSpreadHistory() {
		super();
	}

	public UserSpreadHistory(String id){
		super(id);
	}

	public UserInfo getUser() {
		return user;
	}

	public void setUser(UserInfo user) {
		this.user = user;
	}
	
	@Length(min=0, max=11, message="推荐类型长度必须介于 0 和 11 之间")
	public String getRecomType() {
		return recomType;
	}

	public void setRecomType(String recomType) {
		this.recomType = recomType;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	@Length(min=0, max=255, message="opernname长度必须介于 0 和 255 之间")
	public String getOpernname() {
		return opernname;
	}

	public void setOpernname(String opernname) {
		this.opernname = opernname;
	}
	
	public Date getBeginCreateTime() {
		return beginCreateTime;
	}

	public void setBeginCreateTime(Date beginCreateTime) {
		this.beginCreateTime = beginCreateTime;
	}
	
	public Date getEndCreateTime() {
		return endCreateTime;
	}

	public void setEndCreateTime(Date endCreateTime) {
		this.endCreateTime = endCreateTime;
	}
		
}