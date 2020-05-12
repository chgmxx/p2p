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
public class CreditUserInfo extends DataEntity<CreditUserInfo> {

	private static final long serialVersionUID = 1L;
	private String phone; // 手机号
	private String name; // 姓名
	private String pwd; // 密码
	private String gesturePwd; // 手势密码
	private String creditScore; // 信用评分
	private Date registerDate; // 注册时间
	private Date lastLoginDate; // 最后登录时间
	private String lastLoginIp; // 最后登录ip
	private String state; // 状态
	/**
	 * 已销户.
	 */
	public static final String CREDIT_USER_DELETED = "0";
	/**
	 * 正常.
	 */
	public static final String CREDIT_USER_NORMAL = "1";
	private String beginCreditScore; // 开始 信用评分
	private String endCreditScore; // 结束 信用评分
	private String accountId; // 账户ID
	private String certificateNo; // 身份证号
	private String creditUserType; // 账户类型
	private String enterpriseFullName;// 企业全称
	private String autoState;
	/**
	 * 02：借款户.
	 */
	public static final String CREDIT_USER_TYPE_02 = "02";
	/**
	 * 11：代偿户.
	 */
	public static final String CREDIT_USER_TYPE_11 = "11";
	/**
	 * 15：房产抵押.
	 */
	public static final String CREDIT_USER_TYPE_15 = "15";

	private CreditUserAccount creditUserAccount;
	private CgbUserBankCard cgbUserBankCard;

	private CreditAnnexFile annexFile;
	private Integer firstLogin;// 是否首次登录 0 首次 1多次

	private List<String> supplierIdList;

	private String ownedCompany; // 所属企业

	private String accountType; // 项目类型

	private String level; // 星级

	private WloanSubject wloanSubject;// 融资主体

	private String isCreateBasicInfo; // 是否完善基本信息（1：已完善基本信息，2：未完善基本信息）.

	/**
	 * 1：已完善基本信息.
	 */
	public static final String IS_CREATE_BASIC_INFO_1 = "1";
	/**
	 * 2：未完善基本信息.
	 */
	public static final String IS_CREATE_BASIC_INFO_2 = "2";

	/**
	 * 宁波熙耘科技有限公司，帐号唯一标识.
	 */
	public static final String XIYUN_ID = "7826464034456156057";
	/**
	 * 北京爱亲科技股份有限公司，帐号唯一标识.
	 */
	public static final String AIQIN_ID = "5685145015583919274";

	// 懒猫，开户状态，0：未开户，1：已开户，2：审核中，3：审核回退，4：审核拒绝
	private String openAccountState;
	private String isActivate; // 懒猫2.0迁移，会员激活标识，TRUE、FALSE

	private Date beginRepaymentDate;// 开始时间
	private Date endRepaymentDate;// 结束时间
	

	// 紧用于展示
	private String inTheLoanBalance; // 在贷余额


	public CreditUserInfo() {

		super();
	}

	public CreditUserInfo(String id) {

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

	@Length(min = 0, max = 64, message = "手势密码长度必须介于 0 和 64 之间")
	public String getGesturePwd() {

		return gesturePwd;
	}

	public void setGesturePwd(String gesturePwd) {

		this.gesturePwd = gesturePwd;
	}

	@Length(min = 0, max = 64, message = "信用评分长度必须介于 0 和 64 之间")
	public String getCreditScore() {

		return creditScore;
	}

	public void setCreditScore(String creditScore) {

		this.creditScore = creditScore;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getRegisterDate() {

		return registerDate;
	}

	public void setRegisterDate(Date registerDate) {

		this.registerDate = registerDate;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getLastLoginDate() {

		return lastLoginDate;
	}

	public void setLastLoginDate(Date lastLoginDate) {

		this.lastLoginDate = lastLoginDate;
	}

	@Length(min = 0, max = 16, message = "最后登录ip长度必须介于 0 和 16 之间")
	public String getLastLoginIp() {

		return lastLoginIp;
	}

	public void setLastLoginIp(String lastLoginIp) {

		this.lastLoginIp = lastLoginIp;
	}

	@Length(min = 0, max = 2, message = "状态长度必须介于 0 和 2 之间")
	public String getState() {

		return state;
	}

	public void setState(String state) {

		this.state = state;
	}

	public String getBeginCreditScore() {

		return beginCreditScore;
	}

	public void setBeginCreditScore(String beginCreditScore) {

		this.beginCreditScore = beginCreditScore;
	}

	public String getEndCreditScore() {

		return endCreditScore;
	}

	public void setEndCreditScore(String endCreditScore) {

		this.endCreditScore = endCreditScore;
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

	public String getAutoState() {

		return autoState;
	}

	public void setAutoState(String autoState) {

		this.autoState = autoState;
	}

	public CreditUserAccount getCreditUserAccount() {

		return creditUserAccount;
	}

	public void setCreditUserAccount(CreditUserAccount creditUserAccount) {

		this.creditUserAccount = creditUserAccount;
	}

	public CgbUserBankCard getCgbUserBankCard() {

		return cgbUserBankCard;
	}

	public void setCgbUserBankCard(CgbUserBankCard cgbUserBankCard) {

		this.cgbUserBankCard = cgbUserBankCard;
	}

	public CreditAnnexFile getAnnexFile() {

		return annexFile;
	}

	public void setAnnexFile(CreditAnnexFile annexFile) {

		this.annexFile = annexFile;
	}

	public Integer getFirstLogin() {

		return firstLogin;
	}

	public void setFirstLogin(Integer firstLogin) {

		this.firstLogin = firstLogin;
	}

	public List<String> getSupplierIdList() {

		return supplierIdList;
	}

	public void setSupplierIdList(List<String> supplierIdList) {

		this.supplierIdList = supplierIdList;
	}

	public String getOwnedCompany() {

		return ownedCompany;
	}

	public void setOwnedCompany(String ownedCompany) {

		this.ownedCompany = ownedCompany;
	}

	public String getAccountType() {

		return accountType;
	}

	public void setAccountType(String accountType) {

		this.accountType = accountType;
	}

	public String getLevel() {

		return level;
	}

	public void setLevel(String level) {

		this.level = level;
	}

	public WloanSubject getWloanSubject() {

		return wloanSubject;
	}

	public void setWloanSubject(WloanSubject wloanSubject) {

		this.wloanSubject = wloanSubject;
	}

	public String getIsCreateBasicInfo() {

		return isCreateBasicInfo;
	}

	public void setIsCreateBasicInfo(String isCreateBasicInfo) {

		this.isCreateBasicInfo = isCreateBasicInfo;
	}

	public String getOpenAccountState() {

		return openAccountState;
	}

	public void setOpenAccountState(String openAccountState) {

		this.openAccountState = openAccountState;
	}

	public String getIsActivate() {

		return isActivate;
	}

	public void setIsActivate(String isActivate) {

		this.isActivate = isActivate;
	}


	public Date getBeginRepaymentDate() {
		return beginRepaymentDate;
	}

	public void setBeginRepaymentDate(Date beginRepaymentDate) {
		this.beginRepaymentDate = beginRepaymentDate;
	}

	public Date getEndRepaymentDate() {
		return endRepaymentDate;
	}

	public void setEndRepaymentDate(Date endRepaymentDate) {
		this.endRepaymentDate = endRepaymentDate;
	}


	public String getInTheLoanBalance() {

		return inTheLoanBalance;
	}

	public void setInTheLoanBalance(String inTheLoanBalance) {

		this.inTheLoanBalance = inTheLoanBalance;
	}

}