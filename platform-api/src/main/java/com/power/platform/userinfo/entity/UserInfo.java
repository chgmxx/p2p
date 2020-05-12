/**
 * Copyright &copy; 2012-2014 <a
 * href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.userinfo.entity;

import org.hibernate.validator.constraints.Length;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.power.platform.activity.entity.ZtmgPartnerPlatform;
import com.power.platform.common.persistence.DataEntity;
import com.power.platform.common.utils.excel.annotation.ExcelField;

/**
 * class: UserInfo <br>
 * description: 平台出借端用户帐号信息 <br>
 * author: Roy <br>
 * date: 2019年10月10日 下午5:04:23
 */
public class UserInfo extends DataEntity<UserInfo> {

	private static final long serialVersionUID = 1L;
	private String name; // 手机号码
	private String oldMobilephone; // 旧手机号码
	private Integer userType; // 用户类型

	public static final Integer PRC_ID = 1; // 身份证
	public static final Integer PASSPORT = 2; // 护照
	public static final Integer COMPATRIOTS_CARD = 3; // 港澳台通行证
	public static final Integer PERMANENT_RESIDENCE = 4; // 外国人永久居留证

	private String realName; // 姓名
	private String spell; // 姓名拼写
	private String pwd; // 密码
	private Integer certificateType; // 证件类型
	private String certificateNo; // 证件号码
	private Integer certificateChecked; // 证件是否校验
	private Integer sex; // 性别
	private Integer state; // 状态
	private Integer integral; // 积分
	private Date registerDate; // 注册日期
	private Integer registerFrom; // 注册来源
	private String salt; // 校验码
	private Date lastLoginDate; // 最后登录日期
	private String lastLoginIp; // 最后登录IP地址
	private String recommendUserId; // 推荐人ID
	private String email; // 邮箱
	private Integer emailChecked; // 邮箱是否校验
	private Date sendemaildate; // 激活邮件发送时间
	private String headImg; // 头像
	private String industry; // 从事行业
	private String job; // 工作
	private String degree; // 学历
	private Long privacy; // 隐私策略
	private Integer userAwardType; // user_award_type
	private Integer recomType; // 推荐类型
	private String personSign; // 个人签名
	private Integer bindBankCardState; // 是否绑定银行卡
	private String businessPwd; // 交易密码
	private String accountId; // 账户ID
	private String llagreeNo; // 连连支付号
	private Date beginRegisterDate; // 开始 注册日期
	private Date endRegisterDate; // 结束 注册日期
	private String gesturePwd; // 手势密码(用于移动端)
	private String emergencyUser; // 紧急联系人
	private String emergencyTel; // 紧急联系方式
	private String address;

	public String parentPhone;// 推荐人手机号
	public Double regularTotalAmount;// 总投资金额

	public static final Integer CERTIFICATE_YES = 1; // 实名认证 否
	public static final Integer CERTIFICATE_NO = 2; // 实名认证 是

	public static final Integer BIND_EMAIL_NO = 1; // 邮箱验证否
	public static final Integer BIND_EMAIL_YES = 2; // 邮箱验证是

	public static final Integer BIND_CARD_YES = 2; // 绑卡状态 已绑卡
	public static final Integer BIND_CARD_NO = 1; // 绑卡状态 未绑卡

	public Integer cgbBindBankCardState; // 是否开通银行托管
	public static final Integer CGB_BIND_CARD_YES = 2;// 已绑卡
	public static final Integer CGB_BIND_CARD_NO = 1; // 未绑卡

	private String recommendUserPhone; // 推荐人手机号

	private ZtmgPartnerPlatform partnerForm;

	private String riskType;// 风险等级 1星级:激进型，2星级：进取性，3星级：稳健型，4星级：谨慎型，5星级：保守型
	public static final String RISKTYPE1 = "1";
	public static final String RISKTYPE2 = "2";
	public static final String RISKTYPE3 = "3";
	public static final String RISKTYPE4 = "4";
	public static final String RISKTYPE5 = "5";

	private Integer uvCounter;
	private String birthday;
	private String isActivate; // 懒猫2.0迁移，会员激活标识，TRUE、FALSE

	public String getBankFirst() {

		return bankFirst;
	}

	public void setBankFirst(String bankFirst) {

		this.bankFirst = bankFirst;
	}

	public String getRiskFirst() {

		return riskFirst;
	}

	public void setRiskFirst(String riskFirst) {

		this.riskFirst = riskFirst;
	}

	public String autoState;
	public String bankFirst;// 首页新手引导绑卡字段
	public String riskFirst;// 首页新手引导风险评测字段

	public UserInfo() {

		super();
	}

	public UserInfo(String id) {

		super(id);
	}

	@ExcelField(title = "投资金额", align = 2, sort = 30)
	public Double getRegularTotalAmount() {

		return regularTotalAmount;
	}

	public void setRegularTotalAmount(Double regularTotalAmount) {

		this.regularTotalAmount = regularTotalAmount;
	}

