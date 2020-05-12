package com.power.platform.current.entity.moment;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;
import com.power.platform.userinfo.entity.UserInfo;

/**
 * 投资用户剩余资金信息(投资中间拆分表)Entity
 * @author Mr.Jia
 * @version 2016-01-14
 */
public class WloanCurrentMomentInvest extends DataEntity<WloanCurrentMomentInvest> {
	
	private static final long serialVersionUID = 1L;
	private String 		userid;					// 用户ID
	private Double 		amount;					// 金额
	private String 		state;					// 状态
	private Double 		voucherAmount;			// 抵用券金额
	private String 		userInvest;				// 用户投资记录ID
	
	private UserInfo 	userInfo;				//用户信息
	
	
	public WloanCurrentMomentInvest() {
		super();
	}

	public WloanCurrentMomentInvest(String id){
		super(id);
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
	
	@Length(min=0, max=2, message="状态长度必须介于 0 和 2 之间")
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	public Double getVoucherAmount() {
		return voucherAmount;
	}

	public void setVoucherAmount(Double voucherAmount) {
		this.voucherAmount = voucherAmount;
	}
	
	@Length(min=0, max=32, message="用户投资记录ID长度必须介于 0 和 32 之间")
	public String getUserInvest() {
		return userInvest;
	}

	public void setUserInvest(String userInvest) {
		this.userInvest = userInvest;
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}
	
}