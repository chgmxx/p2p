package com.power.platform.lanmao.rw.pojo;

import java.io.Serializable;

/**
 * 充值返回实体
 */
public class RechargeResultVo implements Serializable{
    private static final long serialVersionUID = 1L;
    private String requestNo;
    private String code;
    private String status;
    private String errorCode;
    private String errorMessage;
    private String rechargeStatus;
    private String platformUserNo;
    private String amount;
    private String commission;
    private String payCompany;
    private String rechargeWay;
    private String bankcode;
    private String payMobile;
    private String transactionTime;
    private String swiftRoute;
    private String channelErrorCode;
    private String channelErrorMessage;

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public String getRequestNo() {
        return requestNo;
    }

    public void setRequestNo(String requestNo) {
        this.requestNo = requestNo;
    }

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

    public String getRechargeStatus() {
        return rechargeStatus;
    }

    public void setRechargeStatus(String rechargeStatus) {
        this.rechargeStatus = rechargeStatus;
    }

    public String getPlatformUserNo() {
        return platformUserNo;
    }

    public void setPlatformUserNo(String platformUserNo) {
        this.platformUserNo = platformUserNo;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCommission() {
        return commission;
    }

    public void setCommission(String commission) {
        this.commission = commission;
    }

    public String getPayCompany() {
        return payCompany;
    }

    public void setPayCompany(String payCompany) {
        this.payCompany = payCompany;
    }

    public String getRechargeWay() {
        return rechargeWay;
    }

    public void setRechargeWay(String rechargeWay) {
        this.rechargeWay = rechargeWay;
    }

    public String getBankcode() {
        return bankcode;
    }

    public void setBankcode(String bankcode) {
        this.bankcode = bankcode;
    }

    public String getPayMobile() {
        return payMobile;
    }

    public void setPayMobile(String payMobile) {
        this.payMobile = payMobile;
    }

    public String getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(String transactionTime) {
        this.transactionTime = transactionTime;
    }

    public String getSwiftRoute() {
        return swiftRoute;
    }

    public void setSwiftRoute(String swiftRoute) {
        this.swiftRoute = swiftRoute;
    }

    public String getChannelErrorCode() {
        return channelErrorCode;
    }

    public void setChannelErrorCode(String channelErrorCode) {
        this.channelErrorCode = channelErrorCode;
    }

    public String getChannelErrorMessage() {
        return channelErrorMessage;
    }

    public void setChannelErrorMessage(String channelErrorMessage) {
        this.channelErrorMessage = channelErrorMessage;
    }

    


}