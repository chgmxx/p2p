/**
 * Copyright &copy; 2012-2014 <a
 * href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.credit.entity.basicinfo;

import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;
import com.power.platform.credit.entity.annexfile.CreditAnnexFile;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;

/**
 * 信贷基本信息Entity
 * 
 * @author nice
 * @version 2017-03-23
 */
public class CreditBasicInfo extends DataEntity<CreditBasicInfo> {

	private static final long serialVersionUID = 1L;
	private String creditUserId; // 用户ID
	private String name; // 姓名
	private String age; // 年龄.
	private String maritalStatus; // 婚姻状况.
	private String educationStatus; // 学历.
	private String idCard; // 身份证号码
	private String remark; // 备注

	private CreditUserInfo ceditUserInfo;
	private CreditAnnexFile creditAnnexFile;

	private List<String> imgList;//

	public CreditBasicInfo() {

		super();
	}

	public CreditBasicInfo(String id) {

		super(id);
	}

	@Length(min = 0, max = 64, message = "用户ID长度必须介于 0 和 64 之间")
	public String getCreditUserId() {

		return creditUserId;
	}

	public void setCreditUserId(String creditUserId) {

		this.creditUserId = creditUserId;
	}

	@Length(min = 0, max = 64, message = "姓名长度必须介于 0 和 64 之间")
	public String getName() {

		return name;
	}

	public void setName(String name) {

		this.name = name;
	}

	public String getAge() {

		return age;
	}

	public void setAge(String age) {

		this.age = age;
	}

	public String getMaritalStatus() {

		return maritalStatus;
	}

	public void setMaritalStatus(String maritalStatus) {

		this.maritalStatus = maritalStatus;
	}

	public String getEducationStatus() {

		return educationStatus;
	}

	public void setEducationStatus(String educationStatus) {

		this.educationStatus = educationStatus;
	}

	@Length(min = 0, max = 64, message = "身份证号码长度必须介于 0 和 64 之间")
	public String getIdCard() {

		return idCard;
	}

	public void setIdCard(String idCard) {

		this.idCard = idCard;
	}

	@Length(min = 0, max = 255, message = "备注长度必须介于 0 和 255 之间")
	public String getRemark() {

		return remark;
	}

	public void setRemark(String remark) {

		this.remark = remark;
	}

	public CreditUserInfo getCeditUserInfo() {

		return ceditUserInfo;
	}

	public void setCeditUserInfo(CreditUserInfo ceditUserInfo) {

		this.ceditUserInfo = ceditUserInfo;
	}

	public CreditAnnexFile getCreditAnnexFile() {

		return creditAnnexFile;
	}

	public void setCreditAnnexFile(CreditAnnexFile creditAnnexFile) {

		this.creditAnnexFile = creditAnnexFile;
	}

	public List<String> getImgList() {

		return imgList;
	}

	public void setImgList(List<String> imgList) {

		this.imgList = imgList;
	}

}