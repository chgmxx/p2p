package com.power.platform.sys.web.yjapi;

import java.util.List;

/**
 * 
 * 类: YjapiResult <br>
 * 描述: 企查查数据结果集（接口： http://i.yjapi.com/ECIV4/GetDetailsByName）. <br>
 * 作者: Mr.Roy <br>
 * 时间: 2018年8月23日 上午11:50:13
 */
public class YjapiResult {

	// 内部KeyNo.
	private String KeyNo;
	// 公司名称.
	private String Name;
	// 注册号.
	private String No;
	// 登记机关.
	private String BelongOrg;
	// 法人名.
	private String OperName;
	// 成立日期.
	private String StartDate;
	// 吊销日期.
	private String EndDate;
	// 企业状态.
	private String Status;
	// 省份.
	private String Province;
	// 更新日期.
	private String UpdatedDate;
	// 社会统一信用代码.
	private String CreditCode;
	// 注册资本.
	private String RegistCapi;
	// 企业类型.
	private String EconKind;
	// 地址.
	private String Address;
	// 经营范围.
	private String Scope;
	// 营业开始日期.
	private String TermStart;
	// 营业结束日期.
	private String TeamEnd;
	// 发照日期.
	private String CheckDate;
	// 组织机构代码.
	private String OrgNo;
	// 是否上市（0为未上市，1为上市）
	private String IsOnStock;
	// 上市公司代码.
	private String StockNumber;
	// 上市类型.
	private String StockType;
	// 企业LOGO.
	private String ImageUrl;
	// 分支机构.
	private List<Branches> Branches;
	// 变更记录.
	private List<ChangeRecords> ChangeRecords;
	// 联系信息.
	private ContactInfo ContactInfo;
	// 员工.
	private List<Employees> Employees;
	// 行业.
	private Industry Industry;
	// 原来的名字.
	private List<OriginalName> OriginalName;
	// 原来的名字.
	private List<Partners> Partners;

	public String getKeyNo() {

		return KeyNo;
	}

	public void setKeyNo(String keyNo) {

		KeyNo = keyNo;
	}

	public String getName() {

		return Name;
	}

	public void setName(String name) {

		Name = name;
	}

	public String getNo() {

		return No;
	}

	public void setNo(String no) {

		No = no;
	}

	public String getBelongOrg() {

		return BelongOrg;
	}

	public void setBelongOrg(String belongOrg) {

		BelongOrg = belongOrg;
	}

	public String getOperName() {

		return OperName;
	}

	public void setOperName(String operName) {

		OperName = operName;
	}

	public String getStartDate() {

		return StartDate;
	}

	public void setStartDate(String startDate) {

		StartDate = startDate;
	}

	public String getEndDate() {

		return EndDate;
	}

	public void setEndDate(String endDate) {

		EndDate = endDate;
	}

	public String getStatus() {

		return Status;
	}

	public void setStatus(String status) {

		Status = status;
	}

	public String getProvince() {

		return Province;
	}

	public void setProvince(String province) {

		Province = province;
	}

	public String getUpdatedDate() {

		return UpdatedDate;
	}

	public void setUpdatedDate(String updatedDate) {

		UpdatedDate = updatedDate;
	}

	public String getCreditCode() {

		return CreditCode;
	}

	public void setCreditCode(String creditCode) {

		CreditCode = creditCode;
	}

	public String getRegistCapi() {

		return RegistCapi;
	}

	public void setRegistCapi(String registCapi) {

		RegistCapi = registCapi;
	}

	public String getEconKind() {

		return EconKind;
	}

	public void setEconKind(String econKind) {

		EconKind = econKind;
	}

	public String getAddress() {

		return Address;
	}

	public void setAddress(String address) {

		Address = address;
	}

	public String getScope() {

		return Scope;
	}

	public void setScope(String scope) {

		Scope = scope;
	}

	public String getTermStart() {

		return TermStart;
	}

	public void setTermStart(String termStart) {

		TermStart = termStart;
	}

	public String getTeamEnd() {

		return TeamEnd;
	}

	public void setTeamEnd(String teamEnd) {

		TeamEnd = teamEnd;
	}

