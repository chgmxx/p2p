package com.power.platform.regular.entity;

import org.hibernate.validator.constraints.Length;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.power.platform.common.persistence.DataEntity;
import com.power.platform.sys.entity.Area;
import com.power.platform.sys.entity.Dict;
import com.power.platform.sys.entity.User;

/**
 * 
 * 类: WloanSubject <br>
 * 描述: 融资主体Entity <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2015年12月29日 上午11:32:20
 */
public class WloanSubject extends DataEntity<WloanSubject> {

	private static final long serialVersionUID = 1L;
	private String companyName; // 企业/个人全称
	private String briefName; // 名称简介
	private String type; // 类型，1：个人，2：企业
	private String locus; // 注册地
	private String industry; // 所属行业
	private Date registerDate; // 成立日期
	private String organNo; // 组织机构代码，若证照类型为营业执照号，则为必填.
	private String taxCode; // 税务登记号，若证照类型为营业执照号，则为必填
	private String briefInfo; // 公司简介信息
	private String webSite; // 网址
	private String registerAmount; // 注册资金
	private String netAssetAmount; // 资产净值
	private String lastYearCash; // 上年现金流量
	private String runCase; // 经营情况

	/**
	 * 企业信息.
	 */
	private String businessLicenseType; // 证照类型，BLC：营业执照，USCC：统一社会信用代码.
	/**
	 * 营业执照.
	 */
	public static final String BUSINESS_LICENSE_TYPE_BLC = "BLC";
	/**
	 * 统一社会信用代码.
	 */
	public static final String BUSINESS_LICENSE_TYPE_USCC = "USCC";
	private String businessNo; // 证照编号
	private String bankPermitCertNo; // 银行开户许可证编号.
	private String agentPersonName; // 联系人姓名.
	private String agentPersonPhone; // 联系人手机号.
	private String agentPersonCertType; // 联系人证件类型，IDC-身份证，GAT-港澳台身份证，MILIARY-军官证，PASS_PORT-护照.
	private String agentPersonCertNo; // 联系人证件号.
	private String corporationCertType; // 法人证件类型，IDC-身份证，GAT-港澳台身份证，MILIARY-军官证，PASS_PORT-护照.
	/**
	 * 身份证.
	 */
	public static final String CERT_TYPE_IDC = "IDC";
	/**
	 * 港澳台身份证.
	 */
	public static final String CERT_TYPE_GAT = "GAT";
	/**
	 * 军官证.
	 */
	public static final String CERT_TYPE_MILIARY = "MILIARY";
	/**
	 * 护照.
	 */
	public static final String CERT_TYPE_PASS_PORT = "PASS_PORT";
	private String corporationCertNo; // 法人证件号.

	/**
	 * 受托人银行信息.
	 */
	private String isEntrustedPay; // 受托支付标识，0：否，1：是.
	private String cashierUser; // 受托人.
	private String cashierIdCard; // 受托人身份证号.
	private String cashierBankNo; // 受托人银行卡号.
	private String cashierBankPhone; // 受托人银行预留手机.
	private String cashierBankAdderss; // 受托人开户行.
	private String cashierBankCode; // 受托人开户行银行代码.
	private String cashierBankNoFlag; // 账户对公对私标识，1：对公，2：对私 ，默认：2 （对私）.
	private String cashierBankIssuer; // 联行号，账户对公时，必填.
	/**
	 * 借款人银行信息.
	 */
	private String loanApplyId; // 借款人ID（CreditUserInfo主键）.
	private String loanUser; // 借款人姓名.
	private String loanPhone; // 借款人手机号.
	private String loanIdCard; // 借款人身份证号.
	private String loanBankNo; // 借款人银行卡.
	private String loanBankPhone; // 借款人银行预留手机.
	private String loanBankName; // 借款人开户行.
	private String loanBankCode; // 借款人银行代码.
	private String loanBankCardName;// 银行开户名
	private String loanBankProvince;// 省
	private String loanBankCity;// 市
	private String loanIssuerName;// 支行名称
	private String loanIssuer;// 支行-联行号
	// --
	private Date beginCreateDate; // 开始 创建时间
	private Date endCreateDate; // 结束 创建时间
	private Date beginUpdateDate; // 开始 更新时间
	private Date endUpdateDate; // 结束 更新时间
	// --
	private User user; // 系统用户.
	private Area area; // 注册地.
	private Dict dict; // 字典类.

