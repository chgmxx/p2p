package com.power.platform.pay.vo;

import java.util.Date;

/**
 * 网银支付VO类
 * @author 曹智
 * 2015-12-23 14:06:19 PM
 */
public class GateWayPayBean {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private String userId;//用户id
    private String orderId;//订单号
    private String amount;//金额
    private String ip;//访问ip
    private Date registerDate;//注册时间
    private String bankCode;//银行编码
    
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public Date getRegisterDate() {
		return registerDate;
	}
	public void setRegisterDate(Date registerDate) {
		this.registerDate = registerDate;
	}
	public String getBankCode() {
		return bankCode;
	}
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
   
    
}
