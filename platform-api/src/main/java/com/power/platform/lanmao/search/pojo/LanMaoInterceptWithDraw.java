package com.power.platform.lanmao.search.pojo;

import java.util.Date;

public class LanMaoInterceptWithDraw {
	/**
	 * 提现拦截明细
	 */
	private String requestNo; // 请求流水号
	private String withdrawRequestNo; // 提现请求流水号
	private String creatTime; // 发起时间
	private String completedTime; // 完成时间
	private String status; // 见【提现拦截状态】
	
	
	public String getCreatTime() {
		return creatTime;
	}
	public void setCreatTime(String creatTime) {
		this.creatTime = creatTime;
	}
	public String getCompletedTime() {
		return completedTime;
	}
	public void setCompletedTime(String completedTime) {
		this.completedTime = completedTime;
	}
	public String getRequestNo() {
		return requestNo;
	}
	public void setRequestNo(String requestNo) {
		this.requestNo = requestNo;
	}
	public String getWithdrawRequestNo() {
		return withdrawRequestNo;
	}
	public void setWithdrawRequestNo(String withdrawRequestNo) {
		this.withdrawRequestNo = withdrawRequestNo;
	}

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	
	
	
}