	private String email;// 邮箱
	private String registAddress;// 注册地址（协议用）

	private WloanTermProject wloanTermProject; // 定期项目.
	// --
	private String cicmorganBankCodeId; // 银行编码对照表ID.
	private String loanBankCounty; // 县(区).
	
	private String province;
	private String city;
	private String county;
	private String street;
	public String getCashierBankCode() {

		return cashierBankCode;
	}

	public void setCashierBankCode(String cashierBankCode) {

		this.cashierBankCode = cashierBankCode;
	}

	public WloanSubject() {

		super();
	}

	public WloanSubject(String id) {

		super(id);
	}

	@Length(min = 0, max = 255, message = "名称长度必须介于 0 和 255 之间")
	public String getCompanyName() {

		return companyName;
	}

	public void setCompanyName(String companyName) {

		this.companyName = companyName;
	}

	@Length(min = 0, max = 1000, message = "名称简介长度必须介于 0 和 1000 之间")
	public String getBriefName() {

		return briefName;
	}

	public void setBriefName(String briefName) {

		this.briefName = briefName;
	}

	@Length(min = 0, max = 1, message = "类型，1：个人，2：企业长度必须介于 0 和 1 之间")
	public String getType() {

		return type;
	}

	public void setType(String type) {

		this.type = type;
	}

	@Length(min = 0, max = 255, message = "注册地长度必须介于 0 和 255 之间")
	public String getLocus() {

		return locus;
	}

	public void setLocus(String locus) {

		this.locus = locus;
	}

	@Length(min = 0, max = 255, message = "所属行业长度必须介于 0 和 255 之间")
	public String getIndustry() {

		return industry;
	}

