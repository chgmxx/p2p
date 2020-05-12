package com.power.platform.wexperience.entity;

import org.hibernate.validator.constraints.Length;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.power.platform.common.persistence.DataEntity;
import com.power.platform.userinfo.entity.UserInfo;


/**
 * 体验金信息Entity
 * @author Mr.Jia
 * @version 2016-01-25
 */
public class WexperienceMoney extends DataEntity<WexperienceMoney> {
	
	private static final long serialVersionUID = 1L;
	private String 	userId;				// 用户id
	private Double 	amount;				// 体验金额
	private Double 	inverst;			// 利息
	private Date 	bidTime;			// 投资日期
	private Date 	endTime;			// 到期时间
	private String 	state;				// 状态 1可用、2已经使用、3已经到期
	private String 	comeForm;			// come_form
	private String 	type;				// 投资类型 1定期 2活期
	private String 	projectId;			// 投资项目
	
	private UserInfo userInfo;			// 用户信息
	private Date	 beginCreatDate;	// 用于查询开始时间
	private Date	 endCreatDate;		// 用于查询结束时间
	
	public WexperienceMoney() {
		super();
	}

	public WexperienceMoney(String id){
		super(id);
	}

	@Length(min=0, max=64, message="用户id长度必须介于 0 和 64 之间")
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}
	
	public Double getInverst() {
		return inverst;
	}

	public void setInverst(Double inverst) {
		this.inverst = inverst;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getBidTime() {
		return bidTime;
	}

	public void setBidTime(Date bidTime) {
		this.bidTime = bidTime;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	
	@Length(min=0, max=1, message="状态 1可用、2已经使用、3已经到期长度必须介于 0 和 1 之间")
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	@Length(min=0, max=1, message="come_form长度必须介于 0 和 1 之间")
	public String getComeForm() {
		return comeForm;
	}

	public void setComeForm(String comeForm) {
		this.comeForm = comeForm;
	}
	
	@Length(min=0, max=1, message="投资类型 1定期 2活期长度必须介于 0 和 1 之间")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	@Length(min=0, max=64, message="投资项目长度必须介于 0 和 64 之间")
	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

	public Date getBeginCreatDate() {
		return beginCreatDate;
	}

	public void setBeginCreatDate(Date beginCreatDate) {
		this.beginCreatDate = beginCreatDate;
	}

	public Date getEndCreatDate() {
		return endCreatDate;
	}

	public void setEndCreatDate(Date endCreatDate) {
		this.endCreatDate = endCreatDate;
	}
	
}