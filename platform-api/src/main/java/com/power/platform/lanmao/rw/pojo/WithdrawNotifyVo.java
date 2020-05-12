package com.power.platform.lanmao.rw.pojo;

import java.io.Serializable;

/**
 *   提现  回调数据
 * @author chenhj ant-loiter.com
 *
 */
public class WithdrawNotifyVo  implements Serializable{

    private static final long serialVersionUID = 1L;
    
    private String code;
    private String status;
    private String errorCode;
    private String errorMessage;
    private String withdrawStatus;
    private String requestNo;
    private String platformUserNo;
    private String commission;
    private String amount;
    private String withdrawWay;
    private String withdrawForm;
    private String bankcardNo;
    private String bankcode;
    private String createTime;
    private String transactionTime;
    private String remitType;
    private String floatAmount;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getWithdrawStatus() {
		return withdrawStatus;
	}
	public void setWithdrawStatus(String withdrawStatus) {
		this.withdrawStatus = withdrawStatus;
	}
	public String getRequestNo() {
		return requestNo;
	}
	public void setRequestNo(String requestNo) {
		this.requestNo = requestNo;
	}
	public String getPlatformUserNo() {
		return platformUserNo;
	}
	public void setPlatformUserNo(String platformUserNo) {
		this.platformUserNo = platformUserNo;
	}
	public String getCommission() {
		return commission;
	}
	public void setCommission(String commission) {
		this.commission = commission;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getWithdrawWay() {
		return withdrawWay;
	}
	public void setWithdrawWay(String withdrawWay) {
		this.withdrawWay = withdrawWay;
	}
	public String getWithdrawForm() {
		return withdrawForm;
	}
	public void setWithdrawForm(String withdrawForm) {
		this.withdrawForm = withdrawForm;
	}
	public String getBankcardNo() {
		return bankcardNo;
	}
	public void setBankcardNo(String bankcardNo) {
		this.bankcardNo = bankcardNo;
	}
	public String getBankcode() {
		return bankcode;
	}
	public void setBankcode(String bankcode) {
		this.bankcode = bankcode;
	}
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
	public String getRemitType() {
		return remitType;
	}
	public void setRemitType(String remitType) {
		this.remitType = remitType;
	}
	public String getFloatAmount() {
		return floatAmount;
	}
	public void setFloatAmount(String floatAmount) {
		this.floatAmount = floatAmount;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
    

}