	public void setIndustry(String industry) {

		this.industry = industry;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getRegisterDate() {

		return registerDate;
	}

	public void setRegisterDate(Date registerDate) {

		this.registerDate = registerDate;
	}

	@Length(min = 0, max = 64, message = "营业执照编号长度必须介于 0 和 64 之间")
	public String getBusinessNo() {

		return businessNo;
	}

	public void setBusinessNo(String businessNo) {

		this.businessNo = businessNo;
	}

	@Length(min = 0, max = 64, message = "组织机构代码长度必须介于 0 和 64 之间")
	public String getOrganNo() {

		return organNo;
	}

	public void setOrganNo(String organNo) {

		this.organNo = organNo;
	}

	@Length(min = 0, max = 64, message = "税务登记号长度必须介于 0 和 64 之间")
	public String getTaxCode() {

		return taxCode;
	}

	public void setTaxCode(String taxCode) {

		this.taxCode = taxCode;
	}

	@Length(min = 0, max = 1000, message = "公司简介信息长度必须介于 0 和 1000 之间")
	public String getBriefInfo() {

		return briefInfo;
	}

	public void setBriefInfo(String briefInfo) {

		this.briefInfo = briefInfo;
	}

	@Length(min = 0, max = 64, message = "网址长度必须介于 0 和 64 之间")
	public String getWebSite() {

		return webSite;
	}

	public void setWebSite(String webSite) {

		this.webSite = webSite;
	}

	public String getRegisterAmount() {

		return registerAmount;
	}

	public void setRegisterAmount(String registerAmount) {

		this.registerAmount = registerAmount;
	}

	public String getNetAssetAmount() {

		return netAssetAmount;
	}

	public void setNetAssetAmount(String netAssetAmount) {

		this.netAssetAmount = netAssetAmount;
	}

	public String getLastYearCash() {

		return lastYearCash;
	}

	public void setLastYearCash(String lastYearCash) {

		this.lastYearCash = lastYearCash;
	}

	@Length(min = 0, max = 500, message = "经营情况长度必须介于 0 和 500 之间")
	public String getRunCase() {

		return runCase;
	}

	public void setRunCase(String runCase) {

		this.runCase = runCase;
	}

	@Length(min = 0, max = 64, message = "收款人长度必须介于 0 和 64 之间")
	public String getCashierUser() {

		return cashierUser;
	}

	public void setCashierUser(String cashierUser) {

		this.cashierUser = cashierUser;
	}

	@Length(min = 0, max = 64, message = "收款人身份证号长度必须介于 0 和 64 之间")
	public String getCashierIdCard() {

		return cashierIdCard;
	}

	public void setCashierIdCard(String cashierIdCard) {

		this.cashierIdCard = cashierIdCard;
	}

	@Length(min = 0, max = 64, message = "收款人银行卡号长度必须介于 0 和 64 之间")
	public String getCashierBankNo() {

		return cashierBankNo;
	}

	public void setCashierBankNo(String cashierBankNo) {

		this.cashierBankNo = cashierBankNo;
	}

	@Length(min = 0, max = 255, message = "开户行长度必须介于 0 和 255 之间")
	public String getCashierBankAdderss() {

		return cashierBankAdderss;
	}

	public void setCashierBankAdderss(String cashierBankAdderss) {

		this.cashierBankAdderss = cashierBankAdderss;
	}

	@Length(min = 0, max = 64, message = "收款人银行卡绑定手机号长度必须介于 0 和 64 之间")
	public String getCashierBankPhone() {

		return cashierBankPhone;
	}

	public void setCashierBankPhone(String cashierBankPhone) {

		this.cashierBankPhone = cashierBankPhone;
	}

	public String getLoanApplyId() {

		return loanApplyId;
	}

	public void setLoanApplyId(String loanApplyId) {

		this.loanApplyId = loanApplyId;
	}

	@Length(min = 0, max = 64, message = "借款人姓名长度必须介于 0 和 64 之间")
	public String getLoanUser() {

		return loanUser;
	}

	public void setLoanUser(String loanUser) {

		this.loanUser = loanUser;
	}

	@Length(min = 0, max = 64, message = "借款人身份证号长度必须介于 0 和 64 之间")
	public String getLoanIdCard() {

		return loanIdCard;
	}

	public void setLoanIdCard(String loanIdCard) {

		this.loanIdCard = loanIdCard;
	}

	@Length(min = 0, max = 64, message = "借款人手机号长度必须介于 0 和 64 之间")
	public String getLoanPhone() {

		return loanPhone;
	}

	public void setLoanPhone(String loanPhone) {

		this.loanPhone = loanPhone;
	}

	public Date getBeginCreateDate() {

		return beginCreateDate;
	}

	public void setBeginCreateDate(Date beginCreateDate) {

		this.beginCreateDate = beginCreateDate;
	}

	public Date getEndCreateDate() {

		return endCreateDate;
	}

	public void setEndCreateDate(Date endCreateDate) {

		this.endCreateDate = endCreateDate;
	}

	public Date getBeginUpdateDate() {

		return beginUpdateDate;
	}

	public void setBeginUpdateDate(Date beginUpdateDate) {

		this.beginUpdateDate = beginUpdateDate;
	}

	public Date getEndUpdateDate() {

		return endUpdateDate;
	}

	public void setEndUpdateDate(Date endUpdateDate) {

		this.endUpdateDate = endUpdateDate;
	}

	public User getUser() {

		return user;
	}

	public void setUser(User user) {

		this.user = user;
	}

	public Area getArea() {

		return area;
	}

	public void setArea(Area area) {

		this.area = area;
	}

	public Dict getDict() {

		return dict;
	}

	public void setDict(Dict dict) {

		this.dict = dict;
	}

	public WloanTermProject getWloanTermProject() {

		return wloanTermProject;
	}

	public void setWloanTermProject(WloanTermProject wloanTermProject) {

		this.wloanTermProject = wloanTermProject;
	}

	public String getIsEntrustedPay() {

		return isEntrustedPay;
	}

	public void setIsEntrustedPay(String isEntrustedPay) {

		this.isEntrustedPay = isEntrustedPay;
	}

	public String getCashierBankNoFlag() {

		return cashierBankNoFlag;
	}

	public void setCashierBankNoFlag(String cashierBankNoFlag) {

		this.cashierBankNoFlag = cashierBankNoFlag;
	}

	public String getCashierBankIssuer() {

		return cashierBankIssuer;
	}

	public void setCashierBankIssuer(String cashierBankIssuer) {

		this.cashierBankIssuer = cashierBankIssuer;
	}

	public String getLoanBankNo() {

		return loanBankNo;
	}

	public void setLoanBankNo(String loanBankNo) {

		this.loanBankNo = loanBankNo;
	}

	public String getLoanBankPhone() {

		return loanBankPhone;
	}

	public void setLoanBankPhone(String loanBankPhone) {

		this.loanBankPhone = loanBankPhone;
	}

	public String getLoanBankName() {

		return loanBankName;
	}

	public void setLoanBankName(String loanBankName) {

		this.loanBankName = loanBankName;
	}

	public String getLoanBankCode() {

		return loanBankCode;
	}

	public void setLoanBankCode(String loanBankCode) {

		this.loanBankCode = loanBankCode;
	}

	public String getLoanBankCardName() {

		return loanBankCardName;
	}

	public void setLoanBankCardName(String loanBankCardName) {

		this.loanBankCardName = loanBankCardName;
	}

	public String getLoanBankProvince() {

		return loanBankProvince;
	}

	public void setLoanBankProvince(String loanBankProvince) {

		this.loanBankProvince = loanBankProvince;
	}

	public String getLoanBankCity() {

		return loanBankCity;
	}

	public void setLoanBankCity(String loanBankCity) {

		this.loanBankCity = loanBankCity;
	}

	public String getLoanIssuerName() {

		return loanIssuerName;
	}

	public void setLoanIssuerName(String loanIssuerName) {

		this.loanIssuerName = loanIssuerName;
	}

	public String getLoanIssuer() {

		return loanIssuer;
	}

	public void setLoanIssuer(String loanIssuer) {

		this.loanIssuer = loanIssuer;
	}

	public String getCicmorganBankCodeId() {

		return cicmorganBankCodeId;
	}

	public void setCicmorganBankCodeId(String cicmorganBankCodeId) {

		this.cicmorganBankCodeId = cicmorganBankCodeId;
	}

	public String getBusinessLicenseType() {

		return businessLicenseType;
	}

	public void setBusinessLicenseType(String businessLicenseType) {

		this.businessLicenseType = businessLicenseType;
	}

	public String getBankPermitCertNo() {

		return bankPermitCertNo;
	}

	public void setBankPermitCertNo(String bankPermitCertNo) {

		this.bankPermitCertNo = bankPermitCertNo;
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

	public String getCorporationCertType() {

		return corporationCertType;
	}

	public void setCorporationCertType(String corporationCertType) {

		this.corporationCertType = corporationCertType;
	}

	public String getCorporationCertNo() {

		return corporationCertNo;
	}

	public void setCorporationCertNo(String corporationCertNo) {

		this.corporationCertNo = corporationCertNo;
	}

	public String getEmail() {

		return email;
	}

	public void setEmail(String email) {

		this.email = email;
	}

	public String getRegistAddress() {

		return registAddress;
	}

	public void setRegistAddress(String registAddress) {

		this.registAddress = registAddress;
	}

	public String getLoanBankCounty() {

		return loanBankCounty;
	}

	public void setLoanBankCounty(String loanBankCounty) {

		this.loanBankCounty = loanBankCounty;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCounty() {
		return county;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

}