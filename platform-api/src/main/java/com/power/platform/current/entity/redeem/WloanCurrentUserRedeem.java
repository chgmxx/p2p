package com.power.platform.current.entity.redeem;

import java.util.Date;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.power.platform.common.persistence.DataEntity;
import com.power.platform.current.entity.WloanCurrentProject;
import com.power.platform.userinfo.entity.UserInfo;


/**
 * 活期赎回Entity
 * @author yb
 * @version 2016-01-13
 */
public class WloanCurrentUserRedeem extends DataEntity<WloanCurrentUserRedeem> {
	
	private static final long serialVersionUID = 1L;
	private String projectId;		// 项目ID
	private String toUserId;		// 转到用户ID
	private String redeemUserId;		// 转让用户ID
	private Double amount;		// 转让金额
	private Date redeemDate;		// 转让日期
	private String redeemContractUrl;		// 转让合同路径
	private Date beginRedeemDate;		// 开始 转让日期
	private Date endRedeemDate;		// 结束 转让日期
	private Integer state;       // 状态
	
	private WloanCurrentProject wloanCurrentProject;//活期项目
	private UserInfo userInfo;//转到用户信息
	private UserInfo userInfo1;//转让用户信息
	
	public WloanCurrentUserRedeem() {
		super();
	}

	public WloanCurrentUserRedeem(String id){
		super(id);
	}

	@Length(min=1, max=32, message="项目ID长度必须介于 1 和 32 之间")
	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	
	@Length(min=1, max=32, message="转到用户ID长度必须介于 1 和 32 之间")
	public String getToUserId() {
		return toUserId;
	}

	public void setToUserId(String toUserId) {
		this.toUserId = toUserId;
	}
	
	@Length(min=1, max=32, message="转让用户ID长度必须介于 1 和 32 之间")
	public String getRedeemUserId() {
		return redeemUserId;
	}

	public void setRedeemUserId(String redeemUserId) {
		this.redeemUserId = redeemUserId;
	}
	
	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getRedeemDate() {
		return redeemDate;
	}

	public void setRedeemDate(Date redeemDate) {
		this.redeemDate = redeemDate;
	}
	
	@Length(min=0, max=255, message="转让合同路径长度必须介于 0 和 255 之间")
	public String getRedeemContractUrl() {
		return redeemContractUrl;
	}

	public void setRedeemContractUrl(String redeemContractUrl) {
		this.redeemContractUrl = redeemContractUrl;
	}
	
	public Date getBeginRedeemDate() {
		return beginRedeemDate;
	}

	public void setBeginRedeemDate(Date beginRedeemDate) {
		this.beginRedeemDate = beginRedeemDate;
	}
	
	public Date getEndRedeemDate() {
		return endRedeemDate;
	}

	public void setEndRedeemDate(Date endRedeemDate) {
		this.endRedeemDate = endRedeemDate;
	}
	
	

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public WloanCurrentProject getWloanCurrentProject() {
		return wloanCurrentProject;
	}

	public void setWloanCurrentProject(WloanCurrentProject wloanCurrentProject) {
		this.wloanCurrentProject = wloanCurrentProject;
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

	public UserInfo getUserInfo1() {
		return userInfo1;
	}

	public void setUserInfo1(UserInfo userInfo1) {
		this.userInfo1 = userInfo1;
	}
	
	
}