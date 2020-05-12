package com.power.platform.lanmao.rw.pojo;

import java.io.Serializable;

/**
 * 提现业务实体
 */
public class WithdrawVo extends  BaseGatewayVo  implements Serializable{

    @Override
	public String toString() {
		return "WithdrawVo [platformUserNo=" + platformUserNo + ", requestNo=" + requestNo + ", expired=" + expired
				+ ", redirectUrl=" + redirectUrl + ", withdrawType=" + withdrawType + ", withdrawForm=" + withdrawForm
				+ ", amount=" + amount + ", commission=" + commission + "]";
	}

	private static final long serialVersionUID = 1L;

    private String platformUserNo;
    private String requestNo;
    private String expired;
    private String redirectUrl;
    private String withdrawType;
    private String withdrawForm;
    private Double amount;
    private Double commission;

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public String getPlatformUserNo() {
        return platformUserNo;
    }

    public void setPlatformUserNo(String platformUserNo) {
        this.platformUserNo = platformUserNo;
    }

    public String getRequestNo() {
        return requestNo;
    }

    public void setRequestNo(String requestNo) {
        this.requestNo = requestNo;
    }

    public String getExpired() {
        return expired;
    }

    public void setExpired(String expired) {
        this.expired = expired;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getWithdrawType() {
        return withdrawType;
    }

    public void setWithdrawType(String withdrawType) {
        this.withdrawType = withdrawType;
    }

    public String getWithdrawForm() {
        return withdrawForm;
    }

    public void setWithdrawForm(String withdrawForm) {
        this.withdrawForm = withdrawForm;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getCommission() {
        return commission;
    }

    public void setCommission(Double commission) {
        this.commission = commission;
    }

    
    
    
}