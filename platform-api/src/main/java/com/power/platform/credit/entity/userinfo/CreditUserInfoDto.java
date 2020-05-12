package com.power.platform.credit.entity.userinfo;

import org.hibernate.validator.constraints.Length;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.power.platform.cgb.entity.CgbUserBankCard;
import com.power.platform.common.persistence.DataEntity;
import com.power.platform.credit.entity.annexfile.CreditAnnexFile;
import com.power.platform.regular.entity.WloanSubject;

/**
 * 信贷用户Entity
 * 
 * @author nice
 * @version 2017-03-22
 */
public class CreditUserInfoDto extends DataEntity<CreditUserInfoDto> {

	private static final long serialVersionUID = 1L;
	private String phone; // 手机号
	private String name; // 姓名
	private String pwd; // 密码
	private Date registerDate; // 注册时间
	private String state; // 状态
	/**
	 * 已销户.
	 */
	public static final String CREDIT_USER_DELETED = "0";
	/**
	 * 正常.
	 */
	public static final String CREDIT_USER_NORMAL = "1";
	private String accountId; // 账户ID
	private String certificateNo; // 身份证号
	private String creditUserType; // 账户类型
	private String enterpriseFullName;// 企业全称
	private String briefName; // 名称简介
	private String businessNo; // 证照编号
	private String agentPersonName; // 联系人姓名.
	private String agentPersonPhone; // 联系人手机号.
	private String agentPersonCertType; // 联系人证件类型，IDC-身份证，GAT-港澳台身份证，MILIARY-军官证，PASS_PORT-护照.
	private String agentPersonCertNo; // 联系人证件号.
	private String email; // 邮箱.
	public CreditUserInfoDto() {

		super();
	}

	public CreditUserInfoDto(String id) {

		super(id);
	}

	@Length(min = 1, max = 64, message = "手机号长度必须介于 1 和 64 之间")
	public String getPhone() {

		return phone;
	}

	public void setPhone(String phone) {

		this.phone = phone;
	}

	@Length(min = 0, max = 64, message = "姓名长度必须介于 0 和 64 之间")
	public String getName() {

		return name;
	}

	public void setName(String name) {

		this.name = name;
	}

	@Length(min = 0, max = 64, message = "密码长度必须介于 0 和 64 之间")
	public String getPwd() {

		return pwd;
	}

	public void setPwd(String pwd) {

		this.pwd = pwd;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getRegisterDate() {

		return registerDate;
	}

	public void setRegisterDate(Date registerDate) {

		this.registerDate = registerDate;
	}

	@Length(min = 0, max = 2, message = "状态长度必须介于 0 和 2 之间")
	public String getState() {

		return state;
	}

	public void setState(String state) {

		this.state = state;
	}


	public String getAccountId() {

		return accountId;
	}

	public void setAccountId(String accountId) {

		this.accountId = accountId;
	}

	public String getCertificateNo() {

		return certificateNo;
	}

	public void setCertificateNo(String certificateNo) {

		this.certificateNo = certificateNo;
	}

	public String getCreditUserType() {

		return creditUserType;
	}

	public void setCreditUserType(String creditUserType) {

		this.creditUserType = creditUserType;
	}

	public String getEnterpriseFullName() {

		return enterpriseFullName;
	}

	public void setEnterpriseFullName(String enterpriseFullName) {

		this.enterpriseFullName = enterpriseFullName;
	}

	public static long getSerialversionuid() {

		return serialVersionUID;
	}

	public String getBriefName() {
		return briefName;
	}

	public void setBriefName(String briefName) {
		this.briefName = briefName;
	}

	public String getBusinessNo() {
		return businessNo;
	}

	public void setBusinessNo(String businessNo) {
		this.businessNo = businessNo;
	}

	public String getAgentPersonName() {
		return agentPersonName;
	}

	public void setAgentPersonName(String agentPersonName) {
		this.agentPersonName = agentPersonName;
	}

	public String getAgentPersonPhone() {
		return agentPersonPhone;
	}

	public void setAgentPersonPhone(String agentPersonPhone) {
		this.agentPersonPhone = agentPersonPhone;
	}

	public String getAgentPersonCertType() {
		return agentPersonCertType;
	}

	public void setAgentPersonCertType(String agentPersonCertType) {
		this.agentPersonCertType = agentPersonCertType;
	}

	public String getAgentPersonCertNo() {
		return agentPersonCertNo;
	}

	public void setAgentPersonCertNo(String agentPersonCertNo) {
		this.agentPersonCertNo = agentPersonCertNo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
}