	@ExcelField(title = "邀请人手机号码", align = 2, sort = 30)
	public String getParentPhone() {

		return parentPhone;
	}

	public void setParentPhone(String parentPhone) {

		this.parentPhone = parentPhone;
	}

	public String getLlagreeNo() {

		return llagreeNo;
	}

	public void setLlagreeNo(String llagreeNo) {

		this.llagreeNo = llagreeNo;
	}

	@Length(min = 1, max = 32, message = "手机号码长度必须介于 1 和 32 之间")
	@ExcelField(title = "手机号码", align = 2, sort = 30)
	public String getName() {

		return name;
	}

	public void setName(String name) {

		this.name = name;
	}

	public String getOldMobilephone() {

		return oldMobilephone;
	}

	public void setOldMobilephone(String oldMobilephone) {

		this.oldMobilephone = oldMobilephone;
	}

	public Integer getUserType() {

		return userType;
	}

	public void setUserType(Integer userType) {

		this.userType = userType;
	}

	@Length(min = 0, max = 32, message = "姓名长度必须介于 0 和 32 之间")
	@ExcelField(title = "姓名", align = 2, sort = 60)
	public String getRealName() {

		return realName;
	}

	public void setRealName(String realName) {

		this.realName = realName;
	}

	@Length(min = 0, max = 320, message = "姓名拼写长度必须介于 0 和 320 之间")
	public String getSpell() {

		return spell;
	}

	public void setSpell(String spell) {

		this.spell = spell;
	}

	@Length(min = 0, max = 32, message = "密码长度必须介于 0 和 32 之间")
	public String getPwd() {

		return pwd;
	}

	public void setPwd(String pwd) {

		this.pwd = pwd;
	}

	public Integer getCertificateType() {

		return certificateType;
	}

	public void setCertificateType(Integer certificateType) {

		this.certificateType = certificateType;
	}

	@Length(min = 0, max = 32, message = "证件号码长度必须介于 0 和 32 之间")
	@ExcelField(title = "身份证号码", align = 2, sort = 90)
	public String getCertificateNo() {

		return certificateNo;
	}

	public void setCertificateNo(String certificateNo) {

		this.certificateNo = certificateNo;
	}

	public Integer getCertificateChecked() {

		return certificateChecked;
	}

	public void setCertificateChecked(Integer certificateChecked) {

		this.certificateChecked = certificateChecked;
	}

	public Integer getSex() {

		return sex;
	}

	public void setSex(Integer sex) {

		this.sex = sex;
	}

	public Integer getState() {

		return state;
	}

	public void setState(Integer state) {

		this.state = state;
	}

	public Integer getIntegral() {

		return integral;
	}

