package com.power.platform.credit.entity.coinsuranceinfo;

import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;
import com.power.platform.credit.entity.annexfile.CreditAnnexFile;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;

/**
 * 
 * 类: CreditCoinsuranceInfo <br>
 * 描述: 信贷联保Entity. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2017年3月30日 下午5:37:04
 */
public class CreditCoinsuranceInfo extends DataEntity<CreditCoinsuranceInfo> {

	private static final long serialVersionUID = 1L;
	private String creditUserId; // 用户ID.
	private String coinsuranceType; // 1-个人,2-公司
	private String companyName; // 公司名称.
	private String name; // 姓名/法人姓名.
	private String phone; // 手机号/法人手机号.
	private String idCard; // 身份证号码.
	private String remark; // 备注
	private List<String> imgList; // 附件列表.
	
	private CreditUserInfo creditUserInfo;
	private CreditAnnexFile creditAnnexFile;

	public CreditCoinsuranceInfo() {

		super();
	}

	public CreditCoinsuranceInfo(String id) {

		super(id);
	}

	@Length(min = 0, max = 64, message = "用户ID长度必须介于 0 和 64 之间")
	public String getCreditUserId() {

		return creditUserId;
	}

	public void setCreditUserId(String creditUserId) {

		this.creditUserId = creditUserId;
	}

	@Length(min = 0, max = 2, message = "1-个人,2-公司长度必须介于 0 和 2 之间")
	public String getCoinsuranceType() {

		return coinsuranceType;
	}

	public void setCoinsuranceType(String coinsuranceType) {

		this.coinsuranceType = coinsuranceType;
	}

	@Length(min = 0, max = 64, message = "姓名长度必须介于 0 和 64 之间")
	public String getCompanyName() {

		return companyName;
	}

	public void setCompanyName(String companyName) {

		this.companyName = companyName;
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

	@Length(min = 0, max = 64, message = "手机号长度必须介于 0 和 64 之间")
	public void setPhone(String phone) {

		this.phone = phone;
	}

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