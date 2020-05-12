package com.power.platform.credit.entity.companyInfo;

import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;

/**
 * 
 * 类: CreditCompanyInfo <br>
 * 描述: 个人信贷公司信息Entity. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2017年3月30日 上午11:44:15
 */
public class CreditCompanyInfo extends DataEntity<CreditCompanyInfo> {

	private static final long serialVersionUID = 1L;
	private String creditUserId; // 用户ID
	private String companyName; // 公司名称
	private String bankAccountNo; // 对公银行账户
	private String bankName; // 开户行名称
	private String remark; // 备注
	private List<String> imgList; // 附件列表.

	public CreditCompanyInfo() {

		super();
	}

	public CreditCompanyInfo(String id) {

		super(id);
	}

	@Length(min = 0, max = 64, message = "用户ID长度必须介于 0 和 64 之间")
	public String getCreditUserId() {

		return creditUserId;
	}

	public void setCreditUserId(String creditUserId) {

		this.creditUserId = creditUserId;
	}

	@Length(min = 0, max = 64, message = "公司名称长度必须介于 0 和 64 之间")
	public String getCompanyName() {

		return companyName;
	}

	public void setCompanyName(String companyName) {

		this.companyName = companyName;
	}

	@Length(min = 0, max = 64, message = "对公银行账户长度必须介于 0 和 64 之间")
	public String getBankAccountNo() {

		return bankAccountNo;
	}

	public void setBankAccountNo(String bankAccountNo) {

		this.bankAccountNo = bankAccountNo;
	}

	@Length(min = 0, max = 255, message = "开户行名称长度必须介于 0 和 255 之间")
	public String getBankName() {

		return bankName;
	}

	public void setBankName(String bankName) {

		this.bankName = bankName;
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

}