package com.power.platform.sys.web.yjapi;

/**
 * 
 * 类: Branches <br>
 * 描述: 分支机构. <br>
 * 作者: Mr.Roy <br>
 * 时间: 2018年8月23日 上午10:49:26
 */
public class Branches {

	// CompanyId.
	private String CompanyId;
	// 注册号.
	private String RegNo;
	// 名称.
	private String Name;
	// 登记机关.
	private String BelongOrg;
	// 社会统一信用代码.
	private String CreditCode;
	// 法人姓名或负责人姓名.
	private String OperName;

	public String getCompanyId() {

		return CompanyId;
	}

	public void setCompanyId(String companyId) {

		CompanyId = companyId;
	}

	public String getRegNo() {

		return RegNo;
	}

	public void setRegNo(String regNo) {

		RegNo = regNo;
	}

	public String getName() {

		return Name;
	}

	public void setName(String name) {

		Name = name;
	}

	public String getBelongOrg() {

		return BelongOrg;
	}

	public void setBelongOrg(String belongOrg) {

		BelongOrg = belongOrg;
	}

	public String getCreditCode() {

		return CreditCode;
	}

	public void setCreditCode(String creditCode) {

		CreditCode = creditCode;
	}

	public String getOperName() {

		return OperName;
	}

	public void setOperName(String operName) {

		OperName = operName;
	}

	@Override
	public String toString() {

		StringBuffer bufferStr = new StringBuffer();
		bufferStr.append("CompanyId:" + CompanyId + ",");
		bufferStr.append("RegNo:" + RegNo + ",");
		bufferStr.append("Name:" + Name + ",");
		bufferStr.append("BelongOrg:" + BelongOrg + ",");
		bufferStr.append("CreditCode:" + CreditCode + ",");
		bufferStr.append("OperName:" + OperName);
		return bufferStr.toString();
	}

}
