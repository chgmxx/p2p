/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.userinfo.entity;

import java.util.Date;

import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.power.platform.common.persistence.DataEntity;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.utils.excel.annotation.ExcelField;

/**
 * 日志Entity
 * @author ThinkGem
 * @version 2014-8-19
 */
public class UserLog extends DataEntity<UserLog> {

	private static final long serialVersionUID = 1L;
	private String userId; 		// 用户 ID
	private String userName;    //用户名称
	private String type; 		// 类型（1：用户登录；2：开户 3、充值（转账）4、网银 5、提现）
	private String remark;		// 备注
	private Date beginDate;		// 开始日期
	private Date endDate;		// 结束日期
	
	
	public UserLog(){
		super();
	}
	
	public UserLog(String id){
		super(id);
	}
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	@ExcelField(title = "手机号码", align = 2, sort = 1)
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	@ExcelField(title = "类型", align = 2, sort = 2)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	@ExcelField(title = "简介", align = 2, sort = 3)
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	@ExcelField(title = "时间", align = 2, sort = 4)
	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	
	 
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}