	public String getCheckDate() {

		return CheckDate;
	}

	public void setCheckDate(String checkDate) {

		CheckDate = checkDate;
	}

	public String getOrgNo() {

		return OrgNo;
	}

	public void setOrgNo(String orgNo) {

		OrgNo = orgNo;
	}

	public String getIsOnStock() {

		return IsOnStock;
	}

	public void setIsOnStock(String isOnStock) {

		IsOnStock = isOnStock;
	}

	public String getStockNumber() {

		return StockNumber;
	}

	public void setStockNumber(String stockNumber) {

		StockNumber = stockNumber;
	}

	public String getStockType() {

		return StockType;
	}

	public void setStockType(String stockType) {

		StockType = stockType;
	}

	public String getImageUrl() {

		return ImageUrl;
	}

	public void setImageUrl(String imageUrl) {

		ImageUrl = imageUrl;
	}

	public List<Branches> getBranches() {

		return Branches;
	}

	public void setBranches(List<Branches> branches) {

		Branches = branches;
	}

	public List<ChangeRecords> getChangeRecords() {

		return ChangeRecords;
	}

	public void setChangeRecords(List<ChangeRecords> changeRecords) {

		ChangeRecords = changeRecords;
	}

	public ContactInfo getContactInfo() {

		return ContactInfo;
	}

	public void setContactInfo(ContactInfo contactInfo) {

		ContactInfo = contactInfo;
	}

	public List<Employees> getEmployees() {

		return Employees;
	}

	public void setEmployees(List<Employees> employees) {

		Employees = employees;
	}

	public Industry getIndustry() {

		return Industry;
	}

	public void setIndustry(Industry industry) {

		Industry = industry;
	}

	public List<OriginalName> getOriginalName() {

		return OriginalName;
	}

	public void setOriginalName(List<OriginalName> originalName) {

		OriginalName = originalName;
	}

	public List<Partners> getPartners() {

		return Partners;
	}

	public void setPartners(List<Partners> partners) {

		Partners = partners;
	}

	@Override
	public String toString() {

		StringBuffer bufferStr = new StringBuffer();
		bufferStr.append("KeyNo:" + KeyNo + "\t");
		bufferStr.append("Name:" + Name + "\t");
		bufferStr.append("No:" + No + "\t");
		bufferStr.append("BelongOrg:" + BelongOrg + "\n");
		bufferStr.append("OperName:" + OperName + "\t");
		bufferStr.append("StartDate:" + StartDate + "\t");
		bufferStr.append("EndDate:" + EndDate + "\t");
		bufferStr.append("Status:" + Status + "\n");
		bufferStr.append("Province:" + Province + "\t");
		bufferStr.append("UpdatedDate:" + UpdatedDate + "\t");
		bufferStr.append("CreditCode:" + CreditCode + "\t");
		bufferStr.append("RegistCapi:" + RegistCapi + "\n");
		bufferStr.append("EconKind:" + EconKind + "\t");
		bufferStr.append("Address:" + Address + "\t");
		bufferStr.append("Scope:" + Scope + "\t");
		bufferStr.append("TermStart:" + TermStart + "\n");
		bufferStr.append("TeamEnd:" + TeamEnd + "\t");
		bufferStr.append("CheckDate:" + CheckDate + "\t");
		bufferStr.append("OrgNo:" + OrgNo + "\t");
		bufferStr.append("IsOnStock:" + IsOnStock + "\n");
		bufferStr.append("StockNumber:" + StockNumber + "\t");
		bufferStr.append("StockType:" + StockType + "\t");
		bufferStr.append("ImageUrl:" + ImageUrl + "\t");
		bufferStr.append("Branches:" + Branches + "\n");
		bufferStr.append("ChangeRecords:" + ChangeRecords + "\n");
		bufferStr.append("ContactInfo:" + ContactInfo + "\n");
		bufferStr.append("Employees:" + Employees + "\n");
		bufferStr.append("Industry:" + Industry + "\n");
		bufferStr.append("OriginalName:" + OriginalName + "\n");
		bufferStr.append("Partners:" + Partners + "\n");
		return bufferStr.toString();
	}
}