	public void setIntegral(Integer integral) {

		this.integral = integral;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ExcelField(title = "注册日期", align = 2, sort = 120)
	public Date getRegisterDate() {

		return registerDate;
	}

	public Date getSendemaildate() {

		return sendemaildate;
	}

	public void setSendemaildate(Date sendemaildate) {

		this.sendemaildate = sendemaildate;
	}

	public void setRegisterDate(Date registerDate) {

		this.registerDate = registerDate;
	}

	@ExcelField(title = "注册来源", align = 2, sort = 150)
	public String getExcelRegisterFrom() {

		String content = "";
		if (registerFrom == 1) {
			content = "PC";
		}
		if (registerFrom == 2) {
			content = "WAP";
		}
		if (registerFrom == 3) {
			content = "ANDROID";
		}
		if (registerFrom == 4) {
			content = "IOS";
		}
		if (registerFrom == 5) {
			content = "WIN_PHONE";
		}

		return content;
	}

	public Integer getRegisterFrom() {

		return registerFrom;
	}

	public void setRegisterFrom(Integer registerFrom) {

		this.registerFrom = registerFrom;
	}

	@Length(min = 0, max = 6, message = "校验码长度必须介于 0 和 6 之间")
	public String getSalt() {

		return salt;
	}

	public void setSalt(String salt) {

		this.salt = salt;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ExcelField(title = "最后登陆日期", align = 2, sort = 180)
	public Date getLastLoginDate() {

		return lastLoginDate;
	}

	public void setLastLoginDate(Date lastLoginDate) {

		this.lastLoginDate = lastLoginDate;
	}

	@Length(min = 0, max = 16, message = "最后登录IP地址长度必须介于 0 和 16 之间")
	public String getLastLoginIp() {

		return lastLoginIp;
	}

	public void setLastLoginIp(String lastLoginIp) {

		this.lastLoginIp = lastLoginIp;
	}

	@Length(min = 0, max = 32, message = "推荐人ID长度必须介于 0 和 32 之间")
	public String getRecommendUserId() {

		return recommendUserId;
	}

	public void setRecommendUserId(String recommendUserId) {

		this.recommendUserId = recommendUserId;
	}

	@Length(min = 0, max = 255, message = "邮箱长度必须介于 0 和 255 之间")
	public String getEmail() {

		return email;
	}

	public void setEmail(String email) {

		this.email = email;
	}

	public Integer getEmailChecked() {

		return emailChecked;
	}

	public void setEmailChecked(Integer emailChecked) {

		this.emailChecked = emailChecked;
	}

	@Length(min = 0, max = 255, message = "头像长度必须介于 0 和 255 之间")
	public String getHeadImg() {

		return headImg;
	}

	public void setHeadImg(String headImg) {

		this.headImg = headImg;
	}

	@Length(min = 0, max = 255, message = "从事行业长度必须介于 0 和 255 之间")
	public String getIndustry() {

		return industry;
	}

	public void setIndustry(String industry) {

		this.industry = industry;
	}

	@Length(min = 0, max = 255, message = "工作长度必须介于 0 和 255 之间")
	public String getJob() {

		return job;
	}

	public void setJob(String job) {

		this.job = job;
	}

	@Length(min = 0, max = 255, message = "学历长度必须介于 0 和 255 之间")
	public String getDegree() {

		return degree;
	}

	public void setDegree(String degree) {

		this.degree = degree;
	}

	public Long getPrivacy() {

		return privacy;
	}

	public void setPrivacy(Long privacy) {

		this.privacy = privacy;
	}

	public Integer getUserAwardType() {

		return userAwardType;
	}

	public void setUserAwardType(Integer userAwardType) {

		this.userAwardType = userAwardType;
	}

	public Integer getRecomType() {

		return recomType;
	}

	public void setRecomType(Integer recomType) {

		this.recomType = recomType;
	}

	@Length(min = 0, max = 200, message = "个人签名长度必须介于 0 和 200 之间")
	public String getPersonSign() {

		return personSign;
	}

	public void setPersonSign(String personSign) {

		this.personSign = personSign;
	}

	public Integer getBindBankCardState() {

		return bindBankCardState;
	}

	public void setBindBankCardState(Integer bindBankCardState) {

		this.bindBankCardState = bindBankCardState;
	}

	@Length(min = 0, max = 32, message = "交易密码长度必须介于 0 和 32 之间")
	public String getBusinessPwd() {

		return businessPwd;
	}

	public void setBusinessPwd(String businessPwd) {

		this.businessPwd = businessPwd;
	}

	@Length(min = 0, max = 32, message = "账户ID长度必须介于 0 和 32 之间")
	public String getAccountId() {

		return accountId;
	}

	public void setAccountId(String accountId) {

		this.accountId = accountId;
	}

	public Date getBeginRegisterDate() {

		return beginRegisterDate;
	}

	public void setBeginRegisterDate(Date beginRegisterDate) {

		this.beginRegisterDate = beginRegisterDate;
	}

	public Date getEndRegisterDate() {

		return endRegisterDate;
	}

	public void setEndRegisterDate(Date endRegisterDate) {

		this.endRegisterDate = endRegisterDate;
	}

	@Length(min = 0, max = 32, message = "手势密码长度必须介于 0 和 32 之间")
	public String getGesturePwd() {

		return gesturePwd;
	}

	public void setGesturePwd(String gesturePwd) {

		this.gesturePwd = gesturePwd;
	}

	public String getEmergencyUser() {

		return emergencyUser;
	}

	public void setEmergencyUser(String emergencyUser) {

		this.emergencyUser = emergencyUser;
	}

	public String getEmergencyTel() {

		return emergencyTel;
	}

	public void setEmergencyTel(String emergencyTel) {

		this.emergencyTel = emergencyTel;
	}

	public String getAddress() {

		return address;
	}

	public void setAddress(String address) {

		this.address = address;
	}

	public Integer getCgbBindBankCardState() {

		return cgbBindBankCardState;
	}

	public void setCgbBindBankCardState(Integer cgbBindBankCardState) {

		this.cgbBindBankCardState = cgbBindBankCardState;
	}

	public String getAutoState() {

		return autoState;
	}

	public void setAutoState(String autoState) {

		this.autoState = autoState;
	}

	public String getRecommendUserPhone() {

		return recommendUserPhone;
	}

	public void setRecommendUserPhone(String recommendUserPhone) {

		this.recommendUserPhone = recommendUserPhone;
	}

	public ZtmgPartnerPlatform getPartnerForm() {

		return partnerForm;
	}

	public void setPartnerForm(ZtmgPartnerPlatform partnerForm) {

		this.partnerForm = partnerForm;
	}

	@ExcelField(title = "渠道来源", align = 2, sort = 60)
	public String getPartnerFormName() {

		return partnerForm.getPlatformName();
	}

	public String getRiskType() {

		return riskType;
	}

	public void setRiskType(String riskType) {

		this.riskType = riskType;
	}

	public Integer getUvCounter() {

		return uvCounter;
	}

	public void setUvCounter(Integer uvCounter) {

		this.uvCounter = uvCounter;
	}

	public String getBirthday() {

		return birthday;
	}

	public void setBirthday(String birthday) {

		this.birthday = birthday;
	}

	public String getIsActivate() {

		return isActivate;
	}

	public void setIsActivate(String isActivate) {

		this.isActivate = isActivate;
	}

}