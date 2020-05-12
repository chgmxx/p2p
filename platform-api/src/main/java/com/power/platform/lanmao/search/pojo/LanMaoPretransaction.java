package com.power.platform.lanmao.search.pojo;

import java.util.Date;

public class LanMaoPretransaction {
	/**
	 * 交易预处理明细
	 */
	private String bizType; // 见【预处理业务类型】
	private String platformUserNo; // 平台用户编号
	private String freezeAmount; // 预处理冻结金额
	private String unfreezeAmount; // 累计已解冻金额
	private String cancelAmount; // 已取消金额
	private String status; // INIT 表示初始化,FREEZED 表示冻结成功, UNFREEZED 表示全部解冻,FAIL 表示 冻结失败,ERROR 表示异常。
	private String createTime; // 交易发起时间
	private String transactionTime; // 交易完成时间,预处理冻结金额全部确认的时间
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getTransactionTime() {
		return transactionTime;
	}
	public void setTransactionTime(String transactionTime) {
		this.transactionTime = transactionTime;
	}
	public String getBizType() {
		return bizType;
	}
	public void setBizType(String bizType) {
		this.bizType = bizType;
	}
	public String getPlatformUserNo() {
		return platformUserNo;
	}
	public void setPlatformUserNo(String platformUserNo) {
		this.platformUserNo = platformUserNo;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public String getFreezeAmount() {
		return freezeAmount;
	}
	public void setFreezeAmount(String freezeAmount) {
		this.freezeAmount = freezeAmount;
	}
	public String getUnfreezeAmount() {
		return unfreezeAmount;
	}
	public void setUnfreezeAmount(String unfreezeAmount) {
		this.unfreezeAmount = unfreezeAmount;
	}
	public String getCancelAmount() {
		return cancelAmount;
	}
	public void setCancelAmount(String cancelAmount) {
		this.cancelAmount = cancelAmount;
	}
	
}
