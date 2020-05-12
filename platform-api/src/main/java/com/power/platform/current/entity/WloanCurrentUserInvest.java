package com.power.platform.current.entity;

import org.hibernate.validator.constraints.Length;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.power.platform.common.persistence.DataEntity;
import com.power.platform.userinfo.entity.UserInfo;

/**
 * 
 * 类: WloanCurrentUserInvest <br>
 * 描述: 活期客户投资Entity. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年1月13日 下午2:55:48
 */
public class WloanCurrentUserInvest extends DataEntity<WloanCurrentUserInvest> {

	private static final long serialVersionUID = 1L;
	private WloanCurrentPool wloanCurrentPool; // 活期资金池.
	private UserInfo userInfo; // 客户信息.
	private Double amount; // 投资金额.
	private Double onLineAmount; // 在投金额，用于计算赎回和计息.
	private Date bidDate; // 投资日期
	private String ip; // 投资IP
	private String state; // 状态
	private String bidState; // 投资状态
	private Double voucherAmount; // 抵用券金额
	private String contractPdfPath; // 投资合同存储路径.
	private Date beginBidDate; // 开始 投资日期
	private Date endBidDate; // 结束 投资日期

	public WloanCurrentUserInvest() {

		super();
	}

	public WloanCurrentUserInvest(String id) {

		super(id);
	}

	public WloanCurrentPool getWloanCurrentPool() {

		return wloanCurrentPool;
	}

	public void setWloanCurrentPool(WloanCurrentPool wloanCurrentPool) {

		this.wloanCurrentPool = wloanCurrentPool;
	}

	public UserInfo getUserInfo() {

		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {

		this.userInfo = userInfo;
	}

	public Double getAmount() {

		return amount;
	}

	public void setAmount(Double amount) {

		this.amount = amount;
	}

	public Double getOnLineAmount() {

		return onLineAmount;
	}

	public void setOnLineAmount(Double onLineAmount) {

		this.onLineAmount = onLineAmount;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getBidDate() {

		return bidDate;
	}

	public void setBidDate(Date bidDate) {

		this.bidDate = bidDate;
	}

	@Length(min = 0, max = 64, message = "投资IP长度必须介于 0 和 64 之间")
	public String getIp() {

		return ip;
	}

	public void setIp(String ip) {

		this.ip = ip;
	}

	@Length(min = 0, max = 1, message = "状态长度必须介于 0 和 1 之间")
	public String getState() {

		return state;
	}

	public void setState(String state) {

		this.state = state;
	}

	@Length(min = 0, max = 1, message = "投资状态长度必须介于 0 和 1 之间")
	public String getBidState() {

		return bidState;
	}

	public void setBidState(String bidState) {

		this.bidState = bidState;
	}

	public Double getVoucherAmount() {

		return voucherAmount;
	}

	public void setVoucherAmount(Double voucherAmount) {

		this.voucherAmount = voucherAmount;
	}

	@Length(min = 0, max = 64, message = "投资IP长度必须介于 0 和 500 之间")
	public String getContractPdfPath() {

		return contractPdfPath;
	}

	public void setContractPdfPath(String contractPdfPath) {

		this.contractPdfPath = contractPdfPath;
	}

	public Date getBeginBidDate() {

		return beginBidDate;
	}

	public void setBeginBidDate(Date beginBidDate) {

		this.beginBidDate = beginBidDate;
	}

	public Date getEndBidDate() {

		return endBidDate;
	}

	public void setEndBidDate(Date endBidDate) {

		this.endBidDate = endBidDate;
	}

}