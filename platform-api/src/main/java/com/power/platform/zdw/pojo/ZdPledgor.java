package com.power.platform.zdw.pojo;

/**
 * 
 * 类: ZdPledgor <br>
 * 描述: 出质人 <br>
 * 作者: Roy <br>
 * 时间: 2019年10月31日 下午2:03:59
 */
public class ZdPledgor {

	private String pledgorType; // 出质人类型
	private String pledgorName; // 出质人名称
	private String registerCode; // 工商注册号
	private String organizationCode; // 组织机构代码/统一社会信用代码，若已发放统一社会信用代码可在此处填写
	private String uscc; // 组织机构代码/统一社会信用代码，若已发放统一社会信用代码可在此处填写
	private String responsiblePerson; // 法定代表人/负责人
	private String lei; // 所属行业
	private String scale; // 企业规模
	private String country; // 国家
	private String province; // 省/直辖市
	private String city; // 市/地级市
	private String address; // 地址

	public String getPledgorType() {

		return pledgorType;
	}

	public void setPledgorType(String pledgorType) {

		this.pledgorType = pledgorType;
	}

	public String getPledgorName() {

		return pledgorName;
	}

	public void setPledgorName(String pledgorName) {

		this.pledgorName = pledgorName;
	}

	public String getRegisterCode() {

		return registerCode;
	}

	public void setRegisterCode(String registerCode) {

		this.registerCode = registerCode;
	}

	public String getOrganizationCode() {

		return organizationCode;
	}

	public void setOrganizationCode(String organizationCode) {

		this.organizationCode = organizationCode;
	}

	public String getUscc() {

		return uscc;
	}

	public void setUscc(String uscc) {

		this.uscc = uscc;
	}

	public String getResponsiblePerson() {

		return responsiblePerson;
	}

	public void setResponsiblePerson(String responsiblePerson) {

		this.responsiblePerson = responsiblePerson;
	}

	public String getLei() {

		return lei;
	}

	public void setLei(String lei) {

		this.lei = lei;
	}

	public String getScale() {

		return scale;
	}

	public void setScale(String scale) {

		this.scale = scale;
	}

	public String getCountry() {

		return country;
	}

	public void setCountry(String country) {

		this.country = country;
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

	public String getAddress() {

		return address;
	}

	public void setAddress(String address) {

		this.address = address;
	}

}
