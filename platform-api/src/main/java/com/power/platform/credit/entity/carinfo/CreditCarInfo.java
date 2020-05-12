package com.power.platform.credit.entity.carinfo;

import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;
import com.power.platform.credit.entity.annexfile.CreditAnnexFile;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;

/**
 * 
 * 类: CreditCarInfo <br>
 * 描述: 信贷车产Entity. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2017年3月30日 下午4:44:57
 */
public class CreditCarInfo extends DataEntity<CreditCarInfo> {

	private static final long serialVersionUID = 1L;
	private String creditUserId; // 用户ID
	private String plateNumber; // 车牌号码
	private String engineNumber; // 发动机号
	private String remark; // 备注

	private List<String> imgList; // 附件列表.
	
	private CreditUserInfo creditUserInfo;
	private CreditAnnexFile creditAnnexFile;

	public CreditCarInfo() {

		super();
	}

	public CreditCarInfo(String id) {

		super(id);
	}

	@Length(min = 0, max = 64, message = "用户ID长度必须介于 0 和 64 之间")
	public String getCreditUserId() {

		return creditUserId;
	}

	public void setCreditUserId(String creditUserId) {

		this.creditUserId = creditUserId;
	}

	@Length(min = 0, max = 64, message = "车牌号码长度必须介于 0 和 64 之间")
	public String getPlateNumber() {

		return plateNumber;
	}

	public void setPlateNumber(String plateNumber) {

		this.plateNumber = plateNumber;
	}

	@Length(min = 0, max = 64, message = "发动机号长度必须介于 0 和 64 之间")
	public String getEngineNumber() {

		return engineNumber;
	}

	public void setEngineNumber(String engineNumber) {

		this.engineNumber = engineNumber;
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