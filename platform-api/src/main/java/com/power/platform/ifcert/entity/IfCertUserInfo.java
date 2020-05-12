/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.ifcert.entity;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;

/**
 * 国家应急中心用户信息Entity
 * 
 * @author Roy
 * @version 2019-05-07
 */
public class IfCertUserInfo extends DataEntity<IfCertUserInfo> {

	private static final long serialVersionUID = 1L;
	private String version; // version
	private String sourceCode; // sourcecode
	private String userType; // usertype
	private String userAttr; // userattr
	private String userCreateTime; // usercreatetime
	private String userName; // username
	private String countries; // countries
	private String cardType; // cardtype
	private String userIdcard; // useridcard
	private String userIdcardHash; // useridcardhash
	private String userPhone; // userphone
	private String userPhoneHash; // userphonehash
	private String userUuid; // useruuid
	private String userLawperson; // userlawperson
	private String userFund; // userfund
	private String userProvince; // userprovince
	private String userAddress; // useraddress
	private String registerDate; // registerdate
	private String userSex; // usersex
	private String userBankAccount; // userbankaccount
	private String batchNum; // batchnum
	private String sendTime; // sendtime

	public IfCertUserInfo() {

		super();
	}

	public IfCertUserInfo(String id) {

		super(id);
	}

	@Length(min = 0, max = 20, message = "version长度必须介于 0 和 20 之间")
	public String getVersion() {

		return version;
	}

	public void setVersion(String version) {

		this.version = version;
	}

	@Length(min = 0, max = 64, message = "sourcecode长度必须介于 0 和 64 之间")
	public String getSourceCode() {

		return sourceCode;
	}

	public void setSourceCode(String sourceCode) {

		this.sourceCode = sourceCode;
	}

	@Length(min = 0, max = 20, message = "usertype长度必须介于 0 和 20 之间")
	public String getUserType() {

		return userType;
	}

	public void setUserType(String userType) {

		this.userType = userType;
	}

	@Length(min = 0, max = 20, message = "userattr长度必须介于 0 和 20 之间")
	public String getUserAttr() {

		return userAttr;
	}

	public void setUserAttr(String userAttr) {

		this.userAttr = userAttr;
	}

	@Length(min = 0, max = 40, message = "usercreatetime长度必须介于 0 和 40 之间")
	public String getUserCreateTime() {

		return userCreateTime;
	}

	public void setUserCreateTime(String userCreateTime) {

		this.userCreateTime = userCreateTime;
	}

	@Length(min = 0, max = 256, message = "username长度必须介于 0 和 256 之间")
	public String getUserName() {

		return userName;
	}

	public void setUserName(String userName) {

		this.userName = userName;
	}

	@Length(min = 0, max = 3, message = "countries长度必须介于 0 和 3 之间")
	public String getCountries() {

		return countries;
	}

	public void setCountries(String countries) {

		this.countries = countries;
	}

	@Length(min = 0, max = 2, message = "cardtype长度必须介于 0 和 2 之间")
	public String getCardType() {

		return cardType;
	}

	public void setCardType(String cardType) {

		this.cardType = cardType;
	}

	@Length(min = 0, max = 64, message = "useridcard长度必须介于 0 和 64 之间")
	public String getUserIdcard() {

		return userIdcard;
	}

	public void setUserIdcard(String userIdcard) {

		this.userIdcard = userIdcard;
	}

	@Length(min = 0, max = 64, message = "useridcardhash长度必须介于 0 和 64 之间")
	public String getUserIdcardHash() {

		return userIdcardHash;
	}

	public void setUserIdcardHash(String userIdcardHash) {

		this.userIdcardHash = userIdcardHash;
	}

	@Length(min = 0, max = 64, message = "userphone长度必须介于 0 和 64 之间")
	public String getUserPhone() {

		return userPhone;
	}

	public void setUserPhone(String userPhone) {

		this.userPhone = userPhone;
	}

	@Length(min = 0, max = 64, message = "userphonehash长度必须介于 0 和 64 之间")
	public String getUserPhoneHash() {

		return userPhoneHash;
	}

	public void setUserPhoneHash(String userPhoneHash) {

		this.userPhoneHash = userPhoneHash;
	}

	@Length(min = 0, max = 64, message = "useruuid长度必须介于 0 和 64 之间")
	public String getUserUuid() {

		return userUuid;
	}

	public void setUserUuid(String userUuid) {

		this.userUuid = userUuid;
	}

	@Length(min = 0, max = 64, message = "userlawperson长度必须介于 0 和 64 之间")
	public String getUserLawperson() {

		return userLawperson;
	}

	public void setUserLawperson(String userLawperson) {

		this.userLawperson = userLawperson;
	}

	@Length(min = 0, max = 20, message = "userfund长度必须介于 0 和 20 之间")
	public String getUserFund() {

		return userFund;
	}

	public void setUserFund(String userFund) {

		this.userFund = userFund;
	}

	@Length(min = 0, max = 20, message = "userprovince长度必须介于 0 和 20 之间")
	public String getUserProvince() {

		return userProvince;
	}

	public void setUserProvince(String userProvince) {

		this.userProvince = userProvince;
	}

	@Length(min = 0, max = 256, message = "useraddress长度必须介于 0 和 256 之间")
	public String getUserAddress() {

		return userAddress;
	}

	public void setUserAddress(String userAddress) {

		this.userAddress = userAddress;
	}

	@Length(min = 0, max = 30, message = "registerdate长度必须介于 0 和 30 之间")
	public String getRegisterDate() {

		return registerDate;
	}

	public void setRegisterDate(String registerDate) {

		this.registerDate = registerDate;
	}

	@Length(min = 0, max = 3, message = "usersex长度必须介于 0 和 3 之间")
	public String getUserSex() {

		return userSex;
	}

	public void setUserSex(String userSex) {

		this.userSex = userSex;
	}

	@Length(min = 0, max = 40, message = "userbankaccount长度必须介于 0 和 40 之间")
	public String getUserBankAccount() {

		return userBankAccount;
	}

	public void setUserBankAccount(String userBankAccount) {

		this.userBankAccount = userBankAccount;
	}

	@Length(min = 0, max = 256, message = "batchnum长度必须介于 0 和 256 之间")
	public String getBatchNum() {

		return batchNum;
	}

	public void setBatchNum(String batchNum) {

		this.batchNum = batchNum;
	}

	@Length(min = 0, max = 40, message = "sendtime长度必须介于 0 和 40 之间")
	public String getSendTime() {

		return sendTime;
	}

	public void setSendTime(String sendTime) {

		this.sendTime = sendTime;
	}

}