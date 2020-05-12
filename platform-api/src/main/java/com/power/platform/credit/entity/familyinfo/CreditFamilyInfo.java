package com.power.platform.credit.entity.familyinfo;

import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;
import com.power.platform.credit.entity.annexfile.CreditAnnexFile;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;

/**
 * 
 * 类: CreditFamilyInfo <br>
 * 描述: 信贷家庭信息Entity. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2017年4月1日 上午10:39:30
 */
public class CreditFamilyInfo extends DataEntity<CreditFamilyInfo> {

	private static final long serialVersionUID = 1L;
	private String creditUserId; // 用户ID
	private String creditAnnexId; // 附件表
	private String relationType; // 关系类型('1'，父母，'2'，配偶，'3'，子女)
	private String name; // 姓名
	private String phone; // 手机号
	private String idCard; // 身份证号码
	private String remark; // 备注

	
	private CreditUserInfo creditUserInfo;
	private CreditAnnexFile creditAnnexFile;
	private List<String> imgList; // 附件列表.

	public CreditFamilyInfo() {

		super();
	}

	public CreditFamilyInfo(String id) {

		super(id);
	}

	@Length(min = 0, max = 64, message = "用户ID长度必须介于 0 和 64 之间")
	public String getCreditUserId() {

		return creditUserId;
	}

	public void setCreditUserId(String creditUserId) {

		this.creditUserId = creditUserId;
	}

	@Length(min = 0, max = 64, message = "附件表长度必须介于 0 和 64 之间")
	public String getCreditAnnexId() {

		return creditAnnexId;
	}

	public void setCreditAnnexId(String creditAnnexId) {

		this.creditAnnexId = creditAnnexId;
	}

	@Length(min = 0, max = 1, message = "关系类型('1'，父母，'2'，配偶，'3'，子女)长度必须介于 0 和 1 之间")
	public String getRelationType() {

		return relationType;
	}

	public void setRelationType(String relationType) {

		this.relationType = relationType;
	}

	@Length(min = 0, max = 64, message = "姓名长度必须介于 0 和 64 之间")
	public String getName() {

		return name;
	}

	public void setName(String name) {

		this.name = name;
	}

	@Length(min = 0, max = 64, message = "手机号长度必须介于 0 和 64 之间")
	public String getPhone() {

		return phone;
	}

	public void setPhone(String phone) {

		this.phone = phone;
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

	public List<String> getImgList() {

		return imgList;
	}

	public void setImgList(List<String> imgList) {

		this.imgList = imgList;
	}

	public CreditUserInfo getCreditUserInfo() {
		return creditUserInfo;
	}

	public void setCreditUserInfo(CreditUserInfo creditUserInfo) {
		this.creditUserInfo = creditUserInfo;
	}

	public CreditAnnexFile getCreditAnnexFile() {
		return creditAnnexFile;
	}

	public void setCreditAnnexFile(CreditAnnexFile creditAnnexFile) {
		this.creditAnnexFile = creditAnnexFile;
	}

	
}