package com.power.platform.credit.entity.ztmgLoanBasicInfo;

import java.util.Date;
import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.power.platform.common.persistence.DataEntity;
import com.power.platform.credit.entity.ztmgLoanBasicInfo.pojo.CreditAnnexFilePojo;

/**
 * 类: ZtmgLoanBasicInfo <br>
 * 描述: 借款人基本信息Entity. <br>
 * 作者: Mr.Roy <br>
 * 时间: 2018年5月2日 下午3:27:00
 */
public class ZtmgLoanBasicInfo extends DataEntity<ZtmgLoanBasicInfo> {

	private static final long serialVersionUID = 1L;
	private String creditUserId; // 借款人ID

	/**
	 * 办公地点.
	 */
	private String province; // 省份.
	private String city; // 地级市.
	private String county; // 市、县级市.
	private String street; // 街道.

	private String contributedCapital; // 实缴资本(元).
	private String industry; // 所属行业
	private String annualRevenue; // 上年营业收入(元)
	private String liabilities; // 负债(元)
	private String creditInformation; // 征信信息
	private String otherCreditInformation; // 其它借款信息
	private String remark; // 备注

	private String shareholdersJsonArrayStr; // 股东信息JSON数据.
	private String creditAnnexFileJsonArrayStr; // 征信报告JSON数据.

	/**
	 * 新增信息披露数据字段.
	 */
	private String companyName; // 公司名称.
	private String operName; // 法定代表人.
	private String registeredAddress; // 注册地址.
	private Date setUpTime; // 成立时间.
	private String registeredCapital; // 注册资本(元).
	private String scope; // 经营区域.
	private String declarationFilePath; // 声明文件路径.

	/**
	 * 股东信息列表.
	 */
	private List<ZtmgLoanShareholdersInfo> ztmgLoanShareholdersInfos;

	private List<CreditAnnexFilePojo> creditAnnexFilePojosList;

	public ZtmgLoanBasicInfo() {

		super();
	}

	public ZtmgLoanBasicInfo(String id) {

		super(id);
	}

	@Length(min = 0, max = 64, message = "借款人ID长度必须介于 0 和 64 之间")
	public String getCreditUserId() {

		return creditUserId;
	}

	public void setCreditUserId(String creditUserId) {

		this.creditUserId = creditUserId;
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

	@Length(min = 0, max = 32, message = "实缴资本(元)长度必须介于 0 和 32 之间")
	public String getContributedCapital() {

		return contributedCapital;
	}

	public void setContributedCapital(String contributedCapital) {

		this.contributedCapital = contributedCapital;
	}

	@Length(min = 0, max = 32, message = "所属行业长度必须介于 0 和 32 之间")
	public String getIndustry() {

		return industry;
	}

	public void setIndustry(String industry) {

		this.industry = industry;
	}

	@Length(min = 0, max = 32, message = "年营业收入(元)长度必须介于 0 和 32 之间")
	public String getAnnualRevenue() {

		return annualRevenue;
	}

	public void setAnnualRevenue(String annualRevenue) {

		this.annualRevenue = annualRevenue;
	}

	@Length(min = 0, max = 32, message = "负债(元)长度必须介于 0 和 32 之间")
	public String getLiabilities() {

		return liabilities;
	}

	public void setLiabilities(String liabilities) {

		this.liabilities = liabilities;
	}

	@Length(min = 0, max = 32, message = "征信信息长度必须介于 0 和 32 之间")
	public String getCreditInformation() {

		return creditInformation;
	}

	public void setCreditInformation(String creditInformation) {

		this.creditInformation = creditInformation;
	}

	@Length(min = 0, max = 32, message = "其它征信信息长度必须介于 0 和 32 之间")
	public String getOtherCreditInformation() {

		return otherCreditInformation;
	}

	public void setOtherCreditInformation(String otherCreditInformation) {

		this.otherCreditInformation = otherCreditInformation;
	}

	@Length(min = 0, max = 255, message = "备注长度必须介于 0 和 255 之间")
	public String getRemark() {

		return remark;
	}

	public void setRemark(String remark) {

		this.remark = remark;
	}

	public String getShareholdersJsonArrayStr() {

		return shareholdersJsonArrayStr;
	}

	public void setShareholdersJsonArrayStr(String shareholdersJsonArrayStr) {

		this.shareholdersJsonArrayStr = shareholdersJsonArrayStr;
	}

	public String getCreditAnnexFileJsonArrayStr() {

		return creditAnnexFileJsonArrayStr;
	}

	public void setCreditAnnexFileJsonArrayStr(String creditAnnexFileJsonArrayStr) {

		this.creditAnnexFileJsonArrayStr = creditAnnexFileJsonArrayStr;
	}

	public List<ZtmgLoanShareholdersInfo> getZtmgLoanShareholdersInfos() {

		return ztmgLoanShareholdersInfos;
	}

	public void setZtmgLoanShareholdersInfos(List<ZtmgLoanShareholdersInfo> ztmgLoanShareholdersInfos) {

		this.ztmgLoanShareholdersInfos = ztmgLoanShareholdersInfos;
	}

	public List<CreditAnnexFilePojo> getCreditAnnexFilePojosList() {

		return creditAnnexFilePojosList;
	}

	public void setCreditAnnexFilePojosList(List<CreditAnnexFilePojo> creditAnnexFilePojosList) {

		this.creditAnnexFilePojosList = creditAnnexFilePojosList;
	}

	public String getCompanyName() {

		return companyName;
	}

	public void setCompanyName(String companyName) {

		this.companyName = companyName;
	}

	public String getOperName() {

		return operName;
	}

	public void setOperName(String operName) {

		this.operName = operName;
	}

	public String getRegisteredAddress() {

		return registeredAddress;
	}

	public void setRegisteredAddress(String registeredAddress) {

		this.registeredAddress = registeredAddress;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getSetUpTime() {

		return setUpTime;
	}

	public void setSetUpTime(Date setUpTime) {

		this.setUpTime = setUpTime;
	}

	public String getRegisteredCapital() {

		return registeredCapital;
	}

	public void setRegisteredCapital(String registeredCapital) {

		this.registeredCapital = registeredCapital;
	}

	public String getScope() {

		return scope;
	}

	public void setScope(String scope) {

		this.scope = scope;
	}

	public String getDeclarationFilePath() {

		return declarationFilePath;
	}

	public void setDeclarationFilePath(String declarationFilePath) {

		this.declarationFilePath = declarationFilePath;
	}

	public static long getSerialversionuid() {

		return serialVersionUID;
	}

}