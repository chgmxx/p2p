package com.power.platform.current.entity.invest;

import java.util.Date;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.power.platform.common.persistence.DataEntity;
import com.power.platform.current.entity.WloanCurrentProject;
import com.power.platform.userinfo.entity.UserInfo;

/**
 * 活期项目投资Entity
 * @author Mr.Jia
 * @version 2016-01-14
 */
public class WloanCurrentProjectInvest extends DataEntity<WloanCurrentProjectInvest> {
	
	private static final long serialVersionUID = 1L;
	private String					projectId;				// 项目ID
	private String 					userid;					// 用户ID
	private Double 					amount;					// 金额
	private Date 					bidDate;				// 投资日期
	private Double 					vouvherAmount;			// 抵用券金额
	private String 					userInvest;				// 用户活期投资记录ID
	private String 					contractUrl;			// 投资合同路径
	
	private UserInfo 				userInfo;				// 用户信息
	private WloanCurrentProject 	currentProjectInfo;		// 项目信息
	
	public WloanCurrentProjectInvest() {
		super();
	}

	public WloanCurrentProjectInvest(String id){
		super(id);
	}

	@Length(min=0, max=32, message="项目ID长度必须介于 0 和 32 之间")
	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	
	@Length(min=0, max=32, message="用户ID长度必须介于 0 和 32 之间")
	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}
	
	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getBidDate() {
		return bidDate;
	}

	public void setBidDate(Date bidDate) {
		this.bidDate = bidDate;
	}
	
	public Double getVouvherAmount() {
		return vouvherAmount;
	}

	public void setVouvherAmount(Double vouvherAmount) {
		this.vouvherAmount = vouvherAmount;
	}
	
	@Length(min=0, max=255, message="用户活期投资记录ID长度必须介于 0 和 255 之间")
	public String getUserInvest() {
		return userInvest;
	}

	public void setUserInvest(String userInvest) {
		this.userInvest = userInvest;
	}

	public String getContractUrl() {
		return contractUrl;
	}

	public void setContractUrl(String contractUrl) {
		this.contractUrl = contractUrl;
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

	public WloanCurrentProject getCurrentProjectInfo() {
		return currentProjectInfo;
	}

	public void setCurrentProjectInfo(WloanCurrentProject currentProjectInfo) {
		this.currentProjectInfo = currentProjectInfo;
	}
	
}