package com.power.platform.lanmao.search.pojo;

import java.util.Date;

public class LanMaoRemit {
	/**
	 * 网银转账充值代付查询-交易记录明细
	 */
	private String remitRequestNo; // 打款流水号
	private String payeeAccount; // 收款方账号（加密）
	private String payeeName; // 收款方账户名
	private String amount; // 代付金额
	private String status; // 打款状态（打款中REMITING；打款订单已生成INIT；打款成功SUCCESS；打款失败FAIL）
	private String transactionTime ; //代付完成时间	【打款订单里的完成时间】
	public String getRemitRequestNo() {
		return remitRequestNo;
	}
	public void setRemitRequestNo(String remitRequestNo) {
		this.remitRequestNo = remitRequestNo;
	}
	public String getPayeeAccount() {
		return payeeAccount;
	}
	public void setPayeeAccount(String payeeAccount) {
		this.payeeAccount = payeeAccount;
	}
	public String getPayeeName() {
		return payeeName;
	}
	public void setPayeeName(String payeeName) {
		this.payeeName = payeeName;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTransactionTime() {
		return transactionTime;
	}
	public void setTransactionTime(String transactionTime) {
		this.transactionTime = transactionTime;
	}
	
}
