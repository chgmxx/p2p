package com.power.platform.pay.vo;

import java.io.Serializable;

/**
 * 大额行号查询参数实体
 * 
 * @author wangjingsong
 * 
 */
public class PrcptcdInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String oid_partner; // 商户编号
	
	private String sign_type; // 签名方式
	
	private String sign; // 签名
	
	private String bank_code;// 银行编码
	
	private String card_no;// 银行账号
	
	private String brabank_name;// 开户支行名称
	
	private String city_code;// 开户行所在市编码

	public String getOid_partner() {
		return oid_partner;
	}

	public void setOid_partner(String oid_partner) {
		this.oid_partner = oid_partner;
	}

	public String getSign_type() {
		return sign_type;
	}

	public void setSign_type(String sign_type) {
		this.sign_type = sign_type;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getBank_code() {
		return bank_code;
	}

	public void setBank_code(String bank_code) {
		this.bank_code = bank_code;
	}

	public String getCard_no() {
		return card_no;
	}

	public void setCard_no(String card_no) {
		this.card_no = card_no;
	}

	public String getBrabank_name() {
		return brabank_name;
	}

	public void setBrabank_name(String brabank_name) {
		this.brabank_name = brabank_name;
	}

	public String getCity_code() {
		return city_code;
	}

	public void setCity_code(String city_code) {
		this.city_code = city_code;
	}

}
